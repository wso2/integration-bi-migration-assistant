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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import describe.model.DescriptionModel;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Set;

/**
 * Validates JSON text against the description schema on the main classpath.
 */
final class SchemaValidation {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final JsonSchema SCHEMA = loadSchema();

    private SchemaValidation() {
    }

    static Set<ValidationMessage> validate(String json) throws IOException {
        return SCHEMA.validate(MAPPER.readTree(json));
    }

    private static JsonSchema loadSchema() {
        try (InputStream in = SchemaValidation.class.getClassLoader()
                .getResourceAsStream(DescriptionModel.SCHEMA_RESOURCE)) {
            assert in != null : "schema not on the classpath: " + DescriptionModel.SCHEMA_RESOURCE;
            return JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012).getSchema(MAPPER.readTree(in));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
