package dw.parser;

import dw.parser.utils.ParserTestUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static dw.parser.DataWeaveScripts.*;

public class DataWeaveTest {

    @Test(dataProvider = "provideTestInput")
    public void testDataWeaveParsing(String script, String expectedJsonPath) {
        ParserTestUtils.compareJson(script, expectedJsonPath);
    }

    @DataProvider (name = "provideTestInput")
    public String[][] provideTestInput() {
        return new String[][]{
                {SCRIPT_DW, "src/test/resources/parser/expected/dw_version.json"},
                {SCRIPT_OUTPUT, "src/test/resources/parser/expected/dw_output.json"},
                {SCRIPT_CONSTANT, "src/test/resources/parser/expected/dw_constant.json"},
                {SCRIPT_SIMPLE_STRING, "src/test/resources/parser/expected/dw_simple_string.json"},
                {SCRIPT_SIMPLE_BOOLEAN, "src/test/resources/parser/expected/dw_simple_boolean.json"},
                {SCRIPT_SIMPLE_NUMBER, "src/test/resources/parser/expected/dw_simple_number.json"},
                {SCRIPT_SIMPLE_NUMBER_DECIMAL, "src/test/resources/parser/expected/dw_simple_number_decimal.json"},
                {SCRIPT_SIMPLE_DATE, "src/test/resources/parser/expected/dw_simple_date.json"},
//                {SCRIPT_SIMPLE_REGEX, "src/test/resources/parser/expected/dw_simple_regex.json"},
//                  TODO: Fix overlapping regex conflict
        };
    }
}
