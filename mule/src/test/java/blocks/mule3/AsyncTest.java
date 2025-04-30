package blocks.mule3;

import blocks.AbstractBlockTest;
import org.testng.annotations.Test;

public class AsyncTest extends AbstractBlockTest {

    @Test
    public void testSimpleAsync() {
        testMule3ToBal("async/simple_async.xml", "async/simple_async.bal");
    }
}
