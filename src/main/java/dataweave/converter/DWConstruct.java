package dataweave.converter;

public enum DWConstruct {
    LITERAL("literal", 1),
    FUNCTION_CALL("function-call", 3),
    MAP("map", 3),
    FILTER("filter", 3),
    OBJECT("object", 2),
    ARRAY("array", 2),
    CONDITIONAL("when", 2),
    IDENTIFIER("identifier", 1),
    CONCAT("concat", 2),
    REPLACE("replace", 2),
    SIZE_OF("size-of", 2),
    TYPE_COERCION("type-coercion", 4),
    UNSUPPORTED("unsupported", 1000),
    LOWER("lower", 3),
    UPPER("upper", 3),
    SINGLE_VALUE_SELECTOR("single-value-selector", 2),
    MULTIPLICATIVE_OPERATOR("multiplicative-operator", 3),
    ADDITIVE_OPERATOR("additive-operator", 3),
    RELATIONAL_OPERATOR("RELATIONAL-OPERATOR", 3),
    EQUALITY_OPERATOR("equality-operator", 3),
    LAMBDA_EXPRESSION("lambda-expression", 4),
    AND_OPERATOR("and-operator", 3),
    OR_OPERATOR("or-operator", 3),
    GROUPED("grouped", 1),
    VARIABLE_DECLARATION("variable-declaration", 4),
    INPUT_DIRECTIVE("input-directive", 3),
    OUTPUT_DIRECTIVE("output-directive", 3);

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
