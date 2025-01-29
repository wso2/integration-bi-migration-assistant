package blocks.mule3;

import blocks.AbstractBlockTest;
import org.testng.annotations.Test;

public class UnsupportedBlockTest extends AbstractBlockTest {

    @Test
    public void testBasicSubFlow() {
        testMule3ToBal("unsupported-block/unsupported_block.xml", "unsupported-block/unsupported_block.bal");
    }
}
