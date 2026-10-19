package org.nipunaml.ramltoopenapi;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.nipunaml.ramltoopenapi.exception.ConverterException;
import org.nipunaml.ramltoopenapi.mapper.MethodMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests that the names of the RAML traits applied to an operation reach the OpenAPI operation.
 */
@DisplayName("Trait Conversion Tests")
class TraitConversionTest extends BaseConversionTest {

    private Paths paths;

    @BeforeEach
    void convert() throws ConverterException {
        paths = converter.convert(parser.parse(getResourceFile("test-cases/traits/12-traits.raml")))
            .getOpenApi().getPaths();
    }

    @Test
    @DisplayName("Resource and method traits are listed once each, without their library prefix")
    void testListsResourceAndMethodTraits() {
        Operation listOrders = paths.get("/orders").getGet();
        assertThat(listOrders.getExtensions().get(MethodMapper.TRAITS_EXTENSION))
            .isEqualTo(List.of("client-id-enforcement", "paged"));
        assertThat(listOrders.getParameters()).extracting(Parameter::getName)
            .contains("client-id", "client-secret", "page");
    }

    @Test
    @DisplayName("A nested resource does not inherit the traits of its parent")
    void testNestedResourceHasNoTraits() {
        assertThat(paths.get("/orders/{id}").getGet().getExtensions()).isNull();
    }
}
