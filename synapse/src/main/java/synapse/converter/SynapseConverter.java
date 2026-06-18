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

import common.BallerinaModel.Import;
import common.BallerinaModel.Listener;
import common.BallerinaModel.Listener.HTTPListener;
import common.BallerinaModel.Service;
import common.BallerinaModel.Statement;
import common.BallerinaModel.TextDocument;
import synapse.converter.BIRConverter.APIConverter;
import synapse.model.Synapse.Kind;
import synapse.model.Synapse.Property;
import synapse.model.Synapse.SynapseNode;
import synapse.reader.SynapseConfigReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    // Maps each SynapseNode kind to the converter responsible for it. Only <api> is supported for now.
    private static final Map<Kind, BIRConverter> CONVERTERS = Map.of(
            Kind.API, new APIConverter());

    private static final String MAIN_BAL_FILE = "main.bal";
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

        List<SynapseNode> synapseModel = SynapseConfigReader.parse(sourcePath);

        ConversionContext context = new ConversionContext();
        for (SynapseNode node : synapseModel) {
            BIRConverter converter = CONVERTERS.get(node.kind());
            if (converter == null) {
                continue;
            }
            converter.convert(node, context);
        }

        if (dryRun) {
            return;
        }
        String targetPath = outputPath != null ? outputPath : stripExtension(sourcePath) + "_converted";
        generateBallerinaPackage(context, targetPath, orgName.orElse(DEFAULT_ORG),
                projectName.orElse(DEFAULT_PACKAGE));
    }

    private static String stripExtension(String path) {
        if (!Files.isRegularFile(Paths.get(path))) {
            return path;
        }
        int lastSeparator = Math.max(path.lastIndexOf('/'), path.lastIndexOf('\\'));
        int lastDot = path.lastIndexOf('.');
        return lastDot > lastSeparator ? path.substring(0, lastDot) : path;
    }

    private static void generateBallerinaPackage(ConversionContext context, String targetPath, String orgName,
                                                 String packageName) {

        List<Service> services = context.services();
        List<Import> imports = List.of(new Import("ballerina", "http"));
        List<Listener> listeners = List.of(new HTTPListener(LISTENER_NAME, DEFAULT_PORT, DEFAULT_HOST));
        TextDocument textDocument = new TextDocument(MAIN_BAL_FILE, imports, List.of(), List.of(), listeners,
                services, List.of(), List.of(), List.of(), List.of());

        try {
            Path targetDir = Paths.get(targetPath);
            Files.createDirectories(targetDir);
            Files.writeString(targetDir.resolve(MAIN_BAL_FILE), textDocument.toSource());
            Files.writeString(targetDir.resolve("Ballerina.toml"), ballerinaToml(orgName, packageName));
        } catch (IOException e) {
            throw new RuntimeException("Error while writing the Ballerina package: ", e);
        }
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

    /**
     * Converts a Synapse {@code <property>} mediator. How a property is converted depends on where it
     * lives: a property within a resource contributes to that resource's body, whereas a property
     * outside a resource (e.g. an api-level property) is handled differently. This converter therefore
     * first identifies its scope.
     */
    static class PropertyConverter implements BIRConverter {

        private static final String TRANSPORT_SCOPE = "transport";
        private static final String AXIS2_SCOPE = "axis2";

        @Override
        public void convert(SynapseNode node, ConversionContext context) {
            Property property = (Property) node;
            if (context.isWithinResource()) {
                convertResourceProperty(property, context);
            } else {
                // TODO: convert a property declared outside a resource.
            }
        }

        private static void convertResourceProperty(Property property, ConversionContext context) {
            String statement = switch (property.scope()) {
                case TRANSPORT_SCOPE -> "response.setHeader(\"" + property.name() + "\", \""
                        + property.value() + "\");";
                case AXIS2_SCOPE -> "response.statusCode = " + property.value() + ";";
                default -> toBallerinaType(property.type()) + " " + property.name() + " = "
                        + property.value() + ";";
            };
            context.statements().add(new Statement.BallerinaStatement(statement));
        }

        private static String toBallerinaType(String synapseType) {
            return switch (synapseType.toUpperCase()) {
                case "INTEGER", "INT", "LONG", "SHORT" -> "int";
                case "BOOLEAN" -> "boolean";
                case "DOUBLE", "FLOAT" -> "float";
                default -> "string";
            };
        }
    }
}
