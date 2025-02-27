package dataweave.converter;

import ballerina.BallerinaModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DWContext {
    public String mimeType;
    public final List<BallerinaModel.Statement> parentStatements;
    public DWScriptContext currentScriptContext;

    public List<String> functionNames;
    public Map<String, String> commonArgs = new HashMap<>();
    final Map<String, DWScriptContext> scriptCache = new HashMap<>();

    public DWContext(List<BallerinaModel.Statement> statementList) {
        this.parentStatements = statementList;
        this.functionNames = new ArrayList<>();
        this.currentScriptContext = new DWScriptContext();
    }

    public void clearScript() {
        this.currentScriptContext = new DWScriptContext(); // Reset only per-script values
    }

    public void finalizeFunction() {
        if (this.currentScriptContext.exprBuilder.isEmpty()) {
            return;
        }
        this.currentScriptContext.statements.add(new BallerinaModel.BallerinaStatement("return " +
                (this.currentScriptContext.exprBuilder + ";")));
        this.currentScriptContext.exprBuilder = new StringBuilder();
    }

    public String getExpression() {
        String s = this.currentScriptContext.exprBuilder.toString();
        this.currentScriptContext.exprBuilder = new StringBuilder();
        return s;
    }

    public void addCheckExpr() {
        this.currentScriptContext.exprBuilder.insert(0, "check ");
        this.currentScriptContext.containsCheck = true;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
        if (mimeType != null) {
            this.currentScriptContext.inputType = DWUtils.findBallerinaType(mimeType);
        }
    }

    public StringBuilder append(String context) {
        return this.currentScriptContext.exprBuilder.append(context);
    }

    public static class DWScriptContext {
        public List<BallerinaModel.Parameter> params = new ArrayList<>();
        public List<BallerinaModel.Statement> statements = new ArrayList<>();
        public Map<String, String> varTypes = new HashMap<>();
        public String inputType;
        public String outputType;
        public String dwVersion;
        public StringBuilder exprBuilder = new StringBuilder();
        public boolean containsCheck = false;
        public Map<String, String> varNames = new HashMap<>();
        public String funcName;
    }

}
