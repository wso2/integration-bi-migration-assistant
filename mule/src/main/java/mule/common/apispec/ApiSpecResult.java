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

import java.nio.file.Path;

/**
 * Outcome of loading the spec of one {@code apikit:config}.
 */
public sealed interface ApiSpecResult {

    /**
     * @param specFile root file the spec was read from; for an Exchange archive this is inside a temporary
     *                 extraction directory that no longer exists
     * @param location where the spec was found, for display; see {@link ApiSpecResolver.Resolution.Found}
     * @param spec     the spec
     */
    record Loaded(Path specFile, String location, ApiSpec spec) implements ApiSpecResult {

        public Loaded {
            assert specFile != null && location != null && spec != null;
        }
    }

    /**
     * @param reason why the spec could not be loaded, for the migration report
     */
    record Unavailable(String reason) implements ApiSpecResult {

        public Unavailable {
            assert reason != null;
        }
    }
}
