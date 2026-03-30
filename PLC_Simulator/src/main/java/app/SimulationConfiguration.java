package app;

import generator.IPostGeneratorRunner;
import generator.JarPostGeneratorRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import runtime.IGeneratedCodeCompiler;
import runtime.ISimulationLoader;
import runtime.JavaxGeneratedCodeCompiler;
import runtime.ReflectionSimulationLoader;
import runtime.SimulationManager;
import simulator.PlcSimulationEngine;

import java.nio.file.Path;

@Configuration
public class SimulationConfiguration {

    @Bean
    public IPostGeneratorRunner postGeneratorRunner() {
        return new JarPostGeneratorRunner(
                Path.of("generator", "poST2JavaGen.jar")
        );
    }

    @Bean
    public IGeneratedCodeCompiler generatedCodeCompiler() {
        return new JavaxGeneratedCodeCompiler();
    }

    @Bean
    public ISimulationLoader simulationLoader() {
        return new ReflectionSimulationLoader();
    }

    @Bean
    public SimulationManager simulationManager(
            IGeneratedCodeCompiler compiler,
            ISimulationLoader loader
    ) {
        return new SimulationManager(compiler, loader);
    }

    @Bean
    public PlcSimulationEngine plcSimulationEngine(
            IPostGeneratorRunner postGeneratorRunner,
            SimulationManager simulationManager
    ) {
        return new PlcSimulationEngine(
                postGeneratorRunner,
                simulationManager,
                Path.of("generated-src"),
                Path.of("generated-classes")
        );
    }
}
