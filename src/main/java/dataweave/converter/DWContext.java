package dataweave.converter;

import ballerina.BallerinaModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DWContext {
    final String mimeType;
    final List<BallerinaModel.Statement> parentStatements;
    public List<BallerinaModel.Parameter> params = new ArrayList<>();
    public List<BallerinaModel.Statement> statements = new ArrayList<>();
    public Map<String, String> varTypes = new HashMap<>();

    public String inputType;
    String dwVersion;
    String outputType;
    StringBuilder exprBuilder;
    public boolean containsCheck = false;
    Map<String, String> commonArgs = new HashMap<>();

    public DWContext(String mimeType, List<BallerinaModel.Statement> statementList) {
        this.exprBuilder = new StringBuilder();
        this.mimeType = mimeType;
        this.parentStatements = statementList;
    }

    public void finalizeFunction() {
        if (exprBuilder.isEmpty()) {
            return;
        }
        this.statements.add(new BallerinaModel.BallerinaStatement("return " + exprBuilder + ";"));
        this.exprBuilder = new StringBuilder();
    }

    public String getExpression() {
        String s = exprBuilder.toString();
        exprBuilder = new StringBuilder();
        return s;
    }

    public void addCheckExpr() {
        this.exprBuilder.insert(0, "check ");
        this.containsCheck = true;
    }
}
