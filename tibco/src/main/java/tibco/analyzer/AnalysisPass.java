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

import org.jetbrains.annotations.NotNull;
import tibco.Process;
import tibco.TibcoModel;

import java.util.Collection;
import java.util.stream.Stream;

public class AnalysisPass {

    public void analyseProcess(ProcessAnalysisContext cx, Process process) {
        switch (process) {
            case TibcoModel.Process5 process5 -> analyseProcess(cx, process5);
            case TibcoModel.Process6 process6 -> analyseProcess(cx, process6);
        }
    }

    public void analyseProcess(ProcessAnalysisContext cx, TibcoModel.Process6 process) {
        analyseTypes(cx, process.types());
        analysePartnerLinks(cx, process.partnerLinks());
        analyzeVariables(cx, process.variables());
        analyseScope(cx, process.scope());
    }

    public void analyseProcess(ProcessAnalysisContext cx, TibcoModel.Process5 process) {
        analyseExplicitTransitionGroup(cx, process.transitionGroup());
    }

    public @NotNull AnalysisResult getResult(ProcessAnalysisContext cx, Process process) {
        return AnalysisResult.empty();
    }

    protected void analyseExplicitTransitionGroup(
            ProcessAnalysisContext cx, TibcoModel.Process5.ExplicitTransitionGroup explicitTransitionGroup) {
        explicitTransitionGroup.transitions().forEach(each -> analyseTransition(cx, explicitTransitionGroup, each));
        explicitTransitionGroup.activities().forEach(each -> analyseActivity(cx, each));
        explicitTransitionGroup.activities().stream()
                .flatMap(each ->
                        each instanceof TibcoModel.Process5.ExplicitTransitionGroup.NestedGroup nestedGroup ?
                                Stream.of(nestedGroup.body()) : Stream.empty())
                .forEach(each -> analyseExplicitTransitionGroup(cx, each));
    }

    protected void analyseTransition(
            ProcessAnalysisContext cx, TibcoModel.Process5.ExplicitTransitionGroup explicitTransitionGroup,
            TibcoModel.Process5.ExplicitTransitionGroup.Transition transition) {
    }

    protected void analyzeVariables(ProcessAnalysisContext cx, Collection<TibcoModel.Variable> variables) {

    }

    protected void analyzeProcessInterface(ProcessAnalysisContext cx, TibcoModel.ProcessInterface processInterface) {

    }

    protected void analyseTypes(ProcessAnalysisContext cx, Collection<TibcoModel.Type> types) {

    }

    protected void analyseWSDLDefinition(ProcessAnalysisContext cx,
                                         TibcoModel.Type.WSDLDefinition wsdlDefinition) {

    }

    protected void analysePartnerLinks(ProcessAnalysisContext cx, Collection<TibcoModel.PartnerLink> links) {

    }

    protected void analyseScope(ProcessAnalysisContext cx, TibcoModel.Scope scope) {
        scope.flows().forEach(flow -> analyseFlow(cx, flow));
        scope.faultHandlers().forEach(faultHandler -> analyseActivity(cx, faultHandler));
        scope.sequence().forEach(sequence -> analyseSequence(cx, sequence));
    }

    protected void analyseSequence(ProcessAnalysisContext cx, TibcoModel.Scope.Sequence sequence) {
        sequence.activities().forEach(each -> analyseActivity(cx, each));
    }

    protected void analyseFlow(ProcessAnalysisContext cx, TibcoModel.Scope.Flow flow) {
        flow.links().forEach(link -> analyseLink(cx, link));
        flow.activities().forEach(activity -> analyseActivity(cx, activity));
    }

    protected void analyseActivity(ProcessAnalysisContext cx, TibcoModel.Scope.Flow.Activity activity) {
        if (activity instanceof TibcoModel.Scope.Flow.Activity.ActivityWithScope activityWithScope) {
            analyseScope(cx, activityWithScope.scope());
        }

    }

    protected void analyseActivityExtensionConfig(ProcessAnalysisContext cx,
                                                  TibcoModel.Scope.Flow.Activity.ActivityExtension.Config config) {
        if (config instanceof TibcoModel.Scope.Flow.Activity.ActivityExtension.Config.SQL sql) {
            cx.allocateIndexForQuery(sql);
        }
    }

    protected void analyseLink(ProcessAnalysisContext cx, TibcoModel.Scope.Flow.Link link) {
    }
}
