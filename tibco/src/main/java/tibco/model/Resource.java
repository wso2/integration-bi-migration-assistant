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
import java.util.Map;
import java.util.Optional;

public interface Resource {

    String name();

    String path();

    Collection<SubstitutionBinding> substitutionBindings();

    record JDBCSharedResource(String name, String path, String location) implements Resource {

        @Override
        public Collection<SubstitutionBinding> substitutionBindings() {
            return List.of();
        }
    }

    record JDBCResource(String name, String path, String userName, String password, String jdbcDriver, String dbUrl,
                        Collection<SubstitutionBinding> substitutionBindings) implements Resource {

    }

    record HTTPConnectionResource(String name, String path, String svcRegServiceName,
                                  Collection<SubstitutionBinding> substitutionBindings) implements Resource {

    }

    record HTTPSharedResource(String name, String path, String host, int port) implements Resource {

        @Override
        public Collection<SubstitutionBinding> substitutionBindings() {
            return List.of();
        }
    }

    record HTTPClientResource(String name, String path, Optional<Integer> port,
                              Collection<SubstitutionBinding> substitutionBindings)
            implements Resource {

    }

    record SubstitutionBinding(String template, String propName) {

    }

    record JMSSharedResource(String name, String path, String fileName, NamingEnvironment namingEnvironment,
            ConnectionAttributes connectionAttributes, Map<String, String> jndiProperties)
            implements Resource {

        @Override
        public Collection<SubstitutionBinding> substitutionBindings() {
            return List.of();
        }

        public record NamingEnvironment(boolean useJNDI, String providerURL, String namingURL,
                String namingInitialContextFactory, String topicFactoryName,
                String queueFactoryName, String namingPrincipal, String namingCredential) {
        }

        public record ConnectionAttributes(Optional<String> username, Optional<String> password,
                Optional<String> clientID, boolean autoGenClientID) {
        }
    }

    record SharedVariable(String name, String path, boolean persistent, String initialValue, boolean isShared, String relativePath)
            implements Resource {
        @Override
        public Collection<SubstitutionBinding> substitutionBindings() {
            return List.of();
        }
    }
}
