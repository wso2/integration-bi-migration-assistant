package baltool.mirth.codegenerator;

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

import static baltool.mirth.Constants.*;

public class MirthChannelMigrationExecutor {

    private static final String BALLERINA_PROJECT_SUFFIX = "_ballerina";
    public static void migrateChannelToBallerina(Path channelFilePath, Path outputDir, boolean verbose, VerboseLogger verboseLogger) {
    }

    public static void migrateChannelToBallerina(Path channelFilePath, boolean verbose, VerboseLogger verboseLogger) {
    }

    private static void processMirthChannel(Path mirthChannelFilePath, String additionalInstructions, Path targetDir,
                                              String copilotAccessToken, boolean isMultiThreaded,
                                              VerboseLoggerFactory logger, String fileName) {
        try {
            if (!isMultiThreaded) {
                logger.printVerboseInfo(fileName, "SINGLE FILE PROCESSING MODE");
            }
            logger.printVerboseInfo(fileName, "Processing file: " + mirthChannelFilePath.toAbsolutePath());
            if (!Files.exists(mirthChannelFilePath)) {
                throw new IOException("Logic App file does not exist: " + mirthChannelFilePath);
            }
            if (!Files.isReadable(mirthChannelFilePath)) {
                throw new IOException("Logic App file is not readable: " + mirthChannelFilePath);
            }
            logger.printVerboseInfo(fileName, "File validation successful");
            logger.printVerboseInfo(fileName, "File size: " + Files.size(mirthChannelFilePath) + " bytes");

            String projectName = mirthChannelFilePath.getFileName().toString().replace(".json", BALLERINA_PROJECT_SUFFIX);
            String packageName = URLEncoder.encode(projectName, StandardCharsets.UTF_8).replace("-", "_");
            logger.printVerboseInfo(fileName, "Package name: " + packageName);

            ModuleDescriptor moduleDescriptor = getModuleDescriptor(packageName);

            JsonArray generatedSourceFiles = CodeGenerationUtils.generateCodeForLogicApp(copilotAccessToken,
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
}
