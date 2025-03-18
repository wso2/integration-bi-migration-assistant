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

import static ballerina.BallerinaModel.TypeDesc.BuiltinType.NEVER;
import static ballerina.BallerinaModel.TypeDesc.BuiltinType.XML;

import ballerina.BallerinaModel;
import ballerina.BallerinaModel.Expression.MemberAccess;
import ballerina.BallerinaModel.Expression.VariableReference;
import ballerina.BallerinaModel.Statement.NamedWorkerDecl;
import ballerina.BallerinaModel.Statement.Return;
import ballerina.BallerinaModel.Statement.VarAssignStatement;
import ballerina.BallerinaModel.Statement.VarDeclStatment;
import converter.tibco.analyzer.AnalysisResult;
import tibco.TibcoModel;

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

        BallerinaModel.Expression.FunctionCall callExpr = genereateActivityFunctionCall(cx, activity,
                new VariableReference(inputVarDecl.varName()));
        String outputVarName = "output";
        VarDeclStatment outputVarDecl = new VarDeclStatment(XML, outputVarName, callExpr);
        body.add(outputVarDecl);
        Collection<TibcoModel.Scope.Flow.Link> destinations = analysisResult.destinations(activity);
        for (TibcoModel.Scope.Flow.Link destination : destinations) {
            body.add(generateSendToWorker(cx, destination, outputVarName));
        }
        if (destinations.isEmpty()) {
            BallerinaModel.Action.WorkerSendAction sendAction = new BallerinaModel.Action.WorkerSendAction(
                    new VariableReference(outputVarName), "function");
            // FIXME:
            body.add(new BallerinaModel.BallerinaStatement(sendAction + ";"));
        }
        String workerName = analysisResult.from(activity).workerName();
        return Optional.of(new NamedWorkerDecl(workerName, body));
    }

    private static BallerinaModel.Expression.FunctionCall genereateActivityFunctionCall(
            ProcessContext cx, TibcoModel.Scope.Flow.Activity activity,
            VariableReference inputVar) {
        AnalysisResult analysisResult = cx.analysisResult;
        String activityFunction = analysisResult.from(activity).functionName();
        return new BallerinaModel.Expression.FunctionCall(activityFunction,
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
        AnalysisResult analysisResult = cx.analysisResult;
        BallerinaModel.Expression.FunctionCall callExpr = genereateActivityFunctionCall(cx, startActivity,
                new VariableReference("input"));
        VarDeclStatment outputVarDecl = new VarDeclStatment(XML, result, callExpr);
        body.add(outputVarDecl);

        Collection<TibcoModel.Scope.Flow.Link> destinationLinks = analysisResult.destinations(startActivity);
        for (TibcoModel.Scope.Flow.Link destinationLink : destinationLinks) {
            body.add(generateSendToWorker(cx, destinationLink, result));
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
        BallerinaModel.Expression.FunctionCall toXMLCall = new BallerinaModel.Expression.FunctionCall(
                cx.getToXmlFunction(), new String[] { inputVariable });
        String inputXML = "inputXML";
        VarDeclStatment inputXMLVar = new VarDeclStatment(XML, inputXML, toXMLCall);
        body.add(inputXMLVar);

        String processFunction = cx.getProcessFunction();
        VarDeclStatment xmlResult = new VarDeclStatment(XML, "xmlResult",
                new BallerinaModel.Expression.FunctionCall(processFunction, new String[] { inputXML }));
        body.add(xmlResult);

        BallerinaModel.TypeDesc returnType = startFuncData.returnType();
        String convertToTypeFunction = cx.getConvertToTypeFunction(returnType);
        VarDeclStatment result = new VarDeclStatment(returnType, "result",
                new BallerinaModel.Expression.FunctionCall(convertToTypeFunction, new String[] { "xmlResult" }));
        body.add(result);

        Return<VariableReference> returnStatement = new Return<>(Optional.of(new VariableReference("result")));
        body.add(returnStatement);

        BallerinaModel.TypeDesc inputType = startFuncData.inputType();
        return new BallerinaModel.Function(startFuncData.name(),
                List.of(new BallerinaModel.Parameter(inputVariable, inputType)), Optional.of(returnType.toString()),
                body);
    }

    static BallerinaModel.ModuleTypeDef convertComplexType(ProcessContext cx,
            TibcoModel.Type.Schema.ComplexType complexType) {
        BallerinaModel.TypeDesc typeDesc = switch (complexType.body()) {
            case TibcoModel.Type.Schema.ComplexType.Choice choice -> convertTypeChoice(cx, choice);
            case TibcoModel.Type.Schema.ComplexType.SequenceBody sequenceBody -> convertSequenceBody(cx, sequenceBody);
            case TibcoModel.Type.Schema.ComplexType.ComplexContent complexContent ->
                convertTypeInclusion(cx, complexContent);
        };
        String name = complexType.name();
        BallerinaModel.ModuleTypeDef typeDef = new BallerinaModel.ModuleTypeDef(name, typeDesc);
        cx.addModuleTypeDef(name, typeDef);
        return typeDef;
    }

    private static BallerinaModel.TypeDesc.RecordTypeDesc convertTypeInclusion(
            ProcessContext cx,
            TibcoModel.Type.Schema.ComplexType.ComplexContent complexContent) {
        List<BallerinaModel.TypeDesc> inclusions = List.of(cx.getTypeByName(complexContent.extension().base().name()));
        RecordBody body = getRecordBody(cx, complexContent.extension().elements());
        return new BallerinaModel.TypeDesc.RecordTypeDesc(inclusions, body.fields(), body.rest().orElse(NEVER));
    }

    private static BallerinaModel.TypeDesc.RecordTypeDesc convertSequenceBody(
            ProcessContext cx,
            TibcoModel.Type.Schema.ComplexType.SequenceBody sequenceBody) {
        Collection<TibcoModel.Type.Schema.ComplexType.SequenceBody.Member> members = sequenceBody.elements();
        RecordBody body = getRecordBody(cx, members);
        return new BallerinaModel.TypeDesc.RecordTypeDesc(List.of(), body.fields(), body.rest().orElse(NEVER));
    }

    private static RecordBody getRecordBody(
            ProcessContext cx,
            Collection<? extends TibcoModel.Type.Schema.ComplexType.SequenceBody.Member> members) {
        List<BallerinaModel.TypeDesc.RecordTypeDesc.RecordField> fields = new ArrayList<>();
        Optional<BallerinaModel.TypeDesc> rest = Optional.empty();
        for (TibcoModel.Type.Schema.ComplexType.SequenceBody.Member member : members) {
            switch (member) {
                case TibcoModel.Type.Schema.ComplexType.SequenceBody.Member.Element element -> {
                    BallerinaModel.TypeDesc typeDesc = cx.getTypeByName(element.type().name());
                    fields.add(new BallerinaModel.TypeDesc.RecordTypeDesc.RecordField(element.name(), typeDesc));
                }
                case TibcoModel.Type.Schema.ComplexType.SequenceBody.Member.Rest ignored -> {
                    // TODO: handle this properly
                    rest = Optional.of(PredefinedTypes.ANYDATA);
                }
                case TibcoModel.Type.Schema.ComplexType.Choice choice -> {
                    rest = Optional.of(convertTypeChoice(cx, choice));
                }
            }
        }
        return new RecordBody(fields, rest);
    }

    private record RecordBody(List<BallerinaModel.TypeDesc.RecordTypeDesc.RecordField> fields,
            Optional<BallerinaModel.TypeDesc> rest) {

    }

    static BallerinaModel.TypeDesc.UnionTypeDesc convertTypeChoice(ProcessContext cx,
            TibcoModel.Type.Schema.ComplexType.Choice choice) {
        List<? extends BallerinaModel.TypeDesc> types = choice.elements().stream().map(element -> {
            BallerinaModel.TypeDesc typeDesc = cx.getTypeByName(element.ref().name());
            assert element.maxOccurs() == 1;
            if (element.minOccurs() == 0) {
                return BallerinaModel.TypeDesc.UnionTypeDesc.of(typeDesc, BallerinaModel.TypeDesc.BuiltinType.NIL);
            } else {
                return typeDesc;
            }
        }).flatMap(type -> {
            if (type instanceof BallerinaModel.TypeDesc.UnionTypeDesc(Collection<? extends BallerinaModel.TypeDesc> members)) {
                return members.stream();
            } else {
                return Stream.of(type);
            }
        }).distinct().toList();
        return new BallerinaModel.TypeDesc.UnionTypeDesc(types);
    }

    private static BallerinaModel.Function generateProcessFunction(ProcessContext cx,
            TibcoModel.Process process) {
        String name = cx.getProcessFunction();
        AnalysisResult analysisResult = cx.analysisResult;
        Collection<TibcoModel.Scope.Flow.Activity> startActivity = cx.analysisResult.startActivities(process);
        List<BallerinaModel.Statement> body = new ArrayList<>();
        body.add(cx.initContextVar());
        body.add(new VarAssignStatement(new MemberAccess(cx.contextVarRef(), "post.item"),
                new VariableReference("input")));
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

    static class PredefinedTypes {

        private static final BallerinaModel.TypeDesc.BuiltinType ANYDATA = BallerinaModel.TypeDesc.BuiltinType.ANYDATA;
    }
}
