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

import mule.MigratorUtils;
import mule.MuleMigrator.MuleVersion;
import mule.common.MuleLogger;
import mule.common.ProjectMigrationResult;
import mule.v4.Context;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Builds the {@link Context} for the describe pass, the same way {@code MuleMigrator.createProjectContext} does for a
 * Mule 4 project. The describe pass cannot reach the normal run's Context, so it runs its own (D2).
 */
final class MuleProjectLoader {

    private MuleProjectLoader() {
    }

    record LoadedProject(Path projectDir, Context context, List<File> xmlFiles, List<Path> testFiles) {
    }

    static LoadedProject load(Path projectDir) {
        MuleVersion version = MigratorUtils.detectVersionForProject(projectDir);
        if (version != MuleVersion.MULE_V4) {
            throw new IllegalArgumentException("--describe supports Mule 4 projects only: " + projectDir);
        }
        Path muleDir = projectDir.resolve("src").resolve("main").resolve("mule");
        List<File> xmlFiles = new ArrayList<>();
        MigratorUtils.collectXmlFiles(muleDir.toFile(), xmlFiles);
        List<File> yamlFiles = new ArrayList<>();
        List<File> propertyFiles = new ArrayList<>();
        MigratorUtils.collectYamlAndPropertyFiles(projectDir.resolve("src").resolve("main").resolve("resources")
                .toFile(), yamlFiles, propertyFiles);

        // MUnit files are only listed, never read, so test elements are not described (D10)
        List<File> munitFiles = new ArrayList<>();
        Path munitDir = projectDir.resolve("src").resolve("test").resolve("munit");
        if (Files.isDirectory(munitDir)) {
            MigratorUtils.collectXmlFiles(munitDir.toFile(), munitFiles);
        }

        String sourceName = projectDir.getFileName().toString();
        ProjectMigrationResult result = new ProjectMigrationResult();
        result.setSourceName(sourceName);
        result.setOrgName(MigratorUtils.getBalOrgName(null));
        result.setProjectName(MigratorUtils.getBalProjectName(null, sourceName));
        // Silent: the normal conversion already logs problems, and the JSON lists the files that failed
        MuleLogger logger = new MuleLogger(state -> { }, log -> { });
        Context context = new Context(xmlFiles, yamlFiles, muleDir, version, propertyFiles, sourceName, true, false,
                false, logger, result, null, List.of());
        return new LoadedProject(projectDir, context, List.copyOf(xmlFiles),
                munitFiles.stream().map(File::toPath).toList());
    }
}
