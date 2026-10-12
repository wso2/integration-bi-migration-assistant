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

import mule.common.apispec.ApiSpec.ApiType;
import mule.common.apispec.ApiSpec.Constraints;
import mule.common.apispec.ApiSpec.Operation;
import mule.common.apispec.ApiSpec.Parameter;
import mule.common.apispec.ApiSpec.Property;
import mule.common.apispec.ApiSpec.ScalarKind;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ApiSpecReaderTest {

    private static final Path ORDERS_API = Path.of("src/test/resources/apispec/orders-api/orders-api.raml");
    private static final ApiType STRING = new ApiType.Scalar(ScalarKind.STRING, Constraints.NONE);

    private ApiSpec spec;

    @BeforeClass
    public void readSpec() throws ApiSpecReadException {
        spec = ApiSpecReader.read(ORDERS_API);
    }

    @Test
    public void testReadsApiMetadata() {
        Assert.assertEquals(spec.title(), "Orders API");
        Assert.assertEquals(spec.version(), Optional.of("v2"));
        Assert.assertEquals(spec.baseUri(), Optional.of("https://api.example.com/orders"));
    }

    @Test
    public void testReadsOperationsInDeclarationOrder() {
        Assert.assertEquals(spec.operations().stream().map(op -> op.method() + " " + op.path()).toList(),
                List.of("post /orders", "get /orders/{orderId}"));
    }

    @Test
    public void testKeysUntypedBodyByDefaultMediaType() {
        Operation post = operation("post", "/orders");
        Assert.assertEquals(post.requestBodies().keySet(), Set.of("application/json"));
        Assert.assertEquals(post.requestBodies().get("application/json").type(),
                Optional.of(new ApiType.Ref("Order")));
        Assert.assertEquals(post.securedBy(), List.of("basic"));
    }

    @Test
    public void testReadsResponsesWithRootRelativeExampleInclude() {
        Operation post = operation("post", "/orders");
        Assert.assertEquals(List.copyOf(post.responses().keySet()), List.of(201));
        Assert.assertTrue(post.responses().get(201).bodies().get("application/json").example().orElseThrow()
                .contains("\"orderId\": \"1001\""));

        Operation get = operation("get", "/orders/{orderId}");
        Assert.assertEquals(List.copyOf(get.responses().keySet()), List.of(200, 404));
        Assert.assertTrue(get.responses().get(404).bodies().isEmpty());
    }

    @Test
    public void testReadsParameters() {
        Operation get = operation("get", "/orders/{orderId}");
        Assert.assertEquals(get.uriParams(), List.of(new Parameter("orderId", STRING, true)));
        Assert.assertEquals(get.queryParams(),
                List.of(new Parameter("expand", new ApiType.Scalar(ScalarKind.BOOLEAN, Constraints.NONE), false)));
        Assert.assertEquals(get.headers(), List.of(
                new Parameter("marketId", new ApiType.Scalar(ScalarKind.STRING, new Constraints(
                        Optional.of("^[A-Z]{2}$"), Optional.empty(), Optional.empty(), Optional.empty(),
                        Optional.empty(), Optional.empty(), Optional.empty())), true),
                new Parameter("x-correlation-id", STRING, false)));
    }

    @Test
    public void testReadsTypes() {
        Assert.assertEquals(List.copyOf(spec.types().keySet()), List.of("Status", "Address", "Order", "PriorityOrder"));
        Assert.assertEquals(spec.types().get("Status"), new ApiType.StringEnum(List.of("NEW", "SHIPPED")));

        ApiType.ObjectType order = (ApiType.ObjectType) spec.types().get("Order");
        Assert.assertEquals(order.properties().get("orderId"), new Property(new ApiType.Scalar(ScalarKind.STRING,
                new Constraints(Optional.of("^[0-9]+$"), Optional.empty(), Optional.of(10), Optional.empty(),
                        Optional.empty(), Optional.empty(), Optional.empty())), true));
        Assert.assertEquals(order.properties().get("status"), new Property(new ApiType.Ref("Status"), true));
        ApiType.Scalar quantity = (ApiType.Scalar) order.properties().get("quantity").type();
        Assert.assertEquals(quantity.kind(), ScalarKind.INTEGER);
        Assert.assertEquals(quantity.constraints().minimum().orElseThrow().compareTo(BigDecimal.ONE), 0);
        Assert.assertEquals(quantity.constraints().maximum().orElseThrow().compareTo(BigDecimal.valueOf(99)), 0);
        Assert.assertEquals(order.properties().get("note"), new Property(STRING, false));
        Assert.assertEquals(((ApiType.Array) order.properties().get("addresses").type()).items(),
                new ApiType.Ref("Address"));
    }

    @Test
    public void testReadsTypeIncludedFromNestedExchangeModule() {
        Assert.assertEquals(spec.types().get("Address"), new ApiType.ObjectType(List.of(),
                Map.of("street", new Property(STRING, true), "town", new Property(STRING, false)), true));
    }

    @Test
    public void testKeepsOnlyOwnPropertiesOfSubtype() {
        Assert.assertEquals(spec.types().get("PriorityOrder"), new ApiType.ObjectType(List.of("Order"),
                Map.of("priority", new Property(new ApiType.Scalar(ScalarKind.BOOLEAN, Constraints.NONE), true)),
                true));
    }

    @Test
    public void testReadsTraitNamesOfEachOperation() throws IOException, ApiSpecReadException {
        Path raml = Files.createTempFile("api-spec-reader-test-", ".raml");
        try {
            Files.writeString(raml, """
                    #%RAML 1.0
                    title: Traits
                    traits:
                      client-id-enforcement:
                        headers:
                          client-id: string
                    /orders:
                      is: [client-id-enforcement]
                      get:
                      /{id}:
                        get:
                    """);
            ApiSpec traitSpec = ApiSpecReader.read(raml);

            Assert.assertEquals(traitSpec.findOperation("get", "/orders", Optional.empty()).orElseThrow().traits(),
                    List.of("client-id-enforcement"));
            Assert.assertEquals(traitSpec.findOperation("get", "/orders/{id}", Optional.empty()).orElseThrow()
                    .traits(), List.of());
        } finally {
            Files.delete(raml);
        }
    }

    @Test
    public void testRejectsOpenApiSpecUntilSupported() throws IOException {
        Path oas = Files.createTempFile("api-spec-reader-test-", ".yaml");
        try {
            Files.writeString(oas, "openapi: 3.0.0\n");
            ApiSpecReadException e = Assert.expectThrows(ApiSpecReadException.class, () -> ApiSpecReader.read(oas));
            Assert.assertTrue(e.getMessage().contains("not implemented"), e.getMessage());
        } finally {
            Files.delete(oas);
        }
    }

    @Test
    public void testReportsUnparseableRaml() throws IOException {
        Path raml = Files.createTempFile("api-spec-reader-test-", ".raml");
        try {
            Files.writeString(raml, "#%RAML 1.0\ntitle: Broken\ntypes:\n  A: !include missing.raml\n");
            ApiSpecReadException e = Assert.expectThrows(ApiSpecReadException.class, () -> ApiSpecReader.read(raml));
            Assert.assertTrue(e.getMessage().startsWith("Could not parse RAML spec"), e.getMessage());
        } finally {
            Files.delete(raml);
        }
    }

    private Operation operation(String method, String path) {
        return spec.findOperation(method, path, Optional.empty()).orElseThrow();
    }
}
