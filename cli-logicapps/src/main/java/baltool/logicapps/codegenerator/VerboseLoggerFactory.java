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
package baltool.logicapps.codegenerator;

import java.io.PrintStream;
import java.util.concurrent.ConcurrentHashMap;

public class VerboseLoggerFactory {
    private static final PrintStream printStream = System.out;

    private static volatile VerboseLoggerFactory instance;
    private final ConcurrentHashMap<String, VerboseLogger> loggers;
    private final ConcurrentHashMap<String, ProgressBar> progressBars;
    private final boolean isVerboseEnabled;
    private boolean isProgressBarActive = false;

    private VerboseLoggerFactory(boolean isVerboseEnabled) {
        this.loggers = new ConcurrentHashMap<>();
        this.progressBars = new ConcurrentHashMap<>();
        this.isVerboseEnabled = isVerboseEnabled;
    }

    public static VerboseLoggerFactory getInstance(boolean isVerboseEnabled) {
        if (instance == null) {
            synchronized (VerboseLoggerFactory.class) {
                if (instance == null) {
                    instance = new VerboseLoggerFactory(isVerboseEnabled);
                }
            }
        }
        return instance;
    }

    /**
     * Adds a process with a file name and the number of steps to the logger and progress bar.
     *
     * @param fileName the name of the file associated with the process
     * @param steps    the total number of steps in the process
     */
    public void addProcess(String fileName, int steps) {
        addLogger(fileName, new VerboseLogger(isVerboseEnabled, fileName));
        addProgressBar(fileName, new ProgressBar(steps, fileName));
    }

    /**
     * Adds a logger with a file name to the logger map.
     *
     * @param fileName the name of the file associated with the logger
     */
    private void addLogger(String fileName, VerboseLogger logger) {
        loggers.put(fileName, logger);
    }

    /**
     * Retrieves the logger associated with the specified file name.
     *
     * @param fileName the name of the file for which to retrieve the logger
     * @return the VerboseLogger associated with the file name, or null if not found
     */
    public VerboseLogger getVerboseLogger(String fileName) {
        if (loggers.containsKey(fileName)) {
            return loggers.get(fileName);
        }
        return null;
    }

    /**
     * Adds a progress bar with a file name and the number of steps to the progress bar map.
     *
     * @param fileName the name of the file associated with the progress bar
     * @param progressBar the ProgressBar instance to be added
     */
    private void addProgressBar(String fileName, ProgressBar progressBar) {
        progressBars.put(fileName, progressBar);
    }

    /**
     * Retrieves the progress bar associated with the specified file name.
     *
     * @param fileName the name of the file for which to retrieve the progress bar
     * @return the ProgressBar associated with the file name, or null if not found
     */
    public ProgressBar getProgressBar(String fileName) {
        if (progressBars.containsKey(fileName)) {
            return progressBars.get(fileName);
        }
        return null;
    }

    /**
     * Sets the state of the progress bar to active or inactive.
     *
     * @param state true to set the progress bar active, false to set it inactive
     */
    public void setProgressBarActive(boolean state) {
        this.isProgressBarActive = state;
    }

    /**
     * Checks if the progress bar is currently active.
     *
     * @return true if the progress bar is active, false otherwise
     */
    public boolean isProgressBarActive() {
        return isProgressBarActive;
    }

    /**
     * Starts a new progress bar for the specified file name with an initial message.
     * If a progress bar is already active, it clears the display before starting a new one.
     *
     * @param fileName the name of the file associated with the progress bar
     * @param message  the initial message to display in the progress bar
     */
    public void startProgress(String fileName, String message) {
        if (isProgressBarActive()) {
            clearProgressDisplay();
        } else {
            setProgressBarActive(true);
        }
        getProgressBar(fileName).updateProgress(1, message);
        printProgressDisplay();
    }

    /**
     * Updates the progress bar for the specified file name with the current step and a message.
     * If a progress bar is already active, it clears the display before updating.
     *
     * @param fileName      the name of the file associated with the progress bar
     * @param currentStep   the current step number in the process
     * @param message       a message to display alongside the progress
     */
    public void updateProgress(String fileName, int currentStep, String message) {
        if (isProgressBarActive()) {
            clearProgressDisplay();
        } else {
            setProgressBarActive(true);
        }
        getProgressBar(fileName).updateProgress(currentStep, message);
        printProgressDisplay();
    }

    /**
     * Finishes the progress bar for the specified file name with a success or failure message.
     * If a progress bar is already active, it clears the display before finishing.
     *
     * @param fileName  the name of the file associated with the progress bar
     * @param isSuccess true if the task was successful, false otherwise
     * @param message   a message to display upon completion
     */
    public void finishProgress(String fileName, boolean isSuccess, String message) {
        if (isProgressBarActive()) {
            clearProgressDisplay();
        }
        getProgressBar(fileName).finishProgress(isSuccess, message);
        printProgressDisplay();
    }

    /**
     * Clears the progress display by moving the cursor up and clearing the lines.
     * This is useful when updating or finishing a progress bar.
     */
    public void clearProgressDisplay() {
        if (isProgressBarActive()) {
            for (int i = 0; i <= progressBars.size(); i++) {
                printStream.print("\033[A\033[K");
            }
        }
    }

    /**
     * Prints the current state of all progress bars to the console.
     * This method iterates through all progress bars and prints their current progress.
     */
    public void printProgressDisplay() {
        printStream.println("Code generation progress:");
        for (ProgressBar progressBar : progressBars.values()) {
            printStream.println(progressBar.getCurrentProgressBar());
        }
    }

    /**
     * Prints verbose information messages, including the file name if set.
     *
     * @param fileName the name of the file associated with the message
     * @param message the message to log
     */
    public void printVerboseInfo(String fileName, String message) {
        if (isVerboseEnabled) {
            if (isProgressBarActive()) {
                clearProgressDisplay();
                getVerboseLogger(fileName).printVerboseInfo(message);
                printProgressDisplay();
            } else {
                getVerboseLogger(fileName).printVerboseInfo(message);
            }
        }
    }

    /**
     * Prints verbose debug messages, including the file name if set.
     *
     * @param fileName the name of the file associated with the message
     * @param message the message to log
     */
    public void printVerboseDebug(String fileName, String message) {
        if (isVerboseEnabled) {
            if (isProgressBarActive()) {
                clearProgressDisplay();
                getVerboseLogger(fileName).printVerboseDebug(message);
                printProgressDisplay();
            } else {
                getVerboseLogger(fileName).printVerboseDebug(message);
            }
        }
    }

    /**
     * Prints verbose warning messages, including the file name if set.
     *
     * @param fileName the name of the file associated with the message
     * @param message the message to log
     */
    public void printVerboseWarn(String fileName, String message) {
        if (isVerboseEnabled) {
            if (isProgressBarActive()) {
                clearProgressDisplay();
                getVerboseLogger(fileName).printVerboseWarn(message);
                printProgressDisplay();
            } else {
                getVerboseLogger(fileName).printVerboseWarn(message);
            }
        }
    }

    /**
     * Prints verbose error messages, including the file name if set.
     *
     * @param fileName the name of the file associated with the message
     * @param message the message to log
     */
    public void printVerboseError(String fileName, String message) {
        if (isVerboseEnabled) {
            if (isProgressBarActive()) {
                clearProgressDisplay();
                getVerboseLogger(fileName).printVerboseError(message);
                printProgressDisplay();
            } else {
                getVerboseLogger(fileName).printVerboseError(message);
            }
        }
    }

    /**
     * Prints general information messages, including the file name if set.
     * If a progress bar is active, it clears the display before printing.
     *
     * @param fileName the name of the file associated with the message
     * @param message the message to log
     */
    public void printInfo(String fileName, String message) {
        if (isProgressBarActive()) {
            clearProgressDisplay();
            getVerboseLogger(fileName).printInfo(message);
            printProgressDisplay();
        } else {
            getVerboseLogger(fileName).printInfo(message);
        }
    }

    /**
     * Prints debug messages, including the file name if set.
     * If a progress bar is active, it clears the display before printing.
     *
     * @param fileName the name of the file associated with the message
     * @param message the message to log
     */
    public void printDebug(String fileName, String message) {
        if (isProgressBarActive()) {
            clearProgressDisplay();
            getVerboseLogger(fileName).printDebug(message);
            printProgressDisplay();
        } else {
            getVerboseLogger(fileName).printDebug(message);
        }
    }

    /**
     * Prints warning messages, including the file name if set.
     * If a progress bar is active, it clears the display before printing.
     *
     * @param fileName the name of the file associated with the message
     * @param message the message to log
     */
    public void printWarn(String fileName, String message) {
        if (isProgressBarActive()) {
            clearProgressDisplay();
            getVerboseLogger(fileName).printWarn(message);
            printProgressDisplay();
        } else {
            getVerboseLogger(fileName).printWarn(message);
        }
    }

    /**
     * Prints error messages, including the file name if set.
     * If a progress bar is active, it clears the display before printing.
     *
     * @param fileName the name of the file associated with the message
     * @param message the message to log
     */
    public void printError(String fileName, String message) {
        if (isProgressBarActive()) {
            clearProgressDisplay();
            getVerboseLogger(fileName).printError(message);
            printProgressDisplay();
        } else {
            getVerboseLogger(fileName).printError(message);
        }
    }

    /**
     * Prints the stack trace of an exception, including the file name if set.
     * If a progress bar is active, it clears the display before printing the stack trace.
     *
     * @param fileName the name of the file associated with the stack trace
     * @param stackTrace the stack trace elements to print
     */
    public void printStackTrace(String fileName, StackTraceElement[] stackTrace) {
        if (isProgressBarActive()) {
            clearProgressDisplay();
            getVerboseLogger(fileName).printStackTrace(stackTrace);
            printProgressDisplay();
        } else {
            getVerboseLogger(fileName).printStackTrace(stackTrace);
        }
    }
}
