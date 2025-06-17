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

package tibco.analyzer;

import common.BallerinaModel;
import tibco.model.PartnerLink;
import tibco.model.Process;
import tibco.model.Process5.ExplicitTransitionGroup;
import tibco.model.Scope;
import tibco.model.XSD;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;

public interface AnalysisResult {
    /**
     * Creates and returns an immutable, empty instance of {@code AnalysisResult}.
     * <p>
     * This factory method is intended for cases where an empty result is required.
     * All internal maps in the returned instance are empty and immutable.
     *
     * @return an immutable, empty {@code AnalysisResult} instance
     */
    static AnalysisResult empty() {
        return new AnalysisResultImpl(Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap(),
                Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap(),
                Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap(),
                Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap(),
                Collections.emptyMap(), TibcoAnalysisReport.empty());
    }

    Collection<String> inputTypeName(Process process);

    String outputTypeName(Process process);

    Collection<Scope.Flow.Activity> sources(Scope.Flow.Link link);

    Optional<Scope.Flow.Activity> findActivity(String name);

    String variableType(Process process, String variableName);

    ActivityData from(Scope.Flow.Activity activity);

    Collection<Scope.Flow.Activity> activities();

    Collection<Scope.Flow.Link> links();

    Collection<Scope.Flow.Link> sources(Scope.Flow.Activity activity);

    Stream<TransitionData> transitionConditions(
            Scope.Flow.Activity activity);

    Stream<Scope.Flow.Activity.Source.Predicate> transitionCondition(
            Scope.Flow.Activity activity, Scope.Flow.Link link);

    Stream<Scope.Flow.Activity> sortedActivities(Scope scope);

    Stream<Scope.Flow.Activity> sortedActivities(ExplicitTransitionGroup group);

    Stream<Scope.Flow.Activity> sortedErrorHandlerActivities(ExplicitTransitionGroup group);

    Collection<Scope> scopes(Process process);

    PartnerLink.Binding getBinding(String partnerLinkName);

    ControlFlowFunctions getControlFlowFunctions(Scope scope);

    ControlFlowFunctions getControlFlowFunctions(ExplicitTransitionGroup group);

    AnalysisResult combine(AnalysisResult other);

    Optional<TibcoAnalysisReport> getReport();

    void setReport(TibcoAnalysisReport report);

    XSD.XSDType getType(String name);

    record ActivityData(String functionName, BallerinaModel.TypeDesc argumentType,
            BallerinaModel.TypeDesc returnType) {

    }

    record TransitionData(Scope.Flow.Activity activity,
            Scope.Flow.Activity.Source.Predicate predicate) {

    }

    record GraphNode(String name, Kind kind, Object data) {

        public enum Kind {
            ACTIVITY, LINK, INLINE_ACTIVITY
        }
    }

    record ControlFlowFunctions(String scopeFn, String activityRunner, String errorHandler) {
    }

}
