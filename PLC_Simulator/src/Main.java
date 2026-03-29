import generator.IPostGeneratorRunner;
import generator.JarPostGeneratorRunner;
import runtime.*;

import java.nio.file.Path;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {
        Path generatorJar = Path.of("generator", "poST2JavaGen.jar");
        Path postFile = Path.of("models", "Post.post");
        Path generatedSourcesDir = Path.of("generated-src");
        Path generatedClassesDir = Path.of("generated-classes");

        IPostGeneratorRunner generatorRunner = new JarPostGeneratorRunner(generatorJar);

        SimulationManager manager = new SimulationManager(
                new JavaxGeneratedCodeCompiler(),
                new ReflectionSimulationLoader()
        );

        generatorRunner.generate(postFile, generatedSourcesDir);
        manager.loadFromCompiledSources(generatedSourcesDir, generatedClassesDir);

        System.out.println("Loaded simulation from: " + postFile);
        System.out.println("Initial inputs: " + manager.dumpInputs());
        System.out.println("Initial outputs: " + manager.dumpOutputs());
        System.out.println("Initial states: " + manager.dumpProcessStates());

        manager.step();

        System.out.println("After step inputs: " + manager.dumpInputs());
        System.out.println("After step outputs: " + manager.dumpOutputs());
        System.out.println("After step globals: " + manager.dumpGlobals());
        System.out.println("After step vars: " + manager.dumpVars());
        System.out.println("After step states: " + manager.dumpProcessStates());
        System.out.println("After step timers: " + manager.dumpProcessTimers());

        manager.updateInputs(Map.of(
                "sensor", true
        ));

        manager.step();

        System.out.println("After input update and step:");
        System.out.println("Inputs: " + manager.dumpInputs());
        System.out.println("Outputs: " + manager.dumpOutputs());
        System.out.println("Globals: " + manager.dumpGlobals());
        System.out.println("Vars: " + manager.dumpVars());
        System.out.println("States: " + manager.dumpProcessStates());
        System.out.println("Timers: " + manager.dumpProcessTimers());
    }
}
