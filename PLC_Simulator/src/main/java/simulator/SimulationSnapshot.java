package simulator;

import java.nio.file.Path;
import java.util.Map;

public record SimulationSnapshot(
        Path modelPath,
        SimulationStatus status,
        Map<String, Object> inputs,
        Map<String, Object> outputs,
        Map<String, Object> globals,
        Map<String, Object> vars,
        Map<String, String> processStates,
        Map<String, Long> processTimers
) {
}
