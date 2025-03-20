package blocks.mule3;

import blocks.AbstractBlockTest;
import org.testng.annotations.Test;

public class CatchExceptionStrategyTest extends AbstractBlockTest {

    @Test
    public void testBasicCatchExceptionStrategy() {
        testMule3ToBal("catch-exception-strategy/basic_catch_exception_strategy.xml",
                "catch-exception-strategy/basic_catch_exception_strategy.bal");
    }

    @Test
    public void testCatchExceptionWithHttpListenerSource() {
        testMule3ToBal("catch-exception-strategy/catch_exception_with_http_listener_source.xml",
                "catch-exception-strategy/catch_exception_with_http_listener_source.bal");
    }
}
