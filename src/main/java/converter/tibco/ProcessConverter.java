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

package converter.tibco;

import ballerina.BallerinaModel;
import ballerina.BallerinaModel.Expression.FunctionCall;
import ballerina.BallerinaModel.Expression.VariableReference;
import ballerina.BallerinaModel.Statement.CallStatement;
import ballerina.BallerinaModel.Statement.Return;
import ballerina.BallerinaModel.Statement.VarAssignStatement;
import ballerina.BallerinaModel.Statement.VarDeclStatment;
import converter.tibco.analyzer.AnalysisResult;
import org.jetbrains.annotations.NotNull;
import tibco.TibcoModel;

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

import static ballerina.BallerinaModel.TypeDesc.BuiltinType.BOOLEAN;
import static ballerina.BallerinaModel.TypeDesc.BuiltinType.ERROR;
import static ballerina.BallerinaModel.TypeDesc.BuiltinType.XML;

public class ProcessConverter {

        private ProcessConverter() {
        }

        static BallerinaModel.Module convertProject(
                        TibcoToBalConverter.ProjectConversionContext conversionContext,
                        Collection<TibcoModel.Process> processes,
                        Collection<TibcoModel.Type.Schema> types,
                        Collection<TibcoModel.Resource.JDBCResource> jdbcResources,
                        Collection<TibcoModel.Resource.HTTPConnectionResource> httpConnectionResources,
                        Set<TibcoModel.Resource.HTTPClientResource> httpClientResources) {
                ProjectContext cx = new ProjectContext(conversionContext);
                convertResources(cx, jdbcResources, httpConnectionResources, httpClientResources);

                record ProcessResult(TibcoModel.Process process, TypeConversionResult result) {

                }
                List<ProcessResult> results = processes.stream().map(process -> new ProcessResult(process,
                                convertTypes(cx.getProcessContext(process), process))).toList();
                convertTypes(cx, types);
                // We need to ensure all the type definitions have been processed before we
                // start processing the functions
                List<BallerinaModel.TextDocument> textDocuments = results.stream().map(result -> {
                        TibcoModel.Process process = result.process();
                        return convertBody(cx.getProcessContext(process), process, result.result());
                }).toList();
                return cx.serialize(textDocuments);
        }

        private static void convertResources(ProjectContext cx,
                        Collection<TibcoModel.Resource.JDBCResource> jdbcResources,
                        Collection<TibcoModel.Resource.HTTPConnectionResource> httpConnectionResources,
                        Set<TibcoModel.Resource.HTTPClientResource> httpClientResources) {
                for (TibcoModel.Resource.JDBCResource resource : jdbcResources) {
                        ResourceConvertor.convertJDBCResource(cx, resource);
                }
                for (TibcoModel.Resource.HTTPConnectionResource resource : httpConnectionResources) {
                        ResourceConvertor.convertHttpConnectionResource(cx, resource);
                }
                for (TibcoModel.Resource.HTTPClientResource resource : httpClientResources) {
                        ResourceConvertor.convertHttpClientResource(cx, resource);
                }
        }

        static void convertTypes(ProjectContext cx, Collection<TibcoModel.Type.Schema> schemas) {
                ContextWithFile typeContext = cx.getTypeContext();
                TypeConverter.convertSchemas(typeContext, schemas);
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
                process.variables().stream().filter(each -> each instanceof TibcoModel.Variable.PropertyVariable)
                                .forEach(var -> cx.addResourceVariable(
                                                (TibcoModel.Variable.PropertyVariable) var));
                List<BallerinaModel.Function> functions = cx.analysisResult.activities().stream()
                                .map(activity -> ActivityConverter.convertActivity(cx, activity))
                                .collect(Collectors.toCollection(ArrayList::new));
                addTransitionPredicates(cx, functions);
                if (process.scope().isPresent()) {
                        functions.add(generateStartFunction(cx));
                        functions.add(generateActivityFlowFunction(cx));
                        functions.add(generateErrorFlowFunction(cx));
                        functions.add(generateProcessFunction(cx));
                }

                functions.sort(Comparator.comparing(BallerinaModel.Function::functionName));

                return cx.serialize(result.service(), functions);
        }

        private static void addTransitionPredicates(ProcessContext cx, List<BallerinaModel.Function> accum) {
                cx.analysisResult.activities().stream()
                                .filter(each -> each instanceof TibcoModel.Scope.Flow.Activity.ActivityWithSources)
                                .forEach(activity -> addTransitionPredicates(cx,
                                                (TibcoModel.Scope.Flow.Activity.ActivityWithSources) activity, accum));
        }

        private static void addTransitionPredicates(
                        ProcessContext cx, TibcoModel.Scope.Flow.Activity.ActivityWithSources activity,
                        List<BallerinaModel.Function> accum) {
                BallerinaModel.Expression prev = null;
                VariableReference value = new VariableReference("input");
                for (TibcoModel.Scope.Flow.Activity.Source source : activity.sources()) {
                        var predicate = source.condition();
                        if (predicate.isEmpty()) {
                                continue;
                        }
                        switch (predicate.get()) {
                                case TibcoModel.Scope.Flow.Activity.Expression.XPath xPath -> {
                                        BallerinaModel.Expression expr = expr(cx, value, xPath);
                                        prev = expr;
                                        accum.add(getTransitionPredicateFn(cx, xPath, expr));
                                }
                                case TibcoModel.Scope.Flow.Activity.Source.Predicate.Else anElse -> {
                                        assert prev != null : "Should not be the first predicate";
                                        accum.add(getTransitionPredicateFn(cx, anElse,
                                                        new BallerinaModel.Expression.Not(prev)));
                                }
                        }
                }
        }

        private static BallerinaModel.Expression expr(ProcessContext cx,
                        VariableReference value,
                        TibcoModel.Scope.Flow.Activity.Expression.XPath predicate) {
                String predicateTestFn = cx.getPredicateTestFunction();
                BallerinaModel.Expression.StringConstant xPathExpr = new BallerinaModel.Expression.StringConstant(
                                ConversionUtils.escapeString(predicate.expression()));
                return new FunctionCall(predicateTestFn, List.of(value, xPathExpr));
        }

        private static BallerinaModel.Function getTransitionPredicateFn(
                        ProcessContext cx,
                        TibcoModel.Scope.Flow.Activity.Source.Predicate predicate,
                        BallerinaModel.Expression expr) {
                return new BallerinaModel.Function(cx.predicateFunction(predicate),
                                List.of(new BallerinaModel.Parameter("input", XML)),
                                Optional.of(BOOLEAN.toString()),
                                List.of(new Return<>(expr)));
        }

        private static TypeConversionResult convertTypes(ProcessContext cx, TibcoModel.Process process) {
                List<BallerinaModel.Service> services = new ArrayList<>();
                Collection<TibcoModel.Type.Schema> schemas = new ArrayList<>();
                for (TibcoModel.Type type : process.types()) {
                        switch (type) {
                                case TibcoModel.Type.Schema schema -> schemas.add(schema);
                                case TibcoModel.Type.WSDLDefinition wsdlDefinition ->
                                        services.addAll(TypeConverter.convertWsdlDefinition(cx, wsdlDefinition));
                        }
                }
                TypeConverter.convertSchemas(cx, schemas);
                return new TypeConversionResult(services);
        }

        private record TypeConversionResult(Collection<BallerinaModel.Service> service) {

        }

        private static BallerinaModel.Function generateStartFunction(ProcessContext cx) {

                List<BallerinaModel.Statement> body = new ArrayList<>();
                var startFuncData = cx.getProcessStartFunction();
                String inputVariable = "input";
                FunctionCall toXMLCall = new FunctionCall(cx.getToXmlFunction(), new String[] { inputVariable });
                String inputXML = "inputXML";
                VarDeclStatment inputXMLVar = new VarDeclStatment(XML, inputXML,
                                new BallerinaModel.Expression.CheckPanic(toXMLCall));
                body.add(inputXMLVar);

                String processFunction = cx.getProcessFunction();
                VarDeclStatment xmlResult = new VarDeclStatment(XML, "xmlResult",
                                new FunctionCall(processFunction, new String[] { inputXML }));
                body.add(xmlResult);

                BallerinaModel.TypeDesc returnType = startFuncData.returnType();
                String convertToTypeFunction = cx.getConvertToTypeFunction(returnType);
                VarDeclStatment result = new VarDeclStatment(returnType, "result",
                                new FunctionCall(convertToTypeFunction, new String[] { "xmlResult" }));
                body.add(result);

                Return<VariableReference> returnStatement = new Return<>(
                                Optional.of(new VariableReference("result")));
                body.add(returnStatement);

                BallerinaModel.TypeDesc inputType = startFuncData.inputType();
                return new BallerinaModel.Function(startFuncData.name(),
                                List.of(new BallerinaModel.Parameter(inputVariable, inputType)),
                                Optional.of(returnType.toString()),
                                body);
        }

        private static BallerinaModel.Function generateProcessFunction(ProcessContext cx) {
                String name = cx.getProcessFunction();
                List<BallerinaModel.Statement> body = new ArrayList<>();
                VarDeclStatment context = cx.initContextVar();
                body.add(context);
                String addToContextFn = cx.getAddToContextFn();
                String inputVarName = "input";
                VariableReference input = new VariableReference(inputVarName);
                body.add(new CallStatement(
                                new FunctionCall(addToContextFn, List.of(cx.contextVarRef(),
                                                new BallerinaModel.Expression.StringConstant(
                                                                ConversionUtils.Constants.CONTEXT_INPUT_NAME),
                                                input))));
                VarDeclStatment result = new VarDeclStatment(
                                BallerinaModel.TypeDesc.UnionTypeDesc.of(XML, ERROR), "result",
                                new FunctionCall(cx.getActivityRunnerFunction(),
                                                List.of(input, context.ref())));
                body.add(result);
                handleErrorResult(cx, result, context, body);
                body.add(new Return<>(result.ref()));
                return new BallerinaModel.Function(name,
                                List.of(new BallerinaModel.Parameter(inputVarName, XML)), Optional.of(XML.toString()),
                                body);
        }

        private static void handleErrorResult(ProcessContext cx, VarDeclStatment result,
                        VarDeclStatment context,
                        List<BallerinaModel.Statement> body) {
                BallerinaModel.Expression.TypeCheckExpression typeCheck = new BallerinaModel.Expression.TypeCheckExpression(
                                result.ref(), ERROR);
                BallerinaModel.IfElseStatement ifElse = new BallerinaModel.IfElseStatement(typeCheck,
                                List.of(new Return<>(
                                                new FunctionCall(cx.getErrorHandlerFunction(),
                                                                List.of(result.ref(), context.ref())))),
                                List.of(), List.of());
                body.add(ifElse);
        }

        private static BallerinaModel.Function generateActivityFlowFunction(ProcessContext cx) {
                AnalysisResult analysisResult = cx.analysisResult;
                List<TibcoModel.Scope.Flow.Activity> activities = analysisResult.sortedActivities(cx.process).toList();
                List<BallerinaModel.Statement> body = new ArrayList<>();
                VariableReference result = generateActivityFlowFunctionInner(cx, activities,
                                BallerinaModel.Expression.Check::new, body,
                                new VariableReference("input"));
                body.add(new Return<>(result));
                return new BallerinaModel.Function(cx.getActivityRunnerFunction(),
                                List.of(new BallerinaModel.Parameter("input", XML),
                                                new BallerinaModel.Parameter("cx",
                                                                new BallerinaModel.TypeDesc.MapTypeDesc(XML))),
                                Optional.of(BallerinaModel.TypeDesc.UnionTypeDesc.of(XML, ERROR).toString()), body);
        }

        private static BallerinaModel.Function generateErrorFlowFunction(ProcessContext cx) {
                AnalysisResult analysisResult = cx.analysisResult;
                List<TibcoModel.Scope.Flow.Activity> activities = analysisResult
                                .sortedFaultHandlerActivities(cx.process).toList();

                List<BallerinaModel.Statement> body = new ArrayList<>();
                if (activities.isEmpty()) {
                        body.add(new BallerinaModel.BallerinaStatement(
                                        new BallerinaModel.Expression.CheckPanic(
                                                        new VariableReference("err")) + ";\n"));
                } else {
                        VarDeclStatment input = new VarDeclStatment(XML, "input",
                                        new BallerinaModel.Expression.XMLTemplate(""));
                        body.add(input);
                        VariableReference result = generateActivityFlowFunctionInner(cx, activities,
                                        BallerinaModel.Expression.CheckPanic::new, body,
                                        input.ref());
                        body.add(new Return<>(result));

                }
                return new BallerinaModel.Function(cx.getErrorHandlerFunction(),
                                List.of(new BallerinaModel.Parameter("err", ERROR),
                                                new BallerinaModel.Parameter("cx",
                                                                new BallerinaModel.TypeDesc.MapTypeDesc(XML))),
                                Optional.of(XML.toString()), body);
        }

        private static VariableReference generateActivityFlowFunctionInner(
                        ProcessContext cx,
                        List<TibcoModel.Scope.Flow.Activity> activities,
                        Function<FunctionCall, BallerinaModel.Expression> callHandler,
                        List<BallerinaModel.Statement> body, VariableReference input) {
                VariableReference context = new VariableReference("cx");
                Map<TibcoModel.Scope.Flow.Activity, VariableReference> activityResult = new HashMap<>();
                for (int i = 0; i < activities.size(); i++) {
                        TibcoModel.Scope.Flow.Activity activity = activities.get(i);
                        VarDeclStatment result = generateActivityFunctionCall(cx, activityResult, activity,
                                        "result" + i, callHandler,
                                        body, input,
                                        context);
                        activityResult.put(activity, result.ref());
                        input = result.ref();
                }
                return input;
        }

        private static VarDeclStatment generateActivityFunctionCall(
                        ProcessContext cx,
                        Map<TibcoModel.Scope.Flow.Activity, VariableReference> activityResults,
                        TibcoModel.Scope.Flow.Activity activity, String varName,
                        Function<FunctionCall, BallerinaModel.Expression> callHandler,
                        List<BallerinaModel.Statement> body,
                        VariableReference input,
                        VariableReference context) {
                AnalysisResult analysisResult = cx.analysisResult;
                record TransitionFunctionData(VariableReference inputVar,
                                String functionName) {

                }
                List<FunctionCall> predicates = analysisResult.transitionConditions(activity)
                                .map(data -> new TransitionFunctionData(activityResults.get(data.activity()),
                                                cx.predicateFunction(
                                                                data.predicate())))
                                .map(data -> new FunctionCall(data.functionName,
                                                List.of(data.inputVar)))
                                .toList();

                if (predicates.isEmpty()) {
                        VarDeclStatment result = activityFunctionCallResult(activity, varName, callHandler, input,
                                        context,
                                        analysisResult);
                        body.add(result);
                        return result;
                }

                VarDeclStatment result = new VarDeclStatment(XML, varName);
                body.add(result);
                BallerinaModel.Expression cond = predicates.getFirst();
                for (int i = 1; i < predicates.size(); i++) {
                        cond = new BallerinaModel.Expression.BinaryLogical(cond, predicates.get(i),
                                        BallerinaModel.Expression.BinaryLogical.Operator.OR);
                }
                BallerinaModel.IfElseStatement ifElse = new BallerinaModel.IfElseStatement(cond,
                                List.of(new VarAssignStatement(result.ref(),
                                                new BallerinaModel.Expression.Check(new FunctionCall(
                                                                analysisResult.from(activity).functionName(),
                                                                List.of(input, context))))),
                                List.of(), List.of(new VarAssignStatement(result.ref(), input)));
                body.add(ifElse);
                return result;
        }

        private static VarDeclStatment activityFunctionCallResult(
                        TibcoModel.Scope.Flow.Activity activity,
                        String varName,
                        Function<FunctionCall, BallerinaModel.Expression> callHandler,
                        VariableReference input,
                        VariableReference context,
                        AnalysisResult analysisResult) {
                FunctionCall callExpr = new FunctionCall(
                                analysisResult.from(activity).functionName(), List.of(input, context));
                return new VarDeclStatment(XML, varName, callHandler.apply(callExpr));
        }
}
