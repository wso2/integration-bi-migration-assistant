package blocks.mule3;

import blocks.AbstractBlockTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TransformMessageTest extends AbstractBlockTest {

    @Test
    public void testTransformMessageWithSimpleValue() {
        testMule3ToBal("transform-message/transform_message_with_simple_value.xml",
                "transform-message/transform_message_with_simple_value.bal");
    }

    @Test
    public void testTransformMessageWithComponents() {
        testMule3ToBal("transform-message/transform_message_with_components.xml",
                "transform-message/transform_message_with_components.bal");
    }

    @Test (dataProvider = "provideTestParams")
    public void testTransformMessageWithCustomDataWeave(String dwFile, String balFile) {
        testDataWeaveMule3ToBal(dwFile, balFile);
    }

    @DataProvider (name = "provideTestParams")
    public Object[][] provideTestParams() {
        return new Object[][]{
                {"transform-message/dataweave-files/transform_message_with_string_return.dwl",
                        "transform-message/dataweave-bal-files/transform_message_with_string_return.bal"},
                {"transform-message/dataweave-files/transform_message_with_single_selector.dwl",
                        "transform-message/dataweave-bal-files/transform_message_with_single_selector.bal"},
                {"transform-message/dataweave-files/transform_message_with_sizeof.dwl",
                        "transform-message/dataweave-bal-files/transform_message_with_sizeof.bal"},
                {"transform-message/dataweave-files/transform_message_with_map_value_identifier.dwl",
                        "transform-message/dataweave-bal-files/transform_message_with_map_value_identifier.bal"},
                {"transform-message/dataweave-files/transform_message_with_map_index_identifier.dwl",
                        "transform-message/dataweave-bal-files/transform_message_with_map_index_identifier.bal"},
                {"transform-message/dataweave-files/transform_message_with_map_index_identifier_only.dwl",
                        "transform-message/dataweave-bal-files/transform_message_with_map_index_identifier_only.bal"},
                {"transform-message/dataweave-files/transform_message_with_upper.dwl",
                        "transform-message/dataweave-bal-files/transform_message_with_upper.bal"},
                {"transform-message/dataweave-files/transform_message_with_lower.dwl",
                        "transform-message/dataweave-bal-files/transform_message_with_lower.bal"},
                {"transform-message/dataweave-files/transform_message_with_map_combination.dwl",
                        "transform-message/dataweave-bal-files/transform_message_with_map_combination.bal"},
                {"transform-message/dataweave-files/transform_message_with_filter_value_identifier.dwl",
                        "transform-message/dataweave-bal-files/transform_message_with_filter_value_identifier.bal"},
                {"transform-message/dataweave-files/transform_message_with_when_otherwise.dwl",
                        "transform-message/dataweave-bal-files/transform_message_with_when_otherwise.bal"},
                {"transform-message/dataweave-files/transform_message_with_when_otherwise_nested.dwl",
                        "transform-message/dataweave-bal-files/transform_message_with_when_otherwise_nested.bal"},
                {"transform-message/dataweave-files/transform_message_with_replace_with.dwl",
                        "transform-message/dataweave-bal-files/transform_message_with_replace_with.bal"},
                {"transform-message/dataweave-files/transform_message_with_concat_string.dwl",
                        "transform-message/dataweave-bal-files/transform_message_with_concat_string.bal"},
                {"transform-message/dataweave-files/transform_message_with_concat_array.dwl",
                        "transform-message/dataweave-bal-files/transform_message_with_concat_array.bal"},
                {"transform-message/dataweave-files/transform_message_with_concat_object.dwl",
                        "transform-message/dataweave-bal-files/transform_message_with_concat_object.bal"},
                {"transform-message/dataweave-files/transform_message_with_date_type.dwl",
                        "transform-message/dataweave-bal-files/transform_message_with_date_type.bal"},
                {"transform-message/dataweave-files/transform_message_with_type_coercion_string.dwl",
                        "transform-message/dataweave-bal-files/transform_message_with_type_coercion_string.bal"},
                {"transform-message/dataweave-files/transform_message_with_type_coercion_number.dwl",
                        "transform-message/dataweave-bal-files/transform_message_with_type_coercion_number.bal"},
                {"transform-message/dataweave-files/transform_message_with_type_coercion_format.dwl",
                        "transform-message/dataweave-bal-files/transform_message_with_type_coercion_format.bal"},
        };
    }

}
