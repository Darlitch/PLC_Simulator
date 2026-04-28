package app.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ApiErrorResponse(
        @Schema(description = "Application-specific error code", example = "BAD_REQUEST")
        String code,
        @Schema(description = "Human-readable error message", example = "source must not be blank")
        String message,
        @Schema(description = "Root cause details when available", example = "IllegalArgumentException: source must not be blank")
        String details
) {
}
