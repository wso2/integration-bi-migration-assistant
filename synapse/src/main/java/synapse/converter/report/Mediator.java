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

public record Mediator(String name, int instances, double confidenceScore, double complexityScore) {

    double timeEstimate(double baseTime) {
        // Tunable parameters for complexity and confidence impact
        double alpha = 1.0; // weight for complexity
        double beta = 1.0; // weight for (1 - confidence)
        return instances * baseTime * (1 + alpha * complexityScore) * (1 + beta * (1 - confidenceScore));
    }
}
