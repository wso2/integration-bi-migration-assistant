package converter;

import org.w3c.dom.Element;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import static ballerina.BallerinaModel.BallerinaType;
import static ballerina.BallerinaModel.BallerinaExpression;
import static ballerina.BallerinaModel.RecordField;
import static ballerina.BallerinaModel.RecordType;
import static ballerina.BallerinaModel.BallerinaStatement;

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
        Map<String, String> keyValues = new LinkedHashMap<>(pairs.length);
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

    public static String convertMuleExprToBal(String melExpression) {
        return convertMuleExprToBal(melExpression, false);
    }

    public static String convertMuleExprToBalStringLiteral(String melExpression) {
        return convertMuleExprToBal(melExpression, true);
    }

    private static String convertMuleExprToBal(String melExpression, boolean addToStringCalls) {
        if (melExpression.startsWith("#[") && melExpression.endsWith("]")) {
            return convertMELToBal(melExpression, addToStringCalls);
        }

        String regex = "#\\[([^]]+)]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(melExpression);

        boolean hasMELParts = false;
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            hasMELParts = true;
            String matchedExpression = matcher.group(0);
            String replacement = "\\${" + convertMELToBal(matchedExpression, true) + "}";
            matcher.appendReplacement(result, replacement);
        }
        matcher.appendTail(result);

        return hasMELParts ? String.format("string `%s`", result) : "\"" + melExpression.replace("\"", "\\\"") + "\"";
    }

    private static String convertMELToBal(String melExpression, boolean addToStringCalls) {
        // Remove #[] wrapper from the MEL expression
        String result = melExpression.substring(2, melExpression.length() - 1);

        // Replace string literals: 'xxx' --> "xxx"
        result = result.replaceAll("'(.*?)'", "\"$1\"");

        // Replace payload: payload --> ctx.payload
        result = result.replaceAll("\\b(payload)\\b", addToStringCalls ? "ctx.$1.toString()" : "ctx.$1");

        // Replace variable references: flowVars.foo --> ctx.flowVars.foo / ctx.flowVars.foo.toString()
        result = result.replaceAll("\\b(flowVars|sessionVars|message|recordVars)(\\.\\w+)\\b",
                addToStringCalls ? "ctx.$1$2.toString()" : "ctx.$1$2");

        return result;
    }

    public static String inferTypeFromBalExpr(String balExpr) {
        if (balExpr.equals("true") || balExpr.equals("false")) {
            return "boolean";
        } else if (balExpr.startsWith("\"") && balExpr.endsWith("\"")) {
            return "string";
        } else if (balExpr.startsWith("[") && balExpr.endsWith("]")) {
            return "anydata[]"; // TODO: infer based on the elements
        } else if (balExpr.matches("-?\\d+")) {
            return "int";
        } else if (balExpr.matches("-?\\d+\\.\\d+")) {
            return "decimal";
        } else {
            return "anydata";
        }
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

    public static String getRecordInitValue(RecordType recordType) {
        List<String> requiredFields = new ArrayList<>();
        for (RecordField recordField : recordType.recordFields()) {
            if (!recordField.isOptional()) {
                String value = getRequiredRecFieldDefaultValue(recordField);
                requiredFields.add(String.format("%s : %s", recordField.name(), value));
            }
        }
        String recordBody = String.join(",", requiredFields);
        return String.format("{ %s }", recordBody);
    }

    private static String getRequiredRecFieldDefaultValue(RecordField recordField) {
        assert !recordField.isOptional();
        if (recordField.type().toString().equals("anydata")) {
            return "()";
        }

        if (recordField.type().toString().equals("FlowVars") || recordField.type().toString().equals("SessionVars")) {
            return "{}";
        }

        if (recordField.type().toString().equals("InboundProperties")) {
            // TODO: handle non-http sources
            return "{response: new}";
        }

        throw new IllegalStateException();
    }

    public static BallerinaExpression exprFrom(String expr) {
        return new BallerinaExpression(expr);
    }

    public static BallerinaStatement stmtFrom(String stmt) {
        return new BallerinaStatement(stmt);
    }

    public static BallerinaType typeFrom(String type) {
        return new BallerinaType(type);
    }
}
