package app.api.dto;

public record LoadModelRequest(
        String modelName,
        String source
) {
}
