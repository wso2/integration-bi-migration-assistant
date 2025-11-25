package cli;

import baltool.mirth.codegenerator.MirthChannelMigrationExecutor;
import baltool.mirth.codegenerator.VerboseLogger;

import java.nio.file.Path;
import java.util.logging.Logger;

public class MirthConnectCli {

    private static final Logger logger = Logger.getLogger(MirthConnectCli.class.getName());


    // For testing jar
    public static void main(String[] args) {

        if (args.length < 1 || args.length > 3) {
            logger.severe("Usage: java -jar mirthconnect-migration-assistant.jar <source-file> " +
                    "[-o|--out <output-directory>] [-v|--verbose]");
            System.exit(1);
        }
        Path sourceFile = Path.of(args[0]);
        // Default to empty path if not provided
        Path outputDirectory = args.length > 1 ? Path.of(args[1]) : Path.of("");

        MirthChannelMigrationExecutor.migrateChannelToBallerina(sourceFile, outputDirectory, "",
                false, new VerboseLogger(false));
    }
}
