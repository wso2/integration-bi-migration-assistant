package dataweave.converter;

import ballerina.BallerinaModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DWContext {
    public String mimeType;
    final List<BallerinaModel.Statement> parentStatements;
    public List<BallerinaModel.Parameter> params = new ArrayList<>();
    public List<BallerinaModel.Statement> statements = new ArrayList<>();
    public Map<String, String> varTypes = new HashMap<>();
    public Map<String, String> varNames = new HashMap<>();

    public String inputType;
    public List<String> functionNames;
    String dwVersion;
    String outputType;
    StringBuilder exprBuilder;
    public boolean containsCheck = false;
    Map<String, String> commonArgs = new HashMap<>();

    public DWContext(List<BallerinaModel.Statement> statementList) {
        this.exprBuilder = new StringBuilder();
        this.parentStatements = statementList;
        this.functionNames = new ArrayList<>();
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

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
        if (mimeType != null) {
            this.inputType = DWUtils.findBallerinaType(mimeType);
        }
    }

    public void clearScript() {
        this.params = new ArrayList<>();
        this.statements = new ArrayList<>();
        this.varTypes = new HashMap<>();
        this.inputType = null;
        this.outputType = null;
        this.dwVersion = null;
        this.exprBuilder = new StringBuilder();
        this.containsCheck = false;
        this.commonArgs = new HashMap<>();
    }
}
