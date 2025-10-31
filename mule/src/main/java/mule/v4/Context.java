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
package mule.v4;

import common.BallerinaModel;
import mule.MuleMigrator.MuleVersion;
import mule.common.ContextBase;
import mule.common.DWConstructBase;
import mule.common.MigrationMetrics;
import mule.common.MuleXMLNavigator;
import mule.v4.dataweave.converter.DWConstruct;
import mule.v4.model.MuleModel.DbConfig;
import mule.v4.model.ParseResult;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import static common.BallerinaModel.Function;
import static common.BallerinaModel.Import;
import static common.BallerinaModel.ModuleTypeDef;
import static common.BallerinaModel.ModuleVar;
import static mule.v4.MuleToBalConverter.generateTextDocument;
import static mule.v4.model.MuleModel.ErrorHandler;
import static mule.v4.model.MuleModel.GlobalProperty;
import static mule.v4.model.MuleModel.HTTPListenerConfig;
import static mule.v4.model.MuleModel.HTTPRequestConfig;
import static mule.v4.model.MuleModel.UnsupportedBlock;
import static mule.v4.reader.MuleConfigReader.readMuleConfigFromRoot;

/**
 * Context class to hold the state of the conversion process.
 */
public class Context extends ContextBase {
    public final ProjectContext projectCtx = new ProjectContext();
    public FileContext currentFileCtx;
    private final boolean isStandaloneBalFile;
    public final MigrationMetrics<DWConstruct> migrationMetrics = new MigrationMetrics<>();
    private final Map<File, ParseResult> parseResults = new HashMap<>();
    private final Map<File, FileContext> fileContexts = new HashMap<>();

    public Context(List<File> xmlFiles, List<File> yamlFiles, Path muleAppDir, MuleVersion muleVersion,
                   List<File> propertyFiles, String sourceName, boolean dryRun, boolean keepStructure,
                   mule.common.MuleLogger logger, mule.common.ProjectMigrationResult result) {
        super(xmlFiles, yamlFiles, muleAppDir, muleVersion, propertyFiles, sourceName, dryRun, keepStructure,
                logger, result);
        isStandaloneBalFile = xmlFiles.size() == 1;
    }

    public Context(List<File> xmlFiles, List<File> yamlFiles) {
        this(xmlFiles, yamlFiles, null, null, Collections.emptyList(), null, false, false, null, null);
    }

    @Override
    public MigrationMetrics<? extends DWConstructBase> getMigrationMetrics() {
        return migrationMetrics;
    }

    @Override
    public boolean isStandaloneBalFile() {
        return isStandaloneBalFile;
    }

    @Override
    public void parseAllFiles() {
        for (File xmlFile : xmlFiles) {
            currentFileCtx =
                    this.fileContexts.computeIfAbsent(xmlFile,
                            (path) -> new FileContext(path.getPath(), projectCtx));
            parseResults.put(xmlFile, readMuleConfigFromRoot(this, getXMLNavigator(),
                    xmlFile.getPath()));
        }
    }

    @Override
    public List<BallerinaModel.TextDocument> codeGen() {
        List<BallerinaModel.TextDocument> result = new ArrayList<>();
        for (File xmlFile : parseResults.keySet()) {
            currentFileCtx = this.fileContexts.get(xmlFile);
            ParseResult parseResult = parseResults.get(xmlFile);
            assert currentFileCtx != null : "We should have created file ctx when we parse the file";
            String balFileName = muleAppDir != null ?
                    muleAppDir.relativize(xmlFile.toPath()).toString().replace(File.separator, ".")
                            .replace(".xml", "") : "internal.bal";
            result.add(generateTextDocument(this, balFileName, parseResult.flows(), parseResult.subFlows()));
        }
        return result;
    }

    @Override
    protected MuleXMLNavigator getXMLNavigator() {
        return new MuleXMLNavigator(this.migrationMetrics, mule.v3.model.MuleXMLTag::isCompatible);
    }

    @Override
    public List<ModuleTypeDef> createContextTypeDefns() {
        return mule.v4.MuleToBalConverter.createContextTypeDefns(this);
    }

    @Override
    public List<Import> getContextImports() {
        List<Import> contextImports = new ArrayList<>();
        if (!projectCtx.attributes.isEmpty()) {
            // TODO: at the moment only http provides 'attributes'
            contextImports.add(new Import("ballerina", "http"));
        }
        return contextImports;
    }

    @Override
    public void appendJavaDependencies(StringBuilder tomlContent) {
        for (var each : projectCtx.javaDependencies()) {
            tomlContent.append("\n");
            tomlContent.append(each.dependencyParam);
        }
    }

    public static class FileContext {
        public final String filePath;
        public final GlobalConfigs configs;
        public final BalConstructs balConstructs;

        FileContext(String filePath, ProjectContext projectContext) {
            this.filePath = filePath;
            this.configs = new GlobalConfigs(projectContext);
            this.balConstructs = new BalConstructs(projectContext);
        }
    }

    public static class ProjectContext {
        private final List<MuleToBalConverter.JavaDependencies> javaDependencies = new ArrayList<>();
        public final Counters counters = new Counters();

        public final LinkedHashMap<String, String> vars = new LinkedHashMap<>();
        public final LinkedHashMap<String, String> attributes = new LinkedHashMap<>();
        public final HashMap<String, String> vmQueueNameToBalFuncMap = new LinkedHashMap<>();

        // Shared mule configs
        List<HashMap<String, HTTPListenerConfig>> httpListenerConfigMaps = new ArrayList<>();
        List<HashMap<String, HTTPRequestConfig>> httpRequestConfigMaps = new ArrayList<>();
        List<HashMap<String, DbConfig>> dbConfigMaps = new ArrayList<>();

        // Shared bal constructs
        List<HashMap<String, ModuleVar>> configurableVarMaps = new ArrayList<>();
        List<HashMap<String, ModuleTypeDef>> typeDefMaps = new ArrayList<>();
        List<HashMap<String, Function>> functionMaps = new ArrayList<>();

        public void addJavaDependency(MuleToBalConverter.JavaDependencies dependencies) {
            javaDependencies.add(dependencies);
        }

        public List<MuleToBalConverter.JavaDependencies> javaDependencies() {
            return Collections.unmodifiableList(javaDependencies);
        }

        public HTTPListenerConfig getHttpListenerConfig(String key) {
            return getValueFromMaps(httpListenerConfigMaps, key);
        }

        public HTTPRequestConfig getHttpRequestConfig(String key) {
            return getValueFromMaps(httpRequestConfigMaps, key);
        }

        public boolean configurableVarExists(String key) {
            return containsKeyInMaps(configurableVarMaps, key);
        }

        public boolean typeDefExists(String key) {
            return containsKeyInMaps(typeDefMaps, key);
        }

        public boolean functionExists(String key) {
            return containsKeyInMaps(functionMaps, key);
        }

        private <T> T getValueFromMaps(List<HashMap<String, T>> maps, String key) {
            for (HashMap<String, T> map : maps) {
                T value = map.get(key);
                if (value != null) {
                    return value;
                }
            }
            return null;
        }

        private <T> boolean containsKeyInMaps(List<HashMap<String, T>> maps, String key) {
            return maps.stream().anyMatch(map -> map.containsKey(key));
        }
    }

    public static class GlobalConfigs {
        public final HashMap<String, HTTPListenerConfig> httpListenerConfigs = new LinkedHashMap<>();
        public final HashMap<String, HTTPRequestConfig> httpRequestConfigs = new LinkedHashMap<>();
        public final HashMap<String, DbConfig> dbConfigs = new LinkedHashMap<>();
        public final List<ErrorHandler> globalErrorHandlers = new ArrayList<>();
        public final List<GlobalProperty> globalProperties = new ArrayList<>();
        public final List<UnsupportedBlock> unsupportedBlocks = new ArrayList<>();

        GlobalConfigs(ProjectContext projCtx) {
            projCtx.httpListenerConfigMaps.add(httpListenerConfigs);
            projCtx.httpRequestConfigMaps.add(httpRequestConfigs);
            projCtx.dbConfigMaps.add(dbConfigs);
        }
    }

    public static class BalConstructs {
        public final HashSet<Import> imports = new LinkedHashSet<>();
        public final HashMap<String, ModuleTypeDef> typeDefs = new LinkedHashMap<>();
        public final HashMap<String, ModuleVar> moduleVars = new LinkedHashMap<>();
        public final HashMap<String, ModuleVar> configurableVars = new LinkedHashMap<>();
        public final HashMap<String, Function> commonFunctions = new LinkedHashMap<>();
        // TODO: merge `commonFunctions` and `functions`
        public final List<Function> functions = new ArrayList<>();
        public final List<String> utilFunctions = new ArrayList<>();

        BalConstructs(ProjectContext projCtx) {
            projCtx.configurableVarMaps.add(configurableVars);
            projCtx.typeDefMaps.add(typeDefs);
            projCtx.functionMaps.add(commonFunctions);
        }
    }

    public void addImport(Import imp) {
        this.currentFileCtx.balConstructs.imports.add(imp);
    }

    /**
     * Converts a flow name to a Ballerina function reference.
     *
     * @param flowName the flow name to convert
     * @return the Ballerina function reference
     */
    @NotNull
    public String getFlowFuncRef(String flowName) {
        return mule.v4.ConversionUtils.convertToBalIdentifier(flowName);
    }

    public static class Counters {
        public int invokeEndPointMethodCount = 0;
        public int dwMethodCount = 0;
        public int dbQueryVarCount = 0;
        public int dbStreamVarCount = 0;
        public int dbSelectVarCount = 0;
        public int enricherFuncCount = 0;
        public int asyncFuncCount = 0;
        public int payloadVarCount = 0;
        public int clientResultVarCount = 0;
        public int vmReceiveFuncCount = 0;
        public int foreachIteratorCount = 0;
        public int originalPayloadVarCount = 0;
        public int scatterGatherVarCount = 0;
        public int scatterGatherWorkerCount = 0;
        public int workerWaitVarCount = 0;
        public int firstSuccessfulCount = 0;
        public int firstSuccessfulFuncCount = 0;
    }
}
