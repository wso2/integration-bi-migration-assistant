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

import mule.common.MuleXMLNavigator;
import mule.common.MuleXMLNavigator.MuleElement;
import mule.v4.Constants;
import mule.v4.Context;
import mule.v4.ConversionUtils;
import mule.v4.model.MuleModel.DbConfig;
import mule.v4.model.MuleModel.DbConnection;
import mule.v4.model.MuleXMLTag;
import mule.v4.model.ParseResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static common.BallerinaModel.Import;
import static mule.v4.ConversionUtils.getAllowedMethods;
import static mule.v4.model.MuleModel.AnypointMqConfig;
import static mule.v4.model.MuleModel.AnypointMqSubscriber;
import static mule.v4.model.MuleModel.PubSubConfig;
import static mule.v4.model.MuleModel.PubSubMessageListener;
import static mule.v4.model.MuleModel.ApiKitConfig;
import static mule.v4.model.MuleModel.ApiKitRouter;
import static mule.v4.model.MuleModel.Async;
import static mule.v4.model.MuleModel.Choice;
import static mule.v4.model.MuleModel.Database;
import static mule.v4.model.MuleModel.DbMySqlConnection;
import static mule.v4.model.MuleModel.DbOracleConnection;
import static mule.v4.model.MuleModel.DbGenericConnection;
import static mule.v4.model.MuleModel.Enricher;
import static mule.v4.model.MuleModel.ErrorHandler;
import static mule.v4.model.MuleModel.ErrorHandlerRecord;
import static mule.v4.model.MuleModel.FirstSuccessful;
import static mule.v4.model.MuleModel.Foreach;
import static mule.v4.model.MuleModel.GlobalProperty;
import static mule.v4.model.MuleModel.OnErrorContinue;
import static mule.v4.model.MuleModel.OnErrorPropagate;
import static mule.v4.model.MuleModel.ExpressionComponent;
import static mule.v4.model.MuleModel.Flow;
import static mule.v4.model.MuleModel.FlowReference;
import static mule.v4.model.MuleModel.HTTPListenerConfig;
import static mule.v4.model.MuleModel.HTTPRequestConfig;
import static mule.v4.model.MuleModel.HttpListener;
import static mule.v4.model.MuleModel.MuleImport;
import static mule.v4.model.MuleModel.HttpRequest;
import static mule.v4.model.MuleModel.Kind;
import static mule.v4.model.MuleModel.LogLevel;
import static mule.v4.model.MuleModel.Logger;
import static mule.v4.model.MuleModel.Scheduler;
import static mule.v4.model.MuleModel.MuleRecord;
import static mule.v4.model.MuleModel.ObjectToJson;
import static mule.v4.model.MuleModel.ObjectToString;
import static mule.v4.model.MuleModel.Payload;
import static mule.v4.model.MuleModel.RaiseError;
import static mule.v4.model.MuleModel.RemoveVariable;
import static mule.v4.model.MuleModel.Route;
import static mule.v4.model.MuleModel.ScatterGather;
import static mule.v4.model.MuleModel.SetPayloadElement;
import static mule.v4.model.MuleModel.SetVariable;
import static mule.v4.model.MuleModel.SetVariableElement;
import static mule.v4.model.MuleModel.SubFlow;
import static mule.v4.model.MuleModel.TransformMessage;
import static mule.v4.model.MuleModel.TransformMessageElement;
import static mule.v4.model.MuleModel.Try;
import static mule.v4.model.MuleModel.UnsupportedBlock;
import static mule.v4.model.MuleModel.VMConfig;
import static mule.v4.model.MuleModel.VMConsume;
import static mule.v4.model.MuleModel.VMListener;
import static mule.v4.model.MuleModel.VMPublish;
import static mule.v4.model.MuleModel.VMQueue;
import static mule.v4.model.MuleModel.WhenInChoice;
import static mule.v4.model.MuleXMLTag.DB_MY_SQL_CONNECTION;
import static mule.v4.model.MuleXMLTag.DB_ORACLE_CONNECTION;
import static mule.v4.model.MuleXMLTag.DB_GENERIC_CONNECTION;
import static mule.v4.model.MuleXMLTag.HTTP_LISTENER_CONNECTION;
import static mule.v4.model.MuleXMLTag.HTTP_REQUEST_CONNECTION;

public class MuleConfigReader {

    public static ParseResult readMuleConfigFromRoot(Context ctx, MuleXMLNavigator muleXMLNavigator,
                                                     String xmlFilePath) {
        Element root;
        try {
            root = parseMuleXMLConfigurationFile(xmlFilePath);
        } catch (Exception e) {
            throw new RuntimeException("Error while parsing the mule XML configuration file: ", e);
        }

        MuleElement muleElement = muleXMLNavigator.createRootMuleElement(root);
        List<Flow> flows = new ArrayList<>();
        List<SubFlow> subFlows = new ArrayList<>();

        while (muleElement.peekChild() != null) {
            MuleElement child = muleElement.consumeChild();
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

        return new ParseResult(flows, subFlows);
    }

    private static Element parseMuleXMLConfigurationFile(String uri) throws ParserConfigurationException, SAXException,
            IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(uri);
        document.getDocumentElement().normalize();
        return document.getDocumentElement();
    }

    public static void readGlobalConfigElement(Context ctx, MuleElement muleElement) {
        String elementTagName = muleElement.getElement().getTagName();
        if (MuleXMLTag.APIKIT_CONFIG.tag().equals(elementTagName)) {
            ApiKitConfig apiKitConfig = readApiKitConfig(ctx, muleElement);
            ctx.currentFileCtx.configs.apiKitConfigs.put(apiKitConfig.name(), apiKitConfig);
        } else if (MuleXMLTag.HTTP_LISTENER_CONFIG.tag().equals(elementTagName)) {
            HTTPListenerConfig httpListenerConfig = readHttpListenerConfig(ctx, muleElement);
            ctx.currentFileCtx.configs.httpListenerConfigs.put(httpListenerConfig.name(), httpListenerConfig);
        } else if (MuleXMLTag.HTTP_REQUEST_CONFIG.tag().equals(elementTagName)) {
            HTTPRequestConfig httpRequestConfig = readHttpRequestConfig(ctx, muleElement);
            ctx.currentFileCtx.configs.httpRequestConfigs.put(httpRequestConfig.name(), httpRequestConfig);
        } else if (MuleXMLTag.DB_CONFIG.tag().equals(elementTagName)) {
            DbConfig dbConfig = readDbConfig(ctx, muleElement);
            if (dbConfig != null) {
                ctx.currentFileCtx.configs.dbConfigs.put(dbConfig.name(), dbConfig);
            }
        } else if (MuleXMLTag.ERROR_HANDLER.tag().equals(elementTagName)) {
            ErrorHandler errorHandler = readErrorHandler(ctx, muleElement);
            ctx.currentFileCtx.configs.globalErrorHandlers.add(errorHandler);
        } else if (MuleXMLTag.VM_CONFIG.tag().equals(elementTagName)) {
            VMConfig vmConfig = readVmConfig(ctx, muleElement);
            // TODO: Revisit how we can use this
        } else if (MuleXMLTag.ANYPOINT_MQ_CONFIG.tag().equals(elementTagName)) {
            AnypointMqConfig anypointMqConfig = readAnypointMqConfig(ctx, muleElement);
            ctx.currentFileCtx.configs.anypointMqConfigs.put(anypointMqConfig.name(), anypointMqConfig);
        } else if (MuleXMLTag.PUBSUB_CONFIG.tag().equals(elementTagName)) {
            PubSubConfig pubSubConfig = readPubSubConfig(ctx, muleElement);
            ctx.currentFileCtx.configs.pubSubConfigs.put(pubSubConfig.name(), pubSubConfig);
        } else if (MuleXMLTag.CONFIGURATION_PROPERTIES.tag().equals(elementTagName)) {
            // Ignore as we automatically add all .yaml and .properties to config.toml
        } else if (MuleXMLTag.GLOBAL_PROPERTY.tag().equals(elementTagName)) {
            GlobalProperty globalProperty = readGlobalProperty(ctx, muleElement);
            ctx.currentFileCtx.configs.globalProperties.add(globalProperty);
        } else if (MuleXMLTag.IMPORT.tag().equals(elementTagName)) {
            MuleImport muleImport = readImport(ctx, muleElement);
            ctx.currentFileCtx.configs.imports.add(muleImport);
        } else {
            UnsupportedBlock unsupportedBlock = readUnsupportedBlock(ctx, muleElement);
            ctx.currentFileCtx.configs.unsupportedBlocks.add(unsupportedBlock);
        }
    }

    public static MuleRecord readBlock(Context ctx, MuleElement muleElement) {
        MuleXMLTag muleXMLTag = MuleXMLTag.fromTag(muleElement.getElement().getTagName());
        switch (muleXMLTag) {
            // Source
            case MuleXMLTag.HTTP_LISTENER -> {
                return readHttpListener(ctx, muleElement);
            }
            case MuleXMLTag.VM_LISTENER -> {
                return readVMListener(ctx, muleElement);
            }
            case MuleXMLTag.SCHEDULER -> {
                return readScheduler(ctx, muleElement);
            }
            case MuleXMLTag.ANYPOINT_MQ_SUBSCRIBER -> {
                return readAnypointMqSubscriber(ctx, muleElement);
            }
            case MuleXMLTag.PUBSUB_MESSAGE_LISTENER -> {
                return readPubSubMessageListener(ctx, muleElement);
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
            case MuleXMLTag.TRY -> {
                return readTry(ctx, muleElement);
            }
            case MuleXMLTag.FOREACH -> {
                return readForeach(ctx, muleElement);
            }
            case MuleXMLTag.SCATTER_GATHER -> {
                return readScatterGather(ctx, muleElement);
            }
            case MuleXMLTag.FIRST_SUCCESSFUL -> {
                return readFirstSuccessful(ctx, muleElement);
            }
            case MuleXMLTag.ERROR_HANDLER -> {
                return readErrorHandler(ctx, muleElement);
            }
            case MuleXMLTag.ON_ERROR_CONTINUE -> {
                return readOnErrorContinue(ctx, muleElement);
            }
            case MuleXMLTag.ON_ERROR_PROPAGATE -> {
                return readOnErrorPropagate(ctx, muleElement);
            }
            case MuleXMLTag.RAISE_ERROR -> {
                return readRaiseError(ctx, muleElement);
            }
            case MuleXMLTag.VM_PUBLISH -> {
                return readVMPublish(ctx, muleElement);
            }
            case MuleXMLTag.VM_CONSUME -> {
                return readVMConsume(ctx, muleElement);
            }
            case MuleXMLTag.APIKIT_ROUTER -> {
                return readApiKitRouter(ctx, muleElement);
            }
            default -> {
                return readUnsupportedBlock(ctx, muleElement);
            }
        }
    }

    // Components
    private static Logger readLogger(Context ctx, MuleElement muleElement) {
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

    private static Scheduler readScheduler(Context ctx, MuleElement muleElement) {
        ctx.addImport(new Import(Constants.ORG_BALLERINA, Constants.MODULE_TASK));

        String frequency = "5";
        String timeUnit = "SECONDS";
        String startDelay = "";

        while (muleElement.peekChild() != null) {
            MuleElement child = muleElement.consumeChild();
            Element childElement = child.getElement();
            if (childElement.getTagName().equals(MuleXMLTag.SCHEDULING_STRATEGY.tag())) {
                while (child.peekChild() != null) {
                    MuleElement strategyChild = child.consumeChild();
                    Element strategyElement = strategyChild.getElement();
                    if (strategyElement.getTagName().equals(MuleXMLTag.FIXED_FREQUENCY.tag())) {
                        frequency = strategyElement.getAttribute("frequency");
                        timeUnit = strategyElement.getAttribute("timeUnit");
                        startDelay = strategyElement.getAttribute("startDelay");
                    }
                }
            }
        }

        if (frequency.isEmpty()) {
            frequency = "5";
        }
        if (timeUnit.isEmpty()) {
            timeUnit = "MILLISECONDS";
        }

        return new Scheduler(frequency, timeUnit, startDelay);
    }

    private static AnypointMqSubscriber readAnypointMqSubscriber(Context ctx, MuleElement muleElement) {
        Element element = muleElement.getElement();
        String configRef = element.getAttribute("config-ref");
        String destination = element.getAttribute("destination");
        return new AnypointMqSubscriber(configRef, destination);
    }

    private static PubSubMessageListener readPubSubMessageListener(Context ctx, MuleElement muleElement) {
        Element element = muleElement.getElement();
        String configRef = element.getAttribute("config-ref");
        String projectId = element.getAttribute("projectId");
        String subscriptionName = element.getAttribute("subscriptionName");
        return new PubSubMessageListener(configRef, projectId, subscriptionName);
    }

    private static MuleRecord readExpressionComponent(Context ctx, MuleElement muleElement) {
        return new ExpressionComponent(muleElement.getElement().getTextContent());
    }

    // Flow Control
    private static Choice readChoice(Context ctx, MuleElement muleElement) {
        List<WhenInChoice> whens = new ArrayList<>();
        List<MuleRecord> otherwiseProcess = new ArrayList<>();
        while (muleElement.peekChild() != null) {
            MuleElement child = muleElement.consumeChild();
            Element childElement = child.getElement();
            if (childElement.getTagName().equals(MuleXMLTag.WHEN.tag())) {
                String condition = childElement.getAttribute("expression");
                List<MuleRecord> whenProcess = new ArrayList<>();

                while (child.peekChild() != null) {
                    MuleElement whenChild = child.consumeChild();
                    MuleRecord r = readBlock(ctx, whenChild);
                    whenProcess.add(r);
                }

                WhenInChoice whenInChoice = new WhenInChoice(condition, whenProcess);
                whens.add(whenInChoice);
            } else {
                assert childElement.getTagName().equals(MuleXMLTag.OTHERWISE.tag());
                assert otherwiseProcess.isEmpty();
                while (child.peekChild() != null) {
                    MuleElement otherwiseChild = child.consumeChild();
                    MuleRecord r = readBlock(ctx, otherwiseChild);
                    otherwiseProcess.add(r);
                }
            }
        }
        return new Choice(whens, otherwiseProcess);
    }

    // ApiKit Flow Helpers
    private static boolean isApiKitFlowPattern(String flowName) {
        return flowName.matches("^(get|post|put|delete|patch|head|options|GET|POST|PUT|DELETE|PATCH|HEAD|OPTIONS)" +
                ":.*:.*$");
    }

    private static Optional<MuleRecord> createApiKitSource(Context ctx, String flowName) {
        ApiKitConfig.HTTPResourceData resourceData = ApiKitConfig.parseApiKitFlowName(flowName);
        return Optional.ofNullable(ctx.projectCtx.getApiKitConfig(resourceData.configName()));
    }

    // Scopes
    public static Flow readFlow(Context ctx, MuleElement mFlowElement) {
        Element flowElement = mFlowElement.getElement();
        String flowName = flowElement.getAttribute("name");

        MuleRecord source = null;
        List<MuleRecord> flowBlocks = new ArrayList<>();

        while (mFlowElement.peekChild() != null) {
            MuleElement child = mFlowElement.consumeChild();
            Element element = child.getElement();
            if (element.getTagName().equals(MuleXMLTag.HTTP_LISTENER.tag())) {
                assert source == null;
                source = readBlock(ctx, child);
            } else if (element.getTagName().equals(MuleXMLTag.VM_LISTENER.tag())) {
                assert source == null;
                source = readBlock(ctx, child);
            } else if (element.getTagName().equals(MuleXMLTag.SCHEDULER.tag())) {
                assert source == null;
                source = readBlock(ctx, child);
            } else if (element.getTagName().equals(MuleXMLTag.ANYPOINT_MQ_SUBSCRIBER.tag())) {
                assert source == null;
                source = readBlock(ctx, child);
            } else if (element.getTagName().equals(MuleXMLTag.PUBSUB_MESSAGE_LISTENER.tag())) {
                assert source == null;
                source = readBlock(ctx, child);
            } else {
                MuleRecord muleRec = readBlock(ctx, child);
                flowBlocks.add(muleRec);
            }
        }

        MuleRecord finalSource = source;
        Supplier<Optional<MuleRecord>> sourceSupplier;

        if (finalSource != null) {
            sourceSupplier = () -> Optional.of(finalSource);
        } else if (isApiKitFlowPattern(flowName)) {
            sourceSupplier = () -> createApiKitSource(ctx, flowName);
        } else {
            sourceSupplier = Optional::empty;
        }

        return new Flow(flowName, sourceSupplier, flowBlocks);
    }

    public static SubFlow readSubFlow(Context ctx, MuleElement mFlowElement) {
        Element flowElement = mFlowElement.getElement();
        String flowName = flowElement.getAttribute("name");

        List<MuleRecord> flowBlocks = new ArrayList<>();
        while (mFlowElement.peekChild() != null) {
            MuleElement muleElement = mFlowElement.consumeChild();
            MuleRecord muleRec = readBlock(ctx, muleElement);
            flowBlocks.add(muleRec);
        }

        return new SubFlow(flowName, flowBlocks);
    }

    private static Enricher readEnricher(Context ctx, MuleElement muleElement) {
        Element element = muleElement.getElement();
        String source = element.getAttribute("source");
        String target = element.getAttribute("target");

        MuleRecord block = null;
        while (muleElement.peekChild() != null) {
            MuleElement muleChild = muleElement.consumeChild();
            assert block == null;
            block = readBlock(ctx, muleChild);
        }

        Optional<MuleRecord> innerBlock = block != null ? Optional.of(block) : Optional.empty();
        return new Enricher(source, target, innerBlock);
    }

    private static Async readAsync(Context ctx, MuleElement muleElement) {
        List<MuleRecord> flowBlocks = new ArrayList<>();
        while (muleElement.peekChild() != null) {
            MuleElement child = muleElement.consumeChild();
            MuleRecord muleRec = readBlock(ctx, child);
            flowBlocks.add(muleRec);
        }

        return new Async(flowBlocks);
    }

    private static Try readTry(Context ctx, MuleElement muleElement) {
        List<MuleRecord> flowBlocks = new ArrayList<>();
        while (muleElement.peekChild() != null) {
            MuleElement child = muleElement.consumeChild();
            MuleRecord muleRec = readBlock(ctx, child);
            flowBlocks.add(muleRec);
        }

        return new Try(flowBlocks);
    }

    private static Foreach readForeach(Context ctx, MuleElement muleElement) {
        Element element = muleElement.getElement();
        String collection = element.getAttribute("collection");

        // Default collection is payload if not specified
        if (collection.isEmpty()) {
            collection = "#[payload]";
        }

        List<MuleRecord> flowBlocks = new ArrayList<>();
        while (muleElement.peekChild() != null) {
            MuleElement child = muleElement.consumeChild();
            MuleRecord muleRec = readBlock(ctx, child);
            flowBlocks.add(muleRec);
        }

        return new Foreach(collection, flowBlocks);
    }

    private static ScatterGather readScatterGather(Context ctx, MuleElement muleElement) {
        List<Route> routes = new ArrayList<>();
        while (muleElement.peekChild() != null) {
            MuleElement child = muleElement.consumeChild();
            Element childElement = child.getElement();
            if (childElement.getTagName().equals(MuleXMLTag.ROUTE.tag())) {
                Route route = readRoute(ctx, child);
                routes.add(route);
            } else {
                throw new UnsupportedOperationException("Unsupported scatter-gather child: " +
                        childElement.getTagName());
            }
        }
        return new ScatterGather(routes);
    }

    private static FirstSuccessful readFirstSuccessful(Context ctx, MuleElement muleElement) {
        List<Route> routes = new ArrayList<>();
        while (muleElement.peekChild() != null) {
            MuleElement child = muleElement.consumeChild();
            Element childElement = child.getElement();
            if (childElement.getTagName().equals(MuleXMLTag.ROUTE.tag())) {
                Route route = readRoute(ctx, child);
                routes.add(route);
            } else {
                throw new UnsupportedOperationException("Unsupported first-successful child: " +
                        childElement.getTagName());
            }
        }
        return new FirstSuccessful(routes);
    }

    private static Route readRoute(Context ctx, MuleElement muleElement) {
        List<MuleRecord> flowBlocks = new ArrayList<>();
        while (muleElement.peekChild() != null) {
            MuleElement child = muleElement.consumeChild();
            MuleRecord muleRec = readBlock(ctx, child);
            flowBlocks.add(muleRec);
        }
        return new Route(flowBlocks);
    }

    // Transformers
    private static Payload readSetPayload(Context ctx, MuleElement muleElement) {
        Element element = muleElement.getElement();
        String muleExpr = element.getAttribute("value");
        String mimeType = element.getAttribute("mimeType");
        return new Payload(muleExpr, mimeType);
    }

    private static SetVariable readSetVariable(Context ctx, MuleElement muleElement) {
        Element element = muleElement.getElement();
        String varName = element.getAttribute("variableName");
        String val = element.getAttribute("value");
        return new SetVariable(varName, val);
    }

    private static RemoveVariable readRemoveVariable(Context ctx, MuleElement muleElement) {
        Element element = muleElement.getElement();
        String varName = element.getAttribute("variableName");
        return new RemoveVariable(Kind.REMOVE_VARIABLE, varName);
    }

    private static RemoveVariable readRemoveSessionVariable(Context ctx, MuleElement muleElement) {
        Element element = muleElement.getElement();
        String varName = element.getAttribute("variableName");
        return new RemoveVariable(Kind.REMOVE_SESSION_VARIABLE, varName);
    }

    private static ObjectToJson readObjectToJson(Context ctx, MuleElement muleElement) {
        return new ObjectToJson();
    }

    private static ObjectToString readObjectToString(Context ctx, MuleElement muleElement) {
        return new ObjectToString();
    }

    // Error handling
    private static ErrorHandler readErrorHandler(Context ctx, MuleElement muleElement) {
        Element element = muleElement.getElement();
        String name = element.getAttribute("name");
        String ref = element.getAttribute("ref");

        List<ErrorHandlerRecord> errorHandlers = new ArrayList<>();
        while (muleElement.peekChild() != null) {
            MuleElement child = muleElement.consumeChild();
            ErrorHandlerRecord muleRec = (ErrorHandlerRecord) readBlock(ctx, child);
            errorHandlers.add(muleRec);
        }
        return new ErrorHandler(name, ref, errorHandlers);
    }

    private static OnErrorContinue readOnErrorContinue(Context ctx, MuleElement muleElement) {
        Element element = muleElement.getElement();
        String type = element.getAttribute("type");
        String when = element.getAttribute("when");
        String enableNotifications = element.getAttribute("enableNotifications");
        String logException = element.getAttribute("logException");

        List<MuleRecord> errorBlocks = new ArrayList<>();
        while (muleElement.peekChild() != null) {
            MuleElement child = muleElement.consumeChild();
            MuleRecord muleRec = readBlock(ctx, child);
            errorBlocks.add(muleRec);
        }

        return new OnErrorContinue(errorBlocks, type, when, enableNotifications, logException);
    }

    private static OnErrorPropagate readOnErrorPropagate(Context ctx, MuleElement muleElement) {
        Element element = muleElement.getElement();
        String type = element.getAttribute("type");
        String when = element.getAttribute("when");
        String enableNotifications = element.getAttribute("enableNotifications");
        String logException = element.getAttribute("logException");

        List<MuleRecord> errorBlocks = new ArrayList<>();
        while (muleElement.peekChild() != null) {
            MuleElement child = muleElement.consumeChild();
            MuleRecord muleRec = readBlock(ctx, child);
            errorBlocks.add(muleRec);
        }

        return new OnErrorPropagate(errorBlocks, type, when, enableNotifications, logException);
    }

    private static RaiseError readRaiseError(Context ctx, MuleElement muleElement) {
        Element element = muleElement.getElement();
        String type = element.getAttribute("type");
        String description = element.getAttribute("description");
        return new RaiseError(type, description);
    }

    // HTTP Module
    private static HttpListener readHttpListener(Context ctx, MuleElement muleElement) {
        Element element = muleElement.getElement();
        String configRef = element.getAttribute("config-ref");
        String resourcePath = element.getAttribute("path");
        String[] allowedMethods = Arrays.stream(getAllowedMethods(element.getAttribute("allowedMethods")))
                .map(String::toLowerCase).toArray(String[]::new);
        return new HttpListener(configRef, resourcePath, allowedMethods);
    }

    private static HttpRequest readHttpRequest(Context ctx, MuleElement muleElement) {
        Element element = muleElement.getElement();
        String configRef = element.getAttribute("config-ref");
        String urlAttribute = element.getAttribute("url");

        // Create a supplier that lazily resolves the URL
        Supplier<String> urlSupplier;
        if (!urlAttribute.isEmpty()) {
            // If url is provided directly, return it as-is
            urlSupplier = () -> urlAttribute;
        } else {
            // If url is empty, create a supplier that resolves config and builds URL
            urlSupplier = () -> {
                HTTPRequestConfig httpRequestConfig = ctx.projectCtx.getHttpRequestConfig(configRef);
                if (httpRequestConfig == null) {
                    throw new IllegalStateException("HTTPRequestConfig not found for config-ref: " + configRef);
                }
                String host = httpRequestConfig.host();
                String port = httpRequestConfig.port();
                String url = String.format("%s:%s", host, port);

                String protocol = httpRequestConfig.protocol();
                if (!protocol.isEmpty()) {
                    url = protocol.toLowerCase() + "://" + url;
                }
                return url;
            };
        }

        String method = element.getAttribute("method").toLowerCase();
        String path = element.getAttribute("path");

        Map<String, String> queryParams = new HashMap<>();
        Optional<String> headersScript = Optional.empty();
        Optional<String> uriParamsScript = Optional.empty();
        Optional<String> queryParamsScript = Optional.empty();
        List<UnsupportedBlock> unsupportedBlocks = new ArrayList<>();

        // Check for inline DataWeave script (CDATA) in http:request element
        String textContent = element.getTextContent();
        if (textContent != null && !textContent.trim().isEmpty() && textContent.trim().startsWith("#[")) {
            headersScript = Optional.of(textContent.trim());
        }

        while (muleElement.peekChild() != null) {
            MuleElement child = muleElement.consumeChild();
            String tagName = child.getElement().getTagName();
            if (tagName.equals(MuleXMLTag.HTTP_REQEUST_BUILDER.tag())) {
                processQueryParams(queryParams, child);
            } else if (tagName.equals(MuleXMLTag.HTTP_HEADERS.tag())) {
                String script = child.getElement().getTextContent();
                if (script != null && !script.trim().isEmpty()) {
                    headersScript = Optional.of(script.trim());
                }
            } else if (tagName.equals(MuleXMLTag.HTTP_URI_PARAMS.tag())) {
                String script = child.getElement().getTextContent();
                if (script != null && !script.trim().isEmpty()) {
                    uriParamsScript = Optional.of(script.trim());
                }
            } else if (tagName.equals(MuleXMLTag.HTTP_QUERY_PARAMS.tag())) {
                String script = child.getElement().getTextContent();
                if (script != null && !script.trim().isEmpty()) {
                    queryParamsScript = Optional.of(script.trim());
                }
            } else {
                UnsupportedBlock unsupportedBlock = readUnsupportedBlock(ctx, child);
                unsupportedBlocks.add(unsupportedBlock);
            }
        }

        return new HttpRequest(configRef, method, urlSupplier, path, queryParams, headersScript, uriParamsScript,
                queryParamsScript, unsupportedBlocks);
    }

    private static void processQueryParams(Map<String, String> queryParams, MuleElement muleElement) {
        while (muleElement.peekChild() != null) {
            MuleElement child = muleElement.consumeChild();
            Element element = child.getElement();
            assert element.getTagName().equals(MuleXMLTag.HTTP_QUERY_PARAM.tag());
            String paramName = element.getAttribute("paramName");
            String value = element.getAttribute("value");
            queryParams.put(paramName, value);
        }
    }

    private static FlowReference readFlowReference(Context ctx, MuleElement muleElement) {
        Element element = muleElement.getElement();
        String flowName = element.getAttribute("name");
        return new FlowReference(flowName);
    }

    // VM Connector
    private static VMListener readVMListener(Context ctx, MuleElement muleElement) {
        Element element = muleElement.getElement();
        String configRef = element.getAttribute("config-ref");
        String queueName = element.getAttribute("queueName");
        return new VMListener(configRef, queueName);
    }

    private static VMPublish readVMPublish(Context ctx, MuleElement muleElement) {
        Element element = muleElement.getElement();
        String configRef = element.getAttribute("config-ref");
        String queueName = element.getAttribute("queueName");
        return new VMPublish(configRef, queueName);
    }

    private static VMConsume readVMConsume(Context ctx, MuleElement muleElement) {
        Element element = muleElement.getElement();
        String configRef = element.getAttribute("config-ref");
        String queueName = element.getAttribute("queueName");
        return new VMConsume(configRef, queueName);
    }

    // Database Connector
    private static Database readDatabase(Context ctx, MuleElement muleElement) {
        Element element = muleElement.getElement();
        String configRef = element.getAttribute("config-ref");

        String query = null;
        List<UnsupportedBlock> unsupportedBlocks = new ArrayList<>();
        while (muleElement.peekChild() != null) {
            MuleElement child = muleElement.consumeChild();
            Element childElement = child.getElement();
            if (childElement.getTagName().equals(MuleXMLTag.DB_SQL.tag())) {
                query = childElement.getTextContent();
            } else {
                // e.g db:input-parameters
                UnsupportedBlock unsupportedBlock = readUnsupportedBlock(ctx, child);
                unsupportedBlocks.add(unsupportedBlock);
            }
        }

        if (query == null) {
            throw new IllegalStateException("No query found in the database block");
        }

        MuleXMLTag muleXMLTag = MuleXMLTag.fromTag(element.getTagName());
        Kind kind = switch (muleXMLTag) {
            case MuleXMLTag.DB_INSERT -> Kind.DB_INSERT;
            case MuleXMLTag.DB_SELECT -> Kind.DB_SELECT;
            case MuleXMLTag.DB_UPDATE -> Kind.DB_UPDATE;
            case MuleXMLTag.DB_DELETE -> Kind.DB_DELETE;
            default -> throw new UnsupportedOperationException();
        };

        return new Database(kind, configRef, query, unsupportedBlocks);
    }

    private static MuleImport readImport(Context ctx, MuleElement muleElement) {
        Element element = muleElement.getElement();
        String file = element.getAttribute("file");
        return new MuleImport(file);
    }

    private static UnsupportedBlock readUnsupportedBlock(Context ctx, MuleElement muleElement) {
        Element element = muleElement.getElement();
        String xmlBlock = ConversionUtils.elementToString(element);
        return new UnsupportedBlock(xmlBlock);
    }

    // Global Elements
    private static ApiKitConfig readApiKitConfig(Context ctx, MuleElement muleElement) {
        Element element = muleElement.getElement();
        String name = element.getAttribute("name");
        String api = element.getAttribute("api");
        return new ApiKitConfig(name, api);
    }

    private static ApiKitRouter readApiKitRouter(Context ctx, MuleElement muleElement) {
        Element element = muleElement.getElement();
        String configRef = element.getAttribute("config-ref");
        return new ApiKitRouter(configRef);
    }

    private static HTTPListenerConfig readHttpListenerConfig(Context ctx, MuleElement muleElement) {
        Element element = muleElement.getElement();
        ctx.addImport(new Import(Constants.ORG_BALLERINA, Constants.MODULE_HTTP));
        String listenerName = element.getAttribute("name");
        String basePath = element.getAttribute("basePath");

        // For Mule 4.x, host and port are within a nested http:listener-connection element
        String host = "0.0.0.0"; // Default value
        String port = "8080";    // Default value

        while (muleElement.peekChild() != null) {
            MuleElement child = muleElement.consumeChild();
            Element childElement = child.getElement();
            if (childElement.getTagName().equals(HTTP_LISTENER_CONNECTION.tag())) {
                host = childElement.getAttribute("host");
                port = childElement.getAttribute("port");
            }
        }

        return new HTTPListenerConfig(listenerName, basePath, port, host);
    }

    private static HTTPRequestConfig readHttpRequestConfig(Context ctx, MuleElement muleElement) {
        Element element = muleElement.getElement();
        ctx.addImport(new Import(Constants.ORG_BALLERINA, Constants.MODULE_HTTP));
        String configName = element.getAttribute("name");

        // For Mule 4.x, host, port, protocol are within a nested http:request-connection element
        String host = "";
        String port = "";
        String protocol = "";

        while (muleElement.peekChild() != null) {
            MuleElement child = muleElement.consumeChild();
            Element childElement = child.getElement();
            if (childElement.getTagName().equals(HTTP_REQUEST_CONNECTION.tag())) {
                host = childElement.getAttribute("host");
                port = childElement.getAttribute("port");
                protocol = childElement.getAttribute("protocol");
            }
        }
        return new HTTPRequestConfig(configName, host, port, protocol);
    }

    private static VMConfig readVmConfig(Context ctx, MuleElement muleElement) {
        Element element = muleElement.getElement();
        String name = element.getAttribute("name");

        List<VMQueue> queues = new ArrayList<>();
        while (muleElement.peekChild() != null) {
            MuleElement child = muleElement.consumeChild();
            Element childElement = child.getElement();
            if (childElement.getTagName().equals(MuleXMLTag.VM_QUEUES.tag())) {
                queues.addAll(readVmQueues(child));
            }
        }

        return new VMConfig(name, queues);
    }

    private static List<VMQueue> readVmQueues(MuleElement muleElement) {
        List<VMQueue> queues = new ArrayList<>();
        while (muleElement.peekChild() != null) {
            MuleElement child = muleElement.consumeChild();
            Element childElement = child.getElement();
            if (childElement.getTagName().equals(MuleXMLTag.VM_QUEUE.tag())) {
                String queueName = childElement.getAttribute("queueName");
                String queueType = childElement.getAttribute("queueType");
                queues.add(new VMQueue(queueName, queueType));
            }
        }
        return queues;
    }

    private static AnypointMqConfig readAnypointMqConfig(Context ctx, MuleElement muleElement) {
        Element element = muleElement.getElement();
        String name = element.getAttribute("name");
        // Consume child elements (like anypoint-mq:connection) without processing them
        while (muleElement.peekChild() != null) {
            muleElement.consumeChild();
        }
        return new AnypointMqConfig(name);
    }

    private static PubSubConfig readPubSubConfig(Context ctx, MuleElement muleElement) {
        Element element = muleElement.getElement();
        String name = element.getAttribute("name");
        // Consume child elements (like pubsub:connection, pubsub:private-key) without processing them
        while (muleElement.peekChild() != null) {
            muleElement.consumeChild();
        }
        return new PubSubConfig(name);
    }

    private static GlobalProperty readGlobalProperty(Context ctx, MuleElement muleElement) {
        Element element = muleElement.getElement();
        String name = element.getAttribute("name");
        String value = element.getAttribute("value");
        return new GlobalProperty(name, value);
    }

    private static DbConfig readDbConfig(Context ctx, MuleElement muleElement) {
        Element element = muleElement.getElement();
        String name = element.getAttribute("name");

        DbConnection connection;
        if (muleElement.peekChild() != null) {
            MuleElement child = muleElement.consumeChild();
            Element childElement = child.getElement();
            if (childElement.getTagName().equals(DB_MY_SQL_CONNECTION.tag())) {
                connection = readDbMySqlConnection(ctx, childElement, name);
                return new DbConfig(name, connection);
            } else if (childElement.getTagName().equals(DB_ORACLE_CONNECTION.tag())) {
                connection = readDbOracleConnection(ctx, childElement, name);
                return new DbConfig(name, connection);
            } else if (childElement.getTagName().equals(DB_GENERIC_CONNECTION.tag())) {
                connection = readDbGenericConnection(ctx, childElement, name);
                return new DbConfig(name, connection);
            } else {
                UnsupportedBlock unsupportedBlock = readUnsupportedBlock(ctx, child);
                ctx.currentFileCtx.configs.unsupportedBlocks.add(unsupportedBlock);
                return null;
            }
        }
        // invalid syntax
        return null;
    }

    private static DbMySqlConnection readDbMySqlConnection(Context ctx, Element childElement, String name) {
        ctx.addImport(new Import(Constants.ORG_BALLERINAX, Constants.MODULE_MYSQL));
        ctx.addImport(new Import(Constants.ORG_BALLERINAX, Constants.MODULE_MYSQL_DRIVER, Optional.of("_")));
        String host = childElement.getAttribute("host");
        String port = childElement.getAttribute("port");
        String user = childElement.getAttribute("user");
        String password = childElement.getAttribute("password");
        String database = childElement.getAttribute("database");
        return new DbMySqlConnection(name, host, port, user, password, database);
    }

    private static DbOracleConnection readDbOracleConnection(Context ctx, Element childElement, String name) {
        ctx.addImport(new Import(Constants.ORG_BALLERINAX, Constants.MODULE_ORACLEDB));
        ctx.addImport(new Import(Constants.ORG_BALLERINAX, Constants.MODULE_ORACLEDB_DRIVER, Optional.of("_")));
        String host = childElement.getAttribute("host");
        String port = childElement.getAttribute("port");
        String user = childElement.getAttribute("user");
        String password = childElement.getAttribute("password");
        String instance = childElement.getAttribute("instance");
        String serviceName = childElement.getAttribute("serviceName");
        return new DbOracleConnection(name, host, port, user, password, instance, serviceName);
    }

    private static DbGenericConnection readDbGenericConnection(Context ctx, Element childElement, String name) {
        ctx.addImport(new Import(Constants.ORG_BALLERINAX, Constants.MODULE_JAVA_JDBC));
        String url = childElement.getAttribute("url");
        String user = childElement.getAttribute("user");
        String password = childElement.getAttribute("password");
        return new DbGenericConnection(name, url, user, password);
    }

    private static TransformMessage readTransformMessage(Context ctx, MuleElement muleElement) {
        List<TransformMessageElement> transformMessageElements = new ArrayList<>();
        while (muleElement.peekChild() != null) {
            MuleElement child = muleElement.consumeChild();
            Element element = child.getElement();
            MuleXMLTag muleXMLTag = MuleXMLTag.fromTag(element.getTagName());
            switch (muleXMLTag) {
                case MuleXMLTag.EE_MESSAGE -> {
                    processTransformMessageChildren(child, transformMessageElements);
                }
                case MuleXMLTag.EE_SET_PAYLOAD -> {
                    processSetPayloadElement(element, transformMessageElements);
                }
                case MuleXMLTag.EE_VARIABLES -> {
                    processVariablesElement(child, transformMessageElements);
                }
                default -> throw new UnsupportedOperationException("Unsupported ee:transform child: " + muleXMLTag);
            }
        }
        return new TransformMessage(transformMessageElements);
    }

    private static void processTransformMessageChildren(MuleElement parent, List<TransformMessageElement> elements) {
        while (parent.peekChild() != null) {
            MuleElement child = parent.consumeChild();
            Element element = child.getElement();
            MuleXMLTag tag = MuleXMLTag.fromTag(element.getTagName());
            switch (tag) {
                case MuleXMLTag.EE_SET_PAYLOAD -> {
                    processSetPayloadElement(element, elements);
                }
                case MuleXMLTag.EE_VARIABLES -> {
                    processVariablesElement(child, elements);
                }
                default -> throw new UnsupportedOperationException("Unsupported ee:message child: " + tag);
            }
        }
    }

    private static void processSetPayloadElement(Element element, List<TransformMessageElement> elements) {
        String resource = element.getAttribute("resource");
        if (resource.isEmpty()) {
            elements.add(new SetPayloadElement(null, element.getTextContent()));
        } else {
            elements.add(new SetPayloadElement(resource, null));
        }
    }

    private static void processVariablesElement(MuleElement parent, List<TransformMessageElement> elements) {
        while (parent.peekChild() != null) {
            MuleElement child = parent.consumeChild();
            Element element = child.getElement();
            if (MuleXMLTag.fromTag(element.getTagName()) == MuleXMLTag.EE_SET_VARIABLE) {
                String variableName = element.getAttribute("variableName");
                String resource = element.getAttribute("resource");
                String script = null;
                if (resource.isEmpty()) {
                    script = element.getTextContent();
                }
                elements.add(new SetVariableElement(resource, script, variableName));
            }
        }
    }
}
