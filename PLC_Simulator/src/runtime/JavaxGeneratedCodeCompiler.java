package runtime;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class JavaxGeneratedCodeCompiler implements IGeneratedCodeCompiler {

    @Override
    public void compile(Path sourcesDir, Path classesDir) throws Exception {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new IllegalStateException("System Java compiler is not available. Use a JDK, not a JRE.");
        }

        if (!Files.exists(sourcesDir) || !Files.isDirectory(sourcesDir)) {
            throw new IllegalArgumentException("Sources directory does not exist: " + sourcesDir);
        }

        Files.createDirectories(classesDir);

        List<Path> javaFiles;
        try (var stream = Files.walk(sourcesDir)) {
            javaFiles = stream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .collect(Collectors.toList());
        }

        if (javaFiles.isEmpty()) {
            throw new IllegalStateException("No Java source files found in: " + sourcesDir);
        }

        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null)) {
            var compilationUnits = fileManager.getJavaFileObjectsFromPaths(javaFiles);

            List<String> options = List.of(
                    "-d", classesDir.toAbsolutePath().toString()
            );

            Boolean ok = compiler.getTask(
                    null,
                    fileManager,
                    null,
                    options,
                    null,
                    compilationUnits
            ).call();

            if (ok == null || !ok) {
                throw new IllegalStateException("Compilation failed for sources in: " + sourcesDir);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to compile generated sources", e);
        }
    }
}
