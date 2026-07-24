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
import synapse.converter.bir.mediators.classmediator.source.Decompiler;
import synapse.converter.bir.mediators.classmediator.source.JavaSource;
import synapse.converter.bir.mediators.classmediator.source.JavaSourceResolver;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Offline tests for the three source-location tiers. Temp source dirs and jars are built per test,
 * so nothing depends on a real project layout, a decompiler, or the network.
 */
public class JavaSourceResolverTest {

    private static final String CLASS_NAME = "samples.mediators.SimpleStockQuoteMediator";
    private static final String JAVA_ENTRY = "samples/mediators/SimpleStockQuoteMediator.java";
    private static final String CLASS_ENTRY = "samples/mediators/SimpleStockQuoteMediator.class";
    private static final String SAMPLE_SOURCE = """
            package samples.mediators;
            public class SimpleStockQuoteMediator {
                public boolean mediate(Object mc) { return true; }
            }
            """;

    // Tier 1: loose .java under a source root
    @Test
    public void resolvesFromSourceRoot() throws IOException {
        Path root = Files.createTempDirectory("src-root");
        Path file = root.resolve(JAVA_ENTRY);
        Files.createDirectories(file.getParent());
        Files.writeString(file, SAMPLE_SOURCE);

        Optional<JavaSource> resolved = new JavaSourceResolver(List.of(root)).resolve(CLASS_NAME);

        assertTrue(resolved.isPresent());
        assertEquals(resolved.get().origin(), JavaSource.Origin.SOURCE_FILE);
        assertTrue(resolved.get().source().contains("mediate"));
    }

    // Tier 2: .java entry inside a jar
    @Test
    public void resolvesFromSourcesJar() throws IOException {
        Path jar = jarWith(JAVA_ENTRY, SAMPLE_SOURCE.getBytes(StandardCharsets.UTF_8));

        Optional<JavaSource> resolved =
                new JavaSourceResolver(List.of(), List.of(jar), Decompiler.NONE).resolve(CLASS_NAME);

        assertTrue(resolved.isPresent());
        assertEquals(resolved.get().origin(), JavaSource.Origin.SOURCES_JAR);
        assertTrue(resolved.get().source().contains("SimpleStockQuoteMediator"));
    }

    // Tier 3: .class entry decompiled
    @Test
    public void resolvesFromBytecodeJarViaDecompiler() throws IOException {
        Path jar = jarWith(CLASS_ENTRY, new byte[] {(byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE});
        // Stand-in decompiler so the test needs no real bytecode or CFR on the classpath.
        Decompiler fake = (name, bytes) -> Optional.of("// decompiled " + name);

        Optional<JavaSource> resolved =
                new JavaSourceResolver(List.of(), List.of(jar), fake).resolve(CLASS_NAME);

        assertTrue(resolved.isPresent());
        assertEquals(resolved.get().origin(), JavaSource.Origin.DECOMPILED);
        assertTrue(resolved.get().source().contains("decompiled"));
    }

    @Test
    public void bytecodeJarSkippedWithoutDecompiler() throws IOException {
        Path jar = jarWith(CLASS_ENTRY, new byte[] {1, 2, 3});
        Optional<JavaSource> resolved =
                new JavaSourceResolver(List.of(), List.of(jar), Decompiler.NONE).resolve(CLASS_NAME);
        assertFalse(resolved.isPresent());
    }

    // Ordering + misses
    @Test
    public void prefersSourceRootOverJar() throws IOException {
        Path root = Files.createTempDirectory("src-root");
        Path file = root.resolve(JAVA_ENTRY);
        Files.createDirectories(file.getParent());
        Files.writeString(file, SAMPLE_SOURCE);
        Path jar = jarWith(JAVA_ENTRY, "// from jar".getBytes(StandardCharsets.UTF_8));

        Optional<JavaSource> resolved =
                new JavaSourceResolver(List.of(root), List.of(jar), Decompiler.NONE).resolve(CLASS_NAME);

        assertTrue(resolved.isPresent());
        assertEquals(resolved.get().origin(), JavaSource.Origin.SOURCE_FILE);
    }

    @Test
    public void returnsEmptyWhenNowhere() throws IOException {
        Path emptyRoot = Files.createTempDirectory("src-root");
        JavaSourceResolver resolver = new JavaSourceResolver(List.of(emptyRoot));
        assertFalse(resolver.resolve(CLASS_NAME).isPresent());
        assertFalse(resolver.resolve("").isPresent());
        assertFalse(resolver.resolve(null).isPresent());
    }

    @Test
    public void resolveMediatorFromResources() {
        Path root = Path.of("src/test/resources/java");
        Optional<JavaSource> resolved =
                new JavaSourceResolver(List.of(root)).resolve("GreetMediator");

        assertTrue(resolved.isPresent(), "GreetMediator.java should be found under the resource root");
        assertEquals(resolved.get().origin(), JavaSource.Origin.SOURCE_FILE);
        assertTrue(resolved.get().source().contains("mediate"));

        System.out.println("=== resolved source ===");
        System.out.println(resolved.get().source());
    }

    /** Writes a single-entry jar to a temp file and returns its path. */
    private static Path jarWith(String entryName, byte[] content) throws IOException {
        Path jar = Files.createTempFile("mediators-", ".jar");
        try (OutputStream out = Files.newOutputStream(jar);
             JarOutputStream jarOut = new JarOutputStream(out)) {
            jarOut.putNextEntry(new JarEntry(entryName));
            jarOut.write(content);
            jarOut.closeEntry();
        }
        return jar;
    }
}
