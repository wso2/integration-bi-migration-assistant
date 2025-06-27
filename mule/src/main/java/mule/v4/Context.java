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

import mule.v4.dataweave.converter.DWConversionStats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

import static common.BallerinaModel.Function;
import static common.BallerinaModel.Import;
import static common.BallerinaModel.ModuleTypeDef;
import static common.BallerinaModel.ModuleVar;
import static mule.v4.model.MuleModel.DbMSQLConfig;
import static mule.v4.model.MuleModel.DbOracleConfig;
import static mule.v4.model.MuleModel.DbTemplateQuery;
import static mule.v4.model.MuleModel.HTTPListenerConfig;
import static mule.v4.model.MuleModel.HTTPRequestConfig;
import static mule.v4.model.MuleModel.MuleRecord;
import static mule.v4.model.MuleModel.UnsupportedBlock;

/**
 * Context class to hold the state of the conversion process.
 */
public class Context {
    public final ProjectContext projectCtx = new ProjectContext();
    public FileContext currentFileCtx;
    private boolean isStandaloneBalFile = false;
    public final MigrationMetrics migrationMetrics = new MigrationMetrics();

    public void startNewFile(String filePath) {
        currentFileCtx = new FileContext(filePath, projectCtx);
    }

    public void startStandaloneFile(String filePath) {
        currentFileCtx = new FileContext(filePath, projectCtx);
        isStandaloneBalFile = true;
    }

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
        List<HashMap<String, DbTemplateQuery>> dbTemplateQueryMaps = new ArrayList<>();
        List<HashMap<String, ModuleVar>> configurableVarMaps = new ArrayList<>();

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
        public final HashMap<String, DbTemplateQuery> dbTemplateQueries = new LinkedHashMap<>();
        public final HashMap<String, ModuleVar> configurableVars = new LinkedHashMap<>();
        public final List<MuleRecord> globalExceptionStrategies = new ArrayList<>();
        public final List<UnsupportedBlock> unsupportedBlocks = new ArrayList<>();

        GlobalConfigs(ProjectContext projCtx) {
            projCtx.httpListenerConfigMaps.add(httpListenerConfigs);
            projCtx.httpRequestConfigMaps.add(httpRequestConfigs);
            projCtx.dbMySQLConfigMaps.add(dbMySQLConfigs);
            projCtx.dbOracleConfigMaps.add(dbOracleConfigs);
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

    public static class MigrationMetrics {
        public final DWConversionStats dwConversionStats = new DWConversionStats();
        public final LinkedHashMap<String, Integer> passedXMLTags = new LinkedHashMap<>();
        public final LinkedHashMap<String, Integer> failedXMLTags = new LinkedHashMap<>();
        public final List<String> failedBlocks = new ArrayList<>();
    }
}
