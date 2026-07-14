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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Shared helpers for comparing a generated Ballerina package against the expected one.
 */
public final class TestUtils {

    private static final boolean UPDATE_EXPECTED = false;

    private TestUtils() {
    }

    /**
     * Assert that the generated package at {@code actual} matches the expected package at
     * {@code expected}, file for file. Every discrepancy — a differing file, an expected file that
     * was not generated, or a generated file with no expected counterpart — is collected and reported
     * together in a single {@link AssertionError}. Expected files are never modified unless
     * {@link #UPDATE_EXPECTED} is set, in which case they are regenerated to match the output.
     */
    public static void compareDirectories(Path actual, Path expected) throws IOException {
        assert actual != null && expected != null : "actual and expected paths must not be null";
        if (UPDATE_EXPECTED) {
            regenerateExpected(actual, expected);
            return;
        }
        List<String> mismatches = new ArrayList<>();
        collectMissingAndDiffering(actual, expected, mismatches);
        collectUnexpected(actual, expected, mismatches);
        if (!mismatches.isEmpty()) {
            throw new AssertionError("Generated package does not match expected at " + expected + ":\n"
                    + String.join("\n", mismatches));
        }
    }

    private static void regenerateExpected(Path actual, Path expected) throws IOException {
        try (Stream<Path> actualFiles = Files.walk(actual)) {
            for (Path relativePath : actualFiles.filter(Files::isRegularFile).map(actual::relativize).toList()) {
                Path expectedFile = expected.resolve(relativePath);
                Files.createDirectories(expectedFile.getParent());
                Files.copy(actual.resolve(relativePath), expectedFile, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        try (Stream<Path> expectedFiles = Files.walk(expected)) {
            for (Path relativePath : expectedFiles.filter(Files::isRegularFile).map(expected::relativize).toList()) {
                if (!Files.exists(actual.resolve(relativePath))) {
                    Files.delete(expected.resolve(relativePath));
                }
            }
        }
    }

    private static void collectMissingAndDiffering(Path actual, Path expected, List<String> mismatches)
            throws IOException {
        try (Stream<Path> expectedFiles = Files.walk(expected)) {
            for (Path relativePath : expectedFiles.filter(Files::isRegularFile).map(expected::relativize).toList()) {
                Path actualFile = actual.resolve(relativePath);
                if (!Files.exists(actualFile)) {
                    mismatches.add("  missing: " + relativePath + " was not generated");
                    continue;
                }
                Optional<String> difference = describeDifference(actualFile, expected.resolve(relativePath));
                if (difference.isPresent()) {
                    mismatches.add("  differs: " + relativePath + " -> " + difference.get());
                }
            }
        }
    }

    private static void collectUnexpected(Path actual, Path expected, List<String> mismatches) throws IOException {
        try (Stream<Path> actualFiles = Files.walk(actual)) {
            for (Path relativePath : actualFiles.filter(Files::isRegularFile).map(actual::relativize).toList()) {
                if (!Files.exists(expected.resolve(relativePath))) {
                    mismatches.add("  unexpected: " + relativePath + " was generated but is not expected");
                }
            }
        }
    }

    private static Optional<String> describeDifference(Path actual, Path expected) throws IOException {
        String actualContent = Files.readString(actual).replace("\r\n", "\n");
        String expectedContent = Files.readString(expected).replace("\r\n", "\n");
        if (actualContent.equals(expectedContent)) {
            return Optional.empty();
        }
        List<String> actualLines = actualContent.lines().toList();
        List<String> expectedLines = expectedContent.lines().toList();
        for (int line = 0; line < Math.max(actualLines.size(), expectedLines.size()); line++) {
            String expectedLine = line < expectedLines.size() ? expectedLines.get(line) : "<no such line>";
            String actualLine = line < actualLines.size() ? actualLines.get(line) : "<no such line>";
            if (!expectedLine.equals(actualLine)) {
                return Optional.of("first differs at line " + (line + 1)
                        + "\n      expected: " + expectedLine + "\n      actual:   " + actualLine);
            }
        }
        return Optional.of("content differs only in trailing whitespace or final newline");
    }

    /**
     * Recursively delete a directory tree. Safe to call on a path that does not exist.
     */
    public static void deleteDirectory(Path dir) throws IOException {
        if (dir == null || !Files.exists(dir)) {
            return;
        }
        try (Stream<Path> paths = Files.walk(dir)) {
            paths.sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to delete " + path, e);
                        }
                    });
        }
    }
}
