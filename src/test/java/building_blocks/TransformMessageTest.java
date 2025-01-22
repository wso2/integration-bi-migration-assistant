package building_blocks;

import org.testng.annotations.Test;

public class TransformMessageTest extends AbstractBuildingBlockTest {

    @Test
    public void testBasicSetPayload() {
        testMuleToBal("transform-message/sample_1.xml", "transform-message/sample_1.bal");
    }

}
