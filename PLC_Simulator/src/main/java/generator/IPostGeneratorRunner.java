package generator;

import java.nio.file.Path;

public interface IPostGeneratorRunner {
    void generate(Path postFile, Path outputSourcesDir) throws Exception;
}
