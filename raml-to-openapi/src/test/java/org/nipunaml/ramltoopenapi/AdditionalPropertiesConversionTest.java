package org.nipunaml.ramltoopenapi;

import io.swagger.v3.oas.models.media.Schema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.nipunaml.ramltoopenapi.exception.ConverterException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
    @DisplayName("A closed subtype closes its own part of the allOf, which also lists the inherited properties")
    void testClosedSubtype() {
        Schema<?> ownPart = (Schema<?>) schemas.get("Square").getAllOf().get(1);
        assertThat(ownPart.getAdditionalProperties()).isEqualTo(false);
        assertThat(ownPart.getProperties()).containsOnlyKeys("name", "origin", "side");
    }

    @Test
    @DisplayName("A subtype of a closed type is flat, so the closed parent does not reject the subtype's properties")
    void testSubtypeOfClosedType(@TempDir Path dir) throws ConverterException, IOException {
        Path raml = Files.writeString(dir.resolve("api.raml"), """
                #%RAML 1.0
                title: Closed Parent
                types:
                  Point:
                    additionalProperties: false
                    properties:
                      x: integer
                  Tile:
                    type: Point
                    properties:
                      colour: string
                  Board:
                    properties:
                      start: Point
                      corner:
                        type: Point
                        properties:
                          label: string
                """);
        Map<String, Schema> closedParentSchemas = converter.convert(parser.parse(raml.toFile()))
            .getOpenApi().getComponents().getSchemas();

        Schema<?> tile = closedParentSchemas.get("Tile");
        assertThat(tile.getAllOf()).isNull();
        assertThat(tile.getProperties()).containsOnlyKeys("x", "colour");
        assertThat(tile.getAdditionalProperties()).isEqualTo(false);

        Map<String, Schema> boardProperties = closedParentSchemas.get("Board").getProperties();
        assertThat(boardProperties.get("start").get$ref()).isEqualTo("#/components/schemas/Point");
        Schema<?> corner = boardProperties.get("corner");
        assertThat(corner.getAllOf()).isNull();
        assertThat(corner.getProperties()).containsOnlyKeys("x", "label");
        assertThat(corner.getAdditionalProperties()).isEqualTo(false);
    }
}
