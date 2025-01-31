package blocks.mule3;

import blocks.AbstractBlockTest;
import org.testng.annotations.Test;

public class TransformMessageTest extends AbstractBlockTest {

    @Test
    public void testBasicSetPayload() {
        testMule3ToBal("transform-message/sample_1.xml", "transform-message/sample_1.bal");
    }

    @Test
    public void testFirstLevelSetPayload() {
        testMule3ToBal("transform-message/sample_2.xml", "transform-message/sample_2.bal");
    }

}
