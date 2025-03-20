package blocks.mule3;

import blocks.AbstractBlockTest;
import org.testng.annotations.Test;

public class ChoiceExceptionStrategyTest extends AbstractBlockTest {

    @Test
    public void testBasicChoiceExceptionStrategy() {
        testMule3ToBal("choice-exception-strategy/basic_choice_exception_strategy.xml",
                "choice-exception-strategy/basic_choice_exception_strategy.bal");
    }

    @Test
    public void testChoiceExceptionWithHttpListenerSource() {
        testMule3ToBal("choice-exception-strategy/choice_exception_with_http_listener_source.xml",
                "choice-exception-strategy/choice_exception_with_http_listener_source.bal");
    }
}
