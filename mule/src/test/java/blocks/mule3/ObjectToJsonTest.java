package blocks.mule3;

import blocks.AbstractBlockTest;
import org.testng.annotations.Test;

public class ObjectToJsonTest extends AbstractBlockTest {

    @Test
    public void testBasicObjectToJson() {
        testMule3ToBal("object-to-json/basic_object_to_json.xml", "object-to-json/basic_object_to_json.bal");
    }
}
