/*
 *  Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com).
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

package tibco;

import common.ConversionUtils;
import common.LoggingUtils;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import tibco.analyzer.DefaultAnalysisPass;
import tibco.analyzer.LoggingAnalysisPass;
import tibco.analyzer.ModelAnalyser;
import tibco.model.Process;
import tibco.model.Resource;
import tibco.model.Type;
import tibco.parser.Context;
import tibco.parser.ProcessContext;
import tibco.parser.ProjectContext;
import tibco.parser.ResourceContext;
import tibco.parser.TypeContext;
import tibco.parser.XmlToTibcoModelParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class TibcoToBalConverter {

    private TibcoToBalConverter() {
    }

    public static @NotNull java.util.Set<tibco.model.Process> parseProcesses(tibco.parser.ProjectContext pcx)
            throws IOException, SAXException, ParserConfigurationException {
        return PROCESS_PARSING_UNIT.parse(pcx);
    }

    public static @NotNull java.util.Set<tibco.model.Type.Schema> parseTypes(tibco.parser.ProjectContext pcx)
            throws IOException, SAXException, ParserConfigurationException {
        return XSD_PARSING_UNIT.parse(pcx);
    }

    public static @NotNull tibco.converter.ProjectConverter.ProjectResources parseResources(
            tibco.parser.ProjectContext pcx)
            throws IOException, SAXException, ParserConfigurationException {
        java.util.Set<Resource.JDBCResource> jdbcResources = JDBC_RESOURCE_PARSING_UNIT.parse(pcx);
        java.util.Set<Resource.HTTPConnectionResource> httpConnectionResources =
                HTTP_CONN_RESOURCE_PARSING_UNIT.parse(pcx);
        java.util.Set<Resource.HTTPClientResource> httpClientResources = HTTP_CLIENT_RESOURCE_PARSING_UNIT.parse(pcx);
        var httpSharedResourceParser = new HTTPSharedResourceParsingUnit();
        java.util.Set<Resource.HTTPSharedResource> httpSharedResources = httpSharedResourceParser.parse(pcx);
        java.util.Set<Resource.JDBCSharedResource> jdbcSharedResource = SHARED_JDBC_RESOURCE_PARSING_UNIT.parse(pcx);
        var jmsSharedResourceParser = new JMSSharedResourceParsingUnit();
        java.util.Set<Resource.JMSSharedResource> jmsSharedResource = jmsSharedResourceParser.parse(pcx);
        java.util.Set<Resource.SharedVariable> sharedVariables = SHARED_VARIABLE_PARSING_UNIT.parse(pcx);

        return new tibco.converter.ProjectConverter.ProjectResources(jdbcResources,
                httpConnectionResources, httpClientResources, httpSharedResources, jdbcSharedResource,
                jmsSharedResource, sharedVariables);
    }


    private static final ParsingUnit<Process> PROCESS_PARSING_UNIT = pcx -> {
        Set<Process> elements = new HashSet<>();
        for (String s : getBwpFiles(pcx.projectPath())) {
            Element element = parseXmlFile(s);
            Optional<Process> parsedElement = XmlToTibcoModelParser.parseProcess(new ProcessContext(pcx, s),
                    element);
            if (parsedElement.isPresent()) {
                elements.add(parsedElement.get());
            } else {
                pcx.log(LoggingUtils.Level.SEVERE, "Failed to parse process: " + s);
            }
        }
        return elements;
    };
    private static final ParsingUnit<Type.Schema> XSD_PARSING_UNIT =
            new ParsingUnit.SimpleParsingUnit<>(
                    TibcoToBalConverter::getXSDFiles, XmlToTibcoModelParser::parseSchema,
                    (ProjectContext pcx, String filePath) -> new TypeContext(pcx));
    private static final ParsingUnit<Resource.JDBCResource> JDBC_RESOURCE_PARSING_UNIT =
            new ParsingUnit.SimpleParsingUnit<>(
                    TibcoToBalConverter::getJDBCResourceFiles, XmlToTibcoModelParser::parseJDBCResource,
                    TibcoToBalConverter::getResourceContext);
    private static final ParsingUnit<Resource.JDBCSharedResource> SHARED_JDBC_RESOURCE_PARSING_UNIT =
            new ParsingUnit.SimpleParsingUnit<>(
                    (String projectPath) -> getFilesWithExtension(projectPath, "sharedjdbc"),
                    XmlToTibcoModelParser::parseSharedJDBCResource,
                    TibcoToBalConverter::getResourceContext);
    private static final ParsingUnit<Resource.HTTPConnectionResource> HTTP_CONN_RESOURCE_PARSING_UNIT =
            new ParsingUnit.SimpleParsingUnit<>(
                    TibcoToBalConverter::getHTTPConnectionResourceFiles,
                    XmlToTibcoModelParser::parseHTTPConnectionResource,
                    TibcoToBalConverter::getResourceContext);
    private static final ParsingUnit<Resource.HTTPClientResource> HTTP_CLIENT_RESOURCE_PARSING_UNIT =
            new ParsingUnit.SimpleParsingUnit<>(
                    TibcoToBalConverter::getHTTPClientResourceFiles,
                    XmlToTibcoModelParser::parseHTTPClientResource,
                    TibcoToBalConverter::getResourceContext);
    private static final ParsingUnit<Resource.SharedVariable> SHARED_VARIABLE_PARSING_UNIT = new ParsingUnit<>() {
        @Override
        public Set<Resource.SharedVariable> parse(ProjectContext pcx)
                throws IOException, ParserConfigurationException, SAXException {
            Set<Resource.SharedVariable> variables = new HashSet<>();

            for (String s : getFilesWithExtension(pcx.projectPath(), "sharedvariable")) {
                String relativePath = "/" + Paths.get(pcx.projectPath()).relativize(Paths.get(s)).toString();
                Optional<Resource.SharedVariable> var = XmlToTibcoModelParser.parseSharedVariable(
                        new ResourceContext(pcx, s),
                        parseXmlFile(s));
                if (var.isPresent()) {
                    variables.add(var.get());
                } else {
                    pcx.log(LoggingUtils.Level.SEVERE, "Failed to parse sharedvariable: " + s);
                }
            }

            for (String s : getFilesWithExtension(pcx.projectPath(), "jobsharedvariable")) {
                Optional<Resource.SharedVariable> var = XmlToTibcoModelParser.parseJobSharedVariable(
                        new ResourceContext(pcx, s),
                        parseXmlFile(s));
                if (var.isPresent()) {
                    variables.add(var.get());
                } else {
                    pcx.log(LoggingUtils.Level.SEVERE, "Failed to parse jobsharedvariable: " + s);
                }
            }

            return variables;
        }
    };

    private static ResourceContext getResourceContext(ProjectContext pcx, String filePath) {
        return new ResourceContext(pcx, filePath);
    }

    static final class HTTPSharedResourceParsingUnit
            implements ParsingUnit<Resource.HTTPSharedResource> {

        @Override
        public Set<Resource.HTTPSharedResource> parse(ProjectContext pcx) throws
                IOException, ParserConfigurationException, SAXException {
            Set<Resource.HTTPSharedResource> result = new LinkedHashSet<>();
            for (String file : getHTTPSharedResourceFiles(pcx.projectPath())) {
                Element element = parseXmlFile(file);
                Path filePath = Path.of(file);
                String fileName = filePath.getFileName().toString();
                Optional<Resource.HTTPSharedResource> resource = XmlToTibcoModelParser.parseHTTPSharedResource(
                        new ResourceContext(pcx, file),
                        fileName,
                        element);
                if (resource.isPresent()) {
                    result.add(resource.get());
                } else {
                    pcx.log(LoggingUtils.Level.SEVERE, "Failed to parse HTTPSharedResource: " + file);
                }
            }
            return result;
        }
    }

    static final class JMSSharedResourceParsingUnit implements ParsingUnit<Resource.JMSSharedResource> {

        @Override
        public Set<Resource.JMSSharedResource> parse(ProjectContext pcx) throws
                IOException, ParserConfigurationException, SAXException {
            Set<Resource.JMSSharedResource> result = new LinkedHashSet<>();
            for (String file : getFilesWithExtension(pcx.projectPath(), "sharedjmscon")) {
                Element element = parseXmlFile(file);
                Path filePath = Path.of(file);
                String fileName = filePath.getFileName().toString();
                Optional<Resource.JMSSharedResource> resource = XmlToTibcoModelParser.parseJMSSharedResource(
                        new ResourceContext(pcx, file),
                        fileName,
                        element);
                if (resource.isPresent()) {
                    result.add(resource.get());
                } else {
                    pcx.log(LoggingUtils.Level.SEVERE, "Failed to parse JMSSharedResource: " + file);
                }
            }
            return result;
        }
    }

    interface ParsingUnit<E> {

        Set<E> parse(ProjectContext pcx)
                throws IOException, ParserConfigurationException, SAXException;

        record SimpleParsingUnit<E, C extends Context>(FileFinder fileFinder,
                                                       BiFunction<C, Element, Optional<E>> parsingFn,
                                                       BiFunction<ProjectContext, String, C> contextSupplier)
                implements ParsingUnit<E> {

            @Override
            public Set<E> parse(ProjectContext pcx)
                    throws IOException, ParserConfigurationException, SAXException {
                Set<E> elements = new HashSet<>();
                for (String s : fileFinder.findFiles(pcx.projectPath())) {
                    Element element = parseXmlFile(s);
                    Optional<E> parsedElement = parsingFn.apply(contextSupplier.apply(pcx, s), element);
                    if (parsedElement.isPresent()) {
                        elements.add(parsedElement.get());
                    } else {
                        pcx.log(LoggingUtils.Level.SEVERE, "Failed to parse resource: " + s);
                    }
                }
                return elements;
            }
        }
    }

    @FunctionalInterface
    private interface FileFinder {

        Collection<String> findFiles(String projectPath) throws IOException;
    }

    private static List<String> getJDBCResourceFiles(String projectPath) throws IOException {
        return getFilesWithExtension(projectPath, "jdbcResource");
    }

    private static List<String> getHTTPClientResourceFiles(String projectPath) throws IOException {
        return getFilesWithExtension(projectPath, "httpClientResource");
    }

    private static List<String> getHTTPSharedResourceFiles(String projectPath) throws IOException {
        return getFilesWithExtension(projectPath, "sharedhttp");
    }

    private static List<String> getHTTPConnectionResourceFiles(String projectPath) throws IOException {
        return getFilesWithExtension(projectPath, "httpConnResource");
    }

    private static List<String> getXSDFiles(String projectPath) throws IOException {
        return getFilesWithExtension(projectPath, "xsd");
    }

    private static List<String> getBwpFiles(String projectPath) throws IOException {
        List<String> bwpFiles = getFilesWithExtension(projectPath, "bwp");
        List<String> processFiles = getFilesWithExtension(projectPath, "process");
        return Stream.concat(bwpFiles.stream(), processFiles.stream())
                .toList();
    }

    private static List<String> getFilesWithExtension(String projectPath, String extension) throws IOException {
        String extensionWithDot = "." + extension;
        try (var pathStream = Files.walk(Paths.get(projectPath))) {
            return pathStream
                    .filter(Files::isRegularFile)
                    .map(Path::toString)
                    .sorted()
                    .filter(string -> string.endsWith(extensionWithDot))
                    .toList();
        }
    }

    public static Element parseXmlFile(String xmlFilePath)
            throws IOException, SAXException, ParserConfigurationException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(xmlFilePath);

        document.getDocumentElement().normalize();

        return document.getDocumentElement();
    }

    public static @NotNull Map<String, Object> migrateTIBCO(Map<String, Object> parameters) {
        try {
            String orgName = validateAndGetString(parameters, "orgName");
            String projectName = validateAndGetString(parameters, "projectName");
            String sourcePath = validateAndGetString(parameters, "sourcePath");
            Consumer<String> stateCallback = validateAndGetConsumer(parameters, "stateCallback");
            Consumer<String> logCallback = validateAndGetConsumer(parameters, "logCallback");

            return migrateTIBCOInner(orgName, projectName, sourcePath, stateCallback, logCallback);
        } catch (IllegalArgumentException e) {
            return Map.of("error", e.getMessage());
        }
    }

    private static @NotNull Map<String, Object> migrateTIBCOInner(String orgName, String projectName, String sourcePath,
            Consumer<String> stateCallback, Consumer<String> logCallback) {
        String escapedOrgName = ConversionUtils.escapeIdentifier(orgName);
        String escapedProjectName = ConversionUtils.escapeIdentifier(projectName);
        ProjectConversionContext cx = new ProjectConversionContext(
                new ConversionContext(escapedOrgName, false, false, stateCallback, logCallback), escapedProjectName);
        try {
            tibco.converter.TibcoConverter.ParsedProject parsed =
                    tibco.converter.TibcoConverter.parseProject(cx, sourcePath);
            tibco.converter.TibcoConverter.AnalyzedProject analyzed =
                    tibco.converter.TibcoConverter.analyzeProject(cx, parsed, new ModelAnalyser(List.of(
                            new DefaultAnalysisPass(),
                            new LoggingAnalysisPass())));
            tibco.converter.TibcoConverter.GeneratedProject generated =
                    tibco.converter.TibcoConverter.generateCode(cx, analyzed);
            tibco.converter.TibcoConverter.SerializedProject serialized =
                    tibco.converter.TibcoConverter.serializeProject(cx, generated, List.of());

            return Map.of("textEdits", serialized.files(), "report", serialized.report().toHTML(),
                    "report-json", serialized.report().toJSON());
        } catch (Exception e) {
            return Map.of("error", e.getMessage());
        }
    }

    private static @NotNull String validateAndGetString(Map<String, Object> parameters, String key) {
        if (!parameters.containsKey(key)) {
            throw new IllegalArgumentException("Missing required parameter: " + key);
        }
        Object value = parameters.get(key);
        if (!(value instanceof String)) {
            throw new IllegalArgumentException("Parameter " + key + " must be a String, got: " +
                    (value != null ? value.getClass().getSimpleName() : "null"));
        }
        return (String) value;
    }

    @SuppressWarnings("unchecked")
    private static @NotNull Consumer<String> validateAndGetConsumer(Map<String, Object> parameters, String key) {
        if (!parameters.containsKey(key)) {
            throw new IllegalArgumentException("Missing required parameter: " + key);
        }
        Object value = parameters.get(key);
        if (!(value instanceof Consumer)) {
            throw new IllegalArgumentException("Parameter " + key + " must be a Consumer<String>, got: " +
                    (value != null ? value.getClass().getSimpleName() : "null"));
        }
        return (Consumer<String>) value;
    }

    public enum JavaDependencies {
        JDBC_H2("""
                [[platform.java17.dependency]]
                artifactId = "h2"
                version = "2.0.206"
                groupId = "com.h2database"
                """),
        JDBC_POSTGRESQL("""
                [[platform.java17.dependency]]
                artifactId = "postgresql"
                version = "42.7.2"
                groupId = "org.postgresql"
                """),
        JDBC_MYSQL("""
                [[platform.java17.dependency]]
                artifactId = "mysql-connector-java"
                version = "8.0.33"
                groupId = "mysql"
                """),
        JDBC_ORACLE("""
                [[platform.java17.dependency]]
                artifactId = "ojdbc8"
                version = "21.9.0.0"
                groupId = "com.oracle.database.jdbc"
                """),
        JDBC_MARIADB("""
                [[platform.java17.dependency]]
                artifactId = "mariadb-java-client"
                version = "3.1.4"
                groupId = "org.mariadb.jdbc"
                """);

        public final String dependencyParam;

        JavaDependencies(String dependencyParam) {
            this.dependencyParam = dependencyParam;
        }
    }

}
