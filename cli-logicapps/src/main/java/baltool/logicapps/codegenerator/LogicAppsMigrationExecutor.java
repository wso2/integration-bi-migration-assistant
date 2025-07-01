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
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static baltool.logicapps.Constants.BALLERINA_TOML_FILE;
import static baltool.logicapps.Constants.CONTENT;
import static baltool.logicapps.Constants.DEFAULT_ORG_NAME;
import static baltool.logicapps.Constants.DEFAULT_PROJECT_VERSION;
import static baltool.logicapps.Constants.FILE_PATH;

public class LogicAppsMigrationExecutor {
    private static final PrintStream errStream = System.err;

    // Testing
    public static void main(String[] args) {
        // Arguments
        Path logicAppFilePath = Paths.get("/Users/nipunal/wso2/logic-apps", "ChckinV5ExpndTimeChiledWF.json");
        String additionalInstructions = "";
        Path projectRootDir = Paths.get("/Users/nipunal/wso2/logic-apps");
        String projectName = "mt-logic-apps-sample-1";

        LogicAppsMigrationExecutor.migrateLogicAppToBallerina(logicAppFilePath, additionalInstructions, projectRootDir,
                projectName);
    }

    /**
     * Migrates a Logic App JSON file to a Ballerina project.
     *
     * @param logicAppFilePath        the path to the Logic App JSON file.
     * @param additionalInstructions  additional instructions for the migration process.
     * @param projectRootDir          the root directory of the project.
     * @param projectName             the name of the project.
     */
    public static void migrateLogicAppToBallerina(Path logicAppFilePath, String additionalInstructions,
                                                  Path projectRootDir, String projectName) {
        try {
            String copilotAccessToken = getAccessToken();
            if (copilotAccessToken == null) {
                return;
            }

            Path projectPath = projectRootDir.resolve(projectName);
            String packageName = URLEncoder.encode(projectName, StandardCharsets.UTF_8).replace("-", "_");
            ModuleDescriptor moduleDescriptor = getModuleDescriptor(packageName);

            JsonArray generatedSourceFiles = CodeGenerationUtils.generateCodeForLogicApp(copilotAccessToken,
                    logicAppFilePath, projectPath, packageName, additionalInstructions, moduleDescriptor);

            if (!generatedSourceFiles.isEmpty()) {
                createProjectFromGeneratedSource(generatedSourceFiles, moduleDescriptor, projectPath);
            }

        } catch (IOException e) {
            errStream.println("Error reading project source files: " + e.getMessage());
        }
    }

    /**
     * Migrates a Logic App JSON file to a Ballerina project.
     *
     * @param logicAppFilePath        the path to the Logic App JSON file.
     * @param additionalInstructions  additional instructions for the migration process.
     * @param projectRootDir          the root directory of the project.
     */
    public static void migrateLogicAppToBallerina(Path logicAppFilePath, String additionalInstructions,
                                                  Path projectRootDir) {
        try {
            String copilotAccessToken = getAccessToken();
            if (copilotAccessToken == null) {
                return;
            }

            String projectName = logicAppFilePath.getFileName().toString().replace(".json", "");
            Path projectPath = projectRootDir.resolve(projectName);
            String packageName = URLEncoder.encode(projectName, StandardCharsets.UTF_8).replace("-", "_");
            ModuleDescriptor moduleDescriptor = getModuleDescriptor(packageName);

            JsonArray generatedSourceFiles = CodeGenerationUtils.generateCodeForLogicApp(copilotAccessToken,
                    logicAppFilePath, projectPath, packageName, additionalInstructions, moduleDescriptor);

            if (!generatedSourceFiles.isEmpty()) {
                createProjectFromGeneratedSource(generatedSourceFiles, moduleDescriptor, projectPath);
            }

        } catch (IOException e) {
            errStream.println("Error reading project source files: " + e.getMessage());
        }
    }

    /**
     * Migrates a Logic App JSON file to a Ballerina project.
     *
     * @param logicAppFilePath        the path to the Logic App JSON file.
     * @param additionalInstructions  additional instructions for the migration process.
     */
    public static void migrateLogicAppToBallerina(Path logicAppFilePath, String additionalInstructions) {
        try {
            String copilotAccessToken = getAccessToken();
            if (copilotAccessToken == null) {
                return;
            }

            String projectName = logicAppFilePath.getFileName().toString().replace(".json", "");
            Path projectPath = Paths.get(getCurrentDirectory()).resolve(projectName);
            String packageName = URLEncoder.encode(projectName, StandardCharsets.UTF_8).replace("-", "_");
            ModuleDescriptor moduleDescriptor = getModuleDescriptor(projectName);

            JsonArray generatedSourceFiles = CodeGenerationUtils.generateCodeForLogicApp(copilotAccessToken,
                    logicAppFilePath, projectPath, packageName, additionalInstructions, moduleDescriptor);

            if (!generatedSourceFiles.isEmpty()) {
                createProjectFromGeneratedSource(generatedSourceFiles, moduleDescriptor, projectPath);
            }
        } catch (IOException e) {
            errStream.println("Error writing project source files: " + e.getMessage());
        }
    }

    /**
     * Creates a ModuleDescriptor for the given package name.
     *
     * @param packageName the name of the package.
     * @return a ModuleDescriptor for the package.
     * @throws UnsupportedEncodingException if the package name cannot be encoded.
     */
    private static ModuleDescriptor getModuleDescriptor(String packageName) throws UnsupportedEncodingException {
        ModuleName moduleName = ModuleName.from(PackageName.from(packageName));
        PackageDescriptor packageDescriptor = PackageDescriptor.from(PackageOrg.from(DEFAULT_ORG_NAME),
                moduleName.packageName(), PackageVersion.from(DEFAULT_PROJECT_VERSION));
        return ModuleDescriptor.from(moduleName, packageDescriptor);
    }

    /**
     * Retrieves a valid access token for the Logic Apps service.
     *
     * @return a valid access token as a String.
     */
    private static String getAccessToken() {
        try {
            return CLIAuthentication.getValidAccessToken();
        } catch (Exception e) {
            errStream.println("Error obtaining access token: " + e.getMessage());
            return null;
        }
    }

    /**
     * Gets the current working directory.
     *
     * @return the current working directory as a String.
     */
    private static String getCurrentDirectory() {
        return System.getProperty("user.dir");
    }

    /**
     * Creates a Ballerina project from the generated source files.
     *
     * @param sourceFiles       the generated source files.
     * @param moduleDescriptor  the module descriptor for the project.
     * @param projectDir        the directory where the project will be created.
     * @throws IOException if an I/O error occurs.
     */
    private static void createProjectFromGeneratedSource(JsonArray sourceFiles, ModuleDescriptor moduleDescriptor,
                                                         Path projectDir)
            throws IOException {
        Files.createDirectories(projectDir);

        for (JsonElement sourceFile : sourceFiles) {
            JsonObject sourceFileObj = sourceFile.getAsJsonObject();
            File file = Files.createFile(projectDir.resolve(Paths.get(sourceFileObj.get(FILE_PATH).getAsString()))).
                    toFile();

            try (FileWriter fileWriter = new FileWriter(file, StandardCharsets.UTF_8)) {
                fileWriter.write(sourceFileObj.get(CONTENT).getAsString());
            }
        }

        Path ballerinaTomlPath = projectDir.resolve(BALLERINA_TOML_FILE);
        File balTomlFile = Files.createFile(ballerinaTomlPath).toFile();

        try (FileWriter fileWriter = new FileWriter(balTomlFile, StandardCharsets.UTF_8)) {
            fileWriter.write(String.format("""
                [package]
                org = "%s"
                name = "%s"
                version = "%s"
                """,
                    moduleDescriptor.org().value(),
                    moduleDescriptor.packageName().value(),
                    moduleDescriptor.version().value()));
        }
    }
}
