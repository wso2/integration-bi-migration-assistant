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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.BallerinaModel.Import;
import common.BallerinaModel.TextDocument;
import mule.common.apispec.ApiSpec;
import mule.common.apispec.ApiSpec.ApiType;
import mule.common.apispec.ApiSpec.Operation;
import mule.common.apispec.ApiSpecResult;
import mule.common.apispec.ApiTypeGenerator;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Writes the tests that check the APIkit services of a migrated project against their specs. The spec's examples
 * must fit the generated records, and a service must answer as APIkit's scaffolded error handlers do: 400 to a
 * request without its required headers, 501 to an operation without a flow and 404 to an unknown path.
 */
public final class ApiSpecTestGenerator {

    private static final String TEST_FILE = "tests/api_spec_test.bal";
    private static final String TEST_CONFIG = "@test:Config {groups: [\"api-spec\"]}";
    private static final String UNKNOWN_PATH = "/api-spec-test/no/such/path";
    private static final Pattern WORD_SEPARATOR = Pattern.compile("[^A-Za-z0-9]+");
    private static final ObjectMapper JSON = new ObjectMapper();

    private ApiSpecTestGenerator() {
    }

    /**
     * How the tests reach the service of an APIkit router.
     *
     * @param listenerRef    Ballerina reference of the service's listener, which knows its port
     * @param urlPrefix      URL path in front of the paths of the spec, such as {@code /api}
     * @param notImplemented spec operations the service answers with {@code APIKIT:NOT_IMPLEMENTED}
     */
    public record ServiceAddress(String listenerRef, String urlPrefix, List<Operation> notImplemented) {

        public ServiceAddress {
            assert listenerRef != null && urlPrefix != null && notImplemented != null;
            notImplemented = List.copyOf(notImplemented);
        }
    }

    /**
     * @param specs          the spec of each {@code apikit:config}
     * @param typeGenerators the generator of the types of each spec, keyed like the specs
     * @param addresses      how to reach the service of each router, keyed like the specs; a router whose listener
     *                       path is not fixed has none, so only the examples of its spec are tested
     * @return the test file, or empty when no spec was read
     */
    public static @NotNull Optional<TextDocument> generate(Map<String, ApiSpecResult> specs,
                                                           Map<String, ApiTypeGenerator> typeGenerators,
                                                           Map<String, ServiceAddress> addresses) {
        assert specs != null && typeGenerators != null && addresses != null;
        Set<Import> imports = new LinkedHashSet<>();
        List<String> declarations = new ArrayList<>();
        List<String> tests = new ArrayList<>();
        Set<String> names = new HashSet<>();
        specs.forEach((configName, result) -> {
            if (result instanceof ApiSpecResult.Loaded loaded) {
                new SpecTests(configName, loaded, typeGenerators.get(configName),
                        Optional.ofNullable(addresses.get(configName)), imports, declarations, tests, names).write();
            }
        });
        if (declarations.isEmpty() && tests.isEmpty()) {
            return Optional.empty();
        }
        imports.add(new Import("ballerina", "test"));
        List<String> intrinsics = new ArrayList<>(declarations);
        intrinsics.addAll(tests);
        return Optional.of(new TextDocument(TEST_FILE, new ArrayList<>(imports), List.of(), List.of(), List.of(),
                List.of(), List.of(), List.of(), intrinsics, List.of()));
    }

    /**
     * Writes the tests of one spec.
     */
    private static final class SpecTests {

        private final String configName;
        private final ApiSpecResult.Loaded loaded;
        private final ApiTypeGenerator typeGenerator;
        private final Optional<ServiceAddress> address;
        private final Set<Import> imports;
        private final List<String> declarations;
        private final List<String> tests;
        private final Set<String> names;
        private final Map<String, String> requestExamples = new LinkedHashMap<>();
        private final List<String> untestedOperations = new ArrayList<>();

        SpecTests(String configName, ApiSpecResult.Loaded loaded, ApiTypeGenerator typeGenerator,
                  Optional<ServiceAddress> address, Set<Import> imports, List<String> declarations,
                  List<String> tests, Set<String> names) {
            this.configName = configName;
            this.loaded = loaded;
            this.typeGenerator = typeGenerator;
            this.address = address;
            this.imports = imports;
            this.declarations = declarations;
            this.tests = tests;
            this.names = names;
        }

        void write() {
            Optional<String> client = address.map(this::declareClient);
            for (Operation operation : loaded.spec().operations()) {
                writeExampleTests(operation);
                client.ifPresent(clientVar -> writeRequestTests(operation, clientVar));
            }
            client.ifPresent(this::writeUnknownPathTest);
        }

        private String declareClient(ServiceAddress serviceAddress) {
            imports.add(new Import("ballerina", "http"));
            String clientVar = uniqueName(camelCase(configName) + "Client");
            declarations.add(("// The service that routes requests to the operations of %s\n"
                    + "final http:Client %s = check new (string `http://localhost:${%s.getPort()}`);").formatted(
                    specFileName(), clientVar, serviceAddress.listenerRef()));
            return clientVar;
        }

        private String specFileName() {
            return loaded.specFile().getFileName().toString();
        }

        private void writeExampleTests(Operation operation) {
            operation.requestBodies().forEach((mediaType, body) -> {
                if (isRecordBody(mediaType, body)) {
                    requestExample(operation, body).ifPresent(exampleVar -> writeExampleTest(
                            operationName("test", operation) + "RequestExample", body, exampleVar));
                }
            });
            operation.responses().forEach((status, response) -> {
                if (status >= 200 && status < 300) {
                    response.bodies().forEach((mediaType, body) -> {
                        if (isRecordBody(mediaType, body)) {
                            jsonExample(body).ifPresent(example -> writeExampleTest(operationName("test", operation)
                                    + status + "ResponseExample", body, "check string `"
                                    + example.replace("\n", "\n    ") + "`.fromJsonString()"));
                        }
                    });
                }
            });
        }

        private boolean isRecordBody(String mediaType, ApiSpec.Body body) {
            return SpecResourceSignature.isJsonMediaType(mediaType) && body.type().isPresent()
                    && typeGenerator.isGenerated(body.type().get());
        }

        // The example becomes the record the converted code binds the body to, and its constraints are checked
        private void writeExampleTest(String name, ApiSpec.Body body, String example) {
            imports.add(new Import("ballerina", "constraint"));
            tests.add(TEST_CONFIG + "\nfunction " + uniqueName(name) + "() returns error? {\n    "
                    + typeGenerator.typeDescriptor(body.type().orElseThrow()) + " _ = check constraint:validate("
                    + example + ");\n}");
        }

        private static Optional<String> jsonExample(ApiSpec.Body body) {
            if (body.example().isEmpty()) {
                return Optional.empty();
            }
            try {
                // Text in a string template is literal except for these two, which it can only interpolate
                return Optional.of(JSON.writerWithDefaultPrettyPrinter()
                        .writeValueAsString(JSON.readTree(body.example().get()))
                        .replace("${", "${\"$\"}{").replace("`", "${\"`\"}"));
            } catch (JsonProcessingException e) {
                return Optional.empty();
            }
        }

        private void writeRequestTests(Operation operation, String clientVar) {
            List<ApiSpec.Parameter> requiredHeaders = operation.headers().stream()
                    .filter(ApiSpec.Parameter::required)
                    .toList();
            if (!requiredHeaders.isEmpty()) {
                tests.add(statusTest(uniqueName(operationName("test", operation) + "RejectsMissingRequiredHeaders"),
                        request(clientVar, operation, Map.of(), requestBody(operation).orElse("()")), 400));
            }
            if (address.orElseThrow().notImplemented().contains(operation)) {
                writeNotImplementedTest(operation, clientVar, requiredHeaders);
            }
        }

        // The request has to pass the checks of the spec, which APIkit made before it found no flow
        private void writeNotImplementedTest(Operation operation, String clientVar,
                                             List<ApiSpec.Parameter> requiredHeaders) {
            Map<String, String> headers = new LinkedHashMap<>();
            for (ApiSpec.Parameter header : requiredHeaders) {
                Optional<String> value = parameterValue(header);
                if (value.isEmpty()) {
                    untestedOperations.add(route(operation));
                    return;
                }
                headers.put(header.name(), value.get());
            }
            Optional<String> body = operation.requestBodies().isEmpty() ? Optional.of("()") : requestBody(operation);
            boolean validPathAndQuery = Stream.concat(operation.uriParams().stream(),
                            operation.queryParams().stream().filter(ApiSpec.Parameter::required))
                    .allMatch(param -> parameterValue(param).isPresent());
            if (body.isEmpty() || !validPathAndQuery) {
                untestedOperations.add(route(operation));
                return;
            }
            tests.add(statusTest(uniqueName(operationName("test", operation) + "AnswersNotImplemented"),
                    request(clientVar, operation, headers, body.get()), 501));
        }

        private static String route(Operation operation) {
            return operation.method().toUpperCase(Locale.ROOT) + " " + operation.path();
        }

        private void writeUnknownPathTest(String clientVar) {
            String path = UNKNOWN_PATH;
            while (loaded.spec().findOperation("get", path, Optional.empty()).isPresent()) {
                path += "/x";
            }
            // A comment alone is no module member, so the note on untested operations heads this last test
            tests.add(untestedNote() + statusTest(uniqueName("test" + pascalCase(configName)
                            + "AnswersNotFoundToUnknownPath"),
                    "check %s->get(\"%s%s\")".formatted(clientVar, address.orElseThrow().urlPrefix(), path), 404));
        }

        private String untestedNote() {
            return untestedOperations.isEmpty() ? "" : ("// These operations of %s have no 501 test, since the spec "
                    + "gives no valid value for a required part of their request:\n// ").formatted(specFileName())
                    + String.join("\n// ", untestedOperations) + "\n";
        }

        private static String statusTest(String name, String request, int status) {
            return TEST_CONFIG + "\nfunction %s() returns error? {\n".formatted(name)
                    + "    http:Response response = %s;\n".formatted(request)
                    + "    test:assertEquals(response.statusCode, %d);\n}".formatted(status);
        }

        // A body the resource accepts: the spec's example, or any JSON when the spec gives the body no type
        private Optional<String> requestBody(Operation operation) {
            if (operation.requestBodies().size() != 1) {
                return Optional.empty();
            }
            Map.Entry<String, ApiSpec.Body> body = operation.requestBodies().entrySet().iterator().next();
            if (!SpecResourceSignature.isJsonMediaType(body.getKey())) {
                return Optional.empty();
            }
            return requestExample(operation, body.getValue()).or(() -> body.getValue().type()
                    .filter(typeGenerator::isNamedType).isPresent() ? Optional.empty() : Optional.of("{}"));
        }

        // A request example goes in a module variable, so that the tests that send it share it
        private Optional<String> requestExample(Operation operation, ApiSpec.Body body) {
            return jsonExample(body).map(example -> requestExamples.computeIfAbsent(operationName("", operation),
                    name -> {
                        String exampleVar = uniqueName(Character.toLowerCase(name.charAt(0)) + name.substring(1)
                                + "RequestExample");
                        declarations.add("final json %s = check string `%s`.fromJsonString();".formatted(exampleVar,
                                example));
                        return exampleVar;
                    }));
        }

        private String request(String clientVar, Operation operation, Map<String, String> headers, String body) {
            String query = operation.queryParams().stream()
                    .filter(ApiSpec.Parameter::required)
                    .flatMap(param -> parameterValue(param).map(value -> encode(param.name()) + "=" + encode(value))
                            .stream())
                    .collect(Collectors.joining("&"));
            String target = stringLiteral(address.orElseThrow().urlPrefix() + Stream.of(operation.path().split("/"))
                    .filter(segment -> !segment.isEmpty())
                    .map(segment -> "/" + pathSegment(operation, segment))
                    .collect(Collectors.joining()) + (query.isEmpty() ? "" : "?" + query));
            String headerMap = headers.isEmpty() ? "" : headers.entrySet().stream()
                    .map(header -> stringLiteral(header.getKey()) + ": " + stringLiteral(header.getValue()))
                    .collect(Collectors.joining(", ", "{", "}"));
            return switch (operation.method()) {
                case "get", "head", "options" -> "check %s->%s(%s%s)".formatted(clientVar, operation.method(),
                        target, headerMap.isEmpty() ? "" : ", " + headerMap);
                default -> "check %s->%s(%s, %s%s)".formatted(clientVar, operation.method(), target, body,
                        headerMap.isEmpty() ? "" : ", " + headerMap);
            };
        }

        private String pathSegment(Operation operation, String segment) {
            if (!segment.startsWith("{") || !segment.endsWith("}")) {
                return segment;
            }
            return operation.uriParams().stream()
                    .filter(param -> segment.equals("{" + param.name() + "}"))
                    .findFirst()
                    .flatMap(this::parameterValue)
                    .map(ApiSpecTestGenerator::encode)
                    .orElse("1");
        }

        // A value the spec accepts for the parameter: its example, or one its type and constraints allow
        private Optional<String> parameterValue(ApiSpec.Parameter param) {
            if (param.example().isPresent()) {
                return param.example();
            }
            return valueOf(resolve(param.type(), new HashSet<>()));
        }

        private Optional<String> valueOf(ApiType type) {
            return switch (type) {
                case ApiType.StringEnum stringEnum -> stringEnum.values().stream().findFirst();
                case ApiType.Array array -> valueOf(resolve(array.items(), new HashSet<>()));
                case ApiType.Scalar scalar -> switch (scalar.kind()) {
                    case BOOLEAN -> Optional.of("true");
                    case INTEGER, NUMBER -> Optional.of(scalar.constraints().minimum()
                            .or(() -> scalar.constraints().maximum()
                                    .filter(max -> max.compareTo(BigDecimal.ONE) < 0))
                            .map(BigDecimal::toPlainString).orElse("1"));
                    case STRING, DATE, TIME, DATE_TIME, DATETIME_ONLY, FILE ->
                            scalar.constraints().pattern().isPresent() ? Optional.empty()
                                    : Optional.of(text(scalar.constraints()));
                };
                default -> Optional.empty();
            };
        }

        private static String text(ApiSpec.Constraints constraints) {
            int length = Math.max(constraints.minLength().orElse(0), Math.min(4, constraints.maxLength().orElse(4)));
            return "test".repeat(length / 4 + 1).substring(0, length);
        }

        private ApiType resolve(ApiType type, Set<String> visited) {
            if (type instanceof ApiType.Ref ref && visited.add(ref.name())
                    && loaded.spec().types().containsKey(ref.name())) {
                return resolve(loaded.spec().types().get(ref.name()), visited);
            }
            if (type instanceof ApiType.Union union) {
                List<ApiType> members = union.members().stream()
                        .filter(member -> !(member instanceof ApiType.Nil))
                        .toList();
                return members.size() == 1 ? resolve(members.get(0), visited) : type;
            }
            return type;
        }

        private String operationName(String prefix, Operation operation) {
            return prefix + pascalCase(operation.method()) + pascalCase(operation.path());
        }

        private String uniqueName(String candidate) {
            String name = candidate;
            for (int suffix = 2; !names.add(name); suffix++) {
                name = candidate + suffix;
            }
            return name;
        }
    }

    private static String pascalCase(String text) {
        return Stream.of(WORD_SEPARATOR.split(text))
                .filter(word -> !word.isEmpty())
                .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1))
                .collect(Collectors.joining());
    }

    private static String camelCase(String text) {
        String name = pascalCase(text);
        return name.isEmpty() ? "api" : Character.toLowerCase(name.charAt(0)) + name.substring(1);
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }

    private static String stringLiteral(String value) {
        return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }
}
