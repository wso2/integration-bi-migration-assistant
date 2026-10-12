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
import common.BallerinaModel.Import;
import common.BallerinaModel.ModuleTypeDef;
import common.BallerinaModel.ModuleVar;
import mule.MuleMigrator.MuleVersion;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static common.BallerinaModel.Import;
import static common.BallerinaModel.ModuleTypeDef;
import static common.BallerinaModel.ModuleVar;

public abstract class ContextBase {

    private static final String MULE_RESOURCES_DIR_NAME = "resources";

    protected final List<File> xmlFiles;
    public final List<File> yamlFiles;
    protected final Path muleAppDir;
    public final MuleVersion muleVersion;
    public final List<File> propertyFiles;
    public final String sourceName;
    public final boolean dryRun;
    public final boolean keepStructure;
    public final MuleLogger logger;
    public final ProjectMigrationResult result;
    protected final MultiRootContext multiRootContext;
    protected final List<File> munitXmlFiles;
    private ProjectProperties projectProperties;

    protected ContextBase(List<File> xmlFiles, List<File> yamlFiles, Path muleAppDir, MuleVersion muleVersion,
                         List<File> propertyFiles, String sourceName, boolean dryRun, boolean keepStructure,
                          @NotNull MuleLogger logger, ProjectMigrationResult result,
                          MultiRootContext multiRootContext) {
        this(xmlFiles, yamlFiles, muleAppDir, muleVersion, propertyFiles, sourceName, dryRun, keepStructure,
                logger, result, multiRootContext, Collections.emptyList());
    }

    protected ContextBase(List<File> xmlFiles, List<File> yamlFiles, Path muleAppDir, MuleVersion muleVersion,
                         List<File> propertyFiles, String sourceName, boolean dryRun, boolean keepStructure,
                          @NotNull MuleLogger logger, ProjectMigrationResult result,
                          MultiRootContext multiRootContext, List<File> munitXmlFiles) {
        assert logger != null : "Logger must not be null";
        this.xmlFiles = xmlFiles;
        this.yamlFiles = yamlFiles;
        this.muleAppDir = muleAppDir;
        this.muleVersion = muleVersion;
        this.propertyFiles = propertyFiles;
        this.sourceName = sourceName;
        this.dryRun = dryRun;
        this.keepStructure = keepStructure;
        this.logger = logger;
        this.result = result;
        this.multiRootContext = multiRootContext;
        this.munitXmlFiles = munitXmlFiles;
        if (multiRootContext != null) {
            multiRootContext.register(this);
        }
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

    /**
     * Returns the {@code src/main/resources} directory of the source Mule project, which is the root that
     * {@code classpath:} resource references (and bare relative resource references) are resolved against.
     *
     * @return the project resources directory, or empty when the source layout is unknown
     */
    @NotNull
    public Optional<Path> getMuleResourcesDir() {
        if (muleAppDir == null || muleAppDir.getParent() == null) {
            return Optional.empty();
        }
        return Optional.of(muleAppDir.getParent().resolve(MULE_RESOURCES_DIR_NAME));
    }

    /**
     * Returns the root directory of the source Mule project, the one holding {@code src/main}.
     *
     * @return the project root, or empty when a single XML file is being converted
     */
    @NotNull
    public Optional<Path> getMuleProjectRoot() {
        if (muleAppDir == null) {
            return Optional.empty();
        }
        Path srcMain = muleAppDir.toAbsolutePath().normalize().getParent();
        if (srcMain == null || !srcMain.endsWith(Path.of("src", "main"))) {
            return Optional.empty();
        }
        return Optional.ofNullable(srcMain.getParent().getParent());
    }

    /**
     * Resolves an attribute value written as a single property placeholder against the project's property files.
     *
     * @param value an attribute value
     * @return see {@link ProjectProperties#resolve(String)}
     */
    @NotNull
    public Optional<String> resolveProjectProperty(String value) {
        if (projectProperties == null) {
            projectProperties = ProjectProperties.load(propertyFiles == null ? List.of() : propertyFiles,
                    yamlFiles == null ? List.of() : yamlFiles, logger);
        }
        return projectProperties.resolve(value);
    }

    public String getOrgName() {
        return result == null ? "" : result.getOrgName();
    }

    public String getProjectName() {
        return result == null ? "" : result.getProjectName();
    }

    public abstract Optional<String> getFlowFuncRef(String flowName);

    public abstract Optional<MultiRootContext.LookupResult> lookupResultFlowFunc(String flowName);

    public abstract void addFunction(common.BallerinaModel.Function function);
    /**
     * Returns all configurable module variables created during conversion.
     *
     * @return Collection of configurable ModuleVars
     */
    public abstract Collection<ModuleVar> getCurrentFileConfigurableVars();


    public abstract Collection<ModuleVar> getConfigurableVars();
}
