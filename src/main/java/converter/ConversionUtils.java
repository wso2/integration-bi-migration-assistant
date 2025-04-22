package converter;

import ballerina.BallerinaModel.TypeDesc.BallerinaType;
import ballerina.BallerinaModel.TypeDesc.RecordTypeDesc;
import ballerina.BallerinaModel.TypeDesc.RecordTypeDesc.RecordField;
import org.w3c.dom.Element;

import java.io.StringWriter;
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

import static ballerina.BallerinaModel.Expression.BallerinaExpression;
import static ballerina.BallerinaModel.Statement.BallerinaStatement;

public class ConversionUtils {

    private static final Pattern UNESCAPED_SPECIAL_CHAR_SET = Pattern
            .compile("([$&+,:;=\\?@#\\\\|/'\\ \\[\\}\\]<\\>.\"^*{}~`()%!-])");

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

        return list.isEmpty() ? "." : String.join("/", list);
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

        return list.isEmpty() ? "/" : "/" + String.join("/", list);
    }

    /**
     * Converts mule http request path to a Ballerina client resource path.
     *
     * @param basePath mule http request path
     * @return ballerina client resource path
     */
    static String getBallerinaClientResourcePath(String basePath) {
        List<String> list = Arrays.stream(basePath.split("/")).filter(s -> !s.isEmpty())
                .map(s -> isInt(s) ? "[" + s + "]" : ConversionUtils.escapeSpecialCharacters(s)).toList();
        return list.isEmpty() ? "/" : "/" + String.join("/", list);
    }

    static void processExprCompContent(MuleToBalConverter.SharedProjectData sharedProjectData,
            String convertedBalStmts) {
        List<String> list = Arrays.stream(convertedBalStmts.split(";")).filter(s -> !s.isEmpty()).map(String::trim)
                .toList();
        for (String stmt : list) {
            processStatement(sharedProjectData, stmt);
        }
    }

    private static void processStatement(MuleToBalConverter.SharedProjectData sharedProjectData, String statement) {
        String regex = "ctx\\.(sessionVars|flowVars)\\.(\\w+)\\s*=\\s*(.*)";
        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(statement);
        if (matcher.find()) {
            String varCategory = matcher.group(1);
            String varName = matcher.group(2);
            String varValue = matcher.group(3);

            if ("sessionVars".equals(varCategory) && !sharedProjectData.existingSessionVar(varName)) {
                String inferredType = inferTypeFromBalExpr(varValue);
                sharedProjectData.sessionVars.add(
                        new MuleToBalConverter.SharedProjectData.TypeAndNamePair(inferredType, varName));
            } else if ("flowVars".equals(varCategory) && !sharedProjectData.existingFlowVar(varName)) {
                String inferredType = inferTypeFromBalExpr(varValue);
                sharedProjectData.flowVars.add(
                        new MuleToBalConverter.SharedProjectData.TypeAndNamePair(inferredType, varName));
            }
        }
    }

    private static boolean isInt(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
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
            return new String[] { "GET", "POST", "PUT", "DELETE", "PATCH", "HEAD", "OPTIONS", "TRACE", "CONNECT" };
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

        // Replace variable references: flowVars.foo --> ctx.flowVars.foo /
        // ctx.flowVars.foo.toString()
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

    public static String elementToString(Element element) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(element), new StreamResult(writer));
            return writer.getBuffer().toString();
        } catch (Exception e) {
            throw new RuntimeException("Error converting Member to String", e);
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

    public static String getRecordInitValue(RecordTypeDesc recordType) {
        String recordBody = recordType.fields().stream()
            .filter(recordField -> !recordField.isOptional())
            .map(recordField -> {
                String value = getRequiredRecFieldDefaultValue(recordField);
                return String.format("%s : %s", recordField.name(), value);
            })
            .collect(java.util.stream.Collectors.joining(", "));
        return String.format("{ %s }", recordBody);
    }

    private static String getRequiredRecFieldDefaultValue(
            BallerinaModel.TypeDesc.RecordTypeDesc.RecordField recordField) {
        assert !recordField.isOptional();
        return switch (recordField.typeDesc().toString()) {
            case "anydata" -> "()";
            case "FlowVars", "SessionVars" -> "{}";
            case "InboundProperties" ->
                // TODO: handle non-http sources
                "{response: new}";
            default -> throw new IllegalStateException();
        };
    }

    public static BallerinaExpression exprFrom(String expr) {
        return new BallerinaExpression(expr);
    }

    public static BallerinaStatement stmtFrom(String stmt) {
        return new BallerinaStatement(stmt);
    }

    public static BallerinaModel.TypeDesc.BallerinaType typeFrom(String type) {
        return new BallerinaModel.TypeDesc.BallerinaType(type);
    }
}
