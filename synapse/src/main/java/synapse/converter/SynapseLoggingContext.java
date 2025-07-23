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

package synapse.converter;

import common.LoggingContext;
import common.LoggingUtils;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

class SynapseLoggingContext implements LoggingContext {

    private final PrintStream out = System.out;
    private String currentState = null;
    private boolean stateActive = false;
    private final List<String> currentStateBuffer = new ArrayList<>();
    private int linesWritten = 0;

    @Override
    public void log(LoggingUtils.Level level, String message) {
        if (currentState == null) {
            throw new IllegalStateException("Cannot log without first setting a state. Call logState() first.");
        }

        String logLine = "  " + formatLogMessage(level, message);
        currentStateBuffer.add(logLine);
        out.println(logLine);
        linesWritten++;
    }

    @Override
    public void logState(String message) {
        if (stateActive) {
            // Clear all lines written under current state (move up and clear)
            for (int i = 0; i < linesWritten; i++) {
                out.print("\u001B[1A"); // Move cursor up one line
                out.print("\u001B[2K"); // Clear entire line
            }
            // Clear the state line itself
            out.print("\u001B[1A\u001B[2K");
            
            // Rewrite completed state
            out.println("\u001B[1m◉ " + currentState + "\u001B[0m");
            
            // Rewrite all buffered log lines
            for (String line : currentStateBuffer) {
                out.println(line);
            }
            out.println();
        }

        currentState = message;
        stateActive = true;
        currentStateBuffer.clear();
        linesWritten = 0;

        // Add extra newline for visual separation before new state
        out.println();
        out.print("\u001B[1m▶ " + message + "\u001B[0m");
        out.flush();
    }

    public void markCurrentStateComplete() {
        if (stateActive) {
            // Clear all lines written under current state (move up and clear)
            for (int i = 0; i < linesWritten; i++) {
                out.print("\u001B[1A"); // Move cursor up one line
                out.print("\u001B[2K"); // Clear entire line
            }
            // Clear the state line itself
            out.print("\u001B[1A\u001B[2K");
            
            // Rewrite completed state
            out.println("\u001B[1m◉ " + currentState + "\u001B[0m");
            
            // Rewrite all buffered log lines
            for (String line : currentStateBuffer) {
                out.println(line);
            }
            out.println();
            
            // Reset state
            stateActive = false;
            currentState = null;
            currentStateBuffer.clear();
            linesWritten = 0;
            
            // Ensure final output with newline
            out.flush();
        }
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

    private String formatLogMessage(LoggingUtils.Level level, String message) {
        String levelPrefix = getLevelPrefix(level);
        return switch (level) {
            case ERROR, SEVERE -> levelPrefix + message + "\u001B[0m"; // Reset after red text
            case INFO, DEBUG -> levelPrefix + message + "\u001B[0m"; // Reset after dim text
            case WARN -> levelPrefix + message; // No color change needed
        };
    }
}
