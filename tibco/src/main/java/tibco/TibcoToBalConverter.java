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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import tibco.analyzer.AnalysisResult;
import tibco.analyzer.DefaultAnalysisPass;
import tibco.analyzer.LoggingAnalysisPass;
import tibco.analyzer.ModelAnalyser;
import tibco.analyzer.ProjectAnalysisContext;
import tibco.analyzer.ReportGenerationPass;
import tibco.analyzer.TibcoAnalysisReport;
import tibco.converter.ConversionResult;
import tibco.converter.ProjectConverter;
import tibco.converter.TibcoConverter;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.logging.Logger;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class TibcoToBalConverter {


    private TibcoToBalConverter() {
    }

    public static ConversionResult convertProject(ProjectConversionContext cx, String projectPath) {
        Set<tibco.model.Process> processes;
        Set<Type.Schema> types;
        Set<Resource.JDBCResource> jdbcResources;
        Set<Resource.HTTPConnectionResource> httpConnectionResources;
        Set<Resource.HTTPClientResource> httpClientResources;
        Set<Resource.HTTPSharedResource> httpSharedResources;
        Set<Resource.JDBCSharedResource> jdbcSharedResource;
        ProjectContext pcx = new ProjectContext();
        try {
            processes = PROCESS_PARSING_UNIT.parse(pcx, projectPath);
            types = XSD_PARSING_UNIT.parse(pcx, projectPath);
            jdbcResources = JDBC_RESOURCE_PARSING_UNIT.parse(pcx, projectPath);
            httpConnectionResources = HTTP_CONN_RESOURCE_PARSING_UNIT.parse(pcx, projectPath);
            httpClientResources = HTTP_CLIENT_RESOURCE_PARSING_UNIT.parse(pcx, projectPath);
            var httpSharedResourceParser = new HTTPSharedResourceParsingUnit();
            httpSharedResources = httpSharedResourceParser.parse(pcx, projectPath);
            jdbcSharedResource = SHARED_JDBC_RESOURCE_PARSING_UNIT.parse(pcx, projectPath);
        } catch (IOException | SAXException | ParserConfigurationException e) {
            logger().severe("Unrecoverable error while parsing project file: " + projectPath);
            throw new RuntimeException("Error while parsing the XML file: ", e);
        }
        ModelAnalyser analyser = new ModelAnalyser(List.of(
                new DefaultAnalysisPass(),
                new LoggingAnalysisPass(),
                new ReportGenerationPass()));
        Map<tibco.model.Process, AnalysisResult> analysisResult =
                analyser.analyseProject(new ProjectAnalysisContext(), processes, types);
        TibcoAnalysisReport report = analysisResult.values().stream()
                .map(AnalysisResult::getReport)
                .flatMap(Optional::stream)
                .reduce(TibcoAnalysisReport.empty(), TibcoAnalysisReport::combine);
        if (cx.dryRun()) {
            return new ConversionResult(null, null, report);
        }
        return ProjectConverter.convertProject(cx, analysisResult, processes, types,
                new ProjectConverter.ProjectResources(jdbcResources,
                        httpConnectionResources, httpClientResources, httpSharedResources, jdbcSharedResource),
                report);
    }

    private static final ParsingUnit<Process> PROCESS_PARSING_UNIT =
            new ParsingUnit.SimpleParsingUnit<>(
                    TibcoToBalConverter::getBwpFiles, XmlToTibcoModelParser::parseProcess,
                    ProcessContext::new);
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

    public static Logger logger() {
        return TibcoConverter.logger();
    }

    private static ResourceContext getResourceContext(ProjectContext pcx, String filePath) {
        return new ResourceContext(pcx, filePath);
    }

    static final class HTTPSharedResourceParsingUnit
            implements ParsingUnit<Resource.HTTPSharedResource> {

        @Override
        public Set<Resource.HTTPSharedResource> parse(ProjectContext pcx, String projectPath) throws
                IOException, ParserConfigurationException, SAXException {
            Set<Resource.HTTPSharedResource> result = new LinkedHashSet<>();
            for (String file : getHTTPSharedResourceFiles(projectPath)) {
                Element element = parseXmlFile(file);
                Path filePath = Path.of(file);
                String fileName = filePath.getFileName().toString();
                result.add(XmlToTibcoModelParser.parseHTTPSharedResource(getResourceContext(pcx, file),
                        element));
            }
            return result;
        }
    }

    interface ParsingUnit<E> {

        Set<E> parse(ProjectContext pcx, String projectPath)
                throws IOException, ParserConfigurationException, SAXException;

        record SimpleParsingUnit<E, C extends Context>(FileFinder fileFinder,
                                                       BiFunction<C, Element, E> parsingFn,
                                                       BiFunction<ProjectContext, String, C> contextSupplier)
                implements ParsingUnit<E> {

            @Override
            public Set<E> parse(ProjectContext pcx, String projectPath)
                    throws IOException, ParserConfigurationException, SAXException {
                Set<E> elements = new HashSet<>();
                for (String s : fileFinder.findFiles(projectPath)) {
                    Element element = parseXmlFile(s);
                    E parsedElement = parsingFn.apply(contextSupplier.apply(pcx, s), element);
                    elements.add(parsedElement);
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

    public enum JavaDependencies {
        JDBC("""
                [[platform.java17.dependency]]
                artifactId = "h2"
                version = "2.0.206"
                groupId = "com.h2database"
                """);

        public final String dependencyParam;

        JavaDependencies(String dependencyParam) {
            this.dependencyParam = dependencyParam;
        }
    }

    public record ProjectConversionContext(boolean verbose, boolean dryRun, List<JavaDependencies> javaDependencies) {


        public ProjectConversionContext(boolean verbose, boolean dryRun) {
            this(verbose, dryRun, new ArrayList<>());
        }
    }
}
