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

package common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import java.util.logging.Logger;

import static converter.MuleToBalConverter.getBallerinaModel;

public class IRMarkdownContentGenerator {

    private static final Logger logger = Logger.getLogger(IRMarkdownContentGenerator.class.getName());

    public static void main(String[] args) {
        if (args.length != 2) {
            logger.severe("Usage: java ballerina.BallerinaModelUtils <method> [<pathToXmlFile>]");
            System.exit(1);
        }

        String method = args[0];
        if (method.equals("getIRAsJson")) {
            String pathToXmlFile = args[1];
            logger.info(getIRAsJson(pathToXmlFile));
        } else {
            logger.severe("Unknown method: " + method);
            System.exit(1);
        }
    }

    public static String getIRAsJson(String pathToXmlFile) {
        BallerinaModel bm = getBallerinaModel(pathToXmlFile);
        return convertToJson(bm);
    }

    public static String convertToJson(BallerinaModel ballerinaModel) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new Jdk8Module());
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(ballerinaModel);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting BallerinaModel to JSON", e);
        }
    }
}
