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
 *  KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package mule.v4;

import mule.MuleMigrator.MuleVersion;
import mule.common.MuleLogger;
import mule.common.apispec.ApiContractCheck;
import mule.common.apispec.ApiSpecResult;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public class ApiSpecContextTest {

    private static final Path API_KIT_PROJECT = Path.of("src/test/resources/mule/v4/projects/apiKitProject");
    private static final Path MULE_APP_DIR = API_KIT_PROJECT.resolve("src/main/mule");

    @Test
    public void testLoadsSpecOfEachApiKitConfigAfterParsing() {
        Context ctx = parse(MULE_APP_DIR);

        ApiSpecResult.Loaded loaded = (ApiSpecResult.Loaded) ctx.projectCtx.apiSpecs.get("apikit-config");
        Assert.assertEquals(loaded.specFile(),
                API_KIT_PROJECT.toAbsolutePath().resolve("src/main/resources/api/test.raml"));
        Assert.assertEquals(loaded.spec().title(), "Orders API");
        Assert.assertEquals(loaded.spec().operations().stream().map(op -> op.method() + " " + op.path()).toList(),
                List.of("get /orders"));
    }

    @Test
    public void testChecksFlowsAgainstSpec() {
        Context ctx = parse(MULE_APP_DIR);

        Assert.assertEquals(ctx.migrationMetrics.apiContractChecks, List.of(new ApiContractCheck.Checked(
                "apikit-config", "resources:test.raml", "src/main/resources/api/test.raml", List.of(),
                List.of("GET /orders"), List.of(new ApiContractCheck.FlowIssue("get:\\orders\\(id):apikit-config",
                "The spec declares no GET /orders/{id} operation; the closest is GET /orders, which takes id as "
                        + "query parameters.")), List.of())));
    }

    @Test
    public void testSkipsSpecLookupOutsideProjectLayout() {
        Context ctx = parse(API_KIT_PROJECT);

        Assert.assertTrue(ctx.projectCtx.apiSpecs.get("apikit-config") instanceof ApiSpecResult.Unavailable);
        ApiContractCheck.Unchecked unchecked = (ApiContractCheck.Unchecked) ctx.migrationMetrics.apiContractChecks
                .get(0);
        Assert.assertEquals(unchecked.flowCount(), 1);
    }

    private static Context parse(Path muleAppDir) {
        List<File> xmlFiles = List.of("globals.xml", "main.xml", "flow.xml").stream()
                .map(name -> MULE_APP_DIR.resolve(name).toFile())
                .toList();
        Context ctx = new Context(xmlFiles, List.of(), muleAppDir, MuleVersion.MULE_V4, List.of(), "apiKitProject",
                true, false, new MuleLogger(false), null, null);
        ctx.parseAllFiles();
        return ctx;
    }
}
