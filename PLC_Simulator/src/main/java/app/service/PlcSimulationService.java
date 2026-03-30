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
    private final PlcSimulationEngine engine;
    private final Path modelsDir = Path.of("models");

    public PlcSimulationService(PlcSimulationEngine engine) {
        this.engine = engine;
    }

    public SimulationSnapshot loadModel(LoadModelRequest request) throws Exception {
        validateLoadRequest(request);

        Files.createDirectories(modelsDir);

        Path modelFile = writeModelFile(request.modelName(), request.source());
        engine.loadModel(modelFile);

        return engine.getSnapshot();
    }

    public SimulationSnapshot reloadCurrentModel() throws Exception {
        engine.reloadCurrentModel();
        return engine.getSnapshot();
    }

    public SimulationSnapshot start() {
        engine.start();
        return engine.getSnapshot();
    }

    public SimulationSnapshot pause() {
        engine.pause();
        return engine.getSnapshot();
    }

    public SimulationSnapshot resume() {
        engine.resume();
        return engine.getSnapshot();
    }

    public SimulationSnapshot stop() {
        engine.stop();
        return engine.getSnapshot();
    }

    public SimulationSnapshot step() {
        engine.step();
        return engine.getSnapshot();
    }

    public SimulationSnapshot updateInputs(Map<String, Object> values) {
        if (values == null) {
            throw new IllegalArgumentException("values must not be null");
        }

        engine.updateInputs(values);
        return engine.getSnapshot();
    }

    public SimulationSnapshot getSnapshot() {
        return engine.getSnapshot();
    }

    public SimulationStatus getStatus() {
        return engine.getStatus();
    }

    private Path writeModelFile(String modelName, String source) throws IOException {
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
