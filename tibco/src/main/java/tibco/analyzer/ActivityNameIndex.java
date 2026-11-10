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

package tibco.analyzer;

import tibco.model.Scope;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Resolves activity names the way TIBCO does: a name is only unique within its scope, so a lookup starts at the
 * scope of the referring activity and walks outwards.
 *
 * @param enclosingScopes  scopes enclosing each activity, innermost first
 * @param activitiesByName named activities of each scope, keyed by name
 */
record ActivityNameIndex(Map<Scope.Flow.Activity, List<Scope>> enclosingScopes,
                         Map<Scope, Map<String, Scope.Flow.Activity>> activitiesByName) {

    ActivityNameIndex {
        assert enclosingScopes != null && activitiesByName != null;
        enclosingScopes = Map.copyOf(enclosingScopes);
        activitiesByName = Map.copyOf(activitiesByName);
    }

    static ActivityNameIndex empty() {
        return new ActivityNameIndex(Map.of(), Map.of());
    }

    static ActivityNameIndex from(Map<Scope.Flow.Activity, List<Scope>> enclosingScopes) {
        Map<Scope, Map<String, Scope.Flow.Activity>> activitiesByName = new LinkedHashMap<>();
        // Iteration follows analysis order, so when a scope repeats a name the first activity wins every run
        enclosingScopes.forEach((activity, scopes) -> {
            if (activity instanceof Scope.Flow.Activity.ActivityWithName named && named.getName().isPresent()) {
                activitiesByName.computeIfAbsent(scopes.getFirst(), ignored -> new LinkedHashMap<>())
                        .putIfAbsent(named.getName().get(), activity);
            }
        });
        return new ActivityNameIndex(enclosingScopes, activitiesByName);
    }

    Optional<Scope.Flow.Activity> find(Scope.Flow.Activity referrer, String name) {
        return enclosingScopes.getOrDefault(referrer, List.of()).stream()
                .map(scope -> activitiesByName.getOrDefault(scope, Map.of()).get(name))
                .filter(Objects::nonNull)
                .findFirst();
    }

    ActivityNameIndex combine(ActivityNameIndex other) {
        return new ActivityNameIndex(AnalysisResultImpl.combineMap(enclosingScopes, other.enclosingScopes),
                AnalysisResultImpl.combineMap(activitiesByName, other.activitiesByName));
    }
}
