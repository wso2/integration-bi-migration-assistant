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
package cli;

import synapse.converter.SynapseConverter;

import java.util.Optional;

/**
 * Standalone CLI for Synapse to Ballerina migration.
 *
 * @since 1.0.0
 */
public class SynapseCli {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Error: Source Synapse configuration file or directory path is required.");
            printUsage();
            System.exit(1);
        }

        String sourcePath = args[0];
        String outputPath = null;
        boolean keepStructure = false;
        boolean verbose = false;
        boolean dryRun = false;
        boolean multiRoot = false;
        String orgName = null;
        String projectName = null;

        // Simple argument parsing
        for (int i = 1; i < args.length; i++) {
            switch (args[i]) {
                case "-o":
                case "--out":
                    if (i + 1 < args.length) {
                        outputPath = args[++i];
                    }
                    break;
                case "-k":
                case "--keep-structure":
                    keepStructure = true;
                    break;
                case "-v":
                case "--verbose":
                    verbose = true;
                    break;
                case "-d":
                case "--dry-run":
                    dryRun = true;
                    break;
                case "-m":
                case "--multi-root":
                    multiRoot = true;
                    break;
                case "-g":
                case "--org-name":
                    if (i + 1 < args.length) {
                        orgName = args[++i];
                    }
                    break;
                case "-p":
                case "--project-name":
                    if (i + 1 < args.length) {
                        projectName = args[++i];
                    }
                    break;
                default:
                    System.err.println("Unknown option: " + args[i]);
                    printUsage();
                    System.exit(1);
            }
        }

        SynapseConverter.migrateSynapse(sourcePath, outputPath, keepStructure, verbose, dryRun, multiRoot,
                Optional.ofNullable(orgName), Optional.ofNullable(projectName));
    }

    private static void printUsage() {
        System.err.println("Usage: java -jar synapse-migration-assistant.jar <source-synapse-config-directory-or-file> " +
                "[-o|--out <output-directory>] [-k|--keep-structure] [-v|--verbose] [-d|--dry-run] [-m|--multi-root] " +
                "[-g|--org-name <organization-name>] [-p|--project-name <project-name>]");
    }
}