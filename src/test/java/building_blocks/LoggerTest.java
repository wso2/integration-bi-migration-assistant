package building_blocks;

import org.testng.annotations.Test;

public class LoggerTest extends AbstractBuildingBlockTest {

    @Test
    public void testBasicLogger() {
        testMuleToBal("logger/sample_1.xml", "logger/sample_1.bal");
    }

    @Test
    public void testLoggerLevels() {
        testMuleToBal("logger/sample_2.xml", "logger/sample_2.bal");
    }
}
