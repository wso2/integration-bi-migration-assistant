package converter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils {
    static String normalizedResourcePath(String path) {
        // TODO: handle dash e.g. main-constract
        List<String> list = Arrays.stream(path.split("/")).filter(s -> !s.isEmpty())
                .map(s -> {
                    if (s.startsWith("{") && s.endsWith("}")) {
                        return "[string " + s.substring(1, s.length() - 1) + "]";
                    }
                    return s;
                }).toList();

        String newPath = String.join("/", list);
        return newPath;
    }

    static String[] getAllowedMethods(String allowedMethods) {
        if (allowedMethods.isEmpty()) {
            // Leaving empty will allow all
            // TODO: check and support other methods
            return new String[]{"GET", "POST"};
        }
        return allowedMethods.split(",\\s*");
    }

    static String insertLeadingSlash(String basePath) {
        return basePath.startsWith("/") ? basePath : "/" + basePath;
    }

    static String removeLeadingSlash(String resourcePath) {
        return resourcePath.startsWith("/") ? resourcePath.substring(1) : resourcePath;
    }

    static Map<String, String> processQueryParams(String queryParams) {
        assert queryParams.endsWith("}]");
        String regex = "#\\[output .*\\n---\\n\\{\\n|\\n}]";
        String trimmed = queryParams.replaceAll(regex, "").trim();
        String[] pairs = trimmed.split(",\\n\\t");
        Map<String, String> keyValues = new HashMap<>(pairs.length);
        for (String pair : pairs) {
            String[] kv = pair.split(":");
            keyValues.put(kv[0].trim().replace("\"", ""), kv[1].trim().replace("\"", ""));
        }
        return keyValues;
    }

    static String genQueryParam(Map<String, String> queryParams) {
        return queryParams.entrySet().stream()
                .map(e -> String.format("%s = \"%s\"", e.getKey(), e.getValue())).reduce((a, b) -> a + ", " + b).orElse("");
    }

    static String normalizedFromMuleExpr(Main.Data data, String muleExpr, boolean encloseInDoubleQuotes) {
        if (muleExpr.startsWith("#[") && muleExpr.endsWith("]")) {
            var innerExpr = muleExpr.substring(2, muleExpr.length() - 1);
            return getVariable(data, innerExpr);
        }
        return encloseInDoubleQuotes? "\"" + muleExpr + "\"" : muleExpr;
    }

    private static String getVariable(Main.Data data, String value) {
        String queryParamPrefix = "attributes.queryParams.";
        String varPrefix = "vars.";

        String v;
        if (value.startsWith(queryParamPrefix)) {
            v = value.substring(queryParamPrefix.length());
            data.queryParams.add(v);
        } else if (value.startsWith(varPrefix)) {
            v = value.substring(varPrefix.length());
        } else {
            v = value;
        }
        return v;
    }
}
