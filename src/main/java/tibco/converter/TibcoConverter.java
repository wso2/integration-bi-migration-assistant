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

package tibco.converter;

import cli.Main;
import common.BallerinaModel;
import common.CodeGenerator;
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import tibco.TibcoToBalConverter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;

public class TibcoConverter {
    public static void migrateTibco(String[] args) {
        if (args.length < 2) {
            Main.LOGGER.severe(
                    "Usage: java -jar integration-bi-migration-assistant --tibco <path to bwp file or project> " +
                            "[-o <output path>]");
            System.exit(1);
        }
        Path inputPath = Paths.get(args[1]);
        String outputPath = null;
        if (args.length >= 4 && args[2].equals("-o")) {
            outputPath = args[3];
        }

        if (Files.isRegularFile(inputPath)) {
            if (outputPath == null) {
                outputPath = inputPath.toString().replaceAll("\\.bwp$", ".bal");
            }
            migrateTibcoFile(inputPath, outputPath);
        } else if (Files.isDirectory(inputPath)) {
            String targetPath = outputPath != null ? outputPath : inputPath + "_converted";
            migrateTibcoProject(inputPath.toString(), targetPath);
        } else {
            Main.LOGGER.severe("Invalid path: " + inputPath);
            System.exit(1);
        }
    }

    private static void migrateTibcoFile(Path inputPath, String outputPath) {
        SyntaxTree syntaxTree = TibcoToBalConverter.convertFile(inputPath.toString());
        String ballerinaCode = syntaxTree.toSourceCode();
        Path outputFilePath = Paths.get(outputPath);
        try {
            Files.writeString(outputFilePath, ballerinaCode);
            Main.LOGGER.info("Conversion successful. Output written to " + outputPath);
        } catch (Exception e) {
            Main.LOGGER.log(Level.SEVERE, "Error writing output to file: " + outputPath, e);
            System.exit(1);
        }
    }

    static void migrateTibcoProject(String projectPath, String targetPath) {
        Path targetDir = Paths.get(targetPath);
        try {
            createTargetDirectoryIfNeeded(targetDir);
        } catch (IOException e) {
            Main.LOGGER.log(Level.SEVERE, "Error creating target directory: " + targetDir, e);
            System.exit(1);
        }
        TibcoToBalConverter.ProjectConversionContext cx = new TibcoToBalConverter.ProjectConversionContext();
        ConversionResult result = TibcoToBalConverter.convertProject(cx, projectPath);
        BallerinaModel.DefaultPackage balPackage = new BallerinaModel.DefaultPackage("tibco", "sample", "0.1");
        for (BallerinaModel.TextDocument textDocument : result.module().textDocuments()) {
            try {
                writeTextDocument(result.module(), balPackage, textDocument, targetDir);
            } catch (IOException e) {
                Main.LOGGER.log(Level.SEVERE, "Failed to create output file" + textDocument.documentName(), e);
            }
        }
        try {
            addProjectArtifacts(cx, targetPath);
        } catch (IOException e) {
            Main.LOGGER.log(Level.SEVERE, "Error adding project artifacts", e);
        }
        try {
            writeASTToFile(targetDir, "types.bal", result.types());
        } catch (IOException e) {
            Main.LOGGER.log(Level.SEVERE, "Error creating types files", e);
        }
    }

    private static void writeTextDocument(BallerinaModel.Module module, BallerinaModel.DefaultPackage balPackage,
                                          BallerinaModel.TextDocument textDocument, Path targetDir) throws IOException {
        BallerinaModel.Module tmpModule = new BallerinaModel.Module(module.name(), List.of(textDocument));
        BallerinaModel ballerinaModel = new BallerinaModel(balPackage, List.of(tmpModule));
        String fileName = textDocument.documentName();
        SyntaxTree st = new CodeGenerator(ballerinaModel).generateBalCode();
        writeASTToFile(targetDir, fileName, st);
    }

    private static void writeASTToFile(Path targetDir, String fileName, SyntaxTree st) throws IOException {
        Path filePath = Path.of(targetDir + "/" + fileName);
        Files.writeString(filePath, st.toSourceCode());
    }

    private static void createTargetDirectoryIfNeeded(Path targetDir) throws IOException {
        if (Files.exists(targetDir)) {
            return;
        }
        Files.createDirectories(targetDir);
        Main.LOGGER.info("Created target directory: " + targetDir);
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
        Main.LOGGER.info("Created Ballerina.toml file at: " + tomlPath);
    }
}
