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
package mule.v3;

import common.BallerinaModel;
import mule.MuleMigrator.MuleVersion;
import mule.common.ContextBase;
import mule.common.DWConstructBase;
import mule.common.MigrationMetrics;
import mule.common.MuleXMLNavigator;
import mule.common.MultiRootContext;
import mule.v3.dataweave.converter.DWConstruct;
import mule.v3.model.MuleModel;
import mule.v3.model.ParseResult;
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
import java.util.Optional;
import java.util.stream.Stream;

import static common.BallerinaModel.Function;
import static common.BallerinaModel.Import;
import static common.BallerinaModel.ModuleTypeDef;
import static common.BallerinaModel.ModuleVar;
import static mule.v3.MuleToBalConverter.generateTextDocument;
import static mule.v3.model.MuleModel.DbMSQLConfig;
import static mule.v3.model.MuleModel.DbOracleConfig;
import static mule.v3.model.MuleModel.DbGenericConfig;
import static mule.v3.model.MuleModel.DbTemplateQuery;
import static mule.v3.model.MuleModel.HTTPListenerConfig;
import static mule.v3.model.MuleModel.HTTPRequestConfig;
import static mule.v3.model.MuleModel.MuleImport;
import static mule.v3.model.MuleModel.MuleRecord;
import static mule.v3.model.MuleModel.UnsupportedBlock;

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
                   mule.common.MuleLogger logger, mule.common.ProjectMigrationResult result,
                   mule.common.MultiRootContext multiRootContext) {
        super(xmlFiles, yamlFiles, muleAppDir, muleVersion, propertyFiles, sourceName, dryRun, keepStructure,
                logger, result, multiRootContext);
        isStandaloneBalFile = xmlFiles.size() == 1;
    }

    public Context(List<File> xmlFiles, List<File> yamlFiles) {
        this(xmlFiles, yamlFiles, null, null, Collections.emptyList(), null, false, false, null, null, null);
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
                    this.fileContexts.computeIfAbsent(xmlFile, (path) -> new FileContext(path.getPath(), projectCtx));
            try {
                parseResults.put(xmlFile,
                        mule.v3.reader.MuleConfigReader.readMuleConfigFromRoot(this, getXMLNavigator(),
                                xmlFile.getPath()));
            } catch (Exception ex) {
                logger.logSevere("Error while parsing %s".formatted(xmlFile));
            }
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
            try {
                result.add(generateTextDocument(this, balFileName, parseResult.flows(),
                        parseResult.subFlows()));
            } catch (Exception e) {
                logger.logSevere("Unrecoverable error while generating code for %s".formatted(xmlFile));
            }
        }
        return result;
    }

    @Override
    protected MuleXMLNavigator getXMLNavigator() {
        return new MuleXMLNavigator(this.migrationMetrics, mule.v3.model.MuleXMLTag::isCompatible);
    }

    @Override
    public List<ModuleTypeDef> createContextTypeDefns() {
        return mule.v3.MuleToBalConverter.createContextTypeDefns(this);
    }

    @Override
    public List<Import> getContextImports() {
        List<Import> contextImports = new ArrayList<>();
        if (!projectCtx.inboundProperties.isEmpty()) {
            // TODO: at the moment only http provides 'inboundProperties'
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
            this.balConstructs = new BalConstructs();
        }
    }

    public static class ProjectContext {
        private final List<MuleToBalConverter.JavaDependencies> javaDependencies = new ArrayList<>();
        public final Counters counters = new Counters();

        public final LinkedHashMap<String, String> flowVars = new LinkedHashMap<>();
        public final LinkedHashMap<String, String> sessionVars = new LinkedHashMap<>();
        public final LinkedHashMap<String, String> inboundProperties = new LinkedHashMap<>();
        public final HashMap<String, String> vmPathToBalFuncMap = new LinkedHashMap<>();

        // Shared configs
        List<HashMap<String, HTTPListenerConfig>> httpListenerConfigMaps = new ArrayList<>();
        List<HashMap<String, HTTPRequestConfig>> httpRequestConfigMaps = new ArrayList<>();
        List<HashMap<String, DbMSQLConfig>> dbMySQLConfigMaps = new ArrayList<>();
        List<HashMap<String, DbOracleConfig>> dbOracleConfigMaps = new ArrayList<>();
        List<HashMap<String, DbGenericConfig>> dbGenericConfigMaps = new ArrayList<>();
        List<HashMap<String, DbTemplateQuery>> dbTemplateQueryMaps = new ArrayList<>();
        List<HashMap<String, ModuleVar>> configurableVarMaps = new ArrayList<>();

        public void addJavaDependency(MuleToBalConverter.JavaDependencies dependencies) {
            javaDependencies.add(dependencies);
        }

        public List<MuleToBalConverter.JavaDependencies> javaDependencies() {
            return Collections.unmodifiableList(javaDependencies);
        }

        public HTTPListenerConfig getHttpListenerConfig(String key) {
            for (HashMap<String, HTTPListenerConfig> configMap : httpListenerConfigMaps) {
                HTTPListenerConfig config = configMap.get(key);
                if (config != null) {
                    return config;
                }
            }
            return null;
        }

        public HTTPRequestConfig getHttpRequestConfig(String key) {
            for (HashMap<String, HTTPRequestConfig> configMap : httpRequestConfigMaps) {
                HTTPRequestConfig config = configMap.get(key);
                if (config != null) {
                    return config;
                }
            }
            return null;
        }

        public boolean configurableVarExists(String key) {
            for (HashMap<String, ModuleVar> configVarMap : configurableVarMaps) {
                if (configVarMap.containsKey(key)) {
                    return true;
                }
            }
            return false;
        }
    }

    public static class GlobalConfigs {
        public final HashMap<String, HTTPListenerConfig> httpListenerConfigs = new LinkedHashMap<>();
        public final HashMap<String, HTTPRequestConfig> httpRequestConfigs = new LinkedHashMap<>();
        public final HashMap<String, DbMSQLConfig> dbMySQLConfigs = new LinkedHashMap<>();
        public final HashMap<String, DbOracleConfig> dbOracleConfigs = new LinkedHashMap<>();
        public final HashMap<String, DbGenericConfig> dbGenericConfigs = new LinkedHashMap<>();
        public final HashMap<String, DbTemplateQuery> dbTemplateQueries = new LinkedHashMap<>();
        public final HashMap<String, ModuleVar> configurableVars = new LinkedHashMap<>();
        public final List<MuleImport> imports = new ArrayList<>();
        public final List<MuleRecord> globalExceptionStrategies = new ArrayList<>();
        public final List<UnsupportedBlock> unsupportedBlocks = new ArrayList<>();

        GlobalConfigs(ProjectContext projCtx) {
            projCtx.httpListenerConfigMaps.add(httpListenerConfigs);
            projCtx.httpRequestConfigMaps.add(httpRequestConfigs);
            projCtx.dbMySQLConfigMaps.add(dbMySQLConfigs);
            projCtx.dbOracleConfigMaps.add(dbOracleConfigs);
            projCtx.dbGenericConfigMaps.add(dbGenericConfigs);
            projCtx.dbTemplateQueryMaps.add(dbTemplateQueries);
            projCtx.configurableVarMaps.add(configurableVars);
        }
    }

    public static class BalConstructs {
        public HashSet<Import> imports = new LinkedHashSet<>();
        public HashMap<String, ModuleTypeDef> typeDefs = new LinkedHashMap<>();
        public HashMap<String, ModuleVar> moduleVars = new LinkedHashMap<>();
        public List<Function> functions = new ArrayList<>();
        public List<String> utilFunctions = new ArrayList<>();
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
    @Override
    public Optional<String> getFlowFuncRef(String flowName) {
        Optional<MultiRootContext.LookupResult> local = lookupResultFlowFunc(flowName);
        if (local.isPresent()) {
            return local.map(MultiRootContext.LookupResult::identifier);
        }
        if (this.multiRootContext != null) {
            Optional<MultiRootContext.LookupResult> shared = multiRootContext.lookupFlow(flowName);
            if (shared.isPresent()) {
                var result = shared.get();
                addImport(new Import(result.org(), result.proj()));
                return Optional.of(result.proj() + ":" + result.identifier());
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<MultiRootContext.LookupResult> lookupResultFlowFunc(String flowName) {
        return parseResults.values().stream()
                .flatMap(each -> Stream.concat(each.flows().stream().map(MuleModel.Flow::name),
                        each.subFlows().stream().map(MuleModel.SubFlow::name))).filter(f -> f.equals(flowName))
                .map(ignored -> new MultiRootContext.LookupResult(getOrgName(), getProjectName(),
                        mule.v3.ConversionUtils.convertToBalIdentifier(flowName)))
                .findFirst();
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
        public int scatterGatherWorkerCount = 0;
        public int workerWaitVarCount = 0;
        public int scatterGatherVarCount = 0;
        public int foreachIteratorCount = 0;
        public int originalPayloadVarCount = 0;
        public int firstSuccessfulFuncCount = 0;
        public int firstSuccessfulCount = 0;
    }
}
