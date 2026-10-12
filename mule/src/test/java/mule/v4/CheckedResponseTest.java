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

public class CheckedResponseTest {

    private static final Path SPEC_PROJECT = Path.of("src/test/resources/mule/v4/projects/apiKitSpecProject");

    private Path workDir;
    private String mainBal;

    @BeforeClass
    public void migrateWithoutStatusCode() throws IOException {
        workDir = Files.createTempDirectory("checked-response-test");
        Path project = workDir.resolve("apiKitSpecProject");
        try (Stream<Path> sources = Files.walk(SPEC_PROJECT)) {
            for (Path source : sources.toList()) {
                Path relative = SPEC_PROJECT.relativize(source);
                if (!relative.getName(0).toString().endsWith("_ballerina")) {
                    Files.copy(source, project.resolve(relative.toString()));
                }
            }
        }
        Path mainXml = project.resolve("src/main/mule/main.xml");
        Files.writeString(mainXml, Files.readString(mainXml).replaceAll("(?s)<http:response .*</http:response>\\s*",
                ""));

        Path outputDir = workDir.resolve("output");
        migrateAndExportMuleSource(project.toString(), outputDir.toString(), null, null, 4, false, false, false,
                true, false);
        try (Stream<Path> files = Files.walk(outputDir)) {
            mainBal = Files.readString(files.filter(file -> file.getFileName().toString().equals("main.bal"))
                    .findFirst().orElseThrow());
        }
    }

    @AfterClass
    public void cleanUp() throws IOException {
        try (Stream<Path> paths = Files.walk(workDir)) {
            for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.delete(path);
            }
        }
    }

    @Test
    public void testChecksTwoHundredResponseWhenTheStatusIsAlwaysTwoHundred() {
        assertContains("http:Request request) returns PriorityOrderOk|error {");
        assertContains("return <PriorityOrderOk>{body: check constraint:validate(check jsonPayload(ctx.payload))};");
        Assert.assertFalse(mainBal.contains("int statusCode"), mainBal);
    }

    @Test
    public void testLeavesOperationWithoutTwoHundredResponseUnchecked() {
        assertContains("resource function post api/orders(@http:Payload Order payload, http:Request request) "
                + "returns http:Response|error {");
    }

    private void assertContains(String expected) {
        Assert.assertTrue(mainBal.contains(expected), "Missing: " + expected + "\nin:\n" + mainBal);
    }
}
