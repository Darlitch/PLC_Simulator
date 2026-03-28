import runtime.*;

import java.nio.file.Path;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {
        Path sourcesDir = Path.of("generated-src");
        Path classesDir = Path.of("generated-classes");

        SimulationManager manager = new SimulationManager(
                new JavaxGeneratedCodeCompiler(),
                new ReflectionSimulationLoader()
        );

        manager.loadFromCompiledSources(sourcesDir, classesDir);

        System.out.println("Loaded simulation");

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
