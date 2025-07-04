package baltool.logicapps.codegenerator;

import java.io.PrintStream;

public class VerboseLogger {
    private String fileName;
    private final boolean isVerboseEnabled;
    private static final PrintStream out = System.out;
    private static final PrintStream err = System.err;
    private ProgressBar progressBar;

    public VerboseLogger(boolean isVerboseEnabled) {
        this.isVerboseEnabled = isVerboseEnabled;
    }

    public VerboseLogger(boolean isVerboseEnabled, String processingFileName) {
        this.isVerboseEnabled = isVerboseEnabled;
        this.fileName = processingFileName;
    }

    public VerboseLogger(boolean isVerboseEnabled, String processingFileName, ProgressBar progressBar) {
        this.isVerboseEnabled = isVerboseEnabled;
        this.fileName = processingFileName;
        this.progressBar = progressBar;
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public ProgressBar getProgressBar() {
        return this.progressBar;
    }

    public void setProgressBarActive(boolean active) {
        if (isProgressBarActive() && !active) {
            clearProgressBar();
        }
        if (getProgressBar() != null) {
            getProgressBar().setActive(active);
        }
    }

    public boolean isProgressBarActive() {
        return getProgressBar() != null && getProgressBar().isActive();
    }

    public void updateProgressBar(int step, String message) {
        getProgressBar().updateProgress(step, message);
        if (isProgressBarActive()) {
            clearProgressBar();
        } else {
            setProgressBarActive(true);
        }
        printProgressBar();
    }

    public void clearProgressBar() {
        out.print("\r\033[K");
        out.print("\033[A\033[K");
    }

    public void printProgressBar() {
        out.println("Code generation progress:");
        out.print(getProgressBar().getCurrentProgressBar());
    }

    public void printVerboseInfo(String message) {
        if (isVerboseEnabled) {
            String log;
            if (fileName != null) {
                log = "INFO [FILENAME: " + fileName + "] " + message;
            } else {
                log = "INFO " + message;
            }
            if (isProgressBarActive()) {
                clearProgressBar();
                out.println(log);
                printProgressBar();
            } else {
                out.println(log);
            }
        }
    }

    public void printVerboseDebug(String message) {
        if (isVerboseEnabled) {
            if (fileName != null) {
                out.println("DEBUG [FILENAME: " + fileName + "] " + message);
            } else {
                out.println("DEBUG " + message);
            }
        }
    }

    public void printVerboseWarn(String message) {
        if (isVerboseEnabled) {
            if (fileName != null) {
                out.println("WARN [FILENAME: " + fileName + "] " + message);
            } else {
                out.println("WARN " + message);
            }
        }
    }

    public void printVerboseError(String message) {
        if (isVerboseEnabled) {
            if (fileName != null) {
                err.println("ERROR [FILENAME: " + fileName + "] " + message);
            } else {
                err.println("ERROR " + message);
            }
        }
    }

    public void printInfo(String message) {
        if (progressBar != null && progressBar.isActive()) {
            clearProgressBar();
            out.println(message);
            printProgressBar();
        } else {
            out.println(message);
        }
    }

    public void printError(String message) {
        err.println(message);
    }

    public PrintStream getPrintStream() {
        return out;
    }

    public PrintStream getErrorPrintStream() {
        return err;
    }
}
