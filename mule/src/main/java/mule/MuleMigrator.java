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
package mule;

import common.BICodeConverter;
import common.BallerinaModel;
import common.CodeGenerator;
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import mule.common.ContextBase;
import mule.common.DWConstructBase;
import mule.common.DWConversionStats;
import mule.common.MigrationMetrics;
import mule.common.MuleXMLNavigator;
import mule.common.report.ProjectMigrationSummary;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static common.BallerinaModel.Import;
import static common.BallerinaModel.ModuleTypeDef;
import static common.BallerinaModel.TextDocument;
import static mule.MigratorUtils.collectXmlFiles;
import static mule.MigratorUtils.collectYamlAndPropertyFiles;
import static mule.MigratorUtils.createDirectories;
import static mule.MigratorUtils.getImmediateSubdirectories;
import static mule.common.report.AggregateReportWriter.AGGREGATE_MIGRATION_REPORT_NAME;
import static mule.common.report.AggregateReportWriter.genAndWriteAggregateReport;
import static mule.common.report.MigrationReportWriter.genAndWriteMigrationReport;
import static mule.common.report.MigrationReportWriter.getProjectMigrationSummary;
import static mule.v3.MuleToBalConverter.convertXMLFileToBir;
import static mule.v4.MuleToBalConverter.convertXMLFileToBir;
import static mule.v3.MuleToBalConverter.createContextTypeDefns;
import static mule.v4.MuleToBalConverter.createContextTypeDefns;
import static mule.v4.MuleToBalConverter.createTextDocument;

public class MuleMigrator {

    public static final String BAL_PROJECT_SUFFIX = "_ballerina";
    public static final String INTERNAL_TYPES_FILE_NAME = "internal_types.bal";
    public static final String MULE_V3_DEFAULT_XML_CONFIGS_DIR_NAME = "app";
    public static final String MULE_V4_DEFAULT_XML_CONFIGS_DIR_NAME = "mule";

    public enum MuleVersion {
        MULE_V3(3), MULE_V4(4);

        private final int version;

        MuleVersion(int version) {
            this.version = version;
        }

        public static MuleVersion fromInt(int version) {
            for (MuleVersion ver : values()) {
                if (ver.version == (version)) {
                    return ver;
                }
            }
            throw new IllegalArgumentException("Undeclared Mule version: " + version);
        }
    }

    private static Logger logger;
    private static final PrintStream OUT = System.out;

    // TODO: add new version option
    public static void migrateMuleSource(String inputPathArg, String outputPathArg, Integer muleVersion,
                                         boolean dryRun, boolean verbose, boolean keepStructure, boolean multiRoot) {
        logger = verbose ? createDefaultLogger("migrate-mule") : createSilentLogger("migrate-mule");
        logger().info("migrate-mule tool initialized with --dry-run =" + dryRun + ", --verbose = " + verbose +
                ", --keep-structure = " + keepStructure + ", --multi-root = " + multiRoot);
        Path sourcePath = Paths.get(inputPathArg);
        if (!Files.exists(sourcePath)) {
            logger().severe("Source path does not exist: '" + sourcePath + "'");
            System.exit(1);
        }

        // TODO: handle migrate-mule pathToProjects missing -m flag scenario
        if (multiRoot) {
            logger().info("Multi-root mode enabled. Converting all Mule projects in the directory: '" +
                    sourcePath + "'");
            if (!Files.isDirectory(sourcePath)) {
                logger().severe("Multi-root mode requires a directory as input, but got a file: '" + sourcePath + "'");
                System.exit(1);
            }
            validateOutputPathArg(outputPathArg);
            validateMuleVersionArg(muleVersion);
            convertMuleMultiProjects(inputPathArg, outputPathArg, muleVersion, dryRun, keepStructure);
        } else if (Files.isDirectory(sourcePath)) {
            logger().info("Source path is a Mule project directory: '" + sourcePath + "'");
            validateOutputPathArg(outputPathArg);
            validateMuleVersionArg(muleVersion);
            convertMuleProject(inputPathArg, outputPathArg, muleVersion, dryRun, keepStructure, false);
        } else if (Files.isRegularFile(sourcePath) && inputPathArg.endsWith(".xml")) {
            logger().info("Source path is a Mule XML file: '" + sourcePath + "'");
            validateOutputPathArg(outputPathArg);
            validateMuleVersionArg(muleVersion);
            convertMuleXmlFile(inputPathArg, outputPathArg, muleVersion, dryRun, keepStructure);
        } else {
            logger().severe("Invalid source path: '" + sourcePath + "'. Must be a directory or .xml file.");
            System.exit(1);
        }
    }

    public static Logger logger() {
        return logger;
    }

    public static Logger createSilentLogger(String name) {
        Logger silentLogger = Logger.getLogger(name);
        silentLogger.setFilter(record -> record.getLevel().intValue() >= java.util.logging.Level.SEVERE.intValue());
        return silentLogger;
    }

    public static Logger createDefaultLogger(String name) {
        return Logger.getLogger(name);
    }

    private static void validateMuleVersionArg(Integer muleVersion) {
        if (muleVersion == null) {
            logger().info("No Mule version specified. Tool will automatically detect the Mule version.");
            return;
        }
        logger().info("Validating Mule version argument: " + muleVersion);
        if (muleVersion != 3 && muleVersion != 4) {
            logger().severe("Invalid Mule version specified: " + muleVersion + ". Must be 3 or 4.");
            System.exit(1);
        }
    }

    private static void validateOutputPathArg(String outputPathArg) {
        if (outputPathArg != null) {
            logger().info("Validating output path argument: '" + outputPathArg + "'");
            Path outputPath = Paths.get(outputPathArg);
            if (!Files.exists(outputPath)) {
                try {
                    Files.createDirectories(outputPath);
                    logger().info("Created output directory: " + outputPath);
                } catch (IOException e) {
                    logger().severe("Cannot create output directory: " + outputPath + " - " + e.getMessage());
                    System.exit(1);
                }
            } else if (!Files.isDirectory(outputPath)) {
                logger().severe("Output path exists but is not a directory: " + outputPath);
                System.exit(1);
            }
        }
    }

    private static void convertMuleMultiProjects(String sourceProjectsDir, String outputPathArg, Integer muleVersion,
                                                 boolean dryRun, boolean keepStructure) {
        Path sourceProjectsDirPath = Path.of(sourceProjectsDir);
        List<Path> projectDirectories;
        try {
            projectDirectories = getImmediateSubdirectories(sourceProjectsDirPath);
        } catch (IOException e) {
            logger().severe("Error listing subdirectories of " + sourceProjectsDir + ": " + e.getMessage());
            return;
        }

        List<ProjectMigrationSummary> projectSummaries = new ArrayList<>();
        for (Path projectDir : projectDirectories) {
            logger().info("Converting Mule project: " + projectDir);
            try {
                ProjectMigrationSummary projSummary = convertMuleProject(projectDir.toString(), outputPathArg,
                        muleVersion, dryRun, keepStructure, true);
                projectSummaries.add(projSummary);
            } catch (Exception e) {
                logger().severe("Error converting Mule project " + projectDir + ": " + e.getMessage());
            }
        }

        Path targetDir = outputPathArg != null ? Path.of(outputPathArg) : sourceProjectsDirPath;
        genAndWriteAggregateReport(projectSummaries, targetDir, dryRun);
        printMultiRootCompletion(targetDir.resolve(AGGREGATE_MIGRATION_REPORT_NAME), dryRun);
    }

    private static ProjectMigrationSummary convertMuleProject(String inputPathArg, String outputPathArg,
                                                              Integer muleVersion, boolean dryRun,
                                                              boolean keepStructure, boolean multiRoot) {
        // Detecting Mule project version
        Path sourcePath = Path.of(inputPathArg);
        MuleVersion version = muleVersion == null ?
                MigratorUtils.detectVersionForProject(sourcePath) : MuleVersion.fromInt(muleVersion);

        // Collect xml configs, yaml and property files
        logger().info("Collecting XML configs, YAML, and property files in Mule project...");
        List<File> xmlFiles = new ArrayList<>();

        Path muleXmlConfigDir = sourcePath.resolve("src").resolve("main");
        if (version.equals(MuleVersion.MULE_V3)) {
            logger().info("Detected Mule version: MULE_V3");
            muleXmlConfigDir = muleXmlConfigDir.resolve(MULE_V3_DEFAULT_XML_CONFIGS_DIR_NAME);
        } else {
            logger().info("Detected Mule version: MULE_V4");
            muleXmlConfigDir = muleXmlConfigDir.resolve(MULE_V4_DEFAULT_XML_CONFIGS_DIR_NAME);
        }
        collectXmlFiles(muleXmlConfigDir.toFile(), xmlFiles);

        List<File> yamlFiles = new ArrayList<>();
        List<File> propertyFiles = new ArrayList<>();
        Path muleResourcesDir = sourcePath.resolve("src").resolve("main").resolve("resources");
        Path propFileLocation = version.equals(MuleVersion.MULE_V3) ? muleXmlConfigDir : muleResourcesDir;
        collectYamlAndPropertyFiles(propFileLocation.toFile(), yamlFiles, propertyFiles);

        logger().info("Found " + xmlFiles.size() + " .xml files, " + yamlFiles.size() + ".yaml files, and " +
                propertyFiles.size() + " .properties files.");

        if (xmlFiles.isEmpty()) {
            logger().severe("No XML files found in the directory: " + muleXmlConfigDir);
            System.exit(1);
        }

        String sourceProjectName = sourcePath.getFileName().toString();
        Path targetDir = outputPathArg != null ? Path.of(outputPathArg) : sourcePath;
        return convertToBalProject(version, xmlFiles, yamlFiles, propertyFiles, muleXmlConfigDir, targetDir,
                sourceProjectName, dryRun, keepStructure, multiRoot);
    }

    private static void convertMuleXmlFile(String inputPathArg, String outputPathArg, Integer muleVersion,
                                           boolean dryRun,
                                           boolean keepStructure) {
        Path inputXmlFilePath = Path.of(inputPathArg);
        String inputFileName = inputXmlFilePath.getFileName().toString().split(".xml")[0];
        Path sourceDir = inputXmlFilePath.getParent() != null ? inputXmlFilePath.getParent() : Path.of(".");

        Path targetDir;
        if (outputPathArg != null) {
            targetDir = Path.of(outputPathArg);
        } else {
            targetDir = sourceDir;
        }

        // TODO: do we need to verify path exists?
        MuleVersion version = muleVersion == null ?
                MigratorUtils.detectVersionForFile(inputXmlFilePath) : MuleVersion.fromInt(muleVersion);
        File xmlConfigFile = inputXmlFilePath.toFile();
        convertToBalProject(version, Collections.singletonList(xmlConfigFile), Collections.emptyList(),
                Collections.emptyList(), sourceDir, targetDir, inputFileName, dryRun, keepStructure, false);
    }

    private static ContextBase getContext(MuleVersion muleVersion) {
        if (muleVersion == MuleVersion.MULE_V3) {
            return new mule.v3.Context();
        } else if (muleVersion == MuleVersion.MULE_V4) {
            return new mule.v4.Context();
        } else {
            throw new IllegalArgumentException("Unsupported Mule version: " + muleVersion);
        }
    }

    private static MuleXMLNavigator getXMLNavigator(ContextBase contextBase) {
        if (contextBase instanceof mule.v3.Context v3Context) {
            return new MuleXMLNavigator(v3Context.migrationMetrics, mule.v3.model.MuleXMLTag::isCompatible);
        } else if (contextBase instanceof mule.v4.Context v4Context) {
            return new MuleXMLNavigator(v4Context.migrationMetrics, mule.v4.model.MuleXMLTag::isCompatible);
        } else {
            throw new IllegalArgumentException("Unsupported context type: " + contextBase.getClass().getName());
        }
    }

    private static ProjectMigrationSummary convertToBalProject(MuleVersion muleVersion, List<File> xmlFiles,
                                                               List<File> yamlFiles, List<File> propertyFiles,
                                                               Path muleAppDir, Path targetDir, String sourceName,
                                                               boolean dryRun, boolean keepStructure,
                                                               boolean multiRoot) {
        logger().info("Converting Mule XML configs to ballerina intermediate representation...");
        // 1. Convert xml configs to ballerina-ir
        ContextBase ctx = getContext(muleVersion);
        MuleXMLNavigator muleXMLNavigator = getXMLNavigator(ctx);
        List<TextDocument> birTxtDocs = new ArrayList<>(xmlFiles.size() + 1);
        for (File xmlFile : xmlFiles) {
            ctx.startNewFile(xmlFile.getPath());
            Path relativePath = muleAppDir.relativize(xmlFile.toPath());
            String balFileName = relativePath.toString().replace(File.separator, ".").replace(".xml", "");
            TextDocument birTextDoc = genBirFromXMLFile(ctx, muleXMLNavigator, xmlFile, balFileName);
            if (birTextDoc != null) {
                birTxtDocs.add(birTextDoc);
            }
        }
        logger().info("Converted " + birTxtDocs.size() + " XML files to Ballerina IR.");

        TextDocument birTxtDoc = genBirForInternalTypes(ctx);
        birTxtDocs.add(birTxtDoc);

        // 2. Generate and write migration report
        String balPackageName = sourceName + BAL_PROJECT_SUFFIX;
        Path balPackageDir = targetDir.resolve(balPackageName);
        createDirectories(balPackageDir);
        ProjectMigrationSummary projSummary = getProjectMigrationSummary(muleVersion, sourceName, balPackageName,
                balPackageDir, dryRun, ctx.getMigrationMetrics());
        genAndWriteMigrationReport(projSummary, muleVersion);

        if (dryRun) {
            if (!multiRoot) {
                printDryRunCompletion(projSummary.reportFilePath());
            }
            return projSummary;
        }

        // 3. Rearrange BIR for BI Structure
        if (!keepStructure) {
            logger().info("Re-arranging BIR files to fit Ballerina Integrator project structure...");
            birTxtDocs = new BICodeConverter().convert(new BallerinaModel.Module("mock", birTxtDocs)).textDocuments();
        }

        // 3. Write project
        writeProjectArtifacts(balPackageName, balPackageDir);
        writeBirAsBalFiles(birTxtDocs, balPackageDir);
        genAndWriteConfigTOMLFile(yamlFiles, propertyFiles, balPackageDir);

        // 4. Print conversion percentages
        if (!multiRoot) {
            printDataWeaveConversionPercentage(ctx.getMigrationMetrics());
            printOverallProjectConversionPercentage(projSummary.migrationCoverage());
        }

        return projSummary;
    }

    /**
     * Generate and write the Ballerina file from the XML file.
     *
     * @param ctx              Context instance
     * @param muleXMLNavigator MuleXMLNavigator instance to navigate the XML file
     * @param xmlFile          xml file to be converted
     * @param balFileName      name of the target Ballerina file (without .bal extension)
     * @return BalFile instance containing the .bal file information
     */
    private static TextDocument genBirFromXMLFile(ContextBase ctx, MuleXMLNavigator muleXMLNavigator,
                                                  File xmlFile, String balFileName) {
        logger().info("Converting XML file: " + xmlFile.getName());
        TextDocument birTextDocument = null;
        try {
            if (ctx instanceof mule.v3.Context v3Ctx) {
                birTextDocument = convertXMLFileToBir(v3Ctx, muleXMLNavigator, xmlFile.getPath(), balFileName);
            } else if (ctx instanceof mule.v4.Context v4Ctx) {
                birTextDocument = convertXMLFileToBir(v4Ctx, muleXMLNavigator, xmlFile.getPath(), balFileName);
            } else {
                throw new IllegalStateException("Unsupported context type: " + ctx.getClass().getName());
            }
        } catch (Exception e) {
            assert false;
            logger().severe(String.format("Error converting the file to ballerina intermediate representation: %s%n%s",
                    xmlFile.getName(), e.getMessage()));
        }
        return birTextDocument;
    }

    /**
     * Generates a BalFile for internal types.
     *
     * @param ctx Context instance
     */
    private static TextDocument genBirForInternalTypes(ContextBase ctx) {
        // TODO: do we need to consider multi-flow-multi-context scenario?

        logger().info("Generating BIR for context type definitions...");
        List<ModuleTypeDef> contextTypeDefns;
        List<Import> contextImports = new ArrayList<>(1);
        if (ctx instanceof mule.v3.Context v3Ctx) {
            contextTypeDefns = createContextTypeDefns(v3Ctx);
            if (!v3Ctx.projectCtx.inboundProperties.isEmpty()) {
                // TODO: at the moment only http provides 'attributes'
                contextImports.add(new Import("ballerina", "http"));
            }
        } else if (ctx instanceof mule.v4.Context v4Ctx) {
            contextTypeDefns = createContextTypeDefns(v4Ctx);
            if (!v4Ctx.projectCtx.attributes.isEmpty()) {
                // TODO: at the moment only http provides 'attributes'
                contextImports.add(new Import("ballerina", "http"));
            }
        } else {
            throw new IllegalStateException("Unsupported context type: " + ctx.getClass().getName());
        }

        return createTextDocument(INTERNAL_TYPES_FILE_NAME, contextImports, contextTypeDefns,
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList());
    }

    private static void writeBirAsBalFiles(List<TextDocument> birTxtDocs, Path balPackageDir) {
        logger().info("Generating syntax trees from BIR files and write them as .bal files...");
        for (TextDocument bir : birTxtDocs) {
            SyntaxTree syntaxTree;
            try {
                logger().info("Generating syntax tree for BIR file: " + bir.documentName());
                syntaxTree = new CodeGenerator(bir).generateSyntaxTree();
            } catch (Exception e) {
                logger().severe("Error generating syntax tree from BIR file: " + bir.documentName());
                continue;
            }
            Path filePath = balPackageDir.resolve(bir.documentName());

            try {
                logger().info("Writing bal file: " + bir.documentName());
                Files.writeString(filePath, syntaxTree.toSourceCode());
            } catch (IOException e) {
                logger().severe("Error writing to file: " + bir.documentName());
            }
        }
    }

    private static void writeProjectArtifacts(String balPackageName, Path balPackageDir)  {
        logger().info("Writing Ballerina project artifacts to: " + balPackageDir);
        String org = "migrate_mule";
        String version = "0.1.0";
        String distribution = "2201.12.3";

        Path tomlPath = balPackageDir.resolve("Ballerina.toml");
        String tomlContent = """
                [package]
                org = "%s"
                name = "%s"
                version = "%s"
                distribution = "%s"
                
                [build-options]
                observabilityIncluded = true
                """.formatted(org, balPackageName, version, distribution);

        try {
            Files.writeString(tomlPath, tomlContent);
            logger().info("Created Ballerina.toml file at: " + tomlPath);
        } catch (IOException e) {
            logger().severe("Error writing Ballerina.toml file: " + e.getMessage());
        }
    }

    private static void genAndWriteConfigTOMLFile(List<File> yamlFiles, List<File> propertyFiles,
                                                  Path targetFolderPath) {
        logger().info("Generating Config.toml file from .yaml and .properties files...");
        Path configPath = targetFolderPath.resolve("Config.toml");
        StringBuilder tomlContent = new StringBuilder();

        try {
            // Process .properties files
            for (File propFile : propertyFiles) {
                if (propFile.getName().equals("mule-deploy.properties")) {
                    // Skip mule-deploy.properties file
                    continue;
                }

                // Add file name as comment
                tomlContent.append("# Properties from ").append(propFile.getName()).append("\n");
                processPropertiesFile(propFile, tomlContent);
                tomlContent.append("\n");
            }

            // Process .yaml files using SnakeYAML
            for (File yamlFile : yamlFiles) {
                tomlContent.append("# Properties from ").append(yamlFile.getName()).append("\n");
                processYamlFile(yamlFile, tomlContent);
                tomlContent.append("\n");
            }

            Files.writeString(configPath, tomlContent.toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger().severe("Error creating Config.toml: " + e.getMessage());
        }
    }

    public static void processPropertiesFile(File propFile, StringBuilder tomlContent) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(propFile), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.trim().startsWith("#")) {
                    continue;
                }

                int equalsIndex = line.indexOf('=');
                if (equalsIndex > 0) {
                    String key = line.substring(0, equalsIndex).trim().replace('.', '_');
                    String value = line.substring(equalsIndex + 1).trim();
                    tomlContent.append(key).append(" = \"").append(escapeTomlValue(value)).append("\"\n");
                }
            }
        }
    }

    private static String escapeTomlValue(String value) {
        return value.replace("\"", "\\\"").replace("\\", "\\\\");
    }

    public static void processYamlFile(File yamlFile, StringBuilder tomlContent) {
        try {
            Yaml yaml = new Yaml();
            try (FileInputStream inputStream = new FileInputStream(yamlFile)) {
                Object data = yaml.load(inputStream);
                if (data instanceof Map) {
                    flattenYamlToToml((Map<String, Object>) data, "", tomlContent);
                }
            }
        } catch (Exception e) {
            logger().severe("Error processing YAML file " + yamlFile.getName() + ": " + e.getMessage());
        }
    }

    static void flattenYamlToToml(Map<String, Object> map, String prefix, StringBuilder tomlContent) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey().replace('-', '_');
            String fullKey = prefix.isEmpty() ? key : prefix + "_" + key;
            Object value = entry.getValue();

            if (value instanceof Map) {
                flattenYamlToToml((Map<String, Object>) value, fullKey, tomlContent);
            } else if (value instanceof List<?> list) {
                for (int i = 0; i < list.size(); i++) {
                    Object listItem = list.get(i);
                    if (listItem instanceof Map) {
                        flattenYamlToToml((Map<String, Object>) listItem, fullKey + "_" + i, tomlContent);
                    } else {
                        String listKey = fullKey + "_" + i;
                        String listValue = escapeTomlValue(String.valueOf(listItem));
                        tomlContent.append(listKey).append(" = \"").append(listValue).append("\"\n");
                    }
                }
            } else {
                String tomlValue = escapeTomlValue(String.valueOf(value));
                tomlContent.append(fullKey).append(" = \"").append(tomlValue).append("\"\n");
            }
        }
    }

    private static void printMultiRootCompletion(Path reportFilePath, boolean dryRun) {
        OUT.println("________________________________________________________________");
        String task = dryRun ? "assessment" : "conversion";
        OUT.printf("Multi root project %s completed. Migration report written to %s%n", task, reportFilePath);
        OUT.println("________________________________________________________________");
    }

    private static void printDryRunCompletion(Path reportFilePath) {
        OUT.println("________________________________________________________________");
        OUT.println("Dry run completed. Migration assessment report written to " + reportFilePath);
        OUT.println("________________________________________________________________");
    }

    private static void printDataWeaveConversionPercentage(MigrationMetrics<? extends DWConstructBase>
                                                                   migrationMetrics) {
        DWConversionStats<? extends DWConstructBase> stats = migrationMetrics.dwConversionStats;
        if (stats.dataWeaveFound()) {
            OUT.println("________________________________________________________________");
            OUT.println("Dataweave conversion percentage: " +
                    String.format("%.2f", stats.getConversionPercentage()) + "%");
            OUT.println("________________________________________________________________");
        } else {
            OUT.println("________________________________________________________________");
            OUT.println("No Dataweave expressions found in the project.");
            OUT.println("________________________________________________________________");
        }
    }

    private static void printOverallProjectConversionPercentage(int conversionPercentage) {
        OUT.println("________________________________________________________________");
        OUT.println("Overall project conversion percentage: " + conversionPercentage + "%");
        OUT.println("________________________________________________________________");
    }

    // ----------------------------------------------- Testing API ------------------------------------------------

    public static void testConvertingMuleProject(Integer muleVersion, String inputPathArg, boolean dryRun,
                                                 boolean keepStructure) {
        logger = createSilentLogger("migrate-mule-test-suite");
        convertMuleProject(inputPathArg, null, muleVersion, dryRun, keepStructure, false);
    }

    public static void testConvertingMultiMuleProjects(Integer muleVersion, String pathToProjects,
                                                       String outputPathArg, boolean dryRun, boolean keepStructure) {
        logger = createSilentLogger("migrate-mule-test-suite");
        convertMuleMultiProjects(pathToProjects, outputPathArg, muleVersion, dryRun, keepStructure);
    }

    // --------------------------------------------- End of Testing API ---------------------------------------------
}
