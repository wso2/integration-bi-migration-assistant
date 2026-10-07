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
package mule.v4;

import io.ballerina.compiler.syntax.tree.SyntaxTree;
import mule.common.MuleLogger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static mule.v4.MuleToBalConverter.convertStandaloneXMLFileToBallerina;

public class ApiKitInterceptorTest {

    private static final String IMPLEMENTATION_FLOW = """
            <flow name="get:\\orders\\(id):api-config">
                <logger level="INFO" message="implementation"/>
            </flow>
            """;

    @Test
    public void testImplicitDefaultResourceWithoutRouterIsPreserved() {
        String source = convert("<logger level=\"INFO\" message=\"main\"/>", "", "", "");
        Assert.assertTrue(source.contains("resource function default"));
        Assert.assertFalse(source.contains("http:InterceptableService"));
    }

    @Test
    public void testImplicitDefaultResourceWithRouterBecomesErrorFallback() {
        String source = convert("<apikit:router config-ref=\"api-config\"/>", "", "", IMPLEMENTATION_FLOW);
        Assert.assertTrue(source.contains("resource function default [string... path](http:Request request)"));
        Assert.assertTrue(source.contains("return error APIKIT__NOT_FOUND(\"APIKIT:NOT_FOUND\");"));
        Assert.assertTrue(source.contains("resource function get orders/[string id]"));
        assertNoLegacyRouting(source);
    }

    @Test
    public void testExplicitAllowedMethodWithRouterIsPreserved() {
        String source = convert("<apikit:router config-ref=\"api-config\"/>",
                " allowedMethods=\"GET\"", "", IMPLEMENTATION_FLOW);
        Assert.assertEquals(count(source, "resource function get "), 2);
        Assert.assertTrue(source.contains("resource function get orders/[string id](http:Request request)"));
        Assert.assertTrue(source.contains("return error APIKIT__NOT_FOUND(\"APIKIT:NOT_FOUND\");"));
    }

    @Test
    public void testRouterOnlyDoesNotGenerateNormalInterceptors() {
        String source = convert("<apikit:router config-ref=\"api-config\"/>", "", "", IMPLEMENTATION_FLOW);
        Assert.assertFalse(source.contains("MuleRequestInterceptor"));
        Assert.assertFalse(source.contains("MuleResponseInterceptor"));
        Assert.assertFalse(source.contains("createInterceptors"));
    }

    @Test
    public void testRequestOnlyInterceptor() {
        String blocks = """
                <logger level="INFO" message="before-router"/>
                <apikit:router config-ref="api-config"/>
                """;
        String source = convert(blocks, "", "", IMPLEMENTATION_FLOW);
        Assert.assertTrue(source.contains("service class MuleRequestInterceptor0"));
        Assert.assertTrue(source.contains("log:printInfo(\"before-router\")"));
        Assert.assertFalse(source.contains("service class MuleResponseInterceptor0"));
    }

    @Test
    public void testResponseOnlyInterceptor() {
        String blocks = """
                <apikit:router config-ref="api-config"/>
                <logger level="INFO" message="after-router"/>
                """;
        String source = convert(blocks, "", "", IMPLEMENTATION_FLOW);
        Assert.assertFalse(source.contains("service class MuleRequestInterceptor0"));
        Assert.assertTrue(source.contains("service class MuleResponseInterceptor0"));
        Assert.assertTrue(source.contains("log:printInfo(\"after-router\")"));
    }

    @Test
    public void testProcessorPartitionAndInterceptorOrder() {
        String blocks = """
                <logger level="INFO" message="request-one"/>
                <logger level="INFO" message="request-two"/>
                <apikit:router config-ref="api-config"/>
                <logger level="INFO" message="response-one"/>
                <logger level="INFO" message="response-two"/>
                """;
        String source = convert(blocks, "", "", IMPLEMENTATION_FLOW);
        assertOrdered(source, "request-one", "request-two");
        assertOrdered(source, "response-one", "response-two");
        Assert.assertTrue(source.contains("returns [MuleRequestInterceptor0, MuleResponseInterceptor0]"));
        Assert.assertEquals(count(source, "request-one"), 1);
        Assert.assertEquals(count(source, "response-one"), 1);
    }

    @Test
    public void testInlineContinueHandlerGeneratesResponseErrorInterceptorOnly() {
        String blocks = """
                <apikit:router config-ref="api-config"/>
                <error-handler>
                    <on-error-continue logException="true">
                        <logger level="ERROR" message="continue-handler"/>
                    </on-error-continue>
                </error-handler>
                """;
        String source = convert(blocks, "", "", IMPLEMENTATION_FLOW);
        Assert.assertFalse(source.contains("MuleRequestErrorInterceptor"));
        Assert.assertTrue(source.contains("service class MuleResponseErrorInterceptor0"));
        Assert.assertTrue(source.contains("remote function interceptResponseError"));
        Assert.assertTrue(source.contains("returns [MuleResponseErrorInterceptor0]"));
        Assert.assertEquals(count(source, "// on-error-continue"), 1);
    }

    @Test
    public void testInlinePropagateHandlerPreservesHttpBehavior() {
        String blocks = """
                <apikit:router config-ref="api-config"/>
                <error-handler>
                    <on-error-propagate logException="false">
                        <logger level="ERROR" message="propagate-handler"/>
                    </on-error-propagate>
                </error-handler>
                """;
        String source = convert(blocks, "", "", IMPLEMENTATION_FLOW);
        Assert.assertEquals(count(source, "// on-error-propagate"), 1);
        Assert.assertEquals(count(source, "response.statusCode = 500"), 1);
        Assert.assertFalse(source.contains("panic err"));
    }

    @Test
    public void testReferencedGlobalErrorHandlerIsUsedByResponseErrorInterceptor() {
        String blocks = """
                <apikit:router config-ref="api-config"/>
                <error-handler ref="global-handler"/>
                """;
        String globals = """
                <error-handler name="global-handler">
                    <on-error-continue>
                        <logger level="ERROR" message="global-handler-body"/>
                    </on-error-continue>
                </error-handler>
                """;
        String source = convert(blocks, "", "", IMPLEMENTATION_FLOW, globals);
        Assert.assertEquals(count(source, "global\\-handler(ctx, err);"), 1);
        Assert.assertTrue(source.contains("public function global\\-handler(Context ctx, error err)"));
    }

    @Test
    public void testListenerResponseIsAppliedByResourceAndErrorResponseByInterceptor() {
        String source = convertWithListenerChildren("<logger level=\"INFO\" message=\"main\"/>",
                "<http:response/><http:error-response/>");
        Assert.assertFalse(source.contains("MuleResponseInterceptor"));
        Assert.assertTrue(source.contains("service class MuleResponseErrorInterceptor0"));
        Assert.assertFalse(source.contains("MuleRequestErrorInterceptor"));
    }

    @Test
    public void testApiKitResourceIsRelativeToServiceBasePath() {
        String source = convert("<apikit:router config-ref=\"api-config\"/>", "", "/api", IMPLEMENTATION_FLOW);
        Assert.assertTrue(source.contains("service /api on"));
        Assert.assertTrue(source.contains("resource function get orders/[string id]"));
        Assert.assertFalse(source.contains("resource function get api/orders"));
        assertNoLegacyRouting(source);
    }

    @Test
    public void testMultipleImplementationResources() {
        String implementationFlows = IMPLEMENTATION_FLOW + """
                <flow name="post:\\orders:api-config">
                    <logger level="INFO" message="create-order"/>
                </flow>
                """;
        String source = convert("<apikit:router config-ref=\"api-config\"/>", "", "", implementationFlows);
        Assert.assertTrue(source.contains("resource function get orders/[string id]"));
        Assert.assertTrue(source.contains("resource function post orders(http:Request request)"));
        Assert.assertEquals(count(source, "resource function get orders/[string id]"), 1);
        Assert.assertEquals(count(source, "resource function post orders(http:Request request)"), 1);
    }

    private static String convert(String blocks, String listenerAttributes, String basePath,
                                  String implementationFlows) {
        return convert(blocks, listenerAttributes, basePath, implementationFlows, "");
    }

    private static String convert(String blocks, String listenerAttributes, String basePath,
                                  String implementationFlows, String globals) {
        return convert(blocks, listenerAttributes, basePath, implementationFlows, globals, "");
    }

    private static String convertWithListenerChildren(String blocks, String listenerChildren) {
        return convert(blocks, "", "", "", "", listenerChildren);
    }

    private static String convert(String blocks, String listenerAttributes, String basePath,
                                  String implementationFlows, String globals, String listenerChildren) {
        String xml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <mule xmlns="http://www.mulesoft.org/schema/mule/core"
                      xmlns:http="http://www.mulesoft.org/schema/mule/http"
                      xmlns:apikit="http://www.mulesoft.org/schema/mule/apikit">
                    <http:listener-config name="listener-config" basePath="%s">
                        <http:listener-connection host="0.0.0.0" port="8080"/>
                    </http:listener-config>
                    <apikit:config name="api-config" api="api.raml"/>
                    %s
                    <flow name="api-main">
                        <http:listener config-ref="listener-config" path="/*"%s>%s</http:listener>
                        %s
                    </flow>
                    %s
                </mule>
                """.formatted(basePath, globals, listenerAttributes, listenerChildren, blocks, implementationFlows);
        Path sourceFile = null;
        try {
            sourceFile = Files.createTempFile("apikit-interceptor", ".xml");
            Files.writeString(sourceFile, xml);
            SyntaxTree tree = convertStandaloneXMLFileToBallerina(sourceFile.toString(), new MuleLogger(false));
            return tree.toSourceCode();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (sourceFile != null) {
                try {
                    Files.deleteIfExists(sourceFile);
                } catch (IOException ignored) {
                    // The test result is more useful than a temporary-file cleanup failure.
                }
            }
        }
    }

    private static void assertNoLegacyRouting(String source) {
        Assert.assertFalse(source.contains("apikit0"));
        Assert.assertFalse(source.contains("apiKitClient"));
        Assert.assertFalse(source.contains("apiKitRedirectPath"));
        Assert.assertFalse(source.contains("localhost:"));
    }

    private static void assertOrdered(String source, String first, String second) {
        Assert.assertTrue(source.indexOf(first) >= 0);
        Assert.assertTrue(source.indexOf(first) < source.indexOf(second));
    }

    private static int count(String source, String text) {
        int count = 0;
        int index = 0;
        while ((index = source.indexOf(text, index)) >= 0) {
            count++;
            index += text.length();
        }
        return count;
    }
}
