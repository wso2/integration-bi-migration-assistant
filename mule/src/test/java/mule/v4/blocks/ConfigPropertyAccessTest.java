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

import org.testng.annotations.Test;

public class ConfigPropertyAccessTest extends AbstractBlockTest {

    @Test
    public void testConfigPropertyAccess() {
        testMule4ToBal("config-property-access/config_property_access_in_message.xml",
                "config-property-access/config_property_access_in_message.bal");
    }

    @Test
    public void testGlobalProperty() {
        testMule4ToBal("config-property-access/global_property.xml",
                "config-property-access/global_property.bal");
    }
}
