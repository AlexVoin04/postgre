package postgre;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import me.atrox.haikunator.Haikunator;
import org.apache.http.util.EntityUtils;
import postgre.model.MyServerlessContainer;
import postgre.model.ResponseBobyServerlessContainer;
import yandex.cloud.sdk.auth.Auth;
import yandex.cloud.sdk.auth.IamToken;
import yandex.cloud.sdk.auth.provider.CredentialProvider;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Supplier;

public class ScmTest {

    public URI deploy()  throws Exception{
        Path path = Path.of(ClassLoader.getSystemResource("key.json").toURI());
        CredentialProvider provider2 = Auth.apiKeyBuilder()
                .fromFile(path)
                .build();
        IamToken iamToken = provider2.get();

        System.out.println("JWT Token: " + iamToken.getToken());

        String apiKey = iamToken.getToken();
        String createClusterURL = "https://serverless-containers.api.cloud.yandex.net/containers/v1/containers";

        Haikunator haikunator = new Haikunator();
        String jsonBody = CreateJsonBody(haikunator.haikunate());

        assert jsonBody != null;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(createClusterURL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        var response = sendAndGetJson(request, ResponseBobyServerlessContainer1.class);
        if (response != null) {
            String containerId = response.getMetadata().getContainerId();
//            URI clusterURI = URI.create("https://console.cloud.yandex.ru/folders/b1ggaqs441crdco4j4it/serverless-containers/containers/" + containerId);
//            return clusterURI;
            URI endpointURI = URI.create(createClusterURL + "/" + containerId);
            return endpointURI;
        }
        return null;
    }

    private static <T> T sendAndGetJson(HttpRequest httpRequest, Class<T> aClass){
        HttpClient client = HttpClient.newHttpClient();
        final ObjectMapper objectMapper = new ObjectMapper();
        try {
            var response = client.send(httpRequest, new JsonBodyHandler<>(aClass));
            if (response.statusCode() != 200) {
                System.err.println(response.statusCode() + " " + response.body().get());
                return null;
            }
//            System.out.println("Response: \n" + objectMapper.enable(SerializationFeature.INDENT_OUTPUT).writeValueAsString(response.body().get()));
            return response.body().get();
        } catch (IOException e) {
            System.out.println("Error in sendAndGetJson");
            e.printStackTrace();
            return null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    public static <W> HttpResponse.BodySubscriber<Supplier<W>> asJson(Class<W> targetType) {
        HttpResponse.BodySubscriber<InputStream> upstream = HttpResponse.BodySubscribers.ofInputStream();
        return HttpResponse.BodySubscribers.mapping(
                upstream,
                inputStream -> () -> {
                    try (InputStream stream = inputStream) {
                        ObjectMapper objectMapper = new ObjectMapper();
                        return objectMapper.readValue(stream, targetType);
                    } catch (IOException e) {
                        System.out.println("Error in asJson");
                        e.printStackTrace();
                        return null;
                    }
                });
    }
    static class JsonBodyHandler<W> implements HttpResponse.BodyHandler<Supplier<W>> {
        private final Class<W> wClass;

        public JsonBodyHandler(Class<W> wClass) {
            this.wClass = wClass;
        }

        @Override
        public HttpResponse.BodySubscriber<Supplier<W>> apply(HttpResponse.ResponseInfo responseInfo) {
            return asJson(wClass);
        }

    }

    private String CreateJsonBody(String name){
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

    private String DeployRevision(String apiKey, String response){
        return "";
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class ResponseBobyServerlessContainer1 {
        private Boolean done;
        private Metadata1 metadata;
        private String id;
        private String description;
        private String createdAt;
        private String createdBy;
        private String modifiedAt;

        public static class Metadata1 {
            @JsonProperty("@type")
            private String type;
            private String containerId;

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getContainerId() {
                return containerId;
            }

            public void setContainerId(String containerId) {
                this.containerId = containerId;
            }
        }

        public Boolean getDone() {
            return done;
        }

        public void setDone(Boolean done) {
            this.done = done;
        }

        public Metadata1 getMetadata() {
            return metadata;
        }

        public void setMetadata(Metadata1 metadata) {
            this.metadata = metadata;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getCreatedBy() {
            return createdBy;
        }

        public void setCreatedBy(String createdBy) {
            this.createdBy = createdBy;
        }

        public String getModifiedAt() {
            return modifiedAt;
        }

        public void setModifiedAt(String modifiedAt) {
            this.modifiedAt = modifiedAt;
        }
    }

}
