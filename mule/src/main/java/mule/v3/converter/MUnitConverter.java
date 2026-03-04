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
package mule.v3.converter;

import common.BallerinaModel.Import;
import common.BallerinaModel.Statement;
import common.BallerinaModel.TextDocument;
import mule.common.MUnitModel.AssertEquals;
import mule.common.MUnitModel.AssertNotNull;
import mule.common.MUnitModel.AssertThat;
import mule.common.MUnitModel.Fail;
import mule.common.MUnitModel.LifecycleBlock;
import mule.common.MUnitModel.MUnitRecord;
import mule.common.MUnitModel.MUnitTest;
import mule.common.MUnitModel.MockWhen;
import mule.common.MUnitModel.SetEvent;
import mule.common.MUnitModel.SetEventVariable;
import mule.common.MUnitModel.TestSuite;
import mule.common.MUnitModel.UnsupportedMUnitBlock;
import mule.common.MUnitModel.VerifyCall;
import mule.v3.Constants;
import mule.v3.Context;
import mule.v3.ConversionUtils;
import mule.v3.model.MUnitModelV3.MuleProcessorRef;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static common.ConversionUtils.stmtFrom;

public class MUnitConverter {

    private static final Import TEST_MODULE_IMPORT = new Import("ballerina", "test");
    private static final String TODO_MOCK_DESC =
            "MUNIT MOCK — MANUAL CONVERSION REQUIRED.";
    private static final String TODO_VERIFY_DESC =
            "MUNIT VERIFY-CALL — MANUAL CONVERSION REQUIRED.";
    private static final String TODO_ASSERT_THAT_DESC =
            "MUNIT ASSERT-THAT — MANUAL CONVERSION MAY BE REQUIRED.";
    private static final String TODO_UNSUPPORTED_DESC =
            "UNSUPPORTED MUNIT BLOCK ENCOUNTERED. MANUAL CONVERSION REQUIRED.";

    @NotNull
    public static TextDocument convertTestSuite(Context ctx, String balFileName, TestSuite testSuite) {
        assert ctx != null && testSuite != null;

        Set<Import> imports = new LinkedHashSet<>();
        imports.add(TEST_MODULE_IMPORT);

        List<String> intrinsics = new ArrayList<>();

        testSuite.beforeSuite().ifPresent(block ->
                intrinsics.add(buildAnnotatedFunction(ctx, block, "@test:BeforeSuite")));
        testSuite.afterSuite().ifPresent(block ->
                intrinsics.add(buildAnnotatedFunction(ctx, block, "@test:AfterSuite")));
        testSuite.beforeTest().ifPresent(block ->
                intrinsics.add(buildAnnotatedFunction(ctx, block, "@test:BeforeEach")));
        testSuite.afterTest().ifPresent(block ->
                intrinsics.add(buildAnnotatedFunction(ctx, block, "@test:AfterEach")));

        for (MUnitTest test : testSuite.tests()) {
            intrinsics.add(buildTestFunction(ctx, test));
        }

        imports.addAll(ctx.currentFileCtx.balConstructs.imports);

        return new TextDocument(
                balFileName,
                new ArrayList<>(imports),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                intrinsics,
                Collections.emptyList());
    }

    private static String buildTestFunction(Context ctx, MUnitTest test) {
        List<String> bodyLines = new ArrayList<>();

        if (!test.description().isEmpty()) {
            bodyLines.add("    // Test: " + test.description());
        }

        test.expectedErrorType().ifPresent(errorType ->
                bodyLines.add("    // TODO: This test expects error type \"%s\". ".formatted(errorType)
                        + "Wrap execution in do/on-fail or use test:Config assertion."));

        if (!test.behavior().isEmpty()) {
            bodyLines.add("    // Behavior (Setup)");
            bodyLines.addAll(convertMUnitRecordsToLines(ctx, test.behavior()));
        }
        if (!test.execution().isEmpty()) {
            bodyLines.add("    // Execution (Act)");
            bodyLines.addAll(convertMUnitRecordsToLines(ctx, test.execution()));
        }
        if (!test.validation().isEmpty()) {
            bodyLines.add("    // Validation (Assert)");
            bodyLines.addAll(convertMUnitRecordsToLines(ctx, test.validation()));
        }

        String funcName = ConversionUtils.convertToBalIdentifier(test.name());
        StringBuilder sb = new StringBuilder();
        sb.append("@test:Config {}\n");
        sb.append("function ").append(funcName).append("() returns error? {\n");
        for (String line : bodyLines) {
            sb.append(line).append("\n");
        }
        sb.append("}");
        return sb.toString();
    }

    private static String buildAnnotatedFunction(Context ctx, LifecycleBlock block, String annotation) {
        List<String> bodyLines = convertMUnitRecordsToLines(ctx, block.processors());
        String funcName = ConversionUtils.convertToBalIdentifier(block.name());

        StringBuilder sb = new StringBuilder();
        sb.append(annotation).append("\n");
        sb.append("function ").append(funcName).append("() returns error? {\n");
        for (String line : bodyLines) {
            sb.append(line).append("\n");
        }
        sb.append("}");
        return sb.toString();
    }

    private static List<String> convertMUnitRecordsToLines(Context ctx, List<MUnitRecord> records) {
        List<String> lines = new ArrayList<>();
        for (MUnitRecord record : records) {
            lines.addAll(convertMUnitRecordToLines(ctx, record));
        }
        return lines;
    }

    private static List<String> convertMUnitRecordToLines(Context ctx, MUnitRecord record) {
        return switch (record) {
            case MockWhen mockWhen -> wrapAsBodyLines(convertMockWhen(mockWhen));
            case AssertEquals assertEqual -> wrapAsBodyLines(convertAssertEquals(ctx, assertEqual));
            case AssertThat assertThat -> wrapAsBodyLines(convertAssertThat(ctx, assertThat));
            case AssertNotNull assertNotNull -> wrapAsBodyLines(convertAssertNotNull(ctx, assertNotNull));
            case Fail fail -> wrapAsBodyLines(convertFail(fail));
            case SetEvent setEvent -> wrapAsBodyLines(convertSetEvent(ctx, setEvent));
            case VerifyCall verifyCall -> wrapAsBodyLines(convertVerifyCall(verifyCall));
            case MuleProcessorRef processorRef -> wrapAsBodyLines(convertMuleProcessor(ctx, processorRef));
            case UnsupportedMUnitBlock unsupported -> wrapAsBodyLines(convertUnsupportedMUnitBlock(unsupported));
            default -> List.of("    // TODO: unsupported MUnit element: " + record.kind());
        };
    }

    private static List<String> wrapAsBodyLines(List<Statement> statements) {
        List<String> lines = new ArrayList<>();
        for (Statement stmt : statements) {
            for (String line : stmt.toString().trim().split("\n")) {
                lines.add("    " + line);
            }
        }
        return lines;
    }

    private static List<Statement> convertMockWhen(MockWhen mockWhen) {
        StringBuilder xmlReconstructed = new StringBuilder();
        xmlReconstructed.append("<munit:mock messageProcessor=\"%s\"".formatted(mockWhen.processor()));
        mockWhen.docName().ifPresent(dn -> xmlReconstructed.append(" doc:name=\"%s\"".formatted(dn)));
        xmlReconstructed.append(">\n");

        if (!mockWhen.withAttributes().isEmpty()) {
            xmlReconstructed.append("    <munit:with-attributes>\n");
            for (var attr : mockWhen.withAttributes()) {
                xmlReconstructed.append(
                        "        <munit:with-attribute name=\"%s\" whereValue=\"%s\"/>\n"
                                .formatted(attr.attributeName(), attr.whereValue()));
            }
            xmlReconstructed.append("    </munit:with-attributes>\n");
        }

        mockWhen.thenReturn()
                .flatMap(mule.common.MUnitModel.MockReturn::payload)
                .ifPresent(payload -> {
                    xmlReconstructed.append("    <munit:when>\n");
                    xmlReconstructed.append("        <munit:with-payload payload='%s'".formatted(payload.value()));
                    payload.mediaType().ifPresent(mt ->
                            xmlReconstructed.append(" mimeType=\"%s\"".formatted(mt)));
                    xmlReconstructed.append("/>\n");
                    xmlReconstructed.append("    </munit:when>\n");
                });

        xmlReconstructed.append("</munit:mock>");
        return List.of(stmtFrom(wrapInTodoComment(xmlReconstructed.toString(), TODO_MOCK_DESC)));
    }

    private static List<Statement> convertAssertEquals(Context ctx, AssertEquals assertEqual) {
        String actual = convertMUnitExpression(ctx, assertEqual.actual());
        String expected = convertMUnitExpression(ctx, assertEqual.expected());
        return List.of(stmtFrom("test:assertEquals(%s, %s);".formatted(actual, expected)));
    }

    private static List<Statement> convertAssertThat(Context ctx, AssertThat assertThat) {
        String expression = convertMUnitExpression(ctx, assertThat.expression());

        if (assertThat.is().contains("notNullValue()")) {
            return List.of(stmtFrom("test:assertNotEquals(%s, ());".formatted(expression)));
        }
        if (assertThat.is().contains("nullValue()")) {
            return List.of(stmtFrom("test:assertEquals(%s, ());".formatted(expression)));
        }
        if (assertThat.is().contains("equalTo(")) {
            String innerValue = extractMatcherArgument(assertThat.is(), "equalTo");
            String converted = convertMUnitExpression(ctx, innerValue);
            return List.of(stmtFrom("test:assertEquals(%s, %s);".formatted(expression, converted)));
        }

        List<Statement> result = new ArrayList<>();
        result.add(stmtFrom(wrapInTodoComment(
                "<munit:assert-that expression=\"%s\" is=\"%s\"/>".formatted(assertThat.expression(), assertThat.is()),
                TODO_ASSERT_THAT_DESC)));
        result.add(stmtFrom("test:assertTrue(%s != ());".formatted(expression)));
        return result;
    }

    private static List<Statement> convertAssertNotNull(Context ctx, AssertNotNull assertNotNull) {
        String expression = convertMUnitExpression(ctx, assertNotNull.expression());
        return List.of(stmtFrom("test:assertNotEquals(%s, ());".formatted(expression)));
    }

    private static List<Statement> convertFail(Fail fail) {
        String message = fail.message().map(m -> "\"" + escapeBalString(m) + "\"")
                .orElse("\"Test explicitly failed\"");
        return List.of(stmtFrom("test:assertFail(%s);".formatted(message)));
    }

    private static String escapeBalString(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private static List<Statement> convertSetEvent(Context ctx, SetEvent setEvent) {
        List<Statement> statements = new ArrayList<>();

        setEvent.payload().ifPresent(payload -> {
            String converted = convertMUnitExpression(ctx, payload);
            statements.add(stmtFrom("ctx.payload = %s;".formatted(converted)));
        });

        for (SetEventVariable variable : setEvent.variables()) {
            String value = convertMUnitExpression(ctx, variable.value());
            String key = ConversionUtils.convertToBalIdentifier(variable.key());
            statements.add(stmtFrom("ctx.flowVars.%s = %s;".formatted(key, value)));
        }

        return statements;
    }

    private static List<Statement> convertVerifyCall(VerifyCall verifyCall) {
        StringBuilder xmlReconstructed = new StringBuilder();
        xmlReconstructed.append("<munit:verify-call messageProcessor=\"%s\"".formatted(verifyCall.processor()));
        verifyCall.docName().ifPresent(dn -> xmlReconstructed.append(" doc:name=\"%s\"".formatted(dn)));
        verifyCall.times().ifPresent(t -> xmlReconstructed.append(" times=\"%s\"".formatted(t)));
        verifyCall.atLeast().ifPresent(al -> xmlReconstructed.append(" atLeast=\"%s\"".formatted(al)));
        verifyCall.atMost().ifPresent(am -> xmlReconstructed.append(" atMost=\"%s\"".formatted(am)));
        xmlReconstructed.append("/>");
        return List.of(stmtFrom(wrapInTodoComment(xmlReconstructed.toString(), TODO_VERIFY_DESC)));
    }

    private static List<Statement> convertMuleProcessor(Context ctx, MuleProcessorRef processorRef) {
        return MuleConfigConverter.convertTopLevelMuleBlocks(ctx, List.of(processorRef.muleRecord()));
    }

    private static List<Statement> convertUnsupportedMUnitBlock(UnsupportedMUnitBlock unsupported) {
        return List.of(stmtFrom(wrapInTodoComment(unsupported.xmlBlock(), TODO_UNSUPPORTED_DESC)));
    }

    private static String convertMUnitExpression(Context ctx, String muleExpr) {
        if (muleExpr == null || muleExpr.isEmpty()) {
            return "()";
        }
        if (muleExpr.startsWith("#[") && muleExpr.endsWith("]")) {
            String inner = muleExpr.substring(2, muleExpr.length() - 1).trim();
            return convertInnerMELExpression(ctx, inner);
        }
        return "\"" + escapeBalString(muleExpr) + "\"";
    }

    private static String convertInnerMELExpression(Context ctx, String inner) {
        if (inner.equals("payload")) {
            return Constants.PAYLOAD_FIELD_ACCESS;
        }
        if (inner.startsWith("payload.")) {
            return Constants.PAYLOAD_FIELD_ACCESS + inner.substring("payload".length());
        }
        if (inner.startsWith("flowVars.") || inner.startsWith("flowVars[")) {
            return Constants.FLOW_VARS_FIELD_ACCESS + inner.substring("flowVars".length());
        }
        if (inner.startsWith("sessionVars.") || inner.startsWith("sessionVars[")) {
            return Constants.SESSION_VARS_FIELD_ACCESS + inner.substring("sessionVars".length());
        }
        if (inner.startsWith("message.inboundProperties.")) {
            String propPath = inner.substring("message.inboundProperties.".length());
            return Constants.INBOUND_PROPERTIES_FIELD_ACCESS + "." + propPath;
        }
        if (inner.startsWith("'")) {
            return "\"" + escapeBalString(inner.substring(1, inner.length() - 1)) + "\"";
        }
        if (isLiteralValue(inner.trim())) {
            return inner.trim();
        }
        return ConversionUtils.convertMuleExprToBal(ctx, inner);
    }

    private static boolean isLiteralValue(String expr) {
        if (expr.equals("true") || expr.equals("false") || expr.equals("null")) {
            return true;
        }
        try {
            Double.parseDouble(expr);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static String extractMatcherArgument(String matcherExpr, String matcherName) {
        int start = matcherExpr.indexOf(matcherName + "(");
        if (start < 0) {
            return matcherExpr;
        }
        int argStart = start + matcherName.length() + 1;
        int depth = 1;
        int i = argStart;
        while (i < matcherExpr.length() && depth > 0) {
            char c = matcherExpr.charAt(i);
            if (c == '(') {
                depth++;
            } else if (c == ')') {
                depth--;
            }
            i++;
        }
        return matcherExpr.substring(argStart, i - 1).trim();
    }

    private static String wrapInTodoComment(String content, String todoDescription) {
        StringBuilder sb = new StringBuilder("\n\n");
        sb.append("// TODO: %s\n".formatted(todoDescription));
        sb.append("// ------------------------------------------------------------------------\n");
        for (String line : content.split("\n")) {
            sb.append("// ").append(line).append("\n");
        }
        sb.append("// ------------------------------------------------------------------------\n\n");
        return sb.toString();
    }
}
