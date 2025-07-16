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

import tibco.converter.TibcoConverter;

import java.io.PrintStream;

public class TibcoCli {

    public static void main(String[] args) {

        PrintStream errStream = System.err;
        if (args.length < 1) {
            errStream.println("Usage: java -jar cli-tibco.jar <source_path> [-o|--out output_path]");
            System.exit(1);
        }
        String sourcePath = args[0];
        if (args.length > 1) {
            if (!args[1].equals("-o") && !args[1].equals("--out")) {
                errStream.println("Invalid option: " + args[1]);
                errStream.println("Usage: java -jar cli-tibco.jar <source_path> [-o|--out output_path]");
                System.exit(1);
            } else {
                if (args.length < 3) {
                    errStream.println("Output path is required when using -o or --out option.");
                    System.exit(1);
                }
                String outputPath = args[2];
                TibcoConverter.migrateTibco(sourcePath, outputPath, false, true, false, false);
            }
        }
    }
}
