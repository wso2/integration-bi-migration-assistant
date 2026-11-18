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
package describe;

import com.networknt.schema.ValidationMessage;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Each document in {@code schema-cases} is valid if its name starts with {@code valid-}, and invalid otherwise.
 */
public class SchemaCasesTest {

    @DataProvider(name = "cases")
    public Object[][] cases() throws IOException, URISyntaxException {
        URL dir = SchemaCasesTest.class.getClassLoader().getResource("schema-cases");
        assert dir != null : "schema-cases not on the test classpath";
        try (Stream<Path> files = Files.list(Path.of(dir.toURI()))) {
            return files.filter(file -> file.getFileName().toString().endsWith(".json"))
                    .sorted()
                    .map(file -> new Object[]{file})
                    .toArray(Object[][]::new);
        }
    }

    @Test(dataProvider = "cases")
    public void testSchemaCase(Path file) throws IOException {
        Set<ValidationMessage> errors = SchemaValidation.validate(Files.readString(file));
        if (file.getFileName().toString().startsWith("valid-")) {
            Assert.assertTrue(errors.isEmpty(), file.getFileName() + " should be valid: " + errors);
        } else {
            Assert.assertFalse(errors.isEmpty(), file.getFileName() + " should be invalid");
        }
    }
}
