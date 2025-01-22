package dataweave;

import ballerina.BallerinaModel;
import converter.Mule2BalConverter;
import dataweave.converter.DataWeaveBallerinaVisitor;
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

    public static Object processDWScript(String script, String mimeType, Mule2BalConverter.Data data,
                                         List<BallerinaModel.Statement> statementList) {
        DataWeaveLexer lexer = new DataWeaveLexer(CharStreams.fromString(script));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
//        tokens.fill();
//
//        System.out.println("Tokens:");
//        for (Token token : tokens.getTokens()) {
//            System.out.println(token.getText() + " -> " + lexer.getVocabulary().getSymbolicName(token.getType()));
//        }
        ParseTree tree = readDWScript(script);
        DataWeaveBallerinaVisitor visitor = new DataWeaveBallerinaVisitor(mimeType, data, statementList);
        visitor.visit(tree);
        return null;
    }
}
