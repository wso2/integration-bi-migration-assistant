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
package mule.v4.dataweave.converter;

import common.BallerinaModel;
import common.BallerinaModel.Statement.BallerinaStatement;
import mule.v4.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DWContext {
    public String mimeType;
    public final List<BallerinaModel.Statement> parentStatements;
    public DWScriptContext currentScriptContext;
    public final Context toolContext;

    public List<String> functionNames;
    public Map<String, String> commonArgs = new HashMap<>();
    final Map<String, DWScriptContext> scriptCache = new HashMap<>();
    public boolean isOutputVarSet = false;
    public boolean referringToPayload = false;
    public boolean inDefaultAccess = false;
    public boolean inKeyAccess = false;
    public boolean isSingleExpression = false;

    public DWContext(Context toolContext, List<BallerinaModel.Statement> statementList) {
        this.parentStatements = statementList;
        this.functionNames = new ArrayList<>();
        this.currentScriptContext = new DWScriptContext();
        this.toolContext = toolContext;
    }

    public void clearScript() {
        if (!this.currentScriptContext.errors.isEmpty() && !this.currentScriptContext.visited) {
            // TODO: revisit 19/02/26
            this.currentScriptContext.addStatement(new BallerinaStatement(DWUtils.PARSER_ERROR_COMMENT));
            for (String error : this.currentScriptContext.errors) {
                this.currentScriptContext.addStatement(new BallerinaStatement("// " + error + "\n"));
            }
            this.currentScriptContext.visited = true;
        }
        this.currentScriptContext = new DWScriptContext();
        this.isSingleExpression = false;
    }

    public BallerinaModel.Expression.BallerinaExpression finalizeFunction() {
        String returnExpr = this.currentScriptContext.exprBuilder.toString();
        if (this.currentScriptContext.hasStatements()) {
            this.currentScriptContext.addStatement(
                    new BallerinaStatement("return " + returnExpr + ";"));
            return new BallerinaModel.Expression.BallerinaExpression("");
        }

        this.isSingleExpression = true;
        if (currentScriptContext.letVariables.isEmpty()) {
            return new BallerinaModel.Expression.BallerinaExpression(returnExpr);
        }

        StringBuilder letExpr = new StringBuilder("let ");
        for (int i = 0; i < this.currentScriptContext.letVariables.size(); i++) {
            if (i > 0) {
                letExpr.append(", ");
            }
            LetVariableDeclaration letVar = this.currentScriptContext.letVariables.get(i);
            String varType = letVar.hasType() ? letVar.type() : inferTypeFromExpression(letVar.expression());
            letExpr.append(varType).append(" ").append(letVar.name()).append(" = ").append(letVar.expression());
        }
        letExpr.append(" in ").append(returnExpr);
        return new BallerinaModel.Expression.BallerinaExpression(letExpr.toString());
    }

    public String getExpression() {
        String s = this.currentScriptContext.exprBuilder.toString();
        this.currentScriptContext.exprBuilder = new StringBuilder();
        return s;
    }

    public void addCheckExpr() {
        StringBuilder exprBuilder = this.currentScriptContext.exprBuilder;
        if (exprBuilder.toString().startsWith("check")) {
            return;
        }
        exprBuilder.insert(0, "check ");
        this.currentScriptContext.containsCheck = true;
    }

    public StringBuilder append(String context) {
        return this.currentScriptContext.exprBuilder.append(context);
    }

    public void addUnsupportedComment(String context) {
        markAsFailedDWExpr(context);
        this.currentScriptContext.addStatement(new BallerinaStatement(
                String.format(DWUtils.UNSUPPORTED_DW_NODE, context)));
    }

    public void addUnsupportedCommentWithType(String context, String type) {
        markAsFailedDWExpr(context);
        this.currentScriptContext.addStatement(new BallerinaStatement(
                String.format(DWUtils.UNSUPPORTED_DW_NODE_WITH_TYPE, context, type)));
    }

    private void markAsFailedDWExpr(String dwExpr) {
        this.toolContext.migrationMetrics.dwConversionStats.failedDWExpressions.add(dwExpr);
    }

    public static class DWScriptContext {
        public List<BallerinaModel.Parameter> params = new ArrayList<>();
        private final List<BallerinaModel.Statement> statements = new ArrayList<>();
        public Map<String, String> varTypes = new HashMap<>();
        public String outputType;
        public String dwVersion;
        public StringBuilder exprBuilder = new StringBuilder();
        public boolean containsCheck = false;
        public Map<String, String> varNames = new HashMap<>();
        public String funcName;
        public String currentType = DWUtils.UNKNOWN;
        public List<String> errors = new ArrayList<>();
        public boolean visited = false;
        public List<LetVariableDeclaration> letVariables = new ArrayList<>();

        public void addStatement(BallerinaModel.Statement statement) {
            if (!letVariables.isEmpty()) {
                for (LetVariableDeclaration letVar : letVariables) {
                    String varDecl = getVarDeclaration(letVar);
                    statements.add(new BallerinaStatement(varDecl));
                }
                letVariables.clear();
            }
            statements.add(statement);
        }

        public void addStatement(int index, BallerinaModel.Statement statement) {
            statements.add(index, statement);
        }

        public List<BallerinaModel.Statement> getStatements() {
            return statements;
        }

        public boolean hasStatements() {
            return !statements.isEmpty();
        }
    }

    public record LetVariableDeclaration(String name, String expression, String type) {
        public LetVariableDeclaration(String name, String expression) {
            this(name, expression, null);
        }

        public LetVariableDeclaration {
            assert name != null : "Variable name cannot be null";
            assert expression != null : "Expression cannot be null";
        }

        public boolean hasType() {
            return type != null && !type.isEmpty();
        }
    }

    private static String getVarDeclaration(LetVariableDeclaration letVar) {
        return "%s %s = %s;".formatted(
                letVar.hasType() ? letVar.type() : inferTypeFromExpression(letVar.expression()),
                letVar.name(),
                letVar.expression());
    }

    private static String inferTypeFromExpression(String expr) {
        // TODO: enhance this to infer more types if needed.
        //  For now, we only need to handle payload references in let expressions.
        if (expr.equals(DWUtils.DW_PAYLOAD_IDENTIFIER)) {
            return "json";
        }
        return "var";
    }
}
