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

public class HttpRequestTest extends AbstractBlockTest {

    @Test
    public void testBasicHttpRequest() {
        testMule3ToBal("http-request/basic_http_request.xml", "http-request/basic_http_request.bal");
    }

    @Test
    public void testHttpRequestWithPathHavingSpecialCharacters() {
        testMule3ToBal("http-request/http_request_path_with_special_characters.xml",
                "http-request/http_request_path_with_special_characters.bal");
    }

    @Test
    public void testHttpRequestWithHttpSource() {
        testMule3ToBal("http-request/http_request_with_http_source.xml",
                "http-request/http_request_with_http_source.bal");
    }

    @Test
    public void testHttpRequestWithUrlProperty() {
        testMule3ToBal("http-request/http_request_with_url_property.xml",
                "http-request/http_request_with_url_property.bal");
    }
}
