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

import mule.MuleMigrationExecutor;

import java.util.logging.Logger;

public class MuleCli {

    private static final Logger logger = Logger.getLogger(MuleCli.class.getName());

    public static void main(String[] args) {
        if (args.length != 1 && args.length != 3) {
            logger.severe("Usage: java -jar mule-migration-assistant.jar <source-project-directory-or-file> " +
                    "[-o|--out <output-directory>]");
            System.exit(1);
        }
        String inputPathArg = args[0];
        String outputPathArg = null;
        if (args.length == 3 && (args[1].equals("-o") || args[1].equals("--out"))) {
            outputPathArg = args[2];
        }
        MuleMigrationExecutor.migrateMuleSource(inputPathArg, outputPathArg, false, false);
    }
}
