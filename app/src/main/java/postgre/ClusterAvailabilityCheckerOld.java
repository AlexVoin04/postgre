package postgre;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import yandex.cloud.sdk.auth.Auth;
import yandex.cloud.sdk.auth.IamToken;
import yandex.cloud.sdk.auth.provider.CredentialProvider;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.*;

class ClusterAvailabilityCheckerOld implements Callable<Boolean> {
    private final String responseString;
    private final String key;
    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> pollingTask;
    private volatile boolean isRunning;


    public ClusterAvailabilityCheckerOld(String responseString, String key) {
        this.responseString = responseString;
        this.key = key;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.pollingTask = null;
        this.isRunning = false;
    }

    @Override
    public Boolean call() {

        try {
            pollingTask = scheduler.scheduleAtFixedRate(() -> {
                CloseableHttpClient httpClient = null;
                try {
                    Path path = Path.of(ClassLoader.getSystemResource("key.json").toURI());
                    CredentialProvider provider2 = Auth.apiKeyBuilder()
                            .fromFile(path)
                            .build();
                    IamToken iamToken = provider2.get();

                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonResponse = objectMapper.readTree(this.responseString);
                    String clusterId = jsonResponse.get("metadata").get("clusterId").asText();
//                    System.out.println("clusterId = " + clusterId);
                    //            String getClusterStatusURL = "https://mdb.api.cloud.yandex.net/managed-postgresql/v1/clusters?folderId=b1ggaqs441crdco4j4it";
                    String getClusterStatusURL = "https://mdb.api.cloud.yandex.net/managed-postgresql/v1/clusters/" + clusterId;
                    httpClient = HttpClients.createDefault();
                    HttpGet httpGet = new HttpGet(getClusterStatusURL);
                    httpGet.setHeader("Content-Type", "application/json");
                    httpGet.setHeader("Authorization", "Bearer " + iamToken.getToken());

                    CloseableHttpResponse response = httpClient.execute(httpGet);
                    HttpEntity entity = response.getEntity();
                    String responseString = EntityUtils.toString(entity);

                    if (response.getStatusLine().getStatusCode() == 200) {
//                        System.out.println(responseString);
                        JsonNode jsonResponseStatus = objectMapper.readTree(responseString);

                        String status = jsonResponseStatus.get("status").asText();

                        System.out.println("Статус кластера: " + status);
                        if ("RUNNING".equals(status)) {
                            System.out.println("Кластер работает");
                            isRunning = true;
                            pollingTask.cancel(true);
                        }
                    } else {
                        System.err.println(responseString);
                    }
                }catch (CancellationException ce){
                    System.err.println("CancellationException: " + ce.getMessage());
                } catch (Exception e) {
                    System.err.println("Exception1: " + e.getMessage());
                } finally {
                    try {
                        if (httpClient != null) {
                            httpClient.close();//Ошибка возникает из-за не закрытого HTTP-клиента
                        }
                    } catch (IOException e) {
                        System.err.println("IOException: "+ e.getMessage());
                    }
                }
            }, 15, 15, TimeUnit.SECONDS);
            pollingTask.get();
        } catch (Exception e) {
            System.err.println("Exception: " + e.getMessage());
        } finally {
            scheduler.shutdown();
        }
        return isRunning;
    }
}
