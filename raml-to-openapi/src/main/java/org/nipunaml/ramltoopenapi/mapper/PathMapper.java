package org.nipunaml.ramltoopenapi.mapper;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import org.nipunaml.ramltoopenapi.exception.ConverterException;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Maps RAML resources to OpenAPI paths.
 * Handles resource hierarchy and nested resources.
 */
public class PathMapper implements ComponentMapper<Api, Paths> {
    
    private static final Logger logger = LoggerFactory.getLogger(PathMapper.class);
    
    private final TypeConverter typeConverter;
    private final MethodMapper methodMapper;
    private Api api; // Store API reference for global security

    public PathMapper() {
        this.typeConverter = new TypeConverter();
        this.methodMapper = new MethodMapper(typeConverter);
    }

    @Override
    public Paths map(Api source, MapperContext context) throws ConverterException {
        if (source == null) {
            throw new ConverterException("Cannot map null API to paths");
        }

        this.api = source; // Store API reference

        logger.debug("Mapping RAML resources to OpenAPI paths");

        Paths paths = new Paths();

        // Map all resources
        if (source.resources() != null && !source.resources().isEmpty()) {
            logger.debug("Found {} top-level resource(s)", source.resources().size());

            for (Resource resource : source.resources()) {
                mapResource(resource, paths, new ArrayList<>(), context);
            }
        } else {
            logger.debug("No resources defined in RAML");
        }

        logger.debug("✓ Path mapping completed: {} path(s)", paths.size());
        return paths;
    }

    /**
     * Maps a RAML resource and its nested resources to OpenAPI paths.
     */
    private void mapResource(Resource resource, Paths paths,
                            List<TypeDeclaration> parentUriParams,
                            MapperContext context) throws ConverterException {

        String resourcePath = resource.resourcePath();
        logger.debug("Mapping resource: {}", resourcePath);

        // Collect URI parameters (parent + current)
        List<TypeDeclaration> allUriParams = new ArrayList<>(parentUriParams);
        if (resource.uriParameters() != null) {
            allUriParams.addAll(resource.uriParameters());
        }

        // Map methods for this resource
        if (resource.methods() != null && !resource.methods().isEmpty()) {
            PathItem pathItem = paths.get(resourcePath);
            if (pathItem == null) {
                pathItem = new PathItem();
                paths.addPathItem(resourcePath, pathItem);
            }

            // Set description
            if (resource.description() != null && resource.description().value() != null) {
                pathItem.setDescription(resource.description().value());
            }
            Optional<String> displayName =
                    MethodMapper.explicitDisplayName(resource.displayName(), resource.relativeUri().value());
            if (displayName.isPresent()) {
                pathItem.addExtension(MethodMapper.DISPLAY_NAME_EXTENSION, displayName.get());
            }

            // Map each HTTP method - PASS resourcePath
            for (Method method : resource.methods()) {
                try {
                    // In the mapResource method, when calling mapMethod:
                    Operation operation = methodMapper.mapMethod(
                        method, allUriParams, resourcePath, resource, api, context);  // ADD resource and api
                    setOperationOnPath(pathItem, method.method(), operation);
                } catch (Exception e) {
                    String message = "Failed to map method " + method.method().toUpperCase() +
                                " for resource '" + resourcePath + "': " + e.getMessage();
                    if (context.isStrict()) {
                        throw new ConverterException(message, e);
                    } else {
                        logger.warn("⚠ {}", message);
                    }
                }
            }
        }

        // Recursively map nested resources
        if (resource.resources() != null && !resource.resources().isEmpty()) {
            logger.debug("  Found {} nested resource(s)", resource.resources().size());
            for (Resource nestedResource : resource.resources()) {
                mapResource(nestedResource, paths, allUriParams, context);
            }
        }
    }
    
    /**
     * Sets an operation on a path item based on HTTP method.
     */
    private void setOperationOnPath(PathItem pathItem, String method, Operation operation) {
        switch (method.toLowerCase()) {
            case "get":
                pathItem.setGet(operation);
                break;
            case "post":
                pathItem.setPost(operation);
                break;
            case "put":
                pathItem.setPut(operation);
                break;
            case "delete":
                pathItem.setDelete(operation);
                break;
            case "patch":
                pathItem.setPatch(operation);
                break;
            case "head":
                pathItem.setHead(operation);
                break;
            case "options":
                pathItem.setOptions(operation);
                break;
            case "trace":
                pathItem.setTrace(operation);
                break;
            default:
                logger.warn("Unsupported HTTP method: {}", method);
        }
    }
}
