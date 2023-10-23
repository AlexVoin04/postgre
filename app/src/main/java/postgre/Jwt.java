/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 *
 * @author Alex
 */
public class Jwt {
    public static void main(String[] args) throws Exception {
        Path path = Path.of(ClassLoader.getSystemResource("key.json").toURI());
        CredentialProvider provider2 = Auth.apiKeyBuilder()
                .fromFile(path)
                .build();
        IamToken iamToken = provider2.get();

        System.out.println("JWT Token: " + iamToken.getToken());

        String apiKey = iamToken.getToken();
        String createClusterURL = "https://mdb.api.cloud.yandex.net/managed-postgresql/v1/clusters";

        try {
            Haikunator haikunator = new Haikunator();
            String jsonBody = CreateJsonBody("b1ggaqs441crdco4j4it", haikunator.haikunate(), "", "user-pg", PasGenerator.Generate());
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(createClusterURL);


            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Authorization", "Bearer " + apiKey);
            httpPost.setEntity(new StringEntity(jsonBody));


            CloseableHttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity);


            if (response.getStatusLine().getStatusCode() == 200) {
                System.out.println("PostgreSQL:\n" +responseString);
                Shedule(responseString, apiKey);
            } else {
                System.err.println(responseString);
            }

            httpClient.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String CreateJsonBody(String folderId, String name, String description, String userName, String userPassword){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            MyCluster jsonData = new MyCluster(
                    folderId,
                    name,
                    description,
                    Map.of(),
                    "PRODUCTION",
                    "enpctngasilslbagno5p",
                    new MyConfigSpec(
                            "15",
                            new MyResources("s3-c2-m8", 10737418240L, "network-ssd"),
                            true,
                            new MyBackupWindowStart(0, 0, 0, 0),
                            7,
                            new MyPerformanceDiagnostics(false, 10, 600)
                    ),
                    new MyHostSpecs[] {
                            new MyHostSpecs(
                                    "ru-central1-b",
                                    "e2ll2959l4rmhucfg4si",
                                    false
                            )
                    },
                    new MyDatabaseSpecs[] {
                            new MyDatabaseSpecs(
                                    "bd-pg",
                                    "user-pg",
                                    "C",
                                    "C"
                            )
                    },
                    new MyUserSpecs[] {
                            new MyUserSpecs(
                                    "user-pg",
                                    "70-Fz82-d"
                            )
                    },
                    new MyAccess(true, true),
                    new MyNetworkSettings("default")
            );

            String jsonString = objectMapper.writeValueAsString(jsonData);

            return jsonString;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static void Shedule(String response, String key) {
        // планировщик с одним потоком
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        // задача для пулинга состояния кластера каждые 15 секунд
        scheduler.scheduleAtFixedRate(new ClusterAvailabilityChecker(response, key, scheduler), 0, 15, TimeUnit.SECONDS);
    }
}
