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

import baltool.logicapps.codegenerator.LogicAppsMigrationExecutor;
import baltool.logicapps.codegenerator.VerboseLogger;

import java.nio.file.Path;
import java.util.logging.Logger;

public class LogicAppsCli {

    private static final Logger logger = Logger.getLogger(LogicAppsCli.class.getName());

    public static void main(String[] args) {
        if (args.length != 1 && args.length != 3) {
            logger.severe("Usage: java -jar logicapps-migration-assistant.jar <source-file> " +
                    "[-a|--add-on <additional-instructions>] [-o|--out <output-directory>] [-n|--name <project-name>]");
            System.exit(1);
        }

        LogicAppsMigrationExecutor.migrateLogicAppToBallerina(Path.of(args[0]), "", Path.of(args[1]),
                false, false, new VerboseLogger(false));
    }
}
