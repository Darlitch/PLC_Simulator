package runtime;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class JavaxGeneratedCodeCompiler implements IGeneratedCodeCompiler {
    private static final String ENTRYPOINT_CLASS = "Simulation.class";

    @Override
    public void compile(Path sourcesDir, Path classesDir) throws Exception {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new IllegalStateException("System Java compiler is not available. Use a JDK, not a JRE.");
        }

        if (!Files.exists(sourcesDir) || !Files.isDirectory(sourcesDir)) {
            throw new IllegalArgumentException("Sources directory does not exist: " + sourcesDir);
        }

        recreateDirectory(classesDir);

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

        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();

        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null)) {
            var compilationUnits = fileManager.getJavaFileObjectsFromPaths(javaFiles);

            List<String> options = List.of(
                    "-d", classesDir.toAbsolutePath().toString()
            );

            Boolean ok = compiler.getTask(
                    null,
                    fileManager,
                    diagnostics,
                    options,
                    null,
                    compilationUnits
            ).call();

            if (ok == null || !ok) {
                throw new IllegalStateException(buildCompilationErrorMessage(diagnostics));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to compile generated sources", e);
        }

        Path simulationClass = classesDir.resolve(ENTRYPOINT_CLASS);
        if (!Files.isRegularFile(simulationClass)) {
            throw new IllegalStateException(
                    "Compilation did not produce required entry point class: " + ENTRYPOINT_CLASS
            );
        }
    }

    private String buildCompilationErrorMessage(DiagnosticCollector<JavaFileObject> diagnostics) {
        String message = diagnostics.getDiagnostics().stream()
                .filter(diagnostic -> diagnostic.getKind() == Diagnostic.Kind.ERROR)
                .map(this::formatDiagnostic)
                .collect(Collectors.joining(System.lineSeparator()));

        if (message.isBlank()) {
            return "Compilation failed for generated Java sources.";
        }

        return "Compilation failed:" + System.lineSeparator() + message;
    }

    private String formatDiagnostic(Diagnostic<? extends JavaFileObject> diagnostic) {
        String fileName = diagnostic.getSource() == null
                ? "<unknown>"
                : Path.of(diagnostic.getSource().toUri()).getFileName().toString();

        long line = diagnostic.getLineNumber();
        String localizedMessage = diagnostic.getMessage(Locale.ROOT).strip();

        if (line > 0) {
            return fileName + ":" + line + ": " + localizedMessage;
        }

        return fileName + ": " + localizedMessage;
    }

    private void recreateDirectory(Path dir) throws IOException {
        if (Files.exists(dir)) {
            deleteRecursively(dir);
        }
        Files.createDirectories(dir);
    }

    private void deleteRecursively(Path dir) throws IOException {
        try (var stream = Files.walk(dir)) {
            stream.sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to delete: " + path, e);
                        }
                    });
        } catch (RuntimeException e) {
            if (e.getCause() instanceof IOException ioException) {
                throw ioException;
            }
            throw e;
        }
    }
}
