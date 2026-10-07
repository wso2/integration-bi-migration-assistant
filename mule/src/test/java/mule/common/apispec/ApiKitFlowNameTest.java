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

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Optional;

public class ApiKitFlowNameTest {

    @Test
    public void testParsesPathWithUriParams() {
        Assert.assertEquals(ApiKitFlowName.parse("get:\\orders\\(id)\\items\\(itemId):orders-config"),
                Optional.of(new ApiKitFlowName("get:\\orders\\(id)\\items\\(itemId):orders-config", "get",
                        "/orders/{id}/items/{itemId}", Optional.empty(), "orders-config")));
    }

    @Test
    public void testParsesMediaTypeOfFourPartName() {
        ApiKitFlowName flowName = ApiKitFlowName.parse("POST:\\orders:application\\json:orders-config").orElseThrow();
        Assert.assertEquals(flowName.method(), "post");
        Assert.assertEquals(flowName.path(), "/orders");
        Assert.assertEquals(flowName.mediaType(), Optional.of("application/json"));
        Assert.assertEquals(flowName.route(), "POST /orders");
    }

    @Test
    public void testParsesRootResource() {
        Assert.assertEquals(ApiKitFlowName.parse("get:\\:orders-config").orElseThrow().path(), "/");
    }

    @Test
    public void testRejectsOtherFlowNames() {
        Assert.assertEquals(ApiKitFlowName.parse("apiKitMain"), Optional.empty());
        Assert.assertEquals(ApiKitFlowName.parse("fetch:\\orders:orders-config"), Optional.empty());
        Assert.assertEquals(ApiKitFlowName.parse("get:\\orders:"), Optional.empty());
        Assert.assertEquals(ApiKitFlowName.parse("get:a:b:c:d"), Optional.empty());
    }
}
