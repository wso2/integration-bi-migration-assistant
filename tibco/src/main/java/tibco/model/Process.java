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

package tibco.model;

import java.util.Collection;
import java.util.List;

public sealed interface Process permits Process5, Process6 {

    String name();

    String path();

    Collection<NameSpace> nameSpaces();

    /**
     * The paths a {@code <processName>} reference may name this process by, most specific first.
     * A reference is written against the logical {@code <pd:name>}, but that need not agree with
     * the file location, so both are candidates.
     *
     * @return the paths this process can be referenced by
     */
    default List<String> lookupPaths() {
        return List.of(path(), name());
    }

    record ProcessIdentifier(String name) {
    }
}
