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
package mule.report;

/**
 * Contains HTML template for aggregate migration report.
 *
 * @since 1.1.1
 */
public class AggregateReportTemplate {

    public static String getHtmlTemplate() {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                  <title>%s</title>
                  <style>
                    body { font-family: Arial, sans-serif; background-color: #f4f4f9; color: #333; margin: 0; padding: 20px; }
                    .container { max-width: 1200px; margin: 0 auto; }
                    table { width: 100%%; border-collapse: collapse; margin: 20px 0; }
                    th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }
                    th { background-color: #4682B4; color: white; }
                    tr:nth-child(even) { background-color: #f2f2f2; }
                    tr:hover { background-color: #ddd; }
                    h1, h2 { text-align: center; color: #333; }
                    footer { text-align: center; margin-top: 20px; font-size: 0.9em; color: #666; }
                    .summary-container { background-color: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); margin: 20px 0; }
                    .project-card {
                      background-color: #fff;
                      border-radius: 8px;
                      box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
                      margin: 15px 0;
                      padding: 15px;
                      display: flex;
                      justify-content: space-between;
                      align-items: center;
                    }
                    .project-name { font-weight: bold; font-size: 1.1em; }
                    .coverage-indicator {
                      width: 100px;
                      height: 10px;
                      background-color: #f0f0f0;
                      border-radius: 5px;
                      overflow: hidden;
                    }
                    .coverage-bar {
                      height: 100%%;
                      background-color: #4CAF50;
                    }
                    .metrics { display: flex; gap: 20px; margin-top: 10px; }
                    .metric { display: flex; flex-direction: column; align-items: center; }
                    .metric-value { font-weight: bold; font-size: 1.2em; }
                    .metric-label { font-size: 0.8em; color: #666; }
                    .status-badge {
                      padding: 5px 10px;
                      border-radius: 12px;
                      font-size: 0.8em;
                      font-weight: bold;
                    }
                    .status-high { background-color: #e6f7e6; color: #2e7d32; }
                    .status-medium { background-color: #fff8e1; color: #ff8f00; }
                    .status-low { background-color: #ffebee; color: #c62828; }
                    .project-link {
                      color: #4682B4;
                      text-decoration: none;
                    }
                    .project-link:hover {
                      text-decoration: underline;
                    }
                  </style>
                </head>
                <body>
                  <div class="container">
                    <h1>%s</h1>

                    <div class="summary-container">
                      <h2>Overview</h2>
                      <div class="metrics">
                        <div class="metric">
                          <span class="metric-value">%d</span>
                          <span class="metric-label">Projects</span>
                        </div>
                        <div class="metric">
                          <span class="metric-value">%.0f%%</span>
                          <span class="metric-label">Avg. Migration Coverage</span>
                        </div>
                        <div class="metric">
                          <span class="metric-value">%d</span>
                          <span class="metric-label">Total Failed Elements</span>
                        </div>
                        <div class="metric">
                          <span class="metric-value">%d</span>
                          <span class="metric-label">Total Failed DataWeave</span>
                        </div>
                      </div>
                    </div>

                    <div class="summary-container">
                      <h2>MuleSoft Projects</h2>
                      %s
                    </div>

                    <div class="summary-container">
                      <h2>Elements Awaiting Tool Support</h2>
                      <div id="toolSupportSection">
                        <table>
                            <tr>
                            <th>Element Type</th>
                            <th>Frequency</th>
                            <th>Projects Affected</th>
                            </tr>
                            %s
                        </table>
                        <p class="empty-message" id="toolSupportEmpty"
                            style="display: none; text-align: center; padding: 20px; color: #666;">
                            No elements awaiting tool support
                        </p>
                      </div>
                    </div>

                    <div class="summary-container">
                      <h2>Implementation Time Estimation</h2>
                      <table>
                        <tr>
                          <th>Project</th>
                          <th>Best Case</th>
                          <th>Average Case</th>
                          <th>Worst Case</th>
                        </tr>
                        %s
                        <tr>
                          <th>Total</th>
                          <th>%.1f days</th>
                          <th>%.1f days</th>
                          <th>%.1f days</th>
                        </tr>
                      </table>
                    </div>
                  </div>

                  <footer>
                    <p>Report generated on: <span id="datetime"></span></p>
                  </footer>
                  <script>
                    document.addEventListener('DOMContentLoaded', function() {
                        // Check Elements Awaiting Tool Support
                        const toolSupportTable = document.querySelector('#toolSupportSection table');
                        if (toolSupportTable.rows.length <= 1) {
                            toolSupportTable.style.display = 'none';
                            document.getElementById('toolSupportEmpty').style.display = 'block';
                        }
                    });
                  </script>
                  <script>
                    document.getElementById("datetime").innerHTML = new Date().toLocaleString();
                  </script>
                </body>
                </html>
                """;
    }
}
