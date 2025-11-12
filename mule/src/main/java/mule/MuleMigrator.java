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
import mule.common.MultiMigrationResult;
import mule.common.MultiRootContext;
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
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static common.BallerinaModel.Import;
import static common.BallerinaModel.ModuleTypeDef;
import static common.BallerinaModel.TextDocument;
import static mule.MigratorUtils.collectXmlFiles;
import static mule.MigratorUtils.collectYamlAndPropertyFiles;
import static mule.MigratorUtils.createDirectories;
import static mule.MigratorUtils.getImmediateSubdirectories;
import static mule.common.report.IndividualReportGenerator.INDIVIDUAL_REPORT_NAME;
import static mule.common.report.IndividualReportGenerator.getProjectMigrationStats;
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

    @SuppressWarnings("unused")
    public static Map<String, Object> migrateMule(Map<String, Object> parameters) {
        try {
            String orgName = validateAndGetString(parameters, "orgName");
            String projectName = validateAndGetString(parameters, "projectName");
            String sourcePath = validateAndGetString(parameters, "sourcePath");
            Consumer<String> stateCallback = validateAndGetConsumer(parameters, "stateCallback");
            Consumer<String> logCallback = validateAndGetConsumer(parameters, "logCallback");
            Integer muleVersion = validateAndGetForceVersion(parameters);
            boolean multiRoot = validateAndGetBoolean(parameters, "multiRoot", false);

            if (multiRoot) {
                return migrateMuleMultiRootInner(orgName, projectName, sourcePath, muleVersion, stateCallback,
                        logCallback);
            } else {
                return migrateMuleInner(orgName, projectName, sourcePath, muleVersion, stateCallback, logCallback);
            }
        } catch (IllegalArgumentException e) {
            return Map.of("error", e.getMessage());
        }
    }

    private static Map<String, Object> migrateMuleInner(String orgName, String projectName, String sourcePath,
                                                        Integer muleVersion, Consumer<String> stateCallback,
                                                        Consumer<String> logCallback) {
        MuleLogger logger = new MuleLogger(stateCallback, logCallback);
        MigrationResult result = migrateMuleSourceInMemory(logger, sourcePath, null, orgName, projectName, muleVersion,
                false, false, false, false);
        if (result.getFatalError().isPresent()) {
            return Map.of("error", result.getFatalError().get());
        }

        ProjectMigrationResult projResult = (ProjectMigrationResult) result;
        return Map.of(
                "textEdits", projResult.getFiles(),
                "report", projResult.getHtmlReport(),
                "report-json", projResult.getJsonReport()
        );
    }

    private static Map<String, Object> migrateMuleMultiRootInner(String orgName, String projectName, String sourcePath,
            Integer muleVersion, Consumer<String> stateCallback, Consumer<String> logCallback) {
        MuleLogger logger = new MuleLogger(stateCallback, logCallback);
        Path inputPath;
        try {
            inputPath = Paths.get(sourcePath).toRealPath();
        } catch (IOException e) {
            return Map.of("error", "Invalid path: " + sourcePath);
        }

        if (!Files.isDirectory(inputPath)) {
            return Map.of("error", "Multi-root conversion requires a directory path: " + sourcePath);
        }

        MigrationResult result = migrateMuleSourceInMemory(logger, sourcePath, null, orgName, projectName, muleVersion,
                false, false, false, true);
        if (result.getFatalError().isPresent()) {
            return Map.of("error", result.getFatalError().get());
        }

        if (!(result instanceof MultiMigrationResult multiResult)) {
            return Map.of("error", "Expected MultiMigrationResult but got: " + result.getClass().getName());
        }

        List<ProjectMigrationResult> projResultList = multiResult.getMigrationResults();
        if (projResultList.isEmpty()) {
            return Map.of("error", "No projects were successfully processed");
        }

        // Collect files and project summaries
        Map<String, String> allFiles = new HashMap<>();
        List<String> packageNames = new ArrayList<>();

        for (ProjectMigrationResult projResult : projResultList) {
            String projectPrefix = projResult.getProjectName();
            packageNames.add(projectPrefix);

            // Add all project files with project prefix
            if (projResult.getFiles() != null) {
                for (Map.Entry<String, String> entry : projResult.getFiles().entrySet()) {
                    String filePath = projectPrefix + "/" + entry.getKey();
                    allFiles.put(filePath, entry.getValue());
                }
            }

            // Add individual project migration report
            String reportPath = projectPrefix + "/" + INDIVIDUAL_REPORT_NAME;
            allFiles.put(reportPath, projResult.getHtmlReport());
        }

        // Generate aggregated HTML report
        Path targetPath = multiResult.getTargetPath();
        AggregateReportGenerator.AggregateStatistics stats =
                AggregateReportGenerator.calculateAggregateStatistics(projResultList);
        String aggregatedHtmlReport = AggregateReportGenerator.generateHtmlReport(stats, logger, projResultList,
                targetPath, false);
        allFiles.put(AggregateReportGenerator.AGGREGATE_MIGRATION_REPORT_NAME, aggregatedHtmlReport);

        // Generate aggregated JSON report
        Map<String, Object> jsonReportMap = generateAggregatedJsonReport(stats);

        // Generate root Ballerina.toml
        String rootBallerinaToml = generateWorkspaceBallerinaToml(packageNames);
        if (!rootBallerinaToml.isEmpty()) {
            allFiles.put("Ballerina.toml", rootBallerinaToml);
        }

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("error", null);
        resultMap.put("textEdits", allFiles);
        resultMap.put("report", aggregatedHtmlReport);
        resultMap.put("report-json", jsonReportMap);
        return resultMap;
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

    private static Integer validateAndGetForceVersion(Map<String, Object> parameters) {
        String key = "forceVersion";
        if (!parameters.containsKey(key)) {
            return null;
        }
        Object value = parameters.get(key);
        if (!(value instanceof Integer)) {
            throw new IllegalArgumentException("Parameter " + key + " must be a Integer, got: " +
                    (value != null ? value.getClass().getSimpleName() : "null"));
        }
        if (!(Integer.valueOf(3).equals(value) || Integer.valueOf(4).equals(value))) {
            throw new IllegalArgumentException("Parameter " + key + " must be either 3 or 4, got: " + value);
        }
        return (Integer) value;
    }

    private static boolean validateAndGetBoolean(Map<String, Object> parameters, String key, boolean defaultValue) {
        if (!parameters.containsKey(key)) {
            return defaultValue;
        }
        Object value = parameters.get(key);
        if (!(value instanceof Boolean)) {
            throw new IllegalArgumentException("Parameter " + key + " must be a Boolean, got: " +
                    (value != null ? value.getClass().getSimpleName() : "null"));
        }
        return (Boolean) value;
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
            ContextBase ctx = createProjectContext(logger, result, inputPathArg, outputPathArg, orgNameArg,
                    projectNameArg, muleVersion, dryRun, keepStructure, false, null);
            if (ctx != null) {
                try {
                    parseMuleProject(ctx);
                    generateCodeFromParsedProject(ctx);
                } catch (Exception e) {
                    logger.logSevere("Unrecoverable error while migrating %s".formatted(sourcePath));
                }
            }
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

        List<ContextBase> projectContexts = new ArrayList<>();
        MultiRootContext multiRootContext = new MultiRootContext();

        // Phase 1: Parse all projects
        for (Path projectDir : projectDirectories) {
            logger.logState("Parsing Mule project: " + projectDir);
            ProjectMigrationResult projResult = new ProjectMigrationResult();
            try {
                ContextBase ctx = createProjectContext(logger, projResult, projectDir.toString(), outputPathArg, null,
                        null, muleVersion, dryRun, keepStructure, true, multiRootContext);
                if (ctx != null) {
                    parseMuleProject(ctx);
                    projectContexts.add(ctx);
                    logger.logState("Completed parsing Mule project: " + projectDir);
                }
            } catch (Exception e) {
                logger.logSevere("Error parsing Mule project " + projectDir + ": " + e.getMessage());
            }
        }

        // Phase 2: Generate code for all parsed projects
        for (ContextBase ctx : projectContexts) {
            try {
                logger.logState("Generating code for Mule project: " + ctx.sourceName);
                generateCodeFromParsedProject(ctx);
                logger.logState("Completed converting Mule project: " + ctx.sourceName);
            } catch (Exception e) {
                logger.logSevere("Error generating code for Mule project " + ctx.sourceName + ": " + e.getMessage());
            }
        }

        List<ProjectMigrationResult> projResultList = projectContexts.stream()
                .map(ctx -> ctx.result)
                .filter(result -> result.getMigrationStats() != null)
                .toList();
        multiResult.setMigrationResults(projResultList);
        AggregateReportGenerator.AggregateStatistics stats =
                AggregateReportGenerator.calculateAggregateStatistics(projResultList);
        String aggregateReport = AggregateReportGenerator.generateHtmlReport(stats, logger, projResultList, targetPath,
                dryRun);
        multiResult.setHtmlReport(aggregateReport);
        logger.logState("Completed converting Mule projects via multi-root mode");
    }

    private static ContextBase createProjectContext(MuleLogger logger, ProjectMigrationResult result,
                                                    String inputPathArg, String outputPathArg, String orgNameArg,
                                                    String projectNameArg, Integer muleVersion, boolean dryRun,
                                                    boolean keepStructure, boolean multiRoot,
                                                    MultiRootContext multiRootContext) {
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
            return null;
        }

        List<File> yamlFiles = new ArrayList<>();
        List<File> propertyFiles = new ArrayList<>();
        Path muleResourcesDir = sourcePath.resolve("src").resolve("main").resolve("resources");
        Path propFileLocation = version.equals(MuleVersion.MULE_V3) ? muleXmlConfigDir : muleResourcesDir;
        collectYamlAndPropertyFiles(propFileLocation.toFile(), yamlFiles, propertyFiles);

        logger.logInfo("Found " + xmlFiles.size() + " .xml files, " + yamlFiles.size() + ".yaml files, and " +
                propertyFiles.size() + " .properties files.");

        ContextBase ctx = getContext(version, xmlFiles, yamlFiles, muleXmlConfigDir, propertyFiles,
                sourceProjectName, dryRun, keepStructure, logger, result, multiRootContext);
        return ctx;
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
        ContextBase ctx = getContext(version, Collections.singletonList(xmlConfigFile), Collections.emptyList(),
                sourceDir, Collections.emptyList(), inputFileName, dryRun, keepStructure, logger, result, null);
        try {
            parseMuleProject(ctx);
            generateCodeFromParsedProject(ctx);
        } catch (Exception ex) {
            logger.logSevere("Unrecoverable error while converting %s".formatted(xmlConfigFile));
        }
    }

    private static ContextBase getContext(MuleVersion muleVersion, List<File> xmlFiles, List<File> yamlFiles,
            Path muleAppDir, List<File> propertyFiles, String sourceName,
                                          boolean dryRun, boolean keepStructure, MuleLogger logger,
                                          ProjectMigrationResult result,
                                          MultiRootContext multiRootContext) {
        if (muleVersion == MuleVersion.MULE_V3) {
            return new mule.v3.Context(xmlFiles, yamlFiles, muleAppDir, muleVersion, propertyFiles, sourceName,
                    dryRun, keepStructure, logger, result, multiRootContext);
        } else if (muleVersion == MuleVersion.MULE_V4) {
            return new mule.v4.Context(xmlFiles, yamlFiles, muleAppDir, muleVersion, propertyFiles, sourceName,
                    dryRun, keepStructure, logger, result, multiRootContext);
        } else {
            throw new IllegalArgumentException("Unsupported Mule version: " + muleVersion);
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

        // Generate workspace Ballerina.toml for multi-root projects
        writeWorkspaceBallerinaToml(logger, multiResult);
    }

    public static String generateWorkspaceBallerinaToml(List<String> packageNames) {
        if (packageNames.isEmpty()) {
            return "";
        }

        List<String> sortedPackageNames = new ArrayList<>(packageNames);
        Collections.sort(sortedPackageNames);

        // Generate workspace Ballerina.toml content
        StringBuilder tomlContent = new StringBuilder("[workspace]\n");
        tomlContent.append("packages = [");
        for (int i = 0; i < sortedPackageNames.size(); i++) {
            if (i > 0) {
                tomlContent.append(", ");
            }
            tomlContent.append("\"").append(sortedPackageNames.get(i)).append("\"");
        }
        tomlContent.append("]\n");

        return tomlContent.toString();
    }

    private static void writeWorkspaceBallerinaToml(MuleLogger logger, MultiMigrationResult multiResult) {
        // Extract package names from successfully converted projects
        List<String> packageNames = multiResult.getMigrationResults().stream()
                .filter(result -> result.getMigrationStats() != null)
                .map(ProjectMigrationResult::getProjectName)
                .toList();

        String tomlContent = generateWorkspaceBallerinaToml(packageNames);
        if (tomlContent.isEmpty()) {
            return;
        }

        // Write workspace Ballerina.toml to target directory
        Path targetPath = multiResult.getTargetPath();
        MigratorUtils.writeFile(logger, targetPath, "Ballerina.toml", tomlContent);
        logger.logInfo("Created workspace Ballerina.toml at: " + targetPath.resolve("Ballerina.toml"));
    }

    private static void parseMuleProject(ContextBase ctx) {
        ctx.logger.logState("Converting Mule XML configs to ballerina intermediate representation...");
        ctx.parseAllFiles();
    }

    private static void generateCodeFromParsedProject(ContextBase ctx) {
        List<TextDocument> birTxtDocs = ctx.codeGen();
        ctx.logger.logInfo("Converted " + birTxtDocs.size() + " XML files to Ballerina IR.");

        TextDocument birTxtDoc = genBirForInternalTypes(ctx.logger, ctx);
        birTxtDocs.add(birTxtDoc);

        // 2. Generate migration report
        ProjectMigrationStats migrationStats = getProjectMigrationStats(ctx.muleVersion, ctx.getMigrationMetrics());
        ctx.result.setMigrationStats(migrationStats);

        String individualReportHtml = IndividualReportGenerator.generateHtmlReport(ctx.logger, migrationStats,
                ctx.muleVersion, ctx.dryRun, ctx.sourceName);
        ctx.result.setHtmlReport(individualReportHtml);

        String individualReportJson = IndividualReportGenerator.generateJsonReport(migrationStats);
        ctx.result.setJsonReport(individualReportJson);

        if (ctx.dryRun) {
            ctx.logger.logState("Dry run completed for project: " + ctx.sourceName);
            return;
        }

        // 3. Rearrange BIR for BI Structure
        if (!ctx.keepStructure) {
            ctx.logger.logState("Re-arranging BIR files to fit Ballerina Integrator project structure...");
            birTxtDocs =
                    new BICodeConverter(ctx.getContextImports()).convert(new BallerinaModel.Module("mock", birTxtDocs))
                            .textDocuments();
        }

        // Collect configurable variable names
        Set<String> configurableVariableNames = ctx.getConfigurableVars().stream()
                .map(common.BallerinaModel.ModuleVar::name)
                .collect(Collectors.toSet());
        ctx.result.setConfigurableVariableNames(configurableVariableNames);

        // 3. Generate project artifacts and bal files
        ctx.logger.logState("Generate project artifacts and bal files...");
        Map<String, String> allFiles = new HashMap<>();
        allFiles.putAll(genProjectArtifacts(ctx, ctx.logger));
        allFiles.putAll(genBalFilesFromBir(ctx.logger, birTxtDocs));
        allFiles.putAll(genConfigTOMLFile(ctx.logger, ctx.yamlFiles, ctx.propertyFiles,
                ctx.result.getConfigurableVariableNames()));
        allFiles = Collections.unmodifiableMap(allFiles);
        ctx.result.setFiles(allFiles);
    }

    /**
     * Generates a BalFile for internal types.
     *
     * @param ctx Context instance
     */
    private static TextDocument genBirForInternalTypes(MuleLogger logger, ContextBase ctx) {
        // TODO: do we need to consider multi-flow-multi-context scenario?

        logger.logInfo("Generating BIR for context type definitions...");
        List<ModuleTypeDef> contextTypeDefns = ctx.createContextTypeDefns();
        List<Import> contextImports = ctx.getContextImports();

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

    private static Map<String, String> genProjectArtifacts(ContextBase ctx, MuleLogger logger) {
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
                """.formatted(ctx.getOrgName(), ctx.getProjectName(), version, distribution));

        ctx.appendJavaDependencies(tomlContent);

        return Map.of("Ballerina.toml", tomlContent.toString());
    }

    public static Map<String, String> genConfigTOMLFile(MuleLogger logger, List<File> yamlFiles,
                                                         List<File> propertyFiles,
                                                         Set<String> configurableVariableNames) {
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
            processPropertiesFile(logger, propFile, tomlContent, configurableVariableNames);
            tomlContent.append("\n");
        }

        // Process .yaml files using SnakeYAML
        for (File yamlFile : yamlFiles) {
            tomlContent.append("# Properties from ").append(yamlFile.getName()).append("\n");
            processYamlFile(logger, yamlFile, tomlContent, configurableVariableNames);
            tomlContent.append("\n");
        }

        return Map.of("Config.toml", tomlContent.toString());
    }

    public static void processPropertiesFile(MuleLogger logger, File propFile, StringBuilder tomlContent,
                                             Set<String> configurableVariableNames) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(propFile), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.trim().startsWith("#")) {
                    continue;
                }

                int equalsIndex = line.indexOf('=');
                if (equalsIndex > 0) {
                    String escapedKey = common.ConversionUtils
                            .convertToBalIdentifier(line.substring(0, equalsIndex).trim().replace('.', '_'));
                    // Only add key if it exists in configurable variable names
                    if (configurableVariableNames != null && configurableVariableNames.contains(escapedKey)) {
                        String value = line.substring(equalsIndex + 1).trim();
                        tomlContent.append(escapedKey).append(" = ").append(formatTomlValue(value)).append("\n");
                    }
                }
            }
        } catch (Exception e) {
            logger.logSevere("Error processing properties file " + propFile.getName() + ": " + e.getMessage());
        }
    }

    private static String escapeTomlValue(String value) {
        return value.replace("\"", "\\\"").replace("\\", "\\\\");
    }

    /**
     * Checks if a value needs to be written as a TOML multi-line string block.
     * Values containing double quotes or newlines should use string blocks for
     * better readability.
     *
     * @param value The value to check
     * @return true if the value should use string block format, false otherwise
     */
    private static boolean needsStringBlock(String value) {
        return value.contains("\"") || value.contains("\n") || value.contains("\r");
    }

    /**
     * Formats a TOML value, using string block format if needed.
     *
     * @param value The value to format
     * @return The formatted TOML value (either as regular string or string block)
     */
    private static String formatTomlValue(String value) {
        if (needsStringBlock(value)) {
            return "\"\"\"" + value + "\"\"\"";
        } else {
            return "\"" + escapeTomlValue(value) + "\"";
        }
    }

    public static void processYamlFile(MuleLogger logger, File yamlFile, StringBuilder tomlContent,
                                       Set<String> configurableVariableNames) {
        try {
            Yaml yaml = new Yaml();
            try (FileInputStream inputStream = new FileInputStream(yamlFile)) {
                Object data = yaml.load(inputStream);
                if (data instanceof Map) {
                    flattenYamlToToml((Map<String, Object>) data, "", tomlContent, configurableVariableNames);
                }
            }
        } catch (Exception e) {
            logger.logSevere("Error processing YAML file " + yamlFile.getName() + ": " + e.getMessage());
        }
    }

    static void flattenYamlToToml(Map<String, Object> map, String prefix, StringBuilder tomlContent,
                                   Set<String> configurableVariableNames) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey().replace('-', '_');
            String fullKey = prefix.isEmpty() ? key : prefix + "_" + key;
            Object value = entry.getValue();

            if (value instanceof Map) {
                flattenYamlToToml((Map<String, Object>) value, fullKey, tomlContent, configurableVariableNames);
            } else if (value instanceof List<?> list) {
                for (int i = 0; i < list.size(); i++) {
                    Object listItem = list.get(i);
                    if (listItem instanceof Map) {
                        flattenYamlToToml((Map<String, Object>) listItem, fullKey + "_" + i, tomlContent,
                                configurableVariableNames);
                    } else {
                        String listKey = fullKey + "_" + i;
                        String escapedListKey = common.ConversionUtils.convertToBalIdentifier(listKey);
                        // Only add key if it exists in configurable variable names
                        if (configurableVariableNames != null && configurableVariableNames.contains(escapedListKey)) {
                            String listValue = String.valueOf(listItem);
                            tomlContent.append(escapedListKey).append(" = ").append(formatTomlValue(listValue))
                                    .append("\n");
                        }
                    }
                }
            } else {
                String escapedFullKey = common.ConversionUtils.convertToBalIdentifier(fullKey);
                // Only add key if it exists in configurable variable names
                if (configurableVariableNames != null && configurableVariableNames.contains(escapedFullKey)) {
                    String tomlValue = String.valueOf(value);
                    tomlContent.append(escapedFullKey).append(" = ").append(formatTomlValue(tomlValue)).append("\n");
                }
            }
        }
    }

    private static Map<String, Object> generateAggregatedJsonReport(
            AggregateReportGenerator.AggregateStatistics stats) {
        Map<String, Object> coverageOverview = new HashMap<>();
        coverageOverview.put("projects", stats.totalProjects());
        coverageOverview.put("unitName", "code lines");
        coverageOverview.put("coveragePercentage", Math.round(stats.avgCoverage()));
        coverageOverview.put("coverageLevel", stats.coverageLevel());
        coverageOverview.put("totalElements", stats.totalItems());
        coverageOverview.put("migratableElements", stats.migratableItems());
        coverageOverview.put("nonMigratableElements", stats.nonMigratableItems());

        Map<String, Object> result = new HashMap<>();
        result.put("coverageOverview", coverageOverview);
        return result;
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
