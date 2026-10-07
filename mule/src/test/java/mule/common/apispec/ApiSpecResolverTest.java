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

import mule.common.apispec.ApiSpecResolver.Resolution;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ApiSpecResolverTest {

    private static final String GROUP_ID = "4819fb6a-7ac0-47e8-bf20-bd93e8526e94";
    private static final String NARVAR_REF = "resource::" + GROUP_ID + ":narvar-eapi:1.0.5:raml:zip:narvar-eapi.raml";

    private Path workDir;
    private Path projectRoot;
    private Path apiDir;

    @BeforeMethod
    public void setUp() throws IOException {
        workDir = Files.createTempDirectory("api-spec-resolver-test-");
        projectRoot = workDir.resolve("project");
        apiDir = projectRoot.resolve("src/main/resources/api");
        Files.createDirectories(apiDir);
    }

    @AfterMethod
    public void tearDown() throws IOException {
        deleteTree(workDir);
    }

    @Test
    public void testResolvesWindowsSeparators() throws IOException {
        Path spec = writeFile(apiDir.resolve("plab-eapi.raml"));
        Resolution resolution = resolve("api\\plab-eapi.raml");
        assertFound(resolution, spec);
        Assert.assertEquals(((Resolution.Found) resolution).location(), "src/main/resources/api/plab-eapi.raml");
    }

    @Test
    public void testResolvesPathRelativeToResources() throws IOException {
        Path spec = writeFile(apiDir.resolve("jms-message.raml"));
        assertFound(resolve("api/jms-message.raml"), spec);
    }

    @Test
    public void testResolvesBareNameUnderApiDir() throws IOException {
        Path spec = writeFile(apiDir.resolve("sfsc-eapi.raml"));
        assertFound(resolve("sfsc-eapi.raml"), spec);
    }

    @Test
    public void testResolvesResourcesPrefix() throws IOException {
        Path spec = writeFile(apiDir.resolve("test.raml"));
        assertFound(resolve("resources:test.raml"), spec);
    }

    @Test
    public void testReportsMissingLocalSpecWithSearchedLocations() {
        Resolution.NotFound notFound = assertNotFound(resolve("api/missing.raml"));
        Assert.assertTrue(notFound.reason().contains("api/missing.raml"), notFound.reason());
        Assert.assertEquals(notFound.searched(), List.of(projectRoot.resolve("src/main/resources/api/missing.raml"),
                apiDir.resolve("api/missing.raml")));
    }

    @Test
    public void testReportsPropertyPlaceholder() {
        Assert.assertTrue(assertNotFound(resolve("${api.spec}")).reason().contains("property placeholder"));
    }

    @Test
    public void testResolvesExchangeSpecFromExchangeModules() throws IOException {
        Path spec = writeFile(apiDir.resolve("exchange_modules/" + GROUP_ID + "/narvar-eapi/1.0.5/narvar-eapi.raml"));
        assertFound(resolve(NARVAR_REF), spec);
    }

    @Test
    public void testExtractsExchangeArchiveFromTargetRepository() throws IOException {
        Path archive = projectRoot.resolve("target/repository/" + GROUP_ID
                + "/narvar-eapi/1.0.5/narvar-eapi-1.0.5-raml.zip");
        writeZip(archive, Map.of("narvar-eapi.raml", "#%RAML 1.0", "examples/order.json", "{}"));

        Path specFile;
        try (ApiSpecResolver resolver = new ApiSpecResolver(projectRoot)) {
            Resolution resolution = resolver.resolve(NARVAR_REF);
            Assert.assertTrue(resolution instanceof Resolution.Found, resolution.toString());
            specFile = ((Resolution.Found) resolution).specFile();
            Assert.assertEquals(((Resolution.Found) resolution).location(), "target/repository/" + GROUP_ID
                    + "/narvar-eapi/1.0.5/narvar-eapi-1.0.5-raml.zip!/narvar-eapi.raml");
            Assert.assertEquals(specFile.getFileName().toString(), "narvar-eapi.raml");
            Assert.assertTrue(Files.isRegularFile(specFile.resolveSibling("examples/order.json")));
        }
        Assert.assertFalse(Files.exists(specFile), "Extracted files must be deleted on close");
    }

    @Test
    public void testRejectsArchiveEntryOutsideExtractionDir() throws IOException {
        writeZip(targetRepositoryArchive(), Map.of("../escaped.raml", "#%RAML 1.0"));
        Assert.assertTrue(assertNotFound(resolve(NARVAR_REF)).reason().contains("escapes"));
    }

    @Test
    public void testReportsArchiveWithoutSpecFile() throws IOException {
        writeZip(targetRepositoryArchive(), Map.of("other.raml", "#%RAML 1.0"));
        Assert.assertTrue(assertNotFound(resolve(NARVAR_REF)).reason().contains("does not contain"));
    }

    @Test
    public void testReportsMissingExchangeSpec() {
        Resolution.NotFound notFound = assertNotFound(resolve(NARVAR_REF));
        Assert.assertTrue(notFound.reason().contains(GROUP_ID + ":narvar-eapi:1.0.5"), notFound.reason());
        Assert.assertTrue(notFound.reason().endsWith("unzip it into src/main/resources/api/exchange_modules/"
                + GROUP_ID + "/narvar-eapi/1.0.5/ and migrate again"), notFound.reason());
        Assert.assertEquals(notFound.searched().size(), 2);
    }

    @Test
    public void testReportsMalformedExchangeReference() {
        Assert.assertTrue(assertNotFound(resolve("resource::group:asset:1.0.0")).reason().contains("Malformed"));
    }

    private Resolution resolve(String apiRef) {
        try (ApiSpecResolver resolver = new ApiSpecResolver(projectRoot)) {
            return resolver.resolve(apiRef);
        }
    }

    private Path targetRepositoryArchive() {
        return projectRoot.resolve("target/repository/" + GROUP_ID + "/narvar-eapi/1.0.5/narvar-eapi-1.0.5-raml.zip");
    }

    private static void assertFound(Resolution resolution, Path expected) {
        Assert.assertTrue(resolution instanceof Resolution.Found, resolution.toString());
        Assert.assertEquals(((Resolution.Found) resolution).specFile(), expected);
    }

    private static Resolution.NotFound assertNotFound(Resolution resolution) {
        Assert.assertTrue(resolution instanceof Resolution.NotFound, resolution.toString());
        return (Resolution.NotFound) resolution;
    }

    private static Path writeFile(Path path) throws IOException {
        Files.createDirectories(path.getParent());
        return Files.writeString(path, "#%RAML 1.0");
    }

    private static void writeZip(Path archive, Map<String, String> entries) throws IOException {
        Files.createDirectories(archive.getParent());
        try (OutputStream out = Files.newOutputStream(archive); ZipOutputStream zip = new ZipOutputStream(out)) {
            for (Map.Entry<String, String> entry : entries.entrySet()) {
                zip.putNextEntry(new ZipEntry(entry.getKey()));
                zip.write(entry.getValue().getBytes(StandardCharsets.UTF_8));
                zip.closeEntry();
            }
        }
    }

    private static void deleteTree(Path root) throws IOException {
        try (Stream<Path> paths = Files.walk(root)) {
            for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.delete(path);
            }
        }
    }
}
