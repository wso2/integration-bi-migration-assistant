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

package cli;

import mule.MuleConverter;
import tibco.converter.TibcoConverter;

import java.util.logging.Logger;

public class Main {

    public static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        if (args.length < 1) {
            logger.severe("Usage: java -jar integration-bi-migration-assistant.jar " +
                    "[-t|--tibco] <mule-or-tibco-xml-config-file-or-project-directory>");
            System.exit(1);
        }
        boolean isTibcoMigration = args[0].equals("--tibco") || args[0].equals("-t");
        if (isTibcoMigration) {
            TibcoConverter.migrateTibco(args);
        } else {
            MuleConverter.migrateMuleProject(args);
        }
    }

}
