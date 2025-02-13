package blocks.mule3;

import blocks.AbstractBlockTest;
import org.testng.annotations.Test;

public class LoggerTest extends AbstractBlockTest {

    @Test
    public void testBasicLogger() {
        testMule3ToBal("logger/basic_logger.xml", "logger/basic_logger.bal");
    }

    @Test
    public void testLoggerLevels() {
        testMule3ToBal("logger/logger_levels.xml", "logger/logger_levels.bal");
    }
}
