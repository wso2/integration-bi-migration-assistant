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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Shared helpers for comparing a generated Ballerina package against the expected one.
 */
public final class TestUtils {

    private TestUtils() {
    }

    /**
     * Compare every regular file under {@code expected} with the file at the same relative path
     * under {@code actual}. When the contents differ, the expected (Ballerina) file is overwritten
     * with the generated output. Newly generated files with no counterpart under {@code expected}
     * are created as well.
     */
    public static void compareDirectories(Path actual, Path expected) throws IOException {
        try (Stream<Path> expectedFiles = Files.walk(expected)) {
            List<Path> expectedPaths = expectedFiles
                    .filter(Files::isRegularFile)
                    .map(expected::relativize)
                    .toList();

            for (Path relativePath : expectedPaths) {
                Path actualFile = actual.resolve(relativePath);
                if (Files.exists(actualFile)) {
                    compareFiles(actualFile, expected.resolve(relativePath));
                }
            }
        }
        copyNewFiles(actual, expected);
    }

    /**
     * Compare the textual contents of two files, normalizing line endings. On mismatch the expected
     * file is overwritten with the actual (generated) contents.
     */
    public static void compareFiles(Path actual, Path expected) throws IOException {
        String actualContent = Files.readString(actual).replace("\r\n", "\n");
        String expectedContent = Files.readString(expected).replace("\r\n", "\n");
        if (actualContent.equals(expectedContent)) {
            return;
        }
        Files.createDirectories(expected.getParent());
        Files.copy(actual, expected, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("Updated expected file: " + expected);
    }

    /**
     * Copy every regular file under {@code actual} that has no counterpart at the same relative path
     * under {@code expected} into {@code expected}, capturing newly generated files.
     */
    public static void copyNewFiles(Path actual, Path expected) throws IOException {
        try (Stream<Path> actualFiles = Files.walk(actual)) {
            List<Path> relativePaths = actualFiles
                    .filter(Files::isRegularFile)
                    .map(actual::relativize)
                    .toList();

            for (Path relativePath : relativePaths) {
                Path expectedFile = expected.resolve(relativePath);
                if (!Files.exists(expectedFile)) {
                    Files.createDirectories(expectedFile.getParent());
                    Files.copy(actual.resolve(relativePath), expectedFile, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("Created expected file: " + expectedFile);
                }
            }
        }
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
