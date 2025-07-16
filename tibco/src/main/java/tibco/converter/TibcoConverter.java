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
import tibco.ProjectConversionContext;
import tibco.TibcoToBalConverter;
import tibco.analyzer.TibcoAnalysisReport;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import static common.LoggingUtils.Level.SEVERE;

public class TibcoConverter {

    record MigrationResult(TibcoAnalysisReport report) {
    }

    public static void migrateTibco(String sourcePath, String outputPath, boolean preserverStructure, boolean verbose,
            boolean dryRun, boolean multiRoot) {
        Logger logger = verbose ? createVerboseLogger("migrate-tibco") : createDefaultLogger("migrate-tibco");
        Consumer<String> stateCallback = LoggingUtils.wrapLoggerForStateCallback(logger);
        Consumer<String> logCallback = LoggingUtils.wrapLoggerForLogCallback(logger);
        ProjectConversionContext context =
                new ProjectConversionContext(verbose, dryRun, stateCallback, logCallback);
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
            migrateTibcoMultiRoot(context, inputPath, outputPath, preserverStructure);
            return;
        }

        migrateTibcoInner(context, sourcePath, outputPath, preserverStructure);
    }

    private static void migrateTibcoMultiRoot(ProjectConversionContext context,
                                              Path inputPath, String outputPath, boolean preserverStructure) {
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
                        context.logState("Converting project: " + childDir);
                        context.log(LoggingUtils.Level.INFO, "Converting project: " + childDir);

                        Optional<MigrationResult> result =
                                migrateTibcoInner(context, childDir.toString(), childOutputPath,
                                        preserverStructure);

                        if (result.isPresent()) {
                            TibcoAnalysisReport report = result.get().report();

                            // Create individual report for this project
                            Path projectOutputPath = Paths.get(childOutputPath);
                            try {
                                writeAnalysisReport(context, projectOutputPath, report);
                            } catch (IOException e) {
                                context.log(SEVERE,
                                        "Error creating individual analysis report for project: " + childName);
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
            context.log(SEVERE, "Error reading directory: " + inputPath);
            System.exit(1);
            return;
        }

        // Create combined summary report
        Path summaryOutputPath = outputPath != null ? Paths.get(outputPath) : inputPath;
        try {
            writeCombinedSummaryReport(context, summaryOutputPath, projectSummaries);
        } catch (IOException e) {
            context.log(SEVERE, "Error creating combined summary report");
        }
    }

    private static Optional<MigrationResult> migrateTibcoInner(ProjectConversionContext context,
                                                               String sourcePath, String outputPath,
                                                               boolean preserverStructure) {
        Path inputPath;
        try {
            inputPath = Paths.get(sourcePath).toRealPath();
        } catch (IOException e) {
            context.log(SEVERE, "Invalid path: " + sourcePath);
            System.exit(1);
            return Optional.empty();
        }

        if (Files.isRegularFile(inputPath)) {
            String inputRootDirectory = inputPath.getParent().toString();
            String targetPath = outputPath != null ? outputPath : inputRootDirectory + "_converted";
            return migrateTibcoProject(context, inputRootDirectory, targetPath, preserverStructure);
        } else if (Files.isDirectory(inputPath)) {
            String targetPath = outputPath != null ? outputPath : inputPath + "_converted";
            return migrateTibcoProject(context, inputPath.toString(), targetPath, preserverStructure);
        } else {
            context.log(SEVERE, "Invalid path: " + inputPath);
            System.exit(1);
            return Optional.empty();
        }
    }

    static Optional<MigrationResult> migrateTibcoProject(ProjectConversionContext cx,
                                                         String projectPath, String targetPath,
                                                         boolean preserverStructure) {
        Path targetDir = Paths.get(targetPath);
        Path codeGenDir = targetDir;
        java.nio.file.Path tempDir = null;
        SerializingContext serializingContext = new SerializingContext();
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
        ConversionResult result;
        try {
            result = TibcoToBalConverter.convertProject(cx, projectPath);
        } catch (Exception e) {
            cx.logState("Unrecoverable error while converting project " + projectPath);
            cx.log(SEVERE,
                    "Unrecoverable error while converting project" + projectPath + ": " + e.getMessage());
            System.exit(1);
            return Optional.empty();
        }
        List<BallerinaModel.TextDocument> textDocuments;
        if (preserverStructure) {
            textDocuments = result.module().textDocuments();
        } else {
            BallerinaModel.Module biModule = new BICodeConverter().convert(result.module());
            textDocuments = biModule.textDocuments();
        }

        for (BallerinaModel.TextDocument textDocument : textDocuments) {
            try {
                writeTextDocument(serializingContext, textDocument, codeGenDir);
            } catch (IOException e) {
                cx.log(SEVERE, "Failed to create output file" + textDocument.documentName());
            }
        }
        try {
            addProjectArtifacts(cx, codeGenDir.toString());
        } catch (IOException e) {
            cx.log(SEVERE, "Error adding project artifacts");
        }
        try {
            appendASTToFile(serializingContext, codeGenDir, "types.bal", result.types());
        } catch (IOException e) {
            cx.log(SEVERE, "Error creating types files");
        }
        TibcoAnalysisReport report = result.report();
        report.lineCount(serializingContext.linesGenerated);
        try {
            createTargetDirectoryIfNeeded(cx, targetDir);
            writeAnalysisReport(cx, targetDir, report);
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
        return Optional.of(new MigrationResult(report));
    }

    private static void writeAnalysisReport(ProjectConversionContext context, Path targetDir,
                                            TibcoAnalysisReport report) throws IOException {
        Path reportFilePath = targetDir.resolve("report.html");
        String htmlContent = report.toHTML();
        Files.writeString(reportFilePath, htmlContent);
        context.log(LoggingUtils.Level.INFO, "Created analysis report at: " + reportFilePath);
    }

    private static void writeCombinedSummaryReport(ProjectConversionContext context, Path targetDir,
                                                   List<ProjectSummary> projectSummaries)
            throws IOException {
        Path reportFilePath = targetDir.resolve("combined_summary_report.html");
        CombinedSummaryReport combinedReport = new CombinedSummaryReport("Combined Migration Assessment",
                projectSummaries);
        String htmlContent = combinedReport.toHTML();
        Files.writeString(reportFilePath, htmlContent);
        context.log(
                LoggingUtils.Level.INFO, "Created combined summary report at: " + reportFilePath);
    }

    private static void writeTextDocument(SerializingContext serializingContext,
                                          BallerinaModel.TextDocument textDocument, Path targetDir) throws IOException {
        String fileName = textDocument.documentName();
        SyntaxTree st = new CodeGenerator(textDocument).generateSyntaxTree();
        String source = st.toSourceCode();
        int lineCount = source.split("\r?\n").length;
        serializingContext.addLines(lineCount);
        writeASTToFile(targetDir, fileName, st);
    }

    private static void writeASTToFile(Path targetDir, String fileName, SyntaxTree st) throws IOException {
        Path filePath = Path.of(targetDir + "/" + fileName);
        Files.writeString(filePath, st.toSourceCode());
    }


    private static void appendASTToFile(SerializingContext serializingContext, Path targetDir, String fileName,
                                        SyntaxTree st) throws IOException {
        Path filePath = Path.of(targetDir + "/" + fileName);
        String newContent = st.toSourceCode();
        int lineCount = newContent.split("\r?\n").length;
        serializingContext.addLines(lineCount);

        if (Files.exists(filePath)) {
            // If file exists, read the existing content and append the new content to it
            String existingContent = Files.readString(filePath);
            Files.writeString(filePath, newContent + existingContent);
        } else {
            // If file doesn't exist, just write the new content
            Files.writeString(filePath, newContent);
        }
    }

    private static void createTargetDirectoryIfNeeded(ProjectConversionContext context,
                                                      Path targetDir) throws IOException {
        if (Files.exists(targetDir)) {
            return;
        }
        Files.createDirectories(targetDir);
        context.log(LoggingUtils.Level.INFO, "Created target directory: " + targetDir);
    }

    private static void addProjectArtifacts(ProjectConversionContext cx, String targetPath)
            throws IOException {
        String org = "converter";
        String name = Paths.get(targetPath).getFileName().toString();
        String version = "0.1.0";
        String distribution = "2201.12.0";

        Path tomlPath = Paths.get(targetPath, "Ballerina.toml");
        StringBuilder tomlContent = new StringBuilder("""
                [package]
                org = "%s"
                name = "%s"
                version = "%s"
                distribution = "%s"

                [build-options]
                observabilityIncluded = true""".formatted(org, name, version, distribution));
        for (var each : cx.javaDependencies()) {
            tomlContent.append("\n");
            tomlContent.append(each.dependencyParam);
        }

        Files.writeString(tomlPath, tomlContent.toString());
        cx.log(LoggingUtils.Level.INFO, "Created Ballerina.toml file at: " + tomlPath);
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

    public static class SerializingContext {
        private long linesGenerated = 0;
        public void addLines(int count) {
            linesGenerated += count;
        }
        public long getLinesGenerated() {
            return linesGenerated;
        }
    }
}
