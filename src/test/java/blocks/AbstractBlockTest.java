package blocks;

import io.ballerina.compiler.syntax.tree.SyntaxTree;
import org.testng.Assert;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static converter.Mule2BalConverter.convertToBallerina;

public class AbstractBlockTest {

    private static final Path RESOURCE_DIRECTORY = Path.of("src/test/resources/");
    private static final String BLOCKS_DIRECTORY = "blocks";
    private static final String MULE_3_DIRECTORY = "mule3";

    public static void testMule3ToBal(String sourcePath, String targetPath) {
        testMuleToBal(MULE_3_DIRECTORY, sourcePath, targetPath);
    }

    private static void testMuleToBal(String muleVersionDir, String sourcePath, String targetPath) {
        SyntaxTree syntaxTree = convertToBallerina(RESOURCE_DIRECTORY.resolve(BLOCKS_DIRECTORY).resolve(muleVersionDir)
                        .resolve(sourcePath).toString());
        String expectedBalCode = getSourceText(Path.of(BLOCKS_DIRECTORY, MULE_3_DIRECTORY, targetPath));
        Assert.assertEquals(syntaxTree.toSourceCode(), expectedBalCode);
    }

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
