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

package converter.tibco;

import java.util.Collection;

public final class ConversionUtils {

    private ConversionUtils() {
    }

    public static String sanitizes(String name) {
        String sanitized = name.replaceAll("[^a-zA-Z0-9]", "_");
        while (!Character.isAlphabetic(sanitized.charAt(0))) {
            sanitized = sanitized.substring(1);
        }
        return sanitized;
    }

    public static String getSanitizedUniqueName(String name, Collection<String> allocatedNames) {
        String sanitized = sanitizes(name);
        String nameToCheck = sanitized;
        if (allocatedNames.contains(nameToCheck)) {
            nameToCheck = sanitized + "_" + allocatedNames.size();
        }
        return nameToCheck;
    }
}
