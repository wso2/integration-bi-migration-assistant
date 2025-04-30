package blocks.mule3;

import blocks.AbstractBlockTest;
import org.testng.annotations.Test;

public class MessageEnricherTest extends AbstractBlockTest {

    @Test
    public void testEmptyMessageEnricher() {
        testMule3ToBal("message-enricher/empty_message_enricher.xml", "message-enricher/empty_message_enricher.bal");
    }

    @Test
    public void testEnricherWithInsideLogger() {
        testMule3ToBal("message-enricher/enricher_with_inside_logger.xml",
                "message-enricher/enricher_with_inside_logger.bal");
    }

    @Test
    public void testEnricherWithInsideFlowReference() {
        testMule3ToBal("message-enricher/enricher_with_inside_flow_reference.xml",
                "message-enricher/enricher_with_inside_flow_reference.bal");
    }
}
