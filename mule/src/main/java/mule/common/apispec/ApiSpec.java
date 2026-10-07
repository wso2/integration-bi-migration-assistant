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

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Format-independent API contract referenced by an {@code apikit:config}. Maps keep insertion order so that code
 * generated from the spec is stable across runs.
 *
 * @param title      API title
 * @param version    API version
 * @param baseUri    base URI of the first declared server
 * @param types      named types, keyed by type name
 * @param operations operations in declaration order
 */
public record ApiSpec(String title,
                      Optional<String> version,
                      Optional<String> baseUri,
                      Map<String, ApiType> types,
                      List<Operation> operations) {

    public ApiSpec {
        assert title != null && version != null && baseUri != null && types != null && operations != null;
        types = orderedCopy(types);
        operations = List.copyOf(operations);
    }

    /**
     * Finds the operation for a method and a path written in spec form, e.g. {@code /orders/{id}}.
     *
     * @param method    HTTP method, in any case
     * @param path      resource path; a trailing slash is ignored
     * @param mediaType request media type the operation must accept, when the caller knows it
     * @return the matching operation
     */
    public @NotNull Optional<Operation> findOperation(String method, String path, Optional<String> mediaType) {
        assert method != null && path != null && mediaType != null;
        String normalizedMethod = method.toLowerCase(Locale.ROOT);
        String normalizedPath = normalizePath(path);
        return operations.stream()
                .filter(op -> op.method().equals(normalizedMethod) && op.path().equals(normalizedPath))
                .filter(op -> mediaType.isEmpty() || op.requestBodies().containsKey(mediaType.get()))
                .findFirst();
    }

    private static String normalizePath(String path) {
        String withLeadingSlash = path.startsWith("/") ? path : "/" + path;
        if (withLeadingSlash.length() > 1 && withLeadingSlash.endsWith("/")) {
            return withLeadingSlash.substring(0, withLeadingSlash.length() - 1);
        }
        return withLeadingSlash;
    }

    private static <K, V> Map<K, V> orderedCopy(Map<K, V> map) {
        return Collections.unmodifiableMap(new LinkedHashMap<>(map));
    }

    /**
     * One method on one resource.
     *
     * @param method        lower-case HTTP method
     * @param path          resource path in spec form, e.g. {@code /orders/{id}}
     * @param uriParams     path parameters
     * @param queryParams   query parameters
     * @param headers       request headers
     * @param requestBodies request bodies, keyed by media type
     * @param responses     responses, keyed by status code
     * @param traits        names of the traits applied to the operation
     * @param securedBy     names of the security schemes protecting the operation
     */
    public record Operation(String method,
                            String path,
                            List<Parameter> uriParams,
                            List<Parameter> queryParams,
                            List<Parameter> headers,
                            Map<String, Body> requestBodies,
                            Map<Integer, Response> responses,
                            List<String> traits,
                            List<String> securedBy) {

        public Operation {
            assert method != null && path != null && uriParams != null && queryParams != null && headers != null;
            assert requestBodies != null && responses != null && traits != null && securedBy != null;
            assert method.equals(method.toLowerCase(Locale.ROOT)) : "Method must be lower case: " + method;
            path = normalizePath(path);
            uriParams = List.copyOf(uriParams);
            queryParams = List.copyOf(queryParams);
            headers = List.copyOf(headers);
            requestBodies = orderedCopy(requestBodies);
            responses = orderedCopy(responses);
            traits = List.copyOf(traits);
            securedBy = List.copyOf(securedBy);
        }
    }

    /**
     * @param name     the parameter's name in the request
     * @param type     its type
     * @param required whether a request must have it
     * @param example  an example value the spec gives for it
     */
    public record Parameter(String name, ApiType type, boolean required, Optional<String> example) {

        public Parameter {
            assert name != null && type != null && example != null;
        }

        public Parameter(String name, ApiType type, boolean required) {
            this(name, type, required, Optional.empty());
        }
    }

    /**
     * Bodies of one response.
     *
     * @param bodies bodies keyed by media type; empty when the response declares no body
     */
    public record Response(Map<String, Body> bodies) {

        public Response {
            assert bodies != null;
            bodies = orderedCopy(bodies);
        }
    }

    /**
     * A request or response body for one media type.
     *
     * @param type    declared type; empty when only the media type is declared
     * @param example example value as written in the spec, which is JSON text for JSON bodies
     */
    public record Body(Optional<ApiType> type, Optional<String> example) {

        public Body {
            assert type != null && example != null;
        }
    }

    public sealed interface ApiType {

        /**
         * Reference to an entry of {@link ApiSpec#types()}.
         *
         * @param name type name
         */
        record Ref(String name) implements ApiType {

            public Ref {
                assert name != null;
            }
        }

        record Scalar(ScalarKind kind, Constraints constraints) implements ApiType {

            public Scalar {
                assert kind != null && constraints != null;
            }
        }

        record StringEnum(List<String> values) implements ApiType {

            public StringEnum {
                assert values != null && !values.isEmpty();
                values = List.copyOf(values);
            }
        }

        record Array(ApiType items, Constraints constraints) implements ApiType {

            public Array {
                assert items != null && constraints != null;
            }
        }

        /**
         * An object type.
         *
         * @param parents    names of the types this one extends
         * @param properties properties declared on this type, excluding inherited ones
         * @param open       whether properties not listed here are allowed
         */
        record ObjectType(List<String> parents, Map<String, Property> properties, boolean open) implements ApiType {

            public ObjectType {
                assert parents != null && properties != null;
                parents = List.copyOf(parents);
                properties = orderedCopy(properties);
            }
        }

        record Union(List<ApiType> members) implements ApiType {

            public Union {
                assert members != null && members.size() > 1;
                members = List.copyOf(members);
            }
        }

        record Nil() implements ApiType {
        }

        record Any() implements ApiType {
        }
    }

    public record Property(ApiType type, boolean required) {

        public Property {
            assert type != null;
        }
    }

    public enum ScalarKind {
        STRING,
        INTEGER,
        NUMBER,
        BOOLEAN,
        DATE,
        TIME,
        DATE_TIME,
        DATETIME_ONLY,
        FILE
    }

    /**
     * Facets that restrict a scalar or array value.
     *
     * @param pattern   regular expression a string must match
     * @param minLength minimum string length
     * @param maxLength maximum string length
     * @param minimum   minimum numeric value
     * @param maximum   maximum numeric value
     * @param minItems  minimum array length
     * @param maxItems  maximum array length
     */
    public record Constraints(Optional<String> pattern,
                              Optional<Integer> minLength,
                              Optional<Integer> maxLength,
                              Optional<BigDecimal> minimum,
                              Optional<BigDecimal> maximum,
                              Optional<Integer> minItems,
                              Optional<Integer> maxItems) {

        public static final Constraints NONE = new Constraints(Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());

        public Constraints {
            assert pattern != null && minLength != null && maxLength != null && minimum != null;
            assert maximum != null && minItems != null && maxItems != null;
        }
    }
}
