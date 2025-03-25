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
import converter.tibco.analyzer.AnalysisResult;
import org.jetbrains.annotations.NotNull;
import tibco.TibcoModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ballerina.BallerinaModel.TypeDesc.BuiltinType.XML;

import ballerina.BallerinaModel.Expression.VariableReference;
import ballerina.BallerinaModel.Statement.NamedWorkerDecl;
import ballerina.BallerinaModel.Statement.Return;
import ballerina.BallerinaModel.Statement.VarDeclStatment;

public class ProcessConverter {

    private ProcessConverter() {
    }

    static BallerinaModel.Module convertProcesses(Collection<TibcoModel.Process> processes,
            Collection<TibcoModel.Type.Schema> types) {
        ProjectContext cx = new ProjectContext();
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
        List<BallerinaModel.ModuleTypeDef> moduleTypeDefs = new ArrayList<>();
        List<BallerinaModel.Service> services = new ArrayList<>();
        for (TibcoModel.Type type : process.types()) {
            switch (type) {
                case TibcoModel.Type.Schema schema -> moduleTypeDefs.addAll(TypeConverter.convertSchema(cx, schema));
                case TibcoModel.Type.WSDLDefinition wsdlDefinition ->
                    services.addAll(TypeConverter.convertWsdlDefinition(cx, wsdlDefinition));
            }
        }
        return new TypeConversionResult(moduleTypeDefs, services);
    }

    private record TypeConversionResult(Collection<BallerinaModel.ModuleTypeDef> moduleTypeDefs,
            Collection<BallerinaModel.Service> service) {

    }

    private static String addTerminalWorkerResultCombinationStatements(
            ProcessContext cx, List<BallerinaModel.Statement> stmts,
            Collection<TibcoModel.Scope.Flow.Activity> endActivities) {
        AnalysisResult analysisResult = cx.analysisResult;
        List<String> resultVars = new ArrayList<>();
        for (TibcoModel.Scope.Flow.Activity activity : endActivities) {
            String activityWorker = analysisResult.from(activity).workerName();
            String resultVar = "result" + resultVars.size();
            resultVars.add(resultVar);
            VarDeclStatment inputVarDecl = receiveVarFromPeer(activityWorker, resultVar);
            stmts.add(inputVarDecl);
        }

        // TODO: correctly combine
        String resultVar = String.join(" + ", resultVars);
        String resultVarName = "result";
        VarDeclStatment inputVarDecl = new VarDeclStatment(XML, resultVarName, new VariableReference(resultVar));
        stmts.add(inputVarDecl);
        return resultVarName;
    }

    private static Optional<BallerinaModel.Statement> generateActivityWorkers(ProcessContext cx,
            TibcoModel.Scope.Flow.Activity activity) {
        AnalysisResult analysisResult = cx.analysisResult;
        Collection<TibcoModel.Scope.Flow.Link> sources = analysisResult.sources(activity);
        if (sources.isEmpty()) {
            return Optional.empty();
        }

        List<BallerinaModel.Statement> body = new ArrayList<>();
        List<String> inputVars = new ArrayList<>();
        for (TibcoModel.Scope.Flow.Link source : sources) {
            String inputVarName = "input" + inputVars.size();
            addReceiveFromPeerStatement(analysisResult.from(source).workerName(), inputVarName, body, inputVars);
        }
        // TODO: concat
        String inputVar = String.join(" + ", inputVars);
        String combinedInput = "combinedInput";
        VarDeclStatment inputVarDecl = new VarDeclStatment(XML, combinedInput, new VariableReference(inputVar));
        body.add(inputVarDecl);

        FunctionCall callExpr = genereateActivityFunctionCall(cx, activity,
                new VariableReference(inputVarDecl.varName()));
        String outputVarName = "output";
        VarDeclStatment outputVarDecl = new VarDeclStatment(XML, outputVarName, callExpr);
        body.add(outputVarDecl);
        addTransitionsToDestination(cx, body, activity,
                new BallerinaModel.Expression.VariableReference(outputVarDecl.varName()));
        if (analysisResult.destinations(activity).isEmpty()) {
            BallerinaModel.Action.WorkerSendAction sendAction = new BallerinaModel.Action.WorkerSendAction(
                    new VariableReference(outputVarName), "function");
            // FIXME:
            body.add(new BallerinaModel.BallerinaStatement(sendAction + ";"));
        }
        String workerName = analysisResult.from(activity).workerName();
        return Optional.of(new NamedWorkerDecl(workerName, body));
    }

    private static FunctionCall genereateActivityFunctionCall(
            ProcessContext cx, TibcoModel.Scope.Flow.Activity activity,
            VariableReference inputVar) {
        AnalysisResult analysisResult = cx.analysisResult;
        String activityFunction = analysisResult.from(activity).functionName();
        return new FunctionCall(activityFunction,
                new BallerinaModel.Expression[] { inputVar, cx.getContextRef() });
    }

    private static BallerinaModel.Statement generateWorkerForStartActions(
            ProcessContext cx,
            Collection<TibcoModel.Scope.Flow.Activity> startActivities) {
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
        String result = "result" + index;
        FunctionCall callExpr = genereateActivityFunctionCall(cx, startActivity,
                new VariableReference("input"));
        VarDeclStatment outputVarDecl = new VarDeclStatment(XML, result, callExpr);
        body.add(outputVarDecl);
        addTransitionsToDestination(cx, body, startActivity, new BallerinaModel.Expression.VariableReference(result));
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
            TibcoModel.Scope.Flow.Activity.Source.Predicate predicate = destination.predicate().get();
            if (!(predicate instanceof TibcoModel.Scope.Flow.Activity.Expression.XPath(String expression))) {
                throw new UnsupportedOperationException("Only XPath predicates are supported");
            }
            String predicateTestFn = cx.getPredicateTestFunction();
            var xPathExpr = new BallerinaModel.Expression.StringConstant(expression);
            BallerinaModel.Expression.FunctionCall predicateTestCall = new BallerinaModel.Expression.FunctionCall(
                    predicateTestFn, List.of(value, xPathExpr));
            boolean hasElse = i < destinations.size() - 1 && destinations.get(i + 1).predicate().stream()
                    .anyMatch(p -> p instanceof TibcoModel.Scope.Flow.Activity.Source.Predicate.Else);
            if (!hasElse) {
                body.add(new BallerinaModel.IfElseStatement(predicateTestCall,
                        List.of(generateSendToWorker(cx, destination.target(), value.varName())), List.of(),
                        List.of()));
            } else {
                var elseDest = destinations.get(++i);
                body.add(new BallerinaModel.IfElseStatement(predicateTestCall,
                        List.of(generateSendToWorker(cx, destination.target(), value.varName())),
                        List.of(), List.of(generateSendToWorker(cx, elseDest.target(), value.varName()))));
            }
        }
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
        List<String> inputVarNames = new ArrayList<>();
        var analysisResult = cx.analysisResult;
        int inputCount = 0;
        Collection<TibcoModel.Scope.Flow.Activity> inputActivities = analysisResult.sources(link);
        AnalysisResult.LinkData linkData = analysisResult.from(link);
        for (TibcoModel.Scope.Flow.Activity activity : inputActivities) {
            boolean isStartActivity = analysisResult.startActivities(cx.process).contains(activity);
            String inputVarName = "result" + inputCount++;
            String workerName = isStartActivity ? cx.startWorkerName : analysisResult.from(activity).workerName();
            addReceiveFromPeerStatement(workerName, inputVarName, body, inputVarNames);
        }
        if (!inputVarNames.isEmpty()) {
            for (TibcoModel.Scope.Flow.Activity destinations : analysisResult.destinations(link)) {
                AnalysisResult.ActivityData activityData = analysisResult.from(destinations);
                assert inputVarNames.size() == 1 : "Multiple input vars not supported";
                String inputVarName = inputVarNames.getFirst();
                String activityWorker = activityData.workerName();
                BallerinaModel.Action.WorkerSendAction sendAction = new BallerinaModel.Action.WorkerSendAction(
                        new VariableReference(inputVarName), activityWorker);
                // FIXME:
                body.add(new BallerinaModel.BallerinaStatement(sendAction + ";"));
            }
        }
        return new NamedWorkerDecl(linkData.workerName(), body);
    }

    private static void addReceiveFromPeerStatement(String peer, String inputVarName,
            List<BallerinaModel.Statement> body,
            List<String> inputVarNames) {
        VarDeclStatment inputVarDecl = receiveVarFromPeer(peer, inputVarName);
        body.add(inputVarDecl);
        inputVarNames.add(inputVarName);
    }

    private static VarDeclStatment receiveVarFromPeer(String peer, String inputVarName) {
        BallerinaModel.Action.WorkerReceiveAction receiveEvent = new BallerinaModel.Action.WorkerReceiveAction(peer);
        return new VarDeclStatment(XML, inputVarName, receiveEvent);
    }

    private static BallerinaModel.Function generateStartFunction(ProcessContext cx) {

        List<BallerinaModel.Statement> body = new ArrayList<>();
        var startFuncData = cx.getProcessStartFunction();
        String inputVariable = "input";
        FunctionCall toXMLCall = new FunctionCall(
                cx.getToXmlFunction(), new String[] { inputVariable });
        String inputXML = "inputXML";
        VarDeclStatment inputXMLVar = new VarDeclStatment(XML, inputXML, toXMLCall);
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

        Return<VariableReference> returnStatement = new Return<>(Optional.of(new VariableReference("result")));
        body.add(returnStatement);

        BallerinaModel.TypeDesc inputType = startFuncData.inputType();
        return new BallerinaModel.Function(startFuncData.name(),
                List.of(new BallerinaModel.Parameter(inputVariable, inputType)), Optional.of(returnType.toString()),
                body);
    }

    private static BallerinaModel.Function generateProcessFunction(ProcessContext cx,
            TibcoModel.Process process) {
        String name = cx.getProcessFunction();
        AnalysisResult analysisResult = cx.analysisResult;
        Collection<TibcoModel.Scope.Flow.Activity> startActivity = cx.analysisResult.startActivities(process);
        List<BallerinaModel.Statement> body = new ArrayList<>();
        body.add(cx.initContextVar());
        String addToContextFn = cx.getAddToContextFn();
        body.add(new CallStatement(new FunctionCall(addToContextFn, List.of(cx.contextVarRef(),
                new StringConstant("post.item"),
                new VariableReference("input")))));
        body.add(generateWorkerForStartActions(cx, startActivity));
        analysisResult.links().stream().sorted(Comparator.comparing(link -> analysisResult.from(link).workerName()))
                .map(link -> generateLink(cx, link)).forEach(body::add);
        analysisResult.activities().stream()
                .sorted(Comparator.comparing(activity -> analysisResult.from(activity).workerName()))
                .map(activity -> generateActivityWorkers(cx, activity))
                .filter(Optional::isPresent).map(Optional::get).forEach(body::add);
        String resultVariableName = addTerminalWorkerResultCombinationStatements(cx, body,
                analysisResult.endActivities(process));
        body.add(new Return<>(new VariableReference(resultVariableName)));
        return new BallerinaModel.Function(name, List.of(new BallerinaModel.Parameter("input", XML)),
                Optional.of(XML.toString()), body);
    }

}
