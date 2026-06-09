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
import synapse.model.Synapse.Resource;
import synapse.model.Synapse.SynapseNode;

import java.util.ArrayList;
import java.util.List;

public class SynapseModelGenerator {

    private static final String API_TAG = "api";
    private static final String RESOURCE_TAG = "resource";

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
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            Element child = (Element) node;
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
        // TODO: uriTemplate or urlMapping should be mandotory, so it should be checked and handled the error.
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

        return new Resource(methods, path, queryParams);
    }
}
