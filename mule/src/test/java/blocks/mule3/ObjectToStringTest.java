package blocks.mule3;

import blocks.AbstractBlockTest;
import org.testng.annotations.Test;

public class ObjectToStringTest extends AbstractBlockTest {

    @Test
    public void testBasicObjectToString() {
        testMule3ToBal("object-to-string/basic_object_to_string.xml", "object-to-string/basic_object_to_string.bal");
    }
}
