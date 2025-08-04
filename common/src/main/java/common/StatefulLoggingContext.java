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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class StatefulLoggingContext implements LoggingContext {

    private final Consumer<String> stateConsumer;
    private final Consumer<String> loggingConsumer;
    private String currentState = null;
    private boolean stateActive = false;
    private final List<String> currentStateBuffer = new ArrayList<>();
    private int linesWritten = 0;

    public StatefulLoggingContext(Consumer<String> stateConsumer, Consumer<String> loggingConsumer) {
        this.stateConsumer = stateConsumer;
        this.loggingConsumer = loggingConsumer;
    }

    @Override
    public void log(LoggingUtils.Level level, String message) {
        if (currentState == null) {
            throw new IllegalStateException("Cannot log without first setting a state. Call logState() first.");
        }

        String logLine = "  " + formatLogMessage(level, message);
        currentStateBuffer.add(logLine);
        loggingConsumer.accept(logLine);
        linesWritten++;
    }

    @Override
    public void logState(String message) {
        if (stateActive) {
            setPreviousStateAsDone();
        }

        setNewState(message);

        loggingConsumer.accept("");
        stateConsumer.accept("\u001B[1m▶ " + message + "\u001B[0m");
    }

    private void setNewState(String message) {
        currentState = message;
        stateActive = true;
        currentStateBuffer.clear();
        linesWritten = 0;
    }

    private void setPreviousStateAsDone() {
        // Clear all lines written under current state (move up and clear)
        StringBuilder clearCommands = new StringBuilder();
        for (int i = 0; i < linesWritten; i++) {
            clearCommands.append("\u001B[1A"); // Move cursor up one line
            clearCommands.append("\u001B[2K"); // Clear entire line
        }
        // Clear the state line itself
        clearCommands.append("\u001B[1A\u001B[2K");
        stateConsumer.accept(clearCommands.toString());

        // Rewrite completed state
        stateConsumer.accept("\u001B[1m◉ " + currentState + "\u001B[0m");

        // Rewrite all buffered log lines
        for (String line : currentStateBuffer) {
            loggingConsumer.accept(line);
        }
        loggingConsumer.accept("");
    }

    public void markCurrentStateComplete() {
        if (!stateActive) {
            return;
        }
        // Clear all lines written under current state (move up and clear)
        setPreviousStateAsDone();

        // Reset state
        stateActive = false;
        currentState = null;
        currentStateBuffer.clear();
        linesWritten = 0;
    }

    private String formatLogMessage(LoggingUtils.Level level, String message) {
        String levelPrefix = getLevelPrefix(level);
        return switch (level) {
            case ERROR, SEVERE -> levelPrefix + message + "\u001B[0m"; // Reset after red text
            case INFO, DEBUG -> levelPrefix + message + "\u001B[0m"; // Reset after dim text
            case WARN -> levelPrefix + message; // No color change needed
        };
    }

    private String getLevelPrefix(LoggingUtils.Level level) {
        return switch (level) {
            case ERROR -> "\u001B[31m❌ "; // Red
            case SEVERE -> "\u001B[31m🚨 "; // Red
            case WARN -> "⚠️  ";
            case INFO -> "\u001B[2mℹ️  "; // Dim/light font
            case DEBUG -> "\u001B[2m🐛 "; // Dim/light font
        };
    }

}
