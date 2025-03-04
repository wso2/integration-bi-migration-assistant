package dw.parser;

import dw.parser.utils.ParserTestUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class DataWeaveTest {

    @Test(dataProvider = "provideTestInput")
    public void testDataWeaveParsing(String script, String expectedJsonPath) {
        ParserTestUtils.compareJson(script, expectedJsonPath);
    }

    @DataProvider (name = "provideTestInput")
    public String[][] provideTestInput() {
        return new String[][]{
                {DataWeaveScripts.SCRIPT_DW, "src/test/resources/parser/expected/dw_version.json"},
                {DataWeaveScripts.SCRIPT_OUTPUT, "src/test/resources/parser/expected/dw_output.json"},
                {DataWeaveScripts.SCRIPT_INPUT, "src/test/resources/parser/expected/dw_input.json"},
                {DataWeaveScripts.SCRIPT_CONSTANT, "src/test/resources/parser/expected/dw_constant.json"},
                {DataWeaveScripts.SCRIPT_NAME_SPACE, "src/test/resources/parser/expected/dw_namespace.json"},
                {DataWeaveScripts.SCRIPT_FUNCTION_VAR, "src/test/resources/parser/expected/dw_function_var.json"},
                {DataWeaveScripts.SCRIPT_FUNCTION, "src/test/resources/parser/expected/dw_function.json"},
                {DataWeaveScripts.SCRIPT_SIMPLE_STRING, "src/test/resources/parser/expected/dw_simple_string.json"},
                {DataWeaveScripts.SCRIPT_SIMPLE_BOOLEAN, "src/test/resources/parser/expected/dw_simple_boolean.json"},
                {DataWeaveScripts.SCRIPT_SIMPLE_NUMBER, "src/test/resources/parser/expected/dw_simple_number.json"},
                {DataWeaveScripts.SCRIPT_SIMPLE_NUMBER_DECIMAL,
                        "src/test/resources/parser/expected/dw_simple_number_decimal.json"},
                {DataWeaveScripts.SCRIPT_SIMPLE_DATE, "src/test/resources/parser/expected/dw_simple_date.json"},
//                {DataWeaveScripts.SCRIPT_SIMPLE_REGEX, "src/test/resources/parser/expected/dw_simple_regex.json"},
// TODO: Fix overlapping regex conflict
                {DataWeaveScripts.SCRIPT_ARRAY, "src/test/resources/parser/expected/dw_array.json"},
                {DataWeaveScripts.SCRIPT_OBJECT, "src/test/resources/parser/expected/dw_object.json"},
                {DataWeaveScripts.SCRIPT_SINGLE_OBJECT, "src/test/resources/parser/expected/dw_single_object.json"},
                {DataWeaveScripts.SCRIPT_OUTPUT_INPUT, "src/test/resources/parser/expected/dw_output_input.json"},
                {DataWeaveScripts.SCRIPT_INPUT_PAYLOAD, "src/test/resources/parser/expected/dw_input_payload.json"},
                {DataWeaveScripts.SCRIPT_BUILTIN_SIZEOF, "src/test/resources/parser/expected/dw_builtin_sizeof.json"},
                {DataWeaveScripts.SCRIPT_BUILTIN_SIZEOF_IN_OBJECT, "src/test/resources/parser/expected/" +
                        "dw_builtin_sizeof_in_object.json"},
                {DataWeaveScripts.SCRIPT_BUILTIN_MAP, "src/test/resources/parser/expected/dw_builtin_map.json"},
                {DataWeaveScripts.SCRIPT_BUILTIN_UPPER, "src/test/resources/parser/expected/" +
                        "dw_builtin_upper.json"},
                {DataWeaveScripts.SCRIPT_BUILTIN_LOWER, "src/test/resources/parser/expected/" +
                        "dw_builtin_lower.json"},
                {DataWeaveScripts.SCRIPT_BUILTIN_FILTER, "src/test/resources/parser/expected/dw_builtin_filter.json"},
                {DataWeaveScripts.SCRIPT_MATH_OPERATOR, "src/test/resources/parser/expected/" +
                        "dw_math_operator.json"},
                {DataWeaveScripts.SCRIPT_LOGICAL_OPERATOR, "src/test/resources/parser/expected/" +
                        "dw_logical_operator.json"},
                {DataWeaveScripts.SCRIPT_COMPARISON_OPERATOR, "src/test/resources/parser/expected/" +
                        "dw_comparison_operator.json"},
                {DataWeaveScripts.SCRIPT_COMPLEX_OPERATORS,
                        "src/test/resources/parser/expected/dw_complex_operators.json"},
                {DataWeaveScripts.SCRIPT_COMPLEX_OPERATORS_IN_OBJECT, "src/test/resources/parser/expected/" +
                        "dw_complex_operators_in_object.json"},
                {DataWeaveScripts.SCRIPT_COMPLEX_OPERATORS_IN_OBJECT_WITH_FUNCTIONS,
                        "src/test/resources/parser/expected/dw_complex_operators_in_object_with_functions.json"},
                {DataWeaveScripts.SCRIPT_COMPLEX_OPERATORS_IN_OBJECT_WITH_FUNCTIONS2,
                        "src/test/resources/parser/expected/dw_complex_operators_in_object_with_functions2.json"},
                {DataWeaveScripts.SCRIPT_WHEN_OTHERWISE, "src/test/resources/parser/expected/dw_when_otherwise.json"},
                {DataWeaveScripts.SCRIPT_WHEN_OTHERWISE_NESTED, "src/test/resources/parser/expected/" +
                        "dw_when_otherwise_nested.json"},

        };
    }
}
