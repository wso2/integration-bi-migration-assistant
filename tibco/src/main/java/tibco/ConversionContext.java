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
import tibco.converter.ConversionUtils;
import tibco.converter.ProjectConverter.ProjectResources;
import tibco.model.Process;
import tibco.model.Resource;
import tibco.util.PathResolver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public final class ConversionContext implements LoggingContext {

    record ProjectResource(Resource resource, ProjectConversionContext originProject) { }
    record ProjectProcess(Process process, ProjectConversionContext originProject) { }

    private final String org;
    private final boolean dryRun;
    private final boolean keepStructure;
    private final Consumer<String> stateCallback;
    private final Consumer<String> logCallback;
    private final Map<Resource.ResourceIdentifier, ProjectResource> projectResourceMap = new HashMap<>();
    // Keyed by origin project and path so that re-registering a project replaces its entries
    // instead of duplicating them, while distinct projects keep their own copy of a shared process.
    private final Map<String, ProjectProcess> projectProcesses = new LinkedHashMap<>();
    private final Map<Process, Collection<ProcessCodeGenData>> processCodeGenData;

    public ConversionContext(String org, boolean dryRun, boolean keepStructure,
                             Consumer<String> stateCallback, Consumer<String> logCallback) {
        this.org = org;
        this.dryRun = dryRun;
        this.keepStructure = keepStructure;
        this.stateCallback = stateCallback;
        this.logCallback = logCallback;
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
        ProjectResource projectResource = projectResourceMap.get(identifier);
        if (projectResource == null) {
            return Optional.empty();
        }
        // Mark the resource as shared in its origin project
        projectResource.originProject().markResourceAsShared(projectResource.resource());
        return Optional.of(projectResource.resource());
    }

    public void addProjectResources(ProjectResources resources, ProjectConversionContext originProject) {
        resources.stream().forEach(resource -> {
            ProjectResource projectResource = new ProjectResource(resource, originProject);
            Resource.ResourceIdentifier identifier = new Resource.ResourceIdentifier(resource.kind(), resource.path());
            projectResourceMap.put(identifier, projectResource);
        });
    }

    public Optional<Process> lookupProcess(Process.ProcessIdentifier identifier) {
        Optional<ProjectProcess> projectProcess = resolveProjectProcess(identifier.name());
        // Mark the process as shared in its origin project
        projectProcess.ifPresent(each -> each.originProject().markProcessAsShared(each.process()));
        return projectProcess.map(ProjectProcess::process);
    }

    private Optional<ProjectProcess> resolveProjectProcess(String processName) {
        PathResolver.Resolution<ProjectProcess> resolution =
                PathResolver.resolve(projectProcesses.values(), each -> each.process().lookupPaths(), processName);
        if (!resolution.ambiguousCandidates().isEmpty()) {
            log(LoggingUtils.Level.WARN, "Ambiguous process reference '" + processName + "'. Candidates: "
                    + resolution.ambiguousCandidates().stream().map(each -> each.process().path()).toList());
        }
        return resolution.match();
    }

    public void addProjectProcesses(Set<Process> processes, ProjectConversionContext originProject) {
        // Sorted so that resolution does not depend on the iteration order of the incoming set.
        processes.stream()
                .sorted(Comparator.comparing(Process::path))
                .forEach(process -> projectProcesses.put(originProject.name() + '\0' + process.path(),
                        new ProjectProcess(process, originProject)));
    }

    public void registerProcessTextDocument(String projectName, Process process,
                                            BallerinaModel.TextDocument textdocument) {
        processCodeGenData.computeIfAbsent(process, k -> new ArrayList<>())
                .add(new ProcessCodeGenData(projectName, ConversionUtils.lineCount(textdocument.toSource())));
    }

    record ProcessCodeGenData(String projectName, ConversionUtils.LineCount lineCount) {

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
        Map<String, ConversionUtils.LineCount> projectLineCounts = new HashMap<>();
        for (ProcessCodeGenData data : codeGenData) {
            projectLineCounts.merge(data.projectName(), data.lineCount(), ConversionUtils.LineCount::sum);
        }
        return Optional.of(new CombinedSummaryReport.DuplicateProcessData(processName,
                Collections.unmodifiableMap(projectLineCounts)));
    }

    public Optional<LookupResult> processFunction(String processName) {
        return resolveProjectProcess(processName).map(process -> new LookupResult(
                Optional.of(process.originProject().getImport()),
                ConversionUtils.processFunctionName(process.process())));
    }


    @Override
    public String toString() {
        return "ConversionContext[" +
                "org=" + org + ", " +
                "dryRun=" + dryRun + ", " +
                "keepStructure=" + keepStructure + "]";
    }

}
