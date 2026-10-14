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
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package synapse.converter.bir.mediators;

import common.BallerinaModel.Expression;
import common.BallerinaModel.Expression.BallerinaExpression;
import common.BallerinaModel.Expression.StringConstant;
import common.BallerinaModel.Expression.XMLTemplate;
import common.BallerinaModel.Statement;
import org.jetbrains.annotations.NotNull;
import synapse.converter.ConversionContext;
import synapse.converter.ConversionContext.UnsupportedEntry;
import synapse.converter.ScopeContext;
import synapse.converter.bir.BIRConverter;
import synapse.model.Synapse.PayloadFactory;
import synapse.model.Synapse.SynapseNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Converts a Synapse {@code <payloadFactory>} mediator into an assignment of the built payload onto
 * {@code ctx.payload}. The payload is not written to an {@code http:Response} here; a later
 * {@code <respond>} reads it back off {@code ctx} through the generated {@code respond} utility.
 */
public class PayloadFactoryConverter implements BIRConverter<ScopeContext> {

    // Matches a JSON string value that is exactly a Synapse '${properties.<scope>.<name>}' template
    // placeholder, e.g. "${properties.synapse.ERROR_MESSAGE}", quotes included.
    private static final Pattern WHOLE_VALUE_PLACEHOLDER_PATTERN =
            Pattern.compile("\"\\$\\{properties\\.(?:synapse|default)\\.([A-Za-z_][A-Za-z0-9_]*)\\}\"");

    // Matches a JSON string literal that contains a '${properties.<scope>.<name>}' placeholder embedded
    // alongside other text, capturing the string's body so it can be rewritten as a Ballerina string
    // template.
    private static final Pattern EMBEDDED_PLACEHOLDER_STRING_PATTERN = Pattern.compile(
            "\"((?:[^\"\\\\]|\\\\.)*\\$\\{properties\\.(?:synapse|default)\\.[A-Za-z_][A-Za-z0-9_]*\\}"
                    + "(?:[^\"\\\\]|\\\\.)*)\"");

    private static final Pattern PLACEHOLDER_PATTERN =
            Pattern.compile("\\$\\{properties\\.(?:synapse|default)\\.([A-Za-z_][A-Za-z0-9_]*)\\}");

    @Override
    public void convert(SynapseNode node, ScopeContext context) {
        PayloadFactory payloadFactory = (PayloadFactory) node;
        List<String> unresolvedProperties = new ArrayList<>();
        Expression value = extractValue(payloadFactory.mediaType(), payloadFactory.format(), context,
                unresolvedProperties);
        context.ensureContextAvailable();
        if (!unresolvedProperties.isEmpty()) {
            reportUnresolvedPropertyTemplates(payloadFactory, unresolvedProperties, context);
        }
        context.statements().add(new Statement.VarAssignStatement(
                new Expression.FieldAccess(new Expression.VariableReference("ctx"), "payload"), value));
    }

    private static Expression extractValue(String mediaType, String format, ScopeContext context,
                                           List<String> unresolvedProperties) {
        return switch (mediaType) {
            case "text" -> new StringConstant(format);
            case "xml" -> new XMLTemplate(format);
            // json (and others): the <format> is already a valid Ballerina literal
            // expression, aside from any '${properties...}' placeholders that still need resolving.
            default -> new BallerinaExpression(resolvePropertyTemplates(format, context, unresolvedProperties));
        };
    }

    // Resolves every '${properties.<scope>.<name>}' placeholder against the known default-scope
    // properties, in two passes.
    private static String resolvePropertyTemplates(String format, ScopeContext context,
                                                    List<String> unresolvedProperties) {
        ConversionContext sharedContext = context.shared();
        Set<String> availableProperties = sharedContext.availableDefaultScopeProperties();
        return substituteEmbeddedPlaceholders(
                substituteWholeValuePlaceholders(format, sharedContext, availableProperties, unresolvedProperties),
                sharedContext, availableProperties, unresolvedProperties);
    }

    @NotNull
    private static String substituteWholeValuePlaceholders(String format, ConversionContext sharedContext,
                                                            Set<String> availableProperties,
                                                            List<String> unresolvedProperties) {
        Matcher matcher = WHOLE_VALUE_PLACEHOLDER_PATTERN.matcher(format);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String name = matcher.group(1);
            String replacement;
            if (availableProperties.contains(name)) {
                // Declares the property if it's well-known but nothing has actually declared it yet.
                // see ConversionContext#ensureWellKnownPropertyDeclared.
                sharedContext.ensureWellKnownPropertyDeclared(name);
                replacement = "ctx.variables." + name;
            } else {
                replacement = matcher.group();
                unresolvedProperties.add(name);
            }
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    // Reached only for a quoted string that WHOLE_VALUE_PLACEHOLDER_PATTERN did not already consume.
    @NotNull
    private static String substituteEmbeddedPlaceholders(String format, ConversionContext sharedContext,
                                                          Set<String> availableProperties,
                                                          List<String> unresolvedProperties) {
        Matcher stringMatcher = EMBEDDED_PLACEHOLDER_STRING_PATTERN.matcher(format);
        StringBuilder result = new StringBuilder();
        while (stringMatcher.find()) {
            List<String> unresolvedInString = new ArrayList<>();
            String rewrittenBody = substitutePlaceholdersInBody(stringMatcher.group(1), sharedContext,
                    availableProperties, unresolvedInString);
            String replacement = unresolvedInString.isEmpty()
                    ? "`" + rewrittenBody + "`"
                    : stringMatcher.group();
            unresolvedProperties.addAll(unresolvedInString);
            stringMatcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        stringMatcher.appendTail(result);
        return result.toString();
    }

    @NotNull
    private static String substitutePlaceholdersInBody(String body, ConversionContext sharedContext,
                                                        Set<String> availableProperties,
                                                        List<String> unresolvedInString) {
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(body);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String name = matcher.group(1);
            String replacement;
            if (availableProperties.contains(name)) {
                sharedContext.ensureWellKnownPropertyDeclared(name);
                replacement = "${ctx.variables." + name + "}";
            } else {
                replacement = matcher.group();
                unresolvedInString.add(name);
            }
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    private static void reportUnresolvedPropertyTemplates(PayloadFactory payloadFactory,
                                                           List<String> unresolvedProperties, ScopeContext context) {
        String names = unresolvedProperties.stream().distinct().collect(Collectors.joining(", "));
        String detail = "This payloadFactory format references '${properties...}' placeholder(s) for "
                + names + ", not among the known default-scope properties; the literal template text is left "
                + "in the generated payload. Manual conversion required.";
        context.statements().add(new Statement.Comment("TODO: " + detail));
        context.shared().reportUnsupported(new UnsupportedEntry("Unsupported payloadFactory property template",
                "payloadFactory", context.shared().currentFile(), detail, payloadFactory.format()));
    }
}
