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

package cli;

import synapse.converter.SynapseConverter;

import java.io.PrintStream;
import java.util.Optional;
import java.util.logging.Logger;

public class SynapseCli {

    private static final Logger logger = Logger.getLogger(SynapseCli.class.getName());

    public static void main(String[] args) {
        if (args.length == 0) {
            printUsageAndExit();
        }

        String inputPathArg = null;
        String outputPathArg = null;
        String orgName = null;
        String projectName = null;
        boolean dryRun = false;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            switch (arg) {
                case "-o":
                case "--out":
                    if (i + 1 >= args.length) {
                        logger.severe("Error: --out requires an argument");
                        printUsageAndExit();
                    }
                    outputPathArg = args[++i];
                    break;
                case "-d":
                case "--dry-run":
                    dryRun = true;
                    break;
                case "-g":
                case "--org-name":
                    if (i + 1 >= args.length) {
                        logger.severe("Error: --org-name requires an argument");
                        printUsageAndExit();
                    }
                    orgName = args[++i];
                    break;
                case "-p":
                case "--project-name":
                    if (i + 1 >= args.length) {
                        logger.severe("Error: --project-name requires an argument");
                        printUsageAndExit();
                    }
                    projectName = args[++i];
                    break;
                // TODO: the following options are accepted for parity with the `bal migrate-synapse`
                //  command and the Mule CLI, but are not yet honoured by the Synapse converter.
                //  Implement their behaviour and forward the values to SynapseConverter.migrateSynapse(...).
                case "-v":
                case "--verbose":
                case "-k":
                case "--keep-structure":
                case "-m":
                case "--multi-root":
                    logger.warning("Option '" + arg + "' is not supported yet and will be ignored.");
                    break;
                default:
                    if (arg.startsWith("-")) {
                        logger.severe("Error: Unknown option: " + arg);
                        printUsageAndExit();
                    } else {
                        if (inputPathArg == null) {
                            inputPathArg = arg;
                        } else {
                            logger.severe("Error: Multiple input paths specified");
                            printUsageAndExit();
                        }
                    }
                    break;
            }
        }

        if (inputPathArg == null) {
            logger.severe("Error: Synapse project directory or Synapse xml file path is required");
            printUsageAndExit();
        }

        SynapseConverter.migrateSynapse(inputPathArg, outputPathArg, false, false, dryRun, false,
                Optional.ofNullable(orgName), Optional.ofNullable(projectName));
    }

    private static void printUsageAndExit() {
        PrintStream err = System.err;
        err.println("Usage: java -jar synapse-migration-assistant.jar <source-project-directory-or-file> " +
                "[-o|--out <output-directory>] [-d|--dry-run] " +
                "[-g|--org-name <organization-name>] [-p|--project-name <project-name>]");
        err.println();
        err.println("Options:");
        err.println("  -o, --out <path>              Output directory path");
        err.println("  -d, --dry-run                 Simulate the conversion without generating output files");
        err.println("  -g, --org-name <name>         Organization name for the generated Ballerina package");
        err.println("  -p, --project-name <name>     Project name for the generated Ballerina package");
        err.println();
        err.println("Not supported yet (accepted but ignored):");
        err.println("  -v, --verbose                 Enable verbose output during conversion");
        err.println("  -k, --keep-structure          Keep Synapse project structure");
        err.println("  -m, --multi-root              Treat each child directory as a separate project");
        err.println();
        err.println("Examples:");
        err.println("  java -jar synapse-migration-assistant.jar /path/to/synapse-artifact.xml");
        err.println("  java -jar synapse-migration-assistant.jar /path/to/synapse-artifact.xml --out /path/to/output");
        err.println("  java -jar synapse-migration-assistant.jar /path/to/synapse-artifact.xml --dry-run");
        err.println("  java -jar synapse-migration-assistant.jar /path/to/synapse-artifact.xml --org-name myorg " +
                "--project-name myproject");
        System.exit(1);
    }
}
