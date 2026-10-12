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
package mule.common.apispec;

import common.BallerinaModel;
import common.BallerinaModel.ModuleTypeDef;
import common.CodeGenerator;
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import mule.common.apispec.ApiSpec.ApiType;
import mule.common.apispec.ApiSpec.Constraints;
import mule.common.apispec.ApiSpec.Property;
import mule.common.apispec.ApiSpec.ScalarKind;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ApiTypeGeneratorTest {

    private static final ApiType STRING = new ApiType.Scalar(ScalarKind.STRING, Constraints.NONE);
    private static final ApiType INTEGER = new ApiType.Scalar(ScalarKind.INTEGER, Constraints.NONE);
    private static final ApiType BOOLEAN = new ApiType.Scalar(ScalarKind.BOOLEAN, Constraints.NONE);
    private static final ApiType NIL = new ApiType.Nil();

    @Test
    public void testGeneratesRootTypesAndTheirDependenciesInSpecOrder() throws ApiSpecReadException {
        ApiSpec spec = ApiSpecReader.read(Path.of("src/test/resources/apispec/orders-api/orders-api.raml"));

        List<String> typeDefs = rendered(new ApiTypeGenerator(spec, new HashSet<>())
                .typeDefinitions(List.of("PriorityOrder")));

        Assert.assertEquals(typeDefs, List.of(
                "public type Status \"NEW\"|\"SHIPPED\";",
                "public type Address record { string street; string town?; };",
                "public type Order record { @constraint:String {pattern: re `^[0-9]+$`, maxLength: 10} string orderId; "
                        + "Status status; @constraint:Int {minValue: 1, maxValue: 99} int quantity; string note?; "
                        + "@constraint:Array {minLength: 1} Address[] addresses; };",
                "public type PriorityOrder record { *Order; @constraint:String {pattern: re `^[0-9]+$`, maxLength: 10} "
                        + "string orderId; // repeated from Order so that Ballerina checks its constraint "
                        + "@constraint:Int {minValue: 1, maxValue: 99} int quantity; // repeated from Order so that "
                        + "Ballerina checks its constraint @constraint:Array {minLength: 1} Address[] addresses; "
                        + "// repeated from Order so that Ballerina checks its constraint boolean priority; };"));
    }

    @Test
    public void testGeneratesOnlyWhatRootsNeed() throws ApiSpecReadException {
        ApiSpec spec = ApiSpecReader.read(Path.of("src/test/resources/apispec/orders-api/orders-api.raml"));

        Assert.assertEquals(rendered(new ApiTypeGenerator(spec, new HashSet<>()).typeDefinitions(List.of("Address"))),
                List.of("public type Address record { string street; string town?; };"));
    }

    @Test
    public void testMapsUnionsArraysAndInlineObjects() {
        Map<String, Property> properties = new LinkedHashMap<>();
        properties.put("crmId", new Property(union(STRING, NIL), false));
        properties.put("flag", new Property(union(BOOLEAN, STRING, NIL), false));
        properties.put("states", new Property(new ApiType.Array(new ApiType.StringEnum(List.of("A", "B")),
                Constraints.NONE), true));
        properties.put("price", new Property(new ApiType.ObjectType(List.of(),
                Map.of("value", new Property(union(new ApiType.Scalar(ScalarKind.NUMBER, Constraints.NONE), NIL),
                        false)), true), false));
        properties.put("metadata", new Property(new ApiType.Any(), false));
        properties.put("placedOn", new Property(new ApiType.Scalar(ScalarKind.DATE, Constraints.NONE), true));
        properties.put("owner", new Property(new ApiType.Ref("Unknown"), true));

        Assert.assertEquals(typeDefinition("Order", new ApiType.ObjectType(List.of(), properties, true)),
                "public type Order record { string? crmId?; (boolean|string)? flag?; (\"A\"|\"B\")[] states; "
                        + "record { decimal? value?; } price?; anydata metadata?; string placedOn; anydata owner; };");
    }

    @Test
    public void testClosesRecordsTheSpecCloses() {
        Assert.assertEquals(typeDefinition("Point", new ApiType.ObjectType(List.of(),
                        Map.of("x", new Property(INTEGER, true)), false)),
                "public type Point record {| int x; |};");
    }

    @Test
    public void testEscapesNamesAndAvoidsReservedTypeNames() {
        Map<String, Property> properties = new LinkedHashMap<>();
        properties.put("type", new Property(STRING, true));
        properties.put("first-name", new Property(STRING, false));
        ApiSpec spec = spec(Map.of("Context", new ApiType.ObjectType(List.of(), properties, true)));

        ApiTypeGenerator generator = new ApiTypeGenerator(spec, new HashSet<>(Set.of("Context", "Attributes")));

        Assert.assertEquals(rendered(generator.typeDefinitions(List.of("Context"))),
                List.of("public type Context2 record { string 'type; string first\\-name?; };"));
        Assert.assertEquals(generator.typeDescriptor(new ApiType.Array(new ApiType.Ref("Context"), Constraints.NONE)),
                "Context2[]");
    }

    @Test
    public void testGeneratedTypesParse() throws ApiSpecReadException {
        ApiSpec spec = ApiSpecReader.read(Path.of("src/test/resources/apispec/orders-api/orders-api.raml"));
        BallerinaModel.TextDocument document = new BallerinaModel.TextDocument("types.bal", List.of(),
                new ApiTypeGenerator(spec, new HashSet<>()).typeDefinitions(spec.types().keySet()), List.of(),
                List.of(),
                List.of(), List.of(), List.of(), List.of());

        SyntaxTree syntaxTree = new CodeGenerator(document).generateSyntaxTree();
        List<String> diagnostics = new ArrayList<>();
        syntaxTree.diagnostics().forEach(diagnostic -> diagnostics.add(diagnostic.toString()));
        Assert.assertTrue(diagnostics.isEmpty(), diagnostics + "\n" + syntaxTree.toSourceCode());
    }

    @Test
    public void testConstrainsFieldsBallerinaCanCheck() {
        Map<String, Property> properties = new LinkedHashMap<>();
        properties.put("path", new Property(string(Optional.of("^[\\w\\/]{2,40}$"), Optional.empty()), true));
        properties.put("code", new Property(string(Optional.of("(?i)abc"), Optional.of(3)), true));
        properties.put("note", new Property(union(string(Optional.empty(), Optional.of(5)), NIL), false));
        properties.put("count", new Property(new ApiType.Scalar(ScalarKind.INTEGER, new Constraints(Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.of(new BigDecimal("0.5")), Optional.of(BigDecimal.TEN),
                Optional.empty(), Optional.empty())), true));

        Assert.assertEquals(typeDefinition("Item", new ApiType.ObjectType(List.of(), properties, true)),
                "public type Item record { @constraint:String {pattern: re `^[\\w/]{2,40}$`} string path; "
                        + "@constraint:String {maxLength: 3} string code; // TODO: Ballerina cannot check pattern "
                        + "(?i)abc string? note?; // TODO: the spec limits this field, but Ballerina cannot check a "
                        + "field that may be nil: {maxLength: 5} @constraint:Int {maxValue: 10} int count; "
                        + "// TODO: Ballerina cannot check minValue 0.5 };");
    }

    @Test
    public void testConstrainsNamedScalarTypes() {
        Assert.assertEquals(typeDefinition("Sku", string(Optional.of("^[A-Z]+$"), Optional.empty())),
                "@constraint:String {pattern: re `^[A-Z]+$`} public type Sku string;");
    }

    @Test
    public void testMarksNilableFieldOfConstrainedNamedTypeAsUnchecked() {
        Map<String, ApiType> types = new LinkedHashMap<>();
        types.put("PersonName", string(Optional.of("^[A-Za-z ]+$"), Optional.empty()));
        types.put("Note", new ApiType.ObjectType(List.of(),
                Map.of("author", new Property(union(new ApiType.Ref("PersonName"), NIL), false)), true));

        Assert.assertEquals(rendered(new ApiTypeGenerator(spec(types), new HashSet<>())
                .typeDefinitions(List.of("Note"))).get(1), "public type Note record { PersonName? author?; "
                + "// TODO: the spec limits this field, but Ballerina cannot check a field that may be nil: "
                + "{pattern: re `^[A-Za-z ]+$`} };");
    }

    @Test
    public void testGeneratesParameterTypesForConstrainedParameters() {
        ApiTypeGenerator generator = new ApiTypeGenerator(spec(Map.of("MarketId", STRING)), new HashSet<>());

        Assert.assertEquals(generator.parameterType("marketId",
                union(string(Optional.of("^[A-Z]{2}$"), Optional.empty()), NIL)), Optional.of("MarketId2"));
        Assert.assertEquals(generator.parameterType("marketId", string(Optional.of("^[A-Z]{2}$"), Optional.empty())),
                Optional.of("MarketId2"));
        Assert.assertEquals(generator.parameterType("count", INTEGER), Optional.empty());
        Assert.assertEquals(rendered(generator.usedTypeDefinitions()),
                List.of("@constraint:String {pattern: re `^[A-Z]{2}$`} public type MarketId2 string;"));
        Assert.assertTrue(generator.usesConstraints());
        Assert.assertFalse(new ApiTypeGenerator(spec(Map.of()), new HashSet<>()).usesConstraints());
    }

    @Test
    public void testNamesResponseRecordsAfterTheirBodyAndStatus() {
        ApiType order = new ApiType.Ref("Order");
        ApiTypeGenerator generator = new ApiTypeGenerator(spec(Map.of("Order", new ApiType.ObjectType(List.of(),
                Map.of("id", new Property(STRING, true)), true))), new HashSet<>(Set.of("OrderOk")));

        Assert.assertEquals(generator.responseType("http:Ok", order), "OrderOk2");
        Assert.assertEquals(generator.responseType("http:Created", new ApiType.Array(order, Constraints.NONE)),
                "OrderArrayCreated");
        Assert.assertEquals(generator.responseType("http:Ok", order), "OrderOk2");
        Assert.assertEquals(rendered(generator.usedTypeDefinitions()), List.of(
                "public type Order record { string id; };",
                "public type OrderOk2 record {| *http:Ok; Order body; |};",
                "public type OrderArrayCreated record {| *http:Created; Order[] body; |};"));
    }

    private static ApiType string(Optional<String> pattern, Optional<Integer> maxLength) {
        return new ApiType.Scalar(ScalarKind.STRING, new Constraints(pattern, Optional.empty(), maxLength,
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));
    }

    private static String typeDefinition(String name, ApiType type) {
        return rendered(new ApiTypeGenerator(spec(Map.of(name, type)), new HashSet<>()).typeDefinitions(List.of(name)))
                .get(0);
    }

    private static List<String> rendered(List<ModuleTypeDef> typeDefs) {
        return typeDefs.stream().map(typeDef -> typeDef.toString().replaceAll("\\s+", " ")).toList();
    }

    private static ApiType union(ApiType... members) {
        return new ApiType.Union(List.of(members));
    }

    private static ApiSpec spec(Map<String, ApiType> types) {
        return new ApiSpec("Test", Optional.empty(), Optional.empty(), types, List.of());
    }
}
