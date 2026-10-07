/*
 *  Copyright (c) 2026, WSO2 LLC. (http://www.wso2.com).
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
 *  KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package mule.common.apispec;

import java.util.List;

/**
 * How the APIkit flows of one {@code apikit:config} line up with its spec, for the migration report.
 */
public sealed interface ApiContractCheck {

    String configName();

    String apiRef();

    /**
     * @return the API Manager policies of the API, one line each, which the migrated service does not enforce
     */
    List<String> policies();

    /**
     * @param configName    {@code apikit:config} name
     * @param apiRef        its {@code api} attribute
     * @param specLocation  where the spec was found
     * @param implemented   routes of spec operations that have a flow, e.g. {@code GET /orders/{id}}
     * @param unimplemented routes of spec operations without a flow; APIkit answers these with 501
     * @param flowIssues    flows that do not line up with an operation
     * @param policies      the API Manager policies of the API
     */
    record Checked(String configName, String apiRef, String specLocation, List<String> implemented,
                   List<String> unimplemented, List<FlowIssue> flowIssues, List<String> policies)
            implements ApiContractCheck {

        public Checked {
            assert configName != null && apiRef != null && specLocation != null && implemented != null;
            assert unimplemented != null && flowIssues != null && policies != null;
            implemented = List.copyOf(implemented);
            unimplemented = List.copyOf(unimplemented);
            flowIssues = List.copyOf(flowIssues);
            policies = List.copyOf(policies);
        }
    }

    /**
     * @param configName {@code apikit:config} name
     * @param apiRef     its {@code api} attribute
     * @param reason     why the spec is unavailable
     * @param flowCount  number of APIkit flows, whose routes come from flow names instead
     * @param policies   the API Manager policies of the API that are known without its spec
     */
    record Unchecked(String configName, String apiRef, String reason, int flowCount, List<String> policies)
            implements ApiContractCheck {

        public Unchecked {
            assert configName != null && apiRef != null && reason != null && policies != null;
            policies = List.copyOf(policies);
        }
    }

    /**
     * @param flowName the flow name as written
     * @param issue    what does not line up, in a sentence
     */
    record FlowIssue(String flowName, String issue) {

        public FlowIssue {
            assert flowName != null && issue != null;
        }
    }
}
