package dataweave.converter.builder;

import ballerina.BallerinaModel;
import ballerina.BallerinaModel.Statement.ElseIfClause;

import java.util.ArrayList;
import java.util.List;

public class IfStatementBuilder extends StatementBuilder {

    private BallerinaModel.Expression.BallerinaExpression ifCondition;
    private final List<BallerinaModel.Statement> ifBody = new ArrayList<>();
    private final List<ElseIfClause> elseIfClauses = new ArrayList<>();
    private final List<BallerinaModel.Statement> elseBody = new ArrayList<>();
    public String resultVar;

    public void setIfCondition(BallerinaModel.Expression.BallerinaExpression condition) {
        this.ifCondition = condition;
    }

    public void addIfBody(BallerinaModel.Statement statement) {
        this.ifBody.add(statement);
    }

    public void addElseIfClause(BallerinaModel.Expression.BallerinaExpression condition,
                                List<BallerinaModel.Statement> body) {
        this.elseIfClauses.add(new ElseIfClause(condition, body));
    }

    public void addElseBody(BallerinaModel.Statement statement) {
        this.elseBody.add(statement);
    }

    public BallerinaModel.Statement.IfElseStatement build() {
        return new BallerinaModel.Statement.IfElseStatement(ifCondition, ifBody, elseIfClauses, elseBody);
    }

    public BallerinaModel.Statement.IfElseStatement getStatement() {
        return new BallerinaModel.Statement.IfElseStatement(ifCondition, ifBody, elseIfClauses, elseBody);
    }
}
