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
import ballerina.BallerinaModel.Expression.StringConstant;
import ballerina.BallerinaModel.Statement.CallStatement;
import ballerina.BallerinaModel.Statement.PanicStatement;
import converter.tibco.analyzer.AnalysisResult;
import org.jetbrains.annotations.NotNull;
import tibco.TibcoModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ballerina.BallerinaModel.TypeDesc.BuiltinType.ERROR;
import static ballerina.BallerinaModel.TypeDesc.BuiltinType.XML;

import ballerina.BallerinaModel.Expression.VariableReference;
import ballerina.BallerinaModel.Statement.NamedWorkerDecl;
import ballerina.BallerinaModel.Statement.Return;
import ballerina.BallerinaModel.Statement.VarDeclStatment;

public class ProcessConverter {

    private ProcessConverter() {
    }

    static BallerinaModel.Module convertProcesses(TibcoToBalConverter.ProjectConversionContext conversionContext,
                                                  Collection<TibcoModel.Process> processes,
                                                  Collection<TibcoModel.Type.Schema> types) {
        ProjectContext cx = new ProjectContext(conversionContext);
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

    static void convertTypes(ProjectContext cx, Collection<TibcoModel.Type.Schema> schemas) {
        ContextWithFile typeContext = cx.getTypeContext();
        schemas.forEach(schema -> TypeConverter.convertSchema(typeContext, schema));
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
        List<BallerinaModel.Function> functions = cx.analysisResult.activities().stream()
                .map(activity -> ActivityConverter.convertActivity(cx, activity))
                .collect(Collectors.toCollection(ArrayList::new));
        if (process.scope().isPresent()) {
            functions.add(generateStartFunction(cx));
            functions.add(generateProcessFunction(cx, process));
        }

        functions.sort(Comparator.comparing(BallerinaModel.Function::functionName));

        return cx.serialize(result.service(), functions);
    }

    private static TypeConversionResult convertTypes(ProcessContext cx, TibcoModel.Process process) {
        List<BallerinaModel.Service> services = new ArrayList<>();
        for (TibcoModel.Type type : process.types()) {
            switch (type) {
                case TibcoModel.Type.Schema schema -> TypeConverter.convertSchema(cx, schema);
                case TibcoModel.Type.WSDLDefinition wsdlDefinition ->
                        services.addAll(TypeConverter.convertWsdlDefinition(cx, wsdlDefinition));
            }
        }
        return new TypeConversionResult(services);
    }

    private record TypeConversionResult(Collection<BallerinaModel.Service> service) {

    }

    private static String addTerminalWorkerResultCombinationStatements(
            ProcessContext cx, List<BallerinaModel.Statement> stmts,
            Collection<TibcoModel.Scope.Flow.Activity> endActivities) {
        VarDeclStatment inputVarDecl = createAlternateReceiveFromActivities(cx, "result",
                endActivities.stream());
        stmts.add(inputVarDecl);
        VarDeclStatment cleanInputVarDecl = new VarDeclStatment(XML,
                "result_clean",
                new BallerinaModel.Expression.TernaryExpression(
                        new BallerinaModel.Expression.TypeCheckExpression(inputVarDecl.ref(),
                                ERROR),
                        new BallerinaModel.Expression.XMLTemplate(""), inputVarDecl.ref()));
        stmts.add(cleanInputVarDecl);
        return cleanInputVarDecl.varName();
    }

    private static VarDeclStatment createAlternateReceiveFromWorkers(Stream<String> workers,
                                                                     String varName) {
        return receiveVarFromPeer(workers.sorted().collect(Collectors.joining(" | ")), varName);
    }

    private static VarDeclStatment createAlternateReceiveFromActivities(
            ProcessContext cx,
            String varName,
            Stream<TibcoModel.Scope.Flow.Activity> activities) {
        var analysisResult = cx.analysisResult;
        return createAlternateReceiveFromWorkers(cx,
                activities.map(analysisResult::from).map(AnalysisResult.ActivityData::workerName),
                varName);
    }

    private static VarDeclStatment createAlternateReceiveFromWorkers(ProcessContext cx,
                                                                     Stream<String> workers,
                                                                     String varName) {
        AnalysisResult analysisResult = cx.analysisResult;
        return receiveVarFromPeer(analysisResult.sortWorkers(workers).collect(Collectors.joining(" | ")),
                varName);
    }

    private static Optional<BallerinaModel.Statement> generateActivityWorkers(ProcessContext cx,
                                                                              TibcoModel.Scope.Flow.Activity activity) {

        AnalysisResult analysisResult = cx.analysisResult;
        Collection<TibcoModel.Scope.Flow.Link> sources = analysisResult.sources(activity);
        if (sources.isEmpty()) {
            return Optional.empty();
        }

        List<BallerinaModel.Statement> body = new ArrayList<>();
        VarDeclStatment inputDecl = createAlternateReceiveFromWorkers(cx,
                sources.stream().map(analysisResult::from).map(AnalysisResult.LinkData::workerName),
                "inputVal");
        body.add(inputDecl);
        handleNoMessage(cx, inputDecl.ref(), body);
        BallerinaModel.Expression.FunctionCall callExpr = genereateActivityFunctionCall(cx, activity,
                inputDecl.ref());

        String workerName = analysisResult.from(activity).workerName();
        BallerinaModel.Expression.VariableReference output = handleErrorValueFromActivity(cx, workerName,
                "output", body, callExpr);
        addTransitionsToDestination(cx, body, activity, output);
        if (analysisResult.destinations(activity).isEmpty()) {
            BallerinaModel.Action.WorkerSendAction sendAction = new BallerinaModel.Action.WorkerSendAction(
                    output, "function");
            body.add(new BallerinaModel.BallerinaStatement(sendAction + ";"));
        }
        return Optional.of(new NamedWorkerDecl(workerName, body));
    }

    private static void handleNoMessage(ProcessContext cx, BallerinaModel.Expression.VariableReference input,
                                        List<BallerinaModel.Statement> body) {
        BallerinaModel.TypeDesc.TypeReference noMessageTd = new BallerinaModel.TypeDesc.TypeReference(
                "error:NoMessage");
        BallerinaModel.Expression.TypeCheckExpression typeCheck = new BallerinaModel.Expression.TypeCheckExpression(
                input, noMessageTd);
        BallerinaModel.IfElseStatement ifElse = new BallerinaModel.IfElseStatement(typeCheck,
                List.of(new Return<>()), List.of(), List.of());
        body.add(ifElse);
    }

    private static FunctionCall genereateActivityFunctionCall(
            ProcessContext cx, TibcoModel.Scope.Flow.Activity activity,
            VariableReference inputVar) {
        AnalysisResult analysisResult = cx.analysisResult;
        String activityFunction = analysisResult.from(activity).functionName();
        return new FunctionCall(activityFunction,
                new BallerinaModel.Expression[]{inputVar, cx.getContextRef()});
    }

    private static BallerinaModel.Statement generateWorkerForErrorHandler(ProcessContext cx,
                                                                          TibcoModel.Process process) {
        List<BallerinaModel.Statement> body = new ArrayList<>();
        String receiver = String.join(" | ", cx.workersWithErrorTransitions());

        VarDeclStatment resultDecl = receiveVarFromPeer(receiver, "result", ERROR);
        body.add(resultDecl);

        Collection<TibcoModel.Scope.Flow.Activity> startActivities = cx.analysisResult
                .faultHandlerStartActivities(process);
        if (startActivities.isEmpty()) {
            body.add(new PanicStatement(resultDecl.ref()));
        } else {
            VarDeclStatment errorXML = new VarDeclStatment(XML, "errorXML",
                    new BallerinaModel.Expression.XMLTemplate(
                            "<error>${" + resultDecl.varName() + ".message()}</error>"));
            body.add(errorXML);

            startActivities
                    .forEach(each -> addTransitionsToDestination(cx, body, each, errorXML.ref()));
        }
        return new NamedWorkerDecl(cx.errorHandlerWorkerName(), body);
    }

    private static BallerinaModel.Statement generateWorkerForStartActions(ProcessContext cx,
                                                                          TibcoModel.Process process) {
        Collection<TibcoModel.Scope.Flow.Activity> startActivities = cx.analysisResult.startActivities(process);
        cx.startWorkerName = "start_worker";
        List<BallerinaModel.Statement> body = new ArrayList<>();
        int index = 0;
        for (TibcoModel.Scope.Flow.Activity startActivity : startActivities) {
            generateWorkerForStartActionsInner(cx, startActivity, index, body);
            index++;
        }
        return new NamedWorkerDecl(cx.startWorkerName, body);
    }

    private static void generateWorkerForStartActionsInner(ProcessContext cx,
                                                           TibcoModel.Scope.Flow.Activity startActivity,
                                                           int index, List<BallerinaModel.Statement> body) {
        BallerinaModel.Expression.FunctionCall callExpr = genereateActivityFunctionCall(cx, startActivity,
                new BallerinaModel.Expression.VariableReference("input"));
        var result = handleErrorValueFromActivity(cx, cx.startWorkerName, "result" + index, body, callExpr);
        addTransitionsToDestination(cx, body, startActivity, result);
    }

    private static BallerinaModel.Expression.VariableReference handleErrorValueFromActivity(
            ProcessContext cx,
            String workerName,
            String resultName,
            List<BallerinaModel.Statement> body,
            BallerinaModel.Expression.FunctionCall callExpr) {
        cx.markAsErrorTransition(workerName);
        VarDeclStatment resultDecl = new VarDeclStatment(ActivityContext.returnType(), resultName, callExpr);
        body.add(resultDecl);
        BallerinaModel.Expression.VariableReference result = resultDecl.ref();
        BallerinaModel.Expression.TypeCheckExpression typeCheck = new BallerinaModel.Expression.TypeCheckExpression(
                result, ERROR);
        BallerinaModel.IfElseStatement ifElse = new BallerinaModel.IfElseStatement(typeCheck,
                List.of(
                        new BallerinaModel.BallerinaStatement(
                                new BallerinaModel.Action.WorkerSendAction(result,
                                        cx.errorHandlerWorkerName()) + ";"),
                        new Return<>()),
                List.of(), List.of());
        body.add(ifElse);
        return result;
    }

    private static void addTransitionsToDestination(ProcessContext cx, List<BallerinaModel.Statement> body,
                                                    TibcoModel.Scope.Flow.Activity activity,
                                                    BallerinaModel.Expression.VariableReference value) {
        AnalysisResult analysisResult = cx.analysisResult;
        List<AnalysisResult.TransitionData> destinations = analysisResult.destinations(activity);
        for (int i = 0; i < destinations.size(); i++) {
            var destination = destinations.get(i);
            if (destination.predicate().isEmpty()) {
                body.add(generateSendToWorker(cx, destination.target(), value.varName()));
                continue;
            }
            BallerinaModel.Expression.FunctionCall predicateTestCall = getPredicateCall(cx, value,
                    destination);
            boolean hasElse = i < destinations.size() - 1 && destinations.get(i + 1).predicate().stream()
                    .anyMatch(p -> p instanceof TibcoModel.Scope.Flow.Activity.Source.Predicate.Else);
            if (!hasElse) {
                body.add(new BallerinaModel.IfElseStatement(predicateTestCall,
                        List.of(generateSendToWorker(cx, destination.target(),
                                value.varName())),
                        List.of(),
                        List.of()));
            } else {
                var elseDest = destinations.get(++i);
                body.add(new BallerinaModel.IfElseStatement(predicateTestCall,
                        List.of(generateSendToWorker(cx, destination.target(),
                                value.varName())),
                        List.of(),
                        List.of(generateSendToWorker(cx, elseDest.target(), value.varName()))));
            }
        }
    }

    private static BallerinaModel.Expression.@NotNull FunctionCall getPredicateCall(
            ProcessContext cx,
            BallerinaModel.Expression.VariableReference value,
            AnalysisResult.TransitionData destination) {
        assert destination.predicate().isPresent() : "Should only be called if we have a predicate";
        TibcoModel.Scope.Flow.Activity.Source.Predicate predicate = destination.predicate().get();
        if (!(predicate instanceof TibcoModel.Scope.Flow.Activity.Expression.XPath(String expression))) {
            throw new UnsupportedOperationException("Only XPath predicates are supported");
        }
        String predicateTestFn = cx.getPredicateTestFunction();
        BallerinaModel.Expression.StringConstant xPathExpr = new BallerinaModel.Expression.StringConstant(
                ConversionUtils.escapeString(expression));
        return new BallerinaModel.Expression.FunctionCall(predicateTestFn, List.of(value, xPathExpr));
    }

    private static BallerinaModel.@NotNull BallerinaStatement generateSendToWorker(
            ProcessContext cx,
            TibcoModel.Scope.Flow.Link destinationLink,
            String variable) {
        AnalysisResult analysisResult = cx.analysisResult;
        AnalysisResult.LinkData linkData = analysisResult.from(destinationLink);
        BallerinaModel.Action.WorkerSendAction sendAction = new BallerinaModel.Action.WorkerSendAction(
                new VariableReference(variable), linkData.workerName());
        // FIXME:
        return new BallerinaModel.BallerinaStatement(sendAction + ";");
    }

    private static BallerinaModel.Statement generateLink(ProcessContext cx,
                                                         TibcoModel.Scope.Flow.Link link) {
        List<BallerinaModel.Statement> body = new ArrayList<>();
        var analysisResult = cx.analysisResult;
        Collection<TibcoModel.Scope.Flow.Activity> inputActivities = analysisResult.sources(link);
        VarDeclStatment input = createAlternateReceiveFromWorkers(
                cx, inputActivities.stream()
                        .map(each -> sourceWorker(cx, each)),
                "input");
        body.add(input);
        handleNoMessage(cx, input.ref(), body);
        AnalysisResult.LinkData linkData = analysisResult.from(link);
        for (TibcoModel.Scope.Flow.Activity destinations : analysisResult.destinations(link)) {
            AnalysisResult.ActivityData activityData = analysisResult.from(destinations);
            String activityWorker = activityData.workerName();
            BallerinaModel.Action.WorkerSendAction sendAction = new BallerinaModel.Action.WorkerSendAction(
                    input.ref(), activityWorker);
            body.add(new BallerinaModel.BallerinaStatement(sendAction + ";"));
        }
        return new NamedWorkerDecl(linkData.workerName(), body);
    }

    private static String sourceWorker(ProcessContext cx, TibcoModel.Scope.Flow.Activity activity) {
        var analysisResult = cx.analysisResult;
        if (analysisResult.startActivities(cx.process).contains(activity)) {
            return cx.startWorkerName;
        }
        if (analysisResult.faultHandlerStartActivities(cx.process).contains(activity)) {
            return cx.errorHandlerWorkerName();
        }
        return analysisResult.from(activity).workerName();
    }

    private static BallerinaModel.Expression.VariableReference addReceiveFromPeerStatement(
            String peer,
            String inputVarName,
            List<BallerinaModel.Statement> body) {
        VarDeclStatment inputVarDecl = receiveVarFromPeer(peer, inputVarName);
        body.add(inputVarDecl);
        return inputVarDecl.ref();
    }

    private static VarDeclStatment receiveVarFromPeer(String peer, String inputVarName) {
        BallerinaModel.TypeDesc.UnionTypeDesc receiveType = new BallerinaModel.TypeDesc.UnionTypeDesc(List.of(
                new BallerinaModel.TypeDesc.TypeReference("error:NoMessage"), XML));
        return receiveVarFromPeer(peer, inputVarName, receiveType);
    }

    private static VarDeclStatment receiveVarFromPeer(String peer, String inputVarName,
                                                      BallerinaModel.TypeDesc receiveType) {
        BallerinaModel.Action.WorkerReceiveAction receiveEvent = new BallerinaModel.Action.WorkerReceiveAction(
                peer);
        return new VarDeclStatment(receiveType, inputVarName, receiveEvent);
    }

    private static BallerinaModel.Function generateStartFunction(ProcessContext cx) {

        List<BallerinaModel.Statement> body = new ArrayList<>();
        var startFuncData = cx.getProcessStartFunction();
        String inputVariable = "input";
        FunctionCall toXMLCall = new FunctionCall(
                cx.getToXmlFunction(), new String[]{inputVariable});
        String inputXML = "inputXML";
        VarDeclStatment inputXMLVar = new VarDeclStatment(XML, inputXML,
                new BallerinaModel.Expression.CheckPanic(toXMLCall));
        body.add(inputXMLVar);

        String processFunction = cx.getProcessFunction();
        VarDeclStatment xmlResult = new VarDeclStatment(XML, "xmlResult",
                new FunctionCall(processFunction, new String[]{inputXML}));
        body.add(xmlResult);

        BallerinaModel.TypeDesc returnType = startFuncData.returnType();
        String convertToTypeFunction = cx.getConvertToTypeFunction(returnType);
        VarDeclStatment result = new VarDeclStatment(returnType, "result",
                new FunctionCall(convertToTypeFunction, new String[]{"xmlResult"}));
        body.add(result);

        Return<VariableReference> returnStatement = new Return<>(Optional.of(new VariableReference("result")));
        body.add(returnStatement);

        BallerinaModel.TypeDesc inputType = startFuncData.inputType();
        return new BallerinaModel.Function(startFuncData.name(),
                List.of(new BallerinaModel.Parameter(inputVariable, inputType)),
                Optional.of(returnType.toString()),
                body);
    }

    private static BallerinaModel.Function generateProcessFunction(ProcessContext cx,
                                                                   TibcoModel.Process process) {
        String name = cx.getProcessFunction();
        AnalysisResult analysisResult = cx.analysisResult;
        List<BallerinaModel.Statement> body = new ArrayList<>();
        body.add(cx.initContextVar());
        String addToContextFn = cx.getAddToContextFn();
        body.add(new CallStatement(new FunctionCall(addToContextFn, List.of(cx.contextVarRef(),
                new StringConstant("post.item"),
                new VariableReference("input")))));
        body.add(generateWorkerForStartActions(cx, process));
        analysisResult.links().stream()
                .sorted(Comparator.comparing(link -> analysisResult.from(link).workerName()))
                .map(link -> generateLink(cx, link)).forEach(body::add);
        analysisResult.activities().stream()
                .sorted(Comparator.comparing(activity -> analysisResult.from(activity).workerName()))
                .map(activity -> generateActivityWorkers(cx, activity))
                .filter(Optional::isPresent).map(Optional::get).forEach(body::add);
        body.add(generateWorkerForErrorHandler(cx, process));
        String resultVariableName = addTerminalWorkerResultCombinationStatements(cx, body,
                analysisResult.endActivities(process));
        body.add(new Return<>(new VariableReference(resultVariableName)));
        return new BallerinaModel.Function(name, List.of(new BallerinaModel.Parameter("input", XML)),
                Optional.of(XML.toString()), body);
    }

}
