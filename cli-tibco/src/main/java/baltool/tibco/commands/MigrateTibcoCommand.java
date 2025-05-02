/*
 *  Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com). All Rights Reserved.
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
            "bal migrate-tibco <source-project-directory-or-file> [-o|--out <output-directory>]";

    public MigrateTibcoCommand() {
        errStream = System.err;
    }

    @CommandLine.Parameters(description = "Source TIBCO BusinessWorks project directory or `.bwp` file path",
            arity = "0..1")
    private String sourcePath;

    @CommandLine.Option(names = { "--out", "-o" }, description = "Output directory path")
    private String outputPath;

    @Override
    public void execute() {
        if (sourcePath == null) {
            errStream.println("Error: Source TIBCO BusinessWorks project directory or `.bwp` file path is required.");
            onInvalidInput();
        }
        TibcoConverter.migrateTibco(sourcePath, outputPath);
    }

    private void onInvalidInput() {
        errStream.println("Usage: bal migrate-tibco <source-project-directory-or-file> " +
                "[-o|--out <output-directory>]");
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
    }

    @Override
    public void printUsage(StringBuilder stringBuilder) {
        stringBuilder.append(USAGE).append("\n\n");
        stringBuilder.append("Examples:\n");
        stringBuilder.append("  bal migrate-tibco /path/to/mule-project\n");
        stringBuilder.append("  bal migrate-tibco /path/to/mule-project --out /path/to/output\n");
        stringBuilder.append("  bal migrate-tibco /path/to/tibco_process.bwp\n");
        stringBuilder.append("  bal migrate-tibco /path/to/tibco_process.bwp --out /path/to/output\n");
    }

    @Override
    public void setParentCmdParser(CommandLine commandLine) {
    }
}
