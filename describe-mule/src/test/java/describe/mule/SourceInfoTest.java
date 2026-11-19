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

import mule.v4.Context;
import mule.v4.Context.SourceInfo;
import mule.v4.model.MuleModel;
import mule.v4.model.MuleModel.ApiKitConfig;
import mule.v4.model.MuleModel.Async;
import mule.v4.model.MuleModel.Choice;
import mule.v4.model.MuleModel.Enricher;
import mule.v4.model.MuleModel.FirstSuccessful;
import mule.v4.model.MuleModel.Foreach;
import mule.v4.model.MuleModel.MuleRecord;
import mule.v4.model.MuleModel.OnErrorContinue;
import mule.v4.model.MuleModel.OnErrorPropagate;
import mule.v4.model.MuleModel.Route;
import mule.v4.model.MuleModel.ScatterGather;
import mule.v4.model.MuleModel.SubFlow;
import mule.v4.model.MuleModel.Try;
import mule.v4.model.MuleModel.WhenInChoice;
import mule.v4.model.ParseResult;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The Week 2 gate for the reader hook: every record that readFlow, readSubFlow or readBlock built from a project's
 * main files has its XML element, from the right file, inside its own flow (D3).
 */
public class SourceInfoTest {

    @DataProvider(name = "repoProjects")
    public Object[][] repoProjects() throws IOException {
        return TestProjects.repoProjects();
    }

    @Test(dataProvider = "repoProjects")
    public void testEveryRecordHasItsSource(Path project) {
        checkProject(project);
    }

    @Test
    public void testEveryRecordHasItsSourceInOutsideProjects() throws IOException {
        for (Path project : TestProjects.outsideProjects()) {
            checkProject(project);
        }
    }

    private static void checkProject(Path project) {
        Context ctx = MuleProjectLoader.load(project.toAbsolutePath().normalize()).context();
        ctx.enableDescribe(new ConversionRecorder(ctx.migrationMetrics));
        ctx.parseAllFiles();
        Map<Element, MuleRecord> usedElements = new IdentityHashMap<>();
        int flowCount = 0;
        for (Map.Entry<File, ParseResult> parsed : ctx.parseResults().entrySet()) {
            Path file = parsed.getKey().toPath().toAbsolutePath().normalize();
            for (MuleModel.Flow flow : parsed.getValue().flows()) {
                checkFlow(ctx, file, flow, "flow", flow.name(), usedElements);
                flowCount++;
            }
            for (SubFlow subFlow : parsed.getValue().subFlows()) {
                checkFlow(ctx, file, subFlow, "sub-flow", subFlow.name(), usedElements);
                flowCount++;
            }
        }
        Assert.assertTrue(flowCount > 0, "No flow was read from " + project);
    }

    private static void checkFlow(Context ctx, Path file, MuleRecord flow, String tag, String name,
                                  Map<Element, MuleRecord> usedElements) {
        Element flowElement = sourceElement(ctx, file, flow, usedElements);
        Assert.assertEquals(flowElement.getTagName(), tag, name);
        Assert.assertEquals(flowElement.getAttribute("name"), name);
        List<MuleRecord> records = new ArrayList<>();
        addRecordsBelow(flow, records);
        for (MuleRecord record : records) {
            Element element = sourceElement(ctx, file, record, usedElements);
            Assert.assertTrue(isInside(element, flowElement),
                    record.kind() + " <" + element.getTagName() + "> is not inside its flow " + name + " in " + file);
        }
    }

    // Two records must never point to the same element
    private static Element sourceElement(Context ctx, Path file, MuleRecord record,
                                         Map<Element, MuleRecord> usedElements) {
        Optional<SourceInfo> source = ctx.sourceOf(record);
        Assert.assertTrue(source.isPresent(), "No source for a " + record.kind() + " record in " + file);
        Assert.assertEquals(Path.of(source.get().filePath()).toAbsolutePath().normalize(), file);
        Element element = source.get().element();
        Assert.assertNull(usedElements.put(element, record),
                "<" + element.getTagName() + "> was registered for two records in " + file);
        return element;
    }

    // The records that readFlow, readSubFlow and readBlock built below this one. They do not build the "when"
    // and "route" records or an APIkit flow's source (D3), so the walk steps over those.
    private static void addRecordsBelow(MuleRecord record, List<MuleRecord> records) {
        List<MuleRecord> children = new ArrayList<>();
        if (record instanceof MuleModel.Flow flow) {
            Optional<MuleRecord> source = flow.source();
            if (source.isPresent() && !(source.get() instanceof ApiKitConfig)) {
                children.add(source.get());
            }
            children.addAll(flow.flowBlocks());
        } else if (record instanceof SubFlow subFlow) {
            children.addAll(subFlow.flowBlocks());
        } else if (record instanceof Choice choice) {
            for (WhenInChoice when : choice.whens()) {
                children.addAll(when.process());
            }
            children.addAll(choice.otherwiseProcess());
        } else if (record instanceof ScatterGather scatterGather) {
            for (Route route : scatterGather.routes()) {
                children.addAll(route.flowBlocks());
            }
        } else if (record instanceof FirstSuccessful firstSuccessful) {
            for (Route route : firstSuccessful.routes()) {
                children.addAll(route.flowBlocks());
            }
        } else if (record instanceof Async async) {
            children.addAll(async.flowBlocks());
        } else if (record instanceof Try tryScope) {
            children.addAll(tryScope.flowBlocks());
        } else if (record instanceof Foreach foreach) {
            children.addAll(foreach.flowBlocks());
        } else if (record instanceof Enricher enricher && enricher.innerBlock().isPresent()) {
            children.add(enricher.innerBlock().get());
        } else if (record instanceof MuleModel.ErrorHandler errorHandler) {
            children.addAll(errorHandler.errorHandlers());
        } else if (record instanceof OnErrorContinue onErrorContinue) {
            children.addAll(onErrorContinue.errorBlocks());
        } else if (record instanceof OnErrorPropagate onErrorPropagate) {
            children.addAll(onErrorPropagate.errorBlocks());
        }
        for (MuleRecord child : children) {
            records.add(child);
            addRecordsBelow(child, records);
        }
    }

    private static boolean isInside(Element element, Element ancestor) {
        Node node = element.getParentNode();
        while (node != null) {
            if (node == ancestor) {
                return true;
            }
            node = node.getParentNode();
        }
        return false;
    }
}
