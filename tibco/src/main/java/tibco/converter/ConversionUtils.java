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
import org.w3c.dom.Element;
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
import static common.BallerinaModel.TypeDesc.BuiltinType.RecordTypeDesc;
import static common.BallerinaModel.TypeDesc.BuiltinType.STRING;
import static common.ConversionUtils.exprFrom;
import static tibco.converter.BallerinaSQLConstants.PARAMETERIZED_QUERY_TYPE;

public final class ConversionUtils {

    private ConversionUtils() {
    }

    public static String sanitizes(String name) {
        String sanitized = name.replaceAll("[^a-zA-Z0-9]", "_");
        while (!Character.isAlphabetic(sanitized.charAt(0))) {
            sanitized = sanitized.substring(1);
        }
        if (isReserved(sanitized)) {
            sanitized = "'" + name;
        }
        return sanitized;
    }

    public static String escapeString(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\"", "\\\"");
    }

    private static boolean isReserved(String name) {
        return name.equals("type");
    }

    public static String getSanitizedUniqueName(String name, Collection<String> allocatedNames) {
        String sanitized = sanitizes(name);
        String nameToCheck = sanitized;
        if (allocatedNames.contains(nameToCheck)) {
            nameToCheck = sanitized + "_" + allocatedNames.size();
        }
        return nameToCheck;
    }

    public static BuiltinType from(SQL.SQLType sqlType) {
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

    public static String elementToString(Element element) {
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

    public static String sanitizePath(String path) {
        return path.replaceAll("^/+", "").replaceAll("/+$", "");
    }

    public static String stripNamespace(String tagName) {
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

    public static String baseName(String value) {
        String[] parts = value.split("/");
        return parts[parts.length - 1];
    }

    public static String createSoapEnvelope(Expression.VariableReference body) {
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
    }

    public static BallerinaModel.TypeDesc toTypeDesc(XSD xsd) {
        return toTypeDesc(xsd.type().type());
    }

    private static BallerinaModel.TypeDesc toTypeDesc(XSD.XSDType type) {
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
}
