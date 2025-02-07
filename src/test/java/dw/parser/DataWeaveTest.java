package dw.parser;

import dw.parser.utils.ParserTestUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static dw.parser.DataWeaveScripts.SCRIPT_BUILTIN_MAP;

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
                {DataWeaveScripts.SCRIPT_OUTPUT_INPUT, "src/test/resources/parser/expected/dw_output_input.json"},
                {DataWeaveScripts.SCRIPT_INPUT_PAYLOAD, "src/test/resources/parser/expected/dw_input_payload.json"},
                {DataWeaveScripts.SCRIPT_BUILTIN_SIZEOF, "src/test/resources/parser/expected/dw_builtin_sizeof.json"},
                {DataWeaveScripts.SCRIPT_BUILTIN_SIZEOF_IN_OBJECT, "src/test/resources/parser/expected/" +
                        "dw_builtin_sizeof_in_object.json"},
                {DataWeaveScripts.SCRIPT_BUILTIN_MAP, "src/test/resources/parser/expected/dw_builtin_map.json"},
        };
    }
}
