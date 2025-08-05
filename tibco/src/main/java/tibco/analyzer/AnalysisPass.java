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
import tibco.model.PartnerLink;
import tibco.model.Process;
import tibco.model.Process5;
import tibco.model.Process6;
import tibco.model.ProcessInterface;
import tibco.model.Scope;
import tibco.model.Type;
import tibco.model.Variable;

import java.util.Collection;
import java.util.stream.Stream;

public class AnalysisPass {

    public void analyseProcess(ProcessAnalysisContext cx, Process process) {
        switch (process) {
            case Process5 process5 -> analyseProcess(cx, process5);
            case Process6 process6 -> analyseProcess(cx, process6);
        }
    }

    public void analyseProcess(ProcessAnalysisContext cx, Process6 process) {
        analyseTypes(cx, process.types());
        analysePartnerLinks(cx, process.partnerLinks());
        analyzeVariables(cx, process.variables());
        analyseScope(cx, process.scope());
    }

    public void analyseProcess(ProcessAnalysisContext cx, Process5 process) {
        analyseExplicitTransitionGroup(cx, process.transitionGroup());
    }

    public @NotNull AnalysisResult getResult(ProcessAnalysisContext cx, Process process) {
        return AnalysisResult.empty();
    }

    protected void analyseExplicitTransitionGroup(
            ProcessAnalysisContext cx, Process5.ExplicitTransitionGroup explicitTransitionGroup) {
        explicitTransitionGroup.transitions().forEach(each -> analyseTransition(cx, explicitTransitionGroup, each));
        explicitTransitionGroup.activities().forEach(each -> analyseActivity(cx, each));
        explicitTransitionGroup.startActivity().ifPresent(startActivity -> analyseActivity(cx, startActivity));
        explicitTransitionGroup.activities().stream()
                .flatMap(each ->
                        each instanceof Process5.ExplicitTransitionGroup.NestedGroup nestedGroup ?
                                Stream.of(nestedGroup.body()) : Stream.empty())
                .forEach(each -> analyseExplicitTransitionGroup(cx, each));
    }

    protected void analyseTransition(
            ProcessAnalysisContext cx, Process5.ExplicitTransitionGroup explicitTransitionGroup,
            Process5.ExplicitTransitionGroup.Transition transition) {
    }

    protected void analyzeVariables(ProcessAnalysisContext cx, Collection<Variable> variables) {

    }

    protected void analyzeProcessInterface(ProcessAnalysisContext cx, ProcessInterface processInterface) {

    }

    protected void analyseTypes(ProcessAnalysisContext cx, Collection<Type> types) {

    }

    protected void analyseWSDLDefinition(ProcessAnalysisContext cx,
                                         Type.WSDLDefinition wsdlDefinition) {

    }

    protected void analysePartnerLinks(ProcessAnalysisContext cx, Collection<PartnerLink> links) {

    }

    protected void analyseScope(ProcessAnalysisContext cx, Scope scope) {
        scope.flows().forEach(flow -> analyseFlow(cx, flow));
        scope.faultHandlers().forEach(faultHandler -> analyseActivity(cx, faultHandler));
        scope.sequence().forEach(sequence -> analyseSequence(cx, sequence));
    }

    protected void analyseSequence(ProcessAnalysisContext cx, Scope.Sequence sequence) {
        sequence.activities().forEach(each -> analyseActivity(cx, each));
    }

    protected void analyseFlow(ProcessAnalysisContext cx, Scope.Flow flow) {
        flow.links().forEach(link -> analyseLink(cx, link));
        flow.activities().forEach(activity -> analyseActivity(cx, activity));
    }

    protected void analyseActivity(ProcessAnalysisContext cx, Scope.Flow.Activity activity) {
        if (activity instanceof Scope.Flow.Activity.ActivityWithScope activityWithScope) {
            analyseScope(cx, activityWithScope.scope());
        }

    }

    protected void analyseActivityExtensionConfig(ProcessAnalysisContext cx,
                                                  Scope.Flow.Activity.ActivityExtension.Config config) {
        if (config instanceof Scope.Flow.Activity.ActivityExtension.Config.SQL sql) {
            cx.allocateIndexForQuery(sql);
        }
    }

    protected void analyseLink(ProcessAnalysisContext cx, Scope.Flow.Link link) {
    }
}
