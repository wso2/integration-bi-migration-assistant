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

public class ResponseFromContext implements ComptimeFunction {
    private static ResponseFromContext instance;

    private final ContextTypeNames typeNames;
    private static final String FUNCTION_NAME = "responseFromContext";

    private ResponseFromContext(ContextTypeNames typeNames) {
        this.typeNames = typeNames;
    }

    public static synchronized ResponseFromContext getInstance(ContextTypeNames typeNames) {
        if (instance == null) {
            instance = new ResponseFromContext(typeNames);
        } else if (!instance.typeNames.equals(typeNames)) {
            throw new IllegalStateException(
                    "ResponseFromContext instance already exists with different ContextTypeNames. " +
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
                function responseFromContext(%s cx) returns http:Response {
                    http:Response httpRes = new;
                    %s? res = cx.response;
                    if res is %s {
                        httpRes.setJsonPayload(res.payload);
                    } else if res is %s {
                        httpRes.setXmlPayload(res.payload);
                    } else if res is %s {
                        httpRes.setTextPayload(res.payload);
                    } else {
                        httpRes.setXmlPayload(cx.result);
                    }

                    if res != () {
                        foreach var header in res.headers.entries() {
                            httpRes.setHeader(header[0], header[1]);
                        }
                    }
                    return httpRes;
                }
                """.formatted(
                typeNames.context(),
                typeNames.response(),
                typeNames.jsonResponse(),
                typeNames.xmlResponse(),
                typeNames.textResponse());
    }
}
