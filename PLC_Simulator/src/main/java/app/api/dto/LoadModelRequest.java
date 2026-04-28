package app.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoadModelRequest(
        @Schema(description = "Display name for the uploaded model", example = "ElevatorController")
        String modelName,
        @Schema(description = "Raw PoST model source code", example = "process Controller() { }")
        String source
) {
}
