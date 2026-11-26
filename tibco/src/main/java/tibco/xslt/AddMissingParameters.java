/*
 *  Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com).
 *
 *  WSO2 LLC. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at
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

import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AddMissingParameters implements Transform {

    private static final Pattern STYLESHEET_START = Pattern.compile("<(?:[\\w.-]+:)?stylesheet\\b");

    @Override
    public String transform(TransformContext cx, String content) {
        Set<String> parameters = cx.parameters().collect(Collectors.toSet());
        Set<String> missingParameters = cx.parameterUsages()
                .map(each -> each.substring(1))
                .filter(Predicate.not(parameters::contains))
                .collect(Collectors.toSet());
        if (missingParameters.isEmpty()) {
            return content;
        }
        Matcher stylesheetStart = STYLESHEET_START.matcher(content);
        boolean found = stylesheetStart.find();
        assert found : "stylesheet element not found";
        int tagEnd = content.indexOf('>', stylesheetStart.end());
        assert tagEnd != -1 : "unterminated stylesheet tag";
        StringBuilder sb = new StringBuilder();
        sb.append(content, 0, tagEnd + 1);
        for (String each : missingParameters) {
            sb.append("<xsl:param name=\"%s\"/>".formatted(each));
        }
        sb.append(content, tagEnd + 1, content.length());
        return sb.toString();
    }

}
