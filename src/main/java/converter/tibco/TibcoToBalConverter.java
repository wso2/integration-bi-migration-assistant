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

package converter.tibco;

import ballerina.BallerinaModel;
import ballerina.CodeGenerator;
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import tibco.TibcoModel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class TibcoToBalConverter {

    private TibcoToBalConverter() {
    }

    public static SyntaxTree convertFile(String xmlFilePath) {
        Element root;
        try {
            root = parseXmlFile(xmlFilePath);
            assert root != null;
        } catch (Exception e) {
            throw new RuntimeException("Error while parsing the XML file: " + xmlFilePath, e);
        }
        TibcoModel.Process process = XmlToTibcoModelConverter.parseProcess(root);
        BallerinaModel.Module ballerinaModule =
                ProcessConverter.convertProcess(process);
        BallerinaModel ballerinaModel = new BallerinaModel(new BallerinaModel.DefaultPackage("tibco", "sample", "0.1"),
                List.of(ballerinaModule));
        return new CodeGenerator(ballerinaModel).generateBalCode();
    }

    public static ConversionResult convertProject(ProjectConversionContext cx, String projectPath) {
        Set<TibcoModel.Process> processes;
        Set<TibcoModel.Type.Schema> types;
        Set<TibcoModel.Resource.JDBCResource> jdbcResources;
        Set<TibcoModel.Resource.HTTPConnectionResource> httpConnectionResources;
        Set<TibcoModel.Resource.HTTPClientResource> httpClientResources;
        try {
            processes = PROCESS_PARSING_UNIT.parse(projectPath);
            types = XSD_PARSING_UNIT.parse(projectPath);
            jdbcResources = JDBC_RESOURCE_PARSING_UNIT.parse(projectPath);
            httpConnectionResources = HTTP_CONN_RESOURCE_PARSING_UNIT.parse(projectPath);
            httpClientResources = HTTP_CLIENT_RESOURCE_PARSING_UNIT.parse(projectPath);
        } catch (IOException | SAXException | ParserConfigurationException e) {
            throw new RuntimeException("Error while parsing the XML file: ", e);
        }

        return ProcessConverter.convertProject(cx, processes, types, jdbcResources, httpConnectionResources,
                httpClientResources);
    }

    private static final ParsingUnit<TibcoModel.Process> PROCESS_PARSING_UNIT =
            new ParsingUnit<>(TibcoToBalConverter::getBwpFiles, XmlToTibcoModelConverter::parseProcess);
    private static final ParsingUnit<TibcoModel.Type.Schema> XSD_PARSING_UNIT =
            new ParsingUnit<>(TibcoToBalConverter::getXSDFiles, XmlToTibcoModelConverter::parseSchema);
    private static final ParsingUnit<TibcoModel.Resource.JDBCResource> JDBC_RESOURCE_PARSING_UNIT =
            new ParsingUnit<>(TibcoToBalConverter::getJDBCResourceFiles, XmlToTibcoModelConverter::parseJDBCResource);
    private static final ParsingUnit<TibcoModel.Resource.HTTPConnectionResource> HTTP_CONN_RESOURCE_PARSING_UNIT =
            new ParsingUnit<>(TibcoToBalConverter::getHTTPConnectionResourceFiles,
                    XmlToTibcoModelConverter::parseHTTPConnectionResource);
    private static final ParsingUnit<TibcoModel.Resource.HTTPClientResource> HTTP_CLIENT_RESOURCE_PARSING_UNIT =
            new ParsingUnit<>(TibcoToBalConverter::getHTTPClientResourceFiles,
                    XmlToTibcoModelConverter::parseHTTPClientResource);

    private record ParsingUnit<E>(FileFinder fileFinder, Function<Element, E> parsingFn) {

        Set<E> parse(String projectPath) throws IOException, ParserConfigurationException, SAXException {
            Set<E> elements = new HashSet<>();
            for (String s : fileFinder.findFiles(projectPath)) {
                Element element = parseXmlFile(s);
                E parsedElement = parsingFn.apply(element);
                elements.add(parsedElement);
            }
            return elements;
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

    private static List<String> getHTTPConnectionResourceFiles(String projectPath) throws IOException {
        return getFilesWithExtension(projectPath, "httpConnResource");
    }

    private static List<String> getXSDFiles(String projectPath) throws IOException {
        return getFilesWithExtension(projectPath, "xsd");
    }

    private static List<String> getBwpFiles(String projectPath) throws IOException {
        return getFilesWithExtension(projectPath, "bwp");
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
