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
package mule.v4;

import common.BallerinaModel.Statement.BallerinaStatement;
import common.BallerinaModel.TypeDesc.RecordTypeDesc;
import common.BallerinaModel.TypeDesc.RecordTypeDesc.RecordField;
import common.CodeGenerator;
import io.ballerina.compiler.syntax.tree.CaptureBindingPatternNode;
import io.ballerina.compiler.syntax.tree.NodeParser;
import io.ballerina.compiler.syntax.tree.NodeVisitor;
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import mule.common.MuleLogger;
import mule.common.apispec.ApiContractChecker;
import mule.common.apispec.ApiKitFlowName;
import mule.common.apispec.ApiPolicies;
import mule.common.apispec.ApiSpec;
import mule.common.apispec.ApiSpecResult;
import mule.common.apispec.ApiTypeGenerator;
import mule.v4.ApiSpecTestGenerator.ServiceAddress;
import mule.v4.converter.MuleConfigConverter;
import mule.v4.converter.MuleConfigConverter.HttpResponseHeaders;
import mule.v4.converter.ScriptConversionException;
import mule.v4.model.MuleModel.AnypointMqSubscriber;
import mule.v4.model.MuleModel.ApiKitConfig;
import mule.v4.model.MuleModel.ApiKitRouter;
import mule.v4.model.MuleModel.DbConfig;
import mule.v4.model.MuleModel.DbGenericConnection;
import mule.v4.model.MuleModel.PubSubMessageListener;
import mule.v4.model.MuleModel.Scheduler;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static common.BallerinaModel.BlockFunctionBody;
import static common.BallerinaModel.ClassDef;
import static common.BallerinaModel.Expression;
import static common.BallerinaModel.Expression.VariableReference;
import static common.BallerinaModel.Function;
import static common.BallerinaModel.HTTPInterceptor;
import static common.BallerinaModel.Import;
import static common.BallerinaModel.Listener;
import static common.BallerinaModel.Remote;
import static common.BallerinaModel.ModuleTypeDef;
import static common.BallerinaModel.ModuleVar;
import static common.BallerinaModel.Parameter;
import static common.BallerinaModel.Resource;
import static common.BallerinaModel.Service;
import static common.BallerinaModel.Statement;
import static common.BallerinaModel.TextDocument;
import static common.BallerinaModel.TypeDesc;
import static common.ConversionUtils.escapeIdentifier;
import static common.ConversionUtils.exprFrom;
import static common.ConversionUtils.stmtFrom;
import static common.ConversionUtils.typeFrom;
import static mule.v4.Constants.BAL_ANYDATA_TYPE;
import static mule.v4.Constants.HTTP_RESPONSE_TYPE;
import static mule.v4.Constants.JMS_CONNECTION_CONFIGURATION_TYPE;
import static mule.v4.Constants.JMS_MESSAGE_REF;
import static mule.v4.ConversionUtils.convertToBalIdentifier;
import static mule.v4.ConversionUtils.getAttrVal;
import static mule.v4.ConversionUtils.getAttrValInt;
import static mule.v4.ConversionUtils.getBallerinaAbsolutePath;
import static mule.v4.ConversionUtils.getBallerinaResourcePath;
import static mule.v4.ConversionUtils.insertLeadingSlash;
import static mule.v4.converter.MuleConfigConverter.convertErrorHandlerRecords;
import static mule.v4.converter.MuleConfigConverter.convertHttpResponseBody;
import static mule.v4.converter.MuleConfigConverter.convertHttpResponseHeaderMap;
import static mule.v4.converter.MuleConfigConverter.convertHttpResponseHeaders;
import static mule.v4.converter.MuleConfigConverter.convertHttpResponseStatusCode;
import static mule.v4.converter.MuleConfigConverter.convertHttpResponseStatusCodeExpr;
import static mule.v4.converter.MuleConfigConverter.setHttpResponseHeaders;
import static mule.v4.converter.MuleConfigConverter.convertTopLevelMuleBlocks;
import static mule.v4.model.MuleModel.DbConnection;
import static mule.v4.model.MuleModel.DbMySqlConnection;
import static mule.v4.model.MuleModel.DbOracleConnection;
import static mule.v4.model.MuleModel.ErrorHandler;
import static mule.v4.model.MuleModel.FileConfig;
import static mule.v4.model.MuleModel.FileListener;
import static mule.v4.model.MuleModel.Flow;
import static mule.v4.model.MuleModel.GlobalProperty;
import static mule.v4.model.MuleModel.HTTPListenerConfig;
import static mule.v4.model.MuleModel.HttpListener;
import static mule.v4.model.MuleModel.HttpResponse;
import static mule.v4.model.MuleModel.Kind;
import static mule.v4.model.MuleModel.MuleRecord;
import static mule.v4.model.MuleModel.SubFlow;
import static mule.v4.model.MuleModel.UnsupportedBlock;
import static mule.v4.model.MuleModel.VMListener;

public class MuleToBalConverter {

    // The response of the resource being generated, which the resource returns
    private static final String RESPONSE_EXPR = "<%s>%s.%s".formatted(HTTP_RESPONSE_TYPE,
            Constants.ATTRIBUTES_FIELD_ACCESS, Constants.HTTP_RESPONSE_REF);
    private static final String RESPONSE_REF_EXPR = "(" + RESPONSE_EXPR + ")";
    private static final String STATUS_CODE_VAR = "statusCode";

    public static SyntaxTree convertStandaloneXMLFileToBallerina(String xmlFilePath, MuleLogger logger) {
        Context ctx = new Context(List.of(Path.of(xmlFilePath).toFile()), List.of(), logger);
        ctx.parseAllFiles();
        TextDocument txtDoc = ctx.codeGen().getFirst();
        return new CodeGenerator(txtDoc).generateSyntaxTree();
    }

    public static TextDocument generateTextDocument(Context ctx, String balFileName,
                                                    List<Flow> flows, List<SubFlow> subFlows)
            throws ScriptConversionException {
        List<Service> services = new ArrayList<>();
        Set<Function> functions = new LinkedHashSet<>();
        List<ClassDef> classDefs = new ArrayList<>();
        List<Flow> privateFlows = new ArrayList<>();
        List<Listener> listeners = new ArrayList<>();

        flows.stream()
                .sorted(Comparator.comparing(flow -> {
                    Optional<MuleRecord> source = flow.source();
                    return !(source.isPresent() && source.get() instanceof HttpListener);
                }))
                .forEachOrdered(flow -> {
                    Optional<MuleRecord> source = flow.source();
                    if (source.isEmpty()) {
                        privateFlows.add(flow);
                        return;
                    }

                    MuleRecord src = source.get();
                    try {
                        switch (src) {
                            case VMListener vmListener -> genVMListenerSource(ctx, flow, vmListener, functions);
                            case Scheduler scheduler -> genSchedulerSource(ctx, flow, scheduler, functions, classDefs);
                            case HttpListener httpListener -> ctx.projectCtx.lastHttpService = genHttpSource(ctx, flow,
                                    httpListener, services, functions);
                            case ApiKitConfig apiKit ->
                                    genApiKitSource(ctx, flow, apiKit, services, ctx.projectCtx.lastHttpService);
                            case AnypointMqSubscriber mqSubscriber ->
                                    genAnypointMqSource(ctx, flow, mqSubscriber, services, listeners);
                            case PubSubMessageListener pubSubListener ->
                                    genPubSubSource(ctx, flow, pubSubListener, services, listeners);
                            case FileListener fileListener ->
                                    genFileListenerSource(ctx, flow, fileListener, services, listeners);
                            default -> throw new IllegalStateException(
                                    "Unsupported source kind: %s".formatted(src.kind()));
                        }
                    } catch (ScriptConversionException e) {
                        throw new RuntimeException(e);
                    }
                });

        List<Service> normalizeServices = normalizeServices(services);

        // Create functions for private flows
        genBalFuncsFromPrivateFlows(ctx, privateFlows, functions);

        // Create functions for sub-flows
        genBalFuncsFromSubFlows(ctx, subFlows, functions);
        functions.addAll(ctx.currentFileCtx.balConstructs.functions);
        functions.addAll(ctx.currentFileCtx.balConstructs.commonFunctions.values());

        // Create functions for global exception strategies
        for (ErrorHandler errorHandler : ctx.currentFileCtx.configs.globalErrorHandlers) {
            genBalFuncForGlobalErrorHandler(ctx, errorHandler, functions);
            functions.addAll(ctx.currentFileCtx.balConstructs.functions); // TODO: this is a hack.
        }

        // Add global HTTP listeners from configs
        for (HTTPListenerConfig httpListenerConfig : ctx.currentFileCtx.configs.httpListenerConfigs.values()) {
            listeners.add(new Listener.HTTPListener(convertToBalIdentifier(httpListenerConfig.name()),
                    getAttrValInt(ctx, httpListenerConfig.port()), httpListenerConfig.host()));
        }

        // Add module vars
        List<ModuleVar> moduleVars = new ArrayList<>();
        for (DbConfig dbConfig : ctx.currentFileCtx.configs.dbConfigs.values()) {
            DbConnection dbConnection = dbConfig.dbConnection();
            TypeDesc dbClientType;
            Expression balExpr;
            if (dbConnection.kind() == Kind.DB_MYSQL_CONNECTION) {
                DbMySqlConnection con = (DbMySqlConnection) dbConnection;
                dbClientType = typeFrom(Constants.MYSQL_CLIENT_TYPE);
                balExpr = exprFrom("check new (%s, %s, %s, %s, %s)".formatted(
                        getAttrVal(ctx, con.host()), getAttrVal(ctx, con.user()),
                        getAttrVal(ctx, con.password()), getAttrVal(ctx, con.database()),
                        getAttrValInt(ctx, con.port())));
            } else if (dbConnection.kind() == Kind.DB_ORACLE_CONNECTION) {
                DbOracleConnection con = (DbOracleConnection) dbConnection;
                dbClientType = typeFrom(Constants.ORACLEDB_CLIENT_TYPE);
                String database = con.instance().isEmpty() ? con.serviceName() : con.instance();
                String port = con.port().isEmpty() ? "1521" : con.port();
                balExpr = exprFrom(String.format("check new (%s, %s, %s, %s, %s)",
                        getAttrVal(ctx, con.host()), getAttrVal(ctx, con.user()),
                        getAttrVal(ctx, con.password()), getAttrVal(ctx, database),
                        getAttrValInt(ctx, port)));
            } else if (dbConnection.kind() == Kind.DB_GENERIC_CONNECTION) {
                DbGenericConnection con = (DbGenericConnection) dbConnection;
                dbClientType = typeFrom(Constants.JDBC_CLIENT_TYPE);
                String url = getAttrVal(ctx, con.url());
                JavaDependencies javaDependencies = determineJdbcDependencyFromUrl(url);
                ctx.projectCtx.addJavaDependency(javaDependencies);
                balExpr = exprFrom(String.format("check new (%s, %s, %s)",
                        url, getAttrVal(ctx, con.user()), getAttrVal(ctx, con.password())));
            } else {
                throw new IllegalStateException("Unsupported DB connection type: " + dbConnection.kind());
            }

            moduleVars
                    .add(new ModuleVar(convertToBalIdentifier(dbConfig.name()), dbClientType, balExpr));
        }

        moduleVars.addAll(ctx.currentFileCtx.balConstructs.moduleVars.values());

        // Global comments at the end of file
        List<String> comments = new ArrayList<>();
        for (UnsupportedBlock unsupportedBlock : ctx.currentFileCtx.configs.unsupportedBlocks) {
            String comment = ConversionUtils.convertToUnsupportedTODO(ctx, unsupportedBlock);
            comments.add(comment);
        }

        List<ModuleTypeDef> typeDefs;
        if (ctx.isStandaloneBalFile()) {
            List<ModuleTypeDef> contextTypeDefns = createContextTypeDefns(ctx);
            contextTypeDefns.addAll(ctx.currentFileCtx.balConstructs.typeDefs.values());
            typeDefs = contextTypeDefns;
        } else {
            typeDefs = ctx.currentFileCtx.balConstructs.typeDefs.values().stream().toList();
        }

        for (GlobalProperty globalProperty : ctx.currentFileCtx.configs.globalProperties) {
            String configVarName = globalProperty.name().replace('.', '_');
            ConversionUtils.addConfigVarEntry(ctx, configVarName, globalProperty.value());
        }

        ArrayList<ModuleVar> orderedModuleVars = new ArrayList<>(
                ctx.getCurrentFileConfigurableVars());
        orderedModuleVars.addAll(moduleVars);
        return createTextDocument(balFileName + ".bal", new ArrayList<>(ctx.currentFileCtx.balConstructs.imports),
                typeDefs, orderedModuleVars, listeners, normalizeServices, classDefs, functions.stream().toList(),
                comments);
    }

    private static void genApiKitSource(Context ctx, Flow flow, ApiKitConfig apiKit, Collection<Service> services,
                                        Service lastHttpService) {
        ctx.inServiceGen = true;
        // TODO: Common with httpSource refactor
        ctx.projectCtx.attributes.put(Constants.HTTP_REQUEST_REF, Constants.HTTP_REQUEST_TYPE);
        ctx.projectCtx.attributes.put(Constants.HTTP_RESPONSE_REF, HTTP_RESPONSE_TYPE);
        ctx.projectCtx.attributes.put(Constants.URI_PARAMS_REF, "map<string>");

        if (lastHttpService == null) {
            throw new IllegalStateException("API Kit flow %s requires an HTTP listener to be processed first"
                    .formatted(flow.name()));
        }

        ApiKitConfig.HTTPResourceData resourceData = apiKit.resourcePathData(flow);
        String resourceMethod = resourceData.method();
        List<Statement> bodyCoreStmts = convertTopLevelMuleBlocks(ctx, flow.flowBlocks());
        Optional<SpecResource> specResource = specResource(ctx, flow, apiKit, reservedNames(bodyCoreStmts));
        Optional<SpecResourceSignature> specSignature = specResource.map(SpecResource::signature);

        String resourcePath;
        List<Parameter> resourceParams;
        Optional<String> uriParamsInit;
        if (specSignature.isPresent()) {
            resourcePath = specSignature.get().resourcePath();
            resourceParams = specSignature.get().parameters();
            uriParamsInit = specSignature.get().uriParamsInit();
        } else {
            // A resource path is relative to the HTTP service base path.
            String apiKitResourcePath = resourceData.resourcePath();
            resourcePath = apiKitResourcePath.startsWith("/") ? apiKitResourcePath.substring(1) : apiKitResourcePath;
            resourceParams = List.of(new Parameter(Constants.HTTP_REQUEST_REF, typeFrom(Constants.HTTP_REQUEST_TYPE)));
            uriParamsInit = uriParamsInit(resourceData.pathParams());
        }
        resourcePath = withPrefix(ctx.projectCtx.apiKitResourcePrefixes.getOrDefault(apiKit.name(), ""), resourcePath);

        // In Mule the payload of a listener flow starts as the request body
        String payloadField = specSignature.flatMap(SpecResourceSignature::payloadRef)
                .map(payloadRef -> payloadRef.equals(Constants.PAYLOAD_REF)
                        ? payloadRef + ", "
                        : "%s: %s, ".formatted(Constants.PAYLOAD_REF, payloadRef))
                .orElse("");
        List<Statement> bodyStmts = new ArrayList<>();
        bodyStmts.add(stmtFrom("Context %s = {%s%s: %s};".formatted(Constants.CONTEXT_REFERENCE, payloadField,
                Constants.ATTRIBUTES_REF, getAttributesInitValue(ctx, uriParamsInit))));

        bodyStmts.addAll(bodyCoreStmts);
        Optional<HttpResponse> routerResponse =
                Optional.ofNullable(ctx.projectCtx.apiKitRouterResponses.get(apiKit.name()));
        Optional<CheckedResponse> checkedResponse = checkedResponse(ctx, routerResponse,
                specResource.map(SpecResource::successResponses).orElse(List.of()));
        bodyStmts.addAll(checkedResponse.map(CheckedResponse::statements)
                .orElseGet(() -> responseStatements(ctx, routerResponse)));

        // Add service resources
        TypeDesc returnType = typeFrom(checkedResponse.map(CheckedResponse::returnType)
                .orElse(Constants.HTTP_RESOURCE_RETURN_TYPE_DEFAULT));
        ctx.currentFileCtx.balConstructs.imports.add(Constants.HTTP_MODULE_IMPORT);

        Resource resource = new Resource(resourceMethod, resourcePath, resourceParams, Optional.of(returnType),
                bodyStmts);
        lastHttpService.resources().add(resource);
        lastHttpService.initFunc().map(initFn -> switch (initFn.body()) {
            case BlockFunctionBody blockFunctionBody -> blockFunctionBody.statements().addAll(ctx.initFunctionBody);
            default -> throw new IllegalStateException("Unexpected value: " + initFn.body());
        });
        lastHttpService.fields().addAll(ctx.serviceFields);
        ctx.resetServiceState();
    }

    private static String withPrefix(String prefix, String resourcePath) {
        if (prefix.isEmpty()) {
            return resourcePath;
        }
        return resourcePath.isEmpty() || resourcePath.equals(".") ? prefix : prefix + "/" + resourcePath;
    }

    // The flow body and the response statements declare these names, so a parameter that took one would clash
    private static Set<String> reservedNames(List<Statement> bodyStmts) {
        Set<String> names = new HashSet<>(MuleConfigConverter.HTTP_RESPONSE_HEADER_VARS);
        names.add(Constants.HTTP_RESPONSE_REF);
        names.add(STATUS_CODE_VAR);
        NodeParser.parseBlockStatement(bodyStmts.stream().map(Statement::toString)
                .collect(Collectors.joining("\n", "{\n", "\n}"))).accept(new NodeVisitor() {
                    @Override
                    public void visit(CaptureBindingPatternNode captureBindingPattern) {
                        names.add(captureBindingPattern.variableName().text());
                    }
                });
        return names;
    }

    private static Optional<SpecResource> specResource(Context ctx, Flow flow, ApiKitConfig apiKit,
                                                       Set<String> reservedNames) {
        if (!(ctx.projectCtx.apiSpecs.get(apiKit.name()) instanceof ApiSpecResult.Loaded loaded)) {
            return Optional.empty();
        }
        ApiTypeGenerator typeGenerator = ctx.projectCtx.apiTypeGenerators.get(apiKit.name());
        return ApiKitFlowName.parse(flow.name()).flatMap(flowName -> loaded.spec()
                .findOperation(flowName.method(), flowName.path(), Optional.empty())
                .flatMap(operation -> SpecResourceSignature.of(operation, loaded.spec().types(),
                                flowName.mediaType(), typeGenerator, reservedNames)
                        .map(signature -> new SpecResource(signature, ctx.checkResponses
                                ? SuccessResponse.of(operation, typeGenerator) : List.of()))));
    }

    /**
     * @param signature        the resource's signature, from its spec operation
     * @param successResponses the success responses the resource checks, none unless responses are checked
     */
    private record SpecResource(SpecResourceSignature signature, List<SuccessResponse> successResponses) {
    }

    // Mule answers with the listener's statusCode, or 200 without one, so that status picks the spec response the
    // payload is checked against; a status the spec declares no JSON body for is answered unchecked, as Mule does
    private static Optional<CheckedResponse> checkedResponse(Context ctx, Optional<HttpResponse> httpResponse,
                                                             List<SuccessResponse> successResponses) {
        if (successResponses.isEmpty()) {
            return Optional.empty();
        }
        List<Statement> stmts = new ArrayList<>();
        Optional<String> statusCodeExpr = Optional.empty();
        Optional<String> statusCode = httpResponse.flatMap(HttpResponse::statusCode);
        if (statusCode.isPresent()) {
            try {
                statusCodeExpr = Optional.of(convertHttpResponseStatusCodeExpr(ctx, statusCode.get()));
            } catch (ScriptConversionException e) {
                stmts.add(new Statement.Comment("TODO: failed to convert " + e.getMelExpression()));
            }
        }
        boolean answersAnyStatus = statusCodeExpr.isPresent();
        List<SuccessResponse> checked = successResponses.stream()
                .filter(response -> answersAnyStatus || response.status() == 200)
                .toList();
        if (checked.isEmpty()) {
            return Optional.empty();
        }

        httpResponse.flatMap(HttpResponse::body)
                .ifPresent(bodyScript -> stmts.addAll(convertHttpResponseBody(ctx, bodyScript)));
        Optional<String> headersVar = Optional.empty();
        if (httpResponse.isPresent() && httpResponse.get().headers().isPresent()) {
            HttpResponseHeaders headers = convertHttpResponseHeaderMap(ctx, httpResponse.get().headers().get());
            stmts.addAll(headers.statements());
            headersVar = headers.mapVar();
        }
        statusCodeExpr.ifPresent(expr -> stmts.add(stmtFrom("\n\n// http response status code\nint %s = %s;"
                .formatted(STATUS_CODE_VAR, expr))));

        String headersField = headersVar.map(var -> "headers: %s, ".formatted(var)).orElse("");
        String checkedPayload = "check %s(%s.payload)".formatted(Constants.FUNC_JSON_PAYLOAD,
                Constants.CONTEXT_REFERENCE);
        stmts.add(stmtFrom(answersAnyStatus
                ? "\n\n// The payload is checked against the body the API spec declares for the status; other "
                + "statuses send it unchecked\n"
                : "\n\n// The payload is checked against the body the API spec declares for the response\n"));
        for (SuccessResponse response : checked) {
            String returnStmt = "return <%s>{%sbody: %s};".formatted(response.type(), headersField,
                    response.hasRecordBody() ? "check constraint:validate(%s)".formatted(checkedPayload)
                            : checkedPayload);
            stmts.add(stmtFrom(answersAnyStatus
                    ? "if %s == %d { %s }".formatted(STATUS_CODE_VAR, response.status(), returnStmt) : returnStmt));
        }
        if (answersAnyStatus) {
            stmts.add(stmtFrom("%s %s = %s;".formatted(HTTP_RESPONSE_TYPE, Constants.HTTP_RESPONSE_REF,
                    RESPONSE_EXPR)));
            stmts.add(stmtFrom("%s.setPayload(%s.payload);".formatted(Constants.HTTP_RESPONSE_REF,
                    Constants.CONTEXT_REFERENCE)));
            headersVar.ifPresent(var -> stmts.add(setHttpResponseHeaders(var, Constants.HTTP_RESPONSE_REF)));
            stmts.add(stmtFrom("%s.statusCode = %s;".formatted(Constants.HTTP_RESPONSE_REF, STATUS_CODE_VAR)));
            stmts.add(stmtFrom("return %s;".formatted(Constants.HTTP_RESPONSE_REF)));
        }

        if (checked.stream().anyMatch(SuccessResponse::hasRecordBody)) {
            ctx.addImport(new Import(Constants.ORG_BALLERINA, "constraint"));
        }
        addJsonPayloadFunction(ctx);
        return Optional.of(new CheckedResponse(stmts, checked.stream().map(SuccessResponse::type)
                .collect(Collectors.joining("|")) + (answersAnyStatus ? "|" + HTTP_RESPONSE_TYPE : "") + "|error"));
    }

    private static void addJsonPayloadFunction(Context ctx) {
        if (ctx.projectCtx.functionExists(Constants.FUNC_JSON_PAYLOAD)) {
            return;
        }
        ctx.currentFileCtx.balConstructs.commonFunctions.put(Constants.FUNC_JSON_PAYLOAD,
                Function.publicFunction(Constants.FUNC_JSON_PAYLOAD,
                        List.of(new Parameter(Constants.PAYLOAD_REF, BAL_ANYDATA_TYPE)), typeFrom("json|error"),
                        List.of(new Statement.Comment(
                                        "A flow may set its payload as JSON text, such as the value of a set-payload"),
                                stmtFrom("return payload is string ? payload.fromJsonString() : payload.toJson();"))));
    }

    /**
     * @param statements statements answering with the checked response
     * @param returnType return type of the resource
     */
    private record CheckedResponse(List<Statement> statements, String returnType) {
    }

    private static void genAnypointMqSource(Context ctx, Flow flow, AnypointMqSubscriber mqSubscriber,
                                            Collection<Service> services, List<Listener> listeners) {
        ctx.inServiceGen = true;
        ctx.projectCtx.attributes.put(Constants.URI_PARAMS_REF, "map<string>");
        ctx.projectCtx.attributes.put(JMS_MESSAGE_REF, Constants.JMS_MESSAGE_TYPE);

        // Add JMS import
        ctx.currentFileCtx.balConstructs.imports.add(new Import(Constants.ORG_BALLERINAX, Constants.MODULE_JMS));

        // Add configurable variable for JMS provider URL (null value generates "?")
        String jmsProviderUrlVar = "JMS_PROVIDER_URL";
        ConversionUtils.addConfigVarEntry(ctx, jmsProviderUrlVar, null);

        String serviceName = "\"" + escapeIdentifier(mqSubscriber.configRef()) + "\"";

        String listenerName = convertToBalIdentifier(mqSubscriber.configRef());
        // Listener name from config-ref
        VariableReference jmsListenerConfig =
                getJmsConnectionConfig(ctx, new VariableReference(jmsProviderUrlVar), mqSubscriber.configRef());

        // Create JMS Listener using the BallerinaModel
        Listener.JMSListener jmsListener =
                new Listener.JMSListener(listenerName, () -> jmsListenerConfig,
                        mqSubscriber.destination());
        listeners.add(jmsListener);

        Parameter message = new Parameter("message", typeFrom(Constants.JMS_MESSAGE_TYPE));
        Parameter caller = new Parameter("caller", typeFrom(Constants.JMS_CALLER_TYPE));
        ctx.jmsCaller = caller.ref();
        // Convert flow blocks to statements
        List<Statement> bodyStmts = new ArrayList<>();
        String attributesInitValue = "{ %s: %s }".formatted(JMS_MESSAGE_REF, message.ref());
        bodyStmts.add(stmtFrom("Context %s = {%s: %s};".formatted(Constants.CONTEXT_REFERENCE,
                Constants.ATTRIBUTES_REF, attributesInitValue)));
        bodyStmts.addAll(convertTopLevelMuleBlocks(ctx, flow.flowBlocks()));

        List<Parameter> params = List.of(message, caller);

        Function onMessageFunction =
                new Function("onMessage", params, TypeDesc.UnionTypeDesc.of(TypeDesc.BuiltinType.ERROR,
                        TypeDesc.BuiltinType.NIL), bodyStmts);
        Remote remoteFunction = new Remote(onMessageFunction);

        // Create service with listener reference
        Service service = new Service(serviceName, List.of(listenerName), ctx.getServiceInitFunction(),
                List.of(), List.of(), new ArrayList<>(ctx.serviceFields), List.of(remoteFunction),
                Optional.of(new Statement.Comment(
                        "TODO: placeholder jms listener for %s".formatted(mqSubscriber.configRef()))));
        services.add(service);
        ctx.resetServiceState();
    }

    public static @NotNull VariableReference getJmsConnectionConfig(Context ctx,
                                                                    VariableReference providerUrl,
                                                                    String mqConfigRef) {
        return ctx.projectCtx.jmsConnectionConfig.computeIfAbsent(mqConfigRef, (configRef) -> {
            String jmsListenerConfigName = ConversionUtils.convertToBalIdentifier(configRef) + "Config";
            ctx.currentFileCtx.balConstructs.moduleVars.put(jmsListenerConfigName,
                    new ModuleVar(jmsListenerConfigName,
                            typeFrom(JMS_CONNECTION_CONFIGURATION_TYPE),
                            new Expression.MappingConstructor(List.of(
                                    new Expression.MappingConstructor.MappingField("initialContextFactory",
                                            new Expression.StringConstant(
                                                    "org.apache.activemq.jndi.ActiveMQInitialContextFactory")),
                                    new Expression.MappingConstructor.MappingField("providerUrl",
                                            providerUrl)
                            ))));
            return new VariableReference(jmsListenerConfigName);
        });
    }

    private static void genPubSubSource(Context ctx, Flow flow, PubSubMessageListener pubSubListener,
                                        Collection<Service> services, List<Listener> listeners) {
        ctx.projectCtx.attributes.put(Constants.URI_PARAMS_REF, "map<string>");
        ctx.inServiceGen = true;

        // Add Pub/Sub import
        ctx.addImport(new Import(Constants.ORG_BALLERINAX, Constants.MODULE_PUBSUB));

        // Add configurable variables (null value generates "?")
        String projectIdVar = "projectId";
        String credentialsPathVar = "credentialsPath";
        String subscriptionNameVar = "subscriptionName";
        ConversionUtils.addConfigVarEntry(ctx, projectIdVar, null);
        ConversionUtils.addConfigVarEntry(ctx, credentialsPathVar, null);
        String subscriptionName = null;
        if (pubSubListener.subscriptionName() != null && !pubSubListener.subscriptionName().isBlank()) {
            subscriptionName = pubSubListener.subscriptionName();
        }
        ConversionUtils.addConfigVarEntry(ctx, subscriptionNameVar, subscriptionName);

        String listenerName = escapeIdentifier(pubSubListener.configRef());

        // Create Pub/Sub Listener using the BallerinaModel
        Listener.PubSubListener pubSubListenerBal = new Listener.PubSubListener(
                listenerName,
                new VariableReference(subscriptionNameVar),
                new VariableReference(projectIdVar),
                new VariableReference(credentialsPathVar));
        listeners.add(pubSubListenerBal);

        // Convert flow blocks to statements
        List<Statement> bodyStmts = new ArrayList<>();
        String attributesInitValue = "{}";
        bodyStmts.add(stmtFrom("Context %s = {%s: %s};".formatted(Constants.CONTEXT_REFERENCE,
                Constants.ATTRIBUTES_REF, attributesInitValue)));
        bodyStmts.addAll(convertTopLevelMuleBlocks(ctx, flow.flowBlocks()));

        // Create remote function for onMessage with two parameters
        List<Parameter> params = new ArrayList<>();
        params.add(new Parameter("message", typeFrom(Constants.PUBSUB_MESSAGE_TYPE)));
        params.add(new Parameter("caller", typeFrom(Constants.PUBSUB_CALLER_TYPE)));

        Function onMessageFunction = new Function("onMessage", params, bodyStmts);
        Remote remoteFunction = new Remote(onMessageFunction);

        // Create service with listener reference
        Service service = new Service("", List.of(listenerName), ctx.getServiceInitFunction(),
                List.of(), List.of(), new ArrayList<>(ctx.serviceFields), List.of(remoteFunction),
                Optional.of(new Statement.Comment(
                        "TODO: placeholder listener for %s".formatted(pubSubListener.configRef()))));
        ctx.resetServiceState();
        services.add(service);
    }

    private static void genFileListenerSource(Context ctx, Flow flow, FileListener fileListener,
                                              Collection<Service> services, List<Listener> listeners) {
        ctx.inServiceGen = true;
        ctx.projectCtx.attributes.put(Constants.URI_PARAMS_REF, "map<string>");

        // Add file and regex imports
        ctx.addImport(new Import(Constants.ORG_BALLERINA, Constants.MODULE_FILE));

        // Get file config to retrieve workingDir
        FileConfig fileConfig = ctx.projectCtx.getFileConfig(fileListener.configRef());
        if (fileConfig == null) {
            ctx.logger.logSevere("failed to find the file config: " + fileListener.configRef());
        }

        String workingDirVar = convertToBalIdentifier(fileListener.configRef() + "WorkingDir");
        ConversionUtils.addConfigVarEntry(ctx, workingDirVar, fileConfig != null ? fileConfig.workingDir() : null);

        String listenerName = convertToBalIdentifier(fileListener.configRef());

        // Create File Listener using the BallerinaModel.FileListener
        Listener.FileListener fileListenerBal = new Listener.FileListener(
                listenerName,
                new VariableReference(workingDirVar),
                false);
        listeners.add(fileListenerBal);

        String flowFuncName = createFlowFunction(ctx, flow);

        List<String> todoComments = fileListenerTodoComments(fileListener);

        // Create service body with onCreate, onDelete, onModify remote functions
        List<Remote> remoteFunctions = new ArrayList<>();

        // Check if we need regex matching
        boolean hasFilenamePattern = fileListener.matcher() != null
                && fileListener.matcher().filenamePattern() != null
                && !fileListener.matcher().filenamePattern().isEmpty();

        if (hasFilenamePattern) {
            ctx.addImport(new Import(Constants.ORG_BALLERINA, Constants.MODULE_REGEX));
        }

        // Generate each remote function (onCreate, onModify)
        for (String eventType : new String[]{"onCreate", "onModify"}) {
            List<Statement> remoteBody = new ArrayList<>();
            remoteBody.add(stmtFrom("Context %s = { %s : {}};".formatted(
                    Constants.CONTEXT_REFERENCE, Constants.ATTRIBUTES_REF)));

            Parameter event = new Parameter("event", typeFrom(Constants.FILE_EVENT_TYPE));
            Statement.CallStatement flowCall = new Statement.CallStatement(
                    new Expression.FunctionCall(flowFuncName,
                            List.of(new VariableReference(Constants.CONTEXT_REFERENCE))));
            if (hasFilenamePattern) {
                String pattern = getFileNamePattern(fileListener);
                remoteBody.add(Statement.IfElseStatement.ifStatement(
                        new Expression.FunctionCall("regex:matches",
                                List.of(new Expression.FieldAccess(event.ref(), "name"),
                                        new Expression.StringConstant(pattern))),
                        List.of(flowCall)));
            } else {
                remoteBody.add(flowCall);
            }

            Function remoteFunc = new Function(eventType, List.of(event), remoteBody);
            remoteFunctions.add(new Remote(remoteFunc));
        }

        Optional<Statement.Comment> serviceComment = todoComments.isEmpty()
                ? Optional.empty()
                : Optional.of(new Statement.Comment(String.join("\n", todoComments)));

        // Create service with listener reference
        Service service = new Service("", List.of(listenerName), ctx.getServiceInitFunction(),
                List.of(), List.of(), new ArrayList<>(ctx.serviceFields), remoteFunctions, serviceComment);
        ctx.resetServiceState();
        services.add(service);
    }

    private static String getFileNamePattern(FileListener fileListener) {
        return ConversionUtils.convertGlobToRegex(fileListener.matcher().filenamePattern());
    }

    private static @NotNull List<String> fileListenerTodoComments(FileListener fileListener) {
        List<String> todoComments = new ArrayList<>();
        if (fileListener.schedulingStrategy() != null) {
            todoComments.add(String.format("TODO: scheduling-strategy not supported (frequency: %s, timeUnit: %s)",
                    fileListener.schedulingStrategy().frequency(),
                    fileListener.schedulingStrategy().timeUnit()));
        }
        if (fileListener.autoDelete() != null && !fileListener.autoDelete().isEmpty()) {
            todoComments.add("TODO: autoDelete attribute not supported: " + fileListener.autoDelete());
        }
        if (fileListener.outputMimeType() != null && !fileListener.outputMimeType().isEmpty()) {
            todoComments.add("TODO: outputMimeType attribute not supported: " + fileListener.outputMimeType());
        }
        if (fileListener.directory() != null && !fileListener.directory().isEmpty()) {
            todoComments.add("TODO: directory attribute not supported: " + fileListener.directory());
        }
        return todoComments;
    }

    private static @NotNull String createFlowFunction(Context ctx, Flow flow) {
        String flowFuncName = convertToBalIdentifier(flow.name());
        List<Statement> flowBody = convertTopLevelMuleBlocks(ctx, flow.flowBlocks());
        Function flowFunction = Function.publicFunction(flowFuncName, Constants.FUNC_PARAMS_WITH_CONTEXT, flowBody);
        ctx.currentFileCtx.balConstructs.functions.add(flowFunction);
        return flowFuncName;
    }

    private static String concatenatePaths(String basePath, String resourcePath) {
        // Normalize paths by removing trailing slashes from basePath and leading slashes from resourcePath
        String normalizedBasePath = basePath.endsWith("/") ? basePath.substring(0, basePath.length() - 1) : basePath;
        String normalizedResourcePath = resourcePath.startsWith("/") ? resourcePath : "/" + resourcePath;

        // Handle root basePath case
        if (normalizedBasePath.equals("/") || normalizedBasePath.isEmpty()) {
            return normalizedResourcePath;
        }

        String normaizedPath = normalizedBasePath + normalizedResourcePath;
        while (normaizedPath.startsWith("/")) {
            normaizedPath = normaizedPath.substring(1);
        }

        return normaizedPath;
    }

    private static List<Service> normalizeServices(List<Service> services) {
        Map<String, Service> serviceMap = new LinkedHashMap<>();

        for (Service service : services) {
            String key = service.basePath() + "|" + service.listenerRefs();
            serviceMap.merge(key, service, (existing, current) -> {
                existing.resources().addAll(current.resources());
                existing.httpInterceptors().addAll(current.httpInterceptors());
                Optional<String> apiKitRouterRef = existing.apiKitRouterRef().or(current::apiKitRouterRef);
                Optional<Statement.Comment> comment = existing.comment().or(current::comment);
                if (apiKitRouterRef.equals(existing.apiKitRouterRef()) && comment.equals(existing.comment())) {
                    return existing;
                }
                return new Service(existing.basePath(), existing.listenerRefs(), existing.initFunc(),
                        existing.resources(), existing.functions(), existing.fields(), existing.remoteFunctions(),
                        apiKitRouterRef, existing.httpInterceptors(), comment);
            });
        }

        return new ArrayList<>(serviceMap.values());
    }

    private static void genVMListenerSource(Context ctx, Flow flow, VMListener vmListener, Set<Function> functions) {
        // TODO: Consider config ref usage
        String queueName = vmListener.queueName();
        String funcName = ctx.projectCtx.vmQueueNameToBalFuncMap.get(queueName);
        if (funcName == null) {
            funcName = convertToBalIdentifier(flow.name());
            ctx.projectCtx.vmQueueNameToBalFuncMap.put(queueName, funcName);
        }
        genBalFunc(ctx, functions, funcName, flow.flowBlocks());
    }

    private static void genSchedulerSource(Context ctx, Flow flow, Scheduler scheduler, Set<Function> functions,
                                           List<ClassDef> classDefs) {
        String jobClassName = "Job";
        ClassDef classDef = genBalJobClass(ctx, jobClassName, flow.flowBlocks());
        classDefs.add(classDef);

        List<Statement> stmts = new ArrayList<>(2);
        if (!scheduler.startDelay().isEmpty()) {
            ctx.currentFileCtx.balConstructs.imports.add(new Import(Constants.ORG_BALLERINA, Constants.MODULE_RUNTIME));
            double startDelayInSeconds = convertToSeconds(scheduler.startDelay(), scheduler.timeUnit());
            BallerinaStatement stmt = stmtFrom("runtime:sleep(%s);".formatted(startDelayInSeconds));
            stmts.add(stmt);
        }

        // Convert time unit to seconds for Ballerina's task:IntervalTimer
        double intervalInSeconds = convertToSeconds(scheduler.frequency(), scheduler.timeUnit());
        BallerinaStatement stmt = stmtFrom("task:JobId id = check task:scheduleJobRecurByFrequency(new %s(), %s);"
                .formatted(jobClassName, intervalInSeconds));
        stmts.add(stmt);
        Function mainFunc = Function.publicFunction("main", List.of(), typeFrom("error?"), stmts);
        functions.add(mainFunc);
    }

    private static double convertToSeconds(String frequency, String timeUnit) {
        double freq = Double.parseDouble(frequency);
        return switch (timeUnit.toLowerCase()) {
            case "milliseconds", "millis" -> freq / 1000.0;
            case "seconds", "s" -> freq;
            case "minutes", "mins", "m" -> freq * 60.0;
            case "hours", "h" -> freq * 3600.0;
            case "days", "d" -> freq * 86400.0;
            default -> freq; // Default to seconds if unknown
        };
    }

    private static ClassDef genBalJobClass(Context ctx, String jobName, List<MuleRecord> flowBlocks) {
        List<Statement> bodyCoreStmts = convertTopLevelMuleBlocks(ctx, flowBlocks);
        Function executeFunc = Function.publicFunction("execute", List.of(), bodyCoreStmts);
        return new ClassDef(jobName, List.of(typeFrom("task:Job")), List.of(), List.of(executeFunc));
    }

    private static Service genHttpSource(Context ctx, Flow flow, HttpListener src, List<Service> services,
                                         Set<Function> functions)
            throws ScriptConversionException {
        ctx.projectCtx.attributes.put(Constants.HTTP_REQUEST_REF, Constants.HTTP_REQUEST_TYPE);
        ctx.projectCtx.attributes.put(Constants.HTTP_RESPONSE_REF, HTTP_RESPONSE_TYPE);
        ctx.projectCtx.attributes.put(Constants.URI_PARAMS_REF, "map<string>");

        HttpFlowSegments segments = splitHttpFlow(flow.flowBlocks());
        Service service = genBalService(ctx, src, segments.serviceBlocks(), functions, policyTodo(ctx, flow));
        if (!segments.requestInterceptorBlocks().isEmpty()) {
            service.httpInterceptors().add(genRequestInterceptor(ctx, segments.requestInterceptorBlocks()));
        }
        if (src.errorResponse().isPresent() || segments.errorHandler().isPresent()) {
            service.httpInterceptors().add(genResponseErrorInterceptor(ctx, segments.errorHandler(),
                    src.errorResponse()));
        }
        if (!segments.responseInterceptorBlocks().isEmpty()) {
            service.httpInterceptors().add(genResponseInterceptor(ctx, segments.responseInterceptorBlocks()));
        }
        segments.serviceBlocks().stream()
                .filter(ApiKitRouter.class::isInstance)
                .map(router -> ((ApiKitRouter) router).configRef())
                .forEach(configRef -> src.response()
                        .ifPresent(response -> ctx.projectCtx.apiKitRouterResponses.put(configRef, response)));
        services.add(service);
        return service;
    }

    // API Manager enforced these policies in front of the Mule flow, so no migrated code enforces them
    private static Optional<Statement.Comment> policyTodo(Context ctx, Flow flow) {
        List<String> policies = new ArrayList<>();
        ctx.autodiscoveryApiId(flow.name()).ifPresent(apiId -> policies.add(ApiPolicies.autodiscovery(apiId)));
        flow.flowBlocks().stream()
                .filter(ApiKitRouter.class::isInstance)
                .map(router -> ctx.projectCtx.apiSpecs.get(((ApiKitRouter) router).configRef()))
                .filter(ApiSpecResult.Loaded.class::isInstance)
                .forEach(loaded -> policies.addAll(ApiPolicies.describe(((ApiSpecResult.Loaded) loaded).spec())));
        if (policies.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new Statement.Comment("TODO: In Mule, API Manager enforced policies on this API, and this "
                + "service does not enforce them:\n"
                + policies.stream().map(policy -> "  - " + policy + "\n").collect(Collectors.joining())
                + "Put an API gateway in front of this service, or add the checks, for example in an "
                + "http:RequestInterceptor."));
    }

    private static HttpFlowSegments splitHttpFlow(List<MuleRecord> flowBlocks) {
        // A flow declares at most one error handler and it is always the last block of the flow.
        boolean hasErrorHandler = !flowBlocks.isEmpty() && flowBlocks.getLast() instanceof ErrorHandler;
        List<MuleRecord> blocks = hasErrorHandler
                ? flowBlocks.subList(0, flowBlocks.size() - 1) : flowBlocks;

        int routerIndex = -1;
        for (int i = 0; i < blocks.size(); i++) {
            if (blocks.get(i) instanceof ApiKitRouter) {
                routerIndex = i;
                break;
            }
        }
        if (routerIndex < 0) {
            return new HttpFlowSegments(List.of(), flowBlocks, List.of(), Optional.empty());
        }

        return new HttpFlowSegments(
                List.copyOf(blocks.subList(0, routerIndex)),
                List.of(blocks.get(routerIndex)),
                List.copyOf(blocks.subList(routerIndex + 1, blocks.size())),
                hasErrorHandler ? Optional.of((ErrorHandler) flowBlocks.getLast()) : Optional.empty());
    }

    private static HTTPInterceptor genRequestInterceptor(Context ctx, List<MuleRecord> flowBlocks) {
        ctx.inServiceGen = true;
        List<Statement> body = new ArrayList<>();
        body.add(stmtFrom("Context %s = {%s: {%s: %s, %s: new}};".formatted(
                Constants.CONTEXT_REFERENCE, Constants.ATTRIBUTES_REF, Constants.HTTP_REQUEST_REF,
                Constants.HTTP_REQUEST_REF, Constants.HTTP_RESPONSE_REF)));
        body.addAll(convertTopLevelMuleBlocks(ctx, flowBlocks));
        body.add(stmtFrom("return requestContext.next();"));

        List<Parameter> parameters = List.of(
                new Parameter("requestContext", typeFrom("http:RequestContext")),
                new Parameter(Constants.HTTP_REQUEST_REF, typeFrom(Constants.HTTP_REQUEST_TYPE)));
        Resource resource = new Resource("'default", "[string... path]", parameters,
                Optional.of(typeFrom("http:NextService|error?")), body);
        String className = "MuleRequestInterceptor" + ctx.projectCtx.counters.requestInterceptorCount++;
        Optional<Function> initFunction = ctx.initFunctionBody.isEmpty()
                ? Optional.empty() : ctx.getServiceInitFunction();
        HTTPInterceptor interceptor = new HTTPInterceptor.RequestInterceptor(className,
                new ArrayList<>(ctx.serviceFields), initFunction, resource);
        ctx.resetServiceState();
        return interceptor;
    }

    // Runs the blocks after an APIkit router; the listener's http:response is applied by the resources instead
    private static HTTPInterceptor genResponseInterceptor(Context ctx, List<MuleRecord> flowBlocks) {
        ctx.inServiceGen = true;
        List<Statement> body = new ArrayList<>();
        body.add(stmtFrom("Context %s = {%s: {%s: %s}};".formatted(Constants.CONTEXT_REFERENCE,
                Constants.ATTRIBUTES_REF, Constants.HTTP_RESPONSE_REF, Constants.HTTP_RESPONSE_REF)));
        body.addAll(convertTopLevelMuleBlocks(ctx, flowBlocks));
        body.add(setPayloadIfSet());
        body.add(stmtFrom("return %s;".formatted(Constants.HTTP_RESPONSE_REF)));

        List<Parameter> parameters = List.of(
                new Parameter("requestContext", typeFrom("http:RequestContext")),
                new Parameter(Constants.HTTP_RESPONSE_REF, typeFrom(HTTP_RESPONSE_TYPE)));
        Function function = new Function("interceptResponse", parameters, typeFrom("http:Response|error"), body);
        String className = "MuleResponseInterceptor" + ctx.projectCtx.counters.responseInterceptorCount++;
        Optional<Function> initFunction = ctx.initFunctionBody.isEmpty()
                ? Optional.empty() : ctx.getServiceInitFunction();
        HTTPInterceptor interceptor = new HTTPInterceptor.ResponseInterceptor(className,
                new ArrayList<>(ctx.serviceFields), initFunction, new Remote(function));
        ctx.resetServiceState();
        return interceptor;
    }

    // An interceptor starts from an empty Context, so it replaces the response body only when one of its blocks set
    // a payload; replacing a body with nil makes the response never complete
    private static Statement setPayloadIfSet() {
        return stmtFrom("if %s.payload !is () { %s.setPayload(%s.payload); }".formatted(Constants.CONTEXT_REFERENCE,
                RESPONSE_REF_EXPR, Constants.CONTEXT_REFERENCE));
    }

    private static HTTPInterceptor genResponseErrorInterceptor(Context ctx, Optional<ErrorHandler> errorHandler,
                                                               Optional<HttpResponse> httpErrorResponse) {
        ctx.inServiceGen = true;
        String interceptedResponse = "interceptedResponse";
        List<Statement> body = new ArrayList<>();
        body.add(stmtFrom("Context %s = {%s: {%s: %s}};".formatted(Constants.CONTEXT_REFERENCE,
                Constants.ATTRIBUTES_REF, Constants.HTTP_RESPONSE_REF, interceptedResponse)));
        errorHandler.ifPresent(handler -> body.addAll(convertErrorHandler(ctx, handler)));
        httpErrorResponse.flatMap(HttpResponse::body)
                .ifPresent(bodyScript -> body.addAll(convertHttpResponseBody(ctx, bodyScript)));
        body.add(setPayloadIfSet());
        httpErrorResponse.ifPresent(response -> {
            body.addAll(convertHttpResponseHeaders(ctx, response.headers(), interceptedResponse));
            body.addAll(convertHttpResponseStatusCode(ctx, response.statusCode(), interceptedResponse));
        });
        body.add(stmtFrom("return <%s>%s.%s;".formatted(HTTP_RESPONSE_TYPE,
                Constants.ATTRIBUTES_FIELD_ACCESS, Constants.HTTP_RESPONSE_REF)));

        List<Parameter> parameters = List.of(
                new Parameter("requestContext", typeFrom("http:RequestContext")),
                new Parameter(interceptedResponse, typeFrom(HTTP_RESPONSE_TYPE)),
                new Parameter(Constants.ON_FAIL_ERROR_VAR_REF, typeFrom("error")));
        Function function = new Function("interceptResponseError", parameters,
                typeFrom("http:Response|error"), body);
        String className = "MuleResponseErrorInterceptor"
                + ctx.projectCtx.counters.responseErrorInterceptorCount++;
        Optional<Function> initFunction = ctx.initFunctionBody.isEmpty()
                ? Optional.empty() : ctx.getServiceInitFunction();
        HTTPInterceptor interceptor = new HTTPInterceptor.ResponseErrorInterceptor(className,
                new ArrayList<>(ctx.serviceFields), initFunction, new Remote(function));
        ctx.resetServiceState();
        return interceptor;
    }

    private static List<Statement> convertErrorHandler(Context ctx, ErrorHandler errorHandler) {
        if (errorHandler.ref().isEmpty()) {
            return convertErrorHandlerRecords(ctx, errorHandler.errorHandlers());
        }
        String functionName = convertToBalIdentifier(errorHandler.ref());
        return List.of(stmtFrom("%s(%s, %s);".formatted(functionName,
                Constants.CONTEXT_REFERENCE, Constants.ON_FAIL_ERROR_VAR_REF)));
    }

    private record HttpFlowSegments(List<MuleRecord> requestInterceptorBlocks, List<MuleRecord> serviceBlocks,
                                    List<MuleRecord> responseInterceptorBlocks, Optional<ErrorHandler> errorHandler) {
    }

    public static List<ModuleTypeDef> createContextTypeDefns(Context ctx) {
        List<ModuleTypeDef> contextTypeDefns = new ArrayList<>();

        List<RecordField> contextRecFields = new ArrayList<>();
        contextRecFields.add(new RecordField(Constants.PAYLOAD_REF, BAL_ANYDATA_TYPE, exprFrom("()")));

        if (!ctx.projectCtx.vars.isEmpty()) {
            contextRecFields.add(new RecordField(Constants.VARS_REF, typeFrom(Constants.VARS_TYPE),
                    exprFrom("{}")));
            List<RecordField> varRecFields = new ArrayList<>();
            for (Map.Entry<String, String> entry : ctx.projectCtx.vars.entrySet()) {
                varRecFields.add(new RecordField(entry.getKey(), typeFrom(entry.getValue()), true));
            }

            RecordTypeDesc varsRecord = RecordTypeDesc.closedRecord(varRecFields);
            contextTypeDefns.add(new ModuleTypeDef(Constants.VARS_TYPE, varsRecord));
        }

        if (!ctx.projectCtx.attributes.isEmpty()) {
            contextRecFields.add(new RecordField(Constants.ATTRIBUTES_REF, typeFrom(Constants.ATTRIBUTES_TYPE),
                    false));
            List<RecordField> attributesRecordFields = new ArrayList<>();
            for (Map.Entry<String, String> entry : ctx.projectCtx.attributes.entrySet()) {
                String name = entry.getKey();
                String type = entry.getValue();
                RecordField attributesField;
                if (type.startsWith("map<") && type.endsWith(">")) {
                    attributesField = new RecordField(name, typeFrom(type), exprFrom("{}"));
                } else {
                    attributesField = new RecordField(name, typeFrom(type), true);
                }
                attributesRecordFields.add(attributesField);
            }
            RecordTypeDesc attributesRecord = RecordTypeDesc.closedRecord(attributesRecordFields);
            contextTypeDefns.add(new ModuleTypeDef(Constants.ATTRIBUTES_TYPE, attributesRecord));
        }

        RecordTypeDesc contextRecord = RecordTypeDesc.closedRecord(contextRecFields);
        contextTypeDefns.add(new ModuleTypeDef(Constants.CONTEXT_RECORD_TYPE, contextRecord));
        // Spec types follow the context types, whose names their generators avoid; a shared generator is written once
        Set<ApiTypeGenerator> generators = Collections.newSetFromMap(new IdentityHashMap<>());
        for (ApiTypeGenerator generator : ctx.projectCtx.apiTypeGenerators.values()) {
            if (generators.add(generator)) {
                contextTypeDefns.addAll(generator.usedTypeDefinitions());
            }
        }
        return contextTypeDefns;
    }

    private static void genBalFuncForGlobalErrorHandler(Context ctx, ErrorHandler errorHandler,
                                                        Set<Function> functions) {
        String name = errorHandler.name();
        String methodName = errorHandler.name().isEmpty() ? "errorHandler" :
                convertToBalIdentifier(name); // Ideally field will not be empty

        List<Parameter> parameters = new ArrayList<>();
        parameters.add(Constants.CONTEXT_FUNC_PARAM);
        parameters.add(new Parameter(Constants.ON_FAIL_ERROR_VAR_REF, Constants.BAL_ERROR_TYPE));

        List<Statement> body = convertErrorHandlerRecords(ctx, errorHandler.errorHandlers());
        Function function = Function.publicFunction(methodName, parameters.stream().toList(), body);
        functions.add(function);
    }

    private static void genBalFuncsFromSubFlows(Context ctx, List<SubFlow> subFlows, Set<Function> functions) {
        for (SubFlow subFlow : subFlows) {
            genBalFuncForPrivateOrSubFlow(ctx, functions, subFlow.name(), subFlow.flowBlocks());
        }
    }

    private static void genBalFuncForPrivateOrSubFlow(Context ctx, Set<Function> functions, String flowName,
                                                      List<MuleRecord> flowBlocks) {
        List<Statement> body = convertTopLevelMuleBlocks(ctx, flowBlocks);
        String methodName = convertToBalIdentifier(flowName);
        Function function = Function.publicFunction(methodName, Constants.FUNC_PARAMS_WITH_CONTEXT, body);
        functions.add(function);
    }

    private static void genBalFunc(Context ctx, Set<Function> functions, String funcName, List<MuleRecord> flowBlocks) {
        List<Statement> body = convertTopLevelMuleBlocks(ctx, flowBlocks);
        Function function = Function.publicFunction(funcName, Constants.FUNC_PARAMS_WITH_CONTEXT, body);
        functions.add(function);
    }

    private static void genBalFuncsFromPrivateFlows(Context ctx, List<Flow> privateFlows, Set<Function> functions) {
        for (Flow privateFlow : privateFlows) {
            genBalFuncForPrivateOrSubFlow(ctx, functions, privateFlow.name(), privateFlow.flowBlocks());
        }
    }

    private static Service genBalService(Context ctx, HttpListener httpListener, List<MuleRecord> flowBlocks,
                                         Set<Function> functions, Optional<Statement.Comment> comment)
            throws ScriptConversionException {
        ctx.inServiceGen = true;
        Optional<String> apiKitRouterRef = flowBlocks.stream()
                .filter(ApiKitRouter.class::isInstance)
                .map(ApiKitRouter.class::cast)
                .map(ApiKitRouter::configRef)
                .findFirst();
        List<String> pathParams = new ArrayList<>();
        // Every APIkit resource path starts with the router's listener path, so it is needed as a value here
        String listenerPath = apiKitRouterRef.isPresent()
                ? ctx.resolveProjectProperty(httpListener.resourcePath()).orElse(httpListener.resourcePath())
                : httpListener.resourcePath();
        String resourcePath = getBallerinaResourcePath(ctx, listenerPath, pathParams);
        String[] resourceMethodNames = httpListener.allowedMethods();
        String listenerRef = convertToBalIdentifier(httpListener.configRef());
        HTTPListenerConfig listenerConfig = ctx.projectCtx.getHttpListenerConfig(httpListener.configRef());
        String muleBasePath = insertLeadingSlash(listenerConfig.basePath());
        String basePath = getBallerinaAbsolutePath(muleBasePath);

        // Add services
        List<Parameter> queryPrams = new ArrayList<>();
        queryPrams.add(new Parameter(Constants.HTTP_REQUEST_REF, typeFrom(Constants.HTTP_REQUEST_TYPE)));

        List<Statement> bodyStmts = new ArrayList<>();
        String attributesInitValue = getAttributesInitValue(ctx, uriParamsInit(pathParams));
        bodyStmts.add(stmtFrom("Context %s = {%s: %s};".formatted(Constants.CONTEXT_REFERENCE,
                Constants.ATTRIBUTES_REF, attributesInitValue)));

        List<Statement> bodyCoreStmts = convertTopLevelMuleBlocks(ctx, flowBlocks);
        bodyStmts.addAll(bodyCoreStmts);
        bodyStmts.addAll(responseStatements(ctx, httpListener.response()));

        // Add service resources
        List<Resource> resources = new ArrayList<>();
        TypeDesc returnType = typeFrom(Constants.HTTP_RESOURCE_RETURN_TYPE_DEFAULT);
        ctx.currentFileCtx.balConstructs.imports.add(Constants.HTTP_MODULE_IMPORT);
        if (apiKitRouterRef.isPresent()) {
            // APIKit implementation flows add the concrete resources to this service. Keep the listener
            // resource as a fallback so paths accepted by the listener, but not matched by APIKit, fail
            // through the service's response-error interceptor and Mule error handler.
            Optional<String> resourcePrefix = apiKitResourcePrefix(listenerPath);
            resourcePrefix.ifPresent(
                    prefix -> ctx.projectCtx.apiKitResourcePrefixes.put(apiKitRouterRef.get(), prefix));
            List<Statement> fallbackBody = new ArrayList<>();
            if (resourcePrefix.isEmpty()) {
                fallbackBody.add(stmtFrom(("\n// TODO: The APIkit router listener path '%s' is not a fixed path, so "
                        + "the APIkit resources of this service are generated without it as their path prefix\n")
                        .formatted(httpListener.resourcePath())));
            }
            fallbackBody.add(stmtFrom("return error %s(\"APIKIT:NOT_FOUND\");"
                    .formatted(ConversionUtils.declareErrorType(ctx, "APIKIT:NOT_FOUND"))));
            for (String resourceMethodName : resourceMethodNames) {
                resources.add(new Resource(resourceMethodName.toLowerCase(), resourcePath, queryPrams,
                        Optional.of(returnType), fallbackBody));
            }
            Map<ApiSpec.Operation, Resource> notImplemented = notImplementedResources(ctx, apiKitRouterRef.get(),
                    resourcePrefix.orElse(""));
            resources.addAll(notImplemented.values());
            if (resourcePrefix.isPresent()) {
                ctx.projectCtx.apiKitServiceAddresses.put(apiKitRouterRef.get(), new ServiceAddress(listenerRef,
                        urlPrefix(muleBasePath, listenerPath), List.copyOf(notImplemented.keySet())));
            }
        } else if (resourceMethodNames.length > 1) {
            // same logic is shared, thus extracting it to a function
            String invokeEndPointMethodName = String.format(Constants.FUNC_NAME_HTTP_ENDPOINT_TEMPLATE,
                    ctx.projectCtx.counters.invokeEndPointMethodCount++);
            List<Statement> body = Collections.singletonList(stmtFrom("return %s(%s);"
                    .formatted(invokeEndPointMethodName, Constants.HTTP_REQUEST_REF)));

            for (String resourceMethodName : resourceMethodNames) {
                resourceMethodName = resourceMethodName.toLowerCase();
                Resource resource = new Resource(resourceMethodName, resourcePath, queryPrams, Optional.of(returnType),
                        body);
                resources.add(resource);
            }

            // Add body as a top level function
            functions.add(new Function(Optional.of("public"), invokeEndPointMethodName, Collections.singletonList(
                    new Parameter(Constants.HTTP_REQUEST_REF, typeFrom(Constants.HTTP_REQUEST_TYPE))),
                    Optional.of(returnType), new BlockFunctionBody(bodyStmts)));
        } else if (resourceMethodNames.length == 1) {
            String resourceMethodName = resourceMethodNames[0].toLowerCase();
            Resource resource = new Resource(resourceMethodName, resourcePath, queryPrams, Optional.of(returnType),
                    bodyStmts);
            resources.add(resource);
        } else {
            throw new IllegalStateException();
        }

        Service service =
                new Service(basePath, List.of(listenerRef), ctx.getServiceInitFunction(), resources, List.of(),
                        new ArrayList<>(ctx.serviceFields),
                        List.of(), apiKitRouterRef, new ArrayList<>(), comment);
        ctx.resetServiceState();
        return service;
    }

    // APIkit raises NOT_IMPLEMENTED for a spec operation without a flow, after it validated the request, and the
    // flow's error handler answers it; without these resources the catch-all would raise NOT_FOUND instead
    private static Map<ApiSpec.Operation, Resource> notImplementedResources(Context ctx, String configName,
                                                                           String resourcePrefix) {
        if (!(ctx.projectCtx.apiSpecs.get(configName) instanceof ApiSpecResult.Loaded loaded)) {
            return Map.of();
        }
        Map<ApiSpec.Operation, Resource> resources = new LinkedHashMap<>();
        for (ApiSpec.Operation operation : ApiContractChecker.unimplementedOperations(loaded.spec(),
                ctx.projectCtx.apiKitFlows.getOrDefault(configName, List.of()))) {
            SpecResourceSignature.of(operation, loaded.spec().types(), Optional.empty(),
                    ctx.projectCtx.apiTypeGenerators.get(configName), Set.of()).ifPresent(signature -> resources.put(
                    operation, new Resource(operation.method(), withPrefix(resourcePrefix, signature.resourcePath()),
                            signature.parameters(), Optional.of(Constants.BAL_ERROR_TYPE),
                            List.of(stmtFrom(("\n// No flow implements this operation of the API spec\n"
                                    + "return error %s(\"APIKIT:NOT_IMPLEMENTED\");").formatted(
                                    ConversionUtils.declareErrorType(ctx, "APIKIT:NOT_IMPLEMENTED")))))));
        }
        return resources;
    }

    // The URL path of a listener path such as /api/*, behind the listener's base path
    private static String urlPrefix(String muleBasePath, String listenerPath) {
        String path = listenerPath.endsWith("/*") ? listenerPath.substring(0, listenerPath.length() - 2)
                : listenerPath;
        return (muleBasePath.equals("/") ? "" : muleBasePath.replaceFirst("/$", ""))
                + (path.isEmpty() || path.startsWith("/") ? path : "/" + path);
    }

    // A listener's http:response is applied by the resource, where the variables the flow set are in scope
    private static List<Statement> responseStatements(Context ctx, Optional<HttpResponse> httpResponse) {
        List<Statement> stmts = new ArrayList<>();
        if (httpResponse.isEmpty()) {
            stmts.add(stmtFrom("\n\n%s.setPayload(%s.payload);".formatted(RESPONSE_REF_EXPR,
                    Constants.CONTEXT_REFERENCE)));
            stmts.add(stmtFrom("return %s;".formatted(RESPONSE_EXPR)));
            return stmts;
        }
        HttpResponse response = httpResponse.get();
        response.body().ifPresent(bodyScript -> stmts.addAll(convertHttpResponseBody(ctx, bodyScript)));
        stmts.add(stmtFrom("\n\n%s %s = %s;".formatted(HTTP_RESPONSE_TYPE, Constants.HTTP_RESPONSE_REF,
                RESPONSE_EXPR)));
        stmts.add(stmtFrom("%s.setPayload(%s.payload);".formatted(Constants.HTTP_RESPONSE_REF,
                Constants.CONTEXT_REFERENCE)));
        stmts.addAll(convertHttpResponseHeaders(ctx, response.headers(), Constants.HTTP_RESPONSE_REF));
        stmts.addAll(convertHttpResponseStatusCode(ctx, response.statusCode(), Constants.HTTP_RESPONSE_REF));
        stmts.add(stmtFrom("return %s;".formatted(Constants.HTTP_RESPONSE_REF)));
        return stmts;
    }

    private static Optional<String> apiKitResourcePrefix(String listenerPath) {
        List<String> segments = Stream.of(listenerPath.split("/")).filter(segment -> !segment.isEmpty()).toList();
        if (!segments.isEmpty() && segments.getLast().equals("*")) {
            segments = segments.subList(0, segments.size() - 1);
        }
        if (segments.stream().anyMatch(segment -> segment.contains("*") || segment.contains("{")
                || segment.contains("$") || segment.contains("#["))) {
            return Optional.empty();
        }
        return Optional.of(segments.stream().map(ConversionUtils::convertToBalIdentifier)
                .collect(Collectors.joining("/")));
    }

    private static Optional<String> uriParamsInit(List<String> pathParams) {
        return pathParams.isEmpty() ? Optional.empty() : Optional.of("{%s}".formatted(String.join(",", pathParams)));
    }

    private static String getAttributesInitValue(Context ctx, Optional<String> uriParamsInit) {
        Map<String, String> attributesPropMap = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : ctx.projectCtx.attributes.entrySet()) {
            switch (entry.getKey()) {
                case Constants.HTTP_REQUEST_REF ->
                        attributesPropMap.put(Constants.HTTP_REQUEST_REF, Constants.HTTP_REQUEST_REF);
                case Constants.HTTP_RESPONSE_REF -> attributesPropMap.put(Constants.HTTP_RESPONSE_REF, "new");
                case Constants.URI_PARAMS_REF -> uriParamsInit.ifPresent(
                        init -> attributesPropMap.put(Constants.URI_PARAMS_REF, init));
                case JMS_MESSAGE_REF -> attributesPropMap.put(JMS_MESSAGE_REF, JMS_MESSAGE_REF);
                default -> throw new IllegalStateException();
            }
        }

        List<String> fields = new ArrayList<>();
        attributesPropMap.forEach((key, value) -> {
            if (key.equals(value)) {
                fields.add(key);
            } else {
                fields.add("%s: %s".formatted(key, value));
            }
        });

        String recordBody = String.join(", ", fields);
        return String.format("{%s}", recordBody);
    }

    public static TextDocument createTextDocument(String docName, List<Import> imports,
                                                  List<ModuleTypeDef> moduleTypeDefs,
                                                  List<ModuleVar> moduleVars, List<Listener> listeners,
                                                  List<Service> services, List<ClassDef> classDefs,
                                                  List<Function> functions, List<String> comments) {
        return new TextDocument(docName, imports, moduleTypeDefs, moduleVars, listeners,
                services, classDefs, functions, comments);
    }

    private static JavaDependencies determineJdbcDependencyFromUrl(String dbUrl) {
        String url = dbUrl.trim();
        if (url.startsWith("jdbc:h2:")) {
            return JavaDependencies.JDBC_H2;
        } else if (url.startsWith("jdbc:mysql:")) {
            return JavaDependencies.JDBC_MYSQL;
        } else if (url.startsWith("jdbc:postgresql:")) {
            return JavaDependencies.JDBC_POSTGRESQL;
        } else if (url.startsWith("jdbc:oracle:")) {
            return JavaDependencies.JDBC_ORACLE;
        } else if (url.startsWith("jdbc:mariadb:")) {
            return JavaDependencies.JDBC_MARIADB;
        }
        // Default to H2 for unknown JDBC URLs
        return JavaDependencies.JDBC_H2;
    }

    public enum JavaDependencies {
        JDBC_H2("""
                [[platform.java17.dependency]]
                artifactId = "h2"
                version = "2.0.206"
                groupId = "com.h2database"
                """),
        JDBC_POSTGRESQL("""
                [[platform.java17.dependency]]
                artifactId = "postgresql"
                version = "42.7.2"
                groupId = "org.postgresql"
                """),
        JDBC_MYSQL("""
                [[platform.java17.dependency]]
                artifactId = "mysql-connector-java"
                version = "8.0.33"
                groupId = "mysql"
                """),
        JDBC_ORACLE("""
                [[platform.java17.dependency]]
                artifactId = "ojdbc8"
                version = "21.9.0.0"
                groupId = "com.oracle.database.jdbc"
                """),
        JDBC_MARIADB("""
                [[platform.java17.dependency]]
                artifactId = "mariadb-java-client"
                version = "3.1.4"
                groupId = "org.mariadb.jdbc"
                """);

        public final String dependencyParam;

        JavaDependencies(String dependencyParam) {
            this.dependencyParam = dependencyParam;
        }
    }
}
