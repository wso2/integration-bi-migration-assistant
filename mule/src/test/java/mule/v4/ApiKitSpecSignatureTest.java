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

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

import static mule.MuleMigrator.migrateAndExportMuleSource;

public class ApiKitSpecSignatureTest {

    private Path outputDir;
    private String mainBal;

    @BeforeClass
    public void migrate() throws IOException {
        outputDir = Files.createTempDirectory("apiKitSpecProject-signature-test");
        migrateAndExportMuleSource("src/test/resources/mule/v4/projects/apiKitSpecProject", outputDir.toString(),
                null, null, 4, false, false, false, false);
        try (Stream<Path> files = Files.walk(outputDir)) {
            mainBal = Files.readString(files.filter(file -> file.getFileName().toString().equals("main.bal"))
                    .findFirst().orElseThrow());
        }
    }

    @AfterClass
    public void cleanUp() throws IOException {
        try (Stream<Path> paths = Files.walk(outputDir)) {
            for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.delete(path);
            }
        }
    }

    @Test
    public void testTypesParametersOfSpecOperation() {
        assertContains("resource function get api/orders/[string orderId](boolean? expand, "
                + "@http:Header MarketId marketId, @http:Header {name: \"x-correlation-id\"} string? xCorrelationId, "
                + "http:Request request) returns http:Response|error {");
        assertContains("Context ctx = {attributes: {request, response: new, uriParams: {orderId}}};");
    }

    @Test
    public void testBindsRequestBodyToPayload() {
        assertContains("resource function post api/orders(@http:Payload Order payload, http:Request request) "
                + "returns http:Response|error {");
        assertContains("Context ctx = {payload, attributes: {request, response: new}};");
    }

    @Test
    public void testKeepsFlowNameSignatureForFlowWithoutSpecOperation() {
        assertContains("resource function delete api/orders/[string orderId](http:Request request) "
                + "returns http:Response|error {");
    }

    @Test
    public void testAnswersSpecOperationWithoutFlowWithNotImplemented() {
        assertContains("""
                    resource function get api/orders(string? status, http:Request request) returns error {
                        // No flow implements this operation of the API spec
                        return error APIKIT__NOT_IMPLEMENTED("APIKIT:NOT_IMPLEMENTED");
                    }
                """);
    }

    private void assertContains(String expected) {
        Assert.assertTrue(mainBal.contains(expected), "Missing: " + expected + "\nin:\n" + mainBal);
    }
}
