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

package tibco.analyzer;

import common.LoggingUtils;
import tibco.LoggingContext;
import tibco.ProjectConversionContext;
import tibco.converter.ProjectConverter.ProjectResources;
import tibco.model.Process;
import tibco.model.Resource;
import tibco.model.Scope;
import tibco.model.XSD;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class ProjectAnalysisContext implements LoggingContext {

    private final Set<String> controlFlowFunctionNames = new LinkedHashSet<>();
    private final Map<Scope.Flow.Activity, String> activityFunctionNames =
            new ConcurrentHashMap<>();
    private final Map<String, XSD.XSDType> xsdTypes = new ConcurrentHashMap<>();
    private final ProjectConversionContext cx;
    private final ProjectResources projectResources;
    private final ProjectResources capturedResources = new ProjectResources(
            new HashSet<>(),
            new HashSet<>(),
            new HashSet<>(),
            new HashSet<>(),
            new HashSet<>(),
            new HashSet<>(),
            new HashSet<>());
    private final Set<Process> capturedProcesses = new HashSet<>();
    private Set<Process> currentProcesses = new HashSet<>();

    // Queue to hold processes for analysis
    private final Queue<Process> processQueue = new LinkedList<>();

    public ProjectAnalysisContext(ProjectConversionContext cx, ProjectResources projectResources) {
        this.cx = cx;
        this.projectResources = projectResources;
    }

    public void setCurrentProcesses(Set<Process> processes) {
        this.currentProcesses = processes;
    }

    public Set<String> controlFlowFunctionNames() {
        return controlFlowFunctionNames;
    }

    public Map<Scope.Flow.Activity, String> activityFunctionNames() {
        return activityFunctionNames;
    }

    public Map<String, XSD.XSDType> xsdTypes() {
        return xsdTypes;
    }

    void addXsdType(String name, XSD.XSDType type) {
        xsdTypes.put(name, type);
    }

    /**
     * Looks up a resource by its identifier.
     * First checks in the project's own resources, then in the ConversionContext.
     * If found in ConversionContext, captures the resource for this project.
     *
     * @param identifier the resource identifier to look up
     * @return Optional containing the resource if found, empty otherwise
     */
    public Optional<Resource> lookupResource(Resource.ResourceIdentifier identifier) {
        // First, look in project's own resources
        Optional<Resource> localResource = findResourceInProjectResources(identifier);

        if (localResource.isPresent()) {
            return localResource;
        }

        // If not found locally, look in ConversionContext
        Optional<Resource> globalResource = cx.conversionContext().lookupResource(identifier);

        // If found in ConversionContext, capture it for this project
        globalResource.ifPresent(this::captureResource);

        return globalResource;
    }

    /**
     * Searches for a resource in the project's resources by identifier.
     *
     * @param identifier the resource identifier to search for
     * @return Optional containing the resource if found, empty otherwise
     */
    private Optional<Resource> findResourceInProjectResources(Resource.ResourceIdentifier identifier) {
        return Stream.of(projectResources, capturedResources)
                .flatMap(ProjectResources::stream)
                .filter(resource -> resource.matches(identifier))
                .findFirst();
    }

    /**
     * Captures a resource for this project by adding it to the appropriate
     * collection
     * in capturedResources based on its type.
     *
     * @param resource the resource to capture
     */
    private void captureResource(Resource resource) {
        switch (resource) {
            case Resource.JDBCResource jdbcResource ->
                capturedResources.jdbcResources().add(jdbcResource);
            case Resource.HTTPConnectionResource httpConnectionResource ->
                capturedResources.httpConnectionResources().add(httpConnectionResource);
            case Resource.HTTPClientResource httpClientResource ->
                capturedResources.httpClientResources().add(httpClientResource);
            case Resource.HTTPSharedResource httpSharedResource ->
                capturedResources.httpSharedResources().add(httpSharedResource);
            case Resource.JDBCSharedResource jdbcSharedResource ->
                capturedResources.jdbcSharedResource().add(jdbcSharedResource);
            case Resource.JMSSharedResource jmsSharedResource ->
                capturedResources.jmsSharedResource().add(jmsSharedResource);
            case Resource.SharedVariable sharedVariable ->
                capturedResources.sharedVariables().add(sharedVariable);
        }
    }

    @Override
    public void log(LoggingUtils.Level level, String message) {
        cx.log(level, message);
    }

    @Override
    public void logState(String message) {
        cx.logState(message);
    }

    public ProjectResources capturedResources() {
        return capturedResources;
    }

    public Optional<Process> lookupProcess(Process.ProcessIdentifier identifier) {
        // First, look in project's own processes
        Optional<Process> localProcess = findProcessInProject(identifier);

        if (localProcess.isPresent()) {
            return localProcess;
        }

        // If not found locally, look in ConversionContext

        return cx.conversionContext().lookupProcess(identifier);
    }

    private Optional<Process> findProcessInProject(Process.ProcessIdentifier identifier) {
        return Stream.of(currentProcesses, capturedProcesses)
                .flatMap(Set::stream)
                .filter(identifier::matches)
                .findFirst();
    }

    public Set<Process> capturedProcesses() {
        return capturedProcesses;
    }

    /**
     * Adds all processes to the analysis queue.
     *
     * @param processes the collection of processes to add to the queue
     */
    public void addProcessesToQueue(Collection<Process> processes) {
        // Filter out null processes before adding to queue
        processes.stream()
                .filter(Objects::nonNull)
                .forEach(processQueue::add);
    }

    /**
     * Retrieves and removes the next process from the analysis queue.
     *
     * @return the next process to analyze
     * @throws IllegalStateException if the queue is empty or contains null
     *                               processes
     */
    public Process getNextProcess() {
        Process process = processQueue.poll();
        if (process == null) {
            throw new IllegalStateException("No more processes in queue or queue contains null processes");
        }
        return process;
    }

    /**
     * Checks if there are more processes in the queue to analyze.
     *
     * @return true if there are processes remaining in the queue, false otherwise
     */
    public boolean hasMoreProcesses() {
        return !processQueue.isEmpty();
    }

}
