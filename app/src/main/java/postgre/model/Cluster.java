package postgre.model;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record Cluster(
        String folderId,
        String name,
        String description,
        Map<String, Object> labels,
        String environment,
        String networkId,
        ConfigSpec configSpec,
        List<HostSpecs> hostSpecs,
        List<DatabaseSpecs> databaseSpecs,
        List<UserSpecs> userSpecs,
        Access access,
        NetworkSettings networkSettings
) {
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public record HostSpecs(
            String zoneId,
            String subnetId,
            boolean assignPublicIp
    ) {
    }
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public record DatabaseSpecs(
            String name,
            String owner,
            String lcCtype,
            String lcCollate
    ) {
    }
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public record UserSpecs(
            String name,
            String password
    ) {
    }
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public record Access(
            boolean dataTransfer,
            boolean serverless
    ) {
    }
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public record NetworkSettings(
            String type
    ) {
    }
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public record ConfigSpec(
            String version,
            Resources resources,
            boolean autofailover,
            BackupWindowStart backupWindowStart,
            int backupRetainPeriodDays,
            PerformanceDiagnostics performanceDiagnostics
    ) {
        @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
        public record Resources(
                String resourcePresetId,
                long diskSize,
                String diskTypeId
        ) {
        }
        @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
        public record BackupWindowStart(
                int hours,
                int minutes,
                int seconds,
                int nanos
        ) {
        }
        @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
        public record PerformanceDiagnostics(
                boolean enabled,
                int sessionsSamplingInterval,
                int statementsSamplingInterval
        ) {
        }
    }
}
