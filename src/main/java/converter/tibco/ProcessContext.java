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
import ballerina.BallerinaModel.Statement.VarDeclStatment;
import converter.tibco.analyzer.AnalysisResult;
import converter.tibco.analyzer.ModelAnalyser;
import tibco.TibcoModel;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static ballerina.BallerinaModel.TypeDesc.BuiltinType.ANYDATA;
import static ballerina.BallerinaModel.TypeDesc.BuiltinType.XML;

public class ProcessContext implements ContextWithFile {

    static final String CONTEXT_VAR_NAME = "context";
    private final Set<BallerinaModel.Import> imports = new HashSet<>();
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
    private final Set<TibcoModel.Scope.Flow.Activity> activitiesWithErrorTransitions = new HashSet<>();

    private static final Logger logger = Logger.getLogger(ProcessContext.class.getName());

    ProcessContext(ProjectContext projectContext, TibcoModel.Process process) {
        this.projectContext = projectContext;
        this.process = process;
        this.analysisResult = ModelAnalyser.analyseProcess(process);
    }

    public static BallerinaModel.TypeDesc contextType() {
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

    // TODO: getting name here is redundant
    public boolean addModuleTypeDef(String name, BallerinaModel.ModuleTypeDef moduleTypeDef) {
        assert Objects.equals(name, moduleTypeDef.name());
        return this.projectContext.addModuleTypeDef(name, moduleTypeDef);
    }

    @Override
    public ProjectContext getProjectContext() {
        return projectContext;
    }

    public BallerinaModel.TypeDesc getTypeByName(String name) {
        return projectContext.getTypeByName(name, this);
    }

    String declareConstant(String name, String valueRepr, String type) {
        name = ConversionUtils.sanitizes(name);
        BallerinaModel.TypeDesc td = getTypeByName(type);
        assert td == BallerinaModel.TypeDesc.BuiltinType.STRING;
        String expr = "\"" + valueRepr + "\"";
        constants.put(name,
                BallerinaModel.ModuleVar.constant(name, td, new BallerinaModel.BallerinaExpression(expr)));
        return name;
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
        if (defaultListner == null) {
            addLibraryImport(Library.HTTP);
            String listenerRef = ConversionUtils.sanitizes(process.name()) + "_listener";
            defaultListner = new BallerinaModel.Listener(BallerinaModel.ListenerType.HTTP, listenerRef,
                    Integer.toString(projectContext.allocatePort()),
                    Map.of("host", "localhost"));
        }
        return defaultListner.name();

    }

    BallerinaModel.TextDocument serialize(Collection<BallerinaModel.Service> processServices,
                                          List<BallerinaModel.Function> functions) {
        String name = ConversionUtils.sanitizes(process.name()) + ".bal";
        List<BallerinaModel.Listener> listeners = defaultListner != null ? List.of(defaultListner) : List.of();
        List<BallerinaModel.ModuleVar> moduleVars =
                Stream.concat(constants.values().stream(), configurables.values().stream()).toList();
        return new BallerinaModel.TextDocument(name, imports.stream().toList(), List.of(),
                moduleVars, listeners, processServices.stream().toList(), functions, List.of());
    }

    ProjectContext.FunctionData getProcessStartFunction() {

        if (processInputType == null || processReturnType == null) {
            logger.warning(String.format(
                    "Can't determine input/output type for process start function %s, " +
                            "maybe failed to handle start activity?",
                    getProcessStartFunctionName()));
            if (processInputType == null) {
                processInputType = ANYDATA;
            }
            if (processReturnType == null) {
                processReturnType = ANYDATA;
            }
        }
        return new ProjectContext.FunctionData(getProcessStartFunctionName(), processInputType,
                processReturnType);
    }

    public String getProcessStartFunctionName() {
        return ConversionUtils.sanitizes(process.name()) + "_start";
    }

    public String getProcessFunction() {
        return "process_" + ConversionUtils.sanitizes(process.name());
    }

    public String getConvertToTypeFunction(BallerinaModel.TypeDesc targetType) {
        return typeConversionFunction.computeIfAbsent(targetType, this::createConvertToTypeFunction);
    }

    private String createConvertToTypeFunction(BallerinaModel.TypeDesc targetType) {
        return projectContext.createConvertToTypeFunction(targetType);
    }

    public ProjectContext.FunctionData getProcessStartFunction(String processName) {
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

    public BallerinaModel.Expression.VariableReference dbClient(String sharedResourcePropertyName) {
        return projectContext.dbClient(sharedResourcePropertyName);
    }

    public String getAddToContextFn() {
        return projectContext.getAddToContextFn();
    }

    public String getTransformXSLTFn() {
        return projectContext.getTransformXSLTFn();
    }

    public BallerinaModel.TypeDesc getFileWriteConfigType() {
        return projectContext.getFileWriteConfigType();
    }

    public String getFileWriteFunction() {
        return projectContext.getFileWriteFunction(this);
    }

    BallerinaModel.TypeDesc getLogInputType() {
        return projectContext.getLogInputType();
    }

    String getLogFunction() {
        return projectContext.getLogFunction();
    }

    public String getPredicateTestFunction() {
        return projectContext.getPredicateTestFunction();
    }

    public String errorHandlerWorkerName() {
        return "errorHandler";
    }

    public void markAsPossibleErrorTransition(TibcoModel.Scope.Flow.Activity activity) {
        activitiesWithErrorTransitions.add(activity);
    }

    public Set<TibcoModel.Scope.Flow.Activity> activitiesWithErrorTransitions() {
        return Collections.unmodifiableSet(activitiesWithErrorTransitions);
    }
}
