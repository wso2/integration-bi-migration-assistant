package org.nipunaml.ramltoopenapi.mapper;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.nipunaml.ramltoopenapi.exception.ConverterException;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.methods.TraitRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Maps RAML methods to OpenAPI operations.
 */
public class MethodMapper {

    /**
     * Extension listing the names of the RAML traits applied to an operation, which OpenAPI has no field for.
     */
    public static final String TRAITS_EXTENSION = "x-raml-traits";

    private static final Logger logger = LoggerFactory.getLogger(MethodMapper.class);

    private final TypeConverter typeConverter;
    private final ParameterMapper parameterMapper;
    private final ResponseMapper responseMapper;
    
    public MethodMapper(TypeConverter typeConverter) {
        this.typeConverter = typeConverter;
        this.parameterMapper = new ParameterMapper(typeConverter);
        this.responseMapper = new ResponseMapper(typeConverter);
    }
    
    /**
     * Maps a RAML method to an OpenAPI operation.
     */
    public Operation mapMethod(Method ramlMethod, List<TypeDeclaration> resourceUriParams,
                            String resourcePath,
                            org.raml.v2.api.model.v10.resources.Resource resource,
                            org.raml.v2.api.model.v10.api.Api api,
                            MapperContext context) throws ConverterException {

        String methodName = ramlMethod.method();
        logger.debug("  Mapping method: {}", methodName.toUpperCase());
        
        Operation operation = new Operation();
        
        // Set operation ID - make it unique by including path
        operation.setOperationId(generateOperationId(ramlMethod, resourcePath));
        
        // Set summary and description
        if (ramlMethod.description() != null && ramlMethod.description().value() != null) {
            operation.setSummary(ramlMethod.description().value());
            operation.setDescription(ramlMethod.description().value());
        } else if (ramlMethod.displayName() != null && ramlMethod.displayName().value() != null) {
            operation.setSummary(ramlMethod.displayName().value());
        }
        
        // Map parameters
        List<Parameter> parameters = new ArrayList<>();
        
        // URI parameters (from resource path)
        if (resourceUriParams != null && !resourceUriParams.isEmpty()) {
            parameters.addAll(parameterMapper.mapUriParameters(resourceUriParams, context));
        }
        
        // Query parameters
        if (ramlMethod.queryParameters() != null && !ramlMethod.queryParameters().isEmpty()) {
            parameters.addAll(parameterMapper.mapQueryParameters(
                ramlMethod.queryParameters(), context));
        }
        
        // Headers
        if (ramlMethod.headers() != null && !ramlMethod.headers().isEmpty()) {
            parameters.addAll(parameterMapper.mapHeaders(ramlMethod.headers(), context));
        }
        
        if (!parameters.isEmpty()) {
            operation.setParameters(parameters);
            logger.debug("    Total parameters: {}", parameters.size());
        }
        
        // Map request body
        if (ramlMethod.body() != null && !ramlMethod.body().isEmpty()) {
            RequestBody requestBody = mapRequestBody(ramlMethod.body(), context);
            operation.setRequestBody(requestBody);
            logger.debug("    Request body: {} media type(s)", ramlMethod.body().size());
        }
        
        // Map responses
        ApiResponses responses = responseMapper.mapResponses(ramlMethod.responses(), context);
        operation.setResponses(responses);

        // Map security requirements with inheritance
        List<io.swagger.v3.oas.models.security.SecurityRequirement> securityRequirements =
            resolveSecurityRequirements(ramlMethod, resource, api);

        if (securityRequirements != null) {
            operation.setSecurity(securityRequirements);
            logger.debug("    Security: {} requirement(s)", securityRequirements.size());
        }

        // The parser merges trait contents such as headers into the method but keeps only the names of the traits,
        // which tell, for example, that a header serves an API Manager policy
        List<String> traits = Stream.concat(traitRefs(resource != null ? resource.is() : null),
                traitRefs(ramlMethod.is()))
            .map(TraitRef::name)
            .distinct()
            .toList();
        if (!traits.isEmpty()) {
            operation.addExtension(TRAITS_EXTENSION, traits);
            logger.debug("    Traits: {}", traits);
        }

        return operation;
    }

    private static Stream<TraitRef> traitRefs(List<TraitRef> refs) {
        return refs != null ? refs.stream() : Stream.empty();
    }

    /**
     * Resolves security requirements with proper inheritance from resource and API levels.
     */
    private List<io.swagger.v3.oas.models.security.SecurityRequirement> resolveSecurityRequirements(
            Method method,
            org.raml.v2.api.model.v10.resources.Resource resource,
            org.raml.v2.api.model.v10.api.Api api) {

        // Priority: method > resource > API
        // Note: The RAML parser returns empty lists for implicit inheritance,
        // so we treat null OR empty as "not declared" and inherit from parent.
        // Only [null] explicitly means "no security"

        // 1. Check method level security
        if (method.securedBy() != null && !method.securedBy().isEmpty()) {
            logger.debug("    Using method-level security");
            return mapMethodSecurity(method.securedBy());
        }

        // 2. Check resource level security  
        if (resource != null && resource.securedBy() != null && !resource.securedBy().isEmpty()) {
            logger.debug("    Using resource-level security");
            return mapMethodSecurity(resource.securedBy());
        }

        // 3. Check API level security
        if (api != null && api.securedBy() != null && !api.securedBy().isEmpty()) {
            logger.debug("    Using API-level security");
            return mapMethodSecurity(api.securedBy());
        }

        // No security specified at any level
        return null;
    }

    /**
     * Maps method-level security requirements.
     */
    private List<io.swagger.v3.oas.models.security.SecurityRequirement> mapMethodSecurity(
            List<org.raml.v2.api.model.v10.security.SecuritySchemeRef> securedBy) {

        List<io.swagger.v3.oas.models.security.SecurityRequirement> requirements =
            new ArrayList<>();

        for (org.raml.v2.api.model.v10.security.SecuritySchemeRef schemeRef : securedBy) {
            // ADD NULL CHECK HERE
            if (schemeRef == null) {
                // securedBy: [null] means no security - return empty list
                logger.debug("      No security required (null element)");
                return new ArrayList<>();
            }

            // Check if it's null security by name
            if (schemeRef.name() == null || schemeRef.name().equals("null")) {
                // securedBy: [null] means no security - return empty list
                logger.debug("      No security required (null)");
                return new ArrayList<>();
            }

            String schemeName = schemeRef.name();
            
            // Extract scopes if present
            List<String> scopes = extractScopes(schemeRef);

            // Create security requirement
            io.swagger.v3.oas.models.security.SecurityRequirement requirement =
                new io.swagger.v3.oas.models.security.SecurityRequirement();
            requirement.addList(schemeName, scopes);
            
            requirements.add(requirement);

            logger.debug("      Security scheme: {} with {} scope(s)", schemeName, scopes.size());
        }

        return requirements;
    }

    /**
     * Extracts scopes from a security scheme reference.
     */
    private List<String> extractScopes(org.raml.v2.api.model.v10.security.SecuritySchemeRef schemeRef) {
        List<String> scopes = new ArrayList<>();
        
        if (schemeRef.structuredValue() == null) {
            return scopes;
        }
        
        try {
            // Get properties list
            java.lang.reflect.Method propertiesMethod = 
                schemeRef.structuredValue().getClass().getMethod("properties");
            Object propsObj = propertiesMethod.invoke(schemeRef.structuredValue());
            
            if (!(propsObj instanceof java.util.List)) {
                return scopes;
            }
            
            java.util.List<?> propsList = (java.util.List<?>) propsObj;
            
            // Find and process "scopes" property
            for (Object prop : propsList) {
                java.lang.reflect.Method nameMethod = prop.getClass().getMethod("name");
                Object nameObj = nameMethod.invoke(prop);
                String propName = nameObj != null ? nameObj.toString() : null;
                
                if (!"scopes".equals(propName)) {
                    continue;
                }
                
                // Get scopes using values() method
                java.lang.reflect.Method valuesMethod = prop.getClass().getMethod("values");
                Object valuesObj = valuesMethod.invoke(prop);
                
                if (!(valuesObj instanceof java.util.List)) {
                    continue;
                }
                
                java.util.List<?> valuesList = (java.util.List<?>) valuesObj;
                
                // Extract each scope value
                for (Object item : valuesList) {
                    if (item != null) {
                        scopes.add(extractScopeValue(item));
                    }
                }
            }
            
        } catch (NoSuchMethodException e) {
            logger.debug("Method not found while extracting scopes: {}", e.getMessage());
        } catch (IllegalAccessException e) {
            logger.debug("Access denied while extracting scopes: {}", e.getMessage());
        } catch (java.lang.reflect.InvocationTargetException e) {
            logger.debug("Invocation error while extracting scopes: {}", e.getMessage());
        } catch (Exception e) {
            logger.debug("Unexpected error while extracting scopes: {}", e.getMessage());
        }
        
        return scopes;
    }

    /**
     * Extracts the string value from a scope item.
     */
    private String extractScopeValue(Object item) {
        try {
            java.lang.reflect.Method valueMethod = item.getClass().getMethod("value");
            Object value = valueMethod.invoke(item);
            return value != null ? value.toString() : item.toString();
        } catch (Exception e) {
            return item.toString();
        }
    }
    
    /**
     * Maps RAML request body to OpenAPI RequestBody with enhanced example handling.
     */
    private RequestBody mapRequestBody(List<TypeDeclaration> ramlBodies, 
                                    MapperContext context) throws ConverterException {
        
        RequestBody requestBody = new RequestBody();
        Content content = new Content();
        
        boolean hasRequiredBody = false;
        String description = null;
        
        for (TypeDeclaration body : ramlBodies) {
            String mediaTypeName = body.name();
            MediaType mediaType = new MediaType();
            
            // Convert body type to schema
            Schema<?> schema = typeConverter.convertType(body, context);
            mediaType.setSchema(schema);
            
            // Map examples (single or multiple)
            if (body.examples() != null && !body.examples().isEmpty()) {
                // Multiple examples
                Map<String, io.swagger.v3.oas.models.examples.Example> examples = 
                    mapBodyExamples(body.examples());
                mediaType.setExamples(examples);
                logger.debug("      Examples: {}", examples.size());
            } else if (body.example() != null && body.example().value() != null) {
                // Single example
                io.swagger.v3.oas.models.examples.Example example = 
                    new io.swagger.v3.oas.models.examples.Example();
                example.setValue(parseExampleValue(body.example().value()));
                mediaType.setExample(example.getValue());
                logger.debug("      Example: provided");
            }
            
            content.addMediaType(mediaTypeName, mediaType);
            
            // Check if body is required
            if (body.required()) {
                hasRequiredBody = true;
            }
            
            // Get description from first body
            if (description == null && body.description() != null && 
                body.description().value() != null) {
                description = body.description().value();
            }
        }
        
        requestBody.setContent(content);
        requestBody.setRequired(hasRequiredBody);
        
        if (description != null) {
            requestBody.setDescription(description);
        }
        
        return requestBody;
    }

    /**
     * Maps RAML examples to OpenAPI examples for request body.
     */
    private Map<String, io.swagger.v3.oas.models.examples.Example> mapBodyExamples(
            List<org.raml.v2.api.model.v10.datamodel.ExampleSpec> ramlExamples) {
        
        Map<String, io.swagger.v3.oas.models.examples.Example> examples = new LinkedHashMap<>();
        
        for (org.raml.v2.api.model.v10.datamodel.ExampleSpec ramlExample : ramlExamples) {
            String exampleName = ramlExample.name();
            io.swagger.v3.oas.models.examples.Example example = 
                new io.swagger.v3.oas.models.examples.Example();
            
            // Set value
            if (ramlExample.value() != null) {
                example.setValue(parseExampleValue(ramlExample.value()));
            }
            
            // ExampleSpec doesn't have description() or displayName() methods
            // Use the example name as summary
            if (exampleName != null && !exampleName.isEmpty()) {
                example.setSummary(exampleName);
            }
            
            examples.put(exampleName, example);
        }
        
        return examples;
    }

    /**
     * Parses example value with type detection.
     */
    private Object parseExampleValue(String exampleValue) {
        if (exampleValue == null) {
            return null;
        }
        
        exampleValue = exampleValue.trim();
        
        // JSON detection
        if (exampleValue.startsWith("{") || exampleValue.startsWith("[")) {
            return exampleValue;
        }
        
        // Number detection
        try {
            if (exampleValue.contains(".")) {
                return Double.parseDouble(exampleValue);
            } else {
                return Integer.parseInt(exampleValue);
            }
        } catch (NumberFormatException e) {
            // Not a number
        }
        
        // Boolean detection
        if (exampleValue.equalsIgnoreCase("true")) {
            return true;
        } else if (exampleValue.equalsIgnoreCase("false")) {
            return false;
        }
        
        return exampleValue;
    }

    /**
     * Generates a unique operation ID from the method and resource path.
     * Priority: 1) method + path, 2) description-based, 3) method only
     */
    private String generateOperationId(Method method, String resourcePath) {
        String methodName = method.method().toLowerCase();
        
        // Priority 1: Use method + resourcePath if path is available
        if (resourcePath != null && !resourcePath.isEmpty() && !resourcePath.equals("/")) {
            String pathPart = resourcePath
                .replaceAll("^/", "")           // Remove leading /
                .replaceAll("/", "_")            // Replace / with _
                .replaceAll("\\{", "")           // Remove {
                .replaceAll("\\}", "")           // Remove }
                .replaceAll("[^a-zA-Z0-9_]", "_") // Replace other special chars
                .replaceAll("_+", "_")           // Replace multiple underscores with single
                .replaceAll("_$", "");           // Remove trailing underscore
            
            if (!pathPart.isEmpty()) {
                String operationId = methodName + "_" + pathPart;
                logger.debug("  Generated operation ID from path: {}", operationId);
                return operationId;
            }
        }
        
        // Priority 2: Generate from description if available
        if (method.description() != null && method.description().value() != null) {
            String description = method.description().value();
            String operationId = generateFromDescription(description, methodName);
            if (operationId != null && !operationId.isEmpty()) {
                logger.debug("  Generated operation ID from description: {}", operationId);
                return operationId;
            }
        }
        
        // Priority 3: Fall back to just method name (not ideal but prevents errors)
        logger.debug("  Using method name as operation ID: {}", methodName);
        return methodName;
    }

    /**
     * Generates operation ID from description.
     * Examples:
     *   "Create product (admin only)" -> "createProduct"
     *   "Get product details" -> "getProductDetails"
     *   "List products (public)" -> "listProducts"
     */
    private String generateFromDescription(String description, String methodName) {
        if (description == null || description.trim().isEmpty()) {
            return null;
        }
        
        // Remove content in parentheses and extra whitespace
        String cleaned = description
            .replaceAll("\\([^)]*\\)", "")  // Remove (text in parentheses)
            .trim();
        
        // Split into words
        String[] words = cleaned.split("\\s+");
        
        if (words.length == 0) {
            return null;
        }
        
        // Build camelCase operation ID
        StringBuilder operationId = new StringBuilder();
        
        for (int i = 0; i < words.length; i++) {
            String word = words[i].toLowerCase();
            
            // Skip common words that don't add meaning
            if (isCommonWord(word)) {
                continue;
            }
            
            // First meaningful word stays lowercase, rest are capitalized
            if (operationId.length() == 0) {
                operationId.append(word);
            } else {
                operationId.append(Character.toUpperCase(word.charAt(0)));
                if (word.length() > 1) {
                    operationId.append(word.substring(1));
                }
            }
        }
        
        String result = operationId.toString();
        
        // If result is empty or too generic, prefix with method name
        if (result.isEmpty() || result.length() < 3) {
            return methodName;
        }
        
        return result;
    }

    /**
     * Checks if a word is a common filler word that should be skipped.
     */
    private boolean isCommonWord(String word) {
        String[] commonWords = {
            "a", "an", "the", "is", "are", "was", "were", "be", "been", "being",
            "have", "has", "had", "do", "does", "did", "will", "would", "should",
            "could", "may", "might", "must", "can", "of", "for", "to", "in", "on",
            "at", "by", "with", "from", "as", "into", "through", "during", "before",
            "after", "above", "below", "between", "under", "public", "private"
        };
        
        for (String common : commonWords) {
            if (word.equals(common)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Converts a string to camelCase.
     */
    private String toCamelCase(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        
        // Replace spaces and special characters with underscore
        text = text.replaceAll("[^a-zA-Z0-9]", "_");
        
        // Split by underscore and capitalize
        String[] parts = text.split("_");
        StringBuilder camelCase = new StringBuilder(parts[0].toLowerCase());
        
        for (int i = 1; i < parts.length; i++) {
            if (!parts[i].isEmpty()) {
                camelCase.append(Character.toUpperCase(parts[i].charAt(0)));
                if (parts[i].length() > 1) {
                    camelCase.append(parts[i].substring(1).toLowerCase());
                }
            }
        }
        
        return camelCase.toString();
    }
}
