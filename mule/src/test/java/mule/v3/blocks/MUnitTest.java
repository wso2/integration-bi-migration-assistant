/*
 *  Copyright (c) 2026, WSO2 LLC. (http://www.wso2.com).
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
 *  KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package mule.v3.blocks;

import common.BallerinaModel.TextDocument;
import common.CodeGenerator;
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import mule.common.MUnitModel.TestSuite;
import mule.common.MigrationMetrics;
import mule.common.MuleLogger;
import mule.common.MuleXMLNavigator;
import mule.v3.Context;
import mule.v3.converter.MUnitConverter;
import mule.v3.dataweave.converter.DWConstruct;
import mule.v3.reader.MUnitConfigReader;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class MUnitTest {

    private static final Path MULE_RESOURCE_DIR = Path.of("src", "test", "resources", "mule", "v3", "blocks");
    private static final boolean UPDATE_ASSERTS = "true".equalsIgnoreCase(System.getenv("BLESS"));

    @Test
    public void testSampleMUnitConversion() {
        String xmlPath = MULE_RESOURCE_DIR.resolve("munit/sample_munit_test.xml").toString();

        MuleLogger logger = new MuleLogger(false);
        Context ctx = new Context(List.of(), List.of(), logger);

        MuleXMLNavigator navigator = new MuleXMLNavigator(
                new MigrationMetrics<DWConstruct>(), mule.v3.model.MuleXMLTag::isCompatible);
        TestSuite testSuite = MUnitConfigReader.readMUnitTestSuite(ctx, navigator, xmlPath);

        Assert.assertNotNull(testSuite);
        Assert.assertEquals(testSuite.name(), "sample-munit-v3-suite");
        Assert.assertEquals(testSuite.tests().size(), 3);
        Assert.assertTrue(testSuite.beforeSuite().isPresent());
        Assert.assertTrue(testSuite.afterSuite().isPresent());
    }

    @Test
    public void testMUnitCodeGeneration() {
        String xmlPath = MULE_RESOURCE_DIR.resolve("munit/sample_munit_test.xml").toString();

        MuleLogger logger = new MuleLogger(false);
        Context ctx = new Context(List.of(), List.of(), logger);

        MuleXMLNavigator navigator = new MuleXMLNavigator(
                new MigrationMetrics<DWConstruct>(), mule.v3.model.MuleXMLTag::isCompatible);
        TestSuite testSuite = MUnitConfigReader.readMUnitTestSuite(ctx, navigator, xmlPath);

        TextDocument testDoc = MUnitConverter.convertTestSuite(ctx, "tests/sample_munit_test.bal", testSuite);
        Assert.assertNotNull(testDoc);
        Assert.assertEquals(testDoc.documentName(), "tests/sample_munit_test.bal");
        Assert.assertFalse(testDoc.intrinsics().isEmpty());

        SyntaxTree syntaxTree = new CodeGenerator(testDoc).generateSyntaxTree();
        String balCode = syntaxTree.toSourceCode();
        Assert.assertNotNull(balCode);
        Assert.assertTrue(balCode.contains("import ballerina/test"));
        Assert.assertTrue(balCode.contains("@test:Config"));
        Assert.assertTrue(balCode.contains("test:assertEquals"));
        Assert.assertTrue(balCode.contains("test:assertFail"));

        Path expectedPath = MULE_RESOURCE_DIR.resolve("munit/sample_munit_test.bal");
        updateAssertFile(expectedPath, balCode);
        if (Files.exists(expectedPath)) {
            String expectedBalCode = readFile(expectedPath);
            Assert.assertEquals(balCode, expectedBalCode);
        }
    }

    @Test
    public void testMUnitTestWithMock() {
        String xmlPath = MULE_RESOURCE_DIR.resolve("munit/sample_munit_test.xml").toString();

        MuleLogger logger = new MuleLogger(false);
        Context ctx = new Context(List.of(), List.of(), logger);

        MuleXMLNavigator navigator = new MuleXMLNavigator(
                new MigrationMetrics<DWConstruct>(), mule.v3.model.MuleXMLTag::isCompatible);
        TestSuite testSuite = MUnitConfigReader.readMUnitTestSuite(ctx, navigator, xmlPath);

        var mockTest = testSuite.tests().stream()
                .filter(t -> t.name().equals("test-with-mock"))
                .findFirst()
                .orElseThrow();
        Assert.assertFalse(mockTest.behavior().isEmpty());
        Assert.assertFalse(mockTest.execution().isEmpty());
        Assert.assertFalse(mockTest.validation().isEmpty());

        TextDocument testDoc = MUnitConverter.convertTestSuite(ctx, "tests/sample_munit_test.bal", testSuite);
        SyntaxTree syntaxTree = new CodeGenerator(testDoc).generateSyntaxTree();
        String balCode = syntaxTree.toSourceCode();

        Assert.assertTrue(balCode.contains("TODO: MUNIT MOCK"));
        Assert.assertTrue(balCode.contains("munit:mock"));
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

    private static String readFile(Path path) {
        try {
            return Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
