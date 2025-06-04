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
package mule;

import mule.dataweave.converter.DWReader;
import mule.dataweave.converter.DWUtils;

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
import static mule.Constants.BAL_ERROR_TYPE;
import static mule.Constants.FUNC_NAME_ASYC_TEMPLATE;
import static mule.ConversionUtils.convertMuleExprToBal;
import static mule.ConversionUtils.convertMuleExprToBalStringLiteral;
import static mule.ConversionUtils.genQueryParam;
import static mule.ConversionUtils.getBallerinaClientResourcePath;
import static mule.ConversionUtils.inferTypeFromBalExpr;
import static mule.MuleModel.Async;
import static mule.MuleModel.CatchExceptionStrategy;
import static mule.MuleModel.Choice;
import static mule.MuleModel.ChoiceExceptionStrategy;
import static mule.MuleModel.Database;
import static mule.MuleModel.Enricher;
import static mule.MuleModel.ExpressionComponent;
import static mule.MuleModel.FlowReference;
import static mule.MuleModel.HttpRequest;
import static mule.MuleModel.Kind;
import static mule.MuleModel.LogLevel;
import static mule.MuleModel.Logger;
import static mule.MuleModel.MuleRecord;
import static mule.MuleModel.ObjectToJson;
import static mule.MuleModel.ObjectToString;
import static mule.MuleModel.Payload;
import static mule.MuleModel.QueryType;
import static mule.MuleModel.ReferenceExceptionStrategy;
import static mule.MuleModel.RemoveVariable;
import static mule.MuleModel.SetSessionVariable;
import static mule.MuleModel.SetVariable;
import static mule.MuleModel.TransformMessage;
import static mule.MuleModel.UnsupportedBlock;
import static mule.MuleModel.VMOutboundEndpoint;
import static mule.MuleModel.WhenInChoice;
import static mule.MuleToBalConverter.Data;
import static mule.MuleToBalConverter.SharedProjectData;

public class MuleConfigConverter {

    public static List<Statement> convertTopLevelMuleBlocks(Data data,
                                                            List<MuleRecord> flowBlocks) {
        // Add function body statements
        List<Statement> body = new ArrayList<>();
        List<Statement> workers = new ArrayList<>();

        // Read flow blocks
        for (MuleRecord record : flowBlocks) {
            List<Statement> stmts = convertMuleBlock(data, record);
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

    public static List<Statement> convertMuleBlocks(Data data,
                                                    List<MuleRecord> muleRecords) {
        List<Statement> statements = new ArrayList<>();
        for (MuleRecord mr : muleRecords) {
            List<Statement> stmts = convertMuleBlock(data, mr);
            statements.addAll(stmts);
        }
        return statements;
    }

    public static List<Statement> convertMuleBlock(Data data,
                                                   MuleRecord muleRec) {
        switch (muleRec) {
            case Logger lg -> {
                return convertLogger(data, lg);
            }
            case SetVariable setVariable -> {
                return convertSetVariable(data, setVariable);
            }
            case SetSessionVariable setSessionVariable -> {
                return convertSetSessionVariable(data, setSessionVariable);
            }
            case RemoveVariable removeVariable -> {
                return convertRemoveVariable(data, removeVariable);
            }
            case Payload payload -> {
                return convertSetPayload(data, payload);
            }
            case Choice choice -> {
                return convertChoice(data, choice);
            }
            case FlowReference flowReference -> {
                return convertFlowReference(data, flowReference);
            }
            case ObjectToJson objectToJson -> {
                return convertObjectToJson(data, objectToJson);
            }
            case ObjectToString objectToString -> {
                return convertObjectToString(data, objectToString);
            }
            case CatchExceptionStrategy catchExpStr -> {
                return convertCatchExceptionStrategy(data, catchExpStr);
            }
            case ChoiceExceptionStrategy choiceExpStr -> {
                return convertChoiceExceptionStrategy(data, choiceExpStr);
            }
            case ReferenceExceptionStrategy refExpStr -> {
                return convertReferenceExceptionStrategy(data, refExpStr);
            }
            case ExpressionComponent ec -> {
                return convertExprComponent(data, ec);
            }
            case Enricher enricher -> {
                return convertEnricher(data, enricher);
            }
            case HttpRequest httpRequest -> {
                return convertHttpRequest(data, httpRequest);
            }
            case Database database -> {
                return convertDatabase(data, database);
            }
            case Async async -> {
                return convertAsync(data, async);
            }
            case VMOutboundEndpoint vmEndPoint -> {
                return convertVMOutboundEndpoint(data, vmEndPoint);
            }
            case TransformMessage transformMessage -> {
                return convertTransformMessage(data, transformMessage);
            }
            case UnsupportedBlock unsupportedBlock -> {
                return convertUnsupportedBlock(data, unsupportedBlock);
            }
            case null -> throw new IllegalStateException();
            default -> throw new UnsupportedOperationException();
        }
    }

    private static List<Statement> convertLogger(Data data, Logger lg) {
        String logFuncName = getBallerinaLogFunction(lg.level());
        String stringLiteral = convertMuleExprToBalStringLiteral(data, lg.message());
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

    private static List<Statement> convertSetVariable(Data data, SetVariable setVariable) {
        String varName = ConversionUtils.convertToBalIdentifier(setVariable.variableName());
        String balExpr = convertMuleExprToBal(data, setVariable.value());
        String type = inferTypeFromBalExpr(balExpr);

        if (!data.sharedProjectData.existingFlowVar(varName)) {
            data.sharedProjectData.flowVars.add(new SharedProjectData.TypeAndNamePair(type, varName));
        }

        var stmt = stmtFrom(String.format("%s.%s = %s;", Constants.FLOW_VARS_FIELD_ACCESS, varName, balExpr));
        return List.of(stmt);
    }

    private static List<Statement> convertSetSessionVariable(Data data, SetSessionVariable setSessionVariable) {
        String varName = ConversionUtils.convertToBalIdentifier(setSessionVariable.variableName());
        String balExpr = convertMuleExprToBal(data, setSessionVariable.value());
        String type = inferTypeFromBalExpr(balExpr);

        if (!data.sharedProjectData.existingSessionVar(varName)) {
            data.sharedProjectData.sessionVars.add(new SharedProjectData.TypeAndNamePair(type, varName));
        }

        var stmt = stmtFrom(String.format("%s.%s = %s;", Constants.SESSION_VARS_FIELD_ACCESS, varName, balExpr));
        return List.of(stmt);
    }

    private static List<Statement> convertRemoveVariable(Data data, RemoveVariable removeVariable) {
        String varName = ConversionUtils.convertToBalIdentifier(removeVariable.variableName());
        Statement stmt;
        if (removeVariable.kind() == Kind.REMOVE_VARIABLE && data.sharedProjectData.existingFlowVar(varName)) {
            stmt = stmtFrom(String.format("%s.%s = %s;", Constants.FLOW_VARS_FIELD_ACCESS, varName, "()"));
        } else if (removeVariable.kind() == Kind.REMOVE_SESSION_VARIABLE &&
                data.sharedProjectData.existingSessionVar(varName)) {
            stmt = stmtFrom(String.format("%s.%s = %s;", Constants.SESSION_VARS_FIELD_ACCESS, varName, "()"));
        } else {
            throw new IllegalStateException();
        }
        return List.of(stmt);
    }

    private static List<Statement> convertSetPayload(Data data, Payload payload) {
        List<Statement> stmts = new ArrayList<>();
        String pyld = convertMuleExprToBal(data, payload.expr());
        String type = inferTypeFromBalExpr(pyld);
        String payloadVar = String.format(Constants.VAR_PAYLOAD_TEMPLATE, data.sharedProjectData.payloadVarCount++);
        stmts.add(stmtFrom("\n\n// set payload\n"));
        stmts.add(stmtFrom(String.format("%s %s = %s;", type, payloadVar, pyld)));
        stmts.add(stmtFrom(String.format("%s.payload = %s;", Constants.CONTEXT_REFERENCE,
                payloadVar)));
        return stmts;
    }

    private static List<Statement> convertChoice(Data data, Choice choice) {
        List<WhenInChoice> whens = choice.whens();
        assert !whens.isEmpty(); // For valid mule config, there is at least one when

        WhenInChoice firstWhen = whens.getFirst();
        String ifCondition = convertMuleExprToBal(data, firstWhen.condition());
        List<Statement> ifBody = new ArrayList<>();
        for (MuleRecord mr : firstWhen.process()) {
            List<Statement> statements = convertMuleBlock(data, mr);
            ifBody.addAll(statements);
        }

        List<ElseIfClause> elseIfClauses = new ArrayList<>(whens.size() - 1);
        for (int i = 1; i < whens.size(); i++) {
            WhenInChoice when = whens.get(i);
            List<Statement> elseIfBody = new ArrayList<>();
            for (MuleRecord mr : when.process()) {
                List<Statement> statements = convertMuleBlock(data, mr);
                elseIfBody.addAll(statements);
            }
            ElseIfClause elseIfClause = new ElseIfClause(exprFrom(convertMuleExprToBal(data, when.condition())),
                    elseIfBody);
            elseIfClauses.add(elseIfClause);
        }

        List<Statement> elseBody = new ArrayList<>(choice.otherwiseProcess().size());
        for (MuleRecord mr : choice.otherwiseProcess()) {
            List<Statement> statements = convertMuleBlock(data, mr);
            elseBody.addAll(statements);
        }

        var ifElseStmt = new IfElseStatement(exprFrom(ifCondition), ifBody, elseIfClauses, elseBody);
        return List.of(ifElseStmt);
    }

    private static List<Statement> convertFlowReference(Data data, FlowReference flowReference) {
        String flowName = flowReference.flowName();
        String funcRef = ConversionUtils.convertToBalIdentifier(flowName);
        var stmt = stmtFrom(String.format("%s(%s);", funcRef, Constants.CONTEXT_REFERENCE));
        return List.of(stmt);
    }

    private static List<Statement> convertObjectToJson(Data data, ObjectToJson objectToJson) {
        List<Statement> stmts = new ArrayList<>();
        stmts.add(stmtFrom("\n\n// json transformation\n"));
        // object to json transformer implicitly sets the payload
        stmts.add(stmtFrom("%s = %s.toJson();".formatted(Constants.PAYLOAD_FIELD_ACCESS,
                Constants.PAYLOAD_FIELD_ACCESS)));
        return stmts;
    }

    private static List<Statement> convertObjectToString(Data data, ObjectToString objectToString) {
        List<Statement> stmts = new ArrayList<>();
        stmts.add(stmtFrom("\n\n// string transformation\n"));
        // object to string transformer implicitly sets the payload
        stmts.add(stmtFrom("%s = %s.toString();".formatted(Constants.PAYLOAD_FIELD_ACCESS,
                Constants.PAYLOAD_FIELD_ACCESS)));
        return stmts;
    }

    private static List<Statement> convertCatchExceptionStrategy(Data data, CatchExceptionStrategy catchExpStr) {
        List<Statement> onFailBody = getCatchExceptionBody(data, catchExpStr);
        OnFailClause onFailClause = new OnFailClause(onFailBody);
        DoStatement doStatement = new DoStatement(Collections.emptyList(), onFailClause);
        return List.of(doStatement);
    }

    static List<Statement> getCatchExceptionBody(Data data, CatchExceptionStrategy catchExceptionStrategy) {
        return convertMuleBlocks(data, catchExceptionStrategy.catchBlocks());
    }

    private static List<Statement> convertChoiceExceptionStrategy(Data data, ChoiceExceptionStrategy choiceExpStr) {
        List<Statement> onFailBody = getChoiceExceptionBody(data, choiceExpStr);
        TypeBindingPattern typeBindingPattern = new TypeBindingPattern(BAL_ERROR_TYPE,
                Constants.ON_FAIL_ERROR_VAR_REF);
        OnFailClause onFailClause = new OnFailClause(onFailBody, typeBindingPattern);
        DoStatement doStatement = new DoStatement(Collections.emptyList(), onFailClause);
        return List.of(doStatement);
    }

    static List<Statement> getChoiceExceptionBody(Data data, ChoiceExceptionStrategy choiceExceptionStrategy) {
        List<CatchExceptionStrategy> catchExceptionStrategies = choiceExceptionStrategy.catchExceptionStrategyList();
        assert !catchExceptionStrategies.isEmpty();

        CatchExceptionStrategy firstCatch = catchExceptionStrategies.getFirst();
        Expression.BallerinaExpression ifCondition = exprFrom(convertMuleExprToBal(data, firstCatch.when()));
        List<Statement> ifBody = convertMuleBlocks(data, firstCatch.catchBlocks());

        List<ElseIfClause> elseIfClauses = new ArrayList<>();
        for (int i = 1; i < catchExceptionStrategies.size() - 1; i++) {
            CatchExceptionStrategy catchExpStrgy = catchExceptionStrategies.get(i);
            List<Statement> elseIfBody = convertMuleBlocks(data, catchExpStrgy.catchBlocks());
            ElseIfClause elseIfClause = new ElseIfClause(exprFrom(convertMuleExprToBal(data, catchExpStrgy.when())),
                    elseIfBody);
            elseIfClauses.add(elseIfClause);
        }

        List<Statement> elseBody;
        if (catchExceptionStrategies.size() > 1) {
            CatchExceptionStrategy lastCatch = catchExceptionStrategies.getLast();
            elseBody = convertMuleBlocks(data, lastCatch.catchBlocks());
        } else {
            elseBody = Collections.emptyList();
        }

        IfElseStatement ifElseStmt = new IfElseStatement(ifCondition, ifBody, elseIfClauses, elseBody);

        List<Statement> statementList = new ArrayList<>();
        statementList.add(stmtFrom("\n// TODO: if conditions may require some manual adjustments\n"));
        statementList.add(ifElseStmt);

        return statementList;
    }

    private static List<Statement> convertReferenceExceptionStrategy(Data data, ReferenceExceptionStrategy refExpStr) {
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

    private static List<Statement> convertExprComponent(Data data, ExpressionComponent ec) {
        String convertedExpr = convertMuleExprToBal(data, String.format("#[%s]", ec.exprCompContent()));
        ConversionUtils.processExprCompContent(data.sharedProjectData, convertedExpr);
        return List.of(stmtFrom(convertedExpr));
    }

    private static List<Statement> convertEnricher(Data data, Enricher enricher) {
        // TODO: support no source
        String source = convertMuleExprToBal(data, enricher.source());
        String target = convertMuleExprToBal(data, enricher.target());

        if (target.startsWith(Constants.FLOW_VARS_FIELD_ACCESS + ".")) {
            String var = target.replace(Constants.FLOW_VARS_FIELD_ACCESS + ".", "");
            if (!data.sharedProjectData.existingFlowVar(var)) {
                data.sharedProjectData.flowVars.add(new SharedProjectData.TypeAndNamePair("string", var));
            }
        } else if (target.startsWith(Constants.SESSION_VARS_FIELD_ACCESS + ".")) {
            String var = target.replace(Constants.SESSION_VARS_FIELD_ACCESS + ".", "");
            if (!data.sharedProjectData.existingSessionVar(var)) {
                data.sharedProjectData.sessionVars.add(new SharedProjectData.TypeAndNamePair("string", var));
            }
        }

        List<Statement> stmts = new ArrayList<>();
        if (enricher.innerBlock().isEmpty()) {
            stmts.add(stmtFrom(String.format("%s = %s;", target, source)));
        } else {
            List<Statement> enricherStmts = new ArrayList<>(convertMuleBlock(data, enricher.innerBlock().get()));

            String methodName = String.format(Constants.FUNC_NAME_ENRICHER_TEMPLATE,
                    data.sharedProjectData.enricherFuncCount);
            Function func = new Function(Optional.of("public"), methodName, Constants.FUNC_PARAMS_WITH_CONTEXT,
                    Optional.of(typeFrom("string?")), new BlockFunctionBody(enricherStmts));
            data.functions.add(func);

            enricherStmts.add(stmtFrom(String.format("return %s;", source)));
            stmts.add(stmtFrom(String.format("%s = %s(%s.clone());", target,
                    String.format(Constants.FUNC_NAME_ENRICHER_TEMPLATE,
                            data.sharedProjectData.enricherFuncCount++),
                    Constants.CONTEXT_REFERENCE)));
        }
        return stmts;
    }

    private static List<Statement> convertHttpRequest(Data data, HttpRequest httpRequest) {
        List<Statement> stmts = new ArrayList<>();
        String path = getBallerinaClientResourcePath(data, httpRequest.path());
        String method = httpRequest.method();
        String url = httpRequest.url();
        Map<String, String> queryParams = httpRequest.queryParams();

        stmts.add(stmtFrom("\n\n// http client request\n"));
        stmts.add(stmtFrom(String.format("http:Client %s = check new(\"%s\");", httpRequest.configRef(), url)));
        String clientResultVar = String.format(Constants.VAR_CLIENT_RESULT_TEMPLATE,
                data.sharedProjectData.clientResultVarCount++);
        stmts.add(stmtFrom("%s %s = check %s->%s.%s(%s);".formatted(Constants.HTTP_RESPONSE_TYPE,
                clientResultVar, httpRequest.configRef(), path, method.toLowerCase(),
                genQueryParam(data, queryParams))));
        stmts.add(stmtFrom(String.format("%s.payload = check %s.getJsonPayload();",
                Constants.CONTEXT_REFERENCE, clientResultVar)));
        return stmts;
    }

    private static List<Statement> convertDatabase(Data data, Database database) {
        data.imports.add(new Import(Constants.ORG_BALLERINA, Constants.MODULE_SQL, Optional.empty()));
        String streamConstraintType = Constants.GENERIC_RECORD_TYPE_REF;
        data.typeDefMap.put(streamConstraintType,
                new ModuleTypeDef(streamConstraintType, typeFrom(Constants.GENERIC_RECORD_TYPE)));

        List<Statement> stmts = new ArrayList<>();
        stmts.add(stmtFrom("\n\n// database operation\n"));
        String dbQueryVarName = Constants.VAR_DB_QUERY_TEMPLATE
                .formatted(data.sharedProjectData.dbQueryVarCount++);
        stmts.add(stmtFrom("%s %s = %s;".formatted(Constants.SQL_PARAMETERIZED_QUERY_TYPE,
                dbQueryVarName, database.queryType() == QueryType.TEMPLATE_QUERY_REF ? database.query()
                        : String.format("`%s`", database.query()))));

        String dbStreamVarName = Constants.VAR_DB_STREAM_TEMPLATE.formatted(data.sharedProjectData.dbStreamVarCount++);
        stmts.add(stmtFrom("%s %s= %s->query(%s);"
                .formatted(Constants.DB_QUERY_DEFAULT_TEMPLATE.formatted(streamConstraintType),
                        dbStreamVarName, database.configRef(), dbQueryVarName)));

        if (database.kind() == Kind.DB_SELECT) {
            String dbSelectVarName = Constants.VAR_DB_SELECT_TEMPLATE
                    .formatted(data.sharedProjectData.dbSelectVarCount++);
            stmts.add(stmtFrom(String.format("%s[] %s = check from %s %s in %s select %s;", streamConstraintType,
                    dbSelectVarName, streamConstraintType, Constants.VAR_ITERATOR, dbStreamVarName,
                    Constants.VAR_ITERATOR)));
            // db:select implicitly sets the payload
            stmts.add(stmtFrom("%s.payload = %s;".formatted(Constants.CONTEXT_REFERENCE, dbSelectVarName)));
        }
        return stmts;
    }

    private static List<Statement> convertAsync(Data data, Async async) {
        List<Statement> body = convertTopLevelMuleBlocks(data, async.flowBlocks());
        int asyncFuncId = data.sharedProjectData.asyncFuncCount++;
        String funcName = String.format(FUNC_NAME_ASYC_TEMPLATE, asyncFuncId);
        Function function = Function.publicFunction(funcName, Constants.FUNC_PARAMS_WITH_CONTEXT, body);
        data.functions.add(function);

        List<Statement> stmts = new ArrayList<>();
        stmts.add(stmtFrom("\n\n// async operation\n"));
        stmts.add(stmtFrom(String.format("_ = start %s(%s);", funcName, Constants.CONTEXT_REFERENCE)));
        return stmts;
    }

    private static List<Statement> convertVMOutboundEndpoint(Data data, VMOutboundEndpoint vmEndPoint) {
        String path = vmEndPoint.path();
        String funcName = data.sharedProjectData.vmPathToBalFuncMap.get(path);
        if (funcName == null) {
            funcName = String.format(Constants.FUNC_NAME_VM_RECEIVE_TEMPLATE,
                    data.sharedProjectData.vmReceiveFuncCount++);
            data.sharedProjectData.vmPathToBalFuncMap.put(path, funcName);
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

    private static List<Statement> convertTransformMessage(Data data, TransformMessage transformMsg) {
        List<Statement> stmts = new ArrayList<>();
        DWReader.processDWElements(transformMsg.children(), data, stmts);
        stmts.add(stmtFrom("%s.payload = %s;".formatted(Constants.CONTEXT_REFERENCE,
                DWUtils.DATAWEAVE_OUTPUT_VARIABLE_NAME)));
        return stmts;
    }

    private static List<Statement> convertUnsupportedBlock(Data data, UnsupportedBlock unsupportedBlock) {
        String comment = ConversionUtils.wrapElementInUnsupportedBlockComment(unsupportedBlock.xmlBlock());
        // TODO: comment is not a statement. Find a better way to handle this
        // This works for now because we concatenate and create a body block `{ stmts }`
        // before parsing.
        return List.of(stmtFrom(comment));
    }
}
