package baltool.logicapps.codegenerator;

import baltool.logicapps.authentication.CLIAuthentication;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.ballerina.projects.ModuleDescriptor;
import io.ballerina.projects.ModuleName;
import io.ballerina.projects.PackageDescriptor;
import io.ballerina.projects.PackageName;
import io.ballerina.projects.PackageOrg;
import io.ballerina.projects.PackageVersion;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

import static baltool.logicapps.Constants.BALLERINA_TOML_FILE;
import static baltool.logicapps.Constants.CONTENT;
import static baltool.logicapps.Constants.DEFAULT_ORG_NAME;
import static baltool.logicapps.Constants.DEFAULT_PROJECT_VERSION;
import static baltool.logicapps.Constants.FILE_PATH;

public class LogicAppsMigrationExecutor {
    private static final ReentrantLock progressLock = new ReentrantLock();
    private static final ConcurrentHashMap<String, ProgressTracker> progressMap = new ConcurrentHashMap<>();
    private static final AtomicInteger completedFiles = new AtomicInteger(0);
    private static int totalFiles = 0;
    private static int lastDisplayLines = 0;

    // Testing
    public static void main(String[] args) {
        // Arguments
        Path logicAppFilePath = Paths.get("/Users/nipunal/wso2/logic-apps", "ChckinV5ExpndTimeChiledWF.json");
        String additionalInstructions = "";
//        Path projectRootDir = Paths.get("/Users/nipunal/wso2/logic-apps/logicapps-migration-sample-1");
        boolean verbose = true;
        boolean multiRoot = false;

//        LogicAppsMigrationExecutor.migrateLogicAppToBallerina(logicAppFilePath, additionalInstructions,
//                projectRootDir, verbose, multiRoot, new VerboseLogger(verbose));
        LogicAppsMigrationExecutor.migrateLogicAppToBallerina(logicAppFilePath, additionalInstructions,
                verbose, multiRoot, new VerboseLogger(verbose));
    }

    /**
     * Migrates a Logic App JSON file to a Ballerina project.
     *
     * @param logicAppFilePath        the path to the Logic App JSON file or directory containing multiple files.
     * @param additionalInstructions  additional instructions for the migration process.
     * @param verbose                 whether to enable verbose logging.
     * @param multiRoot              whether to process multiple logic app files concurrently.
     */
    public static void migrateLogicAppToBallerina(Path logicAppFilePath, String additionalInstructions,
                                                  Path targetDir, boolean verbose, boolean multiRoot,
                                                  VerboseLogger logger) {
        try {
            logger.printVerboseInfo("Starting migration with specified output directory: " +
                    targetDir.toString());

            String projectName = logicAppFilePath.getFileName().toString().replace(".json", "_ballerina");
            Path projectRootDir = targetDir.resolve(projectName);

            logger.printVerboseInfo("Obtaining access token");
            String copilotAccessToken = getAccessToken(logger);
            if (copilotAccessToken == null) {
                logger.printVerboseInfo("Access token is null, terminating migration");
                return;
            }
            logger.printVerboseInfo("Access token obtained successfully");

            if (multiRoot && Files.isDirectory(logicAppFilePath)) {
                logger.printVerboseInfo("Multi-root mode enabled, processing directory: " + logicAppFilePath);
                processMultipleLogicApps(logicAppFilePath, additionalInstructions, projectRootDir,
                        copilotAccessToken, verbose, logger);
            } else {
                logger.printVerboseInfo("Single file mode, processing: " + logicAppFilePath);
                processSingleLogicApp(logicAppFilePath, additionalInstructions, projectRootDir, copilotAccessToken,
                        false, new VerboseLogger(verbose, logicAppFilePath.getFileName().toString(),
                                new ProgressBar(3, logicAppFilePath.getFileName().toString())));
            }
        } catch (IOException e) {
            logger.printError("Error during migration process: " + e.getMessage());
            logger.printVerboseError("Stack trace: \n" + Arrays.toString(e.getStackTrace()));
        } catch (Exception e) {
            logger.printError("Unexpected error during migration process: " + e.getMessage());
            logger.printVerboseError("Stack trace: \n" + Arrays.toString(e.getStackTrace()));
        }
    }

    /**
     * Migrates a Logic App JSON file to a Ballerina project.
     *
     * @param logicAppFilePath        the path to the Logic App JSON file or directory containing multiple files.
     * @param additionalInstructions  additional instructions for the migration process.
     * @param verbose                 whether to enable verbose logging.
     * @param multiRoot              whether to process multiple logic app files concurrently.
     */
    public static void migrateLogicAppToBallerina(Path logicAppFilePath, String additionalInstructions,
                                                  boolean verbose, boolean multiRoot, VerboseLogger logger) {
        try {
            logger.printVerboseInfo("Starting migration with current directory");

            logger.printVerboseInfo("Obtaining access token");
            String copilotAccessToken = getAccessToken(logger);
            if (copilotAccessToken == null) {
                logger.printVerboseInfo("Access token is null, terminating migration");
                return;
            }
            logger.printVerboseInfo("Access token obtained successfully");

            String projectName = logicAppFilePath.getFileName().toString().replace(".json", "_ballerina");
            logger.printVerboseInfo("Project name derived from file: " + projectName);
            Path projectBasePath = Files.isDirectory(logicAppFilePath) ? logicAppFilePath :
                    logicAppFilePath.getParent();
            Path projectRootDir = projectBasePath.resolve(projectName);
            logger.printVerboseInfo("Project root directory: " + projectRootDir.toAbsolutePath());

            if (multiRoot && Files.isDirectory(logicAppFilePath)) {
                logger.printVerboseInfo("Multi-root mode enabled, processing directory: " + logicAppFilePath);
                processMultipleLogicApps(logicAppFilePath, additionalInstructions, projectRootDir,
                        copilotAccessToken, verbose, logger);
            } else {
                logger.printVerboseInfo("Single file mode, processing: " + logicAppFilePath);
                processSingleLogicApp(logicAppFilePath, additionalInstructions, projectRootDir, copilotAccessToken,
                         false, new VerboseLogger(verbose, logicAppFilePath.getFileName().toString(),
                                new ProgressBar(3, logicAppFilePath.getFileName().toString())));
            }
        } catch (IOException e) {
            logger.printError("Error during migration process: " + e.getMessage());
            logger.printVerboseError("Stack trace: ");
            logger.printVerboseError("Stack trace: \n" + Arrays.toString(e.getStackTrace()));
        } catch (Exception e) {
            logger.printError("Unexpected error during migration process: " + e.getMessage());
            logger.printVerboseError("Stack trace: \n" + Arrays.toString(e.getStackTrace()));
        }
    }

    /**
     * Processes multiple logic app files concurrently with proper progress tracking.
     */
    private static void processMultipleLogicApps(Path logicAppDirectory, String additionalInstructions,
                                                 Path projectRootDir, String copilotAccessToken, boolean verbose,
                                                 VerboseLogger logger)
            throws IOException {

        logger.printVerboseInfo("MULTI-ROOT PROCESSING MODE");
        logger.printVerboseInfo("Scanning directory " + logicAppDirectory.toAbsolutePath() + " for JSON files...");
        List<Path> logicAppFiles = new ArrayList<>();
        try (Stream<Path> files = Files.walk(logicAppDirectory)) {
            files.filter(Files::isRegularFile)
                    .filter(path -> path.toString().toLowerCase().endsWith(".json"))
                    .forEach(file -> {
                        logicAppFiles.add(file);
                        logger.printVerboseInfo("Found: " + file.getFileName());
                    });
        }

        if (logicAppFiles.isEmpty()) {
            logger.printError("No JSON files found in directory: " + logicAppDirectory);
            return;
        }

        totalFiles = logicAppFiles.size();
        logger.printInfo("Found " + totalFiles + " logic app files to process");

        // Initialize progress tracking
        progressMap.clear();
        completedFiles.set(0);

        logger.printVerboseInfo("Initializing progress tracking for " + totalFiles + " files...");
        for (Path logicAppFile : logicAppFiles) {
            String fileName = logicAppFile.getFileName().toString();
            progressMap.put(fileName, new ProgressTracker(fileName));
            logger.printVerboseInfo("Registered: " + fileName + "for progress tracking");
        }

        logger.printVerboseInfo("Starting progress display thread...");
        Thread progressDisplayThread = startProgressDisplayThread(logger);

        int threadPoolSize = Math.min(logicAppFiles.size(), Runtime.getRuntime().availableProcessors());
        logger.printVerboseInfo("Available processors: " + Runtime.getRuntime().availableProcessors());
        ExecutorService executorService = Executors.newFixedThreadPool(threadPoolSize);

        try {
            List<CompletableFuture<Void>> futures = new ArrayList<>();
            for (Path logicAppFile : logicAppFiles) {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try {
                        String fileName = logicAppFile.getFileName().toString();
                        String projectName = fileName.replace(".json", "");
                        Path individualProjectDir = projectRootDir.resolve(projectName);

                        updateFileProgress(fileName, 1, "Generating execution plan");

                        processSingleLogicApp(logicAppFile, additionalInstructions, individualProjectDir,
                                copilotAccessToken, true,
                                new VerboseLogger(verbose, fileName, new ProgressBar(3, fileName)));

                        markFileCompleted(fileName);
                    } catch (Exception e) {
                        String fileName = logicAppFile.getFileName().toString();
                        String errorMsg = e.getMessage() != null ? e.getMessage() : "Unknown error";
                        markFileFailed(fileName, errorMsg);

                        if (verbose) {
                            synchronized (logger.getErrorPrintStream()) {
                                logger.printError("[THREAD-" + Thread.currentThread().getId() + "] ERROR processing "
                                        + fileName + ":");
                                logger.printError("  Stack trace: \n" + Arrays.toString(e.getStackTrace()));
                            }
                        } else {
                            logger.printError("\nError processing " + logicAppFile + ": " + errorMsg);
                        }
                    }
                }, executorService);

                futures.add(future);
            }
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                    futures.toArray(new CompletableFuture[0])
            );

            allFutures.join();

            // Give a small delay to ensure all progress updates are complete
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            logger.printVerboseInfo("All tasks completed");

        } finally {
            // Stop progress display
            if (progressDisplayThread.isAlive()) {
                logger.printVerboseInfo("Stopping progress display thread...");
                progressDisplayThread.interrupt();
                try {
                    progressDisplayThread.join(1000); // Wait up to 1 second
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            // Clear the progress display area
            if (lastDisplayLines > 0) {
                logger.getPrintStream().printf("\033[%dA", lastDisplayLines); // Move cursor up
                logger.getPrintStream().print("\033[J"); // Clear from cursor to end of screen
            }

            // Shutdown the executor service
            logger.printVerboseInfo("Shutting down thread pool...");
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                    logger.printVerboseInfo("Thread pool didn't terminate within 60 seconds, forcing shutdown...");
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
            logger.printVerboseInfo("Thread pool shutdown completed");
        }

        // Final status of each process
        displayFinalProgressFresh(logger);

        // Final summary
        logger.printInfo("\nMigration Summary:");
        logger.printInfo("Total files: " + totalFiles);
        logger.printInfo("Completed: " + completedFiles.get());
        logger.printInfo("Failed: " + (totalFiles - completedFiles.get()));
    }

    /**
     * Starts a background thread to display progress for all files.
     */
    private static Thread startProgressDisplayThread(VerboseLogger logger) {
        Thread progressThread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    displayOverallProgress(logger);
                    Thread.sleep(500); // Update every 500ms
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        progressThread.setDaemon(true);
        progressThread.start();
        return progressThread;
    }

    /**
     * Displays the final progress state without any cursor manipulation - fresh display.
     */
    private static void displayFinalProgressFresh(VerboseLogger logger) {
        progressLock.lock();
        try {
            logger.printInfo("File Results:");
            logger.printInfo("-".repeat(80));
            if (!progressMap.isEmpty()) {
                progressMap.entrySet().stream().sorted(Map.Entry.comparingByKey())
                        .forEach(entry -> logger.getPrintStream().printf(entry.getValue().getDisplayString() + "\n"));
            } else {
                logger.printInfo("No results available");
            }
            logger.printInfo("-".repeat(80));
            logger.printInfo("");
            logger.getPrintStream().flush();
        } finally {
            progressLock.unlock();
        }
    }

    private static void displayOverallProgress(VerboseLogger logger) {
        progressLock.lock();
        try {
            // Move cursor up to overwrite previous display
            if (lastDisplayLines > 0) {
                logger.getPrintStream().printf("\033[%dA", lastDisplayLines); // Move cursor up
                logger.getPrintStream().print("\033[J"); // Clear from cursor to end of screen
            }

            int linesCount = 0;

            // Overall progress
            int completed = completedFiles.get();
            String overallBar = createProgressBar(completed, totalFiles);
            logger.getPrintStream().printf("Overall Progress: [%d/%d] %s%n", completed, totalFiles, overallBar);
            linesCount++;

            logger.printInfo(""); // Empty line
            linesCount++;

            // Individual file progress
            logger.printInfo("Code generation Progress:");
            linesCount++;

            List<ProgressTracker> sortedTrackers = progressMap.values().stream()
                    .sorted((a, b) -> a.fileName.compareTo(b.fileName))
                    .toList();

            for (ProgressTracker tracker : sortedTrackers) {
                logger.printInfo(tracker.getDisplayString());
                linesCount++;
            }

            lastDisplayLines = linesCount;
            logger.getPrintStream().flush();
        } finally {
            progressLock.unlock();
        }
    }

    /**
     * Updates progress for a specific file.
     */
    private static void updateFileProgress(String fileName, int step, String message) {
        ProgressTracker tracker = progressMap.get(fileName);
        if (tracker != null) {
            tracker.updateProgress(step, message);
        }
    }

    /**
     * Marks a file as failed.
     */
    private static void markFileFailed(String fileName, String errorMessage) {
        ProgressTracker tracker = progressMap.get(fileName);
        if (tracker != null) {
            tracker.updateProgress(0, "Failed: " + errorMessage);
        }
    }

    /**
     * Marks a file as completed.
     */
    private static void markFileCompleted(String fileName) {
        ProgressTracker tracker = progressMap.get(fileName);
        if (tracker != null) {
            tracker.markCompleted();
            completedFiles.incrementAndGet();
        }
    }

    /**
     * Processes a single logic app file with progress callbacks for multi-threaded mode.
     */
    private static void processSingleLogicApp(Path logicAppFilePath, String additionalInstructions,
                                              Path projectRootDir, String copilotAccessToken, boolean isMultiThreaded,
                                              VerboseLogger logger) {
        try {
            String fileName = logicAppFilePath.getFileName().toString();

            if (!isMultiThreaded) {
                logger.printVerboseInfo("SINGLE FILE PROCESSING MODE");
            }
            logger.printVerboseInfo("Processing file: " + logicAppFilePath.toAbsolutePath());
            if (!Files.exists(logicAppFilePath)) {
                throw new IOException("Logic App file does not exist: " + logicAppFilePath);
            }
            if (!Files.isReadable(logicAppFilePath)) {
                throw new IOException("Logic App file is not readable: " + logicAppFilePath);
            }
            logger.printVerboseInfo("File validation successful");
            logger.printVerboseInfo("File size: " + Files.size(logicAppFilePath) + " bytes");

            String packageName = URLEncoder.encode(projectRootDir.getFileName().toString(),
                    StandardCharsets.UTF_8).replace("-", "_");
            logger.printVerboseInfo("Package name: " + packageName);

            ModuleDescriptor moduleDescriptor = getModuleDescriptor(packageName);

            JsonArray generatedSourceFiles = CodeGenerationUtils.generateCodeForLogicApp(copilotAccessToken,
                    logicAppFilePath, packageName, additionalInstructions, moduleDescriptor,
                    isMultiThreaded ? (step, message) -> updateFileProgress(fileName, step, message) : null, logger);
            logger.printVerboseInfo("Code generation completed. Generated " + generatedSourceFiles.size() +
                    " source files");

            if (isMultiThreaded) {
                updateFileProgress(fileName, 3, "Writing files");
            }

            if (!generatedSourceFiles.isEmpty()) {
                createProjectFromGeneratedSource(generatedSourceFiles, moduleDescriptor, projectRootDir, logger);
                logger.printVerboseInfo("Ballerina Project created successfully");
            } else {
                logger.printVerboseWarn("No source files were generated");
            }
        } catch (IOException e) {
            String errorMsg = "IO error processing logic app file " + logicAppFilePath + ": " + e.getMessage();
            logger.printError(errorMsg);
            logger.printVerboseError("Stack trace: \n" + Arrays.toString(e.getStackTrace()));
            throw new RuntimeException(e);
        } catch (Exception e) {
            String errorMsg = "Unexpected error processing logic app file " + logicAppFilePath + ": " + e.getMessage();
            logger.printError(errorMsg);
            logger.printVerboseError("Stack trace: \n" + Arrays.toString(e.getStackTrace()));
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a mini progress bar for individual file tracking.
     */
    private static String createMiniProgressBar(int current, int total) {
        int barLength = 10;
        int filled = (int) ((double) current / total * barLength);

        StringBuilder bar = new StringBuilder("[");
        for (int i = 0; i < barLength; i++) {
            bar.append(i < filled ? "█" : "░");
        }
        bar.append("]");

        return bar.toString();
    }

    /**
     * Creates a standard progress bar.
     */
    private static String createProgressBar(int completed, int total) {
        if (total == 0) {
            return "[██████████████████████]";
        }

        int barLength = 20;
        int filledLength = (int) ((double) completed / total * barLength);

        StringBuilder bar = new StringBuilder("[");
        for (int i = 0; i < barLength; i++) {
            bar.append(i < filledLength ? "█" : "░");
        }
        bar.append("]");

        return bar.toString();
    }

    private static ModuleDescriptor getModuleDescriptor(String packageName) throws UnsupportedEncodingException {
        ModuleName moduleName = ModuleName.from(PackageName.from(packageName));
        PackageDescriptor packageDescriptor = PackageDescriptor.from(PackageOrg.from(DEFAULT_ORG_NAME),
                moduleName.packageName(), PackageVersion.from(DEFAULT_PROJECT_VERSION));
        return ModuleDescriptor.from(moduleName, packageDescriptor);
    }

    private static String getAccessToken(VerboseLogger logger) {
        try {
            logger.printVerboseInfo("Attempting to retrieve access token via CLI authentication");
            String token = CLIAuthentication.getValidAccessToken(logger);
            logger.printVerboseInfo("Access token retrieved successfully");
            return token;
        } catch (Exception e) {
            logger.printError("Error retrieving access token: " + e.getMessage());
            logger.printVerboseError("Stack trace: \n" + Arrays.toString(e.getStackTrace()));
            return null;
        }
    }

    private static String getCurrentDirectory() {
        String currentDir = System.getProperty("user.dir");
        return currentDir;
    }

    private static void createProjectFromGeneratedSource(JsonArray sourceFiles, ModuleDescriptor moduleDescriptor,
                                                         Path projectDir, VerboseLogger logger) throws IOException {
        Files.createDirectories(projectDir);

        for (JsonElement sourceFile : sourceFiles) {
            JsonObject sourceFileObj = sourceFile.getAsJsonObject();
            String filePath = sourceFileObj.get(FILE_PATH).getAsString();
            String content = sourceFileObj.get(CONTENT).getAsString();

            File file = Files.createFile(projectDir.resolve(Paths.get(filePath))).toFile();

            try (FileWriter fileWriter = new FileWriter(file, StandardCharsets.UTF_8)) {
                fileWriter.write(content);
            }
            logger.printVerboseInfo(filePath + "file written successfully");
        }

        Path ballerinaTomlPath = projectDir.resolve(BALLERINA_TOML_FILE);
        File balTomlFile = Files.createFile(ballerinaTomlPath).toFile();

        String tomlContent = String.format("""
                [package]
                org = "%s"
                name = "%s"
                version = "%s"
                """,
                moduleDescriptor.org().value(),
                moduleDescriptor.packageName().value(),
                moduleDescriptor.version().value());

        try (FileWriter fileWriter = new FileWriter(balTomlFile, StandardCharsets.UTF_8)) {
            fileWriter.write(tomlContent);
        }
        logger.printVerboseInfo(ballerinaTomlPath + "file written successfully");
        logger.printInfo("Ballerina project created successfully at: " + projectDir.toAbsolutePath());
    }

    // Inner class to track progress for each file
    private static class ProgressTracker {
        private final String fileName;
        private volatile int currentStep = 0;
        private volatile String currentMessage = "";
        private volatile boolean completed = false;
        private static final int TOTAL_STEPS = 3;

        public ProgressTracker(String fileName) {
            this.fileName = fileName;
        }

        public void updateProgress(int step, String message) {
            this.currentStep = step;
            this.currentMessage = message;
        }

        public void markCompleted() {
            this.completed = true;
            this.currentStep = TOTAL_STEPS;
            this.currentMessage = "Completed";
        }

        public String getDisplayString() {
            if (completed) {
                return String.format("%-30s ✓ %s", fileName, currentMessage);
            } else if (currentStep == 0) {
                return String.format("%-30s ✗ %s", fileName, currentMessage);
            } else {
                String progressBar = createMiniProgressBar(currentStep - 1, TOTAL_STEPS);
                String stepInfo = String.format("[%d/%d]", currentStep, TOTAL_STEPS);
                return String.format("%-30s %s %s %s", fileName, progressBar, stepInfo, currentMessage);
            }
        }
    }
}
