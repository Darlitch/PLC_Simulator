package app.service;

import simulator.PlcSimulationEngine;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public record SimulationSession(
        String sessionId,
        Path sessionRootDir,
        Path modelsDir,
        Path generatedSourcesDir,
        Path generatedClassesDir,
        PlcSimulationEngine engine,
        AtomicLong lastAccessAt,
        AtomicLong unloadedAt,
        AtomicReference<Path> lastModelPath
) {
    public void touch(long now) {
        lastAccessAt.set(now);
        unloadedAt.set(0L);
    }

    public boolean isLoaded() {
        return engine.getCurrentModelPath() != null;
    }
}
