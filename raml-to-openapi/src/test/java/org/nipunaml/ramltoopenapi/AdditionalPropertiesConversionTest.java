package org.nipunaml.ramltoopenapi;

import io.swagger.v3.oas.models.media.Schema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.nipunaml.ramltoopenapi.exception.ConverterException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests that RAML's additionalProperties facet reaches the OpenAPI schemas.
 */
@DisplayName("Additional Properties Conversion Tests")
class AdditionalPropertiesConversionTest extends BaseConversionTest {

    private Map<String, Schema> schemas;

    @BeforeEach
    void convert() throws ConverterException {
        schemas = converter.convert(parser.parse(getResourceFile("test-cases/types/11-additional-properties.raml")))
            .getOpenApi().getComponents().getSchemas();
    }

    @Test
    @DisplayName("additionalProperties: false closes the object")
    void testClosedObject() {
        assertThat(schemas.get("Point").getAdditionalProperties()).isEqualTo(false);
    }

    @Test
    @DisplayName("Objects stay open by default, and a closed nested object stays closed")
    void testOpenObjectWithClosedNestedObject() {
        Schema<?> shape = schemas.get("Shape");
        assertThat(shape.getAdditionalProperties()).isEqualTo(true);
        assertThat(((Schema<?>) shape.getProperties().get("origin")).getAdditionalProperties()).isEqualTo(false);
    }

    @Test
    @DisplayName("A closed subtype closes its own part of the allOf")
    void testClosedSubtype() {
        Schema<?> ownPart = (Schema<?>) schemas.get("Square").getAllOf().get(1);
        assertThat(ownPart.getAdditionalProperties()).isEqualTo(false);
    }
}
