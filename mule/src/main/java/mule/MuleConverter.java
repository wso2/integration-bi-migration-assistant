/*
 *  Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com). All Rights Reserved.
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

package mule;

import common.BallerinaModel;
import common.CodeGenerator;
import io.ballerina.cli.cmd.NewCommand;
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import mule.dataweave.converter.DWConversionStats;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import static mule.HtmlReportWriter.writeHtmlReport;
import static mule.MuleToBalConverter.convertProjectXMLFileToBallerina;
import static mule.MuleToBalConverter.createBallerinaModel;
import static mule.MuleToBalConverter.createContextInfoHoldingDataStructures;

public class MuleConverter {
    public static final String MULE_DEFAULT_APP_DIR_NAME = "app";
    public static final String BAL_PROJECT_SUFFIX = "-ballerina";
    public static final String MIGRATION_REPORT_NAME = "migration_summary.html";
    private static final PrintStream OUT = System.out;
    private static final Logger logger = Logger.getLogger(MuleConverter.class.getName());

    public static void migrateMuleProject(String[] args) {
        if (args.length < 1) {
            logger.severe("Usage: java -jar mule-migration-assistant.jar <source-project-directory-or-file> " +
                    "[-o|--out <output-directory>]");
            System.exit(1);
        }
        String inputPathArg = args[0];
        String outputPathArg = null;
        if (args.length >= 3 && (args[1].equals("-o") || args[1].equals("--out"))) {
            outputPathArg = args[2];
        }

        boolean standaloneFile = inputPathArg.endsWith(".xml");
        if (standaloneFile) {
            convertMuleXmlFile(inputPathArg, outputPathArg);
        } else {
            convertMuleProject(inputPathArg, outputPathArg);
        }
    }

    public static void convertMuleXmlFile(String inputPathArg, String outputPathArg) {
        Path inputPath = Paths.get(inputPathArg);
        String inputFileName = inputPath.getFileName().toString().split(".xml")[0];
        String balPackageName = inputFileName.concat(BAL_PROJECT_SUFFIX);

        Path balPackageDir;
        if (outputPathArg != null) {
            balPackageDir = Path.of(outputPathArg).resolve(balPackageName);
        } else {
            Path parent = inputPath.getParent();
            if (parent == null) {
                // e.g. bar.xml
                balPackageDir = Path.of(balPackageName);
            } else {
                // e.g foo/bar.xml
                balPackageDir = parent.resolve(balPackageName);
            }
        }
        convertMuleXmlFile(balPackageDir, inputFileName, inputPathArg);
    }

    private static void convertMuleXmlFile(Path balPackageDir, String inputFileName, String inputXmlFilePath) {
        MuleXMLNavigator muleXMLNavigator = new MuleXMLNavigator();
        MuleToBalConverter.SharedProjectData sharedProjectData = new MuleToBalConverter.SharedProjectData(
                muleXMLNavigator);

        String targetBalFile = inputFileName.concat(".bal");
        SyntaxTree syntaxTree;
        try {
            syntaxTree = convertProjectXMLFileToBallerina(muleXMLNavigator, sharedProjectData, inputXmlFilePath);
        } catch (Exception e) {
            logger.severe("Error converting the file: %s%n%s".formatted(inputFileName.concat(".xml"), e.getMessage()));
            return;
        }

        // TODO: merge repeated logic with convertMuleProject()
        // create ballerina project
        String[] args = {balPackageDir.toString()};
        NewCommand newCommand = new NewCommand(System.out, false);
        new CommandLine(newCommand).parseArgs(args);
        newCommand.execute();

        Path targetBalFilePath = balPackageDir.resolve(targetBalFile);
        try {
            Files.writeString(targetBalFilePath, syntaxTree.toSourceCode());
        } catch (Exception e) {
            logger.severe("Error writing to file: " + e.getMessage());
            System.exit(1);
        }

        genAndWriteInternalTypesBalFile(sharedProjectData, balPackageDir.toString());
        Path reportFilePath = Paths.get(balPackageDir.toString(), MIGRATION_REPORT_NAME);
        int conversionPercentage = writeHtmlReport(logger, reportFilePath,
                muleXMLNavigator.getXmlCompatibleTagCountMap(), muleXMLNavigator.getXmlIncompatibleTagCountMap(),
                muleXMLNavigator.getDwConversionStats());
        printDataWeaveConversionPercentage(muleXMLNavigator);
        printOverallProjectConversionPercentage(conversionPercentage);
    }

    public static void convertMuleProject(String inputPathArg, String outputPathArg) {
        Path inputPath = Paths.get(inputPathArg);
        String balPackageName = inputPath.getFileName().toString().concat(BAL_PROJECT_SUFFIX);

        Path balPackageDir;
        if (outputPathArg != null) {
            balPackageDir = Path.of(outputPathArg).resolve(balPackageName);
        } else {
            balPackageDir = inputPath.resolve(balPackageName);
        }
        convertMuleProject(balPackageDir, inputPathArg);
    }

    public static void convertMuleProject(Path balPackageDir, String inputPathArg) {
        // create ballerina project
        String[] args = { balPackageDir.toString() };
        NewCommand newCommand = new NewCommand(System.out, false);
        new CommandLine(newCommand).parseArgs(args);
        newCommand.execute();

        Path sourceFolderPath = Path.of(inputPathArg).resolve("src").resolve("main").resolve(MULE_DEFAULT_APP_DIR_NAME);
        String targetFolderPath = balPackageDir.toString();

        List<File> xmlFiles = new ArrayList<>();
        collectXmlFiles(sourceFolderPath.toFile(), xmlFiles);

        if (xmlFiles.isEmpty()) {
            logger.severe("No XML files found in the directory: " + sourceFolderPath);
            System.exit(1);
        }

        MuleXMLNavigator muleXMLNavigator = new MuleXMLNavigator();
        MuleToBalConverter.SharedProjectData sharedProjectData = new MuleToBalConverter.SharedProjectData(
                muleXMLNavigator);
        for (File xmlFile : xmlFiles) {
            Path relativePath = sourceFolderPath.relativize(xmlFile.toPath());
            String balFileName = relativePath.toString().replace(File.separator, ".").replace(".xml", ".bal");
            Path targetFilePath = Paths.get(targetFolderPath, balFileName);
            createDirectories(targetFilePath.getParent());

            genAndWriteBalFileFromXMLFile(xmlFile, muleXMLNavigator, sharedProjectData, targetFilePath);
        }

        genAndWriteInternalTypesBalFile(sharedProjectData, targetFolderPath);

        Path reportFilePath = Paths.get(targetFolderPath, MIGRATION_REPORT_NAME);
        int conversionPercentage = writeHtmlReport(logger, reportFilePath,
                muleXMLNavigator.getXmlCompatibleTagCountMap(), muleXMLNavigator.getXmlIncompatibleTagCountMap(),
                muleXMLNavigator.getDwConversionStats());
        printDataWeaveConversionPercentage(muleXMLNavigator);
        printOverallProjectConversionPercentage(conversionPercentage);
    }

    /**
     * Generate and write the Ballerina file from the XML file.
     *
     * @param xmlFile           xml file to be converted
     * @param muleXMLNavigator  MuleXMLNavigator instance to navigate the XML file
     * @param sharedProjectData shared project data
     * @param targetFilePath    path to the target file where the Ballerina code
     *                          will be written
     */
    private static void genAndWriteBalFileFromXMLFile(
            File xmlFile, MuleXMLNavigator muleXMLNavigator, MuleToBalConverter.SharedProjectData sharedProjectData,
            Path targetFilePath) {
        SyntaxTree syntaxTree;
        try {
            syntaxTree = convertProjectXMLFileToBallerina(muleXMLNavigator, sharedProjectData, xmlFile.getPath());
        } catch (Exception e) {
            logger.severe(String.format("Error converting the file: %s%n%s", xmlFile.getName(), e.getMessage()));
            return;
        }

        try {
            Files.writeString(targetFilePath, syntaxTree.toSourceCode());
        } catch (IOException e) {
            logger.severe("Error writing to file: " + e.getMessage());
        }
    }

    /**
     * Generate and write the internal-types.bal file.
     *
     * @param sharedProjectData shared project data containing context type
     *                          definitions and imports
     * @param targetFolderPath  path to the target folder where the
     *                          internal-types.bal file will be created
     */
    private static void genAndWriteInternalTypesBalFile(MuleToBalConverter.SharedProjectData sharedProjectData,
            String targetFolderPath) {
        createContextInfoHoldingDataStructures(sharedProjectData);

        Path targetFilePath = Paths.get(targetFolderPath, "internal-types.bal");
        BallerinaModel ballerinaModel = createBallerinaModel(sharedProjectData.contextTypeDefImports.stream().toList(),
                sharedProjectData.contextTypeDefMap.values().stream().toList(), Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        SyntaxTree syntaxTree = new CodeGenerator(ballerinaModel).generateBalCode();
        try {
            Files.writeString(targetFilePath, syntaxTree.toSourceCode());
        } catch (IOException e) {
            logger.severe("Error writing to file: " + e.getMessage());
        }
    }

    private static void printDataWeaveConversionPercentage(MuleXMLNavigator muleXMLNavigator) {
        DWConversionStats stats = muleXMLNavigator.getDwConversionStats();
        OUT.println("________________________________________________________________");
        OUT.println("Dataweave conversion percentage: " + String.format("%.2f", stats.getConversionPercentage()) + "%");
        OUT.println("________________________________________________________________");
    }

    private static void printOverallProjectConversionPercentage(int conversionPercentage) {
        OUT.println("________________________________________________________________");
        OUT.println("Overall project conversion percentage: " + conversionPercentage + "%");
        OUT.println("________________________________________________________________");
    }

    private static void collectXmlFiles(File folder, List<File> xmlFiles) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    collectXmlFiles(file, xmlFiles);
                } else if (file.getName().toLowerCase().endsWith(".xml")) {
                    xmlFiles.add(file);
                }
            }
        }
    }

    private static void createDirectories(Path path) {
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                logger.severe("Error creating directories: " + path);
            }
        }
    }
}
