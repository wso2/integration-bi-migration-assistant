package blocks.mule3;

import blocks.AbstractBlockTest;
import org.testng.annotations.Test;

public class ChoiceTest extends AbstractBlockTest {

    @Test
    public void testBasicChoice() {
        testMule3ToBal("choice/basic_choice.xml", "choice/basic_choice.bal");
    }

    @Test
    public void testChoiceWithMultipleConditions() {
        testMule3ToBal("choice/choice_with_multiple_conditions.xml", "choice/choice_with_multiple_conditions.bal");
    }

    @Test
    public void testChoiceWithHttpListenerSource() {
        testMule3ToBal("choice/choice_with_http_listener_source.xml", "choice/choice_with_http_listener_source.bal");
    }
}
