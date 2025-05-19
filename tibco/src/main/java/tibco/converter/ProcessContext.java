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
import common.BallerinaModel.Statement.VarDeclStatment;
import io.ballerina.compiler.syntax.tree.ModuleMemberDeclarationNode;
import tibco.TibcoModel;
import tibco.analyzer.AnalysisResult;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static common.BallerinaModel.Expression;
import static common.BallerinaModel.TypeDesc.BuiltinType.ANYDATA;
import static common.BallerinaModel.TypeDesc.BuiltinType.STRING;
import static common.BallerinaModel.TypeDesc.BuiltinType.UnionTypeDesc;
import static common.BallerinaModel.TypeDesc.BuiltinType.XML;
import static common.ConversionUtils.exprFrom;
import static tibco.converter.ConversionUtils.baseName;

public class ProcessContext implements ContextWithFile {

    private final Set<BallerinaModel.Import> imports = new HashSet<>();
    private BallerinaModel.Listener defaultListener = null;
    private final Map<String, BallerinaModel.ModuleVar> constants = new HashMap<>();
    private final Map<String, BallerinaModel.ModuleVar> configurables = new HashMap<>();
    public final TibcoModel.Process process;

    public final ProjectContext projectContext;
    private final Map<TibcoModel.Scope.Flow.Activity.Source.Predicate, String> predicateToFunctionMap = new HashMap<>();
    private final Map<String, String> propertyVariableToResourceMap = new HashMap<>();

    private DefaultClientDetails processClient;
    final Set<TibcoModel.Scope> handledScopes = new HashSet<>();
    final Set<String> intrinsics = new HashSet<>();

    ProcessContext(ProjectContext projectContext, TibcoModel.Process process) {
        this.projectContext = projectContext;
        this.process = process;
    }

    static BallerinaModel.TypeDesc contextType() {
        return new BallerinaModel.TypeDesc.MapTypeDesc(XML);
    }

    VarDeclStatment initContextVar(String paramsVarName) {
        return new VarDeclStatment(contextType(), "context", exprFrom("{...%s}".formatted(paramsVarName)));
    }

    void addResourceVariable(TibcoModel.Variable.PropertyVariable propertyVariable) {
        switch (propertyVariable) {
            case TibcoModel.Variable.PropertyVariable.PropertyReference ref ->
                propertyVariableToResourceMap.put(ref.name(), ref.literal());
            case TibcoModel.Variable.PropertyVariable.SimpleProperty simpleProperty ->
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
            defaultListener = new BallerinaModel.Listener(BallerinaModel.ListenerType.HTTP, listenerRef,
                    Integer.toString(projectContext.allocatePort()),
                    Map.of("host", "localhost"));
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

    void addNameSpace(TibcoModel.NameSpace nameSpace) {
        if (nameSpace.prefix().isEmpty()) {
            return;
        }
        String prefix = nameSpace.prefix().get();
        if (!prefix.chars().allMatch(Character::isLetterOrDigit)) {
            return;
        }
        String decl = "xmlns \"%s\" as %s;".formatted(nameSpace.uri(), prefix);
        intrinsics.add(decl);
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
        return new BallerinaModel.Expression.VariableReference(ConversionUtils.Constants.CONTEXT_VAR_NAME);
    }

    BallerinaModel.Expression.VariableReference client(String sharedResourcePropertyName) {
        String resourceRef = propertyVariableToResourceMap.get(sharedResourcePropertyName);
        if (resourceRef == null) {
            throw new RuntimeException("No shared resource found for " + sharedResourcePropertyName);
        }
        return projectContext.dbClient(resourceRef);
    }

    String getAddToContextFn() {
        return projectContext.getAddToContextFn();
    }

    BallerinaModel.TypeDesc getFileWriteConfigType() {
        return projectContext.getFileWriteConfigType();
    }

    String getFileWriteFunction() {
        return projectContext.getFileWriteFunction(this);
    }

    BallerinaModel.TypeDesc getLogInputType() {
        return projectContext.getLogInputType();
    }

    String getLogFunction() {
        return projectContext.getLogFunction();
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

    String predicateFunction(TibcoModel.Scope.Flow.Activity.Source.Predicate predicate) {
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

    static BallerinaModel.Expression.VariableReference processLevelFnInputVariable() {
        return new BallerinaModel.Expression.VariableReference("input");
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

    String variableType(String variable) {
        return getAnalysisResult().variableType(process, variable);
    }

    String getRenderJsonAsXMLFunction(String type) {
        return projectContext.getRenderJsonAsXMLFunction(type);
    }

    public String getRenderJsonFn() {
        return projectContext.getRenderJsonFn();
    }

    public String getToJsonFunction() {
        return projectContext.getToJsonFunction();
    }

    public AnalysisResult getAnalysisResult() {
        return projectContext.getAnalysisResult(process);
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
}
