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
package baltool.logicapps;

/**
 * Constants used in the Logic Apps migration tool.
 *
 * @since 0.1.0
 */
public class Constants {
    public static final String CMD_NAME = "migrate-logicapps";

    public static final String DEV_AUTH_ORG = "ballerinacopilotdev";
    public static final String AUTH_ORG = "ballerinacopilot";
    public static final String DEV_AUTH_REDIRECT_URL = "https://98c70105-822c-4359-8579-4da58f0ab4b7." +
            "e1-us-east-azure.choreoapps.dev";
    public static final String AUTH_REDIRECT_URL = "https://98c70105-822c-4359-8579-4da58f0ab4b7." +
            "e1-us-east-azure.choreoapps.dev";
    public static final String DEV_AUTH_CLIENT_ID = "XpQ6lphi7kjKkWzumYyqqNf7CjIa";
    public static final String AUTH_CLIENT_ID = "9rKng8hSZd0VkeA45Lt4LOfCp9Aa";
    public static final String CONFIG_FILE_PATH = "migrate-logicapps.config";
    public static final int AUTHENTICATION_TIMEOUT_SECONDS = 180;
    public static final String DEV_COPILOT_BACKEND_URL = "https://e95488c8-8511-4882-967f-ec3ae2a0f86f-dev." +
            "e1-us-east-azure.choreoapis.dev/ballerina-copilot/ballerina-copilot-api-byo/v2.0";
    public static final String COPILOT_BACKEND_URL = "https://dev-tools.wso2.com/ballerina-copilot/v2.0";
    public static final String BALLERINA_USER_HOME_NAME = ".ballerina";
    public static final String FILE_PATH = "filePath";
    public static final String CONTENT = "content";
    public static final String BALLERINA_TOML_FILE = "Ballerina.toml";
    public static final String DEFAULT_ORG_NAME = "wso2";
    public static final String DEFAULT_PROJECT_VERSION = "0.1.0";
    public static final String TRIPLE_BACKTICK_BALLERINA = "```ballerina";
    public static final String TRIPLE_BACKTICK = "```";
    public static final int MAXIMUM_RETRY_COUNT = 3;
}
