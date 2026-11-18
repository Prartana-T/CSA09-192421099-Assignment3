
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

public class ResultProcessor {

    private static final int TOTAL_FILES = 10000;
    private static final Path DIRECTORY =
            Paths.get("score_files");

    // Stores successful results safely across threads
    private static final ConcurrentLinkedQueue<String> results =
            new ConcurrentLinkedQueue<>();

    private static final ConcurrentLinkedQueue<String> failures =
            new ConcurrentLinkedQueue<>();

    // Task for processing one student's score file
    static class ScoreTask implements Callable<String> {

        private final Path file;

        ScoreTask(Path file) {
            this.file = file;
        }

        @Override
        public String call() throws Exception {

            if (!Files.exists(file)) {
                throw new IOException("File is missing");
            }

            String content = Files.readString(file).trim();

            try {
                int mark = Integer.parseInt(content);

                if (mark < 0 || mark > 100) {
                    throw new IllegalArgumentException(
                            "Invalid mark: " + mark);
                }

                String result = file.getFileName()
                        + " : " + (mark >= 50 ? "PASS" : "FAIL");

                results.add(result);
                return result;

            } catch (NumberFormatException e) {
                throw new IOException("Corrupt score data", e);
            }
        }
    }

    // Create test files before measuring processing time
    static void createTestFiles() throws IOException {

        Files.createDirectories(DIRECTORY);

        for (int i = 1; i <= TOTAL_FILES; i++) {

            Path file = DIRECTORY.resolve(
                    String.format("student%05d.txt", i));

            // Every 100th file is missing
            if (i % 100 == 0) {
                Files.deleteIfExists(file);
                continue;
            }

            // Every 99th file is corrupt
            if (i % 99 == 0) {
                Files.writeString(file, "INVALID");
                continue;
            }

            int mark = (i * 7) % 101;
            Files.writeString(file, String.valueOf(mark));
        }
    }

    static void processFiles(int poolSize)
            throws InterruptedException {

        results.clear();
        failures.clear();

        ExecutorService executor =
                Executors.newFixedThreadPool(poolSize);

        List<Future<String>> futures = new ArrayList<>();

        long startTime = System.nanoTime();

        try {
            for (int i = 1; i <= TOTAL_FILES; i++) {

                Path file = DIRECTORY.resolve(
                        String.format("student%05d.txt", i));

                futures.add(executor.submit(
                        new ScoreTask(file)));
            }

            // Collect every task's result or failure
            for (int i = 0; i < futures.size(); i++) {
                try {
                    futures.get(i).get();

                } catch (ExecutionException e) {
                    failures.add(
                            "student" + (i + 1)
                            + " : " + e.getCause().getMessage());
                }
            }

        } finally {
            executor.shutdown();
            executor.awaitTermination(
                    1, TimeUnit.MINUTES);
        }

        long endTime = System.nanoTime();
        double timeMs =
                (endTime - startTime) / 1_000_000.0;

        System.out.println("\nThread Pool Size: " + poolSize);
        System.out.println("Total files: " + TOTAL_FILES);
        System.out.println("Successful files: " + results.size());
        System.out.println("Failed files: " + failures.size());
        System.out.printf("Processing time: %.2f ms%n", timeMs);
    }

    public static void main(String[] args)
            throws Exception {

        System.out.println(
                "===== CONCURRENT RESULT PROCESSOR =====");

        System.out.println("Creating test score files...");
        createTestFiles();
        System.out.println("Test files created.");

        int[] poolSizes = {1, 2, 4, 8};

        for (int size : poolSizes) {
            processFiles(size);
        }

        System.out.println(
                "\n===== PROCESSING COMPLETED =====");
        System.out.println(
                "All files were processed or reported as failures.");
    }
}
