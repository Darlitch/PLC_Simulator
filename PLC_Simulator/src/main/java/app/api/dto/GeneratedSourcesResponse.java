package app.api.dto;

import java.util.Map;

public record GeneratedSourcesResponse(
        String programFileName,
        Map<String, String> files
) {
}
