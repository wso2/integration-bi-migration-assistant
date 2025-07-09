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
import io.ballerina.compiler.syntax.tree.ModuleMemberDeclarationNode;
import org.jetbrains.annotations.NotNull;
import tibco.analyzer.AnalysisResult;
import tibco.model.NameSpace;
import tibco.model.Process;
import tibco.model.Resource;
import tibco.model.Scope;
import tibco.model.Variable;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static common.BallerinaModel.Expression;
import static common.BallerinaModel.TypeDesc.BuiltinType.ANYDATA;
import static common.BallerinaModel.TypeDesc.BuiltinType.STRING;
import static common.BallerinaModel.TypeDesc.BuiltinType.UnionTypeDesc;
import static common.BallerinaModel.TypeDesc.BuiltinType.XML;
import static common.ConversionUtils.exprFrom;
import static tibco.converter.ConversionUtils.baseName;

public class ProcessContext implements ContextWithFile {
    private static final Logger logger = TibcoConverter.logger();

    private final Set<BallerinaModel.Import> imports = new HashSet<>();
    private BallerinaModel.Listener.HTTPListener defaultListener = null;
    private final Map<String, BallerinaModel.ModuleVar> constants = new HashMap<>();
    private final Map<String, BallerinaModel.ModuleVar> configurables = new HashMap<>();
    public final Process process;

    public final ProjectContext projectContext;
    private final Map<Scope.Flow.Activity.Source.Predicate, String> predicateToFunctionMap = new HashMap<>();
    private final Map<String, String> propertyVariableToResourceMap = new HashMap<>();

    private DefaultClientDetails processClient;
    final Set<Scope> handledScopes = new HashSet<>();
    final Set<String> intrinsics = new HashSet<>();
    private final Set<NameSpace> nameSpaces = new HashSet<>();

    ProcessContext(ProjectContext projectContext, Process process) {
        this.projectContext = projectContext;
        this.process = process;
    }

    BallerinaModel.TypeDesc contextType() {
        return projectContext.contextType();
    }

    void addResourceVariable(Variable.PropertyVariable propertyVariable) {
        switch (propertyVariable) {
            case Variable.PropertyVariable.PropertyReference ref ->
                propertyVariableToResourceMap.put(ref.name(), ref.literal());
            case Variable.PropertyVariable.SimpleProperty simpleProperty ->
                projectContext.addConfigurableVariable(simpleProperty.name(), simpleProperty.source());
        }
    }

    String getToXmlFunction() {
        return this.projectContext.getToXmlFunction();
    }

    // TODO: getting name here is redundant
    @Override
    public boolean addModuleTypeDef(String name, BallerinaModel.ModuleTypeDef moduleTypeDef) {
        assert Objects.equals(name, moduleTypeDef.name());
        return this.projectContext.addModuleTypeDef(name, moduleTypeDef);
    }

    @Override
    public ProjectContext getProjectContext() {
        return projectContext;
    }

    @Override
    public void addTypeAstNode(String name, ModuleMemberDeclarationNode node) {
        this.projectContext.addTypeAstNode(name, node);
    }

    @Override
    public void addTypeDefAsIntrinsic(String content) {
        this.projectContext.addTypeDefAsIntrinsic(content);
    }

    public BallerinaModel.TypeDesc getTypeByName(String name) {
        return projectContext.getTypeByName(name, this);
    }

    @NotNull
    public tibco.model.Resource.JMSSharedResource getJMSResource(String fileName) {
        return projectContext.getJMSResource(fileName);
    }

    String declareConstant(String name, String valueRepr, String type) {
        name = ConversionUtils.sanitizes(name);
        BallerinaModel.TypeDesc td = getTypeByName(type);
        assert td == STRING;
        String expr = "\"" + valueRepr + "\"";
        constants.put(name, BallerinaModel.ModuleVar.constant(name, td, exprFrom(expr)));
        return name;
    }

    void declareModuleVar(String name, BallerinaModel.ModuleVar var) {
        constants.put(name, var);
    }

    // TODO: properly handle the on demand part
    void addOnDemandModuleVar(String name, BallerinaModel.ModuleVar var) {
        constants.put(name, var);
    }

    void registerProcessClient(String name) {
        projectContext.registerProcessClient(baseName(process.name()).toLowerCase(), name);
    }

    public Expression.VariableReference getProcessClient(String processName) {
        return projectContext.getProcessClient(baseName(processName).toLowerCase());
    }

    @Override
    public boolean hasConstantWithName(String name) {
        return constants.containsKey(name);
    }

    @Override
    public void addLibraryImport(Library library) {
        imports.add(new BallerinaModel.Import(library.orgName, library.moduleName, Optional.empty()));
    }

    String getDefaultHttpListenerRef() {
        if (defaultListener == null) {
            addLibraryImport(Library.HTTP);
            String listenerRef = ConversionUtils.sanitizes(process.name()) + "_listener";
            defaultListener =
                    new BallerinaModel.Listener.HTTPListener(listenerRef,
                    Integer.toString(projectContext.allocatePort()), "localhost");
        }
        return defaultListener.name();
    }

    BallerinaModel.TextDocument serialize(Collection<BallerinaModel.Service> processServices,
            List<BallerinaModel.Function> functions) {
        String name = ConversionUtils.sanitizes(process.name()) + ".bal";
        List<BallerinaModel.Listener> listeners = defaultListener != null ? List.of(defaultListener) : List.of();
        List<BallerinaModel.ModuleVar> moduleVars = Stream
                .concat(constants.values().stream(), configurables.values().stream()).toList();
        return new BallerinaModel.TextDocument(name, imports.stream().toList(), List.of(),
                moduleVars, listeners, processServices.stream().toList(), functions, List.of(),
                intrinsics.stream().toList(), List.of());
    }

    void addNameSpace(NameSpace nameSpace) {
        if (nameSpace.prefix().isEmpty()) {
            return;
        }
        if (nameSpaces.contains(nameSpace)) {
            return; // already added
        }
        String prefix = nameSpace.prefix().get();
        if (!prefix.chars().allMatch(Character::isLetterOrDigit)) {
            return;
        }
        String decl = "xmlns \"%s\" as %s;".formatted(nameSpace.uri(), prefix);
        intrinsics.add(decl);
        nameSpaces.add(nameSpace);
    }

    ProjectContext.FunctionData getProcessStartFunction() {
        return new ProjectContext.FunctionData(getProcessStartFunctionName(), getProcessInputType(),
                getProcessOutputType());
    }

    String getProcessStartFunctionName() {
        return "start_" + ConversionUtils.sanitizes(process.name());
    }

    String getConvertToTypeFunction(BallerinaModel.TypeDesc targetType) {
        return projectContext.getConvertToTypeFunction(targetType);
    }

    String getTryDataBindToTypeFunction(BallerinaModel.TypeDesc targetType) {
        return projectContext.getTryDataBindToTypeFunction(targetType);
    }

    ProjectContext.FunctionData getProcessStartFunction(String processName) {
        return projectContext.getProcessStartFunction(processName);
    }

    String getJsonToXMLFunction() {
        return projectContext.getJsonToXMLFunction();
    }

    BallerinaModel.Expression.VariableReference contextVarRef() {
        BallerinaModel.TypeDesc.FunctionTypeDesc activityFnType = ConversionUtils.activityFnType(this);
        return activityFnType.parameters().getFirst().ref();
    }

    BallerinaModel.Expression.VariableReference client(String sharedResourcePropertyName) {
        String resourceRef = propertyVariableToResourceMap.get(sharedResourcePropertyName);
        if (resourceRef == null) {
            logger.severe(
                    "No shared resource found for " + sharedResourcePropertyName + ". Returning placeholder client.");
            return new BallerinaModel.Expression.VariableReference("placeholder_client");
        }
        return projectContext.dbClient(resourceRef);
    }

    String getAddToContextFn() {
        return projectContext.getAddToContextFn();
    }

    String getFromContextFn() {
        return projectContext.getFromContextFn();
    }

    String getInitContextFn() {
        return projectContext.getInitContextFn();
    }

    String getXPathFunction() {
        addLibraryImport(Library.XML_DATA);
        return "xmldata:transform";
    }

    BallerinaModel.TypeDesc getProcessInputType() {
        Collection<String> typeNames = getAnalysisResult().inputTypeName(process);
        if (typeNames.isEmpty()) {
            return ANYDATA;
        }
        if (typeNames.size() == 1) {
            return getTypeByName(typeNames.iterator().next());
        }
        return UnionTypeDesc.of(typeNames.stream().map(this::getTypeByName).toArray(BallerinaModel.TypeDesc[]::new));
    }

    BallerinaModel.TypeDesc getProcessOutputType() {
        String typeName = getAnalysisResult().outputTypeName(process);
        if (Objects.equals(typeName, "UNKNOWN")) {
            return ANYDATA;
        }
        return getTypeByName(typeName);
    }

    String predicateFunction(Scope.Flow.Activity.Source.Predicate predicate) {
        return predicateToFunctionMap.computeIfAbsent(predicate, p -> "predicate_" + predicateToFunctionMap.size());
    }

    String getConfigVarName(String varName) {
        return projectContext.getConfigVarName(varName);
    }

    BallerinaModel.Expression.VariableReference getHttpClient(String path) {
        return projectContext.getHttpClient(path);
    }

    void allocatedDefaultClient(List<BallerinaModel.Service> services) {
        if (services.isEmpty() || defaultListener == null) {
            return;
        }
        Optional<BallerinaModel.Service> service = services.stream()
                .filter(each -> !each.resources().isEmpty())
                .filter(each -> each.listenerRefs().contains(defaultListener.name())).findAny();
        if (service.isEmpty()) {
            return;
        }
        BallerinaModel.Service defaultService = service.get();
        String port = defaultListener.port();
        BallerinaModel.Resource defaultResource = defaultService.resources().getFirst();
        String path = ConversionUtils.sanitizePath(defaultService.basePath() + "/" + defaultResource.path());
        Expression initExpr = exprFrom("checkpanic new(\"localhost:%s/%s\")".formatted(port, path));
        processClient = new DefaultClientDetails(new BallerinaModel.ModuleVar(
                ConversionUtils.sanitizes(process.name()) + "_client", "http:Client", Optional.of(initExpr),
                false, false), defaultResource.resourceMethodName());
        String name = processClient.varDecl.name();
        constants.put(name, processClient.varDecl);
    }

    Optional<DefaultClientDetails> getDefaultClient() {
        if (processClient == null) {
            return Optional.empty();
        }
        if (processClient.isUsed) {
            return Optional.of(processClient);
        }
        processClient.isUsed = true;
        return Optional.of(processClient);
    }

    static BallerinaModel.Expression.VariableReference processLevelFnParamVariable() {
        return new BallerinaModel.Expression.VariableReference("params");
    }

    public BallerinaModel.TypeDesc serviceInputType(BallerinaModel.TypeDesc expectedBodyType) {
        return UnionTypeDesc.of(expectedBodyType, XML);
    }

    Optional<DefaultClientDetails> getDefaultClientDetails(String processName) {
        return projectContext.getDefaultClientDetails(processName);
    }

    public String getNamespaceFixFn() {
        return projectContext.getNamespaceFixFn();
    }

    public String getResponseFromContextFn() {
        return projectContext.getResponseFromContextFn();
    }

    String variableType(String variable) {
        return getAnalysisResult().variableType(process, variable);
    }

    String getRenderJsonAsXMLFunction(String type) {
        return projectContext.getRenderJsonAsXMLFunction(type);
    }

    public String getToJsonFunction() {
        return projectContext.getToJsonFunction();
    }

    public AnalysisResult getAnalysisResult() {
        return projectContext.getAnalysisResult(process);
    }

    public void registerTransitionPredicateError(Scope.Flow.Activity.ActivityWithSources activity, Exception e) {
        projectContext.registerTransitionPredicateError(activity, e);
    }

    public void registerControlFlowFunctionGenerationError(Process process, Exception e) {
        projectContext.registerControlFlowFunctionGenerationError(process, e);
    }

    public void registerControlFlowFunctionGenerationError(Scope scope, Exception ex) {
        projectContext.registerControlFlowFunctionGenerationError(scope, ex);
    }

    public void registerPartiallySupportedActivity(tibco.model.Scope.Flow.Activity activity) {
        projectContext.registerPartiallySupportedActivity(activity);
    }

    static final class DefaultClientDetails {
        final BallerinaModel.ModuleVar varDecl;
        final String method;
        boolean isUsed = false;

        DefaultClientDetails(BallerinaModel.ModuleVar varDecl, String method) {
            this.varDecl = varDecl;
            this.method = method;
        }

        Expression.VariableReference ref() {
            assert isUsed;
            return new Expression.VariableReference(varDecl.name());
        }
    }

    String getAnonName() {
        return projectContext.getAnonName();
    }

    public String getSetJSONResponseFn() {
        return projectContext.getSetJSONResponseFn();
    }

    public String getSetXMLResponseFn() {
        return projectContext.getSetXMLResponseFn();
    }

    public String getSetTextResponseFn() {
        return projectContext.getSetTextResponseFn();
    }

    public String getParseHeadersFn() {
        return projectContext.getParseHeadersFn();
    }

    public Optional<Resource.SharedVariable> getSharedVariableByRelativePath(String relativePath) {
        return projectContext.getSharedVariableByRelativePath(relativePath);
    }

    public String getSetSharedVariableFn() {
        return projectContext.getSetSharedVariableFn();
    }

    public String getGetSharedVariableFn() {
        return projectContext.getGetSharedVariableFn();
    }

    public Optional<NameSpace> getNameSpaceByUri(String uri) {
        return nameSpaces.stream()
                .filter(ns -> ns.uri().equals(uri))
                .findFirst();
    }

    public String getFilesInPathFunction() {
        return projectContext.getFilesInPathFunction();
    }

    public void registerUnhandledActivity(tibco.model.Scope.Flow.Activity activity, Exception e) {
        projectContext.registerUnhandledActivity(activity, e);
    }
}
