package app.api.dto;

public record ApiErrorResponse(
        String code,
        String message,
        String details
) {
}
