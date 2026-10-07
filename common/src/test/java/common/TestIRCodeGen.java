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

    @Test
    public void testHTTPInterceptorIRCodeGeneration() {
        BallerinaModel.Parameter requestContext = new BallerinaModel.Parameter(
                "requestContext", typeFrom("http:RequestContext"));
        BallerinaModel.Parameter request = new BallerinaModel.Parameter("request", typeFrom("http:Request"));
        BallerinaModel.Parameter response = new BallerinaModel.Parameter("response", typeFrom("http:Response"));
        BallerinaModel.Parameter error = new BallerinaModel.Parameter("err", typeFrom("error"));

        BallerinaModel.Resource requestResource = new BallerinaModel.Resource("'default", "[string... path]",
                List.of(requestContext, request), Optional.of(typeFrom("http:NextService|error?")),
                List.of(stmtFrom("return requestContext.next();")));
        BallerinaModel.Resource requestErrorResource = new BallerinaModel.Resource("'default", "[string... path]",
                List.of(requestContext, request, error), Optional.of(typeFrom("http:Response|error")),
                List.of(stmtFrom("return new;")));
        BallerinaModel.Function responseFunction = new BallerinaModel.Function("interceptResponse",
                List.of(requestContext, response), typeFrom("http:Response|error"),
                List.of(stmtFrom("return response;")));
        BallerinaModel.Function responseErrorFunction = new BallerinaModel.Function("interceptResponseError",
                List.of(requestContext, response, error), typeFrom("http:Response|error"),
                List.of(stmtFrom("return response;")));

        BallerinaModel.Function initFunction = new BallerinaModel.Function("init", List.of(),
                List.of(stmtFrom("self.name = \"request\";")));
        List<BallerinaModel.HTTPInterceptor> interceptors = List.of(
                new BallerinaModel.HTTPInterceptor.RequestInterceptor("RequestInterceptor",
                        List.of(new BallerinaModel.ObjectField(typeFrom("string"), "name")),
                        Optional.of(initFunction), requestResource),
                new BallerinaModel.HTTPInterceptor.RequestErrorInterceptor(
                        "RequestErrorInterceptor", requestErrorResource),
                new BallerinaModel.HTTPInterceptor.ResponseErrorInterceptor(
                        "ResponseErrorInterceptor", new BallerinaModel.Remote(responseErrorFunction)),
                new BallerinaModel.HTTPInterceptor.ResponseInterceptor(
                        "ResponseInterceptor", new BallerinaModel.Remote(responseFunction)));
        BallerinaModel.Service service = new BallerinaModel.Service("/", List.of("listener"), Optional.empty(),
                new ArrayList<>(), List.of(), List.of(), List.of(), Optional.empty(), interceptors, Optional.empty());
        BallerinaModel.TextDocument document = new BallerinaModel.TextDocument("interceptors.bal",
                List.of(new BallerinaModel.Import("ballerina", "http")), List.of(), List.of(),
                List.of(new BallerinaModel.Listener.HTTPListener("listener", "9090", "0.0.0.0")),
                List.of(service), List.of(), List.of(), List.of());

        String source = document.toSource();
        Assert.assertTrue(source.contains("service http:InterceptableService / on listener"));
        Assert.assertTrue(source.contains("returns [RequestInterceptor, RequestErrorInterceptor, "
                + "ResponseErrorInterceptor, ResponseInterceptor]"));
        Assert.assertTrue(source.contains("*http:RequestInterceptor;"));
        Assert.assertTrue(source.contains("*http:RequestErrorInterceptor;"));
        Assert.assertTrue(source.contains("*http:ResponseErrorInterceptor;"));
        Assert.assertTrue(source.contains("*http:ResponseInterceptor;"));
        Assert.assertTrue(source.contains("string name;"));
        Assert.assertTrue(source.contains("function init()"));
    }

    @Test
    public void testAnnotatedResourceParameters() {
        List<BallerinaModel.Parameter> parameters = List.of(
                new BallerinaModel.Parameter("clientId", typeFrom("string"))
                        .withAnnotation("@http:Header {name: \"client-id\"}"),
                new BallerinaModel.Parameter("payload", typeFrom("json")).withAnnotation("@http:Payload"),
                new BallerinaModel.Parameter("request", typeFrom("http:Request")),
                new BallerinaModel.Parameter(typeFrom("int"), "pageSize",
                        new BallerinaModel.Expression.BallerinaExpression("10"))
                        .withAnnotation("@http:Query {name: \"page-size\"}"));
        BallerinaModel.Resource resource = new BallerinaModel.Resource("post", "orders", parameters,
                Optional.of(typeFrom("http:Response|error")), List.of(stmtFrom("return new;")));
        BallerinaModel.TextDocument document = new BallerinaModel.TextDocument("orders.bal",
                List.of(new BallerinaModel.Import("ballerina", "http")), List.of(), List.of(),
                List.of(new BallerinaModel.Listener.HTTPListener("ordersListener", "9090", "0.0.0.0")),
                List.of(new BallerinaModel.Service("/", "ordersListener", List.of(resource))), List.of(),
                List.of(), List.of());

        SyntaxTree syntaxTree = new CodeGenerator(document).generateSyntaxTree();
        List<String> diagnostics = new ArrayList<>();
        syntaxTree.diagnostics().forEach(diagnostic -> diagnostics.add(diagnostic.toString()));
        Assert.assertTrue(diagnostics.isEmpty(), diagnostics + "\n" + syntaxTree.toSourceCode());
        String source = syntaxTree.toSourceCode().replaceAll("\\s+", " ").replaceAll(" ?, ?", ", ");
        Assert.assertTrue(source.contains("service / on ordersListener { resource function post orders("
                + "@http:Header {name: \"client-id\"} string clientId, @http:Payload json payload, "
                + "http:Request request, @http:Query {name: \"page-size\"} int pageSize = 10) "
                + "returns http:Response|error"),
                source);
    }

    @Test
    public void testParameterWithoutAnnotationRendersAsBefore() {
        Assert.assertEquals(new BallerinaModel.Parameter("name", typeFrom("string")).toString(), "string name");
        Assert.assertEquals(new BallerinaModel.Parameter(typeFrom("int"), "count",
                new BallerinaModel.Expression.BallerinaExpression("0")).toString(), "int count = 0");
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
