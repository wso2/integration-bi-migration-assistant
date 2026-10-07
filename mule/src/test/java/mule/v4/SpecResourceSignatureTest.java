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

import common.BallerinaModel;
import common.BallerinaModel.ModuleTypeDef;
import common.CodeGenerator;
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import mule.common.apispec.ApiSpec;
import mule.common.apispec.ApiSpec.ApiType;
import mule.common.apispec.ApiSpec.Body;
import mule.common.apispec.ApiSpec.Constraints;
import mule.common.apispec.ApiSpec.Operation;
import mule.common.apispec.ApiSpec.ScalarKind;
import mule.common.apispec.ApiSpecReadException;
import mule.common.apispec.ApiSpecReader;
import mule.common.apispec.ApiTypeGenerator;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static common.ConversionUtils.stmtFrom;
import static common.ConversionUtils.typeFrom;

public class SpecResourceSignatureTest {

    private static final ApiType STRING = new ApiType.Scalar(ScalarKind.STRING, Constraints.NONE);
    private static final ApiType INTEGER = new ApiType.Scalar(ScalarKind.INTEGER, Constraints.NONE);
    private static final ApiType BOOLEAN = new ApiType.Scalar(ScalarKind.BOOLEAN, Constraints.NONE);
    private static final Body UNTYPED_BODY = new Body(Optional.empty(), Optional.empty());

    @Test
    public void testSignaturesOfSpecOperations() throws ApiSpecReadException {
        ApiSpec spec = ApiSpecReader.read(Path.of("src/test/resources/apispec/orders-api/orders-api.raml"));
        ApiTypeGenerator typeGenerator = new ApiTypeGenerator(spec, new HashSet<>());

        SpecResourceSignature get = signature(spec, typeGenerator, "get", "/orders/{orderId}");
        Assert.assertEquals(get.resourcePath(), "orders/[string orderId]");
        Assert.assertEquals(rendered(get), List.of("boolean? expand", "@http:Header MarketId marketId",
                "@http:Header {name: \"x-correlation-id\"} string? xCorrelationId", "http:Request request"));
        Assert.assertEquals(get.uriParamsInit(), Optional.of("{orderId}"));
        Assert.assertEquals(get.payloadRef(), Optional.empty());
        Assert.assertEquals(typeGenerator.usedTypeDefinitions().stream().map(ModuleTypeDef::toString).toList(),
                List.of("@constraint:String {pattern: re `^[A-Z]{2}$`}\npublic type MarketId string;"));

        SpecResourceSignature post = signature(spec, typeGenerator, "post", "/orders");
        Assert.assertEquals(post.resourcePath(), "orders");
        Assert.assertEquals(rendered(post), List.of("@http:Payload Order payload", "http:Request request"));
        Assert.assertEquals(post.uriParamsInit(), Optional.empty());
        Assert.assertEquals(post.payloadRef(), Optional.of("payload"));
        Assert.assertEquals(typeGenerator.usedTypeDefinitions().stream().map(ModuleTypeDef::name).toList(),
                List.of("Status", "Address", "Order", "MarketId"));
    }

    @Test
    public void testBindsJsonBodiesOfNamedTypesToRecords() {
        Map<String, ApiType> types = Map.of("Order", new ApiType.ObjectType(List.of(),
                Map.of("id", new ApiSpec.Property(STRING, true)), true));
        ApiType inlineObject = new ApiType.ObjectType(List.of(), Map.of(), true);

        Assert.assertEquals(payload(types, "application/json", new ApiType.Ref("Order")),
                Optional.of("@http:Payload Order payload"));
        Assert.assertEquals(payload(types, "application/json", new ApiType.Array(new ApiType.Ref("Order"),
                Constraints.NONE)), Optional.of("@http:Payload Order[] payload"));
        Assert.assertEquals(payload(types, "application/json", inlineObject),
                Optional.of("@http:Payload json payload"));
        Assert.assertEquals(payload(types, "application/json", new ApiType.Ref("Unknown")),
                Optional.of("@http:Payload json payload"));
        Assert.assertEquals(payload(types, "application/xml", new ApiType.Ref("Order")),
                Optional.of("@http:Payload xml payload"));
    }

    @Test
    public void testTypesAndEscapesPathSegments() {
        Operation operation = operation("/orders/{order-id}/items/{itemId}/fraud-risk/{type}",
                List.of(param("order-id", STRING, true), param("itemId", INTEGER, true), param("type", STRING, true)),
                List.of(), List.of(), Map.of());

        SpecResourceSignature signature = of(operation, Map.of(), Optional.empty());
        Assert.assertEquals(signature.resourcePath(),
                "orders/[string orderId]/items/[int itemId]/fraud\\-risk/[string 'type]");
        Assert.assertEquals(signature.uriParamsInit(),
                Optional.of("{\"order-id\": orderId, \"itemId\": itemId.toString(), \"type\": 'type}"));
    }

    @Test
    public void testRootPathAndUnsupportedTemplates() {
        Assert.assertEquals(of(operation("/", List.of(), List.of(), List.of(), Map.of()), Map.of(), Optional.empty())
                .resourcePath(), ".");
        Map<String, ApiType> types = Map.of("File", STRING);
        ApiTypeGenerator typeGenerator = new ApiTypeGenerator(spec(types), new HashSet<>());
        Assert.assertEquals(SpecResourceSignature.of(operation("/files/{name}.json",
                List.of(param("name", STRING, true)), List.of(), List.of(),
                Map.of("application/json", new Body(Optional.of(new ApiType.Ref("File")), Optional.empty()))),
                types, Optional.empty(), typeGenerator, Set.of()), Optional.empty());
        Assert.assertEquals(typeGenerator.usedTypeDefinitions(), List.of());
    }

    @Test
    public void testMapsQueryAndHeaderTypes() {
        Map<String, ApiType> types = Map.of(
                "Status", new ApiType.StringEnum(List.of("NEW", "SHIPPED")),
                "Count", INTEGER,
                "Filter", new ApiType.ObjectType(List.of(), Map.of(), true));
        Operation operation = operation("/orders", List.of(),
                List.of(param("sku", STRING, true),
                        param("page-size", new ApiType.Ref("Count"), false),
                        param("tags", new ApiType.Array(STRING, Constraints.NONE), true),
                        param("expand", new ApiType.Union(List.of(BOOLEAN, new ApiType.Nil())), true),
                        param("status", new ApiType.Ref("Status"), true),
                        param("filter", new ApiType.Ref("Filter"), false),
                        param("type", STRING, false),
                        param("request", STRING, false)),
                List.of(param("X-Dyson-Correlation", STRING, false), param("ID", INTEGER, true)),
                Map.of());

        Assert.assertEquals(rendered(of(operation, types, Optional.empty())), List.of(
                "string sku",
                "@http:Query {name: \"page-size\"} int? pageSize",
                "string[] tags",
                "boolean? expand",
                "string status",
                "string? filter",
                "@http:Query {name: \"type\"} string? 'type",
                "@http:Query {name: \"request\"} string? requestQuery",
                "@http:Header {name: \"X-Dyson-Correlation\"} string? xDysonCorrelation",
                "@http:Header {name: \"ID\"} int id",
                "http:Request request"));
    }

    @Test
    public void testChoosesPayloadByMediaType() {
        Map<String, Body> jsonAndXml = new LinkedHashMap<>();
        jsonAndXml.put("application/json", UNTYPED_BODY);
        jsonAndXml.put("application/xml", UNTYPED_BODY);

        Assert.assertEquals(payload(jsonAndXml, Optional.empty()), Optional.empty());
        Assert.assertEquals(payload(jsonAndXml, Optional.of("application/xml")),
                Optional.of("@http:Payload xml payload"));
        Assert.assertEquals(payload(jsonAndXml, Optional.of("text/csv")), Optional.empty());
        Assert.assertEquals(payload(Map.of("application/vnd.api+json", UNTYPED_BODY), Optional.empty()),
                Optional.of("@http:Payload json payload"));
        Assert.assertEquals(payload(Map.of("text/plain", UNTYPED_BODY), Optional.empty()),
                Optional.of("@http:Payload string payload"));
        Assert.assertEquals(payload(Map.of("application/x-www-form-urlencoded", UNTYPED_BODY), Optional.empty()),
                Optional.of("@http:Payload map<string> payload"));
        Assert.assertEquals(payload(Map.of("application/pdf", UNTYPED_BODY), Optional.empty()),
                Optional.of("@http:Payload byte[] payload"));
        Assert.assertEquals(payload(Map.of("multipart/form-data", UNTYPED_BODY), Optional.empty()), Optional.empty());
    }

    @Test
    public void testRenamesParameterThatClashesWithPayload() {
        Operation operation = operation("/orders", List.of(), List.of(), List.of(param("payload", STRING, true)),
                Map.of("application/json", UNTYPED_BODY));

        SpecResourceSignature signature = of(operation, Map.of(), Optional.empty());
        Assert.assertEquals(rendered(signature).subList(0, 2), List.of(
                "@http:Header {name: \"payload\"} string payloadHeader",
                "@http:Payload json payload"));
        Assert.assertEquals(signature.payloadRef(), Optional.of("payload"));
    }

    @Test
    public void testAvoidsNamesTheResourceBodyDeclares() {
        ApiType marketId = new ApiType.Scalar(ScalarKind.STRING, new Constraints(Optional.of("^[A-Z]{2}$"),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty()));
        Operation operation = operation("/orders/{orderId}", List.of(param("orderId", STRING, true)), List.of(),
                List.of(param("marketId", marketId, true)), Map.of("application/json", UNTYPED_BODY));
        ApiTypeGenerator typeGenerator = new ApiTypeGenerator(spec(Map.of()), new HashSet<>());

        SpecResourceSignature signature = SpecResourceSignature.of(operation, Map.of(), Optional.empty(),
                typeGenerator, Set.of("marketId", "orderId", "payload")).orElseThrow();
        Assert.assertEquals(signature.resourcePath(), "orders/[string orderIdParam]");
        Assert.assertEquals(signature.uriParamsInit(), Optional.of("{\"orderId\": orderIdParam}"));
        Assert.assertEquals(rendered(signature), List.of(
                "@http:Header {name: \"marketId\"} MarketId marketIdHeader",
                "@http:Payload json payloadBody",
                "http:Request request"));
        Assert.assertEquals(signature.payloadRef(), Optional.of("payloadBody"));
    }

    @Test
    public void testGeneratedResourceParses() {
        Operation operation = operation("/orders/{order-id}/fraud-risk",
                List.of(param("order-id", INTEGER, true)),
                List.of(param("page-size", INTEGER, false), param("type", STRING, true)),
                List.of(param("client-id", STRING, true)),
                Map.of("application/json", UNTYPED_BODY));
        SpecResourceSignature signature = of(operation, Map.of(), Optional.empty());
        BallerinaModel.Resource resource = new BallerinaModel.Resource("post", signature.resourcePath(),
                signature.parameters(), Optional.of(typeFrom("http:Response|error")),
                List.of(stmtFrom("map<string> uriParams = %s;".formatted(signature.uriParamsInit().orElseThrow())),
                        stmtFrom("return new;")));
        BallerinaModel.TextDocument document = new BallerinaModel.TextDocument("orders.bal",
                List.of(new BallerinaModel.Import("ballerina", "http")), List.of(), List.of(),
                List.of(new BallerinaModel.Listener.HTTPListener("ordersListener", "9090", "0.0.0.0")),
                List.of(new BallerinaModel.Service("/", "ordersListener", List.of(resource))), List.of(),
                List.of(), List.of());

        SyntaxTree syntaxTree = new CodeGenerator(document).generateSyntaxTree();
        List<String> diagnostics = new ArrayList<>();
        syntaxTree.diagnostics().forEach(diagnostic -> diagnostics.add(diagnostic.toString()));
        Assert.assertTrue(diagnostics.isEmpty(), diagnostics + "\n" + syntaxTree.toSourceCode());
    }

    private static SpecResourceSignature signature(ApiSpec spec, ApiTypeGenerator typeGenerator, String method,
                                                   String path) {
        return SpecResourceSignature.of(spec.findOperation(method, path, Optional.empty()).orElseThrow(),
                spec.types(), Optional.empty(), typeGenerator, Set.of()).orElseThrow();
    }

    private static SpecResourceSignature of(Operation operation, Map<String, ApiType> types,
                                            Optional<String> flowMediaType) {
        return SpecResourceSignature.of(operation, types, flowMediaType,
                new ApiTypeGenerator(spec(types), new HashSet<>()), Set.of()).orElseThrow();
    }

    private static Optional<String> payload(Map<String, ApiType> types, String mediaType, ApiType bodyType) {
        return payload(of(operation("/orders", List.of(), List.of(), List.of(),
                Map.of(mediaType, new Body(Optional.of(bodyType), Optional.empty()))), types, Optional.empty()));
    }

    private static Optional<String> payload(SpecResourceSignature signature) {
        return rendered(signature).stream().filter(param -> param.startsWith("@http:Payload")).findFirst();
    }

    private static ApiSpec spec(Map<String, ApiType> types) {
        return new ApiSpec("Test", Optional.empty(), Optional.empty(), types, List.of());
    }

    private static Optional<String> payload(Map<String, Body> requestBodies, Optional<String> flowMediaType) {
        return payload(of(operation("/orders", List.of(), List.of(), List.of(), requestBodies), Map.of(),
                flowMediaType));
    }

    private static List<String> rendered(SpecResourceSignature signature) {
        return signature.parameters().stream().map(BallerinaModel.Parameter::toString).toList();
    }

    private static ApiSpec.Parameter param(String name, ApiType type, boolean required) {
        return new ApiSpec.Parameter(name, type, required);
    }

    private static Operation operation(String path, List<ApiSpec.Parameter> uriParams,
                                       List<ApiSpec.Parameter> queryParams, List<ApiSpec.Parameter> headers,
                                       Map<String, Body> requestBodies) {
        return new Operation("post", path, uriParams, queryParams, headers, requestBodies, Map.of(), List.of(),
                List.of());
    }
}
