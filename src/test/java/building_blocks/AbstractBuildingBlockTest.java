package building_blocks;

import io.ballerina.compiler.syntax.tree.SyntaxTree;
import org.testng.Assert;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static converter.Mule2BalConverter.convertToBallerina;

public class AbstractBuildingBlockTest {

    public static void testMuleToBal(String sourcePath, String targetPath) {
        SyntaxTree syntaxTree =
                convertToBallerina(RESOURCE_DIRECTORY.resolve("building_blocks").resolve(sourcePath).toString());
        String expectedBalCode = getSourceText(Path.of("building_blocks", targetPath));
        Assert.assertEquals(syntaxTree.toSourceCode(), expectedBalCode);
    }

    private static final Path RESOURCE_DIRECTORY = Path.of("src/test/resources/");

    /**
     * Returns Ballerina source code in the given file as a {@code String}.
     *
     * @param sourceFilePath Path to the ballerina file
     * @return source code as a {@code String}
     */
    public static String getSourceText(Path sourceFilePath) {
        try {
            return Files.readString(RESOURCE_DIRECTORY.resolve(sourceFilePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
