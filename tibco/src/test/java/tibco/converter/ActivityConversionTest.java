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

package tibco.converter;

import common.BallerinaModel;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import tibco.TibcoToBalConverter;
import tibco.analyzer.AnalysisResult;
import tibco.analyzer.TibcoAnalysisReport;
import tibco.model.Method;
import tibco.model.PartnerLink;
import tibco.model.Process;
import tibco.model.Process5;
import tibco.model.Scope;
import tibco.model.XSD;
import tibco.parser.XmlToTibcoModelParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import javax.xml.parsers.ParserConfigurationException;

import static common.BallerinaModel.TypeDesc.BuiltinType.XML;
import static tibco.util.TestUtils.fileContent;
import static tibco.util.TestUtils.stringToElement;

public class ActivityConversionTest {

    @Test(groups = {"tibco", "converter"}, dataProvider = "activityTestCaseProvider")
    public void testProjectConversion(Path activityPath, Path expectedFunction) throws IOException,
            ParserConfigurationException, SAXException {
        tibco.parser.ProcessContext cx =
                new tibco.parser.ProcessContext(new tibco.parser.ProjectContext(), "ANON.process");
        Element activityElement = stringToElement(fileContent(activityPath));
        Scope.Flow.Activity activity = XmlToTibcoModelParser.parseActivity(cx, activityElement);
        BallerinaModel.Function result = ActivityConverter.convertActivity(getProcessContext(activity), activity);
        String actual = toString(result);
        if ("true".equalsIgnoreCase(System.getenv("BLESS"))) {
            bless(expectedFunction, actual);
        }
        String expected = fileContent(expectedFunction);
        Assert.assertEquals(actual, expected);
    }

    private static void bless(Path expectedFunction, String value) {
        try {
            Files.writeString(expectedFunction, value);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write expected function to " + expectedFunction, e);
        }
    }

    private static ProcessContext getProcessContext(Scope.Flow.Activity activity) {
        TibcoToBalConverter.ProjectConversionContext conversionContext =
                new TibcoToBalConverter.ProjectConversionContext(true, false, List.of());
        return new TestProcessContext(new TestProjectContext(conversionContext, Map.of()), activity);
    }

    private static String toString(BallerinaModel.Function function) {
        String params = String.join(",", function.parameters().stream().map(p -> p.defaultExpr().isPresent() ?
                String.format("%s %s = %s", p.type(), p.name(), p.defaultExpr().get().expr()) :
                String.format("%s %s", p.type(), p.name())).toList());
        StringBuilder sb = new StringBuilder();
        String prefix = "function %s(%s)".formatted(function.functionName(), params);
        sb.append(prefix);
        if (function.returnType().isPresent()) {
            sb.append(" returns ").append(function.returnType().get());
        }
        sb.append(" {\n");
        BallerinaModel.BlockFunctionBody body = (BallerinaModel.BlockFunctionBody) function.body();
        body.statements().forEach(s -> sb.append("    ").append(s).append("\n"));
        sb.append("}");
        sb.append("\n");
        return sb.toString();
    }

    @DataProvider
    public Object[][] activityTestCaseProvider() throws IOException {
        Path activityTestCaseDir = Path.of("src", "test", "resources", "tibco.activities");
        Path expectedConvertedResultsDir = Path.of("src", "test", "resources", "tibco.activities.converted");

        return Files.walk(activityTestCaseDir)
                .filter(path -> path.toString().endsWith(".xml"))
                .map(xmlPath -> new Object[]{
                        xmlPath,
                        expectedConvertedResultsDir.resolve(xmlPath.getFileName().toString().replace(".xml", ".bal"))
                })
                .toArray(Object[][]::new);
    }

    static class TestProjectContext extends ProjectContext {

        TestProjectContext(TibcoToBalConverter.ProjectConversionContext conversionContext,
                           Map<Process, AnalysisResult> analysisResult) {
            super(conversionContext, analysisResult);
        }

        @Override
        public String getConfigVarName(String varName) {
            return varName;
        }


        @Override
        Optional<ProcessContext.DefaultClientDetails> getDefaultClientDetails(String processName) {
            ProcessContext.DefaultClientDetails client = new ProcessContext.DefaultClientDetails(
                    new BallerinaModel.ModuleVar(
                            "processClient", "http:client", Optional.empty(), true, false),
                    "post");
            client.isUsed = true;
            return Optional.of(client);
        }
    }

    static class TestProcessContext extends ProcessContext {
        final AnalysisResult analysisResult;

        TestProcessContext(ProjectContext projectContext, Scope.Flow.Activity activity) {
            super(projectContext, null);
            this.analysisResult = initAnalysisResult(activity);
        }

        @Override
        BallerinaModel.Expression.VariableReference client(String sharedResourcePropertyName) {
            return new BallerinaModel.Expression.VariableReference(sharedResourcePropertyName);
        }

        private static AnalysisResult initAnalysisResult(Scope.Flow.Activity activity) {
            String prefix = switch (activity) {
                case Scope.Flow.Activity.ActivityExtension ignored -> "activityExtension";
                case Scope.Flow.Activity.Empty ignored -> "empty";
                case Scope.Flow.Activity.ExtActivity ignored -> "extActivity";
                case Scope.Flow.Activity.Invoke ignored -> "invoke";
                case Scope.Flow.Activity.Pick ignored -> "pick";
                case Scope.Flow.Activity.ReceiveEvent ignored -> "receiveEvent";
                case Scope.Flow.Activity.Reply ignored -> "reply";
                case Scope.Flow.Activity.CatchAll ignored -> "catchAll";
                case Scope.Flow.Activity.Throw ignored -> "throw";
                case Scope.Flow.Activity.NestedScope ignored -> "nestedScope";
                case Scope.Flow.Activity.Assign ignored -> "assign";
                case Scope.Flow.Activity.Foreach ignored -> "forEach";
                case Scope.Flow.Activity.UnhandledActivity ignored -> "unhandled";
                case Process5.ExplicitTransitionGroup.InlineActivity.UnhandledInlineActivity
                             unhandledInlineActivity -> unhandledInlineActivity.name();
                case Process5.ExplicitTransitionGroup.InlineActivity inlineActivity -> inlineActivity.name();
            };
            return new AnalysisResult() {
                @Override
                public Collection<String> inputTypeName(Process process) {
                    return List.of();
                }

                @Override
                public String outputTypeName(Process process) {
                    return "";
                }

                @Override
                public Collection<Scope.Flow.Activity> sources(Scope.Flow.Link link) {
                    return List.of();
                }

                @Override
                public Optional<Scope.Flow.Activity> findActivity(String name) {
                    return Optional.empty();
                }

                @Override
                public String variableType(Process process, String variableName) {
                    return "";
                }

                @Override
                public ActivityData from(Scope.Flow.Activity activity) {
                    return new ActivityData(prefix, new BallerinaModel.TypeDesc.MapTypeDesc(XML), XML);
                }

                @Override
                public Collection<Scope.Flow.Activity> activities() {
                    return List.of();
                }

                @Override
                public Collection<Scope.Flow.Link> links() {
                    return List.of();
                }

                @Override
                public Collection<Scope.Flow.Link> sources(Scope.Flow.Activity activity) {
                    return List.of();
                }

                @Override
                public Stream<TransitionData> transitionConditions(Scope.Flow.Activity activity) {
                    return Stream.empty();
                }

                @Override
                public Stream<Scope.Flow.Activity.Source.Predicate> transitionCondition(
                        Scope.Flow.Activity activity, Scope.Flow.Link link) {
                    return Stream.empty();
                }

                @Override
                public Stream<Scope.Flow.Activity> sortedActivities(Scope scope) {
                    return Stream.empty();
                }

                @Override
                public Stream<Scope.Flow.Activity> sortedActivities(
                        Process5.ExplicitTransitionGroup group) {
                    return Stream.empty();
                }

                @Override
                public Stream<Scope.Flow.Activity> sortedErrorHandlerActivities(
                        Process5.ExplicitTransitionGroup group) {
                    return Stream.empty();
                }

                @Override
                public Collection<Scope> scopes(Process process) {
                    return List.of();
                }

                @Override
                public PartnerLink.Binding getBinding(String partnerLinkName) {
                    return new PartnerLink.Binding(
                            new PartnerLink.Binding.Path("/basePath", "/path"),
                            PartnerLink.Binding.Connector.HTTP_CLIENT_RESOURCE_2,
                            new PartnerLink.Binding.Operation(Method.POST,
                                    Optional.of(
                                            PartnerLink.Binding.Operation.RequestEntityProcessing.CHUNKED),
                                    Optional.of(PartnerLink.Binding.Operation.MessageStyle.ELEMENT),
                                    Optional.of(PartnerLink.Binding.Operation.MessageStyle.ELEMENT),
                                    Optional.of(PartnerLink.Binding.Operation.Format.JSON),
                                    Optional.of(PartnerLink.Binding.Operation.Format.JSON),
                                    List.of()));
                }

                @Override
                public ControlFlowFunctions getControlFlowFunctions(Scope scope) {
                    return new ControlFlowFunctions("scopeFn", "activityRunner", "errorHandler");
                }

                @Override
                public ControlFlowFunctions getControlFlowFunctions(Process5.ExplicitTransitionGroup group) {
                    return new ControlFlowFunctions("scopeFn", "activityRunner", "errorHandler");
                }

                @Override
                public AnalysisResult combine(AnalysisResult other) {
                    return null;
                }

                @Override
                public Optional<TibcoAnalysisReport> getReport() {
                    return Optional.empty();
                }

                @Override
                public void setReport(TibcoAnalysisReport report) {

                }

                @Override
                public XSD.XSDType getType(String name) {
                    return new XSD.XSDType.ComplexType(new XSD.XSDType.ComplexType.ComplexTypeBody.Sequence(
                            List.of(
                                    new XSD.Element("dob", XSD.XSDType.BasicXSDType.STRING, Optional.empty(),
                                            Optional.empty()),
                                    new XSD.Element("firstName", XSD.XSDType.BasicXSDType.STRING, Optional.empty(),
                                            Optional.empty()),
                                    new XSD.Element("lastName", XSD.XSDType.BasicXSDType.STRING, Optional.empty(),
                                            Optional.empty()),
                                    new XSD.Element("ssn", XSD.XSDType.BasicXSDType.STRING, Optional.empty(),
                                            Optional.empty()))));
                }
            };
        }


        @Override
        public AnalysisResult getAnalysisResult() {
            return analysisResult;
        }
    }
}
