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
package baltool.tibco.commands;

import io.ballerina.cli.BLauncherCmd;
import picocli.CommandLine;
import tibco.converter.TibcoConverter;

import java.io.PrintStream;

/**
 * This class represents the "migrate-tibco" bal tool command.
 *
 * @since 1.0.0
 */
@CommandLine.Command(name = "migrate-tibco",
        description = "Accepts a TIBCO BusinessWorks project directory or `.bwp` file path as input")
public class MigrateTibcoCommand implements BLauncherCmd {

    private final PrintStream errStream;
    private static final String CMD_NAME = "migrate-tibco";
    private static final String USAGE =
            "bal migrate-tibco <source-project-directory-or-file> [-o|--out <output-directory>] " +
                    "[-k|--keep-structure] [-v|--verbose] [-d|--dry-run] [-m|--multi-root]";

    public MigrateTibcoCommand() {
        errStream = System.err;
    }

    @CommandLine.Parameters(description = "Source TIBCO BusinessWorks project directory or `.bwp` file path",
            arity = "0..1")
    private String sourcePath;

    @CommandLine.Option(names = { "--out", "-o" }, description = "Output directory path")
    private String outputPath;

    @CommandLine.Option(names = { "--keep-structure", "-k" }, description = "Keep process structure")
    private boolean keepStructure = false;

    @CommandLine.Option(names = {"--verbose", "-v"}, description = "Enable verbose output", defaultValue = "false")
    private boolean verbose;

    @CommandLine.Option(names = {"--dry-run", "-d"},
            description = "Simulate the conversion without generating output files", defaultValue = "false")
    private boolean dryRun;

    @CommandLine.Option(names = {"--multi-root", "-m"},
            description = "Treat each child directory as a separate project and convert all of them",
            defaultValue = "false")
    private boolean multiRoot;

    @Override
    public void execute() {
        if (sourcePath == null) {
            errStream.println("Error: Source TIBCO BusinessWorks project directory or `.bwp` file path is required.");
            onInvalidInput();
        }
        TibcoConverter.migrateTibco(sourcePath, outputPath, keepStructure, verbose, dryRun, multiRoot);
    }

    private void onInvalidInput() {
        errStream.println("Usage: bal migrate-tibco <source-project-directory-or-file> " +
                "[-o|--out <output-directory>] [-k|--keep-structure] [-v|--verbose] [-d|--dry-run] [-m|--multi-root]");
        System.exit(1);
    }

    @Override
    public String getName() {
        return CMD_NAME;
    }

    @Override
    public void printLongDesc(StringBuilder stringBuilder) {
        stringBuilder.append("Migrate TIBCO BusinessWorks project or `.bwp` file to Ballerina Integrator\n\n");
        stringBuilder.append("This command accepts a TIBCO BusinessWorks project directory or `.bwp` file path \n");
        stringBuilder.append("as input and generates equivalent Ballerina code" +
                " that can be opened in Ballerina Integrator.\n\n");
        stringBuilder.append("Optional flags:\n");
        stringBuilder.append("  --keep-structure, -k     Keep process structure\n");
        stringBuilder.append("  --verbose, -v            Enable verbose output during conversion\n");
        stringBuilder.append("  --dry-run, -d            Simulate the conversion without generating output files\n");
        stringBuilder.append(
                "  --multi-root, -m         " +
                        "Treat each child directory as a separate project and convert all of them\n");
    }

    @Override
    public void printUsage(StringBuilder stringBuilder) {
        stringBuilder.append(USAGE).append("\n\n");
        stringBuilder.append("Examples:\n");
        stringBuilder.append("  bal migrate-tibco /path/to/mule-project\n");
        stringBuilder.append("  bal migrate-tibco /path/to/mule-project --out /path/to/output\n");
        stringBuilder.append("  bal migrate-tibco /path/to/tibco_process.bwp\n");
        stringBuilder.append("  bal migrate-tibco /path/to/tibco_process.bwp --out /path/to/output\n");
        stringBuilder.append("  bal migrate-tibco /path/to/tibco_process.bwp -k\n");
        stringBuilder.append(
                "  bal migrate-tibco /path/to/tibco_process.bwp --out /path/to/output --keep-structure\n");
        stringBuilder.append("  bal migrate-tibco /path/to/tibco_process.bwp --verbose\n");
        stringBuilder.append("  bal migrate-tibco /path/to/tibco_process.bwp --dry-run\n");
        stringBuilder.append("  bal migrate-tibco /path/to/tibco_process.bwp -d\n");
        stringBuilder.append("  bal migrate-tibco /path/to/tibco_process.bwp --verbose --dry-run\n");
        stringBuilder.append("  bal migrate-tibco /path/to/tibco_process.bwp -v -d\n");
        stringBuilder.append("  bal migrate-tibco /path/to/projects-directory --multi-root --dry-run\n");
        stringBuilder.append("  bal migrate-tibco /path/to/projects-directory -m -d\n");
    }

    @Override
    public void setParentCmdParser(CommandLine commandLine) {
    }
}
