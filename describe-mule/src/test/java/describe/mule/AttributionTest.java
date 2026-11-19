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
package describe.mule;

import describe.model.DescriptionModel.Branch;
import describe.model.DescriptionModel.Description;
import describe.model.DescriptionModel.Flow;
import describe.model.DescriptionModel.Migration;
import describe.model.DescriptionModel.MigrationReason;
import describe.model.DescriptionModel.MigrationStatus;
import describe.model.DescriptionModel.ReasonKind;
import describe.model.DescriptionModel.Step;
import describe.model.DescriptionModel.StepType;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Optional;

/**
 * Each conversion problem must be reported on the element that has it, not on the elements around it. The fixture
 * project has one problem in each of its two files.
 */
public class AttributionTest {

    private Description description;

    @BeforeClass
    public void describeFixture() {
        description = MuleDescriber.describe(TestProjects.ATTRIBUTION_PROJECT);
    }

    // dataweave-in-when.xml: a script that cannot be parsed, in the first "when" of a choice
    @Test
    public void testDataWeaveProblemStaysOnItsTransform() {
        Flow flow = description.flows().get(0);
        Assert.assertEquals(flow.name(), "routeOrder");
        Step choice = flow.steps().get(0);
        Assert.assertEquals(choice.type(), StepType.DECIDES);
        List<Branch> paths = choice.paths().orElseThrow();
        Step brokenScript = paths.get(0).steps().get(0);
        Step goodScript = paths.get(1).steps().get(0);
        Assert.assertEquals(brokenScript.name(), Optional.of("Broken script"));
        Assert.assertEquals(goodScript.name(), Optional.of("Good script"));

        Migration brokenMigration = brokenScript.migration().orElseThrow();
        Assert.assertEquals(brokenMigration.status(), MigrationStatus.PARTLY_CONVERTED);
        Assert.assertEquals(brokenMigration.reasons().size(), 1);
        Assert.assertEquals(brokenMigration.reasons().get(0).kind(), ReasonKind.DATAWEAVE);
        Assert.assertEquals(goodScript.migration(), Optional.of(Migration.CONVERTED));
        Assert.assertEquals(choice.migration(), Optional.of(Migration.CONVERTED));
    }

    // xref-translate.xml: a custom connector element that the converter does not support, in a try
    @Test
    public void testUnsupportedElementStaysOnItsStep() {
        Flow subFlow = description.flows().get(1);
        Assert.assertEquals(subFlow.name(), "lookUpCountry");
        Step tryScope = subFlow.steps().get(0);
        Assert.assertEquals(tryScope.type(), StepType.IF_IT_FAILS);
        Step translate = tryScope.steps().orElseThrow().get(0);
        Assert.assertEquals(translate.name(), Optional.of("Translate country"));
        Assert.assertEquals(translate.type(), StepType.LOOKS_UP);
        Assert.assertEquals(translate.system().orElseThrow().connector(), "xref-client");

        Assert.assertEquals(translate.migration(), Optional.of(new Migration(MigrationStatus.NOT_CONVERTED,
                List.of(new MigrationReason(ReasonKind.UNSUPPORTED_ELEMENT, "xref-client:translate")))));
        Assert.assertEquals(tryScope.migration(), Optional.of(Migration.CONVERTED));
        Assert.assertEquals(tryScope.onError().orElseThrow().get(0).migration(), Optional.of(Migration.CONVERTED));
        // A lookup the describer understands is described, not flagged
        Assert.assertEquals(description.coverage().counts().flagged(), 0);
    }
}
