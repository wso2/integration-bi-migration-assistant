package blocks.mule3;

import blocks.AbstractBlockTest;
import org.testng.annotations.Test;

public class ReferenceExceptionStrategyTest extends AbstractBlockTest {

    @Test
    public void testBasicRefernceExceptionStrategy() {
        testMule3ToBal("reference-exception-strategy/basic_reference_exception_strategy.xml",
                "reference-exception-strategy/basic_reference_exception_strategy.bal");
    }

    @Test
    public void testReferenceExceptionWithHttpListenerSource() {
        testMule3ToBal("reference-exception-strategy/reference_exception_with_http_listener_source.xml",
                "reference-exception-strategy/reference_exception_with_http_listener_source.bal");
    }
}
