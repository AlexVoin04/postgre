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

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
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
        CloseableHttpClient httpClient = null;
        try {
            Haikunator haikunator = new Haikunator();
            String jsonBody = CreateJsonBody(haikunator.haikunate(), "", "user-pg", PasGenerator.Generate());
            httpClient = HttpClients.createDefault();
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
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (IOException e) {
                System.err.println("IOException JWT.main: " + e.getMessage());
            }
        }
    }

    public static String CreateJsonBody(String name, String description, String userName, String userPassword){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            MyCluster jsonData = new MyCluster(
                    "b1ggaqs441crdco4j4it",
                    name,
                    description,
                    Map.of(),
                    "PRESTABLE",
                    "enpctngasilslbagno5p",
                    new MyConfigSpec(
                            "15",
                            new MyResources("s3-c2-m8", 10737418240L, "network-ssd"),
                            true,
                            new MyBackupWindowStart(0, 0, 0, 0),
                            7,
                            new MyPerformanceDiagnostics(false, 10, 600)
                    ),
                    List.of(
                            new MyHostSpecs(
                                    "ru-central1-b",
                                    "e2ll2959l4rmhucfg4si",
                                    false
                            )
                    ),
                    List.of(
                            new MyDatabaseSpecs(
                                    "bd-pg",
                                    userName,
                                    "C",
                                    "C"
                            )
                    ),
                    List.of(
                            new MyUserSpecs(
                                    userName,
                                    userPassword
                            )
                    ),
                    new MyAccess(true, true),
                    new MyNetworkSettings("default")
            );

            return objectMapper.writeValueAsString(jsonData);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static void Shedule(String response, String key) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


        ClusterAvailabilityCheckerOld checker = new ClusterAvailabilityCheckerOld(response, key);

        ScheduledFuture<?> future = scheduler.scheduleWithFixedDelay(() -> {
            boolean isClusterRunning = checker.call();
            if (isClusterRunning) {
                scheduler.shutdown();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }


}
