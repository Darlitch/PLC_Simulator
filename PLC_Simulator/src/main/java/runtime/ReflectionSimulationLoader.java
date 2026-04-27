package runtime;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;

public class ReflectionSimulationLoader implements ISimulationLoader {

    @Override
    public LoadResult load(Path classesDir) throws Exception {
        if (!Files.isDirectory(classesDir)) {
            throw new IllegalArgumentException("Compiled classes directory does not exist: " + classesDir);
        }

        URL[] urls = new URL[] { classesDir.toUri().toURL() };

        URLClassLoader classLoader = new URLClassLoader(
                urls,
                getClass().getClassLoader()
        );

        try {
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
        } catch (ClassNotFoundException exception) {
            throw new IllegalStateException(
                    "Compiled Simulation class was not found in: " + classesDir,
                    exception
            );
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException(
                    "Failed to instantiate compiled Simulation class.",
                    exception
            );
        }
    }
}
