import generator.IPostGeneratorRunner;
import generator.JarPostGeneratorRunner;
import runtime.JavaxGeneratedCodeCompiler;
import runtime.ReflectionSimulationLoader;
import runtime.SimulationManager;
import simulator.PlcSimulationEngine;

import java.nio.file.Path;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {
        Path generatorJar = Path.of("generator", "poST2JavaGen.jar");
        Path postFile = Path.of("models", "Post.post");
        Path generatedSourcesDir = Path.of("generated-src");
        Path generatedClassesDir = Path.of("generated-classes");

        IPostGeneratorRunner generatorRunner = new JarPostGeneratorRunner(generatorJar);

        SimulationManager simulationManager = new SimulationManager(
                new JavaxGeneratedCodeCompiler(),
                new ReflectionSimulationLoader()
        );

        PlcSimulationEngine engine = new PlcSimulationEngine(
                generatorRunner,
                simulationManager,
                generatedSourcesDir,
                generatedClassesDir
        );

        engine.loadModel(postFile);

        System.out.println("Loaded simulation from: " + postFile);
        System.out.println("Initial status: " + engine.getStatus());
        System.out.println("Initial states: " + engine.dumpProcessStates());
        System.out.println("Initial timers: " + engine.dumpProcessTimers());

        engine.start();
        System.out.println("Started. Status: " + engine.getStatus());

        Thread.sleep(3000);

        System.out.println("After ~3 seconds running:");
        System.out.println("States: " + engine.dumpProcessStates());
        System.out.println("Timers: " + engine.dumpProcessTimers());
        System.out.println("Outputs: " + engine.dumpOutputs());

        engine.pause();
        System.out.println("Paused. Status: " + engine.getStatus());

        Map<String, Long> timersBeforePauseWait = engine.dumpProcessTimers();
        Thread.sleep(2000);
        Map<String, Long> timersAfterPauseWait = engine.dumpProcessTimers();

        System.out.println("After ~2 seconds paused:");
        System.out.println("Timers before pause wait: " + timersBeforePauseWait);
        System.out.println("Timers after pause wait: " + timersAfterPauseWait);

        engine.updateInputs(Map.of(
                "sensor", true
        ));
        System.out.println("Updated inputs while paused: " + engine.dumpInputs());

        engine.resume();
        System.out.println("Resumed. Status: " + engine.getStatus());

        Thread.sleep(12000);

        System.out.println("After ~12 seconds resumed:");
        System.out.println("States: " + engine.dumpProcessStates());
        System.out.println("Timers: " + engine.dumpProcessTimers());
        System.out.println("Outputs: " + engine.dumpOutputs());

        engine.stop();
        System.out.println("Stopped. Status: " + engine.getStatus());

        System.out.println("Final states: " + engine.dumpProcessStates());
        System.out.println("Final timers: " + engine.dumpProcessTimers());
    }
}
