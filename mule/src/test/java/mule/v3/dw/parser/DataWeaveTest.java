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
package mule.v3.dw.parser;

import mule.v3.dw.parser.utils.ParserTestUtils;
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
                {DataWeaveScripts.SCRIPT_SIMPLE_STRING_SINGLE_QUOTED,
                        "src/test/resources/parser/expected/dw_simple_string_single_quote.json"},
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
                {DataWeaveScripts.SCRIPT_MAP_WITH_PARAMS, "src/test/resources/parser/expected/" +
                        "dw_map_with_params.json"},
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
                {DataWeaveScripts.SCRIPT_REPLACE_WITH, "src/test/resources/parser/expected/" +
                        "dw_replace_with.json"},
                {DataWeaveScripts.SCRIPT_CONCAT_STRING, "src/test/resources/parser/expected/" +
                        "dw_concat_string.json"},
                {DataWeaveScripts.SCRIPT_TYPE_COERCION, "src/test/resources/parser/expected/" +
                        "dw_type_coercion_string.json"},
                {DataWeaveScripts.SCRIPT_TYPE_COERCION_WITH_FORMAT, "src/test/resources/parser/expected/" +
                        "dw_type_coercion_with_format.json"},
                {DataWeaveScripts.SCRIPT_TYPE_COERCION_WITH_CLASS, "src/test/resources/parser/expected/" +
                        "dw_type_coercion_with_class.json"},
                {DataWeaveScripts.SCRIPT_TYPE_COERCION_STRING_WITH_DIFFERENT_FORMATS, "src/test/resources/parser" +
                        "/expected/dw_type_coercion_string_with_different_formats.json"},
                {DataWeaveScripts.SCRIPT_TYPE_COERCION_NUMBER_WITH_DATE_FORMATS, "src/test/resources/parser" +
                        "/expected/dw_type_coercion_number_with_date_formats.json"},
                {DataWeaveScripts.SCRIPT_TYPE_COERCION_TO_DATE, "src/test/resources/parser/expected/" +
                        "dw_type_coercion_to_date.json"},
        };
    }
}
