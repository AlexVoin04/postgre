/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package postgre;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.BufferedReader;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import yandex.cloud.sdk.auth.Auth;
import yandex.cloud.sdk.auth.IamToken;
import yandex.cloud.sdk.auth.provider.CredentialProvider;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.Date;
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
            Path cluster = Path.of(ClassLoader.getSystemResource("cluster_creation_data.json").toURI());
            String jsonBody = new String(Files.readAllBytes(cluster));
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(createClusterURL);


            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Authorization", "Bearer " + apiKey);
            httpPost.setEntity(new StringEntity(jsonBody));


            CloseableHttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity);


            if (response.getStatusLine().getStatusCode() == 200) {
                System.out.println("PostgreSQL\n" +responseString);
            } else {
                System.err.println(responseString);
            }

            httpClient.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
