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

import mule.MuleMigrator;

import java.io.PrintStream;
import java.util.logging.Logger;

public class MuleCli {

    private static final Logger logger = Logger.getLogger(MuleCli.class.getName());

    public static void main(String[] args) {
        if (args.length == 0) {
            printUsageAndExit();
        }

        String inputPathArg = null;
        String outputPathArg = null;
        String orgName = null;
        String projectName = null;
        Integer muleVersion = null;
        boolean dryRun = false;
        boolean verbose = false;
        boolean keepStructure = false;
        boolean multiRoot = false;

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
                case "-f":
                case "--force-version":
                    if (i + 1 >= args.length) {
                        logger.severe("Error: --force-version requires an argument (3 or 4)");
                        printUsageAndExit();
                    }
                    try {
                        muleVersion = Integer.parseInt(args[++i]);
                        if (muleVersion != 3 && muleVersion != 4) {
                            logger.severe("Error: --force-version must be 3 or 4");
                            printUsageAndExit();
                        }
                    } catch (NumberFormatException e) {
                        logger.severe("Error: --force-version must be a number (3 or 4)");
                        printUsageAndExit();
                    }
                    break;
                case "-v":
                case "--verbose":
                    verbose = true;
                    break;
                case "-d":
                case "--dry-run":
                    dryRun = true;
                    break;
                case "-k":
                case "--keep-structure":
                    keepStructure = true;
                    break;
                case "-m":
                case "--multi-root":
                    multiRoot = true;
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
            logger.severe("Error: mule project directory or mule xml file path is required");
            printUsageAndExit();
        }

        MuleMigrator.migrateAndExportMuleSource(inputPathArg, outputPathArg, orgName, projectName, muleVersion,
                dryRun, verbose, keepStructure, multiRoot);
    }

    private static void printUsageAndExit() {
        PrintStream err = System.err;
        err.println("Usage: java -jar mule-migration-assistant.jar <source-project-directory-or-file> " +
                "[-o|--out <output-directory>] [-f|--force-version <3|4>] [-v|--verbose] " +
                "[-k|--keep-structure] [-d|--dry-run] [-m|--multi-root] " +
                "[-g|--org-name <organization-name>] [-p|--project-name <project-name>]");
        err.println();
        err.println("Options:");
        err.println("  -o, --out <path>              Output directory path");
        err.println("  -f, --force-version <3|4>     Force Mule version (3 or 4) if automatic detection fails");
        err.println("  -v, --verbose                 Enable verbose output during conversion");
        err.println("  -d, --dry-run                 Simulate the conversion without generating output files");
        err.println("  -k, --keep-structure          Keep mule project structure");
        err.println("  -m, --multi-root              Treat each child directory as a separate project and " +
                "convert all of them");
        err.println("  -g, --org-name <name>         Organization name for the generated Ballerina package");
        err.println("  -p, --project-name <name>     Project name for the generated Ballerina package");
        err.println();
        err.println("Examples:");
        err.println("  java -jar mule-migration-assistant.jar /path/to/mule-project");
        err.println("  java -jar mule-migration-assistant.jar /path/to/mule-project --out /path/to/output");
        err.println("  java -jar mule-migration-assistant.jar /path/to/mule-flow.xml");
        err.println("  java -jar mule-migration-assistant.jar /path/to/mule-project --dry-run");
        err.println("  java -jar mule-migration-assistant.jar /path/to/mule-project --keep-structure");
        err.println("  java -jar mule-migration-assistant.jar /path/to/mule-project --verbose");
        err.println("  java -jar mule-migration-assistant.jar /path/to/mule-project --force-version 3");
        err.println("  java -jar mule-migration-assistant.jar /path/to/mule-projects-directory --multi-root");
        err.println("  java -jar mule-migration-assistant.jar /path/to/mule-project --org-name myorg " +
                "--project-name myproject");
        System.exit(1);
    }
}
