/*
 *  Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com).
 *
 *  WSO2 LLC. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static baltool.logicapps.Constants.BALLERINA_TOML_FILE;
import static baltool.logicapps.Constants.CONTENT;
import static baltool.logicapps.Constants.DEFAULT_ORG_NAME;
import static baltool.logicapps.Constants.DEFAULT_PROJECT_VERSION;
import static baltool.logicapps.Constants.FILE_PATH;

/**
 * This class is responsible for executing the migration of Logic Apps to Ballerina projects.
 * It handles both single and multi-root migrations, manages progress tracking, and generates
 * the necessary Ballerina project structure.
 */
public class LogicAppsMigrationExecutor {
    private static final String BALLERINA_PROJECT_SUFFIX = "_ballerina";
    private static final int TOTAL_STEPS = 3;

    private static AtomicInteger errorFiles = new AtomicInteger(0);

    /**
     * Migrates a Logic App JSON file or a directory to a Ballerina project/s with specified output directory.
     *
     * @param logicAppFilePath        the path to the Logic App JSON file or directory containing multiple files.
     * @param additionalInstructions  additional instructions for the migration process.
     * @param targetDir               the target directory for the generated Ballerina project.
     * @param verbose                 whether to enable verbose logging.
     * @param multiRoot               whether to process multiple logic app files concurrently.
     * @param logger                  logger instance for logging messages.
     */
    public static void migrateLogicAppToBallerina(Path logicAppFilePath, String additionalInstructions,
                                                  Path targetDir, boolean verbose, boolean multiRoot,
                                                  VerboseLogger logger) {
        try {
            logger.printVerboseInfo("Starting migration with specified output directory: " +
                    targetDir.toString());

            logger.printVerboseInfo("Obtaining access token");
            String copilotAccessToken = getAccessToken(logger);
            if (copilotAccessToken == null) {
                logger.printVerboseInfo("Access token is null, terminating migration");
                return;
            }
            logger.printVerboseInfo("Access token obtained successfully");

            if (multiRoot && Files.isDirectory(logicAppFilePath)) {
                logger.printVerboseInfo("Multi-root mode enabled, processing directory: " + logicAppFilePath);
                processMultipleLogicApps(logicAppFilePath, additionalInstructions, targetDir,
                        copilotAccessToken, verbose, logger);
            } else {
                String fileName = logicAppFilePath.getFileName().toString();
                VerboseLoggerFactory loggerFactory = VerboseLoggerFactory.getInstance(verbose);
                loggerFactory.addProcess(fileName, TOTAL_STEPS);
                logger.printVerboseInfo("Single file mode, processing: " + logicAppFilePath);
                processSingleLogicApp(logicAppFilePath, additionalInstructions, targetDir, copilotAccessToken,
                        false, loggerFactory, fileName);
                loggerFactory.setProgressBarActive(false);
            }
            System.exit(0);
        } catch (Exception e) {
            logger.printError("Error during migration process: " + e.getMessage());
            logger.printStackTrace(e.getStackTrace());
        }
    }

    /**
     * Migrates a Logic App JSON file or a directory to a Ballerina project/s with the provided Logic App file
     * directory as the target directory.
     *
     * @param logicAppFilePath        the path to the Logic App JSON file or directory containing multiple files.
     * @param additionalInstructions  additional instructions for the migration process.
     * @param verbose                 whether to enable verbose logging.
     * @param multiRoot               whether to process multiple logic app files concurrently.
     * @param logger                  logger instance for logging messages.
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

            Path targetDir = Files.isDirectory(logicAppFilePath) ? logicAppFilePath :
                    logicAppFilePath.getParent();
            logger.printVerboseInfo("Project root directory: " + targetDir.toAbsolutePath());

            if (multiRoot && Files.isDirectory(logicAppFilePath)) {
                logger.printVerboseInfo("Multi-root mode enabled, processing directory: " + logicAppFilePath);
                processMultipleLogicApps(logicAppFilePath, additionalInstructions, targetDir,
                        copilotAccessToken, verbose, logger);
            } else {
                String fileName = logicAppFilePath.getFileName().toString();
                VerboseLoggerFactory loggerFactory = VerboseLoggerFactory.getInstance(verbose);
                loggerFactory.addProcess(fileName, TOTAL_STEPS);
                logger.printVerboseInfo("Single file mode, processing: " + logicAppFilePath);
                processSingleLogicApp(logicAppFilePath, additionalInstructions, targetDir, copilotAccessToken,
                        false, loggerFactory, fileName);
                loggerFactory.setProgressBarActive(false);
            }
            System.exit(0);
        } catch (IOException e) {
            logger.printError("Error during migration process: " + e.getMessage());
            logger.printStackTrace(e.getStackTrace());
        } catch (Exception e) {
            logger.printError("Unexpected error during migration process: " + e.getMessage());
            logger.printStackTrace(e.getStackTrace());
        }
    }

    /**
     * Processes multiple Logic App files in a directory concurrently.
     *
     * @param logicAppDirectory      the directory containing Logic App JSON files.
     * @param additionalInstructions additional instructions for the migration process.
     * @param projectRootDir         the root directory for the generated Ballerina projects.
     * @param copilotAccessToken     the access token for Copilot authentication.
     * @param verbose                whether to enable verbose logging.
     * @param logger                 logger instance for logging messages.
     * @throws IOException if an I/O error occurs while processing files.
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
        int totalFiles = logicAppFiles.size();
        VerboseLoggerFactory loggerFactory = VerboseLoggerFactory.getInstance(verbose);

        int threadPoolSize = Math.min(logicAppFiles.size(), Runtime.getRuntime().availableProcessors());
        logger.printVerboseInfo("Available processors: " + Runtime.getRuntime().availableProcessors());
        ExecutorService executorService = Executors.newFixedThreadPool(threadPoolSize);

        try {
            List<CompletableFuture<Void>> futures = new ArrayList<>();
            for (Path logicAppFile : logicAppFiles) {
                String fileName = logicAppFile.getFileName().toString();
                loggerFactory.addProcess(fileName, TOTAL_STEPS);
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try {
                        String projectName = fileName.replace(".json", BALLERINA_PROJECT_SUFFIX);
                        Path individualProjectDir = projectRootDir.resolve(projectName);

                        processSingleLogicApp(logicAppFile, additionalInstructions, individualProjectDir,
                                copilotAccessToken, true, loggerFactory, fileName);

                    } catch (Exception e) {
                        errorFiles.incrementAndGet();
                        String errorMsg = e.getMessage() != null ? e.getMessage() : "Unknown error";
                        loggerFactory.finishProgress(fileName, false, errorMsg);

                        if (verbose) {
                            synchronized (logger.getErrorPrintStream()) {
                                logger.printError("[THREAD-" + Thread.currentThread().getId() + "] ERROR processing "
                                        + fileName + ":");
                                logger.printStackTrace(e.getStackTrace());
                            }
                        } else {
                            logger.printError("Processing " + logicAppFile + ": " + errorMsg);
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
        } finally {
            if (loggerFactory.isProgressBarActive()) {
                loggerFactory.setProgressBarActive(false);
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

        // Final summary
        logger.printInfo("Migration Summary:");
        logger.printInfo("Total files: " + totalFiles);
        logger.printInfo("Completed: " + (totalFiles - errorFiles.get()));
        logger.printInfo("Failed: " + errorFiles);
    }

    /**
     * Processes a single Logic App file and generates the corresponding Ballerina project.
     *
     * @param logicAppFilePath        the path to the Logic App JSON file.
     * @param additionalInstructions  additional instructions for the migration process.
     * @param targetDir               the target directory for the generated Ballerina project.
     * @param copilotAccessToken      the access token for Copilot authentication.
     * @param isMultiThreaded         whether this is being called in multi-threaded mode.
     * @param logger                  logger instance for logging messages.
     * @param fileName                the name of the file being processed, used for logging.
     */
    private static void processSingleLogicApp(Path logicAppFilePath, String additionalInstructions, Path targetDir,
                                              String copilotAccessToken, boolean isMultiThreaded,
                                              VerboseLoggerFactory logger, String fileName) {
        try {
            if (!isMultiThreaded) {
                logger.printVerboseInfo(fileName, "SINGLE FILE PROCESSING MODE");
            }
            logger.printVerboseInfo(fileName, "Processing file: " + logicAppFilePath.toAbsolutePath());
            if (!Files.exists(logicAppFilePath)) {
                throw new IOException("Logic App file does not exist: " + logicAppFilePath);
            }
            if (!Files.isReadable(logicAppFilePath)) {
                throw new IOException("Logic App file is not readable: " + logicAppFilePath);
            }
            logger.printVerboseInfo(fileName, "File validation successful");
            logger.printVerboseInfo(fileName, "File size: " + Files.size(logicAppFilePath) + " bytes");

            String projectName = logicAppFilePath.getFileName().toString().replace(".json", BALLERINA_PROJECT_SUFFIX);
            String packageName = URLEncoder.encode(projectName, StandardCharsets.UTF_8).replace("-", "_");
            logger.printVerboseInfo(fileName, "Package name: " + packageName);

            ModuleDescriptor moduleDescriptor = getModuleDescriptor(packageName);

            JsonArray generatedSourceFiles = CodeGenerationUtils.generateCodeForLogicApp(copilotAccessToken,
                    logicAppFilePath, packageName, additionalInstructions, moduleDescriptor, logger, fileName);
            logger.printVerboseInfo(fileName, "Code generation completed. Generated " +
                    generatedSourceFiles.size() + " source files");

            if (!generatedSourceFiles.isEmpty()) {
                Path projectDir = targetDir.resolve(projectName);
                createProjectFromGeneratedSource(generatedSourceFiles, moduleDescriptor, projectDir, logger,
                        fileName);
                logger.finishProgress(fileName, true, "Completed");
            } else {
                logger.printVerboseWarn(fileName, "No source files were generated");
            }
        } catch (Exception e) {
            logger.finishProgress(fileName, false, e.getMessage());
            logger.printError(fileName, e.getMessage());
            logger.printStackTrace(fileName, e.getStackTrace());
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a ModuleDescriptor for the given package name.
     *
     * @param packageName the name of the package.
     * @return a ModuleDescriptor for the specified package.
     * @throws UnsupportedEncodingException if the package name cannot be encoded.
     */
    private static ModuleDescriptor getModuleDescriptor(String packageName) throws UnsupportedEncodingException {
        ModuleName moduleName = ModuleName.from(PackageName.from(packageName));
        PackageDescriptor packageDescriptor = PackageDescriptor.from(PackageOrg.from(DEFAULT_ORG_NAME),
                moduleName.packageName(), PackageVersion.from(DEFAULT_PROJECT_VERSION));
        return ModuleDescriptor.from(moduleName, packageDescriptor);
    }

    /**
     * Retrieves a valid access token for Copilot authentication.
     *
     * @param logger the logger instance for logging messages.
     * @return a valid access token, or null if retrieval fails.
     */
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

    /**
     * Creates a Ballerina project from the generated source files.
     *
     * @param sourceFiles      the generated source files as a JsonArray.
     * @param moduleDescriptor the ModuleDescriptor for the Ballerina project.
     * @param projectDir       the directory where the Ballerina project will be created.
     * @param logger           logger instance for logging messages.
     * @param fileName         the name of the file being processed, used for logging.
     * @throws IOException if an I/O error occurs while creating files or directories.
     */
    private static void createProjectFromGeneratedSource(JsonArray sourceFiles, ModuleDescriptor moduleDescriptor,
                                                         Path projectDir, VerboseLoggerFactory logger, String fileName)
            throws IOException {
        Files.createDirectories(projectDir);

        for (JsonElement sourceFile : sourceFiles) {
            JsonObject sourceFileObj = sourceFile.getAsJsonObject();
            String filePath = sourceFileObj.get(FILE_PATH).getAsString();
            String content = sourceFileObj.get(CONTENT).getAsString();

            File file = Files.createFile(projectDir.resolve(Paths.get(filePath))).toFile();

            try (FileWriter fileWriter = new FileWriter(file, StandardCharsets.UTF_8)) {
                fileWriter.write(content);
            }
            logger.printVerboseInfo(fileName, filePath + "file written successfully");
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
        logger.printVerboseInfo(fileName, BALLERINA_TOML_FILE + " file written successfully");
        logger.printInfo(fileName, "Ballerina project created successfully at: " + projectDir.toAbsolutePath());
    }
}
