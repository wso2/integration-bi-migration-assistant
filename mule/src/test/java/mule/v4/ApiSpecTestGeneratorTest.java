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
package mule.v4;

import common.CodeGenerator;
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import mule.common.apispec.ApiSpec;
import mule.common.apispec.ApiSpec.ApiType;
import mule.common.apispec.ApiSpec.Body;
import mule.common.apispec.ApiSpec.Constraints;
import mule.common.apispec.ApiSpec.Operation;
import mule.common.apispec.ApiSpec.Parameter;
import mule.common.apispec.ApiSpec.Response;
import mule.common.apispec.ApiSpec.ScalarKind;
import mule.common.apispec.ApiSpecResult;
import mule.common.apispec.ApiTypeGenerator;
import mule.v4.ApiSpecTestGenerator.ServiceAddress;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ApiSpecTestGeneratorTest {

    private static final ApiType STRING = new ApiType.Scalar(ScalarKind.STRING, Constraints.NONE);
    private static final ApiType ORDER_REF = new ApiType.Ref("Order");
    private static final Map<String, ApiType> TYPES = Map.of(
            "Order", new ApiType.ObjectType(List.of(), Map.of("id", new ApiSpec.Property(STRING, true)), true),
            "Cart", new ApiType.ObjectType(List.of(), Map.of("id", new ApiSpec.Property(STRING, true)), true));
    private static final Map<String, Body> ORDER_EXAMPLE_BODY = Map.of("application/json",
            new Body(Optional.of(ORDER_REF), Optional.of("{\"id\": \"price `${50}%\"}")));

    @Test
    public void testChecksExamplesOfGeneratedRecordsOnly() {
        Operation createOrder = operation("post", "/orders", List.of(), ORDER_EXAMPLE_BODY, Map.of(201,
                new Response(ORDER_EXAMPLE_BODY)));
        Operation createCart = operation("post", "/carts", List.of(), Map.of("application/json",
                new Body(Optional.of(new ApiType.Ref("Cart")), Optional.of("{\"id\": \"1\"}"))), Map.of());

        String source = generate(List.of(createOrder, createCart), Optional.empty());

        Assert.assertTrue(source.contains("final json postOrdersRequestExample = check string `{\n"
                + "  \"id\" : \"price ${\"`\"}${\"$\"}{50}%\"\n}`.fromJsonString();"), source);
        Assert.assertTrue(source.contains("function testPostOrdersRequestExample() returns error? {\n"
                + "    Order _ = check constraint:validate(postOrdersRequestExample);\n}"), source);
        Assert.assertTrue(source.contains("function testPostOrders201ResponseExample() returns error? {\n"
                + "    Order _ = check constraint:validate(check string `{"), source);
        Assert.assertFalse(source.contains("Cart"), "Cart is not generated, so its example is not tested");
        Assert.assertFalse(source.contains("http:Client"), "Without a service address only examples are tested");
    }

    @Test
    public void testChecksStatusesOfRequestsTheServiceRejects() {
        Parameter clientId = new Parameter("client-id", STRING, true);
        Parameter market = new Parameter("market", new ApiType.StringEnum(List.of("FR", "US")), true);
        Parameter correlationId = new Parameter("x-correlation-id", new ApiType.Scalar(ScalarKind.STRING,
                new Constraints(Optional.of("^[a-f0-9-]+$"), Optional.empty(), Optional.empty(), Optional.empty(),
                        Optional.empty(), Optional.empty(), Optional.empty())), true, Optional.of("9f39-d44d"));
        Operation getOrder = new Operation("get", "/orders/{id}", List.of(new Parameter("id", STRING, true)),
                List.of(new Parameter("page", new ApiType.Scalar(ScalarKind.INTEGER, Constraints.NONE), true)),
                List.of(clientId, market, correlationId), Map.of(), Map.of(), List.of(), List.of());
        Operation createOrder = operation("post", "/orders", List.of(clientId), ORDER_EXAMPLE_BODY, Map.of());

        String source = generate(List.of(getOrder, createOrder),
                Optional.of(new ServiceAddress("orders\\-listener", "/api", List.of(getOrder, createOrder))));

        Assert.assertTrue(source.contains("final http:Client ordersConfigClient = "
                + "check new (string `http://localhost:${orders\\-listener.getPort()}`);"), source);
        assertStatusTest(source, "testGetOrdersIdRejectsMissingRequiredHeaders",
                "ordersConfigClient->get(\"/api/orders/test?page=1\")", 400);
        assertStatusTest(source, "testGetOrdersIdAnswersNotImplemented",
                "ordersConfigClient->get(\"/api/orders/test?page=1\", {\"client-id\": \"test\", \"market\": \"FR\", "
                        + "\"x-correlation-id\": \"9f39-d44d\"})", 501);
        assertStatusTest(source, "testPostOrdersAnswersNotImplemented",
                "ordersConfigClient->post(\"/api/orders\", postOrdersRequestExample, {\"client-id\": \"test\"})", 501);
        assertStatusTest(source, "testOrdersConfigAnswersNotFoundToUnknownPath",
                "ordersConfigClient->get(\"/api/api-spec-test/no/such/path\")", 404);
    }

    @Test
    public void testListsOperationsWithoutValidRequest() {
        Parameter marketId = new Parameter("marketId", new ApiType.Scalar(ScalarKind.STRING,
                new Constraints(Optional.of("^[A-Z]{2}$"), Optional.empty(), Optional.empty(), Optional.empty(),
                        Optional.empty(), Optional.empty(), Optional.empty())), true);
        Operation listOrders = operation("get", "/orders", List.of(marketId), Map.of(), Map.of());

        String source = generate(List.of(listOrders),
                Optional.of(new ServiceAddress("ordersListener", "", List.of(listOrders))));

        Assert.assertFalse(source.contains("AnswersNotImplemented"), source);
        Assert.assertTrue(source.contains("// These operations of orders.raml have no 501 test, since the spec gives "
                + "no valid value for a required part of their request:\n// GET /orders"), source);
    }

    @Test
    public void testWritesNothingWithoutSpec() {
        Assert.assertTrue(ApiSpecTestGenerator.generate(Map.of("orders-config",
                new ApiSpecResult.Unavailable("Spec file not found")), Map.of(), Map.of()).isEmpty());
    }

    private static void assertStatusTest(String source, String name, String request, int status) {
        String test = "function %s() returns error? {\n    http:Response response = check %s;\n"
                .formatted(name, request) + "    test:assertEquals(response.statusCode, %d);\n}".formatted(status);
        Assert.assertTrue(source.contains(test), "Missing:\n" + test + "\nin:\n" + source);
    }

    private static String generate(List<Operation> operations, Optional<ServiceAddress> address) {
        ApiSpec spec = new ApiSpec("Orders", Optional.empty(), Optional.empty(), TYPES, operations);
        ApiTypeGenerator typeGenerator = new ApiTypeGenerator(spec, new HashSet<>());
        typeGenerator.use(ORDER_REF);
        SyntaxTree syntaxTree = new CodeGenerator(ApiSpecTestGenerator.generate(
                Map.of("orders-config", new ApiSpecResult.Loaded(Path.of("orders.raml"), "orders.raml", spec)),
                Map.of("orders-config", typeGenerator),
                address.map(value -> Map.of("orders-config", value)).orElse(Map.of())).orElseThrow())
                .generateSyntaxTree();
        List<String> diagnostics = new ArrayList<>();
        syntaxTree.diagnostics().forEach(diagnostic -> diagnostics.add(diagnostic.toString()));
        Assert.assertTrue(diagnostics.isEmpty(), diagnostics + "\n" + syntaxTree.toSourceCode());
        return syntaxTree.toSourceCode();
    }

    private static Operation operation(String method, String path, List<Parameter> headers,
                                       Map<String, Body> requestBodies, Map<Integer, Response> responses) {
        return new Operation(method, path, List.of(), List.of(), headers, requestBodies, responses, List.of(),
                List.of());
    }
}
