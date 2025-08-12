package mule.v4.blocks;

import org.testng.annotations.Test;

public class RaiseErrorTest extends AbstractBlockTest {

    @Test
    public void testBasicRaiseError() {
        testMule4ToBal("raise-error/basic_raise_error.xml", "raise-error/basic_raise_error.bal");
    }
}
