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

import common.BallerinaModel.TextDocument;
import common.CodeGenerator;
import mule.common.MuleLogger;
import mule.v4.Context;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * The Week 2 gate for the hooks: turning on the describe observer must not change the conversion. Each Mule 4 test
 * file of the mule module is converted twice, once without and once with the observer.
 */
public class UnchangedConversionTest {

    @DataProvider(name = "muleFiles")
    public Object[][] muleFiles() throws IOException {
        // Folders named *_output hold what earlier test runs wrote, not test input
        try (Stream<Path> files = Files.walk(TestProjects.MULE_V4_TEST_RESOURCES)) {
            return files.filter(file -> file.toString().endsWith(".xml"))
                    .filter(file -> !file.toString().contains("_output"))
                    .sorted()
                    .map(file -> new Object[]{file})
                    .toArray(Object[][]::new);
        }
    }

    @Test(dataProvider = "muleFiles")
    public void testObserverDoesNotChangeConversion(Path muleFile) {
        Assert.assertEquals(convert(muleFile, true), convert(muleFile, false));
    }

    // The same steps as MuleToBalConverter.convertStandaloneXMLFileToBallerina, but it keeps every generated
    // document and the problem lists the migration report is built from
    private static Conversion convert(Path muleFile, boolean withObserver) {
        Context ctx = new Context(List.of(muleFile.toFile()), List.of(), new MuleLogger(state -> { }, log -> { }));
        if (withObserver) {
            ctx.enableDescribe(new ConversionRecorder(ctx.migrationMetrics));
        }
        ctx.parseAllFiles();
        StringBuilder ballerina = new StringBuilder();
        for (TextDocument document : ctx.codeGen()) {
            ballerina.append(new CodeGenerator(document).generateSyntaxTree().toSourceCode()).append('\n');
        }
        return new Conversion(ballerina.toString(), new ArrayList<>(ctx.migrationMetrics.failedBlocks),
                new ArrayList<>(ctx.migrationMetrics.dwConversionStats.failedDWExpressions));
    }

    private record Conversion(String ballerina, List<String> failedBlocks, List<String> failedDataWeave) {
    }
}
