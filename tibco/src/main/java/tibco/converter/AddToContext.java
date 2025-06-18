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

public class AddToContext implements ComptimeFunction {
    private static AddToContext instance;

    private final ContextTypeNames typeNames;
    private static final String FUNCTION_NAME = "addToContext";

    private AddToContext(ContextTypeNames typeNames) {
        this.typeNames = typeNames;
    }

    public static synchronized AddToContext getInstance(ContextTypeNames typeNames) {
        if (instance == null) {
            instance = new AddToContext(typeNames);
        } else if (!instance.typeNames.equals(typeNames)) {
            throw new IllegalStateException(
                    "AddToContext instance already exists with different ContextTypeNames. " +
                            "Existing: " + instance.typeNames + ", Requested: " + typeNames);
        }
        return instance;
    }

    @Override
    public String functionName() {
        return FUNCTION_NAME;
    }

    @Override
    public String intrinsify() {
        return """
                function addToContext(%s context, string varName, xml value){
                    xml children = value/*;
                    xml transformed = xml `<root>${children}</root>`;
                    context.variables[varName] = transformed;
                    context.result = value;
                }
                """.formatted(typeNames.context());
    }
}
