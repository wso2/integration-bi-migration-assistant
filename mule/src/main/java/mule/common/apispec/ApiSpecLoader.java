/*
 *  Copyright (c) 2026, WSO2 LLC. (http://www.wso2.com).
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
 *  KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package mule.common.apispec;

import mule.common.apispec.ApiSpecResolver.Resolution;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Resolves and reads the specs of a project's {@code apikit:config}s. A spec shared by several configs is read once.
 */
public final class ApiSpecLoader {

    private ApiSpecLoader() {
    }

    /**
     * @param projectRoot     root of the Mule project
     * @param apiRefsByConfig {@code api} attribute values keyed by {@code apikit:config} name
     * @return one result per config, in the order given
     */
    public static @NotNull Map<String, ApiSpecResult> load(Path projectRoot, Map<String, String> apiRefsByConfig) {
        assert projectRoot != null && apiRefsByConfig != null;
        Map<Path, ApiSpecResult> resultsBySpecFile = new HashMap<>();
        Map<String, ApiSpecResult> results = new LinkedHashMap<>();
        try (ApiSpecResolver resolver = new ApiSpecResolver(projectRoot)) {
            apiRefsByConfig.forEach((configName, apiRef) -> results.put(configName, apiRef.isBlank()
                    ? new ApiSpecResult.Unavailable("apikit:config '" + configName + "' has no api attribute")
                    : switch (resolver.resolve(apiRef)) {
                        case Resolution.Found found ->
                                resultsBySpecFile.computeIfAbsent(found.specFile(), specFile -> read(found));
                        case Resolution.NotFound notFound -> new ApiSpecResult.Unavailable(notFound.reason());
                    }));
        }
        return results;
    }

    private static ApiSpecResult read(Resolution.Found found) {
        try {
            return new ApiSpecResult.Loaded(found.specFile(), found.location(), ApiSpecReader.read(found.specFile()));
        } catch (ApiSpecReadException e) {
            return new ApiSpecResult.Unavailable(e.getMessage());
        }
    }
}
