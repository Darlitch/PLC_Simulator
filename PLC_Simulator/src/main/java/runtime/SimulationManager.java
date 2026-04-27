package runtime;

import java.nio.file.Path;
import java.util.Map;

public class SimulationManager {
    private final IGeneratedCodeCompiler compiler;
    private final ISimulationLoader loader;

    private ISimulationRuntime simulationRuntime;
    private ClassLoader runtimeClassLoader;

    public SimulationManager(IGeneratedCodeCompiler compiler, ISimulationLoader loader) {
        this.compiler = compiler;
        this.loader = loader;
    }

    public void loadFromCompiledSources(Path sourcesDir, Path classesDir) throws Exception {
        compiler.compile(sourcesDir, classesDir);
        loadFromCompiledClasses(classesDir);
    }

    public void loadFromCompiledClasses(Path classesDir) throws Exception {
        unload();

        ISimulationLoader.LoadResult result = loader.load(classesDir);
        this.runtimeClassLoader = result.classLoader();
        this.simulationRuntime = result.simulationRuntime();
    }

    public void step() {
        ensureLoaded();
        simulationRuntime.step();
    }

    public void updateInputs(Map<String, Object> values) {
        ensureLoaded();
        simulationRuntime.updateInputs(values);
    }

    public Map<String, Object> dumpInputs() {
        ensureLoaded();
        return simulationRuntime.dumpInputs();
    }

    public Map<String, Object> dumpOutputs() {
        ensureLoaded();
        return simulationRuntime.dumpOutputs();
    }

    public Map<String, Object> dumpGlobals() {
        ensureLoaded();
        return simulationRuntime.dumpGlobals();
    }

    public Map<String, Object> dumpVars() {
        ensureLoaded();
        return simulationRuntime.dumpVars();
    }

    public Map<String, String> dumpProcessStates() {
        ensureLoaded();
        return simulationRuntime.dumpProcessStates();
    }

    public Map<String, Long> dumpProcessTimers() {
        ensureLoaded();
        return simulationRuntime.dumpProcessTimers();
    }

    public void unload() {
        simulationRuntime = null;
        runtimeClassLoader = null;
    }

    private void ensureLoaded() {
        if (simulationRuntime == null) {
            throw new IllegalStateException("Simulation is not loaded");
        }
    }
}
