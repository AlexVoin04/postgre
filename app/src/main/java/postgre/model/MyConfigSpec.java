package postgre.model;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record MyConfigSpec(
        String version,
        MyResources resources,
        boolean autofailover,
        MyBackupWindowStart backupWindowStart,
        int backupRetainPeriodDays,
        MyPerformanceDiagnostics performanceDiagnostics
) {
}
