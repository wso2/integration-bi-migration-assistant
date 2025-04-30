package blocks.mule3;

import blocks.AbstractBlockTest;
import org.testng.annotations.Test;

public class VariableTest extends AbstractBlockTest {

    @Test
    public void testBasicSetVariable() {
        testMule3ToBal("variable/basic_set_variable.xml", "variable/basic_set_variable.bal");
    }

    @Test
    public void testSimpleRemoveVariable() {
        testMule3ToBal("variable/simple_remove_variable.xml", "variable/simple_remove_variable.bal");
    }
}
