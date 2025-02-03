package dataweave;

import ballerina.BallerinaModel;
import converter.MuleToBalConverter;
import dataweave.converter.BallerinaVisitor;
import dataweave.parser.DataWeaveLexer;
import dataweave.parser.DataWeaveParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.List;

public class DWReader {

    public static ParseTree readDWScript(String script) {
        DataWeaveLexer lexer = new DataWeaveLexer(CharStreams.fromString(script));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        DataWeaveParser parser = new DataWeaveParser(tokens);
        return parser.script();
    }

    public static Object processDWScript(String script, String mimeType, MuleToBalConverter.Data data,
                                         List<BallerinaModel.Statement> statementList) {
        ParseTree tree = readDWScript(script);
        BallerinaVisitor visitor = new BallerinaVisitor(mimeType, data, statementList);
        visitor.visit(tree);
        return null;
    }
}
