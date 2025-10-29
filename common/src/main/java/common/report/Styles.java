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

package common.report;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public record Styles(Map<String, Map<String, String>> styles) {

    public Styles merge(Styles other) {
        Map<String, Map<String, String>> mergedStyles = new LinkedHashMap<>(this.styles);

        for (Map.Entry<String, Map<String, String>> classEntry : other.styles.entrySet()) {
            String className = classEntry.getKey();
            Map<String, String> otherProperties = classEntry.getValue();

            Map<String, String> existingProperties = mergedStyles.getOrDefault(className, new HashMap<>());
            Map<String, String> mergedProperties = new HashMap<>(existingProperties);

            for (Map.Entry<String, String> propertyEntry : otherProperties.entrySet()) {
                String propertyName = propertyEntry.getKey();
                String propertyValue = propertyEntry.getValue();

                if (mergedProperties.containsKey(propertyName)) {
                    String existingValue = mergedProperties.get(propertyName);
                    if (!existingValue.equals(propertyValue)) {
                        throw new IllegalArgumentException(
                                String.format(
                                        "Style conflict for class '%s': property '%s' has conflicting values '%s' vs '%s'",
                                        className, propertyName, existingValue, propertyValue));
                    }
                } else {
                    mergedProperties.put(propertyName, propertyValue);
                }
            }

            mergedStyles.put(className, mergedProperties);
        }

        return new Styles(mergedStyles);
    }

    public String toHTML() {
        StringBuilder html = new StringBuilder();
        for (Map.Entry<String, Map<String, String>> classEntry : styles.entrySet()) {
            String selector = classEntry.getKey();
            Map<String, String> properties = classEntry.getValue();

            html.append(selector).append(" {\n");
            for (Map.Entry<String, String> propertyEntry : properties.entrySet()) {
                html.append("  ").append(propertyEntry.getKey()).append(": ").append(propertyEntry.getValue())
                        .append(";\n");
            }
            html.append("}\n");
        }
        return html.toString();
    }
}
