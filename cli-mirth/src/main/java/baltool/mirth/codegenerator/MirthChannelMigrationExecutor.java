package baltool.mirth.codegenerator;

import baltool.mirth.auth.CLIAuthenticator;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.ballerina.projects.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static baltool.mirth.Constants.*;

public class MirthChannelMigrationExecutor {

    private static final String BALLERINA_PROJECT_SUFFIX = "_ballerina";
    private static final int TOTAL_STEPS = 3;


    public static void main(String[] args) {

//        Path sourceFile = Path.of("/Users/isurus/wso2/integration-bi-migration-assistant/cli-mirth/src/main/resources/mirth_channel.xml");
        Path sourceFile = Path.of("/Users/isurus/wso2/integration-bi-migration-assistant/cli-mirth/src/main/resources/Hl7_Conversion.xml");
        Path outputDirectory = Path.of("/Users/isurus/wso2/integration-bi-migration-assistant/cli-mirth/src/main/resources/output");
        migrateChannelToBallerina(sourceFile, outputDirectory, "",false, new VerboseLogger(false));

    }

    public static void migrateChannelToBallerina(Path channelFilePath, Path outputDir, String additionalInstructions, boolean verbose, VerboseLogger logger) {
        try {
            logger.printVerboseInfo("Starting migration with specified output directory: " +
                    outputDir.toString());

            logger.printVerboseInfo("Obtaining access token");
            String copilotAccessToken = getAccessToken(logger);
            if (copilotAccessToken == null) {
                logger.printVerboseInfo("Access token is null, terminating migration");
                return;
            }
            logger.printVerboseInfo("Access token obtained successfully");

            String fileName = channelFilePath.getFileName().toString();
            VerboseLoggerFactory loggerFactory = VerboseLoggerFactory.getInstance(verbose);
            loggerFactory.addProcess(fileName, TOTAL_STEPS);
            logger.printVerboseInfo("Single file mode, processing: " + channelFilePath);
            processMirthChannel(channelFilePath, additionalInstructions, outputDir, copilotAccessToken,
                    loggerFactory, fileName);
            loggerFactory.setProgressBarActive(false);

            System.exit(0);
        } catch (Exception e) {
            logger.printError("Error during migration process: " + e.getMessage());
            logger.printStackTrace(e.getStackTrace());
        }
    }

    public static void migrateChannelToBallerina(Path channelFilePath, boolean verbose, VerboseLogger verboseLogger) {
    }

    private static void processMirthChannel(Path mirthChannelFilePath, String additionalInstructions, Path targetDir,
                                            String copilotAccessToken, VerboseLoggerFactory logger, String fileName) {
        try {

            logger.printVerboseInfo(fileName, "Processing file: " + mirthChannelFilePath.toAbsolutePath());
            if (!Files.exists(mirthChannelFilePath)) {
                throw new IOException("Logic App file does not exist: " + mirthChannelFilePath);
            }
            if (!Files.isReadable(mirthChannelFilePath)) {
                throw new IOException("Logic App file is not readable: " + mirthChannelFilePath);
            }
            logger.printVerboseInfo(fileName, "File validation successful");
            logger.printVerboseInfo(fileName, "File size: " + Files.size(mirthChannelFilePath) + " bytes");

            String projectName = mirthChannelFilePath.getFileName().toString().replace(".xml", BALLERINA_PROJECT_SUFFIX);
            String packageName = URLEncoder.encode(projectName, StandardCharsets.UTF_8).replace("-", "_");
            logger.printVerboseInfo(fileName, "Package name: " + packageName);

            ModuleDescriptor moduleDescriptor = getModuleDescriptor(packageName);

            JsonArray generatedSourceFiles = CodeGenerationUtils.generateCodeForMirthChannel(copilotAccessToken,
                    mirthChannelFilePath, packageName, additionalInstructions, moduleDescriptor, logger, fileName);
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

    private static ModuleDescriptor getModuleDescriptor(String packageName) throws UnsupportedEncodingException {
        ModuleName moduleName = ModuleName.from(PackageName.from(packageName));
        PackageDescriptor packageDescriptor = PackageDescriptor.from(PackageOrg.from(DEFAULT_ORG_NAME),
                moduleName.packageName(), PackageVersion.from(DEFAULT_PROJECT_VERSION));
        return ModuleDescriptor.from(moduleName, packageDescriptor);
    }

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

    private static String getAccessToken(VerboseLogger logger) {
        try {
            logger.printVerboseInfo("Attempting to retrieve access token via CLI authentication");
            String token = CLIAuthenticator.getValidAccessToken(logger);
            logger.printVerboseInfo("Access token retrieved successfully");
            return token;
        } catch (Exception e) {
            logger.printError("Error retrieving access token: " + e.getMessage());
            logger.printVerboseError("Stack trace: \n" + Arrays.toString(e.getStackTrace()));
            return null;
        }
    }
}
