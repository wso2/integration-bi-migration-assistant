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

package converter.tibco;

import ballerina.BallerinaModel;
import ballerina.CodeGenerator;
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import tibco.TibcoModel;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


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

    public static BallerinaModel.Module convertProject(String projectPath) {
        List<TibcoModel.Process> processes = new ArrayList<>();
        try {
            for (String s : getBwpFiles(projectPath)) {
                Element element = parseXmlFile(s);
                TibcoModel.Process parseProcess = XmlToTibcoModelConverter.parseProcess(element);
                processes.add(parseProcess);
            }
        } catch (IOException | SAXException | ParserConfigurationException e) {
            throw new RuntimeException("Error while parsing the XML file: ", e);
        }

        return ProcessConverter.convertProcesses(processes);
    }


    private static List<String> getBwpFiles(String projectPath) throws IOException {
        try (var pathStream = Files.walk(Paths.get(projectPath))) {
            return pathStream
                    .filter(Files::isRegularFile)
                    .map(Path::toString)
                    .filter(string -> string.endsWith(".bwp"))
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

}
