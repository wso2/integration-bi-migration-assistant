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
import common.BallerinaModel.Expression.FunctionCall;
import common.BallerinaModel.Statement.Return;
import common.BallerinaModel.TypeDesc.UnionTypeDesc;
import io.ballerina.compiler.syntax.tree.ModuleMemberDeclarationNode;
import org.jetbrains.annotations.NotNull;
import tibco.TibcoToBalConverter;
import tibco.analyzer.AnalysisResult;
import tibco.model.Process;
import tibco.model.Type;
import tibco.parser.XmlToTibcoModelParser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static common.BallerinaModel.Expression.TernaryExpression;
import static common.BallerinaModel.Expression.VariableReference;
import static common.BallerinaModel.TypeDesc.BuiltinType.ANYDATA;
import static common.BallerinaModel.TypeDesc.BuiltinType.BOOLEAN;
import static common.BallerinaModel.TypeDesc.BuiltinType.ERROR;
import static common.BallerinaModel.TypeDesc.BuiltinType.JSON;
import static common.BallerinaModel.TypeDesc.BuiltinType.NIL;
import static common.BallerinaModel.TypeDesc.BuiltinType.STRING;
import static common.BallerinaModel.TypeDesc.BuiltinType.XML;
import static common.ConversionUtils.exprFrom;
import static tibco.converter.Library.HTTP;
import static tibco.converter.Library.JDBC;
import static tibco.converter.Library.JSON_DATA;
import static tibco.converter.Library.XML_DATA;

public class ProjectContext {

    private final Map<Process, ProcessContext> processContextMap = new HashMap<>();

    private final List<BallerinaModel.Function> utilityFunctions = new ArrayList<>();
    private final Set<BallerinaModel.Import> utilityFunctionImports = new HashSet<>();
    private final Map<String, BallerinaModel.ModuleVar> utilityVars = new HashMap<>();
    private final Map<String, BallerinaModel.Listener> utilityListeners = new HashMap<>();
    private final Map<String, BallerinaModel.ModuleTypeDef> utilityTypeDefs = new HashMap<>();
    private final Set<Intrinsics> utilityIntrinsics = new HashSet<>();
    private final Set<ComptimeFunction> utilityCompTimeFunctions = new HashSet<>();
    private final Map<String, String> processClients = new HashMap<>();

    private String toXMLFunction = null;
    private String jsonToXMLFunction = null;
    private int nextPort = 8080;
    private int typeCount = 0;
    private int annonVarCount = 0;

    private final ContextWrapperForTypeFile typeCx = new ContextWrapperForTypeFile(this);
    private static final Logger logger = ProjectConverter.logger();
    private final Optional<TibcoToBalConverter.ProjectConversionContext> conversionContext;
    private final Map<String, String> generatedResources = new HashMap<>();
    private final Map<String, BallerinaModel.Expression.VariableReference> httpClients = new HashMap<>();
    private final Map<BallerinaModel.TypeDesc, String> dataBindingFunctions = new HashMap<>();
    private final Map<String, String> renderJsonAsXMLFunction = new HashMap<>();
    private final Map<Process, AnalysisResult> analysisResult;
    private Collection<Type.Schema> schemas = new ArrayList<>();

    ProjectContext(TibcoToBalConverter.ProjectConversionContext conversionContext,
                   Map<Process, AnalysisResult> analysisResult) {
        this.conversionContext = Optional.of(conversionContext);
        this.analysisResult = analysisResult;
    }

    ProcessContext getProcessContext(Process process) {
        return processContextMap.computeIfAbsent(process, p -> new ProcessContext(this, p));
    }

    int allocatePort() {
        return nextPort++;
    }

    BallerinaModel.Module serialize(Collection<BallerinaModel.TextDocument> textDocuments) {
        List<BallerinaModel.TextDocument> combinedTextDocuments = Stream.concat(textDocuments.stream(),
                Stream.of(typesFile(), utilsFile())).toList();
        logger.info(String.format("Type Statistics - Total Types: %d", typeCount));
        return new BallerinaModel.Module("tibco", combinedTextDocuments);
    }

    String getToXmlFunction() {
        if (toXMLFunction != null) {
            return toXMLFunction;
        }
        importLibraryIfNeededToUtility(Library.XML_DATA);
        String functionName = "toXML";
        utilityFunctions.add(new BallerinaModel.Function(functionName,
                List.of(new BallerinaModel.Parameter("data", new BallerinaModel.TypeDesc.MapTypeDesc(ANYDATA))),
                new UnionTypeDesc(List.of(ERROR, XML)),
                List.of(new Return<>(
                        Optional.of(new FunctionCall("xmldata:toXml",
                                new String[]{"data"}))))));
        toXMLFunction = functionName;
        return toXMLFunction;
    }

    String getJsonToXMLFunction() {
        if (jsonToXMLFunction != null) {
            return jsonToXMLFunction;
        }
        importLibraryIfNeededToUtility(Library.XML_DATA);
        String functionName = "fromJson";
        utilityFunctions.add(
                new BallerinaModel.Function(functionName, List.of(new BallerinaModel.Parameter("data", JSON)),
                        UnionTypeDesc.of(ERROR, XML),
                        List.of(new Return<>(new FunctionCall("xmldata:fromJson", new String[]{"data"})))));
        jsonToXMLFunction = functionName;
        return jsonToXMLFunction;
    }

    @NotNull
    BallerinaModel.TypeDesc contextType() {
        BallerinaModel.TypeDesc.TypeReference responseTy =
                getOrCreateUtilityTypeDef("Response", ConversionUtils.Constants.RESPONSE_TYPE_DESC);
        getOrCreateUtilityTypeDef("JSONResponse", ConversionUtils.Constants.JSON_RESPONSE_TYPE_DESC);
        getOrCreateUtilityTypeDef("XMLResponse", ConversionUtils.Constants.XML_RESPONSE_TYPE_DESC);
        getOrCreateUtilityTypeDef("TextResponse", ConversionUtils.Constants.TEXT_RESPONSE_TYPE_DESC);
        return getOrCreateUtilityTypeDef("Context", new BallerinaModel.TypeDesc.RecordTypeDesc(
                List.of(
                        new BallerinaModel.TypeDesc.RecordTypeDesc.RecordField("variables",
                                new BallerinaModel.TypeDesc.MapTypeDesc(XML)),
                        new BallerinaModel.TypeDesc.RecordTypeDesc.RecordField("result", XML),
                        new BallerinaModel.TypeDesc.RecordTypeDesc.RecordField("response", responseTy, true))));
    }

    private BallerinaModel.TypeDesc.TypeReference getOrCreateUtilityTypeDef(String typeName,
            BallerinaModel.TypeDesc typeDesc) {
        return utilityTypeDefs.computeIfAbsent(typeName,
                name -> new BallerinaModel.ModuleTypeDef(name, typeDesc))
                .typeDesc() instanceof BallerinaModel.TypeDesc.TypeReference ref ? ref
                        : new BallerinaModel.TypeDesc.TypeReference(typeName);
    }

    private void importLibraryIfNeededToUtility(Library library) {
        conversionContext.ifPresent(cx -> {
            if (library == JDBC) {
                cx.javaDependencies().add(TibcoToBalConverter.JavaDependencies.JDBC);
            }
        });
        utilityFunctionImports.add(new BallerinaModel.Import(library.orgName, library.moduleName, Optional.empty()));
    }

    String getConvertToTypeFunction(BallerinaModel.TypeDesc targetType) {
        importLibraryIfNeededToUtility(XML_DATA);
        importLibraryIfNeededToUtility(JSON_DATA);
        ComptimeFunction convertToType = new ConvertToType(targetType);
        utilityCompTimeFunctions.add(convertToType);
        return convertToType.functionName();
    }

    String getTryDataBindToTypeFunction(BallerinaModel.TypeDesc targetType) {
        return dataBindingFunctions.computeIfAbsent(targetType, this::createTryDataBindToTypeFunction);
    }

    String createTryDataBindToTypeFunction(BallerinaModel.TypeDesc targetType) {
        String functionName = "tryBindTo" + ConversionUtils.sanitizes(targetType.toString());
        importLibraryIfNeededToUtility(Library.XML_DATA);
        importLibraryIfNeededToUtility(Library.JSON_DATA);
        VariableReference input = new VariableReference("input");

        BallerinaModel.Function function =
                new BallerinaModel.Function(
                        functionName,
                        List.of(new BallerinaModel.Parameter(input.varName(), UnionTypeDesc.of(XML, JSON))),
                        UnionTypeDesc.of(targetType, ERROR),
                        List.of(new Return<>(
                                new TernaryExpression(
                                        new BallerinaModel.Expression.TypeCheckExpression(input, XML),
                                        new FunctionCall("xmldata:parseAsType", List.of(input)),
                                        new FunctionCall("jsondata:parseAsType", List.of(input))))));
        utilityFunctions.add(function);
        return functionName;
    }

    private BallerinaModel.TextDocument utilsFile() {
        List<BallerinaModel.Import> imports = utilityFunctionImports.stream().toList();
        List<BallerinaModel.ModuleTypeDef> sortedTypeDefs = utilityTypeDefs.values().stream()
                .sorted(Comparator.comparing(BallerinaModel.ModuleTypeDef::name))
                .toList();
        List<BallerinaModel.ModuleVar> sortedConstants = utilityVars.values().stream()
                .sorted(Comparator.comparing(BallerinaModel.ModuleVar::name))
                .toList();
        List<BallerinaModel.Function> sortedFunctions = utilityFunctions.stream()
                .sorted(Comparator.comparing(BallerinaModel.Function::functionName))
                .toList();
        Stream<String> sortedIntrinsics = utilityIntrinsics.stream()
                .sorted(Comparator.comparing(Intrinsics::name))
                .map(each -> each.body);
        Stream<String> sortedComptimes = utilityCompTimeFunctions.stream()
                .sorted(Comparator.comparing(ComptimeFunction::functionName))
                .map(ComptimeFunction::intrinsify);
        List<String> combinedIntrinsics = Stream.concat(sortedIntrinsics, sortedComptimes).toList();
        List<BallerinaModel.Listener> listeners = utilityListeners.values().stream()
                .sorted(Comparator.comparing(BallerinaModel.Listener::name)).toList();
        return new BallerinaModel.TextDocument("utils.bal", imports, sortedTypeDefs, sortedConstants,
                        listeners, List.of(), sortedFunctions, List.of(), combinedIntrinsics, List.of());
    }

    private BallerinaModel.TextDocument typesFile() {
        return typeCx.serialize();
    }

    FunctionData getProcessStartFunction(String processName) {
        Process process = getProcess(processName);
        return getProcessContext(process).getProcessStartFunction();
    }

    String getRenderJsonAsXMLFunction(String type) {
        return renderJsonAsXMLFunction.computeIfAbsent(type, this::createRenderJsonAsXMLFunction);
    }

    String createRenderJsonAsXMLFunction(String type) {
        importLibraryIfNeededToUtility(XML_DATA);
        importLibraryIfNeededToUtility(JSON_DATA);
        utilityIntrinsics.add(Intrinsics.RENDER_JSON_AS_XML);
        ComptimeFunction renderJsonAsXML = new RenderJSONAsXML(type);
        utilityCompTimeFunctions.add(renderJsonAsXML);
        return renderJsonAsXML.functionName();
    }

    BallerinaModel.TypeDesc getTypeByName(String name, ContextWithFile cx) {
        // TODO: how to handle names spaces
        name = ConversionUtils.sanitizes(XmlToTibcoModelParser.getTagNameWithoutNameSpace(name));
        if (typeCx.moduleTypeDefs.containsKey(name)) {
            return new BallerinaModel.TypeDesc.TypeReference(name);
        }
        if (cx.hasConstantWithName(name)) {
            return new BallerinaModel.TypeDesc.TypeReference(name);
        }

        Optional<BallerinaModel.TypeDesc.BuiltinType> builtinType = mapToBuiltinType(name);
        if (builtinType.isPresent()) {
            return builtinType.get();
        }

        Optional<BallerinaModel.TypeDesc.TypeReference> libraryType = mapToLibraryType(cx, name);
        if (libraryType.isPresent()) {
            return libraryType.get();
        }

        if (!typeCx.moduleTypeDefs.containsKey(name)) {
            typeCx.moduleTypeDefs.put(name, Optional.empty());
        }
        return new BallerinaModel.TypeDesc.TypeReference(name);
    }

    private Optional<BallerinaModel.TypeDesc.TypeReference> mapToLibraryType(ContextWithFile cx, String name) {
        return switch (name) {
            case "client4XXError" -> Optional.of(getLibraryType(cx, Library.HTTP, "NotFound"));
            case "server5XXError" -> Optional.of(getLibraryType(cx, Library.HTTP, "InternalServerError"));
            case "Client" -> Optional.of(getLibraryType(cx, Library.HTTP, "Client"));
            case "ParameterizedQuery" -> Optional.of(getLibraryType(cx, Library.SQL, "ParameterizedQuery"));
            case "ExecutionResult" -> Optional.of(getLibraryType(cx, Library.SQL, "ExecutionResult"));
            case "Error" -> Optional.of(getLibraryType(cx, Library.SQL, "Error"));
            default -> Optional.empty();
        };
    }

    private BallerinaModel.TypeDesc.TypeReference getLibraryType(ContextWithFile cx, Library library, String typeName) {
        cx.addLibraryImport(library);
        return new BallerinaModel.TypeDesc.TypeReference(library.moduleName + ":" + typeName);
    }

    private Optional<BallerinaModel.TypeDesc.BuiltinType> mapToBuiltinType(String name) {
        return switch (name) {
            case "string" -> Optional.of(BallerinaModel.TypeDesc.BuiltinType.STRING);
            case "integer", "int", "long" -> Optional.of(BallerinaModel.TypeDesc.BuiltinType.INT);
            case "anydata" -> Optional.of(BallerinaModel.TypeDesc.BuiltinType.ANYDATA);
            case "xml" -> Optional.of(XML);
            case "null" -> Optional.of(NIL);
            case "boolean" -> Optional.of(BOOLEAN);
            // TODO: handle base64Binary
            case "base64Binary" -> Optional.of(BallerinaModel.TypeDesc.BuiltinType.INT);
            default -> Optional.empty();
        };
    }

    ContextWithFile getTypeContext() {
        return typeCx;
    }

    boolean addModuleTypeDef(String name, BallerinaModel.ModuleTypeDef moduleTypeDef) {
        return typeCx.addModuleTypeDef(name, moduleTypeDef);
    }

    void incrementTypeCount() {
        typeCount++;
    }

    public String getFromContextFn() {
        utilityIntrinsics.add(Intrinsics.GET_FROM_CONTEXT);
        return Intrinsics.GET_FROM_CONTEXT.name;
    }

    public String getInitContextFn() {
        utilityIntrinsics.add(Intrinsics.INIT_CONTEXT);
        return Intrinsics.INIT_CONTEXT.name;
    }

    public String getPredicateTestFunction() {
        utilityIntrinsics.add(Intrinsics.XPATH_PREDICATE);
        return Intrinsics.XPATH_PREDICATE.name;
    }

    public void addTypeAstNode(String name, ModuleMemberDeclarationNode node) {
        typeCx.addTypeAstNode(name, node);
    }

    public void addTypeDefAsIntrinsic(String content) {
        typeCx.addTypeDefAsIntrinsic(content);
    }

    public void addConfigurableVariable(String name, String source) {
        BallerinaModel.ModuleVar var = BallerinaModel.ModuleVar.configurable(source, STRING);
        utilityVars.put(name, var);
    }

    public BallerinaModel.Expression.VariableReference getHttpClient(String path) {
        if (httpClients.containsKey(path)) {
            return httpClients.get(path);
        }
        importLibraryIfNeededToUtility(HTTP);
        String clientName = "httpClient" + httpClients.size();
        BallerinaModel.ModuleVar client = new BallerinaModel.ModuleVar(clientName, "http:Client",
                exprFrom("checkpanic new (\"%s\")".formatted(path)));
        utilityVars.put(clientName, client);
        var ref = new BallerinaModel.Expression.VariableReference(clientName);
        httpClients.put(path, ref);
        return ref;
    }

    public String getConfigVarName(String varName) {
        var varDecl = utilityVars.get(varName);
        if (varDecl == null) {
            throw new RuntimeException("Failed to find configurable variable for " + varName);
        }
        return varDecl.name();
    }

    Optional<ProcessContext.DefaultClientDetails> getDefaultClientDetails(String processName) {
        Process process = getProcess(processName);
        return getProcessContext(process).getDefaultClient();
    }

    private Process getProcess(String processName) {
        return processContextMap.keySet().stream().filter(proc -> proc.name().equals(processName))
                .findAny()
                .orElseThrow(() -> new IndexOutOfBoundsException("failed to find process" + processName));
    }

    public String getAnonName() {
        return "proj_annon_var" + annonVarCount++;
    }

    public VariableReference getProcessClient(String processName) {
        return new VariableReference(Objects.requireNonNull(processClients.get(processName)));
    }

    public String getToJsonFunction() {
        utilityIntrinsics.add(Intrinsics.XML_PARSER_RESULT);
        utilityIntrinsics.add(Intrinsics.XML_PARSER);
        utilityIntrinsics.add(Intrinsics.TO_JSON);
        return Intrinsics.TO_JSON.name;
    }

    public AnalysisResult getAnalysisResult(Process process) {
        return Objects.requireNonNull(analysisResult.get(process), 
                "Analysis result not found for process: " + process.name());
    }

    public void addXSDSchemaToConversion(Type.Schema schema) {
        schemas.add(schema);
    }

    public Collection<Type.Schema> getXSDSchemas() {
        return schemas;
    }

    record FunctionData(String name, BallerinaModel.TypeDesc inputType, BallerinaModel.TypeDesc returnType) {

        FunctionData {
            assert name != null && !name.isEmpty();
            assert inputType != null;
            assert returnType != null;
        }
    }

    public BallerinaModel.Expression.VariableReference dbClient(String sharedResourcePropertyName) {
        String varName = generatedResources.get(sharedResourcePropertyName);
        if (varName == null) {
            throw new RuntimeException("Failed to find db client for " + sharedResourcePropertyName);
        }
        return new BallerinaModel.Expression.VariableReference(varName);
    }

    public VariableReference httpListener(String name) {
        String varName = generatedResources.get(name);
        if (varName == null) {
            throw new RuntimeException("Failed to find listener for " + name);
        }
        return new BallerinaModel.Expression.VariableReference(varName);
    }

    void addResourceDeclaration(String resourceName, BallerinaModel.ModuleVar resourceVar,
                                Collection<BallerinaModel.ModuleVar> configurables, Collection<Library> imports) {
        imports.forEach(this::importLibraryIfNeededToUtility);
        configurables.forEach(each -> utilityVars.put(each.name(), each));
        utilityVars.put(resourceVar.name(), resourceVar);
        generatedResources.put(resourceName, resourceVar.name());
    }

    void addListnerDeclartion(String resourceName, BallerinaModel.Listener listener,
                              Collection<BallerinaModel.ModuleVar> configurables, Collection<Library> imports) {
        imports.forEach(this::importLibraryIfNeededToUtility);
        configurables.forEach(each -> utilityVars.put(each.name(), each));
        utilityListeners.put(listener.name(), listener);
        generatedResources.put(resourceName, listener.name());
    }

    public String getUtilityVarName(String base) {
        return ConversionUtils.getSanitizedUniqueName(base, utilityVars.keySet());
    }

    public String getAddToContextFn() {
        utilityIntrinsics.add(Intrinsics.ADD_TO_CONTEXT);
        return Intrinsics.ADD_TO_CONTEXT.name;
    }

    public String getResponseFromContextFn() {
        utilityIntrinsics.add(Intrinsics.RESPONSE_FROM_CONTEXT);
        importLibraryIfNeededToUtility(HTTP);
        return Intrinsics.RESPONSE_FROM_CONTEXT.name;
    }

    public String getNamespaceFixFn() {
        utilityIntrinsics.add(Intrinsics.XML_PARSER_RESULT);
        utilityIntrinsics.add(Intrinsics.XML_PARSER);
        utilityIntrinsics.add(Intrinsics.PATCH_XML_NAMESPACES);
        return Intrinsics.PATCH_XML_NAMESPACES.name;
    }

    public String getRenderJsonFn() {
        utilityIntrinsics.add(Intrinsics.XML_PARSER_RESULT);
        utilityIntrinsics.add(Intrinsics.XML_PARSER);
        utilityIntrinsics.add(Intrinsics.RENDER_JSON);
        utilityIntrinsics.add(Intrinsics.TO_JSON);
        return Intrinsics.RENDER_JSON.name;
    }

    public void registerProcessClient(String processName, String clientName) {
        processClients.put(processName, clientName);
    }

    public String getSetJSONResponseFn() {
        utilityIntrinsics.add(Intrinsics.SET_JSON_RESPONSE);
        return Intrinsics.SET_JSON_RESPONSE.name;
    }

    public String getSetXMLResponseFn() {
        utilityIntrinsics.add(Intrinsics.SET_XML_RESPONSE);
        return Intrinsics.SET_XML_RESPONSE.name;
    }

    public String getSetTextResponseFn() {
        utilityIntrinsics.add(Intrinsics.SET_TEXT_RESPONSE);
        return Intrinsics.SET_TEXT_RESPONSE.name;
    }

    public String getParseHeadersFn() {
        utilityIntrinsics.add(Intrinsics.PARSE_HEADERS);
        return Intrinsics.PARSE_HEADERS.name;
    }

    private static class ContextWrapperForTypeFile implements ContextWithFile {

        final Set<BallerinaModel.Import> imports = new HashSet<>();
        private final Map<String, Optional<BallerinaModel.ModuleTypeDef>> moduleTypeDefs = new HashMap<>();
        private final List<String> typeIntrinsics = new ArrayList<>();
        private final Map<String, ModuleMemberDeclarationNode> astNodes = new HashMap<>();
        final ProjectContext cx;

        private ContextWrapperForTypeFile(ProjectContext cx) {
            this.cx = cx;
        }

        @Override
        public boolean hasConstantWithName(String name) {
            return false;
        }

        @Override
        public void addLibraryImport(Library library) {
            imports.add(new BallerinaModel.Import("ballerina", library.moduleName, Optional.empty()));
        }

        @Override
        public BallerinaModel.TypeDesc getTypeByName(String name) {
            return cx.getTypeByName(name, this);
        }

        @Override
        public boolean addModuleTypeDef(String name, BallerinaModel.ModuleTypeDef defn) {
            if (defn.typeDesc() instanceof BallerinaModel.TypeDesc.TypeReference(String name1) && name1.equals(name)) {
                return false;
            }
            addAnnotationsImports(defn.typeDesc());
            addMissingImports(defn.typeDesc());
            this.moduleTypeDefs.put(name, Optional.of(defn));
            return true;
        }

        // Ideally this shouldn't be needed since type reference should add the imports.
        // But for WSDL definitions they
        // are processed in the context of process so we add imports to process (which
        // is problematic, but works for
        // now since they are http) but we add the type definitions to the types files.
        // Need to revisit this and
        // properly fix this.
        private void addMissingImports(BallerinaModel.TypeDesc td) {
            if (td instanceof UnionTypeDesc(Collection<? extends BallerinaModel.TypeDesc> members)) {
                boolean isHttp = members.stream()
                        .filter(member -> member instanceof BallerinaModel.TypeDesc.TypeReference)
                        .map(member -> ((BallerinaModel.TypeDesc.TypeReference) member).name())
                        .anyMatch(name -> name.startsWith("http:"));
                if (isHttp) {
                    importLibraryIfNeeded(Library.HTTP);
                }
            }
        }

        private void addAnnotationsImports(BallerinaModel.TypeDesc td) {
            if (!(td instanceof BallerinaModel.TypeDesc.RecordTypeDesc recordTypeDesc)) {
                return;
            }
            recordTypeDesc.namespace().ifPresent(ignored -> importLibraryIfNeeded(Library.XML_DATA));
            if (recordTypeDesc.fields().stream().anyMatch(recordField -> recordField.namespace().isPresent())) {
                importLibraryIfNeeded(Library.XML_DATA);
            }
        }

        private void importLibraryIfNeeded(Library library) {
            imports.add(new BallerinaModel.Import(library.orgName, library.moduleName, Optional.empty()));
        }

        @Override
        public ProjectContext getProjectContext() {
            return cx;
        }

        @Override
        public void addTypeAstNode(String name, ModuleMemberDeclarationNode node) {
            addLibraryImport(XML_DATA);
            astNodes.put(name, node);
        }

        @Override
        public void addTypeDefAsIntrinsic(String content) {
            typeIntrinsics.add(content);
        }

        public BallerinaModel.TextDocument serialize() {
            List<BallerinaModel.ModuleTypeDef> typeDefs = new ArrayList<>();
            for (Map.Entry<String, Optional<BallerinaModel.ModuleTypeDef>> entry : moduleTypeDefs.entrySet()) {
                if (entry.getValue().isPresent()) {
                    typeDefs.add(entry.getValue().get());
                }
            }
            List<ModuleMemberDeclarationNode> nodes = astNodes.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(Map.Entry::getValue)
                    .toList();
            return new BallerinaModel.TextDocument("types.bal", this.imports.stream().toList(), typeDefs, List.of(),
                    List.of(), List.of(), List.of(), List.of(), typeIntrinsics, nodes);
        }
    }
}
