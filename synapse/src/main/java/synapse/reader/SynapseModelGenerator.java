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

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import synapse.model.Synapse.Api;
import synapse.model.Synapse.InSequence;
import synapse.model.Synapse.PayloadFactory;
import synapse.model.Synapse.Property;
import synapse.model.Synapse.Resource;
import synapse.model.Synapse.Respond;
import synapse.model.Synapse.SynapseNode;

import java.util.ArrayList;
import java.util.List;

public class SynapseModelGenerator {

    private static final String API_TAG = "api";
    private static final String RESOURCE_TAG = "resource";
    private static final String IN_SEQUENCE_TAG = "inSequence";
    private static final String PAYLOAD_FACTORY_TAG = "payloadFactory";
    private static final String RESPOND_TAG = "respond";
    private static final String PROPERTY_TAG = "property";
    private static final String FORMAT_TAG = "format";

    private static final String DEFAULT_PROPERTY_TYPE = "string";
    private static final String DEFAULT_PROPERTY_SCOPE = "default";

    public static List<SynapseNode> generateModel(Element rootElement) {
        List<SynapseNode> nodes = new ArrayList<>();

        if (API_TAG.equals(rootElement.getTagName())) {
            nodes.add(readApi(rootElement));
            return nodes;
        }

        NodeList children = rootElement.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            Element element = (Element) node;
            if (API_TAG.equals(element.getTagName())) {
                nodes.add(readApi(element));
            }
        }

        return nodes;
    }

    private static Api readApi(Element element) {
        String name = element.getAttribute("name");
        String context = element.getAttribute("context");

        List<SynapseNode> resources = new ArrayList<>();
        for (Element child : childElements(element)) {
            if (RESOURCE_TAG.equals(child.getTagName())) {
                resources.add(readResource(child));
            }
        }

        return new Api(name, context, resources);
    }

    private static Resource readResource(Element element) {
        String methods = element.getAttribute("methods");

        String uriTemplate = element.getAttribute("uri-template");
        String urlMapping = element.getAttribute("url-mapping");
        if (uriTemplate.isEmpty() && urlMapping.isEmpty()) {
            throw new IllegalArgumentException("Synapse resource must define either 'uri-template' or 'url-mapping'.");
        }
        String template = !uriTemplate.isEmpty() ? uriTemplate : urlMapping;

        String path = "";
        String query = "";
        int queryStart = template.indexOf('?');
        if (queryStart >= 0) {
            path = template.substring(0, queryStart);
            query = template.substring(queryStart + 1);
        } else {
            path = template;
        }

        List<String> queryParams = new ArrayList<>();
        if (!query.isEmpty()) {
            for (String pair : query.split("&")) {
                int eq = pair.indexOf('=');
                String key = eq >= 0 ? pair.substring(0, eq) : pair;
                if (!key.isEmpty()) {
                    queryParams.add(key);
                }
            }
        }

        InSequence inSequence = null;
        for (Element child : childElements(element)) {
            if (IN_SEQUENCE_TAG.equals(child.getTagName())) {
                inSequence = readInSequence(child);
                break;
            }
        }

        return new Resource(methods, path, queryParams, inSequence);
    }

    private static InSequence readInSequence(Element element) {
        List<SynapseNode> mediators = new ArrayList<>();
        for (Element child : childElements(element)) {
            switch (child.getTagName()) {
                case PAYLOAD_FACTORY_TAG -> mediators.add(readPayloadFactory(child));
                case RESPOND_TAG -> mediators.add(new Respond());
                case PROPERTY_TAG -> mediators.add(readProperty(child));
                default -> {
                    // Other mediators are not supported yet; skip for now.
                }
            }
        }
        return new InSequence(mediators);
    }

    private static Property readProperty(Element element) {
        String name = element.getAttribute("name");

        String type = element.getAttribute("type");
        if (type.isEmpty()) {
            type = DEFAULT_PROPERTY_TYPE;
        }

        String scope = element.getAttribute("scope");
        if (scope.isEmpty()) {
            scope = DEFAULT_PROPERTY_SCOPE;
        }

        String value = element.getAttribute("value");
        return new Property(name, type, scope, value);
    }

    private static PayloadFactory readPayloadFactory(Element element) {
        String mediaType = element.getAttribute("media-type");
        String format = "";
        for (Element child : childElements(element)) {
            if (FORMAT_TAG.equals(child.getTagName())) {
                format = child.getTextContent().trim();
            }
        }
        return new PayloadFactory(mediaType, format);
    }

    private static List<Element> childElements(Element parent) {
        List<Element> elements = new ArrayList<>();
        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                elements.add((Element) node);
            }
        }
        return elements;
    }
}
