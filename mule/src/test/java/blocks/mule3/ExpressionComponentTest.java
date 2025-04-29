package blocks.mule3;

import blocks.AbstractBlockTest;
import org.testng.annotations.Test;

public class ExpressionComponentTest extends AbstractBlockTest {

    @Test
    public void testSimpleExpressionComponent() {
        testMule3ToBal("expression-component/simple_expression_component.xml",
                "expression-component/simple_expression_component.bal");
    }
}
