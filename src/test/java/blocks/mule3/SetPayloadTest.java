package blocks.mule3;

import blocks.AbstractBlockTest;
import org.testng.annotations.Test;

public class SetPayloadTest extends AbstractBlockTest {

    @Test
    public void testBasicSetPayload() {
        testMule3ToBal("set-payload/sample_1.xml", "set-payload/sample_1.bal");
    }

    @Test
    public void testMultipleSetPayloads() {
        testMule3ToBal("set-payload/sample_2.xml", "set-payload/sample_2.bal");
    }
}
