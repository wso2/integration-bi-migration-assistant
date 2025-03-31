package converter;

import dataweave.converter.DWConversionStats;
import mule.MuleXMLTag;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.LinkedHashMap;

public class MuleXMLNavigator {
    private final LinkedHashMap<String, Integer> xmlCompatibleTagCountMap;
    private final LinkedHashMap<String, Integer> xmlIncompatibleTagCountMap;
    private final DWConversionStats dwConversionStats;

    MuleXMLNavigator() {
        this.xmlCompatibleTagCountMap = new LinkedHashMap<>();
        this.xmlIncompatibleTagCountMap = new LinkedHashMap<>();
        this.dwConversionStats = new DWConversionStats();
    }

    DWConversionStats getDwConversionStats() {
        return dwConversionStats;
    }

    LinkedHashMap<String, Integer> getXmlCompatibleTagCountMap() {
        return xmlCompatibleTagCountMap;
    }

    LinkedHashMap<String, Integer> getXmlIncompatibleTagCountMap() {
        return xmlIncompatibleTagCountMap;
    }

    MuleElement createRootMuleElement(Element rootElement) {
        return new MuleElement(rootElement);
    }

    private void updateXMLTagCountMaps(String tagName) {
        if (MuleXMLTag.isCompatible(tagName)) {
            updateXMLTagCountMap(xmlCompatibleTagCountMap, tagName);
        } else {
            updateXMLTagCountMap(xmlIncompatibleTagCountMap, tagName);
        }
    }

    private void updateXMLTagCountMap(LinkedHashMap<String, Integer> hashMap, String tagName) {
        Integer i = hashMap.get(tagName);
        if (i == null) {
            hashMap.put(tagName, 1);
        } else {
            hashMap.put(tagName, ++i);
        }
    }

    class MuleElement {
        private final Element rootElement;
        private Element currentChild;

        private MuleElement(Element rootElement) {
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
