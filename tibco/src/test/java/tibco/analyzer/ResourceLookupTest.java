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

package tibco.analyzer;

import org.testng.Assert;
import org.testng.annotations.Test;
import tibco.ConversionContext;
import tibco.ProjectConversionContext;
import tibco.converter.ProjectConverter.ProjectResources;
import tibco.model.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ResourceLookupTest {

    @Test
    public void testResourceLookupInProjectResources() {
        // Create test resources
        Resource.JDBCResource localResource = new Resource.JDBCResource(
                "testJdbc", "/test/path", "user", "pass", "driver", "url", new ArrayList<>());

        ProjectResources projectResources = new ProjectResources(
                List.of(localResource), new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        // Create contexts
        ConversionContext conversionContext = new ConversionContext("test-org", false, false,
                System.out::println, System.out::println);
        ProjectConversionContext projectConversionContext =
                new ProjectConversionContext(conversionContext, "test-project");
        ProjectAnalysisContext analysisContext = new ProjectAnalysisContext(projectConversionContext, projectResources);

        // Test lookup
        Resource.ResourceIdentifier identifier =
                new Resource.ResourceIdentifier(Resource.ResourceKind.JDBC, "/test/path");
        Optional<Resource> found = analysisContext.lookupResource(identifier);

        Assert.assertTrue(found.isPresent());
        Assert.assertEquals(found.get(), localResource);
    }

    @Test
    public void testResourceLookupInConversionContext() {
        // Create test resources
        Resource.HTTPClientResource globalResource = new Resource.HTTPClientResource(
                "testHttp", "/global/path", Optional.empty(), new ArrayList<>());

        // Create contexts
        ConversionContext conversionContext = new ConversionContext("test-org", false, false,
                System.out::println, System.out::println);
        ProjectResources globalProjectResources = new ProjectResources(
                new ArrayList<>(), new ArrayList<>(), List.of(globalResource),
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        conversionContext.addProjectResources(globalProjectResources);

        ProjectConversionContext projectConversionContext =
                new ProjectConversionContext(conversionContext, "test-project");
        ProjectAnalysisContext analysisContext = new ProjectAnalysisContext(projectConversionContext,
                new ProjectResources(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                        new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));

        // Test lookup
        Resource.ResourceIdentifier identifier =
                new Resource.ResourceIdentifier(Resource.ResourceKind.HTTP_CLIENT, "/global/path");
        Optional<Resource> found = analysisContext.lookupResource(identifier);

        Assert.assertTrue(found.isPresent());
        Assert.assertEquals(found.get(), globalResource);

        // Verify resource was captured
        Assert.assertEquals(analysisContext.capturedResources().httpClientResources().size(), 1);
        Assert.assertEquals(analysisContext.capturedResources().httpClientResources().iterator().next(),
                globalResource);
    }

    @Test
    public void testResourceLookupNotFound() {
        // Create contexts
        ConversionContext conversionContext = new ConversionContext("test-org", false, false,
                System.out::println, System.out::println);
        ProjectConversionContext projectConversionContext =
                new ProjectConversionContext(conversionContext, "test-project");
        ProjectAnalysisContext analysisContext = new ProjectAnalysisContext(projectConversionContext,
                new ProjectResources(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                        new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));

        // Test lookup for non-existent resource
        Resource.ResourceIdentifier identifier =
                new Resource.ResourceIdentifier(Resource.ResourceKind.JDBC, "/nonexistent/path");
        Optional<Resource> found = analysisContext.lookupResource(identifier);

        Assert.assertFalse(found.isPresent());
    }

    @Test
    public void testProcessAnalysisContextDelegation() {
        // Create test resources
        Resource.JMSSharedResource resource = new Resource.JMSSharedResource(
                "testJms", "/test/path", Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                new java.util.HashMap<>());

        ProjectResources projectResources = new ProjectResources(
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>(), List.of(resource), new ArrayList<>());

        // Create contexts
        ConversionContext conversionContext = new ConversionContext("test-org", false, false,
                System.out::println, System.out::println);
        ProjectConversionContext projectConversionContext =
                new ProjectConversionContext(conversionContext, "test-project");
        ProjectAnalysisContext projectAnalysisContext =
                new ProjectAnalysisContext(projectConversionContext, projectResources);
        ProcessAnalysisContext processAnalysisContext = new ProcessAnalysisContext(projectAnalysisContext);

        // Test lookup through ProcessAnalysisContext
        Resource.ResourceIdentifier identifier =
                new Resource.ResourceIdentifier(Resource.ResourceKind.JMS_SHARED, "/test/path");
        Optional<Resource> found = processAnalysisContext.lookupResource(identifier);

        Assert.assertTrue(found.isPresent());
        Assert.assertEquals(found.get(), resource);
    }
}
