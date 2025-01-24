package building_blocks.mule_3;

import building_blocks.AbstractBuildingBlockTest;
import org.testng.annotations.Test;

public class LoggerTest extends AbstractBuildingBlockTest {

    @Test
    public void testBasicLogger() {
        testMule3ToBal("logger/sample_1.xml", "logger/sample_1.bal");
    }

    @Test
    public void testLoggerLevels() {
        testMule3ToBal("logger/sample_2.xml", "logger/sample_2.bal");
    }
}
