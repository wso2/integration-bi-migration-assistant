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

import common.BallerinaModel;
import common.BallerinaModel.Expression;
import common.BallerinaModel.Statement.VarDeclStatment;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Element;
import tibco.model.Process;
import tibco.model.Scope;
import tibco.model.Scope.Flow.Activity.ActivityExtension.Config.SQL;
import tibco.model.XSD;

import java.io.StringWriter;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import static common.BallerinaModel.TypeDesc.BuiltinType.ANYDATA;
import static common.BallerinaModel.TypeDesc.BuiltinType.BOOLEAN;
import static common.BallerinaModel.TypeDesc.BuiltinType.BuiltinType;
import static common.BallerinaModel.TypeDesc.BuiltinType.DECIMAL;
import static common.BallerinaModel.TypeDesc.BuiltinType.FLOAT;
import static common.BallerinaModel.TypeDesc.BuiltinType.INT;
import static common.BallerinaModel.TypeDesc.BuiltinType.JSON;
import static common.BallerinaModel.TypeDesc.BuiltinType.NIL;
import static common.BallerinaModel.TypeDesc.BuiltinType.READONLY;
import static common.BallerinaModel.TypeDesc.BuiltinType.RecordTypeDesc;
import static common.BallerinaModel.TypeDesc.BuiltinType.STRING;
import static common.BallerinaModel.TypeDesc.BuiltinType.XML;
import static common.ConversionUtils.exprFrom;
import static common.ConversionUtils.typeFrom;
import static tibco.converter.BallerinaSQLConstants.PARAMETERIZED_QUERY_TYPE;

public final class ConversionUtils {

    public static @NotNull BallerinaModel.TypeDesc jsonResponseTypeDesc(
            BallerinaModel.TypeDesc.TypeReference responseTypeRef) {
        return BallerinaModel.TypeDesc.IntersectionTypeDesc.of(
                READONLY,
                new RecordTypeDesc(
                        List.of(responseTypeRef),
                        List.of(
                                new RecordTypeDesc.RecordField("kind",
                                        new Expression.StringConstant("JSONResponse"),
                                        new Expression.StringConstant("JSONResponse")),
                                new RecordTypeDesc.RecordField("payload", JSON)),
                        BuiltinType.NEVER));
    }

    public static @NotNull BallerinaModel.TypeDesc xmlResponseTypeDesc(
            BallerinaModel.TypeDesc.TypeReference responseTypeRef) {
        return BallerinaModel.TypeDesc.IntersectionTypeDesc.of(
                READONLY,
                new RecordTypeDesc(
                        List.of(responseTypeRef),
                        List.of(
                                new RecordTypeDesc.RecordField("kind",
                                        new Expression.StringConstant("XMLResponse"),
                                        new Expression.StringConstant("XMLResponse")),
                                new RecordTypeDesc.RecordField("payload", XML)),
                        BuiltinType.NEVER));
    }

    public static @NotNull BallerinaModel.TypeDesc textResponseTypeDesc(
            BallerinaModel.TypeDesc.TypeReference responseTypeRef) {
        return BallerinaModel.TypeDesc.IntersectionTypeDesc.of(
                READONLY,
                new RecordTypeDesc(
                        List.of(responseTypeRef),
                        List.of(
                                new RecordTypeDesc.RecordField("kind",
                                        new Expression.StringConstant("TextResponse"),
                                        new Expression.StringConstant("TextResponse")),
                                new RecordTypeDesc.RecordField("payload", STRING)),
                        BuiltinType.NEVER));
    }

    private ConversionUtils() {
    }

    public static @NotNull String sanitizes(String name) {
        String sanitized = name.replaceAll("[^a-zA-Z0-9]", "_");
        while (!Character.isAlphabetic(sanitized.charAt(0))) {
            sanitized = sanitized.substring(1);
        }
        if (isReserved(sanitized)) {
            sanitized = "'" + name;
        }
        return sanitized;
    }

    public static @NotNull String escapeString(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\"", "\\\"");
    }

    private static boolean isReserved(String name) {
        return name.equals("type");
    }

    public static @NotNull String getSanitizedUniqueName(String name, Collection<String> allocatedNames) {
        String sanitized = sanitizes(name);
        String nameToCheck = sanitized;
        if (allocatedNames.contains(nameToCheck)) {
            nameToCheck = sanitized + "_" + allocatedNames.size();
        }
        return nameToCheck;
    }

    public static @NotNull BuiltinType from(SQL.SQLType sqlType) {
        return switch (sqlType) {
            case INTEGER, BIGINT, SMALLINT -> INT;
            case DECIMAL, NUMERIC, REAL, DOUBLE -> DECIMAL;
            case VARCHAR, CHAR, TEXT, DATE, TIME, TIMESTAMP -> STRING;
            case BOOLEAN -> BOOLEAN;
            case BLOB, CLOB -> ANYDATA;
        };
    }

    static VarDeclStatment createQueryDecl(ActivityContext cx, Map<String, Expression.VariableReference> vars,
                                           SQL query) {
        int paramIndex = 0;
        StringBuilder sb = new StringBuilder();
        String queryStr = query.query();
        sb.append("`");
        for (int i = 0; i < queryStr.length(); i++) {
            char c = queryStr.charAt(i);
            if (c == '?') {
                String varName = query.parameters().get(paramIndex++).name();
                assert vars.get(varName) != null;
                sb.append("${%s}".formatted(vars.get(varName)));
            } else {
                sb.append(c);
            }
        }
        sb.append("`");
        Expression.BallerinaExpression templateExpr = exprFrom(sb.toString());
        return new VarDeclStatment(cx.processContext.getTypeByName(PARAMETERIZED_QUERY_TYPE), cx.getAnnonVarName(),
                templateExpr);
    }

    private static Expression templateExpression(
            Scope.Flow.Activity.Expression.XPath xPath, Expression.VariableReference context) {
        String xPathStr = xPath.expression();
        StringBuilder sb = new StringBuilder();
        char[] chars = xPathStr.toCharArray();
        int i = 0;
        sb.append("`");
        while (i < chars.length) {
            if (chars[i] == '$') {
                StringBuilder accum = new StringBuilder();
                i++;
                while (i < chars.length && (Character.isLetterOrDigit(chars[i]) || chars[i] == '_')) {
                    accum.append(chars[i]);
                    i++;
                }
                sb.append("${%s.get(\"%s\")}".formatted(context, accum));
            } else {
                sb.append(chars[i]);
                i++;
            }
        }
        sb.append("`");
        return exprFrom(sb.toString());
    }

    public static @NotNull String elementToString(Element element) {
        try {
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            // Configure the transformer for clean output
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            DOMSource source = new DOMSource(element);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);

            transformer.transform(source, result);
            return writer.toString();
        } catch (TransformerException e) {
            throw new RuntimeException("Failed to convertTypes element to string", e);
        }
    }

    public static @NotNull String sanitizePath(String path) {
        return path.replaceAll("^/+", "").replaceAll("/+$", "");
    }

    public static @NotNull String stripNamespace(String tagName) {
        String[] parts = tagName.split(":");
        if (parts.length == 1) {
            return parts[0];
        }
        assert parts.length == 2 && !parts[1].isEmpty();
        return parts[1];
    }

    static Expression xPath(ProcessContext cx, Expression value, Expression.VariableReference context,
                            Scope.Flow.Activity.Expression.XPath predicate) {
        String predicateTestFn = cx.getXPathFunction();
        Expression xPathExpr = templateExpression(predicate, context);
        return new Expression.FunctionCall(predicateTestFn, List.of(value, xPathExpr));
    }

    public static @NotNull String baseName(String value) {
        String[] parts = value.split("/");
        return parts[parts.length - 1];
    }

    public static @NotNull String resourceNameFromPath(String path) {
        String baseFileName = baseName(path);
        String[] suffixes = {".sharedjdbc", ".sharedhttp", ".sharedjms", ".sharedvariable"};
        for (String suffix : suffixes) {
            if (baseFileName.endsWith(suffix)) {
                return baseFileName.substring(0, baseFileName.length() - suffix.length());
            }
        }
        int lastDotIndex = baseFileName.lastIndexOf('.');
        return lastDotIndex > 0 ? baseFileName.substring(0, lastDotIndex) : baseFileName;
    }

    public static @NotNull String createSoapEnvelope(Expression.VariableReference body) {
        return """
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
                  soap:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/">
                    <soap:Header/>
                    <soap:Body>
                        ${%s}
                    </soap:Body>
                </soap:Envelope>
                """.formatted(body.varName());
    }

    public enum Constants {
        ;

        static final String CONTEXT_VAR_NAME = "context";
        static final String CONTEXT_INPUT_NAME = "$input";

        static final BallerinaModel.TypeDesc.TypeReference FILE_EVENT =
                new BallerinaModel.TypeDesc.TypeReference("file:FileEvent");
        static final BallerinaModel.TypeDesc.TypeReference JMS_MESSAGE_TYPE =
                new BallerinaModel.TypeDesc.TypeReference("jms:Message");
        static final BallerinaModel.TypeDesc.TypeReference JMS_CONNECTION =
                new BallerinaModel.TypeDesc.TypeReference("jms:Connection");
        static final BallerinaModel.TypeDesc.TypeReference JMS_SESSION =
                new BallerinaModel.TypeDesc.TypeReference("jms:Session");
        static final BallerinaModel.TypeDesc.TypeReference JMS_MESSAGE_PRODUCER =
                new BallerinaModel.TypeDesc.TypeReference("jms:MessageProducer");
        static final BallerinaModel.TypeDesc.TypeReference JMS_MESSAGE_CONSUMER =
                new BallerinaModel.TypeDesc.TypeReference("jms:MessageConsumer");
        static final BallerinaModel.TypeDesc.TypeReference JMS_MESSAGE =
                new BallerinaModel.TypeDesc.TypeReference("jms:Message");
        static final BallerinaModel.TypeDesc.TypeReference JMS_TEXT_MESSAGE =
                new BallerinaModel.TypeDesc.TypeReference("jms:TextMessage");

        static final BallerinaModel.TypeDesc HTTP_RESPONSE = typeFrom("http:Response");

        static final BallerinaModel.TypeDesc RESPONSE_TYPE_DESC =
                new BallerinaModel.TypeDesc.RecordTypeDesc(
                        List.of(
                                new BallerinaModel.TypeDesc.RecordTypeDesc.RecordField("kind",
                                        BallerinaModel.TypeDesc.UnionTypeDesc.of(
                                                new Expression.StringConstant("JSONResponse"),
                                                new Expression.StringConstant("XMLResponse"),
                                                new Expression.StringConstant("TextResponse"))),
                                new BallerinaModel.TypeDesc.RecordTypeDesc.RecordField("payload", ANYDATA),
                                new BallerinaModel.TypeDesc.RecordTypeDesc.RecordField("headers",
                                        new BallerinaModel.TypeDesc.MapTypeDesc(STRING))));

        static final BallerinaModel.TypeDesc.TypeReference FILE_DATA =
                new BallerinaModel.TypeDesc.TypeReference("FileData");
    }

    public static @NotNull BallerinaModel.TypeDesc.FunctionTypeDesc processFunctionType(ProcessContext cx) {
        return new BallerinaModel.TypeDesc.FunctionTypeDesc(
                List.of(new BallerinaModel.Parameter("cx", cx.contextType())));
    }

    public static @NotNull BallerinaModel.TypeDesc.FunctionTypeDesc scopeFnType(ProcessContext cx) {
        return new BallerinaModel.TypeDesc.FunctionTypeDesc(
                List.of(new BallerinaModel.Parameter("cx", cx.contextType())));
    }

    public static @NotNull Expression.FieldAccess getXMLResultFromContext(Expression.VariableReference context) {
        return new Expression.FieldAccess(context, "result");
    }

    public static @NotNull BallerinaModel.TypeDesc.FunctionTypeDesc activityFnType(ProcessContext cx) {
        return new BallerinaModel.TypeDesc.FunctionTypeDesc(
                List.of(new BallerinaModel.Parameter("cx", cx.contextType())),
                BallerinaModel.TypeDesc.UnionTypeDesc.of(NIL, BallerinaModel.TypeDesc.BuiltinType.ERROR));
    }

    public static @NotNull BallerinaModel.TypeDesc.FunctionTypeDesc errorFlowFnType(ProcessContext cx) {
        return new BallerinaModel.TypeDesc.FunctionTypeDesc(
                List.of(new BallerinaModel.Parameter("err", BallerinaModel.TypeDesc.BuiltinType.ERROR),
                        new BallerinaModel.Parameter("cx", cx.contextType())));
    }

    public static @NotNull BallerinaModel.TypeDesc toTypeDesc(XSD xsd) {
        return toTypeDesc(xsd.type().type());
    }

    public static @NotNull BallerinaModel.TypeDesc toTypeDesc(XSD.XSDType type) {
        return switch (type) {
            case XSD.XSDType.BasicXSDType basicXSDType -> basicTypeToTD(basicXSDType);
            case XSD.XSDType.ComplexType complexType -> complexTypeToTD(complexType);
        };
    }

    private static BallerinaModel.TypeDesc complexTypeToTD(XSD.XSDType.ComplexType complexType) {
        List<RecordTypeDesc.RecordField> fields = complexType.body().elements().stream()
                .map(each ->
                        new RecordTypeDesc.RecordField(each.name(), toTypeDesc(each.type()),
                                each.minOccur().map(minOccurs -> minOccurs == 0).orElse(false))).toList();
        return new RecordTypeDesc(fields);
    }

    private static BallerinaModel.TypeDesc basicTypeToTD(XSD.XSDType.BasicXSDType basicXSDType) {
        return switch (basicXSDType) {
            case STRING -> STRING;
            case INTEGER, INT, LONG, SHORT -> INT;
            case DECIMAL -> DECIMAL;
            case FLOAT, DOUBLE -> FLOAT;
            case BOOLEAN -> BOOLEAN;
        };
    }

    public static @NotNull String processFunctionName(Process process) {
        return processFunctionName(process.name());
    }

    public static @NotNull String processFunctionName(String processName) {
        return "start_" + sanitizes(processName);
    }

    public record LineCount(long ballerina, long xml) {

        public static LineCount sum(LineCount a, LineCount b) {
            return new LineCount(a.ballerina + b.ballerina, a.xml + b.xml);
        }

        public long normalize() {
            // Normalize the line count to a single value for reporting
            return ballerina + (long) Math.ceil(xml * 0.5);
        }
    }

    public static LineCount lineCount(String source) {
        String[] lines = source.split("\n");
        long ballerinaLines = 0;
        long xmlLines = 0;
        
        enum State { IN_BALLERINA, IN_XML }
        State currentState = State.IN_BALLERINA;
        
        // Pattern to match XML template literal start (xml `)
        java.util.regex.Pattern xmlStartPattern = java.util.regex.Pattern.compile(".*xml\\s*`");
        // Pattern to match XML template literal end (`)
        java.util.regex.Pattern xmlEndPattern = java.util.regex.Pattern.compile(".*`");
        
        for (String line : lines) {
            String trimmedLine = line.trim();
            
            if (trimmedLine.isEmpty()) {
                // Empty lines don't count toward either category
                continue;
            }
            
            // Check for state transitions within the line
            if (currentState == State.IN_BALLERINA) {
                if (xmlStartPattern.matcher(line).find()) {
                    // Found start of XML template literal
                    currentState = State.IN_XML;
                    xmlLines++;
                    
                    // Check if the XML template also ends on the same line
                    String afterXmlStart = line.substring(line.indexOf("`") + 1);
                    if (afterXmlStart.contains("`")) {
                        // XML template ends on the same line
                        currentState = State.IN_BALLERINA;
                    }
                } else {
                    // Regular Ballerina line
                    ballerinaLines++;
                }
            } else { // currentState == State.IN_XML
                xmlLines++;
                
                // Check if XML template ends on this line
                if (xmlEndPattern.matcher(line).find()) {
                    currentState = State.IN_BALLERINA;
                }
            }
        }
        
        return new LineCount(ballerinaLines, xmlLines);
    }
}
