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

/**
 * Constants for time estimation calculations used in TIBCO analysis.
 * These values represent the estimated time (in days) required to handle
 * different types of unhandled activities.
 */
public final class TimeEstimationConstants {

    private TimeEstimationConstants() {
        // Prevent instantiation
    }

    // TIBCO Activity time estimates (in days)
    public static final int BEST_CASE_ACTIVITY_TIME = 1;
    public static final int WORST_CASE_ACTIVITY_TIME = 3;
    public static final int AVERAGE_CASE_ACTIVITY_TIME =
            (BEST_CASE_ACTIVITY_TIME + WORST_CASE_ACTIVITY_TIME) / 2;

    // Working days per week for conversion
    public static final double WORKING_DAYS_PER_WEEK = 5.0;
}
