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

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Locates the spec file named by the {@code api} attribute of an {@code apikit:config}. Exchange archives found
 * locally are extracted to temporary directories, which {@link #close()} deletes.
 */
public final class ApiSpecResolver implements AutoCloseable {

    private static final String EXCHANGE_PREFIX = "resource::";
    private static final String RESOURCES_PREFIX = "resources:";

    private final Path projectRoot;
    private final Path resourcesDir;
    private final Path targetRepositoryDir;
    private final List<Path> extractedDirs = new ArrayList<>();

    /**
     * @param projectRoot root of the Mule project
     */
    public ApiSpecResolver(Path projectRoot) {
        assert projectRoot != null;
        this.projectRoot = projectRoot;
        this.resourcesDir = projectRoot.resolve("src").resolve("main").resolve("resources");
        this.targetRepositoryDir = projectRoot.resolve("target").resolve("repository");
    }

    public @NotNull Resolution resolve(String apiRef) {
        assert apiRef != null;
        String ref = apiRef.trim();
        if (ref.contains("${")) {
            return new Resolution.NotFound("Spec reference uses a property placeholder: " + ref, List.of());
        }
        if (ref.startsWith(EXCHANGE_PREFIX)) {
            return resolveExchange(ref);
        }
        return resolveLocal(ref.startsWith(RESOURCES_PREFIX) ? ref.substring(RESOURCES_PREFIX.length()) : ref);
    }

    private Resolution resolveLocal(String ref) {
        String relativePath = ref.replace('\\', '/').replaceFirst("^/+", "");
        List<Path> candidates = List.of(resourcesDir.resolve(relativePath),
                resourcesDir.resolve("api").resolve(relativePath));
        return firstFile(candidates)
                .<Resolution>map(this::found)
                .orElseGet(() -> new Resolution.NotFound("Spec file not found: " + relativePath, candidates));
    }

    private Resolution resolveExchange(String ref) {
        String[] parts = ref.substring(EXCHANGE_PREFIX.length()).split(":", 6);
        if (parts.length != 6 || Stream.of(parts).anyMatch(String::isBlank)) {
            return new Resolution.NotFound("Malformed Exchange reference, expected " + EXCHANGE_PREFIX
                    + "groupId:assetId:version:classifier:packaging:file but found: " + ref, List.of());
        }
        ExchangeAsset asset = new ExchangeAsset(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]);

        Path moduleDir = asset.moduleDir(resourcesDir.resolve("api").resolve("exchange_modules"));
        Path extractedSpec = moduleDir.resolve(asset.file());
        if (Files.isRegularFile(extractedSpec)) {
            return found(extractedSpec);
        }
        Path archive = asset.mavenDir(targetRepositoryDir).resolve(asset.archiveName());
        List<Path> searched = List.of(extractedSpec, archive);
        if (Files.isRegularFile(archive)) {
            return resolveInArchive(archive, asset.file(), searched);
        }
        return new Resolution.NotFound(("Exchange spec %s was not found in the project. Download it from Exchange, "
                + "unzip it into %s/ and migrate again").formatted(asset.coordinates(), location(moduleDir)), searched);
    }

    private Resolution resolveInArchive(Path archive, String file, List<Path> searched) {
        Path extractedDir;
        try {
            extractedDir = extract(archive);
        } catch (IOException e) {
            return new Resolution.NotFound("Could not extract " + archive + ": " + e.getMessage(), searched);
        }
        Path specFile = extractedDir.resolve(file).normalize();
        if (!specFile.startsWith(extractedDir) || !Files.isRegularFile(specFile)) {
            return new Resolution.NotFound("Archive " + archive + " does not contain " + file, searched);
        }
        return new Resolution.Found(specFile, location(archive) + "!/" + file);
    }

    private Path extract(Path archive) throws IOException {
        Path targetDir = Files.createTempDirectory("mule-apispec-");
        extractedDirs.add(targetDir);
        try (ZipFile zip = new ZipFile(archive.toFile())) {
            for (ZipEntry entry : zip.stream().toList()) {
                Path target = targetDir.resolve(entry.getName()).normalize();
                if (!target.startsWith(targetDir)) {
                    throw new IOException("Archive entry escapes the extraction directory: " + entry.getName());
                }
                if (entry.isDirectory()) {
                    Files.createDirectories(target);
                    continue;
                }
                Files.createDirectories(target.getParent());
                try (InputStream in = zip.getInputStream(entry)) {
                    Files.copy(in, target);
                }
            }
        }
        return targetDir;
    }

    private Resolution.Found found(Path specFile) {
        return new Resolution.Found(specFile, location(specFile));
    }

    private String location(Path path) {
        return (path.startsWith(projectRoot) ? projectRoot.relativize(path) : path).toString()
                .replace(File.separatorChar, '/');
    }

    private static Optional<Path> firstFile(List<Path> candidates) {
        return candidates.stream().filter(Files::isRegularFile).findFirst();
    }

    @Override
    public void close() {
        for (Path dir : extractedDirs) {
            try (Stream<Path> paths = Files.walk(dir)) {
                paths.sorted(Comparator.reverseOrder()).forEach(path -> path.toFile().delete());
            } catch (IOException e) {
                // Best effort: a leftover temporary directory must not fail the migration.
            }
        }
        extractedDirs.clear();
    }

    private record ExchangeAsset(String groupId, String assetId, String version, String classifier,
                                 String packaging, String file) {

        String coordinates() {
            return groupId + ":" + assetId + ":" + version;
        }

        String archiveName() {
            return assetId + "-" + version + "-" + classifier + "." + packaging;
        }

        Path moduleDir(Path root) {
            return root.resolve(groupId).resolve(assetId).resolve(version);
        }

        Path mavenDir(Path repositoryRoot) {
            return repositoryRoot.resolve(groupId.replace('.', '/')).resolve(assetId).resolve(version);
        }
    }

    public sealed interface Resolution {

        /**
         * @param specFile root file of the spec
         * @param location where the spec was found, for the migration report: a path relative to the project
         *                 root when inside it, and {@code archive!/file} for a spec taken from an Exchange archive
         */
        record Found(Path specFile, String location) implements Resolution {

            public Found {
                assert specFile != null && location != null;
            }
        }

        /**
         * @param reason   why the spec could not be resolved, for the migration report
         * @param searched locations that were checked, in search order
         */
        record NotFound(String reason, List<Path> searched) implements Resolution {

            public NotFound {
                assert reason != null && searched != null;
                searched = List.copyOf(searched);
            }
        }
    }
}
