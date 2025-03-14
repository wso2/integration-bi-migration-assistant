package converter;

import ballerina.BallerinaModel;
import ballerina.CodeGenerator;
import converter.tibco.TibcoToBalConverter;
import dataweave.converter.DWConversionStats;
import io.ballerina.cli.cmd.NewCommand;
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.ballerina.cli.cmd.NewCommand;
import io.ballerina.compiler.syntax.tree.SyntaxTree;

import static converter.HtmlReportWriter.writeHtmlReport;
import static converter.MuleToBalConverter.convertProjectXMLFileToBallerina;
import static converter.MuleToBalConverter.createBallerinaModel;
import static converter.MuleToBalConverter.createContextInfoHoldingDataStructures;
import static converter.MuleToBalConverter.SharedProjectData;

public class MigrationTool {

    private static final PrintStream OUT = System.out;
    private static final Logger logger = Logger.getLogger(MigrationTool.class.getName());
    public static final String MULE_DEFAULT_APP_DIR_NAME = "app";
    public static final String BAL_PROJECT_SUFFIX = "-ballerina";
    public static final String MIGRATION_REPORT_NAME = "migration_summary.html";

    public static void main(String[] args) {
        if (args.length < 1) {
            logger.severe("Usage: java -jar mule-to-bi-migration-assistant.jar " +
                    "<mule-xml-config-file-or-project-directory>");
            System.exit(1);
        }

        if (args[0].equals("--tibco") || args[0].equals("-t")) {
            migrateTibco(args);
            return;
        }

        migrateMuleProject(args);
    }

    private static void migrateTibco(String[] args) {
        if (args.length < 2) {
            logger.severe(
                    "Usage: java -jar mule_to_bal_converter.jar --tibco <path to bwp file or project> [-o <output path>]");
            System.exit(1);
        }
        Path inputPath = Paths.get(args[1]);
        String outputPath = null;
        if (args.length >= 4 && args[2].equals("-o")) {
            outputPath = args[3];
        }

        if (Files.isRegularFile(inputPath)) {
            SyntaxTree syntaxTree = TibcoToBalConverter.convertFile(inputPath.toString());
            String ballerinaCode = syntaxTree.toSourceCode();
            String outputBalFilePath;
            if (outputPath != null) {
                outputBalFilePath = outputPath;
            } else {
                outputBalFilePath = inputPath.toString().replaceAll("\\.bwp$", ".bal");
            }
            Path outputFilePath = Paths.get(outputBalFilePath);
            try {
                Files.writeString(outputFilePath, ballerinaCode);
                logger.info("Conversion successful. Output written to " + outputBalFilePath);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error writing output to file: " + outputBalFilePath, e);
                System.exit(1);
            }
        } else if (Files.isDirectory(inputPath)) {
            String targetPath = outputPath != null ? outputPath : inputPath + "_converted";
            migrateTibcoProject(inputPath.toString(), targetPath);
        } else {
            logger.severe("Invalid path: " + inputPath);
            System.exit(1);
        }
    }

    static void migrateTibcoProject(String projectPath, String targetPath) {
        Path targetDir = Paths.get(targetPath);
        try {
            createTargetDirectoryIfNeeded(targetDir);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error creating target directory: " + targetDir, e);
            System.exit(1);
        }

        BallerinaModel.Module module = TibcoToBalConverter.convertProject(projectPath);
        BallerinaModel.DefaultPackage balPackage = new BallerinaModel.DefaultPackage("tibco", "sample", "0.1");
        for (BallerinaModel.TextDocument textDocument : module.textDocuments()) {
            try {
                writeTextDocument(module, balPackage, textDocument, targetDir);
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Failed to create output file" + textDocument.documentName(), e);
            }
        }
        try {
            addProjectArtifacts(targetPath);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error adding project artifacts", e);
        }
    }

    private static void writeTextDocument(BallerinaModel.Module module, BallerinaModel.DefaultPackage balPackage,
            BallerinaModel.TextDocument textDocument, Path targetDir) throws IOException {
        BallerinaModel.Module tmpModule = new BallerinaModel.Module(module.name(), List.of(textDocument));
        BallerinaModel ballerinaModel = new BallerinaModel(balPackage, List.of(tmpModule));
        SyntaxTree st = new CodeGenerator(ballerinaModel).generateBalCode();
        Path filePath = Path.of(targetDir + "/" + textDocument.documentName());
        Files.writeString(filePath, st.toSourceCode());
    }

    private static void createTargetDirectoryIfNeeded(Path targetDir) throws IOException {
        if (Files.exists(targetDir)) {
            return;
        }
        Files.createDirectories(targetDir);
        logger.info("Created target directory: " + targetDir);
    }

    private static void addProjectArtifacts(String targetPath) throws IOException {
        String org = "converter";
        String name = Paths.get(targetPath).getFileName().toString();
        String version = "0.1.0";
        String distribution = "2201.12.0";

        Path tomlPath = Paths.get(targetPath, "Ballerina.toml");
        String tomlContent = "[package]\n" +
                "org = \"" + org + "\"\n" +
                "name = \"" + name + "\"\n" +
                "version = \"" + version + "\"\n" +
                "distribution = \"" + distribution + "\"\n\n" +
                "[build-options]\n" +
                "observabilityIncluded = true";

        Files.writeString(tomlPath, tomlContent);
        logger.info("Created Ballerina.toml file at: " + tomlPath);
    }

    private static void migrateMuleProject(String[] args) {
        String inputPath = args[0];
        boolean standaloneFile = inputPath.endsWith(".xml");
        if (standaloneFile) {
            convertMuleXmlFile(inputPath);
        } else {
            convertMuleProject(inputPath);
        }
    }

    public static void convertMuleXmlFile(String inputXmlFilePath) {
        String outputBalFilePath = inputXmlFilePath.replace(".xml", ".bal");
        SyntaxTree syntaxTree = MuleToBalConverter.convertStandaloneXMLFileToBallerina(inputXmlFilePath);
        String ballerinaCode = syntaxTree.toSourceCode();
        Path outputPath = Paths.get(outputBalFilePath);
        try {
            Files.writeString(outputPath, ballerinaCode);
            logger.info("Conversion successful. Output written to " + outputBalFilePath);
        } catch (Exception e) {
            logger.severe("Error writing to file: " + e.getMessage());
            System.exit(1);
        }
    }

    public static void convertMuleProject(String projectPath) {
        Path muleProjectPath = Path.of(projectPath);

        String balProjectName = muleProjectPath.getFileName().toString().concat(BAL_PROJECT_SUFFIX);
        Path balProjectPath = muleProjectPath.resolve(balProjectName);

        // create ballerina project
        String[] args = { balProjectPath.toString() };
        NewCommand newCommand = new NewCommand(System.out, false);
        new CommandLine(newCommand).parseArgs(args);
        newCommand.execute();

        Path sourceFolderPath = muleProjectPath.resolve("src").resolve("main").resolve(MULE_DEFAULT_APP_DIR_NAME);
        String targetFolderPath = balProjectPath.toString();

        List<File> xmlFiles = new ArrayList<>();
        collectXmlFiles(sourceFolderPath.toFile(), xmlFiles);

        if (xmlFiles.isEmpty()) {
            logger.severe("No XML files found in the directory: " + sourceFolderPath);
            System.exit(1);
        }

        MuleXMLNavigator muleXMLNavigator = new MuleXMLNavigator();
        SharedProjectData sharedProjectData = new SharedProjectData(muleXMLNavigator);
        for (File xmlFile : xmlFiles) {
            Path relativePath = sourceFolderPath.relativize(xmlFile.toPath());
            String balFileName = relativePath.toString().replace(File.separator, ".").replace(".xml", ".bal");
            Path targetFilePath = Paths.get(targetFolderPath, balFileName);
            createDirectories(targetFilePath.getParent());

            genAndWriteBalFileFromXMLFile(xmlFile, muleXMLNavigator, sharedProjectData, targetFilePath);
        }

        genAndWriteInternalTypesBalFile(sharedProjectData, targetFolderPath);

        Path reportFilePath = Paths.get(targetFolderPath, MIGRATION_REPORT_NAME);
        int conversionPercentage = writeHtmlReport(logger, reportFilePath,
                muleXMLNavigator.getXmlCompatibleTagCountMap(), muleXMLNavigator.getXmlIncompatibleTagCountMap(),
                muleXMLNavigator.getDwConversionStats());
        printConversionPercentage(conversionPercentage);
        printDataWeaveConversionSummary(muleXMLNavigator);
    }

    /**
     * Generate and write the Ballerina file from the XML file.
     *
     * @param xmlFile           xml file to be converted
     * @param muleXMLNavigator  MuleXMLNavigator instance to navigate the XML file
     * @param sharedProjectData shared project data
     * @param targetFilePath    path to the target file where the Ballerina code
     *                          will be written
     */
    private static void genAndWriteBalFileFromXMLFile(File xmlFile, MuleXMLNavigator muleXMLNavigator,
                                                      SharedProjectData sharedProjectData, Path targetFilePath) {
        SyntaxTree syntaxTree;
        try {
            syntaxTree = convertProjectXMLFileToBallerina(muleXMLNavigator, sharedProjectData, xmlFile.getPath());
        } catch (Exception e) {
            logger.severe(String.format("Error converting the file: %s%n%s", xmlFile.getName(), e.getMessage()));
            return;
        }

        try {
            Files.writeString(targetFilePath, syntaxTree.toSourceCode());
        } catch (IOException e) {
            logger.severe("Error writing to file: " + e.getMessage());
        }
    }

    /**
     * Generate and write the internal-types.bal file.
     *
     * @param sharedProjectData shared project data containing context type
     *                          definitions and imports
     * @param targetFolderPath  path to the target folder where the
     *                          internal-types.bal file will be created
     */
    private static void genAndWriteInternalTypesBalFile(SharedProjectData sharedProjectData, String targetFolderPath) {
        createContextInfoHoldingDataStructures(sharedProjectData);

        Path targetFilePath = Paths.get(targetFolderPath, "internal-types.bal");
        BallerinaModel ballerinaModel = createBallerinaModel(sharedProjectData.contextTypeDefImports.stream().toList(),
                sharedProjectData.contextTypeDefMap.values().stream().toList(), Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        SyntaxTree syntaxTree = new CodeGenerator(ballerinaModel).generateBalCode();
        try {
            Files.writeString(targetFilePath, syntaxTree.toSourceCode());
        } catch (IOException e) {
            logger.severe("Error writing to file: " + e.getMessage());
        }
    }

    private static void printDataWeaveConversionSummary(MuleXMLNavigator muleXMLNavigator) {
        DWConversionStats stats = muleXMLNavigator.getDwConversionStats();
        OUT.println("________________________________________________________________");
        OUT.println("Dataweave conversion percentage: " + String.format("%.2f", stats.getConversionPercentage()) + "%");
        OUT.println("________________________________________________________________");
    }

    private static void printConversionPercentage(int conversionPercentage) {
        OUT.println("________________________________________________________________");
        OUT.println("Project conversion percentage: " + conversionPercentage + "%");
        OUT.println("________________________________________________________________");
    }

    private static void collectXmlFiles(File folder, List<File> xmlFiles) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    collectXmlFiles(file, xmlFiles);
                } else if (file.getName().toLowerCase().endsWith(".xml")) {
                    xmlFiles.add(file);
                }
            }
        }
    }

    private static void createDirectories(Path path) {
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                logger.severe("Error creating directories: " + path);
            }
        }
    }
}
