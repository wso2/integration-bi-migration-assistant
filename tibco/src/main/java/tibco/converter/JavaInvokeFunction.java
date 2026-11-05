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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Ballerina interop declaration of a static Java method called by a TIBCO Java Invoke activity.
 *
 * @param functionName   name of the generated Ballerina function
 * @param className      fully qualified Java class name
 * @param methodName     Java method name
 * @param parameterNames Ballerina identifiers of the method parameters
 * @param parameterTypes Java types of the method parameters, in declaration order
 * @param returnType     Java return type, or empty for {@code void}
 */
record JavaInvokeFunction(String functionName, String className, String methodName, List<String> parameterNames,
                          List<JavaType> parameterTypes, Optional<JavaType> returnType) implements ComptimeFunction {

    JavaInvokeFunction {
        assert parameterNames.size() == parameterTypes.size();
        parameterNames = List.copyOf(parameterNames);
        parameterTypes = List.copyOf(parameterTypes);
    }

    // Including error lets checked exceptions come back as errors, and it is accepted even for methods that
    // declare none; unchecked exceptions still panic and are trapped at the call site.
    @Override
    public String intrinsify() {
        String parameters = IntStream.range(0, parameterTypes.size())
                .mapToObj(index -> parameterTypes.get(index).ballerinaType + " " + parameterNames.get(index))
                .collect(Collectors.joining(", "));
        String paramTypes = parameterTypes.stream()
                .map(type -> type.interopParamType)
                .collect(Collectors.joining(", "));
        return """
                function %s(%s) returns %s = @java:Method {
                    'class: "%s",
                    name: "%s",
                    paramTypes: [%s]
                } external;
                """.formatted(functionName, parameters,
                returnType.map(type -> type.ballerinaType + "|error").orElse("error?"),
                className, methodName, paramTypes);
    }
}
