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
import describe.model.DescriptionModel.Counts;
import describe.model.DescriptionModel.Description;
import mule.MigratorUtils;
import mule.MuleMigrator.MuleVersion;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The --describe flag of the Mule CLI and the bal tool. It runs after the normal conversion as its own pass (D2), so
 * the conversion output is the same with or without the flag.
 */
public final class DescribeCommand {

    static final String FOLDER_SUFFIX = "-description";

    private DescribeCommand() {
    }

    public static void run(String inputPathArg, String outputPathArg, boolean dryRun, boolean multiRoot) {
        run(inputPathArg, outputPathArg, dryRun, multiRoot, System.out);
    }

    static void run(String inputPathArg, String outputPathArg, boolean dryRun, boolean multiRoot, PrintStream out) {
        assert inputPathArg != null && out != null;
        if (multiRoot) {
            out.println("--describe is not supported for --multi-root yet. No description was written.");
            return;
        }
        Path source = Path.of(inputPathArg);
        // The same test as the migrator uses to treat the input as a single file
        if (Files.isRegularFile(source) && inputPathArg.endsWith(".xml")) {
            out.println("--describe is not supported for a single .xml file yet. No description was written.");
            return;
        }
        if (!Files.isDirectory(source)) {
            out.println("--describe skipped: '" + source + "' is not a project folder.");
            return;
        }
        if (MigratorUtils.detectVersionForProject(source) != MuleVersion.MULE_V4) {
            out.println("--describe is not supported for Mule 3 projects yet. No description was written.");
            return;
        }

        Description description;
        try {
            description = MuleDescriber.describe(source);
        } catch (RuntimeException e) {
            // The conversion output is already written, so a describe failure must not stop the tool
            out.println("--describe failed: " + e + ". The Ballerina output is not affected.");
            return;
        }
        printSummary(description, out);
        if (dryRun) {
            out.println("Dry run: no description was written.");
            return;
        }

        // The same target folder rule as the migrator uses for the Ballerina package
        Path target = outputPathArg != null ? Path.of(outputPathArg) : source;
        Path folder = target.resolve(description.project().name() + FOLDER_SUFFIX);
        Path file = folder.resolve(MuleDescriber.DESCRIPTION_FILE);
        try {
            Files.createDirectories(folder);
            DescriptionWriter.write(description, file);
        } catch (IOException e) {
            out.println("--describe could not write " + file + ": " + e.getMessage());
            return;
        }
        out.println("Description written to " + file);
    }

    private static void printSummary(Description description, PrintStream out) {
        Counts counts = description.coverage().counts();
        out.println("Description of " + description.project().name() + ": " + description.flows().size()
                + " flow(s), " + description.apis().size() + " API(s); elements: " + counts.described() + " described, "
                + counts.hidden() + " hidden, " + counts.flagged() + " flagged");
        out.println("  Flows with an unknown Ballerina link: " + description.coverage().linkUnknown().size()
                + "; files that could not be read: " + description.coverage().failedFiles().size());
    }
}
