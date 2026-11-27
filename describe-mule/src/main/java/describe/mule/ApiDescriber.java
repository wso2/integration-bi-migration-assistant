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
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package describe.mule;

import describe.model.DescriptionModel.Api;
import describe.model.DescriptionModel.ApiOperation;
import describe.model.DescriptionModel.ApiPolicies;
import describe.model.DescriptionModel.ApiSpecRef;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import mule.common.apispec.ApiKitFlowName;
import mule.common.apispec.ApiSpec;
import mule.common.apispec.ApiSpecResolver;
import mule.common.apispec.ApiSpecResult;
import mule.v4.Context;
import mule.v4.model.MuleModel;
import mule.v4.model.MuleModel.ApiAutodiscovery;
import mule.v4.model.MuleModel.ApiKitRouter;
import mule.v4.model.MuleModel.MuleRecord;
import mule.v4.model.ParseResult;
import org.nipunaml.ramltoopenapi.RamlToOpenApiConverter;
import org.nipunaml.ramltoopenapi.exception.ConverterException;
import org.nipunaml.ramltoopenapi.mapper.MethodMapper;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Describes the project's APIs: one per {@code apikit:config}, with the operations of its spec and the flows that
 * serve them. The converter's {@code ApiSpec} keeps no operation text, so the text comes from our own read of the
 * same spec file (D7).
 */
final class ApiDescriber {

    private final Map<String, String> apiIds = new HashMap<>();
    private final List<Api> apis = new ArrayList<>();

    ApiDescriber(Context ctx, Path projectDir, Map<String, String> flowIds) {
        // The converter keeps its configs in the order the files were read, which is not stable, so sort by name
        List<String> configNames = new ArrayList<>(ctx.projectCtx.apiSpecs.keySet());
        configNames.sort(Comparator.naturalOrder());
        Map<String, MuleModel.Flow> routerFlows = routerFlows(ctx);
        for (String configName : configNames) {
            String apiId = "a" + (apis.size() + 1);
            apiIds.put(configName, apiId);
            apis.add(api(ctx, projectDir, flowIds, configName, apiId,
                    Optional.ofNullable(routerFlows.get(configName))));
        }
    }

    // apikit:config name to API ID
    Map<String, String> apiIds() {
        return apiIds;
    }

    List<Api> apis() {
        return apis;
    }

    // The flow whose apikit:router names each config, found the way Context.checkApiContracts finds it
    static Map<String, MuleModel.Flow> routerFlows(Context ctx) {
        Map<String, MuleModel.Flow> routerFlows = new HashMap<>();
        for (ParseResult parseResult : ctx.parseResults().values()) {
            for (MuleModel.Flow flow : parseResult.flows()) {
                for (MuleRecord block : flow.flowBlocks()) {
                    if (block instanceof ApiKitRouter router) {
                        routerFlows.put(router.configRef(), flow);
                    }
                }
            }
        }
        return routerFlows;
    }

    private static Api api(Context ctx, Path projectDir, Map<String, String> flowIds, String configName,
                           String apiId, Optional<MuleModel.Flow> routerFlow) {
        String apiRef = ctx.projectCtx.getApiKitConfig(configName).api();
        List<ApiKitFlowName> routes = new ArrayList<>(ctx.projectCtx.apiKitFlows.getOrDefault(configName, List.of()));
        routes.sort(Comparator.comparingInt(route -> flowNumber(flowIds, route)));
        List<ApiOperation> operations = new ArrayList<>();
        List<String> traits = new ArrayList<>();
        ApiSpecRef specRef;
        if (ctx.projectCtx.apiSpecs.get(configName) instanceof ApiSpecResult.Loaded loaded) {
            ApiSpec spec = loaded.spec();
            specRef = new ApiSpecRef(loaded.location(), nonEmpty(spec.title()), spec.version());
            Map<String, OperationText> texts = operationTexts(projectDir, apiRef);
            // A flow serves the spec operation with its exact method and path, as genApiKitSource finds it
            Map<ApiSpec.Operation, String> flowsByOperation = new IdentityHashMap<>();
            List<ApiKitFlowName> routesWithoutOperation = new ArrayList<>();
            for (ApiKitFlowName route : routes) {
                Optional<ApiSpec.Operation> operation = spec.findOperation(route.method(), route.path(),
                        Optional.empty());
                if (operation.isPresent()) {
                    flowsByOperation.putIfAbsent(operation.get(), flowIds.get(route.flowName()));
                } else {
                    routesWithoutOperation.add(route);
                }
            }
            for (ApiSpec.Operation operation : spec.operations()) {
                OperationText text = texts.getOrDefault(textKey(operation.method(), operation.path()),
                        OperationText.NONE);
                operations.add(new ApiOperation(operation.method().toUpperCase(Locale.ROOT), operation.path(),
                        text.displayName(), text.description(), Optional.ofNullable(flowsByOperation.get(operation))));
                for (String trait : operation.traits()) {
                    if (!traits.contains(trait)) {
                        traits.add(trait);
                    }
                }
            }
            routes = routesWithoutOperation;
        } else {
            // W2: without the spec, the file is the reference as written, and the operations come from the flows
            specRef = new ApiSpecRef(apiRef.replace('\\', '/'), Optional.empty(), Optional.empty());
        }
        // Flows whose route is not in the spec are listed after the spec's operations
        for (ApiKitFlowName route : routes) {
            operations.add(new ApiOperation(route.method().toUpperCase(Locale.ROOT), route.path(), Optional.empty(),
                    Optional.empty(), Optional.of(flowIds.get(route.flowName()))));
        }
        Optional<String> autodiscovery = Optional.empty();
        for (ApiAutodiscovery candidate : ctx.projectCtx.apiAutodiscoveries) {
            if (routerFlow.isPresent() && candidate.flowRef().equals(routerFlow.get().name())
                    && autodiscovery.isEmpty()) {
                // As written: Context.autodiscoveryApiId would resolve a ${...} placeholder to its value (rule 5)
                autodiscovery = nonEmpty(candidate.apiId());
            }
        }
        Optional<ApiPolicies> policies = autodiscovery.isEmpty() && traits.isEmpty() ? Optional.empty()
                : Optional.of(new ApiPolicies(autodiscovery, traits));
        return new Api(apiId, configName, specRef, operations, policies);
    }

    // D7: the display name and description of each operation, keyed by method and path. The summary is not used:
    // raml-to-openapi copies the description or the display name into it. The display name is in the extension
    // only when the RAML sets one, so RAML's default (the method name, such as "get") never shows up.
    // The converter's own spec finder gives the same file the converter read, also from an Exchange archive.
    private static Map<String, OperationText> operationTexts(Path projectDir, String apiRef) {
        Map<String, OperationText> texts = new HashMap<>();
        try (ApiSpecResolver resolver = new ApiSpecResolver(projectDir)) {
            if (!(resolver.resolve(apiRef) instanceof ApiSpecResolver.Resolution.Found found)) {
                return texts;
            }
            OpenAPI openApi = RamlToOpenApiConverter.create().convertToOpenApi(found.specFile().toFile());
            if (openApi.getPaths() == null) {
                return texts;
            }
            for (Map.Entry<String, PathItem> path : openApi.getPaths().entrySet()) {
                for (Map.Entry<PathItem.HttpMethod, Operation> operation
                        : path.getValue().readOperationsMap().entrySet()) {
                    texts.put(textKey(operation.getKey().name(), path.getKey()),
                            new OperationText(displayName(operation.getValue()),
                                    nonEmpty(operation.getValue().getDescription())));
                }
            }
        } catch (ConverterException e) {
            // The converter read this spec already, so this is not expected; the operations are listed without text
            return texts;
        }
        return texts;
    }

    private static Optional<String> displayName(Operation operation) {
        Map<String, Object> extensions = operation.getExtensions();
        if (extensions != null && extensions.get(MethodMapper.DISPLAY_NAME_EXTENSION) instanceof String name) {
            return nonEmpty(name);
        }
        return Optional.empty();
    }

    private record OperationText(Optional<String> displayName, Optional<String> description) {
        static final OperationText NONE = new OperationText(Optional.empty(), Optional.empty());
    }

    // Same normal form as ApiSpec: lower-case method, no trailing slash
    private static String textKey(String method, String path) {
        String trimmed = path.length() > 1 && path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
        return method.toLowerCase(Locale.ROOT) + " " + trimmed;
    }

    private static int flowNumber(Map<String, String> flowIds, ApiKitFlowName route) {
        String flowId = flowIds.get(route.flowName());
        assert flowId != null : "APIkit flow without an ID: " + route.flowName();
        return Integer.parseInt(flowId.substring(1));
    }

    private static Optional<String> nonEmpty(String value) {
        return value == null || value.isBlank() ? Optional.empty() : Optional.of(value.strip());
    }
}
