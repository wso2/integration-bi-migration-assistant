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

import mule.common.apispec.ApiSpec.Operation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Describes the API Manager policies of an API. API Manager enforces them in front of a Mule application, so a
 * migrated service does not enforce them.
 */
public final class ApiPolicies {

    // The traits that API Manager's policy snippets add to a spec, such as the Client ID Enforcement one
    private static final Set<String> POLICY_TRAITS = Set.of("client-id-enforcement", "client-id-required");

    private ApiPolicies() {
    }

    /**
     * @param apiId the API Manager id of an API that a Mule application registers with autodiscovery
     * @return a line describing the autodiscovery
     */
    public static @NotNull String autodiscovery(String apiId) {
        assert apiId != null;
        return "API %s in API Manager (api-gateway:autodiscovery); check its policies there".formatted(apiId);
    }

    /**
     * @param spec an API spec
     * @return one line for each policy trait and security scheme the spec applies, with how many operations use it
     */
    public static @NotNull List<String> describe(ApiSpec spec) {
        assert spec != null;
        List<String> lines = new ArrayList<>();
        int operationCount = spec.operations().size();
        countPerName(spec, operation -> operation.traits().stream().filter(POLICY_TRAITS::contains)).forEach(
                (trait, count) -> lines.add("%s trait of the API spec, on %d of %d operations".formatted(trait, count,
                        operationCount)));
        countPerName(spec, operation -> operation.securedBy().stream()).forEach(
                (scheme, count) -> lines.add("%s security scheme of the API spec, on %d of %d operations".formatted(
                        scheme, count, operationCount)));
        return lines;
    }

    private static Map<String, Integer> countPerName(ApiSpec spec, Function<Operation, Stream<String>> names) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        spec.operations().forEach(operation -> names.apply(operation).distinct()
                .forEach(name -> counts.merge(name, 1, Integer::sum)));
        return counts;
    }
}
