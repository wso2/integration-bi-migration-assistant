package org.nipunaml.ramltoopenapi.mapper;

import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Schema;
import org.nipunaml.ramltoopenapi.exception.ConverterException;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Maps RAML types to OpenAPI schemas (components.schemas).
 * Handles type definitions, inheritance, and schema composition.
 */
public class SchemaMapper implements ComponentMapper<Api, Map<String, Schema>> {
    
    private static final Logger logger = LoggerFactory.getLogger(SchemaMapper.class);

    /**
     * Extension set on a schema that stands in for a RAML type that could not be converted.
     */
    public static final String CONVERSION_ERROR_EXTENSION = "x-raml-conversion-error";
    
    private final TypeConverter typeConverter;
    private final PropertyMapper propertyMapper;
    
    public SchemaMapper() {
        this.typeConverter = new TypeConverter();
        this.propertyMapper = new PropertyMapper(typeConverter);
    }
    
    @Override
    public Map<String, Schema> map(Api source, MapperContext context) throws ConverterException {
        if (source == null) {
            throw new ConverterException("Cannot map null API to schemas");
        }
        
        logger.debug("Mapping RAML types to OpenAPI schemas");
        
        Map<String, Schema> schemas = new LinkedHashMap<>();
        Set<String> arrayTypesToGenerate = new HashSet<>();  // ADD THIS
        
        // Map all type definitions
        if (source.types() != null && !source.types().isEmpty()) {
            logger.debug("Found {} type definition(s)", source.types().size());
            
            // First pass: register all schema names
            for (TypeDeclaration type : source.types()) {
                context.addSchemaName(type.name());
            }
            
            // Second pass: map schemas and detect array references
            for (TypeDeclaration type : source.types()) {
                String typeName = type.name();
                logger.debug("Mapping type: {}", typeName);
                
                try {
                    Schema<?> schema = mapTypeDefinition(type, context);
                    schemas.put(typeName, schema);
                    logger.debug("✓ Mapped type: {}", typeName);
                } catch (Exception e) {
                    String message = "Failed to map type '" + typeName + "': " + e.getMessage();
                    if (context.isStrict()) {
                        throw new ConverterException(message, e);
                    } else {
                        // Keep the name so references to it still resolve; the extension records why it is empty
                        logger.warn("⚠ {}", message);
                        Schema<?> placeholder = new Schema<>();
                        placeholder.addExtension(CONVERSION_ERROR_EXTENSION, message);
                        schemas.put(typeName, placeholder);
                    }
                }
            }
        }
        
        // Third pass: scan for array type usages in resources
        if (source.resources() != null) {
            scanResourcesForArrayTypes(source.resources(), arrayTypesToGenerate, context);
        }
        
        // Fourth pass: generate array schemas
        for (String baseTypeName : arrayTypesToGenerate) {
            if (context.hasSchema(baseTypeName)) {
                String arraySchemaName = baseTypeName + "Array";
                ArraySchema arraySchema = new ArraySchema();
                
                Schema<?> itemRef = new Schema<>();
                itemRef.set$ref("#/components/schemas/" + baseTypeName);
                arraySchema.setItems(itemRef);
                
                schemas.put(arraySchemaName, arraySchema);
                context.addSchemaName(arraySchemaName);
                
                logger.debug("✓ Generated array schema: {}", arraySchemaName);
            }
        }
        
        logger.debug("✓ Schema mapping completed: {} schema(s)", schemas.size());
        return schemas;
    }

    /**
     * Recursively scans resources to find array type usages like "Product[]".
     */
    private void scanResourcesForArrayTypes(List<Resource> resources, 
                                            Set<String> arrayTypes,
                                            MapperContext context) {
        for (Resource resource : resources) {
            // Check methods
            if (resource.methods() != null) {
                for (Method method : resource.methods()) {
                    // Check request body
                    if (method.body() != null) {
                        for (TypeDeclaration body : method.body()) {
                            checkForArrayType(body.type(), arrayTypes);
                        }
                    }
                    
                    // Check responses
                    if (method.responses() != null) {
                        for (Response response : method.responses()) {
                            if (response.body() != null) {
                                for (TypeDeclaration body : response.body()) {
                                    checkForArrayType(body.type(), arrayTypes);
                                }
                            }
                        }
                    }
                }
            }
            
            // Recursively check nested resources
            if (resource.resources() != null) {
                scanResourcesForArrayTypes(resource.resources(), arrayTypes, context);
            }
        }
    }

    /**
     * Checks if a type string represents an array type and extracts the base type.
     */
    private void checkForArrayType(String typeString, Set<String> arrayTypes) {
        if (typeString != null && typeString.endsWith("[]")) {
            String baseType = typeString.substring(0, typeString.length() - 2);
            arrayTypes.add(baseType);
        }
    }

    /**
     * Maps a RAML type definition to an OpenAPI schema.
     */
    private Schema<?> mapTypeDefinition(TypeDeclaration type, MapperContext context)
            throws ConverterException {

        // Check if this type has inheritance BEFORE converting
        if (hasParentType(type, context)) {
            // Handle inheritance - convert only own properties, not parent
            return handleInheritance(type, context);
        }

        // Convert the type using TypeConverter (for non-inherited types)
        Schema<?> schema = typeConverter.convertType(type, context);

        // Set title (displayName or name)
        if (type.displayName() != null) {
            String displayName = type.displayName().value();
            schema.setTitle(displayName);
        } else {
            schema.setTitle(type.name());
        }

        // Set description
        if (type.description() != null && type.description().value() != null) {
            schema.setDescription(type.description().value());
        }

        // Set example
        if (type.example() != null && type.example().value() != null) {
            schema.setExample(parseExample(type.example().value()));
        }

        // Add RAML-specific extensions
        addTypeExtensions(type, schema);

        return schema;
    }
    
    /**
     * Checks if a type has a parent type (inheritance).
     */
    private boolean hasParentType(TypeDeclaration type, MapperContext context) {
        String typeName = type.type();
        
        // If type is a built-in type, no inheritance
        if (typeName == null || isBuiltInType(typeName)) {
            return false;
        }
        
        // Only an object can extend a named schema. Other declarations name a type expression such as
        // "string | nil" or "Order[]", or narrow a named scalar; those are converted as the type they resolve to.
        return type instanceof ObjectTypeDeclaration && context.hasSchema(typeName) && !typeName.equals(type.name());
    }
    
    /**
     * Handles type inheritance using allOf composition.
     */
    private Schema<?> handleInheritance(TypeDeclaration type, MapperContext context)
            throws ConverterException {

        String parentType = type.type();
        logger.debug("  Inherits from: {}", parentType);
        
        // Create allOf composition
        Schema<?> composedSchema = new Schema<>();
        java.util.List<Schema> allOf = new java.util.ArrayList<>();
        
        // Add reference to parent type
        Schema<?> parentRef = new Schema<>();
        parentRef.set$ref("#/components/schemas/" + parentType);
        allOf.add(parentRef);
        
        // Create schema with only the child's own properties
        Schema<?> childSchema = convertOwnPropertiesOnly(type, context);

        // Set title and description on the composed schema
        if (type.displayName() != null) {
            composedSchema.setTitle(type.displayName().value());
        } else {
            composedSchema.setTitle(type.name());
        }

        if (type.description() != null && type.description().value() != null) {
            composedSchema.setDescription(type.description().value());
        }

        // Add the child schema to allOf
        allOf.add(childSchema);

        composedSchema.setAllOf(allOf);
        
        return composedSchema;
    }
    
    /**
     * Converts only the type's own properties (not inherited ones).
     * This is used when creating the child part of an inheritance schema.
     */
    private Schema<?> convertOwnPropertiesOnly(TypeDeclaration type, MapperContext context)
            throws ConverterException {

        // Create an object schema with only this type's properties
        io.swagger.v3.oas.models.media.ObjectSchema schema =
            new io.swagger.v3.oas.models.media.ObjectSchema();

        // Cast to ObjectTypeDeclaration to access properties
        if (!(type instanceof org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration)) {
            // If not an object type, return empty object
            schema.setAdditionalProperties(true);
            return schema;
        }

        org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration objectType =
            (org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration) type;

        // Process only the properties defined in this type (not inherited)
        if (objectType.properties() != null && !objectType.properties().isEmpty()) {
            java.util.Map<String, Schema> properties = new java.util.LinkedHashMap<>();
            java.util.List<String> required = new java.util.ArrayList<>();

            for (TypeDeclaration property : objectType.properties()) {
                String propertyName = property.name();
                Schema<?> propertySchema = typeConverter.convertType(property, context);

                // Set property description
                if (property.description() != null && property.description().value() != null) {
                    propertySchema.setDescription(property.description().value());
                }

                // Set property example
                if (property.example() != null && property.example().value() != null) {
                    propertySchema.setExample(property.example().value());
                }

                properties.put(propertyName, propertySchema);

                // Track required properties
                if (property.required()) {
                    required.add(propertyName);
                }
            }

            schema.setProperties(properties);

            if (!required.isEmpty()) {
                schema.setRequired(required);
            }
        }

        schema.setAdditionalProperties(TypeConverter.allowsAdditionalProperties(objectType));

        return schema;
    }

    /**
     * Checks if a type name is a built-in RAML type.
     */
    private boolean isBuiltInType(String typeName) {
        String[] builtInTypes = {
            "string", "number", "integer", "boolean", "date-only", 
            "time-only", "datetime-only", "datetime", "file", 
            "array", "object", "nil", "any"
        };
        
        for (String builtIn : builtInTypes) {
            if (typeName.equals(builtIn)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Parses example value (handles JSON strings).
     */
    private Object parseExample(String exampleValue) {
        if (exampleValue == null) {
            return null;
        }
        
        // Try to parse as JSON if it looks like JSON
        exampleValue = exampleValue.trim();
        if (exampleValue.startsWith("{") || exampleValue.startsWith("[")) {
            try {
                // For now, return as string; could parse to object/array
                return exampleValue;
            } catch (Exception e) {
                logger.debug("Failed to parse example as JSON: {}", e.getMessage());
            }
        }
        
        return exampleValue;
    }
    
    /**
     * Adds RAML-specific extensions to the schema.
     */
    private void addTypeExtensions(TypeDeclaration type, Schema<?> schema) {
        // Add facets as extensions - FIX: facets don't have a value() method
        // We'll skip facets for now as they're custom RAML extensions
        // that don't have a standard way to access their values
        
        // Add annotations as extensions
        if (type.annotations() != null && !type.annotations().isEmpty()) {
            type.annotations().forEach(annotation -> {
                String name = annotation.annotation().name();
                Object value = annotation.structuredValue() != null ? 
                    annotation.structuredValue().value() : null;
                if (value != null) {
                    schema.addExtension("x-raml-annotation-" + name, value);
                }
            });
        }
    }
}
