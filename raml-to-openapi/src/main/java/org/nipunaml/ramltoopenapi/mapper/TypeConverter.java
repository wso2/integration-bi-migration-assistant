package org.nipunaml.ramltoopenapi.mapper;

import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.BooleanSchema;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.NumberSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import org.nipunaml.ramltoopenapi.exception.ConverterException;
import org.raml.v2.api.model.v10.datamodel.AnyTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.BooleanTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.DateTimeOnlyTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.DateTimeTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.DateTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.FileTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.IntegerTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.NullTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.NumberTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TimeOnlyTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.UnionTypeDeclaration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Converts RAML type declarations to OpenAPI/JSON Schema types.
 */
public class TypeConverter {
    
    private static final Logger logger = LoggerFactory.getLogger(TypeConverter.class);

    /**
     * OpenAPI has no format for a date-time without a time zone; formats are open-ended, so tools that do not know
     * this one treat the value as a plain string.
     */
    public static final String DATETIME_ONLY_FORMAT = "datetime-only";

    /**
     * Converts a RAML type to an OpenAPI schema.
     */
    public Schema<?> convertType(TypeDeclaration type, MapperContext context)
            throws ConverterException {

        if (type == null) {
            return new ObjectSchema(); // Default to object
        }

        String typeName = type.type();
        logger.debug("Converting type: {} ({})", type.name(), typeName);

        // CRITICAL: Check if this is a reference to an array type like "Product[]"
        if (typeName != null && typeName.endsWith("[]")) {
            String baseType = typeName.substring(0, typeName.length() - 2);
            if (context.hasSchema(baseType)) {
                // Check if array schema exists (e.g., "ProductArray")
                String arraySchemaName = baseType + "Array";
                // A $ref cannot carry facets such as minItems, so an array with facets is always inlined
                if (context.hasSchema(arraySchemaName) && !hasArrayFacets(type)) {
                    // Reference the array schema
                    Schema<?> refSchema = new Schema<>();
                    refSchema.set$ref("#/components/schemas/" + arraySchemaName);
                    logger.debug("  Created reference to array schema: {}", arraySchemaName);
                    return refSchema;
                } else {
                    // Fallback: inline array with $ref items
                    ArraySchema arraySchema = new ArraySchema();
                    Schema<?> refSchema = new Schema<>();
                    refSchema.set$ref("#/components/schemas/" + baseType);
                    arraySchema.setItems(refSchema);
                    if (type instanceof ArrayTypeDeclaration) {
                        applyArrayFacets((ArrayTypeDeclaration) type, arraySchema);
                    }
                    logger.debug("  Created inline array reference to: {}", baseType);
                    return arraySchema;
                }
            }
        }

        // Check if this is a direct reference to a named type
        if (typeName != null && context.hasSchema(typeName)
                && !(type instanceof ObjectTypeDeclaration objectType && addsPropertiesToClosedType(objectType))) {
            Schema<?> refSchema = new Schema<>();
            refSchema.set$ref("#/components/schemas/" + typeName);
            logger.debug("  Created reference to: {}", typeName);
            return refSchema;
        }

        // Handle built-in types
        if (type instanceof StringTypeDeclaration) {
            return convertStringType((StringTypeDeclaration) type);
        } else if (type instanceof IntegerTypeDeclaration) {
            return convertIntegerType((IntegerTypeDeclaration) type);
        } else if (type instanceof BooleanTypeDeclaration) {
            return convertBooleanType((BooleanTypeDeclaration) type);
        } else if (type instanceof ArrayTypeDeclaration) {
            return convertArrayType((ArrayTypeDeclaration) type, context);
        } else if (type instanceof ObjectTypeDeclaration) {
            return convertObjectType((ObjectTypeDeclaration) type, context);
        } else if (type instanceof UnionTypeDeclaration) {
            return convertUnionType((UnionTypeDeclaration) type, context);
        } else if (type instanceof DateTimeTypeDeclaration) {
            return convertDateTimeType((DateTimeTypeDeclaration) type);
        } else if (type instanceof FileTypeDeclaration) {
            return convertFileType((FileTypeDeclaration) type);
        } else if (type instanceof NumberTypeDeclaration) {
            return convertNumberType((NumberTypeDeclaration) type);
        } else if (type instanceof DateTypeDeclaration) {
            return new StringSchema().format("date");
        } else if (type instanceof TimeOnlyTypeDeclaration) {
            return new StringSchema().format("time");
        } else if (type instanceof DateTimeOnlyTypeDeclaration) {
            return new StringSchema().format(DATETIME_ONLY_FORMAT);
        } else if (type instanceof NullTypeDeclaration) {
            // OpenAPI 3.0 has no null type; a nullable schema without a type is the closest equivalent
            return new Schema<>().nullable(true);
        } else if (type instanceof AnyTypeDeclaration) {
            return new Schema<>();
        }

        // Handle reference to named type
        if (isReference(typeName)) {
            return createReference(typeName);
        }

        // External JSON/XML schemas and anything else unrecognised accept any value
        logger.warn("Unknown type: {}, defaulting to any", typeName);
        return new Schema<>();
    }
    
    /**
     * Converts RAML string type to OpenAPI string schema.
     */
    private Schema<?> convertStringType(StringTypeDeclaration type) {
        StringSchema schema = new StringSchema();
        
        // Pattern
        if (type.pattern() != null) {
            schema.setPattern(type.pattern());
            logger.debug("  Pattern: {}", type.pattern());
        }
        
        // Min/Max length
        if (type.minLength() != null) {
            schema.setMinLength(type.minLength());
        }
        if (type.maxLength() != null) {
            schema.setMaxLength(type.maxLength());
        }
        
        // Enum values
        if (type.enumValues() != null && !type.enumValues().isEmpty()) {
            List<String> enumValues = new ArrayList<>(type.enumValues());
            schema.setEnum(enumValues);
            logger.debug("  Enum values: {}", enumValues);
        }
        
        return schema;
    }
    
    /**
     * Converts RAML number type to OpenAPI number schema.
     */
    private Schema<?> convertNumberType(NumberTypeDeclaration type) {
        NumberSchema schema = new NumberSchema();
        
        // Format
        if (type.format() != null) {
            schema.setFormat(type.format());
        }
        
        // Minimum/Maximum
        if (type.minimum() != null) {
            schema.setMinimum(BigDecimal.valueOf(type.minimum()));
        }
        if (type.maximum() != null) {
            schema.setMaximum(BigDecimal.valueOf(type.maximum()));
        }
        
        // Multiple of
        if (type.multipleOf() != null) {
            schema.setMultipleOf(BigDecimal.valueOf(type.multipleOf()));
        }
        
        return schema;
    }
    
    /**
     * Converts RAML integer type to OpenAPI integer schema.
     */
    private Schema<?> convertIntegerType(IntegerTypeDeclaration type) {
        IntegerSchema schema = new IntegerSchema();
        
        // Format
        if (type.format() != null) {
            schema.setFormat(type.format());
        } else {
            // Determine format based on range if specified
            if (type.minimum() != null || type.maximum() != null) {
                long min = type.minimum() != null ? type.minimum().longValue() : Long.MIN_VALUE;
                long max = type.maximum() != null ? type.maximum().longValue() : Long.MAX_VALUE;
                
                if (min >= Integer.MIN_VALUE && max <= Integer.MAX_VALUE) {
                    schema.setFormat("int32");
                } else {
                    schema.setFormat("int64");
                }
            }
        }
        
        // Minimum/Maximum
        if (type.minimum() != null) {
            schema.setMinimum(BigDecimal.valueOf(type.minimum()));
        }
        if (type.maximum() != null) {
            schema.setMaximum(BigDecimal.valueOf(type.maximum()));
        }
        
        // Multiple of
        if (type.multipleOf() != null) {
            schema.setMultipleOf(BigDecimal.valueOf(type.multipleOf()));
        }
        
        // Default value
        if (type.defaultValue() != null) {
            try {
                Integer defaultVal = Integer.parseInt(type.defaultValue());
                schema.setDefault(defaultVal);
            } catch (NumberFormatException e) {
                logger.warn("Invalid default value for integer: {}", type.defaultValue());
            }
        }

        return schema;
    }
    
    /**
     * Converts RAML boolean type to OpenAPI boolean schema.
     */
    private Schema<?> convertBooleanType(BooleanTypeDeclaration type) {
        return new BooleanSchema();
    }
    
    /**
     * Converts RAML array type to OpenAPI array schema.
     */
    private Schema<?> convertArrayType(ArrayTypeDeclaration type, MapperContext context) 
            throws ConverterException {
        
        ArraySchema schema = new ArraySchema();
        
        // Items type
        if (type.items() != null) {
            Schema<?> itemsSchema = convertType(type.items(), context);
            schema.setItems(itemsSchema);
            logger.debug("  Array items: {}", type.items().type());
        }
        
        applyArrayFacets(type, schema);
        
        return schema;
    }

    /**
     * RAML objects are open unless they set {@code additionalProperties: false}.
     */
    static boolean allowsAdditionalProperties(ObjectTypeDeclaration type) {
        return !Boolean.FALSE.equals(type.additionalProperties());
    }

    /**
     * A closed parent's {@code $ref} would reject the properties a subtype adds, so such a subtype is converted as a
     * flat object. The RAML parser already lists the inherited properties in {@code properties()}.
     */
    static boolean addsPropertiesToClosedType(ObjectTypeDeclaration type) {
        return type.parentTypes().stream().anyMatch(parent -> parent instanceof ObjectTypeDeclaration parentObject
                && !allowsAdditionalProperties(parentObject)
                && !propertyNames(parentObject).containsAll(propertyNames(type)));
    }

    private static Set<String> propertyNames(ObjectTypeDeclaration type) {
        return type.properties().stream().map(TypeDeclaration::name).collect(Collectors.toSet());
    }

    private static boolean hasArrayFacets(TypeDeclaration type) {
        if (!(type instanceof ArrayTypeDeclaration)) {
            return false;
        }
        ArrayTypeDeclaration array = (ArrayTypeDeclaration) type;
        return array.minItems() != null || array.maxItems() != null || array.uniqueItems() != null;
    }

    private static void applyArrayFacets(ArrayTypeDeclaration type, ArraySchema schema) {
        if (type.minItems() != null) {
            schema.setMinItems(type.minItems());
        }
        if (type.maxItems() != null) {
            schema.setMaxItems(type.maxItems());
        }
        if (type.uniqueItems() != null) {
            schema.setUniqueItems(type.uniqueItems());
        }
    }

    /**
     * Converts RAML object type to OpenAPI object schema.
     */
    private Schema<?> convertObjectType(ObjectTypeDeclaration type, MapperContext context)
            throws ConverterException {

        String baseTypeName = type.type();
        logger.debug("Converting object type: {} (base: {})", type.name(), baseTypeName);

        // Check for inheritance
        boolean isInheritance = baseTypeName != null &&
                !baseTypeName.equals("object") &&
                !"any".equals(baseTypeName) &&
                context.hasSchema(baseTypeName) &&
                !addsPropertiesToClosedType(type);

        if (isInheritance) {
            return createInheritedSchema(type, baseTypeName, context);
        } else {
            return createSimpleObjectSchema(type, context);
        }
    }

    /**
     * Creates a simple object schema (no inheritance).
     */
    private Schema<?> createSimpleObjectSchema(ObjectTypeDeclaration type, MapperContext context)
            throws ConverterException {

        ObjectSchema schema = new ObjectSchema();

        if (type.name() != null) {
            schema.setTitle(type.name());
        }

        List<TypeDeclaration> properties = type.properties();

        if (properties != null && !properties.isEmpty()) {
            Map<String, Schema> propSchemas = new LinkedHashMap<>();
            List<String> required = new ArrayList<>();

            for (TypeDeclaration property : properties) {
                String propName = property.name();
                Schema<?> propSchema = convertType(property, context);
                propSchemas.put(propName, propSchema);

                if (property.required() != null && property.required()) {
                    required.add(propName);
                }
            }

            schema.setProperties(propSchemas);

            if (!required.isEmpty()) {
                schema.setRequired(required);
            }
        }

        schema.setAdditionalProperties(allowsAdditionalProperties(type));

        return schema;
    }

    /**
     * Creates a schema with inheritance using allOf composition.
     */
    private Schema<?> createInheritedSchema(ObjectTypeDeclaration type, String parentTypeName,
                                            MapperContext context) throws ConverterException {

        logger.debug("  Creating inherited schema: {} extends {}", type.name(), parentTypeName);

        ComposedSchema composedSchema = new ComposedSchema();

        if (type.name() != null) {
            composedSchema.setTitle(type.name());
        }

        // 1. Add reference to parent
        Schema<?> parentRef = new Schema<>();
        parentRef.set$ref("#/components/schemas/" + parentTypeName);
        composedSchema.addAllOfItem(parentRef);
        logger.debug("    Added parent ref: {}", parentTypeName);

        // 2. Add child properties (CRITICAL: Get own properties only, not inherited)
        Schema<?> childPropertiesSchema = convertOwnPropertiesOnly(type, context);

        // Only add child schema if it has properties
        if (childPropertiesSchema instanceof ObjectSchema) {
            ObjectSchema objSchema = (ObjectSchema) childPropertiesSchema;
            if (objSchema.getProperties() != null && !objSchema.getProperties().isEmpty()) {
                composedSchema.addAllOfItem(childPropertiesSchema);
                logger.debug("    Added child properties: {}", objSchema.getProperties().keySet());
            } else {
                logger.debug("    No child properties to add");
            }
        }

        return composedSchema;
    }

    /**
     * Converts only the type's own properties (not inherited ones).
     * CRITICAL: This must NOT recursively call convertType for the type itself,
     * only for individual properties.
     */
    private Schema<?> convertOwnPropertiesOnly(ObjectTypeDeclaration type, MapperContext context)
            throws ConverterException {

        logger.debug("    Converting own properties for: {}", type.name());

        ObjectSchema schema = new ObjectSchema();
        schema.setType("object");

        // Get only the properties directly defined in this type
        List<TypeDeclaration> properties = type.properties();

        logger.debug("    Found {} own properties", properties != null ? properties.size() : 0);

        if (properties != null && !properties.isEmpty()) {
            Map<String, Schema> propSchemas = new LinkedHashMap<>();
            List<String> required = new ArrayList<>();

            for (TypeDeclaration property : properties) {
                String propName = property.name();
                logger.debug("      Property: {} : {}", propName, property.type());

                // IMPORTANT: Call convertType for the PROPERTY, not the parent type
                // This converts individual properties like "string", "integer", etc.
                Schema<?> propSchema = convertType(property, context);

                // Set property description
                if (property.description() != null && property.description().value() != null) {
                    propSchema.setDescription(property.description().value());
                }

                // Set property example
                if (property.example() != null && property.example().value() != null) {
                    propSchema.setExample(property.example().value());
                }

                propSchemas.put(propName, propSchema);

                // Track required properties
                if (property.required() != null && property.required()) {
                    required.add(propName);
                }
            }

            schema.setProperties(propSchemas);

            if (!required.isEmpty()) {
                schema.setRequired(required);
            }
        }

        schema.setAdditionalProperties(allowsAdditionalProperties(type));

        return schema;
    }
    
    /**
     * Converts RAML union type to OpenAPI oneOf schema.
     */
    private Schema<?> convertUnionType(UnionTypeDeclaration type, MapperContext context) 
            throws ConverterException {
        
        List<Schema> oneOf = new ArrayList<>();
        boolean nullable = false;
        
        for (TypeDeclaration option : type.of()) {
            if (option instanceof NullTypeDeclaration) {
                nullable = true;
            } else if (context.hasSchema(option.name())) {
                // A named option reports the base type ("object") as type(), so its own name decides; inlining it
                // would recurse forever on a self-referencing type such as "parent: Order | nil"
                oneOf.add(createReference(option.name()));
            } else {
                oneOf.add(convertType(option, context));
            }
        }
        
        // OpenAPI 3.0 expresses "T | nil" as T with nullable: true. A $ref ignores sibling keywords, so a lone
        // referenced option stays wrapped in oneOf to keep the nullable flag.
        Schema<?> schema;
        if (oneOf.size() == 1 && oneOf.get(0).get$ref() == null) {
            schema = oneOf.get(0);
        } else {
            schema = new Schema<>();
            if (!oneOf.isEmpty()) {
                schema.setOneOf(oneOf);
            }
        }
        if (nullable) {
            schema.setNullable(true);
        }
        logger.debug("  Union of {} types, nullable: {}", oneOf.size(), nullable);
        
        return schema;
    }
    
    /**
     * Converts RAML datetime type to OpenAPI string schema with format.
     */
    private Schema<?> convertDateTimeType(DateTimeTypeDeclaration type) {
        StringSchema schema = new StringSchema();
        
        String format = type.format();
        if (format != null) {
            // Map RAML datetime formats to OpenAPI formats
            switch (format) {
                case "rfc3339":
                    schema.setFormat("date-time");
                    break;
                case "rfc2616":
                    schema.setFormat("date-time");
                    schema.addExtension("x-raml-format", "rfc2616");
                    break;
                default:
                    schema.setFormat("date-time");
                    schema.addExtension("x-raml-format", format);
            }
        } else {
            schema.setFormat("date-time");
        }
        
        return schema;
    }
    
    /**
     * Converts RAML file type to OpenAPI string schema with binary format.
     */
    private Schema<?> convertFileType(FileTypeDeclaration type) {
        StringSchema schema = new StringSchema();
        schema.setFormat("binary");
        
        // Add file types as extension
        if (type.fileTypes() != null && !type.fileTypes().isEmpty()) {
            schema.addExtension("x-raml-fileTypes", type.fileTypes());
        }
        
        // Add min/max length for file size - FIX: Convert Number to Integer
        if (type.minLength() != null) {
            schema.setMinLength(type.minLength().intValue());
        }
        if (type.maxLength() != null) {
            schema.setMaxLength(type.maxLength().intValue());
        }
        
        return schema;
    }
    
    /**
     * Checks if a type name is a reference to another type.
     */
    private boolean isReference(String typeName) {
        if (typeName == null) {
            return false;
        }
        
        // Built-in types are not references
        String[] builtInTypes = {
            "string", "number", "integer", "boolean", "date-only", 
            "time-only", "datetime-only", "datetime", "file", 
            "array", "object", "nil", "any"
        };
        
        for (String builtIn : builtInTypes) {
            if (typeName.equals(builtIn)) {
                return false;
            }
        }
        
        // Check if it's an array type like "Type[]"
        if (typeName.endsWith("[]")) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Creates a reference schema to a named type.
     */
    private Schema<?> createReference(String typeName) {
        // Remove array notation if present
        String cleanTypeName = typeName.replace("[]", "");
        
        Schema<?> schema = new Schema<>();
        schema.set$ref("#/components/schemas/" + cleanTypeName);
        logger.debug("  Reference: {}", cleanTypeName);
        
        return schema;
    }
}
