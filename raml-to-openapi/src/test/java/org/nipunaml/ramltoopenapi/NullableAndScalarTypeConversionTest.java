package org.nipunaml.ramltoopenapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.swagger.v3.oas.models.media.Schema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.nipunaml.ramltoopenapi.exception.ConverterException;
import org.nipunaml.ramltoopenapi.mapper.TypeConverter;
import org.nipunaml.ramltoopenapi.model.openapi.OpenApiDocument;
import org.nipunaml.ramltoopenapi.writer.OpenApiWriter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
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
    @DisplayName("A | B | nil becomes a oneOf with a null-only option, since nullable needs a type next to it")
    void testNilUnionWithSeveralTypes() {
        Schema<?> count = orderProperties.get("count");
        assertThat(count.getNullable()).isNull();
        assertThat(count.getOneOf()).hasSize(3);
        assertThat(count.getOneOf().subList(0, 2)).extracting(Schema::getType).containsExactly("integer", "string");
        assertThat(TypeConverter.isNullSchema(count.getOneOf().get(2))).isTrue();
    }

    @Test
    @DisplayName("A referenced type | nil becomes a oneOf of the reference and a null-only option")
    void testNilUnionWithReference() {
        Schema<?> parent = orderProperties.get("parent");
        assertThat(parent.getNullable()).isNull();
        assertThat(parent.getOneOf()).hasSize(2);
        assertThat(parent.getOneOf().get(0).get$ref()).isEqualTo(REF_PREFIX + "Order");
        assertThat(TypeConverter.isNullSchema(parent.getOneOf().get(1))).isTrue();
    }

    @Test
    @DisplayName("The written null-only option is a nullable type whose enum allows only null")
    void testWrittenNullOption(@TempDir Path dir) throws ConverterException, IOException {
        File ramlFile = getResourceFile("test-cases/types/10-nullable-and-scalar-types.raml");
        OpenApiDocument document = converter.convert(parser.parse(ramlFile));
        // Only YAML: writeJson configures the shared Json.mapper(), which would change the output of later tests
        File written = new OpenApiWriter().write(document, ramlFile, dir.resolve("api.yaml").toFile(), "yaml");
        JsonNode openApi = new ObjectMapper(new YAMLFactory()).readTree(written);
        assertThat(openApi.at("/components/schemas/Order/properties/parent/oneOf/1"))
            .isEqualTo(objectMapper.readTree("{\"type\":\"object\",\"nullable\":true,\"enum\":[null]}"));
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
