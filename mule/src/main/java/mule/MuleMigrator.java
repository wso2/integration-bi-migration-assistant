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
import mule.common.MigrationResult;
import mule.common.MuleLogger;
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
        MuleLogger logger = new MuleLogger(stateCallback, logCallback);
        MigrationResult result = migrateMuleSourceInMemory(logger, sourcePath, null, orgName, projectName, null,
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

    public static void migrateAndExportMuleSource(String inputPathArg, String outputPathArg, String orgName,
                                                  String projectName, Integer muleVersion, boolean dryRun,
                                                  boolean verbose, boolean keepStructure, boolean multiRoot) {
        MuleLogger logger = new MuleLogger(verbose);
        MigrationResult result = migrateMuleSourceInMemory(logger, inputPathArg, outputPathArg, orgName, projectName,
                muleVersion, dryRun, verbose, keepStructure, multiRoot);
        if (result.getFatalError().isPresent()) {
            logger.logSevere(result.getFatalError().get());
            return;
        }

        logger.logState("Writing output...");
        if (result instanceof ProjectMigrationResult projResult) {
            writeSingleRootMigration(logger, projResult, dryRun);
        } else if (result instanceof MultiMigrationResult multiResult)  {
            writeMultiRootMigration(logger, dryRun, multiResult);
        } else {
            throw new IllegalStateException("Unexpected MigrationResult type: " + result.getClass().getName());
        }
        logger.logState("Writing output completed");
    }

    public static MigrationResult migrateMuleSourceInMemory(MuleLogger logger, String inputPathArg,
                                                            String outputPathArg, String orgNameArg,
                                                            String projectNameArg, Integer muleVersion, boolean dryRun,
                                                            boolean verbose, boolean keepStructure, boolean multiRoot) {
        logger.logState("Initializing migrate-mule tool...");
        logger.logInfo("migrate-mule tool initialized with --dry-run =" + dryRun + ", --verbose = " + verbose +
                ", --keep-structure = " + keepStructure + ", --multi-root = " + multiRoot);

        MigrationResult result = multiRoot ? new MultiMigrationResult() : new ProjectMigrationResult();
        validateInputPathArg(result, inputPathArg);
        validateMuleVersionArg(logger, result, muleVersion);

        if (result.getFatalError().isPresent()) {
            return result;
        }

        if (multiRoot) {
            migrateMultiMuleSource(logger, (MultiMigrationResult) result, inputPathArg, outputPathArg, muleVersion,
                    dryRun, verbose, keepStructure);
        } else {
            migrateSingleMuleSource(logger, (ProjectMigrationResult) result, inputPathArg, outputPathArg, orgNameArg,
                    projectNameArg, muleVersion, dryRun, verbose, keepStructure);
        }
        return result;
    }

    private static void migrateSingleMuleSource(MuleLogger logger, ProjectMigrationResult result, String inputPathArg,
                                               String outputPathArg, String orgNameArg, String projectNameArg,
                                               Integer muleVersion,
                                               boolean dryRun, boolean verbose, boolean keepStructure) {
        Path sourcePath = Paths.get(inputPathArg);
        if (Files.isDirectory(sourcePath)) {
            logger.logInfo("Source path is a Mule project directory: '" + sourcePath + "'");
            convertMuleProject(logger, result, inputPathArg, outputPathArg, orgNameArg, projectNameArg, muleVersion,
                    dryRun, keepStructure, false);
        } else if (Files.isRegularFile(sourcePath) && inputPathArg.endsWith(".xml")) {
            logger.logInfo("Source path is a Mule XML file: '" + sourcePath + "'");
            convertMuleXmlFile(logger, result, inputPathArg, outputPathArg, orgNameArg, projectNameArg, muleVersion,
                    dryRun, keepStructure);
        } else {
            result.setFatalError("Invalid source path: '" + sourcePath + "'. Must be a directory or .xml file.");
        }
    }

    private static void migrateMultiMuleSource(MuleLogger logger, MultiMigrationResult result, String inputPathArg,
                                              String outputPathArg, Integer muleVersion, boolean dryRun,
                                              boolean verbose, boolean keepStructure) {
        Path sourcePath = Paths.get(inputPathArg);
        logger.logInfo("Multi-root mode enabled. Converting all Mule projects in the directory: '" +
                sourcePath + "'");
        if (!Files.isDirectory(sourcePath)) {
            result.setFatalError("Multi-root mode requires a directory as input, but got a file: '" + sourcePath + "'");
            return;
        }
        convertMuleMultiProjects(logger, result, inputPathArg, outputPathArg, muleVersion, dryRun, keepStructure);
    }

    private static void validateInputPathArg(MigrationResult result, String inputPathArg) {
        Path inputPath = Paths.get(inputPathArg);
        if (!Files.exists(inputPath)) {
            result.setFatalError("Source path does not exist: '" + inputPath + "'");
        }
    }

    private static void validateMuleVersionArg(MuleLogger logger, MigrationResult result, Integer muleVersion) {
        if (muleVersion == null) {
            logger.logInfo("No Mule version specified. Tool will automatically detect the Mule version.");
            return;
        }
        logger.logInfo("Validating Mule version argument: " + muleVersion);
        if (muleVersion != 3 && muleVersion != 4) {
            result.setFatalError("Invalid Mule version specified: " + muleVersion + ". Must be 3 or 4.");
        }
    }

    private static void convertMuleMultiProjects(MuleLogger logger, MultiMigrationResult multiResult,
                                                 String sourceProjectsDir,
                                                 String outputPathArg,
                                                 Integer muleVersion,
                                                 boolean dryRun, boolean keepStructure) {
        logger.logState("Processing multi-root Mule projects");
        Path sourceProjectsDirPath = Path.of(sourceProjectsDir);
        Path targetPath = outputPathArg != null ? Path.of(outputPathArg) : sourceProjectsDirPath;
        multiResult.setTargetPath(targetPath);

        List<Path> projectDirectories;
        try {
            logger.logInfo("Listing immediate subdirectories in: " + sourceProjectsDir);
            projectDirectories = getImmediateSubdirectories(sourceProjectsDirPath);
        } catch (IOException e) {
            multiResult.setFatalError("Error listing subdirectories of " + sourceProjectsDir + ": " + e.getMessage());
            return;
        }

        if (muleVersion != null) {
            logger.logInfo("Using specified Mule version: " + muleVersion);
        }

        List<ProjectMigrationResult> projResultList = new ArrayList<>();
        for (Path projectDir : projectDirectories) {
            logger.logState("Converting Mule project: " + projectDir);
            ProjectMigrationResult projResult = new ProjectMigrationResult();
            projResultList.add(projResult);
            try {
                convertMuleProject(logger, projResult, projectDir.toString(), outputPathArg, null, null, muleVersion,
                        dryRun, keepStructure, true);
                logger.logState("Completed converting Mule project: " + projectDir);
            } catch (Exception e) {
                logger.logSevere("Error converting Mule project " + projectDir + ": " + e.getMessage());
            }
        }

        multiResult.setMigrationResults(projResultList);
        String aggregateReport = AggregateReportGenerator.generateHtmlReport(logger, projResultList, targetPath,
                dryRun);
        multiResult.setHtmlReport(aggregateReport);
        logger.logState("Completed converting Mule projects via multi-root mode");
    }

    private static void convertMuleProject(MuleLogger logger, ProjectMigrationResult result, String inputPathArg,
                                           String outputPathArg, String orgNameArg, String projectNameArg,
                                           Integer muleVersion,
                                           boolean dryRun, boolean keepStructure, boolean multiRoot) {
        Path sourcePath = Path.of(inputPathArg);
        Path targetPath = outputPathArg != null ? Path.of(outputPathArg) : sourcePath;
        result.setTargetPath(targetPath);

        String sourceProjectName = sourcePath.getFileName().toString();
        result.setSourceName(sourceProjectName);
        logger.logState("Processing Mule Project:" + sourceProjectName);

        result.setOrgName(MigratorUtils.getBalOrgName(orgNameArg));
        result.setProjectName(MigratorUtils.getBalProjectName(projectNameArg, sourceProjectName));

        // Detecting Mule project version
        MuleVersion version;
        if (muleVersion == null) {
            version = MigratorUtils.detectVersionForProject(sourcePath);
            logger.logInfo("Detected Mule version: " + version);
        } else {
            version = MuleVersion.fromInt(muleVersion);
            if (!multiRoot) {
                logger.logInfo("Using specified Mule version: " + version);
            }
        }
        result.setMuleVersion(version);

        // Collect xml configs, yaml and property files
        logger.logInfo("Collecting XML configs, YAML, and property files in Mule project...");
        List<File> xmlFiles = new ArrayList<>();

        Path muleXmlConfigDir = sourcePath.resolve("src").resolve("main");
        if (version.equals(MuleVersion.MULE_V3)) {
            logger.logInfo("Detected Mule version: MULE_V3");
            muleXmlConfigDir = muleXmlConfigDir.resolve(MULE_V3_DEFAULT_XML_CONFIGS_DIR_NAME);
        } else {
            logger.logInfo("Detected Mule version: MULE_V4");
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

        logger.logInfo("Found " + xmlFiles.size() + " .xml files, " + yamlFiles.size() + ".yaml files, and " +
                propertyFiles.size() + " .properties files.");


        convertToBalProject(logger, result, version, xmlFiles, yamlFiles, propertyFiles, muleXmlConfigDir,
                sourceProjectName, dryRun, keepStructure, multiRoot);
    }

    private static void convertMuleXmlFile(MuleLogger logger, ProjectMigrationResult result, String inputPathArg,
                                           String outputPathArg, String orgNameArg, String projectNameArg,
                                           Integer muleVersion, boolean dryRun, boolean keepStructure) {
        logger.logState("Processing Mule XML file");
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
            logger.logInfo("Detected Mule version: " + version);
        } else {
            version = MuleVersion.fromInt(muleVersion);
            logger.logInfo("Using specified Mule version: " + version);
        }
        result.setMuleVersion(version);


        File xmlConfigFile = inputXmlFilePath.toFile();
        convertToBalProject(logger, result, version, Collections.singletonList(xmlConfigFile), Collections.emptyList(),
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

    private static void writeSingleRootMigration(MuleLogger logger, ProjectMigrationResult result, boolean dryRun) {
        Path balPackageDir = result.getTargetPath().resolve(result.getProjectName());
        createDirectories(logger, balPackageDir);
        MigratorUtils.writeFile(logger, balPackageDir, INDIVIDUAL_REPORT_NAME, result.getHtmlReport());
        if (!dryRun) {
            MigratorUtils.writeFilesFromMap(logger, balPackageDir, result.getFiles());
        }
    }

    private static void writeMultiRootMigration(MuleLogger logger, boolean dryRun, MultiMigrationResult multiResult) {
        for (ProjectMigrationResult projResult : multiResult.getMigrationResults()) {
            writeSingleRootMigration(logger, projResult, dryRun);
        }
        String aggregateReport = multiResult.getHtmlReport();
        Path targetPath = multiResult.getTargetPath();
        MigratorUtils.writeFile(logger, targetPath, AggregateReportGenerator.AGGREGATE_MIGRATION_REPORT_NAME,
                aggregateReport);
    }

    private static void convertToBalProject(MuleLogger logger, ProjectMigrationResult result, MuleVersion muleVersion,
                                            List<File> xmlFiles,
                                            List<File> yamlFiles, List<File> propertyFiles,
                                            Path muleAppDir, String sourceName,
                                            boolean dryRun, boolean keepStructure, boolean multiRoot) {
        logger.logState("Converting Mule XML configs to ballerina intermediate representation...");
        // 1. Convert xml configs to ballerina-ir
        ContextBase ctx = getContext(muleVersion);
        MuleXMLNavigator muleXMLNavigator = getXMLNavigator(ctx);
        List<TextDocument> birTxtDocs = new ArrayList<>(xmlFiles.size() + 1);
        for (File xmlFile : xmlFiles) {
            ctx.startNewFile(xmlFile.getPath());
            Path relativePath = muleAppDir.relativize(xmlFile.toPath());
            String balFileName = relativePath.toString().replace(File.separator, ".").replace(".xml", "");
            TextDocument birTextDoc = genBirFromXMLFile(logger, ctx, muleXMLNavigator, xmlFile, balFileName);
            if (birTextDoc != null) {
                birTxtDocs.add(birTextDoc);
            }
        }
        logger.logInfo("Converted " + birTxtDocs.size() + " XML files to Ballerina IR.");

        TextDocument birTxtDoc = genBirForInternalTypes(logger, ctx);
        birTxtDocs.add(birTxtDoc);

        // 2. Generate migration report
        ProjectMigrationStats migrationStats = getProjectMigrationStats(muleVersion, ctx.getMigrationMetrics());
        result.setMigrationStats(migrationStats);

        String individualReport = IndividualReportGenerator.generateHtmlReport(logger, migrationStats, muleVersion,
                dryRun, sourceName);
        result.setHtmlReport(individualReport);

        if (dryRun) {
            logger.logState("Dry run completed for project: " + sourceName);
            return;
        }

        // 3. Rearrange BIR for BI Structure
        if (!keepStructure) {
            logger.logState("Re-arranging BIR files to fit Ballerina Integrator project structure...");
            birTxtDocs = new BICodeConverter().convert(new BallerinaModel.Module("mock", birTxtDocs)).textDocuments();
        }

        // 3. Generate project artifacts and bal files
        logger.logState("Generate project artifacts and bal files...");
        Map<String, String> allFiles = new HashMap<>();
        allFiles.putAll(genProjectArtifacts(ctx, logger, result.getOrgName(), result.getProjectName()));
        allFiles.putAll(genBalFilesFromBir(logger, birTxtDocs));
        allFiles.putAll(genConfigTOMLFile(logger, yamlFiles, propertyFiles));
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
    private static TextDocument genBirFromXMLFile(MuleLogger logger, ContextBase ctx, MuleXMLNavigator muleXMLNavigator,
                                                  File xmlFile, String balFileName) {
        logger.logInfo("Converting XML file: " + xmlFile.getName());
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
            logger.logSevere(String.format("Error converting the file to ballerina intermediate representation: " +
                            "%s%n%s",
                    xmlFile.getName(), e.getMessage()));
        }
        return birTextDocument;
    }

    /**
     * Generates a BalFile for internal types.
     *
     * @param ctx Context instance
     */
    private static TextDocument genBirForInternalTypes(MuleLogger logger, ContextBase ctx) {
        // TODO: do we need to consider multi-flow-multi-context scenario?

        logger.logInfo("Generating BIR for context type definitions...");
        List<ModuleTypeDef> contextTypeDefns;
        List<Import> contextImports = new ArrayList<>(1);
        if (ctx instanceof mule.v3.Context v3Ctx) {
            contextTypeDefns = createContextTypeDefns(v3Ctx);
            if (!v3Ctx.projectCtx.inboundProperties.isEmpty()) {
                // TODO: at the moment only http provides 'inboundProperties'
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
                Collections.emptyList(), Collections.emptyList());
    }

    private static Map<String, String> genBalFilesFromBir(MuleLogger logger, List<TextDocument> birTxtDocs) {
        logger.logState("Generating syntax trees from BIR files and write them as .bal files...");
        Map<String, String> balFiles = new HashMap<>();
        for (TextDocument bir : birTxtDocs) {
            SyntaxTree syntaxTree;
            try {
                logger.logInfo("Generating syntax tree for BIR file: " + bir.documentName());
                syntaxTree = new CodeGenerator(bir).generateSyntaxTree();
            } catch (Exception e) {
                logger.logSevere("Error generating syntax tree from BIR file: " + bir.documentName());
                continue;
            }

            balFiles.put(bir.documentName(), syntaxTree.toSourceCode());
        }
        return balFiles;
    }

    private static Map<String, String> genProjectArtifacts(ContextBase ctx, MuleLogger logger, String orgName,
                                                           String projectName) {
        logger.logState("Generating Ballerina.toml...");
        String version = "0.1.0";
        String distribution = "2201.12.3";

        StringBuilder tomlContent = new StringBuilder("""
                [package]
                org = "%s"
                name = "%s"
                version = "%s"
                distribution = "%s"
                
                [build-options]
                observabilityIncluded = true
                """.formatted(orgName, projectName, version, distribution));

        if (ctx instanceof mule.v4.Context v4Ctx) {
            for (var each : v4Ctx.projectCtx.javaDependencies()) {
                tomlContent.append("\n");
                tomlContent.append(each.dependencyParam);
            }
        } else if (ctx instanceof mule.v3.Context v3Ctx) {
            for (var each : v3Ctx.projectCtx.javaDependencies()) {
                tomlContent.append("\n");
                tomlContent.append(each.dependencyParam);
            }
        } else {
            throw new IllegalStateException();
        }

        return Map.of("Ballerina.toml", tomlContent.toString());
    }

    private static Map<String, String> genConfigTOMLFile(MuleLogger logger, List<File> yamlFiles,
                                                         List<File> propertyFiles) {
        logger.logState("Generating Config.toml file from .yaml and .properties files...");
        StringBuilder tomlContent = new StringBuilder();

        // Process .properties files
        for (File propFile : propertyFiles) {
            if (propFile.getName().equals("mule-deploy.properties")) {
                // Skip mule-deploy.properties file
                continue;
            }

            // Add file name as comment
            tomlContent.append("# Properties from ").append(propFile.getName()).append("\n");
            processPropertiesFile(logger, propFile, tomlContent);
            tomlContent.append("\n");
        }

        // Process .yaml files using SnakeYAML
        for (File yamlFile : yamlFiles) {
            tomlContent.append("# Properties from ").append(yamlFile.getName()).append("\n");
            processYamlFile(logger, yamlFile, tomlContent);
            tomlContent.append("\n");
        }

        return Map.of("Config.toml", tomlContent.toString());
    }

    public static void processPropertiesFile(MuleLogger logger, File propFile, StringBuilder tomlContent) {
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
            logger.logSevere("Error processing properties file " + propFile.getName() + ": " + e.getMessage());
        }
    }

    private static String escapeTomlValue(String value) {
        return value.replace("\"", "\\\"").replace("\\", "\\\\");
    }

    public static void processYamlFile(MuleLogger logger, File yamlFile, StringBuilder tomlContent) {
        try {
            Yaml yaml = new Yaml();
            try (FileInputStream inputStream = new FileInputStream(yamlFile)) {
                Object data = yaml.load(inputStream);
                if (data instanceof Map) {
                    flattenYamlToToml((Map<String, Object>) data, "", tomlContent);
                }
            }
        } catch (Exception e) {
            logger.logSevere("Error processing YAML file " + yamlFile.getName() + ": " + e.getMessage());
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

    // ----------------------------------------------- Testing API ------------------------------------------------

    public static void testConvertingMuleProject(Integer muleVersion, String inputPathArg, boolean dryRun,
                                                 boolean keepStructure) {
        migrateAndExportMuleSource(inputPathArg, null, null, null, muleVersion, dryRun, false, keepStructure, false);
    }

    public static void testConvertingMultiMuleProjects(Integer muleVersion, String pathToProjects,
                                                       String outputPathArg, boolean dryRun, boolean keepStructure) {
        migrateAndExportMuleSource(pathToProjects, outputPathArg, null, null, muleVersion, dryRun, false,
                keepStructure, true);
    }

    // --------------------------------------------- End of Testing API ---------------------------------------------
}
