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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public record Process6(String name, Collection<NameSpace> nameSpaces,
                       // BW 6 parts
                       Collection<Type> types,
                       ProcessInfo processInfo,
                       Optional<ProcessInterface> processInterface,
                       Optional<ProcessTemplateConfigurations> processTemplateConfigurations,
                       Collection<PartnerLink> partnerLinks,
                       Collection<Variable> variables,
                       Scope scope) implements Process {

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Process other)) {
            return false;
        }
        return other.name().equals(name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public Process6 {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        if (types == null) {
            types = List.of();
        } else {
            types = Collections.unmodifiableCollection(types);
        }
        if (partnerLinks == null) {
            partnerLinks = List.of();
        } else {
            partnerLinks = Collections.unmodifiableCollection(partnerLinks);
        }
        if (variables == null) {
            variables = List.of();
        } else {
            variables = Collections.unmodifiableCollection(variables);
        }
    }

}
