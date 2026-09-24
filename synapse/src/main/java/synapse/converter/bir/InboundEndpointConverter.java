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

import common.BallerinaModel.Expression;
import common.BallerinaModel.Expression.MappingConstructor;
import common.BallerinaModel.Expression.MappingConstructor.MappingField;
import common.BallerinaModel.Expression.StringConstant;
import common.BallerinaModel.Expression.VariableReference;
import common.BallerinaModel.Function;
import common.BallerinaModel.Import;
import common.BallerinaModel.Listener.FileListener;
import common.BallerinaModel.Listener.HTTPListener;
import common.BallerinaModel.Listener.JMSListener;
import common.BallerinaModel.ModuleVar;
import common.BallerinaModel.OnFailClause;
import common.BallerinaModel.Parameter;
import common.BallerinaModel.Remote;
import common.BallerinaModel.Resource;
import common.BallerinaModel.Service;
import common.BallerinaModel.Statement;
import common.BallerinaModel.TypeBindingPattern;
import common.BallerinaModel.TypeDesc;
import common.ConversionUtils;
import org.jetbrains.annotations.NotNull;
import synapse.converter.ConversionContext;
import synapse.converter.ConversionContext.UnsupportedEntry;
import synapse.converter.ResourceContext;
import synapse.model.Synapse.InboundEndpoint;
import synapse.model.Synapse.KeyStoreConfig;
import synapse.model.Synapse.Param;
import synapse.model.Synapse.SequenceMediator;
import synapse.model.Synapse.SynapseNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import static common.BallerinaModel.TypeDesc.BuiltinType.INT;
import static common.BallerinaModel.TypeDesc.BuiltinType.STRING;

/**
 * Converts a Synapse {@code <inboundEndpoint>} into a dedicated Ballerina listener plus a service that
 * forwards every message it receives straight into the referenced {@code sequence}, with {@code onError}
 * handled the same way a {@code <resource>}'s {@code faultSequence} is.
 *
 * <p>Only {@code http}/{@code https}, {@code jms} and {@code file} protocols are translated; every other
 * built-in protocol and every {@code class}-based (custom Java) inbound endpoint has no generated
 * Ballerina listener equivalent yet and is instead surfaced in the migration report.
 */
public class InboundEndpointConverter implements BIRConverter<ConversionContext> {

    private static final Set<String> HTTP_PROTOCOLS = Set.of("http", "https");
    private static final String HTTPS_PROTOCOL = "https";
    private static final String JMS_PROTOCOL = "jms";
    private static final String FILE_PROTOCOL = "file";

    private static final String HTTP_PORT_PARAM = "inbound.http.port";
    private static final String HTTP_HOST_PARAM = "inbound.http.host";
    private static final String DEFAULT_PORT = "8080";
    private static final String DEFAULT_HOST = "0.0.0.0";

    private static final String JMS_INITIAL_CONTEXT_FACTORY_PARAM = "java.naming.factory.initial";
    private static final String JMS_PROVIDER_URL_PARAM = "java.naming.provider.url";
    private static final String JMS_DESTINATION_PARAM = "transport.jms.Destination";
    private static final String JMS_CONNECTION_FACTORY_TYPE_PARAM = "transport.jms.ConnectionFactoryType";
    private static final String JMS_USERNAME_PARAM = "transport.jms.UserName";
    private static final String JMS_PASSWORD_PARAM = "transport.jms.Password";
    private static final String JMS_TOPIC_DESTINATION_TYPE = "topic";
    private static final Import JMS_MODULE_IMPORT = new Import("ballerinax", "java.jms");
    // ballerinax/java.jms is a generic JMS client with no bundled broker JNDI provider classes: without
    // this driver import, ActiveMQInitialContextFactory fails to instantiate at runtime.
    private static final Import JMS_ACTIVEMQ_DRIVER_IMPORT =
            new Import("ballerinax", "activemq.driver", Optional.of("_"));
    private static final String ACTIVEMQ_INITIAL_CONTEXT_FACTORY_CLASS =
            "org.apache.activemq.jndi.ActiveMQInitialContextFactory";
    private static final String JMS_MESSAGE_PARAM = "message";
    private static final String JMS_CALLER_PARAM = "caller";
    private static final String ON_MESSAGE_FUNCTION = "onMessage";

    private static final String FILE_URI_PARAM = "transport.vfs.FileURI";
    private static final String FILE_CONTENT_TYPE_PARAM = "transport.vfs.ContentType";
    private static final String FILE_ACTION_AFTER_PROCESS_PARAM = "transport.vfs.ActionAfterProcess";
    private static final String FILE_MOVE_AFTER_PROCESS_PARAM = "transport.vfs.MoveAfterProcess";
    private static final String FILE_ACTION_MOVE = "MOVE";
    private static final String FILE_ACTION_DELETE = "DELETE";
    private static final String LOCAL_FILE_SCHEME = "file";
    private static final String SCHEME_SEPARATOR = "://";
    private static final Import FILE_MODULE_IMPORT = new Import("ballerina", "file");
    private static final Import FILE_IO_MODULE_IMPORT = new Import("ballerina", "io");
    private static final Import LOG_MODULE_IMPORT = new Import("ballerina", "log");
    private static final String SCAN_ERROR_VAR = "err";
    private static final TypeBindingPattern SCAN_ERROR_BINDING =
            new TypeBindingPattern(new TypeDesc.BallerinaType("error"), SCAN_ERROR_VAR);
    private static final String FILE_EVENT_PARAM = "event";
    private static final String FILE_PATH_PARAM = "path";
    private static final String ON_CREATE_FUNCTION = "onCreate";

    private static final String LISTENER_SUFFIX = "Listener";
    private static final String ROOT_BASE_PATH = "/";
    // An inbound endpoint has no per-path/per-method dispatch of its own: every request that arrives on
    // its listener is forwarded to the same sequence, so the generated resource matches any method and
    // any path.
    private static final String ANY_PATH = "[string... path]";
    private static final String ANY_METHOD = "'default";
    private static final String CALLER_PARAM = "caller";
    private static final String REQUEST_PARAM = "request";
    private static final String ERROR_OPTIONAL_TYPE = "error?";

    @Override
    public void convert(SynapseNode node, ConversionContext context) {
        InboundEndpoint inboundEndpoint = (InboundEndpoint) node;
        if (inboundEndpoint.suspend()) {
            reportSuspendedInboundEndpoint(inboundEndpoint, context);
        }
        String protocol = inboundEndpoint.protocol().toLowerCase(Locale.ROOT);
        if (HTTP_PROTOCOLS.contains(protocol)) {
            if (HTTPS_PROTOCOL.equals(protocol) && inboundEndpoint.keyStore().isEmpty()) {
                reportUnmodeledHttpsTls(inboundEndpoint, context);
            }
            convertHttp(inboundEndpoint, context);
        } else if (JMS_PROTOCOL.equals(protocol)) {
            convertJms(inboundEndpoint, context);
        } else if (FILE_PROTOCOL.equals(protocol)) {
            convertFile(inboundEndpoint, context);
        } else {
            reportUnsupportedProtocol(inboundEndpoint, context);
        }
    }

    // https models server-authentication TLS only (keystore); mutual TLS and every other TLS parameter
    // still fall through to reportUnsupportedParameter below.
    private static void convertHttp(InboundEndpoint inboundEndpoint, ConversionContext context) {
        String baseName = ConversionUtils.lowerFirst(inboundEndpoint.name());
        String listenerName = baseName + LISTENER_SUFFIX;
        String port = DEFAULT_PORT;
        String host = DEFAULT_HOST;
        for (Param parameter : inboundEndpoint.parameters()) {
            switch (parameter.name()) {
                case HTTP_PORT_PARAM -> port = parameter.value();
                case HTTP_HOST_PARAM -> host = parameter.value();
                default -> reportUnsupportedParameter(inboundEndpoint, parameter, context);
            }
        }

        String portVar = baseName + "Port";
        String hostVar = baseName + "Host";
        context.addModuleVar(ModuleVar.configurable(portVar, INT, ConversionUtils.exprFrom(port)));
        context.addModuleVar(ModuleVar.configurable(hostVar, STRING, new StringConstant(host)));
        Optional<Expression> secureSocket = inboundEndpoint.keyStore()
                .map(keyStore -> readKeyStoreSecureSocket(inboundEndpoint, keyStore, context));
        context.addListener(new HTTPListener(listenerName, new VariableReference(portVar),
                Optional.of(new VariableReference(hostVar)), secureSocket));

        List<Parameter> parameters = List.of(
                new Parameter(CALLER_PARAM, new TypeDesc.BallerinaType("http:Caller")),
                new Parameter(REQUEST_PARAM, new TypeDesc.BallerinaType("http:Request")));

        ResourceContext resourceContext = new ResourceContext(context);
        resourceContext.initContext();
        resourceContext.statements().add(new Statement.BallerinaStatement("check emitPayload(ctx, request);"));
        MediatorConverters.convertMediators(
                List.of(new SequenceMediator(inboundEndpoint.sequenceKey())), resourceContext);
        FaultSequenceConverter.wrap(resourceContext, context, inboundEndpoint.onErrorRef(), "onError",
                "inbound endpoint", false);

        context.addImports(ConversionContext.MAIN_BAL_FILE, resourceContext.importStatements());
        Resource resource = new Resource(ANY_METHOD, ANY_PATH, parameters,
                Optional.of(new TypeDesc.BallerinaType(ERROR_OPTIONAL_TYPE)), resourceContext.statements());
        context.addService(new Service(ROOT_BASE_PATH, listenerName, List.of(resource)));
    }

    // Builds secureSocket.key (a crypto:KeyStore) from the keystore's Location/Password.
    @NotNull
    private static Expression readKeyStoreSecureSocket(InboundEndpoint inboundEndpoint, KeyStoreConfig keyStore,
                                                        ConversionContext context) {
        if (keyStore.keyPassword().isPresent() && !keyStore.keyPassword().get().equals(keyStore.password())) {
            reportUnmodeledKeyPassword(inboundEndpoint, context);
        }
        String baseName = ConversionUtils.lowerFirst(inboundEndpoint.name());
        String keyStorePathVar = baseName + "KeyStorePath";
        String keyStorePasswordVar = baseName + "KeyStorePassword";
        context.addModuleVar(ModuleVar.configurable(keyStorePathVar, STRING, new StringConstant(
                keyStore.location())));
        context.addModuleVar(ModuleVar.configurable(keyStorePasswordVar, STRING, new StringConstant(
                keyStore.password())));
        MappingConstructor key = new MappingConstructor(List.of(
                new MappingField("path", new VariableReference(keyStorePathVar)),
                new MappingField("password", new VariableReference(keyStorePasswordVar))));
        return new MappingConstructor(List.of(new MappingField("key", key)));
    }

    // A Synapse jms inbound endpoint is a fire-and-forget consumer with no reply transport: its
    // resourceContext's ctx never gets a caller (see initContextWithoutCaller), so a <respond/>
    // reached while converting its sequence, directly or via a called sequence, is still converted,
    // but the generated respond() reports an error at runtime instead of attempting to reply. That keeps
    // the generated code safe to compile, but it's still guaranteed to fail there at runtime, so
    // reportUnsupportedRespondIfNeeded also flags it in the migration report for manual review.
    private static void convertJms(InboundEndpoint inboundEndpoint, ConversionContext context) {
        String baseName = ConversionUtils.lowerFirst(inboundEndpoint.name());
        String listenerName = baseName + LISTENER_SUFFIX;
        Optional<String> initialContextFactory = Optional.empty();
        Optional<String> providerUrl = Optional.empty();
        Optional<String> destinationName = Optional.empty();
        Optional<String> username = Optional.empty();
        Optional<String> password = Optional.empty();
        for (Param parameter : inboundEndpoint.parameters()) {
            switch (parameter.name()) {
                case JMS_INITIAL_CONTEXT_FACTORY_PARAM -> initialContextFactory = Optional.of(parameter.value());
                case JMS_PROVIDER_URL_PARAM -> providerUrl = Optional.of(parameter.value());
                case JMS_DESTINATION_PARAM -> destinationName = Optional.of(parameter.value());
                case JMS_USERNAME_PARAM -> username = Optional.of(parameter.value());
                case JMS_PASSWORD_PARAM -> password = Optional.of(parameter.value());
                case JMS_CONNECTION_FACTORY_TYPE_PARAM ->
                        reportUnsupportedJmsConnectionFactoryType(inboundEndpoint, parameter, context);
                default -> reportUnsupportedParameter(inboundEndpoint, parameter, context);
            }
        }
        if (destinationName.isEmpty()) {
            destinationName = Optional.of(inboundEndpoint.name());
            reportMissingParameter(inboundEndpoint, JMS_DESTINATION_PARAM, context);
        }
        if (username.isPresent() != password.isPresent()) {
            reportUnpairedJmsCredential(inboundEndpoint, username.isPresent() ? JMS_USERNAME_PARAM
                    : JMS_PASSWORD_PARAM, context);
        }

        String initialContextFactoryVar = baseName + "InitialContextFactory";
        String providerUrlVar = baseName + "ProviderUrl";
        context.addModuleVar(ModuleVar.configurable(initialContextFactoryVar, STRING,
                new StringConstant(initialContextFactory.orElse(""))));
        context.addModuleVar(ModuleVar.configurable(providerUrlVar, STRING,
                new StringConstant(providerUrl.orElse(""))));

        Optional<Expression> usernameExpr = Optional.empty();
        Optional<Expression> passwordExpr = Optional.empty();
        if (username.isPresent() && password.isPresent()) {
            String usernameVar = baseName + "Username";
            String passwordVar = baseName + "Password";
            context.addModuleVar(ModuleVar.configurable(usernameVar, STRING, new StringConstant(username.get())));
            context.addModuleVar(ModuleVar.configurable(passwordVar, STRING, new StringConstant(password.get())));
            usernameExpr = Optional.of(new VariableReference(usernameVar));
            passwordExpr = Optional.of(new VariableReference(passwordVar));
        }

        context.addListener(new JMSListener(listenerName,
                jmsConnectionConfigSupplier(initialContextFactoryVar, providerUrlVar, usernameExpr, passwordExpr),
                destinationName.get()));
        context.addImports(ConversionContext.MAIN_BAL_FILE, List.of(JMS_MODULE_IMPORT));
        if (ACTIVEMQ_INITIAL_CONTEXT_FACTORY_CLASS.equals(initialContextFactory.orElse(""))) {
            context.addImports(ConversionContext.MAIN_BAL_FILE, List.of(JMS_ACTIVEMQ_DRIVER_IMPORT));
        }

        ResourceContext resourceContext = new ResourceContext(context);
        resourceContext.initContextWithoutCaller();
        resourceContext.statements().add(new Statement.BallerinaStatement(
                "if message !is jms:TextMessage { fail error(\"Unsupported JMS message type: expected a "
                        + "TextMessage\"); }"));
        resourceContext.statements().add(new Statement.BallerinaStatement("ctx.payload = message.content;"));
        MediatorConverters.convertMediators(
                List.of(new SequenceMediator(inboundEndpoint.sequenceKey())), resourceContext);
        boolean respondedInMainSequence = resourceContext.isResponded();
        FaultSequenceConverter.wrap(resourceContext, context, inboundEndpoint.onErrorRef(), "onError",
                "inbound endpoint", false);
        reportUnsupportedRespondIfNeeded(inboundEndpoint,
                respondedInMainSequence || resourceContext.isResponded(), context);

        context.addImports(ConversionContext.MAIN_BAL_FILE, resourceContext.importStatements());
        List<Parameter> parameters = List.of(
                new Parameter(JMS_MESSAGE_PARAM, new TypeDesc.BallerinaType("jms:Message")),
                new Parameter(JMS_CALLER_PARAM, new TypeDesc.BallerinaType("jms:Caller")));
        Function onMessage = new Function(ON_MESSAGE_FUNCTION, parameters,
                new TypeDesc.BallerinaType(ERROR_OPTIONAL_TYPE), resourceContext.statements());
        String serviceName = "\"" + inboundEndpoint.name() + "\"";
        context.addService(new Service(serviceName, List.of(listenerName), Optional.empty(), List.of(),
                List.of(), List.of(), List.of(new Remote(onMessage)), Optional.empty()));
    }

    // Mirrors JMSListener's own (initialContextFactory, providerUrl, username, password) convenience
    // constructor, but referencing the configurable variables convertJms declared instead of embedding
    // literal values directly, and evaluated lazily like that constructor since JMSListener.toString()
    // calls the supplier on every render.
    private static Supplier<Expression> jmsConnectionConfigSupplier(
            String initialContextFactoryVar, String providerUrlVar, Optional<Expression> usernameExpr,
            Optional<Expression> passwordExpr) {
        return () -> {
            List<MappingField> fields = new ArrayList<>();
            fields.add(new MappingField("initialContextFactory", new VariableReference(initialContextFactoryVar)));
            fields.add(new MappingField("providerUrl", new VariableReference(providerUrlVar)));
            usernameExpr.ifPresent(expr -> fields.add(new MappingField("username", expr)));
            passwordExpr.ifPresent(expr -> fields.add(new MappingField("password", expr)));
            return new MappingConstructor(fields);
        };
    }

    // A Synapse file (VFS) inbound endpoint polls a directory and processes every file found there
    // exactly once, then applies ActionAfterProcess. file:Listener only reports files created after
    // it starts, so the per-file work is extracted into <baseName>ProcessFile and reused both by
    // onCreate (files created after startup) and by a one-time directory scan contributed to the
    // project's combined init() (files already present at startup) — see the comment attached to the
    // generated service for what is still not equivalent to Synapse's own VFS polling.
    private static void convertFile(InboundEndpoint inboundEndpoint, ConversionContext context) {
        String baseName = ConversionUtils.lowerFirst(inboundEndpoint.name());
        String listenerName = baseName + LISTENER_SUFFIX;
        Optional<String> fileUri = Optional.empty();
        Optional<String> contentType = Optional.empty();
        Optional<String> actionAfterProcess = Optional.empty();
        Optional<String> moveAfterProcessUri = Optional.empty();
        for (Param parameter : inboundEndpoint.parameters()) {
            switch (parameter.name()) {
                case FILE_URI_PARAM -> fileUri = Optional.of(parameter.value());
                case FILE_CONTENT_TYPE_PARAM -> contentType = Optional.of(parameter.value());
                case FILE_ACTION_AFTER_PROCESS_PARAM -> actionAfterProcess = Optional.of(parameter.value());
                case FILE_MOVE_AFTER_PROCESS_PARAM -> moveAfterProcessUri = Optional.of(parameter.value());
                default -> reportUnsupportedParameter(inboundEndpoint, parameter, context);
            }
        }
        if (fileUri.isEmpty()) {
            reportMissingParameter(inboundEndpoint, FILE_URI_PARAM, context);
        } else {
            Optional<String> scheme = vfsScheme(fileUri.get());
            if (scheme.isPresent() && !LOCAL_FILE_SCHEME.equalsIgnoreCase(scheme.get())) {
                reportUnsupportedRemoteVfsScheme(inboundEndpoint, new Param(FILE_URI_PARAM, fileUri.get()),
                        scheme.get(), context);
                return;
            }
        }

        String pathVar = baseName + "Path";
        context.addModuleVar(ModuleVar.configurable(pathVar, STRING,
                new StringConstant(toLocalPath(fileUri.orElse("")))));
        context.addListener(new FileListener(listenerName, new VariableReference(pathVar), false));
        context.addImports(ConversionContext.MAIN_BAL_FILE, List.of(FILE_MODULE_IMPORT));
        context.addImports(ConversionContext.FUNCTIONS_BAL_FILE, List.of(FILE_MODULE_IMPORT, FILE_IO_MODULE_IMPORT));

        ResourceContext resourceContext = new ResourceContext(context);
        resourceContext.initContextWithoutCaller();
        resourceContext.statements().add(new Statement.BallerinaStatement(readFileContentStatement(contentType)));
        MediatorConverters.convertMediators(
                List.of(new SequenceMediator(inboundEndpoint.sequenceKey())), resourceContext);
        boolean respondedInMainSequence = resourceContext.isResponded();
        addActionAfterProcessStatement(inboundEndpoint, baseName, actionAfterProcess, moveAfterProcessUri,
                resourceContext, context);
        FaultSequenceConverter.wrap(resourceContext, context, inboundEndpoint.onErrorRef(), "onError",
                "inbound endpoint", false);
        reportUnsupportedRespondIfNeeded(inboundEndpoint,
                respondedInMainSequence || resourceContext.isResponded(), context);

        context.addImports(ConversionContext.FUNCTIONS_BAL_FILE, resourceContext.importStatements());
        String processFileFunction = baseName + "ProcessFile";
        context.addFunction(new Function(processFileFunction, List.of(new Parameter(FILE_PATH_PARAM, STRING)),
                new TypeDesc.BallerinaType(ERROR_OPTIONAL_TYPE), resourceContext.statements()));

        List<Parameter> parameters = List.of(
                new Parameter(FILE_EVENT_PARAM, new TypeDesc.BallerinaType("file:FileEvent")));
        Function onCreate = new Function(ON_CREATE_FUNCTION, parameters,
                new TypeDesc.BallerinaType(ERROR_OPTIONAL_TYPE), List.of(new Statement.BallerinaStatement(
                        "return " + processFileFunction + "(" + FILE_EVENT_PARAM + ".name);")));
        Statement.Comment comment = new Statement.Comment("Synapse VFS inbound endpoints process each "
                + "discovered file exactly once. file:Listener only reports files created after it starts, "
                + "so pre-existing files are instead handled by a one-time directory scan in this project's "
                + "init() function. A file created in the brief window between that scan and this listener "
                + "actually starting, or one still being written when detected, may be missed or read "
                + "prematurely - file:Listener has no equivalent to Synapse's file-locking/stability checks.");
        context.addService(new Service("", List.of(listenerName), Optional.empty(), List.of(),
                List.of(), List.of(), List.of(new Remote(onCreate)), Optional.of(comment)));

        context.addModuleInitStatements(
                existingFilesScanStatements(inboundEndpoint, baseName, pathVar, processFileFunction, context));
    }

    // Absent/text content is read as a string; any other declared content type is read as raw bytes.
    // A transport.vfs.ContentType this doesn't recognize as text still gets one of these two fallbacks
    // rather than being reported as unsupported, since some read is always possible.
    private static String readFileContentStatement(Optional<String> contentType) {
        return "ctx.payload = check "
                + (contentType.isPresent() && !contentType.get().toLowerCase(Locale.ROOT).startsWith("text/")
                        ? "io:fileReadBytes" : "io:fileReadString")
                + "(" + FILE_PATH_PARAM + ");";
    }

    // Runs only after mediation of the file's content succeeds, matching Synapse's own ActionAfterProcess,
    // which does not apply on failure.
    private static void addActionAfterProcessStatement(InboundEndpoint inboundEndpoint, String baseName,
                                                        Optional<String> actionAfterProcess,
                                                        Optional<String> moveAfterProcessUri,
                                                        ResourceContext resourceContext,
                                                        ConversionContext context) {
        if (actionAfterProcess.isEmpty()) {
            return;
        }
        String action = actionAfterProcess.get();
        if (FILE_ACTION_DELETE.equalsIgnoreCase(action)) {
            resourceContext.statements().add(new Statement.BallerinaStatement(
                    "check file:remove(" + FILE_PATH_PARAM + ");"));
        } else if (FILE_ACTION_MOVE.equalsIgnoreCase(action)) {
            if (moveAfterProcessUri.isEmpty()) {
                reportMissingParameter(inboundEndpoint, FILE_MOVE_AFTER_PROCESS_PARAM, context);
            } else {
                Optional<String> moveScheme = vfsScheme(moveAfterProcessUri.get());
                if (moveScheme.isPresent() && !LOCAL_FILE_SCHEME.equalsIgnoreCase(moveScheme.get())) {
                    reportUnsupportedRemoteVfsScheme(inboundEndpoint,
                            new Param(FILE_MOVE_AFTER_PROCESS_PARAM, moveAfterProcessUri.get()), moveScheme.get(),
                            context);
                } else {
                    String moveDirVar = baseName + "MoveAfterProcessPath";
                    context.addModuleVar(ModuleVar.configurable(moveDirVar, STRING,
                            new StringConstant(toLocalPath(moveAfterProcessUri.get()))));
                    resourceContext.statements().add(new Statement.BallerinaStatement(
                            "check file:rename(" + FILE_PATH_PARAM + ", check file:joinPath(" + moveDirVar
                                    + ", check file:basename(" + FILE_PATH_PARAM + ")));"));
                }
            }
        } else {
            reportUnsupportedParameter(inboundEndpoint,
                    new Param(FILE_ACTION_AFTER_PROCESS_PARAM, action), context);
        }
    }

    // file:Listener has no way to replay files that already existed when it starts, so this backfill is
    // contributed to the project's single combined init() (see SynapseConverter#addModuleInitFunction)
    // instead. It is started asynchronously so a large backlog, or slow mediation, does not delay any
    // other listener's startup, and runs in its own do/on-fail so a failure - file:readDir itself, or
    // any file's mediation - is logged and swallowed there instead of aborting the combined init().
    private static List<Statement> existingFilesScanStatements(InboundEndpoint inboundEndpoint, String baseName,
                                                                 String pathVar, String processFileFunction,
                                                                 ConversionContext context) {
        String scanFunction = baseName + "ScanExistingFiles";
        String existingFilesVar = baseName + "ExistingFiles";
        List<Statement> scanBody = List.of(
                new Statement.BallerinaStatement("file:MetaData[] & readonly " + existingFilesVar
                        + " = check file:readDir(" + pathVar + ");"),
                new Statement.BallerinaStatement("foreach file:MetaData m in " + existingFilesVar + " {"
                        + " if !m.dir { check " + processFileFunction + "(m.absPath); } }"));
        List<Statement> onFailBody = List.of(new Statement.BallerinaStatement(
                "log:printError(\"Failed to process pre-existing files for inbound endpoint '"
                        + inboundEndpoint.name() + "'\", 'error = " + SCAN_ERROR_VAR + ");"));
        context.addImports(ConversionContext.FUNCTIONS_BAL_FILE, List.of(LOG_MODULE_IMPORT));
        context.addFunction(new Function(scanFunction, List.of(),
                List.of(new Statement.DoStatement(scanBody, new OnFailClause(onFailBody, SCAN_ERROR_BINDING)))));
        return List.of(new Statement.BallerinaStatement("_ = start " + scanFunction + "();"));
    }

    // transport.vfs.FileURI legitimately carries a non-"file" scheme (ftp/sftp/ftps/smb/webdav/zip): a
    // bare local path or a "file://..." URI has no scheme worth flagging, so only a scheme other than
    // "file" is reported. Absent entirely, empty is returned rather than the raw string prefix.
    private static Optional<String> vfsScheme(String fileUri) {
        int separatorIndex = fileUri.indexOf(SCHEME_SEPARATOR);
        return separatorIndex <= 0 ? Optional.empty() : Optional.of(fileUri.substring(0, separatorIndex));
    }

    private static String toLocalPath(String fileUri) {
        int separatorIndex = fileUri.indexOf(SCHEME_SEPARATOR);
        if (separatorIndex <= 0 || !LOCAL_FILE_SCHEME.equalsIgnoreCase(fileUri.substring(0, separatorIndex))) {
            return fileUri;
        }
        String path = fileUri.substring(separatorIndex + SCHEME_SEPARATOR.length());
        return path.matches("/[A-Za-z]:/.*") ? path.substring(1) : path;
    }

    private static void reportUnsupportedProtocol(InboundEndpoint inboundEndpoint, ConversionContext context) {
        String protocolLabel = inboundEndpoint.protocol().isBlank()
                ? "class=\"" + inboundEndpoint.className() + "\""
                : "protocol=\"" + inboundEndpoint.protocol() + "\"";
        String detail = "Inbound endpoint '" + inboundEndpoint.name() + "' uses " + protocolLabel
                + ", which has no generated Ballerina listener equivalent yet; manual conversion required.";
        context.reportUnsupported(new UnsupportedEntry("Unsupported inbound endpoint protocol", "inboundEndpoint",
                context.currentFile(), detail, inboundEndpoint.rawXml()));
    }

    // A file:Listener only understands local filesystem paths: a remote VFS scheme (ftp/sftp/ftps/smb/
    // webdav/zip) has no Ballerina equivalent yet, so no listener is generated at all rather than emitting
    // one that would silently fail to connect.
    private static void reportUnsupportedRemoteVfsScheme(InboundEndpoint inboundEndpoint, Param parameter,
                                                         String scheme, ConversionContext context) {
        String detail = "Inbound endpoint '" + inboundEndpoint.name() + "' parameter '" + parameter.name()
                + "' uses the \"" + scheme + "\" scheme, which has no generated Ballerina listener equivalent "
                + "yet (file:Listener only supports local paths); manual conversion required.";
        String snippet = "<parameter name=\"" + parameter.name() + "\">" + parameter.value() + "</parameter>";
        context.reportUnsupported(new UnsupportedEntry("Unsupported inbound endpoint parameter", "parameter",
                context.currentFile(), detail, snippet));
    }

    // Synapse deploys a suspend="true" inbound endpoint inactive; the generated Ballerina listener has no
    // equivalent paused state and starts accepting traffic immediately.
    private static void reportSuspendedInboundEndpoint(InboundEndpoint inboundEndpoint, ConversionContext context) {
        String detail = "Inbound endpoint '" + inboundEndpoint.name() + "' declares suspend=\"true\", so Synapse "
                + "would deploy it inactive; the generated Ballerina listener has no equivalent and starts "
                + "accepting traffic immediately. Manual conversion required.";
        context.reportUnsupported(new UnsupportedEntry("Unsupported inbound endpoint attribute", "suspend",
                context.currentFile(), detail, inboundEndpoint.rawXml()));
    }

    private static void reportUnsupportedParameter(InboundEndpoint inboundEndpoint, Param parameter,
                                                    ConversionContext context) {
        String detail = "Inbound endpoint '" + inboundEndpoint.name() + "' parameter '" + parameter.name()
                + "' is not mapped to any Ballerina construct; manual conversion required.";
        String snippet = "<parameter name=\"" + parameter.name() + "\">" + parameter.value() + "</parameter>";
        context.reportUnsupported(new UnsupportedEntry("Unsupported inbound endpoint parameter", "parameter",
                context.currentFile(), detail, snippet));
    }

    private static void reportMissingParameter(InboundEndpoint inboundEndpoint, String paramName,
                                                ConversionContext context) {
        String detail = "Inbound endpoint '" + inboundEndpoint.name() + "' is missing required parameter '"
                + paramName + "'; manual conversion required.";
        context.reportUnsupported(new UnsupportedEntry("Missing inbound endpoint parameter", "parameter",
                context.currentFile(), detail, inboundEndpoint.rawXml()));
    }

    // No keystore parameter to flag here: the gap is inherent to the protocol, not a specific parameter.
    private static void reportUnmodeledHttpsTls(InboundEndpoint inboundEndpoint, ConversionContext context) {
        String detail = "Inbound endpoint '" + inboundEndpoint.name() + "' uses protocol=\"https\" but declares no "
                + "keystore; TLS configuration cannot be built, so the generated listener is a plain, unencrypted "
                + "http:Listener. Manual conversion required to add TLS.";
        context.reportUnsupported(new UnsupportedEntry("Unmodeled inbound endpoint TLS configuration", "protocol",
                context.currentFile(), detail, inboundEndpoint.rawXml()));
    }

    // crypto:KeyStore has no separate key-password field, so a differing one can't be modeled.
    private static void reportUnmodeledKeyPassword(InboundEndpoint inboundEndpoint, ConversionContext context) {
        String detail = "Inbound endpoint '" + inboundEndpoint.name() + "' keystore declares a KeyPassword "
                + "distinct from its own Password; crypto:KeyStore has no separate key-password field, so the "
                + "keystore password is used for both. Manual verification required.";
        context.reportUnsupported(new UnsupportedEntry("Unmodeled inbound endpoint keystore key password",
                "parameter", context.currentFile(), detail, inboundEndpoint.rawXml()));
    }

    // JMSListener always binds to a jms:QUEUE destination; a topic destination has no IR equivalent yet,
    // so it is reported but a queue listener is still generated best-effort. A "queue" value (or the
    // parameter's absence) matches the hardcoded default and needs no report.
    private static void reportUnsupportedJmsConnectionFactoryType(InboundEndpoint inboundEndpoint, Param parameter,
                                                                  ConversionContext context) {
        if (!JMS_TOPIC_DESTINATION_TYPE.equalsIgnoreCase(parameter.value())) {
            return;
        }
        String detail = "Inbound endpoint '" + inboundEndpoint.name() + "' parameter '" + parameter.name()
                + "' is \"topic\", but the generated jms:Listener always binds to a queue; manual conversion "
                + "required.";
        String snippet = "<parameter name=\"" + parameter.name() + "\">" + parameter.value() + "</parameter>";
        context.reportUnsupported(new UnsupportedEntry("Unsupported inbound endpoint parameter", "parameter",
                context.currentFile(), detail, snippet));
    }

    // JMSListener's convenience constructor only carries username/password through when both are present
    // (see common.BallerinaModel.Listener.JMSListener), silently connecting without credentials otherwise.
    private static void reportUnpairedJmsCredential(InboundEndpoint inboundEndpoint, String presentParam,
                                                     ConversionContext context) {
        String missingParam = JMS_USERNAME_PARAM.equals(presentParam) ? JMS_PASSWORD_PARAM : JMS_USERNAME_PARAM;
        String detail = "Inbound endpoint '" + inboundEndpoint.name() + "' sets '" + presentParam + "' but not '"
                + missingParam + "'; the generated jms:Listener connects with no credentials at all. Manual "
                + "conversion required.";
        context.reportUnsupported(new UnsupportedEntry("Unsupported inbound endpoint parameter", "parameter",
                context.currentFile(), detail, inboundEndpoint.rawXml()));
    }

    // A referenced sequence may already have been converted standalone, assuming an http:Caller, before
    // this inbound endpoint's protocol was known - the generated respond() only reports an error at
    // runtime there, so this surfaces the residual risk in the migration report instead.
    //
    // responded is captured by the caller rather than read off resourceContext directly, since
    // FaultSequenceConverter.wrapInFaultHandler resets isResponded() to false before converting fault
    // mediators.
    private static void reportUnsupportedRespondIfNeeded(InboundEndpoint inboundEndpoint, boolean responded,
                                                          ConversionContext context) {
        if (!responded) {
            return;
        }
        String detail = "Inbound endpoint '" + inboundEndpoint.name() + "' (protocol=\""
                + inboundEndpoint.protocol() + "\") reaches a <respond/> mediator, directly or via a called "
                + "sequence, but this protocol has no reply transport to respond on; manual conversion required.";
        context.reportUnsupported(new UnsupportedEntry("Unsupported respond in non-HTTP inbound endpoint",
                "respond", context.currentFile(), detail, inboundEndpoint.rawXml()));
    }
}
