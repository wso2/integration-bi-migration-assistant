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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Resolves activity names the way TIBCO does: a name is only unique within its scope, so a lookup starts at the
 * scope of the referring activity and walks outwards.
 *
 * @param enclosingScopes  scopes enclosing each activity, innermost first
 * @param activitiesByName named activities of each scope, keyed by name, in analysis order
 */
record ActivityNameIndex(Map<Scope.Flow.Activity, List<Scope>> enclosingScopes,
                         Map<Scope, Map<String, List<Scope.Flow.Activity>>> activitiesByName) {

    ActivityNameIndex {
        assert enclosingScopes != null && activitiesByName != null;
        enclosingScopes = Map.copyOf(enclosingScopes);
        activitiesByName = activitiesByName.entrySet().stream().collect(Collectors.toUnmodifiableMap(
                Map.Entry::getKey,
                byScope -> byScope.getValue().entrySet().stream().collect(Collectors.toUnmodifiableMap(
                        Map.Entry::getKey, byName -> List.copyOf(byName.getValue())))));
    }

    /**
     * Index for analysis passes that do not track activities, so every lookup misses.
     */
    static ActivityNameIndex empty() {
        return new ActivityNameIndex(Map.of(), Map.of());
    }

    /**
     * Builds the index from the enclosing scopes recorded during analysis. The map must iterate in analysis order,
     * since that order decides which of several same-named activities a lookup returns.
     */
    static ActivityNameIndex from(Map<Scope.Flow.Activity, List<Scope>> enclosingScopes) {
        Map<Scope, Map<String, List<Scope.Flow.Activity>>> activitiesByName = new LinkedHashMap<>();
        enclosingScopes.forEach((activity, scopes) -> {
            if (activity instanceof Scope.Flow.Activity.ActivityWithName named && named.getName().isPresent()) {
                activitiesByName.computeIfAbsent(scopes.getFirst(), ignored -> new LinkedHashMap<>())
                        .computeIfAbsent(named.getName().get(), ignored -> new ArrayList<>())
                        .add(activity);
            }
        });
        return new ActivityNameIndex(enclosingScopes, activitiesByName);
    }

    /**
     * Resolves a name to a single activity; when the nearest scope repeats the name, the first one analysed wins so
     * the generated code is the same on every run.
     */
    Optional<Scope.Flow.Activity> find(Scope.Flow.Activity referrer, String name) {
        return findAll(referrer, name).stream().findFirst();
    }

    /**
     * Every activity with the given name in the nearest scope, seen from the referrer, that defines the name.
     */
    List<Scope.Flow.Activity> findAll(Scope.Flow.Activity referrer, String name) {
        return enclosingScopes.getOrDefault(referrer, List.of()).stream()
                .map(scope -> activitiesByName.getOrDefault(scope, Map.of()).getOrDefault(name, List.of()))
                .filter(activities -> !activities.isEmpty())
                .findFirst()
                .orElse(List.of());
    }

    /**
     * Merges the indexes of separately analysed processes; scopes never span processes, so no entries collide.
     */
    ActivityNameIndex combine(ActivityNameIndex other) {
        return new ActivityNameIndex(AnalysisResultImpl.combineMap(enclosingScopes, other.enclosingScopes),
                AnalysisResultImpl.combineMap(activitiesByName, other.activitiesByName));
    }
}
