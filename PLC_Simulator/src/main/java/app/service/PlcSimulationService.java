package app.service;

import app.api.dto.LoadModelRequest;
import org.springframework.stereotype.Service;
import simulator.PlcSimulationEngine;
import simulator.SimulationSnapshot;
import simulator.SimulationStatus;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@Service
public class PlcSimulationService {
    private final SimulationSessionManager sessionManager;

    public PlcSimulationService(SimulationSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public SimulationSnapshot loadModel(String sessionId, LoadModelRequest request) throws Exception {
        validateLoadRequest(request);

        SimulationSession session = sessionManager.getOrCreateSession(sessionId);
        Path modelFile = writeModelFile(session.modelsDir(), request.modelName(), request.source());
        session.engine().loadModel(modelFile);
        session.lastModelPath().set(modelFile);

        return session.engine().getSnapshot();
    }

    public SimulationSnapshot reloadCurrentModel(String sessionId) throws Exception {
        SimulationSession session = sessionManager.getOrCreateSession(sessionId);
        PlcSimulationEngine engine = restoreIfNeeded(session);
        engine.reloadCurrentModel();
        session.lastModelPath().set(engine.getCurrentModelPath());
        return engine.getSnapshot();
    }

    public SimulationSnapshot start(String sessionId) throws Exception {
        SimulationSession session = sessionManager.getOrCreateSession(sessionId);
        PlcSimulationEngine engine = restoreIfNeeded(session);
        engine.start();
        return engine.getSnapshot();
    }

    public SimulationSnapshot pause(String sessionId) throws Exception {
        SimulationSession session = sessionManager.getOrCreateSession(sessionId);
        PlcSimulationEngine engine = restoreIfNeeded(session);
        engine.pause();
        return snapshotOf(engine);
    }

    public SimulationSnapshot resume(String sessionId) throws Exception {
        SimulationSession session = sessionManager.getOrCreateSession(sessionId);
        PlcSimulationEngine engine = restoreIfNeeded(session);
        engine.resume();
        return snapshotOf(engine);
    }

    public SimulationSnapshot stop(String sessionId) throws Exception {
        SimulationSession session = sessionManager.getOrCreateSession(sessionId);
        PlcSimulationEngine engine = restoreIfNeeded(session);
        engine.reloadCurrentModel();
        session.lastModelPath().set(engine.getCurrentModelPath());
        return engine.getSnapshot();
    }

    public SimulationSnapshot step(String sessionId) throws Exception {
        SimulationSession session = sessionManager.getOrCreateSession(sessionId);
        PlcSimulationEngine engine = restoreIfNeeded(session);
        engine.step();
        return engine.getSnapshot();
    }

    public SimulationSnapshot updateInputs(String sessionId, Map<String, Object> values) throws Exception {
        if (values == null) {
            throw new IllegalArgumentException("values must not be null");
        }

        SimulationSession session = sessionManager.getOrCreateSession(sessionId);
        PlcSimulationEngine engine = restoreIfNeeded(session);
        engine.updateInputs(values);
        return engine.getSnapshot();
    }

    public SimulationSnapshot getSnapshot(String sessionId) throws Exception {
        SimulationSession session = sessionManager.getOrCreateSession(sessionId);
        PlcSimulationEngine engine = restoreIfNeeded(session);
        return snapshotOf(engine);
    }

    public SimulationStatus getStatus(String sessionId) throws Exception {
        SimulationSession session = sessionManager.getOrCreateSession(sessionId);
        PlcSimulationEngine engine = restoreIfNeeded(session);
        if (engine.getCurrentModelPath() == null) {
            return SimulationStatus.STOPPED;
        }
        return engine.getStatus();
    }

    private PlcSimulationEngine restoreIfNeeded(SimulationSession session) throws Exception {
        PlcSimulationEngine engine = session.engine();
        if (engine.getCurrentModelPath() != null) {
            return engine;
        }

        Path lastModelPath = session.lastModelPath().get();
        if (lastModelPath == null || !Files.exists(lastModelPath) || !hasCompiledClasses(session.generatedClassesDir())) {
            return engine;
        }

        synchronized (session) {
            engine = session.engine();
            if (engine.getCurrentModelPath() != null) {
                return engine;
            }

            Path candidateModelPath = session.lastModelPath().get();
            if (candidateModelPath == null || !Files.exists(candidateModelPath) || !hasCompiledClasses(session.generatedClassesDir())) {
                return engine;
            }

            engine.restoreModel(candidateModelPath);
            session.lastModelPath().set(candidateModelPath);
            return engine;
        }
    }

    private boolean hasCompiledClasses(Path generatedClassesDir) throws IOException {
        if (!Files.isDirectory(generatedClassesDir)) {
            return false;
        }

        try (var stream = Files.walk(generatedClassesDir)) {
            return stream.anyMatch(path -> Files.isRegularFile(path) && path.getFileName().toString().endsWith(".class"));
        }
    }

    private SimulationSnapshot snapshotOf(PlcSimulationEngine engine) {
        if (engine.getCurrentModelPath() == null) {
            return new SimulationSnapshot(
                    null,
                    SimulationStatus.STOPPED,
                    Map.of(),
                    Map.of(),
                    Map.of(),
                    Map.of(),
                    Map.of(),
                    Map.of()
            );
        }

        return engine.getSnapshot();
    }

    private Path writeModelFile(Path modelsDir, String modelName, String source) throws IOException {
        Files.createDirectories(modelsDir);

        String fileName = sanitizeModelName(modelName);
        Path modelFile = modelsDir.resolve(fileName + ".post");
        Files.writeString(modelFile, source, StandardCharsets.UTF_8);
        return modelFile;
    }

    private void validateLoadRequest(LoadModelRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request must not be null");
        }

        if (request.source() == null || request.source().isBlank()) {
            throw new IllegalArgumentException("source must not be blank");
        }
    }

    private String sanitizeModelName(String modelName) {
        String value = (modelName == null || modelName.isBlank())
                ? "runtime-input"
                : modelName.trim();

        value = value.replaceAll("[^a-zA-Z0-9._-]", "_");

        if (value.isBlank()) {
            value = "runtime-input";
        }

        return value;
    }
}
