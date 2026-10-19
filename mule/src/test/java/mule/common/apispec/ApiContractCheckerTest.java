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

import mule.common.apispec.ApiContractCheck.FlowIssue;
import mule.common.apispec.ApiSpec.ApiType;
import mule.common.apispec.ApiSpec.Body;
import mule.common.apispec.ApiSpec.Constraints;
import mule.common.apispec.ApiSpec.Operation;
import mule.common.apispec.ApiSpec.Parameter;
import mule.common.apispec.ApiSpec.ScalarKind;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ApiContractCheckerTest {

    private static final String CONFIG = "orders-config";
    private static final Parameter ID_QUERY_PARAM =
            new Parameter("id", new ApiType.Scalar(ScalarKind.STRING, Constraints.NONE), true);
    private static final Map<String, Body> JSON_BODY =
            Map.of("application/json", new Body(Optional.empty(), Optional.empty()));

    @Test
    public void testSplitsOperationsByWhetherAFlowImplementsThem() {
        ApiContractCheck.Checked checked = check(spec(
                        operation("get", "/orders/{orderId}", List.of(), Map.of()),
                        operation("post", "/orders", List.of(), JSON_BODY),
                        operation("delete", "/orders/{orderId}", List.of(), Map.of())),
                "get:\\orders\\(orderId):" + CONFIG, "post:\\orders:application\\json:" + CONFIG);

        Assert.assertEquals(checked.specLocation(), "src/main/resources/api/orders.raml");
        Assert.assertEquals(checked.implemented(), List.of("GET /orders/{orderId}", "POST /orders"));
        Assert.assertEquals(checked.unimplemented(), List.of("DELETE /orders/{orderId}"));
        Assert.assertTrue(checked.flowIssues().isEmpty(), checked.flowIssues().toString());
    }

    @Test
    public void testFindsOperationsWithoutFlowInSpecOrder() {
        Operation listOrders = operation("get", "/orders", List.of(), Map.of());
        Operation getOrder = operation("get", "/orders/{orderId}", List.of(), Map.of());
        Operation createOrder = operation("post", "/orders", List.of(), JSON_BODY);

        Assert.assertEquals(ApiContractChecker.unimplementedOperations(spec(listOrders, getOrder, createOrder),
                        List.of(flow("get:\\orders\\(orderId):" + CONFIG),
                                flow("post:\\orders:application\\json:" + CONFIG))),
                List.of(listOrders));
    }

    @Test
    public void testReportsPathParamDeclaredAsQueryParam() {
        ApiContractCheck.Checked checked = check(spec(operation("get", "/orders", List.of(ID_QUERY_PARAM), Map.of())),
                "get:\\orders\\(id):" + CONFIG);

        Assert.assertEquals(checked.flowIssues(), List.of(new FlowIssue("get:\\orders\\(id):" + CONFIG,
                "The spec declares no GET /orders/{id} operation; the closest is GET /orders, which takes id as "
                        + "query parameters.")));
        Assert.assertEquals(checked.unimplemented(), List.of("GET /orders"));
    }

    @Test
    public void testReportsRenamedPathParams() {
        ApiContractCheck.Checked checked = check(spec(operation("get", "/orders/{orderId}", List.of(), Map.of())),
                "get:\\orders\\(id):" + CONFIG);

        Assert.assertEquals(checked.flowIssues().get(0).issue(), "The spec declares this route as "
                + "GET /orders/{orderId}; APIkit matches path parameter names exactly.");
    }

    @Test
    public void testReportsFlowWithoutOperationAndUnacceptedMediaType() {
        ApiContractCheck.Checked checked = check(spec(operation("post", "/orders", List.of(), JSON_BODY)),
                "post:\\orders:application\\xml:" + CONFIG, "put:\\carts:" + CONFIG);

        Assert.assertEquals(checked.flowIssues().stream().map(FlowIssue::issue).toList(), List.of(
                "The spec does not accept application/xml request bodies on POST /orders; it declares "
                        + "application/json.",
                "The spec declares no PUT /carts operation."));
    }

    @Test
    public void testIgnoresMediaTypeOfOperationWithoutRequestBody() {
        ApiContractCheck.Checked checked = check(spec(operation("get", "/orders", List.of(), Map.of())),
                "get:\\orders:application\\json:" + CONFIG);

        Assert.assertEquals(checked.implemented(), List.of("GET /orders"));
        Assert.assertTrue(checked.flowIssues().isEmpty(), checked.flowIssues().toString());
    }

    @Test
    public void testCountsFlowsOfUnavailableSpec() {
        ApiContractCheck check = ApiContractChecker.check(CONFIG, "api/orders.raml",
                new ApiSpecResult.Unavailable("Spec file not found: api/orders.raml"),
                List.of(flow("get:\\orders:" + CONFIG), flow("post:\\orders:" + CONFIG)), Optional.of("1829"));

        Assert.assertEquals(check, new ApiContractCheck.Unchecked(CONFIG, "api/orders.raml",
                "Spec file not found: api/orders.raml", 2,
                List.of("API 1829 in API Manager (api-gateway:autodiscovery); check its policies there")));
    }

    @Test
    public void testListsAutodiscoveryBeforePoliciesOfSpec() {
        Operation listOrders = new Operation("get", "/orders", List.of(), List.of(), List.of(), Map.of(), Map.of(),
                List.of("client-id-enforcement"), List.of());
        ApiContractCheck check = ApiContractChecker.check(CONFIG, "api/orders.raml",
                new ApiSpecResult.Loaded(Path.of("orders.raml"), "orders.raml", spec(listOrders)), List.of(),
                Optional.of("1829"));

        Assert.assertEquals(check.policies(), List.of(
                "API 1829 in API Manager (api-gateway:autodiscovery); check its policies there",
                "client-id-enforcement trait of the API spec, on 1 of 1 operations"));
    }

    private static ApiContractCheck.Checked check(ApiSpec spec, String... flowNames) {
        ApiSpecResult.Loaded loaded = new ApiSpecResult.Loaded(Path.of("orders.raml"),
                "src/main/resources/api/orders.raml", spec);
        return (ApiContractCheck.Checked) ApiContractChecker.check(CONFIG, "api/orders.raml", loaded,
                Arrays.stream(flowNames).map(ApiContractCheckerTest::flow).toList(), Optional.empty());
    }

    private static ApiKitFlowName flow(String flowName) {
        return ApiKitFlowName.parse(flowName).orElseThrow();
    }

    private static ApiSpec spec(Operation... operations) {
        return new ApiSpec("Orders", Optional.empty(), Optional.empty(), Map.of(), List.of(operations));
    }

    private static Operation operation(String method, String path, List<Parameter> queryParams,
                                       Map<String, Body> requestBodies) {
        return new Operation(method, path, List.of(), queryParams, List.of(), requestBodies, Map.of(), List.of(),
                List.of());
    }
}
