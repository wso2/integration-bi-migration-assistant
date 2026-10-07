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
package mule.common.report;

import mule.MuleMigrator.MuleVersion;
import mule.common.MigrationMetrics;
import mule.common.MuleLogger;
import mule.common.apispec.ApiContractCheck;
import mule.v4.dataweave.converter.DWConstruct;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class ApiContractReportTest {

    @Test
    public void testRendersApiContractSection() {
        MigrationMetrics<DWConstruct> metrics = new MigrationMetrics<>();
        metrics.apiContractChecks.add(new ApiContractCheck.Checked("orders-config", "api\\orders.raml",
                "src/main/resources/api/orders.raml", List.of("GET /orders/{id}"), List.of("POST /orders"),
                List.of(new ApiContractCheck.FlowIssue("put:\\carts:orders-config",
                        "The spec declares no PUT /carts operation.")),
                List.of("API 1829 in API Manager (api-gateway:autodiscovery); check its policies there",
                        "client-id-enforcement trait of the API spec, on 2 of 2 operations")));
        metrics.apiContractChecks.add(new ApiContractCheck.Unchecked("narvar-config",
                "resource::g:a:1.0.5:raml:zip:a.raml", "Exchange spec g:a:1.0.5 was not found locally", 2,
                List.of()));

        String html = render(metrics);

        Assert.assertTrue(html.contains("<h2>API Contracts</h2>"));
        Assert.assertTrue(html.contains("<h3>orders-config</h3>"));
        assertRow(html, "Implemented operations", "<code>GET /orders/{id}</code>");
        assertRow(html, "Operations without a flow", "<code>POST /orders</code>");
        assertRow(html, "Flow issue",
                "<code>put:\\carts:orders-config</code><br>The spec declares no PUT /carts operation.");
        assertRow(html, "Not checked", "Exchange spec g:a:1.0.5 was not found locally");
        assertRow(html, "APIkit flows", "2, migrated with routes taken from their flow names");
        assertRow(html, "API Manager policies", "API 1829 in API Manager (api-gateway:autodiscovery); check its "
                + "policies there<br>client-id-enforcement trait of the API spec, on 2 of 2 operations<br>"
                + "The migrated service does not enforce these; see the TODO on its service.");
        Assert.assertEquals(html.split("API Manager policies", -1).length, 2, "Only orders-config has policies");
    }

    private static void assertRow(String html, String heading, String valueHtml) {
        String row = "<tr><th>" + heading + "</th><td>" + valueHtml + "</td></tr>";
        Assert.assertTrue(html.contains(row), "Missing row: " + row);
    }

    @Test
    public void testOmitsApiContractSectionWithoutApiKitConfigs() {
        Assert.assertFalse(render(new MigrationMetrics<DWConstruct>()).contains("API Contracts"));
    }

    private static String render(MigrationMetrics<DWConstruct> metrics) {
        return IndividualReportGenerator.generateHtmlReport(new MuleLogger(false),
                IndividualReportGenerator.getProjectMigrationStats(MuleVersion.MULE_V4, metrics), MuleVersion.MULE_V4,
                true, "orders");
    }
}
