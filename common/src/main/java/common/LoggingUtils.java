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

package common;

import java.util.function.Consumer;
import java.util.logging.Logger;

public final class LoggingUtils {

    private LoggingUtils() {

    }

    public static Consumer<String> wrapLoggerForLogCallback(Logger logger) {
        return message -> {
            if (message == null) {
                logger.info("");
                return;
            }
            String trimmed = message.trim();
            if (trimmed.startsWith("[SEVERE]")) {
                logger.severe(trimmed.substring(8).trim());
            } else if (trimmed.startsWith("[ERROR]")) {
                logger.severe(trimmed.substring(7).trim());
            } else if (trimmed.startsWith("[WARN]")) {
                logger.warning(trimmed.substring(6).trim());
            } else if (trimmed.startsWith("[INFO]")) {
                logger.info(trimmed.substring(6).trim());
            } else if (trimmed.startsWith("[DEBUG]")) {
                logger.fine(trimmed.substring(7).trim());
            } else {
                logger.info(trimmed);
            }
        };
    }

    public static Consumer<String> wrapLoggerForStateCallback(Logger logger) {
        return logger::info;
    }

    public enum Level {
        DEBUG,
        INFO,
        WARN,
        ERROR,
        SEVERE
    }
}
