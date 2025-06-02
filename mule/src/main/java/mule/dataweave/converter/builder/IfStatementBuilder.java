/*
 *  Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com).
 *
 *  WSO2 LLC. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package mule.dataweave.converter.builder;

import common.BallerinaModel;
import common.BallerinaModel.Statement.ElseIfClause;
import common.BallerinaModel.Statement.IfElseStatement;

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

    public IfElseStatement build() {
        return new IfElseStatement(ifCondition, ifBody, elseIfClauses, elseBody);
    }

    public IfElseStatement getStatement() {
        return new IfElseStatement(ifCondition, ifBody, elseIfClauses, elseBody);
    }
}
