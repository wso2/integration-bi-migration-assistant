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

public record RenderJSONAsXML(String type) implements ComptimeFunction {

    private static final String NAME = "renderJsonAs%sXML";
    private static final String FUNCTION = """
            function %1$s(xml value) returns xml|error {
                string jsonString = (value/<jsonString>).data();
                map<json> jsonValue = check jsondata:parseString(jsonString);
                string? namespace = (%2$s).@xmldata:Namespace["uri"];
                return %3$s(jsonValue, namespace, "%2$s");
            }
            """;

    private static final String FUNCTION_IGNORE_NAMESPACE = """
            function %1$s(xml value) returns xml|error {
                string jsonString = (value/<jsonString>).data();
                map<json> jsonValue = check jsondata:parseString(jsonString);
                string? namespace = ();
                return %3$s(jsonValue, namespace, ());
            }
            """;

    @Override
    public String functionName() {
        return NAME.formatted(ConversionUtils.sanitizes(type));
    }

    @Override
    public String intrinsify() {
        if (type.equals(new BallerinaModel.TypeDesc.MapTypeDesc(BallerinaModel.TypeDesc.BuiltinType.JSON).toString())) {
            // Special case for map<json>
            return FUNCTION_IGNORE_NAMESPACE.formatted(functionName(), type, Intrinsics.RENDER_JSON_AS_XML.name);
        }
        return FUNCTION.formatted(functionName(), type, Intrinsics.RENDER_JSON_AS_XML.name);
    }
}
