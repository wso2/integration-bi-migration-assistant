package blocks;

import io.ballerina.compiler.syntax.tree.SyntaxTree;
import org.testng.Assert;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static converter.Mule2BalConverter.convertToBallerina;

public class AbstractBlockTest {

    private static final Path RESOURCE_DIRECTORY = Path.of("src", "test", "resources");
    private static final String BLOCKS_DIRECTORY = "blocks";
    private static final String MULE_3_DIRECTORY = "mule3";

    /**
     * <b>WARNING</b>: Enabling this flag will update all the assertion files in unit tests.
     * Should be used only if there is a bulk update that needs to be made to the test assertions.
     */
    private static final boolean UPDATE_ASSERTS = false;

    public static void testMule3ToBal(String sourcePath, String targetPath) {
        testMuleToBal(MULE_3_DIRECTORY, sourcePath, targetPath);
    }

    private static void testMuleToBal(String muleVersionDir, String sourcePath, String targetPath) {
        Path testDir = RESOURCE_DIRECTORY.resolve(BLOCKS_DIRECTORY).resolve(muleVersionDir);
        SyntaxTree syntaxTree = convertToBallerina(testDir.resolve(sourcePath).toString());
        String expectedBalCode = getSourceText(testDir.resolve(targetPath));
        String actualBalCode = syntaxTree.toSourceCode();
        updateAssertFile(testDir.resolve(targetPath), actualBalCode);
        Assert.assertEquals(actualBalCode, expectedBalCode);
    }

    private static void updateAssertFile(Path filePath, String newContent) {
        if (!UPDATE_ASSERTS) {
            return;
        }

        try {
            Files.writeString(filePath, newContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns Ballerina source code in the given file as a {@code String}.
     *
     * @param sourceFilePath Path to the ballerina file
     * @return source code as a {@code String}
     */
    public static String getSourceText(Path sourceFilePath) {
        try {
            return Files.readString(sourceFilePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
