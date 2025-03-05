package dataweave.converter.builder;

import ballerina.BallerinaModel;

import java.util.ArrayList;
import java.util.List;

public class IfStatementBuilder extends StatementBuilder {
    private BallerinaModel.BallerinaExpression ifCondition;
    private final List<BallerinaModel.Statement> ifBody = new ArrayList<>();
    private final List<BallerinaModel.ElseIfClause> elseIfClauses = new ArrayList<>();
    private final List<BallerinaModel.Statement> elseBody = new ArrayList<>();
    public String resultVar;

    public void setIfCondition(BallerinaModel.BallerinaExpression condition) {
        this.ifCondition = condition;
    }

    public void addIfBody(BallerinaModel.Statement statement) {
        this.ifBody.add(statement);
    }

    public void addElseIfClause(BallerinaModel.BallerinaExpression condition,
                                List<BallerinaModel.Statement> body) {
        this.elseIfClauses.add(new BallerinaModel.ElseIfClause(condition, body));
    }

    public void addElseBody(BallerinaModel.Statement statement) {
        this.elseBody.add(statement);
    }

    public BallerinaModel.IfElseStatement build() {
        return new BallerinaModel.IfElseStatement(ifCondition, ifBody, elseIfClauses, elseBody);
    }

    public BallerinaModel.IfElseStatement getStatement() {
        return new BallerinaModel.IfElseStatement(ifCondition, ifBody, elseIfClauses, elseBody);
    }
}
