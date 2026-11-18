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
package describe;

import describe.json.DescriptionWriter;
import describe.model.DescriptionModel;
import describe.model.DescriptionModel.Api;
import describe.model.DescriptionModel.ApiOperation;
import describe.model.DescriptionModel.ApiPolicies;
import describe.model.DescriptionModel.ApiSpecRef;
import describe.model.DescriptionModel.BallerinaLink;
import describe.model.DescriptionModel.Branch;
import describe.model.DescriptionModel.Calls;
import describe.model.DescriptionModel.Connection;
import describe.model.DescriptionModel.Construct;
import describe.model.DescriptionModel.Counts;
import describe.model.DescriptionModel.Coverage;
import describe.model.DescriptionModel.DataWeave;
import describe.model.DescriptionModel.Description;
import describe.model.DescriptionModel.ErrorHandler;
import describe.model.DescriptionModel.FlaggedElement;
import describe.model.DescriptionModel.Flow;
import describe.model.DescriptionModel.FlowKind;
import describe.model.DescriptionModel.Generator;
import describe.model.DescriptionModel.Handling;
import describe.model.DescriptionModel.Import;
import describe.model.DescriptionModel.Migration;
import describe.model.DescriptionModel.MigrationReason;
import describe.model.DescriptionModel.MigrationStatus;
import describe.model.DescriptionModel.Project;
import describe.model.DescriptionModel.PropertyRef;
import describe.model.DescriptionModel.RaisedError;
import describe.model.DescriptionModel.ReasonKind;
import describe.model.DescriptionModel.Schedule;
import describe.model.DescriptionModel.SourceRef;
import describe.model.DescriptionModel.Step;
import describe.model.DescriptionModel.StepCoverage;
import describe.model.DescriptionModel.StepInfo;
import describe.model.DescriptionModel.StepType;
import describe.model.DescriptionModel.SystemRef;
import describe.model.DescriptionModel.Trigger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Writes a sample description that uses every step type shape and trigger kind. Set BLESS=true to rewrite the
 * expected file after a deliberate format change.
 */
public class DescriptionWriterTest {

    private static final Path EXPECTED = Path.of("src/test/resources/writer/sample-description.json");
    private static final String FILE = "src/main/mule/orders.xml";

    @Test
    public void testOutputMatchesExpectedFile() throws IOException {
        String json = DescriptionWriter.toJson(sample());
        if ("true".equalsIgnoreCase(System.getenv("BLESS"))) {
            Files.writeString(EXPECTED, json);
        }
        Assert.assertEquals(json, Files.readString(EXPECTED));
    }

    @Test
    public void testOutputIsValidAgainstSchema() throws IOException {
        Assert.assertEquals(SchemaValidation.validate(DescriptionWriter.toJson(sample())), Set.of());
    }

    @Test
    public void testSameDescriptionGivesSameBytes() {
        String first = DescriptionWriter.toJson(sample());
        Assert.assertEquals(DescriptionWriter.toJson(sample()), first);
        Assert.assertFalse(first.contains("\r"), "line ends must be '\\n' only");
        Assert.assertTrue(first.endsWith("}\n"));
    }

    private static Description sample() {
        List<Connection> connections = List.of(
                new Connection(Optional.of("api-config"), "http", "http:request-config", true,
                        List.of("secure::api.host")),
                new Connection(Optional.empty(), "core", "configuration", false, List.of()));
        List<PropertyRef> properties = List.of(new PropertyRef("secure::queue.name", true),
                new PropertyRef("input.dir", false));
        return new Description(DescriptionModel.SCHEMA_VERSION,
                new Generator("mule-migration-assistant", "0.1.0-SNAPSHOT"),
                new Project("orders", "mule", Optional.of("4.6.0")),
                List.of(queueFlow(), subFlow(), apiFlow(), fileFlow(), cronFlow(), httpFlow()), List.of(api()),
                connections, properties, List.of(new Import("common-errors.xml", false)), coverage());
    }

    private static Flow queueFlow() {
        Trigger trigger = Trigger.queueMessage(Optional.of("Subscribe to orders"), "anypoint-mq", "mq-config",
                "${secure::queue.name}", src("anypoint-mq:subscriber", "/mule/flow[1]/anypoint-mq:subscriber[1]"));
        StepInfo logInfo = new StepInfo("f1.1", StepCoverage.HIDDEN, 2, Optional.of("Log START"), Optional.empty(),
                Optional.empty(), stepSource("f1.1"), Optional.of(notConverted("json-logger:logger")));
        DataWeave script = new DataWeave(Optional.of(3), Optional.empty(),
                Optional.of("%dw 2.0\noutput application/java\n---\n'x'"));
        ErrorHandler tryHandler = new ErrorHandler("f1.8.e1", Handling.CONTINUE, List.of("HTTP:NOT_FOUND"),
                Optional.empty(), List.of(),
                src("on-error-continue", "/mule/flow[1]/try[1]/error-handler[1]/on-error-continue[1]"),
                Optional.empty());
        List<Step> steps = List.of(
                Step.plain(logInfo, StepType.TECHNICAL),
                Step.converts(info("f1.2", "Set Variables", Migration.CONVERTED), List.of("vars.source"),
                        Optional.of(script)),
                decision(),
                scatterGather(),
                Step.parallel(info("f1.5", "First successful"), StepType.TRIES_EACH_UNTIL_ONE_WORKS,
                        List.of(Branch.parallel(List.of(), Optional.empty()))),
                Step.continuesInBackground(info("f1.6", "Async"),
                        List.of(Step.callsFlow(info("f1.6.1", "error handler"), Calls.external("commonErrors")))),
                Step.repeatsForEach(info("f1.7", "For Each"), "#[payload]", List.of()),
                Step.tryScope(info("f1.8", "Try"), List.of(), List.of(tryHandler)));

        Step raise = Step.raisesError(info("f1.e1.1", "Raise error"),
                new RaisedError("BILLING:UNAVAILABLE", Optional.empty()));
        ErrorHandler flowHandler = new ErrorHandler("f1.e1", Handling.PROPAGATE, List.of(), Optional.empty(),
                List.of(raise), src("on-error-propagate", "/mule/flow[1]/error-handler[1]/on-error-propagate[1]"),
                Optional.empty());
        BallerinaLink link = new BallerinaLink(Construct.REMOTE_METHOD, Optional.of("onMessage"),
                Optional.of("\"mq-config\""), Optional.empty(), Optional.empty(), true);
        return new Flow("f1", "order-events", FlowKind.FLOW, src("flow", "/mule/flow[1]"), Optional.of(trigger),
                steps, List.of(flowHandler), Optional.of(link));
    }

    private static Step decision() {
        Step translate = Step.callsSystem(info("f1.3.b1.1", "Translate", notConverted("code-client:translate")),
                StepType.LOOKS_UP, new SystemRef("code-client", Optional.of("code-config"), Optional.of("translate"),
                        Optional.empty()));
        Step publish = Step.callsFlow(info("f1.3.b2.1", "Publish"), Calls.flow("f2"));
        return Step.decides(info("f1.3", "Is source = billing?"), List.of(
                Branch.when("#[vars.source == 'billing']", List.of(translate),
                        Optional.of(src("when", "/mule/flow[1]/choice[1]/when[1]"))),
                Branch.otherwise(List.of(publish), Optional.empty())));
    }

    private static Step scatterGather() {
        Step callApi = Step.callsSystem(info("f1.4.b1.1", "Call API", Migration.CONVERTED), StepType.SENDS_TO,
                new SystemRef("http", Optional.of("api-config"), Optional.of("POST"),
                        Optional.of("${secure::api.path}")));
        StepInfo insertInfo = new StepInfo("f1.4.b2.1", StepCoverage.FLAGGED, 1, Optional.of("Insert records"),
                Optional.of("db:bulk-insert"), Optional.of("db"), src("db:bulk-insert", "/mule/flow[1]/f1.4.b2.1"),
                Optional.of(notConverted("db:bulk-insert")));
        Step insert = Step.callsSystem(insertInfo, StepType.SENDS_TO, new SystemRef("db", Optional.of("db-config"),
                Optional.of("bulk-insert"), Optional.empty()));
        return Step.parallel(info("f1.4", "Scatter-Gather"), StepType.AT_THE_SAME_TIME, List.of(
                Branch.parallel(List.of(callApi), Optional.empty()),
                Branch.parallel(List.of(insert), Optional.empty())));
    }

    private static Flow subFlow() {
        BallerinaLink link = new BallerinaLink(Construct.FUNCTION, Optional.of("publish"), Optional.empty(),
                Optional.empty(), Optional.empty(), false);
        return new Flow("f2", "publish", FlowKind.SUB_FLOW, src("sub-flow", "/mule/sub-flow[1]"), Optional.empty(),
                List.of(), List.of(), Optional.of(link));
    }

    private static Flow apiFlow() {
        BallerinaLink link = new BallerinaLink(Construct.RESOURCE, Optional.empty(), Optional.empty(),
                Optional.of("get"), Optional.of("orders/[string id]"), true);
        return new Flow("f3", "get:\\orders\\(id):api-config", FlowKind.FLOW, src("flow", "/mule/flow[2]"),
                Optional.of(Trigger.apiOperation("a1", "GET", "/orders/{id}")), List.of(), List.of(),
                Optional.of(link));
    }

    private static Flow fileFlow() {
        Schedule poll = Schedule.fixedFrequency("${poll.frequency}", Optional.of("SECONDS"), Optional.empty());
        Trigger trigger = Trigger.newFile(Optional.empty(), "file-config", "${input.dir}", Optional.of(poll),
                src("file:listener", "/mule/flow[3]/file:listener[1]"));
        return new Flow("f4", "import", FlowKind.FLOW, src("flow", "/mule/flow[3]"), Optional.of(trigger),
                List.of(), List.of(), Optional.empty());
    }

    private static Flow cronFlow() {
        Trigger trigger = Trigger.onSchedule(Optional.empty(), Schedule.cron(),
                src("scheduler", "/mule/flow[4]/scheduler[1]"));
        return new Flow("f5", "nightly", FlowKind.FLOW, src("flow", "/mule/flow[4]"), Optional.of(trigger),
                List.of(), List.of(), Optional.empty());
    }

    private static Flow httpFlow() {
        Trigger trigger = Trigger.httpRequest(Optional.empty(), "http-listener-config", "/status",
                src("http:listener", "/mule/flow[5]/http:listener[1]"));
        return new Flow("f6", "status", FlowKind.FLOW, src("flow", "/mule/flow[5]"), Optional.of(trigger),
                List.of(), List.of(), Optional.empty());
    }

    private static Api api() {
        List<ApiOperation> operations = List.of(
                new ApiOperation("GET", "/orders/{id}", Optional.empty(), Optional.of("Get one order"),
                        Optional.of("f3")),
                new ApiOperation("POST", "/orders", Optional.empty(), Optional.empty(), Optional.empty()));
        return new Api("a1", "api-config", new ApiSpecRef("src/main/resources/api/orders.raml",
                Optional.of("Orders API"), Optional.of("v1")), operations,
                Optional.of(new ApiPolicies(Optional.of("${api.id}"), List.of("client-id-enforcement"))));
    }

    private static Coverage coverage() {
        return new Coverage(new Counts(12, 1, 1),
                List.of(new FlaggedElement("f1.4.b2.1", "db:bulk-insert", Optional.of("db"))), List.of(),
                List.of("src/test/munit/orders-test.xml"), List.of("f2"));
    }

    private static StepInfo info(String id, String name) {
        return new StepInfo(id, StepCoverage.DESCRIBED, 1, Optional.of(name), Optional.empty(), Optional.empty(),
                stepSource(id), Optional.empty());
    }

    private static StepInfo info(String id, String name, Migration migration) {
        return new StepInfo(id, StepCoverage.DESCRIBED, 1, Optional.of(name), Optional.empty(), Optional.empty(),
                stepSource(id), Optional.of(migration));
    }

    private static SourceRef stepSource(String id) {
        return src("element", "/mule/flow[1]/" + id);
    }

    private static Migration notConverted(String element) {
        return new Migration(MigrationStatus.NOT_CONVERTED,
                List.of(new MigrationReason(ReasonKind.UNSUPPORTED_ELEMENT, element)));
    }

    private static SourceRef src(String element, String position) {
        return new SourceRef(FILE, element, position, Optional.empty(), Optional.empty());
    }
}
