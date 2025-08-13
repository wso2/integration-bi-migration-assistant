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
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public final class ProjectConversionContext implements LoggingContext {

    private final String name;
    private final List<TibcoToBalConverter.JavaDependencies> javaDependencies = new ArrayList<>();
    private final ConversionContext cx;

    public ProjectConversionContext(ConversionContext cx, String name) {
        this.cx = cx;
        this.name = name;
    }

    public void log(LoggingUtils.Level level, String message) {
        cx.logCallback().accept("[" + level + "] " + message);
    }

    public void logState(String message) {
        cx.stateCallback().accept(message);
    }

    public String org() {
        return cx.org();
    }

    public String name() {
        return name;
    }

    public boolean dryRun() {
        return cx.dryRun();
    }

    public List<TibcoToBalConverter.JavaDependencies> javaDependencies() {
        return Collections.unmodifiableList(javaDependencies);
    }

    public Consumer<String> stateCallback() {
        return cx.stateCallback();
    }

    public Consumer<String> logCallback() {
        return cx.logCallback();
    }

    public boolean keepStructure() {
        return cx.keepStructure();
    }

    public void addJavaDependency(TibcoToBalConverter.JavaDependencies dependencies) {
        javaDependencies.add(dependencies);
    }

    public ConversionContext conversionContext() {
        return cx;
    }
}
