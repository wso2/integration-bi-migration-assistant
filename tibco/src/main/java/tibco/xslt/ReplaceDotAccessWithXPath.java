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

package tibco.xslt;

public class ReplaceDotAccessWithXPath implements Transform {

    @Override
    public String transformParameter(TransformContext cx, String parameter) {
        assert parameter != null : "Parameter cannot be null";
        assert !parameter.isEmpty() : "Parameter cannot be empty";

        int dotIndex = parameter.indexOf('.');
        if (dotIndex == -1) {
            return parameter;
        }

        return parameter.substring(0, dotIndex);
    }

    @Override
    public String transformParameterUsage(TransformContext cx, String parameterUsage) {
        assert parameterUsage != null : "Parameter usage cannot be null";
        assert parameterUsage.startsWith("$");
        if (!parameterUsage.contains(".")) {
            return parameterUsage;
        }

        return parameterUsage.replaceAll("\\.", "/");
    }
}
