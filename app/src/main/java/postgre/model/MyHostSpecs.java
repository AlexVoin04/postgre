package postgre.model;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record MyHostSpecs(
        String zoneId,
        String subnetId,
        boolean assignPublicIp
) {
}
