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
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import tibco.TibcoToBalConverter;
import tibco.analyzer.TibcoAnalysisReport;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TibcoConverter {
    private static Logger logger;

    record MigrationResult(TibcoAnalysisReport report) {
    }

    public static void migrateTibco(String sourcePath, String outputPath, boolean preserverStructure, boolean verbose,
            boolean dryRun, boolean multiRoot) {
        logger = verbose ? createDefaultLogger("migrate-tibco") : createSilentLogger("migrate-tibco");
        Path inputPath = null;
        try {
            inputPath = Paths.get(sourcePath).toRealPath();
        } catch (IOException e) {
            logger().severe("Invalid path: " + sourcePath);
            System.exit(1);
        }

        if (multiRoot) {
            if (!Files.isDirectory(inputPath)) {
                logger().severe("Error: Multi-root conversion requires a directory path, but a file was provided: "
                        + sourcePath);
                System.exit(1);
            }
            if (!dryRun) {
                logger().severe("Error: Multi-root conversion is only supported with dry run mode. "
                        + "Please use the --dry-run flag.");
                System.exit(1);
            }
            migrateTibcoMultiRoot(inputPath, outputPath, preserverStructure, verbose, dryRun);
            return;
        }

        migrateTibcoInner(sourcePath, outputPath, preserverStructure, verbose, dryRun);
    }

    private static void migrateTibcoMultiRoot(Path inputPath, String outputPath, boolean preserverStructure,
            boolean verbose, boolean dryRun) {
        TibcoAnalysisReport combinedReport;
        try {
            combinedReport = Files.list(inputPath)
                    .filter(Files::isDirectory)
                    .map(childDir -> {
                        String childName = childDir.getFileName().toString();
                        String childOutputPath;
                        if (outputPath != null) {
                            childOutputPath = Paths.get(outputPath, childName + "_converted").toString();
                        } else {
                            childOutputPath = childDir + "_converted";
                        }
                        logger().info("Converting project: " + childDir);
                        return migrateTibcoInner(childDir.toString(), childOutputPath,
                                preserverStructure, verbose, dryRun);
                    })
                    .flatMap(Optional::stream)
                    .map(MigrationResult::report)
                    .reduce(TibcoAnalysisReport.empty(), TibcoAnalysisReport::combine);
        } catch (IOException e) {
            logger().severe("Error reading directory: " + inputPath);
            System.exit(1);
            return;
        }

        Path reportOutputPath = outputPath != null ? Paths.get(outputPath) : inputPath;
        try {
            writeAnalysisReport(reportOutputPath, combinedReport);
        } catch (IOException e) {
            logger().log(Level.SEVERE, "Error creating combined analysis report", e);
        }
    }

    private static Optional<MigrationResult> migrateTibcoInner(String sourcePath, String outputPath,
            boolean preserverStructure, boolean verbose, boolean dryRun) {
        Path inputPath;
        try {
            inputPath = Paths.get(sourcePath).toRealPath();
        } catch (IOException e) {
            logger().severe("Invalid path: " + sourcePath);
            System.exit(1);
            return Optional.empty();
        }

        if (Files.isRegularFile(inputPath)) {
            String inputRootDirectory = inputPath.getParent().toString();
            String targetPath = outputPath != null ? outputPath : inputRootDirectory + "_converted";
            return migrateTibcoProject(inputRootDirectory, targetPath, preserverStructure, verbose, dryRun);
        } else if (Files.isDirectory(inputPath)) {
            String targetPath = outputPath != null ? outputPath : inputPath + "_converted";
            return migrateTibcoProject(inputPath.toString(), targetPath, preserverStructure, verbose, dryRun);
        } else {
            logger().severe("Invalid path: " + inputPath);
            System.exit(1);
            return Optional.empty();
        }
    }

    static Optional<MigrationResult> migrateTibcoProject(String projectPath, String targetPath,
            boolean preserverStructure,
            boolean verbose, boolean dryRun) {
        logger = verbose ? createDefaultLogger("migrate-tibco") : createSilentLogger("migrate-tibco");
        Path targetDir = Paths.get(targetPath);
        try {
            createTargetDirectoryIfNeeded(targetDir);
        } catch (IOException e) {
            logger().log(Level.SEVERE, "Error creating target directory: " + targetDir, e);
            System.exit(1);
            return Optional.empty();
        }
        TibcoToBalConverter.ProjectConversionContext cx =
                new TibcoToBalConverter.ProjectConversionContext(verbose, dryRun);
        ConversionResult result;
        try {
            result = TibcoToBalConverter.convertProject(cx, projectPath);
        } catch (Exception e) {
            logger().severe("Unrecoverable error while converting project");
            System.exit(1);
            return Optional.empty();
        }
        try {
            writeAnalysisReport(targetDir, result.report());
        } catch (IOException e) {
            logger().log(Level.SEVERE, "Error creating analysis report", e);
        }
        if (cx.dryRun()) {
            return Optional.of(new MigrationResult(result.report()));
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
                writeTextDocument(textDocument, targetDir);
            } catch (IOException e) {
                logger().log(Level.SEVERE, "Failed to create output file" + textDocument.documentName(), e);
            }
        }
        try {
            addProjectArtifacts(cx, targetPath);
        } catch (IOException e) {
            logger().log(Level.SEVERE, "Error adding project artifacts", e);
        }
        try {
            appendASTToFile(targetDir, "types.bal", result.types());
        } catch (IOException e) {
            logger().log(Level.SEVERE, "Error creating types files", e);
        }
        return Optional.of(new MigrationResult(result.report()));
    }

    private static void writeAnalysisReport(Path targetDir, TibcoAnalysisReport report) throws IOException {
        Path reportFilePath = targetDir.resolve("report.html");
        String htmlContent = report.toHTML();
        Files.writeString(reportFilePath, htmlContent);
        logger().info("Created analysis report at: " + reportFilePath);
    }

    private static void writeTextDocument(BallerinaModel.TextDocument textDocument, Path targetDir) throws IOException {
        String fileName = textDocument.documentName();
        SyntaxTree st = new CodeGenerator(textDocument).generateSyntaxTree();
        writeASTToFile(targetDir, fileName, st);
    }

    private static void writeASTToFile(Path targetDir, String fileName, SyntaxTree st) throws IOException {
        Path filePath = Path.of(targetDir + "/" + fileName);
        Files.writeString(filePath, st.toSourceCode());
    }


    private static void appendASTToFile(Path targetDir, String fileName, SyntaxTree st) throws IOException {
        Path filePath = Path.of(targetDir + "/" + fileName);
        String newContent = st.toSourceCode();

        if (Files.exists(filePath)) {
            // If file exists, read the existing content and append the new content to it
            String existingContent = Files.readString(filePath);
            Files.writeString(filePath, newContent + existingContent);
        } else {
            // If file doesn't exist, just write the new content
            Files.writeString(filePath, newContent);
        }
    }

    private static void createTargetDirectoryIfNeeded(Path targetDir) throws IOException {
        if (Files.exists(targetDir)) {
            return;
        }
        Files.createDirectories(targetDir);
        logger().info("Created target directory: " + targetDir);
    }

    private static void addProjectArtifacts(TibcoToBalConverter.ProjectConversionContext cx, String targetPath)
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
        logger().info("Created Ballerina.toml file at: " + tomlPath);
    }

    public static Logger logger() {
        return logger;
    }

    public static Logger createSilentLogger(String name) {
        Logger silentLogger = Logger.getLogger(name);
        silentLogger.setFilter(record ->
                record.getLevel().intValue() >= java.util.logging.Level.SEVERE.intValue());
        return silentLogger;
    }

    public static Logger createDefaultLogger(String name) {
        return Logger.getLogger(name);
    }
}
