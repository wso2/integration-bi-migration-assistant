/*
 *  Copyright (c) 2026, WSO2 LLC. (http://www.wso2.com).
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
 *  KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package mule.common.apispec;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * An APIkit flow name, {@code method:path[:mediaType]:config}, with the path in spec form. APIkit writes
 * {@code get:\orders\(id):api-config} for {@code GET /orders/{id}}.
 *
 * @param flowName   the flow name as written
 * @param method     lower-case HTTP method
 * @param path       resource path in spec form, e.g. {@code /orders/{id}}
 * @param mediaType  request media type, when the flow handles only one
 * @param configName name of the {@code apikit:config} the flow belongs to
 */
public record ApiKitFlowName(String flowName, String method, String path, Optional<String> mediaType,
                             String configName) {

    private static final Pattern METHOD = Pattern.compile("(?i)get|post|put|delete|patch|head|options|trace");
    private static final Pattern URI_PARAM = Pattern.compile("\\(([^)]+)\\)");

    public ApiKitFlowName {
        assert flowName != null && method != null && path != null && mediaType != null && configName != null;
    }

    public static @NotNull Optional<ApiKitFlowName> parse(String flowName) {
        assert flowName != null;
        String[] parts = flowName.split(":", -1);
        if ((parts.length != 3 && parts.length != 4) || !METHOD.matcher(parts[0]).matches()
                || parts[parts.length - 1].isBlank()) {
            return Optional.empty();
        }
        return Optional.of(new ApiKitFlowName(flowName,
                parts[0].toLowerCase(Locale.ROOT),
                specPath(parts[1]),
                parts.length == 4 ? Optional.of(parts[2].replace('\\', '/')) : Optional.empty(),
                parts[parts.length - 1]));
    }

    private static String specPath(String flowPath) {
        String path = URI_PARAM.matcher(flowPath.replace('\\', '/')).replaceAll("{$1}");
        return path.startsWith("/") ? path : "/" + path;
    }

    public String route() {
        return method.toUpperCase(Locale.ROOT) + " " + path;
    }
}
