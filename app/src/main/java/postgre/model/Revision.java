package postgre.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record Revision(
        String containerId,
        String description,
        Resources resources,
        ImageSpec imageSpec,
        List<String> secrets,
        List<String> storageMounts,
        String serviceAccountId,
        ExecutionTimeout executionTimeout,
        String concurrency,
        Connectivity connectivity,
        LogOptions logOptions
) {
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public record Resources(
            String cores,
            String coreFraction,
            String memory
    ) {}
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public record ImageSpec(
            String imageUrl,
            String workingDir,
            Environment environment
    ) {
        @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
        public record Environment(

        ) {}
    }
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public record ExecutionTimeout(
            String seconds
    ) {}
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public record Connectivity(
            String networkId
    ) {}
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public record LogOptions(
            boolean disabled,
            String minLevel
    ) {}
}
