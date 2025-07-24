package synapse.converter.tools;

import org.testng.Assert;
import org.testng.annotations.Test;

public class ReportGenerationTest {
    private final ReportGeneration tool = new ReportGeneration();
    private final ToolContext dummyContext = new DummyToolContext();

    static class DummyToolContext extends ToolContext {

        DummyToolContext() {
            super(null);
        }

        @Override
        public void log(common.LoggingUtils.Level level, String message) {
        }

        @Override
        public void logState(String message) {
        }

        @Override
        public String projectPath() {
            return ".";
        }

        @Override
        public String targetPath() {
            return ".";
        }
    }

    private String normalizeJson(String json) {
        return json.replaceAll("\\s+", "");
    }

    @Test
    public void testValidPayload() {
        String payload = """
            {
                "overall_confidence": 0.9,
                "mediators": [
                    {
                        "name": "HTTP",
                        "instances": 3,
                        "confidence_score": 0.8,
                        "complexity_score": 0.7
                    },
                    {
                        "name": "Database",
                        "instances": 2,
                        "confidence_score": 0.9,
                        "complexity_score": 0.6
                    }
                ]
            }
            """;
        String result = tool.execute(dummyContext, normalizeJson(payload));
        Assert.assertEquals(result, "Payload validated successfully.");
    }
    @Test
    public void testMissingOverallConfidence() {
        String payload = """
            {
                "mediators": []
            }
            """;
        String result = tool.execute(dummyContext, normalizeJson(payload));
        Assert.assertTrue(result.contains("Error: 'overall_confidence'"));
    }

    @Test
    public void testOverallConfidenceOutOfRange() {
        String payload = """
            {
                "overall_confidence": 1.5,
                "mediators": []
            }
            """;
        String result = tool.execute(dummyContext, normalizeJson(payload));
        Assert.assertTrue(result.contains("Error: 'overall_confidence' must be between 0 and 1"));
    }

    @Test
    public void testMediatorsMissing() {
        String payload = """
            {
                "overall_confidence": 0.5
            }
            """;
        String result = tool.execute(dummyContext, normalizeJson(payload));
        Assert.assertTrue(result.contains("Error: 'mediators' is required"));
    }

    @Test
    public void testMediatorsNotArray() {
        String payload = """
            {
                "overall_confidence": 0.5,
                "mediators": {}
            }
            """;
        String result = tool.execute(dummyContext, normalizeJson(payload));
        Assert.assertTrue(result.contains("Error: 'mediators' must be an array"));
    }

    @Test
    public void testMediatorMissingName() {
        String payload = """
            {
                "overall_confidence": 0.5,
                "mediators": [
                    {
                        "instances": 1,
                        "confidence_score": 0.5,
                        "complexity_score": 0.5
                    }
                ]
            }
            """;
        String result = tool.execute(dummyContext, normalizeJson(payload));
        Assert.assertTrue(result.contains("Error: Mediator at index 0 missing or empty 'name'"));
    }

    @Test
    public void testMediatorInstancesNotPositive() {
        String payload = """
            {
                "overall_confidence": 0.5,
                "mediators": [
                    {
                        "name": "HTTP",
                        "instances": 0,
                        "confidence_score": 0.5,
                        "complexity_score": 0.5
                    }
                ]
            }
            """;
        String result = tool.execute(dummyContext, normalizeJson(payload));
        Assert.assertTrue(result.contains("Error: Mediator at index 0 must have 'instances' > 0"));
    }

    @Test
    public void testMediatorConfidenceScoreOutOfRange() {
        String payload = """
            {
                "overall_confidence": 0.5,
                "mediators": [
                    {
                        "name": "HTTP",
                        "instances": 1,
                        "confidence_score": 1.5,
                        "complexity_score": 0.5
                    }
                ]
            }
            """;
        String result = tool.execute(dummyContext, normalizeJson(payload));
        Assert.assertTrue(result.contains("Error: Mediator at index 0 must have 'confidence_score' between 0 and 1"));
    }

    @Test
    public void testMediatorComplexityScoreOutOfRange() {
        String payload = """
            {
                "overall_confidence": 0.5,
                "mediators": [
                    {
                        "name": "HTTP",
                        "instances": 1,
                        "confidence_score": 0.5,
                        "complexity_score": -0.1
                    }
                ]
            }
            """;
        String result = tool.execute(dummyContext, normalizeJson(payload));
        Assert.assertTrue(result.contains("Error: Mediator at index 0 must have 'complexity_score' between 0 and 1"));
    }

    @Test
    public void testReportParsingOutput() {
        String payload = """
                {
                    "overall_confidence": 0.85,
                    "mediators": [
                        {
                            "name": "HTTP",
                            "instances": 2,
                            "confidence_score": 0.9,
                            "complexity_score": 0.3
                        },
                        {
                            "name": "DB",
                            "instances": 1,
                            "confidence_score": 0.7,
                            "complexity_score": 0.5
                        }
                    ]
                }
                """;
        com.google.gson.JsonObject json = com.google.gson.JsonParser.parseString(payload).getAsJsonObject();
        ReportGeneration.Report report = ReportGeneration.parseRequest(json);
        Assert.assertEquals(report.overallConfidence(), 0.85);
        Assert.assertEquals(report.mediators().length, 2);
        Assert.assertEquals(report.mediators()[0].name(), "HTTP");
        Assert.assertEquals(report.mediators()[0].instances(), 2);
        Assert.assertEquals(report.mediators()[0].confidenceScore(), 0.9);
        Assert.assertEquals(report.mediators()[0].complexityScore(), 0.3);
        Assert.assertEquals(report.mediators()[1].name(), "DB");
        Assert.assertEquals(report.mediators()[1].instances(), 1);
        Assert.assertEquals(report.mediators()[1].confidenceScore(), 0.7);
        Assert.assertEquals(report.mediators()[1].complexityScore(), 0.5);
    }
}
