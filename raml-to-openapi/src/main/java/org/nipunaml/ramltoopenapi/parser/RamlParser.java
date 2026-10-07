package org.nipunaml.ramltoopenapi.parser;

import org.nipunaml.ramltoopenapi.exception.ConverterException;
import org.nipunaml.ramltoopenapi.model.raml.RamlDocument;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.loader.CompositeResourceLoader;
import org.raml.v2.api.loader.DefaultResourceLoader;
import org.raml.v2.api.loader.FileResourceLoader;
import org.raml.v2.api.model.common.ValidationResult;
import org.raml.v2.api.model.v10.api.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses RAML files using the RAML Java Parser.
 */
public class RamlParser {
    
    private static final Logger logger = LoggerFactory.getLogger(RamlParser.class);
    
    /**
     * Parses a RAML file and returns a RamlDocument.
     */
    public RamlDocument parse(File ramlFile) throws ConverterException {
        logger.debug("Parsing RAML file: {}", ramlFile.getName());
        
        // Check if file exists first
        if (ramlFile == null || !ramlFile.exists()) {
            throw new ConverterException("RAML file not found: " +
                (ramlFile != null ? ramlFile.getPath() : "null"));
        }

        try {
            // Build the RAML model - this resolves all !include and uses
            File apiRoot = ramlFile.getAbsoluteFile().getParentFile();
            RamlModelBuilder builder = new RamlModelBuilder(new CompositeResourceLoader(
                new FileResourceLoader(apiRoot),
                new DefaultResourceLoader(),
                new ApiRootResourceLoader(apiRoot)));
            RamlModelResult result = builder.buildApi(ramlFile);
            
            // Check for validation errors (including missing includes)
            if (result.hasErrors()) {
                List<ValidationResult> errors = result.getValidationResults();
                
                // Check for file not found errors
                for (ValidationResult error : errors) {
                    String errorMsg = error.getMessage();
                    
                    // Detect include/reference errors
                    if (errorMsg.contains("include") || 
                        errorMsg.contains("cannot be resolved") ||
                        errorMsg.contains("not found") ||
                        errorMsg.contains("No such file")) {
                        
                        throw new ConverterException(
                            "Failed to resolve included file: " + errorMsg + 
                            "\nMake sure all referenced files exist relative to: " + 
                            ramlFile.getParent()
                        );
                    }
                }
                
                // Other validation errors
                throw new ConverterException(
                    "RAML validation failed for '" + ramlFile.getName() + "': " + 
                    formatValidationErrors(errors)
                );
            }
            
            // Get the parsed API
            Api api = result.getApiV10();
            if (api == null) {
                throw new ConverterException(
                    "Failed to parse RAML file '" + ramlFile.getName() + 
                    "': No API definition found"
                );
            }
            
            // Create RamlDocument
            RamlDocument document = new RamlDocument(
                api,
                ramlFile.getName()
            );
            
            logger.debug("✓ Successfully parsed: {} - {}", 
                document.getTitle(), document.getVersion());
            
            return document;
            
        } catch (Exception e) {
            if (e instanceof ConverterException) {
                throw (ConverterException) e;
            }
            throw new ConverterException(
                "Failed to parse RAML file '" + ramlFile.getName() + "': " + e.getMessage(),
                e
            );
        }
    }
    
    /**
     * Parses multiple RAML files.
     */
    public List<RamlDocument> parseMultiple(List<File> ramlFiles) throws ConverterException {
        if (ramlFiles == null || ramlFiles.isEmpty()) {
            throw new ConverterException("No RAML files provided for parsing");
        }
        
        logger.info("Parsing {} RAML file(s)...", ramlFiles.size());
        
        List<RamlDocument> documents = new ArrayList<>();
        
        for (File ramlFile : ramlFiles) {
            try {
                RamlDocument document = parse(ramlFile);
                documents.add(document);
                logger.debug("✓ Parsed: {}", ramlFile.getName());
            } catch (ConverterException e) {
                logger.error("✗ Failed to parse: {}", ramlFile.getName());
                throw new ConverterException(
                    "Failed to parse '" + ramlFile.getName() + "': " + e.getMessage(),
                    e
                );
            }
        }
        
        logger.info("✓ Successfully parsed all {} file(s)", documents.size());
        
        return documents;
    }
    
    /**
     * Formats validation errors into a readable string.
     */
    private String formatValidationErrors(List<ValidationResult> errors) {
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < errors.size() && i < 5; i++) {
            ValidationResult error = errors.get(i);
            sb.append("\n  - ").append(error.getMessage());
        }
        
        if (errors.size() > 5) {
            sb.append("\n  ... and ").append(errors.size() - 5).append(" more error(s)");
        }
        
        return sb.toString();
    }
    
    /**
     * Validates includes before parsing (optional pre-check).
     */
    public void validateIncludes(File ramlFile) throws ConverterException {
        logger.debug("Validating includes for: {}", ramlFile.getName());
        
        try {
            List<String> lines = java.nio.file.Files.readAllLines(ramlFile.toPath());
            File parentDir = ramlFile.getParentFile();
            
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                
                // Check for !include directives
                if (line.contains("!include")) {
                    String includePath = extractIncludePath(line);
                    if (includePath != null) {
                        File includedFile = new File(parentDir, includePath);
                        if (!includedFile.exists()) {
                            throw new ConverterException(
                                "Referenced file not found at line " + (i + 1) + 
                                ": " + includePath + 
                                "\nExpected location: " + includedFile.getAbsolutePath()
                            );
                        }
                        logger.debug("  ✓ Found include: {}", includePath);
                    }
                }
            }
            
            logger.debug("✓ All includes validated");
            
        } catch (java.io.IOException e) {
            throw new ConverterException(
                "Failed to read RAML file for validation: " + e.getMessage(),
                e
            );
        }
    }
    
    /**
     * Extracts the file path from an !include directive.
     */
    private String extractIncludePath(String line) {
        // Pattern: !include path/to/file.raml
        if (!line.contains("!include")) {
            return null;
        }
        
        String[] parts = line.split("!include");
        if (parts.length < 2) {
            return null;
        }
        
        String path = parts[1].trim();
        
        // Remove any trailing comments or content
        if (path.contains("#")) {
            path = path.substring(0, path.indexOf("#")).trim();
        }
        
        return path;
    }
}
