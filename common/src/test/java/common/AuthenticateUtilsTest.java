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

package common;

import org.testng.Assert;
import org.testng.annotations.Test;

public class AuthenticateUtilsTest {

    @Test
    public void testProductionEnvironmentConfiguration() {
        AuthenticateUtils.Config prodConfig = new AuthenticateUtils.Config(false, "TestTool");

        Assert.assertEquals(prodConfig.authOrg(), "ballerinacopilot");
        Assert.assertEquals(prodConfig.authClientId(), "9rKng8hSZd0VkeA45Lt4LOfCp9Aa");
        Assert.assertEquals(prodConfig.authRedirectUrl(),
                "https://98c70105-822c-4359-8579-4da58f0ab4b7.e1-us-east-azure.choreoapps.dev");
    }

    @Test
    public void testDevelopmentEnvironmentConfiguration() {
        AuthenticateUtils.Config devConfig = new AuthenticateUtils.Config(true, "TestTool");

        Assert.assertEquals(devConfig.authOrg(), "ballerinacopilotdev");
        Assert.assertEquals(devConfig.authClientId(), "XpQ6lphi7kjKkWzumYyqqNf7CjIa");
        Assert.assertEquals(devConfig.authRedirectUrl(),
                "https://98c70105-822c-4359-8579-4da58f0ab4b7.e1-us-east-azure.choreoapps.dev");
    }

    @Test
    public void testCommonConfigurationValues() {
        AuthenticateUtils.Config config = new AuthenticateUtils.Config(false, "TestTool");

        Assert.assertEquals(config.ballerinaUserHomeName(), ".ballerina");
        Assert.assertEquals(config.authenticationTimeoutSeconds(), 180);
        Assert.assertEquals(config.configFilePath(), "migrate-tool.toml");
    }

    @Test
    public void testConfigurationConsistencyBetweenEnvironments() {
        AuthenticateUtils.Config prodConfig = new AuthenticateUtils.Config(false, "TestTool");
        AuthenticateUtils.Config devConfig = new AuthenticateUtils.Config(true, "TestTool");

        Assert.assertEquals(prodConfig.ballerinaUserHomeName(), devConfig.ballerinaUserHomeName());
        Assert.assertEquals(prodConfig.authenticationTimeoutSeconds(), devConfig.authenticationTimeoutSeconds());
        Assert.assertEquals(prodConfig.configFilePath(), devConfig.configFilePath());
        Assert.assertEquals(prodConfig.authRedirectUrl(), devConfig.authRedirectUrl());
    }

    @Test
    public void testToolNamePreservation() {
        String testToolName = "MyCustomTool";
        AuthenticateUtils.Config config = new AuthenticateUtils.Config(false, testToolName);

        Assert.assertEquals(config.toolName(), testToolName);
    }
}
