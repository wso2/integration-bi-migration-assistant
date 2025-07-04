package baltool.logicapps.codegenerator;

public class ProgressBar {
    private String currentProgressBar;
    private final int totalSteps;
    private final String fileName;
    private boolean active = false;

    public ProgressBar(int steps, String fileName) {
        this.totalSteps = steps;
        this.fileName = fileName;
    }

    private int getTotalSteps() {
        return this.totalSteps;
    }

    private String getFileName() {
        return this.fileName;
    }

    public void updateProgress(int currentStep, String message) {
        String progressBar = createProgressBar(currentStep - 1);
        this.currentProgressBar = String.format("\r%-30s %s [%d/%d] %s", getFileName(), progressBar, currentStep,
                getTotalSteps(), message);
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return this.active;
    }

    public String getCurrentProgressBar() {
        return this.currentProgressBar;
    }

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
