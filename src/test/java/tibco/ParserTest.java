package tibco;
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

import converter.TibcoToBalConverter;
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class ParserTest {

    private static final Path RESOURCE_DIRECTORY = Path.of("src", "test", "resources", "tibco");

    @Test(groups = {"tibco", "parser"}, dataProvider = "parserDataProvider")
    public void testParser(Path path, TestKind kind) {
        try {
            SyntaxTree tree = TibcoToBalConverter.convertToBallerina(path.toString());
            if (kind == TestKind.ERROR) {
                throw new AssertionError("Parsing succeeded for an invalid input: " + path);
            }
        } catch (Exception e) {
            if (kind == TestKind.VALID) {
                throw new AssertionError("Parsing failed for a valid input: " + path, e);
            }
        }
    }

    @DataProvider(name = "parserDataProvider")
    public Object[][] parserDataProvider() throws IOException {
        try (Stream<Path> paths = Files.walk(RESOURCE_DIRECTORY)) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(ParserTest::isBWP)
                    .map(path -> new Object[]{path, TestKind.from(path)})
                    .toArray(Object[][]::new);
        }
    }

    private static boolean isBWP(Path path) {
        return path.toString().endsWith(".bwp");
    }

    public enum TestKind {
        VALID,
        ERROR;

        public static TestKind from(Path path) {
            String fileName = path.getFileName().toString();
            int startIndex = fileName.lastIndexOf('-') + 1;
            int endIndex = fileName.lastIndexOf(".bwp");
            if (startIndex > 0 && endIndex > startIndex) {
                String suffix = fileName.substring(startIndex, endIndex).toLowerCase();
                return switch (suffix) {
                    case "t" -> VALID;
                    case "e" -> ERROR;
                    default -> throw new IllegalArgumentException("Invalid file suffix in" + path);
                };
            }
            throw new IllegalArgumentException("Invalid file name" + path);
        }
    }

}