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
import tibco.model.NameSpace;
import tibco.model.Resource;
import tibco.model.Scope;
import tibco.model.Type;

import java.util.Optional;

public class ActivityContext {

    public final ProcessContext processContext;
    private final Scope.Flow.Activity activity;
    private int varCounter = 0;

    ActivityContext(ProcessContext processContext, Scope.Flow.Activity activity) {
        this.activity = activity;
        this.processContext = processContext;
    }

    String getAnnonVarName() {
        return "var" + varCounter++;
    }

    Optional<ProcessContext.DefaultClientDetails> getDefaultClientDetails(String processName) {
        return processContext.getDefaultClientDetails(processName);
    }

    ProjectContext.FunctionData getProcessStartFunctionName(String processName) {
        return processContext.getProcessStartFunction(processName);
    }

    String functionName() {
        return processContext.getAnalysisResult().from(activity).functionName();
    }

    BallerinaModel.Expression.VariableReference contextVarRef() {
        return processContext.contextVarRef();
    }

    BallerinaModel.Expression.VariableReference client(String sharedResourcePropertyName) {
        return processContext.client(sharedResourcePropertyName);
    }

    public String getNamespaceFixFn() {
        return processContext.getNamespaceFixFn();
    }

    public String getResponseFromContextFn() {
        return processContext.getResponseFromContextFn();
    }

    String getAddToContextFn() {
        return processContext.getAddToContextFn();
    }

    String getFromContextFn() {
        return processContext.getFromContextFn();
    }

    void addLibraryImport(Library library) {
        processContext.addLibraryImport(library);
    }

    public String getConfigVarName(String name) {
        return processContext.getConfigVarName(name);
    }

    BallerinaModel.Expression.VariableReference getHttpClient(String path) {
        return processContext.getHttpClient(path);
    }

    public String getJsonToXMLFunction() {
        return processContext.getJsonToXMLFunction();
    }


    String getToXmlFunction() {
        return this.processContext.getToXmlFunction();
    }

    String variableType(String variable) {
        return processContext.variableType(variable);
    }

    String getRenderJsonAsXMLFunction(String type) {
        return processContext.getRenderJsonAsXMLFunction(type);
    }

    BallerinaModel.Expression.VariableReference getProcessClient(String processName) {
        return processContext.getProcessClient(processName);
    }

    public String getToJsonFunction() {
        return processContext.getToJsonFunction();
    }

    public ProjectContext projectContext() {
        return processContext.projectContext;
    }

    public void addXSDSchemaToConversion(Type.Schema schema) {
        projectContext().addXSDSchemaToConversion(schema);
    }

    public BallerinaModel.Expression.VariableReference dbClient(String connection) {
        return projectContext().dbClient(connection);
    }

    public String getSetJSONResponseFn() {
        return processContext.getSetJSONResponseFn();
    }

    public String getSetXMLResponseFn() {
        return processContext.getSetXMLResponseFn();
    }

    public String getSetTextResponseFn() {
        return processContext.getSetTextResponseFn();
    }

    public String getParseHeadersFn() {
        return processContext.getParseHeadersFn();
    }

    public Resource.JMSSharedResource getJmsResource(String connectionReference) {
        return processContext.getJMSResource(connectionReference);
    }

    public Optional<Resource.SharedVariable> getSharedVariableByRelativePath(String relativePath) {
        return processContext.getSharedVariableByRelativePath(relativePath);
    }

    public String getSetSharedVariableFn() {
        return processContext.getSetSharedVariableFn();
    }

    public String getGetSharedVariableFn() {
        return processContext.getGetSharedVariableFn();
    }

    public Optional<NameSpace> getNameSpaceByUri(String uri) {
        return processContext.getNameSpaceByUri(uri);
    }

    public String getFilesInPathFunction() {
        return processContext.getFilesInPathFunction();
    }

    public void registerActivityConversionFailure(tibco.model.Scope.Flow.Activity activity, Exception e) {
        processContext.registerActivityConversionFailure(activity, e);
    }

    public void registerPartiallySupportedActivity(tibco.model.Scope.Flow.Activity activity) {
        processContext.registerPartiallySupportedActivity(activity);
    }
}
