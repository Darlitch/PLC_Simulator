package app.service;

import generator.IPostGeneratorRunner;
import org.springframework.stereotype.Component;
import runtime.IGeneratedCodeCompiler;
import runtime.ISimulationLoader;
import runtime.SimulationManager;
import simulator.PlcSimulationEngine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SimulationSessionManager {
    private final Map<String, SimulationSession> sessions = new ConcurrentHashMap<>();
    private final IPostGeneratorRunner postGeneratorRunner;
    private final IGeneratedCodeCompiler generatedCodeCompiler;
    private final ISimulationLoader simulationLoader;
    private final Path sessionsRootDir = Path.of("sessions");

    public SimulationSessionManager(
            IPostGeneratorRunner postGeneratorRunner,
            IGeneratedCodeCompiler generatedCodeCompiler,
            ISimulationLoader simulationLoader
    ) {
        this.postGeneratorRunner = postGeneratorRunner;
        this.generatedCodeCompiler = generatedCodeCompiler;
        this.simulationLoader = simulationLoader;
    }

    public SimulationSession getOrCreateSession(String sessionId) {
        String normalizedSessionId = normalizeSessionId(sessionId);
        return sessions.computeIfAbsent(normalizedSessionId, this::createSession);
    }

    private SimulationSession createSession(String sessionId) {
        try {
            Path sessionRootDir = sessionsRootDir.resolve(sessionId);
            Path modelsDir = sessionRootDir.resolve("models");
            Path generatedSourcesDir = sessionRootDir.resolve("generated-src");
            Path generatedClassesDir = sessionRootDir.resolve("generated-classes");

            Files.createDirectories(modelsDir);
            Files.createDirectories(generatedSourcesDir);
            Files.createDirectories(generatedClassesDir);

            SimulationManager simulationManager = new SimulationManager(generatedCodeCompiler, simulationLoader);
            PlcSimulationEngine engine = new PlcSimulationEngine(
                    postGeneratorRunner,
                    simulationManager,
                    generatedSourcesDir,
                    generatedClassesDir
            );

            return new SimulationSession(
                    sessionId,
                    sessionRootDir,
                    modelsDir,
                    generatedSourcesDir,
                    generatedClassesDir,
                    engine
            );
        } catch (IOException e) {
            throw new IllegalStateException("Failed to initialize session workspace for: " + sessionId, e);
        }
    }

    private String normalizeSessionId(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalArgumentException("Session-Id header must not be blank");
        }

        String value = sessionId.trim().replaceAll("[^a-zA-Z0-9._-]", "_");
        if (value.isBlank()) {
            throw new IllegalArgumentException("Session-Id header must not be blank");
        }

        return value;
    }
}
