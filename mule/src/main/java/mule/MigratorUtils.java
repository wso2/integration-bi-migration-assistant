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

import common.ConversionUtils;
import mule.common.MuleLogger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MigratorUtils {

    public static final String BAL_PROJECT_SUFFIX = "_ballerina";
    public static final String BAL_DEFAULT_PROJECT_ORG = "mule_migrator";

    public static final List<String> MULE_V4_ONLY_TERMS = Arrays.asList(
            "doc:id", "xmlns:ee", "<ee:message", "<ee:transform", "<ttp:listener-connection"
    );

    public static final List<String> MULE_V3_ONLY_TERMS = Arrays.asList(
            "xmlns:dw", "<dw:transform-message"
    );


    public static String getBalProjectName(String projectNameArg, String sourceName) {
        String projectName = projectNameArg != null ? projectNameArg : sourceName;
        return ConversionUtils.escapeIdentifier(projectName);
    }

    public static String getBalOrgName(String orgNameArg) {
        String orgName = orgNameArg != null ? orgNameArg : BAL_DEFAULT_PROJECT_ORG;
        return ConversionUtils.escapeIdentifier(orgName);
    }

    public static void writeFilesFromMap(MuleLogger logger, Path targetDir, Map<String, String> files) {
        for (Map.Entry<String, String> entry : files.entrySet()) {
            writeFile(logger, targetDir, entry.getKey(), entry.getValue());
        }
    }

    public static void writeFile(MuleLogger logger, Path targetDir, String fileName, String content) {
        Path filePath = targetDir.resolve(fileName);
        try {
            Files.writeString(filePath, content, StandardCharsets.UTF_8);
            logger.logInfo("Wrote file: " + filePath);
        } catch (IOException e) {
            logger.logSevere("Error writing to file: " + filePath + ", " + e.getMessage());
        }
    }

    public static void createDirectories(MuleLogger logger, Path path) {
        logger.logInfo("Creating directories if they do not exist: " + path);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                logger.logSevere("Error creating directories: " + path);
            }
        }
    }

    public static List<Path> getImmediateSubdirectories(Path directory) throws IOException {
        Stream<Path> paths = Files.list(directory);
        return paths.filter(Files::isDirectory).collect(Collectors.toList());
    }

    public static void collectXmlFiles(File folder, List<File> xmlFiles) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    collectXmlFiles(file, xmlFiles);
                } else if (file.getName().toLowerCase().endsWith(".xml")) {
                    xmlFiles.add(file);
                }
            }
        }
    }

    public static void collectYamlAndPropertyFiles(File folder, List<File> yamlFiles, List<File> propertiesFiles) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    collectYamlAndPropertyFiles(file, yamlFiles, propertiesFiles);
                } else if (file.getName().toLowerCase().endsWith(".yaml")) {
                    yamlFiles.add(file);
                } else if (file.getName().toLowerCase().endsWith(".properties")) {
                    propertiesFiles.add(file);
                }
            }
        }
    }

    /**
     * Detects the Mule version based on the presence of specific files in the given directory.
     *
     * @param muleSourceDir The path to the Mule source directory.
     * @return An Optional containing the detected MuleVersion, or empty if no version could be detected.
     */
    public static @NotNull MuleMigrator.MuleVersion detectVersionForProject(Path muleSourceDir) {
        assert muleSourceDir != null;
        if (!Files.exists(muleSourceDir)) {
            // TODO:
            throw new IllegalArgumentException("Mule source directory does not exist: " + muleSourceDir);
        }

        // Mule 4 projects have a 'mule-artifact.json' file
        if (Files.exists(muleSourceDir.resolve("mule-artifact.json"))) {
            return MuleMigrator.MuleVersion.MULE_V4;
        }

        // Mule 3 projects typically have a 'mule-project.xml' or 'mule-deploy.properties' file
        if (Files.exists(muleSourceDir.resolve("mule-project.xml")) ||
                Files.exists(muleSourceDir.resolve("src").resolve("main").resolve("app")
                        .resolve("mule-deploy.properties"))) {
            return MuleMigrator.MuleVersion.MULE_V3;
        }

        // Unable to determine version, default to MULE_V3
        return MuleMigrator.MuleVersion.MULE_V3;
    }

    /**
     * Detects the Mule version based on the content of the provided XML file.
     *
     * @param xmlFilePath The path to the XML file to analyze.
     * @return The detected MuleVersion, either MULE_V3 or MULE_V4.
     */
    public static MuleMigrator.MuleVersion detectVersionForFile(Path xmlFilePath) {
        assert xmlFilePath != null;
        String content;
        try {
            content = Files.readString(xmlFilePath);
        } catch (Exception e) {
            throw new IllegalStateException("Error reading xml file: " + xmlFilePath, e);
        }

        for (String s : MULE_V4_ONLY_TERMS) {
            if (content.contains(s)) {
                return MuleMigrator.MuleVersion.MULE_V4;
            }
        }

        for (String s : MULE_V3_ONLY_TERMS) {
            if (content.contains(s)) {
                return MuleMigrator.MuleVersion.MULE_V3;
            }
        }

        // Unable to determine version, default to MULE_V3
        return MuleMigrator.MuleVersion.MULE_V3;
    }
}
