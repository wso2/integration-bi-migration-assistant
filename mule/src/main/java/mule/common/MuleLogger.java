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
package mule.common;

import common.LoggingUtils;

import java.util.function.Consumer;
import java.util.logging.Logger;

public class MuleLogger {

    public static final String LOGGER_NAME = "migrate-mule";
    private final Consumer<String> stateCallback;
    private final Consumer<String> logCallback;

    public MuleLogger(Consumer<String> stateCallback, Consumer<String> logCallback) {
        this.stateCallback = stateCallback;
        this.logCallback = logCallback;
    }

    public MuleLogger(boolean verbose) {
        Logger logger = verbose ? createDefaultLogger() : createSilentLogger();
        this.stateCallback = LoggingUtils.wrapLoggerForStateCallback(logger);
        this.logCallback = LoggingUtils.wrapLoggerForLogCallback(logger);
    }

    public void logState(String message) {
        stateCallback.accept(message);
    }

    public void log(LoggingUtils.Level level, String message) {
        logCallback.accept("[" + level + "] " + message);
    }

    public void logInfo(String message) {
        log(LoggingUtils.Level.INFO, message);
    }

    public void logWarn(String message) {
        log(LoggingUtils.Level.WARN, message);
    }

    public void logError(String message) {
        log(LoggingUtils.Level.ERROR, message);
    }

    public void logSevere(String message) {
        log(LoggingUtils.Level.SEVERE, message);
    }

    private static Logger createSilentLogger() {
        Logger silentLogger = Logger.getLogger(LOGGER_NAME);
        silentLogger.setFilter(record -> record.getLevel().intValue() >= java.util.logging.Level.SEVERE.intValue());
        return silentLogger;
    }

    private static Logger createDefaultLogger() {
        return Logger.getLogger(LOGGER_NAME);
    }
}
