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
import synapse.model.Synapse.SynapseNode;

import java.util.ArrayList;
import java.util.List;

public class SynapseModelGenerator {

    private static final String API_TAG = "api";

    public static List<SynapseNode> generateModel(Element rootElement) {
        List<SynapseNode> nodes = new ArrayList<>();

        // The <api> may be the root element itself, or nested under the root.
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
        return new Api(name, context, new ArrayList<>());
    }
}
