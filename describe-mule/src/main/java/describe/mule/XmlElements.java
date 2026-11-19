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
package describe.mule;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Small helpers that read facts from Mule XML elements.
 */
final class XmlElements {

    private XmlElements() {
    }

    static Optional<String> attribute(Element element, String name) {
        String value = element.getAttribute(name);
        return value.isEmpty() ? Optional.empty() : Optional.of(value);
    }

    static Optional<String> docName(Element element) {
        return attribute(element, "doc:name");
    }

    static Optional<String> docId(Element element) {
        return attribute(element, "doc:id");
    }

    // The XML prefix names the connector, for example "xref-client" for xref-client:translate
    static String connector(String tagName) {
        int colon = tagName.indexOf(':');
        return colon < 0 ? "core" : tagName.substring(0, colon);
    }

    // An element path such as /mule/flow[1]/choice[2]. The index counts the earlier siblings with the same tag.
    static String position(Element element) {
        StringBuilder path = new StringBuilder();
        Node node = element;
        while (node instanceof Element current) {
            Node parent = current.getParentNode();
            if (parent instanceof Element) {
                path.insert(0, "/" + current.getTagName() + "[" + sameTagIndex(current) + "]");
            } else {
                path.insert(0, "/" + current.getTagName());
            }
            node = parent;
        }
        return path.toString();
    }

    // The position of an element among all element children of its parent, counted from 0
    static int childIndex(Element element) {
        int index = 0;
        Node sibling = element.getPreviousSibling();
        while (sibling != null) {
            if (sibling instanceof Element) {
                index++;
            }
            sibling = sibling.getPreviousSibling();
        }
        return index;
    }

    static List<Element> children(Element parent) {
        List<Element> children = new ArrayList<>();
        Node child = parent.getFirstChild();
        while (child != null) {
            if (child instanceof Element element) {
                children.add(element);
            }
            child = child.getNextSibling();
        }
        return children;
    }

    // The n-th child element with this tag, counted from 1
    static Optional<Element> child(Element parent, String tagName, int number) {
        int seen = 0;
        for (Element child : children(parent)) {
            if (child.getTagName().equals(tagName)) {
                seen++;
                if (seen == number) {
                    return Optional.of(child);
                }
            }
        }
        return Optional.empty();
    }

    static Element parseRoot(Path file) throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        // Mule configs have no DOCTYPE; refusing one keeps external entities out
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        return factory.newDocumentBuilder().parse(file.toFile()).getDocumentElement();
    }

    private static int sameTagIndex(Element element) {
        int index = 1;
        Node sibling = element.getPreviousSibling();
        while (sibling != null) {
            if (sibling instanceof Element other && other.getTagName().equals(element.getTagName())) {
                index++;
            }
            sibling = sibling.getPreviousSibling();
        }
        return index;
    }
}
