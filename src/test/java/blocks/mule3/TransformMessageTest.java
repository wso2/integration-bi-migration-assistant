package blocks.mule3;

import blocks.AbstractBlockTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TransformMessageTest extends AbstractBlockTest {

    @Test
    public void testBasicTransformMessageWithStringReturn() {
        testMule3ToBal("transform-message/basic_transform_message_with_string_return.xml",
                "transform-message/basic_transform_message_with_string_return.bal");
    }

    @Test
    public void testTransformMessageWithSimpleValue() {
        testMule3ToBal("transform-message/transform_message_with_simple_value.xml",
                "transform-message/transform_message_with_simple_value.bal");
    }

    @Test
    public void testTransformMessageWithSingleSelector() {
        testMule3ToBal("transform-message/transform_message_with_single_selector.xml",
                "transform-message/transform_message_with_single_selector.bal");
    }

    @Test
    public void testTransformMessageWithSizeOfFunction() {
        testMule3ToBal("transform-message/transform_message_with_sizeof.xml",
                "transform-message/transform_message_with_sizeof.bal");
    }

    @Test
    public void testTransformMessageWithMapFunctionValueIdentifier() {
        testMule3ToBal("transform-message/transform_message_with_map_value_identifier.xml",
                "transform-message/transform_message_with_map_value_identifier.bal");
    }

    @Test
    public void testTransformMessageWithMapFunctionIndexIdentifier() {
        testMule3ToBal("transform-message/transform_message_with_map_index_identifier.xml",
                "transform-message/transform_message_with_map_index_identifier.bal");
    }

    @Test
    public void testTransformMessageWithMapFunctionIndexIdentifierOnly() {
        testMule3ToBal("transform-message/transform_message_with_map_index_identifier_only.xml",
                "transform-message/transform_message_with_map_index_identifier_only.bal");
    }

    @Test
    public void testTransformMessageWithUpper() {
        testMule3ToBal("transform-message/transform_message_with_upper.xml",
                "transform-message/transform_message_with_upper.bal");
    }

    @Test
    public void testTransformMessageWithLower() {
        testMule3ToBal("transform-message/transform_message_with_lower.xml",
                "transform-message/transform_message_with_lower.bal");
    }

    @Test
    public void testTransformMessageWithMapFunctionCombination() {
        testMule3ToBal("transform-message/transform_message_with_map_combination.xml",
                "transform-message/transform_message_with_map_combination.bal");
    }

    @Test
    public void testTransformMessageWithFilterFunctionValueIdentifier() {
        testMule3ToBal("transform-message/transform_message_with_filter_value_identifier.xml",
                "transform-message/transform_message_with_filter_value_identifier.bal");
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
                {"transform-message/dataweave-files/transform_message_with_components.dwl",
                        "transform-message/transform_message_with_template.bal"},
        };
    }

}
