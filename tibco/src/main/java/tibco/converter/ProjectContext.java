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
import common.LoggingUtils;
import io.ballerina.compiler.syntax.tree.ModuleMemberDeclarationNode;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Element;
import tibco.LoggingContext;
import tibco.ProjectConversionContext;
import tibco.TibcoToBalConverter;
import tibco.analyzer.AnalysisResult;
import tibco.analyzer.TibcoAnalysisReport.PartiallySupportedActivityElement;
import tibco.analyzer.TibcoAnalysisReport.PartiallySupportedActivityElement.NamedPartiallySupportedActivityElement;
import tibco.analyzer.TibcoAnalysisReport.PartiallySupportedActivityElement.UnNamedPartiallySupportedActivityElement;
import tibco.analyzer.TibcoAnalysisReport.UnhandledActivityElement;
import tibco.analyzer.TibcoAnalysisReport.UnhandledActivityElement.NamedUnhandledActivityElement;
import tibco.analyzer.TibcoAnalysisReport.UnhandledActivityElement.UnNamedUnhandledActivityElement;
import tibco.model.Process;
import tibco.model.Process5;
import tibco.model.Process5.ExplicitTransitionGroup.InlineActivity;
import tibco.model.Resource;
import tibco.model.Scope;
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
import java.util.function.Predicate;
import java.util.stream.Collectors;
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
import static tibco.converter.Library.JSON_DATA;
import static tibco.converter.Library.XML_DATA;

public class ProjectContext implements LoggingContext {

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
    // TODO: We need to fix this so logging works correctly even for single files.
    private final ProjectConversionContext conversionContext;
    private final Map<String, String> generatedResources = new HashMap<>();
    private final Map<String, BallerinaModel.Expression.VariableReference> httpClients = new HashMap<>();
    private final Map<BallerinaModel.TypeDesc, String> dataBindingFunctions = new HashMap<>();
    private final Map<String, String> renderJsonAsXMLFunction = new HashMap<>();
    private final Map<String, Resource.JMSSharedResource> jmsResourceMap = new HashMap<>();
    private final Map<Process, AnalysisResult> analysisResult;
    private Collection<Type.Schema> schemas = new ArrayList<>();
    private ContextTypeNames contextTypeNames = null;
    private final Set<Resource.SharedVariable> sharedVariables = new HashSet<>();
    private final Set<UnhandledActivityElement> unhandledActivities = new HashSet<>();
    private final Set<PartiallySupportedActivityElement> partiallySupportedActivities = new HashSet<>();

    ProjectContext(ProjectConversionContext conversionContext,
                   Map<Process, AnalysisResult> analysisResult) {
        this.conversionContext = conversionContext;
        this.analysisResult = analysisResult;
    }

    @Override
    public void log(LoggingUtils.Level level, String message) {
        conversionContext.log(level, message);
    }

    @Override
    public void logState(String message) {
        conversionContext.logState(message);
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
        log(LoggingUtils.Level.INFO, String.format("Type Statistics - Total Types: %d", typeCount));
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
        ContextTypeNames contextTypeNames = getContextTypeNames();
        BallerinaModel.TypeDesc.TypeReference responseTy =
                getOrCreateUtilityTypeDef(contextTypeNames.response(), ConversionUtils.Constants.RESPONSE_TYPE_DESC);
        getOrCreateUtilityTypeDef(contextTypeNames.jsonResponse(), ConversionUtils.jsonResponseTypeDesc(responseTy));
        getOrCreateUtilityTypeDef(contextTypeNames.xmlResponse(), ConversionUtils.xmlResponseTypeDesc(responseTy));
        getOrCreateUtilityTypeDef(contextTypeNames.textResponse(), ConversionUtils.textResponseTypeDesc(responseTy));

        // Create SharedVariableContext type
        BallerinaModel.TypeDesc getterFunctionType = new BallerinaModel.TypeDesc.FunctionTypeDesc(
                List.of(), XML);
        BallerinaModel.TypeDesc setterFunctionType = new BallerinaModel.TypeDesc.FunctionTypeDesc(
                List.of(new BallerinaModel.Parameter("value", XML)), NIL);
        BallerinaModel.TypeDesc sharedVariableContextType = new BallerinaModel.TypeDesc.RecordTypeDesc(
                List.of(
                        new BallerinaModel.TypeDesc.RecordTypeDesc.RecordField("getter", getterFunctionType),
                        new BallerinaModel.TypeDesc.RecordTypeDesc.RecordField("setter", setterFunctionType)));
        getOrCreateUtilityTypeDef("SharedVariableContext", sharedVariableContextType);

        return getOrCreateUtilityTypeDef(contextTypeNames.context(), new BallerinaModel.TypeDesc.RecordTypeDesc(
                List.of(
                        new BallerinaModel.TypeDesc.RecordTypeDesc.RecordField("variables",
                                new BallerinaModel.TypeDesc.MapTypeDesc(XML)),
                        new BallerinaModel.TypeDesc.RecordTypeDesc.RecordField("result", XML),
                        new BallerinaModel.TypeDesc.RecordTypeDesc.RecordField("response", responseTy, true),
                        new BallerinaModel.TypeDesc.RecordTypeDesc.RecordField("sharedVariables",
                                new BallerinaModel.TypeDesc.MapTypeDesc(
                                        new BallerinaModel.TypeDesc.TypeReference("SharedVariableContext"))))));
    }

    private BallerinaModel.TypeDesc.TypeReference getOrCreateUtilityTypeDef(String typeName,
            BallerinaModel.TypeDesc typeDesc) {
        return utilityTypeDefs.computeIfAbsent(typeName,
                name -> new BallerinaModel.ModuleTypeDef(name, typeDesc))
                .typeDesc() instanceof BallerinaModel.TypeDesc.TypeReference ref ? ref
                        : new BallerinaModel.TypeDesc.TypeReference(typeName);
    }

    private void importLibraryIfNeededToUtility(Library library) {
        utilityFunctionImports.add(new BallerinaModel.Import(library.orgName, library.moduleName, Optional.empty()));
    }

    public void addJavaDependency(TibcoToBalConverter.JavaDependencies dependencies) {
        conversionContext.addJavaDependency(dependencies);
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
        ContextTypeNames typeNames = getContextTypeNames();
        ComptimeFunction getFromContext = new GetFromContext(typeNames);
        utilityCompTimeFunctions.add(getFromContext);
        return getFromContext.functionName();
    }

    public String getInitContextFn() {
        Collection<SharedVariableInfo> sharedVariables = getProjectSharedVariables().map(this::addProjectSharedVariable)
                .toList();
        ComptimeFunction initContext = new InitContext(sharedVariables);
        utilityCompTimeFunctions.add(initContext);
        return initContext.functionName();
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

    public void addJMSResource(Resource.JMSSharedResource jmsResource) {
        String fileName = ConversionUtils.extractFileName(jmsResource.fileName());
        jmsResourceMap.put(fileName, jmsResource);
    }

    public Resource.JMSSharedResource getJMSResource(String fileName) {
        String extractedFileName = ConversionUtils.extractFileName(fileName);
        Resource.JMSSharedResource resource = jmsResourceMap.get(extractedFileName);
        if (resource == null) {
            log(LoggingUtils.Level.SEVERE,
                    "JMS shared resource not found for file: " + fileName + ". Returning placeholder resource.");
            return new Resource.JMSSharedResource("placeholder_" + extractedFileName, "placeholder",
                    new Resource.JMSSharedResource.NamingEnvironment(false, "", "", "", "", "", "", ""),
                    new Resource.JMSSharedResource.ConnectionAttributes(Optional.empty(), Optional.empty(),
                            Optional.empty(), false),
                    Map.of());
        }
        return resource;
    }

    public void addSharedVariable(Resource.SharedVariable sharedVariable) {
        sharedVariables.add(sharedVariable);
    }

    public Stream<Resource.SharedVariable> getProjectSharedVariables() {
        return sharedVariables.stream()
                .filter(Resource.SharedVariable::isShared);
    }

    public Stream<Resource.SharedVariable> getJobSharedVariables() {
        return sharedVariables.stream()
                .filter(Predicate.not(Resource.SharedVariable::isShared));
    }

    public Optional<Resource.SharedVariable> getSharedVariableByRelativePath(String relativePath) {
        return sharedVariables.stream()
                .filter(sv -> sv.relativePath().equals(relativePath))
                .findFirst();
    }

    private SharedVariableInfo addProjectSharedVariable(Resource.SharedVariable sharedVariable) {
        assert sharedVariable.isShared() : "job shared variables must be declared within service";
        assert !sharedVariable.initialValue().isBlank() : "Initial value should be a valid XML";
        String name = ConversionUtils.getSanitizedUniqueName(sharedVariable.name(), utilityVars.keySet());
        BallerinaModel.ModuleVar var = new BallerinaModel.ModuleVar(name, XML,
                new BallerinaModel.Expression.XMLTemplate(sharedVariable.initialValue()));
        utilityVars.put(name, var);
        return new SharedVariableInfo(sharedVariable.name(), new VariableReference(name));
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
            log(LoggingUtils.Level.SEVERE,
                    "Failed to find configurable variable for " + varName + ". Returning placeholder name.");
            return "placeholder_" + varName;
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
                .orElseGet(() -> {
                    log(LoggingUtils.Level.SEVERE,
                            "Failed to find process: " + processName + ". Returning placeholder process.");
                    return new Process5("placeholder_" + processName, List.of(),
                            new Process5.ExplicitTransitionGroup());
                });
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

    public void registerServiceGenerationError(Process process, Exception e) {
        log(LoggingUtils.Level.SEVERE,
                "Failed to generate service for process: " + process.name() + ". Error: " + e.getMessage());
        log(LoggingUtils.Level.SEVERE, "Please check the process definition and ensure it is supported.");
    }

    public void registerUnhandledActivity(tibco.model.Scope.Flow.Activity activity, Exception e) {
        String fileName = activity.fileName();
        String name;
        if (activity instanceof InlineActivity inlineActivity) {
            name = inlineActivity.name();
            String type = inlineActivity.type().name();
            Element element = inlineActivity.element();
            unhandledActivities.add(new NamedUnhandledActivityElement(name, type, element, fileName));
        } else {
            name = "<unnamed>";
            unhandledActivities.add(new UnNamedUnhandledActivityElement(activity.element(), fileName));
        }
        log(LoggingUtils.Level.SEVERE, "Failed to convert activity: " + name + ". Error: " + e.getMessage());
    }

    public void registerTransitionPredicateError(Scope.Flow.Activity.ActivityWithSources activity, Exception e) {
        log(LoggingUtils.Level.SEVERE, "Failed to convert transition predicate for activity: "
                + (activity != null ? activity.toString() : "<null>") + ". Error: " + e.getMessage());
    }

    public void registerControlFlowFunctionGenerationError(Process process, Exception e) {
        log(LoggingUtils.Level.SEVERE,
                "Failed to generate control flow function for process: " + process.name() + ". Error: "
                + e.getMessage());
    }

    public void registerControlFlowFunctionGenerationError(Scope scope, Exception ex) {
        log(LoggingUtils.Level.SEVERE,
                "Failed to generate control flow function for scope: " + scope.name() + ". Error: "
                + ex.getMessage());
    }

    public void registerPartiallySupportedActivity(tibco.model.Scope.Flow.Activity activity) {
        String fileName = activity.fileName();
        String name;
        if (activity instanceof InlineActivity inlineActivity) {
            name = inlineActivity.name();
            String type = inlineActivity.type().name();
            Element element = inlineActivity.element();
            partiallySupportedActivities.add(new NamedPartiallySupportedActivityElement(name, type, element, fileName));
        } else {
            name = "<unnamed>";
            partiallySupportedActivities.add(
                    new UnNamedPartiallySupportedActivityElement(activity.element(), fileName));
        }
        log(LoggingUtils.Level.WARN, "Partially supported activity: " + name);
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
            log(LoggingUtils.Level.SEVERE,
                    "Failed to find db client for " + sharedResourcePropertyName + ". Returning placeholder client.");
            return new BallerinaModel.Expression.VariableReference("placeholder_db_client");
        }
        return new BallerinaModel.Expression.VariableReference(varName);
    }

    public VariableReference httpListener(String name) {
        String varName = generatedResources.get(name);
        if (varName == null) {
            log(LoggingUtils.Level.SEVERE, "Failed to find listener for " + name + ". Returning placeholder listener.");
            return new BallerinaModel.Expression.VariableReference("placeholder_listener");
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
        ContextTypeNames typeNames = getContextTypeNames();
        ComptimeFunction addToContext = new AddToContext(typeNames);
        utilityCompTimeFunctions.add(addToContext);
        return addToContext.functionName();
    }

    public String getResponseFromContextFn() {
        ContextTypeNames typeNames = getContextTypeNames();
        ComptimeFunction responseFromContext = new ResponseFromContext(typeNames);
        utilityCompTimeFunctions.add(responseFromContext);
        importLibraryIfNeededToUtility(HTTP);
        return responseFromContext.functionName();
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
        ContextTypeNames typeNames = getContextTypeNames();
        ComptimeFunction setJsonResponse = new SetJsonResponse(typeNames);
        utilityCompTimeFunctions.add(setJsonResponse);
        return setJsonResponse.functionName();
    }

    public String getSetXMLResponseFn() {
        ContextTypeNames typeNames = getContextTypeNames();
        ComptimeFunction setXmlResponse = new SetXmlResponse(typeNames);
        utilityCompTimeFunctions.add(setXmlResponse);
        return setXmlResponse.functionName();
    }

    public String getSetTextResponseFn() {
        ContextTypeNames typeNames = getContextTypeNames();
        ComptimeFunction setTextResponse = new SetTextResponse(typeNames);
        utilityCompTimeFunctions.add(setTextResponse);
        return setTextResponse.functionName();
    }

    private ContextTypeNames getContextTypeNames() {
        if (contextTypeNames == null) {
            contextTypeNames = new ContextTypeNames(
                    getTypeName("Context"), getTypeName("Response"), getTypeName("JSONResponse"),
                    getTypeName("XMLResponse"), getTypeName("TextResponse"));
        }
        return contextTypeNames;
    }

    private String getTypeName(String name) {
        Set<String> used =
                analysisResult.values().stream().map(AnalysisResult::getTypeNames).flatMap(Set::stream).collect(
                        Collectors.toSet());
        return ConversionUtils.getSanitizedUniqueName(name, used);
    }

    public String getParseHeadersFn() {
        utilityIntrinsics.add(Intrinsics.PARSE_HEADERS);
        return Intrinsics.PARSE_HEADERS.name;
    }

    public String getSetSharedVariableFn() {
        utilityIntrinsics.add(Intrinsics.SET_SHARED_VARIABLE);
        return Intrinsics.SET_SHARED_VARIABLE.name;
    }

    public String getGetSharedVariableFn() {
        utilityIntrinsics.add(Intrinsics.GET_SHARED_VARIABLE);
        return Intrinsics.GET_SHARED_VARIABLE.name;
    }

    public String getFilesInPathFunction() {
        utilityIntrinsics.add(Intrinsics.GET_FILES_IN_PATH);
        utilityTypeDefs.put("FileData", new BallerinaModel.ModuleTypeDef("FileData",
                new BallerinaModel.TypeDesc.RecordTypeDesc(List.of(
                        new BallerinaModel.TypeDesc.RecordTypeDesc.RecordField("fileName", STRING),
                        new BallerinaModel.TypeDesc.RecordTypeDesc.RecordField("fullName", STRING)))));
        importLibraryIfNeededToUtility(Library.FILE);
        importLibraryIfNeededToUtility(Library.IO);
        importLibraryIfNeededToUtility(Library.REGEX);
        return Intrinsics.GET_FILES_IN_PATH.name;
    }

    private static class ContextWrapperForTypeFile implements ContextWithFile, LoggingContext {

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

        @Override
        public void log(LoggingUtils.Level level, String message) {
            cx.log(level, message);
        }

        @Override
        public void logState(String message) {
            cx.logState(message);
        }
    }

    public void registerResourceConversionFailure(Resource resource) {
        log(LoggingUtils.Level.SEVERE, "failed to convert resource: " + resource.name()
                + ". Please check the resource definition and ensure it is supported.");
    }

    public Set<UnhandledActivityElement> getUnhandledActivities() {
        return new HashSet<>(unhandledActivities);
    }

    public Set<PartiallySupportedActivityElement> getPartiallySupportedActivities() {
        return new HashSet<>(partiallySupportedActivities);
    }
}
