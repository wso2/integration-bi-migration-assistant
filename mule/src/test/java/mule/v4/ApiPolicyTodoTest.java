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
 *  KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package mule.v4;

import mule.common.MuleLogger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static mule.v4.MuleToBalConverter.convertStandaloneXMLFileToBallerina;

public class ApiPolicyTodoTest {

    private static final String TODO = """
            // TODO: In Mule, API Manager enforced policies on this API, and this service does not enforce them:
            //   - API ${api.id} in API Manager (api-gateway:autodiscovery); check its policies there
            // Put an API gateway in front of this service, or add the checks, for example in an \
            http:RequestInterceptor.
            """;

    @Test
    public void testPutsTodoOnServiceOfAutodiscoveredApiKitFlow() {
        // The console flow comes first, so the APIkit flow's service is merged into the console flow's one
        String source = convert("""
                <flow name="api-console">
                    <http:listener config-ref="listener-config" path="/console/*"/>
                    <logger level="INFO" message="console"/>
                </flow>
                <flow name="api-main">
                    <http:listener config-ref="listener-config" path="/api/*"/>
                    <apikit:router config-ref="api-config"/>
                </flow>
                <flow name="get:\\orders:api-config">
                    <logger level="INFO" message="orders"/>
                </flow>
                """, "api-main");

        Assert.assertTrue(source.contains(TODO + "service / on listener\\-config {"), source);
        Assert.assertFalse(source.contains("<api-gateway:autodiscovery"), source);
    }

    @Test
    public void testPutsTodoOnlyOnServiceOfAutodiscoveredFlow() {
        String source = convert("""
                <flow name="orders">
                    <http:listener config-ref="listener-config" path="/orders"/>
                    <logger level="INFO" message="orders"/>
                </flow>
                <flow name="health">
                    <http:listener config-ref="other-listener-config" path="/health"/>
                    <logger level="INFO" message="health"/>
                </flow>
                """, "orders");

        Assert.assertTrue(source.contains(TODO + "service / on listener\\-config {"), source);
        Assert.assertEquals(source.split("API Manager enforced", -1).length, 2, source);
    }

    @Test
    public void testAddsNoTodoWithoutPolicies() {
        String source = convert("""
                <flow name="orders">
                    <http:listener config-ref="listener-config" path="/orders"/>
                    <logger level="INFO" message="orders"/>
                </flow>
                """, "");

        Assert.assertFalse(source.contains("API Manager"), source);
    }

    private static String convert(String flows, String autodiscoveredFlow) {
        String xml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <mule xmlns="http://www.mulesoft.org/schema/mule/core"
                      xmlns:http="http://www.mulesoft.org/schema/mule/http"
                      xmlns:apikit="http://www.mulesoft.org/schema/mule/apikit"
                      xmlns:api-gateway="http://www.mulesoft.org/schema/mule/api-gateway">
                    <http:listener-config name="listener-config">
                        <http:listener-connection host="0.0.0.0" port="8080"/>
                    </http:listener-config>
                    <http:listener-config name="other-listener-config">
                        <http:listener-connection host="0.0.0.0" port="8081"/>
                    </http:listener-config>
                    <apikit:config name="api-config" api="api.raml"/>
                    %s
                    %s
                </mule>
                """.formatted(autodiscoveredFlow.isEmpty() ? "" : """
                <api-gateway:autodiscovery apiId="${api.id}" flowRef="%s"/>""".formatted(autodiscoveredFlow), flows);
        try {
            Path sourceFile = Files.createTempFile("api-policy-todo", ".xml");
            try {
                Files.writeString(sourceFile, xml);
                return convertStandaloneXMLFileToBallerina(sourceFile.toString(), new MuleLogger(false))
                        .toSourceCode();
            } finally {
                Files.delete(sourceFile);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
