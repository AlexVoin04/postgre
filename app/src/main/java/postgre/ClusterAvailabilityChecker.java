package postgre;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import yandex.cloud.sdk.auth.Auth;
import yandex.cloud.sdk.auth.IamToken;
import yandex.cloud.sdk.auth.provider.CredentialProvider;
import java.util.logging.Logger;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.concurrent.*;

class ClusterAvailabilityChecker implements Callable<Boolean> {
    private final String clusterId;
    private final String jsonFilePath;

    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> pollingTask;
    private Boolean isRunning;
    private URI clusterURL;


    public ClusterAvailabilityChecker(String clusterId, String jsonFilePath) {
        this.clusterId = clusterId;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.pollingTask = null;
        this.jsonFilePath = jsonFilePath;
        this.clusterURL = null;
        this.isRunning = false;
    }

    @Override
    public Boolean call() {
        try {
            pollingTask = scheduler.scheduleAtFixedRate(() -> {
                try {
                    String requestURL = "https://mdb.api.cloud.yandex.net/managed-postgresql/v1/clusters/" + clusterId;
                    System.out.println("TEST: \n" + requestURL);
                    var request = YandexHttp.requestGet(jsonFilePath, requestURL);
                    HttpClient httpClient = HttpClient.newHttpClient();
                    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                    if (response.statusCode() == 200) {
                        ObjectMapper objectMapper = new ObjectMapper();
                        JsonNode jsonResponseStatus = objectMapper.readTree(response.body());
                        String status = jsonResponseStatus.get("status").asText();

                        System.out.println("Статус кластера: " + status);
                        if ("RUNNING".equals(status)) {
                            System.out.println("Кластер работает");
                            String clusterUrl = "https://mdb.api.cloud.yandex.net/managed-postgresql/v1/clusters/" + clusterId + "/hosts";
                            var clusterRequest = YandexHttp.requestGet(jsonFilePath,clusterUrl);
                            var clusterResponse = JsonRequestSender.sendAndGetJson(clusterRequest, PostgresqlManager.ClusterHostInfo.class);
                            if (clusterResponse != null){
                                this.clusterURL = URI.create(clusterResponse.getHosts().get(0).getName());
                                isRunning = true;
                                pollingTask.cancel(true);
                            }

                        }
                    } else {
                        System.err.println(response.body());
                    }
                } catch (CancellationException ce) {
                    System.err.println("CancellationException: " + ce.getMessage());
                } catch (IOException e) {
                    Logger.getLogger(ClusterAvailabilityChecker.class.getName()).severe("IOException occurred: " + e.getMessage());
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
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
    public URI getClusterURL() {
        return clusterURL;
    }
}

