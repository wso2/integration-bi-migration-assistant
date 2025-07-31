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
package mule.common.report;

import mule.common.DWConstructBase;
import mule.common.DWConversionStats;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Record to hold the project migration statistics.
 *
 * @param passedXMLTags             a map of successfully migrated XML tags with their counts
 * @param failedXMLTags             a map of failed XML tags with their counts
 * @param failedBlocks              a list of blocks that failed during migration
 * @param dwConversionStats         statistics of DataWeave conversion
 * @param migrationCoverage         percentage of migration coverage achieved
 * @param bestCaseDays              estimated days for best case migration
 * @param averageCaseDays           estimated days for average case migration
 * @param worstCaseDays             estimated days for worst case migration
 * @param failedDistinctXMLTagCount count of distinct XML tags that failed migration
 * @param failedDWExprCount         count of DataWeave expressions that failed migration
 * @since 1.1.1
 */
public record ProjectMigrationStats(LinkedHashMap<String, Integer> passedXMLTags,
                                    LinkedHashMap<String, Integer> failedXMLTags,
                                    List<String> failedBlocks,
                                    DWConversionStats<? extends DWConstructBase> dwConversionStats,
                                    int migrationCoverage,
                                    double bestCaseDays,
                                    double averageCaseDays,
                                    double worstCaseDays,
                                    int failedDistinctXMLTagCount,
                                    int failedDWExprCount) {
}
