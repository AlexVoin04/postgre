
package postgre;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.atrox.haikunator.Haikunator;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.http.entity.StringEntity;
import yandex.cloud.sdk.auth.Auth;
import yandex.cloud.sdk.auth.IamToken;
import yandex.cloud.sdk.auth.provider.CredentialProvider;

import static postgre.Jwt.CreateJsonBody;

public class App {

    public static void main(String[] args) throws Exception {

//        Haikunator haikunator = new Haikunator();
//        String jsonBody = CreateJsonBody(haikunator.haikunate(), "", "user-pg", PasGenerator.Generate());
//        System.out.println("PostgreSQL:\n" +jsonBody);
        Path path = Path.of(ClassLoader.getSystemResource("key.json").toURI());
        CredentialProvider provider2 = Auth.apiKeyBuilder()
                .fromFile(path)
                .build();
        IamToken iamToken = provider2.get();
        String apiKey = iamToken.getToken();
        String test = "{\n" +
                "  \"id\": \"c9qsnjbtv7vgkp9lhiv1\",\n" +
                "  \"description\": \"string\",\n" +
                "  \"createdAt\": \"string\",\n" +
                "  \"createdBy\": \"string\",\n" +
                "  \"modifiedAt\": \"string\",\n" +
                "  \"done\": true,\n" +
                "  \"metadata\": \"object\",\n" +
                "\n" +
                "  \"error\": {\n" +
                "    \"code\": \"integer\",\n" +
                "    \"message\": \"string\",\n" +
                "    \"details\": [\n" +
                "      \"object\"\n" +
                "    ]\n" +
                "  },\n" +
                "  \"response\": \"object\"\n" +
                "\n" +
                "}";
        TestTest(test, apiKey);
    }

    Object getGreeting() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public static void TestTest(String responseString, String key) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonResponse = objectMapper.readTree(responseString);
            String clusterId = jsonResponse.get("id").asText();
            String getClusterStatusURL = "https://mdb.api.cloud.yandex.net/managed-postgresql/v1/clusters/" + clusterId;
            CloseableHttpClient httpClient = HttpClients.createDefault();

            //GET-запрос к кластеру
            HttpGet httpGet = new HttpGet(getClusterStatusURL);
            httpGet.setHeader("Content-Type", "application/json");
            httpGet.setHeader("Authorization", "Bearer " + key);

            CloseableHttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            String responseString1 = EntityUtils.toString(entity);

            if (response.getStatusLine().getStatusCode() == 200) {
                System.out.println(responseString1);
                JsonNode jsonResponseStatus = objectMapper.readTree(responseString1);

                String status = jsonResponseStatus.get("status").asText();
                if ("RUNNING".equals(status)) {
                    System.out.println("Статус кластера: RUNNING");
                } else {
                    System.out.println("Статус кластера: " + status);
                }
            } else {
                System.err.println(responseString1);
            }

            // Закрываем HTTP-клиент
            httpClient.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
