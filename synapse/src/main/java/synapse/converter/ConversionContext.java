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
import common.BallerinaModel.ModuleTypeDef;
import common.BallerinaModel.Service;

import java.util.ArrayList;
import java.util.List;

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

    private final List<Service> services = new ArrayList<>();
    private final List<Function> functions = new ArrayList<>();
    private final List<ModuleTypeDef> records = new ArrayList<>();

    // Extension point: cross-artifact metadata (e.g. a sequence/service registry keyed by name) that
    // must outlive a single artifact goes here, and must NOT be touched by clearArtifactOutput().

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

    /**
     * Discards the output accumulated for the artifact just written, leaving cross-artifact metadata
     * intact, so the next artifact starts from a clean output buffer.
     */
    public void clearArtifactOutput() {
        services.clear();
        functions.clear();
        records.clear();
    }
}
