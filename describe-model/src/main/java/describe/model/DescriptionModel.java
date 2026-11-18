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
package describe.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * The integration description, schema v0.1 ({@value #SCHEMA_RESOURCE}). It holds facts only; the plain-language
 * wording is written outside the tool, keyed by step ID. The order of record components is the JSON key order, and
 * an empty {@link Optional} is not written.
 */
public final class DescriptionModel {

    public static final String SCHEMA_VERSION = "0.1";
    public static final String SCHEMA_RESOURCE = "describe/description-0.1.schema.json";

    private static final Pattern FLOW_ID = Pattern.compile("f[1-9][0-9]*");
    private static final Pattern STEP_ID = Pattern.compile("f[1-9][0-9]*(\\.([be][1-9][0-9]*|[1-9][0-9]*))+");
    private static final Pattern ERROR_HANDLER_ID =
            Pattern.compile("f[1-9][0-9]*(\\.([be][1-9][0-9]*|[1-9][0-9]*))*\\.e[1-9][0-9]*");
    private static final Pattern API_ID = Pattern.compile("a[1-9][0-9]*");
    private static final Pattern HTTP_METHOD = Pattern.compile("[A-Z]+");

    private DescriptionModel() {
    }

    public record Description(String schemaVersion, Generator generator, Project project, List<Flow> flows,
                              List<Api> apis, List<Connection> connections, List<PropertyRef> properties,
                              List<Import> imports, Coverage coverage) {
        public Description {
            assert SCHEMA_VERSION.equals(schemaVersion);
            assert generator != null && project != null && coverage != null;
            flows = List.copyOf(flows);
            apis = List.copyOf(apis);
            connections = List.copyOf(connections);
            properties = List.copyOf(properties);
            imports = List.copyOf(imports);
        }
    }

    public record Generator(String name, String version) {
        public Generator {
            assert name != null && !name.isEmpty() && version != null && !version.isEmpty();
        }
    }

    public record Project(String name, String platform, Optional<String> platformVersion) {
        public Project {
            assert name != null && !name.isEmpty() && platform != null && !platform.isEmpty();
            assert platformVersion != null;
        }
    }

    // file is relative to the project root, with '/' separators. position (an element path such as
    // /mule/flow[1]/choice[1]) locates the element; docId is not the step ID, because copies can share it.
    public record SourceRef(String file, String element, String position, Optional<String> docName,
                            Optional<String> docId) {
        public SourceRef {
            assert file != null && !file.isEmpty() && !file.startsWith("/") && !file.contains("\\")
                    : "relative file path expected: " + file;
            assert element != null && !element.isEmpty() && position != null && position.startsWith("/");
            assert docName != null && docId != null;
        }
    }

    public record Migration(MigrationStatus status,
                            @JsonInclude(JsonInclude.Include.NON_EMPTY) List<MigrationReason> reasons) {
        public static final Migration CONVERTED = new Migration(MigrationStatus.CONVERTED, List.of());

        public Migration {
            assert status != null;
            reasons = List.copyOf(reasons);
            assert (status == MigrationStatus.CONVERTED) == reasons.isEmpty()
                    : "only a non-converted status has reasons";
        }
    }

    public enum MigrationStatus {
        @JsonProperty("converted") CONVERTED,
        @JsonProperty("partlyConverted") PARTLY_CONVERTED,
        @JsonProperty("notConverted") NOT_CONVERTED
    }

    public record MigrationReason(ReasonKind kind, String text) {
        public MigrationReason {
            assert kind != null && text != null && !text.isEmpty();
        }
    }

    public enum ReasonKind {
        @JsonProperty("unsupportedElement") UNSUPPORTED_ELEMENT,
        @JsonProperty("dataweave") DATAWEAVE,
        @JsonProperty("todo") TODO,
        @JsonProperty("conversionError") CONVERSION_ERROR
    }

    // every is kept as written, so a placeholder is never shown as a number. A cron expression is not interpreted,
    // so it is never described as an interval.
    public record Schedule(ScheduleKind kind, Optional<String> every, Optional<String> unit,
                           Optional<String> startDelay, Optional<Boolean> understood) {
        public Schedule {
            assert kind != null && every != null && unit != null && startDelay != null && understood != null;
            assert kind != ScheduleKind.FIXED_FREQUENCY
                    || (every.isPresent() && !every.get().isEmpty() && understood.isEmpty());
            assert kind != ScheduleKind.CRON || (every.isEmpty() && unit.isEmpty() && startDelay.isEmpty()
                    && understood.equals(Optional.of(false)));
        }

        public static Schedule fixedFrequency(String every, Optional<String> unit, Optional<String> startDelay) {
            return new Schedule(ScheduleKind.FIXED_FREQUENCY, Optional.of(every), unit, startDelay, Optional.empty());
        }

        public static Schedule cron() {
            return new Schedule(ScheduleKind.CRON, Optional.empty(), Optional.empty(), Optional.empty(),
                    Optional.of(false));
        }
    }

    public enum ScheduleKind {
        @JsonProperty("fixedFrequency") FIXED_FREQUENCY,
        @JsonProperty("cron") CRON
    }

    // The "starts when" of a flow. It is not a step, because an APIkit operation has no trigger element of its own.
    // Create it with the factory method for its kind; each one fills only the fields of that kind.
    public record Trigger(TriggerKind kind, Optional<String> name, Optional<String> api, Optional<String> method,
                          Optional<String> connector, Optional<String> connection, Optional<String> destination,
                          Optional<String> directory, Optional<String> path, Optional<Schedule> poll,
                          Optional<Schedule> schedule, Optional<SourceRef> source) {
        public Trigger {
            assert kind != null && name != null && api != null && method != null && connector != null;
            assert connection != null && destination != null && directory != null && path != null;
            assert poll != null && schedule != null && source != null;
            boolean isApi = kind == TriggerKind.API_OPERATION;
            assert api.isPresent() == isApi && method.isPresent() == isApi;
            assert api.isEmpty() || API_ID.matcher(api.get()).matches() : "bad api id: " + api;
            assert method.isEmpty() || HTTP_METHOD.matcher(method.get()).matches();
            assert name.isEmpty() || !isApi;
            assert source.isPresent() == !isApi;
            assert path.isPresent() == (isApi || kind == TriggerKind.HTTP_REQUEST);
            assert connection.isPresent() == (kind == TriggerKind.HTTP_REQUEST || kind == TriggerKind.QUEUE_MESSAGE
                    || kind == TriggerKind.NEW_FILE);
            assert connector.isPresent() == (kind == TriggerKind.QUEUE_MESSAGE);
            assert connector.isEmpty() || !connector.get().isEmpty();
            assert destination.isPresent() == (kind == TriggerKind.QUEUE_MESSAGE);
            assert directory.isPresent() == (kind == TriggerKind.NEW_FILE);
            assert poll.isEmpty() || kind == TriggerKind.NEW_FILE;
            assert schedule.isPresent() == (kind == TriggerKind.SCHEDULE);
        }

        public static Trigger apiOperation(String api, String method, String path) {
            return new Trigger(TriggerKind.API_OPERATION, Optional.empty(), Optional.of(api), Optional.of(method),
                    Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(path),
                    Optional.empty(), Optional.empty(), Optional.empty());
        }

        public static Trigger httpRequest(Optional<String> name, String connection, String path, SourceRef source) {
            return new Trigger(TriggerKind.HTTP_REQUEST, name, Optional.empty(), Optional.empty(), Optional.empty(),
                    Optional.of(connection), Optional.empty(), Optional.empty(), Optional.of(path), Optional.empty(),
                    Optional.empty(), Optional.of(source));
        }

        public static Trigger queueMessage(Optional<String> name, String connector, String connection,
                                           String destination, SourceRef source) {
            return new Trigger(TriggerKind.QUEUE_MESSAGE, name, Optional.empty(), Optional.empty(),
                    Optional.of(connector), Optional.of(connection), Optional.of(destination), Optional.empty(),
                    Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(source));
        }

        public static Trigger newFile(Optional<String> name, String connection, String directory,
                                      Optional<Schedule> poll, SourceRef source) {
            return new Trigger(TriggerKind.NEW_FILE, name, Optional.empty(), Optional.empty(), Optional.empty(),
                    Optional.of(connection), Optional.empty(), Optional.of(directory), Optional.empty(), poll,
                    Optional.empty(), Optional.of(source));
        }

        public static Trigger onSchedule(Optional<String> name, Schedule schedule, SourceRef source) {
            return new Trigger(TriggerKind.SCHEDULE, name, Optional.empty(), Optional.empty(), Optional.empty(),
                    Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                    Optional.of(schedule), Optional.of(source));
        }
    }

    public enum TriggerKind {
        @JsonProperty("apiOperation") API_OPERATION,
        @JsonProperty("httpRequest") HTTP_REQUEST,
        @JsonProperty("queueMessage") QUEUE_MESSAGE,
        @JsonProperty("newFile") NEW_FILE,
        @JsonProperty("schedule") SCHEDULE
    }

    // found == false means "link unknown". There is no file name: the names from codeGen() are not the names of the
    // written files.
    public record BallerinaLink(Construct construct, Optional<String> name, Optional<String> service,
                                Optional<String> method, Optional<String> path, boolean found) {
        public BallerinaLink {
            assert construct != null && name != null && service != null && method != null && path != null;
        }
    }

    public enum Construct {
        @JsonProperty("function") FUNCTION,
        @JsonProperty("resource") RESOURCE,
        @JsonProperty("remoteMethod") REMOTE_METHOD,
        @JsonProperty("jobClass") JOB_CLASS
    }

    public record Flow(String id, String name, FlowKind kind, SourceRef source, Optional<Trigger> startsWhen,
                       List<Step> steps, @JsonInclude(JsonInclude.Include.NON_EMPTY) List<ErrorHandler> onError,
                       Optional<BallerinaLink> ballerina) {
        public Flow {
            assert id != null && FLOW_ID.matcher(id).matches() : "bad flow id: " + id;
            assert name != null && !name.isEmpty() && kind != null && source != null && ballerina != null;
            assert kind == FlowKind.FLOW || startsWhen.isEmpty() : "a sub-flow has no trigger";
            steps = List.copyOf(steps);
            onError = List.copyOf(onError);
        }
    }

    public enum FlowKind {
        @JsonProperty("flow") FLOW,
        @JsonProperty("subFlow") SUB_FLOW
    }

    // One on-error-continue or on-error-propagate branch. The error-handler element has no entry of its own; its
    // status comes from these branches.
    public record ErrorHandler(String id, Handling handling, List<String> errorTypes, Optional<String> when,
                               List<Step> steps, SourceRef source, Optional<Migration> migration) {
        public ErrorHandler {
            assert id != null && ERROR_HANDLER_ID.matcher(id).matches() : "bad error handler id: " + id;
            assert handling != null && when != null && source != null && migration != null;
            errorTypes = List.copyOf(errorTypes);
            steps = List.copyOf(steps);
        }
    }

    public enum Handling {
        @JsonProperty("continue") CONTINUE,
        @JsonProperty("propagate") PROPAGATE
    }

    public enum StepType {
        @JsonProperty("decides") DECIDES,
        @JsonProperty("converts") CONVERTS,
        @JsonProperty("looksUp") LOOKS_UP,
        @JsonProperty("sendsTo") SENDS_TO,
        @JsonProperty("atTheSameTime") AT_THE_SAME_TIME,
        @JsonProperty("triesEachUntilOneWorks") TRIES_EACH_UNTIL_ONE_WORKS,
        @JsonProperty("continuesInBackground") CONTINUES_IN_BACKGROUND,
        @JsonProperty("repeatsForEach") REPEATS_FOR_EACH,
        @JsonProperty("ifItFails") IF_IT_FAILS,
        // checks, combines and responds are reserved: no Mule kind maps to them in v0.1
        @JsonProperty("checks") CHECKS,
        @JsonProperty("combines") COMBINES,
        @JsonProperty("responds") RESPONDS,
        @JsonProperty("callsFlow") CALLS_FLOW,
        @JsonProperty("technical") TECHNICAL,
        @JsonProperty("unknown") UNKNOWN
    }

    public enum StepCoverage {
        @JsonProperty("described") DESCRIBED,
        @JsonProperty("hidden") HIDDEN,
        @JsonProperty("flagged") FLAGGED
    }

    // The fields every step has. element and connector are only for flagged or unknown steps.
    public record StepInfo(String id, StepCoverage coverage, int level, Optional<String> name,
                           Optional<String> element, Optional<String> connector, SourceRef source,
                           Optional<Migration> migration) {
    }

    // One source element of a flow body. level is the first level that shows it. Create it with the factory method
    // for its type; each one fills only the fields of that type.
    public record Step(String id, StepType type, StepCoverage coverage, int level, Optional<String> name,
                       Optional<String> element, Optional<String> connector, SourceRef source,
                       Optional<Migration> migration, Optional<List<Branch>> paths, Optional<String> over,
                       Optional<List<Step>> steps, Optional<List<ErrorHandler>> onError, Optional<RaisedError> raises,
                       Optional<SystemRef> system, Optional<List<String>> writes, Optional<DataWeave> dataweave,
                       Optional<Calls> calls) {
        public Step {
            assert id != null && STEP_ID.matcher(id).matches() : "bad step id: " + id;
            assert type != null && coverage != null && name != null && source != null && migration != null;
            assert level >= 1 && level <= 3 && (coverage != StepCoverage.HIDDEN || level >= 2)
                    : "a hidden step starts at level 2 or later";
            assert element != null && connector != null;
            assert coverage != StepCoverage.FLAGGED || element.isPresent() : "a flagged step names its element";
            assert type != StepType.UNKNOWN || (element.isPresent() && connector.isPresent());
            assert (element.isEmpty() && connector.isEmpty()) || coverage == StepCoverage.FLAGGED
                    || type == StepType.UNKNOWN : "element and connector are only for flagged or unknown steps";
            assert paths != null && over != null && steps != null && onError != null && raises != null;
            assert system != null && writes != null && dataweave != null && calls != null;

            // Each step type has exactly its own fields
            boolean isDecision = type == StepType.DECIDES;
            boolean isParallel = type == StepType.AT_THE_SAME_TIME || type == StepType.TRIES_EACH_UNTIL_ONE_WORKS;
            assert paths.isPresent() == (isDecision || isParallel);
            assert paths.isEmpty() || !paths.get().isEmpty();
            assert paths.isEmpty() || paths.get().stream().allMatch(path -> hasCondition(path) == isDecision)
                    : "decision paths have a condition; parallel paths do not";
            assert over.isPresent() == (type == StepType.REPEATS_FOR_EACH);
            assert over.isEmpty() || !over.get().isEmpty();
            assert raises.isEmpty() || type == StepType.IF_IT_FAILS;
            assert onError.isPresent() == (type == StepType.IF_IT_FAILS && raises.isEmpty());
            assert steps.isPresent() == (type == StepType.REPEATS_FOR_EACH
                    || type == StepType.CONTINUES_IN_BACKGROUND || onError.isPresent());
            assert system.isPresent() == (type == StepType.LOOKS_UP || type == StepType.SENDS_TO);
            assert writes.isPresent() == (type == StepType.CONVERTS);
            assert dataweave.isEmpty() || type == StepType.CONVERTS;
            assert calls.isPresent() == (type == StepType.CALLS_FLOW);

            paths = paths.map(List::copyOf);
            steps = steps.map(List::copyOf);
            onError = onError.map(List::copyOf);
            writes = writes.map(List::copyOf);
        }

        // technical, checks, combines, responds and unknown steps have no type-specific fields
        public static Step plain(StepInfo info, StepType type) {
            return create(info, type, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                    Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
        }

        public static Step decides(StepInfo info, List<Branch> paths) {
            return create(info, StepType.DECIDES, Optional.of(paths), Optional.empty(), Optional.empty(),
                    Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                    Optional.empty());
        }

        // type is atTheSameTime or triesEachUntilOneWorks
        public static Step parallel(StepInfo info, StepType type, List<Branch> paths) {
            return create(info, type, Optional.of(paths), Optional.empty(), Optional.empty(), Optional.empty(),
                    Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
        }

        public static Step repeatsForEach(StepInfo info, String over, List<Step> steps) {
            return create(info, StepType.REPEATS_FOR_EACH, Optional.empty(), Optional.of(over), Optional.of(steps),
                    Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                    Optional.empty());
        }

        public static Step continuesInBackground(StepInfo info, List<Step> steps) {
            return create(info, StepType.CONTINUES_IN_BACKGROUND, Optional.empty(), Optional.empty(),
                    Optional.of(steps), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                    Optional.empty(), Optional.empty());
        }

        public static Step tryScope(StepInfo info, List<Step> steps, List<ErrorHandler> onError) {
            return create(info, StepType.IF_IT_FAILS, Optional.empty(), Optional.empty(), Optional.of(steps),
                    Optional.of(onError), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                    Optional.empty());
        }

        public static Step raisesError(StepInfo info, RaisedError raises) {
            return create(info, StepType.IF_IT_FAILS, Optional.empty(), Optional.empty(), Optional.empty(),
                    Optional.empty(), Optional.of(raises), Optional.empty(), Optional.empty(), Optional.empty(),
                    Optional.empty());
        }

        // type is looksUp or sendsTo
        public static Step callsSystem(StepInfo info, StepType type, SystemRef system) {
            return create(info, type, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                    Optional.empty(), Optional.of(system), Optional.empty(), Optional.empty(), Optional.empty());
        }

        public static Step converts(StepInfo info, List<String> writes, Optional<DataWeave> dataweave) {
            return create(info, StepType.CONVERTS, Optional.empty(), Optional.empty(), Optional.empty(),
                    Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(writes), dataweave,
                    Optional.empty());
        }

        public static Step callsFlow(StepInfo info, Calls calls) {
            return create(info, StepType.CALLS_FLOW, Optional.empty(), Optional.empty(), Optional.empty(),
                    Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                    Optional.of(calls));
        }

        private static Step create(StepInfo info, StepType type, Optional<List<Branch>> paths, Optional<String> over,
                                   Optional<List<Step>> steps, Optional<List<ErrorHandler>> onError,
                                   Optional<RaisedError> raises, Optional<SystemRef> system,
                                   Optional<List<String>> writes, Optional<DataWeave> dataweave,
                                   Optional<Calls> calls) {
            return new Step(info.id(), type, info.coverage(), info.level(), info.name(), info.element(),
                    info.connector(), info.source(), info.migration(), paths, over, steps, onError, raises, system,
                    writes, dataweave, calls);
        }

        private static boolean hasCondition(Branch path) {
            return path.when().isPresent() || path.otherwise().isPresent();
        }
    }

    // One path of a decision (when or otherwise), or of a parallel step (neither).
    public record Branch(Optional<String> when, Optional<Boolean> otherwise, List<Step> steps,
                         Optional<SourceRef> source) {
        public Branch {
            assert when != null && otherwise != null && source != null;
            assert when.isEmpty() || otherwise.isEmpty() : "a path is either 'when' or 'otherwise'";
            assert otherwise.isEmpty() || otherwise.get() : "otherwise is only ever true";
            steps = List.copyOf(steps);
        }

        public static Branch when(String condition, List<Step> steps, Optional<SourceRef> source) {
            return new Branch(Optional.of(condition), Optional.empty(), steps, source);
        }

        public static Branch otherwise(List<Step> steps, Optional<SourceRef> source) {
            return new Branch(Optional.empty(), Optional.of(true), steps, source);
        }

        public static Branch parallel(List<Step> steps, Optional<SourceRef> source) {
            return new Branch(Optional.empty(), Optional.empty(), steps, source);
        }
    }

    public record RaisedError(String type, Optional<String> description) {
        public RaisedError {
            assert type != null && !type.isEmpty() && description != null;
        }
    }

    // target is a path, queue or table as written; placeholders stay placeholders
    public record SystemRef(String connector, Optional<String> connection, Optional<String> operation,
                            Optional<String> target) {
        public SystemRef {
            assert connector != null && !connector.isEmpty();
            assert connection != null && operation != null && target != null;
        }
    }

    public record DataWeave(Optional<Integer> lines, Optional<String> resource, Optional<String> script) {
        public DataWeave {
            assert lines != null && lines.orElse(0) >= 0 && resource != null && script != null;
        }
    }

    // A flow-ref calls a flow of this project (flow) or a flow outside it, such as a shared library (external)
    public record Calls(Optional<String> flow, Optional<String> external) {
        public Calls {
            assert flow != null && external != null && flow.isPresent() != external.isPresent();
            assert flow.isEmpty() || FLOW_ID.matcher(flow.get()).matches();
        }

        public static Calls flow(String flowId) {
            return new Calls(Optional.of(flowId), Optional.empty());
        }

        public static Calls external(String flowName) {
            return new Calls(Optional.empty(), Optional.of(flowName));
        }
    }

    public record Api(String id, String config, ApiSpecRef spec, List<ApiOperation> operations,
                      Optional<ApiPolicies> policies) {
        public Api {
            assert id != null && API_ID.matcher(id).matches() : "bad api id: " + id;
            assert config != null && spec != null && policies != null;
            operations = List.copyOf(operations);
        }
    }

    public record ApiSpecRef(String file, Optional<String> title, Optional<String> version) {
        public ApiSpecRef {
            assert file != null && title != null && version != null;
        }
    }

    // An empty flow is written as null: "in the spec, no flow"
    public record ApiOperation(String method, String path, Optional<String> displayName,
                               Optional<String> description,
                               @JsonInclude(JsonInclude.Include.ALWAYS) Optional<String> flow) {
        public ApiOperation {
            assert method != null && HTTP_METHOD.matcher(method).matches() && path != null && path.startsWith("/");
            assert displayName != null && description != null && flow != null;
        }
    }

    public record ApiPolicies(Optional<String> autodiscovery, List<String> traits) {
        public ApiPolicies {
            assert autodiscovery != null;
            traits = List.copyOf(traits);
        }
    }

    // A global config element, by name only: no values and no passwords. Some elements have no name.
    public record Connection(Optional<String> name, String connector, String element, boolean understood,
                             @JsonInclude(JsonInclude.Include.NON_EMPTY) List<String> properties) {
        public Connection {
            assert name != null && connector != null && element != null;
            properties = List.copyOf(properties);
        }
    }

    public record PropertyRef(String name, boolean secure) {
        public PropertyRef {
            assert name != null && !name.isEmpty();
        }
    }

    // found == false means the imported file is not in the project, for example a shared library
    public record Import(String file, boolean found) {
        public Import {
            assert file != null && !file.isEmpty();
        }
    }

    public record Coverage(Counts counts, List<FlaggedElement> flagged, List<FailedFile> failedFiles,
                           List<String> testFiles, List<String> linkUnknown) {
        public Coverage {
            assert counts != null;
            flagged = List.copyOf(flagged);
            failedFiles = List.copyOf(failedFiles);
            testFiles = List.copyOf(testFiles);
            linkUnknown = List.copyOf(linkUnknown);
        }
    }

    public record Counts(int described, int hidden, int flagged) {
        public Counts {
            assert described >= 0 && hidden >= 0 && flagged >= 0;
        }
    }

    public record FlaggedElement(String step, String element, Optional<String> connector) {
        public FlaggedElement {
            assert step != null && STEP_ID.matcher(step).matches() && element != null && connector != null;
        }
    }

    public record FailedFile(String file, Optional<String> reason) {
        public FailedFile {
            assert file != null && !file.isEmpty() && reason != null;
        }
    }
}
