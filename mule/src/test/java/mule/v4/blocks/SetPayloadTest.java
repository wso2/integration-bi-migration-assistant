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

public class SetPayloadTest extends AbstractBlockTest {

    @Test
    public void testBasicSetPayload() {
        testMule4ToBal("set-payload/basic_set_payload.xml", "set-payload/basic_set_payload.bal");
    }

    @Test
    public void testMultipleSetPayloads() {
        testMule4ToBal("set-payload/multiple_set_payloads.xml", "set-payload/multiple_set_payloads.bal");
    }
}
