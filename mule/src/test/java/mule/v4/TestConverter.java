/*
 *  Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com).
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
package mule.v4;

import io.ballerina.compiler.syntax.tree.SyntaxTree;
import mule.common.MuleLogger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.stream.Stream;

import static mule.MuleMigrator.migrateAndExportMuleSource;
import static mule.MuleMigrator.testConvertingMuleProject;
import static mule.MuleMigrator.testConvertingMultiMuleProjects;
import static mule.v4.MuleToBalConverter.convertStandaloneXMLFileToBallerina;

public class TestConverter {

    private static final PrintStream OUT = System.out;

    @Test(description = "Test converting standalone mule xml file")
    public void convertAndPrintMuleXMLFile() {
        OUT.println("Generating Ballerina code...");
        SyntaxTree syntaxTree = convertStandaloneXMLFileToBallerina("src/test/resources/mule/v4/test_converter.xml",
                new MuleLogger(false));
        OUT.println("________________________________________________________________");
        OUT.println(syntaxTree.toSourceCode());
        OUT.println("________________________________________________________________");
    }

    @Test(enabled = false, description = "Test converting a Mule project with default BI structure")
    public void testMuleProjectConversionWithBiStructure() {
        deleteDirectoryIfExists("src/test/resources/mule/v4/projects/demo_project_bi/demo_project_bi_ballerina");
        testConvertingMuleProject(4, "src/test/resources/mule/v4/projects/demo_project_bi", false, false);
    }

    @Test(enabled = false, description = "Test converting a Mule project with --keep-structure option enabled")
    public void testMuleProjectConversionWithKeepStructure() {
        deleteDirectoryIfExists(
                "src/test/resources/mule/v4/projects/demo_project_classic/demo_project_classic_ballerina");
        testConvertingMuleProject(4, "src/test/resources/mule/v4/projects/demo_project_classic", false, true);
    }

    @Test(enabled = false, description = "Test converting multi Mule projects with --multi-root option enabled")
    public void testMultiMuleProjectsConversion() {
        deleteDirectoryIfExists("src/test/resources/mule/v4/misc/multi_root_output");
        testConvertingMultiMuleProjects(4, "src/test/resources/mule/v4/projects",
                "src/test/resources/mule/v4/misc/multi_root_output", false, false);
    }

    @Test(description = "Test parsing import elements in sharedResources multi-root project")
    public void testSharedResourcesImportParsing() {
        deleteDirectoryIfExists("src/test/resources/mule/v4/sharedResources_output");
        testConvertingMultiMuleProjects(4, "src/test/resources/mule/v4/sharedResources",
                "src/test/resources/mule/v4/sharedResources_output", false, false);
    }

    @Test(description = "Test converting apiKitProject Mule project")
    public void testApiKitProjectConversion() throws IOException {
        String projectPath = "src/test/resources/mule/v4/projects/apiKitProject";
        Path expectedDir = Path.of(projectPath, "apiKitProject_ballerina");

        // Create a temporary directory for the output
        Path tempDir = Files.createTempDirectory("apiKitProject-conversion-test");
        boolean bless = "true".equalsIgnoreCase(System.getenv("BLESS"));
        try {
            migrateAndExportMuleSource(projectPath, tempDir.toString(), null, null, 4,
                    false, false, false, false);

            Path actualDir = locateGeneratedProjectDir(tempDir, expectedDir.getFileName().toString());
            if (bless) {
                replaceExpectedWithActual(actualDir, expectedDir);
            }
            compareDirectories(actualDir, expectedDir);
        } finally {
            deleteDirectory(tempDir);
        }
    }

    private void deleteDirectoryIfExists(String first) {
        Path balProjectDir = Path.of(first);
        if (Files.exists(balProjectDir)) {
            try {
                deleteDirectory(balProjectDir);
            } catch (IOException e) {
                throw new RuntimeException("Issue deleting directory: " + balProjectDir, e);
            }
        }
    }

    private void deleteDirectory(Path path) throws IOException {
        Files.walkFileTree(path, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private void compareDirectories(Path actual, Path expected) throws IOException {
        // First check if both directories exist
        Assert.assertTrue(Files.isDirectory(actual), "Actual path is not a directory: " + actual);
        Assert.assertTrue(Files.isDirectory(expected), "Expected path is not a directory: " + expected);

        // Compare directory contents
        try (Stream<Path> expectedFiles = Files.walk(expected);
                Stream<Path> actualFiles = Files.walk(actual)) {

            // Get relative paths for comparison, filtering relevant files
            var expectedPaths = expectedFiles
                    .filter(Files::isRegularFile)
                    .filter(path -> {
                        String fileName = path.getFileName().toString();
                        return fileName.endsWith(".bal") || fileName.endsWith(".toml") ||
                                fileName.equals("Config.toml");
                    })
                    .map(expected::relativize)
                    .toList();

            var actualPaths = actualFiles
                    .filter(Files::isRegularFile)
                    .filter(path -> {
                        String fileName = path.getFileName().toString();
                        return fileName.endsWith(".bal") || fileName.endsWith(".toml") ||
                                fileName.equals("Config.toml");
                    })
                    .map(actual::relativize)
                    .toList();

            // Check if all expected files exist
            for (Path relativePath : expectedPaths) {
                if (relativePath.endsWith("types.bal") || relativePath.endsWith("migration_report.html")) {
                    // Skip types.bal and migration_report.html as they may differ
                    continue;
                }
                Assert.assertTrue(actualPaths.contains(relativePath),
                        "Missing file in actual directory: " + relativePath);

                // Compare file contents
                Path expectedFile = expected.resolve(relativePath);
                Path actualFile = actual.resolve(relativePath);
                compareFiles(actualFile, expectedFile);
            }

            // Check for extra files
            for (Path relativePath : actualPaths) {
                if (relativePath.endsWith("types.bal") || relativePath.endsWith("migration_report.html")) {
                    // Skip types.bal and migration_report.html as they may differ
                    continue;
                }
                Assert.assertTrue(expectedPaths.contains(relativePath),
                        "Extra file in actual directory: " + relativePath);
            }
        }
    }

    private void compareFiles(Path actual, Path expected) throws IOException {
        if (actual.toString().contains("types") || actual.toString().contains(".html")) {
            // These are generated and may change from run to run
            return;
        }
        String actualContent = Files.readString(actual);
        String expectedContent = Files.readString(expected);
        Assert.assertEquals(actualContent, expectedContent,
                "File contents do not match for: " + actual.getFileName());
    }

    private Path locateGeneratedProjectDir(Path outputDir, String expectedName) throws IOException {
        Path candidate = outputDir.resolve(expectedName);
        if (Files.isDirectory(candidate)) {
            return candidate;
        }

        try (Stream<Path> children = Files.list(outputDir)) {
            return children.filter(Files::isDirectory)
                    .findFirst()
                    .orElseThrow(() -> new IOException("No generated project directory found in " + outputDir));
        }
    }

    private void replaceExpectedWithActual(Path actualDir, Path expectedDir) throws IOException {
        deleteDirectoryIfExists(expectedDir.toString());
        Files.walk(actualDir)
                .forEach(source -> {
                    Path target = expectedDir.resolve(actualDir.relativize(source));
                    try {
                        if (Files.isDirectory(source)) {
                            if (!Files.exists(target)) {
                                Files.createDirectories(target);
                            }
                        } else {
                            Path parent = target.getParent();
                            if (parent != null && !Files.exists(parent)) {
                                Files.createDirectories(parent);
                            }
                            Files.copy(source, target);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to copy " + source + " to " + target, e);
                    }
                });
    }
}
