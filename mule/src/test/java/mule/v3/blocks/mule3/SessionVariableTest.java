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

public class SessionVariableTest extends AbstractBlockTest {

    @Test
    public void testBasicSetSessionVariable() {
        testMule3ToBal("session-variable/basic_set_session_variable.xml",
                "session-variable/basic_set_session_variable.bal");
    }

    @Test
    public void testUpdatingSameSessionVariable() {
        testMule3ToBal("session-variable/updating_same_session_variable.xml",
                "session-variable/updating_same_session_variable.bal");
    }

    @Test
    public void testSetSessionVariableWithHttpSource() {
        testMule3ToBal("session-variable/set_session_variable_with_http_source.xml",
                "session-variable/set_session_variable_with_http_source.bal");
    }

    @Test
    public void testSimpleRemoveSessionVariable() {
        testMule3ToBal("session-variable/simple_remove_session_variable.xml",
                "session-variable/simple_remove_session_variable.bal");
    }
}
