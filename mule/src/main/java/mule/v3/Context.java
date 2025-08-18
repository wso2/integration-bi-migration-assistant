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

import mule.common.ContextBase;
import mule.common.DWConstructBase;
import mule.common.MigrationMetrics;
import mule.v3.dataweave.converter.DWConstruct;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

import static common.BallerinaModel.Function;
import static common.BallerinaModel.Import;
import static common.BallerinaModel.ModuleTypeDef;
import static common.BallerinaModel.ModuleVar;
import static mule.v3.model.MuleModel.DbMSQLConfig;
import static mule.v3.model.MuleModel.DbOracleConfig;
import static mule.v3.model.MuleModel.DbGenericConfig;
import static mule.v3.model.MuleModel.DbTemplateQuery;
import static mule.v3.model.MuleModel.HTTPListenerConfig;
import static mule.v3.model.MuleModel.HTTPRequestConfig;
import static mule.v3.model.MuleModel.MuleRecord;
import static mule.v3.model.MuleModel.UnsupportedBlock;

/**
 * Context class to hold the state of the conversion process.
 */
public class Context extends ContextBase {
    public final ProjectContext projectCtx = new ProjectContext();
    public FileContext currentFileCtx;
    private boolean isStandaloneBalFile = false;
    public final MigrationMetrics<DWConstruct> migrationMetrics = new MigrationMetrics<>();

    @Override
    public MigrationMetrics<? extends DWConstructBase> getMigrationMetrics() {
        return migrationMetrics;
    }

    @Override
    public void startNewFile(String filePath) {
        currentFileCtx = new FileContext(filePath, projectCtx);
    }

    @Override
    public void startStandaloneFile(String filePath) {
        currentFileCtx = new FileContext(filePath, projectCtx);
        isStandaloneBalFile = true;
    }

    @Override
    public boolean isStandaloneBalFile() {
        return isStandaloneBalFile;
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
    }
}
