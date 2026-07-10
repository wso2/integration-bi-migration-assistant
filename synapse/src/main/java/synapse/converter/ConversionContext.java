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

import common.BallerinaModel.Function;
import common.BallerinaModel.Import;
import common.BallerinaModel.ModuleTypeDef;
import common.BallerinaModel.Service;
import synapse.model.DependencyGraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Project-wide state shared across every artifact and every converter for a single migration run.
 *
 * <p>A single instance is created in {@code SynapseConverter} and threaded through all converters.
 * Scope-local state (the statements, payload and {@code respondInitialized} flag of the resource or
 * sequence currently being converted) does <b>not</b> live here; it lives on a {@link ScopeContext},
 * which holds a reference back to this context.
 *
 * <p>State here falls into two categories:
 * <ul>
 *   <li><b>Per-artifact output</b> ({@link #services()}, {@link #functions()}, {@link #records()})
 *       is accumulated while an artifact is converted, flushed to the generated package, then
 *       discarded via {@link #clearArtifactOutput()} before the next artifact is read.</li>
 *   <li><b>Cross-artifact metadata</b> (e.g. a registry of generated services / sequences for
 *       resolving references between artifacts) must survive {@link #clearArtifactOutput()}, so it
 *       belongs in fields that the clear does not touch. See the extension point below.</li>
 * </ul>
 */
public class ConversionContext {

    public static final String MAIN_BAL_FILE = "main.bal";
    public static final String FUNCTIONS_BAL_FILE = "functions.bal";
    public static final String TYPES_BAL_FILE = "types.bal";

    private final List<Service> services = new ArrayList<>();
    private final List<Function> functions = new ArrayList<>();
    private final List<ModuleTypeDef> records = new ArrayList<>();

    private final Map<String, SequenceMetadata> sequenceMetadata = new HashMap<>();
    private final Map<String, Set<Import>> importsByFile = new HashMap<>();

    private DependencyGraph dependencyGraph;

    public void setDependencyGraph(DependencyGraph dependencyGraph) {
        this.dependencyGraph = dependencyGraph;
    }

    public DependencyGraph dependencyGraph() {
        return dependencyGraph;
    }

    public void addService(Service service) {
        services.add(service);
    }

    public List<Service> services() {
        return services;
    }

    public void addFunction(Function function) {
        functions.add(function);
    }

    public List<Function> functions() {
        return functions;
    }

    public void addRecord(ModuleTypeDef record) {
        records.add(record);
    }

    public List<ModuleTypeDef> records() {
        return records;
    }

    public void addSequenceMetadata(SequenceMetadata metadata) {
        sequenceMetadata.put(metadata.name(), metadata);
    }

    public Optional<SequenceMetadata> sequenceMetadata(String name) {
        return Optional.ofNullable(sequenceMetadata.get(name));
    }

    /**
     * Records the imports needed by a generated {@code .bal} file, accumulated across every artifact
     * flushed into that file. Deduplicated, and preserved across {@link #clearArtifactOutput()} since a
     * later artifact may add imports to a file an earlier one already created.
     */
    public void addImports(String balFile, Collection<Import> imports) {
        importsByFile.computeIfAbsent(balFile, key -> new LinkedHashSet<>()).addAll(imports);
    }

    public Set<Import> importsFor(String balFile) {
        return importsByFile.getOrDefault(balFile, Set.of());
    }

    /**
     * Discards the output accumulated for the artifact just written, leaving cross-artifact metadata
     * intact, so the next artifact starts from a clean output buffer.
     */
    public void clearArtifactOutput() {
        services.clear();
        functions.clear();
        records.clear();
    }

    // Facts about a <sequence>, recorded once it has been converted. Both fall out of the conversion
    // itself: containsRespond is whether a respond was emitted into the sequence's scope, and
    // containsPayloadFactory whether the sequence ended up taking an http:Response parameter to set a
    // payload on. Both are transitive — reaching a called sequence that responds / sets a payload
    // propagates during conversion — so a call site can decide across chains whether to return a
    // response or pass one in. Only mediators actually reached count: a payloadFactory left unreached
    // after a respond, say, is not recorded.
    public record SequenceMetadata(String name, boolean containsRespond, boolean containsPayloadFactory) {
    }
}
