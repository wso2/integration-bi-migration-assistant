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

import mule.common.report.ProjectMigrationStats;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static mule.MuleMigrator.MuleVersion;

public final class ProjectMigrationResult implements MigrationResult {
    private String sourceName;
    private MuleVersion muleVersion;
    private ProjectMigrationStats migrationStats;
    private Optional<String> fatalError = Optional.empty();

    // Bal project specific fields
    private String orgName;
    private String projectName;
    private Map<String, String> files;
    private String htmlReport;
    private Map<String, Object> jsonReport;
    private Path targetPath;
    private Set<String> configurableVariableNames = new HashSet<>();

    @Override
    public String toString() {
        return "ProjectMigrationResult[" +
                "sourceProjectName=" + sourceName + ", " +
                "muleVersion=" + muleVersion + ", " +
                "projectName=" + projectName + ']';
    }

    @Override
    public Optional<String> getFatalError() {
        return fatalError;
    }

    @Override
    public void setFatalError(String fatalError) {
        this.fatalError = Optional.ofNullable(fatalError);
    }

    public Path getTargetPath() {
        return targetPath;
    }

    public void setTargetPath(Path targetPath) {
        this.targetPath = targetPath;
    }

    public MuleVersion getMuleVersion() {
        return muleVersion;
    }

    public void setMuleVersion(MuleVersion muleVersion) {
        this.muleVersion = muleVersion;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public ProjectMigrationStats getMigrationStats() {
        return migrationStats;
    }

    public void setMigrationStats(ProjectMigrationStats migrationStats) {
        this.migrationStats = migrationStats;
    }

    public Map<String, String> getFiles() {
        return files;
    }

    public void setFiles(Map<String, String> files) {
        this.files = files;
    }

    public String getHtmlReport() {
        return htmlReport;
    }

    public void setHtmlReport(String htmlReport) {
        this.htmlReport = htmlReport;
    }

    public Map<String, Object> getJsonReport() {
        return jsonReport;
    }

    public void setJsonReport(Map<String, Object> jsonReport) {
        this.jsonReport = jsonReport;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public Set<String> getConfigurableVariableNames() {
        return configurableVariableNames;
    }

    public void setConfigurableVariableNames(Set<String> configurableVariableNames) {
        this.configurableVariableNames = configurableVariableNames;
    }
}
