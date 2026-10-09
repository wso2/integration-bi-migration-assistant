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

import common.BallerinaModel.Expression.MappingConstructor;
import common.BallerinaModel.Expression.NilConstant;
import common.BallerinaModel.Function;
import common.BallerinaModel.Import;
import common.BallerinaModel.Listener;
import common.BallerinaModel.Listener.HTTPListener;
import common.BallerinaModel.ModuleTypeDef;
import common.BallerinaModel.ModuleVar;
import common.BallerinaModel.Parameter;
import common.BallerinaModel.Service;
import common.BallerinaModel.Statement;
import common.BallerinaModel.TextDocument;
import common.BallerinaModel.TypeDesc;
import common.BallerinaModel.TypeDesc.BallerinaType;
import common.BallerinaModel.TypeDesc.BuiltinType;
import common.BallerinaModel.TypeDesc.MapTypeDesc;
import common.BallerinaModel.TypeDesc.RecordTypeDesc;
import common.BallerinaModel.TypeDesc.RecordTypeDesc.RecordField;
import common.BallerinaModel.TypeDesc.UnionTypeDesc;
import org.jetbrains.annotations.NotNull;
import synapse.converter.ConversionContext.PropertyInfo;
import synapse.converter.ConversionContext.UnsupportedEntry;
import synapse.converter.bir.APIConverter;
import synapse.converter.bir.BIRConverter;
import synapse.converter.bir.InboundEndpointConverter;
import synapse.converter.bir.SequenceConverter;
import synapse.converter.bir.mediators.classmediator.source.CfrDecompiler;
import synapse.converter.bir.mediators.classmediator.source.Decompiler;
import synapse.converter.bir.mediators.classmediator.source.JavaSourceResolver;
import synapse.converter.report.MigrationReport;
import synapse.model.DependencyGraph;
import synapse.model.DependencyGraph.ArtifactNode;
import synapse.model.DependencyGraph.UnsupportedArtifactEntry;
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
import java.util.stream.Stream;

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
            Kind.SEQUENCE, new SequenceConverter(),
            Kind.INBOUND_ENDPOINT, new InboundEndpointConverter());

    private static final String DEFAULT_PORT = "8080";
    private static final String DEFAULT_HOST = "0.0.0.0";
    private static final String DEFAULT_ORG = "wso2";
    private static final String DEFAULT_PACKAGE = "synapse";
    private static final String CONTEXT_TYPE = "Context";
    private static final String VARIABLES_TYPE = "Variables";
    private static final String VARIABLES_FIELD = "variables";
    private static final String PAYLOAD_FIELD = "payload";
    private static final String HEADERS_FIELD = "headers";
    private static final String AXIS2_FIELD = "axis2";
    private static final String STATUS_CODE_FIELD = "statusCode";
    private static final String CALLER_FIELD = "caller";
    private static final String REQUEST_PARAM = "request";
    private static final String RESPOND_FUNCTION = "respond";
    private static final String EMIT_PAYLOAD_FUNCTION = "emitPayload";
    private static final String HTTP_CALLER = "http:Caller";
    private static final String HTTP_REQUEST = "http:Request";
    private static final String ERROR_OPTIONAL = "error?";
    private static final String DEFAULT_SCOPE = "default";
    private static final String SYNAPSE_SCOPE = "synapse";
    private static final String REPORT_FILE = "migration_report.md";
    private static final String UNSUPPORTED_ARTIFACT_CATEGORY = "Unsupported artifact";

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
    public static void migrateSynapse(String sourcePath, String outputPath, boolean keepStructure,
                                      boolean verbose, boolean dryRun, boolean multiRoot, Optional<String> orgName,
                                      Optional<String> projectName) {
        migrateSynapse(sourcePath, outputPath, keepStructure, verbose, dryRun, multiRoot, orgName,
                projectName, javaSourceRoots(sourcePath), javaArchives(sourcePath), new CfrDecompiler());
    }

    // Explicit source roots only
    public static void migrateSynapse(String sourcePath, String outputPath, boolean keepStructure,
                                      boolean verbose, boolean dryRun, boolean multiRoot, Optional<String> orgName,
                                      Optional<String> projectName, List<Path> javaSourceRoots) {
        migrateSynapse(sourcePath, outputPath, keepStructure, verbose, dryRun, multiRoot, orgName,
                projectName, javaSourceRoots, List.of(), Decompiler.NONE);
    }

    /**
     * As {@link #migrateSynapse(String, String, boolean, boolean, boolean, boolean, Optional, Optional)},
     * but with explicit Java source roots for locating class mediator sources.
     * Used by tests that keep mediator sources in a shared location.
     */
    public static void migrateSynapse(String sourcePath, String outputPath, boolean keepStructure,
                                      boolean verbose, boolean dryRun, boolean multiRoot, Optional<String> orgName,
                                      Optional<String> projectName, List<Path> javaSourceRoots, List<Path> javaArchives,
                                      Decompiler decompiler) {

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
        context.setJavaSourceResolver(new JavaSourceResolver(javaSourceRoots, javaArchives, decompiler));
        Path sourceRoot = sourceRoot(sourcePath);
        // Must run before any artifact converts: <api> and <inboundEndpoint> have no dependency edge, so
        // their conversion order isn't guaranteed, and each artifact's output is discarded right after
        // it's flushed.
        DispatchFilterIndexer.index(dependencyGraph, context, sourceRoot);
        // Reserved before any artifact is converted, so a class mediator stub can never collide with
        // these fixed functions
        context.reserveFunctionName(RESPOND_FUNCTION);
        context.reserveFunctionName(EMIT_PAYLOAD_FUNCTION);

        registerUnsupportedArtifacts(dependencyGraph, context, sourceRoot);

        if (dryRun) {
            for (ArtifactNode artifactNode : dependencyGraph.sortedNodes()) {
                context.setCurrentFile(relativePath(sourceRoot, artifactNode.file()));
                convertArtifact(artifactNode, context);
                context.clearArtifactOutput();
            }
            printReport(context);
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
                context.setCurrentFile(relativePath(sourceRoot, artifactNode.file()));
                convertArtifact(artifactNode, context);
                writeArtifacts(targetDir, context, writtenImports);
                context.clearArtifactOutput();
            }
            addContextRecord(context);
            context.converterFunctions().forEach(context::addFunction);
            context.classMediatorStubs().forEach(context::addFunction);
            addRespondFunction(context);
            addEmitPayloadFunction(context);
            // Flush the Context record to types.bal now that every artifact's default properties have
            // been collected.
            writeArtifacts(targetDir, context, writtenImports);
            writeReport(targetDir, context);
        } catch (IOException e) {
            throw new RuntimeException("Error while writing the Ballerina package: ", e);
        }
    }

    /**
     * Registers every unsupported top-level artifact (e.g. {@code <proxy>}) collected while building the
     * dependency graph, so it appears in the migration report. Such artifacts have no Ballerina
     * construct to host an inline to-do, so the report is their only surfacing point.
     */
    private static void registerUnsupportedArtifacts(DependencyGraph dependencyGraph, ConversionContext context,
                                                     Path sourceRoot) {
        for (UnsupportedArtifactEntry artifact : dependencyGraph.unsupportedArtifacts()) {
            context.reportUnsupported(new UnsupportedEntry(UNSUPPORTED_ARTIFACT_CATEGORY, artifact.tag(),
                    relativePath(sourceRoot, artifact.file()),
                    "Top-level '<" + artifact.tag() + ">' artifact is not supported; manual conversion required.",
                    artifact.rawXml()));
        }
    }

    private static void writeReport(Path targetDir, ConversionContext context) throws IOException {
        if (context.unsupported().isEmpty()) {
            return;
        }
        Files.writeString(targetDir.resolve(REPORT_FILE), MigrationReport.render(context.unsupported()));
    }

    private static void printReport(ConversionContext context) {
        if (context.unsupported().isEmpty()) {
            return;
        }
        LOG.info(System.lineSeparator() + MigrationReport.render(context.unsupported()));
    }

    private static Path sourceRoot(String sourcePath) {
        Path source = Paths.get(sourcePath);
        return Files.isDirectory(source) ? source : source.getParent();
    }

    @NotNull
    static String relativePath(Path sourceRoot, Path file) {
        if (file == null) {
            return "";
        }
        if (sourceRoot == null) {
            return file.getFileName().toString();
        }
        try {
            return sourceRoot.relativize(file).toString();
        } catch (IllegalArgumentException e) {
            return file.toString();
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

    /**
     * Class mediator Java lives beside the artifacts being converted.
     * Returns whichever of these exist, as source roots for the {@link JavaSourceResolver};
     * an empty list means no original source is embedded.
     */
    private static List<Path> javaSourceRoots(String sourcePath) {
        Path base = Paths.get(sourcePath).toAbsolutePath();
        if (!Files.isDirectory(base)) {
            base = base.getParent();
        }
        List<Path> roots = new ArrayList<>();
        if (base != null) {
            for (String candidate : List.of("java", "src/main/java")) {
                Path root = base.resolve(candidate);
                if (Files.isDirectory(root)) {
                    roots.add(root);
                }
            }
        }
        return roots;
    }

    /** Jars carrying mediator sources or bytecode: {@code <project>/lib}, {@code <project>/dropins}. */
    private static List<Path> javaArchives(String sourcePath) {
        Path base = Paths.get(sourcePath).toAbsolutePath();
        if (!Files.isDirectory(base)) {
            base = base.getParent();
        }
        List<Path> jars = new ArrayList<>();
        if (base != null) {
            for (String dir : List.of("lib", "dropins")) {
                Path d = base.resolve(dir);
                if (Files.isDirectory(d)) {
                    try (Stream<Path> s = Files.list(d)) {
                        s.filter(p -> p.toString().endsWith(".jar")).forEach(jars::add);
                    } catch (IOException e) {
                        LOG.warning("Could not list archive directory " + d + ": " + e.getMessage());
                    }
                }
            }
        }
        return jars;
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
        List<RecordField> variableFields = new ArrayList<>();
        for (Map.Entry<String, PropertyInfo> property : context.properties().entrySet()) {
            String scope = property.getValue().scope();
            if (DEFAULT_SCOPE.equals(scope) || SYNAPSE_SCOPE.equals(scope)) {
                variableFields.add(new RecordField(property.getKey(), fieldType(property.getValue().types()), true));
            }
        }
        context.addImports(ConversionContext.TYPES_BAL_FILE, List.of(new Import("ballerina", "http")));
        context.addRecord(new ModuleTypeDef(VARIABLES_TYPE, RecordTypeDesc.closedRecord(variableFields)));
        context.addRecord(new ModuleTypeDef(CONTEXT_TYPE, RecordTypeDesc.closedRecord(List.of(
                new RecordField(VARIABLES_FIELD, new BallerinaType(VARIABLES_TYPE)),
                new RecordField(PAYLOAD_FIELD, BuiltinType.ANYDATA, new NilConstant()),
                new RecordField(HEADERS_FIELD, new MapTypeDesc(BuiltinType.STRING),
                        new MappingConstructor(List.of())),
                new RecordField(AXIS2_FIELD, new MapTypeDesc(BuiltinType.ANYDATA),
                        new MappingConstructor(List.of())),
                new RecordField(STATUS_CODE_FIELD, BuiltinType.INT, true),
                new RecordField(CALLER_FIELD, new BallerinaType(HTTP_CALLER), true)))));
    }

    private static void addRespondFunction(ConversionContext context) {
        context.addImports(ConversionContext.FUNCTIONS_BAL_FILE, List.of(new Import("ballerina", "http")));
        context.addFunction(new Function(RESPOND_FUNCTION,
                List.of(new Parameter("ctx", new BallerinaType(CONTEXT_TYPE))),
                new BallerinaType(ERROR_OPTIONAL),
                List.of(
                        new Statement.BallerinaStatement("http:Response response = new;"),
                        new Statement.BallerinaStatement("response.setPayload(ctx.payload);"),
                        new Statement.BallerinaStatement(
                                "foreach [string, string] [name, value] in ctx.headers.entries() {"
                                        + " response.setHeader(name, value); }"),
                        new Statement.BallerinaStatement("int? statusCode = ctx.statusCode;"),
                        new Statement.BallerinaStatement("if statusCode is int { response.statusCode = statusCode; }"),
                        new Statement.BallerinaStatement(
                                "check (<http:Caller>ctx.caller)->respond(response);"))));
    }

    private static void addEmitPayloadFunction(ConversionContext context) {
        context.addImports(ConversionContext.FUNCTIONS_BAL_FILE, List.of(new Import("ballerina", "http")));
        context.addFunction(new Function(EMIT_PAYLOAD_FUNCTION,
                List.of(new Parameter("ctx", new BallerinaType(CONTEXT_TYPE)),
                        new Parameter(REQUEST_PARAM, new BallerinaType(HTTP_REQUEST))),
                new BallerinaType("error?"),
                List.of(
                        new Statement.BallerinaStatement("string contentType = request.getContentType();"),
                        new Statement.BallerinaStatement(
                                "if contentType.startsWith(\"application/json\") {"
                                        + " ctx.payload = check request.getJsonPayload(); }"
                                        + " else if contentType.startsWith(\"application/xml\")"
                                        + " || contentType.startsWith(\"text/xml\") {"
                                        + " ctx.payload = check request.getXmlPayload(); }"
                                        + " else if contentType.startsWith(\"text/\") {"
                                        + " ctx.payload = check request.getTextPayload(); }"
                                        + " else { ctx.payload = check request.getBinaryPayload(); }"))));
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
        Path mainBalFile = targetDir.resolve(ConversionContext.MAIN_BAL_FILE);
        List<Listener> listeners = new ArrayList<>();
        // The shared HTTP listener every <api> service binds to is declared once, the first round that
        // actually converts an <api>; an <inboundEndpoint>'s own dedicated listener (context.listeners())
        // is per-artifact output instead, so it is appended on whichever round actually introduces it.
        if (!context.isSharedListenerDeclared() && usesSharedListener(context.services())) {
            listeners.add(new HTTPListener(APIConverter.DEFAULT_LISTENER_REF, DEFAULT_PORT, DEFAULT_HOST));
            context.setSharedListenerDeclared(true);
        }
        listeners.addAll(context.listeners());
        // main.bal only needs ballerina/http when it actually declares an HTTP listener: a jms/file-only
        // inbound endpoint's main.bal never references an http: type, so an unconditional import here
        // would be an unused-import compile error.
        if (listeners.stream().anyMatch(listener -> listener.type() == Listener.ListenerType.HTTP)) {
            context.addImports(ConversionContext.MAIN_BAL_FILE, List.of(new Import("ballerina", "http")));
        }
        writeToFile(mainBalFile, context.importsFor(ConversionContext.MAIN_BAL_FILE),
                context.moduleVars(), listeners, context.services(), List.of(), List.of(), writtenImports);
        if (!context.functions().isEmpty()) {
            writeToFile(targetDir.resolve(ConversionContext.FUNCTIONS_BAL_FILE),
                    context.importsFor(ConversionContext.FUNCTIONS_BAL_FILE), List.of(),
                    List.of(), List.of(), context.functions(), List.of(), writtenImports);
        }
        if (!context.records().isEmpty()) {
            writeToFile(targetDir.resolve(ConversionContext.TYPES_BAL_FILE),
                    context.importsFor(ConversionContext.TYPES_BAL_FILE), List.of(),
                    List.of(), List.of(), List.of(), context.records(), writtenImports);
        }
    }

    // Whether this round's services include one bound to the shared listener, as opposed to only
    // <inboundEndpoint> services, which bind to their own dedicated listener instead.
    private static boolean usesSharedListener(List<Service> services) {
        return services.stream()
                .anyMatch(service -> service.listenerRefs().contains(APIConverter.DEFAULT_LISTENER_REF));
    }

    private static void writeToFile(Path file, Set<Import> imports, List<ModuleVar> moduleVars,
                                    List<Listener> listeners, List<Service> services, List<Function> functions,
                                    List<ModuleTypeDef> records,
                                    Map<Path, Set<Import>> writtenImports) throws IOException {
        appendConstructs(file, moduleVars, listeners, services, functions, records);
        prependNewImports(file, imports, writtenImports);
    }

    private static void appendConstructs(Path file, List<ModuleVar> moduleVars, List<Listener> listeners,
                                         List<Service> services, List<Function> functions,
                                         List<ModuleTypeDef> records)
            throws IOException {
        boolean exists = Files.exists(file);
        if (moduleVars.isEmpty() && listeners.isEmpty() && services.isEmpty() && functions.isEmpty()
                && records.isEmpty()) {
            return;
        }
        TextDocument document = new TextDocument(file.getFileName().toString(),
                List.of(), records, moduleVars, listeners,
                services, functions, List.of(), List.of(), List.of());
        String source = blankLineAfterConfigurableBlock(document.toSource());
        if (exists) {
            Files.writeString(file, System.lineSeparator() + source, StandardOpenOption.APPEND);
        } else {
            Files.writeString(file, source);
        }
    }

    private static String blankLineAfterConfigurableBlock(String source) {
        String[] lines = source.split("\n", -1);
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            result.append(lines[i]);
            boolean isLastLine = i == lines.length - 1;
            if (!isLastLine) {
                result.append("\n");
            }
            boolean endsConfigurableBlock = lines[i].strip().startsWith("configurable ") && !isLastLine
                    && !lines[i + 1].isBlank() && !lines[i + 1].strip().startsWith("configurable ");
            if (endsConfigurableBlock) {
                result.append("\n");
            }
        }
        return result.toString();
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
        String existingContent = Files.readString(file);
        // A prior round may have already prepended its own imports; merge directly above them instead of
        // stacking another blank-line-separated import block on top of one that's already there.
        if (existingContent.startsWith("import ")) {
            importSource = importSource.stripTrailing() + System.lineSeparator();
        }
        Files.writeString(file, importSource + existingContent);
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
