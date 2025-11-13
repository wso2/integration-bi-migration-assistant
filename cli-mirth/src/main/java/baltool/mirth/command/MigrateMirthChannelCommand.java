package baltool.mirth.command;

import baltool.mirth.codegenerator.MirthChannelMigrationExecutor;
import baltool.mirth.codegenerator.VerboseLogger;
import io.ballerina.cli.BLauncherCmd;
import picocli.CommandLine;

import java.io.PrintStream;
import java.nio.file.Path;

import static baltool.mirth.Constants.CMD_NAME;

/**
 * This class represents the "migrate-mirth-channel" bal tool command.
 *
 * @since 1.0.0
 */
@CommandLine.Command(
        name = CMD_NAME,
        description = "Accepts a Mirth Connect channel `.xml` file path as input"
)
public class MigrateMirthChannelCommand implements BLauncherCmd {

    private final PrintStream errStream;
    private final PrintStream outStream;
    private static final String USAGE = "bal migrate-mirthchannel <source-channel-file> " +
            "[-v|--verbose] [-o|--out <output-directory>]";

    public MigrateMirthChannelCommand() {
        errStream = System.err;
        outStream = System.out;
    }

    @CommandLine.Parameters(description = "Source Mirth Connect channel `.xml` file path",
            arity = "0..1")
    private String sourcePath;

    @CommandLine.Option(names = {"--verbose", "-v"}, description = "Enable verbose output", defaultValue = "false")
    private boolean verbose;

    @CommandLine.Option(names = {"--out", "-o"}, description = "Output directory path")
    private String outputPath;

    @Override
    public void execute() {
        try {
            if (verbose) {
                outStream.println("Starting Mirth Channel Migration Tool");
                outStream.println("Command line arguments:");
                outStream.println("  Source path: " + (sourcePath != null ? sourcePath : "Not provided"));
                outStream.println("  Output path: " + (outputPath != null ? outputPath : "Default (source directory)"));
                outStream.println();
            }

            if (sourcePath == null) {
                errStream.println("Error: Mirth Connect channel XML file path is required.");
                if (verbose) {
                    errStream.println("DEBUG: sourcePath parameter was null");
                }
                onInvalidInput();
                return;
            }

            if (verbose) {
                outStream.println("Validating input parameters");
                outStream.println("  Checking if source path exists: " + sourcePath);
            }

            Path channelFilePath = Path.of(sourcePath).toAbsolutePath();

            if (verbose) {
                outStream.println("  Resolved source path: " + channelFilePath.toAbsolutePath());
                outStream.println();
            }

            if (outputPath != null) {
                Path outputDir = Path.of(outputPath).toAbsolutePath();
                MirthChannelMigrationExecutor.migrateChannelToBallerina(channelFilePath, outputDir,
                        "", verbose, new VerboseLogger(verbose));
            } else {
                MirthChannelMigrationExecutor.migrateChannelToBallerina(channelFilePath, verbose,
                        new VerboseLogger(verbose));
            }
        } catch (Exception e) {
            errStream.println("Error during command execution: " + e.getMessage());
            if (verbose) {
                errStream.println();
                errStream.println("=".repeat(60));
                errStream.println("DETAILED ERROR INFORMATION:");
                errStream.println("=".repeat(60));
                errStream.println("Exception type: " + e.getClass().getSimpleName());
                errStream.println("Error message: " + e.getMessage());
                errStream.println();
                errStream.println("Stack trace:");
                e.printStackTrace(errStream);
                errStream.println("=".repeat(60));
            }
            System.exit(1);
        }
    }

    private void onInvalidInput() {
        errStream.println("Usage: " + USAGE);
        if (verbose) {
            errStream.println();
            errStream.println("DEBUG: Invalid input detected, terminating with exit code 1");
        }
        System.exit(1);
    }

    @Override
    public String getName() {
        return CMD_NAME;
    }

    @Override
    public void printLongDesc(StringBuilder stringBuilder) {
        stringBuilder.append("Migrate Mirth Connect channel to Ballerina Integrator\n\n");
        stringBuilder.append("This command accepts a Mirth Connect channel `.xml` file path\n");
        stringBuilder.append("as input and generates equivalent Ballerina code" +
                " that can be opened in Ballerina Integrator.\n\n");
        stringBuilder.append("Optional flags:\n");
        stringBuilder.append("  --out, -o               Specify the output directory for the generated Ballerina " +
                "project\n");
        stringBuilder.append("  --verbose, -v           Enable detailed logging output\n");
    }

    @Override
    public void printUsage(StringBuilder stringBuilder) {
        stringBuilder.append(USAGE).append("\n\n");
        stringBuilder.append("Examples:\n");
        stringBuilder.append("  bal migrate-mirth-channel /path/to/channel-xml-file\n");
        stringBuilder.append("  bal migrate-mirth-channel /path/to/channel-xml-file --out /path/to/output\n");
        stringBuilder.append("  bal migrate-mirth-channel /path/to/channel-xml-file --verbose\n");
    }

    @Override
    public void setParentCmdParser(CommandLine commandLine) {
    }
}
