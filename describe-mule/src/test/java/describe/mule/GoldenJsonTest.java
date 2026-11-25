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
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * The description of each repo project must match its reviewed golden file. After an intended change, run the
 * tests with BLESS=true to rewrite the golden files, and review the diff before committing it.
 */
public class GoldenJsonTest {

    private static final Path GOLDEN_DIR = Path.of("src", "test", "resources", "golden");
    private static final boolean BLESS = "true".equalsIgnoreCase(System.getenv("BLESS"));

    @DataProvider(name = "repoProjects")
    public Object[][] repoProjects() throws IOException {
        return TestProjects.repoProjects();
    }

    @Test(dataProvider = "repoProjects")
    public void testJsonMatchesGoldenFile(Path project) throws IOException {
        String json = DescriptionWriter.toJson(MuleDescriber.describe(project));
        Path golden = GOLDEN_DIR.resolve(project.getFileName() + ".json");
        if (BLESS) {
            Files.createDirectories(GOLDEN_DIR);
            Files.writeString(golden, json, StandardCharsets.UTF_8);
        }
        Assert.assertTrue(Files.isRegularFile(golden), "No golden file " + golden + ". Run with BLESS=true.");
        // A Windows checkout may turn the golden file's line ends into \r\n
        Assert.assertEquals(json, Files.readString(golden, StandardCharsets.UTF_8).replace("\r\n", "\n"),
                "The description of " + project + " is not the same as " + golden);
    }

    // Rule 5: a description names properties but never holds their values. These are the names and values in
    // api-project/src/main/resources/config.yaml.
    @Test
    public void testPropertyValuesAreLeftOut() {
        String json = DescriptionWriter.toJson(MuleDescriber.describe(TestProjects.API_PROJECT));
        for (String name : List.of("http.port", "api.id", "inbox.dir", "inbox.poll.frequency")) {
            Assert.assertTrue(json.contains(name), "The description does not name the property " + name);
        }
        for (String value : List.of("8081", "9090123", "/data/inbox-4711", "4242")) {
            Assert.assertFalse(json.contains(value), "The description holds the property value " + value);
        }
    }
}
