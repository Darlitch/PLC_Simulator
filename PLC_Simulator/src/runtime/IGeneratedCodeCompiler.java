package runtime;

import java.nio.file.Path;

public interface IGeneratedCodeCompiler {
    void compile(Path sourcesDir, Path classesDir) throws Exception;
}
