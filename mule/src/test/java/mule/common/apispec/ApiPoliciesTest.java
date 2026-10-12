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

import mule.common.apispec.ApiSpec.Operation;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ApiPoliciesTest {

    @Test
    public void testDescribesPolicyTraitsAndSecuritySchemes() {
        ApiSpec spec = spec(
                operation("/orders", List.of("client-id-enforcement", "correlation-header"), List.of("oauth")),
                operation("/carts", List.of("client-id-required", "client-id-enforcement"), List.of("oauth")),
                operation("/health", List.of("correlation-header"), List.of()));

        Assert.assertEquals(ApiPolicies.describe(spec), List.of(
                "client-id-enforcement trait of the API spec, on 2 of 3 operations",
                "client-id-required trait of the API spec, on 1 of 3 operations",
                "oauth security scheme of the API spec, on 2 of 3 operations"));
    }

    @Test
    public void testDescribesNothingForSpecWithoutPolicies() {
        Assert.assertEquals(ApiPolicies.describe(spec(operation("/orders", List.of("pagination"), List.of()))),
                List.of());
    }

    @Test
    public void testDescribesAutodiscovery() {
        Assert.assertEquals(ApiPolicies.autodiscovery("18291872"),
                "API 18291872 in API Manager (api-gateway:autodiscovery); check its policies there");
    }

    private static ApiSpec spec(Operation... operations) {
        return new ApiSpec("Orders", Optional.empty(), Optional.empty(), Map.of(), List.of(operations));
    }

    private static Operation operation(String path, List<String> traits, List<String> securedBy) {
        return new Operation("get", path, List.of(), List.of(), List.of(), Map.of(), Map.of(), traits, securedBy);
    }
}
