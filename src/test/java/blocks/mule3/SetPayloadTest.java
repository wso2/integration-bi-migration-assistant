package blocks.mule3;

import blocks.AbstractBlockTest;
import org.testng.annotations.Test;

public class SetPayloadTest extends AbstractBlockTest {

    @Test
    public void testBasicSetPayload() {
        testMule3ToBal("set-payload/basic_set_payload.xml", "set-payload/basic_set_payload.bal");
    }

    @Test
    public void testMultipleSetPayloads() {
        testMule3ToBal("set-payload/multiple_set_payloads.xml", "set-payload/multiple_set_payloads.bal");
    }
}
