package baltool.logicapps.codegenerator;

/**
 * Methods to generate code at compile-time.
 *
 * @since 0.4.0
 */
@FunctionalInterface
public interface ProgressCallback {
    void updateProgress(int step, String message);
}
