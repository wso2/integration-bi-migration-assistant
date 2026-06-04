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

import org.testng.Assert;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
     * under {@code actual}. Fails if a file is missing or its contents differ.
     */
    public static void compareDirectories(Path actual, Path expected) throws IOException {
        try (Stream<Path> expectedFiles = Files.walk(expected)) {
            List<Path> expectedPaths = expectedFiles
                    .filter(Files::isRegularFile)
                    .map(expected::relativize)
                    .toList();

            for (Path relativePath : expectedPaths) {
                Path actualFile = actual.resolve(relativePath);
                Assert.assertTrue(Files.exists(actualFile),
                        "Missing expected file in generated output: " + relativePath);
                compareFiles(actualFile, expected.resolve(relativePath));
            }
        }
    }

    /**
     * Compare the textual contents of two files, normalizing line endings.
     */
    public static void compareFiles(Path actual, Path expected) throws IOException {
        String actualContent = Files.readString(actual).replace("\r\n", "\n");
        String expectedContent = Files.readString(expected).replace("\r\n", "\n");
        Assert.assertEquals(actualContent, expectedContent,
                "File contents do not match for: " + expected.getFileName());
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
