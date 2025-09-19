package cli;

import baltool.mirth.codegenerator.MirthChannelMigrationExecutor;
import baltool.mirth.codegenerator.VerboseLogger;

import java.nio.file.Path;
import java.util.logging.Logger;

public class MirthConnectCli {

    private static final Logger logger = Logger.getLogger(MirthConnectCli.class.getName());


    public static void main(String[] args) {
        Path sourceFile = Path.of(args[0]);

//        Path sourceFile = Path.of("/Users/isurus/wso2/integration-bi-migration-assistant/cli-mirth/src/main/resources/Hl7_Conversion.xml");
        // Default to empty path if not provided
        Path outputDirectory = args.length > 1 ? Path.of(args[1]) : Path.of("");
//        Path outputDirectory = Path.of("/Users/isurus/wso2/integration-bi-migration-assistant/cli-mirth/src/main/resources/output");

        MirthChannelMigrationExecutor.migrateChannelToBallerina(sourceFile, outputDirectory, "",false, new VerboseLogger(false));
    }
}
