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
package synapse.converter.bir.mediators.classmediator.source;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

/**
 * Locates the Java source for a class mediator across the shapes it realistically arrives in, in a
 * fixed preference order (highest fidelity first):
 *
 * <ol>
 *   <li><b>Source roots</b> — a loose {@code pkg/Name.java} under a configured directory.
 *   <li><b>Sources jars</b> — a {@code pkg/Name.java} entry inside a configured jar (e.g. a
 *       {@code *-sources.jar} shipped alongside the mediator).</li>
 *   <li><b>Bytecode jars</b> — a {@code pkg/Name.class} entry, decompiled via the configured
 *       {@link Decompiler}.
 * </ol>
 *
 * <p>When none of these yield source (including when no decompiler is configured), {@link #resolve}
 * returns empty and the caller falls back to an empty stub. {@link JavaSource#origin()} on a hit
 * tells the caller whether the source is genuine or decompiled.
 */
public final class JavaSourceResolver {

    private final List<Path> sourceRoots;
    private final List<Path> archives;
    private final Decompiler decompiler;

    /** Source-only resolver: searches loose {@code .java} under {@code sourceRoots}, no jars. */
    public JavaSourceResolver(List<Path> sourceRoots) {
        this(sourceRoots, List.of(), Decompiler.NONE);
    }

    /**
     * @param sourceRoots directories searched for loose {@code .java} files
     * @param archives    jar files searched for {@code .java} then {@code .class} entries
     * @param decompiler  used to reconstruct source from {@code .class} entries; pass
     *                    {@link Decompiler#NONE} to skip bytecode-only jars
     */
    public JavaSourceResolver(List<Path> sourceRoots, List<Path> archives, Decompiler decompiler) {
        this.sourceRoots = List.copyOf(sourceRoots);
        this.archives = List.copyOf(archives);
        this.decompiler = decompiler;
    }

    // A resolvable name is dot-separated Java identifiers; anything else is rejected.
    private static final Pattern BINARY_CLASS_NAME =
            Pattern.compile("[\\p{L}_$][\\p{L}\\p{N}_$]*(\\.[\\p{L}_$][\\p{L}\\p{N}_$]*)*");

    /** Resolves {@code className} to its Java source, or empty if it cannot be found anywhere. */
    @NotNull
    public Optional<JavaSource> resolve(String className) {
        if (className == null || !BINARY_CLASS_NAME.matcher(className).matches()) {
            return Optional.empty();
        }
        String javaEntry = className.replace('.', '/') + ".java";
        String classEntry = className.replace('.', '/') + ".class";

        return fromSourceRoots(className, javaEntry)
                .or(() -> fromSourcesJars(className, javaEntry))
                .or(() -> fromBytecodeJars(className, classEntry));
    }

    @NotNull
    private Optional<JavaSource> fromSourceRoots(String className, String javaEntry) {
        for (Path root : sourceRoots) {
            Path candidate = root.resolve(javaEntry).normalize();
            if (!candidate.startsWith(root.normalize())) {
                continue;
            }
            if (Files.isRegularFile(candidate)) {
                try {
                    return Optional.of(new JavaSource(
                            className, Files.readString(candidate), JavaSource.Origin.SOURCE_FILE));
                } catch (IOException e) {
                    // Unreadable here; keep looking under the remaining roots.
                }
            }
        }
        return Optional.empty();
    }

    @NotNull
    private Optional<JavaSource> fromSourcesJars(String className, String javaEntry) {
        for (Path archive : archives) {
            Optional<String> text = readEntry(archive, javaEntry)
                    .map(bytes -> new String(bytes, StandardCharsets.UTF_8));
            if (text.isPresent()) {
                return text.map(t -> new JavaSource(className, t, JavaSource.Origin.SOURCES_JAR));
            }
        }
        return Optional.empty();
    }

    @NotNull
    private Optional<JavaSource> fromBytecodeJars(String className, String classEntry) {
        for (Path archive : archives) {
            Optional<byte[]> classBytes = readEntry(archive, classEntry);
            if (classBytes.isEmpty()) {
                continue;
            }
            Optional<JavaSource> decompiled = decompiler.decompile(className, classBytes.get())
                    .map(src -> new JavaSource(className, src, JavaSource.Origin.DECOMPILED));
            if (decompiled.isPresent()) {
                return decompiled;
            }
        }
        return Optional.empty();
    }

    @NotNull
    private static Optional<byte[]> readEntry(Path archive, String entryName) {
        if (!Files.isRegularFile(archive)) {
            return Optional.empty();
        }
        try (JarFile jar = new JarFile(archive.toFile())) {
            JarEntry entry = jar.getJarEntry(entryName);
            if (entry == null) {
                return Optional.empty();
            }
            try (var in = jar.getInputStream(entry)) {
                return Optional.of(in.readAllBytes());
            }
        } catch (IOException e) {
            return Optional.empty();
        }
    }
}
