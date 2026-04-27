package app;

import generator.IPostGeneratorRunner;
import generator.JarPostGeneratorRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import runtime.IGeneratedCodeCompiler;
import runtime.ISimulationLoader;
import runtime.JavaxGeneratedCodeCompiler;
import runtime.ReflectionSimulationLoader;

import java.nio.file.Path;

@Configuration
@EnableConfigurationProperties(SimulationSessionProperties.class)
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
}
