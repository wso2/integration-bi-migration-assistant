package converter;

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
import java.util.List;
import java.util.logging.Logger;

import static converter.MuleToBalConverter.convertToBallerina;

public class MigrationTool {

    private static final PrintStream OUT = System.out;
    private static final Logger logger = Logger.getLogger(MigrationTool.class.getName());
    public static final String MULE_DEFAULT_APP_DIR_NAME = "app";
    public static final String BAL_PROJECT_SUFFIX = "-ballerina";

    public static void main(String[] args) {
        if (args.length != 1) {
            logger.severe("Usage: java -jar mule-to-bi-migration-assistant.jar " +
                    "<mule-xml-config-file-or-project-directory>");
            System.exit(1);
        }

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
        SyntaxTree syntaxTree = convertToBallerina(inputXmlFilePath);
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
        String[] args = {balProjectPath.toString()};
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

        int totalBlockCount = 0;
        int unsupportedBlockCount = 0;
        int totalDWNodesCount = 0;
        int convertedDWNodesCount = 0;
        for (File xmlFile : xmlFiles) {
            Path relativePath = sourceFolderPath.relativize(xmlFile.toPath());
            String balFileName = relativePath.toString().replace(File.separator, ".").replace(".xml", ".bal");
            Path targetFilePath = Paths.get(targetFolderPath, balFileName);
            createDirectories(targetFilePath.getParent());

            SyntaxTree syntaxTree;
            try {
                MuleToBalConverter.Data data = new MuleToBalConverter.Data();
                syntaxTree = convertToBallerina(xmlFile.getPath(), data);
                totalBlockCount += data.totalBlockCount;
                unsupportedBlockCount += data.unsupportedBlockCount;
                totalDWNodesCount += data.totalDWNodesCount;
                convertedDWNodesCount += data.convertedDWNodesCount;
            } catch (Exception e) {
                logger.severe(String.format("Error converting the file: %s%n%s", xmlFile.getName(), e.getMessage()));
                continue;
            }

            try {
                Files.writeString(targetFilePath, syntaxTree.toSourceCode());
            } catch (IOException e) {
                logger.severe("Error writing to file: " + e.getMessage());
            }
        }

        OUT.println("________________________________________________________________");
        OUT.println("Project conversion rate: " +
                (totalBlockCount - unsupportedBlockCount) * 100 / totalBlockCount + "%");
        if (totalDWNodesCount > 0) {
            OUT.println("DataWeave conversion rate: " +
                    convertedDWNodesCount * 100 / totalDWNodesCount + "%");
        }
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
