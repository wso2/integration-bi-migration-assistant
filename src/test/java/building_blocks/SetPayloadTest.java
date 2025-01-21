package building_blocks;

import org.testng.annotations.Test;

public class SetPayloadTest extends AbstractBuildingBlockTest {

    @Test
    public void testBasicSetPayload() {
        testMuleToBal("set-payload/sample_1.xml", "set-payload/sample_1.bal");
    }
}
