package dataweave.converter;

public enum DWConstruct {
    // Basic Constructs
    LITERAL("literal", 1),
    IDENTIFIER("identifier", 1),

    // Headers
    VARIABLE_DECLARATION("variable-declaration", 4),
    INPUT_DIRECTIVE("input-directive", 3),
    OUTPUT_DIRECTIVE("output-directive", 3),
    NAMESPACE_DIRECTIVE("namespace-directive", 3),
    FUNCTION_DECLARATION("function-declaration", 4),

    // Dataweave types
    OBJECT("object", 2),
    ARRAY("array", 2),

    // Expressions
    CONDITIONAL("when", 2),
    UNLESS("unless", 2),
    FUNCTION_CALL("function-call", 3),
    CONCAT("concat", 2),
    REPLACE("replace", 2),
    SIZE_OF("size-of", 2),
    MAP("map", 3),
    FILTER("filter", 3),
    TYPE_COERCION("type-coercion", 4),
    LOWER("lower", 3),
    UPPER("upper", 3),
    TYPE_EXPRESSION("type-expression", 3),
    GROUP_BY("group-by", 3),

    // Binary operators
    MULTIPLICATIVE_OPERATOR("multiplicative-operator", 3),
    ADDITIVE_OPERATOR("additive-operator", 3),
    RELATIONAL_OPERATOR("RELATIONAL-OPERATOR", 3),
    EQUALITY_OPERATOR("equality-operator", 3),
    AND_OPERATOR("and-operator", 3),
    OR_OPERATOR("or-operator", 3),
    GROUPED("grouped", 1),
    LAMBDA_EXPRESSION("lambda-expression", 4),

    // Selectors
    SINGLE_VALUE_SELECTOR("single-value-selector", 2),
    MULTI_VALUE_SELECTOR("multi-value-selector", 2),
    DESCENDANT_SELECTOR("descendant-selector", 2),
    INDEXED_SELECTOR("indexed-selector", 2),
    ATTRIBUTE_SELECTOR("attribute-selector", 2),
    EXISTENCE_QUERY_SELECTOR("existence-query-selector", 2),

    UNSUPPORTED("unsupported", 1000);

    private final String component;
    private final int weight;

    DWConstruct(String component, int weight) {
        this.component = component;
        this.weight = weight;
    }

    public String component() {
        return component;
    }

    public int weight() {
        return weight;
    }

}
