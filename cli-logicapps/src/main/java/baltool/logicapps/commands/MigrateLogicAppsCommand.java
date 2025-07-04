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
package baltool.logicapps.commands;

import baltool.logicapps.codegenerator.LogicAppsMigrationExecutor;
import baltool.logicapps.codegenerator.VerboseLogger;
import io.ballerina.cli.BLauncherCmd;
import picocli.CommandLine;

import java.io.PrintStream;
import java.nio.file.Path;

import static baltool.logicapps.Constants.CMD_NAME;

/**
 * This class represents the "migrate-logicapps" bal tool command.
 *
 * @since 1.0.0
 */
@CommandLine.Command(
        name = CMD_NAME,
        description = "Accepts a LogicApp `.json` file path as input"
)
public class MigrateLogicAppsCommand implements BLauncherCmd {

    private final PrintStream errStream;
    private final PrintStream outStream;
    private static final String USAGE = "bal migrate-logicapps <source-logicapp-file> " +
            "[-v|--verbose] [-o|--out <output-directory>] [-m|--multi-root]";

    public MigrateLogicAppsCommand() {
        errStream = System.err;
        outStream = System.out;
    }

    @CommandLine.Parameters(description = "Source LogicApp `.json` file path",
            arity = "0..1")
    private String sourcePath;

    @CommandLine.Option(names = {"--verbose", "-v"}, description = "Enable verbose output", defaultValue = "false")
    private boolean verbose;

    @CommandLine.Option(names = { "--out", "-o" }, description = "Output directory path")
    private String outputPath;

    @CommandLine.Option(names = {"--multi-root", "-m"},
            description = "Treat each child directory as a separate project and convert all of them",
            defaultValue = "false")
    private boolean multiRoot;

    @Override
    public void execute() {
        try {
            if (verbose) {
                outStream.println("Starting Logic Apps Migration Tool");
                outStream.println("Command line arguments:");
                outStream.println("  Source path: " + (sourcePath != null ? sourcePath : "Not provided"));
                outStream.println("  Output path: " + (outputPath != null ? outputPath : "Default (source directory)"));
                outStream.println("  Multi-root mode: " + multiRoot);
                outStream.println("  Verbose mode: " + verbose);
                outStream.println();
            }

            if (sourcePath == null) {
                errStream.println("Error: LogicApp json file path is required.");
                if (verbose) {
                    errStream.println("DEBUG: sourcePath parameter was null");
                }
                onInvalidInput();
                return;
            }

            if (verbose) {
                outStream.println("Validating input parameters");
                outStream.println("  Checking if source path exists: " + sourcePath);
            }

            // Temporary disable the additional instructions feature
            String additionalInstructions = "";

            Path logicAppFilePath = Path.of(sourcePath);

            if (verbose) {
                outStream.println("  Resolved source path: " + logicAppFilePath.toAbsolutePath());
                outStream.println();
            }

            if (outputPath != null) {
                Path outputDir = Path.of(outputPath);
                LogicAppsMigrationExecutor.migrateLogicAppToBallerina(logicAppFilePath, additionalInstructions,
                        outputDir, verbose, multiRoot, new VerboseLogger(verbose));
            } else {
                LogicAppsMigrationExecutor.migrateLogicAppToBallerina(logicAppFilePath, additionalInstructions, verbose,
                        multiRoot, new VerboseLogger(verbose));
            }
        } catch (Exception e) {
            errStream.println("Error during command execution: " + e.getMessage());
            if (verbose) {
                errStream.println();
                errStream.println("=".repeat(60));
                errStream.println("DETAILED ERROR INFORMATION:");
                errStream.println("=".repeat(60));
                errStream.println("Exception type: " + e.getClass().getSimpleName());
                errStream.println("Error message: " + e.getMessage());
                errStream.println();
                errStream.println("Stack trace:");
                e.printStackTrace(errStream);
                errStream.println("=".repeat(60));
            }
            System.exit(1);
        }
    }

    private void onInvalidInput() {
        errStream.println("Usage: " + USAGE);
        if (verbose) {
            errStream.println();
            errStream.println("DEBUG: Invalid input detected, terminating with exit code 1");
        }
        System.exit(1);
    }

    @Override
    public String getName() {
        return CMD_NAME;
    }

    @Override
    public void printLongDesc(StringBuilder stringBuilder) {
        stringBuilder.append("Migrate LogicApp to Ballerina Integrator\n\n");
        stringBuilder.append("This command accepts a LogicApp `.json` file path\n");
        stringBuilder.append("as input and generates equivalent Ballerina code" +
                " that can be opened in Ballerina Integrator.\n\n");
        stringBuilder.append("Optional flags:\n");
        stringBuilder.append("  --add-on, -a            Additional instructions for the migration process\n");
        stringBuilder.append("  --out, -o               Specify the output directory for the generated Ballerina " +
                "project\n");
        stringBuilder.append("  --name, -n              Specify the project name for the generated Ballerina " +
                "project\n");
        stringBuilder.append("  --verbose, -v           Enable detailed logging output\n");
        stringBuilder.append("  --multi-root, -m        Process multiple logic app files concurrently\n");
    }

    @Override
    public void printUsage(StringBuilder stringBuilder) {
        stringBuilder.append(USAGE).append("\n\n");
        stringBuilder.append("Examples:\n");
        stringBuilder.append("  bal migrate-logicapps /path/to/logicapp-json-file\n");
        stringBuilder.append("  bal migrate-logicapps /path/to/logicapp-json-file --out /path/to/output\n");
        stringBuilder.append("  bal migrate-logicapps /path/to/logicapp-json-file --verbose\n");
        stringBuilder.append("  bal migrate-logicapps /path/to/logicapp-json-file --add-on additional-instructions\n");
        stringBuilder.append("  bal migrate-logicapps /path/to/logicapp-json-file --add-on additional-instructions " +
                "--out /path/to/output\n");
        stringBuilder.append("  bal migrate-logicapps /path/to/logicapp-json-file --out /path/to/output --name " +
                "my-logic-app-project\n");
        stringBuilder.append("  bal migrate-logicapps /path/to/directory --multi-root --verbose\n");
    }

    @Override
    public void setParentCmdParser(CommandLine commandLine) {
    }
}
