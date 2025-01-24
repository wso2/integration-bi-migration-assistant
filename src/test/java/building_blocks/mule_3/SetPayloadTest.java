package building_blocks.mule_3;

import building_blocks.AbstractBuildingBlockTest;
import org.testng.annotations.Test;

public class SetPayloadTest extends AbstractBuildingBlockTest {

    @Test
    public void testBasicSetPayload() {
        testMule3ToBal("set-payload/sample_1.xml", "set-payload/sample_1.bal");
    }

    @Test
    public void testMultipleSetPayloads() {
        testMule3ToBal("set-payload/sample_2.xml", "set-payload/sample_2.bal");
    }
}
