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
 *  KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package mule.v4;

import common.BallerinaModel.ModuleTypeDef;
import mule.MuleMigrator.MuleVersion;
import mule.common.MuleLogger;
import mule.common.apispec.ApiSpec.ApiType;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class ApiSpecTypesContextTest {

    private static final Path SPEC_PROJECT = Path.of("src/test/resources/mule/v4/projects/apiKitSpecProject");

    @Test
    public void testWritesUsedSpecTypesAfterContextTypes() {
        Context ctx = parse(SPEC_PROJECT);

        Assert.assertEquals(ctx.projectCtx.apiTypeGenerators.get("orders-config").use(new ApiType.Ref("PriorityOrder")),
                "PriorityOrder");
        Assert.assertEquals(typeNames(ctx), List.of("Context", "Status", "Address", "Order", "PriorityOrder"));
    }

    @Test
    public void testWritesNoSpecTypesWhenNoneAreUsed() {
        Assert.assertEquals(typeNames(parse(SPEC_PROJECT)), List.of("Context"));
    }

    @Test
    public void testConfigsSharingASpecShareItsTypes() throws IOException {
        Path project = Files.createTempDirectory("shared-spec-project");
        try {
            copyTree(SPEC_PROJECT.resolve("src/main/resources"), project.resolve("src/main/resources"));
            Path muleDir = Files.createDirectories(project.resolve("src/main/mule"));
            Files.writeString(muleDir.resolve("globals.xml"), """
                    <?xml version="1.0" encoding="UTF-8"?>
                    <mule xmlns="http://www.mulesoft.org/schema/mule/core"
                          xmlns:apikit="http://www.mulesoft.org/schema/mule/apikit">
                        <apikit:config name="first-config" api="api\\orders-api.raml"/>
                        <apikit:config name="second-config" api="orders-api.raml"/>
                    </mule>
                    """);
            Context ctx = parse(project);

            Assert.assertSame(ctx.projectCtx.apiTypeGenerators.get("first-config"),
                    ctx.projectCtx.apiTypeGenerators.get("second-config"));
            ctx.projectCtx.apiTypeGenerators.get("first-config").use(new ApiType.Ref("Order"));
            ctx.projectCtx.apiTypeGenerators.get("second-config").use(new ApiType.Ref("Order"));
            Assert.assertEquals(typeNames(ctx), List.of("Context", "Status", "Address", "Order"));
        } finally {
            deleteTree(project);
        }
    }

    private static Context parse(Path project) {
        Path muleAppDir = project.resolve("src/main/mule");
        List<File> xmlFiles;
        try (Stream<Path> files = Files.list(muleAppDir)) {
            xmlFiles = files.filter(file -> file.toString().endsWith(".xml")).sorted().map(Path::toFile).toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Context ctx = new Context(xmlFiles, List.of(), muleAppDir, MuleVersion.MULE_V4, List.of(),
                project.getFileName().toString(), true, false, new MuleLogger(false), null, null);
        ctx.parseAllFiles();
        return ctx;
    }

    private static List<String> typeNames(Context ctx) {
        return MuleToBalConverter.createContextTypeDefns(ctx).stream().map(ModuleTypeDef::name).toList();
    }

    private static void copyTree(Path source, Path target) throws IOException {
        try (Stream<Path> paths = Files.walk(source)) {
            for (Path path : paths.toList()) {
                Path destination = target.resolve(source.relativize(path).toString());
                if (Files.isDirectory(path)) {
                    Files.createDirectories(destination);
                } else {
                    Files.copy(path, destination);
                }
            }
        }
    }

    private static void deleteTree(Path root) throws IOException {
        try (Stream<Path> paths = Files.walk(root)) {
            for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.delete(path);
            }
        }
    }
}
