package postgre;

import yandex.cloud.sdk.auth.Auth;
import yandex.cloud.sdk.auth.IamToken;
import yandex.cloud.sdk.auth.provider.CredentialProvider;

import java.net.URI;
import java.net.http.HttpRequest;
import java.nio.file.Path;

public class YandexHttp {
    public static HttpRequest requestCreate(String jsonFilePath, String jsonBody, String createURL) {
        String apiKey = getApiKey(jsonFilePath);
        assert jsonBody != null;
        return HttpRequest.newBuilder()
                .uri(URI.create(createURL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
    }

    public static HttpRequest requestGet(String jsonFilePath, String URL){
        String apiKey = getApiKey(jsonFilePath);
        return HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .GET()
                .build();
    }

    private static String getApiKey(String jsonFilePath) {
        Path path = Path.of(jsonFilePath);
        CredentialProvider provider = Auth.apiKeyBuilder()
                .fromFile(path)
                .build();
        IamToken iamToken = provider.get();
        System.out.println("JWT Token: " + iamToken.getToken());
        return iamToken.getToken();
    }

}
