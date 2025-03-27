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

import ballerina.BallerinaModel;
import ballerina.BallerinaModel.Statement.VarDeclStatment;
import converter.tibco.analyzer.AnalysisResult;
import tibco.TibcoModel;

import java.io.StringWriter;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static ballerina.BallerinaModel.TypeDesc.BuiltinType.NEVER;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Element;

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

    public static BallerinaModel.TypeDesc.BuiltinType from(
            TibcoModel.Scope.Flow.Activity.ActivityExtension.Config.SQL.SQLParameter.SQLType sqlType) {
        return switch (sqlType) {
            case INTEGER, BIGINT, SMALLINT -> BallerinaModel.TypeDesc.BuiltinType.INT;
            case DECIMAL, NUMERIC, REAL, DOUBLE -> BallerinaModel.TypeDesc.BuiltinType.DECIMAL;
            case VARCHAR, CHAR, TEXT, DATE, TIME, TIMESTAMP -> BallerinaModel.TypeDesc.BuiltinType.STRING;
            case BOOLEAN -> BallerinaModel.TypeDesc.BuiltinType.BOOLEAN;
            case BLOB, CLOB -> BallerinaModel.TypeDesc.BuiltinType.ANYDATA;
        };
    }

    static BallerinaModel.TypeDesc createQueryInputType(
            ActivityContext cx,
            TibcoModel.Scope.Flow.Activity.ActivityExtension.Config.SQL sql) {
        ProcessContext processContext = cx.processContext;
        AnalysisResult analysisResult = processContext.analysisResult;
        String typeName = "QueryData" + analysisResult.queryIndex(sql);
        List<BallerinaModel.TypeDesc.RecordTypeDesc.RecordField> fields = sql.parameters().stream()
                .map(each -> new BallerinaModel.TypeDesc.RecordTypeDesc.RecordField(
                        each.name(),
                        from(each.type())))
                .toList();
        BallerinaModel.TypeDesc.RecordTypeDesc recordTy = new BallerinaModel.TypeDesc.RecordTypeDesc(List.of(), fields,
                NEVER);
        processContext.addModuleTypeDef(typeName, new BallerinaModel.ModuleTypeDef(typeName, recordTy));
        return processContext.getTypeByName(typeName);
    }

    static VarDeclStatment createQueryDecl(ActivityContext cx, BallerinaModel.Expression.VariableReference paramData,
            TibcoModel.Scope.Flow.Activity.ActivityExtension.Config.SQL query) {
        int paramIndex = 0;
        StringBuilder sb = new StringBuilder();
        String queryStr = query.query();
        sb.append("`");
        for (int i = 0; i < queryStr.length(); i++) {
            char c = queryStr.charAt(i);
            if (c == '?') {
                sb.append("${").append(paramData.varName()).append(".")
                        .append(query.parameters().get(paramIndex++).name()).append("}");
            } else {
                sb.append(c);
            }
        }
        sb.append("`");
        BallerinaModel.BallerinaExpression templateExpr = new BallerinaModel.BallerinaExpression(sb.toString());
        return new VarDeclStatment(cx.processContext.getTypeByName("sql:ParameterizedQuery"), cx.getAnnonVarName(),
                templateExpr);
    }

    static BallerinaModel.TypeDesc.RecordTypeDesc.Namespace createNamespace(TibcoModel.NameSpace nameSpace) {
        return new BallerinaModel.TypeDesc.RecordTypeDesc.Namespace(nameSpace.prefix(), nameSpace.uri());
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
}
