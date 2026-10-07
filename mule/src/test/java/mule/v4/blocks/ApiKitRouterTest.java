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
package mule.v4.blocks;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ApiKitRouterTest extends AbstractBlockTest {

    @Test(dataProvider = "allowedMethodsWithRouter")
    public void testAllowedMethodsWithApiKitRouter(String inputFile, String outputFile) {
        testMule4ToBal(inputFile, outputFile);
    }

    @DataProvider(name = "allowedMethodsWithRouter")
    public Object[][] allowedMethodsWithRouter() {
        return new Object[][] {
                {"apikit-router/no_allowed_methods.xml", "apikit-router/no_allowed_methods.bal"},
                {"apikit-router/single_allowed_method.xml", "apikit-router/single_allowed_method.bal"},
                {"apikit-router/multiple_allowed_methods.xml", "apikit-router/multiple_allowed_methods.bal"},
                {"apikit-router/error_handlers.xml", "apikit-router/error_handlers.bal"},
                {"apikit-router/response_bodies_with_any_error_handler.xml",
                        "apikit-router/response_bodies_with_any_error_handler.bal"},
                {"apikit-router/nested_listener_path.xml", "apikit-router/nested_listener_path.bal"},
                {"apikit-router/unresolved_listener_path.xml", "apikit-router/unresolved_listener_path.bal"}
        };
    }
}
