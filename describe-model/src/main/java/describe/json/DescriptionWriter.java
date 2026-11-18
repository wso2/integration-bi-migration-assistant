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
package describe.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.core.util.Separators;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import describe.model.DescriptionModel.Description;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Writes a {@link Description} as JSON. The format is fixed (2-space indent, '\n' line ends, a final newline), so
 * the same description always gives the same bytes on every platform.
 */
public final class DescriptionWriter {

    private static final String LINE_END = "\n";
    private static final ObjectWriter WRITER = JsonMapper.builder()
            .addModule(new Jdk8Module())
            .defaultPropertyInclusion(JsonInclude.Value.construct(JsonInclude.Include.NON_ABSENT,
                    JsonInclude.Include.NON_ABSENT))
            .build()
            .writer(new DefaultPrettyPrinter()
                    .withObjectIndenter(new DefaultIndenter("  ", LINE_END))
                    .withArrayIndenter(new DefaultIndenter("  ", LINE_END))
                    .withSeparators(Separators.createDefaultInstance()
                            .withObjectFieldValueSpacing(Separators.Spacing.AFTER)
                            .withObjectEmptySeparator("")
                            .withArrayEmptySeparator("")));

    private DescriptionWriter() {
    }

    @NotNull
    public static String toJson(@NotNull Description description) {
        assert description != null;
        try {
            return WRITER.writeValueAsString(description) + LINE_END;
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("The description could not be written as JSON", e);
        }
    }

    public static void write(@NotNull Description description, @NotNull Path file) throws IOException {
        assert file != null;
        Files.writeString(file, toJson(description), StandardCharsets.UTF_8);
    }
}
