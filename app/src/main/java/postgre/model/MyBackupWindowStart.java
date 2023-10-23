package postgre.model;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record MyBackupWindowStart(
        int hours,
        int minutes,
        int seconds,
        int nanos
) {
}
