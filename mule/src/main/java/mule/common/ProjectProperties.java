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
package mule.common;

import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Configuration property values of a Mule project, read from its {@code .properties} and YAML files, for the few
 * places where the conversion needs a value rather than a configurable reference. Nested YAML keys are joined with
 * dots, as Mule names them. The first file that defines a property wins.
 */
public final class ProjectProperties {

    private static final Pattern PLACEHOLDER = Pattern.compile("\\$\\{(?:secure::)?([^}]+)}");

    private final Map<String, String> values;

    private ProjectProperties(Map<String, String> values) {
        this.values = values;
    }

    public static @NotNull ProjectProperties load(List<File> propertyFiles, List<File> yamlFiles, MuleLogger logger) {
        assert propertyFiles != null && yamlFiles != null && logger != null;
        Map<String, String> values = new HashMap<>();
        for (File propertyFile : propertyFiles) {
            try (Reader reader = Files.newBufferedReader(propertyFile.toPath(), StandardCharsets.UTF_8)) {
                Properties properties = new Properties();
                properties.load(reader);
                properties.stringPropertyNames()
                        .forEach(name -> values.putIfAbsent(name, properties.getProperty(name)));
            } catch (IOException | IllegalArgumentException e) {
                logger.logWarn("Could not read properties file %s: %s".formatted(propertyFile, e.getMessage()));
            }
        }
        for (File yamlFile : yamlFiles) {
            try (Reader reader = Files.newBufferedReader(yamlFile.toPath(), StandardCharsets.UTF_8)) {
                if (new Yaml().load(reader) instanceof Map<?, ?> map) {
                    flatten(map, "", values);
                }
            } catch (IOException | RuntimeException e) {
                logger.logWarn("Could not read YAML file %s: %s".formatted(yamlFile, e.getMessage()));
            }
        }
        return new ProjectProperties(values);
    }

    private static void flatten(Map<?, ?> map, String prefix, Map<String, String> values) {
        map.forEach((key, value) -> {
            String name = prefix + key;
            if (value instanceof Map<?, ?> nested) {
                flatten(nested, name + ".", values);
            } else if (value != null && !(value instanceof List<?>)) {
                values.putIfAbsent(name, String.valueOf(value));
            }
        });
    }

    /**
     * Resolves a value written as a single property placeholder, e.g. {@code ${secure::http.listener.path}}.
     *
     * @param value an attribute value
     * @return the property's value for a placeholder, the value itself otherwise, or empty when the placeholder
     *         names a property that no project file defines
     */
    public @NotNull Optional<String> resolve(String value) {
        assert value != null;
        Matcher placeholder = PLACEHOLDER.matcher(value.trim());
        return placeholder.matches() ? Optional.ofNullable(values.get(placeholder.group(1))) : Optional.of(value);
    }
}
