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
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import static mule.v3.MuleMigrationExecutor.testConvertingMuleProject;
import static mule.v3.MuleMigrationExecutor.testConvertingMultiMuleProjects;
import static mule.v3.MuleToBalConverter.convertStandaloneXMLFileToBallerina;

public class TestConverter {

    private static final PrintStream OUT = System.out;

    @Test(description = "Test converting standalone mule xml file")
    public void convertAndPrintMuleXMLFile() {
        OUT.println("Generating Ballerina code...");
        SyntaxTree syntaxTree = convertStandaloneXMLFileToBallerina("src/test/resources/test_converter.xml");
        OUT.println("________________________________________________________________");
        OUT.println(syntaxTree.toSourceCode());
        OUT.println("________________________________________________________________");
    }

    @Test(description = "Test converting a Mule project with default BI structure")
    public void testMuleProjectConversionWithBiStructure() {
        deleteDirectoryIfExists("src/test/resources/projects/demo_project_bi/demo_project_bi_ballerina");
        testConvertingMuleProject("src/test/resources/projects/demo_project_bi", false, false);
    }

    @Test(description = "Test converting a Mule project with --keep-structure option enabled")
    public void testMuleProjectConversionWithKeepStructure() {
        deleteDirectoryIfExists("src/test/resources/projects/demo_project_classic/demo_project_classic_ballerina");
        testConvertingMuleProject("src/test/resources/projects/demo_project_classic", false, true);
    }

    @Test(description = "Test converting multi Mule projects with --multi-root option enabled")
    public void testMultiMuleProjectsConversion() {
        deleteDirectoryIfExists("src/test/resources/misc/multi_root_output");
        testConvertingMultiMuleProjects("src/test/resources/projects", "src/test/resources/misc/multi_root_output",
                false, false);
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
}
