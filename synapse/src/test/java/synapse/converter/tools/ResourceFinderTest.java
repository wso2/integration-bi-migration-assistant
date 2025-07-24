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

package synapse.converter.tools;

import common.LoggingUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import synapse.converter.ConversionContext;

import java.nio.file.Paths;

class TestConversionContext extends ConversionContext {

    public TestConversionContext(String projectPath) {
        super(projectPath);
    }

    @Override
    public void log(LoggingUtils.Level level, String message) {
        // No-op for testing
    }

    @Override
    public void logState(String message) {
        // No-op for testing
    }
}

public class ResourceFinderTest {

    @Test
    public void testToolMetadata() {
        ResourceFinder resourceFinder = new ResourceFinder();

        Assert.assertEquals(resourceFinder.name(), "get_resource", "Tool name should be 'get_resource'");
        Assert.assertEquals(resourceFinder.description(), "Find resources such as sequences, endpoints by name",
                "Tool description should match expected value");

        String inputSchema = resourceFinder.inputSchema();
        Assert.assertTrue(inputSchema.contains("\"name\""), "Input schema should contain 'name' property");
        Assert.assertTrue(inputSchema.contains("\"kind\""), "Input schema should contain 'kind' property");
        Assert.assertTrue(inputSchema.contains("\"required\""), "Input schema should specify required fields");
    }

    @Test
    public void testFindApiResource() {
        ResourceFinder resourceFinder = new ResourceFinder();

        String projectPath = Paths.get("src/test/resources/projects/ContentBasedRouting").toAbsolutePath().toString();
        TestConversionContext conversionContext = new TestConversionContext(projectPath);
        ToolContext toolContext = new ToolContext(conversionContext);

        String request = """
                {
                    "name": "ArithmaticOperationServiceAPI",
                    "kind": "api"
                }
                """;

        String result = resourceFinder.execute(toolContext, request);

        Assert.assertTrue(result.contains("ArithmaticOperationServiceAPI"), 
                "Should contain XML content with the API name");
        Assert.assertTrue(result.startsWith("<?xml"), "Should return XML file content");
    }

    @Test
    public void testFindEndpointResource() {
        ResourceFinder resourceFinder = new ResourceFinder();

        String projectPath = Paths.get("src/test/resources/projects/ContentBasedRouting").toAbsolutePath().toString();
        TestConversionContext conversionContext = new TestConversionContext(projectPath);
        ToolContext toolContext = new ToolContext(conversionContext);

        String request = """
                {
                    "name": "NumberAdditionEP",
                    "kind": "endpoint"
                }
                """;

        String result = resourceFinder.execute(toolContext, request);

        Assert.assertTrue(result.contains("NumberAdditionEP"), "Should contain XML content with the endpoint name");
        Assert.assertTrue(result.startsWith("<?xml"), "Should return XML file content");
    }

    @Test
    public void testFindSequenceResource() {
        ResourceFinder resourceFinder = new ResourceFinder();

        String projectPath = Paths.get("src/test/resources/projects/DatabasePolling").toAbsolutePath().toString();
        TestConversionContext conversionContext = new TestConversionContext(projectPath);
        ToolContext toolContext = new ToolContext(conversionContext);

        String request = """
                {
                    "name": "DoctorsRecordsSyncSeq",
                    "kind": "sequence"
                }
                """;

        String result = resourceFinder.execute(toolContext, request);

        Assert.assertTrue(result.contains("DoctorsRecordsSyncSeq"), 
                "Should contain XML content with the sequence name");
        Assert.assertTrue(result.startsWith("<?xml"), "Should return XML file content");
    }

    @Test
    public void testFindLocalEntryResource() {
        ResourceFinder resourceFinder = new ResourceFinder();

        String projectPath = Paths.get("src/test/resources/projects/EmailService").toAbsolutePath().toString();
        TestConversionContext conversionContext = new TestConversionContext(projectPath);
        ToolContext toolContext = new ToolContext(conversionContext);

        String request = """
                {
                    "name": "imapsconnection",
                    "kind": "localEntry"
                }
                """;

        String result = resourceFinder.execute(toolContext, request);

        Assert.assertTrue(result.contains("imapsconnection"), "Should contain XML content with the local entry key");
        Assert.assertTrue(result.startsWith("<?xml"), "Should return XML file content");
    }

    @Test
    public void testFindMessageStoreResource() {
        ResourceFinder resourceFinder = new ResourceFinder();

        String projectPath = Paths.get("src/test/resources/projects/GuaranteedDelivery").toAbsolutePath().toString();
        TestConversionContext conversionContext = new TestConversionContext(projectPath);
        ToolContext toolContext = new ToolContext(conversionContext);

        String request = """
                {
                    "name": "UserRegistrationMS",
                    "kind": "messageStore"
                }
                """;

        String result = resourceFinder.execute(toolContext, request);

        Assert.assertTrue(result.contains("UserRegistrationMS"), 
                "Should contain XML content with the message store name");
        Assert.assertTrue(result.startsWith("<?xml"), "Should return XML file content");
    }

    @Test
    public void testFindMessageProcessorResource() {
        ResourceFinder resourceFinder = new ResourceFinder();

        String projectPath = Paths.get("src/test/resources/projects/GuaranteedDelivery").toAbsolutePath().toString();
        TestConversionContext conversionContext = new TestConversionContext(projectPath);
        ToolContext toolContext = new ToolContext(conversionContext);

        String request = """
                {
                    "name": "UserRegistrationMP",
                    "kind": "messageProcessor"
                }
                """;

        String result = resourceFinder.execute(toolContext, request);

        Assert.assertTrue(result.contains("UserRegistrationMP"), 
                "Should contain XML content with the message processor name");
        Assert.assertTrue(result.startsWith("<?xml"), "Should return XML file content");
    }

    @Test
    public void testFindTaskResource() {
        ResourceFinder resourceFinder = new ResourceFinder();

        String projectPath = Paths.get("src/test/resources/projects/DatabasePolling").toAbsolutePath().toString();
        TestConversionContext conversionContext = new TestConversionContext(projectPath);
        ToolContext toolContext = new ToolContext(conversionContext);

        String request = """
                {
                    "name": "DoctorsRecordsSyncTask",
                    "kind": "task"
                }
                """;

        String result = resourceFinder.execute(toolContext, request);

        Assert.assertTrue(result.contains("DoctorsRecordsSyncTask"), "Should contain XML content with the task name");
        Assert.assertTrue(result.startsWith("<?xml"), "Should return XML file content");
    }

    @Test
    public void testFindInboundEndpointResource() {
        ResourceFinder resourceFinder = new ResourceFinder();

        String projectPath = Paths.get("src/test/resources/projects/FileTransfer").toAbsolutePath().toString();
        TestConversionContext conversionContext = new TestConversionContext(projectPath);
        ToolContext toolContext = new ToolContext(conversionContext);

        String request = """
                {
                    "name": "StudentDataFileProcessInboundEP",
                    "kind": "inboundEndpoint"
                }
                """;

        String result = resourceFinder.execute(toolContext, request);

        Assert.assertTrue(result.contains("StudentDataFileProcessInboundEP"),
                "Should contain XML content with the inbound endpoint name");
        Assert.assertTrue(result.startsWith("<?xml"), "Should return XML file content");
    }

    @Test
    public void testResourceNotFound() {
        ResourceFinder resourceFinder = new ResourceFinder();

        String projectPath = Paths.get("src/test/resources/projects/ContentBasedRouting").toAbsolutePath().toString();
        TestConversionContext conversionContext = new TestConversionContext(projectPath);
        ToolContext toolContext = new ToolContext(conversionContext);

        String request = """
                {
                    "name": "NonExistentResource",
                    "kind": "api"
                }
                """;

        String result = resourceFinder.execute(toolContext, request);

        Assert.assertTrue(result.startsWith("Couldn't find"), "Should indicate resource not found");
        Assert.assertTrue(result.contains("NonExistentResource"), "Should mention the resource name");
        Assert.assertTrue(result.contains("api"), "Should mention the resource kind");
    }

    @Test
    public void testInvalidKind() {
        ResourceFinder resourceFinder = new ResourceFinder();

        String projectPath = Paths.get("src/test/resources/projects/ContentBasedRouting").toAbsolutePath().toString();
        TestConversionContext conversionContext = new TestConversionContext(projectPath);
        ToolContext toolContext = new ToolContext(conversionContext);

        String request = """
                {
                    "name": "SomeResource",
                    "kind": "invalidKind"
                }
                """;

        String result = resourceFinder.execute(toolContext, request);

        Assert.assertTrue(result.startsWith("Error: Unsupported resource kind"), "Should indicate unsupported kind");
        Assert.assertTrue(result.contains("invalidKind"), "Should mention the invalid kind");
    }

    @Test
    public void testMissingNameParameter() {
        ResourceFinder resourceFinder = new ResourceFinder();

        String projectPath = Paths.get("src/test/resources/projects/ContentBasedRouting").toAbsolutePath().toString();
        TestConversionContext conversionContext = new TestConversionContext(projectPath);
        ToolContext toolContext = new ToolContext(conversionContext);

        String request = """
                {
                    "kind": "api"
                }
                """;

        String result = resourceFinder.execute(toolContext, request);

        Assert.assertTrue(result.startsWith("Error: 'name' parameter is required"),
                "Should indicate missing name parameter");
    }

    @Test
    public void testMissingKindParameter() {
        ResourceFinder resourceFinder = new ResourceFinder();

        String projectPath = Paths.get("src/test/resources/projects/ContentBasedRouting").toAbsolutePath().toString();
        TestConversionContext conversionContext = new TestConversionContext(projectPath);
        ToolContext toolContext = new ToolContext(conversionContext);

        String request = """
                {
                    "name": "SomeResource"
                }
                """;

        String result = resourceFinder.execute(toolContext, request);

        Assert.assertTrue(result.startsWith("Error: 'kind' parameter is required"),
                "Should indicate missing kind parameter");
    }

    @Test
    public void testEmptyNameParameter() {
        ResourceFinder resourceFinder = new ResourceFinder();

        String projectPath = Paths.get("src/test/resources/projects/ContentBasedRouting").toAbsolutePath().toString();
        TestConversionContext conversionContext = new TestConversionContext(projectPath);
        ToolContext toolContext = new ToolContext(conversionContext);

        String request = """
                {
                    "name": "",
                    "kind": "api"
                }
                """;

        String result = resourceFinder.execute(toolContext, request);

        Assert.assertTrue(result.startsWith("Error: Resource name cannot be empty"),
                "Should indicate empty name parameter");
    }

    @Test
    public void testWhitespaceOnlyNameParameter() {
        ResourceFinder resourceFinder = new ResourceFinder();

        String projectPath = Paths.get("src/test/resources/projects/ContentBasedRouting").toAbsolutePath().toString();
        TestConversionContext conversionContext = new TestConversionContext(projectPath);
        ToolContext toolContext = new ToolContext(conversionContext);

        String request = """
                {
                    "name": "   ",
                    "kind": "api"
                }
                """;

        String result = resourceFinder.execute(toolContext, request);

        Assert.assertTrue(result.startsWith("Error: Resource name cannot be empty"),
                "Should indicate empty name parameter when only whitespace");
    }

    @Test
    public void testInvalidJson() {
        ResourceFinder resourceFinder = new ResourceFinder();

        String projectPath = Paths.get("src/test/resources/projects/ContentBasedRouting").toAbsolutePath().toString();
        TestConversionContext conversionContext = new TestConversionContext(projectPath);
        ToolContext toolContext = new ToolContext(conversionContext);

        String request = "{ invalid json }";

        String result = resourceFinder.execute(toolContext, request);

        Assert.assertTrue(result.startsWith("Error: Invalid JSON input"),
                "Should indicate JSON parsing error, but got: " + result);
    }

    @Test
    public void testNonExistentProjectDirectory() {
        ResourceFinder resourceFinder = new ResourceFinder();

        String projectPath = "/non/existent/path";
        TestConversionContext conversionContext = new TestConversionContext(projectPath);
        ToolContext toolContext = new ToolContext(conversionContext);

        String request = """
                {
                    "name": "SomeResource",
                    "kind": "api"
                }
                """;

        String result = resourceFinder.execute(toolContext, request);

        Assert.assertTrue(result.startsWith("Error: Project directory does not exist"),
                "Should indicate non-existent project directory");
    }

    @Test
    public void testCaseInsensitivity() {
        ResourceFinder resourceFinder = new ResourceFinder();

        String projectPath = Paths.get("src/test/resources/projects/ContentBasedRouting").toAbsolutePath().toString();
        TestConversionContext conversionContext = new TestConversionContext(projectPath);
        ToolContext toolContext = new ToolContext(conversionContext);

        String request = """
                {
                    "name": "arithmaticopeRationServiceAPI",
                    "kind": "API"
                }
                """;

        String result = resourceFinder.execute(toolContext, request);

        Assert.assertTrue(result.contains("ArithmaticOperationServiceAPI"), 
                "Should find resource with different case and return XML content");
        Assert.assertTrue(result.startsWith("<?xml"), "Should return XML file content");
    }

    @Test
    public void testLocalEntryUsesKeyAttribute() {
        ResourceFinder resourceFinder = new ResourceFinder();

        String projectPath = Paths.get("src/test/resources/projects/EmailService").toAbsolutePath().toString();
        TestConversionContext conversionContext = new TestConversionContext(projectPath);
        ToolContext toolContext = new ToolContext(conversionContext);

        // Test finding by key attribute (should succeed)
        String request1 = """
                {
                    "name": "imapsconnection",
                    "kind": "localEntry"
                }
                """;

        String result1 = resourceFinder.execute(toolContext, request1);
        Assert.assertTrue(result1.contains("imapsconnection"), "Should find local entry by key attribute");
        Assert.assertTrue(result1.startsWith("<?xml"), "Should return XML file content");

        // Test that it doesn't find by name attribute if it had one
        String request2 = """
                {
                    "name": "nonExistentKey",
                    "kind": "localEntry"
                }
                """;

        String result2 = resourceFinder.execute(toolContext, request2);
        Assert.assertTrue(result2.startsWith("Couldn't find"),
                "Should not find local entry with non-existent key");
    }

    @Test
    public void testExcludesTestFiles() {
        ResourceFinder resourceFinder = new ResourceFinder();

        String projectPath = Paths.get("src/test/resources/projects/ContentBasedRouting").toAbsolutePath().toString();
        TestConversionContext conversionContext = new TestConversionContext(projectPath);
        ToolContext toolContext = new ToolContext(conversionContext);

        // This test file exists but should be excluded from search
        String request = """
                {
                    "name": "ArithmaticOperationServiceTest",
                    "kind": "unit-test"
                }
                """;

        String result = resourceFinder.execute(toolContext, request);

        Assert.assertTrue(
                result.startsWith("Error: Unsupported resource kind") || result.startsWith("Couldn't find"),
                "Should not find test files or should indicate unsupported kind");
    }
}
