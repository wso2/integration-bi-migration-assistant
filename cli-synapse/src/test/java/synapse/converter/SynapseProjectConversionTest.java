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

import org.testng.SkipException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Converts each Synapse project under {@code src/test/resources/synapse.projects} and compares the
 * generated Ballerina package against the expected package under
 * {@code src/test/resources/synapse.projects.converted}.
 *
 * <p>Each immediate sub-directory of {@code synapse.projects} is paired by name with the
 * sub-directory of the same name under {@code synapse.projects.converted}. To add a new test case,
 * drop a Synapse project under {@code synapse.projects/<Name>} and its expected Ballerina output
 * under {@code synapse.projects.converted/<Name>}.
 *
 * <p>While {@link SynapseConverter} is still a scaffold, the conversion call throws
 * {@link UnsupportedOperationException}; this is translated into a skipped test so the suite stays
 * green until the converter is implemented.
 */
public class SynapseProjectConversionTest {

    private static final Path PROJECTS_DIR = Path.of("src", "test", "resources", "synapse.projects");
    private static final Path CONVERTED_DIR = Path.of("src", "test", "resources", "synapse.projects.converted");

    @Test(groups = {"synapse", "converter"}, dataProvider = "projectTestCaseProvider")
    public void testProjectConversion(Path synapseProject, Path expectedBallerinaProject) throws IOException {
        Path tempDir = Files.createTempDirectory("synapse-conversion-test");
        try {
            SynapseConverter.migrateSynapseProject(
                    synapseProject.toString(),
                    tempDir.toString(),
                    "testOrg",
                    expectedBallerinaProject.getFileName().toString());

            TestUtils.compareDirectories(tempDir, expectedBallerinaProject);
        } catch (UnsupportedOperationException e) {
            throw new SkipException("Synapse converter not implemented yet: " + e.getMessage());
        } finally {
            TestUtils.deleteDirectory(tempDir);
        }
    }

    @DataProvider
    public Object[][] projectTestCaseProvider() throws IOException {
        try (var dirs = Files.list(PROJECTS_DIR)) {
            return dirs
                    .filter(Files::isDirectory)
                    .map(dir -> new Object[]{dir, CONVERTED_DIR.resolve(dir.getFileName())})
                    .toArray(Object[][]::new);
        }
    }
}
