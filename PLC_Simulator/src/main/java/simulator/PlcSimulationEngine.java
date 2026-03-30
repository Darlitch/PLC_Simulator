package simulator;

import generator.IPostGeneratorRunner;
import runtime.SimulationManager;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PlcSimulationEngine {
    private final IPostGeneratorRunner postGeneratorRunner;
    private final SimulationManager simulationManager;
    private final Path generatedSourcesDir;
    private final Path generatedClassesDir;

    private Path currentModelPath;
    private SimulationStatus status = SimulationStatus.STOPPED;

    private ScheduledExecutorService executor;
    private long cyclePeriodMs = 1000L;

    public PlcSimulationEngine(
            IPostGeneratorRunner postGeneratorRunner,
            SimulationManager simulationManager,
            Path generatedSourcesDir,
            Path generatedClassesDir
    ) {
        this.postGeneratorRunner = postGeneratorRunner;
        this.simulationManager = simulationManager;
        this.generatedSourcesDir = generatedSourcesDir;
        this.generatedClassesDir = generatedClassesDir;
    }

    public void loadModel(Path postFile) throws Exception {
        stop();

        postGeneratorRunner.generate(postFile, generatedSourcesDir);
        simulationManager.loadFromCompiledSources(generatedSourcesDir, generatedClassesDir);
        currentModelPath = postFile;
        status = SimulationStatus.STOPPED;
    }

    public void reloadCurrentModel() throws Exception {
        if (currentModelPath == null) {
            throw new IllegalStateException("No model has been loaded yet");
        }

        loadModel(currentModelPath);
    }

    public void reloadModel(Path postFile) throws Exception {
        loadModel(postFile);
    }

    public void unload() {
        stop();
        simulationManager.unload();
        currentModelPath = null;
        status = SimulationStatus.STOPPED;
    }

    public Path getCurrentModelPath() {
        return currentModelPath;
    }

    public SimulationStatus getStatus() {
        return status;
    }

    public boolean isRunning() {
        return status == SimulationStatus.RUNNING;
    }

    public boolean isPaused() {
        return status == SimulationStatus.PAUSED;
    }

    public boolean isStopped() {
        return status == SimulationStatus.STOPPED;
    }

    public void setCyclePeriodMs(long cyclePeriodMs) {
        if (cyclePeriodMs <= 0) {
            throw new IllegalArgumentException("cyclePeriodMs must be > 0");
        }
        this.cyclePeriodMs = cyclePeriodMs;
    }

    public long getCyclePeriodMs() {
        return cyclePeriodMs;
    }

    public void start() {
        if (currentModelPath == null) {
            throw new IllegalStateException("No model loaded");
        }

        if (status == SimulationStatus.RUNNING) {
            return;
        }

        if (executor == null || executor.isShutdown()) {
            executor = Executors.newSingleThreadScheduledExecutor();
            executor.scheduleAtFixedRate(() -> {
                if (status == SimulationStatus.RUNNING) {
                    try {
                        simulationManager.step();
                    } catch (Exception e) {
                        e.printStackTrace();
                        status = SimulationStatus.STOPPED;
                    }
                }
            }, 0, cyclePeriodMs, TimeUnit.MILLISECONDS);
        }

        status = SimulationStatus.RUNNING;
    }

    public void pause() {
        if (status == SimulationStatus.RUNNING) {
            status = SimulationStatus.PAUSED;
        }
    }

    public void resume() {
        if (status == SimulationStatus.PAUSED) {
            status = SimulationStatus.RUNNING;
        }
    }

    public void stop() {
        status = SimulationStatus.STOPPED;

        if (executor != null) {
            executor.shutdownNow();
            executor = null;
        }
    }

    public void step() {
        simulationManager.step();
    }

    public void updateInputs(Map<String, Object> values) {
        simulationManager.updateInputs(values);
    }

    public Map<String, Object> dumpInputs() {
        return simulationManager.dumpInputs();
    }

    public Map<String, Object> dumpOutputs() {
        return simulationManager.dumpOutputs();
    }

    public Map<String, Object> dumpGlobals() {
        return simulationManager.dumpGlobals();
    }

    public Map<String, Object> dumpVars() {
        return simulationManager.dumpVars();
    }

    public Map<String, String> dumpProcessStates() {
        return simulationManager.dumpProcessStates();
    }

    public Map<String, Long> dumpProcessTimers() {
        return simulationManager.dumpProcessTimers();
    }
}
