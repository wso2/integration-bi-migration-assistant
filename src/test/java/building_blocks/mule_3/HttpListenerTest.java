package building_blocks.mule_3;

import building_blocks.AbstractBuildingBlockTest;
import org.testng.annotations.Test;

public class HttpListenerTest extends AbstractBuildingBlockTest {

    @Test
    public void testHttpListenerBasic() {
        testMule3ToBal("http-listener/sample_1.xml", "http-listener/sample_1.bal");
    }

    @Test
    public void testEmptyAbsolutePath() {
        testMule3ToBal("http-listener/sample_2.xml", "http-listener/sample_2.bal");
    }

    @Test
    public void testEmptyResourcePath() {
        testMule3ToBal("http-listener/sample_3.xml", "http-listener/sample_3.bal");
    }

    @Test
    public void testEmptyAbsoluteAndResourcePaths() {
        testMule3ToBal("http-listener/sample_4.xml", "http-listener/sample_4.bal");
    }

    @Test
    public void testSpecialCharactersInResourcePath() {
        testMule3ToBal("http-listener/sample_5.xml", "http-listener/sample_5.bal");
    }

    @Test
    public void testResourcePathParams() {
        testMule3ToBal("http-listener/sample_6.xml", "http-listener/sample_6.bal");
    }
}
