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
package baltool.mirth.codegenerator;

/**
 * Represents a progress bar for tracking the progress of tasks.
 * It displays the current step, total steps, and a message.
 */
public class ProgressBar {
    private String currentProgressBar;
    private final int totalSteps;
    private final String fileName;
    private boolean active = false;

    public ProgressBar(int steps, String fileName) {
        this.totalSteps = steps;
        this.fileName = fileName;
        this.currentProgressBar = String.format("\r%-30s %s", getFileName(), "Not Started");
    }

    /**
     * Returns the total number of steps for the progress bar.
     *
     * @return total steps
     */
    private int getTotalSteps() {
        return totalSteps;
    }

    /**
     * Returns the file name associated with the progress bar.
     *
     * @return file name
     */
    private String getFileName() {
        return fileName;
    }

    /**
     * Updates the progress bar with the current step and a message.
     *
     * @param currentStep the current step number
     * @param message     a message to display alongside the progress
     */
    public void updateProgress(int currentStep, String message) {
        setActive(true);
        String progressBar = createProgressBar(currentStep - 1);
        this.currentProgressBar = String.format("\r%-30s %s [%d/%d] %s", getFileName(), progressBar, currentStep,
                getTotalSteps(), message);
    }

    /**
     * Finishes the progress bar with a success or failure message.
     *
     * @param success  true if the task was successful, false otherwise
     * @param message  a message to display upon completion
     */
    public void finishProgress(boolean success, String message) {
        if (success) {
            this.currentProgressBar = String.format("%-30s ✓ %s", getFileName(), message);
        } else {
            this.currentProgressBar = String.format("%-30s ✗ %s", getFileName(), message);
        }
        setActive(false);
    }

    /**
     * Sets the active state of the progress bar.
     *
     * @param active true to set the progress bar as active, false otherwise
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Checks if the progress bar is currently active.
     *
     * @return true if active, false otherwise
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Returns the current progress bar string.
     *
     * @return the current progress bar representation
     */
    public String getCurrentProgressBar() {
        return currentProgressBar;
    }

    /**
     * Creates a visual representation of the progress bar.
     *
     * @param completed the number of completed steps
     * @return a string representing the progress bar
     */
    private String createProgressBar(int completed) {
        int barLength = 20;
        int filledLength = (int) ((double) completed / this.totalSteps * barLength);

        StringBuilder bar = new StringBuilder("[");
        for (int i = 0; i < barLength; i++) {
            if (i < filledLength) {
                bar.append("█");
            } else {
                bar.append("░");
            }
        }
        bar.append("]");

        return bar.toString();
    }
}
