package postgre.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.Map;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record ResponseBobyServerlessContainerError(
        Integer code,
        String message,
        Map<String, Object>[] details
) {
}
