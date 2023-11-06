package postgre;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.atrox.haikunator.Haikunator;
import postgre.model.ClusterPostgr;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public class PostgresqlManager {

    public URI deploy(String environment, String networkId, String zoneId, String subnetId, Boolean assignPublicIp) {
        String jsonFilePath = "/home/elena/Загрузки/key.json";
        Haikunator haikunator = new Haikunator();
        String password = PasGenerator.Generate();
        String userName = "user-pg";
        String createClusterURL = "https://mdb.api.cloud.yandex.net/managed-postgresql/v1/clusters";
        String jsonBody = createJsonBody(haikunator.haikunate(), "", userName, password,environment, networkId, zoneId, subnetId, assignPublicIp);
        var request = YandexHttp.requestCreate(jsonFilePath, jsonBody, createClusterURL);
        var response = JsonRequestSender.sendAndGetJson(request, ResponseBobyPostgreSql.class);
        if (response != null) {
            String clusterId = response.getMetadata().getClusterId();
//            String clusterUrl = "https://mdb.api.cloud.yandex.net/managed-postgresql/v1/clusters/" + clusterId + "/hosts";
//            var clusterRequest = YandexHttp.requestGet(jsonFilePath,clusterUrl);
//            var clusterResponse = JsonRequestSender.sendAndGetJson(clusterRequest, ClusterHostInfo.class);
//            if (clusterResponse != null){
//                String clusterURL = clusterResponse.getHosts().get(0).getName();
//                return URI.create(clusterURL);
//            }
            return shedule(clusterId);
        }
        return null;
    }

    public String createJsonBody(String name, String description, String userName, String userPassword, String environment, String networkId, String zoneId, String subnetId, Boolean assignPublicIp){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ClusterPostgr jsonData = new ClusterPostgr(
                    "b1ggaqs441crdco4j4it",
                    name,
                    description,
                    Map.of(),
                    environment,
                    networkId,
                    new ClusterPostgr.ConfigSpec(
                            "15",
                            new ClusterPostgr.ConfigSpec.Resources("s3-c2-m8", 10737418240L, "network-ssd"),
                            true,
                            new ClusterPostgr.ConfigSpec.BackupWindowStart(0, 0, 0, 0),
                            7,
                            new ClusterPostgr.ConfigSpec.PerformanceDiagnostics(false, 10, 600)
                    ),
                    List.of(
                            new ClusterPostgr.HostSpecs(
                                    zoneId,
                                    subnetId,
                                    assignPublicIp
                            )
                    ),
                    List.of(
                            new ClusterPostgr.DatabaseSpecs(
                                    "bd-pg",
                                    userName,
                                    "C",
                                    "C"
                            )
                    ),
                    List.of(
                            new ClusterPostgr.UserSpecs(
                                    userName,
                                    userPassword
                            )
                    ),
                    new ClusterPostgr.Access(true, true),
                    new ClusterPostgr.NetworkSettings("default")
            );

            return objectMapper.writeValueAsString(jsonData);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public URI shedule(String clusterId) {
        CompletableFuture<URI> uriFuture = new CompletableFuture<>();
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        ClusterAvailabilityChecker checker = new ClusterAvailabilityChecker(clusterId, "/home/elena/Загрузки/key.json");

        ScheduledFuture<?> future = scheduler.scheduleWithFixedDelay(() -> {
            Boolean isRunning = checker.call();
            if (isRunning) {
                scheduler.shutdown();
                uriFuture.complete(checker.getClusterURL());
            }
        }, 0, 1, TimeUnit.SECONDS);
        try {
            return uriFuture.get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class ResponseBobyPostgreSql{
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
            private String clusterId;

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getClusterId() {
                return clusterId;
            }

            public void setClusterId(String clusterId) {
                this.clusterId = clusterId;
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
    public static class ClusterHostInfo{
        private List<Host> hosts;
        public static class Host {
            private Resources resources;
            private List<Service> services;
            private boolean assignPublicIp;
            private String name;
            private String clusterId;
            private String zoneId;
            private String role;
            private String health;
            private String subnetId;
            private String priority;

            public static class Resources {
                private String resourcePresetId;
                private String diskSize;
                private String diskTypeId;

                public String getResourcePresetId() {
                    return resourcePresetId;
                }

                public void setResourcePresetId(String resourcePresetId) {
                    this.resourcePresetId = resourcePresetId;
                }

                public String getDiskSize() {
                    return diskSize;
                }

                public void setDiskSize(String diskSize) {
                    this.diskSize = diskSize;
                }

                public String getDiskTypeId() {
                    return diskTypeId;
                }

                public void setDiskTypeId(String diskTypeId) {
                    this.diskTypeId = diskTypeId;
                }
            }

            public static class Service {
                private String type;
                private String health;

                public String getType() {
                    return type;
                }

                public void setType(String type) {
                    this.type = type;
                }

                public String getHealth() {
                    return health;
                }

                public void setHealth(String health) {
                    this.health = health;
                }
            }

            public Resources getResources() {
                return resources;
            }

            public void setResources(Resources resources) {
                this.resources = resources;
            }

            public List<Service> getServices() {
                return services;
            }

            public void setServices(List<Service> services) {
                this.services = services;
            }

            public boolean isAssignPublicIp() {
                return assignPublicIp;
            }

            public void setAssignPublicIp(boolean assignPublicIp) {
                this.assignPublicIp = assignPublicIp;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getClusterId() {
                return clusterId;
            }

            public void setClusterId(String clusterId) {
                this.clusterId = clusterId;
            }

            public String getZoneId() {
                return zoneId;
            }

            public void setZoneId(String zoneId) {
                this.zoneId = zoneId;
            }

            public String getRole() {
                return role;
            }

            public void setRole(String role) {
                this.role = role;
            }

            public String getHealth() {
                return health;
            }

            public void setHealth(String health) {
                this.health = health;
            }

            public String getSubnetId() {
                return subnetId;
            }

            public void setSubnetId(String subnetId) {
                this.subnetId = subnetId;
            }

            public String getPriority() {
                return priority;
            }

            public void setPriority(String priority) {
                this.priority = priority;
            }
        }

        public List<Host> getHosts() {
            return hosts;
        }

        public void setHosts(List<Host> hosts) {
            this.hosts = hosts;
        }
    }
}
