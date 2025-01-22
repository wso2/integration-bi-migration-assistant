package unit.dw.parser;

import dataweave.DWReader;
import dataweave.parser.DataWeaveParser;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Test cases for DataWeave parser.
 */
public class DWParserTest {

    @Test(description = "Test DataWeave version parsing")
    public void testVersion() {
        ParseTree tree = DWReader.readDWScript(DataWeaveScripts.SCRIPT_DW);
        Assert.assertNotNull(tree);
        DataWeaveParser.DwVersionContext versionContext = ((DataWeaveParser.ScriptContext) tree).header()
                .directive(0).dwVersion();
        Assert.assertEquals(versionContext.NUMBER().getText(), "1.0");
    }

    @Test(description = "Test DataWeave output parsing")
    public void testOutput() {
        ParseTree tree = DWReader.readDWScript(DataWeaveScripts.SCRIPT_OUTPUT);
        Assert.assertNotNull(tree);
        List<TerminalNode> outputs = ((DataWeaveParser.ScriptContext) tree).header().directive(0)
                .outputDirective().IDENTIFIER();
        Assert.assertEquals(outputs.getFirst().getText(), "application");
        Assert.assertEquals(outputs.get(1).getText(), "xml");
    }

    @Test(description = "Test DataWeave constant parsing")
    public void testConstant() {
        ParseTree tree = DWReader.readDWScript(DataWeaveScripts.SCRIPT_CONSTANT);
        Assert.assertNotNull(tree);
        DataWeaveParser.VariableDeclarationContext variableDeclaration = ((DataWeaveParser.ScriptContext) tree)
                .header().directive(0).variableDeclaration();
        TerminalNode varName = variableDeclaration.IDENTIFIER();
        DataWeaveParser.LiteralExpressionContext expression = (DataWeaveParser.LiteralExpressionContext)
                variableDeclaration.expression();
        Assert.assertEquals(varName.getText(), "conversionRate");
        Assert.assertEquals(expression.literal().getText(), "13.15");
    }

    @Test(description = "Test DataWeave simple string parsing")
    public void testSimpleString() {
        ParseTree tree = DWReader.readDWScript(DataWeaveScripts.SCRIPT_SIMPLE_STRING);
        Assert.assertNotNull(tree);
        DataWeaveParser.LiteralExpressionContext expression = (DataWeaveParser.LiteralExpressionContext)
                ((DataWeaveParser.ScriptContext) tree).body().expression(0);
        Assert.assertEquals(expression.getText(), "\"Hello World\"");
    }

    @Test(description = "Test DataWeave simple boolean parsing")
    public void testSimpleBoolean() {
        ParseTree tree = DWReader.readDWScript(DataWeaveScripts.SCRIPT_SIMPLE_BOOLEAN);
        Assert.assertNotNull(tree);
        DataWeaveParser.LiteralExpressionContext expression = (DataWeaveParser.LiteralExpressionContext)
                ((DataWeaveParser.ScriptContext) tree).body().expression(0);
        Assert.assertEquals(expression.getText(), "true");
    }

    @Test(description = "Test DataWeave simple number parsing")
    public void testSimpleNumber() {
        ParseTree tree = DWReader.readDWScript(DataWeaveScripts.SCRIPT_SIMPLE_NUMBER);
        Assert.assertNotNull(tree);
        DataWeaveParser.LiteralExpressionContext expression = (DataWeaveParser.LiteralExpressionContext)
                ((DataWeaveParser.ScriptContext) tree).body().expression(0);
        Assert.assertEquals(expression.getText(), "123");
    }

    @Test(description = "Test DataWeave simple decimal number parsing")
    public void testSimpleDecimalNumber() {
        ParseTree tree = DWReader.readDWScript(DataWeaveScripts.SCRIPT_SIMPLE_NUMBER_DECIMAL);
        Assert.assertNotNull(tree);
        DataWeaveParser.LiteralExpressionContext expression = (DataWeaveParser.LiteralExpressionContext)
                ((DataWeaveParser.ScriptContext) tree).body().expression(0);
        Assert.assertEquals(expression.getText(), "123.321");
    }

    @Test(description = "Test DataWeave simple date parsing")
    public void testSimpleDate() {
        ParseTree tree = DWReader.readDWScript(DataWeaveScripts.SCRIPT_SIMPLE_DATE);
        Assert.assertNotNull(tree);
        DataWeaveParser.LiteralExpressionContext expression = (DataWeaveParser.LiteralExpressionContext)
                ((DataWeaveParser.ScriptContext) tree).body().expression(0);
        Assert.assertEquals(expression.getText(), "|2021-01-01|");
    }

    @Test(description = "Test DataWeave simple regex parsing", enabled = false)
    public void testSimpleRegex() {
        ParseTree tree = DWReader.readDWScript(DataWeaveScripts.SCRIPT_SIMPLE_REGEX);
        Assert.assertNotNull(tree);
        DataWeaveParser.LiteralExpressionContext expression = (DataWeaveParser.LiteralExpressionContext)
                ((DataWeaveParser.ScriptContext) tree).body().expression(0);
        Assert.assertEquals(expression.getText(), "/a/");
    }

}
