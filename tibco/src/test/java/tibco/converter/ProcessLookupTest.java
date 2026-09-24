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

package tibco.converter;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import tibco.ConversionContext;
import tibco.LookupResult;
import tibco.ProjectConversionContext;
import tibco.model.Process;
import tibco.model.Process5;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Covers resolution of a BW5 {@code <processName>} reference against the process it names.
 * A reference is written against {@code <pd:name>} and is routinely absolute, while
 * {@code <pd:name>} routinely is not, so the two never have the same shape.
 */
public class ProcessLookupTest {

    private static Process process(String name, String path) {
        return new Process5(name, path, List.of(), new Process5.ExplicitTransitionGroup());
    }

    private static ProjectConversionContext projectWith(String projectName, Process... processes) {
        return projectWith(new ConversionContext("testOrg", false, false, s -> {
        }, s -> {
        }), projectName, processes);
    }

    private static ProjectConversionContext projectWith(ConversionContext cx, String projectName,
                                                        Process... processes) {
        ProjectConversionContext projectCx = new ProjectConversionContext(cx, projectName);
        for (Process process : processes) {
            projectCx.addProcess(process);
        }
        cx.addProjectProcesses(Set.of(processes), projectCx);
        return projectCx;
    }

    @DataProvider
    public Object[][] leadingSlashMatrixProvider() {
        return new Object[][]{
                {"Processes/Worker.process", "Processes/Worker.process"},
                {"Processes/Worker.process", "/Processes/Worker.process"},
                {"/Processes/Worker.process", "Processes/Worker.process"},
                {"/Processes/Worker.process", "/Processes/Worker.process"},
        };
    }

    @Test(dataProvider = "leadingSlashMatrixProvider")
    public void testLocalLookupIgnoresLeadingSlash(String definedName, String reference) {
        ProjectConversionContext cx = projectWith("Caller",
                process(definedName, "/Caller/Processes/Worker.process"));

        Optional<LookupResult> result = cx.processFunction(reference);

        Assert.assertTrue(result.isPresent(), "Failed to resolve '" + reference + "' against '" + definedName + "'");
        Assert.assertEquals(result.get().symbol(), "start_Processes_Worker_process");
        Assert.assertTrue(result.get().importIdentifier().isEmpty(), "A local process must not be imported");
    }

    @Test(dataProvider = "leadingSlashMatrixProvider")
    public void testGlobalLookupIgnoresLeadingSlash(String definedName, String reference) {
        ConversionContext cx = new ConversionContext("testOrg", false, false, s -> {
        }, s -> {
        });
        projectWith(cx, "Worker", process(definedName, "/Worker/Processes/Worker.process"));

        Optional<LookupResult> result = cx.processFunction(reference);

        Assert.assertTrue(result.isPresent(), "Failed to resolve '" + reference + "' against '" + definedName + "'");
        Assert.assertEquals(result.get().symbol(), "start_Processes_Worker_process");
        Assert.assertEquals(result.get().importIdentifier().map(imp -> imp.moduleName()), Optional.of("Worker"));
    }

    @Test
    public void testUnknownProcessIsNotResolved() {
        ProjectConversionContext cx = projectWith("Caller",
                process("Processes/Worker.process", "/Caller/Processes/Worker.process"));

        Assert.assertTrue(cx.processFunction("/Processes/Missing.process").isEmpty());
        Assert.assertTrue(cx.conversionContext().processFunction("/Processes/Missing.process").isEmpty());
    }

    @Test
    public void testReferenceIsResolvedLocallyWhenOtherProjectsShareTheName() {
        ConversionContext cx = new ConversionContext("testOrg", false, false, s -> {
        }, s -> {
        });
        Process caProcess = process("Libraries/Common.process", "/CA/Libraries/Common.process");
        Process paProcess = process("Libraries/Common.process", "/PA/Libraries/Common.process");
        ProjectConversionContext ca = projectWith(cx, "CA", caProcess);
        ProjectConversionContext pa = projectWith(cx, "PA", paProcess);

        for (ProjectConversionContext project : List.of(ca, pa)) {
            Optional<LookupResult> result = project.processFunction("/Libraries/Common.process");
            Assert.assertTrue(result.isPresent());
            Assert.assertTrue(result.get().importIdentifier().isEmpty(),
                    "Project " + project.name() + " must bind to its own copy without a cross-module import");
        }
    }

    @Test
    public void testAmbiguousReferenceIsNotResolved() {
        ProjectConversionContext cx = projectWith("Caller",
                process("Processes/A/Common.process", "/Caller/Processes/A/Common.process"),
                process("Processes/B/Common.process", "/Caller/Processes/B/Common.process"));

        Assert.assertTrue(cx.processFunction("Common.process").isEmpty(),
                "An ambiguous reference must fall back to the error stub rather than pick a candidate");
        Assert.assertTrue(cx.processFunction("/Processes/A/Common.process").isPresent(),
                "A fully qualified reference disambiguates");
    }

    @Test
    public void testAmbiguousReferenceDoesNotFallThroughToAnotherProject() {
        ConversionContext cx = new ConversionContext("testOrg", false, false, s -> {
        }, s -> {
        });
        projectWith(cx, "Lib", process("Common.process", "/Lib/Common.process"));
        ProjectConversionContext caller = projectWith(cx, "Caller",
                process("Processes/A/Common.process", "/Caller/Processes/A/Common.process"),
                process("Processes/B/Common.process", "/Caller/Processes/B/Common.process"));

        Assert.assertTrue(caller.processFunction("Common.process").isEmpty(),
                "An ambiguous local reference must not be resolved against another project");
    }

    @Test
    public void testSymbolFollowsTheDefinitionNotTheReference() {
        // The defining file need not sit where <pd:name> says it does.
        ProjectConversionContext cx = projectWith("RestHelloWorld",
                process("Processes/Other.process", "/RestHelloWorld/other.process"));

        Optional<LookupResult> byName = cx.processFunction("/Processes/Other.process");
        Assert.assertTrue(byName.isPresent());
        Assert.assertEquals(byName.get().symbol(), "start_Processes_Other_process");

        Optional<LookupResult> byPath = cx.processFunction("/RestHelloWorld/other.process");
        Assert.assertTrue(byPath.isPresent());
        Assert.assertEquals(byPath.get().symbol(), "start_Processes_Other_process");
    }
}
