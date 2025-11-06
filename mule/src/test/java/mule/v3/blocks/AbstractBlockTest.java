/*
 *  Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com).
 *
 *  WSO2 LLC. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package mule.v3.blocks;

import io.ballerina.compiler.syntax.tree.SyntaxTree;
import mule.common.MuleLogger;
import org.testng.Assert;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static mule.v3.MuleToBalConverter.convertStandaloneXMLFileToBallerina;

public class AbstractBlockTest {

    private static final Path MULE_RESOURCE_DIR = Path.of("src", "test", "resources", "mule");
    private static final String BLOCKS_DIR = "blocks";
    private static final String MULE_V3_DIR = "v3";
    private static final String TEMPLATES_DIR = "templates";

    /**
     * <b>WARNING</b>: Enabling this flag will update all the assertion files in unit tests.
     * Should be used only if there is a bulk update that needs to be made to the test assertions.
     */
    private static final boolean UPDATE_ASSERTS = false;

    public static void testMule3ToBal(String sourcePath, String targetPath) {
        testMuleToBal(MULE_V3_DIR, sourcePath, targetPath);
    }

    private static void testMuleToBal(String muleVersionDir, String sourcePath, String targetPath) {
        Path testDir = MULE_RESOURCE_DIR.resolve(muleVersionDir).resolve(BLOCKS_DIR);
        SyntaxTree syntaxTree = convertStandaloneXMLFileToBallerina(testDir.resolve(sourcePath).toString(),
                new MuleLogger(false));
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
            Files.writeString(tempXmlFile, modifiedXml, StandardOpenOption.TRUNCATE_EXISTING);
            testMule3ToBal(tempXmlFile.toString(), targetBalPath);
            Files.deleteIfExists(tempXmlFile);
        } catch (IOException e) {
            throw new RuntimeException("Error creating temporary Mule XML file", e);
        }
    }

    private static String getXmlTemplate() throws IOException {
        Path templatePath = MULE_RESOURCE_DIR.resolve(MULE_V3_DIR).resolve(TEMPLATES_DIR).resolve("dw_set_payload.xml");
        return Files.readString(templatePath); // Specify UTF-8 explicitly
    }
}
