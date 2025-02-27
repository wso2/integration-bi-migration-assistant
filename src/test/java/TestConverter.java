import io.ballerina.compiler.syntax.tree.SyntaxTree;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import static converter.MigrationTool.convertMuleProject;
import static converter.MuleToBalConverter.convertToBallerina;

public class TestConverter {

    private static final PrintStream OUT = System.out;

    @Test(description = "Test converting standalone mule xml file")
    public void convertAndPrintMuleXMLFile() {
        OUT.println("Generating Ballerina code...");
        SyntaxTree syntaxTree = convertToBallerina("src/test/resources/test_converter.xml");
        OUT.println("________________________________________________________________");
        OUT.println(syntaxTree.toSourceCode());
        OUT.println("________________________________________________________________");
    }

    @Test(description = "Test converting mule project")
    public void testMuleProjectConversion() {
        Path balProjectDir = Paths.get("src/test/resources/projects/muleprojectdemo/muleprojectdemo-ballerina");
        if (Files.exists(balProjectDir)) {
            try {
                deleteDirectory(balProjectDir);
            } catch (IOException e) {
                throw new RuntimeException("Issue deleting directory: balProjectDir.toString()", e);
            }
        }

        OUT.println("Generating Ballerina package...");
        convertMuleProject("src/test/resources/projects/muleprojectdemo");
        OUT.println("________________________________________________________________");
        OUT.println("Conversion completed. Output written to " +
                "src/test/resources/muleprojectdemo/muleprojectdemo-ballerina");
        OUT.println("________________________________________________________________");
    }

    private void deleteDirectory(Path path) throws IOException {
        Files.walkFileTree(path, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
