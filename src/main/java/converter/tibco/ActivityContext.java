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
import tibco.TibcoModel;

import java.util.List;
import java.util.Optional;

import static ballerina.BallerinaModel.TypeDesc.BuiltinType.ERROR;
import static ballerina.BallerinaModel.TypeDesc.BuiltinType.XML;

public class ActivityContext {

    public final ProcessContext processContext;
    private final TibcoModel.Scope.Flow.Activity activity;
    private int varCounter = 0;

    ActivityContext(ProcessContext processContext, TibcoModel.Scope.Flow.Activity activity) {
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
        return processContext.analysisResult.from(activity).functionName();
    }

    List<BallerinaModel.Parameter> parameters() {
        return List.of(
                new BallerinaModel.Parameter(ConversionUtils.Constants.CONTEXT_VAR_NAME, ProcessContext.contextType()));
    }

    static BallerinaModel.TypeDesc returnType() {
        return new BallerinaModel.TypeDesc.UnionTypeDesc(List.of(XML, ERROR));
    }

    BallerinaModel.Expression.VariableReference contextVarRef() {
        return processContext.contextVarRef();
    }

    BallerinaModel.Expression.VariableReference client(String sharedResourcePropertyName) {
        return processContext.client(sharedResourcePropertyName);
    }

    String getConvertToTypeFunction(BallerinaModel.TypeDesc targetType) {
        return processContext.getConvertToTypeFunction(targetType);
    }

    String getAddToContextFn() {
        return processContext.getAddToContextFn();
    }

    void addLibraryImport(Library library) {
        processContext.addLibraryImport(library);
    }

    BallerinaModel.TypeDesc getFileWriteConfigType() {
        return processContext.getFileWriteConfigType();
    }

    String getFileWriteFunction() {
        return processContext.getFileWriteFunction();
    }

    BallerinaModel.TypeDesc getLogInputType() {
        return processContext.getLogInputType();
    }

    String getLogFunction() {
        return processContext.getLogFunction();
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
}
