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

import java.util.Collection;

public class InitContext implements ComptimeFunction {

    private static final String FUNCTION_NAME = "initContext";

    private final Collection<SharedVariableInfo> sharedVariables;

    public InitContext(Collection<SharedVariableInfo> sharedVariables) {
        this.sharedVariables = sharedVariables;
    }

    @Override
    public String functionName() {
        return FUNCTION_NAME;
    }

    @Override
    public String intrinsify() {
        StringBuilder sharedVarContexts = new StringBuilder();
        StringBuilder sharedVarAssignments = new StringBuilder();

        int index = 1;
        for (SharedVariableInfo sharedVar : sharedVariables) {
            String varName = sharedVar.varName();
            String refName = sharedVar.ref().varName();

            // Generate SharedVariableContext for each shared variable
            sharedVarContexts.append(String.format("""
                    SharedVariableContext sharedVarContext%d = {
                        getter: function() returns xml {
                            return %s;
                        },
                        setter: function(xml value) {
                            %s = value;
                        }
                    };
                    """, index, refName, refName));

            // Generate assignment to sharedVariables map
            sharedVarAssignments.append(String.format("""
                    sharedVariables["%s"] = sharedVarContext%d;
                    """, varName, index));

            index++;
        }

        return String
                .format("""
                                function initContext(map<xml> initVariables = {}, map<SharedVariableContext> jobSharedVariables = {}) returns Context {
                                    map<SharedVariableContext> sharedVariables = {};
                                    %s
                                    %s
                                    foreach var key in jobSharedVariables.keys() {
                                        sharedVariables[key] = jobSharedVariables.get(key);
                                    }
                                    return {variables: initVariables, result: xml `<root/>`, sharedVariables};
                }
                        """,
                        sharedVarContexts.toString(), sharedVarAssignments.toString());
    }
}
