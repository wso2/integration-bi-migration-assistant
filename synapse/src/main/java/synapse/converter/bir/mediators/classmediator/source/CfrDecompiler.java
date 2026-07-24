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

import org.benf.cfr.reader.api.CfrDriver;
import org.benf.cfr.reader.api.OutputSinkFactory;
import org.benf.cfr.reader.api.SinkReturns;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * A {@link Decompiler} backed by CFR ({@code org.benf:cfr}). Reconstructs Java source from a
 * {@code .class} file so a bytecode-only class mediator can still be surfaced to the developer.
 *
 * <p>CFR reads from the filesystem, so the bytes are written to a temp file laid out by package
 * before analysis; the decompiled output is captured through an in-memory sink. Any failure yields
 * {@link Optional#empty()} so resolution falls back to an empty stub.
 */
public final class CfrDecompiler implements Decompiler {

    @Override
    public Optional<String> decompile(String className, byte[] classBytes) {
        Path tempDir = null;
        try {
            tempDir = Files.createTempDirectory("cfr-");
            Path classFile = tempDir.resolve(className.replace('.', '/') + ".class");
            Files.createDirectories(classFile.getParent());
            Files.write(classFile, classBytes);

            StringBuilder decompiled = new StringBuilder();
            OutputSinkFactory sink = new OutputSinkFactory() {
                @Override
                public List<SinkClass> getSupportedSinks(SinkType sinkType, Collection<SinkClass> available) {
                    return sinkType == SinkType.JAVA && available.contains(SinkClass.DECOMPILED)
                            ? List.of(SinkClass.DECOMPILED)
                            : List.of(SinkClass.STRING);
                }

                @Override
                @SuppressWarnings("unchecked")
                public <T> Sink<T> getSink(SinkType sinkType, SinkClass sinkClass) {
                    if (sinkType == SinkType.JAVA && sinkClass == SinkClass.DECOMPILED) {
                        return (Sink<T>) (Sink<SinkReturns.Decompiled>)
                                d -> decompiled.append(d.getJava());
                    }
                    return ignored -> { };
                }
            };

            new CfrDriver.Builder().withOutputSink(sink).build()
                    .analyse(List.of(classFile.toString()));

            String java = decompiled.toString().strip();
            return java.isEmpty() ? Optional.empty() : Optional.of(java);
        } catch (IOException | RuntimeException e) {
            return Optional.empty();
        } finally {
            deleteQuietly(tempDir);
        }
    }

    private static void deleteQuietly(Path dir) {
        if (dir == null) {
            return;
        }
        try (var walk = Files.walk(dir)) {
            walk.sorted((a, b) -> b.getNameCount() - a.getNameCount())
                    .forEach(p -> p.toFile().delete());
        } catch (IOException ignored) {
            // Best-effort cleanup of a temp directory.
        }
    }
}
