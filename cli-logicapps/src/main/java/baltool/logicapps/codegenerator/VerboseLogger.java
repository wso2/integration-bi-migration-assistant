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

/**
 * VerboseLogger is a utility class for logging messages with different severity levels.
 * It supports verbose logging and can include a file name in the log messages.
 */
public class VerboseLogger {
    private String fileName;
    private final boolean isVerboseEnabled;
    private static final PrintStream out = System.out;
    private static final PrintStream err = System.err;

    public VerboseLogger(boolean isVerboseEnabled) {
        this.isVerboseEnabled = isVerboseEnabled;
    }

    public VerboseLogger(boolean isVerboseEnabled, String processingFileName) {
        this.isVerboseEnabled = isVerboseEnabled;
        this.fileName = processingFileName;
    }

    /**
     * Prints verbose information if verbose logging is enabled.
     *
     * @param message the message to log
     */
    public void printVerboseInfo(String message) {
        if (isVerboseEnabled) {
            if (fileName != null) {
                out.println("INFO [FILENAME: " + fileName + "] " + message);
            } else {
                out.println("INFO " + message);
            }
        }
    }

    /**
     * Prints verbose debug messages if verbose logging is enabled.
     *
     * @param message the message to log
     */
    public void printVerboseDebug(String message) {
        if (isVerboseEnabled) {
            if (fileName != null) {
                out.println("DEBUG [FILENAME: " + fileName + "] " + message);
            } else {
                out.println("DEBUG " + message);
            }
        }
    }

    /**
     * Prints verbose warning messages if verbose logging is enabled.
     *
     * @param message the message to log
     */
    public void printVerboseWarn(String message) {
        if (isVerboseEnabled) {
            if (fileName != null) {
                out.println("WARN [FILENAME: " + fileName + "] " + message);
            } else {
                out.println("WARN " + message);
            }
        }
    }

    /**
     * Prints verbose error messages if verbose logging is enabled.
     *
     * @param message the message to log
     */
    public void printVerboseError(String message) {
        if (isVerboseEnabled) {
            if (fileName != null) {
                err.println("ERROR [FILENAME: " + fileName + "] " + message);
            } else {
                err.println("ERROR " + message);
            }
        }
    }

    /**
     * Prints general information messages, including the file name if set.
     *
     * @param message the message to log
     */
    public void printInfo(String message) {
        if (fileName != null) {
            err.println("INFO [FILENAME: " + fileName + "] " + message);
        } else {
            err.println("INFO " + message);
        }
    }

    /**
     * Prints debug messages, including the file name if set.
     *
     * @param message the message to log
     */
    public void printDebug(String message) {
        if (fileName != null) {
            err.println("DEBUG [FILENAME: " + fileName + "] " + message);
        } else {
            err.println("DEBUG " + message);
        }
    }

    /**
     * Prints warning messages, including the file name if set.
     *
     * @param message the message to log
     */
    public void printWarn(String message) {
        if (fileName != null) {
            err.println("WARN [FILENAME: " + fileName + "] " + message);
        } else {
            err.println("WARN " + message);
        }
    }

    /**
     * Prints error messages, including the file name if set.
     *
     * @param message the message to log
     */
    public void printError(String message) {
        if (fileName != null) {
            err.println("ERROR [FILENAME: " + fileName + "] " + message);
        } else {
            err.println("ERROR " + message);
        }
    }

    /**
     * Prints the stack trace of an exception, including the file name if set.
     *
     * @param stackTrace the stack trace elements to print
     */
    public void printStackTrace(StackTraceElement[] stackTrace) {
        if (fileName != null) {
            err.println("ERROR [FILENAME: " + fileName + "]");
        } else {
            err.println("ERROR");
        }
        for (StackTraceElement stackTraceElement : stackTrace) {
            err.println("   " + stackTraceElement);
        }
    }

    /**
     * Returns the PrintStream for standard output.
     *
     * @return the PrintStream for standard output
     */
    public PrintStream getPrintStream() {
        return out;
    }

    /**
     * Returns the PrintStream for standard error output.
     *
     * @return the PrintStream for standard error output
     */
    public PrintStream getErrorPrintStream() {
        return err;
    }
}
