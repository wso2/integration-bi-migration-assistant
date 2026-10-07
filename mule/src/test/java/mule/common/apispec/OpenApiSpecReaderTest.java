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

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import mule.common.apispec.ApiSpec.ApiType;
import mule.common.apispec.ApiSpec.Constraints;
import mule.common.apispec.ApiSpec.Operation;
import mule.common.apispec.ApiSpec.ScalarKind;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class OpenApiSpecReaderTest {

    private static final ApiType STRING = new ApiType.Scalar(ScalarKind.STRING, Constraints.NONE);
    private static final ApiType INTEGER = new ApiType.Scalar(ScalarKind.INTEGER, Constraints.NONE);

    @Test
    public void testMapsNullableToUnionWithNil() {
        Assert.assertEquals(type(new StringSchema().nullable(true)), new ApiType.Union(List.of(STRING,
                new ApiType.Nil())));
        Schema<?> nullableUnion = new Schema<>().oneOf(List.of(new StringSchema(), new Schema<>().type("integer")))
                .nullable(true);
        Assert.assertEquals(type(nullableUnion), new ApiType.Union(List.of(STRING, INTEGER, new ApiType.Nil())));
    }

    @Test
    public void testMapsNullOnlyOptionToNil() {
        ObjectSchema nullOnly = new ObjectSchema();
        nullOnly.setNullable(true);
        nullOnly.setEnum(Collections.singletonList(null));
        Schema<?> nullableRef = new Schema<>().oneOf(List.of(new Schema<>().$ref("#/components/schemas/Order"),
                nullOnly));
        Assert.assertEquals(type(nullableRef), new ApiType.Union(List.of(new ApiType.Ref("Order"),
                new ApiType.Nil())));
    }

    @Test
    public void testMapsClosedAndOpenObjects() {
        Schema<?> closed = new ObjectSchema().addProperty("id", new StringSchema()).additionalProperties(false);
        Assert.assertEquals(type(closed), new ApiType.ObjectType(List.of(),
                Map.of("id", new ApiSpec.Property(STRING, false)), false));
        Assert.assertTrue(((ApiType.ObjectType) type(new ObjectSchema())).open());
    }

    @Test
    public void testMapsStringFormats() {
        Assert.assertEquals(kind(new StringSchema().format("date")), ScalarKind.DATE);
        Assert.assertEquals(kind(new StringSchema().format("time")), ScalarKind.TIME);
        Assert.assertEquals(kind(new StringSchema().format("date-time")), ScalarKind.DATE_TIME);
        Assert.assertEquals(kind(new StringSchema().format("datetime-only")), ScalarKind.DATETIME_ONLY);
        Assert.assertEquals(kind(new StringSchema().format("binary")), ScalarKind.FILE);
        Assert.assertEquals(kind(new StringSchema().format("email")), ScalarKind.STRING);
    }

    @Test
    public void testMapsRefOutsideComponentsToAny() {
        Assert.assertEquals(type(new Schema<>().$ref("https://example.com/schemas/order.json")), new ApiType.Any());
    }

    @Test
    public void testMergesPathLevelParametersAndSynthesizesUndeclaredPathParams() {
        PathItem pathItem = new PathItem()
                .addParametersItem(new Parameter().in("query").name("limit").schema(new StringSchema()))
                .get(new io.swagger.v3.oas.models.Operation()
                        .addParametersItem(new Parameter().in("query").name("limit")
                                .schema(new Schema<>().type("integer")).required(true))
                        .addParametersItem(new Parameter().in("path").name("itemId").schema(new StringSchema()
                                .pattern("^[0-9]+$"))));
        Operation get = read(new OpenAPI().paths(new Paths().addPathItem("/orders/{orderId}/items/{itemId}",
                pathItem))).operations().get(0);

        Assert.assertEquals(get.queryParams(), List.of(new ApiSpec.Parameter("limit", INTEGER, true)));
        Assert.assertEquals(get.uriParams().stream().map(ApiSpec.Parameter::name).toList(),
                List.of("orderId", "itemId"));
        Assert.assertEquals(get.uriParams().get(0), new ApiSpec.Parameter("orderId", STRING, true));
        Assert.assertTrue(get.uriParams().get(1).required());
    }

    @Test
    public void testReadsResponsesExamplesAndGlobalSecurity() {
        MediaType json = new MediaType()
                .examples(Map.of("order", new Example().value(Map.of("orderId", 7))));
        ApiResponses responses = new ApiResponses()
                .addApiResponse("200", new ApiResponse().content(new Content().addMediaType("application/json", json)))
                .addApiResponse("default", new ApiResponse());
        OpenAPI openApi = new OpenAPI()
                .components(new Components())
                .addSecurityItem(new SecurityRequirement().addList("oauth"))
                .paths(new Paths().addPathItem("/orders", new PathItem().get(
                        new io.swagger.v3.oas.models.Operation().responses(responses))));

        Operation get = read(openApi).operations().get(0);
        Assert.assertEquals(List.copyOf(get.responses().keySet()), List.of(200));
        Assert.assertEquals(get.responses().get(200).bodies().get("application/json").example(),
                Optional.of("{\"orderId\":7}"));
        Assert.assertEquals(get.securedBy(), List.of("oauth"));
    }

    private static ApiSpec read(OpenAPI openApi) {
        return OpenApiSpecReader.read(openApi);
    }

    private static ApiType type(Schema<?> schema) {
        return read(new OpenAPI().components(new Components().addSchemas("T", schema))).types().get("T");
    }

    private static ScalarKind kind(Schema<?> schema) {
        return ((ApiType.Scalar) type(schema)).kind();
    }
}
