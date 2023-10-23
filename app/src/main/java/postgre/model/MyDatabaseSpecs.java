package postgre.model;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record MyDatabaseSpecs(
        String name,
        String owner,
        String lcCtype,
        String lcCollate
) {
}
