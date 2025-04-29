package blocks.mule3;

import blocks.AbstractBlockTest;
import org.testng.annotations.Test;

public class FlowTest extends AbstractBlockTest {

    @Test
    public void testBasicFlow() {
        testMule3ToBal("flow/basic_flow.xml", "flow/basic_flow.bal");
    }

    @Test
    public void testPrivateFlow() {
        testMule3ToBal("flow/private_flow.xml", "flow/private_flow.bal");
    }
}
