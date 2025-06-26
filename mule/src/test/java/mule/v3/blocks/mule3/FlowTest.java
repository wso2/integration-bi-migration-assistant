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
package mule.v3.blocks.mule3;

import mule.v3.blocks.AbstractBlockTest;
import org.testng.annotations.Test;

public class FlowTest extends AbstractBlockTest {

    @Test
    public void testBasicFlow() {
        testMule3ToBal("flow/basic_flow.xml", "flow/basic_flow.bal");
    }

    @Test
    public void testPrivateFlow() {
        testMule3ToBal("flow/private_flow.xml", "flow/private_flow.bal");
    }
}
