package postgre;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
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
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;

public class ScmTest {

    public URI deploy(String regionId)  throws Exception{
        String jsonFilePath = "/home/elena/Загрузки/key.json";
        Haikunator haikunator = new Haikunator();
        String jsonBody = createJsonBody(haikunator.haikunate(), regionId);
        String createContainerURL = "https://serverless-containers.api.cloud.yandex.net/containers/v1/containers";
        var request = YandexHttp.requestCreate(jsonFilePath, jsonBody, createContainerURL);
        var response = JsonRequestSender.sendAndGetJson(request, ResponseBobyServerlessContainer.class);
        if (response != null) {
            String containerId = response.getMetadata().getContainerId();
            String containerUrl = "https://serverless-containers.api.cloud.yandex.net/containers/v1/containers/" + containerId;
            var containerRequest = YandexHttp.requestGet(jsonFilePath,containerUrl);
            var containerResponse = JsonRequestSender.sendAndGetJson(containerRequest, ContainerInfo.class);
            if (containerResponse != null){
                String containerURL = containerResponse.getUrl();
                return URI.create(containerURL);
            }
        }
        return null;
    }

    private String createJsonBody(String name, String regionId){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            MyServerlessContainer jsonData = new MyServerlessContainer(
                    "b1ggaqs441crdco4j4it",
                    regionId,
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
    public static class ResponseBobyServerlessContainer {
        private Boolean done;
        private Metadata metadata;
        private String id;
        private String description;
        private String createdAt;
        private String createdBy;
        private String modifiedAt;

        public static class Metadata {
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

        public Metadata getMetadata() {
            return metadata;
        }

        public void setMetadata(Metadata metadata) {
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

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class ContainerInfo {
        private String id;
        private String folderId;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        private Date createdAt;

        private String name;
        private String url;
        private String status;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getFolderId() {
            return folderId;
        }

        public void setFolderId(String folderId) {
            this.folderId = folderId;
        }

        public Date getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(Date createdAt) {
            this.createdAt = createdAt;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
