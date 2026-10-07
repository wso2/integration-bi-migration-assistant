package org.nipunaml.ramltoopenapi;

import io.swagger.v3.oas.models.media.Schema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.nipunaml.ramltoopenapi.exception.ConverterException;

import java.io.File;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests nil unions, date and time scalars, any, array facets and named type expressions.
 */
@DisplayName("Nullable And Scalar Type Conversion Tests")
class NullableAndScalarTypeConversionTest extends BaseConversionTest {

    private static final String REF_PREFIX = "#/components/schemas/";

    private Map<String, Schema> schemas;
    private Map<String, Schema> orderProperties;

    @BeforeEach
    void convert() throws ConverterException {
        File ramlFile = getResourceFile("test-cases/types/10-nullable-and-scalar-types.raml");
        schemas = converter.convert(parser.parse(ramlFile)).getOpenApi().getComponents().getSchemas();
        orderProperties = schemas.get("Order").getProperties();
    }

    @Test
    @DisplayName("T | nil becomes T with nullable")
    void testNilUnionWithOneType() {
        Schema<?> note = orderProperties.get("note");
        assertThat(note.getType()).isEqualTo("string");
        assertThat(note.getNullable()).isTrue();
        assertThat(note.getOneOf()).isNull();
    }

    @Test
    @DisplayName("A | B | nil becomes a nullable oneOf without the nil option")
    void testNilUnionWithSeveralTypes() {
        Schema<?> count = orderProperties.get("count");
        assertThat(count.getNullable()).isTrue();
        assertThat(count.getOneOf()).extracting(Schema::getType).containsExactly("integer", "string");
    }

    @Test
    @DisplayName("A referenced type | nil keeps the reference inside a nullable oneOf")
    void testNilUnionWithReference() {
        Schema<?> parent = orderProperties.get("parent");
        assertThat(parent.getNullable()).isTrue();
        assertThat(parent.getOneOf()).extracting(Schema::get$ref).containsExactly(REF_PREFIX + "Order");
    }

    @Test
    @DisplayName("nil alone becomes a nullable schema without a type")
    void testNil() {
        Schema<?> nothing = orderProperties.get("nothing");
        assertThat(nothing.getNullable()).isTrue();
        assertThat(nothing.getType()).isNull();
    }

    @Test
    @DisplayName("Date and time scalars become strings with a format")
    void testDateAndTimeScalars() {
        assertStringWithFormat(orderProperties.get("placedOn"), "date");
        assertStringWithFormat(orderProperties.get("placedAt"), "datetime-only");
        assertStringWithFormat(orderProperties.get("cutoff"), "time");
        assertStringWithFormat(orderProperties.get("createdAt"), "date-time");
    }

    @Test
    @DisplayName("any becomes a schema without a type")
    void testAny() {
        Schema<?> metadata = orderProperties.get("metadata");
        assertThat(metadata.getType()).isNull();
        assertThat(metadata.getProperties()).isNull();
    }

    @Test
    @DisplayName("Type[] with facets keeps minItems and maxItems")
    void testArrayShorthandKeepsFacets() {
        Schema<?> lines = orderProperties.get("lines");
        assertThat(lines.getType()).isEqualTo("array");
        assertThat(lines.getMinItems()).isEqualTo(1);
        assertThat(lines.getMaxItems()).isEqualTo(5);
        assertThat(lines.getItems().get$ref()).isEqualTo(REF_PREFIX + "Order");
    }

    @Test
    @DisplayName("Named type expressions are converted, not treated as inheritance")
    void testNamedTypeExpressions() {
        Schema<?> maybeName = schemas.get("MaybeName");
        assertThat(maybeName.getAllOf()).isNull();
        assertThat(maybeName.getType()).isEqualTo("string");
        assertThat(maybeName.getNullable()).isTrue();

        Schema<?> orders = schemas.get("Orders");
        assertThat(orders.getAllOf()).isNull();
        assertThat(orders.getType()).isEqualTo("array");
        assertThat(orders.getItems().get$ref()).isEqualTo(REF_PREFIX + "Order");

        Schema<?> shortId = schemas.get("ShortId");
        assertThat(shortId.getAllOf()).isNull();
        assertThat(shortId.getType()).isNotEqualTo("object");
    }

    private static void assertStringWithFormat(Schema<?> schema, String format) {
        assertThat(schema.getType()).isEqualTo("string");
        assertThat(schema.getFormat()).isEqualTo(format);
    }
}
