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

import common.BallerinaModel.ModuleTypeDef;
import mule.common.apispec.ApiSpec;
import mule.common.apispec.ApiSpec.ApiType;
import mule.common.apispec.ApiSpec.Body;
import mule.common.apispec.ApiSpec.Constraints;
import mule.common.apispec.ApiSpec.Operation;
import mule.common.apispec.ApiSpec.Response;
import mule.common.apispec.ApiSpecReadException;
import mule.common.apispec.ApiSpecReader;
import mule.common.apispec.ApiTypeGenerator;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SuccessResponseTest {

    private static final Map<String, ApiType> TYPES = Map.of("Order", new ApiType.ObjectType(List.of(),
            Map.of("id", new ApiSpec.Property(new ApiType.Scalar(ApiSpec.ScalarKind.STRING, Constraints.NONE), true)),
            true));
    private static final ApiType ORDER = new ApiType.Ref("Order");
    private static final SuccessResponse OK_JSON = new SuccessResponse(200, "http:Ok", false);

    @Test
    public void testChecksSuccessStatusesWithJsonBodiesInStatusOrder() {
        Map<Integer, Response> responses = new LinkedHashMap<>();
        responses.put(201, json(ORDER));
        responses.put(404, json(ORDER));
        responses.put(200, json(ORDER));
        responses.put(202, noBody());

        Assert.assertEquals(of(responses), List.of(new SuccessResponse(200, "OrderOk", true),
                new SuccessResponse(201, "OrderCreated", true)));
    }

    @Test
    public void testChecksBodyAgainstRecordOrOnlyAsJson() {
        Assert.assertEquals(of(Map.of(200, json(new ApiType.Array(ORDER, Constraints.NONE)))),
                List.of(new SuccessResponse(200, "OrderArrayOk", true)));
        Assert.assertEquals(of(Map.of(200, json(new ApiType.Any()))), List.of(OK_JSON));
        Assert.assertEquals(of(Map.of(200, new Response(Map.of("application/json",
                new Body(Optional.empty(), Optional.of("{}")))))), List.of(OK_JSON));
        Assert.assertEquals(of(Map.of(201, json(new ApiType.Ref("Unknown")))),
                List.of(new SuccessResponse(201, "http:Created", false)));
    }

    @Test
    public void testSkipsResponsesThatCannotBeChecked() {
        ApiTypeGenerator typeGenerator = new ApiTypeGenerator(spec(), new HashSet<>());
        Map<String, Body> jsonAndXml = new LinkedHashMap<>();
        jsonAndXml.put("application/json", new Body(Optional.of(ORDER), Optional.empty()));
        jsonAndXml.put("application/xml", new Body(Optional.of(ORDER), Optional.empty()));

        Assert.assertEquals(SuccessResponse.of(operation(Map.of(200, new Response(jsonAndXml), 201,
                new Response(Map.of("application/xml", new Body(Optional.of(ORDER), Optional.empty()))), 204,
                noBody(), 299, json(ORDER))), typeGenerator), List.of());
        Assert.assertEquals(typeGenerator.usedTypeDefinitions(), List.of());
    }

    @Test
    public void testOperationsShareResponseRecords() {
        ApiTypeGenerator typeGenerator = new ApiTypeGenerator(spec(), new HashSet<>());

        Assert.assertEquals(SuccessResponse.of(operation(Map.of(200, json(ORDER))), typeGenerator),
                SuccessResponse.of(operation(Map.of(200, json(ORDER))), typeGenerator));
        Assert.assertEquals(typeGenerator.usedTypeDefinitions().stream()
                        .map(typeDef -> typeDef.toString().replaceAll("\\s+", " ")).toList(),
                List.of("public type Order record { string id; };",
                        "public type OrderOk record {| *http:Ok; Order body; |};"));
    }

    @Test
    public void testWritesTypesOfCheckedResponses() throws ApiSpecReadException {
        ApiSpec spec = ApiSpecReader.read(Path.of("src/test/resources/apispec/orders-api/orders-api.raml"));
        ApiTypeGenerator typeGenerator = new ApiTypeGenerator(spec, new HashSet<>());
        Operation getOrder = spec.findOperation("get", "/orders/{orderId}", Optional.empty()).orElseThrow();

        Assert.assertEquals(SuccessResponse.of(getOrder, typeGenerator),
                List.of(new SuccessResponse(200, "PriorityOrderOk", true)));
        Assert.assertEquals(typeGenerator.usedTypeDefinitions().stream().map(ModuleTypeDef::name).toList(),
                List.of("Status", "Address", "Order", "PriorityOrder", "PriorityOrderOk"));
    }

    private static List<SuccessResponse> of(Map<Integer, Response> responses) {
        return SuccessResponse.of(operation(responses), new ApiTypeGenerator(spec(), new HashSet<>()));
    }

    private static Response json(ApiType type) {
        return new Response(Map.of("application/json", new Body(Optional.of(type), Optional.empty())));
    }

    private static Response noBody() {
        return new Response(Map.of());
    }

    private static ApiSpec spec() {
        return new ApiSpec("Test", Optional.empty(), Optional.empty(), TYPES, List.of());
    }

    private static Operation operation(Map<Integer, Response> responses) {
        return new Operation("get", "/orders", List.of(), List.of(), List.of(), Map.of(), responses, List.of(),
                List.of());
    }
}
