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
package baltool.mule.commands;

import io.ballerina.cli.BLauncherCmd;
import mule.MuleConverter;
import picocli.CommandLine;

/**
 * This class represents the "migrate-mule" bal tool command.
 *
 * @since 1.0.0
 */
@CommandLine.Command(
        name = "migrate-mule",
        description = "Accepts a Mule project directory or a standalone Mule `.xml` file path as input")
public class MigrateMuleCommand implements BLauncherCmd {

    private static final String CMD_NAME = "migrate-mule";
    private static final String USAGE =
            "bal migrate-mule <source-project-directory-or-file> [--out <output-directory>]";

    public MigrateMuleCommand() {
    }

    @CommandLine.Parameters(description = "Source Mule project directory or standalone Mule `.xml` file path")
    private String sourcePath;

    @CommandLine.Option(names = { "--out", "-o" }, description = "Output directory path")
    private String outputPath;

    @Override
    public void execute() {
        MuleConverter.migrateMuleProject(sourcePath, outputPath);
    }

    @Override
    public String getName() {
        return CMD_NAME;
    }

    @Override
    public void printLongDesc(StringBuilder stringBuilder) {
        stringBuilder.append("Migrate Mule project or standalone `.xml` file to Ballerina Integrator\n\n");
        stringBuilder.append("This command accepts a Mule project directory or a standalone Mule `.xml` file path\n");
        stringBuilder.append("as input and generates equivalent Ballerina code" +
                " that can be opened in Ballerina Integrator.\n\n");
    }

    @Override
    public void printUsage(StringBuilder stringBuilder) {
        stringBuilder.append(USAGE).append("\n\n");
        stringBuilder.append("Examples:\n");
        stringBuilder.append("  bal migrate-mule /path/to/mule-project\n");
        stringBuilder.append("  bal migrate-mule /path/to/mule-project --out /path/to/output\n");
        stringBuilder.append("  bal migrate-mule /path/to/mule-flow.xml");
        stringBuilder.append("  bal migrate-mule /path/to/mule-flow.xml --out /path/to/output\n");
    }

    @Override
    public void setParentCmdParser(CommandLine commandLine) {
    }
}
