package postgre.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

 public record MyCluster(
         @JsonProperty("folderId") String folderId,
         @JsonProperty("name") String name,
         @JsonProperty("description") String description,
         @JsonProperty("labels") Map<String, Object> labels,
         @JsonProperty("environment") String environment,
         @JsonProperty("networkId") String networkId,
         @JsonProperty("configSpec") MyConfigSpec configSpec,
         @JsonProperty("hostSpecs") List<MyHostSpecs> hostSpecs,
         @JsonProperty("databaseSpecs") List<MyDatabaseSpecs> databaseSpecs,
         @JsonProperty("userSpecs") List<MyUserSpecs> userSpecs,
         @JsonProperty("access") MyAccess access,
         @JsonProperty("networkSettings") MyNetworkSettings networkSettings
) {
}
