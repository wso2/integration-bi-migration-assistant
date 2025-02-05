package blocks.mule3;

import blocks.AbstractBlockTest;
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
}
