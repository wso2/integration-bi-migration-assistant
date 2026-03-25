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
 *  KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package mule.v4.reader;

import mule.common.MUnitModel.AssertEquals;
import mule.common.MUnitModel.AssertNotNull;
import mule.common.MUnitModel.AssertThat;
import mule.common.MUnitModel.Fail;
import mule.common.MUnitModel.LifecycleBlock;
import mule.common.MUnitModel.MUnitRecord;
import mule.common.MUnitModel.MUnitTest;
import mule.common.MUnitModel.MockAttribute;
import mule.common.MUnitModel.MockPayload;
import mule.common.MUnitModel.MockReturn;
import mule.common.MUnitModel.MockVariable;
import mule.common.MUnitModel.MockVariableScope;
import mule.common.MUnitModel.MockWhen;
import mule.common.MUnitModel.SetEvent;
import mule.common.MUnitModel.SetEventVariable;
import mule.common.MUnitModel.TestSuite;
import mule.common.MUnitModel.UnsupportedMUnitBlock;
import mule.common.MUnitModel.VerifyCall;
import mule.common.MuleXMLNavigator;
import mule.common.MuleXMLNavigator.MuleElement;
import mule.v4.Context;
import mule.v4.ConversionUtils;
import mule.v4.model.MUnitModelV4.MuleProcessorRef;
import mule.v4.model.MUnitXMLTag;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MUnitConfigReader {

    public static TestSuite readMUnitTestSuite(Context ctx, MuleXMLNavigator navigator, String xmlFilePath) {
        assert ctx != null && navigator != null && xmlFilePath != null;

        Element root;
        try {
            root = parseMUnitXMLFile(xmlFilePath);
        } catch (Exception e) {
            throw new RuntimeException("Error while parsing MUnit XML file: " + xmlFilePath, e);
        }

        if (ctx.currentFileCtx == null) {
            ctx.currentFileCtx = new Context.FileContext(xmlFilePath, ctx.projectCtx);
        }

        MuleElement muleElement = navigator.createRootMuleElement(root);

        String suiteName = "";
        List<MUnitTest> tests = new ArrayList<>();
        Optional<LifecycleBlock> beforeSuite = Optional.empty();
        Optional<LifecycleBlock> afterSuite = Optional.empty();
        Optional<LifecycleBlock> beforeTest = Optional.empty();
        Optional<LifecycleBlock> afterTest = Optional.empty();

        while (muleElement.peekChild() != null) {
            MuleElement child = muleElement.consumeChild();
            Element element = child.getElement();
            String tagName = element.getTagName();
            MUnitXMLTag munitTag = MUnitXMLTag.fromTag(tagName);

            switch (munitTag) {
                case MUNIT_CONFIG -> suiteName = element.getAttribute("name");
                case MUNIT_TEST -> tests.add(readMUnitTest(ctx, child));
                case MUNIT_BEFORE_SUITE -> beforeSuite = Optional.of(readLifecycleBlock(ctx, child));
                case MUNIT_AFTER_SUITE -> afterSuite = Optional.of(readLifecycleBlock(ctx, child));
                case MUNIT_BEFORE_TEST -> beforeTest = Optional.of(readLifecycleBlock(ctx, child));
                case MUNIT_AFTER_TEST -> afterTest = Optional.of(readLifecycleBlock(ctx, child));
                default -> {
                    if (MUnitXMLTag.isMUnitTag(tagName)) {
                        ctx.logger.logWarn("Unsupported MUnit element ignored: <%s>".formatted(tagName));
                    } else {
                        MuleConfigReader.readGlobalConfigElement(ctx, child);
                    }
                }
            }
        }

        return new TestSuite(suiteName, tests, beforeSuite, afterSuite, beforeTest, afterTest);
    }

    private static Element parseMUnitXMLFile(String uri)
            throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        factory.setNamespaceAware(true);
        factory.setXIncludeAware(false);
        factory.setExpandEntityReferences(false);
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new java.io.File(uri));
        document.getDocumentElement().normalize();
        return document.getDocumentElement();
    }

    private static MUnitTest readMUnitTest(Context ctx, MuleElement testElement) {
        Element element = testElement.getElement();
        String name = element.getAttribute("name");
        String description = element.getAttribute("description");
        String expectedErrorType = element.getAttribute("expectedErrorType");

        List<MUnitRecord> behavior = new ArrayList<>();
        List<MUnitRecord> execution = new ArrayList<>();
        List<MUnitRecord> validation = new ArrayList<>();

        while (testElement.peekChild() != null) {
            MuleElement child = testElement.consumeChild();
            Element childElement = child.getElement();
            MUnitXMLTag childTag = MUnitXMLTag.fromTag(childElement.getTagName());

            switch (childTag) {
                case MUNIT_BEHAVIOR -> behavior.addAll(readMUnitProcessors(ctx, child));
                case MUNIT_EXECUTION -> execution.addAll(readMUnitProcessors(ctx, child));
                case MUNIT_VALIDATION -> validation.addAll(readMUnitProcessors(ctx, child));
                case MUNIT_ENABLE_FLOW_SOURCES -> {
                    execution.add(new UnsupportedMUnitBlock(ConversionUtils.elementToString(childElement)));
                }
                default -> execution.add(readMUnitProcessor(ctx, child));
            }
        }

        return new MUnitTest(name, description,
                expectedErrorType.isEmpty() ? Optional.empty() : Optional.of(expectedErrorType),
                behavior, execution, validation);
    }

    private static LifecycleBlock readLifecycleBlock(Context ctx, MuleElement lifecycleElement) {
        Element element = lifecycleElement.getElement();
        String name = element.getAttribute("name");
        String description = element.getAttribute("description");
        List<MUnitRecord> processors = readMUnitProcessors(ctx, lifecycleElement);
        return new LifecycleBlock(name, description, processors);
    }

    private static List<MUnitRecord> readMUnitProcessors(Context ctx, MuleElement parentElement) {
        List<MUnitRecord> records = new ArrayList<>();
        while (parentElement.peekChild() != null) {
            MuleElement child = parentElement.consumeChild();
            records.add(readMUnitProcessor(ctx, child));
        }
        return records;
    }

    private static MUnitRecord readMUnitProcessor(Context ctx, MuleElement muleElement) {
        Element element = muleElement.getElement();
        String tagName = element.getTagName();
        MUnitXMLTag munitTag = MUnitXMLTag.fromTag(tagName);

        return switch (munitTag) {
            case MOCK_WHEN -> readMockWhen(ctx, muleElement);
            case ASSERT_THAT -> readAssertThat(element);
            case ASSERT_EQUALS -> readAssertEquals(element);
            case ASSERT_NOT_NULL -> readAssertNotNull(element);
            case FAIL -> readFail(element);
            case MUNIT_SET_EVENT -> readSetEvent(ctx, muleElement);
            case VERIFY_CALL -> readVerifyCall(element);
            default -> {
                if (MUnitXMLTag.isMUnitTag(tagName)) {
                    yield new UnsupportedMUnitBlock(ConversionUtils.elementToString(element));
                }
                yield new MuleProcessorRef(MuleConfigReader.readBlock(ctx, muleElement));
            }
        };
    }

    private static MockWhen readMockWhen(Context ctx, MuleElement muleElement) {
        Element element = muleElement.getElement();
        String processor = element.getAttribute("processor");
        String docName = element.getAttribute("doc:name");

        List<MockAttribute> withAttributes = new ArrayList<>();
        Optional<MockReturn> thenReturn = Optional.empty();

        while (muleElement.peekChild() != null) {
            MuleElement child = muleElement.consumeChild();
            Element childElement = child.getElement();
            MUnitXMLTag childTag = MUnitXMLTag.fromTag(childElement.getTagName());

            switch (childTag) {
                case MOCK_WITH_ATTRIBUTES -> withAttributes.addAll(readMockWithAttributes(child));
                case MOCK_THEN_RETURN -> thenReturn = Optional.of(readMockThenReturn(child));
                default -> {
                    // skip unknown children
                }
            }
        }

        return new MockWhen(processor,
                docName.isEmpty() ? Optional.empty() : Optional.of(docName),
                withAttributes, thenReturn);
    }

    private static List<MockAttribute> readMockWithAttributes(MuleElement parentElement) {
        List<MockAttribute> attributes = new ArrayList<>();
        while (parentElement.peekChild() != null) {
            MuleElement child = parentElement.consumeChild();
            Element element = child.getElement();
            if (MUnitXMLTag.fromTag(element.getTagName()) == MUnitXMLTag.MOCK_WITH_ATTRIBUTE) {
                attributes.add(new MockAttribute(
                        element.getAttribute("attributeName"),
                        element.getAttribute("whereValue")));
            }
        }
        return attributes;
    }

    private static MockReturn readMockThenReturn(MuleElement parentElement) {
        Optional<MockPayload> payload = Optional.empty();
        List<MockVariable> variables = new ArrayList<>();

        while (parentElement.peekChild() != null) {
            MuleElement child = parentElement.consumeChild();
            Element element = child.getElement();
            MUnitXMLTag tag = MUnitXMLTag.fromTag(element.getTagName());

            switch (tag) {
                case MOCK_PAYLOAD -> {
                    String value = element.getAttribute("value");
                    String mediaType = element.getAttribute("mediaType");
                    payload = Optional.of(new MockPayload(value,
                            mediaType.isEmpty() ? Optional.empty() : Optional.of(mediaType)));
                }
                case MOCK_VARIABLES -> variables.addAll(readMockVariables(child));
                default -> {
                    // skip unknown children
                }
            }
        }

        return new MockReturn(payload, variables);
    }

    private static List<MockVariable> readMockVariables(MuleElement parentElement) {
        List<MockVariable> variables = new ArrayList<>();
        while (parentElement.peekChild() != null) {
            MuleElement child = parentElement.consumeChild();
            Element element = child.getElement();
            if (MUnitXMLTag.fromTag(element.getTagName()) == MUnitXMLTag.MOCK_VARIABLE) {
                String key = element.getAttribute("key");
                String value = element.getAttribute("value");
                String mediaType = element.getAttribute("mediaType");
                variables.add(new MockVariable(key, value,
                        mediaType.isEmpty() ? Optional.empty() : Optional.of(mediaType),
                        MockVariableScope.VARIABLE));
            }
        }
        return variables;
    }

    private static AssertThat readAssertThat(Element element) {
        return new AssertThat(element.getAttribute("expression"), element.getAttribute("is"));
    }

    private static AssertEquals readAssertEquals(Element element) {
        return new AssertEquals(element.getAttribute("actual"), element.getAttribute("expected"));
    }

    private static AssertNotNull readAssertNotNull(Element element) {
        return new AssertNotNull(element.getAttribute("expression"));
    }

    private static Fail readFail(Element element) {
        String message = element.getAttribute("message");
        return new Fail(message.isEmpty() ? Optional.empty() : Optional.of(message));
    }

    private static SetEvent readSetEvent(Context ctx, MuleElement muleElement) {
        Element element = muleElement.getElement();
        String payload = element.getAttribute("payload");
        String mimeType = element.getAttribute("mimeType");

        List<SetEventVariable> variables = new ArrayList<>();
        while (muleElement.peekChild() != null) {
            MuleElement child = muleElement.consumeChild();
            Element childElement = child.getElement();
            String childTag = childElement.getTagName();
            if ("munit:variables".equals(childTag)) {
                variables.addAll(readSetEventVariables(child));
            }
        }

        return new SetEvent(
                payload.isEmpty() ? Optional.empty() : Optional.of(payload),
                mimeType.isEmpty() ? Optional.empty() : Optional.of(mimeType),
                variables);
    }

    private static List<SetEventVariable> readSetEventVariables(MuleElement parentElement) {
        List<SetEventVariable> variables = new ArrayList<>();
        while (parentElement.peekChild() != null) {
            MuleElement child = parentElement.consumeChild();
            Element element = child.getElement();
            if ("munit:variable".equals(element.getTagName())) {
                String key = element.getAttribute("key");
                String value = element.getAttribute("value");
                String mediaType = element.getAttribute("mediaType");
                variables.add(new SetEventVariable(key, value,
                        mediaType.isEmpty() ? Optional.empty() : Optional.of(mediaType)));
            }
        }
        return variables;
    }

    private static VerifyCall readVerifyCall(Element element) {
        String processor = element.getAttribute("processor");
        String docName = element.getAttribute("doc:name");
        String times = element.getAttribute("times");
        String atLeast = element.getAttribute("atLeast");
        String atMost = element.getAttribute("atMost");
        return new VerifyCall(processor,
                docName.isEmpty() ? Optional.empty() : Optional.of(docName),
                times.isEmpty() ? Optional.empty() : Optional.of(times),
                atLeast.isEmpty() ? Optional.empty() : Optional.of(atLeast),
                atMost.isEmpty() ? Optional.empty() : Optional.of(atMost));
    }
}

