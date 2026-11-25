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

import org.testng.Assert;
import org.testng.SkipException;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * The Mule 4 projects the describe tests run on. Gradle runs the tests from the describe-mule folder.
 */
final class TestProjects {

    static final String PROJECTS_DIR_PROPERTY = "describe.projects.dir";
    static final Path MULE_V4_TEST_RESOURCES = Path.of("..", "mule", "src", "test", "resources", "mule", "v4");
    static final Path ATTRIBUTION_PROJECT = Path.of("src", "test", "resources", "attribution-project");
    static final Path API_PROJECT = Path.of("src", "test", "resources", "api-project");
    static final Path API_MISSING_SPEC_PROJECT = Path.of("src", "test", "resources", "api-missing-spec-project");

    private TestProjects() {
    }

    // The fixture projects of this module and the Mule 4 test projects of the mule module
    static Object[][] repoProjects() throws IOException {
        List<Path> projects = new ArrayList<>();
        projects.add(ATTRIBUTION_PROJECT);
        projects.add(API_PROJECT);
        projects.add(API_MISSING_SPEC_PROJECT);
        projects.addAll(muleProjectsIn(MULE_V4_TEST_RESOURCES.resolve("projects")));
        Object[][] rows = new Object[projects.size()][];
        for (int i = 0; i < projects.size(); i++) {
            rows[i] = new Object[]{projects.get(i)};
        }
        return rows;
    }

    // Internal projects must not be copied into the repo, so these tests read them from a folder given with
    // -Ddescribe.projects.dir, and skip when it is not given
    static List<Path> outsideProjects() throws IOException {
        String dir = System.getProperty(PROJECTS_DIR_PROPERTY, "");
        if (dir.isBlank()) {
            throw new SkipException("Set -D" + PROJECTS_DIR_PROPERTY + "=<folder> to check the projects in it");
        }
        List<Path> projects = muleProjectsIn(Path.of(dir));
        Assert.assertFalse(projects.isEmpty(), "No Mule 4 project in " + dir);
        return projects;
    }

    // A Mule 4 project has a mule-artifact.json file
    private static List<Path> muleProjectsIn(Path dir) throws IOException {
        List<Path> projects = new ArrayList<>();
        try (DirectoryStream<Path> children = Files.newDirectoryStream(dir)) {
            for (Path child : children) {
                if (Files.isRegularFile(child.resolve("mule-artifact.json"))) {
                    projects.add(child);
                }
            }
        }
        projects.sort(Comparator.naturalOrder());
        return projects;
    }
}
