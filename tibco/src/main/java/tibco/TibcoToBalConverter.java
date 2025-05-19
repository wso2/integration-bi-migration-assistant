/*
 *  Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com). All Rights Reserved.
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
import tibco.analyzer.ModelAnalyser;
import tibco.analyzer.ProjectAnalysisContext;
import tibco.converter.ConversionResult;
import tibco.converter.ProjectConverter;

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
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class TibcoToBalConverter {

    private static final Logger logger = Logger.getLogger(TibcoToBalConverter.class.getName());
    private TibcoToBalConverter() {
    }

    public static ConversionResult convertProject(ProjectConversionContext cx, String projectPath) {
        Set<TibcoModel.Process> processes;
        Set<TibcoModel.Type.Schema> types;
        Set<TibcoModel.Resource.JDBCResource> jdbcResources;
        Set<TibcoModel.Resource.HTTPConnectionResource> httpConnectionResources;
        Set<TibcoModel.Resource.HTTPClientResource> httpClientResources;
        Set<TibcoModel.Resource.HTTPSharedResource> httpSharedResources;
        try {
            processes = PROCESS_PARSING_UNIT.parse(projectPath);
            types = XSD_PARSING_UNIT.parse(projectPath);
            jdbcResources = JDBC_RESOURCE_PARSING_UNIT.parse(projectPath);
            httpConnectionResources = HTTP_CONN_RESOURCE_PARSING_UNIT.parse(projectPath);
            httpClientResources = HTTP_CLIENT_RESOURCE_PARSING_UNIT.parse(projectPath);
            var httpSharedResourceParser = new HTTPSharedResourceParsingUnit();
            httpSharedResources = httpSharedResourceParser.parse(projectPath);
        } catch (IOException | SAXException | ParserConfigurationException e) {
            logger.severe("Unrecoverable error while parsing project file: " + projectPath);
            throw new RuntimeException("Error while parsing the XML file: ", e);
        }
        Map<TibcoModel.Process, AnalysisResult> analysisResult =
                ModelAnalyser.analyseProcesses(new ProjectAnalysisContext(), processes);

        return ProjectConverter.convertProject(cx, analysisResult, processes, types, jdbcResources,
                httpConnectionResources, httpClientResources, httpSharedResources);
    }

    private static final ParsingUnit<TibcoModel.Process> PROCESS_PARSING_UNIT = new ParsingUnit.SimpleParsingUnit<>(
            TibcoToBalConverter::getBwpFiles, XmlToTibcoModelConverter::parseProcess);
    private static final ParsingUnit<TibcoModel.Type.Schema> XSD_PARSING_UNIT = new ParsingUnit.SimpleParsingUnit<>(
            TibcoToBalConverter::getXSDFiles, XmlToTibcoModelConverter::parseSchema);
    private static final ParsingUnit<TibcoModel.Resource.JDBCResource> JDBC_RESOURCE_PARSING_UNIT =
            new ParsingUnit.SimpleParsingUnit<>(
            TibcoToBalConverter::getJDBCResourceFiles, XmlToTibcoModelConverter::parseJDBCResource);
    private static final ParsingUnit<TibcoModel.Resource.HTTPConnectionResource> HTTP_CONN_RESOURCE_PARSING_UNIT =
            new ParsingUnit.SimpleParsingUnit<>(
                    TibcoToBalConverter::getHTTPConnectionResourceFiles,
                    XmlToTibcoModelConverter::parseHTTPConnectionResource);
    private static final ParsingUnit<TibcoModel.Resource.HTTPClientResource> HTTP_CLIENT_RESOURCE_PARSING_UNIT =
            new ParsingUnit.SimpleParsingUnit<>(
                    TibcoToBalConverter::getHTTPClientResourceFiles,
                    XmlToTibcoModelConverter::parseHTTPClientResource);

    static final class HTTPSharedResourceParsingUnit implements ParsingUnit<TibcoModel.Resource.HTTPSharedResource> {

        @Override
        public Set<TibcoModel.Resource.HTTPSharedResource> parse(String projectPath) throws
                IOException, ParserConfigurationException, SAXException {
            Set<TibcoModel.Resource.HTTPSharedResource> result = new LinkedHashSet<>();
            for (String file : getHTTPSharedResourceFiles(projectPath)) {
                Element element = parseXmlFile(file);
                Path filePath = Path.of(file);
                String fileName = filePath.getFileName().toString();
                result.add(XmlToTibcoModelConverter.parseHTTPSharedResource(fileName, element));
            }
            return result;
        }
    }

    interface ParsingUnit<E> {
        Set<E> parse(String projectPath) throws IOException, ParserConfigurationException, SAXException;

        record SimpleParsingUnit<E>(FileFinder fileFinder,
                                    Function<Element, E> parsingFn) implements ParsingUnit<E> {

            @Override
            public Set<E> parse(String projectPath) throws IOException, ParserConfigurationException, SAXException {
                Set<E> elements = new HashSet<>();
                for (String s : fileFinder.findFiles(projectPath)) {
                    Element element = parseXmlFile(s);
                    E parsedElement = parsingFn.apply(element);
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

    public record ProjectConversionContext(List<JavaDependencies> javaDependencies) {

        public ProjectConversionContext() {
            this(new ArrayList<>());
        }
    }
}
