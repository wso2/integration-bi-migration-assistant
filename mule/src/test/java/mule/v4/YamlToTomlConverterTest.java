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
package mule.v4;

import mule.MuleMigrator;
import mule.common.MuleLogger;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.testng.Assert.assertEquals;


/**
 * Test class for converting YAML file properties to TOML format.
 */
public class YamlToTomlConverterTest {

    private Path tempDir;

    @BeforeClass
    public void setup() throws IOException {
        tempDir = Files.createTempDirectory("yaml-toml-test");
    }

    @AfterClass
    public void cleanup() throws IOException {
        Files.walk(tempDir)
                .sorted((a, b) -> b.compareTo(a))
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        // Ignore cleanup errors
                    }
                });
    }

    @Test
    public void testSimpleYamlToToml() throws IOException {
        String yamlContent = """
                app:
                  name: "test-app"
                  version: "1.0.0"
                  debug: true
                """;

        String expectedToml = """
                # Properties from test.yaml
                app_name = "test-app"
                app_version = "1.0.0"
                app_debug = "true"
                
                """;

        assertYamlToTomlConversion(yamlContent, expectedToml, "test.yaml");
    }

    @Test
    public void testNestedYamlToToml() throws IOException {
        String yamlContent = """
                database:
                  connection:
                    host: "localhost"
                    port: 5432
                    ssl:
                      enabled: true
                      verify: false
                """;

        String expectedToml = """
                # Properties from database.yaml
                database_connection_host = "localhost"
                database_connection_port = "5432"
                database_connection_ssl_enabled = "true"
                database_connection_ssl_verify = "false"
                
                """;

        assertYamlToTomlConversion(yamlContent, expectedToml, "database.yaml");
    }

    @Test
    public void testYamlArrayToToml() throws IOException {
        String yamlContent = """
                servers:
                  - "server1.example.com"
                  - "server2.example.com"
                  - "server3.example.com"
                """;

        String expectedToml = """
                # Properties from servers.yaml
                servers_0 = "server1.example.com"
                servers_1 = "server2.example.com"
                servers_2 = "server3.example.com"
                
                """;

        assertYamlToTomlConversion(yamlContent, expectedToml, "servers.yaml");
    }

    @Test
    public void testComplexYamlToToml() throws IOException {
        String yamlContent = """
                application:
                  name: "my-mule-app"
                  environment: "production"
                  features:
                    - "feature-a"
                    - "feature-b"
                  database:
                    primary:
                      host: "db1.example.com"
                      port: 3306
                      credentials:
                        username: "admin"
                        password: "secret123"
                    secondary:
                      host: "db2.example.com"
                      port: 3306
                """;

        String expectedToml = """
                # Properties from complex.yaml
                application_name = "my-mule-app"
                application_environment = "production"
                application_features_0 = "feature-a"
                application_features_1 = "feature-b"
                application_database_primary_host = "db1.example.com"
                application_database_primary_port = "3306"
                application_database_primary_credentials_username = "admin"
                application_database_primary_credentials_password = "secret123"
                application_database_secondary_host = "db2.example.com"
                application_database_secondary_port = "3306"
                
                """;

        assertYamlToTomlConversion(yamlContent, expectedToml, "complex.yaml");
    }

    @Test
    public void testYamlWithHyphensToToml() throws IOException {
        String yamlContent = """
                http-listener:
                  host-name: "localhost"
                  port-number: 8080
                  ssl-config:
                    key-store: "/path/to/keystore"
                    trust-store: "/path/to/truststore"
                """;

        String expectedToml = """
                # Properties from hyphen-test.yaml
                http_listener_host_name = "localhost"
                http_listener_port_number = "8080"
                http_listener_ssl_config_key_store = "/path/to/keystore"
                http_listener_ssl_config_trust_store = "/path/to/truststore"
                
                """;

        assertYamlToTomlConversion(yamlContent, expectedToml, "hyphen-test.yaml");
    }

    @Test
    public void testMixedYamlAndPropertiesFiles() throws IOException {
        // Create YAML file
        String yamlContent = """
                app:
                  name: "yaml-app"
                  version: "2.0.0"
                """;
        Path yamlFile = createTempFile("config.yaml", yamlContent);

        // Create properties file
        String propertiesContent = """
                # Database configuration
                db.host=localhost
                db.port=5432
                db.username=admin
                """;
        Path propertiesFile = createTempFile("database.properties", propertiesContent);

        String expectedToml = """
                # Properties from database.properties
                db_host = "localhost"
                db_port = "5432"
                db_username = "admin"
                
                # Properties from config.yaml
                app_name = "yaml-app"
                app_version = "2.0.0"
                
                """;

        StringBuilder tomlContent = new StringBuilder();
        tomlContent.append("# Properties from database.properties\n");
        MuleLogger logger = new MuleLogger(true);
        MuleMigrator.processPropertiesFile(logger, propertiesFile.toFile(), tomlContent);
        tomlContent.append("\n");
        tomlContent.append("# Properties from config.yaml\n");
        MuleMigrator.processYamlFile(logger, yamlFile.toFile(), tomlContent);
        tomlContent.append("\n");

        assertEquals(tomlContent.toString(), expectedToml);
    }

    private void assertYamlToTomlConversion(String yamlContent, String expectedToml, String filename)
            throws IOException {
        Path yamlFile = createTempFile(filename, yamlContent);

        StringBuilder tomlContent = new StringBuilder();
        tomlContent.append("# Properties from ").append(filename).append("\n");
        MuleLogger logger = new MuleLogger(true);
        MuleMigrator.processYamlFile(logger, yamlFile.toFile(), tomlContent);
        tomlContent.append("\n");

        assertEquals(tomlContent.toString(), expectedToml);
    }

    private Path createTempFile(String filename, String content) throws IOException {
        Path file = tempDir.resolve(filename);
        Files.writeString(file, content);
        return file;
    }
}
