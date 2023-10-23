package postgre.model;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record MyResources(
        String resourcePresetId,
        long diskSize,
        String diskTypeId
) {
    public MyResources(String resourcePresetId, long diskSize, String diskTypeId) {
        this.resourcePresetId = resourcePresetId;
        this.diskSize = diskSize;
        this.diskTypeId = diskTypeId;
    }
}
