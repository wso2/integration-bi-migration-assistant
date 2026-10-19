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

import common.BallerinaModel.ModuleTypeDef;
import common.BallerinaModel.Statement.Comment;
import common.BallerinaModel.TypeDesc;
import common.BallerinaModel.TypeDesc.BuiltinType;
import common.BallerinaModel.TypeDesc.RecordTypeDesc;
import common.BallerinaModel.TypeDesc.RecordTypeDesc.RecordField;
import io.ballerina.compiler.syntax.tree.NodeParser;
import mule.common.apispec.ApiSpec.ApiType;
import mule.common.apispec.ApiSpec.Constraints;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static common.ConversionUtils.convertToBalIdentifier;
import static common.ConversionUtils.typeFrom;

/**
 * Generates Ballerina type definitions from the named types of a spec. Objects become records, open unless the spec
 * closes them, and a type that extends another includes it with {@code *Parent;}. Dates stay strings, so converted
 * flows that handle them as text keep working; nested objects without a name become inline records. Facets such as
 * {@code pattern} and {@code maximum} become {@code @constraint} annotations where Ballerina can check them; where it
 * cannot, a TODO comment names them.
 */
public final class ApiTypeGenerator {

    private static final String CONSTRAINT_ANNOTATION = "@constraint:";

    private final ApiSpec spec;
    private final Set<String> usedNames;
    private final Map<String, String> typeNames = new LinkedHashMap<>();
    private final Set<String> usedTypes = new LinkedHashSet<>();
    private final Map<String, ModuleTypeDef> parameterTypes = new LinkedHashMap<>();
    private final Map<String, ModuleTypeDef> responseTypes = new LinkedHashMap<>();

    /**
     * @param spec      the spec whose types are generated
     * @param usedNames type names already used in the generated module; every name the generator gives out is added,
     *                  so generators that share the set never give out the same name
     */
    public ApiTypeGenerator(ApiSpec spec, Set<String> usedNames) {
        assert spec != null && usedNames != null;
        this.spec = spec;
        this.usedNames = usedNames;
        spec.types().keySet().forEach(specName -> typeNames.put(specName, uniqueName(specName)));
    }

    private String uniqueName(String candidateName) {
        String candidate = convertToBalIdentifier(candidateName);
        String name = candidate;
        for (int suffix = 2; usedNames.contains(name); suffix++) {
            name = candidate + suffix;
        }
        usedNames.add(name);
        return name;
    }

    /**
     * Returns the Ballerina type descriptor for a spec type that converted code uses, and remembers the spec types
     * it needs, so that {@link #usedTypeDefinitions()} generates them.
     *
     * @param type a spec type used by the generated code
     * @return the Ballerina type descriptor
     */
    public @NotNull String use(ApiType type) {
        assert type != null;
        usedTypes.addAll(referencedTypeNames(type));
        return typeDescriptor(type);
    }

    /**
     * @param type a spec type
     * @return whether it is a named spec type, or an array of one, which converted code can bind to a record
     */
    public boolean isNamedType(ApiType type) {
        assert type != null;
        ApiType element = type instanceof ApiType.Array array ? array.items() : type;
        return element instanceof ApiType.Ref ref && spec.types().containsKey(ref.name());
    }

    /**
     * Ballerina checks constraints on a parameter only through its type, so a header or query parameter whose spec
     * type has facets gets a named type that carries them.
     *
     * @param nameHint name to derive the type name from
     * @param type     the parameter's spec type
     * @return name of the generated type, or empty when the type has no facet Ballerina can check
     */
    public @NotNull Optional<String> parameterType(String nameHint, ApiType type) {
        assert nameHint != null && type != null;
        if (!(nonNil(resolve(type)) instanceof ApiType.Scalar scalar)) {
            return Optional.empty();
        }
        Constraint constraint = constraint(scalar);
        if (constraint.annotation().isEmpty()) {
            return Optional.empty();
        }
        // Operations that share a parameter, such as a client-id header, share its type
        String key = String.join("\n", nameHint, typeDescriptor(scalar), constraint.annotation().get());
        return Optional.of(parameterTypes.computeIfAbsent(key, ignored -> new ModuleTypeDef(
                uniqueName(Character.toUpperCase(nameHint.charAt(0)) + nameHint.substring(1)),
                typeFrom(typeDescriptor(scalar)), comments(constraint), constraint.annotation())).name());
    }

    /**
     * Ballerina ties a response status to a typed body through a record that includes the {@code http} record of the
     * status, such as {@code record {| *http:Ok; Order body; |}}.
     *
     * @param statusType the {@code http} record of the status, such as {@code http:Ok}
     * @param bodyType   a named spec type, or an array of one, as {@link #isNamedType(ApiType)} accepts
     * @return name of the generated response record
     */
    public @NotNull String responseType(String statusType, ApiType bodyType) {
        assert statusType != null && bodyType != null && isNamedType(bodyType);
        String body = use(bodyType);
        // Operations that answer with the same status and body share the record
        return responseTypes.computeIfAbsent(statusType + "\n" + body, ignored -> new ModuleTypeDef(
                uniqueName(specName(bodyType) + statusType.substring(statusType.indexOf(':') + 1)),
                new RecordTypeDesc(List.of(typeFrom(statusType)),
                        List.of(new RecordField("body", typeFrom(body), false)), BuiltinType.NEVER))).name();
    }

    private static String specName(ApiType namedType) {
        return namedType instanceof ApiType.Array array
                ? ((ApiType.Ref) array.items()).name() + "Array" : ((ApiType.Ref) namedType).name();
    }

    /**
     * @return definitions of the spec types used so far, of every spec type they reference, in spec order, then of
     *         the parameter types and the response records
     */
    public @NotNull List<ModuleTypeDef> usedTypeDefinitions() {
        List<ModuleTypeDef> typeDefs = new ArrayList<>(typeDefinitions(usedTypes));
        typeDefs.addAll(parameterTypes.values());
        typeDefs.addAll(responseTypes.values());
        return typeDefs;
    }

    /**
     * @return whether the used type definitions need the {@code ballerina/constraint} module
     */
    public boolean usesConstraints() {
        return usedTypeDefinitions().stream().anyMatch(typeDef -> typeDef.toString().contains(CONSTRAINT_ANNOTATION));
    }

    /**
     * Generates the given spec types and every spec type they reference, in the order the spec declares them.
     *
     * @param rootTypeNames spec names of the types to generate
     * @return the type definitions
     */
    public @NotNull List<ModuleTypeDef> typeDefinitions(Collection<String> rootTypeNames) {
        assert rootTypeNames != null;
        Set<String> needed = withReferencedTypes(rootTypeNames);
        List<ModuleTypeDef> typeDefs = new ArrayList<>();
        spec.types().forEach((name, type) -> {
            if (needed.contains(name)) {
                typeDefs.add(typeDefinition(typeNames.get(name), type));
            }
        });
        return typeDefs;
    }

    private Set<String> withReferencedTypes(Collection<String> rootTypeNames) {
        Set<String> names = new HashSet<>();
        Deque<String> pending = new ArrayDeque<>(rootTypeNames);
        while (!pending.isEmpty()) {
            String name = pending.pop();
            if (spec.types().containsKey(name) && names.add(name)) {
                pending.addAll(referencedTypeNames(spec.types().get(name)));
            }
        }
        return names;
    }

    /**
     * @param type a spec type
     * @return whether it is a named spec type, or an array of one, whose definition the code used so far needs, so
     *         that tests of the generated code can name it
     */
    public boolean isGenerated(ApiType type) {
        assert type != null;
        return isNamedType(type) && withReferencedTypes(usedTypes)
                .contains(referencedTypeNames(type).iterator().next());
    }

    private ModuleTypeDef typeDefinition(String name, ApiType type) {
        if (type instanceof ApiType.ObjectType object) {
            return new ModuleTypeDef(name, recordTypeDesc(object));
        }
        Constraint constraint = constraint(type);
        return new ModuleTypeDef(name, typeFrom(typeDescriptor(type)), comments(constraint), constraint.annotation());
    }

    private static List<Comment> comments(Constraint constraint) {
        return constraint.todo().map(todo -> List.of(new Comment(todo))).orElse(List.of());
    }

    /**
     * @param type a spec type
     * @return names of the spec types it refers to directly
     */
    public static @NotNull Set<String> referencedTypeNames(ApiType type) {
        assert type != null;
        Set<String> names = new LinkedHashSet<>();
        collectReferences(type, names);
        return names;
    }

    private static void collectReferences(ApiType type, Set<String> names) {
        if (type instanceof ApiType.Ref ref) {
            names.add(ref.name());
        } else if (type instanceof ApiType.Array array) {
            collectReferences(array.items(), names);
        } else if (type instanceof ApiType.Union union) {
            union.members().forEach(member -> collectReferences(member, names));
        } else if (type instanceof ApiType.ObjectType object) {
            names.addAll(object.parents());
            object.properties().values().forEach(property -> collectReferences(property.type(), names));
        }
    }

    /**
     * @param type a spec type
     * @return the Ballerina type descriptor for it, naming spec types by their generated names
     */
    public @NotNull String typeDescriptor(ApiType type) {
        assert type != null;
        return switch (type) {
            case ApiType.Ref ref -> typeNames.getOrDefault(ref.name(), "anydata");
            case ApiType.Scalar scalar -> switch (scalar.kind()) {
                case INTEGER -> "int";
                case NUMBER -> "decimal";
                case BOOLEAN -> "boolean";
                case STRING, DATE, TIME, DATE_TIME, DATETIME_ONLY, FILE -> "string";
            };
            case ApiType.StringEnum stringEnum -> stringEnum.values().stream()
                    .map(ApiTypeGenerator::stringLiteral)
                    .collect(Collectors.joining("|"));
            case ApiType.Array array -> parenthesized(array.items()) + "[]";
            case ApiType.ObjectType object -> recordTypeDesc(object).toString();
            case ApiType.Union union -> unionDescriptor(union);
            case ApiType.Nil nil -> "()";
            case ApiType.Any any -> "anydata";
        };
    }

    private String unionDescriptor(ApiType.Union union) {
        List<ApiType> members = union.members().stream().filter(member -> !(member instanceof ApiType.Nil)).toList();
        if (members.stream().anyMatch(ApiType.Any.class::isInstance)) {
            return "anydata";
        }
        boolean nilable = members.size() < union.members().size();
        if (members.size() == 1) {
            return nilable ? parenthesized(members.get(0)) + "?" : typeDescriptor(members.get(0));
        }
        String alternatives = members.stream().map(this::parenthesized).collect(Collectors.joining("|"));
        return nilable ? "(" + alternatives + ")?" : alternatives;
    }

    private String parenthesized(ApiType type) {
        String descriptor = typeDescriptor(type);
        boolean compound = type instanceof ApiType.Union
                || (type instanceof ApiType.StringEnum stringEnum && stringEnum.values().size() > 1);
        return compound ? "(" + descriptor + ")" : descriptor;
    }

    private RecordTypeDesc recordTypeDesc(ApiType.ObjectType object) {
        List<TypeDesc> inclusions = object.parents().stream()
                .filter(typeNames::containsKey)
                .<TypeDesc>map(parent -> typeFrom(typeNames.get(parent)))
                .toList();
        List<RecordField> fields = new ArrayList<>();
        // Ballerina does not check the constraints of included fields, so constrained ones are repeated
        inheritedProperties(object.parents(), new HashSet<>()).forEach((name, inherited) -> {
            if (!object.properties().containsKey(name)
                    && constraint(inherited.property().type()).annotation().isPresent()) {
                fields.add(recordField(name, inherited.property(), Optional.of(
                        "repeated from %s so that Ballerina checks its constraint".formatted(inherited.owner()))));
            }
        });
        object.properties().forEach((name, property) -> fields.add(recordField(name, property, Optional.empty())));
        return new RecordTypeDesc(inclusions, fields, object.open() ? BuiltinType.ANYDATA : BuiltinType.NEVER);
    }

    private Map<String, InheritedProperty> inheritedProperties(List<String> parents, Set<String> visited) {
        Map<String, InheritedProperty> inherited = new LinkedHashMap<>();
        for (String parent : parents) {
            if (visited.add(parent) && spec.types().get(parent) instanceof ApiType.ObjectType parentObject) {
                inherited.putAll(inheritedProperties(parentObject.parents(), visited));
                parentObject.properties().forEach((name, property) ->
                        inherited.put(name, new InheritedProperty(typeNames.get(parent), property)));
            }
        }
        return inherited;
    }

    private record InheritedProperty(String owner, ApiSpec.Property property) {
    }

    private RecordField recordField(String name, ApiSpec.Property property, Optional<String> note) {
        String type = typeDescriptor(property.type());
        Constraint constraint = constraint(property.type());
        if (constraint.annotation().isEmpty() && isNilable(property.type())) {
            // Ballerina rejects constraint annotations on a nilable type
            constraint = constraint(resolve(nonNil(property.type())))
                    .uncheckable("the spec limits this field, but Ballerina cannot check a field that may be nil");
        }
        return new RecordField(convertToBalIdentifier(name),
                typeFrom(constraint.annotation().map(annotation -> annotation + "\n").orElse("") + type),
                !property.required(), Optional.empty(), Optional.empty(), constraint.todo().or(() -> note));
    }

    private static boolean isNilable(ApiType type) {
        return type instanceof ApiType.Union union && union.members().stream().anyMatch(ApiType.Nil.class::isInstance);
    }

    private static ApiType nonNil(ApiType type) {
        if (!(type instanceof ApiType.Union union)) {
            return type;
        }
        List<ApiType> members = union.members().stream().filter(member -> !(member instanceof ApiType.Nil)).toList();
        return members.size() == 1 ? members.get(0) : type;
    }

    private ApiType resolve(ApiType type) {
        ApiType resolved = type;
        Set<String> visited = new HashSet<>();
        while (resolved instanceof ApiType.Ref ref && visited.add(ref.name()) && spec.types().containsKey(ref.name())) {
            resolved = spec.types().get(ref.name());
        }
        return resolved;
    }

    private static Constraint constraint(ApiType type) {
        return switch (type) {
            case ApiType.Scalar scalar -> switch (scalar.kind()) {
                case INTEGER -> numberConstraint("Int", scalar.constraints(), true);
                case NUMBER -> numberConstraint("Number", scalar.constraints(), false);
                case BOOLEAN -> Constraint.NONE;
                case STRING, DATE, TIME, DATE_TIME, DATETIME_ONLY, FILE -> stringConstraint(scalar.constraints());
            };
            case ApiType.Array array -> Constraint.of("Array", lengthFacets(array.constraints().minItems(),
                    array.constraints().maxItems()), List.of());
            default -> Constraint.NONE;
        };
    }

    private static Constraint stringConstraint(Constraints constraints) {
        List<String> facets = new ArrayList<>();
        List<String> unchecked = new ArrayList<>();
        constraints.pattern().ifPresent(pattern -> {
            // A slash needs no escape in a Ballerina regular expression, which rejects the escaped form
            String ballerinaPattern = pattern.replace("\\/", "/");
            if (isBallerinaRegex(ballerinaPattern)) {
                facets.add("pattern: re `%s`".formatted(ballerinaPattern));
            } else {
                unchecked.add("pattern " + pattern);
            }
        });
        facets.addAll(lengthFacets(constraints.minLength(), constraints.maxLength()));
        return Constraint.of("String", facets, unchecked);
    }

    private static boolean isBallerinaRegex(String pattern) {
        return !pattern.contains("`") && !pattern.contains("${")
                && !NodeParser.parseExpression("re `" + pattern + "`").hasDiagnostics();
    }

    private static List<String> lengthFacets(Optional<Integer> min, Optional<Integer> max) {
        List<String> facets = new ArrayList<>();
        min.ifPresent(value -> facets.add("minLength: " + value));
        max.ifPresent(value -> facets.add("maxLength: " + value));
        return facets;
    }

    private static Constraint numberConstraint(String annotation, Constraints constraints, boolean integral) {
        List<String> facets = new ArrayList<>();
        List<String> unchecked = new ArrayList<>();
        constraints.minimum().ifPresent(value -> numberFacet("minValue", value, integral, facets, unchecked));
        constraints.maximum().ifPresent(value -> numberFacet("maxValue", value, integral, facets, unchecked));
        return Constraint.of(annotation, facets, unchecked);
    }

    private static void numberFacet(String facet, BigDecimal value, boolean integral, List<String> facets,
                                    List<String> unchecked) {
        BigDecimal number = value.stripTrailingZeros();
        if (integral && number.scale() > 0) {
            unchecked.add("%s %s".formatted(facet, value.toPlainString()));
        } else {
            facets.add("%s: %s".formatted(facet, number.toPlainString()));
        }
    }

    private static String stringLiteral(String value) {
        return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    /**
     * @param annotation annotation that checks the facets Ballerina can check
     * @param todo       comment naming the facets it cannot
     */
    private record Constraint(Optional<String> annotation, Optional<String> todo) {

        static final Constraint NONE = new Constraint(Optional.empty(), Optional.empty());

        static Constraint of(String annotationName, List<String> facets, List<String> unchecked) {
            return new Constraint(facets.isEmpty() ? Optional.empty() : Optional.of("%s%s {%s}".formatted(
                    CONSTRAINT_ANNOTATION, annotationName, String.join(", ", facets))),
                    unchecked.isEmpty() ? Optional.empty()
                            : Optional.of("TODO: Ballerina cannot check " + String.join(", ", unchecked)));
        }

        Constraint uncheckable(String reason) {
            return annotation.map(value -> new Constraint(Optional.empty(), Optional.of("TODO: " + reason + ": "
                    + value.substring(value.indexOf('{'))))).orElse(this);
        }
    }
}
