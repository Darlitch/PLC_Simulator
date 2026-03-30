package app.api.dto;

import java.util.Map;

public record UpdateInputsRequest(Map<String, Object> values) {
}
