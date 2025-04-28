/*
 *  Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com). All Rights Reserved.
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

package converter.tibco;

import ballerina.BallerinaModel;
import ballerina.BallerinaModel.Expression;
import ballerina.BallerinaModel.Expression.BinaryLogical;
import ballerina.BallerinaModel.Expression.Check;
import ballerina.BallerinaModel.Expression.CheckPanic;
import ballerina.BallerinaModel.Expression.FunctionCall;
import ballerina.BallerinaModel.Expression.Panic;
import ballerina.BallerinaModel.Expression.StringConstant;
import ballerina.BallerinaModel.Expression.TernaryExpression;
import ballerina.BallerinaModel.Expression.TypeCheckExpression;
import ballerina.BallerinaModel.Expression.VariableReference;
import ballerina.BallerinaModel.Expression.XMLTemplate;
import ballerina.BallerinaModel.Parameter;
import ballerina.BallerinaModel.Statement;
import ballerina.BallerinaModel.Statement.CallStatement;
import ballerina.BallerinaModel.Statement.Return;
import ballerina.BallerinaModel.Statement.VarAssignStatement;
import ballerina.BallerinaModel.Statement.VarDeclStatment;
import ballerina.BallerinaModel.TypeDesc;
import converter.tibco.analyzer.AnalysisResult;
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import org.jetbrains.annotations.NotNull;
import tibco.TibcoModel;
import tibco.TibcoModel.Resource;
import tibco.TibcoModel.Scope.Flow.Activity;
import tibco.TibcoModel.Scope.Flow.Activity.Expression.XPath;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ballerina.BallerinaModel.TypeDesc.BuiltinType.ANYDATA;
import static ballerina.BallerinaModel.TypeDesc.BuiltinType.BOOLEAN;
import static ballerina.BallerinaModel.TypeDesc.BuiltinType.ERROR;
import static ballerina.BallerinaModel.TypeDesc.BuiltinType.JSON;
import static ballerina.BallerinaModel.TypeDesc.BuiltinType.XML;
import static converter.ConversionUtils.exprFrom;
import static converter.ConversionUtils.stmtFrom;

public class ProcessConverter {

    private ProcessConverter() {
    }

    static ConversionResult convertProject(TibcoToBalConverter.ProjectConversionContext conversionContext,
                                           Collection<TibcoModel.Process> processes,
                                           Collection<TibcoModel.Type.Schema> types,
                                           Collection<Resource.JDBCResource> jdbcResources,
                                           Collection<Resource.HTTPConnectionResource> httpConnectionResources,
                                           Set<Resource.HTTPClientResource> httpClientResources) {
        ProjectContext cx = new ProjectContext(conversionContext);
        convertResources(cx, jdbcResources, httpConnectionResources, httpClientResources);

        record ProcessResult(TibcoModel.Process process, TypeConversionResult result) {

        }
        List<ProcessResult> results =
                processes.stream()
                        .map(process -> new ProcessResult(process,
                                convertTypes(cx.getProcessContext(process), process)))
                        .toList();
        List<TibcoModel.Type.Schema> schemas = new ArrayList<>(types);
        for (TibcoModel.Process each : processes) {
            accumSchemas(each, schemas);
        }
        SyntaxTree typeSyntaxTree = convertTypes(cx, schemas);
        // We need to ensure all the type definitions have been processed before we
        // start processing the functions
        List<BallerinaModel.TextDocument> textDocuments = results.stream()
                .map(result -> {
                    TibcoModel.Process process = result.process();
                    return convertBody(cx.getProcessContext(process), process, result.result());
                }).toList();
        return new ConversionResult(cx.serialize(textDocuments), typeSyntaxTree);
    }

    private static void accumSchemas(TibcoModel.Process process, Collection<TibcoModel.Type.Schema> accum) {
        for (TibcoModel.Type each : process.types()) {
            if (each instanceof TibcoModel.Type.Schema schema) {
                accum.add(schema);
            }
        }
    }

    private static void convertResources(ProjectContext cx, Collection<Resource.JDBCResource> jdbcResources,
                                         Collection<Resource.HTTPConnectionResource> httpConnectionResources,
                                         Set<Resource.HTTPClientResource> httpClientResources) {
        for (Resource.JDBCResource resource : jdbcResources) {
            ResourceConvertor.convertJDBCResource(cx, resource);
        }
        for (Resource.HTTPConnectionResource resource : httpConnectionResources) {
            ResourceConvertor.convertHttpConnectionResource(cx, resource);
        }
        for (Resource.HTTPClientResource resource : httpClientResources) {
            ResourceConvertor.convertHttpClientResource(cx, resource);
        }
    }

    static SyntaxTree convertTypes(ProjectContext cx, Collection<TibcoModel.Type.Schema> schemas) {
        ContextWithFile typeContext = cx.getTypeContext();
        return TypeConverter.convertSchemas(typeContext, schemas);
    }

    static BallerinaModel.Module convertProcess(TibcoModel.Process process) {
        ProjectContext cx = new ProjectContext();
        return convertProcess(cx.getProcessContext(process), process);
    }

    private static BallerinaModel.Module convertProcess(ProcessContext cx, TibcoModel.Process process) {
        TypeConversionResult result = convertTypes(cx, process);
        BallerinaModel.TextDocument textDocument = convertBody(cx, process, result);
        ProjectContext projectContext = cx.projectContext;
        return projectContext.serialize(List.of(textDocument));
    }

    private static BallerinaModel.TextDocument convertBody(ProcessContext cx, TibcoModel.Process process,
                                                           TypeConversionResult result) {
        process.variables().stream()
                .filter(each -> each instanceof TibcoModel.Variable.PropertyVariable)
                .forEach(var -> cx.addResourceVariable((TibcoModel.Variable.PropertyVariable) var));
        List<BallerinaModel.Function> functions = cx.analysisResult.activities().stream()
                .map(activity -> ActivityConverter.convertActivity(cx, activity))
                .collect(Collectors.toCollection(ArrayList::new));
        addTransitionPredicates(cx, functions);
        functions.add(generateStartFunction(cx));
        functions.add(generateActivityFlowFunction(cx));
        functions.add(generateErrorFlowFunction(cx));
        functions.add(generateProcessFunction(cx));

        functions.sort(Comparator.comparing(BallerinaModel.Function::functionName));

        return cx.serialize(result.service(), functions);
    }

    private static void addTransitionPredicates(ProcessContext cx, List<BallerinaModel.Function> accum) {
        cx.analysisResult.activities().stream()
                .filter(each -> each instanceof Activity.ActivityWithSources)
                .forEach(activity -> addTransitionPredicates(cx,
                        (Activity.ActivityWithSources) activity, accum));
    }

    private static void addTransitionPredicates(ProcessContext cx, Activity.ActivityWithSources activity,
                                                List<BallerinaModel.Function> accum) {
        Expression prev = null;
        VariableReference value = new VariableReference("input");
        for (Activity.Source source : activity.sources()) {
            var predicate = source.condition();
            if (predicate.isEmpty()) {
                continue;
            }
            switch (predicate.get()) {
                case XPath xPath -> {
                    Expression expr = expr(cx, value, xPath);
                    prev = expr;
                    accum.add(getTransitionPredicateFn(cx, xPath, expr));
                }
                case Activity.Source.Predicate.Else anElse -> {
                    assert prev != null : "Should not be the first predicate";
                    accum.add(getTransitionPredicateFn(cx, anElse,
                            new Expression.Not(prev)));
                }
            }
        }
    }

    private static Expression expr(ProcessContext cx, VariableReference value, XPath predicate) {
        String predicateTestFn = cx.getPredicateTestFunction();
        StringConstant xPathExpr = new StringConstant(ConversionUtils.escapeString(predicate.expression()));
        return new FunctionCall(predicateTestFn, List.of(value, xPathExpr));
    }

    private static BallerinaModel.Function getTransitionPredicateFn(ProcessContext cx,
                                                                    Activity.Source.Predicate predicate,
                                                                    Expression expr) {
        return new BallerinaModel.Function(cx.predicateFunction(predicate),
                List.of(new Parameter("input", XML)), BOOLEAN, List.of(new Return<>(expr)));
    }

    private static TypeConversionResult convertTypes(ProcessContext cx, TibcoModel.Process process) {
        List<BallerinaModel.Service> services = process.types().stream()
                .filter(type -> type instanceof TibcoModel.Type.WSDLDefinition)
                .map(type -> (TibcoModel.Type.WSDLDefinition) type)
                .flatMap(wsdlDefinition -> TypeConverter.convertWsdlDefinition(cx, wsdlDefinition)
                        .stream())
                .collect(Collectors.toList());
        cx.allocatedDefaultClient(services);
        return new TypeConversionResult(services);
    }

    private record TypeConversionResult(Collection<BallerinaModel.Service> service) {

    }

    private static BallerinaModel.Function generateStartFunction(ProcessContext cx) {
        List<Statement> body = new ArrayList<>();
        var startFuncData = cx.getProcessStartFunction();
        VariableReference inputVar = ProcessContext.processLevelFnInputVariable();
        VarDeclStatment inputXMLVar = new VarDeclStatment(XML, "inputXML",
                new TernaryExpression(
                        new TypeCheckExpression(inputVar, new TypeDesc.MapTypeDesc(ANYDATA)),
                        new CheckPanic(new FunctionCall(cx.getToXmlFunction(), List.of(inputVar))),
                        new XMLTemplate("")));
        body.add(inputXMLVar);

        VariableReference params = ProcessContext.processLevelFnParamVariable();
        VarDeclStatment xmlResult = new VarDeclStatment(XML, "xmlResult",
                new FunctionCall(cx.analysisResult.getControlFlowFunctions(cx.process.scope()).scopeFn(),
                        List.of(inputXMLVar.ref(), params)));
        body.add(xmlResult);

        TypeDesc returnType = startFuncData.returnType();
        String convertToTypeFunction = cx.getConvertToTypeFunction(returnType);
        VarDeclStatment result = new VarDeclStatment(returnType, "result",
                new FunctionCall(convertToTypeFunction, List.of(xmlResult.ref())));
        body.add(result);

        Return<VariableReference> returnStatement = new Return<>(Optional.of(new VariableReference("result")));
        body.add(returnStatement);

        TypeDesc inputType = startFuncData.inputType();
        if (inputType == ANYDATA) {
            inputType = JSON;
        }
        return new BallerinaModel.Function(startFuncData.name(),
                List.of(new Parameter(inputVar.varName(), inputType),
                        new Parameter(new TypeDesc.MapTypeDesc(XML), params.varName(), exprFrom("{}"))),
                returnType.toString(), body);
    }

    private static BallerinaModel.Function generateProcessFunction(ProcessContext cx) {
        AnalysisResult analysisResult = cx.analysisResult;
        AnalysisResult.ControlFlowFunctions controlFlowFunctions = analysisResult.getControlFlowFunctions(cx.process.scope());
        String name = controlFlowFunctions.scopeFn();
        List<Statement> body = new ArrayList<>();
        String inputVarName = "input";
        String paramsVarName = "params";
        VarDeclStatment context = cx.initContextVar(paramsVarName);
        body.add(context);
        String addToContextFn = cx.getAddToContextFn();
        VariableReference input = new VariableReference(inputVarName);
        body.add(new CallStatement(new FunctionCall(addToContextFn,
                List.of(cx.contextVarRef(), new StringConstant(ConversionUtils.Constants.CONTEXT_INPUT_NAME), input))));
        VarDeclStatment result =
                new VarDeclStatment(TypeDesc.UnionTypeDesc.of(XML, ERROR), "result",
                        new FunctionCall(controlFlowFunctions.activityRunner(), List.of(context.ref())));
        body.add(result);
        handleErrorResult(cx, result, context, body);
        body.add(new Return<>(result.ref()));
        return new BallerinaModel.Function(name,
                List.of(new Parameter(inputVarName, XML),
                        new Parameter(paramsVarName, new TypeDesc.MapTypeDesc(XML))), XML,
                body);
    }

    private static void handleErrorResult(ProcessContext cx, VarDeclStatment result, VarDeclStatment context,
                                          List<Statement> body) {
        TypeCheckExpression typeCheck = new TypeCheckExpression(result.ref(), ERROR);
        Statement.IfElseStatement ifElse =
                new Statement.IfElseStatement(typeCheck,
                        List.of(new Return<>(
                                new FunctionCall(cx.analysisResult.getControlFlowFunctions(cx.process.scope()).
                                        errorHandler(), List.of(result.ref(), context.ref())))),
                        List.of(), List.of());
        body.add(ifElse);
    }

    private static BallerinaModel.Function generateActivityFlowFunction(ProcessContext cx) {
        AnalysisResult analysisResult = cx.analysisResult;
        List<Activity> activities = analysisResult.sortedActivities(cx.process).toList();
        List<Statement> body = new ArrayList<>();
        VariableReference result = generateActivityFlowFunctionInner(cx, activities,
                Check::new, body, new VariableReference("input"));
        body.add(new Return<>(result));
        String activityRunnerFunction = analysisResult.getControlFlowFunctions(cx.process.scope()).activityRunner();
        return new BallerinaModel.Function(activityRunnerFunction,
                List.of(new Parameter("cx", new TypeDesc.MapTypeDesc(XML))),
                TypeDesc.UnionTypeDesc.of(XML, ERROR), body);
    }

    private static BallerinaModel.Function generateErrorFlowFunction(ProcessContext cx) {
        AnalysisResult analysisResult = cx.analysisResult;
        List<Activity> activities = analysisResult.sortedFaultHandlerActivities(cx.process).toList();

        List<Statement> body = new ArrayList<>();
        if (activities.isEmpty()) {
            body.add(stmtFrom(new Panic(new VariableReference("err")) + ";\n"));
        } else {
            VarDeclStatment input =
                    new VarDeclStatment(XML, "input", new XMLTemplate("<root></root>"));
            body.add(input);
            VariableReference result = generateActivityFlowFunctionInner(cx, activities, CheckPanic::new, body,
                    input.ref());
            body.add(new Return<>(result));

        }
        String errorHandlerFunction = analysisResult.getControlFlowFunctions(cx.process.scope()).errorHandler();
        return new BallerinaModel.Function(errorHandlerFunction,
                List.of(new Parameter("err", ERROR),
                        new Parameter("cx", new TypeDesc.MapTypeDesc(XML))),
                XML.toString(), body);
    }

    private static VariableReference generateActivityFlowFunctionInner(ProcessContext cx, List<Activity> activities,
                                                                       Function<FunctionCall, Expression> callHandler,
                                                                       List<Statement> body, VariableReference input) {
        VariableReference context = new VariableReference("cx");
        Map<Activity, VariableReference> activityResult = new HashMap<>();
        for (int i = 0; i < activities.size(); i++) {
            Activity activity = activities.get(i);
            VarDeclStatment result = generateActivityFunctionCall(cx, activityResult, activity, "result" + i,
                    callHandler, body, input, context);
            activityResult.put(activity, result.ref());
            input = result.ref();
        }
        return input;
    }

    private static VarDeclStatment generateActivityFunctionCall(ProcessContext cx,
                                                                Map<Activity, VariableReference> activityResults,
                                                                Activity activity, String varName,
                                                                Function<FunctionCall, Expression> callHandler,
                                                                List<Statement> body, VariableReference input,
                                                                VariableReference context) {
        AnalysisResult analysisResult = cx.analysisResult;
        record TransitionFunctionData(VariableReference inputVar, String functionName) {

        }
        List<FunctionCall> predicates = analysisResult.transitionConditions(activity)
                .map(data -> new TransitionFunctionData(activityResults.get(data.activity()),
                        cx.predicateFunction(data.predicate())))
                .map(data -> new FunctionCall(data.functionName, List.of(data.inputVar)))
                .toList();

        if (predicates.isEmpty()) {
            VarDeclStatment result = activityFunctionCallResult(activity, varName, callHandler, context,
                    analysisResult);
            body.add(result);
            return result;
        }

        VarDeclStatment result = new VarDeclStatment(XML, varName);
        body.add(result);
        Expression cond = predicates.getFirst();
        for (int i = 1; i < predicates.size(); i++) {
            cond = new Expression.BinaryLogical(cond, predicates.get(i), BinaryLogical.Operator.OR);
        }
        Statement.IfElseStatement ifElse = new Statement.IfElseStatement(cond,
                List.of(new VarAssignStatement(result.ref(),
                        new Expression.Check(activityFunctionCall(activity, context, analysisResult)))),
                List.of(), List.of(new VarAssignStatement(result.ref(), input)));
        body.add(ifElse);
        return result;
    }

    private static VarDeclStatment activityFunctionCallResult(Activity activity, String varName,
                                                              Function<FunctionCall, Expression> callHandler,
                                                              VariableReference context,
                                                              AnalysisResult analysisResult) {
        return new VarDeclStatment(XML, varName, callHandler.apply(
                activityFunctionCall(activity, context, analysisResult)));
    }

    private static @NotNull FunctionCall activityFunctionCall(Activity activity, VariableReference context,
                                                              AnalysisResult analysisResult) {
        return new FunctionCall(analysisResult.from(activity).functionName(), List.of(context));
    }
}
