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
package mule.v3.converter;

import mule.v3.Constants;
import mule.v3.Context;
import mule.v3.ConversionUtils;
import mule.v3.dataweave.converter.DWReader;
import mule.v3.dataweave.converter.DWUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static common.BallerinaModel.BlockFunctionBody;
import static common.BallerinaModel.Expression;
import static common.BallerinaModel.Function;
import static common.BallerinaModel.Import;
import static common.BallerinaModel.ModuleTypeDef;
import static common.BallerinaModel.OnFailClause;
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
import static mule.v3.Constants.BAL_ERROR_TYPE;
import static mule.v3.Constants.FUNC_NAME_ASYC_TEMPLATE;
import static mule.v3.ConversionUtils.convertMuleExprToBal;
import static mule.v3.ConversionUtils.convertMuleExprToBalStringLiteral;
import static mule.v3.ConversionUtils.genQueryParam;
import static mule.v3.ConversionUtils.getBallerinaClientResourcePath;
import static mule.v3.ConversionUtils.inferTypeFromBalExpr;
import static mule.v3.model.MuleModel.Async;
import static mule.v3.model.MuleModel.CatchExceptionStrategy;
import static mule.v3.model.MuleModel.Choice;
import static mule.v3.model.MuleModel.ChoiceExceptionStrategy;
import static mule.v3.model.MuleModel.Database;
import static mule.v3.model.MuleModel.Enricher;
import static mule.v3.model.MuleModel.ExpressionComponent;
import static mule.v3.model.MuleModel.FirstSuccessful;
import static mule.v3.model.MuleModel.FlowReference;
import static mule.v3.model.MuleModel.Foreach;
import static mule.v3.model.MuleModel.HttpRequest;
import static mule.v3.model.MuleModel.Kind;
import static mule.v3.model.MuleModel.LogLevel;
import static mule.v3.model.MuleModel.Logger;
import static mule.v3.model.MuleModel.MuleRecord;
import static mule.v3.model.MuleModel.ObjectToJson;
import static mule.v3.model.MuleModel.ObjectToString;
import static mule.v3.model.MuleModel.Payload;
import static mule.v3.model.MuleModel.ProcessorChain;
import static mule.v3.model.MuleModel.QueryType;
import static mule.v3.model.MuleModel.ReferenceExceptionStrategy;
import static mule.v3.model.MuleModel.RemoveVariable;
import static mule.v3.model.MuleModel.SetSessionVariable;
import static mule.v3.model.MuleModel.SetVariable;
import static mule.v3.model.MuleModel.TransformMessage;
import static mule.v3.model.MuleModel.UnsupportedBlock;
import static mule.v3.model.MuleModel.VMOutboundEndpoint;
import static mule.v3.model.MuleModel.WhenInChoice;
import static mule.v3.model.MuleModel.ScatterGather;

public class MuleConfigConverter {

    public static List<Statement> convertTopLevelMuleBlocks(Context ctx, List<MuleRecord> flowBlocks) {
        // Add function body statements
        List<Statement> body = new ArrayList<>();
        List<Statement> workers = new ArrayList<>();

        // Read flow blocks
        for (MuleRecord record : flowBlocks) {
            List<Statement> stmts = convertMuleBlock(ctx, record);
            // TODO: handle these properly
            if (stmts.size() > 1 && stmts.getFirst() instanceof NamedWorkerDecl namedWorkerDecl) {
                workers.add(namedWorkerDecl);
                stmts.remove(namedWorkerDecl);
            }

            if (stmts.size() == 1 && stmts.getFirst() instanceof DoStatement doStatement) {
                body = new ArrayList<>(Collections.singletonList(new DoStatement(body, doStatement.onFailClause())));
                continue;
            }
            body.addAll(stmts);
        }

        workers.addAll(body);
        return workers;
    }

    public static List<Statement> convertMuleBlocks(Context ctx, List<MuleRecord> muleRecords) {
        List<Statement> statements = new ArrayList<>();
        for (MuleRecord mr : muleRecords) {
            List<Statement> stmts = convertMuleBlock(ctx, mr);
            statements.addAll(stmts);
        }
        return statements;
    }

    public static List<Statement> convertMuleBlock(Context ctx,
                                                   MuleRecord muleRec) {
        switch (muleRec) {
            case Logger lg -> {
                return convertLogger(ctx, lg);
            }
            case SetVariable setVariable -> {
                return convertSetVariable(ctx, setVariable);
            }
            case SetSessionVariable setSessionVariable -> {
                return convertSetSessionVariable(ctx, setSessionVariable);
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
            case CatchExceptionStrategy catchExpStr -> {
                return convertCatchExceptionStrategy(ctx, catchExpStr);
            }
            case ChoiceExceptionStrategy choiceExpStr -> {
                return convertChoiceExceptionStrategy(ctx, choiceExpStr);
            }
            case ReferenceExceptionStrategy refExpStr -> {
                return convertReferenceExceptionStrategy(ctx, refExpStr);
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
            case VMOutboundEndpoint vmEndPoint -> {
                return convertVMOutboundEndpoint(ctx, vmEndPoint);
            }
            case TransformMessage transformMessage -> {
                return convertTransformMessage(ctx, transformMessage);
            }
            case ScatterGather scatterGather -> {
                return convertScatterGather(ctx, scatterGather);
            }
            case FirstSuccessful firstSuccessful -> {
                return convertFirstSuccessful(ctx, firstSuccessful);
            }
            case Foreach foreach -> {
                return convertForeach(ctx, foreach);
            }
            case UnsupportedBlock unsupportedBlock -> {
                return convertUnsupportedBlock(ctx, unsupportedBlock);
            }
            case null -> throw new IllegalStateException();
            default -> throw new UnsupportedOperationException();
        }
    }

    private static List<Statement> convertLogger(Context ctx, Logger lg) {
        String logFuncName = getBallerinaLogFunction(lg.level());
        String stringLiteral = convertMuleExprToBalStringLiteral(ctx, lg.message());
        BallerinaStatement stmt = stmtFrom("log:%s(%s);".formatted(logFuncName, stringLiteral));
        return List.of(stmt);
    }

    private static String getBallerinaLogFunction(LogLevel logLevel) {
        return switch (logLevel) {
            case DEBUG -> "printDebug";
            case ERROR -> "printError";
            case INFO, TRACE -> "printInfo";
            case WARN -> "printWarn";
        };
    }

    private static List<Statement> convertSetVariable(Context ctx, SetVariable setVariable) {
        String varName = ConversionUtils.convertToBalIdentifier(setVariable.variableName());
        String balExpr = convertMuleExprToBal(ctx, setVariable.value());
        String type = inferTypeFromBalExpr(balExpr);

        if (!ctx.projectCtx.flowVars.containsKey(varName)) {
            ctx.projectCtx.flowVars.put(varName, type);
        }

        var stmt = stmtFrom(String.format("%s.%s = %s;", Constants.FLOW_VARS_FIELD_ACCESS, varName, balExpr));
        return List.of(stmt);
    }

    private static List<Statement> convertSetSessionVariable(Context ctx, SetSessionVariable setSessionVariable) {
        String varName = ConversionUtils.convertToBalIdentifier(setSessionVariable.variableName());
        String balExpr = convertMuleExprToBal(ctx, setSessionVariable.value());
        String type = inferTypeFromBalExpr(balExpr);

        if (!ctx.projectCtx.sessionVars.containsKey(varName)) {
            ctx.projectCtx.sessionVars.put(varName, type);
        }

        var stmt = stmtFrom(String.format("%s.%s = %s;", Constants.SESSION_VARS_FIELD_ACCESS, varName, balExpr));
        return List.of(stmt);
    }

    private static List<Statement> convertRemoveVariable(Context ctx, RemoveVariable removeVariable) {
        String varName = ConversionUtils.convertToBalIdentifier(removeVariable.variableName());
        Statement stmt;
        if (removeVariable.kind() == Kind.REMOVE_VARIABLE && ctx.projectCtx.flowVars.containsKey(varName)) {
            stmt = stmtFrom(String.format("%s.%s = %s;", Constants.FLOW_VARS_FIELD_ACCESS, varName, "()"));
        } else if (removeVariable.kind() == Kind.REMOVE_SESSION_VARIABLE &&
                ctx.projectCtx.sessionVars.containsKey(varName)) {
            stmt = stmtFrom(String.format("%s.%s = %s;", Constants.SESSION_VARS_FIELD_ACCESS, varName, "()"));
        } else {
            throw new IllegalStateException();
        }
        return List.of(stmt);
    }

    private static List<Statement> convertSetPayload(Context ctx, Payload payload) {
        List<Statement> stmts = new ArrayList<>();
        String pyld = convertMuleExprToBal(ctx, payload.expr());
        String type = inferTypeFromBalExpr(pyld);
        String payloadVar = String.format(Constants.VAR_PAYLOAD_TEMPLATE, ctx.projectCtx.counters.payloadVarCount++);
        stmts.add(stmtFrom("\n\n// set payload\n"));
        stmts.add(stmtFrom(String.format("%s %s = %s;", type, payloadVar, pyld)));
        stmts.add(stmtFrom(String.format("%s.payload = %s;", Constants.CONTEXT_REFERENCE,
                payloadVar)));
        return stmts;
    }

    private static List<Statement> convertChoice(Context ctx, Choice choice) {
        List<WhenInChoice> whens = choice.whens();
        assert !whens.isEmpty(); // For valid mule config, there is at least one when

        WhenInChoice firstWhen = whens.getFirst();
        String ifCondition = convertMuleExprToBal(ctx, firstWhen.condition());
        List<Statement> ifBody = new ArrayList<>();
        for (MuleRecord mr : firstWhen.process()) {
            List<Statement> statements = convertMuleBlock(ctx, mr);
            ifBody.addAll(statements);
        }

        List<ElseIfClause> elseIfClauses = new ArrayList<>(whens.size() - 1);
        for (int i = 1; i < whens.size(); i++) {
            WhenInChoice when = whens.get(i);
            List<Statement> elseIfBody = new ArrayList<>();
            for (MuleRecord mr : when.process()) {
                List<Statement> statements = convertMuleBlock(ctx, mr);
                elseIfBody.addAll(statements);
            }
            ElseIfClause elseIfClause = new ElseIfClause(exprFrom(convertMuleExprToBal(ctx, when.condition())),
                    elseIfBody);
            elseIfClauses.add(elseIfClause);
        }

        List<Statement> elseBody = new ArrayList<>(choice.otherwiseProcess().size());
        for (MuleRecord mr : choice.otherwiseProcess()) {
            List<Statement> statements = convertMuleBlock(ctx, mr);
            elseBody.addAll(statements);
        }

        var ifElseStmt = new IfElseStatement(exprFrom(ifCondition), ifBody, elseIfClauses, elseBody);
        return List.of(ifElseStmt);
    }

    private static List<Statement> convertFlowReference(Context ctx, FlowReference flowReference) {
        String flowName = flowReference.flowName();
        List<Statement> statements = new ArrayList<>();
        String funcRef = ctx.getFlowFuncRef(flowName).orElseGet(() -> {
                    statements.add(new Statement.Comment("FIXME: failed to resolve flow %s".formatted(flowName)));
                    return flowName;
        });
        var stmt = stmtFrom(String.format("%s(%s);", funcRef, Constants.CONTEXT_REFERENCE));
        statements.add(stmt);
        return statements;
    }

    private static List<Statement> convertObjectToJson(Context ctx, ObjectToJson objectToJson) {
        List<Statement> stmts = new ArrayList<>();
        stmts.add(stmtFrom("\n\n// json transformation\n"));
        // object to json transformer implicitly sets the payload
        stmts.add(stmtFrom("%s = %s.toJson();".formatted(Constants.PAYLOAD_FIELD_ACCESS,
                Constants.PAYLOAD_FIELD_ACCESS)));
        return stmts;
    }

    private static List<Statement> convertObjectToString(Context ctx, ObjectToString objectToString) {
        List<Statement> stmts = new ArrayList<>();
        stmts.add(stmtFrom("\n\n// string transformation\n"));
        // object to string transformer implicitly sets the payload
        stmts.add(stmtFrom("%s = %s.toString();".formatted(Constants.PAYLOAD_FIELD_ACCESS,
                Constants.PAYLOAD_FIELD_ACCESS)));
        return stmts;
    }

    private static List<Statement> convertCatchExceptionStrategy(Context ctx, CatchExceptionStrategy catchExpStr) {
        List<Statement> onFailBody = getCatchExceptionBody(ctx, catchExpStr);
        OnFailClause onFailClause = new OnFailClause(onFailBody);
        DoStatement doStatement = new DoStatement(Collections.emptyList(), onFailClause);
        return List.of(doStatement);
    }

    public static List<Statement> getCatchExceptionBody(Context ctx, CatchExceptionStrategy catchExceptionStrategy) {
        return convertMuleBlocks(ctx, catchExceptionStrategy.catchBlocks());
    }

    private static List<Statement> convertChoiceExceptionStrategy(Context ctx, ChoiceExceptionStrategy choiceExpStr) {
        List<Statement> onFailBody = getChoiceExceptionBody(ctx, choiceExpStr);
        TypeBindingPattern typeBindingPattern = new TypeBindingPattern(BAL_ERROR_TYPE,
                Constants.ON_FAIL_ERROR_VAR_REF);
        OnFailClause onFailClause = new OnFailClause(onFailBody, typeBindingPattern);
        DoStatement doStatement = new DoStatement(Collections.emptyList(), onFailClause);
        return List.of(doStatement);
    }

    public static List<Statement> getChoiceExceptionBody(Context ctx, ChoiceExceptionStrategy choiceExceptionStrategy) {
        List<CatchExceptionStrategy> catchExceptionStrategies = choiceExceptionStrategy.catchExceptionStrategyList();
        assert !catchExceptionStrategies.isEmpty();

        CatchExceptionStrategy firstCatch = catchExceptionStrategies.getFirst();
        Expression.BallerinaExpression ifCondition = exprFrom(convertMuleExprToBal(ctx, firstCatch.when()));
        List<Statement> ifBody = convertMuleBlocks(ctx, firstCatch.catchBlocks());

        List<ElseIfClause> elseIfClauses = new ArrayList<>();
        for (int i = 1; i < catchExceptionStrategies.size() - 1; i++) {
            CatchExceptionStrategy catchExpStrgy = catchExceptionStrategies.get(i);
            List<Statement> elseIfBody = convertMuleBlocks(ctx, catchExpStrgy.catchBlocks());
            ElseIfClause elseIfClause = new ElseIfClause(exprFrom(convertMuleExprToBal(ctx, catchExpStrgy.when())),
                    elseIfBody);
            elseIfClauses.add(elseIfClause);
        }

        List<Statement> elseBody;
        if (catchExceptionStrategies.size() > 1) {
            CatchExceptionStrategy lastCatch = catchExceptionStrategies.getLast();
            elseBody = convertMuleBlocks(ctx, lastCatch.catchBlocks());
        } else {
            elseBody = Collections.emptyList();
        }

        IfElseStatement ifElseStmt = new IfElseStatement(ifCondition, ifBody, elseIfClauses, elseBody);

        List<Statement> statementList = new ArrayList<>();
        statementList.add(stmtFrom("\n// TODO: if conditions may require some manual adjustments\n"));
        statementList.add(ifElseStmt);

        return statementList;
    }

    private static List<Statement> convertReferenceExceptionStrategy(Context ctx,
                                                                     ReferenceExceptionStrategy refExpStr) {
        String refName = refExpStr.refName();
        String funcRef = ConversionUtils.convertToBalIdentifier(refName);
        BallerinaStatement funcCallStmt = stmtFrom(String.format("%s(%s, %s);", funcRef, Constants.CONTEXT_REFERENCE,
                Constants.ON_FAIL_ERROR_VAR_REF));
        List<Statement> onFailBody = Collections.singletonList(funcCallStmt);
        TypeBindingPattern typeBindingPattern = new TypeBindingPattern(BAL_ERROR_TYPE, Constants.ON_FAIL_ERROR_VAR_REF);
        OnFailClause onFailClause = new OnFailClause(onFailBody, typeBindingPattern);
        DoStatement doStatement = new DoStatement(Collections.emptyList(), onFailClause);
        return List.of(doStatement);
    }

    private static List<Statement> convertExprComponent(Context ctx, ExpressionComponent ec) {
        String convertedExpr = convertMuleExprToBal(ctx, String.format("#[%s]", ec.exprCompContent()));
        ConversionUtils.processExprCompContent(ctx, convertedExpr);
        return List.of(stmtFrom(convertedExpr));
    }

    private static List<Statement> convertEnricher(Context ctx, Enricher enricher) {
        // TODO: support no source
        String source = convertMuleExprToBal(ctx, enricher.source());
        String target = convertMuleExprToBal(ctx, enricher.target());

        if (target.startsWith(Constants.FLOW_VARS_FIELD_ACCESS + ".")) {
            String var = target.replace(Constants.FLOW_VARS_FIELD_ACCESS + ".", "");
            if (!ctx.projectCtx.flowVars.containsKey(var)) {
                ctx.projectCtx.flowVars.put(var, "string");
            }
        } else if (target.startsWith(Constants.SESSION_VARS_FIELD_ACCESS + ".")) {
            String var = target.replace(Constants.SESSION_VARS_FIELD_ACCESS + ".", "");
            if (!ctx.projectCtx.sessionVars.containsKey(var)) {
                ctx.projectCtx.sessionVars.put(var, "string");
            }
        }

        List<Statement> stmts = new ArrayList<>();
        if (enricher.innerBlock().isEmpty()) {
            stmts.add(stmtFrom(String.format("%s = %s;", target, source)));
        } else {
            List<Statement> enricherStmts = new ArrayList<>(convertMuleBlock(ctx, enricher.innerBlock().get()));

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
        return stmts;
    }

    private static List<Statement> convertHttpRequest(Context ctx, HttpRequest httpRequest) {
        List<Statement> stmts = new ArrayList<>();
        String path = getBallerinaClientResourcePath(ctx, httpRequest.path());
        String method = httpRequest.method();
        String url = httpRequest.url();
        Map<String, String> queryParams = httpRequest.queryParams();

        stmts.add(stmtFrom("\n\n// http client request\n"));
        String escapedConfigRef = ConversionUtils.convertToBalIdentifier(httpRequest.configRef());
        stmts.add(stmtFrom(String.format("http:Client %s = check new(\"%s\");", escapedConfigRef, url)));
        String clientResultVar = String.format(Constants.VAR_CLIENT_RESULT_TEMPLATE,
                ctx.projectCtx.counters.clientResultVarCount++);
        stmts.add(stmtFrom("%s %s = check %s->%s.%s(%s);".formatted(Constants.HTTP_RESPONSE_TYPE,
                clientResultVar, escapedConfigRef, path, method.toLowerCase(),
                        genQueryParam(ctx, queryParams))));
        stmts.add(stmtFrom(String.format("%s.payload = check %s.getJsonPayload();",
                Constants.CONTEXT_REFERENCE, clientResultVar)));
        return stmts;
    }

    private static List<Statement> convertDatabase(Context ctx, Database database) {
        ctx.addImport(new Import(Constants.ORG_BALLERINA, Constants.MODULE_SQL, Optional.empty()));
        String streamConstraintType = Constants.GENERIC_RECORD_TYPE_REF;
        ctx.currentFileCtx.balConstructs.typeDefs.put(streamConstraintType,
                new ModuleTypeDef(streamConstraintType, typeFrom(Constants.GENERIC_RECORD_TYPE)));

        List<Statement> stmts = new ArrayList<>();
        stmts.add(stmtFrom("\n\n// database operation\n"));
        String dbQueryVarName = Constants.VAR_DB_QUERY_TEMPLATE.formatted(ctx.projectCtx.counters.dbQueryVarCount++);
        stmts.add(stmtFrom("%s %s = %s;".formatted(Constants.SQL_PARAMETERIZED_QUERY_TYPE,
                dbQueryVarName, database.queryType() == QueryType.TEMPLATE_QUERY_REF ? database.query()
                        : String.format("`%s`", database.query()))));

        String dbStreamVarName = Constants.VAR_DB_STREAM_TEMPLATE.formatted(ctx.projectCtx.counters.dbStreamVarCount++);
        String escapedDbConfigRef = ConversionUtils.convertToBalIdentifier(database.configRef());
        stmts.add(stmtFrom("%s %s= %s->query(%s);"
                .formatted(Constants.DB_QUERY_DEFAULT_TEMPLATE.formatted(streamConstraintType),
                        dbStreamVarName, escapedDbConfigRef, dbQueryVarName)));

        if (database.kind() == Kind.DB_SELECT) {
            String dbSelectVarName = Constants.VAR_DB_SELECT_TEMPLATE
                    .formatted(ctx.projectCtx.counters.dbSelectVarCount++);
            stmts.add(stmtFrom(String.format("%s[] %s = check from %s %s in %s select %s;", streamConstraintType,
                    dbSelectVarName, streamConstraintType, Constants.VAR_ITERATOR, dbStreamVarName,
                    Constants.VAR_ITERATOR)));
            // db:select implicitly sets the payload
            stmts.add(stmtFrom("%s.payload = %s;".formatted(Constants.CONTEXT_REFERENCE, dbSelectVarName)));
        }
        return stmts;
    }

    private static List<Statement> convertAsync(Context ctx, Async async) {
        List<Statement> body = convertTopLevelMuleBlocks(ctx, async.flowBlocks());
        int asyncFuncId = ctx.projectCtx.counters.asyncFuncCount++;
        String funcName = String.format(FUNC_NAME_ASYC_TEMPLATE, asyncFuncId);
        Function function = Function.publicFunction(funcName, Constants.FUNC_PARAMS_WITH_CONTEXT, body);
        ctx.currentFileCtx.balConstructs.functions.add(function);

        List<Statement> stmts = new ArrayList<>();
        stmts.add(stmtFrom("\n\n// async operation\n"));
        stmts.add(stmtFrom(String.format("_ = start %s(%s);", funcName, Constants.CONTEXT_REFERENCE)));
        return stmts;
    }

    private static List<Statement> convertVMOutboundEndpoint(Context ctx, VMOutboundEndpoint vmEndPoint) {
        String path = vmEndPoint.path();
        String funcName = ctx.projectCtx.vmPathToBalFuncMap.get(path);
        if (funcName == null) {
            funcName = String.format(Constants.FUNC_NAME_VM_RECEIVE_TEMPLATE,
                    ctx.projectCtx.counters.vmReceiveFuncCount++);
            ctx.projectCtx.vmPathToBalFuncMap.put(path, funcName);
        }

        List<Statement> namedWorkerBody = new ArrayList<>(4);
        namedWorkerBody.add(stmtFrom("\n// VM Inbound Endpoint\n"));
        namedWorkerBody.add(stmtFrom("anydata receivedPayload = <- function;"));
        namedWorkerBody.add(stmtFrom("ctx.payload = receivedPayload;"));
        namedWorkerBody.add(stmtFrom(String.format("%s(ctx);", funcName)));

        List<Statement> stmts = new ArrayList<>(3);
        stmts.add(new NamedWorkerDecl("W", Optional.of(typeFrom("error?")), namedWorkerBody));
        stmts.add(stmtFrom("\n\n// VM Outbound Endpoint\n"));
        stmts.add(stmtFrom(String.format("%s.payload -> W;", Constants.CONTEXT_REFERENCE)));
        return stmts;
    }

    private static List<Statement> convertTransformMessage(Context ctx, TransformMessage transformMsg) {
        List<Statement> stmts = new ArrayList<>();
        DWReader.processDWElements(transformMsg.children(), ctx, stmts);
        stmts.add(stmtFrom("%s.payload = %s;".formatted(Constants.CONTEXT_REFERENCE,
                DWUtils.DATAWEAVE_OUTPUT_VARIABLE_NAME)));
        return stmts;
    }

    private static List<Statement> convertUnsupportedBlock(Context ctx, UnsupportedBlock unsupportedBlock) {
        ctx.migrationMetrics.failedBlocks.add(unsupportedBlock.xmlBlock());
        String comment = ConversionUtils.wrapElementInUnsupportedBlockComment(unsupportedBlock.xmlBlock());
        // TODO: comment is not a statement. Find a better way to handle this
        // This works for now because we concatenate and create a body block `{ stmts }`
        // before parsing.
        return List.of(stmtFrom(comment));
    }

    private static List<Statement> convertScatterGather(Context ctx, ScatterGather scatterGather) {
        List<ProcessorChain> routes = scatterGather.processorChains();
        if (routes.isEmpty()) {
            return List.of();
        }

        List<Statement> stmts = new ArrayList<>();
        stmts.add(stmtFrom("\n\n// scatter-gather parallel execution\n"));

        // Create named workers for each route
        List<NamedWorkerDecl> workers = new ArrayList<>();
        String[] workerNames = new String[scatterGather.processorChains().size()];
        for (int i = 0; i < scatterGather.processorChains().size(); i++) {
            ProcessorChain route = scatterGather.processorChains().get(i);
            String workerName = Constants.WORKER_SCATTER_GATHER
                    .formatted(ctx.projectCtx.counters.scatterGatherWorkerCount++);
            workerNames[i] = workerName;

            List<Statement> workerBody = new ArrayList<>();
            workerBody.add(stmtFrom(String.format("\n// Route %d\n", i)));

            // Convert route blocks to worker statements
            List<Statement> routeStmts = convertMuleBlocks(ctx, route.flowBlocks());
            workerBody.addAll(routeStmts);

            // Send result back to main worker
            workerBody.add(stmtFrom(String.format("return %s.payload;", Constants.CONTEXT_REFERENCE)));

            NamedWorkerDecl workerDecl = new NamedWorkerDecl(workerName, Optional.of(typeFrom("anydata|error")),
                    workerBody);
            workers.add(workerDecl);
        }

        Statement.ForkStatement fork = new Statement.ForkStatement(workers);
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

        // Add the error wrapper function if not already added
        if (!ctx.currentFileCtx.balConstructs.utilFunctions.contains(Constants.FUNC_WRAP_ROUTE_ERR)) {
            addRouteErrorWrapperFunction(ctx);
            ctx.currentFileCtx.balConstructs.utilFunctions.add(Constants.FUNC_WRAP_ROUTE_ERR);
        }

        // Set the collected results as payload
        stmts.add(stmtFrom(String.format("%s.payload = %s;\n\n", Constants.CONTEXT_REFERENCE,
                scatterGatherResultsVar)));
        return stmts;
    }

    private static void addRouteErrorWrapperFunction(Context ctx) {
        List<common.BallerinaModel.Parameter> params = new ArrayList<>();
        params.add(new common.BallerinaModel.Parameter("key", typeFrom("string")));
        params.add(new common.BallerinaModel.Parameter("value", typeFrom("anydata|error")));

        List<Statement> body = new ArrayList<>();
        body.add(stmtFrom(
                "if value is error { return error(string `Error in Route ${key}: ${value.message()}`, value); }"
        ));
        body.add(stmtFrom("return value;"));

        Function errorWrapFunc = Function.publicFunction(Constants.FUNC_WRAP_ROUTE_ERR, params,
                typeFrom("anydata|error"), body);
        ctx.currentFileCtx.balConstructs.functions.add(errorWrapFunc);
    }

    private static List<Statement> convertFirstSuccessful(Context ctx, FirstSuccessful firstSuccessful) {
        List<ProcessorChain> processorChains = firstSuccessful.processorChains();
        if (processorChains.isEmpty()) {
            return List.of();
        }

        // Create a function for each route
        String[] funcNames = new String[firstSuccessful.processorChains().size()];
        for (int i = 0; i < firstSuccessful.processorChains().size(); i++) {
            ProcessorChain processorChain = firstSuccessful.processorChains().get(i);
            String funcName = Constants.FUNC_FIRST_SUCCESSFUL_ROUTE
                    .formatted(ctx.projectCtx.counters.firstSuccessfulFuncCount++);
            funcNames[i] = funcName;

            List<Statement> funcBody = new ArrayList<>();
            funcBody.add(stmtFrom("\n// Route %d\n".formatted(i)));

            // Convert route blocks to worker statements
            List<Statement> routeStmts = convertMuleBlocks(ctx, processorChain.flowBlocks());
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
                    mule.v3.Constants.CONTEXT_REFERENCE)));
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
        stmts.add(stmtFrom("%s.payload = %s;\n\n".formatted(Constants.CONTEXT_REFERENCE,
                firstSuccessfulResultVar)));
        return stmts;
    }

    private static List<Statement> convertForeach(Context ctx, Foreach foreach) {
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
        foreachBody.addAll(convertMuleBlocks(ctx, foreach.flowBlocks()));

        // Create foreach statement using Ballerina syntax
        stmts.add(stmtFrom(String.format("foreach anydata %s in %s {", iteratorVar, collection)));
        stmts.addAll(foreachBody);
        stmts.add(stmtFrom("}"));

        // Restore original payload after foreach
        stmts.add(stmtFrom(String.format("%s.payload = %s;", Constants.CONTEXT_REFERENCE, originalPayloadVar)));

        return stmts;
    }
}
