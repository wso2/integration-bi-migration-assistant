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

package tibco.converter;

import common.BallerinaModel;
import common.BallerinaModel.Expression;
import common.BallerinaModel.Expression.BinaryLogical;
import common.BallerinaModel.Expression.Check;
import common.BallerinaModel.Expression.CheckPanic;
import common.BallerinaModel.Expression.FunctionCall;
import common.BallerinaModel.Expression.Panic;
import common.BallerinaModel.Expression.TypeCheckExpression;
import common.BallerinaModel.Expression.VariableReference;
import common.BallerinaModel.Expression.XMLTemplate;
import common.BallerinaModel.Parameter;
import common.BallerinaModel.Statement;
import common.BallerinaModel.Statement.Return;
import common.BallerinaModel.Statement.VarAssignStatement;
import common.BallerinaModel.Statement.VarDeclStatment;
import common.BallerinaModel.TypeDesc;
import common.BallerinaModel.TypeDesc.UnionTypeDesc;
import common.LoggingUtils;
import org.jetbrains.annotations.NotNull;
import tibco.analyzer.AnalysisResult;
import tibco.model.Process;
import tibco.model.Process5;
import tibco.model.Process5.ExplicitTransitionGroup;
import tibco.model.Process5.ExplicitTransitionGroup.InlineActivity.FileEventSource;
import tibco.model.Process5.ExplicitTransitionGroup.InlineActivity.HttpEventSource;
import tibco.model.Process6;
import tibco.model.Resource;
import tibco.model.Scope;
import tibco.model.Scope.Flow.Activity;
import tibco.model.Scope.Flow.Activity.Expression.XPath;
import tibco.model.Type;
import tibco.model.Variable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static common.BallerinaModel.TypeDesc.BuiltinType.BOOLEAN;
import static common.BallerinaModel.TypeDesc.BuiltinType.ERROR;
import static common.BallerinaModel.TypeDesc.BuiltinType.NIL;
import static common.BallerinaModel.TypeDesc.BuiltinType.STRING;
import static common.BallerinaModel.TypeDesc.BuiltinType.XML;
import static common.ConversionUtils.exprFrom;
import static common.ConversionUtils.stmtFrom;
import static tibco.converter.Library.FILE;
import static tibco.converter.Library.JMS;

public class ProcessConverter {

    private ProcessConverter() {
    }

    static @NotNull BallerinaModel.Service convertStartActivityService(
            ProcessContext cx, ExplicitTransitionGroup group) {
        ExplicitTransitionGroup.InlineActivity startActivity = group.startActivity().get();
        return switch (startActivity) {
            case HttpEventSource httpEventSource -> createHTTPServiceForStartActivity(cx, group, httpEventSource);
            case FileEventSource fileEventSource -> createFileEventServiceForStartActivity(cx, group, fileEventSource);
            case ExplicitTransitionGroup.InlineActivity.JMSQueueEventSource jmsQueueEventSource ->
                    createJMSListenerServiceForStartActivity(cx, group, jmsQueueEventSource);
            default -> createFallbackServices(cx, startActivity);
        };
    }

    private static BallerinaModel.Service createFileEventServiceForStartActivity(ProcessContext cx,
                                                                                 ExplicitTransitionGroup group,
                                                                                 FileEventSource fileEventSource) {
        VariableReference listener = createFileListener(cx, fileEventSource);
        List<BallerinaModel.Remote> remoteMethods = new ArrayList<>();
        if (fileEventSource.createEvent()) {
            remoteMethods.add(createFileEventCallBack(cx, "onCreate"));
        }
        if (fileEventSource.modifyEvent()) {
            remoteMethods.add(createFileEventCallBack(cx, "onModify"));
        }
        if (fileEventSource.deleteEvent()) {
            remoteMethods.add(createFileEventCallBack(cx, "onDelete"));
        }

        return new BallerinaModel.Service("\"" + ConversionUtils.sanitizes(fileEventSource.name()) + "\"",
                        List.of(listener.varName()), Optional.empty(), List.of(), List.of(), List.of(), remoteMethods,
                        Optional.empty());
    }

    private static BallerinaModel.Remote createFileEventCallBack(ProcessContext cx, String event) {
        Parameter parameter = new Parameter("event", ConversionUtils.Constants.FILE_EVENT);
        List<Statement> body = new ArrayList<>();
        VarDeclStatment jobSharedVariables =
                new VarDeclStatment(new TypeDesc.MapTypeDesc(common.ConversionUtils.typeFrom("SharedVariableContext")),
                        "jobSharedVariables", exprFrom("{}"));
        body.add(jobSharedVariables);
        body.addAll(initJobSharedVariables(cx.projectContext, jobSharedVariables.ref()));
        body.add(new Statement.Comment("TODO: add any addition data to this XML"));
        VarDeclStatment eventData = new VarDeclStatment(XML, "data", new XMLTemplate(
                """
                        <fileInfo>
                            <fileName>
                                ${%s.name}
                            </fileName>
                        </fileInfo>
                        """.formatted(parameter.name())
        ));
        body.add(eventData);

        VarDeclStatment paramXmlDecl = new VarDeclStatment(new TypeDesc.MapTypeDesc(XML), "paramXML",
                exprFrom("{file: %s}".formatted(eventData.varName())));
        body.add(paramXmlDecl);

        BallerinaModel.TypeDesc.FunctionTypeDesc processFnType = ConversionUtils.processFunctionType(cx);
        VarDeclStatment contextDecl = new VarDeclStatment(cx.contextType(), processFnType.parameters().get(0).name(),
                new FunctionCall(cx.getInitContextFn(), List.of(paramXmlDecl.ref(), jobSharedVariables.ref())));
        body.add(contextDecl);

        body.add(new Statement.CallStatement(
                new FunctionCall(cx.getProcessStartFunction().name(), List.of(contextDecl.ref()))));
        return new BallerinaModel.Remote(
                new BallerinaModel.Function(Optional.empty(), event, List.of(parameter), Optional.empty(),
                        new BallerinaModel.BlockFunctionBody(body)));
    }

    private static VariableReference createFileListener(ProcessContext cx, FileEventSource fileEventSource) {
        String name = ConversionUtils.sanitizes(fileEventSource.name()) + "Listener";
        // TODO: may need to fix path
        String filePath = fileEventSource.fileName().trim();
        BallerinaModel.Listener listener =
                new BallerinaModel.Listener.FileListener(name, filePath, true);
        cx.projectContext.addListnerDeclartion(name, listener, List.of(), List.of(FILE));
        return new VariableReference(listener.name());
    }

    private static BallerinaModel.Service createFallbackServices(ProcessContext cx,
                                                                 ExplicitTransitionGroup.InlineActivity startActivity) {
        cx.log(LoggingUtils.Level.WARN,
                "Unsupported start activity %s generating fallback service".formatted(startActivity.name()));
        List<Statement> body = List.of(
                new Statement.Comment("FIXME: service for start activity: %s".formatted(startActivity.name())),
                new Statement.Comment(
                        "FIXME: call %s from fixed service".formatted(cx.getProcessStartFunction().name()))
        );

        BallerinaModel.Remote remoteFn = new BallerinaModel.Remote(
                new BallerinaModel.Function(Optional.empty(), "onStart", List.of(), Optional.empty(),
                        new BallerinaModel.BlockFunctionBody(body)));

        return new BallerinaModel.Service("\"" + ConversionUtils.sanitizes(startActivity.name()) + "\"",
                List.of(), Optional.empty(), List.of(), List.of(), List.of(), List.of(remoteFn), Optional.empty());
    }

    private static BallerinaModel.Service createJMSListenerServiceForStartActivity(
                    ProcessContext cx, ExplicitTransitionGroup group,
                    ExplicitTransitionGroup.InlineActivity.JMSQueueEventSource jmsQueueEventSource) {
        cx.addLibraryImport(JMS);
        BallerinaModel.Listener.JMSListener listener = createJMSListener(cx, jmsQueueEventSource);
        BallerinaModel.Remote remoteFn = generateRemoteFunctionForJMSStartActivity(cx, group, jmsQueueEventSource);

        return new BallerinaModel.Service("\"" + ConversionUtils.sanitizes(jmsQueueEventSource.name()) + "\"",
                List.of(listener.name()), Optional.empty(), List.of(), List.of(), List.of(), List.of(remoteFn),
                Optional.empty());
    }

    private static BallerinaModel.Remote generateRemoteFunctionForJMSStartActivity(
                    ProcessContext cx, ExplicitTransitionGroup group,
                    ExplicitTransitionGroup.InlineActivity.JMSQueueEventSource jmsQueueEventSource) {
        Parameter parameter = new Parameter("message", ConversionUtils.Constants.JMS_MESSAGE_TYPE);
        List<Statement> body = new ArrayList<>();
        VarDeclStatment jobSharedVariables =
                new VarDeclStatment(new TypeDesc.MapTypeDesc(common.ConversionUtils.typeFrom("SharedVariableContext")),
                        "jobSharedVariables", exprFrom("{}"));
        body.add(jobSharedVariables);
        body.addAll(initJobSharedVariables(cx.projectContext, jobSharedVariables.ref()));
        body.add(Statement.IfElseStatement.ifStatement(common.ConversionUtils.exprFrom(
                        "%s !is %s".formatted(parameter.name(), ConversionUtils.Constants.JMS_TEXT_MESSAGE)),
                List.of(common.ConversionUtils.stmtFrom("panic error(\"Unsupported JMS message type\");"))));
        VarDeclStatment content =
                new VarDeclStatment(STRING, "content", new Expression.FieldAccess(parameter.ref(), "content"));
        body.add(content);
        XMLTemplate inputXml = new XMLTemplate("${%s}".formatted(content.ref()));
        VarDeclStatment inputValDecl = new VarDeclStatment(XML, "inputXML", inputXml);
        body.add(inputValDecl);
        VarDeclStatment paramXmlDecl = new VarDeclStatment(new TypeDesc.MapTypeDesc(XML), "paramXML",
                exprFrom("{jms: %s}".formatted(inputValDecl.varName())));
        body.add(paramXmlDecl);

        BallerinaModel.TypeDesc.FunctionTypeDesc processFnType = ConversionUtils.processFunctionType(cx);
        VarDeclStatment contextDecl = new VarDeclStatment(cx.contextType(), processFnType.parameters().get(0).name(),
                new FunctionCall(cx.getInitContextFn(), List.of(paramXmlDecl.ref(), jobSharedVariables.ref())));
        body.add(contextDecl);

        body.add(new Statement.CallStatement(
                new FunctionCall(cx.getProcessStartFunction().name(), List.of(contextDecl.ref()))));
        return new BallerinaModel.Remote(
                new BallerinaModel.Function(Optional.empty(), "onMessage", List.of(parameter), Optional.empty(),
                        new BallerinaModel.BlockFunctionBody(body)));
    }

    private static BallerinaModel.Listener.JMSListener createJMSListener(
            ProcessContext cx, ExplicitTransitionGroup.InlineActivity.JMSQueueEventSource jmsQueueEventSource) {
        String connectionReference = jmsQueueEventSource.connectionReference();
        assert connectionReference != null : "JMS connection reference cannot be null";

        Resource.JMSSharedResource jmsResource = cx.getJMSResource(connectionReference);
        String listenerName = "jms" + ConversionUtils.sanitizes(jmsQueueEventSource.name()) + "Listener";

        return ResourceConvertor.convertJMSSharedResource(cx.projectContext, jmsQueueEventSource, jmsResource,
                listenerName, connectionReference);
    }

    private static BallerinaModel.@NotNull Service createHTTPServiceForStartActivity(ProcessContext cx,
                                                                                     ExplicitTransitionGroup group,
                                                                                     HttpEventSource startActivity) {
        BallerinaModel.Resource resource = generateResourceFunctionForHTTPStartActivity(cx, group);

        // Handle missing HTTP listener resource
        Optional<VariableReference> listenerOpt = cx.getProjectContext().httpListener(startActivity.sharedChannel());
        VariableReference listenerRef;
        if (listenerOpt.isEmpty()) {
                cx.log(LoggingUtils.Level.SEVERE,
                                "WARNING: Failed to find listener for " + startActivity.sharedChannel()
                                + ". Creating placeholder listener.");
                // Create a placeholder listener
                BallerinaModel.Listener placeholderListener = new BallerinaModel.Listener.HTTPListener(
                                "placeholder_listener", "8080", "0.0.0.0");
                cx.getProjectContext().addListnerDeclartion(startActivity.sharedChannel(), placeholderListener,
                                List.of(), List.of(Library.HTTP));
                listenerRef = new VariableReference("placeholder_listener");
        } else {
                listenerRef = listenerOpt.get();
        }

        return new BallerinaModel.Service("", listenerRef.varName(), List.of(resource));
    }

    private static BallerinaModel.@NotNull Resource generateResourceFunctionForHTTPStartActivity(
            ProcessContext cx, ExplicitTransitionGroup group) {
        List<Statement> body = new ArrayList<>();
        VarDeclStatment jobSharedVariables =
                new VarDeclStatment(new TypeDesc.MapTypeDesc(common.ConversionUtils.typeFrom("SharedVariableContext")),
                        "jobSharedVariables", exprFrom("{}"));
        body.add(jobSharedVariables);
        body.addAll(initJobSharedVariables(cx.projectContext, jobSharedVariables.ref()));
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
                exprFrom("{post: %s}".formatted(inputValDecl.varName())));
        body.add(paramXmlDecl);

        BallerinaModel.TypeDesc.FunctionTypeDesc processFnType = ConversionUtils.processFunctionType(cx);
        VarDeclStatment contextDecl = new VarDeclStatment(cx.contextType(), processFnType.parameters().get(0).name(),
                new FunctionCall(cx.getInitContextFn(), List.of(paramXmlDecl.ref(), jobSharedVariables.ref())));
        body.add(contextDecl);

        // TODO: it is better if we can reuse the same logic as Process6 where we use the intrinsic and return
        //  http response. But we need to refactor how deal with return bindings
        body.add(new Statement.CallStatement(
                new FunctionCall(cx.getProcessStartFunction().name(), List.of(contextDecl.ref()))));
        VarDeclStatment resultDecl =
                        new VarDeclStatment(XML, "response",
                                        ConversionUtils.getXMLResultFromContext(contextDecl.ref()));
        body.add(resultDecl);
        group.returnBindings().ifPresent(binding -> {
            ActivityConverter.XsltTransformResult transformResult = xsltTransform(cx, resultDecl.ref(), binding);
            if (!transformResult.nonStandardFunctions().isEmpty()) {
                        body.add(new Statement.Comment("WARNING: Non-standard XSLT functions detected: " +
                    String.join(", ", transformResult.nonStandardFunctions())));
            }
            body.add(new VarAssignStatement(resultDecl.ref(), transformResult.expression()));
        });
        body.add(new Return<>(resultDecl.ref()));
        return new BallerinaModel.Resource("'default[string... path]", "",
                List.of(parameter), Optional.of(XML), body);
    }

    static @NotNull BallerinaModel.TextDocument convertBody(ProcessContext cx, Process5 process,
                                                   TypeConversionResult result) {
        process.nameSpaces().forEach(cx::addNameSpace);
        List<BallerinaModel.Function> functions = cx.getAnalysisResult().activities().stream()
                .flatMap(activity -> ActivityConverter.convertActivity(cx, activity).stream())
                .collect(Collectors.toCollection(ArrayList::new));
        result.mainFn.ifPresent(functions::add);
        functions.addAll(convertExplicitTransitionGroup(cx, process.transitionGroup()));
        functions.add(generateProcessFunction(cx, process));
        AnalysisResult analysisResult = cx.getAnalysisResult();
        analysisResult.scopes(process).stream().map(scope -> generateControlFlowFunctionsForScope(cx, scope))
                .flatMap(Collection::stream).forEach(functions::add);

        functions.sort(Comparator.comparing(BallerinaModel.Function::functionName));
        return cx.serialize(result.service(), functions);
    }

    static @NotNull BallerinaModel.TextDocument convertBody(ProcessContext cx, Process6 process,
                                                   TypeConversionResult result) {
        process.variables().stream()
                .filter(each -> each instanceof Variable.PropertyVariable)
                .forEach(var -> cx.addResourceVariable((Variable.PropertyVariable) var));
        List<BallerinaModel.Function> functions = cx.getAnalysisResult().activities().stream()
                .flatMap(activity -> ActivityConverter.convertActivity(cx, activity).stream())
                .collect(Collectors.toCollection(ArrayList::new));
        functions.addAll(generateControlFlowFunctions(cx, process));
        cx.handledScopes.add(process.scope());
        AnalysisResult analysisResult = cx.getAnalysisResult();
        analysisResult.scopes(process).stream().map(scope -> generateControlFlowFunctionsForScope(cx, scope))
                .flatMap(Collection::stream).forEach(functions::add);

        functions.sort(Comparator.comparing(BallerinaModel.Function::functionName));
        process.nameSpaces().forEach(cx::addNameSpace);
        return cx.serialize(result.service(), functions);
    }

    private static List<BallerinaModel.Function> generateControlFlowFunctions(ProcessContext cx, Process6 process) {
        List<BallerinaModel.Function> functions = new ArrayList<>();
        try {
            addTransitionPredicates(cx, functions);
            functions.add(generateStartFunction(cx));
            functions.add(generateActivityFlowFunction(cx, process));
            functions.add(generateErrorFlowFunction(cx, process));
            functions.add(generateProcessFunction(cx, process));
        } catch (Exception e) {
            cx.registerControlFlowFunctionGenerationError(process, e);
        }
        return functions;
    }

    private static Collection<BallerinaModel.Function> convertExplicitTransitionGroup(
            ProcessContext cx, ExplicitTransitionGroup group) {
        return convertExplicitTransitionGroupInner(cx, group).collect(Collectors.toList());
    }

    private static Stream<BallerinaModel.Function> convertExplicitTransitionGroupInner(
            ProcessContext cx, ExplicitTransitionGroup group) {
        Stream<BallerinaModel.Function> childFunctions = group.activities().stream()
                .flatMap(each -> {
                    if (each instanceof ExplicitTransitionGroup.InlineActivityWithBody inlineActivityWithBody) {
                        return Stream.of(inlineActivityWithBody);
                    } else {
                        return Stream.empty();
                    }
                })
                .map(ExplicitTransitionGroup.InlineActivityWithBody::body)
                .flatMap(each -> convertExplicitTransitionGroupInner(cx, each));
        record FunctionGenerationData(BiFunction<ProcessContext, ExplicitTransitionGroup, BallerinaModel.Function> fn,
                                      String functionName) {

        }
        Stream<BallerinaModel.Function> functions = Stream.of(
                        new FunctionGenerationData(ProcessConverter::generateExplicitTransitionBlockErrorFunction,
                                "generateExplicitTransitionBlockErrorFunction"),
                        new FunctionGenerationData(ProcessConverter::generateExplicitTransitionBlockActivityFunction,
                                "generateExplicitTransitionBlockActivityFunction"),
                        new FunctionGenerationData(ProcessConverter::generateExplicitTransitionBlockScopeFunction,
                                "generateExplicitTransitionBlockScopeFunction"))
                .map(data -> tryGenerateFunction(data.fn, cx, group, data.functionName))
                .flatMap(Optional::stream);
        return Stream.concat(functions, childFunctions);
    }

private static Optional<BallerinaModel.Function> tryGenerateFunction(
                BiFunction<ProcessContext, ExplicitTransitionGroup, BallerinaModel.Function> fn,
                ProcessContext cx, ExplicitTransitionGroup group, String functionName) {
        try {
            return Optional.ofNullable(fn.apply(cx, group));
        } catch (Exception e) {
            cx.log(LoggingUtils.Level.SEVERE, "Exception in " + functionName + ": " + e.getMessage());
            return Optional.empty();
        }
    }

    private static void addTransitionPredicates(ProcessContext cx, List<BallerinaModel.Function> accum) {
        cx.getAnalysisResult().activities().stream()
                .filter(each -> each instanceof Activity.ActivityWithSources)
                .forEach(activity -> addTransitionPredicates(cx,
                        (Activity.ActivityWithSources) activity, accum));
    }

    private static void addTransitionPredicates(ProcessContext cx, Activity.ActivityWithSources activity,
            List<BallerinaModel.Function> accum) {
        try {
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
        } catch (Exception e) {
            cx.registerTransitionPredicateError(activity, e);
        }
    }

    private static BallerinaModel.Function getTransitionPredicateFn(ProcessContext cx,
            Activity.Source.Predicate predicate,
            Expression expr) {
        return new BallerinaModel.Function(cx.predicateFunction(predicate),
                List.of(new Parameter("input", XML), new Parameter("cx", new TypeDesc.MapTypeDesc(XML))),
                BOOLEAN, List.of(new Return<>(expr)));
    }

    static @NotNull TypeConversionResult convertTypes(ProcessContext cx, Process6 process) {
        List<BallerinaModel.Service> services = process.types().stream()
                .filter(type -> type instanceof Type.WSDLDefinition)
                .map(type -> (Type.WSDLDefinition) type)
                .flatMap(wsdlDefinition -> TypeConverter.convertWsdlDefinition(cx, wsdlDefinition)
                        .stream())
                .collect(Collectors.toList());
        cx.allocatedDefaultClient(services);
        return new TypeConversionResult(services);
    }

    @NotNull
    static BallerinaModel.Function createMainFunction(ProcessContext cx, Process5 process) {
        List<Statement> body = new ArrayList<>();
        VarDeclStatment jobSharedVariables =
                new VarDeclStatment(new TypeDesc.MapTypeDesc(common.ConversionUtils.typeFrom("SharedVariableContext")),
                        "jobSharedVariables", exprFrom("{}"));
        body.add(jobSharedVariables);
        body.addAll(initJobSharedVariables(cx.projectContext, jobSharedVariables.ref()));
        VarDeclStatment paramXmlDecl = new VarDeclStatment(new TypeDesc.MapTypeDesc(XML), "paramXML",
                exprFrom("{}"));
        body.add(paramXmlDecl);

        BallerinaModel.TypeDesc.FunctionTypeDesc processFnType = ConversionUtils.processFunctionType(cx);
        VarDeclStatment contextDecl = new VarDeclStatment(cx.contextType(), processFnType.parameters().get(0).name(),
                new FunctionCall(cx.getInitContextFn(), List.of(paramXmlDecl.ref(), jobSharedVariables.ref())));
        body.add(contextDecl);

        body.add(new Statement.CallStatement(
                new FunctionCall(cx.getProcessStartFunction().name(), List.of(contextDecl.ref()))));
        return new BallerinaModel.Function(Optional.of("public"), "main", List.of(), Optional.empty(),
                new BallerinaModel.BlockFunctionBody(body));
    }

    record TypeConversionResult(Collection<BallerinaModel.Service> service, Optional<BallerinaModel.Function> mainFn) {

        TypeConversionResult(Collection<BallerinaModel.Service> service) {
            this(service, Optional.empty());
        }

        public static @NotNull TypeConversionResult empty() {
            return new TypeConversionResult(List.of(), Optional.empty());
        }

    }

    private static BallerinaModel.Function generateProcessFunction(ProcessContext cx, Process5 process) {
        TypeDesc.FunctionTypeDesc scopeFnType = ConversionUtils.scopeFnType(cx);
        List<Parameter> parameters = scopeFnType.parameters();
        assert parameters.size() == 1 : "Scope function type should have exactly one parameter";

        AnalysisResult.ControlFlowFunctions controlFn =
                cx.getAnalysisResult().getControlFlowFunctions(process.transitionGroup());
        String scopeFn = controlFn.scopeFn();
        List<Statement> body = List.of(new Return<>(new FunctionCall(scopeFn, List.of(parameters.getFirst().ref()))));
        return new BallerinaModel.Function(cx.getProcessStartFunction().name(), parameters, scopeFnType.returnType(),
                body);
    }


    private static BallerinaModel.Function generateExplicitTransitionBlockActivityFunction(
            ProcessContext cx, ExplicitTransitionGroup group) {
        AnalysisResult analysisResult = cx.getAnalysisResult();
        List<Activity> activities = analysisResult.sortedActivities(group).toList();
        String activityRunnerFunction = analysisResult.getControlFlowFunctions(group).activityRunner();
        TypeDesc.FunctionTypeDesc activityFnType = ConversionUtils.activityFnType(cx);
        List<Parameter> parameters = activityFnType.parameters();
        assert parameters.size() == 1 : "Activity function type should have exactly one parameter";

        return generateActivityFlowFunctionInner(cx, activities, activityRunnerFunction,
                activityFnType.returnType(), Check::new, parameters);
    }


    private static BallerinaModel.Function generateExplicitTransitionBlockScopeFunction(
            ProcessContext cx, ExplicitTransitionGroup group) {
        AnalysisResult analysisResult = cx.getAnalysisResult();
        AnalysisResult.ControlFlowFunctions controlFlowFunctions = analysisResult.getControlFlowFunctions(group);
        return generateScopeFunctionInner(cx, controlFlowFunctions);
    }

    // TODO: refactor common code with generateErrorFlowFunction
    private static BallerinaModel.Function generateExplicitTransitionBlockErrorFunction(
            ProcessContext cx, ExplicitTransitionGroup group) {
        AnalysisResult analysisResult = cx.getAnalysisResult();
        String errorHandlerFunction = analysisResult.getControlFlowFunctions(group).errorHandler();
        List<Activity> activities = analysisResult.sortedErrorHandlerActivities(group).toList();
        if (activities.isEmpty()) {
            return defaultErrorHandlerFunction(cx, errorHandlerFunction);
        }
        TypeDesc.FunctionTypeDesc errorFlowFnType = ConversionUtils.errorFlowFnType(cx);
        return generateActivityFlowFunctionInner(cx, activities, errorHandlerFunction,
                errorFlowFnType.returnType(), CheckPanic::new, errorFlowFnType.parameters());
    }

    private static BallerinaModel.@NotNull Function defaultErrorHandlerFunction(ProcessContext cx,
                                                                                String errorHandlerFunction) {
        TypeDesc.FunctionTypeDesc errorFlowFnType = ConversionUtils.errorFlowFnType(cx);
        List<Parameter> parameters = errorFlowFnType.parameters();
        List<Statement> body = List.of(stmtFrom(new Panic(new VariableReference("err")) + ";\n"));
        return new BallerinaModel.Function(errorHandlerFunction, parameters, errorFlowFnType.returnType(), body);
    }

    private static BallerinaModel.Function generateStartFunction(ProcessContext cx) {
        List<Statement> body = new ArrayList<>();
        ProjectContext.FunctionData startFuncData = cx.getProcessStartFunction();

        VariableReference params = ProcessContext.processLevelFnParamVariable();
        Process process = cx.process;

        AnalysisResult.ControlFlowFunctions controlFlowFunctions =
                switch (process) {
                    case Process5 process5 -> cx.getAnalysisResult().getControlFlowFunctions(
                            process5.transitionGroup());
                    case Process6 process6 -> cx.getAnalysisResult().getControlFlowFunctions(
                            process6.scope());
                };
        body.add(new Statement.CallStatement(new FunctionCall(controlFlowFunctions.scopeFn(), List.of(params))));

        return new BallerinaModel.Function(startFuncData.name(),
                List.of(new Parameter(params.varName(), cx.contextType())), NIL, body);
    }

    private static BallerinaModel.Function generateProcessFunction(ProcessContext cx, Process6 process) {

        AnalysisResult.ControlFlowFunctions controlFlowFunctions =
                cx.getAnalysisResult().getControlFlowFunctions(
                        process.scope());
        String name = controlFlowFunctions.scopeFn();
        List<Statement> body = new ArrayList<>();
        TypeDesc.FunctionTypeDesc processFunctionType = ConversionUtils.processFunctionType(cx);
        List<Parameter> parameters = processFunctionType.parameters();
        assert parameters.size() == 1 : "Process function type should have exactly one parameter";

        generateScopeFnBody(controlFlowFunctions, parameters.getFirst().ref(), body);
        return new BallerinaModel.Function(name, parameters, processFunctionType.returnType(), body);
    }

    private static Collection<BallerinaModel.Function> generateControlFlowFunctionsForScope(
            ProcessContext cx, Scope scope) {
        if (cx.handledScopes.contains(scope)) {
            return List.of();
        }
        cx.handledScopes.add(scope);
        try {
            BallerinaModel.Function activityFlowFn = generateActivityFlowFunction(cx, scope);
            BallerinaModel.Function errorFlowFn = generateErrorFlowFunction(cx, scope);
            BallerinaModel.Function scopeFn = generateInnerScopeFunction(cx, scope);
            return List.of(activityFlowFn, errorFlowFn, scopeFn);
        } catch (Exception ex) {
            cx.registerControlFlowFunctionGenerationError(scope, ex);
            return List.of();
        }
    }

    private static BallerinaModel.Function generateInnerScopeFunction(ProcessContext cx, Scope scope) {
        AnalysisResult analysisResult = cx.getAnalysisResult();
        AnalysisResult.ControlFlowFunctions controlFlowFunctions = analysisResult.getControlFlowFunctions(scope);
        return generateScopeFunctionInner(cx, controlFlowFunctions);
    }

    private static BallerinaModel.@NotNull Function generateScopeFunctionInner(
            ProcessContext cx, AnalysisResult.ControlFlowFunctions controlFlowFunctions) {
        String name = controlFlowFunctions.scopeFn();
        List<Statement> body = new ArrayList<>();
        TypeDesc.FunctionTypeDesc scopeFnType = ConversionUtils.scopeFnType(cx);
        List<Parameter> parameters = scopeFnType.parameters();
        assert parameters.size() == 1 : "Scope function type should have exactly one parameter";

        generateScopeFnBody(controlFlowFunctions, parameters.getFirst().ref(), body);
        return new BallerinaModel.Function(name, parameters, scopeFnType.returnType(), body);
    }

    private static void generateScopeFnBody(AnalysisResult.ControlFlowFunctions controlFlowFunctions,
            VariableReference context, List<Statement> body) {
        VarDeclStatment result = new VarDeclStatment(UnionTypeDesc.of(NIL, ERROR), "result",
                new FunctionCall(controlFlowFunctions.activityRunner(), List.of(context)));
        body.add(result);
        handleErrorResult(result, context, controlFlowFunctions.errorHandler(), body);
    }

    private static void handleErrorResult(VarDeclStatment result, VariableReference context,
            String errorHandlerFn, List<Statement> body) {
        body.add(
                new Statement.IfElseStatement(
                        new TypeCheckExpression(result.ref(), ERROR),
                        List.of(new Statement.CallStatement(
                                new FunctionCall(errorHandlerFn, List.of(result.ref(), context)))),
                        List.of(), List.of()));
    }

    private static BallerinaModel.Function generateActivityFlowFunction(ProcessContext cx,
                                                                        Process6 process) {
        return generateActivityFlowFunction(cx, process.scope());
    }

    private static BallerinaModel.Function generateActivityFlowFunction(ProcessContext cx, Scope scope) {
        AnalysisResult analysisResult = cx.getAnalysisResult();
        List<Activity> activities = analysisResult.sortedActivities(scope).toList();
        String activityRunnerFunction = analysisResult.getControlFlowFunctions(scope).activityRunner();
        TypeDesc.FunctionTypeDesc activityFnType = ConversionUtils.activityFnType(cx);
        List<Parameter> parameters = activityFnType.parameters();
        assert parameters.size() == 1 : "Activity function type should have exactly one parameter";

        return generateActivityFlowFunctionInner(cx, activities, activityRunnerFunction,
                activityFnType.returnType(), Check::new, parameters);
    }

    private static BallerinaModel.@NotNull Function generateActivityFlowFunctionInner(
            ProcessContext cx, List<Activity> activities, String functionName, TypeDesc returnType,
            Function<FunctionCall, Expression> callHandler, List<Parameter> parameters) {
        List<Statement> body = new ArrayList<>();
        generateActivityFlowFunctionInner(cx, activities, callHandler, body, new VariableReference("input"));
        return new BallerinaModel.Function(functionName, parameters, returnType, body);
    }

    private static BallerinaModel.Function generateErrorFlowFunction(ProcessContext cx, Process6 process) {
        return generateErrorFlowFunction(cx, process.scope());
    }

    private static BallerinaModel.@NotNull Function generateErrorFlowFunction(ProcessContext cx,
                                                                              Scope scope) {
        Parameter context = new Parameter("cx", new TypeDesc.MapTypeDesc(XML));
        AnalysisResult analysisResult = cx.getAnalysisResult();
        Collection<Scope.FaultHandler> faultHandlers = scope.faultHandlers();
        List<Statement> body = new ArrayList<>();
        int resultCount = 0;
        if (faultHandlers.isEmpty()) {
            body.add(stmtFrom(new Panic(new VariableReference("err")) + ";\n"));
        } else {
            VariableReference finalResult = null;
            for (Scope.FaultHandler each : faultHandlers) {
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
        TypeDesc.FunctionTypeDesc errorFlowFnType = ConversionUtils.errorFlowFnType(cx);
        return new BallerinaModel.Function(errorHandlerFunction,
                errorFlowFnType.parameters(), errorFlowFnType.returnType(), body);
    }

    private static void generateActivityFlowFunctionInner(
            ProcessContext cx, List<Activity> activities, Function<FunctionCall, Expression> callHandler,
            List<Statement> body, VariableReference input) {
        VariableReference context = new VariableReference("cx");
        Map<Activity, VariableReference> activityResult = new HashMap<>();
        for (Activity activity : activities) {
            generateActivityFunctionCall(cx, activityResult, activity, callHandler, body, input, context);
        }
    }

    private static void generateActivityFunctionCall(
            ProcessContext cx, Map<Activity, VariableReference> activityResults, Activity activity,
            Function<FunctionCall, Expression> callHandler, List<Statement> body, VariableReference input,
            VariableReference context) {
        AnalysisResult analysisResult = cx.getAnalysisResult();
        record TransitionFunctionData(VariableReference inputVar, String functionName) {

        }
        List<FunctionCall> predicates = analysisResult.transitionConditions(activity)
                .map(data -> new TransitionFunctionData(activityResults.get(data.activity()),
                        cx.predicateFunction(data.predicate())))
                .map(data -> new FunctionCall(data.functionName, List.of(data.inputVar, context)))
                .toList();

        if (predicates.isEmpty()) {
            body.add(new Statement.CallStatement(callHandler.apply(
                    activityFunctionCall(activity, context, analysisResult))));
            return;
        }

        Expression cond = predicates.getFirst();
        for (int i = 1; i < predicates.size(); i++) {
            cond = new BinaryLogical(cond, predicates.get(i), BinaryLogical.Operator.OR);
        }
        Statement.IfElseStatement ifElse = new Statement.IfElseStatement(cond,
                List.of(new Statement.CallStatement(new Check(
                        activityFunctionCall(activity, context, analysisResult)))),
                List.of(), List.of());
        body.add(ifElse);
    }

    private static @NotNull FunctionCall activityFunctionCall(Activity activity, VariableReference context,
            AnalysisResult analysisResult) {
        return new FunctionCall(analysisResult.from(activity).functionName(), List.of(context));
    }

    private static ActivityConverter.XsltTransformResult xsltTransform(ProcessContext cx,
                                                                       VariableReference inputVariable,
                                                                       Activity.Expression.XSLT xslt) {
        cx.addLibraryImport(Library.XSLT);
        String xsltContent = xslt.expression();

        // Detect non-standard functions (Tibco-specific functions)
        List<String> nonStandardFunctions = ActivityConverter.detectNonStandardFunctions(xsltContent);

        Expression expression = new CheckPanic(new FunctionCall(ActivityConverter.XSLTConstants.XSLT_TRANSFORM_FUNCTION,
                List.of(inputVariable, new XMLTemplate(xsltContent))));

        return new ActivityConverter.XsltTransformResult(expression, nonStandardFunctions);
    }

    private static List<Statement> initJobSharedVariables(ProjectContext projectContext,
                                                          VariableReference jobSharedVariables) {
        List<Statement> statements = new ArrayList<>();

        projectContext.getJobSharedVariables().forEach(jobSharedVariable -> {
            String varName = ConversionUtils.sanitizes(jobSharedVariable.name());
            VarDeclStatment varDecl = new VarDeclStatment(XML, varName,
                    new XMLTemplate(jobSharedVariable.initialValue()));
            statements.add(varDecl);

            String contextVarName = varName + "Context";
            BallerinaModel.Expression getterFunction = new BallerinaModel.Expression.BallerinaExpression(
                    "function() returns xml { return " + varName + "; }");
            BallerinaModel.Expression setterFunction = new BallerinaModel.Expression.BallerinaExpression(
                    "function(xml value) { " + varName + " = value; }");
            BallerinaModel.Expression contextExpr = new BallerinaModel.Expression.BallerinaExpression(
                    "{ getter: " + getterFunction + ", setter: " + setterFunction + " }");

            VarDeclStatment contextDecl = new VarDeclStatment(
                    new BallerinaModel.TypeDesc.BallerinaType("SharedVariableContext"),
                    contextVarName, contextExpr);
            statements.add(contextDecl);

            BallerinaModel.Expression mapAssignment = new BallerinaModel.Expression.BallerinaExpression(
                    jobSharedVariables.varName() + "[\"" + jobSharedVariable.name() + "\"] = " + contextVarName);
            statements.add(new Statement.BallerinaStatement(mapAssignment + ";"));
        });

        return statements;
    }
}
