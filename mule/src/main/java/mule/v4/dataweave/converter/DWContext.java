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

    public DWContext(Context toolContext, List<BallerinaModel.Statement> statementList) {
        this.parentStatements = statementList;
        this.functionNames = new ArrayList<>();
        this.currentScriptContext = new DWScriptContext();
        this.toolContext = toolContext;
    }

    public void clearScript() {
        if (!this.currentScriptContext.errors.isEmpty() && !this.currentScriptContext.visited) {
            this.currentScriptContext.statements.add(new BallerinaStatement(DWUtils.PARSER_ERROR_COMMENT));
            for (String error : this.currentScriptContext.errors) {
                this.currentScriptContext.statements.add(new BallerinaStatement("// " + error + "\n"));
            }
            this.currentScriptContext.visited = true;
        }
        this.currentScriptContext = new DWScriptContext();
    }

    public void finalizeFunction() {
        if (this.currentScriptContext.exprBuilder.isEmpty()) {
            return;
        }
        if (this.referringToPayload) {
            this.currentScriptContext.statements.add(0,
                    new BallerinaStatement("json %s = check ctx.%s.ensureType(json);"
                            .formatted(DWUtils.DW_PAYLOAD_IDENTIFIER, DWUtils.DW_PAYLOAD_IDENTIFIER)));
        }
        this.currentScriptContext.statements.add(
                new BallerinaStatement("return " + (this.currentScriptContext.exprBuilder + ";")));
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

    public StringBuilder append(String context) {
        return this.currentScriptContext.exprBuilder.append(context);
    }

    public void addUnsupportedComment(String context) {
        markAsFailedDWExpr(context);
        this.currentScriptContext.statements.add(new BallerinaStatement(
                String.format(DWUtils.UNSUPPORTED_DW_NODE, context)));


    }

    public void addUnsupportedCommentWithType(String context, String type) {
        markAsFailedDWExpr(context);
        this.currentScriptContext.statements.add(new BallerinaStatement(
                String.format(DWUtils.UNSUPPORTED_DW_NODE_WITH_TYPE, context, type)));
    }

    private void markAsFailedDWExpr(String dwExpr) {
        this.toolContext.migrationMetrics.dwConversionStats.failedDWExpressions.add(dwExpr);
    }

    public static class DWScriptContext {
        public List<BallerinaModel.Parameter> params = new ArrayList<>();
        public List<BallerinaModel.Statement> statements = new ArrayList<>();
        public Map<String, String> varTypes = new HashMap<>();
        public String outputType;
        public String dwVersion;
        public StringBuilder exprBuilder = new StringBuilder();
        public boolean containsCheck = false;
        public Map<String, String> varNames = new HashMap<>();
        public String funcName;
        public String currentType;
        public List<String> errors = new ArrayList<>();
        public boolean visited = false;
    }
}
