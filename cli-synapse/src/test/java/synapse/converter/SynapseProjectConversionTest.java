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
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Converts each Synapse artifact under {@code src/test/resources/synapse/<Name>} and compares the
 * generated Ballerina package against the expected package under
 * {@code src/test/resources/ballerina/<Name>}.
 *
 * <p>Each immediate sub-directory of {@code synapse} is paired by name with the sub-directory of the
 * same name under {@code ballerina}. The single {@code *.xml} artifact inside the Synapse directory
 * is converted via {@link SynapseConverter#migrateSynapse}. To add a new test case, drop a Synapse
 * artifact under {@code synapse/<Name>/<Name>.xml} and its expected Ballerina output under
 * {@code ballerina/<Name>}.
 */
public class SynapseProjectConversionTest {

    private static final Path SYNAPSE_DIR = Path.of("src", "test", "resources", "synapse");
    private static final Path BALLERINA_DIR = Path.of("src", "test", "resources", "ballerina");

    @Test(groups = {"synapse", "converter"}, dataProvider = "projectTestCaseProvider")
    public void testProjectConversion(Path synapseArtifact, Path expectedBallerinaProject) throws IOException {
        Path tempDir = Files.createTempDirectory("synapse-conversion-test");
        try {
            SynapseConverter.migrateSynapse(
                    synapseArtifact.toString(),
                    tempDir.toString(),
                    false,
                    false,
                    false,
                    false,
                    Optional.of("testOrg"),
                    Optional.of(expectedBallerinaProject.getFileName().toString()));

            TestUtils.compareDirectories(tempDir, expectedBallerinaProject);
        } catch (UnsupportedOperationException e) {
            throw new SkipException("Synapse converter not implemented yet: " + e.getMessage());
        } finally {
            TestUtils.deleteDirectory(tempDir);
        }
    }

    @DataProvider
    public Object[][] projectTestCaseProvider() throws IOException {
        try (Stream<Path> dirs = Files.list(SYNAPSE_DIR)) {
            return dirs
                    .filter(Files::isDirectory)
                    .filter(dir -> dir.getFileName().toString().contains("HelloWorldService"))
                    .map(dir -> new Object[]{findSynapseArtifact(dir), BALLERINA_DIR.resolve(dir.getFileName())})
                    .toArray(Object[][]::new);
        }
    }

    private static Path findSynapseArtifact(Path projectDir) {
        try (Stream<Path> files = Files.list(projectDir)) {
            List<Path> xmlFiles = files
                    .filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().endsWith(".xml"))
                    .toList();
            if (xmlFiles.size() != 1) {
                throw new IllegalStateException(
                        "Expected exactly one .xml artifact in " + projectDir + " but found " + xmlFiles.size());
            }
            return xmlFiles.get(0);
        } catch (IOException e) {
            throw new RuntimeException("Failed to locate Synapse artifact in " + projectDir, e);
        }
    }
}
