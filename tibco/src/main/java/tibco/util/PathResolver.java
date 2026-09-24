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

package tibco.util;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Resolves a lookup path against a collection of candidates identified by their full paths.
 * <p>
 * Since a reference may be a suffix of the defining path (a TIBCO {@code processName} carries
 * no project directory segment, and may or may not be absolute) more than one candidate can
 * match. Resolution is therefore tiered: a normalized exact match wins, then a unique suffix
 * match; anything else is ambiguous and is reported back rather than silently picked.
 */
public final class PathResolver {

    private PathResolver() {
        // Utility class
    }

    /**
     * Outcome of a resolution. Exactly one of the two is populated: {@code match} when the
     * lookup resolved, {@code ambiguousCandidates} when several candidates matched equally
     * well. Both are empty when nothing matched at all.
     *
     * @param match the resolved candidate, if resolution was unambiguous
     * @param ambiguousCandidates the equally good candidates, sorted by path, if it was not
     * @param <T> the candidate type
     */
    public record Resolution<T>(Optional<T> match, List<T> ambiguousCandidates) {

        public Resolution {
            assert match != null;
            assert ambiguousCandidates != null;
            assert match.isEmpty() || ambiguousCandidates.isEmpty();
        }
    }

    /**
     * Resolves {@code lookupPath} against {@code candidates}.
     *
     * @param candidates the candidates to resolve against
     * @param pathsOf extracts the non-empty paths a candidate may be referenced by, most specific first
     * @param lookupPath the referenced path, possibly a suffix of one of those paths
     * @param <T> the candidate type
     * @return the resolution outcome
     */
    public static <T> @NotNull Resolution<T> resolve(Collection<T> candidates, Function<T, List<String>> pathsOf,
                                                     String lookupPath) {
        assert candidates != null;
        assert pathsOf != null;
        assert lookupPath != null;

        List<T> matching = candidates.stream()
                .filter(candidate -> pathsOf.apply(candidate).stream()
                        .anyMatch(path -> PathMatcher.matches(path, lookupPath)))
                .sorted(Comparator.comparing(candidate -> pathsOf.apply(candidate).getFirst()))
                .toList();
        if (matching.isEmpty()) {
            return new Resolution<>(Optional.empty(), List.of());
        }
        String normalizedLookupPath = PathMatcher.normalize(lookupPath);
        List<T> exact = matching.stream()
                .filter(candidate -> pathsOf.apply(candidate).stream()
                        .anyMatch(path -> PathMatcher.normalize(path).equals(normalizedLookupPath)))
                .toList();
        List<T> best = exact.isEmpty() ? matching : exact;
        return best.size() == 1
                ? new Resolution<>(Optional.of(best.getFirst()), List.of())
                : new Resolution<>(Optional.empty(), best);
    }
}
