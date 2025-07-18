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
package baltool.mule.commands;

import io.ballerina.cli.BLauncherCmd;
import mule.MuleMigrator;
import picocli.CommandLine;

import java.io.PrintStream;

/**
 * This class represents the "migrate-mule" bal tool command.
 *
 * @since 1.0.0
 */
@CommandLine.Command(
        name = "migrate-mule",
        description = "Accepts a Mule project directory or a standalone Mule `.xml` file path as input")
public class MigrateMuleCommand implements BLauncherCmd {

    private final PrintStream errStream;
    private static final String CMD_NAME = "migrate-mule";
    private static final String USAGE = "bal migrate-mule <source-project/s-directory-or-file> " +
            "[-o|--out <output-directory>] [-f|--force-version <3|4>]" +
            "[-v|--verbose] [-k|--keep-structure] [-d|--dry-run] [-m|--multi-root]";

    public MigrateMuleCommand() {
        errStream = System.err;
    }

    @CommandLine.Parameters(description = "Source Mule project directory or standalone Mule `.xml` file path",
            arity = "0..1")
    private String sourcePath;

    @CommandLine.Option(names = { "--out", "-o" }, description = "Output directory path")
    private String outputPath;

    @CommandLine.Option(names = { "--force-version", "-f" },
            description = "Force Mule version (3 or 4) if automatic detection fails")
    private Integer muleVersion;

    @CommandLine.Option(names = {"--verbose", "-v"}, description = "Enable verbose output", defaultValue = "false")
    private boolean verbose;

    @CommandLine.Option(names = {"--dry-run", "-d"},
            description = "Simulate the conversion without generating output files", defaultValue = "false")
    private boolean dryRun;

    @CommandLine.Option(names = { "--keep-structure", "-k" },
            description = "Keep mule project structure", defaultValue = "false")
    private boolean keepStructure;

    @CommandLine.Option(names = {"--multi-root", "-m"},
            description = "Treat each child directory as a separate project and convert all of them",
            defaultValue = "false")
    private boolean multiRoot;

    @Override
    public void execute() {
        if (sourcePath == null) {
            errStream.println("Error: mule project directory or mule xml file path is required.");
            onInvalidInput();
        }
        MuleMigrator.migrateMuleSource(sourcePath, outputPath, muleVersion, dryRun, verbose, keepStructure, multiRoot);
    }

    private void onInvalidInput() {
        errStream.println("Usage: " + USAGE);
        System.exit(1);
    }

    @Override
    public String getName() {
        return CMD_NAME;
    }

    @Override
    public void printLongDesc(StringBuilder stringBuilder) {
        stringBuilder.append("Migrate Mule project or standalone `.xml` file to Ballerina\n\n");
        stringBuilder.append("This command accepts a Mule project directory or a standalone Mule `.xml` file path\n");
        stringBuilder.append("as input and generates equivalent Ballerina code" +
                " that can be opened in Ballerina Integrator.\n\n");
        stringBuilder.append("Optional flags:\n");
        stringBuilder.append("  --out, -o                Output directory path\n");
        stringBuilder.append("  --force-version, -f      Force Mule version (3 or 4) if automatic detection fails\n");
        stringBuilder.append("  --keep-structure, -k     Keep mule project structure\n");
        stringBuilder.append("  --verbose, -v            Enable verbose output during conversion\n");
        stringBuilder.append("  --dry-run, -d            Simulate the conversion without generating output files\n");
        stringBuilder.append("  --multi-root, -m         Treat each child directory as a separate project and convert" +
                " all of them\n");
    }

    @Override
    public void printUsage(StringBuilder stringBuilder) {
        stringBuilder.append(USAGE).append("\n\n");
        stringBuilder.append("Examples:\n");
        stringBuilder.append("  bal migrate-mule /path/to/mule-project\n");
        stringBuilder.append("  bal migrate-mule /path/to/mule-project --out /path/to/output\n");
        stringBuilder.append("  bal migrate-mule /path/to/mule-flow.xml\n");
        stringBuilder.append("  bal migrate-mule /path/to/mule-flow.xml --out /path/to/output\n");
        stringBuilder.append("  bal migrate-mule /path/to/mule-project --dry-run\n");
        stringBuilder.append("  bal migrate-mule /path/to/mule-project --keep-structure\n");
        stringBuilder.append("  bal migrate-mule /path/to/mule-project --verbose\n");
        stringBuilder.append("  bal migrate-mule /path/to/mule-project --dry-run --verbose\n");
        stringBuilder.append("  bal migrate-mule /path/to/mule-projects-directory --multi-root\n");
        stringBuilder.append("  bal migrate-mule /path/to/mule-projects-directory --multi-root --dry-run\n");
        stringBuilder.append("  bal migrate-mule /path/to/mule-project --force-version 3\n");
        stringBuilder.append("  bal migrate-mule /path/to/mule-project --force-version 4\n");
    }

    @Override
    public void setParentCmdParser(CommandLine commandLine) {
    }
}
