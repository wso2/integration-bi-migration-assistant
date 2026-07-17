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
import common.BallerinaModel.Listener.HTTPListener;
import common.BallerinaModel.ModuleTypeDef;
import common.BallerinaModel.Service;
import common.BallerinaModel.TextDocument;
import common.BallerinaModel.TypeDesc;
import common.BallerinaModel.TypeDesc.BallerinaType;
import common.BallerinaModel.TypeDesc.RecordTypeDesc;
import common.BallerinaModel.TypeDesc.RecordTypeDesc.RecordField;
import common.BallerinaModel.TypeDesc.UnionTypeDesc;
import org.jetbrains.annotations.NotNull;
import synapse.converter.ConversionContext.PropertyInfo;
import synapse.converter.bir.APIConverter;
import synapse.converter.bir.BIRConverter;
import synapse.converter.bir.SequenceConverter;
import synapse.model.DependencyGraph;
import synapse.model.DependencyGraph.ArtifactNode;
import synapse.model.DependencyResolver;
import synapse.model.Synapse.Kind;
import synapse.model.Synapse.SynapseNode;
import synapse.reader.SynapseConfigReader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Entry point for converting WSO2 Synapse (ESB / Micro Integrator) artifacts to Ballerina.
 *
 * <p>This is the scaffold for the Synapse migration assistant. The parsing, analysis and
 * code-generation phases are still to be implemented; the public surface mirrors the
 * TIBCO converter so that the CLI ({@code cli.SynapseCli}) and the bal tool command
 * ({@code baltool.synapse.commands.MigrateSynapseCommand}) can be wired against a stable API.
 *
 * @since 1.0.0
 */
public final class SynapseConverter {

    private static final Map<Kind, BIRConverter<ConversionContext>> ROOT_CONVERTERS = Map.of(
            Kind.API, new APIConverter(),
            Kind.SEQUENCE, new SequenceConverter());

    private static final String LISTENER_NAME = "httpListener";
    private static final String DEFAULT_PORT = "8080";
    private static final String DEFAULT_HOST = "0.0.0.0";
    private static final String DEFAULT_ORG = "wso2";
    private static final String DEFAULT_PACKAGE = "synapse";
    private static final String CONTEXT_TYPE = "Context";
    private static final String VARIABLES_TYPE = "Variables";
    private static final String VARIABLES_FIELD = "variables";
    private static final String DEFAULT_SCOPE = "default";
    private static final String SYNAPSE_SCOPE = "synapse";

    private static final Logger LOG = Logger.getLogger(SynapseConverter.class.getName());

    private SynapseConverter() {
    }

    /**
     * Migrate a Synapse project directory or a single artifact file to a Ballerina package.
     *
     * <p>Artifacts are processed one at a time in dependency order (leaves first), as given by the
     * {@link DependencyGraph}: each artifact is parsed, converted and flushed to the generated
     * Ballerina package before the next one, so the whole project is never held in memory at once.
     * Converting leaves first means a sequence's dependencies are already converted when it is
     * reached, so its {@link ConversionContext.SequenceMetadata} (whether it responds or sets a
     * payload, transitively) is generated during conversion rather than in a separate pass. The
     * generated constructs are consolidated by kind across all artifacts: services (with the shared
     * HTTP listener) go to {@code main.bal}, functions to {@code functions.bal} and record types to
     * {@code types.bal}.
     *
     * @param sourcePath    Synapse project directory or artifact file path
     * @param outputPath    output directory for the generated Ballerina package (nullable -> default)
     * @param keepStructure preserve the original artifact structure instead of the standard BI layout
     * @param verbose       enable verbose logging during conversion
     * @param dryRun        run parsing/analysis and emit the report only, without generating sources
     * @param multiRoot     treat each child directory of {@code sourcePath} as a separate project
     * @param orgName       organization name for the generated Ballerina package
     * @param projectName   project name for the generated Ballerina package
     */
    public static void migrateSynapse(String sourcePath, String outputPath, boolean keepStructure, boolean verbose,
                                      boolean dryRun, boolean multiRoot, Optional<String> orgName,
                                      Optional<String> projectName) {

        if (keepStructure) {
            throw new UnsupportedOperationException("The 'keepStructure' option is not supported yet.");
        }
        if (verbose) {
            throw new UnsupportedOperationException("The 'verbose' option is not supported yet.");
        }
        if (multiRoot) {
            throw new UnsupportedOperationException("The 'multiRoot' option is not supported yet.");
        }

        List<File> artifactFiles = SynapseConfigReader.collectArtifactFiles(sourcePath);
        if (artifactFiles.isEmpty()) {
            throw new RuntimeException("No Synapse .xml artifacts found at: " + sourcePath);
        }

        DependencyGraph dependencyGraph = DependencyGraph.buildDependencyGraph(artifactFiles);
        logDependencyWarnings(dependencyGraph);

        ConversionContext context = new ConversionContext();
        context.setDependencyGraph(dependencyGraph);

        if (dryRun) {
            for (ArtifactNode artifactNode : dependencyGraph.sortedNodes()) {
                convertArtifact(artifactNode, context);
                context.clearArtifactOutput();
            }
            return;
        }

        String targetPath = outputPath != null ? outputPath : stripExtension(sourcePath) + "_converted";
        try {
            Path targetDir = Paths.get(targetPath);
            Files.createDirectories(targetDir);
            Files.writeString(targetDir.resolve("Ballerina.toml"),
                    ballerinaToml(orgName.orElse(DEFAULT_ORG), projectName.orElse(DEFAULT_PACKAGE)));

            Map<Path, Set<Import>> writtenImports = new HashMap<>();
            for (ArtifactNode artifactNode : dependencyGraph.sortedNodes()) {
                convertArtifact(artifactNode, context);
                writeArtifacts(targetDir, context, writtenImports);
                context.clearArtifactOutput();
            }
            addContextRecord(context);
            if (dependencyGraph.sortedNodes().isEmpty() || !context.records().isEmpty()) {
                // Emit the base package skeleton when there were no convertible artifacts (e.g. a
                // <proxy>), and flush the Context record to types.bal once every artifact's default
                // properties have been collected.
                writeArtifacts(targetDir, context, writtenImports);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error while writing the Ballerina package: ", e);
        }
    }

    /**
     * Logs a warning for each dependency cycle and each unresolved reference in the graph. Both are
     * best-effort situations the conversion still proceeds through, so they are surfaced rather than
     * failing the migration: a cycle has no valid leaf-first order, and an unresolved reference points
     * at a sequence that was never found among the artifacts.
     */
    private static void logDependencyWarnings(DependencyGraph dependencyGraph) {
        for (List<ArtifactNode> cycle : dependencyGraph.cycles()) {
            String cyclePath = cycle.stream().map(ArtifactNode::id).collect(Collectors.joining(" -> "));
            LOG.warning("Cyclic dependency detected among artifacts: " + cyclePath + " -> " + cycle.get(0).id());
        }
        for (ArtifactNode unresolved : dependencyGraph.unresolvedNodes()) {
            LOG.warning("Unresolved dependency: sequence '" + unresolved.name()
                    + "' is referenced but no matching artifact was found.");
        }
    }

    private static void convertArtifact(ArtifactNode artifactNode, ConversionContext context) {
        SynapseNode node = DependencyResolver.findArtifact(artifactNode);
        BIRConverter<ConversionContext> converter = ROOT_CONVERTERS.get(node.kind());
        if (converter == null) {
            throw new UnsupportedOperationException("No root converter for Synapse node kind: " + node.kind());
        }
        converter.convert(node, context);
    }

    private static void addContextRecord(ConversionContext context) {
        List<RecordField> fields = new ArrayList<>();
        for (Map.Entry<String, PropertyInfo> property : context.properties().entrySet()) {
            String scope = property.getValue().scope();
            if (DEFAULT_SCOPE.equals(scope) || SYNAPSE_SCOPE.equals(scope)) {
                fields.add(new RecordField(property.getKey(), fieldType(property.getValue().types()), true));
            }
        }
        if (fields.isEmpty()) {
            return;
        }
        context.addRecord(new ModuleTypeDef(VARIABLES_TYPE, RecordTypeDesc.closedRecord(fields)));
        context.addRecord(new ModuleTypeDef(CONTEXT_TYPE, RecordTypeDesc.closedRecord(
                List.of(new RecordField(VARIABLES_FIELD, new BallerinaType(VARIABLES_TYPE))))));
    }

    @NotNull
    private static TypeDesc fieldType(Set<String> types) {
        if (types.size() == 1) {
            return new BallerinaType(types.iterator().next());
        }
        return new UnionTypeDesc(types.stream().map(BallerinaType::new).toList());
    }

    private static void writeArtifacts(Path targetDir, ConversionContext context,
                                       Map<Path, Set<Import>> writtenImports) throws IOException {
        context.addImports(ConversionContext.MAIN_BAL_FILE, List.of(new Import("ballerina", "http")));
        writeToFile(targetDir.resolve(ConversionContext.MAIN_BAL_FILE),
                context.importsFor(ConversionContext.MAIN_BAL_FILE),
                List.of(new HTTPListener(LISTENER_NAME, DEFAULT_PORT, DEFAULT_HOST)),
                context.services(), List.of(), List.of(), writtenImports);
        if (!context.functions().isEmpty()) {
            writeToFile(targetDir.resolve(ConversionContext.FUNCTIONS_BAL_FILE),
                    context.importsFor(ConversionContext.FUNCTIONS_BAL_FILE), List.of(),
                    List.of(), context.functions(), List.of(), writtenImports);
        }
        if (!context.records().isEmpty()) {
            writeToFile(targetDir.resolve(ConversionContext.TYPES_BAL_FILE),
                    context.importsFor(ConversionContext.TYPES_BAL_FILE), List.of(),
                    List.of(), List.of(), context.records(), writtenImports);
        }
    }

    private static void writeToFile(Path file, Set<Import> imports, List<Listener> listeners,
                                    List<Service> services, List<Function> functions,
                                    List<ModuleTypeDef> records,
                                    Map<Path, Set<Import>> writtenImports) throws IOException {
        appendConstructs(file, listeners, services, functions, records);
        prependNewImports(file, imports, writtenImports);
    }

    private static void appendConstructs(Path file, List<Listener> listeners, List<Service> services,
                                         List<Function> functions, List<ModuleTypeDef> records)
            throws IOException {
        boolean exists = Files.exists(file);
        if (exists && services.isEmpty() && functions.isEmpty() && records.isEmpty()) {
            return;
        }
        TextDocument document = new TextDocument(file.getFileName().toString(),
                List.of(), records, List.of(), exists ? List.of() : listeners,
                services, functions, List.of(), List.of(), List.of());
        String source = document.toSource();
        if (exists) {
            Files.writeString(file, System.lineSeparator() + source, StandardOpenOption.APPEND);
        } else {
            Files.writeString(file, source);
        }
    }

    private static void prependNewImports(Path file, Set<Import> imports,
                                          Map<Path, Set<Import>> writtenImports) throws IOException {
        if (!Files.exists(file)) {
            return;
        }
        Set<Import> written = writtenImports.computeIfAbsent(file, key -> new LinkedHashSet<>());
        Set<Import> newImports = new LinkedHashSet<>(imports);
        newImports.removeAll(written);
        if (newImports.isEmpty()) {
            return;
        }
        String importSource = new TextDocument(file.getFileName().toString(), new ArrayList<>(newImports),
                List.of(), List.of(), List.of(), List.of(), List.of(), List.of(), List.of(), List.of())
                .toSource();
        Files.writeString(file, importSource + Files.readString(file));
        written.addAll(newImports);
    }

    private static String stripExtension(String path) {
        if (!Files.isRegularFile(Paths.get(path))) {
            return path;
        }
        int lastSeparator = Math.max(path.lastIndexOf('/'), path.lastIndexOf('\\'));
        int lastDot = path.lastIndexOf('.');
        return lastDot > lastSeparator ? path.substring(0, lastDot) : path;
    }

    private static String ballerinaToml(String orgName, String packageName) {
        return """
                [package]
                org = "%s"
                name = "%s"
                version = "0.1.0"
                distribution = "2201.12.3"

                [build-options]
                observabilityIncluded = true
                """.formatted(orgName, packageName);
    }

    /**
     * Convert a single Synapse project directory into a Ballerina package at {@code targetPath}.
     *
     * <p>Used by the project-conversion test harness to compare generated output against the
     * checked-in expected Ballerina package.
     *
     * @param sourcePath  Synapse project directory
     * @param targetPath  directory to write the generated Ballerina package into
     * @param orgName     organization name for the generated Ballerina package
     * @param projectName project name for the generated Ballerina package
     */
    public static void migrateSynapseProject(String sourcePath, String targetPath, String orgName,
                                             String projectName) {
        // TODO: implement single-project Synapse -> Ballerina conversion.
        throw new UnsupportedOperationException(
                "Synapse project migration is not implemented yet. Source: " + sourcePath);
    }
}
