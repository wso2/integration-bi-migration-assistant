package blocks;

import io.ballerina.compiler.syntax.tree.SyntaxTree;
import org.testng.Assert;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static mule.MuleToBalConverter.convertStandaloneXMLFileToBallerina;

public class AbstractBlockTest {

    private static final Path RESOURCE_DIRECTORY = Path.of("src", "test", "resources");
    private static final String BLOCKS_DIRECTORY = "blocks";
    private static final String MULE_3_DIRECTORY = "mule3";
    private static final String TEMPLATES_DIRECTORY = "templates";

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
        SyntaxTree syntaxTree = convertStandaloneXMLFileToBallerina(testDir.resolve(sourcePath).toString());
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

    public static void testDataWeaveMule3ToBal(String sourceDwlPath, String targetBalPath) {
        try {
            String xmlTemplate = getXmlTemplate();
            String modifiedXml = xmlTemplate.replace("DW_PATH", sourceDwlPath);
            Path tempXmlFile = Files.createTempFile("TEMP_XML", ".xml");
            Files.write(tempXmlFile, modifiedXml.getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.TRUNCATE_EXISTING);
            testMule3ToBal(tempXmlFile.toString(), targetBalPath);
            Files.deleteIfExists(tempXmlFile);
        } catch (IOException e) {
            throw new RuntimeException("Error creating temporary Mule XML file", e);
        }
    }

    private static String getXmlTemplate() throws IOException {
        Path templatePath = RESOURCE_DIRECTORY.resolve(TEMPLATES_DIRECTORY).resolve("dw_set_payload.xml");
        return new String(Files.readAllBytes(templatePath), StandardCharsets.UTF_8); // Specify UTF-8 explicitly
    }

}
