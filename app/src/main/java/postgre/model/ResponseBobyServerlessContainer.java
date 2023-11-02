package postgre.model;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record ResponseBobyServerlessContainer(
        Boolean done,
        List<Metadata> metadata,
        String id,
        String description,
        String createdAt,
        String createdBy,
        String modifiedAt
) {
    public record Metadata(
            @JsonProperty("@type") String type,
            @JsonProperty("containerId") String containerId
    ) {
    }
}

/*
{
 "done": false,
 "metadata": {
  "@type": "type.googleapis.com/yandex.cloud.serverless.containers.v1.CreateContainerMetadata",
  "containerId": "bbanufgt777pe5ad169e"
 },
 "id": "bbaok2aothjc4ntblbgs",
 "description": "Create container",
 "createdAt": "2023-11-02T03:58:40.492371330Z",
 "createdBy": "ajeo4f6pqi43bttp0li3",
 "modifiedAt": "2023-11-02T03:58:40.492371330Z"
}
 */

/*
{
  "id": "string",
  "description": "string",
  "createdAt": "string",
  "createdBy": "string",
  "modifiedAt": "string",
  "done": true,
  "metadata": "object",

  //  includes only one of the fields `error`, `response`
  "error": {
    "code": "integer",
    "message": "string",
    "details": [
      "object"
    ]
  },
  "response": "object",
  // end of the list of possible fields
}
 */