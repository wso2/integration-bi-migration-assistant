package blocks.mule3;

import blocks.AbstractBlockTest;
import org.testng.annotations.Test;

public class UnsupportedBlockTest extends AbstractBlockTest {

    @Test
    public void testUnsupportedBlock() {
        testMule3ToBal("unsupported-block/unsupported_block.xml", "unsupported-block/unsupported_block.bal");
    }

    @Test
    public void testUnsupportedSource() {
        testMule3ToBal("unsupported-block/unsupported_source.xml", "unsupported-block/unsupported_source.bal");
    }
}
