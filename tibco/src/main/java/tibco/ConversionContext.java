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

import common.LoggingUtils;
import tibco.converter.ProjectConverter.ProjectResources;
import tibco.model.Process;
import tibco.model.Resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public record ConversionContext(String org, boolean dryRun, boolean keepStructure,
        Consumer<String> stateCallback, Consumer<String> logCallback,
                                List<ProjectResources> projectResources, List<Set<Process>> projectProcesses)
        implements LoggingContext {

    public ConversionContext(String org, boolean dryRun, boolean keepStructure,
            Consumer<String> stateCallback, Consumer<String> logCallback) {
        this(org, dryRun, keepStructure, stateCallback, logCallback, new ArrayList<>(), new ArrayList<>());
    }

    @Override
    public void log(LoggingUtils.Level level, String message) {
        logCallback.accept("[" + level + "] " + message);
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

    public void addProjectResources(Collection<ProjectResources> resources) {
        projectResources.addAll(resources);
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

    public void addProjectProcesses(Collection<Set<Process>> processes) {
        projectProcesses.addAll(processes);
    }
}
