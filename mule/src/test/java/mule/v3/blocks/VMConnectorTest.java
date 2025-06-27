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
package mule.v3.blocks;

import org.testng.annotations.Test;

public class VMConnectorTest extends AbstractBlockTest {

    @Test
    public void testSimpleVMConnector() {
        testMule3ToBal("vm-connector/simple_vm_connector.xml", "vm-connector/simple_vm_connector.bal");
    }

    @Test
    public void testVMConnectorWithHttpSource() {
        testMule3ToBal("vm-connector/vm_connector_wth_http_source.xml",
                "vm-connector/vm_connector_wth_http_source.bal");
    }

    @Test
    public void testVMConnectorInsideAnASyncBlock() {
        testMule3ToBal("vm-connector/vm_connector_inside_async_block.xml",
                "vm-connector/vm_connector_inside_async_block.bal");
    }
}
