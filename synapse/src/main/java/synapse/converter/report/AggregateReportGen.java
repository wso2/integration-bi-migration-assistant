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

package synapse.converter.report;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

public class AggregateReportGen {

    public record ProjectData(Project project, String reportPath, int totalCodeLines) {

    }

    public String generateReport(Map<String, ProjectData> data, Estimation mediatorEstimate,
                                 Estimation lineEstimation) {
        StringBuilder html = new StringBuilder();
        
        html.append(generateHtmlHeader());
        html.append(generatePageTitle());
        html.append(generateOverviewSection(data.values(), mediatorEstimate, lineEstimation));
        html.append(generateProjectsSection(data, mediatorEstimate, lineEstimation));
        html.append(generateMediatorBreakdownSection(data.values()));
        html.append(generateFooter());
        html.append(generateHtmlFooter());
        
        return html.toString();
    }

    private record WorkEstimation(double bestCaseDays, double avgCaseDays, double worstCaseDays) {

        public int bestCaseWeeks() {
            return Math.max(1, (int) Math.ceil(bestCaseDays / 5));
        }

        public int avgCaseWeeks() {
            return Math.max(1, (int) Math.ceil(avgCaseDays / 5));
        }

        public int worstCaseWeeks() {
            return Math.max(1, (int) Math.ceil(worstCaseDays / 5));
        }

        static WorkEstimation combine(WorkEstimation we1, WorkEstimation we2) {
            return new WorkEstimation(
                    we1.bestCaseDays + we2.bestCaseDays,
                    we1.avgCaseDays + we2.avgCaseDays,
                    we1.worstCaseDays + we2.worstCaseDays
            );
        }
    }

    private static double totalConfidence(Collection<ProjectData> projects) {
        int totalLines = projects.stream().map(ProjectData::totalCodeLines).reduce(0, Integer::sum);
        double accumConfidence = projects.stream().map(each -> each.project.overallConfidence() * each.totalCodeLines)
                .reduce(0.0, Double::sum);
        return accumConfidence / totalLines;
    }

    private String generateHtmlHeader() {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                  <title>Aggregate Migration Summary</title>
                  <style>
                    %s
                    %s
                    %s
                    %s
                  </style>
                </head>
                <body>
                <div class="container">
                """.formatted(getBaseStyles(), getComponentStyles(), getLayoutStyles(), getThemeStyles());
    }

    private String getBaseStyles() {
        return """
                    body {
                      font-family: Arial, sans-serif;
                      background-color: #f4f4f9;
                      color: #333;
                      margin: 0;
                      padding: 20px;
                    }

                    .container {
                      max-width: 1200px;
                      margin: 0 auto;
                    }

                    h1, h2, h3 {
                      color: #333;
                    }
                
                    .status-badge-container {
                      margin-top: 5px;
                    }

                    h1 {
                      text-align: center;
                      color: #4682B4;
                      font-size: 2.5em;
                      font-weight: 300;
                      margin: 15px auto 40px;
                      padding: 0 0 15px;
                      max-width: fit-content;
                      position: relative;
                      border-bottom: 1px solid rgba(70, 130, 180, 0.2);
                    }

                    h1::after {
                      content: "";
                      position: absolute;
                      bottom: -1px;
                      left: 50%;
                      transform: translateX(-50%);
                      width: 100px;
                      height: 3px;
                      background-color: rgba(70, 130, 180, 0.8);
                    }

                    /* Footer */
                    footer {
                      text-align: center;
                      margin-top: 20px;
                      font-size: 0.9em;
                      color: #666;
                    }
                """;
    }

    private String getComponentStyles() {
        return """
                    .summary-container {
                      background-color: #fff;
                      padding: 25px;
                      border-radius: 10px;
                      box-shadow: 0 3px 10px rgba(0, 0, 0, 0.1);
                      margin: 25px 0;
                      transition: box-shadow 0.3s;
                    }

                    .summary-container:hover {
                      box-shadow: 0 5px 15px rgba(0, 0, 0, 0.15);
                    }

                    .summary-container h2 {
                      margin-top: 0;
                      color: #4682B4;
                      border-bottom: 2px solid #f0f0f0;
                      padding-bottom: 10px;
                      margin-bottom: 20px;
                      text-align: center;
                      font-size: 1.5em;
                    }

                    /* Metric styling */
                    .metric {
                      width: 100%;
                      box-sizing: border-box;
                      padding: 15px 20px;
                      display: flex;
                      flex-direction: row;
                      align-items: center;
                      gap: 20px;
                      background-color: #f0f8ff;
                      border-radius: 8px;
                      transition: transform 0.2s, box-shadow 0.2s;
                      margin-bottom: 15px;
                      border: 1px solid #eaeaea;
                      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
                    }

                    .metric:hover {
                      transform: translateY(-3px);
                      box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
                    }

                    .metric-value {
                      font-weight: bold;
                      font-size: 2em;
                      color: #4682B4;
                      margin-bottom: 5px;
                    }

                    .metric-label {
                      font-size: 1em;
                      margin-top: 5px;
                      color: #666;
                      text-align: center;
                    }

                    .metric-left {
                      flex: 1;
                      display: flex;
                      flex-direction: column;
                      align-items: center;
                    }

                    .metric-right {
                      flex: 1;
                      padding-top: 10px;
                    }

                    .coverage-breakdown {
                      font-size: 0.85em;
                      color: #666;
                    }

                    .breakdown-value {
                      font-weight: 600;
                    }

                    /* Coverage indicator */
                    .coverage-indicator {
                      width: 80%;
                      height: 6px;
                      background-color: #f0f0f0;
                      border-radius: 3px;
                      overflow: hidden;
                      box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.1);
                      margin-top: 8px;
                    }

                    .coverage-bar {
                      height: 100%;
                      border-radius: 3px;
                      transition: width 0.5s ease-in-out;
                    }
                """;
    }

    private String getLayoutStyles() {
        return """
                    /* Time estimates styling */
                    .time-estimates {
                      display: flex;
                      flex: 1;
                      gap: 10px;
                      background-color: #eff8ff;
                      border-radius: 8px;
                      padding: 15px;
                      padding-top: 30px;
                      position: relative;
                      transition: transform 0.2s, box-shadow 0.2s;
                      margin: 20px 0;
                      border: 1px solid #eaeaea;
                      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
                    }
                
                    .time-estimates:hover {
                      transform: translateY(-3px);
                      box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
                    }

                    .time-estimates::before {
                      content: "Manual Work Estimation";
                      position: absolute;
                      top: 8px;
                      left: 0;
                      width: 100%;
                      text-align: center;
                      font-weight: normal;
                      font-size: 0.9em;
                      color: #666;
                    }

                    .time-estimate {
                      display: flex;
                      flex: 1;
                      flex-direction: column;
                      align-items: center;
                    }

                    .time-label {
                      font-size: 0.8em;
                      color: #666;
                      margin-bottom: 5px;
                    }

                    .time-value {
                      font-weight: bold;
                      color: #4682B4;
                      display: flex;
                      flex-direction: column;
                      align-items: center;
                    }

                    .time-days {
                      font-size: 1.1em;
                    }

                    .time-weeks {
                      font-size: 0.75em;
                      color: #777;
                      margin-top: 2px;
                    }

                    .time-best {
                      color: #4CAF50;
                    }

                    .time-avg {
                      color: #4682B4;
                    }

                    .time-worst {
                      color: #FF5722;
                    }

                    /* Project cards */
                    .project-card {
                      background-color: #fff;
                      border-radius: 10px;
                      box-shadow: 0 3px 12px rgba(0, 0, 0, 0.08);
                      margin: 20px 0;
                      padding: 20px;
                      display: grid;
                      grid-template-columns: 3fr 2fr;
                      gap: 15px;
                      transition: transform 0.2s, box-shadow 0.2s;
                      align-items: center;
                    }

                    .project-card:hover {
                      transform: translateY(-2px);
                      box-shadow: 0 5px 15px rgba(0, 0, 0, 0.12);
                    }

                    .project-header {
                      display: flex;
                      align-items: center;
                      margin-bottom: 15px;
                      gap: 12px;
                    }

                    .project-name {
                      font-size: 1.2em;
                      font-weight: 600;
                      margin-right: 10px;
                    }

                    .project-link {
                      color: #4682B4;
                      text-decoration: none;
                      position: relative;
                      padding-bottom: 2px;
                    }

                    .project-link:after {
                      content: '';
                      position: absolute;
                      width: 0;
                      height: 2px;
                      bottom: 0;
                      left: 0;
                      background-color: #4682B4;
                      transition: width 0.3s;
                    }

                    .project-link:hover:after {
                      width: 100%;
                    }
                """;
    }

    private String getThemeStyles() {
        return """
                    /* Status badges */
                    .status-badge {
                      padding: 6px 12px;
                      border-radius: 20px;
                      font-size: 0.75em;
                      font-weight: 600;
                      letter-spacing: 0.3px;
                      text-transform: uppercase;
                      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
                      display: inline-block;
                    }

                    .status-high {
                      background-color: #e8f5e9;
                      color: #2e7d32;
                      border: 1px solid rgba(46, 125, 50, 0.2);
                    }

                    .status-medium {
                      background-color: #fff8e1;
                      color: #f57c00;
                      border: 1px solid rgba(245, 124, 0, 0.2);
                    }

                    .status-low {
                      background-color: #ffebee;
                      color: #c62828;
                      border: 1px solid rgba(198, 40, 40, 0.2);
                    }

                    /* Table styling */
                    table {
                      width: 100%;
                      border-collapse: collapse;
                      margin: 20px 0;
                    }

                    th, td {
                      border: 1px solid #ddd;
                      padding: 12px;
                      text-align: left;
                    }

                    th {
                      background-color: #4682B4;
                      color: white;
                    }

                    tr:nth-child(even) {
                      background-color: #f2f2f2;
                    }

                    tr:hover {
                      background-color: #ddd;
                    }

                    /* Estimation notes */
                    .estimation-notes {
                      margin-top: 25px;
                      padding: 20px;
                      background-color: #f8f9fa;
                      border-radius: 8px;
                      border-left: 4px solid #4682B4;
                      box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
                    }

                    .estimation-notes p {
                      margin-top: 0;
                    }

                    .estimation-notes ul {
                      margin: 15px 0 5px 25px;
                      padding-left: 0;
                    }

                    .estimation-notes li {
                      margin-bottom: 8px;
                      line-height: 1.4;
                    }
                """;
    }

    private String generatePageTitle() {
        return """
                  <h1>Aggregate Migration Summary</h1>
                """;
    }

    private String generateOverviewSection(Collection<ProjectData> projects, Estimation mediatorEstimate,
                                           Estimation lineEstimation) {
        int projectCount = projects.size();
        double avgConfidence = totalConfidence(projects);
        int totalLines = projects.stream().mapToInt(ProjectData::totalCodeLines).sum();
        
        WorkEstimation totalEstimation = calculateTotalWorkEstimation(projects, mediatorEstimate, lineEstimation);
        
        String overviewMetric = CommonComponents.generateMetric(avgConfidence, totalLines, 
                projects.stream().mapToInt(p -> p.project.mediators().length).sum());
        
        String timeEstimatesHtml = generateTimeEstimatesHtml(totalEstimation);
        String estimationNotes = CommonComponents.estimationNotes(mediatorEstimate, lineEstimation);
        
        return """
                  <div class="summary-container">
                    <h2>Overview</h2>
                    <div class="metrics" style="flex-direction: column; align-items: center; width: 100%%;">
                      <div class="metric" style="flex-direction: column; align-items: center; gap: 0px">
                        <span class="metric-value">%d</span>
                        <span class="metric-label">Projects Analyzed</span>
                      </div>
                      %s
                    </div>
                    %s
                    <div class="estimation-notes">
                      <p><strong>Note:</strong></p>
                      <ul>
                        <li>%d Synapse projects analyzed for migration to Ballerina</li>
                        <li>%.0f%% average automated conversion confidence across all projects</li>
                        <li>Time estimates shown above represents manual work required to complete migration for all
                        projects combined</li>
                      </ul>
                    </div>
                    %s
                  </div>
                """.formatted(projectCount, overviewMetric, timeEstimatesHtml, projectCount, 
                        avgConfidence * 100, estimationNotes);
    }

    private String generateProjectsSection(Map<String, ProjectData> data, Estimation mediatorEstimate,
                                           Estimation lineEstimation) {
        StringBuilder projectsHtml = new StringBuilder();
        projectsHtml.append("\n  <div class=\"projects-container\">\n");
        record ProjectGenData(String name, ProjectData data) {

        }
        data.keySet().stream().map(name -> new ProjectGenData(name, data.get(name)))
                .sorted(Comparator.comparingDouble(p -> -1 * p.data.project.overallConfidence()))
                .forEachOrdered(p -> projectsHtml.append(
                        generateProjectCard(p.name, p.data, mediatorEstimate, lineEstimation)));
        
        projectsHtml.append("  </div>\n");
        return projectsHtml.toString();
    }

    private String generateProjectCard(String projectName, ProjectData projectData, Estimation mediatorEstimate,
                                       Estimation lineEstimation) {
        Project project = projectData.project();
        double confidence = project.overallConfidence();
        String confidenceLevel = getConfidenceLevel(confidence);
        String badgeClass = getConfidenceBadgeClass(confidence);
        String coverageBarColor = getCoverageBarColor(confidence);
        int coverageWidth = (int) Math.round(confidence * 100);
        
        return """
                    <div class="project-card" style="display:flex; flex:1; flex-direction:column">
                      <div style="display:flex; flex:1; flex-direction:column; width:100%%">
                        <div class="project-header">
                          <div class="project-name">
                            <a href="%s" class="project-link">%s</a>
                          </div>
                          <span class="status-badge %s">%s</span>
                        </div>
                        <div style="display:flex; flex:1; flex-direction:row; justify-content: space-between; align-items: center;">
                          <div class="project-left" style="display:flex; flex:1; margin-right: 20px; margin-top: 20px;">
                            <div class="project-details" style="display:flex; flex:1">
                              <div class="project-metrics" style="display:flex; flex:1">
                                <div class="metric">
                                  <div class="metric-left">
                                    <span class="metric-value">%d%%</span>
                                    <span class="metric-label">Project Confidence</span>
                                    <div class="coverage-indicator">
                                      <div class="coverage-bar" style="width: %d%%; background-color: %s;"></div>
                                    </div>
                                  </div>
                                  <div class="metric-right">
                                    <div class="coverage-breakdown">
                                      <div style="display: flex; justify-content: space-between; margin-bottom: 4px;">
                                        <span class="breakdown-label">Total Code Lines:</span>
                                        <span class="breakdown-value">%d</span>
                                      </div>
                                      <div style="display: flex; justify-content: space-between; margin-bottom: 4px;">
                                        <span class="breakdown-label">Mediators:</span>
                                        <span class="breakdown-value">%d</span>
                                      </div>
                                    </div>
                                  </div>
                                </div>
                              </div>
                            </div>
                          </div>
                          %s
                          </div>
                        </div>
                    </div>
                """.formatted(projectData.reportPath(), projectName, badgeClass, confidenceLevel,
                        coverageWidth, coverageWidth, coverageBarColor, projectData.totalCodeLines(),
                project.mediators().length,
                timeEstimatesPerProject(calculateWorkEstimation(projectData, mediatorEstimate, lineEstimation)));
    }

    private String timeEstimatesPerProject(WorkEstimation workEstimation) {
        return """
                <div class="time-estimates" style="margin-left: 20px;">
                  <div class="time-estimate best-case">
                    <div class="time-label">Best Case</div>
                    <div class="time-value time-best">
                      <span class="time-days">%.1f</span>
                      <span class="time-weeks">(~%dw)</span>
                    </div>
                  </div>
                  <div class="time-estimate avg-case">
                    <div class="time-label">Average Case</div>
                    <div class="time-value time-avg">
                      <span class="time-days">%.1f</span>
                      <span class="time-weeks">(~%dw)</span>
                    </div>
                  </div>
                  <div class="time-estimate worst-case">
                    <div class="time-label">Worst Case</div>
                    <div class="time-value time-worst">
                      <span class="time-days">%.1f</span>
                      <span class="time-weeks">(~%dw)</span>
                    </div>
                  </div>
                </div>
                """.formatted(workEstimation.bestCaseDays(), workEstimation.bestCaseWeeks(),
                workEstimation.avgCaseDays(), workEstimation.avgCaseWeeks(), workEstimation.worstCaseDays(),
                workEstimation.worstCaseWeeks());
    }

    private String generateMediatorBreakdownSection(Collection<ProjectData> projects) {
        Map<String, MediatorStats> mediatorStats = projects.stream()
                .flatMap(p -> Arrays.stream(p.project().mediators()))
                .collect(Collectors.groupingBy(
                        Mediator::name,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> new MediatorStats(
                                        list.size(),
                                        list.stream().mapToDouble(Mediator::confidenceScore).average().orElse(0.0),
                                        list.stream().mapToDouble(Mediator::complexityScore).average().orElse(0.0)
                                )
                        )
                ));

        StringBuilder tableRows = new StringBuilder();
        mediatorStats.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(
                        Comparator.comparingDouble(MediatorStats::confidence)))
                .forEach(entry -> {
                    String name = entry.getKey();
                    MediatorStats stats = entry.getValue();
                    tableRows.append("""
                                    <tr>
                                      <td>%s</td>
                                      <td>%d</td>
                                      <td>%.2f</td>
                                      <td>%.2f</td>
                                    </tr>
                            """.formatted(name, stats.frequency(), stats.confidence(), stats.complexity()));
                });

        return """
                  <div class="summary-container">
                    <h2>Mediator wise breakdown</h2>
                    <table>
                      <tr><th>Tag Name</th><th>Frequency</th><th>Confidence</th><th>Complexity</th></tr>
                      %s
                    </table>
                  </div>
                """.formatted(tableRows.toString());
    }

    private static WorkEstimation calculateTotalWorkEstimation(Collection<ProjectData> projects,
                                                       Estimation mediatorEstimate, Estimation lineEstimation) {
        return projects.stream()
                .map(projectData -> calculateWorkEstimation(projectData, mediatorEstimate, lineEstimation))
                .reduce(new WorkEstimation(0, 0, 0), WorkEstimation::combine);
    }

    private static WorkEstimation calculateWorkEstimation(ProjectData projectData, Estimation mediatorEstimate,
                                                          Estimation lineEstimation) {
        Project project = projectData.project();
        double lines = projectData.totalCodeLines();

        double bestHours = project.timeEstimate(mediatorEstimate.bestCaseHours()) +
                lineEstimation.bestCaseHours() * lines;
        double avgHours = project.timeEstimate(mediatorEstimate.avgCaseHours()) +
                lineEstimation.avgCaseHours() * lines;
        double worstHours = project.timeEstimate(mediatorEstimate.worstCaseHours()) +
                lineEstimation.worstCaseHours() * lines;

        return new WorkEstimation(bestHours / 8.0, avgHours / 8.0, worstHours / 8.0);
    }

    private String generateTimeEstimatesHtml(WorkEstimation estimation) {
        return """
                    <div style="display: flex; justify-content: center; margin: 20px 0;">
                      <div class="time-estimates" style="width: 100%%; box-sizing: border-box;">
                        <div class="time-estimate best-case">
                          <div class="time-label">Best Case</div>
                          <div class="time-value time-best">
                            <span class="time-days">%.1fd</span>
                            <span class="time-weeks">(~%.1fw)</span>
                          </div>
                        </div>
                        <div class="time-estimate avg-case">
                          <div class="time-label">Average Case</div>
                          <div class="time-value time-avg">
                            <span class="time-days">%.1fd</span>
                            <span class="time-weeks">(~%.1fw)</span>
                          </div>
                        </div>
                        <div class="time-estimate worst-case">
                          <div class="time-label">Worst Case</div>
                          <div class="time-value time-worst">
                            <span class="time-days">%.1fd</span>
                            <span class="time-weeks">(~%.1fw)</span>
                          </div>
                        </div>
                      </div>
                    </div>
                """.formatted(estimation.bestCaseDays, estimation.bestCaseDays / 5.0,
                        estimation.avgCaseDays, estimation.avgCaseDays / 5.0,
                        estimation.worstCaseDays, estimation.worstCaseDays / 5.0);
    }

    private String generateFooter() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy, h:mm:ss a");
        return """
                </div>
                <footer><p>Report generated on: %s</p></footer>
                """.formatted(now.format(formatter));
    }

    private String generateHtmlFooter() {
        return """
                </body>
                </html>
                """;
    }

    private String getConfidenceLevel(double confidence) {
        if (confidence >= 0.8) {
            return "High Coverage";
        }
        if (confidence >= 0.5) {
            return "Medium Coverage";
        }
        return "Low Coverage";
    }

    private String getConfidenceBadgeClass(double confidence) {
        if (confidence >= 0.8) {
            return "status-high";
        }
        if (confidence >= 0.5) {
            return "status-medium";
        }
        return "status-low";
    }

    private String getCoverageBarColor(double confidence) {
        if (confidence >= 0.8) {
            return "#4CAF50";
        }
        if (confidence >= 0.5) {
            return "#FF9800";
        }
        return "#F44336";
    }

    private record MediatorStats(int frequency, double confidence, double complexity) {
    }
}
