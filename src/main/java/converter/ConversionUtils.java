package converter;

import org.w3c.dom.Element;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class ConversionUtils {

    private static final Pattern UNESCAPED_SPECIAL_CHAR_SET =
            Pattern.compile("([$&+,:;=\\?@#\\\\|/'\\ \\[\\}\\]<\\>.\"^*{}~`()%!-])");

    /**
     * Converts mule path to a Ballerina resource path.
     *
     * @param path mule path
     * @return ballerina resource path
     */
    static String getBallerinaResourcePath(String path) {
        List<String> list = Arrays.stream(path.split("/")).filter(s -> !s.isEmpty())
                .map(s -> {
                    if (s.startsWith("{") && s.endsWith("}")) {
                        // We come here for mule path params. e.g. foo/{bar}/baz
                        String pathParamName = s.substring(1, s.length() - 1);
                        pathParamName = escapeSpecialCharacters(pathParamName);
                        return "[string " + pathParamName + "]";
                    }
                    return escapeSpecialCharacters(s);
                }).toList();

        String resourcePath;
        if (list.isEmpty()) {
            resourcePath = ".";
        } else {
            resourcePath = String.join("/", list);
        }
        return resourcePath;
    }

    /**
     * Converts mule base path to a Ballerina absolute path.
     *
     * @param basePath mule base path
     * @return ballerina absolute path
     */
    static String getBallerinaAbsolutePath(String basePath) {
        List<String> list = Arrays.stream(basePath.split("/")).filter(s -> !s.isEmpty())
                .map(ConversionUtils::escapeSpecialCharacters).toList();

        String absolutePath;
        if (list.isEmpty()) {
            absolutePath = "/";
        } else {
            absolutePath = "/" + String.join("/", list);
        }
        return absolutePath;
    }

    /**
     * Escape the special characters in an identifier with a preceding `\`.
     *
     * @param identifier encoded identifier string
     * @return decoded identifier
     */
    public static String escapeSpecialCharacters(String identifier) {
        return UNESCAPED_SPECIAL_CHAR_SET.matcher(identifier).replaceAll("\\\\$1");
    }

    static String[] getAllowedMethods(String allowedMethods) {
        if (allowedMethods.isEmpty()) {
            // Leaving empty will allow all methods
            return new String[]{"GET", "POST", "PUT", "DELETE", "PATCH", "HEAD", "OPTIONS", "TRACE", "CONNECT"};
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
        return queryParams.entrySet().stream().map(e -> String.format("%s = \"%s\"", e.getKey(), e.getValue()))
                .reduce((a, b) -> a + ", " + b).orElse("");
    }

    static String convertToBallerinaExpression(MuleToBalConverter.Data data, String muleExpr,
                                               boolean encloseInDoubleQuotes) {
        // Check if the string contains mule expression in form #[]
        if (!muleExpr.contains("#[")) {
            return surroundWithDoubleQuotes(muleExpr);
        }

        // Replace #[...] with ${...}
        String replacedExpr = muleExpr.replaceAll("#\\[([^]]+)]", "\\${$1}");
        return String.format("string `%s`", replacedExpr);
    }

    private static String surroundWithDoubleQuotes(String str) {
        // Escape double quotes in the input string
        String escapedExpr = str.replace("\"", "\\\"");
        return String.format("\"%s\"", escapedExpr);
    }

    public static String getSimpleMuleFlowVar(String muleExpr) {
        String expr = extractSimpleMuleExpr(muleExpr);
        return getVariable(null, expr);
    }

    public static String extractSimpleMuleExpr(String muleExpr) {
        assert muleExpr.startsWith("#[") && muleExpr.endsWith("]");
        return muleExpr.substring(2, muleExpr.length() - 1);
    }

    private static String getVariable(MuleToBalConverter.Data data, String value) {
        String queryParamPrefix = "attributes.queryParams.";
        String varPrefix = "flowVars.";

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

    public static String elementToString(Element element) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(element), new StreamResult(writer));
            return writer.getBuffer().toString();
        } catch (Exception e) {
            throw new RuntimeException("Error converting Element to String", e);
        }
    }

    public static String wrapElementInUnsupportedBlockComment(String input) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n\n");
        sb.append("// TODO: UNSUPPORTED MULE BLOCK ENCOUNTERED. MANUAL CONVERSION REQUIRED.\n");
        sb.append("// ------------------------------------------------------------------------\n");
        String[] lines = input.split("\n");
        for (String line : lines) {
            sb.append("// ").append(line).append("\n");
        }
        sb.append("// ------------------------------------------------------------------------\n\n");
        return sb.toString();
    }
}
