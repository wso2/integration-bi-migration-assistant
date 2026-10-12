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
package mule.common.apispec;

import org.jetbrains.annotations.NotNull;
import org.nipunaml.ramltoopenapi.RamlToOpenApiConverter;
import org.nipunaml.ramltoopenapi.exception.ConverterException;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

/**
 * Reads a spec file into an {@link ApiSpec}. Every format is first brought to an OpenAPI model, so
 * {@link OpenApiSpecReader} is the only place that builds an {@code ApiSpec}.
 */
public final class ApiSpecReader {

    private static final String RAML_HEADER = "#%RAML";

    private ApiSpecReader() {
    }

    public static @NotNull ApiSpec read(Path specFile) throws ApiSpecReadException {
        assert specFile != null;
        if (!isRaml(specFile)) {
            throw new ApiSpecReadException("Only RAML specs are supported; OpenAPI support is not implemented yet: "
                    + specFile);
        }
        try {
            return OpenApiSpecReader.read(RamlToOpenApiConverter.create().convertToOpenApi(specFile.toFile()));
        } catch (ConverterException e) {
            throw new ApiSpecReadException("Could not parse RAML spec " + specFile + ": " + e.getMessage(), e);
        }
    }

    private static boolean isRaml(Path specFile) throws ApiSpecReadException {
        if (specFile.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".raml")) {
            return true;
        }
        try (BufferedReader reader = Files.newBufferedReader(specFile, StandardCharsets.UTF_8)) {
            String firstLine = reader.readLine();
            return firstLine != null && firstLine.stripLeading().startsWith(RAML_HEADER);
        } catch (IOException e) {
            throw new ApiSpecReadException("Could not read spec " + specFile + ": " + e.getMessage(), e);
        }
    }
}
