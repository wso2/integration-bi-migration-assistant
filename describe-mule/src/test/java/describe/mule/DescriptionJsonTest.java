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

import com.networknt.schema.InputFormat;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import describe.json.DescriptionWriter;
import describe.model.DescriptionModel;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Set;

/**
 * Every description must match the JSON Schema, and the same project must always give the same JSON.
 */
public class DescriptionJsonTest {

    private JsonSchema schema;

    @BeforeClass
    public void loadSchema() throws IOException {
        try (InputStream in = DescriptionJsonTest.class.getClassLoader()
                .getResourceAsStream(DescriptionModel.SCHEMA_RESOURCE)) {
            Assert.assertNotNull(in, "The schema is not on the classpath: " + DescriptionModel.SCHEMA_RESOURCE);
            schema = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012).getSchema(in);
        }
    }

    @DataProvider(name = "repoProjects")
    public Object[][] repoProjects() throws IOException {
        return TestProjects.repoProjects();
    }

    @Test(dataProvider = "repoProjects")
    public void testJsonIsValidAndRepeatable(Path project) {
        checkProject(project);
    }

    @Test
    public void testJsonIsValidAndRepeatableForOutsideProjects() throws IOException {
        for (Path project : TestProjects.outsideProjects()) {
            checkProject(project);
        }
    }

    private void checkProject(Path project) {
        String json = DescriptionWriter.toJson(MuleDescriber.describe(project));
        Assert.assertEquals(DescriptionWriter.toJson(MuleDescriber.describe(project)), json,
                "Two runs gave different JSON for " + project);
        Set<ValidationMessage> errors = schema.validate(json, InputFormat.JSON);
        Assert.assertTrue(errors.isEmpty(), project + " does not match the schema: " + errors);
    }
}
