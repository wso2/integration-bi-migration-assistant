package blocks.mule3;

import blocks.AbstractBlockTest;
import org.testng.annotations.Test;

public class SessionVariableTest extends AbstractBlockTest {

    @Test
    public void testBasicSetSessionVariable() {
        testMule3ToBal("session-variable/basic_set_session_variable.xml",
                "session-variable/basic_set_session_variable.bal");
    }

    @Test
    public void testUpdatingSameSessionVariable() {
        testMule3ToBal("session-variable/updating_same_session_variable.xml",
                "session-variable/updating_same_session_variable.bal");
    }

    @Test
    public void testSetSessionVariableWithHttpSource() {
        testMule3ToBal("session-variable/set_session_variable_with_http_source.xml",
                "session-variable/set_session_variable_with_http_source.bal");
    }

    @Test
    public void testSimpleRemoveSessionVariable() {
        testMule3ToBal("session-variable/simple_remove_session_variable.xml",
                "session-variable/simple_remove_session_variable.bal");
    }
}
