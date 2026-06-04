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
        // TODO: implement Synapse -> Ballerina migration.
        //  1. Parse the Synapse artifacts (proxy / api / sequence / endpoint) under `sourcePath`.
        //  2. Build an intermediate model and analyze mediator coverage.
        //  3. Generate the Ballerina package at `outputPath` (or the default `_converted` location).
        throw new UnsupportedOperationException(
                "Synapse migration is not implemented yet. Source: " + sourcePath);
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
