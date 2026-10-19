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

import mule.common.apispec.ApiContractCheck.FlowIssue;
import mule.common.apispec.ApiSpec.Operation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Compares the APIkit flows of an {@code apikit:config} with the operations of its spec. A flow implements an
 * operation when the method and the path match exactly, which is how APIkit routes requests to flows.
 */
public final class ApiContractChecker {

    private static final Pattern PATH_PARAM = Pattern.compile("\\{([^}]+)}");

    private ApiContractChecker() {
    }

    /**
     * @param configName         {@code apikit:config} name
     * @param apiRef             its {@code api} attribute
     * @param specResult         its spec, or why it is unavailable
     * @param flows              the APIkit flows of the config
     * @param autodiscoveryApiId API Manager id of the API, when the flow that routes to the config is registered with
     *                           autodiscovery
     * @return how the flows line up with the spec
     */
    public static @NotNull ApiContractCheck check(String configName, String apiRef, ApiSpecResult specResult,
                                                  List<ApiKitFlowName> flows, Optional<String> autodiscoveryApiId) {
        assert configName != null && apiRef != null && specResult != null && flows != null;
        assert autodiscoveryApiId != null;
        List<String> autodiscovery = autodiscoveryApiId.map(ApiPolicies::autodiscovery).stream().toList();
        return switch (specResult) {
            case ApiSpecResult.Unavailable unavailable -> new ApiContractCheck.Unchecked(configName, apiRef,
                    unavailable.reason(), flows.size(), autodiscovery);
            case ApiSpecResult.Loaded loaded -> checkFlows(configName, apiRef, loaded, flows,
                    Stream.concat(autodiscovery.stream(), ApiPolicies.describe(loaded.spec()).stream()).toList());
        };
    }

    private static ApiContractCheck.Checked checkFlows(String configName, String apiRef, ApiSpecResult.Loaded loaded,
                                                       List<ApiKitFlowName> flows, List<String> policies) {
        ApiSpec spec = loaded.spec();
        List<FlowIssue> flowIssues = new ArrayList<>();
        for (ApiKitFlowName flow : flows) {
            Optional<Operation> operation = spec.findOperation(flow.method(), flow.path(), Optional.empty());
            if (operation.isEmpty()) {
                flowIssues.add(new FlowIssue(flow.flowName(), missingOperationIssue(flow, spec)));
                continue;
            }
            mediaTypeIssue(flow, operation.get())
                    .ifPresent(issue -> flowIssues.add(new FlowIssue(flow.flowName(), issue)));
        }
        List<Operation> unimplemented = unimplementedOperations(spec, flows);
        return new ApiContractCheck.Checked(configName, apiRef, loaded.location(),
                spec.operations().stream().filter(operation -> !unimplemented.contains(operation))
                        .map(ApiContractChecker::route).toList(),
                unimplemented.stream().map(ApiContractChecker::route).toList(),
                flowIssues, policies);
    }

    /**
     * @param spec  an API spec
     * @param flows the APIkit flows of its {@code apikit:config}
     * @return the operations of the spec that no flow implements, in spec order; APIkit answers these with
     *         {@code APIKIT:NOT_IMPLEMENTED}
     */
    public static @NotNull List<Operation> unimplementedOperations(ApiSpec spec, List<ApiKitFlowName> flows) {
        assert spec != null && flows != null;
        Set<Operation> implemented = flows.stream()
                .flatMap(flow -> spec.findOperation(flow.method(), flow.path(), Optional.empty()).stream())
                .collect(Collectors.toSet());
        return spec.operations().stream().filter(operation -> !implemented.contains(operation)).toList();
    }

    private static String missingOperationIssue(ApiKitFlowName flow, ApiSpec spec) {
        Optional<Operation> renamedParams = closestOperation(flow, spec, ApiContractChecker::withoutParamNames);
        if (renamedParams.isPresent()) {
            return "The spec declares this route as %s; APIkit matches path parameter names exactly."
                    .formatted(route(renamedParams.get()));
        }
        Optional<Operation> sameLiterals = closestOperation(flow, spec, ApiContractChecker::withoutParams);
        if (sameLiterals.isEmpty()) {
            return "The spec declares no %s operation.".formatted(flow.route());
        }
        Set<String> queryParams = sameLiterals.get().queryParams().stream()
                .map(ApiSpec.Parameter::name)
                .collect(Collectors.toSet());
        String pathParamsDeclaredAsQuery = PATH_PARAM.matcher(flow.path()).results()
                .map(match -> match.group(1))
                .filter(queryParams::contains)
                .collect(Collectors.joining(", "));
        return "The spec declares no %s operation; the closest is %s%s.".formatted(flow.route(),
                route(sameLiterals.get()), pathParamsDeclaredAsQuery.isEmpty() ? ""
                        : ", which takes %s as query parameters".formatted(pathParamsDeclaredAsQuery));
    }

    private static Optional<Operation> closestOperation(ApiKitFlowName flow, ApiSpec spec,
                                                        Function<String, String> pathKey) {
        String flowPathKey = pathKey.apply(flow.path());
        return spec.operations().stream()
                .filter(op -> op.method().equals(flow.method()) && pathKey.apply(op.path()).equals(flowPathKey))
                .findFirst();
    }

    private static String withoutParamNames(String path) {
        return PATH_PARAM.matcher(path).replaceAll("{}");
    }

    private static String withoutParams(String path) {
        return "/" + Stream.of(path.split("/"))
                .filter(segment -> !segment.isEmpty() && !PATH_PARAM.matcher(segment).matches())
                .collect(Collectors.joining("/"));
    }

    private static Optional<String> mediaTypeIssue(ApiKitFlowName flow, Operation operation) {
        if (flow.mediaType().isEmpty() || operation.requestBodies().isEmpty()
                || operation.requestBodies().containsKey(flow.mediaType().get())) {
            return Optional.empty();
        }
        return Optional.of("The spec does not accept %s request bodies on %s; it declares %s.".formatted(
                flow.mediaType().get(), route(operation), String.join(", ", operation.requestBodies().keySet())));
    }

    private static String route(Operation operation) {
        return operation.method().toUpperCase(Locale.ROOT) + " " + operation.path();
    }
}
