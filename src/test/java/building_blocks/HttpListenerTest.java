package building_blocks;

import org.testng.annotations.Test;

public class HttpListenerTest extends AbstractBuildingBlockTest {

    @Test
    public void testEmptyResourcePath() {
        testMuleToBal("logger/sample_1.xml", "logger/sample_1.bal");
    }

    @Test
    public void testSpecialCharactersInResourcePath() {
        testMuleToBal("logger/sample_2.xml", "logger/sample_2.bal");
    }

    @Test
    public void testResourcePathParams() {
        testMuleToBal("logger/sample_1.xml", "logger/sample_1.bal");
    }
}
