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

public class UnsupportedBlockTest extends AbstractBlockTest {

    @Test
    public void testUnsupportedBlock() {
        testMule3ToBal("unsupported-block/unsupported_block.xml", "unsupported-block/unsupported_block.bal");
    }

    @Test
    public void testUnsupportedSource() {
        testMule3ToBal("unsupported-block/unsupported_source.xml", "unsupported-block/unsupported_source.bal");
    }
}
