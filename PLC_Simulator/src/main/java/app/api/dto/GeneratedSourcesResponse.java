package app.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

public record GeneratedSourcesResponse(
        @Schema(description = "Main generated program source file", example = "Controller.java")
        String programFileName,
        @Schema(description = "Generated Java source files keyed by file name")
        Map<String, String> files
) {
}
