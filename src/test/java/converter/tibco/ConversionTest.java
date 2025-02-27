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

package converter.tibco;

import io.ballerina.compiler.syntax.tree.SyntaxTree;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConversionTest {

    @DataProvider
    public static Object[][] testCaseProvider() throws IOException {
        return TestUtils.testCaseProvider();
    }

    @Test(groups = {"tibco", "converter"}, dataProvider = "testCaseProvider")
    public void testBallerinaModelConverter(Path path, TestUtils.TestKind kind) {
        try {
            var element = TibcoToBalConverter.parseXmlFile(path.toString());
            var process = XmlToTibcoModelConverter.parseProcess(element);
            var cx = new TibcoToBallerinaModelConverter.Context();
            var module = TibcoToBallerinaModelConverter.convertProcess(cx, process);
            // TODO: figure out how to validate the module
            if (kind == TestUtils.TestKind.ERROR) {
                throw new AssertionError("Parsing succeeded for an invalid input: " + path);
            }
        } catch (Exception e) {
            if (kind == TestUtils.TestKind.VALID) {
                throw new AssertionError("Parsing failed for a valid input: " + path, e);
            }
        }
    }

    @Test(groups = {"tibco", "converter"}, dataProvider = "testCaseProvider")
    public void testBallerinaSourceConverter(Path path, TestUtils.TestKind kind) {
        try {
            SyntaxTree syntaxTree = TibcoToBalConverter.convertToBallerina(path.toString());
            String source = syntaxTree.toSourceCode();
            String expectedSource = Files.readString(Path.of(path.toString().replace(".bwp", ".bal")));
            Assert.assertEquals(source, expectedSource);
        } catch (Exception e) {
            if (kind == TestUtils.TestKind.VALID) {
                throw new AssertionError("Parsing failed for a valid input: " + path, e);
            }
        }
    }
}
