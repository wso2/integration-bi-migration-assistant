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
package mule.v4.model;

import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class MuleModelTest {

    @Test
    public void testParseApiKitFlowNameThreePartPattern() {
        String flowName = "get:\\orders\\(id):order-papi-config";
        MuleModel.ApiKitConfig.HTTPResourceData result =
                MuleModel.ApiKitConfig.parseApiKitFlowName(flowName);

        assertEquals(result.method(), "get");
        assertEquals(result.resourcePath(), "orders/[string id]");
        assertEquals(result.pathParams(), List.of("id"));
        assertEquals(result.configName(), "order-papi-config");
    }

    @Test
    public void testParseApiKitFlowNameFourPartPattern() {
        String flowName = "get:\\orders\\(id):application\\json:order-papi-config";
        MuleModel.ApiKitConfig.HTTPResourceData result =
                MuleModel.ApiKitConfig.parseApiKitFlowName(flowName);

        assertEquals(result.method(), "get");
        assertEquals(result.resourcePath(), "orders/[string id]");
        assertEquals(result.pathParams(), List.of("id"));
        assertEquals(result.configName(), "order-papi-config");
    }

    @Test
    public void testParseApiKitFlowNameDifferentHttpMethods() {
        // Test GET
        MuleModel.ApiKitConfig.HTTPResourceData getResult =
                MuleModel.ApiKitConfig.parseApiKitFlowName("get:\\users:config");
        assertEquals(getResult.method(), "get");

        // Test POST
        MuleModel.ApiKitConfig.HTTPResourceData postResult =
                MuleModel.ApiKitConfig.parseApiKitFlowName("post:\\users:config");
        assertEquals(postResult.method(), "post");

        // Test PUT
        MuleModel.ApiKitConfig.HTTPResourceData putResult =
                MuleModel.ApiKitConfig.parseApiKitFlowName("put:\\users\\(id):config");
        assertEquals(putResult.method(), "put");

        // Test DELETE
        MuleModel.ApiKitConfig.HTTPResourceData deleteResult =
                MuleModel.ApiKitConfig.parseApiKitFlowName("delete:\\users\\(id):config");
        assertEquals(deleteResult.method(), "delete");

        // Test PATCH
        MuleModel.ApiKitConfig.HTTPResourceData patchResult =
                MuleModel.ApiKitConfig.parseApiKitFlowName("patch:\\users\\(id):config");
        assertEquals(patchResult.method(), "patch");
    }

    @Test
    public void testParseApiKitFlowNameMultiplePathParameters() {
        String flowName = "get:\\orders\\(orderId)\\items\\(itemId):order-config";
        MuleModel.ApiKitConfig.HTTPResourceData result =
                MuleModel.ApiKitConfig.parseApiKitFlowName(flowName);

        assertEquals(result.method(), "get");
        assertEquals(result.resourcePath(), "orders/[string orderId]/items/[string itemId]");
        assertEquals(result.pathParams(), List.of("orderId", "itemId"));
    }

    @Test
    public void testParseApiKitFlowNameNoPathParameters() {
        String flowName = "get:\\orders:order-config";
        MuleModel.ApiKitConfig.HTTPResourceData result =
                MuleModel.ApiKitConfig.parseApiKitFlowName(flowName);

        assertEquals(result.method(), "get");
        assertEquals(result.resourcePath(), "orders");
        assertTrue(result.pathParams().isEmpty());
        assertEquals(result.configName(), "order-config");
    }

    @Test
    public void testParseApiKitFlowNameWithLeadingBackslash() {
        String flowName = "get:\\orders\\(id):config";
        MuleModel.ApiKitConfig.HTTPResourceData result =
                MuleModel.ApiKitConfig.parseApiKitFlowName(flowName);

        assertEquals(result.resourcePath(), "orders/[string id]");
    }

    @Test
    public void testParseApiKitFlowNameWithoutLeadingBackslash() {
        String flowName = "get:orders\\(id):config";
        MuleModel.ApiKitConfig.HTTPResourceData result =
                MuleModel.ApiKitConfig.parseApiKitFlowName(flowName);

        assertEquals(result.resourcePath(), "orders/[string id]");
    }

    @Test
    public void testParseApiKitFlowNameFourPartPatternWithDifferentPayloadFormats() {
        // Test with application/json
        MuleModel.ApiKitConfig.HTTPResourceData jsonResult =
                MuleModel.ApiKitConfig.parseApiKitFlowName("get:\\orders\\(id):application\\json:config");
        assertEquals(jsonResult.resourcePath(), "orders/[string id]");

        // Test with application/xml
        MuleModel.ApiKitConfig.HTTPResourceData xmlResult =
                MuleModel.ApiKitConfig.parseApiKitFlowName("get:\\orders\\(id):application\\xml:config");
        assertEquals(xmlResult.resourcePath(), "orders/[string id]");

        // Both should produce the same result (payload format is ignored)
        assertEquals(jsonResult.resourcePath(), xmlResult.resourcePath());
        assertEquals(jsonResult.pathParams(), xmlResult.pathParams());
    }
}

