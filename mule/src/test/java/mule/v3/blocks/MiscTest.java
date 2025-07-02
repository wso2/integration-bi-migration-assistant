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

/**
 * Tests for miscellaneous Mule 3 parts that do not fit into other categories.
 *
 * @since 1.0.2
 */
public class MiscTest extends AbstractBlockTest {

    @Test
    public void testExternalPropertyAccess() {
        testMule3ToBal("misc/external_property_access_in_message.xml", "misc/external_property_access_in_message.bal");
    }
}
