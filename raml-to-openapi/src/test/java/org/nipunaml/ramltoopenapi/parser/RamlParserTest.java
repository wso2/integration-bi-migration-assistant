package org.nipunaml.ramltoopenapi.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.nipunaml.ramltoopenapi.exception.ConverterException;
import org.nipunaml.ramltoopenapi.model.raml.RamlDocument;

import java.io.File;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for RamlParser.
 */
@DisplayName("RAML Parser Tests")
class RamlParserTest {

    private RamlParser parser;

    @BeforeEach
    void setUp() {
        parser = new RamlParser();
    }

    private File getResourceFile(String path) {
        URL resource = getClass().getClassLoader().getResource(path);
        assertThat(resource).as("Resource file not found: " + path).isNotNull();
        return new File(resource.getFile());
    }

    @Test
    @DisplayName("Should parse simple RAML file successfully")
    void shouldParseSimpleRaml() throws ConverterException {
        File ramlFile = getResourceFile("test-cases/basic/01-simple-api.raml");

        RamlDocument document = parser.parse(ramlFile);

        assertThat(document).isNotNull();
        assertThat(document.getTitle()).isEqualTo("Simple API");
        assertThat(document.getVersion()).isEqualTo("1.0");
        assertThat(document.getFileName()).isEqualTo("01-simple-api.raml");
        assertThat(document.getApi()).isNotNull();
    }

    @Test
    @DisplayName("Should parse RAML with types")
    void shouldParseRamlWithTypes() throws ConverterException {
        File ramlFile = getResourceFile("test-cases/basic/03-object-types.raml");

        RamlDocument document = parser.parse(ramlFile);

        assertThat(document).isNotNull();
        assertThat(document.getTitle()).isEqualTo("Types API");
        assertThat(document.getApi().types()).isNotEmpty();
    }

    @Test
    @DisplayName("Should parse RAML with security schemes")
    void shouldParseRamlWithSecurity() throws ConverterException {
        File ramlFile = getResourceFile("test-cases/security/05-oauth2.raml");

        RamlDocument document = parser.parse(ramlFile);

        assertThat(document).isNotNull();
        assertThat(document.getApi().securitySchemes()).isNotEmpty();
    }

    @Test
    @DisplayName("Should throw exception for non-existent file")
    void shouldThrowExceptionForNonExistentFile() {
        File nonExistent = new File("non-existent.raml");

        assertThatThrownBy(() -> parser.parse(nonExistent))
            .isInstanceOf(ConverterException.class)
            .hasMessageContaining("RAML file not found");
    }

    @Test
    @DisplayName("Should resolve exchange_modules and root-relative includes against the API root")
    void shouldResolveIncludesAgainstApiRoot() throws ConverterException {
        File ramlFile = getResourceFile("test-cases/includes/api-root-includes/api.raml");

        RamlDocument document = parser.parse(ramlFile);

        assertThat(document.getApi().types()).extracting(type -> type.name()).containsExactly("Order");
        assertThat(document.getApi().resources().get(0).methods().get(0).responses().get(0).body().get(0)
            .example().value()).contains("1001");
    }
}

