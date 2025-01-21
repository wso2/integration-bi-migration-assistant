import io.ballerina.compiler.syntax.tree.SyntaxTree;
import org.testng.annotations.Test;

import java.io.PrintStream;

import static converter.Mule2BalConverter.convertToBallerina;

public class TestConverter {

    private static final PrintStream OUT = System.out;

    @Test(description = "Test converter with sample xml files")
    public void convertAndPrint() {
        OUT.println("Generating Ballerina code...");
        SyntaxTree syntaxTree = convertToBallerina("src/test/resources/test_converter.xml");
        OUT.println("________________________________________________________________");
        OUT.println(syntaxTree.toSourceCode());
        OUT.println("________________________________________________________________");
    }
}
