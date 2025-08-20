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

/**
 * Utility class for common report generation methods.
 */
public final class ReportUtils {

    private ReportUtils() {
        // Utility class, prevent instantiation
    }

    /**
     * Format a number as a day string with correct singular/plural form.
     *
     * @param number The number of days
     * @return A string with the number and "day" or "days"
     */
    public static String toDays(int number) {
        return number + " " + (number == 1 ? "day" : "days");
    }

    /**
     * Format a number as a week string with correct singular/plural form.
     *
     * @param number The number of weeks
     * @return A string with the number and "week" or "weeks"
     */
    public static String toWeeks(int number) {
        return number + " " + (number == 1 ? "week" : "weeks");
    }

    /**
     * Escape special HTML characters in a string.
     *
     * @param input The input string to escape
     * @return The escaped string
     */
    public static String escapeHtml(String input) {
        if (input == null) {
            return "";
        }

        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    /**
     * Generates the estimation scenarios section HTML.
     *
     * @param elementType The type of elements being analyzed (e.g., "activity", "component")
     * @return HTML string for the estimation scenarios section
     */
    public static String generateEstimationScenarios(String elementType) {
        return """
                <div class="estimation-notes">
                    <p><strong>Estimation Scenarios:</strong> Time measurement: 1 day = 8 hours, 5 working days = 1 week</p>
                    <ul>
                        <li>Best case scenario:
                          <ul>
                            <li>1.0 day per each new unsupported %s for analysis, implementation, and testing</li>
                            <li>1.0 hour per each repeated unsupported %s for implementation</li>
                            <li>1 minutes per each line of code generated</li>
                            <li>Assumes minimal complexity and straightforward implementations</li>
                          </ul>
                        </li>
                        <li>Average case scenario:
                          <ul>
                            <li>2.0 days per each new unsupported %s for analysis, implementation, and testing</li>
                            <li>2.0 hour per each repeated unsupported %s for implementation</li>
                            <li>3 minutes per each line of code generated</li>
                            <li>Assumes medium complexity with moderate implementation challenges</li>
                          </ul>
                        </li>
                        <li>Worst case scenario:
                          <ul>
                            <li>3.0 days per each new unsupported %s for analysis, implementation, and testing</li>
                            <li>4.0 hour per each repeated unsupported %s for implementation</li>
                            <li>5 minutes per each line of code generated</li>
                            <li>Assumes high complexity with significant implementation challenges</li>
                          </ul>
                        </li>
                    </ul>
                </div>
                """.formatted(
                elementType.toLowerCase(), elementType.toLowerCase(),
                elementType.toLowerCase(), elementType.toLowerCase(),
                elementType.toLowerCase(), elementType.toLowerCase()
        );
    }
}
