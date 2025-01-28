package converter;

import io.ballerina.compiler.syntax.tree.SyntaxTree;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MigrationTool {
    private static final Logger logger = Logger.getLogger(MigrationTool.class.getName());

    public static void main(String[] args) {
        if (args.length != 1) {
            logger.severe("Usage: java -jar mule_to_bal_converter.jar <mule-xml-configuration-file>");
            System.exit(1);
        }

        String inputXmlFilePath = args[0];
        Path inputPath = Paths.get(inputXmlFilePath);
        String outputBalFilePath = inputPath.toString().replaceAll("\\.xml$", ".bal");

        try {
            SyntaxTree syntaxTree = MuleToBalConverter.convertToBallerina(inputXmlFilePath);
            String ballerinaCode = syntaxTree.toSourceCode();
            Path outputPath = Paths.get(outputBalFilePath);
            Files.writeString(outputPath, ballerinaCode);
            logger.info("Conversion successful. Output written to " + outputBalFilePath);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during conversion: " + e.getMessage(), e);
            System.exit(1);
        }
    }
}
