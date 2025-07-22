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
package baltool.synapse.commands;

import io.ballerina.cli.BLauncherCmd;
import picocli.CommandLine;
import synapse.converter.SynapseConverter;

import java.io.PrintStream;
import java.util.Optional;

/**
 * This class represents the "migrate-synapse" bal tool command.
 *
 * @since 1.0.0
 */
@CommandLine.Command(name = "migrate-synapse",
        description = "Accepts a Synapse configuration file or directory as input")
public class MigrateSynapseCommand implements BLauncherCmd {

    private final PrintStream errStream;
    private static final String CMD_NAME = "migrate-synapse";
    private static final String USAGE = "bal migrate-synapse <source-synapse-config-directory-or-file> " +
            "[-o|--out <output-directory>] [-k|--keep-structure] [-v|--verbose] [-d|--dry-run] " +
                    "[-m|--multi-root] [-g|--org-name <organization-name>] [-p|--project-name <project-name>]";

    public MigrateSynapseCommand() {
        errStream = System.err;
    }

    @CommandLine.Parameters(description = "Source Synapse configuration file or directory path",
            arity = "0..1")
    private String sourcePath;

    @CommandLine.Option(names = { "--out", "-o" }, description = "Output directory path")
    private String outputPath;

    @CommandLine.Option(names = { "--keep-structure", "-k" }, description = "Keep process structure")
    private boolean keepStructure = false;

    @CommandLine.Option(names = { "--verbose", "-v" }, description = "Enable verbose output", defaultValue = "false")
    private boolean verbose;

    @CommandLine.Option(names = { "--dry-run",
            "-d" }, description = "Simulate the conversion without generating output files", defaultValue = "false")
    private boolean dryRun;

    @CommandLine.Option(names = { "--multi-root", "-m" },
            description = "Treat each child directory as a separate project and convert all of them",
            defaultValue = "false")
    private boolean multiRoot;

    @CommandLine.Option(names = { "--org-name",
            "-g" }, description = "Organization name for the generated Ballerina package")
    private String orgName;

    @CommandLine.Option(names = { "--project-name",
            "-p" }, description = "Project name for the generated Ballerina package")
    private String projectName;

    @Override
    public void execute() {
        if (sourcePath == null) {
            errStream.println("Error: Source Synapse configuration file or directory path is required.");
            onInvalidInput();
        }
        SynapseConverter.migrateSynapse(sourcePath, outputPath, keepStructure, verbose, dryRun, multiRoot,
                Optional.ofNullable(orgName), Optional.ofNullable(projectName));
    }

    private void onInvalidInput() {
        errStream.println("Usage: bal migrate-synapse <source-synapse-config-directory-or-file> " +
                "[-o|--out <output-directory>] [-k|--keep-structure] [-v|--verbose] [-d|--dry-run] [-m|--multi-root] " +
                        "[-g|--org-name <organization-name>] [-p|--project-name <project-name>]");
        System.exit(1);
    }

    @Override
    public String getName() {
        return CMD_NAME;
    }

    @Override
    public void printLongDesc(StringBuilder stringBuilder) {
        stringBuilder.append("Migrate Synapse configuration to Ballerina Integrator\n\n");
        stringBuilder.append("This command accepts a Synapse configuration file or directory path \n");
        stringBuilder.append("as input and generates equivalent Ballerina code" +
                " that can be opened in Ballerina Integrator.\n\n");
        stringBuilder.append("Optional flags:\n");
        stringBuilder.append("  --keep-structure, -k     Keep process structure\n");
        stringBuilder.append("  --verbose, -v            Enable verbose output during conversion\n");
        stringBuilder.append("  --dry-run, -d            Simulate the conversion without generating output files\n");
        stringBuilder.append(
                "  --multi-root, -m         " +
                        "Treat each child directory as a separate project and convert all of them\n");
        stringBuilder.append("  --org-name, -g           Organization name for the generated Ballerina package\n");
        stringBuilder.append("  --project-name, -p       Project name for the generated Ballerina package\n");
    }

    @Override
    public void printUsage(StringBuilder stringBuilder) {
        stringBuilder.append(USAGE).append("\n\n");
        stringBuilder.append("Examples:\n");
        stringBuilder.append("  bal migrate-synapse /path/to/synapse-config\n");
        stringBuilder.append("  bal migrate-synapse /path/to/synapse-config --out /path/to/output\n");
        stringBuilder.append("  bal migrate-synapse /path/to/synapse-config.xml\n");
        stringBuilder.append("  bal migrate-synapse /path/to/synapse-config.xml --out /path/to/output\n");
        stringBuilder.append("  bal migrate-synapse /path/to/synapse-config.xml -k\n");
        stringBuilder.append(
                "  bal migrate-synapse /path/to/synapse-config.xml --out /path/to/output --keep-structure\n");
        stringBuilder.append("  bal migrate-synapse /path/to/synapse-config.xml --verbose\n");
        stringBuilder.append("  bal migrate-synapse /path/to/synapse-config.xml --dry-run\n");
        stringBuilder.append("  bal migrate-synapse /path/to/synapse-config.xml -d\n");
        stringBuilder.append("  bal migrate-synapse /path/to/synapse-config.xml --verbose --dry-run\n");
        stringBuilder.append("  bal migrate-synapse /path/to/synapse-config.xml -v -d\n");
        stringBuilder.append("  bal migrate-synapse /path/to/configs-directory --multi-root --dry-run\n");
        stringBuilder.append("  bal migrate-synapse /path/to/configs-directory -m -d\n");
    }

    @Override
    public void setParentCmdParser(CommandLine commandLine) {
    }
}