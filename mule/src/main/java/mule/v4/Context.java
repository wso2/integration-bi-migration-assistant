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
import mule.common.MultiRootContext;
import mule.v4.dataweave.converter.DWConstruct;
import mule.v4.model.MuleModel;
import mule.v4.model.MuleModel.AnypointMqConfig;
import mule.v4.model.MuleModel.ApiKitConfig;
import mule.v4.model.MuleModel.DbConfig;
import mule.v4.model.MuleModel.HttpListener;
import mule.v4.model.ParseResult;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static common.BallerinaModel.Import;
import static common.BallerinaModel.ModuleTypeDef;
import static common.BallerinaModel.ModuleVar;
import static mule.v4.MuleToBalConverter.generateTextDocument;
import static mule.v4.model.MuleModel.ErrorHandler;
import static mule.v4.model.MuleModel.GlobalProperty;
import static mule.v4.model.MuleModel.HTTPListenerConfig;
import static mule.v4.model.MuleModel.HTTPRequestConfig;
import static mule.v4.model.MuleModel.MuleImport;
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
    public String currentServiceBasePath;
    public String currentResourcePath;
    public String currentListenerPort;
    public String currentApiKitBasePath;

    public Context(List<File> xmlFiles, List<File> yamlFiles, Path muleAppDir, MuleVersion muleVersion,
                   List<File> propertyFiles, String sourceName, boolean dryRun, boolean keepStructure,
                   mule.common.MuleLogger logger, mule.common.ProjectMigrationResult result,
                   mule.common.MultiRootContext multiRootContext) {
        super(xmlFiles, yamlFiles, muleAppDir, muleVersion, propertyFiles, sourceName, dryRun, keepStructure,
                logger, result, multiRootContext);
        isStandaloneBalFile = muleAppDir == null;
    }

    public Context(List<File> xmlFiles, List<File> yamlFiles, mule.common.MuleLogger logger) {
        this(xmlFiles, yamlFiles, null, null, Collections.emptyList(), null, false, false, logger, null, null);
    }

    @Override
    public MigrationMetrics<? extends DWConstructBase> getMigrationMetrics() {
        return migrationMetrics;
    }

    @Override
    public boolean isStandaloneBalFile() {
        return isStandaloneBalFile;
    }

    public HTTPListenerConfig getDefaultHttpListenerConfig() {
        return projectCtx.httpListenerConfigMaps.stream().flatMap(each -> each.values().stream()).findFirst()
                .orElse(null);
    }

    @Override
    public void parseAllFiles() {
        for (File xmlFile : xmlFiles) {
            currentFileCtx =
                    this.fileContexts.computeIfAbsent(xmlFile,
                            (path) -> new FileContext(path.getPath(), projectCtx));
            try {
                parseResults.put(xmlFile, readMuleConfigFromRoot(this, getXMLNavigator(),
                        xmlFile.getPath()));
            } catch (Exception ex) {
                logger.logSevere("Error while parsing %s".formatted(xmlFile));
            }
        }
    }

    @Override
    public List<BallerinaModel.TextDocument> codeGen() {
        return parseResults.keySet().stream().filter(f -> {
                    var parserResult = parseResults.get(f);
                    return parserResult != null && parserResult.flows() != null && parserResult.subFlows() != null;
                })
                .sorted((f1, f2) -> {
                    boolean f1HasHttp = hasHttpListener(parseResults.get(f1));
                    boolean f2HasHttp = hasHttpListener(parseResults.get(f2));
                    if (f1HasHttp == f2HasHttp) {
                        return 0;
                    }
                    return f1HasHttp ? -1 : 1; // HTTP listeners first
                }).map(xmlFile -> {
                    currentFileCtx = this.fileContexts.get(xmlFile);
                    ParseResult parseResult = parseResults.get(xmlFile);
                    assert currentFileCtx != null : "We should have created file ctx when we parse the file";

                    String balFileName = muleAppDir != null
                            ? muleAppDir.relativize(xmlFile.toPath()).toString().replace(File.separator, ".")
                            .replace(".xml", "")
                            : "internal.bal";
                    try {
                        return generateTextDocument(this, balFileName, parseResult.flows(),
                                parseResult.subFlows());
                    } catch (Exception e) {
                        logger.logSevere("Unrecoverable error while generating code for %s".formatted(xmlFile));
                        return null;
                    }
                }).filter(Objects::nonNull).collect(Collectors.toCollection(ArrayList::new));
    }

    private boolean hasHttpListener(ParseResult parseResult) {
        return parseResult.flows().stream()
                .anyMatch(flow -> flow.source().filter(source -> source instanceof HttpListener).isPresent());
    }

    @Override
    protected MuleXMLNavigator getXMLNavigator() {
        return new MuleXMLNavigator(this.migrationMetrics, mule.v4.model.MuleXMLTag::isCompatible);
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

    public String getApiKitBasePath(ApiKitConfig apiKitConfig) {
        return getApiKitBasePath(apiKitConfig.name());
    }

    public String getApiKitBasePath(String configName) {
        String key = configName == null || configName.isBlank() ? "__defaultApiKitConfig__" : configName;
        return projectCtx.apiKitBasePaths.computeIfAbsent(key,
                ignored -> "/apikit" + projectCtx.apiKitBasePaths.size());
    }

    public static class FileContext {
        public final String filePath;
        public final GlobalConfigs configs;
        public final BalConstructs balConstructs;
        private final Map<String, ModuleVar> configurables;

        FileContext(String filePath, ProjectContext projectContext) {
            this.filePath = filePath;
            this.configs = new GlobalConfigs(projectContext);
            this.balConstructs = new BalConstructs(projectContext);
            this.configurables = new LinkedHashMap<>();
        }
    }

    public static class ProjectContext {
        private final List<MuleToBalConverter.JavaDependencies> javaDependencies = new ArrayList<>();
        public final Counters counters = new Counters();

        public final LinkedHashMap<String, String> vars = new LinkedHashMap<>();
        public final LinkedHashMap<String, String> attributes = new LinkedHashMap<>();
        public final HashMap<String, String> vmQueueNameToBalFuncMap = new LinkedHashMap<>();

        // Track last HTTP service across all files for API Kit resource merging
        public BallerinaModel.Service lastHttpService = null;

        // Shared mule configs
        List<HashMap<String, ApiKitConfig>> apiKitConfigMaps = new ArrayList<>();
        List<HashMap<String, HTTPListenerConfig>> httpListenerConfigMaps = new ArrayList<>();
        List<HashMap<String, HTTPRequestConfig>> httpRequestConfigMaps = new ArrayList<>();
        List<HashMap<String, DbConfig>> dbConfigMaps = new ArrayList<>();
        List<HashMap<String, AnypointMqConfig>> anypointMqConfigMaps = new ArrayList<>();

        // Shared bal constructs
        public final HashMap<String, ModuleVar> configurableVars = new LinkedHashMap<>();
        List<HashMap<String, ModuleTypeDef>> typeDefMaps = new ArrayList<>();
        List<HashMap<String, BallerinaModel.Function>> functionMaps = new ArrayList<>();

        private final Map<String, String> apiKitBasePaths = new HashMap<>();

        public void addJavaDependency(MuleToBalConverter.JavaDependencies dependencies) {
            javaDependencies.add(dependencies);
        }

        public List<MuleToBalConverter.JavaDependencies> javaDependencies() {
            return Collections.unmodifiableList(javaDependencies);
        }

        public ApiKitConfig getApiKitConfig(String key) {
            return getValueFromMaps(apiKitConfigMaps, key);
        }

        public HTTPListenerConfig getHttpListenerConfig(String key) {
            return getValueFromMaps(httpListenerConfigMaps, key);
        }

        public HTTPRequestConfig getHttpRequestConfig(String key) {
            return getValueFromMaps(httpRequestConfigMaps, key);
        }

        public AnypointMqConfig getAnypointMqConfig(String key) {
            return getValueFromMaps(anypointMqConfigMaps, key);
        }

        public boolean configurableVarExists(String key) {
            return configurableVars.containsKey(key);
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

        public final HashMap<String, ApiKitConfig> apiKitConfigs = new LinkedHashMap<>();
        public final HashMap<String, HTTPListenerConfig> httpListenerConfigs = new LinkedHashMap<>();
        public final HashMap<String, HTTPRequestConfig> httpRequestConfigs = new LinkedHashMap<>();
        public final HashMap<String, DbConfig> dbConfigs = new LinkedHashMap<>();
        public final HashMap<String, AnypointMqConfig> anypointMqConfigs = new LinkedHashMap<>();
        public final List<ErrorHandler> globalErrorHandlers = new ArrayList<>();
        public final List<GlobalProperty> globalProperties = new ArrayList<>();
        public final List<MuleImport> imports = new ArrayList<>();
        public final List<UnsupportedBlock> unsupportedBlocks = new ArrayList<>();

        GlobalConfigs(ProjectContext projCtx) {
            projCtx.apiKitConfigMaps.add(apiKitConfigs);
            projCtx.httpListenerConfigMaps.add(httpListenerConfigs);
            projCtx.httpRequestConfigMaps.add(httpRequestConfigs);
            projCtx.dbConfigMaps.add(dbConfigs);
            projCtx.anypointMqConfigMaps.add(anypointMqConfigs);
        }
    }

    public static class BalConstructs {
        public final HashSet<Import> imports = new LinkedHashSet<>();
        public final HashMap<String, ModuleTypeDef> typeDefs = new LinkedHashMap<>();
        public final HashMap<String, ModuleVar> moduleVars = new LinkedHashMap<>();
        public final HashMap<String, BallerinaModel.Function> commonFunctions = new LinkedHashMap<>();
        // TODO: merge `commonFunctions` and `functions`
        public final List<BallerinaModel.Function> functions = new ArrayList<>();
        public final List<String> utilFunctions = new ArrayList<>();

        BalConstructs(ProjectContext projCtx) {
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
        // TODO: add a warning for this
        return Optional.empty();
    }

    @Override
    public Optional<MultiRootContext.LookupResult> lookupResultFlowFunc(String flowName) {
        return parseResults.values().stream()
                .flatMap(each -> Stream.concat(each.flows().stream().map(MuleModel.Flow::name),
                        each.subFlows().stream().map(MuleModel.SubFlow::name))).filter(f -> f.equals(flowName))
                .map(ignored -> new MultiRootContext.LookupResult(getOrgName(), getProjectName(),
                        mule.v4.ConversionUtils.convertToBalIdentifier(flowName)))
                .findFirst();
    }

    @Override
    public void addFunction(BallerinaModel.Function function) {
        this.currentFileCtx.balConstructs.functions.add(function);
    }

    public void addConfigurableVar(String varName, ModuleVar var) {
        assert varName.equals(var.name());
        this.currentFileCtx.configurables.put(varName, var);
        this.projectCtx.configurableVars.put(varName, var);
    }

    @Override
    public Collection<ModuleVar> getCurrentFileConfigurableVars() {
        return this.currentFileCtx.configurables.values();
    }

    @Override
    public Collection<ModuleVar> getConfigurableVars() {
        return this.projectCtx.configurableVars.values();
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
        public int requestPathBuilderCount = 0;
    }
}
