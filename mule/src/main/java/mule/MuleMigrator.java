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
import mule.common.MigrationResult;
import mule.common.MuleXMLNavigator;
import mule.common.MultiMigrationResult;
import mule.common.ProjectMigrationResult;
import mule.common.report.AggregateReportGenerator;
import mule.common.report.IndividualReportGenerator;
import mule.common.report.ProjectMigrationStats;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Logger;

import static common.BallerinaModel.Import;
import static common.BallerinaModel.ModuleTypeDef;
import static common.BallerinaModel.TextDocument;
import static mule.MigratorUtils.collectXmlFiles;
import static mule.MigratorUtils.collectYamlAndPropertyFiles;
import static mule.MigratorUtils.createDirectories;
import static mule.MigratorUtils.getImmediateSubdirectories;
import static mule.common.report.IndividualReportGenerator.INDIVIDUAL_REPORT_NAME;
import static mule.common.report.IndividualReportGenerator.getProjectMigrationStats;
import static mule.v3.MuleToBalConverter.convertXMLFileToBir;
import static mule.v4.MuleToBalConverter.convertXMLFileToBir;
import static mule.v3.MuleToBalConverter.createContextTypeDefns;
import static mule.v4.MuleToBalConverter.createContextTypeDefns;
import static mule.v4.MuleToBalConverter.createTextDocument;

public class MuleMigrator {

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


    public static Map<String, Object> migrateMule(Map<String, Object> parameters) {
        try {
            String orgName = validateAndGetString(parameters, "orgName");
            String projectName = validateAndGetString(parameters, "projectName");
            String sourcePath = validateAndGetString(parameters, "sourcePath");
            Consumer<String> stateCallback = validateAndGetConsumer(parameters, "stateCallback");
            Consumer<String> logCallback = validateAndGetConsumer(parameters, "logCallback");
            return migrateMuleInner(orgName, projectName, sourcePath, stateCallback, logCallback);
        } catch (IllegalArgumentException e) {
            return Map.of("error", e.getMessage());
        }
    }

    private static Map<String, Object> migrateMuleInner(String orgName, String projectName, String sourcePath,
                                                        Consumer<String> stateCallback, Consumer<String> logCallback) {
        MigrationResult result = migrateMuleSource(sourcePath, null, orgName, projectName, null,
                false, false, false, false);
        if (result.getFatalError().isPresent()) {
            return Map.of("error", result.getFatalError().get());
        }

        ProjectMigrationResult projResult = (ProjectMigrationResult) result;
        return Map.of("textEdits", projResult.getFiles(), "report", projResult.getHtmlReport());
    }

    private static String validateAndGetString(Map<String, Object> parameters, String key) {
        if (!parameters.containsKey(key)) {
            throw new IllegalArgumentException("Missing required parameter: " + key);
        }
        Object value = parameters.get(key);
        if (!(value instanceof String)) {
            throw new IllegalArgumentException("Parameter " + key + " must be a String, got: " +
                    (value != null ? value.getClass().getSimpleName() : "null"));
        }
        return (String) value;
    }

    @SuppressWarnings("unchecked")
    private static Consumer<String> validateAndGetConsumer(Map<String, Object> parameters, String key) {
        if (!parameters.containsKey(key)) {
            throw new IllegalArgumentException("Missing required parameter: " + key);
        }
        Object value = parameters.get(key);
        if (!(value instanceof Consumer)) {
            throw new IllegalArgumentException("Parameter " + key + " must be a Consumer<String>, got: " +
                    (value != null ? value.getClass().getSimpleName() : "null"));
        }
        return (Consumer<String>) value;
    }

    public static void migrateMuleSourceAndWrite(String inputPathArg, String outputPathArg, String orgName,
                                                           String projectName, Integer muleVersion, boolean dryRun,
                                                           boolean verbose, boolean keepStructure, boolean multiRoot) {
        MigrationResult result = migrateMuleSource(inputPathArg, outputPathArg, orgName, projectName, muleVersion,
                dryRun, verbose, keepStructure, multiRoot);
        if (result.getFatalError().isPresent()) {
            logger().severe(result.getFatalError().get());
            return;
        }

        if (result instanceof ProjectMigrationResult projResult) {
            writeSingleRootMigration(projResult, dryRun);
        } else if (result instanceof MultiMigrationResult multiResult)  {
            writeMultiRootMigration(dryRun, multiResult);
        } else {
            throw new IllegalStateException("Unexpected MigrationResult type: " + result.getClass().getName());
        }
    }

    public static MigrationResult migrateMuleSource(String inputPathArg, String outputPathArg, String orgNameArg,
                                                    String projectNameArg, Integer muleVersion, boolean dryRun,
                                                    boolean verbose, boolean keepStructure, boolean multiRoot) {

        logger = verbose ? createDefaultLogger("migrate-mule") : createSilentLogger("migrate-mule");
        logger().info("migrate-mule tool initialized with --dry-run =" + dryRun + ", --verbose = " + verbose +
                ", --keep-structure = " + keepStructure + ", --multi-root = " + multiRoot);

        MigrationResult result = multiRoot ? new MultiMigrationResult() : new ProjectMigrationResult();
        validateInputPathArg(result, inputPathArg);
        validateMuleVersionArg(result, muleVersion);

        if (result.getFatalError().isPresent()) {
            return result;
        }

        if (multiRoot) {
            migrateMultiMuleSource((MultiMigrationResult) result, inputPathArg, outputPathArg, muleVersion, dryRun,
                    verbose, keepStructure);
        } else {
            migrateSingleMuleSource((ProjectMigrationResult) result, inputPathArg, outputPathArg, orgNameArg,
                    projectNameArg, muleVersion, dryRun, verbose, keepStructure);
        }
        return result;
    }

    public static void migrateSingleMuleSource(ProjectMigrationResult result, String inputPathArg, String outputPathArg,
                                               String orgNameArg, String projectNameArg, Integer muleVersion,
                                               boolean dryRun, boolean verbose, boolean keepStructure) {
        Path sourcePath = Paths.get(inputPathArg);
        if (Files.isDirectory(sourcePath)) {
            logger().info("Source path is a Mule project directory: '" + sourcePath + "'");
            convertMuleProject(result, inputPathArg, outputPathArg, orgNameArg, projectNameArg, muleVersion, dryRun,
                    keepStructure, false);
        } else if (Files.isRegularFile(sourcePath) && inputPathArg.endsWith(".xml")) {
            logger().info("Source path is a Mule XML file: '" + sourcePath + "'");
            convertMuleXmlFile(result, inputPathArg, outputPathArg, orgNameArg, projectNameArg, muleVersion, dryRun,
                    keepStructure);
        } else {
            result.setFatalError("Invalid source path: '" + sourcePath + "'. Must be a directory or .xml file.");
        }
    }

    public static void migrateMultiMuleSource(MultiMigrationResult result, String inputPathArg, String outputPathArg,
                                              Integer muleVersion, boolean dryRun, boolean verbose,
                                              boolean keepStructure) {
        Path sourcePath = Paths.get(inputPathArg);
        logger().info("Multi-root mode enabled. Converting all Mule projects in the directory: '" +
                sourcePath + "'");
        if (!Files.isDirectory(sourcePath)) {
            result.setFatalError("Multi-root mode requires a directory as input, but got a file: '" + sourcePath + "'");
            return;
        }
        convertMuleMultiProjects(result, inputPathArg, outputPathArg, muleVersion, dryRun, keepStructure);
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

    private static void validateInputPathArg(MigrationResult result, String inputPathArg) {
        Path inputPath = Paths.get(inputPathArg);
        if (!Files.exists(inputPath)) {
            result.setFatalError("Source path does not exist: '" + inputPath + "'");
        }
    }

//    private static Optional<String> validateOutputPathArg(ProjectMigrationResult result, String outputPathArg) {
//        if (outputPathArg != null) {
//            Path outputPath = Paths.get(outputPathArg);
////            if (!Files.exists(outputPath)) {
//            // TODO:
////                try {
////                    Files.createDirectories(outputPath);
////                    logger().info("Created output directory: " + outputPath);
////                } catch (IOException e) {
////                    logger().severe("Cannot create output directory: " + outputPath + " - " + e.getMessage());
////                    System.exit(1);
////                }
////            } else
//            if (!Files.isDirectory(outputPath)) {
//                logger().severe("Output path exists but is not a directory: " + outputPath);
//            }
//        }
//    }

    private static void validateMuleVersionArg(MigrationResult result, Integer muleVersion) {
        if (muleVersion == null) {
            logger().info("No Mule version specified. Tool will automatically detect the Mule version.");
            return;
        }
        logger().info("Validating Mule version argument: " + muleVersion);
        if (muleVersion != 3 && muleVersion != 4) {
            result.setFatalError("Invalid Mule version specified: " + muleVersion + ". Must be 3 or 4.");
        }
    }

    private static void convertMuleMultiProjects(MultiMigrationResult multiResult, String sourceProjectsDir,
                                                 String outputPathArg,
                                                 Integer muleVersion,
                                                 boolean dryRun, boolean keepStructure) {
        Path sourceProjectsDirPath = Path.of(sourceProjectsDir);
        Path targetPath = outputPathArg != null ? Path.of(outputPathArg) : sourceProjectsDirPath;
        multiResult.setTargetPath(targetPath);

        List<Path> projectDirectories;
        try {
            projectDirectories = getImmediateSubdirectories(sourceProjectsDirPath);
        } catch (IOException e) {
            multiResult.setFatalError("Error listing subdirectories of " + sourceProjectsDir + ": " + e.getMessage());
            return;
        }

        if (muleVersion != null) {
            OUT.println("Using specified Mule version: " + muleVersion);
        }

        List<ProjectMigrationResult> projResultList = new ArrayList<>();
        for (Path projectDir : projectDirectories) {
            logger().info("Converting Mule project: " + projectDir);
            ProjectMigrationResult projResult = new ProjectMigrationResult();
            projResultList.add(projResult);
            try {
                convertMuleProject(projResult, projectDir.toString(), outputPathArg, null, null, muleVersion, dryRun,
                        keepStructure, true);
            } catch (Exception e) {
                logger().severe("Error converting Mule project " + projectDir + ": " + e.getMessage());
            }
        }

        multiResult.setMigrationResults(projResultList);

        String aggregateReport = AggregateReportGenerator.generateHtmlReport(projResultList, targetPath, dryRun);
        multiResult.setHtmlReport(aggregateReport);
//        printMultiRootCompletion(targetPath.resolve(AGGREGATE_MIGRATION_REPORT_NAME), dryRun);
    }

    private static void convertMuleProject(ProjectMigrationResult result, String inputPathArg, String outputPathArg,
                                           String orgNameArg, String projectNameArg, Integer muleVersion,
                                           boolean dryRun, boolean keepStructure, boolean multiRoot) {
        Path sourcePath = Path.of(inputPathArg);
        Path targetPath = outputPathArg != null ? Path.of(outputPathArg) : sourcePath;
        result.setTargetPath(targetPath);

        String sourceProjectName = sourcePath.getFileName().toString();
        result.setSourceName(sourceProjectName);

        result.setOrgName(MigratorUtils.getBalOrgName(orgNameArg));
        result.setProjectName(MigratorUtils.getBalProjectName(projectNameArg, sourceProjectName));

        // Detecting Mule project version
        MuleVersion version;
        if (muleVersion == null) {
            version = MigratorUtils.detectVersionForProject(sourcePath);
            OUT.println("Detected Mule version: " + version);
        } else {
            version = MuleVersion.fromInt(muleVersion);
            if (!multiRoot) {
                OUT.println("Using specified Mule version: " + version);
            }
        }
        result.setMuleVersion(version);

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
        if (xmlFiles.isEmpty()) {
            result.setFatalError("No XML files found in the directory: " + muleXmlConfigDir);
            return;
        }

        List<File> yamlFiles = new ArrayList<>();
        List<File> propertyFiles = new ArrayList<>();
        Path muleResourcesDir = sourcePath.resolve("src").resolve("main").resolve("resources");
        Path propFileLocation = version.equals(MuleVersion.MULE_V3) ? muleXmlConfigDir : muleResourcesDir;
        collectYamlAndPropertyFiles(propFileLocation.toFile(), yamlFiles, propertyFiles);

        logger().info("Found " + xmlFiles.size() + " .xml files, " + yamlFiles.size() + ".yaml files, and " +
                propertyFiles.size() + " .properties files.");


        convertToBalProject(result, version, xmlFiles, yamlFiles, propertyFiles, muleXmlConfigDir,
                sourceProjectName, dryRun, keepStructure, multiRoot);
    }

    private static void convertMuleXmlFile(ProjectMigrationResult result, String inputPathArg,
                                           String outputPathArg, String orgNameArg, String projectNameArg,
                                           Integer muleVersion, boolean dryRun, boolean keepStructure) {
        Path inputXmlFilePath = Path.of(inputPathArg);
        Path sourceDir = inputXmlFilePath.getParent() != null ? inputXmlFilePath.getParent() : Path.of(".");
        Path targetPath = outputPathArg != null ? Path.of(outputPathArg) : sourceDir;
        result.setTargetPath(targetPath);

        String inputFileName = inputXmlFilePath.getFileName().toString().split(".xml")[0];
        result.setSourceName(inputFileName);

        result.setOrgName(MigratorUtils.getBalOrgName(orgNameArg));
        result.setProjectName(MigratorUtils.getBalProjectName(projectNameArg, inputFileName));

        MuleVersion version;
        if (muleVersion == null) {
            version = MigratorUtils.detectVersionForFile(inputXmlFilePath);
            OUT.println("Detected Mule version: " + version);
        } else {
            version = MuleVersion.fromInt(muleVersion);
            OUT.println("Using specified Mule version: " + version);
        }
        result.setMuleVersion(version);


        File xmlConfigFile = inputXmlFilePath.toFile();
        convertToBalProject(result, version, Collections.singletonList(xmlConfigFile), Collections.emptyList(),
                Collections.emptyList(), sourceDir, inputFileName, dryRun, keepStructure, false);
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

    private static void writeSingleRootMigration(ProjectMigrationResult result, boolean dryRun) {
        Path balPackageDir = result.getTargetPath().resolve(result.getProjectName());
        createDirectories(balPackageDir);
        MigratorUtils.writeFile(balPackageDir, INDIVIDUAL_REPORT_NAME, result.getHtmlReport());
        if (!dryRun) {
            MigratorUtils.writeFilesFromMap(balPackageDir, result.getFiles());
        }
    }

    private static void writeMultiRootMigration(boolean dryRun, MultiMigrationResult multiResult) {
        for (ProjectMigrationResult projResult : multiResult.getMigrationResults()) {
            writeSingleRootMigration(projResult, dryRun);
        }
        String aggregateReport = multiResult.getHtmlReport();
        Path targetPath = multiResult.getTargetPath();
        MigratorUtils.writeFile(targetPath, AggregateReportGenerator.AGGREGATE_MIGRATION_REPORT_NAME,
                aggregateReport);
    }

    private static void convertToBalProject(ProjectMigrationResult result, MuleVersion muleVersion,
                                            List<File> xmlFiles,
                                            List<File> yamlFiles, List<File> propertyFiles,
                                            Path muleAppDir, String sourceName,
                                            boolean dryRun, boolean keepStructure, boolean multiRoot) {
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

        // 2. Generate migration report
        ProjectMigrationStats migrationStats = getProjectMigrationStats(muleVersion, ctx.getMigrationMetrics());
        result.setMigrationStats(migrationStats);

        String individualReport = IndividualReportGenerator
                .generateHtmlReport(migrationStats, muleVersion, dryRun, sourceName);
        result.setHtmlReport(individualReport);

        if (dryRun) {
            return;
        }

        // 3. Rearrange BIR for BI Structure
        if (!keepStructure) {
            logger().info("Re-arranging BIR files to fit Ballerina Integrator project structure...");
            birTxtDocs = new BICodeConverter().convert(new BallerinaModel.Module("mock", birTxtDocs)).textDocuments();
        }

        // 3. Generate project artifacts and bal files
        Map<String, String> allFiles = new HashMap<>();
        allFiles.putAll(genProjectArtifacts(result.getOrgName(), result.getProjectName()));
        allFiles.putAll(genBalFilesFromBir(birTxtDocs));
        allFiles.putAll(genConfigTOMLFile(yamlFiles, propertyFiles));
        allFiles = Collections.unmodifiableMap(allFiles);
        result.setFiles(allFiles);
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

    private static Map<String, String> genBalFilesFromBir(List<TextDocument> birTxtDocs) {
//        logger().info("Generating syntax trees from BIR files and write them as .bal files...");
        Map<String, String> balFiles = new HashMap<>();
        for (TextDocument bir : birTxtDocs) {
            SyntaxTree syntaxTree;
            try {
                logger().info("Generating syntax tree for BIR file: " + bir.documentName());
                syntaxTree = new CodeGenerator(bir).generateSyntaxTree();
            } catch (Exception e) {
                logger().severe("Error generating syntax tree from BIR file: " + bir.documentName());
                continue;
            }

            balFiles.put(bir.documentName(), syntaxTree.toSourceCode());
        }
        return balFiles;
    }

    private static Map<String, String> genProjectArtifacts(String orgName, String projectName) {
        String version = "0.1.0";
        String distribution = "2201.12.3";

        String tomlContent = """
                [package]
                org = "%s"
                name = "%s"
                version = "%s"
                distribution = "%s"
                
                [build-options]
                observabilityIncluded = true
                """.formatted(orgName, projectName, version, distribution);

        return Map.of("Ballerina.toml", tomlContent);
    }

    private static Map<String, String> genConfigTOMLFile(List<File> yamlFiles, List<File> propertyFiles) {
        logger().info("Generating Config.toml file from .yaml and .properties files...");
        StringBuilder tomlContent = new StringBuilder();

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

        return Map.of("Config.toml", tomlContent.toString());
    }

    public static void processPropertiesFile(File propFile, StringBuilder tomlContent) {
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
        } catch (Exception e) {
            logger().severe("Error processing properties file " + propFile.getName() + ": " + e.getMessage());
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
        migrateMuleSourceAndWrite(inputPathArg, null, null, null, muleVersion, dryRun, false, keepStructure, false);
    }

    public static void testConvertingMultiMuleProjects(Integer muleVersion, String pathToProjects,
                                                       String outputPathArg, boolean dryRun, boolean keepStructure) {
        logger = createSilentLogger("migrate-mule-test-suite");
        migrateMuleSourceAndWrite(pathToProjects, outputPathArg, null, null, muleVersion, dryRun, false,
                keepStructure, true);
    }

    // --------------------------------------------- End of Testing API ---------------------------------------------
}
