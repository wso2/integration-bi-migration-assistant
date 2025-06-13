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

public record ConvertToType(String targetType) implements ComptimeFunction {

    private static final String NAME = "convertTo%s";
    private static final String FUNCTION = """
            function convertTo%1$s(anydata input) returns %1$s {
                if input is %1$s {
                    return input;
                }
                if input is xml {
                    return checkpanic xmldata:parseAsType(input);
                }
                if input is json {
                    return checkpanic jsondata:parseAsType(input);
                }
                panic error("Unexpected: unsupported source type for convert to %1$s");
            }
            """;

    public ConvertToType(BallerinaModel.TypeDesc td) {
        this(td.toString());
    }

    @Override
    public String functionName() {
        return NAME.formatted(targetType);
    }

    @Override
    public String intrinsify() {
        return FUNCTION.formatted(targetType);
    }
}
