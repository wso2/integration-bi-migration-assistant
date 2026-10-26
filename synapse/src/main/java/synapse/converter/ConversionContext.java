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
import common.BallerinaModel.Listener;
import common.BallerinaModel.ModuleTypeDef;
import common.BallerinaModel.ModuleVar;
import common.BallerinaModel.Service;
import common.BallerinaModel.Statement;
import org.jetbrains.annotations.NotNull;
import synapse.converter.bir.mediators.classmediator.source.JavaSourceResolver;
import synapse.model.DependencyGraph;
import synapse.model.Synapse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
    private final List<Listener> listeners = new ArrayList<>();
    private final List<ModuleVar> moduleVars = new ArrayList<>();
    private final List<Statement> moduleInitStatements = new ArrayList<>();

    private final Map<String, SequenceMetadata> sequenceMetadata = new HashMap<>();
    private final Map<String, Set<Import>> importsByFile = new HashMap<>();
    private final Map<String, PropertyInfo> properties = new LinkedHashMap<>();
    private final Map<String, Function> converterFunctions = new LinkedHashMap<>();
    private final Map<String, Function> classMediatorStubs = new LinkedHashMap<>();
    private final Set<String> generatedFunctionNames = new LinkedHashSet<>();
    private final List<UnsupportedEntry> unsupported = new ArrayList<>();

    private DependencyGraph dependencyGraph;
    private JavaSourceResolver javaSourceResolver = new JavaSourceResolver(List.of());
    private String currentFile = "";
    private boolean sharedListenerDeclared;

    public void setDependencyGraph(DependencyGraph dependencyGraph) {
        this.dependencyGraph = dependencyGraph;
    }

    public DependencyGraph dependencyGraph() {
        return dependencyGraph;
    }

    /** The resolver used to locate a class mediator's original Java source; empty by default. */
    @NotNull
    public JavaSourceResolver javaSourceResolver() {
        return javaSourceResolver;
    }

    public void setJavaSourceResolver(JavaSourceResolver javaSourceResolver) {
        assert javaSourceResolver != null : "javaSourceResolver must not be null";
        this.javaSourceResolver = javaSourceResolver;
    }
    
    /**
     * The source artifact file currently being converted, relative to the migration source root. Set by
     * {@code SynapseConverter} before each artifact so converters can attribute a to-do to its origin.
     */
    public void setCurrentFile(String currentFile) {
        this.currentFile = currentFile;
    }

    @NotNull
    public String currentFile() {
        return currentFile;
    }

    /**
     * Whether the shared HTTP listener every {@code <api>} service binds to has already been declared in
     * {@code main.bal}. Set once, the first time an {@code <api>} artifact is converted, so later rounds
     * (including inbound-endpoint-only ones) don't redeclare it; survives {@link #clearArtifactOutput()}
     * since the shared listener, once written, must never be written again.
     */
    public boolean isSharedListenerDeclared() {
        return sharedListenerDeclared;
    }

    public void setSharedListenerDeclared(boolean sharedListenerDeclared) {
        this.sharedListenerDeclared = sharedListenerDeclared;
    }

    /**
     * Records an unsupported Synapse construct so it can be surfaced in the migration report. Populated
     * as artifacts are converted and preserved across {@link #clearArtifactOutput()}, since the report
     * is written once at the end of the whole run.
     */
    public void reportUnsupported(UnsupportedEntry entry) {
        unsupported.add(entry);
    }

    @NotNull
    public List<UnsupportedEntry> unsupported() {
        return unsupported;
    }

    public void addService(Service service) {
        services.add(service);
    }

    public List<Service> services() {
        return services;
    }

    /**
     * Registers a listener dedicated to the artifact currently being converted (e.g. the transport-level
     * entry point an {@code <inboundEndpoint>} opens), as opposed to the single shared HTTP listener
     * every {@code <api>} service binds to. Per-artifact output, like {@link #services()}: cleared by
     * {@link #clearArtifactOutput()} once flushed.
     */
    public void addListener(Listener listener) {
        assert listener != null : "listener must not be null";
        listeners.add(listener);
    }

    @NotNull
    public List<Listener> listeners() {
        return listeners;
    }

    /**
     * Registers a {@code configurable} module-level variable backing a listener setting that is inherently
     * tied to the machine the config was authored on (a connection URL, credential, or filesystem path) so
     * it can be overridden per deployment via {@code Config.toml} instead of requiring a source edit. Per-
     * artifact output, like {@link #listeners()}: cleared by {@link #clearArtifactOutput()} once flushed.
     */
    public void addModuleVar(ModuleVar moduleVar) {
        assert moduleVar != null : "moduleVar must not be null";
        moduleVars.add(moduleVar);
    }

    @NotNull
    public List<ModuleVar> moduleVars() {
        return moduleVars;
    }

    public void addFunction(Function function) {
        functions.add(function);
        generatedFunctionNames.add(function.functionName());
    }

    public List<Function> functions() {
        return functions;
    }

    /**
     * Registers statements to append to the module's combined {@code init()} function — for a
     * converter that needs a one-time startup action (e.g. a file inbound endpoint's initial
     * directory scan for pre-existing files) that no per-artifact function alone can express.
     * Cross-artifact, like {@link #converterFunctions()}: preserved across
     * {@link #clearArtifactOutput()} and assembled into a single generated {@code init()} function
     * once, at the very end of the run.
     */
    public void addModuleInitStatements(List<Statement> statements) {
        moduleInitStatements.addAll(statements);
    }

    @NotNull
    public List<Statement> moduleInitStatements() {
        return moduleInitStatements;
    }

    /**
     * Registers a type-conversion helper function (e.g. {@code convertToInt}, {@code stringToInt}) that a
     * property conversion needs, keyed by its name so the same converter requested by many properties is
     * emitted once. Populated while artifacts are converted and preserved across
     * {@link #clearArtifactOutput()}, since the converters are flushed into {@code functions.bal} in a
     * single final pass alongside {@code respond} / {@code emitPayload}.
     */
    public void addConverterFunction(Function function) {
        converterFunctions.putIfAbsent(function.functionName(), function);
        generatedFunctionNames.add(function.functionName());
    }

    public Collection<Function> converterFunctions() {
        return converterFunctions.values();
    }

    /**
     * Registers the stub function generated for a class mediator, keyed by class name so every
     * occurrence reuses one stub. Preserved across {@link #clearArtifactOutput()} and flushed
     * into {@link #functions()} once.
     */
    public void addClassMediatorStub(String className, Function function) {
        classMediatorStubs.putIfAbsent(className, function);
        generatedFunctionNames.add(function.functionName());
    }

    @NotNull
    public Optional<Function> classMediatorStub(String className) {
        return Optional.ofNullable(classMediatorStubs.get(className));
    }

    /**
     * Reserves {@code name} so no class mediator stub can claim it — used for the fixed
     * {@code respond}/{@code emitPayload} functions, whose names must be off-limits from the start of the
     * run, before {@link #addFunction} is called for them at the very end.
     */
    public void reserveFunctionName(String name) {
        generatedFunctionNames.add(name);
    }

    /**
     * Whether {@code functionName} is already claimed by any generated function — reserved, a sequence or
     * other function added via {@link #addFunction}, a converter helper, or another class's stub. Every
     * one of those registers its name here as it's created, so this check catches a collision regardless
     * of which kind of function claimed the name first.
     */
    public boolean isClassMediatorStubNameTaken(String functionName) {
        return generatedFunctionNames.contains(functionName);
    }

    @NotNull
    public Collection<Function> classMediatorStubs() {
        return classMediatorStubs.values();
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
     * Registers a Synapse property so it becomes a field of the generated {@code Context} record. The
     * types seen for a name are accumulated and its scope retained; the {@code Context} record
     * aggregates every property across the whole project, so this map is populated as artifacts are
     * converted (leaf-first) and preserved across {@link #clearArtifactOutput()}. A name declared with
     * more than one type across the project therefore records every type — the field becomes a union of
     * them — while the scope of the first registration is kept.
     */
    public void addProperty(String name, String type, String scope) {
        PropertyInfo existing = properties.get(name);
        if (existing == null) {
            Set<String> types = new LinkedHashSet<>();
            types.add(type);
            properties.put(name, new PropertyInfo(types, scope));
        } else {
            existing.types().add(type);
        }
    }

    @NotNull
    public Map<String, PropertyInfo> properties() {
        return properties;
    }

    // ERROR_MESSAGE is populated by Synapse itself on entry to fault handling, not by a <property>
    // mediator reached during conversion, so it is always available regardless of artifact conversion
    // order.
    private static final Set<String> WELL_KNOWN_DEFAULT_SCOPE_PROPERTIES = Set.of(Synapse.ERROR_MESSAGE_PROPERTY);

    /**
     * Names of the default-scope properties a {@code get-property(...)} expression may resolve against:
     * every property registered via {@link #addProperty} so far, plus the well-known properties Synapse
     * itself populates regardless of conversion order.
     */
    @NotNull
    public Set<String> availableDefaultScopeProperties() {
        Set<String> available = new LinkedHashSet<>(properties.keySet());
        available.addAll(WELL_KNOWN_DEFAULT_SCOPE_PROPERTIES);
        return available;
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
        listeners.clear();
        moduleVars.clear();
    }

    // Facts about a <sequence>, recorded once it has been converted, all falling out of the conversion
    // itself: containsRespond is whether a respond was emitted into the sequence's scope, and usesContext
    // whether it ended up taking a Context ctx parameter (to set default properties / a payload on, or to
    // carry the http:Caller a respond needs). Both are transitive — reaching a called sequence that
    // responds / sets a property propagates during conversion — so a call site can decide across chains
    // whether to check the call and pass ctx. Only mediators actually reached count: a mediator left
    // unreached after a respond, say, is not recorded.
    public record SequenceMetadata(String name, boolean containsRespond, boolean usesContext) {
    }

    // Types and scope of a Synapse property, retained per property name so the generated Context record
    // can declare a field of the right type. More than one type appears when the same name is declared
    // with different types across the project, and the field becomes a union of them. Scope is kept for
    // future non-default scopes even though only default-scope properties currently become Context
    // fields.
    public record PropertyInfo(Set<String> types, String scope) {
    }

    // A single unsupported Synapse construct surfaced in the migration report. category groups the case
    // (e.g. "Unsupported mediator", "Unsupported artifact", "Unsupported property"), tag is the Synapse
    // element name, file is the source artifact (relative to the source root), detail is a human-readable
    // reason, and rawXml is the original Synapse code.
    public record UnsupportedEntry(String category, String tag, String file, String detail, String rawXml) {
    }
}
