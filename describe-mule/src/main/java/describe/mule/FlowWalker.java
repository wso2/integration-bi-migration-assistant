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
package describe.mule;

import describe.model.DescriptionModel.Branch;
import describe.model.DescriptionModel.Calls;
import describe.model.DescriptionModel.DataWeave;
import describe.model.DescriptionModel.ErrorHandler;
import describe.model.DescriptionModel.FlaggedElement;
import describe.model.DescriptionModel.Flow;
import describe.model.DescriptionModel.FlowKind;
import describe.model.DescriptionModel.Handling;
import describe.model.DescriptionModel.RaisedError;
import describe.model.DescriptionModel.Schedule;
import describe.model.DescriptionModel.SourceRef;
import describe.model.DescriptionModel.Step;
import describe.model.DescriptionModel.StepCoverage;
import describe.model.DescriptionModel.StepInfo;
import describe.model.DescriptionModel.StepType;
import describe.model.DescriptionModel.SystemRef;
import describe.model.DescriptionModel.Trigger;
import mule.v4.Context;
import mule.v4.Context.SourceInfo;
import mule.v4.model.MuleModel;
import mule.v4.model.MuleModel.AnypointMqPublish;
import mule.v4.model.MuleModel.AnypointMqSubscriber;
import mule.v4.model.MuleModel.ApiKitConfig;
import mule.v4.model.MuleModel.Async;
import mule.v4.model.MuleModel.Choice;
import mule.v4.model.MuleModel.Database;
import mule.v4.model.MuleModel.ErrorHandlerRecord;
import mule.v4.model.MuleModel.FileListener;
import mule.v4.model.MuleModel.FirstSuccessful;
import mule.v4.model.MuleModel.FlowReference;
import mule.v4.model.MuleModel.Foreach;
import mule.v4.model.MuleModel.HttpListener;
import mule.v4.model.MuleModel.HttpRequest;
import mule.v4.model.MuleModel.MuleRecord;
import mule.v4.model.MuleModel.OnErrorContinue;
import mule.v4.model.MuleModel.OnErrorPropagate;
import mule.v4.model.MuleModel.Payload;
import mule.v4.model.MuleModel.PubSubMessageListener;
import mule.v4.model.MuleModel.RaiseError;
import mule.v4.model.MuleModel.Route;
import mule.v4.model.MuleModel.ScatterGather;
import mule.v4.model.MuleModel.SetPayloadElement;
import mule.v4.model.MuleModel.SetVariableElement;
import mule.v4.model.MuleModel.SubFlow;
import mule.v4.model.MuleModel.TransformMessage;
import mule.v4.model.MuleModel.TransformMessageElement;
import mule.v4.model.MuleModel.Try;
import mule.v4.model.MuleModel.UnsupportedBlock;
import mule.v4.model.MuleModel.VMConsume;
import mule.v4.model.MuleModel.VMListener;
import mule.v4.model.MuleModel.VMPublish;
import mule.v4.model.MuleModel.WhenInChoice;
import org.w3c.dom.Element;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Turns Mule flows and their blocks into description flows and steps, using the adapter table in
 * {@code docs/describe-poc/implementation-notes.md}. It also counts coverage as it goes.
 */
final class FlowWalker {

    private final Context ctx;
    private final ConversionRecorder recorder;
    private final BallerinaLinker linker;
    private final Path projectDir;
    private final Map<String, String> flowIds;
    private final List<FlaggedElement> flaggedElements = new ArrayList<>();
    private int described;
    private int hidden;
    private int flagged;

    FlowWalker(Context ctx, ConversionRecorder recorder, BallerinaLinker linker, Path projectDir,
               Map<String, String> flowIds) {
        this.ctx = ctx;
        this.recorder = recorder;
        this.linker = linker;
        this.projectDir = projectDir;
        this.flowIds = flowIds;
    }

    int described() {
        return described;
    }

    int hidden() {
        return hidden;
    }

    int flagged() {
        return flagged;
    }

    List<FlaggedElement> flaggedElements() {
        return flaggedElements;
    }

    Flow flow(String id, MuleModel.Flow flow) {
        return new Flow(id, flow.name(), FlowKind.FLOW, sourceRef(flow), trigger(flow), steps(id, flow.flowBlocks()),
                errorHandlers(id, flow.flowBlocks()), linker.link(flow.name(), flow.source()));
    }

    Flow subFlow(String id, SubFlow subFlow) {
        return new Flow(id, subFlow.name(), FlowKind.SUB_FLOW, sourceRef(subFlow), Optional.empty(),
                steps(id, subFlow.flowBlocks()), List.of(), linker.link(subFlow.name(), Optional.empty()));
    }

    // Step IDs are positions: parentId.1, parentId.2, ... The error-handler of a flow or try is not a step.
    private List<Step> steps(String parentId, List<MuleRecord> blocks) {
        List<Step> steps = new ArrayList<>();
        for (MuleRecord block : blocks) {
            if (!(block instanceof MuleModel.ErrorHandler)) {
                steps.add(step(parentId + "." + (steps.size() + 1), block));
            }
        }
        return steps;
    }

    // Each on-error-continue or on-error-propagate branch becomes one entry: parentId.e1, parentId.e2, ...
    private List<ErrorHandler> errorHandlers(String parentId, List<MuleRecord> blocks) {
        List<ErrorHandler> handlers = new ArrayList<>();
        for (MuleRecord block : blocks) {
            if (block instanceof MuleModel.ErrorHandler errorHandler) {
                for (ErrorHandlerRecord branch : errorHandler.errorHandlers()) {
                    handlers.add(errorHandler(parentId + ".e" + (handlers.size() + 1), branch));
                }
            }
        }
        return handlers;
    }

    private ErrorHandler errorHandler(String id, ErrorHandlerRecord branch) {
        described++;
        Handling handling;
        List<MuleRecord> body;
        if (branch instanceof OnErrorContinue onErrorContinue) {
            handling = Handling.CONTINUE;
            body = onErrorContinue.errorBlocks();
        } else {
            handling = Handling.PROPAGATE;
            body = ((OnErrorPropagate) branch).errorBlocks();
        }
        List<String> errorTypes = new ArrayList<>();
        for (String type : branch.type().split(",")) {
            if (!type.isBlank()) {
                errorTypes.add(type.strip());
            }
        }
        return new ErrorHandler(id, handling, errorTypes, nonEmpty(branch.when()), steps(id, body), sourceRef(branch),
                recorder.migrationOf(branch));
    }

    private Step step(String id, MuleRecord block) {
        switch (block.kind()) {
            case LOGGER, SET_VARIABLE, REMOVE_VARIABLE, REMOVE_SESSION_VARIABLE, OBJECT_TO_JSON, OBJECT_TO_STRING,
                 ANYPOINT_MQ_ACK, APIKIT_ROUTER -> {
                return Step.plain(info(id, block, StepCoverage.HIDDEN), StepType.TECHNICAL);
            }
            case TRANSFORM_MESSAGE -> {
                return converts(id, (TransformMessage) block);
            }
            case PAYLOAD -> {
                String expression = ((Payload) block).expr();
                DataWeave dataWeave = new DataWeave(Optional.of(lineCount(expression)), Optional.empty(),
                        Optional.of(expression));
                return Step.converts(info(id, block, StepCoverage.DESCRIBED), List.of("payload"),
                        Optional.of(dataWeave));
            }
            case CHOICE -> {
                return decides(id, (Choice) block);
            }
            case SCATTER_GATHER -> {
                return parallel(id, block, StepType.AT_THE_SAME_TIME, ((ScatterGather) block).routes());
            }
            case FIRST_SUCCESSFUL -> {
                return parallel(id, block, StepType.TRIES_EACH_UNTIL_ONE_WORKS, ((FirstSuccessful) block).routes());
            }
            case ASYNC -> {
                return Step.continuesInBackground(info(id, block, StepCoverage.DESCRIBED),
                        steps(id, ((Async) block).flowBlocks()));
            }
            case FOREACH -> {
                Foreach foreach = (Foreach) block;
                return Step.repeatsForEach(info(id, block, StepCoverage.DESCRIBED), foreach.collection(),
                        steps(id, foreach.flowBlocks()));
            }
            case TRY -> {
                List<MuleRecord> body = ((Try) block).flowBlocks();
                return Step.tryScope(info(id, block, StepCoverage.DESCRIBED), steps(id, body),
                        errorHandlers(id, body));
            }
            case RAISE_ERROR -> {
                RaiseError raiseError = (RaiseError) block;
                return Step.raisesError(info(id, block, StepCoverage.DESCRIBED),
                        new RaisedError(raiseError.type(), nonEmpty(raiseError.description())));
            }
            case FLOW_REFERENCE -> {
                String flowName = ((FlowReference) block).flowName();
                Calls calls = flowIds.containsKey(flowName) ? Calls.flow(flowIds.get(flowName))
                        : Calls.external(flowName);
                return Step.callsFlow(info(id, block, StepCoverage.DESCRIBED), calls);
            }
            case HTTP_REQUEST -> {
                HttpRequest request = (HttpRequest) block;
                return system(id, block, StepType.SENDS_TO, "http", request.configRef(), request.method(),
                        request.path());
            }
            case DB_SELECT, DB_INSERT, DB_UPDATE, DB_DELETE -> {
                String operation = block.kind().name().substring("DB_".length()).toLowerCase(Locale.ROOT);
                StepType type = block.kind() == MuleModel.Kind.DB_SELECT ? StepType.LOOKS_UP : StepType.SENDS_TO;
                return system(id, block, type, "db", ((Database) block).configRef(), operation, "");
            }
            case VM_PUBLISH -> {
                VMPublish publish = (VMPublish) block;
                return system(id, block, StepType.SENDS_TO, "vm", publish.configRef(), "publish",
                        publish.queueName());
            }
            case VM_CONSUME -> {
                VMConsume consume = (VMConsume) block;
                return system(id, block, StepType.LOOKS_UP, "vm", consume.configRef(), "consume",
                        consume.queueName());
            }
            case ANYPOINT_MQ_PUBLISH -> {
                AnypointMqPublish publish = (AnypointMqPublish) block;
                return system(id, block, StepType.SENDS_TO, "anypoint-mq", publish.configRef(), "publish",
                        publish.destination());
            }
            case UNSUPPORTED_BLOCK -> {
                return unsupported(id, (UnsupportedBlock) block);
            }
            default -> {
                // expression-component, enricher, and any kind not mapped yet
                return Step.plain(info(id, block, StepCoverage.FLAGGED), StepType.UNKNOWN);
            }
        }
    }

    // The reader keeps unknown elements as UnsupportedBlock; the XML tag tells custom loggers and the xref-client
    // lookup apart from elements that are really not understood.
    private Step unsupported(String id, UnsupportedBlock block) {
        Element element = sourceInfo(block).element();
        String tag = element.getTagName();
        if (tag.equals("json-logger:logger") || tag.startsWith("mule-utilities:log-")) {
            return Step.plain(info(id, block, StepCoverage.HIDDEN), StepType.TECHNICAL);
        }
        if (tag.equals("xref-client:translate")) {
            SystemRef system = new SystemRef("xref-client", XmlElements.attribute(element, "config-ref"),
                    Optional.of("translate"), Optional.empty());
            return Step.callsSystem(info(id, block, StepCoverage.DESCRIBED), StepType.LOOKS_UP, system);
        }
        return Step.plain(info(id, block, StepCoverage.FLAGGED), StepType.UNKNOWN);
    }

    private Step system(String id, MuleRecord block, StepType type, String connector, String connection,
                        String operation, String target) {
        SystemRef system = new SystemRef(connector, nonEmpty(connection), nonEmpty(operation), nonEmpty(target));
        return Step.callsSystem(info(id, block, StepCoverage.DESCRIBED), type, system);
    }

    // Each script part is headed by the variable it writes, so one transform with several parts stays readable
    private Step converts(String id, TransformMessage transform) {
        List<String> writes = new ArrayList<>();
        List<String> resources = new ArrayList<>();
        StringBuilder script = new StringBuilder();
        int lines = 0;
        for (TransformMessageElement child : transform.children()) {
            String target = "";
            String text = null;
            String resource = null;
            if (child instanceof SetPayloadElement payload) {
                target = "payload";
                text = payload.script();
                resource = payload.resource();
            } else if (child instanceof SetVariableElement variable) {
                target = "vars." + variable.variableName();
                text = variable.script();
                resource = variable.resource();
            }
            if (target.isEmpty()) {
                continue;
            }
            writes.add(target);
            if (text != null && !text.isBlank()) {
                script.append("// writes ").append(target).append('\n').append(text.strip()).append('\n');
                lines += lineCount(text);
            }
            if (resource != null && !resource.isEmpty()) {
                resources.add(resource);
            }
        }
        Optional<DataWeave> dataWeave = Optional.empty();
        if (!writes.isEmpty()) {
            dataWeave = Optional.of(new DataWeave(Optional.of(lines), nonEmpty(String.join(", ", resources)),
                    nonEmpty(script.toString().strip())));
        }
        return Step.converts(info(id, transform, StepCoverage.DESCRIBED), writes, dataWeave);
    }

    // Paths are numbered b1, b2, ...: the when routes first, then otherwise (left out when it has no steps)
    private Step decides(String id, Choice choice) {
        List<Branch> paths = new ArrayList<>();
        for (WhenInChoice when : choice.whens()) {
            int number = paths.size() + 1;
            paths.add(Branch.when(when.condition(), steps(id + ".b" + number, when.process()),
                    childSource(choice, "when", number)));
        }
        if (!choice.otherwiseProcess().isEmpty()) {
            int number = paths.size() + 1;
            paths.add(Branch.otherwise(steps(id + ".b" + number, choice.otherwiseProcess()),
                    childSource(choice, "otherwise", 1)));
        }
        return Step.decides(info(id, choice, StepCoverage.DESCRIBED), paths);
    }

    private Step parallel(String id, MuleRecord block, StepType type, List<Route> routes) {
        List<Branch> paths = new ArrayList<>();
        for (Route route : routes) {
            int number = paths.size() + 1;
            paths.add(Branch.parallel(steps(id + ".b" + number, route.flowBlocks()),
                    childSource(block, "route", number)));
        }
        return Step.parallel(info(id, block, StepCoverage.DESCRIBED), type, paths);
    }

    // APIkit operations have no trigger element; they come in Week 3 with the API case
    private Optional<Trigger> trigger(MuleModel.Flow flow) {
        Optional<MuleRecord> source = flow.source();
        if (source.isEmpty() || source.get() instanceof ApiKitConfig) {
            return Optional.empty();
        }
        MuleRecord record = source.get();
        SourceInfo sourceInfo = sourceInfo(record);
        Element element = sourceInfo.element();
        Optional<String> name = XmlElements.docName(element);
        SourceRef sourceRef = sourceRef(sourceInfo);
        Trigger trigger;
        if (record instanceof AnypointMqSubscriber subscriber) {
            trigger = Trigger.queueMessage(name, "anypoint-mq", subscriber.configRef(), subscriber.destination(),
                    sourceRef);
        } else if (record instanceof VMListener listener) {
            trigger = Trigger.queueMessage(name, "vm", listener.configRef(), listener.queueName(), sourceRef);
        } else if (record instanceof PubSubMessageListener listener) {
            trigger = Trigger.queueMessage(name, "pubsub", listener.configRef(), listener.subscriptionName(),
                    sourceRef);
        } else if (record instanceof HttpListener listener) {
            trigger = Trigger.httpRequest(name, listener.configRef(), listener.resourcePath(), sourceRef);
        } else if (record instanceof FileListener listener) {
            trigger = Trigger.newFile(name, listener.configRef(), listener.directory(), schedule(element),
                    sourceRef);
        } else if (record.kind() == MuleModel.Kind.SCHEDULER) {
            // Without a fixed frequency we cannot state an interval, so it is "not understood"
            trigger = Trigger.onSchedule(name, schedule(element).orElse(Schedule.cron()), sourceRef);
        } else {
            return Optional.empty();
        }
        described++;
        return Optional.of(trigger);
    }

    // Read from the XML, not from the reader's record: the reader turns a cron schedule into "every 5 seconds"
    private static Optional<Schedule> schedule(Element listener) {
        Optional<Element> strategy = XmlElements.child(listener, "scheduling-strategy", 1);
        if (strategy.isEmpty()) {
            return Optional.empty();
        }
        Optional<Element> fixedFrequency = XmlElements.child(strategy.get(), "fixed-frequency", 1);
        if (fixedFrequency.isPresent()) {
            Optional<String> every = XmlElements.attribute(fixedFrequency.get(), "frequency");
            if (every.isPresent()) {
                return Optional.of(Schedule.fixedFrequency(every.get(),
                        XmlElements.attribute(fixedFrequency.get(), "timeUnit"),
                        XmlElements.attribute(fixedFrequency.get(), "startDelay")));
            }
        }
        return Optional.of(Schedule.cron());
    }

    // Counts the step for coverage. A flagged step also names its element and the connector from its XML prefix.
    private StepInfo info(String id, MuleRecord block, StepCoverage coverage) {
        SourceInfo sourceInfo = sourceInfo(block);
        Element element = sourceInfo.element();
        Optional<String> tag = Optional.empty();
        Optional<String> connector = Optional.empty();
        if (coverage == StepCoverage.FLAGGED) {
            flagged++;
            tag = Optional.of(element.getTagName());
            connector = Optional.of(XmlElements.connector(element.getTagName()));
            flaggedElements.add(new FlaggedElement(id, tag.get(), connector));
        } else if (coverage == StepCoverage.HIDDEN) {
            hidden++;
        } else {
            described++;
        }
        int level = coverage == StepCoverage.HIDDEN ? 2 : 1;
        return new StepInfo(id, coverage, level, XmlElements.docName(element), tag, connector, sourceRef(sourceInfo),
                recorder.migrationOf(block));
    }

    private SourceInfo sourceInfo(MuleRecord muleRecord) {
        Optional<SourceInfo> sourceInfo = ctx.sourceOf(muleRecord);
        if (sourceInfo.isEmpty()) {
            throw new IllegalStateException("No source element was recorded for " + muleRecord.kind());
        }
        return sourceInfo.get();
    }

    private SourceRef sourceRef(MuleRecord muleRecord) {
        return sourceRef(sourceInfo(muleRecord));
    }

    private SourceRef sourceRef(SourceInfo sourceInfo) {
        String file = projectDir.relativize(Path.of(sourceInfo.filePath()).toAbsolutePath().normalize()).toString()
                .replace(File.separatorChar, '/');
        Element element = sourceInfo.element();
        return new SourceRef(file, element.getTagName(), XmlElements.position(element), XmlElements.docName(element),
                XmlElements.docId(element));
    }

    // when, otherwise and route elements are not read by readBlock, so they are found under their parent (D3)
    private Optional<SourceRef> childSource(MuleRecord parent, String tag, int number) {
        SourceInfo parentInfo = sourceInfo(parent);
        Optional<Element> child = XmlElements.child(parentInfo.element(), tag, number);
        if (child.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(sourceRef(new SourceInfo(parentInfo.filePath(), child.get())));
    }

    private static int lineCount(String text) {
        return text.isBlank() ? 0 : text.strip().split("\n", -1).length;
    }

    private static Optional<String> nonEmpty(String value) {
        return value == null || value.isEmpty() ? Optional.empty() : Optional.of(value);
    }
}
