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

package tibco.converter;

import common.BallerinaModel;
import common.BallerinaModel.Expression;
import common.BallerinaModel.Expression.BinaryLogical;
import common.BallerinaModel.Expression.Check;
import common.BallerinaModel.Expression.CheckPanic;
import common.BallerinaModel.Expression.FunctionCall;
import common.BallerinaModel.Expression.Panic;
import common.BallerinaModel.Expression.StringConstant;
import common.BallerinaModel.Expression.TernaryExpression;
import common.BallerinaModel.Expression.TypeCheckExpression;
import common.BallerinaModel.Expression.VariableReference;
import common.BallerinaModel.Expression.XMLTemplate;
import common.BallerinaModel.Parameter;
import common.BallerinaModel.Statement;
import common.BallerinaModel.Statement.CallStatement;
import common.BallerinaModel.Statement.Return;
import common.BallerinaModel.Statement.VarAssignStatement;
import common.BallerinaModel.Statement.VarDeclStatment;
import common.BallerinaModel.TypeDesc;
import org.jetbrains.annotations.NotNull;
import tibco.TibcoModel;
import tibco.TibcoModel.Process.ExplicitTransitionGroup.InlineActivity.HttpEventSource;
import tibco.TibcoModel.Scope.Flow.Activity;
import tibco.TibcoModel.Scope.Flow.Activity.Expression.XPath;
import tibco.analyzer.AnalysisResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static common.BallerinaModel.TypeDesc.BuiltinType.ANYDATA;
import static common.BallerinaModel.TypeDesc.BuiltinType.BOOLEAN;
import static common.BallerinaModel.TypeDesc.BuiltinType.ERROR;
import static common.BallerinaModel.TypeDesc.BuiltinType.JSON;
import static common.BallerinaModel.TypeDesc.BuiltinType.NIL;
import static common.BallerinaModel.TypeDesc.BuiltinType.UnionTypeDesc;
import static common.BallerinaModel.TypeDesc.BuiltinType.XML;
import static common.ConversionUtils.exprFrom;
import static common.ConversionUtils.stmtFrom;
import static tibco.converter.ConversionUtils.baseName;

public class ProcessConverter {

    private ProcessConverter() {
    }

    static BallerinaModel.Service convertStartActivityService(
            ProcessContext cx,
            TibcoModel.Process.ExplicitTransitionGroup group) {
        BallerinaModel.Resource resource = generateResourceFunctionForStartActivity(cx, group);
        TibcoModel.Process.ExplicitTransitionGroup.InlineActivity startActivity = group.startActivity();
        assert startActivity instanceof HttpEventSource;
        String name = baseName(((HttpEventSource) startActivity).sharedChannel());
        VariableReference listener = cx.getProjectContext().httpListener(name);
        // Workaround for https://github.com/ballerina-platform/ballerina-library/issues/7893
        String localListenerName = cx.getAnonName();
        BallerinaModel.ModuleVar localRef = new BallerinaModel.ModuleVar(localListenerName, "listener http:Listener",
                Optional.of(listener), false, false);
        cx.addLibraryImport(Library.HTTP);
        cx.declareModuleVar(localListenerName, localRef);
        return new BallerinaModel.Service("", List.of(localListenerName), Optional.empty(), List.of(resource),
                List.of(), List.of(), List.of(), List.of());
    }

    static void addProcessClient(ProcessContext cx, TibcoModel.Process.ExplicitTransitionGroup group,
                                 Collection<TibcoModel.Resource.HTTPSharedResource> httpSharedResources) {
        TibcoModel.Process.ExplicitTransitionGroup.InlineActivity startActivity = group.startActivity();
        if (!(startActivity instanceof HttpEventSource httpEventSource)) {
            return;
        }
        String name = baseName(httpEventSource.sharedChannel());
        TibcoModel.Resource.HTTPSharedResource http = httpSharedResources.stream()
                .filter(each -> each.name().equals(name))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Failed to find http source for " + name));
        String path = http.host() + ":" + http.port();
        cx.addLibraryImport(Library.HTTP);
        BallerinaModel.ModuleVar moduleVar = new BallerinaModel.ModuleVar(cx.getAnonName(), "http:Client",
                Optional.of(new CheckPanic(common.ConversionUtils.exprFrom("new (\"%s\")".formatted(path)))),
                false, false);
        cx.addOnDemandModuleVar(moduleVar.name(), moduleVar);
        cx.registerProcessClient(moduleVar.name());
    }

    private static BallerinaModel.@NotNull Resource generateResourceFunctionForStartActivity(
            ProcessContext cx, TibcoModel.Process.ExplicitTransitionGroup group) {
        List<Statement> body = new ArrayList<>();
        Parameter parameter = new Parameter("input", XML);
        XMLTemplate inputXml = new XMLTemplate("""
                <root>
                    <item>
                        ${%s}
                    </item>
                </root>
                """.formatted(parameter.name()));
        VarDeclStatment inputValDecl = new VarDeclStatment(XML, "inputVal", inputXml);
        body.add(inputValDecl);
        VarDeclStatment paramXmlDecl = new VarDeclStatment(new TypeDesc.MapTypeDesc(XML), "paramXML",
                common.ConversionUtils.exprFrom("{post: %s}".formatted(inputValDecl.varName())));
        body.add(paramXmlDecl);
        FunctionCall procFnCall = new FunctionCall(cx.getProcessStartFunction().name(),
                List.of(new VariableReference(parameter.name()), paramXmlDecl.ref()));
        VarDeclStatment resultDecl = new VarDeclStatment(XML, "result", procFnCall);
        body.add(resultDecl);
        group.returnBindings().ifPresent(binding ->
                body.add(new VarAssignStatement(resultDecl.ref(), xsltTransform(cx, resultDecl.ref(), binding))));
        body.add(new Return<>(resultDecl.ref()));
        return new BallerinaModel.Resource("'default[string... path]", "",
                List.of(parameter), Optional.of("xml"), body);
    }

    public static BallerinaModel.Module convertProcess(TibcoModel.Process process) {
        ProjectContext cx = new ProjectContext();
        return convertProcess(cx.getProcessContext(process), process);
    }

    private static BallerinaModel.Module convertProcess(ProcessContext cx, TibcoModel.Process process) {
        TypeConversionResult result = convertTypes(cx, process);
        BallerinaModel.TextDocument textDocument = convertBody(cx, process, result);
        ProjectContext projectContext = cx.projectContext;
        return projectContext.serialize(List.of(textDocument));
    }

    static BallerinaModel.TextDocument convertBody(ProcessContext cx, TibcoModel.Process process,
            TypeConversionResult result) {
        process.variables().stream()
                .filter(each -> each instanceof TibcoModel.Variable.PropertyVariable)
                .forEach(var -> cx.addResourceVariable((TibcoModel.Variable.PropertyVariable) var));
        List<BallerinaModel.Function> functions = cx.analysisResult.activities().stream()
                .map(activity -> ActivityConverter.convertActivity(cx, activity))
                .collect(Collectors.toCollection(ArrayList::new));
        if (process.transitionGroup() != null) {
            functions.addAll(convertExplicitTransitionGroup(cx, process.transitionGroup()));
        } else {
            addTransitionPredicates(cx, functions);
            functions.add(generateStartFunction(cx));
            functions.add(generateActivityFlowFunction(cx));
            functions.add(generateErrorFlowFunction(cx));
            functions.add(generateProcessFunction(cx));
            cx.handledScopes.add(process.scope());
        }
        AnalysisResult analysisResult = cx.analysisResult;
        analysisResult.scopes(process).stream().map(scope -> generateControlFlowFunctionsForScope(cx, scope))
                .flatMap(Collection::stream).forEach(functions::add);

        functions.sort(Comparator.comparing(BallerinaModel.Function::functionName));
        process.nameSpaces().forEach(cx::addNameSpace);
        return cx.serialize(result.service(), functions);
    }

    private static Collection<BallerinaModel.Function> convertExplicitTransitionGroup(
            ProcessContext cx, TibcoModel.Process.ExplicitTransitionGroup group) {
        return List.of(
                generateExplicitTransitionBlockStartFunction(cx, group),
                generateExplicitTransitionBlockErrorFunction(cx, group),
                generateExplicitTransitionBlockActivityFunction(cx, group),
                generateExplicitTransitionBlockScopeFunction(cx, group));
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
                    Expression expr = ConversionUtils.xPath(cx, value, new VariableReference("cx"), xPath);
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

    private static BallerinaModel.Function getTransitionPredicateFn(ProcessContext cx,
            Activity.Source.Predicate predicate,
            Expression expr) {
        return new BallerinaModel.Function(cx.predicateFunction(predicate),
                List.of(new Parameter("input", XML), new Parameter("cx", new TypeDesc.MapTypeDesc(XML))),
                BOOLEAN, List.of(new Return<>(expr)));
    }

    static TypeConversionResult convertTypes(ProcessContext cx, TibcoModel.Process process) {
        List<BallerinaModel.Service> services = process.types().stream()
                .filter(type -> type instanceof TibcoModel.Type.WSDLDefinition)
                .map(type -> (TibcoModel.Type.WSDLDefinition) type)
                .flatMap(wsdlDefinition -> TypeConverter.convertWsdlDefinition(cx, wsdlDefinition)
                        .stream())
                .collect(Collectors.toList());
        cx.allocatedDefaultClient(services);
        return new TypeConversionResult(services);
    }

    record TypeConversionResult(Collection<BallerinaModel.Service> service) {

    }

    private static BallerinaModel.Function generateExplicitTransitionBlockStartFunction(
            ProcessContext cx, TibcoModel.Process.ExplicitTransitionGroup group) {
        List<Parameter> parameters = List.of(new Parameter("inputXML", XML),
                new Parameter("params", new TypeDesc.MapTypeDesc(XML)));
        AnalysisResult.ControlFlowFunctions controlFn = cx.analysisResult.getControlFlowFunctions(group);
        String scopeFn = controlFn.scopeFn();
        List<Statement> body = List.of(new Return<>(new FunctionCall(scopeFn,
                List.of(new VariableReference("params")))));
        return new BallerinaModel.Function(cx.getProcessStartFunction().name(), parameters, XML, body);
    }


    private static BallerinaModel.Function generateExplicitTransitionBlockActivityFunction(
            ProcessContext cx, TibcoModel.Process.ExplicitTransitionGroup group) {
        AnalysisResult analysisResult = cx.analysisResult;
        List<Activity> activities = analysisResult.sortedActivities(group).toList();
        String activityRunnerFunction = analysisResult.getControlFlowFunctions(group).activityRunner();
        return generateActivityFlowFunctionInner(cx, activities, activityRunnerFunction);
    }


    private static BallerinaModel.Function generateExplicitTransitionBlockScopeFunction(
            ProcessContext cx, TibcoModel.Process.ExplicitTransitionGroup group) {
        AnalysisResult analysisResult = cx.analysisResult;
        AnalysisResult.ControlFlowFunctions controlFlowFunctions = analysisResult.getControlFlowFunctions(group);
        return generateScopeFunctionInner(controlFlowFunctions);
    }


    // TODO: refactor common code with generateErrorFlowFunction
    private static BallerinaModel.Function generateExplicitTransitionBlockErrorFunction(
            ProcessContext cx, TibcoModel.Process.ExplicitTransitionGroup group) {
        Parameter context = new Parameter("cx", new TypeDesc.MapTypeDesc(XML));
        List<Statement> body = List.of(stmtFrom(new Panic(new VariableReference("err")) + ";\n"));
        String errorHandlerFunction = cx.analysisResult.getControlFlowFunctions(group).errorHandler();
        return new BallerinaModel.Function(errorHandlerFunction,
                List.of(new Parameter("err", ERROR),
                        context),
                XML.toString(), body);
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
        } else if (inputType == NIL) {
            inputType = UnionTypeDesc.of(new TypeDesc.MapTypeDesc(ANYDATA), NIL);
        }
        return new BallerinaModel.Function(startFuncData.name(),
                List.of(new Parameter(inputVar.varName(), inputType),
                        new Parameter(new TypeDesc.MapTypeDesc(XML), params.varName(), exprFrom("{}"))),
                returnType.toString(), body);
    }

    private static BallerinaModel.Function generateProcessFunction(ProcessContext cx) {
        AnalysisResult analysisResult = cx.analysisResult;
        AnalysisResult.ControlFlowFunctions controlFlowFunctions = analysisResult.getControlFlowFunctions(
                cx.process.scope());
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
        generateScopeFnBody(controlFlowFunctions, context.ref(), body);
        return new BallerinaModel.Function(name,
                List.of(new Parameter(inputVarName, XML),
                        new Parameter(paramsVarName, new TypeDesc.MapTypeDesc(XML))),
                XML,
                body);
    }

    private static Collection<BallerinaModel.Function> generateControlFlowFunctionsForScope(
            ProcessContext cx, TibcoModel.Scope scope) {
        if (cx.handledScopes.contains(scope)) {
            return List.of();
        }
        cx.handledScopes.add(scope);
        BallerinaModel.Function activityFlowFn = generateActivityFlowFunction(cx, scope);
        BallerinaModel.Function errorFlowFn = generateErrorFlowFunction(cx, scope);
        BallerinaModel.Function scopeFn = generateInnerScopeFunction(cx, scope);
        return List.of(activityFlowFn, errorFlowFn, scopeFn);
    }

    private static BallerinaModel.Function generateInnerScopeFunction(ProcessContext cx, TibcoModel.Scope scope) {
        AnalysisResult analysisResult = cx.analysisResult;
        AnalysisResult.ControlFlowFunctions controlFlowFunctions = analysisResult.getControlFlowFunctions(scope);
        return generateScopeFunctionInner(controlFlowFunctions);
    }

    private static BallerinaModel.@NotNull Function generateScopeFunctionInner(
            AnalysisResult.ControlFlowFunctions controlFlowFunctions) {
        String name = controlFlowFunctions.scopeFn();
        List<Statement> body = new ArrayList<>();
        Parameter parameter = new Parameter("cx", new TypeDesc.MapTypeDesc(XML));
        generateScopeFnBody(controlFlowFunctions, new VariableReference(parameter.name()), body);
        return new BallerinaModel.Function(name, List.of(parameter), XML, body);
    }

    private static void generateScopeFnBody(AnalysisResult.ControlFlowFunctions controlFlowFunctions,
            VariableReference context, List<Statement> body) {
        VarDeclStatment result = new VarDeclStatment(TypeDesc.UnionTypeDesc.of(XML, ERROR), "result",
                new FunctionCall(controlFlowFunctions.activityRunner(), List.of(context)));
        body.add(result);
        handleErrorResult(result, context, controlFlowFunctions.errorHandler(), body);
        body.add(new Return<>(result.ref()));
    }

    private static void handleErrorResult(VarDeclStatment result, VariableReference context,
            String errorHandlerFn, List<Statement> body) {
        TypeCheckExpression typeCheck = new TypeCheckExpression(result.ref(), ERROR);
        Statement.IfElseStatement ifElse = new Statement.IfElseStatement(typeCheck,
                List.of(new Return<>(
                        new FunctionCall(errorHandlerFn, List.of(result.ref(), context)))),
                List.of(), List.of());
        body.add(ifElse);
    }

    private static BallerinaModel.Function generateActivityFlowFunction(ProcessContext cx) {
        return generateActivityFlowFunction(cx, cx.process.scope());
    }

    private static BallerinaModel.Function generateActivityFlowFunction(ProcessContext cx, TibcoModel.Scope scope) {
        AnalysisResult analysisResult = cx.analysisResult;
        List<Activity> activities = analysisResult.sortedActivities(scope).toList();
        String activityRunnerFunction = analysisResult.getControlFlowFunctions(scope).activityRunner();
        return generateActivityFlowFunctionInner(cx, activities, activityRunnerFunction);
    }

    private static BallerinaModel.@NotNull Function generateActivityFlowFunctionInner(
            ProcessContext cx, List<Activity> activities, String activityRunnerFunction) {
        List<Statement> body = new ArrayList<>();
        VariableReference result = generateActivityFlowFunctionInner(cx, activities,
                Check::new, body, new VariableReference("input"));
        body.add(new Return<>(result));
        return new BallerinaModel.Function(activityRunnerFunction,
                List.of(new Parameter("cx", new TypeDesc.MapTypeDesc(XML))),
                UnionTypeDesc.of(XML, ERROR), body);
    }

    private static BallerinaModel.Function generateErrorFlowFunction(ProcessContext cx) {
        return generateErrorFlowFunction(cx, cx.process.scope());
    }

    private static BallerinaModel.@NotNull Function generateErrorFlowFunction(ProcessContext cx,
            TibcoModel.Scope scope) {
        Parameter context = new Parameter("cx", new TypeDesc.MapTypeDesc(XML));
        AnalysisResult analysisResult = cx.analysisResult;
        Collection<TibcoModel.Scope.FaultHandler> faultHandlers = scope.faultHandlers();
        List<Statement> body = new ArrayList<>();
        int resultCount = 0;
        if (faultHandlers.isEmpty()) {
            body.add(stmtFrom(new Panic(new VariableReference("err")) + ";\n"));
        } else {
            VariableReference finalResult = null;
            for (TibcoModel.Scope.FaultHandler each : faultHandlers) {
                AnalysisResult.ActivityData data = analysisResult.from(each);
                VarDeclStatment result = new VarDeclStatment(XML, "result" + (resultCount++),
                        new CheckPanic(
                                new FunctionCall(data.functionName(), List.of(new VariableReference(context.name())))));
                body.add(result);
                finalResult = result.ref();
            }
            body.add(new Return<>(finalResult));
        }
        String errorHandlerFunction = analysisResult.getControlFlowFunctions(scope).errorHandler();
        return new BallerinaModel.Function(errorHandlerFunction,
                List.of(new Parameter("err", ERROR),
                        context),
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
                .map(data -> new FunctionCall(data.functionName, List.of(data.inputVar, context)))
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

    private static BallerinaModel.Expression xsltTransform(ProcessContext cx, VariableReference inputVariable,
                                                           Activity.Expression.XSLT xslt) {
        cx.addLibraryImport(Library.XSLT);
        return new CheckPanic(new FunctionCall(ActivityConverter.XSLTConstants.XSLT_TRANSFORM_FUNCTION,
                List.of(inputVariable, new XMLTemplate(xslt.expression()))));
    }
}
