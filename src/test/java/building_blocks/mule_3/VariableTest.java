package building_blocks.mule_3;

import building_blocks.AbstractBuildingBlockTest;
import org.testng.annotations.Test;

public class VariableTest extends AbstractBuildingBlockTest {

    @Test
    public void testSetVariable() {
        testMule3ToBal("set-payload/sample_1.xml", "set-payload/sample_1.bal");
    }
}
