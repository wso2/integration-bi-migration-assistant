package blocks.mule3;

import blocks.AbstractBlockTest;
import org.testng.annotations.Test;

public class SubFlowTest extends AbstractBlockTest {

    @Test
    public void testBasicSetPayload() {
        testMule3ToBal("sub-flow/sample_1.xml", "sub-flow/sample_1.bal");
    }
}
