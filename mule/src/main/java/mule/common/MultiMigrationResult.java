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
package mule.common;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class MultiMigrationResult implements MigrationResult {
    private List<ProjectMigrationResult> migrationResults;
    private String htmlReport;
    private Path targetPath;
    private Optional<String> fatalError = Optional.empty();

    @Override
    public Optional<String> getFatalError() {
        return fatalError;
    }

    @Override
    public void setFatalError(String fatalError) {
        this.fatalError = Optional.ofNullable(fatalError);
    }

    public List<ProjectMigrationResult> getMigrationResults() {
        return migrationResults;
    }

    public void setMigrationResults(List<ProjectMigrationResult> migrationResults) {
        this.migrationResults = migrationResults;
    }

    public Path getTargetPath() {
        return targetPath;
    }

    public void setTargetPath(Path targetPath) {
        this.targetPath = targetPath;
    }

    public String getHtmlReport() {
        return htmlReport;
    }

    public void setHtmlReport(String htmlReport) {
        this.htmlReport = htmlReport;
    }
}
