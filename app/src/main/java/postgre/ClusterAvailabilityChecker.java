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

import java.nio.file.Path;
import java.util.concurrent.ScheduledExecutorService;

class ClusterAvailabilityChecker implements Runnable {
    private final String responseString;
    private final String key;

    public ClusterAvailabilityChecker(String responseString, String key) {
        this.responseString = responseString;
        this.key = key;
    }

    @Override
    public void run() {
        try {
            Path path = Path.of(ClassLoader.getSystemResource("key.json").toURI());
            CredentialProvider provider2 = Auth.apiKeyBuilder()
                    .fromFile(path)
                    .build();
            IamToken iamToken = provider2.get();

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonResponse = objectMapper.readTree(this.responseString);
            String clusterId = jsonResponse.get("id").asText();
            System.out.println("clusterId = " + clusterId);
//            String getClusterStatusURL = "https://mdb.api.cloud.yandex.net/managed-postgresql/v1/clusters?folderId=b1ggaqs441crdco4j4it";
            String getClusterStatusURL = "https://mdb.api.cloud.yandex.net/managed-postgresql/v1/clusters/" + clusterId;
            CloseableHttpClient httpClient = HttpClients.createDefault();

            HttpGet httpGet = new HttpGet(getClusterStatusURL);
            httpGet.setHeader("Content-Type", "application/json");
            httpGet.setHeader("Authorization", "Bearer " + iamToken.getToken());

            CloseableHttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity);

            if (response.getStatusLine().getStatusCode() == 200) {
                System.out.println(responseString);
                JsonNode jsonResponseStatus = objectMapper.readTree(responseString);

                String status = jsonResponseStatus.get("status").asText();

                if ("RUNNING".equals(status)) {
                    System.out.println("Статус кластера: RUNNING");
                } else {
                    System.out.println("Статус кластера: " + status);
                }
            } else {
                System.err.println(responseString);
            }
            httpClient.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
