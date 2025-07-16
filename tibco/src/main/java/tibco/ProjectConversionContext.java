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

package tibco;

import common.LoggingUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public record ProjectConversionContext(boolean verbose, boolean dryRun,
                                       List<TibcoToBalConverter.JavaDependencies> javaDependencies,
                                       Consumer<String> stateCallback, Consumer<String> logCallback) implements
        LoggingContext {

    public ProjectConversionContext(boolean verbose, boolean dryRun, Consumer<String> stateCallback,
                                    Consumer<String> logCallback) {
        this(verbose, dryRun, new ArrayList<>(), stateCallback, logCallback);
    }

    public void log(LoggingUtils.Level level, String message) {
        logCallback.accept("[" + level + "] " + message);
    }

    public void logState(String message) {
        stateCallback.accept(message);
    }
}
