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
import common.CombinedSummaryReport;
import common.LoggingUtils;
import common.ProjectSummary;
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import tibco.ConversionContext;
import tibco.LoggingContext;
import tibco.ProjectConversionContext;
import tibco.TibcoToBalConverter;
import tibco.analyzer.AnalysisResult;
import tibco.analyzer.DefaultAnalysisPass;
import tibco.analyzer.LoggingAnalysisPass;
import tibco.analyzer.ModelAnalyser;
import tibco.analyzer.ProjectAnalysisContext;
import tibco.analyzer.ResourceAnalysisPass;
import tibco.analyzer.TibcoAnalysisReport;
import tibco.converter.ProjectConverter.ProjectResources;
import tibco.model.Process;
import tibco.model.Type.Schema;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    public record SerializedProject(java.util.Map<String, String> files, tibco.analyzer.TibcoAnalysisReport report) {

    }

    public static ParsedProject parseProject(ProjectConversionContext cx, String projectPath) {
        try {
            tibco.parser.ProjectContext pcx = new tibco.parser.ProjectContext(cx, projectPath);
            Set<Process> processes = TibcoToBalConverter.parseProcesses(pcx);
            Set<Schema> types = TibcoToBalConverter.parseTypes(pcx);
            ProjectResources resources = TibcoToBalConverter.parseResources(pcx);

            // Add all parsed resources to the ConversionContext for global lookup
            cx.conversionContext().addProjectResources(resources);

            return new ParsedProject(processes, types, resources, pcx);
        } catch (Exception e) {
            cx.log(LoggingUtils.Level.SEVERE,
                    "Unrecoverable error while parsing project: " + projectPath + ": " + e.getMessage());
            throw new RuntimeException("Error while parsing project: " + projectPath, e);
        }
    }

    public static AnalyzedProject analyzeProject(ProjectConversionContext cx, ParsedProject parsed,
                                                 ModelAnalyser modelAnalyser) {

        ProjectAnalysisContext analysisContext = new ProjectAnalysisContext(cx, parsed.resources());
        Map<Process, AnalysisResult> analysisResults =
                modelAnalyser.analyseProject(analysisContext, parsed.processes(), parsed.types(), parsed.resources());
        ProjectResources resources = ProjectResources.merge(parsed.resources(), analysisContext.capturedResources());
        return new AnalyzedProject(parsed.processes(), parsed.types(), resources, parsed.parserContext(),
                analysisResults);
    }

    public static GeneratedProject generateCode(ProjectConversionContext cx, AnalyzedProject analyzed) {
        ConversionResult result = ProjectConverter.convertProject(cx, analyzed.analysisResults(), analyzed.processes(),
                analyzed.types(), analyzed.resources(), analyzed.parserContext());
        return new GeneratedProject(result);
    }

    public static SerializedProject serializeProject(ProjectConversionContext cx, GeneratedProject generated) {
        Map<String, String> files = new HashMap<>();
        ConversionResult result = generated.conversionResult();

        BallerinaModel.Module module =
                cx.keepStructure() ? result.module() : new BICodeConverter().convert(result.module());
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
        report.lineCount(files.values().stream().
                mapToInt(content -> content.split("\r?\n").length)
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
        SerializedProject serialized = serializeProject(cx, generated);
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
        ConversionContext context =
                new ConversionContext(orgName.orElse("converter"), dryRun, keepStructure,
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
            migrateTibcoMultiRoot(context, inputPath, outputPath, projectName, keepStructure);
            return;
        }

        migrateTibcoInner(context, sourcePath, outputPath, projectName);
    }

    static void migrateTibcoMultiRoot(ConversionContext cx, Path inputPath, String outputPath,
                                      Optional<String> projectName, boolean keepStructure) {
        List<ProjectSummary> projectSummaries = new ArrayList<>();
        List<String> childPaths = new ArrayList<>();
        List<String> childOutputPaths = new ArrayList<>();
        List<String> childNames = new ArrayList<>();

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
                        childPaths.add(childDir.toString());
                        childOutputPaths.add(childOutputPath);
                        childNames.add(childName);
                    });
        } catch (IOException e) {
            cx.log(SEVERE, "Error reading directory: " + inputPath);
            System.exit(1);
            return;
        }

        // Stage 1: Parse all projects
        List<ParsedProject> parsedProjects = new ArrayList<>();
        List<ProjectConversionContext> contexts = new ArrayList<>();
        for (int i = 0; i < childPaths.size(); i++) {
            String childPath = childPaths.get(i);
            String childName = childNames.get(i);
            String finalProjectName = projectName.orElse(childName);
            ProjectConversionContext context = new ProjectConversionContext(cx, finalProjectName);
            contexts.add(context);

            cx.logState("Parsing project: " + childPath);
            cx.log(LoggingUtils.Level.INFO, "Parsing project: " + childPath);
            try {
                ParsedProject parsed = parseProject(context, childPath);
                parsedProjects.add(parsed);
            } catch (Exception e) {
                cx.log(SEVERE, "Failed to parse project: " + childPath + ": " + e.getMessage());
                parsedProjects.add(null);
            }
        }

        // Stage 2: Analyze all projects
        List<AnalyzedProject> analyzedProjects = new ArrayList<>();
        for (int i = 0; i < parsedProjects.size(); i++) {
            ParsedProject parsed = parsedProjects.get(i);
            ProjectConversionContext context = contexts.get(i);
            String childName = childNames.get(i);

            if (parsed != null) {
                cx.logState("Analyzing project: " + childName);
                try {
                    ModelAnalyser modelAnalyser = new ModelAnalyser(List.of(
                            new DefaultAnalysisPass(),
                            new LoggingAnalysisPass(),
                            new ResourceAnalysisPass()));
                    AnalyzedProject analyzed = analyzeProject(context, parsed, modelAnalyser);
                    analyzedProjects.add(analyzed);
                } catch (Exception e) {
                    cx.log(SEVERE, "Failed to analyze project: " + childName + ": " + e.getMessage());
                    analyzedProjects.add(null);
                }
            } else {
                analyzedProjects.add(null);
            }
        }

        // Stage 3: Generate code for all projects
        List<GeneratedProject> generatedProjects = new ArrayList<>();
        for (int i = 0; i < analyzedProjects.size(); i++) {
            AnalyzedProject analyzed = analyzedProjects.get(i);
            ProjectConversionContext context = contexts.get(i);
            String childName = childNames.get(i);

            if (analyzed != null) {
                cx.logState("Generating code for project: " + childName);
                try {
                    GeneratedProject generated = generateCode(context, analyzed);
                    generatedProjects.add(generated);
                } catch (Exception e) {
                    cx.log(SEVERE, "Failed to generate code for project: " + childName + ": " + e.getMessage());
                    generatedProjects.add(null);
                }
            } else {
                generatedProjects.add(null);
            }
        }

        // Stage 4: Serialize all projects
        List<SerializedProject> serializedProjects = new ArrayList<>();
        for (int i = 0; i < generatedProjects.size(); i++) {
            GeneratedProject generated = generatedProjects.get(i);
            ProjectConversionContext context = contexts.get(i);
            String childName = childNames.get(i);

            if (generated != null) {
                cx.logState("Serializing project: " + childName);
                try {
                    SerializedProject serialized = serializeProject(context, generated);
                    serializedProjects.add(serialized);
                } catch (Exception e) {
                    cx.log(SEVERE, "Failed to serialize project: " + childName + ": " + e.getMessage());
                    serializedProjects.add(null);
                }
            } else {
                serializedProjects.add(null);
            }
        }

        // Stage 5: Write all projects to disk
        for (int i = 0; i < serializedProjects.size(); i++) {
            SerializedProject serialized = serializedProjects.get(i);
            ProjectConversionContext context = contexts.get(i);
            String childOutputPath = childOutputPaths.get(i);
            String childName = childNames.get(i);

            if (serialized != null) {
                cx.logState("Writing project: " + childName);
                try {
                    writeProjectFiles(context, serialized, childOutputPath, context.dryRun());

                    // Create project summary
                    String reportRelativePath = childName + "_converted/report.html";
                    ProjectSummary projectSummary = serialized.report().toProjectSummary(
                            childName,
                            childPaths.get(i),
                            reportRelativePath
                    );
                    projectSummaries.add(projectSummary);
                } catch (Exception e) {
                    cx.log(SEVERE, "Failed to write project: " + childName + ": " + e.getMessage());
                }
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
        ProjectConversionContext context = new ProjectConversionContext(cx, finalProjectName);

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
            SerializedProject serialized = serializeProject(context, generated);
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
        CombinedSummaryReport combinedReport = new CombinedSummaryReport("Combined Migration Assessment",
                projectSummaries);
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
