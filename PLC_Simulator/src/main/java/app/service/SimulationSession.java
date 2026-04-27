package app.service;

import simulator.PlcSimulationEngine;

import java.nio.file.Path;

public record SimulationSession(
        String sessionId,
        Path sessionRootDir,
        Path modelsDir,
        Path generatedSourcesDir,
        Path generatedClassesDir,
        PlcSimulationEngine engine
) {
}
