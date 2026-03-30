package runtime;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;

public class ReflectionSimulationLoader implements ISimulationLoader {

    @Override
    public LoadResult load(Path classesDir) throws Exception {
        URL[] urls = new URL[] { classesDir.toUri().toURL() };

        URLClassLoader classLoader = new URLClassLoader(
                urls,
                getClass().getClassLoader()
        );

        Class<?> simulationClass = classLoader.loadClass("Simulation");
        Object instance = simulationClass.getDeclaredConstructor().newInstance();

        if (!(instance instanceof ISimulationRuntime simulationRuntime)) {
            throw new IllegalStateException(
                    "Loaded Simulation does not implement ISimulationRuntime"
            );
        }

        return new LoadResult(
                classLoader,
                simulationRuntime
        );
    }
}
