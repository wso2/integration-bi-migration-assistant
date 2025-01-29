package blocks.mule3;

import blocks.AbstractBlockTest;
import org.testng.annotations.Test;

public class SubFlowTest extends AbstractBlockTest {

    @Test
    public void testBasicSubFlow() {
        testMule3ToBal("sub-flow/basic_sub_flow.xml", "sub-flow/basic_sub_flow.bal");
    }
}
