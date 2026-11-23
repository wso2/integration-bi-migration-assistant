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
package describe.mule;

import describe.json.DescriptionWriter;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Where --describe writes, and what it does for the inputs it does not support yet.
 */
public class DescribeCommandTest {

    private Path outputDir;
    private Path mule3Project;

    @BeforeMethod
    public void createFolders() throws IOException {
        outputDir = Files.createTempDirectory("describe-out");
        mule3Project = Files.createTempDirectory("mule3-project");
        // A mule-project.xml file and no mule-artifact.json makes the migrator detect Mule 3
        Files.writeString(mule3Project.resolve("mule-project.xml"), "<mule-project/>", StandardCharsets.UTF_8);
    }

    @AfterMethod(alwaysRun = true)
    public void deleteFolders() throws IOException {
        deleteTree(outputDir);
        deleteTree(mule3Project);
    }

    @Test
    public void testWritesDescriptionInOutputFolder() throws IOException {
        String printed = run(TestProjects.ATTRIBUTION_PROJECT, false, false);

        Path file = outputDir.resolve("attribution-project" + DescribeCommand.FOLDER_SUFFIX)
                .resolve(MuleDescriber.DESCRIPTION_FILE);
        Assert.assertTrue(Files.isRegularFile(file), "No description at " + file + ". Printed: " + printed);
        Assert.assertEquals(Files.readString(file, StandardCharsets.UTF_8),
                DescriptionWriter.toJson(MuleDescriber.describe(TestProjects.ATTRIBUTION_PROJECT)));
        Assert.assertTrue(printed.contains("Description written to " + file), printed);
    }

    @Test
    public void testDryRunWritesNothing() throws IOException {
        String printed = run(TestProjects.ATTRIBUTION_PROJECT, true, false);

        Assert.assertEquals(filesIn(outputDir), List.of());
        Assert.assertTrue(printed.contains("Description of attribution-project: 2 flow(s)"), printed);
        Assert.assertTrue(printed.contains("Dry run: no description was written."), printed);
    }

    @Test
    public void testMultiRootIsNotSupported() throws IOException {
        checkNotSupported(TestProjects.ATTRIBUTION_PROJECT, true, "--multi-root");
    }

    @Test
    public void testXmlFileIsNotSupported() throws IOException {
        checkNotSupported(TestProjects.ATTRIBUTION_PROJECT.resolve("src/main/mule/xref-translate.xml"), false,
                "a single .xml file");
    }

    @Test
    public void testMule3IsNotSupported() throws IOException {
        checkNotSupported(mule3Project, false, "Mule 3 projects");
    }

    private void checkNotSupported(Path input, boolean multiRoot, String inputKind) throws IOException {
        String printed = run(input, false, multiRoot);

        Assert.assertEquals(filesIn(outputDir), List.of());
        Assert.assertEquals(printed.strip(),
                "--describe is not supported for " + inputKind + " yet. No description was written.");
    }

    private String run(Path input, boolean dryRun, boolean multiRoot) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (PrintStream out = new PrintStream(bytes, true, StandardCharsets.UTF_8)) {
            DescribeCommand.run(input.toString(), outputDir.toString(), dryRun, multiRoot, out);
        }
        return bytes.toString(StandardCharsets.UTF_8);
    }

    private static List<Path> filesIn(Path dir) throws IOException {
        try (Stream<Path> paths = Files.walk(dir)) {
            return paths.filter(path -> !path.equals(dir)).toList();
        }
    }

    private static void deleteTree(Path dir) throws IOException {
        try (Stream<Path> paths = Files.walk(dir)) {
            for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.delete(path);
            }
        }
    }
}
