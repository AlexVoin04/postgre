package postgre.model;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record MyPerformanceDiagnostics(
        boolean enabled,
        int sessionsSamplingInterval,
        int statementsSamplingInterval
) {
}
