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

package tibco.converter;

import common.BICodeConverter;
import common.BallerinaModel;
import common.CodeGenerator;
import common.LoggingUtils;
import common.ProjectSummary;
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import org.jetbrains.annotations.NotNull;
import tibco.ConversionContext;
import tibco.LoggingContext;
import tibco.ProjectConversionContext;
import tibco.TibcoToBalConverter;
import tibco.analyzer.AnalysisResult;
import tibco.analyzer.CombinedSummaryReport;
import tibco.analyzer.DefaultAnalysisPass;
import tibco.analyzer.DependencyAnalysisPass;
import tibco.analyzer.LoggingAnalysisPass;
import tibco.analyzer.ModelAnalyser;
import tibco.analyzer.ProjectAnalysisContext;
import tibco.analyzer.TibcoAnalysisReport;
import tibco.converter.ProjectConverter.ProjectResources;
import tibco.model.Process;
import tibco.model.Type.Schema;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static common.LoggingUtils.Level.SEVERE;

public class TibcoConverter {

    record MigrationResult(TibcoAnalysisReport report) {
    }

    public record ParsedProject(Set<Process> processes, Set<Schema> types,
                                ProjectResources resources,
                                tibco.parser.ProjectContext parserContext) {

    }

    public record AnalyzedProject(Set<Process> processes, Set<Schema> types,
                                  ProjectResources resources,
                                  tibco.parser.ProjectContext parserContext,
                                  Map<Process, tibco.analyzer.AnalysisResult> analysisResults) {

    }

    public record GeneratedProject(ConversionResult conversionResult) {

    }

    public record SerializedProject(Map<String, String> files, tibco.analyzer.TibcoAnalysisReport report) {

    }

    public static @NotNull ParsedProject parseProject(ProjectConversionContext cx, String projectPath) {
        try {
            tibco.parser.ProjectContext pcx = new tibco.parser.ProjectContext(cx, projectPath);
            Set<Process> processes = TibcoToBalConverter.parseProcesses(pcx);
            Set<Schema> types = TibcoToBalConverter.parseTypes(pcx);
            ProjectResources resources = TibcoToBalConverter.parseResources(pcx);

            // Add processes and resources to the ProjectConversionContext
            processes.forEach(cx::addProcess);
            resources.stream().forEach(cx::addResource);

            // Add all parsed resources to the ConversionContext for global lookup
            cx.conversionContext().addProjectResources(resources, cx);

            // Add all parsed processes to the ConversionContext for global lookup
            cx.conversionContext().addProjectProcesses(processes, cx);

            return new ParsedProject(processes, types, resources, pcx);
        } catch (Exception e) {
            cx.log(LoggingUtils.Level.SEVERE,
                    "Unrecoverable error while parsing project: " + projectPath + ": " + e.getMessage());
            throw new RuntimeException("Error while parsing project: " + projectPath, e);
        }
    }

    public static @NotNull AnalyzedProject analyzeProject(ProjectConversionContext cx, ParsedProject parsed,
                                                         ModelAnalyser modelAnalyser) {

        ProjectAnalysisContext analysisContext = new ProjectAnalysisContext(cx, parsed.resources());
        analysisContext.setCurrentProcesses(parsed.processes());
        Map<Process, AnalysisResult> analysisResults =
                modelAnalyser.analyseProject(analysisContext, parsed.processes(), parsed.types(), parsed.resources());
        ProjectResources resources = ProjectResources.merge(parsed.resources(), analysisContext.capturedResources());
        return new AnalyzedProject(parsed.processes(), parsed.types(), resources, parsed.parserContext(),
                analysisResults);
    }

    public static @NotNull GeneratedProject generateCode(ProjectConversionContext cx, AnalyzedProject analyzed) {
        ConversionResult result = ProjectConverter.convertProject(cx, analyzed.analysisResults(), analyzed.processes(),
                analyzed.types(), analyzed.resources(), analyzed.parserContext());
        return new GeneratedProject(result);
    }

    public static @NotNull SerializedProject serializeProject(ProjectConversionContext cx, GeneratedProject generated,
                                                              Collection<BallerinaModel.Import> allProjectImports) {
        Map<String, String> files = new HashMap<>();
        ConversionResult result = generated.conversionResult();

        BallerinaModel.Module module = cx.keepStructure() ? result.module() :
                new BICodeConverter(BICodeConverter.DEFAULT_IS_CONFIGURABLE_PREDICATE,
                        BICodeConverter.DEFAULT_IS_CONNECTION_PREDICATE,
                        BICodeConverter.DEFAULT_SKIP_CONVERSION_PREDICATE, allProjectImports).convert(result.module());
        for (BallerinaModel.TextDocument textDocument : module.textDocuments()) {
            SyntaxTree st = new CodeGenerator(textDocument).generateSyntaxTree();
            files.put(textDocument.documentName(), st.toSourceCode());
        }

        SyntaxTree typesTree = result.types();
        if (typesTree != null) {
            String xsdTypeSource = typesTree.toSourceCode();
            String typeSource;
            if (files.containsKey("types.bal")) {
                typeSource = files.get("types.bal") + "\n" + xsdTypeSource;
            } else {
                typeSource = xsdTypeSource;
            }
            files.put("types.bal", typeSource);
        }
        files.put("Ballerina.toml", ballerinaToml(cx));

        TibcoAnalysisReport report = result.report();
        report.lineCount(
                files.values().stream()
                        .map(ConversionUtils::lineCount)
                        .mapToLong(ConversionUtils.LineCount::normalize)
                        .sum());

        return new SerializedProject(files, report);
    }

    public static void migrateTibcoProject(ProjectConversionContext cx, String projectPath, String targetPath)
            throws Exception {
        ParsedProject parsed = parseProject(cx, projectPath);
        AnalyzedProject analyzed = analyzeProject(cx, parsed, new ModelAnalyser(List.of(
                new DefaultAnalysisPass(),
                new LoggingAnalysisPass())));
        GeneratedProject generated = generateCode(cx, analyzed);
        SerializedProject serialized = serializeProject(cx, generated, List.of());
        writeProjectFiles(cx, serialized, targetPath, false);
    }

    static void writeProjectFiles(LoggingContext cx, SerializedProject serialized, String targetPath, boolean dryRun)
            throws IOException {
        Path targetDir = Paths.get(targetPath);
        Path codeGenDir = targetDir;
        java.nio.file.Path tempDir = null;

        if (dryRun) {
            tempDir = Files.createTempDirectory("tibco-dryrun");
            codeGenDir = tempDir;
            cx.logState("Generating code in temporary directory: " + codeGenDir);
            cx.log(LoggingUtils.Level.INFO, "[Dry Run] Generating code in temporary directory: " + codeGenDir);
        } else {
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
                cx.log(LoggingUtils.Level.INFO, "Created target directory: " + targetDir);
            }
        }

        for (Map.Entry<String, String> entry : serialized.files().entrySet()) {
            Path filePath = codeGenDir.resolve(entry.getKey());
            Files.writeString(filePath, entry.getValue());
        }

        if (!Files.exists(targetDir)) {
            Files.createDirectories(targetDir);
            cx.log(LoggingUtils.Level.INFO, "Created target directory: " + targetDir);
        }
        writeAnalysisReport(cx, targetDir, serialized.report().toHTML());

        if (dryRun) {
            cx.log(LoggingUtils.Level.INFO, "[Dry Run] Temporary code generation directory: " + tempDir);
        }
    }

    public static void migrateTibco(String sourcePath, String outputPath, boolean keepStructure, boolean verbose,
            boolean dryRun, boolean multiRoot, Optional<String> orgName, Optional<String> projectName) {
        Logger logger = verbose ? createVerboseLogger("migrate-tibco") : createDefaultLogger("migrate-tibco");
        Consumer<String> stateCallback = LoggingUtils.wrapLoggerForStateCallback(logger);
        Consumer<String> logCallback = LoggingUtils.wrapLoggerForLogCallback(logger);
        String escapedOrgName = common.ConversionUtils.escapeIdentifier(orgName.orElse("converter"));
        ConversionContext context =
                new ConversionContext(escapedOrgName, dryRun, keepStructure,
                                stateCallback, logCallback);
        Path inputPath = null;
        try {
            inputPath = Paths.get(sourcePath).toRealPath();
        } catch (IOException e) {
            context.log(SEVERE, "Invalid path: " + sourcePath);
            System.exit(1);
        }

        if (multiRoot) {
            if (!Files.isDirectory(inputPath)) {
                context.log(SEVERE,
                        "Error: Multi-root conversion requires a directory path, but a file was provided: "
                        + sourcePath);
                System.exit(1);
            }
            migrateTibcoMultiRoot(context, inputPath, outputPath, projectName);
            return;
        }

        migrateTibcoInner(context, sourcePath, outputPath, projectName);
    }

    static void migrateTibcoMultiRoot(ConversionContext cx, Path inputPath, String outputPath,
                                      Optional<String> projectName) {
        // Stage 0: Initialize project info
        record ProjectInfo(
                String childPath,
                String childOutputPath,
                String childName,
                ProjectConversionContext context) {
        }

        List<ProjectInfo> projectInfoList = new ArrayList<>();
        try {
            Files.list(inputPath)
                    .filter(Files::isDirectory)
                    .forEach(childDir -> {
                        String childName = childDir.getFileName().toString();
                        String childOutputPath;
                        if (outputPath != null) {
                            childOutputPath = Paths.get(outputPath, childName + "_converted").toString();
                        } else {
                            childOutputPath = childDir + "_converted";
                        }
                        String finalProjectName = projectName.orElse(childName);
                        String escapedProjectName = common.ConversionUtils.escapeIdentifier(finalProjectName);
                        ProjectConversionContext context = new ProjectConversionContext(cx, escapedProjectName);

                        projectInfoList.add(new ProjectInfo(
                                childDir.toString(),
                                childOutputPath,
                                childName,
                                context));
                    });
        } catch (IOException e) {
            cx.log(SEVERE, "Error reading directory: " + inputPath);
            System.exit(1);
            return;
        }

        // Stage 1: Parse all projects
        record ParsedProjectInfo(
                ProjectInfo info,
                ParsedProject parsed) {
        }

        List<ParsedProjectInfo> parsedProjects = new ArrayList<>();
        for (ProjectInfo info : projectInfoList) {
            cx.logState("Parsing project: " + info.childPath());
            cx.log(LoggingUtils.Level.INFO, "Parsing project: " + info.childPath());
            try {
                ParsedProject parsed = parseProject(info.context(), info.childPath());
                parsedProjects.add(new ParsedProjectInfo(info, parsed));
            } catch (Exception e) {
                cx.log(SEVERE, "Failed to parse project: " + info.childPath() + ": " + e.getMessage());
                // Skip this project
            }
        }

        // Stage 2: Analyze all projects
        record AnalyzedProjectInfo(
                ProjectInfo info,
                ParsedProject parsed,
                AnalyzedProject analyzed) {
        }

        List<AnalyzedProjectInfo> analyzedProjects = new ArrayList<>();
        for (ParsedProjectInfo parsedInfo : parsedProjects) {
            cx.logState("Analyzing project: " + parsedInfo.info().childName());
            try {
                ModelAnalyser modelAnalyser = new ModelAnalyser(List.of(
                        new DefaultAnalysisPass(),
                        new LoggingAnalysisPass(),
                        new DependencyAnalysisPass()));
                AnalyzedProject analyzed = analyzeProject(parsedInfo.info().context(), parsedInfo.parsed(),
                        modelAnalyser);
                analyzedProjects.add(new AnalyzedProjectInfo(
                        parsedInfo.info(),
                        parsedInfo.parsed(),
                        analyzed));
            } catch (Exception e) {
                cx.log(SEVERE, "Failed to analyze project: " + parsedInfo.info().childName() + ": " + e.getMessage());
                // Skip this project
            }
        }

        // Stage 3: Generate code for all projects
        record GeneratedProjectInfo(
                ProjectInfo info,
                ParsedProject parsed,
                AnalyzedProject analyzed,
                GeneratedProject generated) {
        }

        List<GeneratedProjectInfo> generatedProjects = new ArrayList<>();
        for (AnalyzedProjectInfo analyzedInfo : analyzedProjects) {
            cx.logState("Generating code for project: " + analyzedInfo.info().childName());
            try {
                GeneratedProject generated = generateCode(analyzedInfo.info().context(), analyzedInfo.analyzed());
                generatedProjects.add(new GeneratedProjectInfo(
                        analyzedInfo.info(),
                        analyzedInfo.parsed(),
                        analyzedInfo.analyzed(),
                        generated));
            } catch (Exception e) {
                cx.log(SEVERE, "Failed to generate code for project: " + analyzedInfo.info().childName() + ": "
                        + e.getMessage());
                // Skip this project
            }
        }

        // Stage 4: Serialize all projects
        record SerializedProjectInfo(
                ProjectInfo info,
                ParsedProject parsed,
                AnalyzedProject analyzed,
                GeneratedProject generated,
                SerializedProject serialized) {
        }

        List<SerializedProjectInfo> serializedProjects = new ArrayList<>();
        for (GeneratedProjectInfo generatedInfo : generatedProjects) {
            cx.logState("Serializing project: " + generatedInfo.info().childName());
            try {
                SerializedProject serialized =
                        serializeProject(generatedInfo.info().context(), generatedInfo.generated(),
                                projectInfoList.stream()
                                        .map(ProjectInfo::context)
                                        .map(ProjectConversionContext::getImport)
                                        .collect(Collectors.toList()));
                serializedProjects.add(new SerializedProjectInfo(
                        generatedInfo.info(),
                        generatedInfo.parsed(),
                        generatedInfo.analyzed(),
                        generatedInfo.generated(),
                        serialized));
            } catch (Exception e) {
                cx.log(SEVERE,
                        "Failed to serialize project: " + generatedInfo.info().childName() + ": " + e.getMessage());
                // Skip this project
            }
        }

        // Stage 5: Write all projects to disk and collect summaries
        List<ProjectSummary> projectSummaries = new ArrayList<>();
        for (SerializedProjectInfo serializedInfo : serializedProjects) {
            cx.logState("Writing project: " + serializedInfo.info().childName());
            try {
                writeProjectFiles(serializedInfo.info().context(), serializedInfo.serialized(),
                        serializedInfo.info().childOutputPath(), serializedInfo.info().context().dryRun());

                // Create project summary
                String reportRelativePath = serializedInfo.info().childName() + "_converted/report.html";
                        ProjectSummary projectSummary = serializedInfo.serialized().report().toProjectSummary(
                        serializedInfo.info().childName(),
                        serializedInfo.info().childPath(),
                        reportRelativePath);
                projectSummaries.add(projectSummary);
            } catch (Exception e) {
                cx.log(SEVERE, "Failed to write project: " + serializedInfo.info().childName() + ": " + e.getMessage());
            }
        }

        // Create combined summary report
        Path summaryOutputPath = outputPath != null ? Paths.get(outputPath) : inputPath;
        try {
            writeCombinedSummaryReport(cx, summaryOutputPath, projectSummaries);
        } catch (IOException e) {
            cx.log(SEVERE, "Error creating combined summary report");
        }
    }


    private static Optional<MigrationResult> migrateTibcoInner(ConversionContext cx, String sourcePath,
                                                               String outputPath, Optional<String> projectName) {
        Path inputPath;
        try {
            inputPath = Paths.get(sourcePath).toRealPath();
        } catch (IOException e) {
            cx.log(SEVERE, "Invalid path: " + sourcePath);
            System.exit(1);
            return Optional.empty();
        }
        String finalProjectName = projectName.orElse(inputPath.getFileName().toString());
        String escapedProjectName = common.ConversionUtils.escapeIdentifier(finalProjectName);
        ProjectConversionContext context = new ProjectConversionContext(cx, escapedProjectName);

        String projectPath;
        String targetPath;
        if (Files.isRegularFile(inputPath)) {
            projectPath = inputPath.getParent().toString();
            targetPath = outputPath != null ? outputPath : projectPath + "_converted";
        } else if (Files.isDirectory(inputPath)) {
            projectPath = inputPath.toString();
            targetPath = outputPath != null ? outputPath : projectPath + "_converted";
        } else {
            context.log(SEVERE, "Invalid path: " + inputPath);
            System.exit(1);
            return Optional.empty();
        }

        try {
            ParsedProject parsed = parseProject(context, projectPath);
            AnalyzedProject analyzed = analyzeProject(context, parsed, new ModelAnalyser(List.of(
                    new DefaultAnalysisPass(),
                    new LoggingAnalysisPass())));
            GeneratedProject generated = generateCode(context, analyzed);
            SerializedProject serialized = serializeProject(context, generated, List.of());
            writeProjectFiles(context, serialized, targetPath, context.dryRun());

            return Optional.of(new MigrationResult(serialized.report()));
        } catch (Exception e) {
            context.log(SEVERE, "Error during project conversion: " + e.getMessage());
            return Optional.empty();
        }
    }


    private static void writeAnalysisReport(LoggingContext context, Path targetDir,
                                            String htmlContent) throws IOException {
        Path reportFilePath = targetDir.resolve("report.html");
        Files.writeString(reportFilePath, htmlContent);
        context.log(LoggingUtils.Level.INFO, "Created analysis report at: " + reportFilePath);
    }

    private static void writeCombinedSummaryReport(ConversionContext context, Path targetDir,
                                                   List<ProjectSummary> projectSummaries) throws IOException {
        Path reportFilePath = targetDir.resolve("combined_summary_report.html");
        CombinedSummaryReport combinedReport =
                new CombinedSummaryReport("Combined Migration Assessment",
                        projectSummaries, context.getDuplicateProcessData());
        String htmlContent = combinedReport.toHTML();
        Files.writeString(reportFilePath, htmlContent);
        context.log(
                LoggingUtils.Level.INFO, "Created combined summary report at: " + reportFilePath);
    }

    private static void createTargetDirectoryIfNeeded(ProjectConversionContext context,
                                                      Path targetDir) throws IOException {
        if (Files.exists(targetDir)) {
            return;
        }
        Files.createDirectories(targetDir);
        context.log(LoggingUtils.Level.INFO, "Created target directory: " + targetDir);
    }

    private static String ballerinaToml(ProjectConversionContext cx) {
        String version = "0.1.0";
        String distribution = "2201.12.0";

        StringBuilder tomlContent = new StringBuilder("""
                [package]
                org = "%s"
                name = "%s"
                version = "%s"
                distribution = "%s"

                [build-options]
                observabilityIncluded = true""".formatted(cx.org(), cx.name(), version, distribution));
        for (var each : cx.javaDependencies()) {
            tomlContent.append("\n");
            tomlContent.append(each.dependencyParam);
        }
        return tomlContent.toString();
    }


    public static Logger createDefaultLogger(String name) {
        Logger defaultLogger = Logger.getLogger(name);
        defaultLogger.setFilter(record ->
                record.getLevel().intValue() >= java.util.logging.Level.WARNING.intValue());
        return defaultLogger;
    }

    public static Logger createVerboseLogger(String name) {
        Logger verboseLogger = Logger.getLogger(name);
        verboseLogger.setUseParentHandlers(false); // Avoid duplicate logs

        // Remove existing handlers
        for (java.util.logging.Handler handler : verboseLogger.getHandlers()) {
            verboseLogger.removeHandler(handler);
        }

        java.util.logging.ConsoleHandler handler = new java.util.logging.ConsoleHandler() {
            @Override
            protected synchronized void setOutputStream(java.io.OutputStream out) throws SecurityException {
                super.setOutputStream(System.out);
            }
        };
        handler.setLevel(Level.ALL);
        handler.setFormatter(new java.util.logging.SimpleFormatter());

        verboseLogger.addHandler(handler);
        verboseLogger.setLevel(Level.ALL);

        return verboseLogger;
    }
}
