package blocks.mule3;

import blocks.AbstractBlockTest;
import org.testng.annotations.Test;

public class VMConnectorTest extends AbstractBlockTest {

    @Test
    public void testSimpleVMConnector() {
        testMule3ToBal("vm-connector/simple_vm_connector.xml", "vm-connector/simple_vm_connector.bal");
    }

    @Test
    public void testVMConnectorWithHttpSource() {
        testMule3ToBal("vm-connector/vm_connector_wth_http_source.xml",
                "vm-connector/vm_connector_wth_http_source.bal");
    }

    @Test
    public void testVMConnectorInsideAnASyncBlock() {
        testMule3ToBal("vm-connector/vm_connector_inside_async_block.xml",
                "vm-connector/vm_connector_inside_async_block.bal");
    }
}
