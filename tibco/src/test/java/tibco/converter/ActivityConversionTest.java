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
                new TibcoToBalConverter.ProjectConversionContext(List.of());
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
    }

    static class TestProcessContext extends ProcessContext {
        final AnalysisResult analysisResult;

        TestProcessContext(ProjectContext projectContext, TibcoModel.Scope.Flow.Activity activity) {
            super(projectContext, null);
            this.analysisResult = initAnalysisResult(activity);
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
                    return null;
                }

                @Override
                public ControlFlowFunctions getControlFlowFunctions(TibcoModel.Scope scope) {
                    return null;
                }

                @Override
                public ControlFlowFunctions getControlFlowFunctions(TibcoModel.Process.ExplicitTransitionGroup group) {
                    return null;
                }

                @Override
                public AnalysisResult combine(AnalysisResult other) {
                    return null;
                }
            };
        }


        @Override
        public AnalysisResult getAnalysisResult() {
            return analysisResult;
        }
    }
}
