package converter;

import common.BallerinaModel.TypeDesc.BallerinaType;
import common.BallerinaModel.TypeDesc.RecordTypeDesc;
import common.BallerinaModel.TypeDesc.RecordTypeDesc.RecordField;
import io.ballerina.compiler.syntax.tree.SyntaxInfo;
import org.w3c.dom.Element;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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

import static common.BallerinaModel.Expression.BallerinaExpression;
import static common.BallerinaModel.ModuleTypeDef;
import static common.BallerinaModel.Statement.BallerinaStatement;
import static converter.MELConverter.convertMELToBal;

public class ConversionUtils {

    private static final Pattern UNESCAPED_SPECIAL_CHAR_SET =
            Pattern.compile("([$&+,:;=\\?@#\\\\|/'\\ \\[\\}\\]<\\>.\"^*{}~`()%!-])");

    /**
     * Converts mule path to a Ballerina resource path.
     *
     * @param path mule path
     * @return ballerina resource path
     */
    static String getBallerinaResourcePath(String path, List<String> pathParams) {
        List<String> list = Arrays.stream(path.split("/")).filter(s -> !s.isEmpty())
                .map(s -> {
                    if (s.startsWith("{") && s.endsWith("}")) {
                        // We come here for mule path params. e.g. foo/{bar}/baz
                        String pathParamName = s.substring(1, s.length() - 1);
                        pathParamName = escapeSpecialCharacters(pathParamName);
                        pathParams.add(pathParamName);
                        return "[string " + pathParamName + "]";
                    }
                    if (s.startsWith("#[") && s.endsWith("]")) {
                        // Handle MEL in url. e.g. /users/#[flowVars.userId]
                        String balExpr = convertMELToBal(s, false);
                        return "[" + balExpr + "]";
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
                .map(s -> {
                    if (isInt(s)) {
                        return "[" + s + "]";
                    }
                    if (s.startsWith("#[") && s.endsWith("]")) {
                        // Handle MEL in url. e.g. /users/#[flowVars.userId]
                        String balExpr = convertMELToBal(s, false);
                        return "[" + balExpr + "]";
                    }
                    return ConversionUtils.escapeSpecialCharacters(s);
                }).toList();
        return list.isEmpty() ? "/" : "/" + String.join("/", list);
    }

    static void processExprCompContent(MuleToBalConverter.SharedProjectData sharedProjectData,
                                       String convertedBalStmts) {
        List<String> list =
                Arrays.stream(convertedBalStmts.split(";")).filter(s -> !s.isEmpty()).map(String::trim).toList();
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
        return queryParams.entrySet().stream().map(e -> {
                    String k = e.getKey();
                    String key = SyntaxInfo.isKeyword(k) ? "'" + k : k;
                    String balExpr = convertMuleExprToBal(e.getValue());
                    if (balExpr.startsWith("ctx.")) {
                        balExpr = "check " + balExpr + ".ensureType(http:QueryParamType)";
                    }
                    return String.format("%s = %s", key, balExpr);
                })
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
            String replacement = "\\${" + convertMELToBal(matchedExpression, addToStringCalls) + "}";
            matcher.appendReplacement(result, replacement);
        }
        matcher.appendTail(result);

        return hasMELParts ? String.format("string `%s`", result) : "\"" + melExpression.replace("\"", "\\\"") + "\"";
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
        } else if (balExpr.startsWith("ctx.inboundProperties.request.getQueryParamValue")) {
            return "string";
        } else if (balExpr.startsWith("ctx.inboundProperties.uriParams")) {
            return "string";
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

    public static String getRecordInitValue(HashMap<String, ModuleTypeDef> contextTypeDefMap,
                                            RecordTypeDesc recordType) {
        List<String> requiredFields = new ArrayList<>();
        for (RecordField recordField : recordType.fields()) {
            if (!recordField.isOptional()) {
                String value = getRequiredRecFieldDefaultValue(contextTypeDefMap, recordField);
                requiredFields.add(String.format("%s: %s", recordField.name(), value));
            }
        }
        String recordBody = String.join(", ", requiredFields);
        return String.format("{%s}", recordBody);
    }

    private static String getRequiredRecFieldDefaultValue(HashMap<String, ModuleTypeDef> contextTypeDefMap,
                                                          RecordField recordField) {
        assert !recordField.isOptional();
        String typeStr = recordField.typeDesc().toString();
        switch (typeStr) {
            case "anydata" -> {
                return "()";
            }
            case Constants.HTTP_RESPONSE_TYPE, Constants.HTTP_REQUEST_TYPE -> {
                return "new";
            }
            case "map<string>" -> {
                return "{}";
            }
            case Constants.INBOUND_PROPERTIES_TYPE, Constants.FLOW_VARS_TYPE, Constants.SESSION_VARS_TYPE -> {
                ModuleTypeDef moduleTypeDef = contextTypeDefMap.get(typeStr);
                return getRecordInitValue(contextTypeDefMap, (RecordTypeDesc) moduleTypeDef.typeDesc());
            }
            default -> throw new IllegalStateException();
        }
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
