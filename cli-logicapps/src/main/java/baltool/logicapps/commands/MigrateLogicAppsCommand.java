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

import baltool.logicapps.Constants;
import baltool.logicapps.codegenerator.LogicAppsMigrationExecutor;
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
    private static final String USAGE = "bal migrate-logicapps <source-logicapp-file> " +
            "[-a|--add-on <additional-instructions>] [-o|--out <output-directory>] [-n|--name <project-name>]";

    public MigrateLogicAppsCommand() {
        errStream = System.err;
    }

    @CommandLine.Parameters(description = "Source LogicApp `.json` file path",
            arity = "0..1")
    private String sourcePath;

    @CommandLine.Option(names = { "--add-on", "-a" },
            description = "Additional instructions for the migration process")
    private String additionalInstructions;

    @CommandLine.Option(names = { "--out", "-o" }, description = "Output directory path")
    private String outputPath;

    @CommandLine.Option(names = { "--name", "-n" }, description = "Project name for the generated Ballerina project")
    private String projectName;

    @Override
    public void execute() {
        if (sourcePath == null) {
            errStream.println("Error: LogicApp json file path is required.");
            onInvalidInput();
        }
        if (additionalInstructions == null) {
            additionalInstructions = "";
        }

        Path logicAppFilePath = Path.of(sourcePath);
        if (outputPath != null && projectName != null) {
            LogicAppsMigrationExecutor.migrateLogicAppToBallerina(logicAppFilePath, additionalInstructions,
                    Path.of(outputPath), projectName);
        } else if (outputPath != null) {
            LogicAppsMigrationExecutor.migrateLogicAppToBallerina(logicAppFilePath, additionalInstructions,
                    Path.of(outputPath));
        } else {
            LogicAppsMigrationExecutor.migrateLogicAppToBallerina(logicAppFilePath, additionalInstructions);
        }
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
    }

    @Override
    public void printUsage(StringBuilder stringBuilder) {
        stringBuilder.append(USAGE).append("\n\n");
        stringBuilder.append("Examples:\n");
        stringBuilder.append("  bal migrate-logicapps /path/to/logicapp-json-file\n");
        stringBuilder.append("  bal migrate-logicapps /path/to/logicapp-json-file --out /path/to/output\n");
        stringBuilder.append("  bal migrate-logicapps /path/to/logicapp-json-file --add-on additional-instructions\n");
        stringBuilder.append("  bal migrate-logicapps /path/to/logicapp-json-file --add-on additional-instructions " +
                "--out /path/to/output\n");
        stringBuilder.append("  bal migrate-logicapps /path/to/logicapp-json-file --out /path/to/output --name " +
                "my-logic-app-project\n");
    }

    @Override
    public void setParentCmdParser(CommandLine commandLine) {
    }
}
