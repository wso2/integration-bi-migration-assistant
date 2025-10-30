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
package mule.common;

import common.BallerinaModel;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import static common.BallerinaModel.Import;
import static common.BallerinaModel.ModuleTypeDef;

public abstract class ContextBase {

    protected final List<File> xmlFiles;
    public final List<File> yamlFiles;
    protected final Path muleAppDir;

    protected ContextBase(List<File> xmlFiles, List<File> yamlFiles, Path muleAppDir) {
        this.xmlFiles = xmlFiles;
        this.yamlFiles = yamlFiles;
        this.muleAppDir = muleAppDir;
    }

    public abstract MigrationMetrics<? extends DWConstructBase> getMigrationMetrics();

    public abstract boolean isStandaloneBalFile();

    public abstract void parseAllFiles();

    public abstract List<BallerinaModel.TextDocument> codeGen();

    protected abstract MuleXMLNavigator getXMLNavigator();

    /**
     * Creates context type definitions for internal types.
     *
     * @return List of module type definitions
     */
    public abstract List<ModuleTypeDef> createContextTypeDefns();

    /**
     * Returns the list of imports required for context types.
     * This is typically used when context has properties that require HTTP imports.
     *
     * @return List of imports required for context types
     */
    public abstract List<Import> getContextImports();

    /**
     * Appends Java dependencies to the TOML content.
     *
     * @param tomlContent StringBuilder to append dependencies to
     */
    public abstract void appendJavaDependencies(StringBuilder tomlContent);
}
