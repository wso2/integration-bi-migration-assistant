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
package mule.v4.converter;

import common.BallerinaModel;
import common.BallerinaModel.Statement.ForeachStatement;
import common.BallerinaModel.Statement.ForkStatement;
import mule.v4.Constants;
import mule.v4.Context;
import mule.v4.ConversionUtils;
import mule.v4.dataweave.converter.DWReader;
import mule.v4.dataweave.converter.DWUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static common.BallerinaModel.BlockFunctionBody;
import static common.BallerinaModel.Expression.BallerinaExpression;
import static common.BallerinaModel.Function;
import static common.BallerinaModel.Import;
import static common.BallerinaModel.ModuleTypeDef;
import static common.BallerinaModel.OnFailClause;
import static common.BallerinaModel.Parameter;
import static common.BallerinaModel.Statement;
import static common.BallerinaModel.Statement.BallerinaStatement;
import static common.BallerinaModel.Statement.DoStatement;
import static common.BallerinaModel.Statement.ElseIfClause;
import static common.BallerinaModel.Statement.IfElseStatement;
import static common.BallerinaModel.Statement.NamedWorkerDecl;
import static common.BallerinaModel.TypeBindingPattern;
import static common.ConversionUtils.exprFrom;
import static common.ConversionUtils.stmtFrom;
import static common.ConversionUtils.typeFrom;
import static mule.v4.Constants.BAL_ERROR_TYPE;
import static mule.v4.Constants.FUNC_NAME_ASYC_TEMPLATE;
import static mule.v4.ConversionUtils.convertMuleExprToBal;
import static mule.v4.ConversionUtils.convertMuleExprToBalStringLiteral;
import static mule.v4.ConversionUtils.convertToUnsupportedTODO;
import static mule.v4.ConversionUtils.genQueryParam;
import static mule.v4.ConversionUtils.getBallerinaClientResourcePath;
import static mule.v4.ConversionUtils.inferTypeFromBalExpr;
import static mule.v4.converter.MELConverter.convertMELToBal;
import static mule.v4.converter.MuleConfigConverter.ConversionResult.FailClauseResult;
import static mule.v4.converter.MuleConfigConverter.ConversionResult.WorkerStatementResult;
import static mule.v4.model.MuleModel.Async;
import static mule.v4.model.MuleModel.Choice;
import static mule.v4.model.MuleModel.Database;
import static mule.v4.model.MuleModel.Enricher;
import static mule.v4.model.MuleModel.ErrorHandler;
import static mule.v4.model.MuleModel.ErrorHandlerRecord;
import static mule.v4.model.MuleModel.FirstSuccessful;
import static mule.v4.model.MuleModel.Foreach;
import static mule.v4.model.MuleModel.OnErrorContinue;
import static mule.v4.model.MuleModel.OnErrorPropagate;
import static mule.v4.model.MuleModel.ExpressionComponent;
import static mule.v4.model.MuleModel.FlowReference;
import static mule.v4.model.MuleModel.HttpRequest;
import static mule.v4.model.MuleModel.Kind;
import static mule.v4.model.MuleModel.LogLevel;
import static mule.v4.model.MuleModel.Logger;
import static mule.v4.model.MuleModel.MuleRecord;
import static mule.v4.model.MuleModel.ObjectToJson;
import static mule.v4.model.MuleModel.ObjectToString;
import static mule.v4.model.MuleModel.Payload;
import static mule.v4.model.MuleModel.RaiseError;
import static mule.v4.model.MuleModel.RemoveVariable;
import static mule.v4.model.MuleModel.Route;
import static mule.v4.model.MuleModel.ScatterGather;
import static mule.v4.model.MuleModel.SetVariable;
import static mule.v4.model.MuleModel.TransformMessage;
import static mule.v4.model.MuleModel.Try;
import static mule.v4.model.MuleModel.UnsupportedBlock;
import static mule.v4.model.MuleModel.VMPublish;
import static mule.v4.model.MuleModel.WhenInChoice;

public class MuleConfigConverter {

    /**
     * Result of converting a Mule record.
     */
    public sealed interface ConversionResult {
        record WorkerStatementResult(List<Statement> statements,
                                     List<NamedWorkerDecl> workers) implements ConversionResult {
            public WorkerStatementResult(List<Statement> statements) {
                this(statements, List.of());
            }
        }

        record FailClauseResult(OnFailClause onFailClause) implements ConversionResult {
        }
    }

    public static List<Statement> convertTopLevelMuleBlocks(Context ctx, List<MuleRecord> flowBlocks) {
        // Add function body statements
        List<Statement> body = new ArrayList<>();
        List<Statement> workers = new ArrayList<>();

        // Read flow blocks
        for (MuleRecord record : flowBlocks) {
            ConversionResult result = convertMuleBlock(ctx, record);
            if (result instanceof WorkerStatementResult(List<Statement> stmtList, List<NamedWorkerDecl> workerList)) {
                workers.addAll(workerList);
                body.addAll(stmtList);
            } else if (result instanceof FailClauseResult(OnFailClause onFailClause)) {
                body = new ArrayList<>(Collections.singletonList(new DoStatement(body, onFailClause)));
            } else {
                throw new IllegalStateException("Unexpected conversion result: " + result);
            }
        }

        workers.addAll(body);
        return workers;
    }

    public static List<Statement> convertErrorHandlerRecords(Context ctx, List<ErrorHandlerRecord> errHandlerRecords) {
        if (errHandlerRecords.isEmpty()) {
            return Collections.emptyList();
        } else if (errHandlerRecords.size() == 1) {
            return convertMuleRegularBlock(ctx, errHandlerRecords.getFirst()).statements();
        }

        ErrorHandlerRecord firstErrRec = errHandlerRecords.getFirst();
        BallerinaExpression ifCondition = getErrorHandlerRecCondition(ctx, firstErrRec);
        List<Statement> ifBody = convertMuleRegularBlock(ctx, firstErrRec).statements();

        List<ElseIfClause> elseIfClauses = new ArrayList<>();
        for (int i = 1; i < errHandlerRecords.size() - 1; i++) {
            ErrorHandlerRecord errRec = errHandlerRecords.get(i);
            List<Statement> elseIfBody = convertMuleRegularBlock(ctx, errRec).statements();
            BallerinaExpression condition = getErrorHandlerRecCondition(ctx, errRec);
            ElseIfClause elseIfClause = new ElseIfClause(condition, elseIfBody);
            elseIfClauses.add(elseIfClause);
        }

        List<Statement> elseBody = new ArrayList<>();
        ErrorHandlerRecord lastErrRec = errHandlerRecords.getLast();
        List<Statement> body = convertMuleRegularBlock(ctx, lastErrRec).statements();
        if (lastErrRec.type().isEmpty() && lastErrRec.when().isEmpty()) {
            elseBody = body;
        } else {
            BallerinaExpression condition = getErrorHandlerRecCondition(ctx, lastErrRec);
            ElseIfClause elseIfClause = new ElseIfClause(condition, body);
            elseIfClauses.add(elseIfClause);
        }

        IfElseStatement ifElseStmt = new IfElseStatement(ifCondition, ifBody, elseIfClauses, elseBody);

        List<Statement> statementList = new ArrayList<>();
        statementList.add(stmtFrom("\n// TODO: if conditions may require some manual adjustments\n"));
        statementList.add(ifElseStmt);
        return statementList;
    }

    private static BallerinaExpression getErrorHandlerRecCondition(Context ctx, ErrorHandlerRecord errRec) {
        StringBuilder conditionExpr = new StringBuilder();
        if (!errRec.type().isEmpty()) {
            conditionExpr.append(Constants.ON_FAIL_ERROR_VAR_REF).append(" is ")
                    .append("\"").append(errRec.type()).append("\"");
        }
        if (!errRec.when().isEmpty()) {
            if (!conditionExpr.isEmpty()) {
                conditionExpr.append(" && ");
            }
            conditionExpr.append("%s.message() ==".formatted(Constants.ON_FAIL_ERROR_VAR_REF))
                    .append(parseAsBalString(errRec.when()));
        }

        return exprFrom(conditionExpr.toString());
    }

    private static String parseAsBalString(String input) {
        if (input == null) {
            return "null";
        }

        return "\"" + input
                .replace("\\", "\\\\")     // Escape backslashes first
                .replace("\"", "\\\"")     // Escape double quotes
                .replace("\n", "\\n")      // Escape newlines
                .replace("\r", "\\r")      // Escape carriage returns
                .replace("\t", "\\t")      // Escape tabs
                .replace("\b", "\\b")      // Escape backspace
                .replace("\f", "\\f")      // Escape form feed
                + "\"";
    }

    public static <T extends MuleRecord> WorkerStatementResult convertMuleRegularBlocks(Context ctx,
                                                                                        List<T> muleRecords) {
        List<Statement> statements = new ArrayList<>();
        List<NamedWorkerDecl> workers = new ArrayList<>();
        for (MuleRecord mr : muleRecords) {
            WorkerStatementResult workerStatementResult = convertMuleRegularBlock(ctx, mr);
            statements.addAll(workerStatementResult.statements());
            workers.addAll(workerStatementResult.workers());
        }
        return new WorkerStatementResult(statements, workers);
    }

    public static ConversionResult convertMuleBlock(Context ctx, MuleRecord muleRec) {
        switch (muleRec) {
            case ErrorHandler errorHandler -> {
                return convertErrorHandler(ctx, errorHandler);
            }
            default -> {
                return convertMuleRegularBlock(ctx, muleRec);
            }
        }
    }

    public static WorkerStatementResult convertMuleRegularBlock(Context ctx, MuleRecord muleRec) {
        switch (muleRec) {
            case Logger lg -> {
                return convertLogger(ctx, lg);
            }
            case SetVariable setVariable -> {
                return convertSetVariable(ctx, setVariable);
            }
            case RemoveVariable removeVariable -> {
                return convertRemoveVariable(ctx, removeVariable);
            }
            case Payload payload -> {
                return convertSetPayload(ctx, payload);
            }
            case Choice choice -> {
                return convertChoice(ctx, choice);
            }
            case FlowReference flowReference -> {
                return convertFlowReference(ctx, flowReference);
            }
            case ObjectToJson objectToJson -> {
                return convertObjectToJson(ctx, objectToJson);
            }
            case ObjectToString objectToString -> {
                return convertObjectToString(ctx, objectToString);
            }
            case OnErrorContinue onErrorContinue -> {
                return convertOnErrorContinue(ctx, onErrorContinue);
            }
            case OnErrorPropagate onErrorPropagate -> {
                return convertOnErrorPropagate(ctx, onErrorPropagate);
            }
            case RaiseError raiseError -> {
                return convertRaiseError(ctx, raiseError);
            }
            case ExpressionComponent ec -> {
                return convertExprComponent(ctx, ec);
            }
            case Enricher enricher -> {
                return convertEnricher(ctx, enricher);
            }
            case HttpRequest httpRequest -> {
                return convertHttpRequest(ctx, httpRequest);
            }
            case Database database -> {
                return convertDatabase(ctx, database);
            }
            case Async async -> {
                return convertAsync(ctx, async);
            }
            case Try tr -> {
                return convertTry(ctx, tr);
            }
            case Foreach foreach -> {
                return convertForeach(ctx, foreach);
            }
            case ScatterGather scatterGather -> {
                return convertScatterGather(ctx, scatterGather);
            }
            case FirstSuccessful firstSuccessful -> {
                return convertFirstSuccessful(ctx, firstSuccessful);
            }
            case VMPublish vmPublish -> {
                return convertVMPublish(ctx, vmPublish);
            }
            case TransformMessage transformMessage -> {
                return convertTransformMessage(ctx, transformMessage);
            }
            case UnsupportedBlock unsupportedBlock -> {
                return convertUnsupportedBlock(ctx, unsupportedBlock);
            }
            case null -> throw new IllegalStateException();
            default -> throw new UnsupportedOperationException();
        }
    }

    private static WorkerStatementResult convertLogger(Context ctx, Logger lg) {
        String logFuncName = getBallerinaLogFunction(lg.level());
        String stringLiteral = convertMuleExprToBalStringLiteral(ctx, lg.message());
        BallerinaStatement stmt = stmtFrom("log:%s(%s);".formatted(logFuncName, stringLiteral));
        return new WorkerStatementResult(List.of(stmt));
    }

    private static String getBallerinaLogFunction(LogLevel logLevel) {
        return switch (logLevel) {
            case DEBUG -> "printDebug";
            case ERROR -> "printError";
            case INFO, TRACE -> "printInfo";
            case WARN -> "printWarn";
        };
    }

    private static WorkerStatementResult convertSetVariable(Context ctx, SetVariable setVariable) {
        String varName = ConversionUtils.convertToBalIdentifier(setVariable.variableName());
        String balExpr = convertMuleExprToBal(ctx, setVariable.value());
        String type = inferTypeFromBalExpr(balExpr);

        if (!ctx.projectCtx.vars.containsKey(varName)) {
            ctx.projectCtx.vars.put(varName, type);
        }

        var stmt = stmtFrom(String.format("%s.%s = %s;", Constants.VARS_FIELD_ACCESS, varName, balExpr));
        return new WorkerStatementResult(List.of(stmt));
    }

    private static WorkerStatementResult convertRemoveVariable(Context ctx, RemoveVariable removeVariable) {
        String variableName = removeVariable.variableName();
        if (variableName.startsWith("#[vars.") && variableName.endsWith("]")) {
            variableName = variableName.substring(7, variableName.length() - 1);
        }

        String varName = ConversionUtils.convertToBalIdentifier(variableName);
        Statement stmt;
        if (removeVariable.kind() == Kind.REMOVE_VARIABLE && ctx.projectCtx.vars.containsKey(varName)) {
            stmt = stmtFrom(String.format("%s.%s = %s;", Constants.VARS_FIELD_ACCESS, varName, "()"));
        } else {
            // Removing undeclared variable in mule won't give error, so we just ignore it here
            return new WorkerStatementResult(Collections.emptyList());
        }
        return new WorkerStatementResult(List.of(stmt));
    }

    private static WorkerStatementResult convertSetPayload(Context ctx, Payload payload) {
        String pyld;
        String type;
        switch (payload.mimeType()) {
            // TODO: handle other mime types
            default -> {
                pyld = convertMuleExprToBal(ctx, payload.expr());
                type = inferTypeFromBalExpr(pyld);
            }
        }

        String payloadVar = String.format(Constants.VAR_PAYLOAD_TEMPLATE, ctx.projectCtx.counters.payloadVarCount++);

        List<Statement> stmts = new ArrayList<>();
        stmts.add(stmtFrom("\n\n// set payload\n"));
        stmts.add(stmtFrom(String.format("%s %s = %s;", type, payloadVar, pyld)));
        stmts.add(stmtFrom(String.format("%s.payload = %s;", Constants.CONTEXT_REFERENCE,
                payloadVar)));
        return new WorkerStatementResult(stmts);
    }

    private static WorkerStatementResult convertChoice(Context ctx, Choice choice) {
        List<WhenInChoice> whens = choice.whens();
        assert !whens.isEmpty(); // For valid mule config, there is at least one when

        WhenInChoice firstWhen = whens.getFirst();
        String ifCondition = convertMuleExprToBal(ctx, firstWhen.condition());
        List<Statement> ifBody = new ArrayList<>();
        for (MuleRecord mr : firstWhen.process()) {
            List<Statement> statements = convertMuleRegularBlock(ctx, mr).statements();
            ifBody.addAll(statements);
        }

        List<ElseIfClause> elseIfClauses = new ArrayList<>(whens.size() - 1);
        for (int i = 1; i < whens.size(); i++) {
            WhenInChoice when = whens.get(i);
            List<Statement> elseIfBody = new ArrayList<>();
            for (MuleRecord mr : when.process()) {
                List<Statement> statements = convertMuleRegularBlock(ctx, mr).statements();
                elseIfBody.addAll(statements);
            }
            ElseIfClause elseIfClause = new ElseIfClause(exprFrom(convertMuleExprToBal(ctx, when.condition())),
                    elseIfBody);
            elseIfClauses.add(elseIfClause);
        }

        List<Statement> elseBody = new ArrayList<>(choice.otherwiseProcess().size());
        for (MuleRecord mr : choice.otherwiseProcess()) {
            List<Statement> statements = convertMuleRegularBlock(ctx, mr).statements();
            elseBody.addAll(statements);
        }

        var ifElseStmt = new IfElseStatement(exprFrom(ifCondition), ifBody, elseIfClauses, elseBody);
        return new WorkerStatementResult(List.of(ifElseStmt));
    }

    private static WorkerStatementResult convertFlowReference(Context ctx, FlowReference flowReference) {
        String flowName = flowReference.flowName();
        List<Statement> statements = new ArrayList<>();
        String funcRef = ctx.getFlowFuncRef(flowName).orElseGet(() -> {
            statements.add(new Statement.Comment("FIXME: failed to find flow %s".formatted(flowName)));
            return flowName;
        });
        var stmt = stmtFrom(String.format("%s(%s);", funcRef, Constants.CONTEXT_REFERENCE));
        statements.add(stmt);
        return new WorkerStatementResult(statements);
    }

    private static WorkerStatementResult convertObjectToJson(Context ctx, ObjectToJson objectToJson) {
        List<Statement> stmts = new ArrayList<>();
        stmts.add(stmtFrom("\n\n// json transformation\n"));
        // object to json transformer implicitly sets the payload
        stmts.add(stmtFrom("%s = %s.toJson();".formatted(Constants.PAYLOAD_FIELD_ACCESS,
                Constants.PAYLOAD_FIELD_ACCESS)));
        return new WorkerStatementResult(stmts);
    }

    private static WorkerStatementResult convertObjectToString(Context ctx, ObjectToString objectToString) {
        List<Statement> stmts = new ArrayList<>();
        stmts.add(stmtFrom("\n\n// string transformation\n"));
        // object to string transformer implicitly sets the payload
        stmts.add(stmtFrom("%s = %s.toString();".formatted(Constants.PAYLOAD_FIELD_ACCESS,
                Constants.PAYLOAD_FIELD_ACCESS)));
        return new WorkerStatementResult(stmts);
    }

    private static WorkerStatementResult convertExprComponent(Context ctx, ExpressionComponent ec) {
        String convertedExpr = convertMuleExprToBal(ctx, String.format("#[%s]", ec.exprCompContent()));
        ConversionUtils.processExprCompContent(ctx, convertedExpr);
        return new WorkerStatementResult(List.of(stmtFrom(convertedExpr)));
    }

    private static WorkerStatementResult convertEnricher(Context ctx, Enricher enricher) {
        // TODO: support no source
        String source = convertMuleExprToBal(ctx, enricher.source());
        String target = convertMuleExprToBal(ctx, enricher.target());

        if (target.startsWith(Constants.VARS_FIELD_ACCESS + ".")) {
            String var = target.replace(Constants.VARS_FIELD_ACCESS + ".", "");
            if (!ctx.projectCtx.vars.containsKey(var)) {
                ctx.projectCtx.vars.put(var, "string");
            }
        }

        List<Statement> stmts = new ArrayList<>();
        if (enricher.innerBlock().isEmpty()) {
            stmts.add(stmtFrom(String.format("%s = %s;", target, source)));
        } else {
            WorkerStatementResult workerStatementResult = convertMuleRegularBlock(ctx, enricher.innerBlock().get());
            List<Statement> enricherStmts = new ArrayList<>(workerStatementResult.statements());

            String methodName = Constants.FUNC_NAME_ENRICHER_TEMPLATE
                    .formatted(ctx.projectCtx.counters.enricherFuncCount);
            Function func = new Function(Optional.of("public"), methodName, Constants.FUNC_PARAMS_WITH_CONTEXT,
                    Optional.of(typeFrom("string?")), new BlockFunctionBody(enricherStmts));
            ctx.currentFileCtx.balConstructs.functions.add(func);

            enricherStmts.add(stmtFrom("return %s;".formatted(source)));
            stmts.add(stmtFrom("%s = %s(%s.clone());".formatted(target,
                    Constants.FUNC_NAME_ENRICHER_TEMPLATE.formatted(ctx.projectCtx.counters.enricherFuncCount++),
                    Constants.CONTEXT_REFERENCE)));
        }
        return new WorkerStatementResult(stmts);
    }

    private static String extractVariables(Context cx, String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        // Pattern to match ${...} expressions, including those with :: and other
        // characters
        Pattern pattern = Pattern.compile("\\$\\{([^}]+)\\}");
        Matcher matcher = pattern.matcher(value);
        StringBuilder result = new StringBuilder();
        int lastEnd = 0;
        boolean concat = false;
        while (matcher.find()) {
            concat = true;
            // Add text before the match
            result.append(value, lastEnd, matcher.start());

            // Extract the variable name (content between ${ and })
            String variableName = matcher.group(1);

            // Process the variable name using ConversionUtils.processPropertyName
            String processedName = ConversionUtils.processPropertyName(cx, variableName);

            // Replace with processed variable name
            result.append("${").append(processedName).append("}");

            lastEnd = matcher.end();
        }

        // Add remaining text after the last match
        result.append(value.substring(lastEnd));
        String body = result.toString();
        return concat ? "string `%s`".formatted(body) : "\"%s\"".formatted(body);
    }

    private static WorkerStatementResult convertHttpRequest(Context ctx, HttpRequest httpRequest) {
        List<Statement> stmts = new ArrayList<>();
        boolean isConfigurablePath = httpRequest.path().startsWith("${");
        String path;
        if (isConfigurablePath) {
            path = extractVariables(ctx, httpRequest.path());
        } else {
            path = getBallerinaClientResourcePath(ctx, httpRequest.path());
        }
        String method = httpRequest.method();
        String url = extractVariables(ctx, httpRequest.url());
        Map<String, String> queryParams = httpRequest.queryParams();

        stmts.add(stmtFrom("\n\n// http client request\n"));
        stmts.add(stmtFrom(String.format("http:Client %s = check new(%s);", httpRequest.configRef(), url)));

        String headersVar = httpRequest.headersScript()
                .map(script -> processMapScript(ctx, script, stmts, "headers"))
                .orElse(null);

        String uriParamsVar = httpRequest.uriParamsScript()
                .map(script -> processMapScript(ctx, script, stmts, "uriParams"))
                .orElse(null);

        String queryParamsVar = httpRequest.queryParamsScript()
                .map(script -> processMapScript(ctx, script, stmts, "queryParams"))
                .orElse(null);

        String clientResultVar = String.format(Constants.VAR_CLIENT_RESULT_TEMPLATE,
                ctx.projectCtx.counters.clientResultVarCount++);

        if (isConfigurablePath) {
            String pathRepr = buildPathRepresentation(httpRequest.uriParamsScript());
            String queryParamsStr = buildQueryParamsString(httpRequest.queryParamsScript());
            String commentMessage = String.format("Instead try to use %s->%s.%s.%s%s",
                    clientResultVar, httpRequest.configRef(), pathRepr, method.toLowerCase(), queryParamsStr);
            String pathBuilderFn = generateRequestPathBuilder(ctx, commentMessage);
            List<String> params = new ArrayList<>();
            params.add(path);
            params.add(uriParamsVar != null ? uriParamsVar : "{}");
            params.add(queryParamsVar != null ? queryParamsVar : "{}");
            String queryPathVarName = "queryPath";

            stmts.add(stmtFrom(
                    "string %s = %s(%s);".formatted(queryPathVarName, pathBuilderFn, String.join(", ", params))));
            stmts.add(stmtFrom("%s %s = check %s->%s(%s, %s);".formatted(Constants.HTTP_RESPONSE_TYPE,
                    clientResultVar, httpRequest.configRef(), method.toLowerCase(), queryPathVarName,
                    headersVar != null ? headersVar : "")));
        } else {
            // Build HTTP request parameters
            List<String> params = new ArrayList<>();
            String queryParamsStr = genQueryParam(ctx, queryParams);
            if (!queryParamsStr.isEmpty()) {
                params.add(queryParamsStr);
            }
            if (headersVar != null) {
                params.add(headersVar);
            }
            if (uriParamsVar != null) {
                params.add(uriParamsVar);
            }
            if (queryParamsVar != null) {
                params.add(queryParamsVar);
            }

            stmts.add(stmtFrom("%s %s = check %s->%s.%s(%s);".formatted(Constants.HTTP_RESPONSE_TYPE,
                    clientResultVar, httpRequest.configRef(), path, method.toLowerCase(), String.join(", ", params))));
        }

        stmts.add(stmtFrom(String.format("%s.payload = check %s.getJsonPayload();",
                Constants.CONTEXT_REFERENCE, clientResultVar)));
        return new WorkerStatementResult(stmts);
    }

    private static String generateRequestPathBuilder(Context ctx, String todoComment) {
        ctx.addImport(new Import("ballerina", "lang.regexp"));
        String pathParam = "path";
        String uriParam = "uriParams";
        String queryParam = "queryParams";
        List<Statement> body = List.of(common.ConversionUtils.stmtFrom("""
                \n// TODO: %s
                string requestPath = %s;
                foreach var [key, value] in %s.entries() {
                    requestPath = regexp:replaceAll(check regexp:fromString(string `\\{${key}\\}`), requestPath, value);
                }
                foreach var [key, value] in %s.entries() {
                    requestPath = regexp:replaceAll(check regexp:fromString(string `\\{${key}\\}`), requestPath, value);
                }
                return requestPath;
                """.formatted(todoComment, pathParam, uriParam, queryParam)));
        String functionName = "pathBuilder" + ctx.projectCtx.counters.requestPathBuilderCount;
        BallerinaModel.TypeDesc.MapTypeDesc stringMapTD = new BallerinaModel.TypeDesc.MapTypeDesc(
                BallerinaModel.TypeDesc.BuiltinType.STRING);
        ctx.addFunction(new Function(functionName,
                List.of(new Parameter(pathParam, BallerinaModel.TypeDesc.BuiltinType.STRING),
                        new Parameter(uriParam, stringMapTD),
                        new Parameter(queryParam, stringMapTD)),
                BallerinaModel.TypeDesc.BuiltinType.STRING, body));
        return functionName;
    }

    private static String processMapScript(Context ctx, String script, List<Statement> stmts, String varName) {
        stmts.add(common.ConversionUtils.stmtFrom(
                "map<string> %s = %s;".formatted(varName, convertMELToBal(ctx, script, false))));
        return varName;
    }

    /**
     * Builds a path representation from URI params script. Extracts keys from the
     * DataWeave script and formats them as
     * Ballerina path segments.
     *
     * @param uriParamsScript Optional DataWeave script for URI params
     * @return Path representation like "/[id]" or "/[id]/[name]" or "/" if no URI
     *         params
     */
    private static String buildPathRepresentation(Optional<String> uriParamsScript) {
        final String basePath = "/basePath/";
        if (uriParamsScript.isEmpty()) {
            return basePath;
        }
        List<String> keys = extractMapKeys(uriParamsScript.get());
        if (keys.isEmpty()) {
            return basePath;
        }
        return basePath + String.join("/", keys.stream().map(key -> "[" + key + "]").toList());
    }

    /**
     * Builds a query params string representation from query params script.
     * Extracts keys from the DataWeave script and
     * formats them as function parameters.
     *
     * @param queryParamsScript Optional DataWeave script for query params
     * @return Query params string like "(language=language)" or "(key1=key1,
     *         key2=key2)" or empty string
     */
    private static String buildQueryParamsString(Optional<String> queryParamsScript) {
        if (queryParamsScript.isEmpty()) {
            return "";
        }
        List<String> keys = extractMapKeys(queryParamsScript.get());
        if (keys.isEmpty()) {
            return "";
        }
        return "(" + String.join(", ", keys.stream().map(key -> key + "=" + key).toList()) + ")";
    }

    /**
     * Extracts map keys from a DataWeave script by finding "key" : patterns.
     *
     * @param script DataWeave script containing a map literal
     * @return List of extracted keys
     */
    private static List<String> extractMapKeys(String script) {
        List<String> keys = new ArrayList<>();
        // Pattern to match "key" : in map literals
        Pattern pattern = Pattern.compile("\"([^\"]+)\"\\s*:");
        Matcher matcher = pattern.matcher(script);
        while (matcher.find()) {
            keys.add(matcher.group(1));
        }
        return keys;
    }

    private static WorkerStatementResult convertDatabase(Context ctx, Database database) {
        ctx.addImport(new Import(Constants.ORG_BALLERINA, Constants.MODULE_SQL, Optional.empty()));
        String streamConstraintType = Constants.GENERIC_RECORD_TYPE_REF;

        if (!ctx.projectCtx.typeDefExists(streamConstraintType)) {
            ctx.currentFileCtx.balConstructs.typeDefs.put(streamConstraintType,
                    new ModuleTypeDef(streamConstraintType, typeFrom(Constants.GENERIC_RECORD_TYPE)));
        }

        List<Statement> stmts = new ArrayList<>();
        stmts.add(stmtFrom("\n\n// database operation\n"));
        String dbQueryVarName = Constants.VAR_DB_QUERY_TEMPLATE.formatted(ctx.projectCtx.counters.dbQueryVarCount++);
        stmts.add(stmtFrom("%s %s = %s;".formatted(Constants.SQL_PARAMETERIZED_QUERY_TYPE, dbQueryVarName,
                "`%s`".formatted(database.query()))));

        String dbStreamVarName = Constants.VAR_DB_STREAM_TEMPLATE.formatted(ctx.projectCtx.counters.dbStreamVarCount++);
        stmts.add(stmtFrom("%s %s= %s->query(%s);"
                .formatted(Constants.DB_QUERY_DEFAULT_TEMPLATE.formatted(streamConstraintType),
                        dbStreamVarName, database.configRef(), dbQueryVarName)));

        if (!database.unsupportedBlocks().isEmpty()) {
            stmts.add(stmtFrom(convertToUnsupportedTODO(ctx, database.unsupportedBlocks())));
        }

        if (database.kind() == Kind.DB_SELECT) {
            String dbSelectVarName = Constants.VAR_DB_SELECT_TEMPLATE
                    .formatted(ctx.projectCtx.counters.dbSelectVarCount++);
            stmts.add(stmtFrom(String.format("%s[] %s = check from %s %s in %s select %s;", streamConstraintType,
                    dbSelectVarName, streamConstraintType, Constants.VAR_ITERATOR, dbStreamVarName,
                    Constants.VAR_ITERATOR)));
            // db:select implicitly sets the payload
            stmts.add(stmtFrom("%s.payload = %s;".formatted(Constants.CONTEXT_REFERENCE, dbSelectVarName)));
        }
        return new WorkerStatementResult(stmts);
    }

    private static WorkerStatementResult convertAsync(Context ctx, Async async) {
        List<Statement> body = convertTopLevelMuleBlocks(ctx, async.flowBlocks());
        int asyncFuncId = ctx.projectCtx.counters.asyncFuncCount++;
        String funcName = String.format(FUNC_NAME_ASYC_TEMPLATE, asyncFuncId);
        Function function = Function.publicFunction(funcName, Constants.FUNC_PARAMS_WITH_CONTEXT, body);
        ctx.currentFileCtx.balConstructs.functions.add(function);

        List<Statement> stmts = new ArrayList<>();
        stmts.add(stmtFrom("\n\n// async operation\n"));
        stmts.add(stmtFrom(String.format("_ = start %s(%s);", funcName, Constants.CONTEXT_REFERENCE)));
        return new WorkerStatementResult(stmts);
    }

    private static WorkerStatementResult convertTry(Context ctx, Try tr) {
        List<Statement> stmts = convertTopLevelMuleBlocks(ctx, tr.flowBlocks());
        return new WorkerStatementResult(stmts);
    }

    private static WorkerStatementResult convertForeach(Context ctx, Foreach foreach) {
        List<Statement> stmts = new ArrayList<>();
        String collection = convertMuleExprToBal(ctx, foreach.collection());

        // Generate unique variable names for the foreach loop
        String iteratorVar = String.format(Constants.VAR_ITERATOR_TEMPLATE,
                ctx.projectCtx.counters.foreachIteratorCount++);
        String originalPayloadVar = String.format(Constants.VAR_ORIGINAL_PAYLOAD_TEMPLATE,
                ctx.projectCtx.counters.originalPayloadVarCount++);

        stmts.add(stmtFrom("\n\n// foreach loop\n"));

        // Store original payload
        stmts.add(stmtFrom(String.format("anydata %s = %s.payload;", originalPayloadVar, Constants.CONTEXT_REFERENCE)));

        List<Statement> foreachBody = new ArrayList<>();
        foreachBody.add(stmtFrom(String.format("%s.payload = %s;", Constants.CONTEXT_REFERENCE, iteratorVar)));
        foreachBody.addAll(convertTopLevelMuleBlocks(ctx, foreach.flowBlocks()));

        ForeachStatement foreachStmt = new ForeachStatement(
                new TypeBindingPattern(typeFrom("anydata"), iteratorVar),
                exprFrom(collection),
                foreachBody
        );
        stmts.add(foreachStmt);

        // Restore original payload after foreach
        stmts.add(stmtFrom(String.format("%s.payload = %s;", Constants.CONTEXT_REFERENCE, originalPayloadVar)));

        return new WorkerStatementResult(stmts);
    }

    private static WorkerStatementResult convertScatterGather(Context ctx, ScatterGather scatterGather) {
        List<Route> routes = scatterGather.routes();
        if (routes.isEmpty()) {
            return new WorkerStatementResult(List.of());
        }

        List<Statement> stmts = new ArrayList<>();
        stmts.add(stmtFrom("\n\n// scatter-gather parallel execution\n"));

        // Create named workers for each route
        List<NamedWorkerDecl> workers = new ArrayList<>();
        String[] workerNames = new String[scatterGather.routes().size()];
        for (int i = 0; i < scatterGather.routes().size(); i++) {
            Route route = scatterGather.routes().get(i);
            String workerName = Constants.WORKER_SCATTER_GATHER
                    .formatted(ctx.projectCtx.counters.scatterGatherWorkerCount++);
            workerNames[i] = workerName;

            List<Statement> workerBody = new ArrayList<>();
            workerBody.add(stmtFrom(String.format("\n// Route %d\n", i)));

            // Convert route blocks to worker statements
            List<Statement> routeStmts = convertMuleRegularBlocks(ctx, route.flowBlocks()).statements();
            workerBody.addAll(routeStmts);

            // Send result back to main worker
            workerBody.add(stmtFrom(String.format("return %s.payload;", Constants.CONTEXT_REFERENCE)));

            NamedWorkerDecl workerDecl = new NamedWorkerDecl(workerName, Optional.of(typeFrom("anydata|error")),
                    workerBody);
            workers.add(workerDecl);
        }

        ForkStatement fork = new ForkStatement(workers);
        stmts.add(fork);

        // Collect results from all workers
        stmts.add(stmtFrom("\n\n// wait for all workers to complete\n"));
        String workerResultsVar =
                Constants.VAR_WORKER_RESULT_TEMPLATE.formatted(ctx.projectCtx.counters.workerWaitVarCount++);
        String scatterGatherResultsVar =
                Constants.VAR_SCATTER_GATHER_TEMPLATE.formatted(ctx.projectCtx.counters.scatterGatherVarCount++);
        stmts.add(stmtFrom(String.format("map<anydata|error> %s = wait {%s};",
                workerResultsVar, String.join(",", workerNames))));
        stmts.add(stmtFrom(String.format("map<anydata> %s = %s.entries().'map(e => %s(e[0], e[1])).'map(m => check m);",
                scatterGatherResultsVar, workerResultsVar, Constants.FUNC_WRAP_ROUTE_ERR)));

        if (!ctx.projectCtx.functionExists(Constants.FUNC_WRAP_ROUTE_ERR)) {
            List<Parameter> params = new ArrayList<>();
            params.add(new Parameter("key", typeFrom("string")));
            params.add(new Parameter("value", typeFrom("anydata|error")));

            List<Statement> body = new ArrayList<>();
            body.add(stmtFrom(
                    "if value is error { return error(string `Error in Route ${key}: ${value.message()}`, value); }"
            ));
            body.add(stmtFrom("return value;"));

            Function errorWrapFunc = Function.publicFunction(Constants.FUNC_WRAP_ROUTE_ERR, params,
                    typeFrom("anydata|error"), body);
            ctx.currentFileCtx.balConstructs.commonFunctions.put(Constants.FUNC_WRAP_ROUTE_ERR, errorWrapFunc);
        }

        // Set the collected results as payload
        stmts.add(stmtFrom(String.format("%s.payload = %s;\n\n", Constants.CONTEXT_REFERENCE,
                scatterGatherResultsVar)));
        return new WorkerStatementResult(stmts);
    }

    private static WorkerStatementResult convertFirstSuccessful(Context ctx, FirstSuccessful firstSuccessful) {
        List<Route> routes = firstSuccessful.routes();
        if (routes.isEmpty()) {
            return new WorkerStatementResult(List.of());
        }

        // Create a function for each route
        String[] funcNames = new String[firstSuccessful.routes().size()];
        for (int i = 0; i < firstSuccessful.routes().size(); i++) {
            Route route = firstSuccessful.routes().get(i);
            String funcName = Constants.FUNC_FIRST_SUCCESSFUL_ROUTE
                    .formatted(ctx.projectCtx.counters.firstSuccessfulFuncCount++);
            funcNames[i] = funcName;

            List<Statement> funcBody = new ArrayList<>();
            funcBody.add(stmtFrom("\n// Route %d\n".formatted(i)));

            // Convert route blocks to worker statements
            List<Statement> routeStmts = convertMuleRegularBlocks(ctx, route.flowBlocks()).statements();
            funcBody.addAll(routeStmts);

            funcBody.add(stmtFrom("return %s.payload;".formatted(Constants.CONTEXT_REFERENCE)));

            Function func = Function.publicFunction(funcName, Constants.FUNC_PARAMS_WITH_CONTEXT,
                    typeFrom("anydata|error"), funcBody);
            ctx.currentFileCtx.balConstructs.functions.add(func);
        }

        // Create a function that calls each function sequentially until one succeeds
        List<Statement> firstSuccessfulBody = new ArrayList<>();
        for (int i = 0; i < funcNames.length; i++) {
            String funcName = funcNames[i];
            firstSuccessfulBody.add(stmtFrom("anydata|error r%s = %s(%s);".formatted(i, funcName,
                    Constants.CONTEXT_REFERENCE)));
            firstSuccessfulBody.add(stmtFrom("if r%s !is error { return r%s; }".formatted(i, i)));
        }

        // If all routes fail, return an error with last error message
        firstSuccessfulBody.add(stmtFrom("return error(\"All routes failed\", r%s);".formatted(funcNames.length - 1)));

        int firstSuccessfulCount = ctx.projectCtx.counters.firstSuccessfulCount;
        ctx.projectCtx.counters.firstSuccessfulCount++;

        String firstSuccessfulFuncName = Constants.FUNC_FIRST_SUCCESSFUL.formatted(firstSuccessfulCount);
        Function firstSuccessfulFunc = Function.publicFunction(
                firstSuccessfulFuncName, Constants.FUNC_PARAMS_WITH_CONTEXT,
                typeFrom("anydata|error"), firstSuccessfulBody);
        ctx.currentFileCtx.balConstructs.functions.add(firstSuccessfulFunc);

        List<Statement> stmts = new ArrayList<>();
        stmts.add(stmtFrom("\n\n// first-successful sequential route execution\n"));
        String firstSuccessfulResultVar = Constants.VAR_FIRST_SUCCESSFUL_RESULT.formatted(firstSuccessfulCount);
        stmts.add(stmtFrom("anydata %s = check %s(%s);".formatted(firstSuccessfulResultVar, firstSuccessfulFuncName,
                Constants.CONTEXT_REFERENCE)));
        stmts.add(stmtFrom("%s.payload = %s;\n\n".formatted(Constants.CONTEXT_REFERENCE, firstSuccessfulResultVar)));
        return new WorkerStatementResult(stmts);
    }

    private static WorkerStatementResult convertVMPublish(Context ctx, VMPublish vmPublish) {
        String queueName = vmPublish.queueName();
        String funcName = ctx.projectCtx.vmQueueNameToBalFuncMap.get(queueName);
        if (funcName == null) {
            funcName = String.format(Constants.FUNC_NAME_VM_RECEIVE_TEMPLATE,
                    ctx.projectCtx.counters.vmReceiveFuncCount++);
            ctx.projectCtx.vmQueueNameToBalFuncMap.put(queueName, funcName);
        }

        List<Statement> namedWorkerBody = new ArrayList<>(4);
        namedWorkerBody.add(stmtFrom("\n// VM Listener\n"));
        namedWorkerBody.add(stmtFrom("anydata receivedPayload = <- function;"));
        namedWorkerBody.add(stmtFrom("ctx.payload = receivedPayload;"));
        namedWorkerBody.add(stmtFrom(String.format("%s(ctx);", funcName)));

        List<Statement> stmts = new ArrayList<>(2);
        stmts.add(stmtFrom("\n\n// VM Publish\n"));
        stmts.add(stmtFrom(String.format("%s.payload -> W;", Constants.CONTEXT_REFERENCE)));

        NamedWorkerDecl worker = new NamedWorkerDecl("W", Optional.of(typeFrom("error?")), namedWorkerBody);
        return new WorkerStatementResult(stmts, List.of(worker));
    }

    private static WorkerStatementResult convertTransformMessage(Context ctx, TransformMessage transformMsg) {
        List<Statement> stmts = new ArrayList<>();
        DWReader.processDWElements(transformMsg.children(), ctx, stmts);
        stmts.add(stmtFrom("%s.payload = %s;".formatted(Constants.CONTEXT_REFERENCE,
                DWUtils.DATAWEAVE_OUTPUT_VARIABLE_NAME)));
        return new WorkerStatementResult(stmts);
    }

    private static WorkerStatementResult convertUnsupportedBlock(Context ctx, UnsupportedBlock unsupportedBlock) {
        String comment = ConversionUtils.convertToUnsupportedTODO(ctx, unsupportedBlock);
        // TODO: comment is not a statement. Find a better way to handle this
        // This works for now because we concatenate and create a body block `{ stmts }`
        // before parsing.
        return new WorkerStatementResult(List.of(stmtFrom(comment)));
    }

    // Mule 4.x Error Handling Converters
    private static ConversionResult convertErrorHandler(Context ctx, ErrorHandler errorHandler) {
        List<Statement> onFailBody;
        if (errorHandler.ref().isEmpty()) {
            onFailBody = convertErrorHandlerRecords(ctx, errorHandler.errorHandlers());
        } else {
            // A reference function is provided. We create a function call statement
            String refName = errorHandler.ref();
            String funcRef = ConversionUtils.convertToBalIdentifier(refName);
            BallerinaStatement funcCallStmt = stmtFrom(String.format("%s(%s, %s);", funcRef,
                    Constants.CONTEXT_REFERENCE, Constants.ON_FAIL_ERROR_VAR_REF));
            onFailBody = Collections.singletonList(funcCallStmt);
        }

        TypeBindingPattern typeBindingPattern = new TypeBindingPattern(BAL_ERROR_TYPE, Constants.ON_FAIL_ERROR_VAR_REF);
        OnFailClause onFailClause = new OnFailClause(onFailBody, typeBindingPattern);
        return new FailClauseResult(onFailClause);
    }

    private static WorkerStatementResult convertOnErrorContinue(Context ctx, OnErrorContinue onErrorContinue) {
        List<Statement> stmts = new ArrayList<>();
        stmts.add(stmtFrom("\n// on-error-continue\n"));

        if (onErrorContinue.logException().equals("true")) {
            stmts.add(stmtFrom("log:printError(\"Message: \" + %s.message());"
                    .formatted(Constants.ON_FAIL_ERROR_VAR_REF)));
            stmts.add(stmtFrom("log:printError(\"Trace: \" + %s.stackTrace().toString());"
                    .formatted(Constants.ON_FAIL_ERROR_VAR_REF)));
            stmts.add(stmtFrom("\n\n"));
        }

        List<Statement> errorBlocks = convertMuleRegularBlocks(ctx, onErrorContinue.errorBlocks()).statements();
        stmts.addAll(errorBlocks);

        return new WorkerStatementResult(stmts);
    }

    private static WorkerStatementResult convertOnErrorPropagate(Context ctx, OnErrorPropagate onErrorPropagate) {
        List<Statement> stmts = new ArrayList<>();
        stmts.add(stmtFrom("\n// on-error-propagate\n"));

        if (onErrorPropagate.logException().equals("true")) {
            ctx.addImport(new Import(Constants.ORG_BALLERINA, Constants.MODULE_LOG));
            stmts.add(stmtFrom("log:printError(\"Message: \" + %s.message());"
                    .formatted(Constants.ON_FAIL_ERROR_VAR_REF)));
            stmts.add(stmtFrom("log:printError(\"Trace: \" + %s.stackTrace().toString());"
                    .formatted(Constants.ON_FAIL_ERROR_VAR_REF)));
            stmts.add(stmtFrom("\n\n"));
        }

        List<Statement> errorBlocks = convertMuleRegularBlocks(ctx, onErrorPropagate.errorBlocks()).statements();
        stmts.addAll(errorBlocks);

        if (ctx.projectCtx.attributes.containsKey(Constants.HTTP_RESPONSE_REF)) {
            stmts.add(stmtFrom("%s.%s.statusCode = 500;".formatted(Constants.ATTRIBUTES_FIELD_ACCESS,
                    Constants.HTTP_RESPONSE_REF)));
        } else {
            // Add a panic statement to propagate the error
            stmts.add(stmtFrom("panic " + Constants.ON_FAIL_ERROR_VAR_REF + ";"));
        }

        return new WorkerStatementResult(stmts);
    }

    private static WorkerStatementResult convertRaiseError(Context ctx, RaiseError raiseError) {
        String errorType = raiseError.type().replace(":", "__");
        if (!ctx.projectCtx.typeDefExists(errorType)) {
            ctx.currentFileCtx.balConstructs.typeDefs.put(
                    errorType, new ModuleTypeDef(errorType, typeFrom("distinct error"))
            );
        }
        BallerinaStatement stmt = stmtFrom("fail error %s(\"%s\");".formatted(errorType, raiseError.description()));
        return new WorkerStatementResult(List.of(stmt));
    }
}
