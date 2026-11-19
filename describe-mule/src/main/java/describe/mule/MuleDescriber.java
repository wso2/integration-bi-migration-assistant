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

import describe.json.DescriptionWriter;
import describe.model.DescriptionModel;
import describe.model.DescriptionModel.Counts;
import describe.model.DescriptionModel.Coverage;
import describe.model.DescriptionModel.Description;
import describe.model.DescriptionModel.FailedFile;
import describe.model.DescriptionModel.Flow;
import describe.model.DescriptionModel.Generator;
import describe.model.DescriptionModel.Project;
import mule.v4.Context;
import mule.v4.model.MuleModel;
import mule.v4.model.MuleModel.MuleRecord;
import mule.v4.model.MuleModel.SubFlow;
import mule.v4.model.ParseResult;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Describes a Mule 4 project as an integration description (--describe). It runs its own parse and convert pass, so
 * the normal conversion is not touched.
 */
public final class MuleDescriber {

    public static final String DESCRIPTION_FILE = "description.json";
    private static final String GENERATOR_NAME = "mule-migration-assistant";
    private static final String GENERATOR_VERSION = "0.1.0-SNAPSHOT";
    private static final Pattern MIN_MULE_VERSION = Pattern.compile("\"minMuleVersion\"\\s*:\\s*\"([^\"]+)\"");

    private MuleDescriber() {
    }

    // For manual runs until the --describe flag arrives in Week 3
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            throw new IllegalArgumentException("Usage: MuleDescriber <mule-project-dir> <output-dir>");
        }
        Path outputDir = Path.of(args[1]);
        Files.createDirectories(outputDir);
        DescriptionWriter.write(describe(Path.of(args[0])), outputDir.resolve(DESCRIPTION_FILE));
    }

    @NotNull
    public static Description describe(@NotNull Path projectDir) {
        Path project = projectDir.toAbsolutePath().normalize();
        MuleProjectLoader.LoadedProject loaded = MuleProjectLoader.load(project);
        Context ctx = loaded.context();
        ConversionRecorder recorder = new ConversionRecorder(ctx.migrationMetrics);
        ctx.enableDescribe(recorder);
        ctx.parseAllFiles();
        BallerinaLinker linker = new BallerinaLinker(ctx, ctx.codeGen());

        // Files in path order and flows in document order, so the same project always gives the same JSON
        List<File> files = new ArrayList<>(loaded.xmlFiles());
        files.sort(Comparator.comparing(file -> relativePath(project, file.toPath())));
        List<MuleRecord> flowRecords = new ArrayList<>();
        List<FailedFile> failedFiles = new ArrayList<>();
        GlobalElements globals = new GlobalElements();
        for (File file : files) {
            ParseResult parseResult = ctx.parseResults().get(file);
            Optional<Element> root = parseRoot(file.toPath());
            if (parseResult == null || root.isEmpty()) {
                failedFiles.add(new FailedFile(relativePath(project, file.toPath()),
                        Optional.of("The file could not be read")));
                continue;
            }
            List<MuleRecord> inFile = new ArrayList<>(parseResult.flows());
            inFile.addAll(parseResult.subFlows());
            inFile.sort(Comparator.comparingInt(record -> documentIndex(ctx, record)));
            flowRecords.addAll(inFile);
            globals.read(root.get(), project);
        }

        Map<String, String> flowIds = new HashMap<>();
        for (MuleRecord record : flowRecords) {
            flowIds.put(flowName(record), "f" + (flowIds.size() + 1));
        }
        FlowWalker walker = new FlowWalker(ctx, recorder, linker, project, flowIds);
        List<Flow> flows = new ArrayList<>();
        List<String> linkUnknown = new ArrayList<>();
        for (MuleRecord record : flowRecords) {
            String id = flowIds.get(flowName(record));
            Flow flow = record instanceof MuleModel.Flow muleFlow ? walker.flow(id, muleFlow)
                    : walker.subFlow(id, (SubFlow) record);
            flows.add(flow);
            if (flow.ballerina().isPresent() && !flow.ballerina().get().found()) {
                linkUnknown.add(id);
            }
        }

        List<String> testFiles = new ArrayList<>();
        for (Path testFile : loaded.testFiles()) {
            testFiles.add(relativePath(project, testFile));
        }
        testFiles.sort(Comparator.naturalOrder());
        Coverage coverage = new Coverage(new Counts(walker.described(), walker.hidden(), walker.flagged()),
                walker.flaggedElements(), failedFiles, testFiles, linkUnknown);
        Project projectInfo = new Project(project.getFileName().toString(), "mule", platformVersion(project));
        // APIs (the APIkit case) come in Week 3
        return new Description(DescriptionModel.SCHEMA_VERSION, new Generator(GENERATOR_NAME, GENERATOR_VERSION),
                projectInfo, flows, List.of(), globals.connections(), globals.properties(), globals.imports(),
                coverage);
    }

    private static String flowName(MuleRecord record) {
        return record instanceof MuleModel.Flow flow ? flow.name() : ((SubFlow) record).name();
    }

    private static int documentIndex(Context ctx, MuleRecord record) {
        Optional<Context.SourceInfo> sourceInfo = ctx.sourceOf(record);
        if (sourceInfo.isEmpty()) {
            throw new IllegalStateException("No source element was recorded for flow " + flowName(record));
        }
        return XmlElements.childIndex(sourceInfo.get().element());
    }

    // The reader already parsed these files; this second read only collects the global elements
    private static Optional<Element> parseRoot(Path file) {
        try {
            return Optional.of(XmlElements.parseRoot(file));
        } catch (IOException | SAXException | ParserConfigurationException e) {
            return Optional.empty();
        }
    }

    private static Optional<String> platformVersion(Path project) {
        Path artifact = project.resolve("mule-artifact.json");
        if (!Files.isRegularFile(artifact)) {
            return Optional.empty();
        }
        try {
            Matcher matcher = MIN_MULE_VERSION.matcher(Files.readString(artifact, StandardCharsets.UTF_8));
            return matcher.find() ? Optional.of(matcher.group(1)) : Optional.empty();
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    private static String relativePath(Path project, Path file) {
        return project.relativize(file.toAbsolutePath().normalize()).toString().replace(File.separatorChar, '/');
    }
}
