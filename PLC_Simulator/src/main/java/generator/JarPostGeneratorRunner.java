package generator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class JarPostGeneratorRunner implements IPostGeneratorRunner {
    private final Path generatorJar;

    public JarPostGeneratorRunner(Path generatorJar) {
        this.generatorJar = generatorJar;
    }

    @Override
    public void generate(Path postFile, Path outputSourcesDir) throws Exception {
        if (!Files.exists(generatorJar) || !Files.isRegularFile(generatorJar)) {
            throw new IllegalArgumentException("Generator jar not found: " + generatorJar);
        }

        if (!Files.exists(postFile) || !Files.isRegularFile(postFile)) {
            throw new IllegalArgumentException("PoST file not found: " + postFile);
        }

        recreateDirectory(outputSourcesDir);

        List<String> command = new ArrayList<>();
        command.add("java");
        command.add("-jar");
        command.add(generatorJar.toAbsolutePath().toString());
        command.add(postFile.toAbsolutePath().toString());
        command.add("-o=" + outputSourcesDir.toString());

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(Path.of("").toAbsolutePath().toFile());
        pb.redirectErrorStream(true);

        Process process = pb.start();

        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append(System.lineSeparator());
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IllegalStateException(
                    "Generator process failed with exit code " + exitCode +
                            System.lineSeparator() +
                            output
            );
        }

        if (!containsJavaFiles(outputSourcesDir)) {
            throw new IllegalStateException(
                    "Generator finished successfully, but no .java files were created in: " +
                            outputSourcesDir + System.lineSeparator() + output
            );
        }
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

    private boolean containsJavaFiles(Path dir) throws IOException {
        try (var stream = Files.walk(dir)) {
            return stream.anyMatch(path ->
                    Files.isRegularFile(path) && path.toString().endsWith(".java"));
        }
    }
}
