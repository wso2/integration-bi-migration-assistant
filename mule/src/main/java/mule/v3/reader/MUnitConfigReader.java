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
package mule.v3.reader;

import mule.common.MuleXMLNavigator;
import mule.common.MuleXMLNavigator.MuleElement;
import mule.v3.Context;
import mule.v3.ConversionUtils;
import mule.v3.model.MUnitMuleProcessorRef;
import mule.v3.model.MUnitXMLTag;
import mule.v4.model.MUnitModel.AssertEquals;
import mule.v4.model.MUnitModel.AssertNotNull;
import mule.v4.model.MUnitModel.AssertThat;
import mule.v4.model.MUnitModel.Fail;
import mule.v4.model.MUnitModel.LifecycleBlock;
import mule.v4.model.MUnitModel.MUnitRecord;
import mule.v4.model.MUnitModel.MUnitTest;
import mule.v4.model.MUnitModel.MockAttribute;
import mule.v4.model.MUnitModel.MockPayload;
import mule.v4.model.MUnitModel.MockReturn;
import mule.v4.model.MUnitModel.MockWhen;
import mule.v4.model.MUnitModel.SetEvent;
import mule.v4.model.MUnitModel.TestSuite;
import mule.v4.model.MUnitModel.UnsupportedMUnitBlock;
import mule.v4.model.MUnitModel.VerifyCall;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
                    if (!MUnitXMLTag.isMUnitTag(tagName)) {
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
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(uri);
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
                case MUNIT_MOCK, MUNIT_SPY -> behavior.add(readMUnitProcessor(ctx, child));
                default -> {
                    MUnitRecord record = readMUnitProcessor(ctx, child);
                    if (isAssertionRecord(childTag)) {
                        validation.add(record);
                    } else {
                        execution.add(record);
                    }
                }
            }
        }

        return new MUnitTest(name, description,
                expectedErrorType.isEmpty() ? Optional.empty() : Optional.of(expectedErrorType),
                behavior, execution, validation);
    }

    private static boolean isAssertionRecord(MUnitXMLTag tag) {
        return switch (tag) {
            case MUNIT_ASSERT_ON_EQUALS, MUNIT_ASSERT_NOT_NULL, MUNIT_ASSERT_NULL,
                    MUNIT_ASSERT_TRUE, MUNIT_ASSERT_THAT, MUNIT_FAIL,
                    MUNIT_VERIFY_CALL -> true;
            default -> false;
        };
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
            case MUNIT_MOCK -> readMockWhen(muleElement);
            case MUNIT_ASSERT_ON_EQUALS -> readAssertEquals(element);
            case MUNIT_ASSERT_NOT_NULL -> readAssertNotNull(element);
            case MUNIT_ASSERT_NULL -> readAssertNull(element);
            case MUNIT_ASSERT_THAT -> readAssertThat(element);
            case MUNIT_FAIL -> readFail(element);
            case MUNIT_SET_PAYLOAD -> readSetPayload(element);
            case MUNIT_VERIFY_CALL -> readVerifyCall(element);
            default -> {
                if (MUnitXMLTag.isMUnitTag(tagName)) {
                    yield new UnsupportedMUnitBlock(ConversionUtils.elementToString(element));
                }
                yield new MUnitMuleProcessorRef(MuleConfigReader.readBlock(ctx, muleElement));
            }
        };
    }

    private static MockWhen readMockWhen(MuleElement muleElement) {
        Element element = muleElement.getElement();
        String processor = element.getAttribute("messageProcessor");
        String docName = element.getAttribute("doc:name");

        List<MockAttribute> withAttributes = new ArrayList<>();
        Optional<MockReturn> thenReturn = Optional.empty();

        while (muleElement.peekChild() != null) {
            MuleElement child = muleElement.consumeChild();
            Element childElement = child.getElement();
            MUnitXMLTag childTag = MUnitXMLTag.fromTag(childElement.getTagName());

            switch (childTag) {
                case MUNIT_WITH_ATTRIBUTES -> withAttributes.addAll(readWithAttributes(child));
                case MUNIT_WHEN -> thenReturn = Optional.of(readMockReturn(child));
                default -> {
                    // skip unknown children
                }
            }
        }

        return new MockWhen(processor,
                docName.isEmpty() ? Optional.empty() : Optional.of(docName),
                withAttributes, thenReturn);
    }

    private static List<MockAttribute> readWithAttributes(MuleElement parentElement) {
        List<MockAttribute> attributes = new ArrayList<>();
        while (parentElement.peekChild() != null) {
            MuleElement child = parentElement.consumeChild();
            Element element = child.getElement();
            String attrName = element.getAttribute("name");
            String whereValue = element.getAttribute("whereValue");
            if (!attrName.isEmpty()) {
                attributes.add(new MockAttribute(attrName, whereValue));
            }
        }
        return attributes;
    }

    private static MockReturn readMockReturn(MuleElement parentElement) {
        Optional<MockPayload> payload = Optional.empty();

        while (parentElement.peekChild() != null) {
            MuleElement child = parentElement.consumeChild();
            Element element = child.getElement();
            MUnitXMLTag tag = MUnitXMLTag.fromTag(element.getTagName());

            if (tag == MUnitXMLTag.MUNIT_WITH_PAYLOAD) {
                String value = element.getAttribute("payload");
                String mediaType = element.getAttribute("mimeType");
                payload = Optional.of(new MockPayload(value,
                        mediaType.isEmpty() ? Optional.empty() : Optional.of(mediaType)));
            }
        }

        return new MockReturn(payload, List.of());
    }

    private static AssertEquals readAssertEquals(Element element) {
        String expectedValue = element.getAttribute("expectedValue");
        String valueToCheck = element.getAttribute("valueToCheck");
        return new AssertEquals(valueToCheck, expectedValue);
    }

    private static AssertNotNull readAssertNotNull(Element element) {
        return new AssertNotNull(element.getAttribute("payload"));
    }

    private static AssertThat readAssertNull(Element element) {
        String payload = element.getAttribute("payload");
        return new AssertThat(payload, "nullValue()");
    }

    private static AssertThat readAssertThat(Element element) {
        return new AssertThat(element.getAttribute("expression"), element.getAttribute("is"));
    }

    private static Fail readFail(Element element) {
        String message = element.getAttribute("message");
        return new Fail(message.isEmpty() ? Optional.empty() : Optional.of(message));
    }

    private static SetEvent readSetPayload(Element element) {
        String payload = element.getAttribute("payload");
        String mimeType = element.getAttribute("mimeType");
        return new SetEvent(
                payload.isEmpty() ? Optional.empty() : Optional.of(payload),
                mimeType.isEmpty() ? Optional.empty() : Optional.of(mimeType),
                List.of());
    }

    private static VerifyCall readVerifyCall(Element element) {
        String processor = element.getAttribute("messageProcessor");
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
