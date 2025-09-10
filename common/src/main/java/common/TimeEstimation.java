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
 * Record to hold time estimation data.
 *
 * @param bestCaseDays    The best case scenario in days
 * @param averageCaseDays The average case scenario in days
 * @param worstCaseDays   The worst case scenario in days
 */
public record TimeEstimation(
        double bestCaseDays,
        double averageCaseDays,
        double worstCaseDays
) {

    public static TimeEstimation zero() {
        return new TimeEstimation(0, 0, 0);
    }

    public boolean isZero() {
        return bestCaseDays == 0 && averageCaseDays == 0 && worstCaseDays == 0;
    }

    public int bestCaseDaysAsInt() {
        return (int) Math.ceil(bestCaseDays);
    }

    public int averageCaseDaysAsInt() {
        return (int) Math.ceil(averageCaseDays);
    }

    public int worstCaseDaysAsInt() {
        return (int) Math.ceil(worstCaseDays);
    }

    public int bestCaseWeeks() {
        return (int) Math.ceil(bestCaseDays / 5.0);
    }

    public int averageCaseWeeks() {
        return (int) Math.ceil(averageCaseDays / 5.0);
    }

    public int worstCaseWeeks() {
        return (int) Math.ceil(worstCaseDays / 5.0);
    }

    public TimeEstimation prod(long scalar) {
        return new TimeEstimation(
                bestCaseDays * scalar,
                averageCaseDays * scalar,
                worstCaseDays * scalar
        );
    }

    public static TimeEstimation sum(TimeEstimation t1, TimeEstimation t2) {
        return new TimeEstimation(
                t1.bestCaseDays + t2.bestCaseDays,
                t1.averageCaseDays + t2.averageCaseDays,
                t1.worstCaseDays + t2.worstCaseDays
        );
    }
}
