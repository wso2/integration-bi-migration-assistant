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
package mule.common;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class ProjectPropertiesTest {

    private Path dir;
    private ProjectProperties properties;

    @BeforeClass
    public void setUp() throws IOException {
        dir = Files.createTempDirectory("project-properties-test");
        File appProperties = Files.writeString(dir.resolve("mule-app.properties"),
                "http.port=9090\nhttp.listener.path=/from-properties/*\n").toFile();
        File configuration = Files.writeString(dir.resolve("configuration.yaml"), """
                http:
                  listener:
                    path: "/api/*"
                    port: 8092
                  hosts: [a, b]
                """).toFile();
        File overrides = Files.writeString(dir.resolve("overrides.yaml"), "api:\n  version: v2\n").toFile();
        properties = ProjectProperties.load(List.of(appProperties), List.of(configuration, overrides),
                new MuleLogger(false));
    }

    @AfterClass
    public void tearDown() throws IOException {
        try (Stream<Path> paths = Files.walk(dir)) {
            for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.delete(path);
            }
        }
    }

    @Test
    public void testResolvesPlaceholdersFromPropertiesAndNestedYaml() {
        Assert.assertEquals(properties.resolve("${http.port}"), Optional.of("9090"));
        Assert.assertEquals(properties.resolve("${http.listener.port}"), Optional.of("8092"));
        Assert.assertEquals(properties.resolve("${api.version}"), Optional.of("v2"));
    }

    @Test
    public void testFirstFileWins() {
        Assert.assertEquals(properties.resolve("${http.listener.path}"), Optional.of("/from-properties/*"));
    }

    @Test
    public void testIgnoresSecurePrefix() {
        Assert.assertEquals(properties.resolve("${secure::api.version}"), Optional.of("v2"));
    }

    @Test
    public void testReturnsPlainValuesUnchangedAndUnknownPlaceholdersAsEmpty() {
        Assert.assertEquals(properties.resolve("/orders/*"), Optional.of("/orders/*"));
        Assert.assertEquals(properties.resolve("${undefined.key}"), Optional.empty());
        Assert.assertEquals(properties.resolve("${http.hosts}"), Optional.empty());
    }
}
