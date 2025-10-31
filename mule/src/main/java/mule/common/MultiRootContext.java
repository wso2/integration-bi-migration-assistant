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
import java.util.List;
import java.util.Optional;

public class MultiRootContext {
    private final List<ContextBase> projectContexts = new ArrayList<>();

    public void register(ContextBase cx) {
        assert cx != null;
        projectContexts.add(cx);
    }

    public Optional<LookupResult> lookupFlow(String flowName) {
        return projectContexts.stream().flatMap(cx -> cx.lookupResultFlowFunc(flowName).stream()).findFirst();
    }

    public record LookupResult(String org, String proj, String identifier) {}
}
