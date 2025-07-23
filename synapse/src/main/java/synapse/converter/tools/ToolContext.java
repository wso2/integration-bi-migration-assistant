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

package synapse.converter.tools;

import common.LoggingContext;
import common.LoggingUtils;

public class ToolContext implements LoggingContext {

    private final LoggingContext loggingDelegate;

    public ToolContext(LoggingContext loggingDelegate) {
        this.loggingDelegate = loggingDelegate;
    }

    @Override
    public void log(LoggingUtils.Level level, String message) {
        loggingDelegate.log(level, message);
    }

    @Override
    public void logState(String message) {
        loggingDelegate.logState(message);
    }
}
