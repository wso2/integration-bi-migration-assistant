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
package synapse.converter;

import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Tests that the per-artifact import handling writes correct {@code import} statements into the
 * generated files: {@code main.bal} always imports {@code ballerina/http} for the listener, while
 * {@code functions.bal} imports it only when a sequence uses {@code http:Response}. The import is kept
 * at the top of the file and deduplicated even when introduced by a later-flushed artifact.
 */
public class SynapseImportsTest {

    private static final Path IMPORTS = Path.of("src", "test", "resources", "imports");
    private static final String HTTP_IMPORT = "import ballerina/http;";

    private static Path convert(String project) throws IOException {
        Path output = Files.createTempDirectory("synapse-imports-test");
        SynapseConverter.migrateSynapse(IMPORTS.resolve(project).toString(), output.toString(),
                false, false, false, false, Optional.of("testOrg"), Optional.of("pkg"));
        return output;
    }

    private static int count(String content, String needle) {
        int count = 0;
        for (int index = content.indexOf(needle); index >= 0; index = content.indexOf(needle, index + 1)) {
            count++;
        }
        return count;
    }

    @Test
    public void mainAlwaysImportsHttpAtTheTop() throws IOException {
        Path output = convert("no-http-import");
        try {
            String main = Files.readString(output.resolve("main.bal"));
            assertTrue(main.stripLeading().startsWith(HTTP_IMPORT), "main.bal must import http at the top");
        } finally {
            TestUtils.deleteDirectory(output);
        }
    }

    @Test
    public void functionsWithoutHttpUsageHaveNoImport() throws IOException {
        Path output = convert("no-http-import");
        try {
            String functions = Files.readString(output.resolve("functions.bal"));
            assertFalse(functions.contains(HTTP_IMPORT), "functions.bal must not import http when unused");
        } finally {
            TestUtils.deleteDirectory(output);
        }
    }

    @Test
    public void httpImportPrependedWhenIntroducedByLaterArtifact() throws IOException {
        Path output = convert("late-http-import");
        try {
            String functions = Files.readString(output.resolve("functions.bal"));
            // 'plain' (no http) is flushed before 'withPayload' (needs http), yet the import stays on top.
            assertTrue(functions.stripLeading().startsWith(HTTP_IMPORT),
                    "http import must be prepended to the top of functions.bal");
            assertTrue(functions.contains("function plain()"));
            assertTrue(functions.contains("function withPayload(http:Response response)"));
        } finally {
            TestUtils.deleteDirectory(output);
        }
    }

    @Test
    public void httpImportDeduplicatedAcrossArtifacts() throws IOException {
        Path output = convert("deduplicated-http-import");
        try {
            String functions = Files.readString(output.resolve("functions.bal"));
            assertEquals(count(functions, HTTP_IMPORT), 1, "http import must appear exactly once");
        } finally {
            TestUtils.deleteDirectory(output);
        }
    }

    @Test
    public void proxyOnlyProjectWritesListenerSkeletonWithHttp() throws IOException {
        Path output = convert("listener-only");
        try {
            String main = Files.readString(output.resolve("main.bal"));
            assertTrue(main.stripLeading().startsWith(HTTP_IMPORT));
            assertTrue(main.contains("listener http:Listener httpListener"));
            assertFalse(Files.exists(output.resolve("functions.bal")), "a proxy has no functions");
        } finally {
            TestUtils.deleteDirectory(output);
        }
    }
}
