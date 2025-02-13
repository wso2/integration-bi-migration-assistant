package blocks.mule3;

import blocks.AbstractBlockTest;
import org.testng.annotations.Test;

public class MessageEnricherTest extends AbstractBlockTest {

    @Test
    public void testEmptyMessageEnricher() {
        testMule3ToBal("message-enricher/empty_message_enricher.xml", "message-enricher/empty_message_enricher.bal");
    }
}
