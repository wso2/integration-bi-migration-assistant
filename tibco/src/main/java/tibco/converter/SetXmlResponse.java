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

public class SetXmlResponse implements ComptimeFunction {
    private static SetXmlResponse instance;

    private final ContextTypeNames typeNames;
    private static final String FUNCTION_NAME = "setXMLResponse";

    private SetXmlResponse(ContextTypeNames typeNames) {
        this.typeNames = typeNames;
    }

    public static synchronized SetXmlResponse getInstance(ContextTypeNames typeNames) {
        if (instance == null) {
            instance = new SetXmlResponse(typeNames);
        } else if (!instance.typeNames.equals(typeNames)) {
            throw new IllegalStateException(
                    "SetXmlResponse instance already exists with different ContextTypeNames. " +
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
                function setXMLResponse(%s cx, xml payload, map<string> headers) {
                    %s res = {
                        kind: "XMLResponse",
                        payload: payload.cloneReadOnly(),
                        headers: headers.cloneReadOnly()
                    };
                    cx.response = res;
                }
                """.formatted(typeNames.context(), typeNames.xmlResponse());
    }
}
