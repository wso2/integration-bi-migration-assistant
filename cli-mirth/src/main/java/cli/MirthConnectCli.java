package cli;

import baltool.mirth.codegenerator.MirthChannelMigrationExecutor;

import java.nio.file.Path;
import java.util.logging.Logger;

public class MirthConnectCli {

    private static final Logger logger = Logger.getLogger(MirthConnectCli.class.getName());


    public static void main(String[] args) {
        Path sourceFile = Path.of(args[0]);
        // Default to empty path if not provided
        Path outputDirectory = args.length > 1 ? Path.of(args[1]) : Path.of("");

        MirthChannelMigrationExecutor.migrateChannelToBallerina(sourceFile, outputDirectory, false, null);
    }
}
