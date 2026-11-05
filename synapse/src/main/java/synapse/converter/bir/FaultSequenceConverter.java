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
package synapse.converter.bir;

import common.BallerinaModel.Import;
import common.BallerinaModel.OnFailClause;
import common.BallerinaModel.Statement;
import common.BallerinaModel.TypeBindingPattern;
import common.BallerinaModel.TypeDesc;
import org.jetbrains.annotations.NotNull;
import synapse.converter.ConversionContext;
import synapse.converter.ConversionContext.UnsupportedEntry;
import synapse.converter.ResourceContext;
import synapse.model.Synapse;
import synapse.model.Synapse.FaultSequence;
import synapse.model.Synapse.FaultSequenceRef;
import synapse.model.Synapse.SequenceMediator;
import synapse.model.Synapse.SynapseNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Wraps a resource body's tail statements in the {@code on fail} clause its {@link FaultSequenceRef}
 * describes. Shared between any converter that ends up with a resource-shaped body driven by a
 * Synapse construct offering a faultSequence-style attribute (an {@code <api>}'s {@code <resource>}
 * and an {@code <inboundEndpoint>}'s {@code onError} alike).
 */
final class FaultSequenceConverter {

    // Type and bound variable name for a faultSequence's 'on fail error err { ... }' clause.
    private static final TypeDesc ERROR_TYPE = new TypeDesc.BallerinaType("error");
    private static final String FAULT_ERROR_VAR = "err";
    private static final TypeBindingPattern ERROR_BINDING = new TypeBindingPattern(ERROR_TYPE, FAULT_ERROR_VAR);

    // Synapse's own hardcoded default (Log + Drop) composes no response at all.
    // Rather than guess at that, we deliberately respond ourselves: log the error
    // and send a real error status with a real error payload.
    private static final Import LOG_IMPORT = new Import("ballerina", "log");
    private static final String UNHANDLED_ERROR_LOG_MESSAGE = "Unhandled error in mediation";
    private static final int UNHANDLED_ERROR_STATUS_CODE = 500;

    private FaultSequenceConverter() {
    }

    // An unresolved key is reported and falls back to a default. An explicit but empty <faultSequence/>
    // is the author's deliberate choice to leave failures unhandled, so it gets an empty 'on fail'.
    static void wrap(ResourceContext resourceContext, ConversionContext context, FaultSequenceRef faultSequenceRef,
                      String sourceAttribute, String constructName, boolean unresolvedKeyUsesProjectDefault) {
        switch (faultSequenceRef) {
            case FaultSequenceRef.KeyRef(String key) -> {
                if (context.sequenceMetadata(key).isPresent()) {
                    wrapInFaultHandler(resourceContext, List.of(new SequenceMediator(key)));
                } else if (unresolvedKeyUsesProjectDefault) {
                    boolean usesProjectFaultSequence = wrapInGlobalDefaultFaultHandler(resourceContext, context);
                    reportUnresolvedFaultSequence(key, resourceContext, usesProjectFaultSequence, sourceAttribute);
                } else {
                    wrapInDefaultFaultHandler(resourceContext);
                    reportUnresolvedFaultSequence(key, resourceContext, false, sourceAttribute);
                }
            }
            case FaultSequenceRef.Inline(FaultSequence faultSequence) -> {
                if (faultSequence.mediators().isEmpty()) {
                    wrapInEmptyFaultHandler(resourceContext);
                } else {
                    wrapInFaultHandler(resourceContext, faultSequence.mediators());
                }
            }
            case FaultSequenceRef.None ignored -> {
                if (wrapInGlobalDefaultFaultHandler(resourceContext, context)) {
                    reportImplicitFaultSequence(resourceContext, sourceAttribute, constructName);
                }
            }
        }
    }

    // Wraps everything converted so far as `do { ... } on fail error err { <faultMediators> }`. The
    // ctx local at index 0 stays outside the 'do': Ballerina doesn't carry do-body locals into 'on
    // fail', and faultSequence mediators (e.g. <respond>) need ctx in scope.
    private static void wrapInFaultHandler(ResourceContext resourceContext, List<SynapseNode> faultMediators) {
        List<Statement> doBody = extractTrailingStatements(resourceContext);

        resourceContext.setResponded(false);
        seedErrorMessageProperty(resourceContext);
        MediatorConverters.convertMediators(faultMediators, resourceContext);
        List<Statement> onFailBody = extractTrailingStatements(resourceContext);

        resourceContext.statements().add(
                new Statement.DoStatement(doBody, new OnFailClause(onFailBody, ERROR_BINDING)));
    }

    // An explicit but empty <faultSequence/>: wrap in a 'do' with an empty 'on fail' so a failure is
    // caught and swallowed (the resource function returns normally, sending no response), rather than
    // running any fault handling at all.
    private static void wrapInEmptyFaultHandler(ResourceContext resourceContext) {
        List<Statement> doBody = extractTrailingStatements(resourceContext);
        resourceContext.statements().add(new Statement.DoStatement(doBody, new OnFailClause(List.of(), ERROR_BINDING)));
    }

    // Extracts everything after the ctx local at index 0 into its own list, clearing it from
    // resourceContext so the next section of the resource body starts clean.
    @NotNull
    private static List<Statement> extractTrailingStatements(ResourceContext resourceContext) {
        List<Statement> statements = resourceContext.statements();
        assert resourceContext.contextAvailable() && !statements.isEmpty()
                : "resource context must have its ctx local declared at statement index 0";
        List<Statement> tail = new ArrayList<>(statements.subList(1, statements.size()));
        statements.subList(1, statements.size()).clear();
        return tail;
    }

    // Seeds ERROR_MESSAGE from the caught err so get-property('ERROR_MESSAGE') resolves even when the
    // faultSequence runs as a separately generated function with no lexical access to err.
    private static void seedErrorMessageProperty(ResourceContext resourceContext) {
        resourceContext.shared().addProperty(Synapse.ERROR_MESSAGE_PROPERTY, "string", "default");
        resourceContext.statements().add(new Statement.BallerinaStatement(
                "ctx.variables." + Synapse.ERROR_MESSAGE_PROPERTY + " = " + FAULT_ERROR_VAR + ".message();"));
    }

    // Flags a faultSequence="X" (or onError="X") attribute that names no known sequence with an inline
    // TODO comment, so the fallback is never silent. usesProjectFaultSequence distinguishes what it
    // actually falls back to: the project's "fault" sequence, or the hardcoded default handler.
    private static void reportUnresolvedFaultSequence(String key, ResourceContext resourceContext,
                                                        boolean usesProjectFaultSequence, String sourceAttribute) {
        String file = resourceContext.shared().currentFile();
        String origin = file.isEmpty() ? "" : " (from " + file + ")";
        String snippet = sourceAttribute + "=\"" + key + "\"";
        String fallback = usesProjectFaultSequence
                ? "falling back to the project-level '" + Synapse.DEFAULT_FAULT_SEQUENCE_KEY + "' sequence"
                : "falling back to the default error handler";
        String detail = "Referenced fault sequence '" + key
                + "' was not found among the converted artifacts; " + fallback + ".";
        List<Statement> statements = resourceContext.statements();
        statements.add(statements.size() - 1, new Statement.Comment(
                "TODO: Unresolved Synapse fault sequence reference '" + key + "'" + origin + ". " + detail));
        resourceContext.shared().reportUnsupported(
                new UnsupportedEntry("Unresolved fault sequence", sourceAttribute, file, detail, snippet));
    }

    // Falls back to the project's "fault" sequence, if defined, else the hardcoded default. Reached for
    // a resource with no faultSequence at all, and for an unresolved faultSequence="X" attribute. Returns
    // whether the project's "fault" sequence was the one used, so callers can report accordingly.
    private static boolean wrapInGlobalDefaultFaultHandler(ResourceContext resourceContext,
                                                            ConversionContext context) {
        boolean usesProjectFaultSequence = context.sequenceMetadata(Synapse.DEFAULT_FAULT_SEQUENCE_KEY).isPresent();
        if (usesProjectFaultSequence) {
            wrapInFaultHandler(resourceContext, List.of(new SequenceMediator(Synapse.DEFAULT_FAULT_SEQUENCE_KEY)));
        } else {
            wrapInDefaultFaultHandler(resourceContext);
        }
        return usesProjectFaultSequence;
    }

    // Surfaces the implicit fallback to the project's "fault" sequence in the migration report, and flags
    // it with an inline TODO comment, so the fallback is visible in the generated code too, not just the
    // report. Only reached for a construct with no faultSequence/onError of its own; an unresolved
    // explicit faultSequence="X"/onError="X" is reported by reportUnresolvedFaultSequence instead.
    private static void reportImplicitFaultSequence(ResourceContext resourceContext, String sourceAttribute,
                                                     String constructName) {
        String file = resourceContext.shared().currentFile();
        String detail = "This " + constructName + " has no " + sourceAttribute + " of its own; because a "
                + "project-level sequence named '" + Synapse.DEFAULT_FAULT_SEQUENCE_KEY + "' exists, it is used "
                + "implicitly as this " + constructName + "'s error handler. Verify this matches the intended "
                + "behavior, or rename the sequence if it is unrelated to error handling.";
        List<Statement> statements = resourceContext.statements();
        statements.add(statements.size() - 1, new Statement.Comment(
                "TODO: Implicit Synapse fault sequence '" + Synapse.DEFAULT_FAULT_SEQUENCE_KEY + "'. " + detail));
        resourceContext.shared().reportUnsupported(
                new UnsupportedEntry("Implicit fault sequence", sourceAttribute, file, detail, ""));
    }

    // No faultSequence at all, and no project-level "fault" sequence either: log the error, and respond
    // with an error status and a JSON error payload carrying the caught error's message if this scope has
    // a caller to respond to.
    private static void wrapInDefaultFaultHandler(ResourceContext resourceContext) {
        List<Statement> doBody = extractTrailingStatements(resourceContext);
        resourceContext.importStatements().add(LOG_IMPORT);
        resourceContext.statements().add(new Statement.DoStatement(doBody,
                new OnFailClause(defaultOnFailBody(resourceContext.hasCaller()), ERROR_BINDING)));
    }

    // Log the error and, if this scope has a caller (see ScopeContext#hasCaller), respond
    // with an error status and a JSON error payload carrying the caught error's message. respond() is a
    // no-op if the caller has already had a response attempted on it, so this is always safe to call.
    @NotNull
    private static List<Statement> defaultOnFailBody(boolean hasCaller) {
        List<Statement> body = new ArrayList<>(List.of(new Statement.BallerinaStatement(
                "log:printError(\"" + UNHANDLED_ERROR_LOG_MESSAGE + "\", 'error = " + FAULT_ERROR_VAR + ");")));
        if (hasCaller) {
            body.add(new Statement.BallerinaStatement(
                    "ctx.payload = {\"error\": " + FAULT_ERROR_VAR + ".message()};"));
            body.add(new Statement.BallerinaStatement("ctx.statusCode = " + UNHANDLED_ERROR_STATUS_CODE + ";"));
            body.add(new Statement.BallerinaStatement("check respond(ctx);"));
        }
        return body;
    }
}
