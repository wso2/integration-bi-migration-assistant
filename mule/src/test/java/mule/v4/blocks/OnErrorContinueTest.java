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

public class OnErrorContinueTest extends AbstractBlockTest {

    @Test
    public void testBasicOnErrorContinue() {
        testMule3ToBal("on-error-continue/basic_on_error_continue.xml",
                "on-error-continue/basic_on_error_continue.bal");
    }

    @Test
    public void testOnErrorContinueWithHttpListenerSource() {
        testMule3ToBal("on-error-continue/on_error_continue_with_http_listener_source.xml",
                "on-error-continue/on_error_continue_with_http_listener_source.bal");
    }
}
