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
package mule.common;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.LinkedHashMap;
import java.util.function.Function;

public class MuleXMLNavigator {
    protected final MigrationMetrics<? extends DWConstructBase> migrationMetrics;
    private final Function<String, Boolean> isCompatibleFunction;

    public MuleXMLNavigator(MigrationMetrics<? extends DWConstructBase> migrationMetrics,
                            Function<String, Boolean> isCompatibleFunction) {
        assert migrationMetrics != null && isCompatibleFunction != null;
        this.migrationMetrics = migrationMetrics;
        this.isCompatibleFunction = isCompatibleFunction;
    }

    public MuleElement createRootMuleElement(Element rootElement) {
        return new MuleElement(rootElement);
    }

    protected boolean isCompatible(String tagName) {
        return isCompatibleFunction.apply(tagName);
    }

    protected void updateXMLTagCountMaps(String tagName) {
        if (isCompatible(tagName)) {
            updateXMLTagCountMap(migrationMetrics.passedXMLTags, tagName);
        } else {
            updateXMLTagCountMap(migrationMetrics.failedXMLTags, tagName);
        }
    }

    protected void updateXMLTagCountMap(LinkedHashMap<String, Integer> hashMap, String tagName) {
        Integer i = hashMap.get(tagName);
        if (i == null) {
            hashMap.put(tagName, 1);
        } else {
            hashMap.put(tagName, ++i);
        }
    }

    public class MuleElement {
        protected final Element rootElement;
        protected Element currentChild;

        protected MuleElement(Element rootElement) {
            assert rootElement != null;
            this.rootElement = rootElement;
            this.currentChild = peekFirstChild();
        }

        public Element getElement() {
            return rootElement;
        }

        public Element peekChild() {
            return currentChild;
        }

        public MuleElement consumeChild() {
            updateXMLTagCountMaps(currentChild.getTagName());
            MuleElement muleElement = new MuleElement(currentChild);
            this.currentChild = peekNextSibling();
            return muleElement;
        }

        private Element peekFirstChild() {
            Node firstChild = rootElement.getFirstChild();
            while (firstChild != null && firstChild.getNodeType() != Node.ELEMENT_NODE) {
                firstChild = firstChild.getNextSibling();
            }
            return firstChild != null ? (Element) firstChild : null;
        }

        private Element peekNextSibling() {
            Node nextSibling = currentChild.getNextSibling();
            while (nextSibling != null && nextSibling.getNodeType() != Node.ELEMENT_NODE) {
                nextSibling = nextSibling.getNextSibling();
            }
            return nextSibling != null ? (Element) nextSibling : null;
        }
    }
}
