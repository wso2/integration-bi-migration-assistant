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
package mule;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static common.BallerinaModel.Import;
import static mule.ConversionUtils.getAllowedMethods;
import static mule.MuleModel.Async;
import static mule.MuleModel.CatchExceptionStrategy;
import static mule.MuleModel.Choice;
import static mule.MuleModel.ChoiceExceptionStrategy;
import static mule.MuleModel.Database;
import static mule.MuleModel.DbInParam;
import static mule.MuleModel.DbMSQLConfig;
import static mule.MuleModel.DbOracleConfig;
import static mule.MuleModel.DbTemplateQuery;
import static mule.MuleModel.Enricher;
import static mule.MuleModel.ExpressionComponent;
import static mule.MuleModel.Flow;
import static mule.MuleModel.FlowReference;
import static mule.MuleModel.HTTPListenerConfig;
import static mule.MuleModel.HTTPRequestConfig;
import static mule.MuleModel.HttpListener;
import static mule.MuleModel.HttpRequest;
import static mule.MuleModel.InputPayloadElement;
import static mule.MuleModel.Kind;
import static mule.MuleModel.LogLevel;
import static mule.MuleModel.Logger;
import static mule.MuleModel.MuleRecord;
import static mule.MuleModel.ObjectToJson;
import static mule.MuleModel.ObjectToString;
import static mule.MuleModel.Payload;
import static mule.MuleModel.QueryType;
import static mule.MuleModel.ReferenceExceptionStrategy;
import static mule.MuleModel.RemoveVariable;
import static mule.MuleModel.SetPayloadElement;
import static mule.MuleModel.SetSessionVariable;
import static mule.MuleModel.SetSessionVariableElement;
import static mule.MuleModel.SetVariable;
import static mule.MuleModel.SetVariableElement;
import static mule.MuleModel.SubFlow;
import static mule.MuleModel.TransformMessage;
import static mule.MuleModel.TransformMessageElement;
import static mule.MuleModel.Type;
import static mule.MuleModel.UnsupportedBlock;
import static mule.MuleModel.VMInboundEndpoint;
import static mule.MuleModel.VMOutboundEndpoint;
import static mule.MuleModel.WhenInChoice;
import static mule.MuleToBalConverter.Data;

public class MuleConfigReader {

    public static void readMuleConfigFromRoot(Data data, MuleXMLNavigator.MuleElement muleElement,
                                              List<Flow> flows, List<SubFlow> subFlows) {
        while (muleElement.peekChild() != null) {
            MuleXMLNavigator.MuleElement child = muleElement.consumeChild();
            Element element = child.getElement();

            String elementTagName = element.getTagName();
            if (MuleXMLTag.FLOW.tag().equals(elementTagName)) {
                Flow flow = readFlow(data, child);
                flows.add(flow);
                continue;
            } else if (MuleXMLTag.SUB_FLOW.tag().equals(elementTagName)) {
                SubFlow subFlow = readSubFlow(data, child);
                subFlows.add(subFlow);
                continue;
            }

            readGlobalConfigElement(data, child);
        }
    }

    public static void readGlobalConfigElement(Data data, MuleXMLNavigator.MuleElement muleElement) {
        String elementTagName = muleElement.getElement().getTagName();
        if (MuleXMLTag.HTTP_LISTENER_CONFIG.tag().equals(elementTagName)) {
            HTTPListenerConfig httpListenerConfig = readHttpListenerConfig(data, muleElement);
            data.globalHttpListenerConfigsMap.put(httpListenerConfig.name(), httpListenerConfig);
            data.sharedProjectData.sharedHttpListenerConfigsMap.put(httpListenerConfig.name(), httpListenerConfig);
        } else if (MuleXMLTag.HTTP_REQUEST_CONFIG.tag().equals(elementTagName)) {
            HTTPRequestConfig httpRequestConfig = readHttpRequestConfig(data, muleElement);
            data.globalHttpRequestConfigsMap.put(httpRequestConfig.name(), httpRequestConfig);
            data.sharedProjectData.sharedHttpRequestConfigsMap.put(httpRequestConfig.name(), httpRequestConfig);
        } else if (MuleXMLTag.DB_MYSQL_CONFIG.tag().equals(elementTagName)) {
            DbMSQLConfig dbMSQLConfig = readDbMySQLConfig(data, muleElement);
            data.globalDbMySQLConfigsMap.put(dbMSQLConfig.name(), dbMSQLConfig);
            data.sharedProjectData.sharedDbMySQLConfigsMap.put(dbMSQLConfig.name(), dbMSQLConfig);
        } else if (MuleXMLTag.DB_ORACLE_CONFIG.tag().equals(elementTagName)) {
            DbOracleConfig dbOracleConfig = readDbOracleConfig(data, muleElement);
            data.globalDbOracleConfigsMap.put(dbOracleConfig.name(), dbOracleConfig);
            data.sharedProjectData.sharedDbOracleConfigsMap.put(dbOracleConfig.name(), dbOracleConfig);
        } else if (MuleXMLTag.DB_TEMPLATE_QUERY.tag().equals(elementTagName)) {
            DbTemplateQuery dbTemplateQuery = readDbTemplateQuery(data, muleElement);
            data.globalDbTemplateQueryMap.put(dbTemplateQuery.name(), dbTemplateQuery);
            data.sharedProjectData.sharedDbTemplateQueryMap.put(dbTemplateQuery.name(), dbTemplateQuery);
        } else if (MuleXMLTag.CATCH_EXCEPTION_STRATEGY.tag().equals(elementTagName)) {
            CatchExceptionStrategy catchExceptionStrategy = readCatchExceptionStrategy(data, muleElement);
            data.globalExceptionStrategies.add(catchExceptionStrategy);
        } else if (MuleXMLTag.CHOICE_EXCEPTION_STRATEGY.tag().equals(elementTagName)) {
            ChoiceExceptionStrategy choiceExceptionStrategy = readChoiceExceptionStrategy(data, muleElement);
            data.globalExceptionStrategies.add(choiceExceptionStrategy);
        } else {
            UnsupportedBlock unsupportedBlock = readUnsupportedBlock(data, muleElement);
            data.globalUnsupportedBlocks.add(unsupportedBlock);
        }
    }

    public static MuleRecord readBlock(Data data, MuleXMLNavigator.MuleElement muleElement) {
        MuleXMLTag muleXMLTag = MuleXMLTag.fromTag(muleElement.getElement().getTagName());
        switch (muleXMLTag) {
            // Source
            case MuleXMLTag.HTTP_LISTENER -> {
                return readHttpListener(data, muleElement);
            }

            case MuleXMLTag.VM_INBOUND_ENDPOINT -> {
                return readVMInboundEndpoint(data, muleElement);
            }

            // Process Items
            case MuleXMLTag.LOGGER -> {
                return readLogger(data, muleElement);
            }
            case MuleXMLTag.EXPRESSION_COMPONENT -> {
                return readExpressionComponent(data, muleElement);
            }
            case MuleXMLTag.SET_VARIABLE -> {
                return readSetVariable(data, muleElement);
            }
            case MuleXMLTag.SET_SESSION_VARIABLE -> {
                return readSetSessionVariable(data, muleElement);
            }
            case MuleXMLTag.REMOVE_VARIABLE -> {
                return readRemoveVariable(data, muleElement);
            }
            case MuleXMLTag.REMOVE_SESSION_VARIABLE -> {
                return readRemoveSessionVariable(data, muleElement);
            }
            case MuleXMLTag.HTTP_REQUEST -> {
                return readHttpRequest(data, muleElement);
            }
            case MuleXMLTag.SET_PAYLOAD -> {
                return readSetPayload(data, muleElement);
            }
            case MuleXMLTag.CHOICE -> {
                return readChoice(data, muleElement);
            }
            case MuleXMLTag.FLOW_REFERENCE -> {
                return readFlowReference(data, muleElement);
            }
            case MuleXMLTag.TRANSFORM_MESSAGE -> {
                return readTransformMessage(data, muleElement);
            }
            case MuleXMLTag.DB_INSERT, MuleXMLTag.DB_SELECT, MuleXMLTag.DB_UPDATE, MuleXMLTag.DB_DELETE -> {
                return readDatabase(data, muleElement);
            }
            case MuleXMLTag.OBJECT_TO_JSON -> {
                return readObjectToJson(data, muleElement);
            }
            case MuleXMLTag.OBJECT_TO_STRING -> {
                return readObjectToString(data, muleElement);
            }
            case MuleXMLTag.ENRICHER -> {
                return readEnricher(data, muleElement);
            }
            case MuleXMLTag.ASYNC -> {
                return readAsync(data, muleElement);
            }
            case MuleXMLTag.CATCH_EXCEPTION_STRATEGY -> {
                return readCatchExceptionStrategy(data, muleElement);
            }
            case MuleXMLTag.CHOICE_EXCEPTION_STRATEGY -> {
                return readChoiceExceptionStrategy(data, muleElement);
            }
            case MuleXMLTag.REFERENCE_EXCEPTION_STRATEGY -> {
                return readReferenceExceptionStrategy(data, muleElement);
            }
            case MuleXMLTag.VM_OUTBOUND_ENDPOINT -> {
                return readVMOutboundEndpoint(data, muleElement);
            }
            default -> {
                return readUnsupportedBlock(data, muleElement);
            }
        }
    }

    // Components
    private static Logger readLogger(Data data, MuleXMLNavigator.MuleElement muleElement) {
        Element element = muleElement.getElement();
        data.imports.add(new Import(Constants.ORG_BALLERINA, Constants.MODULE_LOG));
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

    private static MuleRecord readExpressionComponent(Data data, MuleXMLNavigator.MuleElement muleElement) {
        return new ExpressionComponent(muleElement.getElement().getTextContent());
    }

    // Flow Control
    private static Choice readChoice(Data data, MuleXMLNavigator.MuleElement muleElement) {
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
                    MuleRecord r = readBlock(data, whenChild);
                    whenProcess.add(r);
                }

                WhenInChoice whenInChoice = new WhenInChoice(condition, whenProcess);
                whens.add(whenInChoice);
            } else {
                assert childElement.getTagName().equals(MuleXMLTag.OTHERWISE.tag());
                assert otherwiseProcess.isEmpty();
                while (child.peekChild() != null) {
                    MuleXMLNavigator.MuleElement otherwiseChild = child.consumeChild();
                    MuleRecord r = readBlock(data, otherwiseChild);
                    otherwiseProcess.add(r);
                }
            }
        }
        return new Choice(whens, otherwiseProcess);
    }

    // Scopes
    public static Flow readFlow(Data data, MuleXMLNavigator.MuleElement mFlowElement) {
        Element flowElement = mFlowElement.getElement();
        String flowName = flowElement.getAttribute("name");

        MuleRecord source = null;
        List<MuleRecord> flowBlocks = new ArrayList<>();

        while (mFlowElement.peekChild() != null) {
            MuleXMLNavigator.MuleElement child = mFlowElement.consumeChild();
            Element element = child.getElement();
            if (element.getTagName().equals(MuleXMLTag.HTTP_LISTENER.tag())) {
                assert source == null;
                source = readBlock(data, child);
            } else if (element.getTagName().equals(MuleXMLTag.VM_INBOUND_ENDPOINT.tag())) {
                assert source == null;
                source = readBlock(data, child);
            } else {
                MuleRecord muleRec = readBlock(data, child);
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

    public static SubFlow readSubFlow(Data data, MuleXMLNavigator.MuleElement mFlowElement) {
        Element flowElement = mFlowElement.getElement();
        String flowName = flowElement.getAttribute("name");

        List<MuleRecord> flowBlocks = new ArrayList<>();
        while (mFlowElement.peekChild() != null) {
            MuleXMLNavigator.MuleElement muleElement = mFlowElement.consumeChild();
            MuleRecord muleRec = readBlock(data, muleElement);
            flowBlocks.add(muleRec);
        }

        return new SubFlow(flowName, flowBlocks);
    }

    private static Enricher readEnricher(Data data, MuleXMLNavigator.MuleElement muleElement) {
        Element element = muleElement.getElement();
        String source = element.getAttribute("source");
        String target = element.getAttribute("target");

        MuleRecord block = null;
        while (muleElement.peekChild() != null) {
            MuleXMLNavigator.MuleElement muleChild = muleElement.consumeChild();
            assert block == null;
            block = readBlock(data, muleChild);
        }

        Optional<MuleRecord> innerBlock = block != null ? Optional.of(block) : Optional.empty();
        return new Enricher(source, target, innerBlock);
    }

    private static Async readAsync(Data data, MuleXMLNavigator.MuleElement muleElement) {
        List<MuleRecord> flowBlocks = new ArrayList<>();
        while (muleElement.peekChild() != null) {
            MuleXMLNavigator.MuleElement child = muleElement.consumeChild();
            MuleRecord muleRec = readBlock(data, child);
            flowBlocks.add(muleRec);
        }

        return new Async(flowBlocks);
    }

    // Transformers
    private static Payload readSetPayload(Data data, MuleXMLNavigator.MuleElement muleElement) {
        Element element = muleElement.getElement();
        String muleExpr = element.getAttribute("value");
        return new Payload(muleExpr);
    }

    private static SetVariable readSetVariable(Data data, MuleXMLNavigator.MuleElement muleElement) {
        Element element = muleElement.getElement();
        String varName = element.getAttribute("variableName");
        String val = element.getAttribute("value");
        return new SetVariable(varName, val);
    }

    private static SetSessionVariable readSetSessionVariable(Data data, MuleXMLNavigator.MuleElement muleElement) {
        Element element = muleElement.getElement();
        String varName = element.getAttribute("variableName");
        String val = element.getAttribute("value");
        return new SetSessionVariable(varName, val);
    }

    private static RemoveVariable readRemoveVariable(Data data, MuleXMLNavigator.MuleElement muleElement) {
        Element element = muleElement.getElement();
        String varName = element.getAttribute("variableName");
        return new RemoveVariable(Kind.REMOVE_VARIABLE, varName);
    }

    private static RemoveVariable readRemoveSessionVariable(Data data, MuleXMLNavigator.MuleElement muleElement) {
        Element element = muleElement.getElement();
        String varName = element.getAttribute("variableName");
        return new RemoveVariable(Kind.REMOVE_SESSION_VARIABLE, varName);
    }

    private static ObjectToJson readObjectToJson(Data data, MuleXMLNavigator.MuleElement muleElement) {
        return new ObjectToJson();
    }

    private static ObjectToString readObjectToString(Data data, MuleXMLNavigator.MuleElement muleElement) {
        return new ObjectToString();
    }

    // Error handling
    private static CatchExceptionStrategy readCatchExceptionStrategy(Data data,
                                                                     MuleXMLNavigator.MuleElement muleElement) {
        Element element = muleElement.getElement();
        String name = element.getAttribute("name");
        String when = element.getAttribute("when");

        List<MuleRecord> catchBlocks = new ArrayList<>();
        while (muleElement.peekChild() != null) {
            MuleXMLNavigator.MuleElement muleChild = muleElement.consumeChild();
            MuleRecord muleRec = readBlock(data, muleChild);
            catchBlocks.add(muleRec);
        }

        return new CatchExceptionStrategy(catchBlocks, when, name);
    }

    private static ChoiceExceptionStrategy readChoiceExceptionStrategy(Data data,
                                                                       MuleXMLNavigator.MuleElement muleElement) {
        Element element = muleElement.getElement();
        String name = element.getAttribute("name");

        List<CatchExceptionStrategy> catchExceptionStrategyList = new ArrayList<>();
        while (muleElement.peekChild() != null) {
            MuleXMLNavigator.MuleElement muleChild = muleElement.consumeChild();
            assert muleChild.getElement().getTagName().equals(MuleXMLTag.CATCH_EXCEPTION_STRATEGY.tag());
            // TODO: only catch-exp-strategy is supported for now
            CatchExceptionStrategy catchExceptionStrategy = readCatchExceptionStrategy(data, muleChild);
            catchExceptionStrategyList.add(catchExceptionStrategy);
        }

        return new ChoiceExceptionStrategy(catchExceptionStrategyList, name);
    }

    private static ReferenceExceptionStrategy readReferenceExceptionStrategy(Data data,
                                                                             MuleXMLNavigator.MuleElement muleElement) {
        Element element = muleElement.getElement();
        String refName = element.getAttribute("ref");
        return new ReferenceExceptionStrategy(refName);
    }

    // HTTP Module
    private static HttpListener readHttpListener(Data data, MuleXMLNavigator.MuleElement muleElement) {
        Element element = muleElement.getElement();
        String configRef = element.getAttribute("config-ref");
        String resourcePath = element.getAttribute("path");
        String[] allowedMethods = Arrays.stream(getAllowedMethods(element.getAttribute("allowedMethods")))
                .map(String::toLowerCase).toArray(String[]::new);
        return new HttpListener(configRef, resourcePath, allowedMethods);
    }

    private static HttpRequest readHttpRequest(Data data, MuleXMLNavigator.MuleElement muleElement) {
        Element element = muleElement.getElement();
        String configRef = element.getAttribute("config-ref");
        HTTPRequestConfig httpRequestConfig = data.sharedProjectData.sharedHttpRequestConfigsMap.get(configRef);
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

    private static FlowReference readFlowReference(Data data, MuleXMLNavigator.MuleElement muleElement) {
        Element element = muleElement.getElement();
        String flowName = element.getAttribute("name");
        return new FlowReference(flowName);
    }

    // VM Connector
    private static VMInboundEndpoint readVMInboundEndpoint(Data data, MuleXMLNavigator.MuleElement muleElement) {
        Element element = muleElement.getElement();
        String path = element.getAttribute("path");
        String exchangePattern = element.getAttribute("exchange-pattern");
        return new VMInboundEndpoint(path, exchangePattern);
    }

    private static VMOutboundEndpoint readVMOutboundEndpoint(Data data, MuleXMLNavigator.MuleElement muleElement) {
        Element element = muleElement.getElement();
        String path = element.getAttribute("path");
        String exchangePattern = element.getAttribute("exchange-pattern");
        return new VMOutboundEndpoint(path, exchangePattern);
    }

    // Database Connector
    private static Database readDatabase(Data data, MuleXMLNavigator.MuleElement muleElement) {
        Element element = muleElement.getElement();
        String configRef = element.getAttribute("config-ref");

        QueryType queryType = null;
        String query = null;

        while (muleElement.peekChild() != null) {
            MuleXMLNavigator.MuleElement child = muleElement.consumeChild();
            assert queryType == null;

            queryType = getQueryType(child.getElement().getTagName());
            query = readQuery(data, child, queryType);
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

    private static String readQuery(Data data, MuleXMLNavigator.MuleElement muleElement, QueryType queryType) {
        return switch (queryType) {
            case PARAMETERIZED_QUERY -> readDbParameterizedQuery(data, muleElement);
            case DYNAMIC_QUERY -> readDbDynamicQuery(data, muleElement);
            case TEMPLATE_QUERY_REF -> readDbTemplateQueryRef(data, muleElement);
        };
    }

    private static String readDbParameterizedQuery(Data data, MuleXMLNavigator.MuleElement muleElement) {
        return muleElement.getElement().getTextContent();
    }

    private static String readDbDynamicQuery(Data data, MuleXMLNavigator.MuleElement muleElement) {
        return muleElement.getElement().getTextContent();
    }

    private static String readDbTemplateQueryRef(Data data, MuleXMLNavigator.MuleElement muleElement) {
        return muleElement.getElement().getAttribute("name");
    }

    private static UnsupportedBlock readUnsupportedBlock(Data data, MuleXMLNavigator.MuleElement muleElement) {
        Element element = muleElement.getElement();
        String xmlBlock = ConversionUtils.elementToString(element);
        return new UnsupportedBlock(xmlBlock);
    }

    // Global Elements
    private static HTTPListenerConfig readHttpListenerConfig(Data data, MuleXMLNavigator.MuleElement muleElement) {
        Element element = muleElement.getElement();
        data.imports.add(new Import(Constants.ORG_BALLERINA, Constants.MODULE_HTTP));
        String listenerName = element.getAttribute("name");
        String host = element.getAttribute("host");
        String port = element.getAttribute("port");
        String basePath = element.getAttribute("basePath");
        return new HTTPListenerConfig(listenerName, basePath, port, host);
    }

    private static HTTPRequestConfig readHttpRequestConfig(Data data, MuleXMLNavigator.MuleElement muleElement) {
        Element element = muleElement.getElement();
        data.imports.add(new Import(Constants.ORG_BALLERINA, Constants.MODULE_HTTP));
        String configName = element.getAttribute("name");
        String host = element.getAttribute("host");
        String port = element.getAttribute("port");
        String protocol = element.getAttribute("protocol");
        return new HTTPRequestConfig(configName, host, port, protocol);
    }

    private static DbMSQLConfig readDbMySQLConfig(Data data, MuleXMLNavigator.MuleElement muleElement) {
        Element element = muleElement.getElement();
        data.imports.add(new Import(Constants.ORG_BALLERINAX, Constants.MODULE_MYSQL));
        data.imports.add(new Import(Constants.ORG_BALLERINAX, Constants.MODULE_MYSQL_DRIVER, Optional.of("_")));
        String name = element.getAttribute("name");
        String host = element.getAttribute("host");
        String port = element.getAttribute("port");
        String user = element.getAttribute("user");
        String password = element.getAttribute("password");
        String database = element.getAttribute("database");
        return new DbMSQLConfig(name, host, port, user, password, database);
    }

    private static DbOracleConfig readDbOracleConfig(Data data, MuleXMLNavigator.MuleElement muleElement) {
        Element element = muleElement.getElement();
        data.imports.add(new Import(Constants.ORG_BALLERINAX, Constants.MODULE_ORACLEDB));
        data.imports.add(new Import(Constants.ORG_BALLERINAX, Constants.MODULE_ORACLEDB_DRIVER, Optional.of("_")));
        String name = element.getAttribute("name");
        String host = element.getAttribute("host");
        String port = element.getAttribute("port");
        String user = element.getAttribute("user");
        String password = element.getAttribute("password");
        String instance = element.getAttribute("instance");
        return new DbOracleConfig(name, host, port, user, password, instance);
    }

    private static DbTemplateQuery readDbTemplateQuery(Data data, MuleXMLNavigator.MuleElement muleElement) {
        String name = muleElement.getElement().getAttribute("name");
        String query = null;
        List<DbInParam> dbInParams = new ArrayList<>();

        while (muleElement.peekChild() != null) {
            MuleXMLNavigator.MuleElement child = muleElement.consumeChild();
            Element childElement = child.getElement();

            if (childElement.getTagName().equals("db:parameterized-query")) {
                query = readDbParameterizedQuery(data, child);
            } else if (childElement.getTagName().equals("db:in-param")) {
                DbInParam dbInParam = readDbInParam(data, child);
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

    private static DbInParam readDbInParam(Data data, MuleXMLNavigator.MuleElement muleElement) {
        Element element = muleElement.getElement();
        String name = element.getAttribute("name");
        String type = element.getAttribute("type");
        Type ty = Type.from(type);
        String defaultValue = element.getAttribute("defaultValue");
        return new DbInParam(name, ty, defaultValue);
    }

    private static TransformMessage readTransformMessage(Data data, MuleXMLNavigator.MuleElement muleElement) {
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
