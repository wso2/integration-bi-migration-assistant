/*
 *  Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com). All Rights Reserved.
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

public class ReplaceVariableReference implements Transform {

    @Override
    public String transform(TransformContext cx, String path) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("bw:getModuleProperty\\('([^']+)'\\)");
        java.util.regex.Matcher matcher = pattern.matcher(path);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            String propertyName = matcher.group(1);
            String configVarName = cx.getConfigVarName(propertyName);
            matcher.appendReplacement(result, "\\$\\{" + configVarName + "\\}");
        }
        matcher.appendTail(result);
        return result.toString();
    }
}
