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
import mule.common.apispec.ApiSpec.Body;
import mule.common.apispec.ApiSpec.Operation;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ApiSpecTest {

    private static final Operation GET_ORDER = operation("get", "/orders/{id}", Map.of());
    private static final Operation POST_ORDER_JSON = operation("post", "/orders",
            Map.of("application/json", new Body(Optional.of(new ApiType.Ref("Order")), Optional.empty())));
    private static final ApiSpec SPEC = new ApiSpec("Orders", Optional.of("v1"), Optional.empty(), Map.of(),
            List.of(GET_ORDER, POST_ORDER_JSON));

    @Test
    public void testFindOperationIgnoresMethodCaseAndSlashes() {
        Assert.assertEquals(SPEC.findOperation("GET", "orders/{id}/", Optional.empty()), Optional.of(GET_ORDER));
    }

    @Test
    public void testFindOperationFiltersByRequestMediaType() {
        Assert.assertEquals(SPEC.findOperation("post", "/orders", Optional.of("application/json")),
                Optional.of(POST_ORDER_JSON));
        Assert.assertEquals(SPEC.findOperation("post", "/orders", Optional.of("application/xml")), Optional.empty());
    }

    @Test
    public void testFindOperationReturnsEmptyForUnknownOperation() {
        Assert.assertEquals(SPEC.findOperation("delete", "/orders/{id}", Optional.empty()), Optional.empty());
    }

    @Test
    public void testTypesKeepDeclarationOrder() {
        Map<String, ApiType> types = new LinkedHashMap<>();
        for (String name : List.of("Zebra", "Apple", "Mango", "Banana")) {
            types.put(name, new ApiType.Any());
        }
        ApiSpec spec = new ApiSpec("Ordered", Optional.empty(), Optional.empty(), types, List.of());
        Assert.assertEquals(List.copyOf(spec.types().keySet()), List.of("Zebra", "Apple", "Mango", "Banana"));
    }

    private static Operation operation(String method, String path, Map<String, Body> requestBodies) {
        return new Operation(method, path, List.of(), List.of(), List.of(), requestBodies, Map.of(), List.of(),
                List.of());
    }
}
