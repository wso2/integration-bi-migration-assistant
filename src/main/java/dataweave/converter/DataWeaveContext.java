package dataweave.converter;

import ballerina.BallerinaModel;
import converter.Mule2BalConverter;

import java.util.List;

public class DataWeaveContext {
    private final String mimeType;
    private final Mule2BalConverter.Data data;
    private final List<BallerinaModel.Statement> statementList;
    private String dwVersion;
    private DWConstants.OutputDirective outputDirective;
    private String variableDeclaration;
    private StringBuilder exprBuilder;

    public DataWeaveContext(String mimeType, Mule2BalConverter.Data data,
                            List<BallerinaModel.Statement> statementList) {
        this.exprBuilder = new StringBuilder();
        this.mimeType = mimeType;
        this.data = data;
        this.statementList = statementList;
    }

    public String getDwVersion() {
        return dwVersion;
    }

    public void setDwVersion(String dwVersion) {
        this.dwVersion = dwVersion;
    }

    public DWConstants.OutputDirective getOutputDirective() {
        return outputDirective;
    }

    public void setOutputDirective(DWConstants.OutputDirective outputDirective) {
        this.outputDirective = outputDirective;
    }

    public String getVariableDeclaration() {
        return variableDeclaration;
    }

    public void setVariableDeclaration(String variableDeclaration) {
        this.variableDeclaration = variableDeclaration;
    }

    public void appendExpr(String expression) {
        exprBuilder.append(expression);
    }

    public String getBallerinaType(String dwType) {
        return switch (dwType) {
            case DWConstants.ARRAY -> "anydata[]";
            case DWConstants.BOOLEAN -> "boolean";
            case DWConstants.FUNCTION -> "function";
            case DWConstants.NULL -> "()";
            case DWConstants.NUMBER -> "int";
            case DWConstants.OBJECT -> "map<anydata>";
            case DWConstants.STRING -> "string";
            default -> "any";
        };
    }

    public void addStatement(String statement) {
        this.statementList.add(new BallerinaModel.BallerinaStatement(statement));
    }

    public String getExpression() {
        String s = exprBuilder.toString();
        exprBuilder = new StringBuilder();
        return s;
    }
}
