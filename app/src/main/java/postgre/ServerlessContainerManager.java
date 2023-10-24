package postgre;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.atrox.haikunator.Haikunator;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import postgre.model.*;
import yandex.cloud.sdk.auth.Auth;
import yandex.cloud.sdk.auth.IamToken;
import yandex.cloud.sdk.auth.provider.CredentialProvider;

import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ServerlessContainerManager {
    public static void main(String[] args) throws Exception {
        Path path = Path.of(ClassLoader.getSystemResource("key.json").toURI());
        CredentialProvider provider2 = Auth.apiKeyBuilder()
                .fromFile(path)
                .build();
        IamToken iamToken = provider2.get();

        System.out.println("JWT Token: " + iamToken.getToken());

        String apiKey = iamToken.getToken();
        String createClusterURL = "https://serverless-containers.api.cloud.yandex.net/containers/v1/containers";

        try {
            Haikunator haikunator = new Haikunator();
            String jsonBody = CreateJsonBody(haikunator.haikunate());
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(createClusterURL);

            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Authorization", "Bearer " + apiKey);
            httpPost.setEntity(new StringEntity(jsonBody));

            CloseableHttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity);

            if (response.getStatusLine().getStatusCode() == 200) {
                System.out.println("ServerlessContainer:\n" +responseString);
            } else {
                System.err.println(responseString);
            }
            httpClient.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String CreateJsonBody(String name){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            MyServerlessContainer jsonData = new MyServerlessContainer(
                    "b1ggaqs441crdco4j4it",
                    "ru-central1",
                    name
            );
            return objectMapper.writeValueAsString(jsonData);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String DeployRevision(String apiKey, String response){
        return "";
    }

}
