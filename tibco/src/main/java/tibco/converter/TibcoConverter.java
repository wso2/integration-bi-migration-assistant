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
import tibco.analyzer.TibcoAnalysisReport;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import static common.LoggingUtils.Level.SEVERE;

public class TibcoConverter {

    record MigrationResult(TibcoAnalysisReport report) {
    }

    public static void migrateTibco(String sourcePath, String outputPath, boolean preserverStructure, boolean verbose,
                                    boolean dryRun, boolean multiRoot, Optional<String> orgName, 
                                    Optional<String> projectName) {
        Logger logger = verbose ? createVerboseLogger("migrate-tibco") : createDefaultLogger("migrate-tibco");
        Consumer<String> stateCallback = LoggingUtils.wrapLoggerForStateCallback(logger);
        Consumer<String> logCallback = LoggingUtils.wrapLoggerForLogCallback(logger);
        ConversionContext context =
                new ConversionContext(orgName.orElse("converter"), dryRun, preserverStructure, 
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
        List<ProjectSummary> projectSummaries = new ArrayList<>();
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
                        cx.logState("Converting project: " + childDir);
                        cx.log(LoggingUtils.Level.INFO, "Converting project: " + childDir);

                        Optional<MigrationResult> result =
                                migrateTibcoInner(cx, childDir.toString(), childOutputPath, projectName);

                        if (result.isPresent()) {
                            TibcoAnalysisReport report = result.get().report();

                            // Create individual report for this project
                            Path projectOutputPath = Paths.get(childOutputPath);
                            try {
                                writeAnalysisReport(cx, projectOutputPath, report.toHTML());
                            } catch (IOException e) {
                                cx.log(SEVERE, "Error creating individual analysis report for project: " + childName);
                            }

                            // Create project summary
                            String reportRelativePath = childName + "_converted/report.html";
                            ProjectSummary projectSummary = report.toProjectSummary(
                                    childName,
                                            childDir.toString(),
                                    reportRelativePath
                            );
                            projectSummaries.add(projectSummary);
                        }
                    });
        } catch (IOException e) {
            cx.log(SEVERE, "Error reading directory: " + inputPath);
            System.exit(1);
            return;
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
        if (Files.isRegularFile(inputPath)) {
            String inputRootDirectory = inputPath.getParent().toString();
            String targetPath = outputPath != null ? outputPath : inputRootDirectory + "_converted";
            return migrateTibcoProject(context, inputRootDirectory, targetPath);
        } else if (Files.isDirectory(inputPath)) {
            String targetPath = outputPath != null ? outputPath : inputPath + "_converted";
            return migrateTibcoProject(context, inputPath.toString(), targetPath);
        } else {
            context.log(SEVERE, "Invalid path: " + inputPath);
            System.exit(1);
            return Optional.empty();
        }
    }

    static Optional<MigrationResult> migrateTibcoProject(ProjectConversionContext cx,
                                                         String projectPath, String targetPath) {
        Path targetDir = Paths.get(targetPath);
        Path codeGenDir = targetDir;
        java.nio.file.Path tempDir = null;
        if (cx.dryRun()) {
            try {
                tempDir = Files.createTempDirectory("tibco-dryrun");
                codeGenDir = tempDir;
                cx.logState("Generating code in temporary directory: " + codeGenDir);
                cx.log(LoggingUtils.Level.INFO,
                        "[Dry Run] Generating code in temporary directory: " + codeGenDir);
            } catch (IOException e) {
                cx.log(SEVERE, "Error creating temporary directory for dry run");
                System.exit(1);
                return Optional.empty();
            }
        } else {
            try {
                createTargetDirectoryIfNeeded(cx, targetDir);
            } catch (IOException e) {
                cx.log(SEVERE, "Error creating target directory: " + targetDir);
                System.exit(1);
                return Optional.empty();
            }
        }

        ConvertResult convertResult = convertProjectInner(cx, projectPath);
        if (convertResult.errorMessage().isPresent()) {
            cx.log(SEVERE, "Conversion failed: " + convertResult.errorMessage().get());
            return Optional.empty();
        }
        Map<String, String> files = convertResult.files();
        // Write files to disk
        for (Map.Entry<String, String> entry : files.entrySet()) {
            Path filePath = codeGenDir.resolve(entry.getKey());
            try {
                Files.writeString(filePath, entry.getValue());
            } catch (IOException e) {
                cx.log(SEVERE, "Failed to create output file " + entry.getKey());
            }
        }
        try {
            createTargetDirectoryIfNeeded(cx, targetDir);
            writeAnalysisReport(cx, targetDir, convertResult.reportHTML);
        } catch (IOException e) {
            cx.log(SEVERE, "Error creating analysis report");
        }
        if (cx.dryRun()) {
            try {
                cx.log(LoggingUtils.Level.INFO,
                        "[Dry Run] Temporary code generation directory: " + tempDir);
            } catch (Exception e) {
                cx.log(LoggingUtils.Level.WARN,
                        "Failed to clean up temporary directory: " + tempDir + " - " + e.getMessage());
            }
        }
        return Optional.of(new MigrationResult(convertResult.report()));
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

    public record ConvertResult(Optional<String> errorMessage, Map<String, String> files, String reportHTML,
                                TibcoAnalysisReport report) {

    }

    public static ConvertResult convertProjectInner(ProjectConversionContext cx, String projectPath) {
        try {
            ConversionResult result = TibcoToBalConverter.convertProject(cx, projectPath);
            Map<String, String> files = new HashMap<>();
            // Collect text documents
            BallerinaModel.Module module =
                    cx.keepStructure() ? result.module() : new BICodeConverter().convert(result.module());
            for (BallerinaModel.TextDocument textDocument : module.textDocuments()) {
                SyntaxTree st = new CodeGenerator(textDocument).generateSyntaxTree();
                files.put(textDocument.documentName(), st.toSourceCode());
            }
            // Add types.bal
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
            // Prepare report
            TibcoAnalysisReport report = result.report();
            report.lineCount(files.values().stream().
                    mapToInt(content -> content.split("\r?\n").length)
                    .sum());
            return new ConvertResult(Optional.empty(), files, report.toHTML(), report);
        } catch (Exception e) {
            cx.logState("Unrecoverable error while converting project " + projectPath);
            cx.log(SEVERE, "Unrecoverable error while converting project" + projectPath + ": " + e.getMessage());
            return new ConvertResult(Optional.of(e.getMessage()), null, null, null);
        }
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
