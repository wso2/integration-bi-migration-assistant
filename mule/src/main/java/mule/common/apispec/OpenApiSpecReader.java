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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.servers.Server;
import mule.common.apispec.ApiSpec.ApiType;
import mule.common.apispec.ApiSpec.Body;
import mule.common.apispec.ApiSpec.Constraints;
import mule.common.apispec.ApiSpec.Operation;
import mule.common.apispec.ApiSpec.Property;
import mule.common.apispec.ApiSpec.Response;
import mule.common.apispec.ApiSpec.ScalarKind;
import org.jetbrains.annotations.NotNull;
import org.nipunaml.ramltoopenapi.mapper.MethodMapper;
import org.nipunaml.ramltoopenapi.mapper.TypeConverter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Maps an OpenAPI 3.0 model to an {@link ApiSpec}.
 */
final class OpenApiSpecReader {

    private static final String SCHEMA_REF_PREFIX = "#/components/schemas/";
    private static final Pattern PATH_PARAM = Pattern.compile("\\{([^}/]+)}");
    private static final ObjectMapper JSON = new ObjectMapper();

    private OpenApiSpecReader() {
    }

    static @NotNull ApiSpec read(OpenAPI openApi) {
        assert openApi != null;
        Map<String, ApiType> types = new LinkedHashMap<>();
        if (openApi.getComponents() != null && openApi.getComponents().getSchemas() != null) {
            openApi.getComponents().getSchemas().forEach((name, schema) -> types.put(name, toApiType(schema)));
        }
        List<Operation> operations = new ArrayList<>();
        if (openApi.getPaths() != null) {
            openApi.getPaths().forEach((path, pathItem) -> pathItem.readOperationsMap().forEach(
                    (method, operation) -> operations.add(toOperation(path, method, pathItem, operation,
                            openApi.getSecurity()))));
        }
        return new ApiSpec(Optional.ofNullable(openApi.getInfo()).map(Info::getTitle).orElse(""),
                Optional.ofNullable(openApi.getInfo()).map(Info::getVersion),
                baseUri(openApi.getServers()),
                withoutInheritedProperties(types),
                operations);
    }

    // RAML-derived allOf members repeat every inherited property; ApiSpec keeps only the ones a type declares
    // or narrows, so a subtype can be generated as an inclusion of its parent.
    private static Map<String, ApiType> withoutInheritedProperties(Map<String, ApiType> types) {
        Map<String, ApiType> result = new LinkedHashMap<>();
        types.forEach((name, type) -> result.put(name,
                type instanceof ApiType.ObjectType object && !object.parents().isEmpty()
                        ? withoutProperties(object, inheritedProperties(object.parents(), types, new HashSet<>()))
                        : type));
        return result;
    }

    private static Map<String, Property> inheritedProperties(List<String> parents, Map<String, ApiType> types,
                                                             Set<String> visited) {
        Map<String, Property> inherited = new LinkedHashMap<>();
        for (String parent : parents) {
            if (visited.add(parent) && types.get(parent) instanceof ApiType.ObjectType object) {
                inherited.putAll(inheritedProperties(object.parents(), types, visited));
                inherited.putAll(object.properties());
            }
        }
        return inherited;
    }

    private static ApiType.ObjectType withoutProperties(ApiType.ObjectType object, Map<String, Property> inherited) {
        Map<String, Property> own = new LinkedHashMap<>();
        object.properties().forEach((name, property) -> {
            if (!property.equals(inherited.get(name))) {
                own.put(name, property);
            }
        });
        return new ApiType.ObjectType(object.parents(), own, object.open());
    }

    private static Optional<String> baseUri(List<Server> servers) {
        return servers == null ? Optional.empty() : servers.stream()
                .map(Server::getUrl)
                .filter(url -> url != null && !url.isBlank() && !url.equals("/"))
                .findFirst();
    }

    private static Operation toOperation(String path, PathItem.HttpMethod method, PathItem pathItem,
                                         io.swagger.v3.oas.models.Operation operation,
                                         List<SecurityRequirement> globalSecurity) {
        Map<String, Parameter> parameters = new LinkedHashMap<>();
        Stream.of(pathItem.getParameters(), operation.getParameters())
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .forEach(parameter -> parameters.put(parameter.getIn() + ":" + parameter.getName(), parameter));
        return new Operation(method.name().toLowerCase(Locale.ROOT),
                path,
                uriParams(path, parametersIn("path", parameters)),
                parametersIn("query", parameters),
                parametersIn("header", parameters),
                requestBodies(operation.getRequestBody()),
                responses(operation),
                traitNames(operation),
                securitySchemeNames(operation.getSecurity() != null ? operation.getSecurity() : globalSecurity));
    }

    private static List<String> traitNames(io.swagger.v3.oas.models.Operation operation) {
        return operation.getExtensions() != null
                && operation.getExtensions().get(MethodMapper.TRAITS_EXTENSION) instanceof List<?> traits
                ? traits.stream().map(String::valueOf).toList() : List.of();
    }

    private static List<ApiSpec.Parameter> parametersIn(String location, Map<String, Parameter> parameters) {
        return parameters.values().stream()
                .filter(parameter -> location.equals(parameter.getIn()))
                .map(parameter -> new ApiSpec.Parameter(parameter.getName(), toApiType(parameter.getSchema()),
                        "path".equals(location) || Boolean.TRUE.equals(parameter.getRequired()),
                        Optional.ofNullable(parameter.getExample()).map(String::valueOf)))
                .toList();
    }

    private static List<ApiSpec.Parameter> uriParams(String path, List<ApiSpec.Parameter> declared) {
        Map<String, ApiSpec.Parameter> byName = new LinkedHashMap<>();
        declared.forEach(parameter -> byName.put(parameter.name(), parameter));
        List<ApiSpec.Parameter> ordered = new ArrayList<>();
        Matcher matcher = PATH_PARAM.matcher(path);
        while (matcher.find()) {
            String name = matcher.group(1);
            ordered.add(Optional.ofNullable(byName.remove(name)).orElseGet(
                    () -> new ApiSpec.Parameter(name, new ApiType.Scalar(ScalarKind.STRING, Constraints.NONE), true)));
        }
        ordered.addAll(byName.values());
        return ordered;
    }

    private static Map<String, Body> requestBodies(RequestBody requestBody) {
        return requestBody == null ? Map.of() : bodies(requestBody.getContent());
    }

    private static Map<Integer, Response> responses(io.swagger.v3.oas.models.Operation operation) {
        Map<Integer, Response> responses = new LinkedHashMap<>();
        if (operation.getResponses() == null) {
            return responses;
        }
        for (Map.Entry<String, ApiResponse> entry : operation.getResponses().entrySet()) {
            statusCode(entry.getKey()).ifPresent(
                    status -> responses.put(status, new Response(bodies(entry.getValue().getContent()))));
        }
        return responses;
    }

    private static Optional<Integer> statusCode(String key) {
        try {
            return Optional.of(Integer.parseInt(key));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    private static Map<String, Body> bodies(Content content) {
        Map<String, Body> bodies = new LinkedHashMap<>();
        if (content != null) {
            content.forEach((mediaType, body) -> bodies.put(mediaType, toBody(body)));
        }
        return bodies;
    }

    private static Body toBody(MediaType body) {
        Object example = body.getExample() != null ? body.getExample()
                : body.getExamples() == null ? null : body.getExamples().values().stream()
                        .map(Example::getValue)
                        .filter(Objects::nonNull)
                        .findFirst().orElse(null);
        return new Body(Optional.ofNullable(body.getSchema()).map(OpenApiSpecReader::toApiType),
                Optional.ofNullable(example).map(OpenApiSpecReader::exampleText));
    }

    private static String exampleText(Object example) {
        if (example instanceof String text) {
            return text;
        }
        try {
            return JSON.writeValueAsString(example);
        } catch (JsonProcessingException e) {
            return String.valueOf(example);
        }
    }

    private static List<String> securitySchemeNames(List<SecurityRequirement> requirements) {
        LinkedHashSet<String> names = new LinkedHashSet<>();
        if (requirements != null) {
            requirements.forEach(requirement -> names.addAll(requirement.keySet()));
        }
        return List.copyOf(names);
    }

    private static ApiType toApiType(Schema<?> schema) {
        if (schema == null) {
            return new ApiType.Any();
        }
        ApiType type = toNonNullableType(schema);
        if (!Boolean.TRUE.equals(schema.getNullable())) {
            return type;
        }
        List<ApiType> members = new ArrayList<>(type instanceof ApiType.Union union ? union.members() : List.of(type));
        members.add(new ApiType.Nil());
        return new ApiType.Union(members);
    }

    private static ApiType toNonNullableType(Schema<?> schema) {
        if (schema.get$ref() != null) {
            return schema.get$ref().startsWith(SCHEMA_REF_PREFIX)
                    ? new ApiType.Ref(schema.get$ref().substring(SCHEMA_REF_PREFIX.length()))
                    : new ApiType.Any();
        }
        if (schema.getAllOf() != null && !schema.getAllOf().isEmpty()) {
            return allOfType(schema.getAllOf());
        }
        List<Schema> alternatives = schema.getOneOf() != null ? schema.getOneOf() : schema.getAnyOf();
        if (alternatives != null && !alternatives.isEmpty()) {
            List<ApiType> members = alternatives.stream().map(OpenApiSpecReader::toApiType).toList();
            return members.size() == 1 ? members.get(0) : new ApiType.Union(members);
        }
        if (schema.getEnum() != null && !schema.getEnum().isEmpty()
                && (schema.getType() == null || "string".equals(schema.getType()))) {
            return new ApiType.StringEnum(schema.getEnum().stream().map(String::valueOf).toList());
        }
        String type = schema.getType();
        if (type == null) {
            return schema.getProperties() != null ? objectType(schema) : new ApiType.Any();
        }
        return switch (type) {
            case "string" -> new ApiType.Scalar(stringKind(schema.getFormat()), constraints(schema));
            case "integer" -> new ApiType.Scalar(ScalarKind.INTEGER, constraints(schema));
            case "number" -> new ApiType.Scalar(ScalarKind.NUMBER, constraints(schema));
            case "boolean" -> new ApiType.Scalar(ScalarKind.BOOLEAN, constraints(schema));
            case "array" -> new ApiType.Array(toApiType(schema.getItems()), constraints(schema));
            case "object" -> objectType(schema);
            default -> new ApiType.Any();
        };
    }

    private static ApiType allOfType(List<Schema> members) {
        List<String> parents = new ArrayList<>();
        Map<String, Property> properties = new LinkedHashMap<>();
        boolean open = true;
        for (Schema<?> member : members) {
            if (member.get$ref() != null && member.get$ref().startsWith(SCHEMA_REF_PREFIX)) {
                parents.add(member.get$ref().substring(SCHEMA_REF_PREFIX.length()));
            } else if (toNonNullableType(member) instanceof ApiType.ObjectType object) {
                parents.addAll(object.parents());
                properties.putAll(object.properties());
                open &= object.open();
            } else {
                return new ApiType.Any();
            }
        }
        return new ApiType.ObjectType(parents, properties, open);
    }

    private static ApiType.ObjectType objectType(Schema<?> schema) {
        Map<String, Property> properties = new LinkedHashMap<>();
        if (schema.getProperties() != null) {
            List<String> required = schema.getRequired() != null ? schema.getRequired() : List.of();
            schema.getProperties().forEach((name, property) -> properties.put(name,
                    new Property(toApiType(property), required.contains(name))));
        }
        return new ApiType.ObjectType(List.of(), properties, !Boolean.FALSE.equals(schema.getAdditionalProperties()));
    }

    private static ScalarKind stringKind(String format) {
        if (format == null) {
            return ScalarKind.STRING;
        }
        return switch (format) {
            case "date" -> ScalarKind.DATE;
            case "time" -> ScalarKind.TIME;
            case "date-time" -> ScalarKind.DATE_TIME;
            case TypeConverter.DATETIME_ONLY_FORMAT -> ScalarKind.DATETIME_ONLY;
            case "binary" -> ScalarKind.FILE;
            default -> ScalarKind.STRING;
        };
    }

    private static Constraints constraints(Schema<?> schema) {
        return new Constraints(Optional.ofNullable(schema.getPattern()),
                Optional.ofNullable(schema.getMinLength()),
                Optional.ofNullable(schema.getMaxLength()),
                Optional.ofNullable(schema.getMinimum()),
                Optional.ofNullable(schema.getMaximum()),
                Optional.ofNullable(schema.getMinItems()),
                Optional.ofNullable(schema.getMaxItems()));
    }
}
