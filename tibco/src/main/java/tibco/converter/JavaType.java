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

import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Optional;

/**
 * Java parameter and return types of a TIBCO Java Invoke activity that have a Ballerina interop mapping.
 */
enum JavaType {
    STRING("string", "\"java.lang.String\"", "handle"),
    STRING_ARRAY("string[]", "{'class: \"java.lang.String\", dimensions: 1}", "handle"),
    INT("int", "\"int\"", "int"),
    LONG("long", "\"long\"", "int"),
    SHORT("short", "\"short\"", "int"),
    BYTE("byte", "\"byte\"", "int"),
    DOUBLE("double", "\"double\"", "float"),
    FLOAT("float", "\"float\"", "float"),
    BOOLEAN("boolean", "\"boolean\"", "boolean");

    private final String tibcoType;
    final String interopParamType;
    final String ballerinaType;

    JavaType(String tibcoType, String interopParamType, String ballerinaType) {
        this.tibcoType = tibcoType;
        this.interopParamType = interopParamType;
        this.ballerinaType = ballerinaType;
    }

    static @NotNull Optional<JavaType> fromTibcoType(String tibcoType) {
        String normalized = tibcoType.trim().toLowerCase(Locale.ROOT);
        for (JavaType each : values()) {
            if (each.tibcoType.equals(normalized)) {
                return Optional.of(each);
            }
        }
        return Optional.empty();
    }

    boolean isHandle() {
        return ballerinaType.equals("handle");
    }
}
