package org.nipunaml.ramltoopenapi;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * Tests for basic RAML to OpenAPI conversion scenarios.
 * Tests simple API structures, descriptions, and object types.
 */
@DisplayName("Basic Conversion Tests")
class BasicConversionTest extends BaseConversionTest {

    private static final String TEST_DIR = "test-cases/basic";

    @Test
    @DisplayName("01 - Simple API with minimal structure")
    void testSimpleApi() throws Exception {
        testConversion(TEST_DIR, "01-simple-api");
    }

    @Test
    @DisplayName("02 - API with descriptions")
    void testWithDescription() throws Exception {
        testConversion(TEST_DIR, "02-with-description");
    }

    @Test
    @DisplayName("03 - Object types definition")
    void testObjectTypes() throws Exception {
        testConversion(TEST_DIR, "03-object-types");
    }

    @Test
    @DisplayName("13 - Display names of methods and resources")
    void testDisplayNames() throws Exception {
        // NON_EXTENSIBLE fails on any field the expected file does not list, so a default display name such as
        // "get" cannot appear unnoticed
        JSONAssert.assertEquals(loadExpectedJson(TEST_DIR + "/13-display-names.expected.json"),
                convertRamlToJson(TEST_DIR + "/13-display-names.raml"), JSONCompareMode.NON_EXTENSIBLE);
    }
}

