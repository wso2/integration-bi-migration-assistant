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
import tibco.TibcoModel;
import tibco.TibcoToBalConverter;
import tibco.XmlToTibcoModelConverter;
import tibco.analyzer.AnalysisResult;
import tibco.analyzer.TibcoAnalysisReport;

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
        Element activityElement = stringToElement(fileContent(activityPath));
        TibcoModel.Scope.Flow.Activity activity = XmlToTibcoModelConverter.parseActivity(activityElement);
        ProcessContext cx = getProcessContext(activity);
        BallerinaModel.Function result = ActivityConverter.convertActivity(cx, activity);
        String actual = toString(result);
//        bless(expectedFunction, actual);
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

    private static ProcessContext getProcessContext(TibcoModel.Scope.Flow.Activity activity) {
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
                           Map<TibcoModel.Process, AnalysisResult> analysisResult) {
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

        TestProcessContext(ProjectContext projectContext, TibcoModel.Scope.Flow.Activity activity) {
            super(projectContext, null);
            this.analysisResult = initAnalysisResult(activity);
        }

        @Override
        BallerinaModel.Expression.VariableReference client(String sharedResourcePropertyName) {
            return new BallerinaModel.Expression.VariableReference(sharedResourcePropertyName);
        }

        private static AnalysisResult initAnalysisResult(TibcoModel.Scope.Flow.Activity activity) {
            String prefix = switch (activity) {
                case TibcoModel.Scope.Flow.Activity.ActivityExtension ignored -> "activityExtension";
                case TibcoModel.Scope.Flow.Activity.Empty ignored -> "empty";
                case TibcoModel.Scope.Flow.Activity.ExtActivity ignored -> "extActivity";
                case TibcoModel.Scope.Flow.Activity.Invoke ignored -> "invoke";
                case TibcoModel.Scope.Flow.Activity.Pick ignored -> "pick";
                case TibcoModel.Scope.Flow.Activity.ReceiveEvent ignored -> "receiveEvent";
                case TibcoModel.Scope.Flow.Activity.Reply ignored -> "reply";
                case TibcoModel.Scope.Flow.Activity.CatchAll ignored -> "catchAll";
                case TibcoModel.Scope.Flow.Activity.Throw ignored -> "throw";
                case TibcoModel.Scope.Flow.Activity.NestedScope ignored -> "nestedScope";
                case TibcoModel.Scope.Flow.Activity.Assign ignored -> "assign";
                case TibcoModel.Scope.Flow.Activity.Foreach ignored -> "forEach";
                case TibcoModel.Scope.Flow.Activity.UnhandledActivity ignored -> "unhandled";
                case TibcoModel.Process.ExplicitTransitionGroup.InlineActivity.UnhandledInlineActivity
                             unhandledInlineActivity -> unhandledInlineActivity.name();
                case TibcoModel.Process.ExplicitTransitionGroup.InlineActivity inlineActivity -> inlineActivity.name();
            };
            return new AnalysisResult() {
                @Override
                public Collection<String> inputTypeName(TibcoModel.Process process) {
                    return List.of();
                }

                @Override
                public String outputTypeName(TibcoModel.Process process) {
                    return "";
                }

                @Override
                public Collection<TibcoModel.Scope.Flow.Activity> sources(TibcoModel.Scope.Flow.Link link) {
                    return List.of();
                }

                @Override
                public Optional<TibcoModel.Scope.Flow.Activity> findActivity(String name) {
                    return Optional.empty();
                }

                @Override
                public String variableType(TibcoModel.Process process, String variableName) {
                    return "";
                }

                @Override
                public ActivityData from(TibcoModel.Scope.Flow.Activity activity) {
                    return new ActivityData(prefix, new BallerinaModel.TypeDesc.MapTypeDesc(XML), XML);
                }

                @Override
                public Collection<TibcoModel.Scope.Flow.Activity> activities() {
                    return List.of();
                }

                @Override
                public Collection<TibcoModel.Scope.Flow.Link> links() {
                    return List.of();
                }

                @Override
                public Collection<TibcoModel.Scope.Flow.Link> sources(TibcoModel.Scope.Flow.Activity activity) {
                    return List.of();
                }

                @Override
                public Stream<TransitionData> transitionConditions(TibcoModel.Scope.Flow.Activity activity) {
                    return Stream.empty();
                }

                @Override
                public Stream<TibcoModel.Scope.Flow.Activity.Source.Predicate> transitionCondition(
                        TibcoModel.Scope.Flow.Activity activity, TibcoModel.Scope.Flow.Link link) {
                    return Stream.empty();
                }

                @Override
                public Stream<TibcoModel.Scope.Flow.Activity> sortedActivities(TibcoModel.Scope scope) {
                    return Stream.empty();
                }

                @Override
                public Stream<TibcoModel.Scope.Flow.Activity> sortedActivities(
                        TibcoModel.Process.ExplicitTransitionGroup group) {
                    return Stream.empty();
                }

                @Override
                public Stream<TibcoModel.Scope.Flow.Activity> sortedErrorHandlerActivities(
                        TibcoModel.Process.ExplicitTransitionGroup group) {
                    return Stream.empty();
                }

                @Override
                public Collection<TibcoModel.Scope> scopes(TibcoModel.Process process) {
                    return List.of();
                }

                @Override
                public TibcoModel.PartnerLink.Binding getBinding(String partnerLinkName) {
                    return new TibcoModel.PartnerLink.Binding(
                            new TibcoModel.PartnerLink.Binding.Path("/basePath", "/path"),
                            TibcoModel.PartnerLink.Binding.Connector.HTTP_CLIENT_RESOURCE_2,
                            new TibcoModel.PartnerLink.Binding.Operation(TibcoModel.Method.POST,
                                    Optional.of(
                                            TibcoModel.PartnerLink.Binding.Operation.RequestEntityProcessing.CHUNKED),
                                    Optional.of(TibcoModel.PartnerLink.Binding.Operation.MessageStyle.ELEMENT),
                                    Optional.of(TibcoModel.PartnerLink.Binding.Operation.MessageStyle.ELEMENT),
                                    Optional.of(TibcoModel.PartnerLink.Binding.Operation.Format.JSON),
                                    Optional.of(TibcoModel.PartnerLink.Binding.Operation.Format.JSON),
                                    List.of()));
                }

                @Override
                public ControlFlowFunctions getControlFlowFunctions(TibcoModel.Scope scope) {
                    return new ControlFlowFunctions("scopeFn", "activityRunner", "errorHandler");
                }

                @Override
                public ControlFlowFunctions getControlFlowFunctions(TibcoModel.Process.ExplicitTransitionGroup group) {
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
            };
        }


        @Override
        public AnalysisResult getAnalysisResult() {
            return analysisResult;
        }
    }
}
