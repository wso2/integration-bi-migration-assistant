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
import synapse.converter.BIRConverter.APIConverter;
import synapse.converter.ConversionContext.SequenceMetadata;
import synapse.model.Synapse.Kind;
import synapse.model.Synapse.Sequence;
import synapse.model.Synapse.SequenceMediator;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
            Kind.SEQUENCE, new BIRConverter.SequenceConverter());

    private static final String MAIN_BAL_FILE = "main.bal";
    private static final String FUNCTIONS_BAL_FILE = "functions.bal";
    private static final String TYPES_BAL_FILE = "types.bal";
    private static final String LISTENER_NAME = "httpListener";
    private static final String DEFAULT_PORT = "8080";
    private static final String DEFAULT_HOST = "0.0.0.0";
    private static final String DEFAULT_ORG = "wso2";
    private static final String DEFAULT_PACKAGE = "synapse";

    private SynapseConverter() {
    }

    /**
     * Migrate a Synapse project directory or a single artifact file to a Ballerina package.
     *
     * <p>Artifacts are processed one at a time: each artifact file is parsed, converted and flushed
     * to the generated Ballerina package before the next one is read, so the whole project is never
     * held in memory at once. The generated constructs are consolidated by kind across all artifacts:
     * services (with the shared HTTP listener) go to {@code main.bal}, functions to
     * {@code functions.bal} and record types to {@code types.bal}.
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

        if (dryRun) {
            for (File artifact : artifactFiles) {
                convertArtifact(artifact, new ConversionContext());
            }
            return;
        }

        String targetPath = outputPath != null ? outputPath : stripExtension(sourcePath) + "_converted";
        try {
            Path targetDir = Paths.get(targetPath);
            Files.createDirectories(targetDir);
            Files.writeString(targetDir.resolve("Ballerina.toml"),
                    ballerinaToml(orgName.orElse(DEFAULT_ORG), projectName.orElse(DEFAULT_PACKAGE)));

            ConversionContext context = new ConversionContext();
            collectSequenceMetadata(artifactFiles, context);
            for (File artifact : artifactFiles) {
                convertArtifact(artifact, context);
                writeArtifacts(targetDir, context);
                context.clearArtifactOutput();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error while writing the Ballerina package: ", e);
        }
    }

    private static void collectSequenceMetadata(List<File> artifactFiles, ConversionContext context) {
        Map<String, SequenceMetadata> metadata = new HashMap<>();
        for (File artifact : artifactFiles) {
            for (SynapseNode node : SynapseConfigReader.parse(artifact)) {
                if (node instanceof Sequence sequence) {
                    SequenceMetadata sequenceMetadata = buildSequenceMetadata(sequence);
                    metadata.put(sequenceMetadata.name(), sequenceMetadata);
                }
            }
        }
        propagateRespond(metadata);
        propagatePayloadFactory(metadata);
        metadata.values().forEach(context::addSequenceMetadata);
    }

    private static SequenceMetadata buildSequenceMetadata(Sequence sequence) {
        boolean containsRespond = false;
        boolean containsPayloadFactory = false;
        List<String> referencedSequences = new ArrayList<>();
        for (SynapseNode mediator : sequence.mediators()) {
            if (mediator.kind() == Kind.RESPOND) {
                containsRespond = true;
            } else if (mediator.kind() == Kind.PAYLOAD_FACTORY) {
                containsPayloadFactory = true;
            } else if (mediator instanceof SequenceMediator sequenceMediator) {
                referencedSequences.add(sequenceMediator.key());
            }
        }
        return new SequenceMetadata(sequence.name(), containsRespond, containsPayloadFactory,
                referencedSequences);
    }

    private static void propagateRespond(Map<String, SequenceMetadata> metadata) {
        boolean changed = true;
        while (changed) {
            changed = false;
            for (String name : new ArrayList<>(metadata.keySet())) {
                SequenceMetadata sequenceMetadata = metadata.get(name);
                if (sequenceMetadata.containsRespond()) {
                    continue;
                }
                for (String referenced : sequenceMetadata.referencedSequences()) {
                    SequenceMetadata referencedMetadata = metadata.get(referenced);
                    if (referencedMetadata != null && referencedMetadata.containsRespond()) {
                        metadata.put(name, new SequenceMetadata(name, true,
                                sequenceMetadata.containsPayloadFactory(),
                                sequenceMetadata.referencedSequences()));
                        changed = true;
                        break;
                    }
                }
            }
        }
    }

    private static void propagatePayloadFactory(Map<String, SequenceMetadata> metadata) {
        boolean changed = true;
        while (changed) {
            changed = false;
            for (String name : new ArrayList<>(metadata.keySet())) {
                SequenceMetadata sequenceMetadata = metadata.get(name);
                if (sequenceMetadata.containsPayloadFactory()) {
                    continue;
                }
                for (String referenced : sequenceMetadata.referencedSequences()) {
                    SequenceMetadata referencedMetadata = metadata.get(referenced);
                    if (referencedMetadata != null && referencedMetadata.containsPayloadFactory()) {
                        metadata.put(name, new SequenceMetadata(name, sequenceMetadata.containsRespond(),
                                true, sequenceMetadata.referencedSequences()));
                        changed = true;
                        break;
                    }
                }
            }
        }
    }

    private static ConversionContext convertArtifact(File artifact, ConversionContext context) {
        List<SynapseNode> nodes = SynapseConfigReader.parse(artifact);
        for (SynapseNode node : nodes) {
            BIRConverter<ConversionContext> converter = ROOT_CONVERTERS.get(node.kind());
            if (converter == null) {
                throw new UnsupportedOperationException("No root converter for Synapse node kind: " + node.kind());
            }
            converter.convert(node, context);
        }
        return context;
    }

    private static void writeArtifacts(Path targetDir, ConversionContext context) throws IOException {
        appendToFile(targetDir.resolve(MAIN_BAL_FILE), List.of(new Import("ballerina", "http")),
                List.of(new HTTPListener(LISTENER_NAME, DEFAULT_PORT, DEFAULT_HOST)),
                context.services(), List.of(), List.of());
        if (!context.functions().isEmpty()) {
            List<Import> functionImports = context.functionsRequireHttpImport()
                    ? List.of(new Import("ballerina", "http")) : List.of();
            appendToFile(targetDir.resolve(FUNCTIONS_BAL_FILE), functionImports, List.of(),
                List.of(), context.functions(), List.of());
        }
        if (!context.records().isEmpty()) {
            appendToFile(targetDir.resolve(TYPES_BAL_FILE), List.of(), List.of(),
                List.of(), List.of(), context.records());
        }
    }

    private static void appendToFile(Path file, List<Import> imports, List<Listener> listeners,
                                     List<Service> services, List<Function> functions,
                                     List<ModuleTypeDef> records) throws IOException {
        boolean exists = Files.exists(file);
        if (exists && services.isEmpty() && functions.isEmpty() && records.isEmpty()) {
            return;
        }
        TextDocument document = new TextDocument(file.getFileName().toString(),
                exists ? List.of() : imports, records, List.of(), exists ? List.of() : listeners,
                services, functions, List.of(), List.of(), List.of());
        String source = document.toSource();
        if (exists) {
            Files.writeString(file, System.lineSeparator() + source, StandardOpenOption.APPEND);
        } else {
            Files.writeString(file, source);
        }
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
