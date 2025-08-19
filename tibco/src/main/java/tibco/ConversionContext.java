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

package tibco;

import common.BallerinaModel;
import common.LoggingUtils;
import tibco.analyzer.CombinedSummaryReport;
import tibco.converter.ProjectConverter.ProjectResources;
import tibco.model.Process;
import tibco.model.Resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public final class ConversionContext implements LoggingContext {

    private final String org;
    private final boolean dryRun;
    private final boolean keepStructure;
    private final Consumer<String> stateCallback;
    private final Consumer<String> logCallback;
    private final List<ProjectResources> projectResources;
    private final List<Set<Process>> projectProcesses;
    private final Map<Process, Collection<ProcessCodeGenData>> processCodeGenData;

    public ConversionContext(String org, boolean dryRun, boolean keepStructure,
                             Consumer<String> stateCallback, Consumer<String> logCallback) {
        this.org = org;
        this.dryRun = dryRun;
        this.keepStructure = keepStructure;
        this.stateCallback = stateCallback;
        this.logCallback = logCallback;
        this.projectResources = new ArrayList<>();
        this.projectProcesses = new ArrayList<>();
        processCodeGenData = new IdentityHashMap<>();
    }

    @Override
    public void log(LoggingUtils.Level level, String message) {
        logCallback.accept("[" + level + "] " + message);
    }

    public String org() {
        return org;
    }

    public boolean dryRun() {
        return dryRun;
    }

    public boolean keepStructure() {
        return keepStructure;
    }

    @Override
    public void logState(String message) {
        stateCallback.accept(message);
    }

    public Optional<Resource> lookupResource(Resource.ResourceIdentifier identifier) {
        return projectResources.stream()
                .flatMap(ProjectResources::stream)
                .filter(resource -> resource.matches(identifier))
                .findFirst();
    }

    public void addProjectResources(ProjectResources resources) {
        projectResources.add(resources);
    }

    public Optional<Process> lookupProcess(Process.ProcessIdentifier identifier) {
        return projectProcesses.stream()
                .flatMap(Set::stream)
                .filter(identifier::matches)
                .findFirst();
    }

    public void addProjectProcesses(Set<Process> processes) {
        projectProcesses.add(processes);
    }

    public void registerProcessTextDocument(String projectName, Process process,
                                            BallerinaModel.TextDocument textdocument) {
        processCodeGenData.computeIfAbsent(process, k -> new ArrayList<>())
                .add(new ProcessCodeGenData(projectName, textdocument.getLineCount()));
    }

    record ProcessCodeGenData(String projectName, long lineCount) {

    }

    public Collection<CombinedSummaryReport.DuplicateProcessData> getDuplicateProcessData() {
        List<CombinedSummaryReport.DuplicateProcessData> duplicates = new ArrayList<>();
        for (Map.Entry<Process, Collection<ProcessCodeGenData>> entry : processCodeGenData.entrySet()) {
            getDuplicateProcessDataInner(entry).ifPresent(duplicates::add);
        }
        return Collections.unmodifiableCollection(duplicates);
    }

    private static Optional<CombinedSummaryReport.DuplicateProcessData> getDuplicateProcessDataInner(
            Map.Entry<Process, Collection<ProcessCodeGenData>> entry) {
        Process process = entry.getKey();
        Collection<ProcessCodeGenData> codeGenData = entry.getValue();
        if (codeGenData == null || codeGenData.isEmpty()) {
            return Optional.empty();
        }
        String processName = process.name();
        Map<String, Long> projectLineCounts = new HashMap<>();
        for (ProcessCodeGenData data : codeGenData) {
            projectLineCounts.merge(data.projectName(), data.lineCount(), Long::sum);
        }
        return Optional.of(new CombinedSummaryReport.DuplicateProcessData(processName,
                Collections.unmodifiableMap(projectLineCounts)));
    }

    @Override
    public String toString() {
        return "ConversionContext[" +
                "org=" + org + ", " +
                "dryRun=" + dryRun + ", " +
                "keepStructure=" + keepStructure + "]";
    }

}
