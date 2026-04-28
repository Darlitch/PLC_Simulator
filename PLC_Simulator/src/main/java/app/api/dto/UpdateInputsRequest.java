package app.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

public record UpdateInputsRequest(
        @Schema(description = "Input values to apply to the running simulation")
        Map<String, Object> values
) {
}
