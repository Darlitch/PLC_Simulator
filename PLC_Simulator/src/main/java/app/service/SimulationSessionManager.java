package app.service;

import app.SimulationSessionProperties;
import generator.IPostGeneratorRunner;
import org.springframework.scheduling.annotation.Scheduled;
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
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class SimulationSessionManager {
    private final Map<String, SimulationSession> sessions = new ConcurrentHashMap<>();
    private final IPostGeneratorRunner postGeneratorRunner;
    private final IGeneratedCodeCompiler generatedCodeCompiler;
    private final ISimulationLoader simulationLoader;
    private final SimulationSessionProperties properties;
    private final Path sessionsRootDir;

    public SimulationSessionManager(
            IPostGeneratorRunner postGeneratorRunner,
            IGeneratedCodeCompiler generatedCodeCompiler,
            ISimulationLoader simulationLoader,
            SimulationSessionProperties properties
    ) {
        this.postGeneratorRunner = postGeneratorRunner;
        this.generatedCodeCompiler = generatedCodeCompiler;
        this.simulationLoader = simulationLoader;
        this.properties = properties;
        this.sessionsRootDir = Path.of(properties.getRootDir());
    }

    public SimulationSession getOrCreateSession(String sessionId) {
        String normalizedSessionId = normalizeSessionId(sessionId);
        long now = System.currentTimeMillis();
        SimulationSession session = sessions.computeIfAbsent(normalizedSessionId, this::createSession);
        session.touch(now);
        return session;
    }

    @Scheduled(fixedDelay = 60000L)
    public void cleanupSessions() {
        long now = System.currentTimeMillis();

        for (SimulationSession session : sessions.values()) {
            long idleForMs = now - session.lastAccessAt().get();

            if (session.isLoaded() && idleForMs >= properties.getIdleUnloadAfter().toMillis()) {
                unloadSession(session, now);
            }

            long unloadedAt = session.unloadedAt().get();
            if (unloadedAt > 0 && now - unloadedAt >= properties.getDeleteAfterUnload().toMillis()) {
                deleteSession(session);
            }
        }
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

            long now = System.currentTimeMillis();
            return new SimulationSession(
                    sessionId,
                    sessionRootDir,
                    modelsDir,
                    generatedSourcesDir,
                    generatedClassesDir,
                    engine,
                    new AtomicLong(now),
                    new AtomicLong(0L),
                    new AtomicReference<>()
            );
        } catch (IOException e) {
            throw new IllegalStateException("Failed to initialize session workspace for: " + sessionId, e);
        }
    }

    private void unloadSession(SimulationSession session, long now) {
        synchronized (session) {
            if (!session.isLoaded()) {
                return;
            }

            Path currentModelPath = session.engine().getCurrentModelPath();
            if (currentModelPath != null) {
                session.lastModelPath().set(currentModelPath);
            }

            session.engine().unload();
            session.unloadedAt().compareAndSet(0L, now);
        }
    }

    private void deleteSession(SimulationSession session) {
        synchronized (session) {
            if (!sessions.remove(session.sessionId(), session)) {
                return;
            }

            try {
                deleteRecursively(session.sessionRootDir());
            } catch (IOException e) {
                throw new IllegalStateException("Failed to delete session workspace for: " + session.sessionId(), e);
            }
        }
    }

    private void deleteRecursively(Path dir) throws IOException {
        if (!Files.exists(dir)) {
            return;
        }

        try (var stream = Files.walk(dir)) {
            stream.sorted(java.util.Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to delete: " + path, e);
                        }
                    });
        } catch (RuntimeException e) {
            if (e.getCause() instanceof IOException ioException) {
                throw ioException;
            }
            throw e;
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
