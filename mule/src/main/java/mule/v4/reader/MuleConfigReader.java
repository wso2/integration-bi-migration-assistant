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
package mule.v4.reader;

import mule.v4.Constants;
import mule.v4.Context;
import mule.v4.ConversionUtils;
import mule.v4.model.MuleXMLTag;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static common.BallerinaModel.Import;
import static mule.v4.ConversionUtils.getAllowedMethods;
import static mule.v4.model.MuleModel.Async;
import static mule.v4.model.MuleModel.CatchExceptionStrategy;
import static mule.v4.model.MuleModel.Choice;
import static mule.v4.model.MuleModel.ChoiceExceptionStrategy;
import static mule.v4.model.MuleModel.Database;
import static mule.v4.model.MuleModel.DbInParam;
import static mule.v4.model.MuleModel.DbMSQLConfig;
import static mule.v4.model.MuleModel.DbOracleConfig;
import static mule.v4.model.MuleModel.DbTemplateQuery;
import static mule.v4.model.MuleModel.Enricher;
import static mule.v4.model.MuleModel.ExpressionComponent;
import static mule.v4.model.MuleModel.Flow;
import static mule.v4.model.MuleModel.FlowReference;
import static mule.v4.model.MuleModel.HTTPListenerConfig;
import static mule.v4.model.MuleModel.HTTPRequestConfig;
import static mule.v4.model.MuleModel.HttpListener;
import static mule.v4.model.MuleModel.HttpRequest;
import static mule.v4.model.MuleModel.InputPayloadElement;
import static mule.v4.model.MuleModel.Kind;
import static mule.v4.model.MuleModel.LogLevel;
import static mule.v4.model.MuleModel.Logger;
import static mule.v4.model.MuleModel.MuleRecord;
import static mule.v4.model.MuleModel.ObjectToJson;
import static mule.v4.model.MuleModel.ObjectToString;
import static mule.v4.model.MuleModel.Payload;
import static mule.v4.model.MuleModel.QueryType;
import static mule.v4.model.MuleModel.ReferenceExceptionStrategy;
import static mule.v4.model.MuleModel.RemoveVariable;
import static mule.v4.model.MuleModel.SetPayloadElement;
import static mule.v4.model.MuleModel.SetSessionVariable;
import static mule.v4.model.MuleModel.SetSessionVariableElement;
import static mule.v4.model.MuleModel.SetVariable;
import static mule.v4.model.MuleModel.SetVariableElement;
import static mule.v4.model.MuleModel.SubFlow;
import static mule.v4.model.MuleModel.TransformMessage;
import static mule.v4.model.MuleModel.TransformMessageElement;
import static mule.v4.model.MuleModel.Type;
import static mule.v4.model.MuleModel.UnsupportedBlock;
import static mule.v4.model.MuleModel.VMInboundEndpoint;
import static mule.v4.model.MuleModel.VMOutboundEndpoint;
import static mule.v4.model.MuleModel.WhenInChoice;
import static mule.v4.model.MuleXMLTag.HTTP_LISTENER_CONNECTION;

public class MuleConfigReader {

    public static void readMuleConfigFromRoot(Context ctx, MuleXMLNavigator.MuleElement muleElement,
                                              List<Flow> flows, List<SubFlow> subFlows) {
        while (muleElement.peekChild() != null) {
            MuleXMLNavigator.MuleElement child = muleElement.consumeChild();
            Element element = child.getElement();

            String elementTagName = element.getTagName();
            if (MuleXMLTag.FLOW.tag().equals(elementTagName)) {
                Flow flow = readFlow(ctx, child);
                flows.add(flow);
                continue;
            } else if (MuleXMLTag.SUB_FLOW.tag().equals(elementTagName)) {
                SubFlow subFlow = readSubFlow(ctx, child);
                subFlows.add(subFlow);
                continue;
            }

            readGlobalConfigElement(ctx, child);
        }
    }

    public static void readGlobalConfigElement(Context ctx, MuleXMLNavigator.MuleElement muleElement) {
        String elementTagName = muleElement.getElement().getTagName();
        if (MuleXMLTag.HTTP_LISTENER_CONFIG.tag().equals(elementTagName)) {
            HTTPListenerConfig httpListenerConfig = readHttpListenerConfig(ctx, muleElement);
            ctx.currentFileCtx.configs.httpListenerConfigs.put(httpListenerConfig.name(), httpListenerConfig);
        } else if (MuleXMLTag.HTTP_REQUEST_CONFIG.tag().equals(elementTagName)) {
            HTTPRequestConfig httpRequestConfig = readHttpRequestConfig(ctx, muleElement);
            ctx.currentFileCtx.configs.httpRequestConfigs.put(httpRequestConfig.name(), httpRequestConfig);
        } else if (MuleXMLTag.DB_MYSQL_CONFIG.tag().equals(elementTagName)) {
            DbMSQLConfig dbMSQLConfig = readDbMySQLConfig(ctx, muleElement);
            ctx.currentFileCtx.configs.dbMySQLConfigs.put(dbMSQLConfig.name(), dbMSQLConfig);
        } else if (MuleXMLTag.DB_ORACLE_CONFIG.tag().equals(elementTagName)) {
            DbOracleConfig dbOracleConfig = readDbOracleConfig(ctx, muleElement);
            ctx.currentFileCtx.configs.dbOracleConfigs.put(dbOracleConfig.name(), dbOracleConfig);
        } else if (MuleXMLTag.DB_TEMPLATE_QUERY.tag().equals(elementTagName)) {
            DbTemplateQuery dbTemplateQuery = readDbTemplateQuery(ctx, muleElement);
            ctx.currentFileCtx.configs.dbTemplateQueries.put(dbTemplateQuery.name(), dbTemplateQuery);
        } else if (MuleXMLTag.CATCH_EXCEPTION_STRATEGY.tag().equals(elementTagName)) {
            CatchExceptionStrategy catchExceptionStrategy = readCatchExceptionStrategy(ctx, muleElement);
            ctx.currentFileCtx.configs.globalExceptionStrategies.add(catchExceptionStrategy);
        } else if (MuleXMLTag.CHOICE_EXCEPTION_STRATEGY.tag().equals(elementTagName)) {
            ChoiceExceptionStrategy choiceExceptionStrategy = readChoiceExceptionStrategy(ctx, muleElement);
            ctx.currentFileCtx.configs.globalExceptionStrategies.add(choiceExceptionStrategy);
        } else {
            UnsupportedBlock unsupportedBlock = readUnsupportedBlock(ctx, muleElement);
            ctx.currentFileCtx.configs.unsupportedBlocks.add(unsupportedBlock);
        }
    }

    public static MuleRecord readBlock(Context ctx, MuleXMLNavigator.MuleElement muleElement) {
        MuleXMLTag muleXMLTag = MuleXMLTag.fromTag(muleElement.getElement().getTagName());
        switch (muleXMLTag) {
            // Source
            case MuleXMLTag.HTTP_LISTENER -> {
                return readHttpListener(ctx, muleElement);
            }

            case MuleXMLTag.VM_INBOUND_ENDPOINT -> {
                return readVMInboundEndpoint(ctx, muleElement);
            }

            // Process Items
            case MuleXMLTag.LOGGER -> {
                return readLogger(ctx, muleElement);
            }
            case MuleXMLTag.EXPRESSION_COMPONENT -> {
                return readExpressionComponent(ctx, muleElement);
            }
            case MuleXMLTag.SET_VARIABLE -> {
                return readSetVariable(ctx, muleElement);
            }
            case MuleXMLTag.SET_SESSION_VARIABLE -> {
                return readSetSessionVariable(ctx, muleElement);
            }
            case MuleXMLTag.REMOVE_VARIABLE -> {
                return readRemoveVariable(ctx, muleElement);
            }
            case MuleXMLTag.REMOVE_SESSION_VARIABLE -> {
                return readRemoveSessionVariable(ctx, muleElement);
            }
            case MuleXMLTag.HTTP_REQUEST -> {
                return readHttpRequest(ctx, muleElement);
            }
            case MuleXMLTag.SET_PAYLOAD -> {
                return readSetPayload(ctx, muleElement);
            }
            case MuleXMLTag.CHOICE -> {
                return readChoice(ctx, muleElement);
            }
            case MuleXMLTag.FLOW_REFERENCE -> {
                return readFlowReference(ctx, muleElement);
            }
            case MuleXMLTag.TRANSFORM_MESSAGE -> {
                return readTransformMessage(ctx, muleElement);
            }
            case MuleXMLTag.DB_INSERT, MuleXMLTag.DB_SELECT, MuleXMLTag.DB_UPDATE, MuleXMLTag.DB_DELETE -> {
                return readDatabase(ctx, muleElement);
            }
            case MuleXMLTag.OBJECT_TO_JSON -> {
                return readObjectToJson(ctx, muleElement);
            }
            case MuleXMLTag.OBJECT_TO_STRING -> {
                return readObjectToString(ctx, muleElement);
            }
            case MuleXMLTag.ENRICHER -> {
                return readEnricher(ctx, muleElement);
            }
            case MuleXMLTag.ASYNC -> {
                return readAsync(ctx, muleElement);
            }
            case MuleXMLTag.CATCH_EXCEPTION_STRATEGY -> {
                return readCatchExceptionStrategy(ctx, muleElement);
            }
            case MuleXMLTag.CHOICE_EXCEPTION_STRATEGY -> {
                return readChoiceExceptionStrategy(ctx, muleElement);
            }
            case MuleXMLTag.REFERENCE_EXCEPTION_STRATEGY -> {
                return readReferenceExceptionStrategy(ctx, muleElement);
            }
            case MuleXMLTag.VM_OUTBOUND_ENDPOINT -> {
                return readVMOutboundEndpoint(ctx, muleElement);
            }
            default -> {
                return readUnsupportedBlock(ctx, muleElement);
            }
        }
    }

    // Components
    private static Logger readLogger(Context ctx, MuleXMLNavigator.MuleElement muleElement) {
        Element element = muleElement.getElement();
        ctx.addImport(new Import(Constants.ORG_BALLERINA, Constants.MODULE_LOG));
        String message = element.getAttribute("message");
        String level = element.getAttribute("level");
        LogLevel logLevel = switch (level) {
            case "DEBUG" -> LogLevel.DEBUG;
            case "ERROR" -> LogLevel.ERROR;
            case "INFO" -> LogLevel.INFO;
            case "TRACE" -> LogLevel.TRACE;
            case "WARN" -> LogLevel.WARN;
            default -> throw new IllegalStateException();
        };
        return new Logger(message, logLevel);
    }

    private static MuleRecord readExpressionComponent(Context ctx, MuleXMLNavigator.MuleElement muleElement) {
        return new ExpressionComponent(muleElement.getElement().getTextContent());
    }

    // Flow Control
    private static Choice readChoice(Context ctx, MuleXMLNavigator.MuleElement muleElement) {
        List<WhenInChoice> whens = new ArrayList<>();
        List<MuleRecord> otherwiseProcess = new ArrayList<>();
        while (muleElement.peekChild() != null) {
            MuleXMLNavigator.MuleElement child = muleElement.consumeChild();
            Element childElement = child.getElement();
            if (childElement.getTagName().equals(MuleXMLTag.WHEN.tag())) {
                String condition = childElement.getAttribute("expression");
                List<MuleRecord> whenProcess = new ArrayList<>();

                while (child.peekChild() != null) {
                    MuleXMLNavigator.MuleElement whenChild = child.consumeChild();
                    MuleRecord r = readBlock(ctx, whenChild);
                    whenProcess.add(r);
                }

                WhenInChoice whenInChoice = new WhenInChoice(condition, whenProcess);
                whens.add(whenInChoice);
            } else {
                assert childElement.getTagName().equals(MuleXMLTag.OTHERWISE.tag());
                assert otherwiseProcess.isEmpty();
                while (child.peekChild() != null) {
                    MuleXMLNavigator.MuleElement otherwiseChild = child.consumeChild();
                    MuleRecord r = readBlock(ctx, otherwiseChild);
                    otherwiseProcess.add(r);
                }
            }
        }
        return new Choice(whens, otherwiseProcess);
    }

    // Scopes
    public static Flow readFlow(Context ctx, MuleXMLNavigator.MuleElement mFlowElement) {
        Element flowElement = mFlowElement.getElement();
        String flowName = flowElement.getAttribute("name");

        MuleRecord source = null;
        List<MuleRecord> flowBlocks = new ArrayList<>();

        while (mFlowElement.peekChild() != null) {
            MuleXMLNavigator.MuleElement child = mFlowElement.consumeChild();
            Element element = child.getElement();
            if (element.getTagName().equals(MuleXMLTag.HTTP_LISTENER.tag())) {
                assert source == null;
                source = readBlock(ctx, child);
            } else if (element.getTagName().equals(MuleXMLTag.VM_INBOUND_ENDPOINT.tag())) {
                assert source == null;
                source = readBlock(ctx, child);
            } else {
                MuleRecord muleRec = readBlock(ctx, child);
                flowBlocks.add(muleRec);
            }
        }

        Optional<MuleRecord> optSource;
        if (source == null) {
            optSource = Optional.empty();
        } else {
            optSource = Optional.of(source);
        }
        return new Flow(flowName, optSource, flowBlocks);
    }

    public static SubFlow readSubFlow(Context ctx, MuleXMLNavigator.MuleElement mFlowElement) {
        Element flowElement = mFlowElement.getElement();
        String flowName = flowElement.getAttribute("name");

        List<MuleRecord> flowBlocks = new ArrayList<>();
        while (mFlowElement.peekChild() != null) {
            MuleXMLNavigator.MuleElement muleElement = mFlowElement.consumeChild();
            MuleRecord muleRec = readBlock(ctx, muleElement);
            flowBlocks.add(muleRec);
        }

        return new SubFlow(flowName, flowBlocks);
    }

    private static Enricher readEnricher(Context ctx, MuleXMLNavigator.MuleElement muleElement) {
        Element element = muleElement.getElement();
        String source = element.getAttribute("source");
        String target = element.getAttribute("target");

        MuleRecord block = null;
        while (muleElement.peekChild() != null) {
            MuleXMLNavigator.MuleElement muleChild = muleElement.consumeChild();
            assert block == null;
            block = readBlock(ctx, muleChild);
        }

        Optional<MuleRecord> innerBlock = block != null ? Optional.of(block) : Optional.empty();
        return new Enricher(source, target, innerBlock);
    }

    private static Async readAsync(Context ctx, MuleXMLNavigator.MuleElement muleElement) {
        List<MuleRecord> flowBlocks = new ArrayList<>();
        while (muleElement.peekChild() != null) {
            MuleXMLNavigator.MuleElement child = muleElement.consumeChild();
            MuleRecord muleRec = readBlock(ctx, child);
            flowBlocks.add(muleRec);
        }

        return new Async(flowBlocks);
    }

    // Transformers
    private static Payload readSetPayload(Context ctx, MuleXMLNavigator.MuleElement muleElement) {
        Element element = muleElement.getElement();
        String muleExpr = element.getAttribute("value");
        return new Payload(muleExpr);
    }

    private static SetVariable readSetVariable(Context ctx, MuleXMLNavigator.MuleElement muleElement) {
        Element element = muleElement.getElement();
        String varName = element.getAttribute("variableName");
        String val = element.getAttribute("value");
        return new SetVariable(varName, val);
    }

    private static SetSessionVariable readSetSessionVariable(Context ctx, MuleXMLNavigator.MuleElement muleElement) {
        Element element = muleElement.getElement();
        String varName = element.getAttribute("variableName");
        String val = element.getAttribute("value");
        return new SetSessionVariable(varName, val);
    }

    private static RemoveVariable readRemoveVariable(Context ctx, MuleXMLNavigator.MuleElement muleElement) {
        Element element = muleElement.getElement();
        String varName = element.getAttribute("variableName");
        return new RemoveVariable(Kind.REMOVE_VARIABLE, varName);
    }

    private static RemoveVariable readRemoveSessionVariable(Context ctx, MuleXMLNavigator.MuleElement muleElement) {
        Element element = muleElement.getElement();
        String varName = element.getAttribute("variableName");
        return new RemoveVariable(Kind.REMOVE_SESSION_VARIABLE, varName);
    }

    private static ObjectToJson readObjectToJson(Context ctx, MuleXMLNavigator.MuleElement muleElement) {
        return new ObjectToJson();
    }

    private static ObjectToString readObjectToString(Context ctx, MuleXMLNavigator.MuleElement muleElement) {
        return new ObjectToString();
    }

    // Error handling
    private static CatchExceptionStrategy readCatchExceptionStrategy(Context ctx,
                                                                     MuleXMLNavigator.MuleElement muleElement) {
        Element element = muleElement.getElement();
        String name = element.getAttribute("name");
        String when = element.getAttribute("when");

        List<MuleRecord> catchBlocks = new ArrayList<>();
        while (muleElement.peekChild() != null) {
            MuleXMLNavigator.MuleElement muleChild = muleElement.consumeChild();
            MuleRecord muleRec = readBlock(ctx, muleChild);
            catchBlocks.add(muleRec);
        }

        return new CatchExceptionStrategy(catchBlocks, when, name);
    }

    private static ChoiceExceptionStrategy readChoiceExceptionStrategy(Context ctx,
                                                                       MuleXMLNavigator.MuleElement muleElement) {
        Element element = muleElement.getElement();
        String name = element.getAttribute("name");

        List<CatchExceptionStrategy> catchExceptionStrategyList = new ArrayList<>();
        while (muleElement.peekChild() != null) {
            MuleXMLNavigator.MuleElement muleChild = muleElement.consumeChild();
            assert muleChild.getElement().getTagName().equals(MuleXMLTag.CATCH_EXCEPTION_STRATEGY.tag());
            // TODO: only catch-exp-strategy is supported for now
            CatchExceptionStrategy catchExceptionStrategy = readCatchExceptionStrategy(ctx, muleChild);
            catchExceptionStrategyList.add(catchExceptionStrategy);
        }

        return new ChoiceExceptionStrategy(catchExceptionStrategyList, name);
    }

    private static ReferenceExceptionStrategy readReferenceExceptionStrategy(Context ctx,
                                                                             MuleXMLNavigator.MuleElement muleElement) {
        Element element = muleElement.getElement();
        String refName = element.getAttribute("ref");
        return new ReferenceExceptionStrategy(refName);
    }

    // HTTP Module
    private static HttpListener readHttpListener(Context ctx, MuleXMLNavigator.MuleElement muleElement) {
        Element element = muleElement.getElement();
        String configRef = element.getAttribute("config-ref");
        String resourcePath = element.getAttribute("path");
        String[] allowedMethods = Arrays.stream(getAllowedMethods(element.getAttribute("allowedMethods")))
                .map(String::toLowerCase).toArray(String[]::new);
        return new HttpListener(configRef, resourcePath, allowedMethods);
    }

    private static HttpRequest readHttpRequest(Context ctx, MuleXMLNavigator.MuleElement muleElement) {
        Element element = muleElement.getElement();
        String configRef = element.getAttribute("config-ref");
        HTTPRequestConfig httpRequestConfig = ctx.projectCtx.getHttpRequestConfig(configRef);;
        String host = httpRequestConfig.host();
        String port = httpRequestConfig.port();
        String url = String.format("%s:%s", host, port);

        String protocol = httpRequestConfig.protocol();
        if (!protocol.isEmpty()) {
            url = protocol.toLowerCase() + "://" + url;
        }

        String method = element.getAttribute("method").toLowerCase();
        String path = element.getAttribute("path");

        Map<String, String> queryParams = new HashMap<>();
        while (muleElement.peekChild() != null) {
            MuleXMLNavigator.MuleElement child = muleElement.consumeChild();
            if (child.getElement().getTagName().equals(MuleXMLTag.HTTP_REQEUST_BUILDER.tag())) {
                processQueryParams(queryParams, child);
            } else {
                // TODO: handle all other scenarios
                throw new UnsupportedOperationException();
            }
        }

        return new HttpRequest(configRef, method, url, path, queryParams);
    }

    private static void processQueryParams(Map<String, String> queryParams, MuleXMLNavigator.MuleElement muleElement) {
        while (muleElement.peekChild() != null) {
            MuleXMLNavigator.MuleElement child = muleElement.consumeChild();
            Element element = child.getElement();
            assert element.getTagName().equals(MuleXMLTag.HTTP_QUERY_PARAM.tag());
            String paramName = element.getAttribute("paramName");
            String value = element.getAttribute("value");
            queryParams.put(paramName, value);
        }
    }

    private static FlowReference readFlowReference(Context ctx, MuleXMLNavigator.MuleElement muleElement) {
        Element element = muleElement.getElement();
        String flowName = element.getAttribute("name");
        return new FlowReference(flowName);
    }

    // VM Connector
    private static VMInboundEndpoint readVMInboundEndpoint(Context ctx, MuleXMLNavigator.MuleElement muleElement) {
        Element element = muleElement.getElement();
        String path = element.getAttribute("path");
        String exchangePattern = element.getAttribute("exchange-pattern");
        return new VMInboundEndpoint(path, exchangePattern);
    }

    private static VMOutboundEndpoint readVMOutboundEndpoint(Context ctx, MuleXMLNavigator.MuleElement muleElement) {
        Element element = muleElement.getElement();
        String path = element.getAttribute("path");
        String exchangePattern = element.getAttribute("exchange-pattern");
        return new VMOutboundEndpoint(path, exchangePattern);
    }

    // Database Connector
    private static Database readDatabase(Context ctx, MuleXMLNavigator.MuleElement muleElement) {
        Element element = muleElement.getElement();
        String configRef = element.getAttribute("config-ref");

        QueryType queryType = null;
        String query = null;

        while (muleElement.peekChild() != null) {
            MuleXMLNavigator.MuleElement child = muleElement.consumeChild();
            assert queryType == null;

            queryType = getQueryType(child.getElement().getTagName());
            query = readQuery(ctx, child, queryType);
        }

        if (queryType == null) {
            throw new IllegalStateException("No valid query found in the database block");
        }

        MuleXMLTag muleXMLTag = MuleXMLTag.fromTag(element.getTagName());
        Kind kind = switch (muleXMLTag) {
            case MuleXMLTag.DB_INSERT -> Kind.DB_INSERT;
            case MuleXMLTag.DB_SELECT -> Kind.DB_SELECT;
            case MuleXMLTag.DB_UPDATE -> Kind.DB_UPDATE;
            case MuleXMLTag.DB_DELETE -> Kind.DB_DELETE;
            default -> throw new UnsupportedOperationException();
        };

        return new Database(kind, configRef, queryType, query);
    }

    private static QueryType getQueryType(String tagName) {
        MuleXMLTag muleXMLTag = MuleXMLTag.fromTag(tagName);
        return switch (muleXMLTag) {
            case MuleXMLTag.DB_PARAMETERIZED_QUERY -> QueryType.PARAMETERIZED_QUERY;
            case MuleXMLTag.DB_DYNAMIC_QUERY -> QueryType.DYNAMIC_QUERY;
            case MuleXMLTag.DB_TEMPLATE_QUERY_REF -> QueryType.TEMPLATE_QUERY_REF;
            default -> throw new IllegalStateException("Invalid query type");
        };
    }

    private static String readQuery(Context ctx, MuleXMLNavigator.MuleElement muleElement, QueryType queryType) {
        return switch (queryType) {
            case PARAMETERIZED_QUERY -> readDbParameterizedQuery(ctx, muleElement);
            case DYNAMIC_QUERY -> readDbDynamicQuery(ctx, muleElement);
            case TEMPLATE_QUERY_REF -> readDbTemplateQueryRef(ctx, muleElement);
        };
    }

    private static String readDbParameterizedQuery(Context ctx, MuleXMLNavigator.MuleElement muleElement) {
        return muleElement.getElement().getTextContent();
    }

    private static String readDbDynamicQuery(Context ctx, MuleXMLNavigator.MuleElement muleElement) {
        return muleElement.getElement().getTextContent();
    }

    private static String readDbTemplateQueryRef(Context ctx, MuleXMLNavigator.MuleElement muleElement) {
        return muleElement.getElement().getAttribute("name");
    }

    private static UnsupportedBlock readUnsupportedBlock(Context ctx, MuleXMLNavigator.MuleElement muleElement) {
        Element element = muleElement.getElement();
        String xmlBlock = ConversionUtils.elementToString(element);
        return new UnsupportedBlock(xmlBlock);
    }

    // Global Elements
    private static HTTPListenerConfig readHttpListenerConfig(Context ctx, MuleXMLNavigator.MuleElement muleElement) {
        Element element = muleElement.getElement();
        ctx.addImport(new Import(Constants.ORG_BALLERINA, Constants.MODULE_HTTP));
        String listenerName = element.getAttribute("name");
        String basePath = element.getAttribute("basePath");

        // For Mule 4.x, host and port are within a nested http:listener-connection element
        String host = "0.0.0.0"; // Default value
        String port = "8080";    // Default value

        while (muleElement.peekChild() != null) {
            MuleXMLNavigator.MuleElement child = muleElement.consumeChild();
            Element childElement = child.getElement();
            if (childElement.getTagName().equals(HTTP_LISTENER_CONNECTION.tag())) {
                host = childElement.getAttribute("host");
                port = childElement.getAttribute("port");
            }
        }

        return new HTTPListenerConfig(listenerName, basePath, port, host);
    }

    private static HTTPRequestConfig readHttpRequestConfig(Context ctx, MuleXMLNavigator.MuleElement muleElement) {
        Element element = muleElement.getElement();
        ctx.addImport(new Import(Constants.ORG_BALLERINA, Constants.MODULE_HTTP));
        String configName = element.getAttribute("name");
        String host = element.getAttribute("host");
        String port = element.getAttribute("port");
        String protocol = element.getAttribute("protocol");
        return new HTTPRequestConfig(configName, host, port, protocol);
    }

    private static DbMSQLConfig readDbMySQLConfig(Context ctx, MuleXMLNavigator.MuleElement muleElement) {
        Element element = muleElement.getElement();
        ctx.addImport(new Import(Constants.ORG_BALLERINAX, Constants.MODULE_MYSQL));
        ctx.addImport(new Import(Constants.ORG_BALLERINAX, Constants.MODULE_MYSQL_DRIVER, Optional.of("_")));
        String name = element.getAttribute("name");
        String host = element.getAttribute("host");
        String port = element.getAttribute("port");
        String user = element.getAttribute("user");
        String password = element.getAttribute("password");
        String database = element.getAttribute("database");
        return new DbMSQLConfig(name, host, port, user, password, database);
    }

    private static DbOracleConfig readDbOracleConfig(Context ctx, MuleXMLNavigator.MuleElement muleElement) {
        Element element = muleElement.getElement();
        ctx.addImport(new Import(Constants.ORG_BALLERINAX, Constants.MODULE_ORACLEDB));
        ctx.addImport(new Import(Constants.ORG_BALLERINAX, Constants.MODULE_ORACLEDB_DRIVER, Optional.of("_")));
        String name = element.getAttribute("name");
        String host = element.getAttribute("host");
        String port = element.getAttribute("port");
        String user = element.getAttribute("user");
        String password = element.getAttribute("password");
        String instance = element.getAttribute("instance");
        return new DbOracleConfig(name, host, port, user, password, instance);
    }

    private static DbTemplateQuery readDbTemplateQuery(Context ctx, MuleXMLNavigator.MuleElement muleElement) {
        String name = muleElement.getElement().getAttribute("name");
        String query = null;
        List<DbInParam> dbInParams = new ArrayList<>();

        while (muleElement.peekChild() != null) {
            MuleXMLNavigator.MuleElement child = muleElement.consumeChild();
            Element childElement = child.getElement();

            if (childElement.getTagName().equals("db:parameterized-query")) {
                query = readDbParameterizedQuery(ctx, child);
            } else if (childElement.getTagName().equals("db:in-param")) {
                DbInParam dbInParam = readDbInParam(ctx, child);
                dbInParams.add(dbInParam);
            } else {
                throw new UnsupportedOperationException();
            }
        }

        if (query == null) {
            throw new IllegalStateException("No query found in the db:template-query block");
        }
        return new DbTemplateQuery(name, query, dbInParams);
    }

    private static DbInParam readDbInParam(Context ctx, MuleXMLNavigator.MuleElement muleElement) {
        Element element = muleElement.getElement();
        String name = element.getAttribute("name");
        String type = element.getAttribute("type");
        Type ty = Type.from(type);
        String defaultValue = element.getAttribute("defaultValue");
        return new DbInParam(name, ty, defaultValue);
    }

    private static TransformMessage readTransformMessage(Context ctx, MuleXMLNavigator.MuleElement muleElement) {
        List<TransformMessageElement> transformMessageElements = new ArrayList<>();
        while (muleElement.peekChild() != null) {
            MuleXMLNavigator.MuleElement child = muleElement.consumeChild();
            Element element = child.getElement();

            MuleXMLTag muleXMLTag = MuleXMLTag.fromTag(child.getElement().getTagName());
            switch (muleXMLTag) {
                case MuleXMLTag.DW_SET_PAYLOAD -> {
                    String resource = element.getAttribute("resource");
                    if (resource.isEmpty()) {
                        transformMessageElements.add(new SetPayloadElement(null, element.getTextContent()));
                    } else {
                        transformMessageElements.add(new SetPayloadElement(resource, null));
                    }
                }
                case MuleXMLTag.DW_INPUT_PAYLOAD -> {
                    String mimeType = element.getAttribute("mimeType");
                    String docSamplePath = element.getAttribute("doc:sample");
                    transformMessageElements.add(new InputPayloadElement(mimeType, docSamplePath));
                }
                case MuleXMLTag.DW_SET_VARIABLE -> {
                    String variableName = element.getAttribute("variableName");
                    String resource = element.getAttribute("resource");
                    String script = null;
                    if (resource.isEmpty()) {
                        script = ((CDATASection) element.getChildNodes().item(0)).getData();
                    }
                    transformMessageElements.add(new SetVariableElement(resource, script, variableName));
                }
                case MuleXMLTag.DW_SET_SESSION_VARIABLE -> {
                    String variableName = element.getAttribute("variableName");
                    String resource = element.getAttribute("resource");
                    String script = null;
                    if (resource.isEmpty()) {
                        // TODO: fix CDATA cast
                        script = ((CDATASection) element.getChildNodes().item(0)).getData();
                    }
                    transformMessageElements.add(new SetSessionVariableElement(
                            resource, script, variableName));
                }

                default -> throw new UnsupportedOperationException();
            }
        }
        return new TransformMessage(transformMessageElements);
    }
}
