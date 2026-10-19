/*
 *  Copyright (c) 2026, WSO2 LLC. (http://www.wso2.com).
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

public class CorrelationIdTest extends AbstractBlockTest {

    @Test
    public void testCorrelationIdReads() {
        testMule4ToBal("correlation-id/correlation_id_reads.xml", "correlation-id/correlation_id_reads.bal");
    }

    @Test
    public void testCorrelationIdInSubFlow() {
        testMule4ToBal("correlation-id/correlation_id_in_sub_flow.xml",
                "correlation-id/correlation_id_in_sub_flow.bal");
    }

    @Test
    public void testCorrelationIdInDataWeave() {
        testMule4ToBal("correlation-id/correlation_id_in_dataweave.xml",
                "correlation-id/correlation_id_in_dataweave.bal");
    }

    @Test
    public void testCorrelationIdWithErrorHandler() {
        testMule4ToBal("correlation-id/correlation_id_with_error_handler.xml",
                "correlation-id/correlation_id_with_error_handler.bal");
    }

    @Test
    public void testCorrelationIdWithMultipleMethods() {
        testMule4ToBal("correlation-id/correlation_id_with_multiple_methods.xml",
                "correlation-id/correlation_id_with_multiple_methods.bal");
    }

    @Test
    public void testCorrelationIdWithApiKit() {
        testMule4ToBal("correlation-id/correlation_id_with_apikit.xml",
                "correlation-id/correlation_id_with_apikit.bal");
    }

    @Test
    public void testCorrelationIdWithoutHttpSource() {
        testMule4ToBal("correlation-id/correlation_id_without_http_source.xml",
                "correlation-id/correlation_id_without_http_source.bal");
    }

    @Test
    public void testHttpRequestSendsCorrelationId() {
        testMule4ToBal("correlation-id/http_request_sends_correlation_id.xml",
                "correlation-id/http_request_sends_correlation_id.bal");
    }

    @Test
    public void testHttpRequestNeverSendsCorrelationId() {
        testMule4ToBal("correlation-id/http_request_never_sends_correlation_id.xml",
                "correlation-id/http_request_never_sends_correlation_id.bal");
    }

    @Test
    public void testHttpRequestWithPropertyPathSendsCorrelationId() {
        testMule4ToBal("correlation-id/http_request_property_path_sends_correlation_id.xml",
                "correlation-id/http_request_property_path_sends_correlation_id.bal");
    }
}
