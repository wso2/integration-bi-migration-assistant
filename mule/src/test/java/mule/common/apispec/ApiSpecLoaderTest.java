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
package mule.common.apispec;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class ApiSpecLoaderTest {

    private static final String ORDERS_RAML = """
            #%RAML 1.0
            title: Orders
            /orders:
              get:
            """;

    private Path projectRoot;
    private Path apiDir;

    @BeforeMethod
    public void setUp() throws IOException {
        projectRoot = Files.createTempDirectory("api-spec-loader-test-");
        apiDir = Files.createDirectories(projectRoot.resolve("src/main/resources/api"));
    }

    @AfterMethod
    public void tearDown() throws IOException {
        try (Stream<Path> paths = Files.walk(projectRoot)) {
            for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.delete(path);
            }
        }
    }

    @Test
    public void testReadsSharedSpecOnce() throws IOException {
        Files.writeString(apiDir.resolve("orders.raml"), ORDERS_RAML);
        Map<String, ApiSpecResult> results = load(Map.of("first", "api\\orders.raml", "second", "orders.raml"));

        ApiSpecResult.Loaded first = (ApiSpecResult.Loaded) results.get("first");
        Assert.assertEquals(first.spec().title(), "Orders");
        Assert.assertSame(results.get("second"), first);
    }

    @Test
    public void testReportsEachFailureAgainstItsConfig() throws IOException {
        Files.writeString(apiDir.resolve("broken.raml"), "#%RAML 1.0\ntitle: Broken\ntypes:\n  A: !include x.raml\n");
        Map<String, String> apiRefs = new LinkedHashMap<>();
        apiRefs.put("missing", "api/missing.raml");
        apiRefs.put("broken", "api/broken.raml");
        apiRefs.put("blank", "");
        Map<String, ApiSpecResult> results = load(apiRefs);

        Assert.assertEquals(List.copyOf(results.keySet()), List.of("missing", "broken", "blank"));
        Assert.assertTrue(reason(results, "missing").startsWith("Spec file not found"), reason(results, "missing"));
        Assert.assertTrue(reason(results, "broken").startsWith("Could not parse RAML spec"), reason(results, "broken"));
        Assert.assertEquals(reason(results, "blank"), "apikit:config 'blank' has no api attribute");
    }

    private Map<String, ApiSpecResult> load(Map<String, String> apiRefsByConfig) {
        return ApiSpecLoader.load(projectRoot, apiRefsByConfig);
    }

    private static String reason(Map<String, ApiSpecResult> results, String configName) {
        return ((ApiSpecResult.Unavailable) results.get(configName)).reason();
    }
}
