/*
 *  Copyright (c) 2026, WSO2 LLC. (http://www.wso2.com).
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
package synapse.reader;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import synapse.model.Synapse.SynapseNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class SynapseConfigReader {

    /**
     * Resolve the Synapse artifact files to convert from a source path. The path may be either a
     * single artifact {@code .xml} file or a Synapse project directory. When a directory is given,
     * every {@code .xml} artifact found under it (recursively) is returned in a stable order.
     *
     * <p>The artifacts are intentionally <em>not</em> parsed here: callers parse and convert them one
     * at a time so that a whole project never needs to be held in memory at once.
     *
     * @param path Synapse project directory or artifact file path
     * @return the artifact files to convert, in deterministic order
     */
    public static List<File> collectArtifactFiles(String path) {
        Path sourcePath = Paths.get(path);
        if (!Files.isDirectory(sourcePath)) {
            String fileName = sourcePath.getFileName() == null ? "" :
                    sourcePath.getFileName().toString().toLowerCase(Locale.ROOT);
            if (Files.isRegularFile(sourcePath) && fileName.endsWith(".xml")) {
                return List.of(sourcePath.toFile());
            }
            return List.of();
        }

        List<File> xmlFiles = new ArrayList<>();
        collectXmlFiles(sourcePath.toFile(), xmlFiles);
        xmlFiles.sort(Comparator.comparing(File::getAbsolutePath));
        return xmlFiles;
    }

    /**
     * Parse a single Synapse artifact file into its model nodes.
     *
     * @param xmlFile the Synapse {@code .xml} artifact file
     * @return the {@link SynapseNode}s declared in the file
     */
    public static List<SynapseNode> parse(File xmlFile) {
        try {
            Element rootElement = readXMLConfigurationFile(xmlFile);
            return SynapseModelGenerator.generateModel(rootElement);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException("Error while parsing the Synapse XML configuration file: "
                    + xmlFile.getPath(), e);
        }
    }

    /**
     * Recursively collect every {@code .xml} file under {@code folder} into {@code xmlFiles}.
     */
    static void collectXmlFiles(File folder, List<File> xmlFiles) {
        File[] files = folder.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                collectXmlFiles(file, xmlFiles);
            } else if (file.getName().toLowerCase().endsWith(".xml")) {
                xmlFiles.add(file);
            }
        }
    }

    public static Element readXMLConfigurationFile(File xmlFile) throws ParserConfigurationException,
            SAXException, IOException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        factory.setXIncludeAware(false);
        factory.setExpandEntityReferences(false);

        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(xmlFile);
        document.getDocumentElement().normalize();

        return document.getDocumentElement();
    }
}
