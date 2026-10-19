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
        testMule4ToBal("http-request/basic_http_request.xml", "http-request/basic_http_request.bal");
    }

    @Test
    public void testHttpRequestWithPathHavingSpecialCharacters() {
        testMule4ToBal("http-request/http_request_path_with_special_characters.xml",
                "http-request/http_request_path_with_special_characters.bal");
    }

    @Test
    public void testHttpRequestWithHttpSource() {
        testMule4ToBal("http-request/http_request_with_http_source.xml",
                "http-request/http_request_with_http_source.bal");
    }

    @Test
    public void testHttpRequestWithUrlProperty() {
        testMule4ToBal("http-request/http_request_with_url_property.xml",
                "http-request/http_request_with_url_property.bal");
    }

    @Test
    public void testHttpRequestWithHeaders() {
        testMule4ToBal("http-request/http_request_with_headers.xml",
                "http-request/http_request_with_headers.bal");
    }

    @Test
    public void testHttpRequestWithInboundHeaders() {
        testMule4ToBal("http-request/http_request_with_inbound_headers.xml",
                "http-request/http_request_with_inbound_headers.bal");
    }

    @Test
    public void testHttpRequestWithInboundQueryParams() {
        testMule4ToBal("http-request/http_request_with_inbound_query_params.xml",
                "http-request/http_request_with_inbound_query_params.bal");
    }

    @Test
    public void testHttpRequestWithUriAndQueryParams() {
        testMule4ToBal("http-request/http_request_with_uri_and_query_params.xml",
                "http-request/http_request_with_uri_and_query_params.bal");
    }

    @Test
    public void testHttpRequestWithMultipleUriParams() {
        testMule4ToBal("http-request/http_request_with_multiple_uri_params.xml",
                "http-request/http_request_with_multiple_uri_params.bal");
    }

    @Test
    public void testHttpRequestWithUriParamsOnly() {
        testMule4ToBal("http-request/http_request_with_uri_params_only.xml",
                "http-request/http_request_with_uri_params_only.bal");
    }

    @Test
    public void testHttpRequestWithQueryParamsOnly() {
        testMule4ToBal("http-request/http_request_with_query_params_only.xml",
                "http-request/http_request_with_query_params_only.bal");
    }

    @Test
    public void testHttpRequestWithUnboundUriParam() {
        testMule4ToBal("http-request/http_request_with_unbound_uri_param.xml",
                "http-request/http_request_with_unbound_uri_param.bal");
    }

    @Test
    public void testHttpRequestWithUriParamInSegment() {
        testMule4ToBal("http-request/http_request_with_uri_param_in_segment.xml",
                "http-request/http_request_with_uri_param_in_segment.bal");
    }

    @Test
    public void testHttpRequestWithPropertyAndUriParamInSegment() {
        testMule4ToBal("http-request/http_request_with_property_and_uri_param_in_segment.xml",
                "http-request/http_request_with_property_and_uri_param_in_segment.bal");
    }

    @Test
    public void testHttpRequestWithBasePathAndTimeouts() {
        testMule4ToBal("http-request/http_request_with_base_path_and_timeouts.xml",
                "http-request/http_request_with_base_path_and_timeouts.bal");
    }

    @Test
    public void testHttpRequestConfigBasePathFormats() {
        testMule4ToBal("http-request/http_request_config_base_path_formats.xml",
                "http-request/http_request_config_base_path_formats.bal");
    }

    @Test
    public void testHttpRequestConfigBasePathProperty() {
        testMule4ToBal("http-request/http_request_config_base_path_property.xml",
                "http-request/http_request_config_base_path_property.bal");
    }

    @Test
    public void testHttpRequestTimeoutPrecedence() {
        testMule4ToBal("http-request/http_request_timeout_precedence.xml",
                "http-request/http_request_timeout_precedence.bal");
    }

    @Test
    public void testHttpRequestConfigurationWithOtherAttributes() {
        testMule4ToBal("http-request/http_request_configuration_with_other_attributes.xml",
                "http-request/http_request_configuration_with_other_attributes.bal");
    }

    @Test
    public void testHttpRequestTimeoutNotANumber() {
        testMule4ToBal("http-request/http_request_timeout_not_a_number.xml",
                "http-request/http_request_timeout_not_a_number.bal");
    }

    // TODO: due to some reason depends on the order, fix it
    @Test(enabled = false)
    public void testHttpRequestWithMelConfig() {
        testMule4ToBal("http-request/http_request_with_mel_config.xml",
                "http-request/http_request_with_mel_config.bal");
    }
}
