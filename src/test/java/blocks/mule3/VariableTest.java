package blocks.mule3;

import blocks.AbstractBlockTest;
import org.testng.annotations.Test;

public class VariableTest extends AbstractBlockTest {

    @Test
    public void testSetVariable() {
        testMule3ToBal("set-payload/sample_1.xml", "set-payload/sample_1.bal");
    }
}
