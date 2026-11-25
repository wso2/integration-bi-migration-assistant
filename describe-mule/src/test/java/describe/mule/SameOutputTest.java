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

import mule.MuleMigrator;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

/**
 * Rule 1: --describe must not change the conversion output. Each repo project is converted with and without the
 * describe pass, as the CLI does it, and every written file outside the description folder must be the same.
 */
public class SameOutputTest {

    @DataProvider(name = "repoProjects")
    public Object[][] repoProjects() throws IOException {
        return TestProjects.repoProjects();
    }

    @Test(dataProvider = "repoProjects")
    public void testDescribeDoesNotChangeConversionOutput(Path project) throws IOException {
        Path withDescribe = Files.createTempDirectory("with-describe");
        Path withoutDescribe = Files.createTempDirectory("without-describe");
        try {
            // The run with the describe pass goes first, so the run without it also shows that the describe pass
            // leaves nothing behind in the JVM that changes a later conversion
            convert(project, withDescribe);
            try (PrintStream out = new PrintStream(OutputStream.nullOutputStream(), true, StandardCharsets.UTF_8)) {
                DescribeCommand.run(project.toString(), withDescribe.toString(), false, false, out);
            }
            convert(project, withoutDescribe);

            Assert.assertTrue(Files.isRegularFile(descriptionFolder(withDescribe, project)
                    .resolve(MuleDescriber.DESCRIPTION_FILE)), "No description was written for " + project);
            Map<String, String> withoutFiles = conversionFiles(withoutDescribe, project);
            Assert.assertFalse(withoutFiles.isEmpty(), "The conversion wrote nothing for " + project);
            Assert.assertEquals(conversionFiles(withDescribe, project), withoutFiles,
                    "--describe changed the conversion output of " + project);
        } finally {
            deleteTree(withDescribe);
            deleteTree(withoutDescribe);
        }
    }

    private static void convert(Path project, Path outputDir) {
        MuleMigrator.migrateAndExportMuleSource(project.toString(), outputDir.toString(), null, null, null, false,
                false, false, false, false);
    }

    // Relative path to content, for every file the conversion wrote below outputDir
    private static Map<String, String> conversionFiles(Path outputDir, Path project) throws IOException {
        Path descriptionFolder = descriptionFolder(outputDir, project);
        Map<String, String> files = new TreeMap<>();
        List<Path> paths;
        try (Stream<Path> walk = Files.walk(outputDir)) {
            paths = walk.filter(Files::isRegularFile).filter(path -> !path.startsWith(descriptionFolder)).toList();
        }
        for (Path path : paths) {
            files.put(outputDir.relativize(path).toString(), Files.readString(path, StandardCharsets.UTF_8));
        }
        return files;
    }

    private static Path descriptionFolder(Path outputDir, Path project) {
        return outputDir.resolve(project.getFileName() + DescribeCommand.FOLDER_SUFFIX);
    }

    private static void deleteTree(Path dir) throws IOException {
        try (Stream<Path> paths = Files.walk(dir)) {
            for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.delete(path);
            }
        }
    }
}
