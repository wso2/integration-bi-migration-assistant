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

package common.report;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Utility class for defining CSS styles used in HTML reports.
 * Each method returns a Styles object containing CSS rules for specific components.
 */
public final class StyleDefinitions {

    private StyleDefinitions() {
        // Utility class, prevent instantiation
    }

    /**
     * Returns base styles for HTML document (body, container, headings, footer).
     */
    public static Styles getBaseStyles() {
        Map<String, Map<String, String>> styles = new LinkedHashMap<>();

        // Body styles
        Map<String, String> body = new LinkedHashMap<>();
        body.put("font-family", "Arial, sans-serif");
        body.put("background-color", "#f4f4f9");
        body.put("color", "#333");
        body.put("margin", "0");
        body.put("padding", "20px");
        styles.put("body", body);

        // Container styles
        Map<String, String> container = new LinkedHashMap<>();
        container.put("max-width", "1200px");
        container.put("margin", "0 auto");
        styles.put(".container", container);

        // H1, H2, H3 base color
        Map<String, String> h1h2h3 = new LinkedHashMap<>();
        h1h2h3.put("color", "#333");
        styles.put("h1, h2, h3", h1h2h3);

        // H1 specific styles
        Map<String, String> h1 = new LinkedHashMap<>();
        h1.put("text-align", "center");
        h1.put("color", "#4682B4");
        h1.put("font-size", "2.5em");
        h1.put("font-weight", "300");
        h1.put("margin", "15px auto 40px");
        h1.put("padding", "0 0 15px");
        h1.put("max-width", "600px");
        h1.put("position", "relative");
        h1.put("border-bottom", "1px solid rgba(70, 130, 180, 0.2)");
        styles.put("h1", h1);

        // H1::after (decorative underline)
        Map<String, String> h1After = new LinkedHashMap<>();
        h1After.put("content", "\"\"");
        h1After.put("position", "absolute");
        h1After.put("bottom", "-1px");
        h1After.put("left", "50%");
        h1After.put("transform", "translateX(-50%)");
        h1After.put("width", "100px");
        h1After.put("height", "3px");
        h1After.put("background-color", "rgba(70, 130, 180, 0.8)");
        styles.put("h1::after", h1After);

        // H2 within summary-container
        Map<String, String> summaryH2 = new LinkedHashMap<>();
        summaryH2.put("margin-top", "0");
        summaryH2.put("color", "#4682B4");
        summaryH2.put("border-bottom", "2px solid #f0f0f0");
        summaryH2.put("padding-bottom", "10px");
        summaryH2.put("margin-bottom", "20px");
        summaryH2.put("text-align", "center");
        summaryH2.put("font-size", "1.5em");
        styles.put(".summary-container h2", summaryH2);

        // H3 styles
        Map<String, String> h3 = new LinkedHashMap<>();
        h3.put("color", "#4682B4");
        h3.put("border-bottom", "2px solid #f0f0f0");
        h3.put("padding-bottom", "10px");
        h3.put("margin-bottom", "20px");
        styles.put("h3", h3);

        // Footer styles
        Map<String, String> footer = new LinkedHashMap<>();
        footer.put("text-align", "center");
        footer.put("margin-top", "20px");
        footer.put("font-size", "0.9em");
        footer.put("color", "#666");
        styles.put("footer", footer);

        return new Styles(styles);
    }

    /**
     * Returns styles for shared container components.
     */
    public static Styles getSharedContainerStyles() {
        Map<String, Map<String, String>> styles = new LinkedHashMap<>();

        // Summary container
        Map<String, String> summaryContainer = new LinkedHashMap<>();
        summaryContainer.put("background-color", "#fff");
        summaryContainer.put("padding", "25px");
        summaryContainer.put("border-radius", "10px");
        summaryContainer.put("box-shadow", "0 3px 10px rgba(0, 0, 0, 0.1)");
        summaryContainer.put("margin", "25px 0");
        summaryContainer.put("transition", "box-shadow 0.3s");
        styles.put(".summary-container", summaryContainer);

        Map<String, String> summaryContainerHover = new LinkedHashMap<>();
        summaryContainerHover.put("box-shadow", "0 5px 15px rgba(0, 0, 0, 0.15)");
        styles.put(".summary-container:hover", summaryContainerHover);

        // Estimation notes
        Map<String, String> estimationNotes = new LinkedHashMap<>();
        estimationNotes.put("margin-top", "25px");
        estimationNotes.put("padding", "20px");
        estimationNotes.put("background-color", "#f8f9fa");
        estimationNotes.put("border-radius", "8px");
        estimationNotes.put("border-left", "4px solid #4682B4");
        estimationNotes.put("box-shadow", "0 2px 5px rgba(0, 0, 0, 0.05)");
        styles.put(".estimation-notes", estimationNotes);

        Map<String, String> estimationNotesP = new LinkedHashMap<>();
        estimationNotesP.put("margin-top", "0");
        styles.put(".estimation-notes p", estimationNotesP);

        Map<String, String> estimationNotesUl = new LinkedHashMap<>();
        estimationNotesUl.put("margin", "15px 0 5px 25px");
        estimationNotesUl.put("padding-left", "0");
        styles.put(".estimation-notes ul", estimationNotesUl);

        Map<String, String> estimationNotesLi = new LinkedHashMap<>();
        estimationNotesLi.put("margin-bottom", "8px");
        estimationNotesLi.put("line-height", "1.4");
        styles.put(".estimation-notes li", estimationNotesLi);

        return new Styles(styles);
    }

    /**
     * Returns styles for table components.
     */
    public static Styles getTableStyles() {
        Map<String, Map<String, String>> styles = new LinkedHashMap<>();

        // Table
        Map<String, String> table = new LinkedHashMap<>();
        table.put("width", "100%");
        table.put("border-collapse", "collapse");
        table.put("margin", "20px 0");
        styles.put("table", table);

        // TH and TD
        Map<String, String> thTd = new LinkedHashMap<>();
        thTd.put("border", "1px solid #ddd");
        thTd.put("padding", "12px");
        thTd.put("text-align", "left");
        styles.put("th, td", thTd);

        // TH
        Map<String, String> th = new LinkedHashMap<>();
        th.put("background-color", "#4682B4");
        th.put("color", "white");
        styles.put("th", th);

        // TR even rows
        Map<String, String> trEven = new LinkedHashMap<>();
        trEven.put("background-color", "#f2f2f2");
        styles.put("tr:nth-child(even)", trEven);

        // TR hover
        Map<String, String> trHover = new LinkedHashMap<>();
        trHover.put("background-color", "#ddd");
        styles.put("tr:hover", trHover);

        // Code in tables
        Map<String, String> tableCode = new LinkedHashMap<>();
        tableCode.put("background-color", "#f0f0f0");
        tableCode.put("padding", "2px 6px");
        tableCode.put("border-radius", "4px");
        tableCode.put("font-family", "monospace");
        tableCode.put("font-size", "0.9em");
        styles.put("table code", tableCode);

        return new Styles(styles);
    }

    /**
     * Returns styles for coverage indicator component.
     */
    public static Styles getCoverageIndicatorStyles() {
        Map<String, Map<String, String>> styles = new LinkedHashMap<>();

        // Coverage indicator
        Map<String, String> coverageIndicator = new LinkedHashMap<>();
        coverageIndicator.put("width", "100%");
        coverageIndicator.put("height", "12px");
        coverageIndicator.put("background-color", "#f0f0f0");
        coverageIndicator.put("border-radius", "6px");
        coverageIndicator.put("overflow", "hidden");
        coverageIndicator.put("box-shadow", "inset 0 1px 3px rgba(0, 0, 0, 0.1)");
        coverageIndicator.put("margin", "10px 0 20px 0");
        styles.put(".coverage-indicator", coverageIndicator);

        // Coverage bar
        Map<String, String> coverageBar = new LinkedHashMap<>();
        coverageBar.put("height", "100%");
        coverageBar.put("border-radius", "6px");
        coverageBar.put("transition", "width 0.5s ease-in-out");
        styles.put(".coverage-bar", coverageBar);

        // Coverage bar with data attributes
        Map<String, String> coverageBarData = new LinkedHashMap<>();
        coverageBarData.put("height", "100%");
        coverageBarData.put("border-radius", "6px");
        coverageBarData.put("transition", "width 0.5s ease-in-out");
        styles.put(".coverage-bar[data-width]", coverageBarData);

        return new Styles(styles);
    }

    /**
     * Returns styles for metric box components.
     */
    public static Styles getMetricStyles() {
        Map<String, Map<String, String>> styles = new LinkedHashMap<>();

        // Metric
        Map<String, String> metric = new LinkedHashMap<>();
        metric.put("width", "100%");
        metric.put("box-sizing", "border-box");
        metric.put("padding", "15px 20px");
        metric.put("display", "flex");
        metric.put("flex-direction", "row");
        metric.put("align-items", "flex-start");
        metric.put("gap", "20px");
        metric.put("background-color", "#f8f9fa");
        metric.put("border-radius", "8px");
        metric.put("transition", "transform 0.2s, box-shadow 0.2s");
        metric.put("margin-bottom", "15px");
        metric.put("border", "1px solid #eaeaea");
        metric.put("box-shadow", "0 1px 3px rgba(0, 0, 0, 0.05)");
        styles.put(".metric", metric);

        Map<String, String> metricHover = new LinkedHashMap<>();
        metricHover.put("transform", "translateY(-3px)");
        metricHover.put("box-shadow", "0 5px 15px rgba(0, 0, 0, 0.1)");
        styles.put(".metric:hover", metricHover);

        // Metric value
        Map<String, String> metricValue = new LinkedHashMap<>();
        metricValue.put("font-weight", "bold");
        metricValue.put("font-size", "1.8em");
        metricValue.put("color", "#4682B4");
        metricValue.put("margin-bottom", "5px");
        styles.put(".metric-value", metricValue);

        // Metric label
        Map<String, String> metricLabel = new LinkedHashMap<>();
        metricLabel.put("font-size", "0.9em");
        metricLabel.put("color", "#666");
        metricLabel.put("text-align", "center");
        styles.put(".metric-label", metricLabel);

        // Metric left
        Map<String, String> metricLeft = new LinkedHashMap<>();
        metricLeft.put("flex", "1");
        metricLeft.put("display", "flex");
        metricLeft.put("flex-direction", "column");
        metricLeft.put("align-items", "center");
        styles.put(".metric-left", metricLeft);

        // Metric right
        Map<String, String> metricRight = new LinkedHashMap<>();
        metricRight.put("flex", "1");
        metricRight.put("padding-top", "10px");
        styles.put(".metric-right", metricRight);

        return new Styles(styles);
    }

    /**
     * Returns styles for time estimation components.
     */
    public static Styles getTimeEstimationStyles() {
        Map<String, Map<String, String>> styles = new LinkedHashMap<>();

        // Time classes
        Map<String, String> timeBest = new LinkedHashMap<>();
        timeBest.put("color", "#4CAF50");
        timeBest.put("font-weight", "600");
        styles.put(".time-best", timeBest);

        Map<String, String> timeAvg = new LinkedHashMap<>();
        timeAvg.put("color", "#4682B4");
        timeAvg.put("font-weight", "600");
        styles.put(".time-avg", timeAvg);

        Map<String, String> timeWorst = new LinkedHashMap<>();
        timeWorst.put("color", "#FF5722");
        timeWorst.put("font-weight", "600");
        styles.put(".time-worst", timeWorst);

        // Time estimates horizontal layout
        Map<String, String> timeEstimatesHorizontal = new LinkedHashMap<>();
        timeEstimatesHorizontal.put("display", "flex");
        timeEstimatesHorizontal.put("justify-content", "space-around");
        timeEstimatesHorizontal.put("align-items", "stretch");
        timeEstimatesHorizontal.put("background-color", "#f8f9fa");
        timeEstimatesHorizontal.put("border-radius", "8px");
        timeEstimatesHorizontal.put("padding", "20px");
        timeEstimatesHorizontal.put("margin", "20px 0");
        timeEstimatesHorizontal.put("transition", "transform 0.2s, box-shadow 0.2s");
        timeEstimatesHorizontal.put("box-shadow", "0 2px 5px rgba(0, 0, 0, 0.05)");
        styles.put(".time-estimates-horizontal", timeEstimatesHorizontal);

        Map<String, String> timeEstimatesHorizontalHover = new LinkedHashMap<>();
        timeEstimatesHorizontalHover.put("transform", "translateY(-3px)");
        timeEstimatesHorizontalHover.put("box-shadow", "0 5px 15px rgba(0, 0, 0, 0.1)");
        styles.put(".time-estimates-horizontal:hover", timeEstimatesHorizontalHover);

        // Time estimate
        Map<String, String> timeEstimate = new LinkedHashMap<>();
        timeEstimate.put("display", "flex");
        timeEstimate.put("flex-direction", "column");
        timeEstimate.put("align-items", "center");
        timeEstimate.put("flex", "1");
        timeEstimate.put("text-align", "center");
        styles.put(".time-estimate", timeEstimate);

        // Time label
        Map<String, String> timeLabel = new LinkedHashMap<>();
        timeLabel.put("font-size", "0.9em");
        timeLabel.put("color", "#666");
        timeLabel.put("margin-bottom", "10px");
        timeLabel.put("font-weight", "500");
        styles.put(".time-label", timeLabel);

        // Time value
        Map<String, String> timeValue = new LinkedHashMap<>();
        timeValue.put("font-weight", "bold");
        timeValue.put("display", "flex");
        timeValue.put("flex-direction", "column");
        timeValue.put("align-items", "center");
        styles.put(".time-value", timeValue);

        // Time days
        Map<String, String> timeDays = new LinkedHashMap<>();
        timeDays.put("font-size", "1.4em");
        timeDays.put("margin-bottom", "2px");
        styles.put(".time-days", timeDays);

        // Time weeks
        Map<String, String> timeWeeks = new LinkedHashMap<>();
        timeWeeks.put("font-size", "0.8em");
        timeWeeks.put("color", "#777");
        timeWeeks.put("font-weight", "normal");
        styles.put(".time-weeks", timeWeeks);

        return new Styles(styles);
    }

    /**
     * Returns styles for code block components.
     */
    public static Styles getCodeBlockStyles() {
        Map<String, Map<String, String>> styles = new LinkedHashMap<>();

        // Unsupported blocks
        Map<String, String> unsupportedBlocks = new LinkedHashMap<>();
        unsupportedBlocks.put("padding", "10px");
        styles.put(".unsupported-blocks", unsupportedBlocks);

        // Block item
        Map<String, String> blockItem = new LinkedHashMap<>();
        blockItem.put("background-color", "#f8f9fa");
        blockItem.put("border", "1px solid #ddd");
        blockItem.put("border-radius", "5px");
        blockItem.put("margin-bottom", "15px");
        blockItem.put("overflow", "hidden");
        blockItem.put("transition", "transform 0.2s, box-shadow 0.2s");
        styles.put(".block-item", blockItem);

        Map<String, String> blockItemHover = new LinkedHashMap<>();
        blockItemHover.put("transform", "translateY(-2px)");
        blockItemHover.put("box-shadow", "0 5px 15px rgba(0, 0, 0, 0.12)");
        styles.put(".block-item:hover", blockItemHover);

        // Block header
        Map<String, String> blockHeader = new LinkedHashMap<>();
        blockHeader.put("background-color", "#4682B4");
        blockHeader.put("color", "white");
        blockHeader.put("padding", "10px");
        blockHeader.put("display", "flex");
        blockHeader.put("justify-content", "space-between");
        styles.put(".block-header", blockHeader);

        // Block code
        Map<String, String> blockCode = new LinkedHashMap<>();
        blockCode.put("margin", "0");
        blockCode.put("padding", "15px");
        blockCode.put("background-color", "#fff");
        blockCode.put("overflow-x", "auto");
        blockCode.put("font-family", "monospace");
        blockCode.put("white-space", "pre-wrap");
        styles.put(".block-code", blockCode);

        // Block number
        Map<String, String> blockNumber = new LinkedHashMap<>();
        blockNumber.put("font-weight", "bold");
        styles.put(".block-number", blockNumber);

        // Block type
        Map<String, String> blockType = new LinkedHashMap<>();
        blockType.put("font-family", "monospace");
        styles.put(".block-type", blockType);

        return new Styles(styles);
    }

    /**
     * Returns styles for status badge components.
     */
    public static Styles getStatusBadgeStyles() {
        Map<String, Map<String, String>> styles = new LinkedHashMap<>();

        // Status badge base
        Map<String, String> statusBadge = new LinkedHashMap<>();
        statusBadge.put("padding", "6px 12px");
        statusBadge.put("border-radius", "20px");
        statusBadge.put("font-size", "0.75em");
        statusBadge.put("font-weight", "600");
        statusBadge.put("letter-spacing", "0.3px");
        statusBadge.put("text-transform", "uppercase");
        statusBadge.put("box-shadow", "0 1px 3px rgba(0, 0, 0, 0.1)");
        statusBadge.put("display", "inline-block");
        statusBadge.put("margin-left", "15px");
        styles.put(".status-badge", statusBadge);

        // Status high
        Map<String, String> statusHigh = new LinkedHashMap<>();
        statusHigh.put("background-color", "#e8f5e9");
        statusHigh.put("color", "#2e7d32");
        statusHigh.put("border", "1px solid rgba(46, 125, 50, 0.2)");
        styles.put(".status-high", statusHigh);

        // Status medium
        Map<String, String> statusMedium = new LinkedHashMap<>();
        statusMedium.put("background-color", "#fff8e1");
        statusMedium.put("color", "#f57c00");
        statusMedium.put("border", "1px solid rgba(245, 124, 0, 0.2)");
        styles.put(".status-medium", statusMedium);

        // Status low
        Map<String, String> statusLow = new LinkedHashMap<>();
        statusLow.put("background-color", "#ffebee");
        statusLow.put("color", "#c62828");
        statusLow.put("border", "1px solid rgba(198, 40, 40, 0.2)");
        styles.put(".status-low", statusLow);

        return new Styles(styles);
    }

    /**
     * Returns utility styles (hidden, visible, empty-message, drawer).
     */
    public static Styles getUtilityStyles() {
        Map<String, Map<String, String>> styles = new LinkedHashMap<>();

        // Drawer
        Map<String, String> drawer = new LinkedHashMap<>();
        drawer.put("overflow", "hidden");
        drawer.put("transition", "max-height 0.3s ease-out");
        drawer.put("max-height", "0");
        styles.put(".drawer", drawer);

        Map<String, String> drawerOpen = new LinkedHashMap<>();
        drawerOpen.put("max-height", "500px");
        styles.put(".drawer.open", drawerOpen);

        // Empty message
        Map<String, String> emptyMessage = new LinkedHashMap<>();
        emptyMessage.put("text-align", "center");
        emptyMessage.put("padding", "20px");
        emptyMessage.put("color", "#666");
        styles.put(".empty-message", emptyMessage);

        // Hidden
        Map<String, String> hidden = new LinkedHashMap<>();
        hidden.put("display", "none");
        styles.put(".hidden", hidden);

        // Visible
        Map<String, String> visible = new LinkedHashMap<>();
        visible.put("display", "block");
        styles.put(".visible", visible);

        return new Styles(styles);
    }

    /**
     * Returns all styles combined (used for backwards compatibility testing).
     */
    public static Styles getAllStyles() {
        return getBaseStyles()
                .merge(getSharedContainerStyles())
                .merge(getTableStyles())
                .merge(getCoverageIndicatorStyles())
                .merge(getMetricStyles())
                .merge(getTimeEstimationStyles())
                .merge(getCodeBlockStyles())
                .merge(getStatusBadgeStyles())
                .merge(getUtilityStyles());
    }
}
