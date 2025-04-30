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

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the "bi-migrate-tibco" bal tool command.
 *
 * @since 1.0.0
 */
@CommandLine.Command(name = "bi-migrate-tibco",
        description = "Accepts a TIBCO BusinessWorks project directory or `.bwp` file path as input")
public class BiMigrateTibcoCommand implements BLauncherCmd {

    private static final String CMD_NAME = "bi-migrate-tibco";
    private static final String USAGE =
            "bal bi-migrate-tibco <source-project-directory-or-file> [--out <output-directory>]";

    public BiMigrateTibcoCommand() {
    }

    @CommandLine.Parameters(description = "Source TIBCO BusinessWorks project directory or `.bwp` file path")
    private String sourcePath;

    @CommandLine.Option(names = { "--out", "-o" }, description = "Output directory path")
    private String outputPath;

    @Override
    public void execute() {
        List<String> args = new ArrayList<>();
        if (sourcePath != null) {
            args.add(sourcePath);
        }
        if (outputPath != null) {
            args.add("-o");
            args.add(outputPath);
        }
        TibcoConverter.migrateTibco(args.toArray(String[]::new));
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
        stringBuilder.append("  bal bi-migrate-tibco /path/to/mule-project\n");
        stringBuilder.append("  bal bi-migrate-tibco /path/to/mule-project --out /path/to/output\n");
        stringBuilder.append("  bal bi-migrate-tibco /path/to/tibco_process.bwp\n");
        stringBuilder.append("  bal bi-migrate-tibco /path/to/tibco_process.bwp --out /path/to/output\n");
    }

    @Override
    public void setParentCmdParser(CommandLine commandLine) {
    }
}
