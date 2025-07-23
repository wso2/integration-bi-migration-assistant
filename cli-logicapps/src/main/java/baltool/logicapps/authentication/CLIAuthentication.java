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
package baltool.logicapps.authentication;

import baltool.logicapps.codegenerator.VerboseLogger;
import common.AuthenticateUtils;
import common.LoggingContext;
import common.LoggingUtils;

import static baltool.logicapps.Constants.TOOL_NAME;

/**
 * Handles CLI authentication for the Ballerina LogicApps migration tool. This class delegates to AuthenticateUtils and
 * manages the OAuth flow to obtain access and refresh tokens.
 */
public class CLIAuthentication {

    public static final boolean BALLERINA_DEV_UPDATE = Boolean.parseBoolean(
            System.getenv("BALLERINA_DEV_UPDATE"));

    /**
     * Retrieves a valid access token, either from the config file or by performing authentication.
     *
     * @param logger Logger to log messages during the process
     * @return Valid access token if available, otherwise performs authentication
     * @throws Exception If authentication fails or network issues occur
     */
    public static String getValidAccessToken(VerboseLogger logger) throws Exception {
        AuthenticateUtils.Config config = createAuthConfig();
        LoggingContext loggingContext = createLoggingContext(logger);
        return AuthenticateUtils.getValidAccessToken(config, loggingContext);
    }

    /**
     * Creates the authentication configuration based on environment settings.
     *
     * @return AuthConfig with appropriate settings for the current environment
     */
    private static AuthenticateUtils.Config createAuthConfig() {
        return new AuthenticateUtils.Config(
                BALLERINA_DEV_UPDATE,
                TOOL_NAME
        );
    }

    /**
     * Creates a LoggingContext wrapper around the VerboseLogger.
     *
     * @param logger The VerboseLogger to wrap
     * @return LoggingContext that delegates to the VerboseLogger
     */
    private static LoggingContext createLoggingContext(VerboseLogger logger) {
        return new LoggingContext() {
            @Override
            public void log(LoggingUtils.Level level, String message) {
                switch (level) {
                    case INFO -> logger.printInfo(message);
                    case ERROR, SEVERE -> logger.printError(message);
                    case WARN -> logger.printWarn(message);
                    case DEBUG -> logger.printDebug(message);
                }
            }

            @Override
            public void logState(String message) {
                logger.printInfo(message);
            }
        };
    }

}
