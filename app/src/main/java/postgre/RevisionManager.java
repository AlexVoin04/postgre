package postgre;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import postgre.model.Revision;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class RevisionManager {


    public void deploy() {
        String jsonFilePath = "/home/elena/Загрузки/key.json";
        String jsonBody = createJsonBody();
        String revisionURL = "https://serverless-containers.api.cloud.yandex.net/containers/v1/revisions:deploy";
        var request = YandexHttp.requestCreate(jsonFilePath, jsonBody, revisionURL);
        var response = JsonRequestSender.sendAndGetJson(request, ResponseBobyRevision.class);
        if (response != null) {
            System.out.println(response.getMetadata().getContainerRevisionId());
        }

    }

    public String createJsonBody(){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Revision jsonData = new Revision(
                    "bba3bgj2v07lrah5mapq",
                    "",
                    new Revision.Resources("1", "100", "134217728"),
                    new Revision.ImageSpec(
                            "cr.yandex/crp7pl6mhdi8q77ad8lm/fastapi-docker-image:ca065cff84ae3a80e447e2ff1078e453dd1697db",
                            "",
                            new Revision.ImageSpec.Environment()
                    ),
                    List.of(),
                    List.of(),
                    "ajeo4f6pqi43bttp0li3",
                    new Revision.ExecutionTimeout("10"),
                    "1",
                    new Revision.Connectivity("enpctngasilslbagno5p"),
                    new Revision.LogOptions(false, "LEVEL_UNSPECIFIED")
            );
            return objectMapper.writeValueAsString(jsonData);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class ResponseBobyRevision{
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
            private String containerRevisionId;

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getContainerRevisionId() {
                return containerRevisionId;
            }

            public void setContainerRevisionId(String containerRevisionId) {
                this.containerRevisionId = containerRevisionId;
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

}
