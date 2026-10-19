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
package mule.v4;

import common.BallerinaModel.Parameter;
import mule.common.apispec.ApiSpec;
import mule.common.apispec.ApiSpec.ApiType;
import mule.common.apispec.ApiSpec.Operation;
import mule.common.apispec.ApiTypeGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static common.ConversionUtils.typeFrom;
import static mule.v4.ConversionUtils.convertToBalIdentifier;

/**
 * The signature of a resource function generated from a spec operation: typed path, query, header and body
 * parameters, so that Ballerina's parameter binding validates requests the way the APIkit router did. The
 * {@code http:Request} parameter stays, because converted flow bodies read the request through it.
 *
 * @param resourcePath  resource path relative to the service, e.g. {@code orders/[string id]}
 * @param parameters    query, header and payload parameters, followed by the request parameter
 * @param uriParamsInit mapping constructor for the {@code uriParams} attribute, keyed by spec parameter name
 * @param payloadRef    name of the payload parameter, when the operation's request body is bound to one
 */
public record SpecResourceSignature(String resourcePath, List<Parameter> parameters, Optional<String> uriParamsInit,
                                    Optional<String> payloadRef) {

    private static final String PAYLOAD_NAME = "payload";
    private static final Pattern PATH_PARAM = Pattern.compile("\\{([^}]+)}");
    private static final Pattern WORD_SEPARATOR = Pattern.compile("[^A-Za-z0-9]+");

    public SpecResourceSignature {
        assert resourcePath != null && parameters != null && uriParamsInit != null && payloadRef != null;
        parameters = List.copyOf(parameters);
    }

    /**
     * @param operation     the spec operation the flow implements
     * @param types         named types of the spec, to resolve parameter types that reference them
     * @param flowMediaType request media type named by the flow, for a flow that handles one of several
     * @param typeGenerator generator of the spec's types; a JSON body of a named type is bound to its record
     * @param reservedNames names the resource body declares, which no parameter may take
     * @return the signature, or empty when the path cannot be written as a Ballerina resource path
     */
    public static @NotNull Optional<SpecResourceSignature> of(Operation operation, Map<String, ApiType> types,
                                                             Optional<String> flowMediaType,
                                                             ApiTypeGenerator typeGenerator,
                                                             Set<String> reservedNames) {
        assert operation != null && types != null && flowMediaType != null && typeGenerator != null;
        assert reservedNames != null;
        Set<String> usedNames = new HashSet<>(Set.of(Constants.HTTP_REQUEST_REF, Constants.CONTEXT_REFERENCE));
        usedNames.addAll(reservedNames);
        Optional<String> payloadMediaType = payloadMediaType(operation, flowMediaType);
        Optional<String> payloadType = payloadMediaType.flatMap(SpecResourceSignature::payloadType);
        Optional<String> payloadName = payloadType.map(type -> uniqueName(PAYLOAD_NAME, "Body", usedNames));

        Map<String, ApiSpec.Parameter> uriParamsByName = new LinkedHashMap<>();
        operation.uriParams().forEach(param -> uriParamsByName.put(param.name(), param));
        List<String> segments = new ArrayList<>();
        Map<String, String> uriParamsInit = new LinkedHashMap<>();
        for (String segment : operation.path().split("/")) {
            if (segment.isEmpty()) {
                continue;
            }
            if (!segment.contains("{")) {
                segments.add(convertToBalIdentifier(segment));
                continue;
            }
            if (!PATH_PARAM.matcher(segment).matches()) {
                return Optional.empty();
            }
            String specName = segment.substring(1, segment.length() - 1);
            String type = Optional.ofNullable(uriParamsByName.get(specName))
                    .map(param -> scalarType(param.type(), types).orElse("string"))
                    .orElse("string");
            String name = uniqueName(variableName(specName), "Param", usedNames);
            segments.add("[%s %s]".formatted(type, name));
            uriParamsInit.put(specName, type.equals("string") ? name : name + ".toString()");
        }

        List<Parameter> parameters = new ArrayList<>();
        operation.queryParams().forEach(param -> parameters.add(
                parameter(param, "Query", "@http:Query", false, types, typeGenerator, usedNames)));
        operation.headers().forEach(param -> parameters.add(
                parameter(param, "Header", "@http:Header", true, types, typeGenerator, usedNames)));
        payloadType.ifPresent(type -> parameters.add(new Parameter(payloadName.orElseThrow(), typeFrom(
                recordPayloadType(type, operation.requestBodies().get(payloadMediaType.get()), typeGenerator)))
                .withAnnotation("@http:Payload")));
        parameters.add(new Parameter(Constants.HTTP_REQUEST_REF, typeFrom(Constants.HTTP_REQUEST_TYPE)));

        return Optional.of(new SpecResourceSignature(segments.isEmpty() ? "." : String.join("/", segments),
                parameters,
                uriParamsInit.isEmpty() ? Optional.empty() : Optional.of(mappingConstructor(uriParamsInit)),
                payloadName));
    }

    private static Optional<String> payloadMediaType(Operation operation, Optional<String> flowMediaType) {
        if (flowMediaType.isPresent()) {
            return operation.requestBodies().containsKey(flowMediaType.get()) ? flowMediaType : Optional.empty();
        }
        return operation.requestBodies().size() == 1
                ? Optional.of(operation.requestBodies().keySet().iterator().next())
                : Optional.empty();
    }

    // Asked only once the signature is certain, so that the generator writes no type an unused signature names
    private static String recordPayloadType(String payloadType, ApiSpec.Body body,
                                            ApiTypeGenerator typeGenerator) {
        return payloadType.equals("json")
                ? body.type().filter(typeGenerator::isNamedType).map(typeGenerator::use).orElse(payloadType)
                : payloadType;
    }

    static boolean isJsonMediaType(String mediaType) {
        String essence = mediaType.split(";")[0].trim().toLowerCase(Locale.ROOT);
        return essence.equals("application/json") || essence.endsWith("+json");
    }

    private static Optional<String> payloadType(String mediaType) {
        if (isJsonMediaType(mediaType)) {
            return Optional.of("json");
        }
        String essence = mediaType.split(";")[0].trim().toLowerCase(Locale.ROOT);
        if (essence.equals("application/xml") || essence.equals("text/xml") || essence.endsWith("+xml")) {
            return Optional.of("xml");
        }
        if (essence.equals("application/x-www-form-urlencoded")) {
            return Optional.of("map<string>");
        }
        if (essence.startsWith("multipart/")) {
            // Parts are read through http:Request, as the flow does today
            return Optional.empty();
        }
        return Optional.of(essence.startsWith("text/") ? "string" : "byte[]");
    }

    private static Parameter parameter(ApiSpec.Parameter param, String kind, String annotation,
                                       boolean alwaysAnnotate, Map<String, ApiType> types,
                                       ApiTypeGenerator typeGenerator, Set<String> usedNames) {
        String candidateName = variableName(param.name());
        String name = uniqueName(candidateName, kind, usedNames);
        boolean optional = !param.required() || isNilable(param.type(), types);
        // A renamed parameter keeps the type of its spec name, which operations that share the parameter share
        String type = typeGenerator.parameterType(candidateName.replaceFirst("^'", ""), param.type())
                .orElseGet(() -> valueType(param.type(), types));
        Parameter parameter = new Parameter(name, typeFrom(type + (optional ? "?" : "")));
        if (!name.equals(param.name())) {
            return parameter.withAnnotation("%s {name: \"%s\"}".formatted(annotation, param.name()));
        }
        return alwaysAnnotate ? parameter.withAnnotation(annotation) : parameter;
    }

    private static String valueType(ApiType type, Map<String, ApiType> types) {
        ApiType resolved = nonNil(resolve(type, types, new HashSet<>()), types);
        if (resolved instanceof ApiType.Array array) {
            return scalarType(array.items(), types).orElse("string") + "[]";
        }
        return scalarType(resolved, types).orElse("string");
    }

    private static Optional<String> scalarType(ApiType type, Map<String, ApiType> types) {
        return switch (nonNil(resolve(type, types, new HashSet<>()), types)) {
            case ApiType.Scalar scalar -> Optional.of(switch (scalar.kind()) {
                case INTEGER -> "int";
                case NUMBER -> "decimal";
                case BOOLEAN -> "boolean";
                case STRING, DATE, TIME, DATE_TIME, DATETIME_ONLY, FILE -> "string";
            });
            case ApiType.StringEnum stringEnum -> Optional.of("string");
            default -> Optional.empty();
        };
    }

    private static boolean isNilable(ApiType type, Map<String, ApiType> types) {
        return resolve(type, types, new HashSet<>()) instanceof ApiType.Union union
                && union.members().stream().anyMatch(ApiType.Nil.class::isInstance);
    }

    private static ApiType nonNil(ApiType type, Map<String, ApiType> types) {
        if (!(type instanceof ApiType.Union union)) {
            return type;
        }
        List<ApiType> members = union.members().stream().filter(member -> !(member instanceof ApiType.Nil)).toList();
        return members.size() == 1 ? resolve(members.get(0), types, new HashSet<>()) : new ApiType.Any();
    }

    private static ApiType resolve(ApiType type, Map<String, ApiType> types, Set<String> visited) {
        if (type instanceof ApiType.Ref ref) {
            return visited.add(ref.name()) && types.containsKey(ref.name())
                    ? resolve(types.get(ref.name()), types, visited)
                    : new ApiType.Any();
        }
        return type;
    }

    private static String variableName(String specName) {
        List<String> words = Stream.of(WORD_SEPARATOR.split(specName)).filter(word -> !word.isEmpty()).toList();
        if (words.isEmpty()) {
            return "param";
        }
        String first = words.get(0);
        String head = first.equals(first.toUpperCase(Locale.ROOT))
                ? first.toLowerCase(Locale.ROOT)
                : Character.toLowerCase(first.charAt(0)) + first.substring(1);
        return convertToBalIdentifier(head + words.stream().skip(1)
                .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1))
                .collect(Collectors.joining()));
    }

    private static String uniqueName(String candidate, String kind, Set<String> usedNames) {
        String name = candidate;
        if (usedNames.contains(name)) {
            name = candidate.replaceFirst("^'", "") + kind;
        }
        for (int suffix = 2; usedNames.contains(name); suffix++) {
            name = candidate.replaceFirst("^'", "") + kind + suffix;
        }
        usedNames.add(name);
        return name;
    }

    private static String mappingConstructor(Map<String, String> fields) {
        return fields.entrySet().stream()
                .map(field -> field.getKey().equals(field.getValue())
                        ? field.getValue()
                        : "\"%s\": %s".formatted(field.getKey(), field.getValue()))
                .collect(Collectors.joining(", ", "{", "}"));
    }
}
