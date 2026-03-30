package runtime;

import java.nio.file.Path;

public interface ISimulationLoader {
    LoadResult load(Path classesDir) throws Exception;

    record LoadResult(
            ClassLoader classLoader,
            ISimulationRuntime simulationRuntime
    ) {}
}
