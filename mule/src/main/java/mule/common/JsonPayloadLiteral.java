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
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package mule.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Turns the JSON text of a {@code set-payload} with a JSON {@code mimeType} into a Ballerina {@code json} value,
 * so the payload keeps its JSON shape (and is sent as {@code application/json}) instead of staying a string.
 */
public final class JsonPayloadLiteral {

    private static final ObjectMapper JSON = new ObjectMapper()
            .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
            .enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS);

    private JsonPayloadLiteral() {
    }

    /**
     * @param mimeType the {@code mimeType} attribute of a Mule element, possibly empty or with parameters
     * @return whether the mime type denotes JSON, such as {@code application/json; charset=UTF-8} or
     *         {@code application/hal+json}
     */
    public static boolean isJsonMimeType(@NotNull String mimeType) {
        String mediaType = mimeType.split(";", 2)[0].trim().toLowerCase(Locale.ROOT);
        return mediaType.equals("application/json") || mediaType.equals("text/json") || mediaType.endsWith("+json");
    }

    /**
     * @param muleValue the raw {@code value} attribute of a {@code set-payload}
     * @return whether the value is plain text, with no MEL/DataWeave expression or property placeholder in it
     */
    public static boolean isPlainText(@NotNull String muleValue) {
        return !muleValue.contains("#[") && !muleValue.contains("${");
    }

    /**
     * @param text text that may be a JSON document
     * @return whether the text is a single valid JSON document
     */
    public static boolean isJsonText(@NotNull String text) {
        return parse(text).isPresent();
    }

    /**
     * @param jsonText text expected to be a JSON document
     * @return the equivalent Ballerina {@code json} expression, or empty when the text is not valid JSON or holds a
     *         value Ballerina source cannot represent exactly, such as a number out of range or a lone surrogate
     */
    @NotNull
    public static Optional<String> toBalJsonLiteral(@NotNull String jsonText) {
        return parse(jsonText).flatMap(node -> {
            StringBuilder literal = new StringBuilder();
            return appendBalJson(node, literal) ? Optional.of(literal.toString()) : Optional.empty();
        });
    }

    /**
     * @param text text from a Mule config; XML cannot carry an unpaired surrogate, so neither can this text
     * @return the text as a Ballerina string literal, with quotes, backslashes and control characters escaped
     */
    @NotNull
    public static String toBalStringLiteral(@NotNull String text) {
        StringBuilder literal = new StringBuilder();
        boolean written = appendBalString(text, literal);
        assert written : "unpaired surrogate in " + text;
        return literal.toString();
    }

    private static Optional<JsonNode> parse(String text) {
        try {
            JsonNode node = JSON.readTree(text);
            return node == null || node.isMissingNode() ? Optional.empty() : Optional.of(node);
        } catch (JsonProcessingException e) {
            return Optional.empty();
        }
    }

    private static boolean appendBalJson(JsonNode node, StringBuilder out) {
        if (node.isObject()) {
            out.append('{');
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                if (!appendBalString(field.getKey(), out)) {
                    return false;
                }
                out.append(": ");
                if (!appendBalJson(field.getValue(), out)) {
                    return false;
                }
                if (fields.hasNext()) {
                    out.append(", ");
                }
            }
            out.append('}');
            return true;
        }
        if (node.isArray()) {
            out.append('[');
            for (int i = 0; i < node.size(); i++) {
                if (i > 0) {
                    out.append(", ");
                }
                if (!appendBalJson(node.get(i), out)) {
                    return false;
                }
            }
            out.append(']');
            return true;
        }
        if (node.isTextual()) {
            return appendBalString(node.textValue(), out);
        }
        if (node.isNumber()) {
            return appendBalNumber(node.decimalValue(), out);
        }
        if (node.isBoolean() || node.isNull()) {
            out.append(node.asText());
            return true;
        }
        return false;
    }

    private static boolean appendBalNumber(BigDecimal value, StringBuilder out) {
        // A number written without a fraction, such as 12 or 1e2, is an integer; Ballerina's int is 64-bit
        if (value.scale() <= 0) {
            try {
                out.append(value.longValueExact());
                return true;
            } catch (ArithmeticException e) {
                return false;
            }
        }
        // Ballerina reads a fractional literal in a json context as a float, so it must survive that round trip
        double asFloat = value.doubleValue();
        if (Double.isInfinite(asFloat) || new BigDecimal(Double.toString(asFloat)).compareTo(value) != 0) {
            return false;
        }
        out.append(value);
        return true;
    }

    private static boolean appendBalString(String value, StringBuilder out) {
        out.append('"');
        for (int cp : value.codePoints().toArray()) {
            // An unpaired surrogate cannot be written in Ballerina source, not even as a unicode escape
            if (cp >= Character.MIN_SURROGATE && cp <= Character.MAX_SURROGATE) {
                return false;
            }
            switch (cp) {
                case '"' -> out.append("\\\"");
                case '\\' -> out.append("\\\\");
                case '\n' -> out.append("\\n");
                case '\r' -> out.append("\\r");
                case '\t' -> out.append("\\t");
                default -> {
                    if (cp < 0x20 || cp == 0x7F) {
                        out.append("\\u{").append(Integer.toHexString(cp).toUpperCase(Locale.ROOT)).append('}');
                    } else {
                        out.appendCodePoint(cp);
                    }
                }
            }
        }
        out.append('"');
        return true;
    }
}
