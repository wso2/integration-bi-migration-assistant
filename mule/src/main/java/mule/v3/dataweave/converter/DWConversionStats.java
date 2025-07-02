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
package mule.v3.dataweave.converter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DWConversionStats {
    final List<String> failedDWExpressions = new ArrayList<>();
    private final Map<DWConstruct, Integer> encountered = new LinkedHashMap<>();
    private final Map<DWConstruct, Integer> converted = new LinkedHashMap<>();

    public void record(DWConstruct construct, boolean isConverted) {
        encountered.merge(construct, 1, Integer::sum);
        if (isConverted) {
            converted.merge(construct, 1, Integer::sum);
        }
    }

    public int getTotalEncounteredCount() {
        return encountered.values().stream().mapToInt(i -> i).sum();
    }

    public int getConvertedCount() {
        return converted.values().stream().mapToInt(i -> i).sum();
    }

    public int getTotalWeight() {
        return encountered.entrySet().stream()
                .mapToInt(e -> e.getKey().weight() * e.getValue()).sum();
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
        return !encountered.isEmpty();
    }
}
