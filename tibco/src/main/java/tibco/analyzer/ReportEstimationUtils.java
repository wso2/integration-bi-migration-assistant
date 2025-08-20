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

import common.TimeEstimation;

public class ReportEstimationUtils {

    static TimeEstimation getValidationTimeEstimation(long lineCount) {
        double bestCaseLineDays = (lineCount * 1.0) / 60.0 / 8.0; // 1 min/line
        double averageCaseLineDays = (lineCount * 3.0) / 60.0 / 8.0; // 3 min/line
        double worstCaseLineDays = (lineCount * 5.0) / 60.0 / 8.0; // 5 min/line
        return new TimeEstimation(bestCaseLineDays, averageCaseLineDays, worstCaseLineDays);
    }
}
