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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static ballerina.BallerinaModel.TypeDesc.BuiltinType.ANYDATA;
import static ballerina.BallerinaModel.TypeDesc.BuiltinType.JSON;
import static ballerina.BallerinaModel.TypeDesc.BuiltinType.STRING;
import static ballerina.BallerinaModel.TypeDesc.BuiltinType.XML;

import ballerina.BallerinaModel;
import ballerina.BallerinaModel.Statement;
import ballerina.BallerinaModel.Statement.Return;
import ballerina.BallerinaModel.Statement.VarDeclStatment;
import converter.tibco.analyzer.AnalysisResult;
import converter.tibco.analyzer.ModelAnalyser;
import tibco.TibcoModel;

public class ProjectContext {

    private final Map<TibcoModel.Process, ProcessContext> processContextMap = new HashMap<>();
    private final Map<String, Optional<BallerinaModel.ModuleTypeDef>> moduleTypeDefs = new HashMap<>();
    private final List<BallerinaModel.Function> utilityFunctions = new ArrayList<>();
    private final Set<BallerinaModel.Import> utilityFunctionImports = new HashSet<>();
    private final List<String> typeIntrinsics = new ArrayList<>();
    private String toXMLFunction = null;
    private String jsonToXMLFunction = null;
    private String toHttpConfigFunction = null;
    private int nextPort = 8080;

    ProcessContext getProcessContext(TibcoModel.Process process) {
        return processContextMap.computeIfAbsent(process, p -> new ProcessContext(this, p));
    }

    private int allocatePort() {
        return nextPort++;
    }

    public BallerinaModel.Module serialize(Collection<BallerinaModel.TextDocument> textDocuments) {
        // FIXME: also do utils
        List<BallerinaModel.TextDocument> combinedTextDocuments = Stream.concat(textDocuments.stream(),
                Stream.of(typesFile(), utilsFile())).toList();
        return new BallerinaModel.Module("tibco", combinedTextDocuments);
    }

    String getToXmlFunction() {
        if (toXMLFunction == null) {
            importLibraryIfNeeded(Library.XML_DATA);
            String functionName = "toXML";
            utilityFunctions.add(new BallerinaModel.Function(functionName,
                    List.of(new BallerinaModel.Parameter("data", new BallerinaModel.TypeDesc.MapTypeDesc(ANYDATA))),
                    Optional.of("xml"), List.of(new Return<>(
                    Optional.of(new BallerinaModel.Expression.CheckPanic(
                            new BallerinaModel.Expression.FunctionCall("xmldata:toXml", new String[]{"data"})))))));
            toXMLFunction = functionName;
        }
        return toXMLFunction;
    }

    private String getJsonToXMLFunction() {
        if (jsonToXMLFunction == null) {
            importLibraryIfNeeded(Library.XML_DATA);
            String functionName = "fromJson";
            utilityFunctions.add(
                    new BallerinaModel.Function(functionName, List.of(new BallerinaModel.Parameter("data", JSON)),
                            "xml", List.of(new Return<>(Optional.of(
                            new BallerinaModel.Expression.CheckPanic(
                                    new BallerinaModel.Expression.FunctionCall("xmldata:fromJson",
                                            new String[]{"data"})))))));
            jsonToXMLFunction = functionName;
        }
        return jsonToXMLFunction;
    }

    private String getParseHttpConfigFunction() {
        if (toHttpConfigFunction == null) {
            BallerinaModel.TypeDesc targetType = getHttpConfigType();
            toHttpConfigFunction = createConvertToTypeFunction(targetType);
        }
        return toHttpConfigFunction;
    }

    private BallerinaModel.TypeDesc.TypeReference getHttpConfigType() {
        // type HTTPRequestConfig record {
        //     string Method;
        //     string RequestURI;
        //     json PostData = "";
        //     map<string> Headers = {};
        //     map<string> parameters = {};
        // };
        String httpConfigTy = "HTTPRequestConfig";
        if (moduleTypeDefs.containsKey(httpConfigTy)) {
            return new BallerinaModel.TypeDesc.TypeReference(httpConfigTy);
        }
        BallerinaModel.ModuleTypeDef httpConfigType = new BallerinaModel.ModuleTypeDef(httpConfigTy,
                new BallerinaModel.TypeDesc.RecordTypeDesc(
                        List.of(
                                new BallerinaModel.TypeDesc.RecordTypeDesc.RecordField("Method", STRING),
                                new BallerinaModel.TypeDesc.RecordTypeDesc.RecordField("RequestURI", STRING),
                                // FIXME: what if the method is put
                                new BallerinaModel.TypeDesc.RecordTypeDesc.RecordField("PostData", JSON, Optional.of(
                                        new BallerinaModel.Expression.StringConstant(""))),
                                new BallerinaModel.TypeDesc.RecordTypeDesc.RecordField("Headers",
                                        new BallerinaModel.TypeDesc.MapTypeDesc(STRING), Optional.of(
                                        new BallerinaModel.Expression.MappingConstructor(List.of()))),
                                new BallerinaModel.TypeDesc.RecordTypeDesc.RecordField("parameters",
                                        new BallerinaModel.TypeDesc.MapTypeDesc(STRING), Optional.of(
                                        new BallerinaModel.Expression.MappingConstructor(List.of())))
                        )));
        moduleTypeDefs.put(httpConfigTy, Optional.of(httpConfigType));
        typeIntrinsics.add(Intrinsics.CREATE_HTTP_REQUEST_PATH_FROM_CONFIG.body);

        return new BallerinaModel.TypeDesc.TypeReference(httpConfigTy);
    }

    private void importLibraryIfNeeded(Library library) {
        utilityFunctionImports.add(new BallerinaModel.Import("ballerina", library.value, Optional.empty()));
    }

    private String createConvertToTypeFunction(BallerinaModel.TypeDesc targetType) {
        String functionName = "convertTo" + ConversionUtils.sanitizes(targetType.toString());
        importLibraryIfNeeded(Library.XML_DATA);
        BallerinaModel.Expression.FunctionCall parseAsTypeCall =
                new BallerinaModel.Expression.FunctionCall("xmldata:parseAsType", new String[]{"input"});
        BallerinaModel.Expression.CheckPanic checkPanic = new BallerinaModel.Expression.CheckPanic(parseAsTypeCall);
        Return<BallerinaModel.Expression.CheckPanic> returnStmt = new Return<>(Optional.of(checkPanic));
        BallerinaModel.Function function = new BallerinaModel.Function(functionName,
                List.of(new BallerinaModel.Parameter("input", XML)), Optional.of(targetType.toString()),
                List.of(returnStmt));
        utilityFunctions.add(function);
        return functionName;
    }

    private BallerinaModel.TextDocument utilsFile() {
        List<BallerinaModel.Import> imports = utilityFunctionImports.stream().toList();
        return new BallerinaModel.TextDocument("utils.bal", imports, List.of(), List.of(), List.of(), List.of(),
                utilityFunctions, List.of());
    }

    private BallerinaModel.TextDocument typesFile() {
        List<BallerinaModel.ModuleTypeDef> typeDefs = new ArrayList<>();
        for (var entry : moduleTypeDefs.entrySet()) {
            if (entry.getValue().isPresent()) {
                typeDefs.add(entry.getValue().get());
            } else {
                throw new IllegalStateException("Type definition not found for " + entry.getKey());
            }
        }
        // FIXME: handle imports
        List<BallerinaModel.Import> imports = List.of();
        return new BallerinaModel.TextDocument("types.bal", imports, typeDefs, List.of(), List.of(), List.of(),
                List.of(), List.of(), typeIntrinsics);
    }

    private FunctionData getProcessStartFunction(String processName) {
        TibcoModel.Process
                process = processContextMap.keySet().stream().filter(proc -> proc.name().equals(processName)).findAny()
                .orElseThrow(() -> new IndexOutOfBoundsException("failed to find process" + processName));
        return getProcessContext(process).getProcessStartFunction();
    }

    BallerinaModel.TypeDesc getTypeByName(String name, ProcessContext processContext) {
        // TODO: how to handle names spaces
        name = ConversionUtils.sanitizes(XmlToTibcoModelConverter.getTagNameWithoutNameSpace(name));
        if (moduleTypeDefs.containsKey(name)) {
            return new BallerinaModel.TypeDesc.TypeReference(name);
        }
        if (processContext.constants.containsKey(name)) {
            return new BallerinaModel.TypeDesc.TypeReference(name);
        }

        Optional<BallerinaModel.TypeDesc.BuiltinType> builtinType = mapToBuiltinType(name);
        if (builtinType.isPresent()) {
            return builtinType.get();
        }

        Optional<BallerinaModel.TypeDesc.TypeReference> libraryType = mapToLibraryType(name, processContext);
        if (libraryType.isPresent()) {
            return libraryType.get();
        }

        if (!moduleTypeDefs.containsKey(name)) {
            moduleTypeDefs.put(name, Optional.empty());
        }
        return new BallerinaModel.TypeDesc.TypeReference(name);
    }

    private Optional<BallerinaModel.TypeDesc.TypeReference> mapToLibraryType(String name,
                                                                             ProcessContext processContext) {
        return switch (name) {
            case "client4XXError" -> Optional.of(getLibraryType(Library.HTTP, "NotFound", processContext));
            case "server5XXError" -> Optional.of(getLibraryType(Library.HTTP, "InternalServerError", processContext));
            case "Client" -> Optional.of(getLibraryType(Library.HTTP, "Client", processContext));
            default -> Optional.empty();
        };
    }

    private BallerinaModel.TypeDesc.TypeReference getLibraryType(Library library, String typeName,
                                                                 ProcessContext processContext) {
        processContext.addLibraryImport(library);
        return new BallerinaModel.TypeDesc.TypeReference(library.value + ":" + typeName);
    }

    private Optional<BallerinaModel.TypeDesc.BuiltinType> mapToBuiltinType(String name) {
        return switch (name) {
            case "string" -> Optional.of(BallerinaModel.TypeDesc.BuiltinType.STRING);
            case "integer", "int" -> Optional.of(BallerinaModel.TypeDesc.BuiltinType.INT);
            case "anydata" -> Optional.of(BallerinaModel.TypeDesc.BuiltinType.ANYDATA);
            case "xml" -> Optional.of(XML);
            // FIXME:
            case "base64Binary" -> Optional.of(BallerinaModel.TypeDesc.BuiltinType.INT);
            default -> Optional.empty();
        };
    }

    static class ProcessContext {

        private final Set<BallerinaModel.Import> imports = new HashSet<>();
        final String CONTEXT_VAR_NAME = "context";
        private BallerinaModel.Listener defaultListner = null;
        private final Map<String, BallerinaModel.ModuleVar> constants = new HashMap<>();
        private final Map<String, BallerinaModel.ModuleVar> configurables = new HashMap<>();
        private final Map<BallerinaModel.TypeDesc, String> typeConversionFunction = new HashMap<>();
        public String startWorkerName;
        public final TibcoModel.Process process;
        public BallerinaModel.TypeDesc processInputType;
        public BallerinaModel.TypeDesc processReturnType;

        public final ProjectContext projectContext;
        public final AnalysisResult analysisResult;
        private BallerinaModel.Expression.VariableReference contextRef;

        private ProcessContext(ProjectContext projectContext, TibcoModel.Process process) {
            this.projectContext = projectContext;
            this.process = process;
            this.analysisResult = ModelAnalyser.analyseProcess(process);
        }

        public BallerinaModel.TypeDesc contextType() {
            return new BallerinaModel.TypeDesc.MapTypeDesc(XML);
        }

        public VarDeclStatment initContextVar() {
            VarDeclStatment varDeclStatment =
                    new VarDeclStatment(contextType(), "context", new BallerinaModel.BallerinaExpression("{}"));
            this.contextRef = new BallerinaModel.Expression.VariableReference(varDeclStatment.varName());
            return varDeclStatment;
        }

        public BallerinaModel.Expression.VariableReference getContextRef() {
            assert contextRef != null;
            return contextRef;
        }

        public BallerinaModel.Expression.VariableReference addConfigurableVariable(BallerinaModel.TypeDesc td,
                                                                                   String name) {
            var varDecl = this.configurables.computeIfAbsent(name, k -> createConfigurableVariable(td, name));
            return new BallerinaModel.Expression.VariableReference(varDecl.name());
        }

        private static BallerinaModel.ModuleVar createConfigurableVariable(BallerinaModel.TypeDesc td, String name) {
            return BallerinaModel.ModuleVar.configurable(name, td, new BallerinaModel.BallerinaExpression("?"));
        }

        String getToXmlFunction() {
            return this.projectContext.getToXmlFunction();
        }

        boolean addModuleTypeDef(String name, BallerinaModel.ModuleTypeDef moduleTypeDef) {
            if (moduleTypeDef.typeDesc() instanceof BallerinaModel.TypeDesc.TypeReference(String name1) &&
                    name1.equals(name)) {
                return false;
            }
            this.projectContext.moduleTypeDefs.put(name, Optional.of(moduleTypeDef));
            return true;
        }

        BallerinaModel.TypeDesc getTypeByName(String name) {
            return projectContext.getTypeByName(name, this);
        }

        String declareConstant(String name, String valueRepr, String type) {
            name = ConversionUtils.sanitizes(name);
            BallerinaModel.TypeDesc td = getTypeByName(type);
            assert td == BallerinaModel.TypeDesc.BuiltinType.STRING;
            String expr = "\"" + valueRepr + "\"";
            var prev = constants.put(name,
                    BallerinaModel.ModuleVar.constant(name, td, new BallerinaModel.BallerinaExpression(expr)));
            assert prev == null || prev.expr().expr().equals(expr);
            return name;
        }

        void addLibraryImport(Library library) {
            imports.add(new BallerinaModel.Import("ballerina", library.value, Optional.empty()));
        }

        String getDefaultHttpListenerRef() {
            if (defaultListner == null) {
                addLibraryImport(Library.HTTP);
                String listenerRef = ConversionUtils.sanitizes(process.name()) + "_listener";
                defaultListner = new BallerinaModel.Listener(BallerinaModel.ListenerType.HTTP, listenerRef,
                        Integer.toString(projectContext.allocatePort()),
                        Map.of("host", "localhost"));
            }
            return defaultListner.name();

        }

        // FIXME: don't get the typeDefs
        BallerinaModel.TextDocument serialize(Collection<BallerinaModel.Service> processServices,
                                              List<BallerinaModel.Function> functions) {
            String name = ConversionUtils.sanitizes(process.name()) + ".bal";
            List<BallerinaModel.Listener> listeners = defaultListner != null ? List.of(defaultListner) : List.of();
            List<BallerinaModel.ModuleVar> moduleVars =
                    Stream.concat(constants.values().stream(), configurables.values().stream()).toList();
            return new BallerinaModel.TextDocument(name, imports.stream().toList(), List.of(),
                    moduleVars, listeners, processServices.stream().toList(), functions, List.of());
        }

        public FunctionData getProcessStartFunction() {
            return new FunctionData(ConversionUtils.sanitizes(process.name()) + "_start", processInputType,
                    processReturnType);
        }

        public String getProcessFunction() {
            return "process_" + ConversionUtils.sanitizes(process.name());
        }

        public String getConvertToTypeFunction(BallerinaModel.TypeDesc targetType) {
            // FIXME: create a utility function
            return typeConversionFunction.computeIfAbsent(targetType, this::createConvertToTypeFunction);
        }

        private String createConvertToTypeFunction(BallerinaModel.TypeDesc targetType) {
            return projectContext.createConvertToTypeFunction(targetType);
        }

        public FunctionData getProcessStartFunction(String processName) {
            return projectContext.getProcessStartFunction(processName);
        }

        public String getJsonToXMLFunction() {
            return projectContext.getJsonToXMLFunction();
        }

        public String getParseHttpConfigFunction() {
            return projectContext.getParseHttpConfigFunction();
        }

        public BallerinaModel.TypeDesc.TypeReference getHttpConfigType() {
            return projectContext.getHttpConfigType();
        }

        public BallerinaModel.Expression contextVarRef() {
            return new BallerinaModel.Expression.VariableReference(CONTEXT_VAR_NAME);
        }
    }

    record FunctionData(String name, BallerinaModel.TypeDesc inputType, BallerinaModel.TypeDesc returnType) {

        FunctionData {
            assert name != null && !name.isEmpty();
            assert inputType != null;
            assert returnType != null;
        }
    }
}
