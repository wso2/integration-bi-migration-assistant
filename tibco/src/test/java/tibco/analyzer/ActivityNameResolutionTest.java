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
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package tibco.analyzer;

import org.testng.Assert;
import org.testng.annotations.Test;
import tibco.ConversionContext;
import tibco.ProjectConversionContext;
import tibco.converter.ProjectConverter.ProjectResources;
import tibco.model.Process5;
import tibco.model.Scope;
import tibco.model.Scope.Flow.Activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Covers resolution of activity names, which TIBCO only requires to be unique within a scope.
 */
public class ActivityNameResolutionTest {

    private static Activity.Empty empty(String name, String fileName) {
        return new Activity.Empty(name, List.of(), List.of(), null, fileName);
    }

    private static Scope scopeWith(String name, Activity... activities) {
        return new Scope(name, List.of(new Scope.Flow(name + "Flow", List.of(), List.of(activities))), List.of(),
                List.of());
    }

    private static AnalysisResult analyse(Scope processScope) {
        ProjectResources resources = new ProjectResources(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>());
        ConversionContext conversionContext = new ConversionContext("test-org", false, false, s -> {
        }, s -> {
        });
        ProcessAnalysisContext cx = new ProcessAnalysisContext(new ProjectAnalysisContext(
                new ProjectConversionContext(conversionContext, "test-project"), resources));
        DefaultAnalysisPass pass = new DefaultAnalysisPass();
        pass.analyseScope(cx, processScope);
        return pass.getResult(cx, new Process5("Test", "/Test.process", List.of(),
                new Process5.ExplicitTransitionGroup()));
    }

    @Test
    public void testPrefersReferrerScope() {
        Activity.Empty outerJoin = empty("Join", "outer");
        Activity.Empty outerReferrer = empty("OuterReferrer", "outer");
        Activity.Empty innerJoin = empty("Join", "inner");
        Activity.Empty innerReferrer = empty("InnerReferrer", "inner");
        Scope group = scopeWith("Group", innerJoin, innerReferrer);
        Activity.NestedScope groupActivity = new Activity.NestedScope("Group", List.of(), List.of(), group.sequence(),
                group.flows(), group.faultHandlers(), null, "outer");

        AnalysisResult result = analyse(scopeWith("Process", outerJoin, outerReferrer, groupActivity));

        Assert.assertEquals(result.findActivityInScopeOf(innerReferrer, "Join"), Optional.of(innerJoin));
        Assert.assertEquals(result.findActivityInScopeOf(outerReferrer, "Join"), Optional.of(outerJoin));
    }

    @Test
    public void testFallsBackToEnclosingScope() {
        Activity.Empty outerSource = empty("Source", "outer");
        Activity.Empty innerReferrer = empty("InnerReferrer", "inner");
        Scope group = scopeWith("Group", innerReferrer);
        Activity.NestedScope groupActivity = new Activity.NestedScope("Group", List.of(), List.of(), group.sequence(),
                group.flows(), group.faultHandlers(), null, "outer");

        AnalysisResult result = analyse(scopeWith("Process", outerSource, groupActivity));

        Assert.assertEquals(result.findActivityInScopeOf(innerReferrer, "Source"), Optional.of(outerSource));
        Assert.assertTrue(result.findActivityInScopeOf(innerReferrer, "Missing").isEmpty());
    }

    @Test
    public void testDuplicateInScopePicksFirst() {
        Activity.Empty first = empty("Join", "first");
        Activity.Empty second = empty("Join", "second");
        Activity.Empty referrer = empty("Referrer", "first");

        AnalysisResult result = analyse(scopeWith("Process", first, second, referrer));

        Assert.assertEquals(result.findActivityInScopeOf(referrer, "Join"), Optional.of(first));
    }
}
