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
package mule.common;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DWConversionStats<T extends DWConstructBase> {

    // Average coverage weight assigned per DW line that failed to parse.
    // Calibrated to the mean of all DWConstruct enum weights (~2).
    public static final int DW_WEIGHT_PER_LINE = 2;

    // Default line count used when a DW script file cannot be located at runtime.
    public static final int DEFAULT_MISSING_SCRIPT_LINES = 10;

    // Estimated number of DW AST constructs per source line.
    // Derived from typical DW patterns: ~2-3 constructs per line (selectors, operators, literals).
    public static final int CONSTRUCTS_PER_DW_LINE = 3;

    public final List<String> failedDWExpressions = new ArrayList<>();
    private final Map<T, Integer> encountered = new LinkedHashMap<>();
    private final Map<T, Integer> converted = new LinkedHashMap<>();

    // Lines from parse-failed scripts (exact) and missing scripts (estimated).
    // Does NOT include unsupported construct lines. Those are derived from unsupportedConstructCount.
    private int failedDWLineCount = 0;
    // Accumulated coverage weight for parse-failed scripts (lineCount × DW_WEIGHT_PER_LINE).
    private int failedScriptWeight = 0;
    // Count of scripts that failed to parse; used to keep construct count display consistent with coverage %.
    private int parseFailureCount = 0;
    // Count of individual unsupported DW constructs encountered by the visitor.
    private int unsupportedConstructCount = 0;

    public void record(T construct, boolean isConverted) {
        encountered.merge(construct, 1, Integer::sum);
        if (isConverted) {
            converted.merge(construct, 1, Integer::sum);
        }
    }

    /** Records a DW script that failed to parse. Line count drives both coverage weight and time estimation. */
    public void recordParseFailure(int lineCount, String script) {
        failedDWLineCount += lineCount;
        failedScriptWeight += lineCount * DW_WEIGHT_PER_LINE;
        parseFailureCount++;
        failedDWExpressions.add(script);
    }

    public int getParseFailureCount() {
        return parseFailureCount;
    }

    /** Adds the default line estimate for a DW script whose source file could not be found. */
    public void addMissingScriptLineEstimate() {
        failedDWLineCount += DEFAULT_MISSING_SCRIPT_LINES;
    }

    /** Records an individual unsupported DW expression encountered by the visitor. */
    public void addFailedExpression(String expr) {
        unsupportedConstructCount++;
        failedDWExpressions.add(expr);
    }

    /**
     * Total DW lines estimated to require manual conversion:
     * parse failures (exact) + missing scripts (default estimate) + unsupported constructs (construct-count estimate).
     */
    public int getFailedDWLineCount() {
        // Ceiling division: ensures at least 1 line is counted when any unsupported constructs exist.
        int unsupportedLines = (unsupportedConstructCount + CONSTRUCTS_PER_DW_LINE - 1) / CONSTRUCTS_PER_DW_LINE;
        return failedDWLineCount + unsupportedLines;
    }

    /** DW lines estimated to have been successfully auto-converted. */
    public int getConvertedDWLineCount() {
        // Ceiling division: ensures at least 1 line is counted when any unsupported constructs exist.
        return (getConvertedCount() + CONSTRUCTS_PER_DW_LINE - 1) / CONSTRUCTS_PER_DW_LINE;
    }

    /** Total DW source lines (converted + failed), used for report display. */
    public int getTotalDWLineCount() {
        return getConvertedDWLineCount() + getFailedDWLineCount();
    }

    public int getTotalEncounteredCount() {
        return encountered.values().stream().mapToInt(i -> i).sum();
    }

    public int getConvertedCount() {
        return converted.values().stream().mapToInt(i -> i).sum();
    }

    public int getTotalWeight() {
        int constructWeight = encountered.entrySet().stream()
                .mapToInt(e -> e.getKey().weight() * e.getValue()).sum();
        return constructWeight + failedScriptWeight;
    }

    public int getConvertedWeight() {
        return converted.entrySet().stream()
                .mapToInt(e -> e.getKey().weight() * e.getValue()).sum();
    }

    public double getConversionPercentage() {
        int total = getTotalWeight();
        return total == 0 ? 0 : (100.0 * getConvertedWeight() / total);
    }

    public List<String> getFailedDWExpressions() {
        return failedDWExpressions;
    }

    public boolean dataWeaveFound() {
        return !encountered.isEmpty() || failedScriptWeight > 0 || unsupportedConstructCount > 0;
    }
}
