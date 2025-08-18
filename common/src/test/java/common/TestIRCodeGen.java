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
package common;

import io.ballerina.compiler.syntax.tree.SyntaxTree;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static common.ConversionUtils.stmtFrom;
import static common.ConversionUtils.typeFrom;

public class TestIRCodeGen {

    @Test
    public void testSimpleIRToBalCodeGen() {
        final String listenerName = "myHttpListener";
        final String queryParamName = "name";

        HashSet<BallerinaModel.Import> imports = new HashSet<>();

        // Create a http listener
        BallerinaModel.Listener httpListener =
                new BallerinaModel.Listener.HTTPListener(listenerName, "9090", "0.0.0.0");
        imports.add(new BallerinaModel.Import("ballerina", "http"));

        // Create resource body statements
        List<BallerinaModel.Statement> resourceBody = new ArrayList<>();
        resourceBody.add(stmtFrom("log:printInfo(\"Received request for greeting with name: \" + %s);"
                .formatted(queryParamName)));
        imports.add(new BallerinaModel.Import("ballerina", "log"));
        resourceBody.add(stmtFrom("json payload = {\"message\": \"Hello \" + %s};".formatted(queryParamName)));
        resourceBody.add(stmtFrom("return payload;"));

        // Create simple get resource
        BallerinaModel.Resource getResource = new BallerinaModel.Resource(
                "get", "hello",
                Collections.singletonList(new BallerinaModel.Parameter(queryParamName, typeFrom("string"))),
                Optional.of(typeFrom("json")), resourceBody);

        // Create a service
        BallerinaModel.Service service = new BallerinaModel.Service("/greetings", listenerName,
                Collections.singletonList(getResource));

        // Comments
        List<String> comments = new ArrayList<>();
        comments.add("\n");
        comments.add("// This Ballerina service listens on port 9090 and provides a resource to greet users.");
        comments.add("// e.g. curl -X GET http://localhost:9090/greetings/hello?name=John");

        // Create new TextDocument
        BallerinaModel.TextDocument textDocument = new BallerinaModel.TextDocument(
                "demo.bal",
                imports.stream().toList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.singletonList(httpListener),
                Collections.singletonList(service),
                Collections.emptyList(),
                Collections.emptyList(),
                comments);

        SyntaxTree syntaxTree = new CodeGenerator(textDocument).generateSyntaxTree();
        String generatedCode = syntaxTree.toSourceCode();
        assertGeneratedCode(generatedCode, "src/test/resources/common/greetings_http_service.bal");
    }

    private static void assertGeneratedCode(String actualCode, String pathToExpectedCode) {
        String expectedCode = getSourceText(Path.of(pathToExpectedCode));
        Assert.assertEquals(actualCode, expectedCode,
                "Generated Ballerina code does not match the expected code.");
    }

    /**
     * Returns Ballerina source code in the given file as a {@code String}.
     *
     * @param sourceFilePath Path to the ballerina file
     * @return source code as a {@code String}
     */
    private static String getSourceText(Path sourceFilePath) {
        try {
            return Files.readString(sourceFilePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
