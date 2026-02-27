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
package mule.v4.model;

import java.util.List;
import java.util.Optional;

public record MUnitModel() {

    public record TestSuite(String name,
                            List<MUnitTest> tests,
                            Optional<LifecycleBlock> beforeSuite,
                            Optional<LifecycleBlock> afterSuite,
                            Optional<LifecycleBlock> beforeTest,
                            Optional<LifecycleBlock> afterTest) {
    }

    public record MUnitTest(String name,
                            String description,
                            Optional<String> expectedErrorType,
                            List<MUnitRecord> behavior,
                            List<MUnitRecord> execution,
                            List<MUnitRecord> validation) {
    }

    public record LifecycleBlock(String name,
                                 String description,
                                 List<MUnitRecord> processors) {
    }

    public interface MUnitRecord {
        Kind kind();
    }

    public record MockWhen(Kind kind,
                           String processor,
                           Optional<String> docName,
                           List<MockAttribute> withAttributes,
                           Optional<MockReturn> thenReturn) implements MUnitRecord {
        public MockWhen(String processor, Optional<String> docName,
                        List<MockAttribute> withAttributes, Optional<MockReturn> thenReturn) {
            this(Kind.MOCK_WHEN, processor, docName, withAttributes, thenReturn);
        }
    }

    public record MockAttribute(String attributeName, String whereValue) {
    }

    public record MockReturn(Optional<MockPayload> payload,
                             List<MockVariable> variables) {
    }

    public record MockPayload(String value, Optional<String> mediaType) {
    }

    public record MockVariable(String key, String value, Optional<String> mediaType) {
    }

    public record AssertThat(Kind kind,
                             String expression,
                             String is) implements MUnitRecord {
        public AssertThat(String expression, String is) {
            this(Kind.ASSERT_THAT, expression, is);
        }
    }

    public record AssertEquals(Kind kind,
                               String actual,
                               String expected) implements MUnitRecord {
        public AssertEquals(String actual, String expected) {
            this(Kind.ASSERT_EQUALS, actual, expected);
        }
    }

    public record AssertNotNull(Kind kind, String expression) implements MUnitRecord {
        public AssertNotNull(String expression) {
            this(Kind.ASSERT_NOT_NULL, expression);
        }
    }

    public record Fail(Kind kind, Optional<String> message) implements MUnitRecord {
        public Fail(Optional<String> message) {
            this(Kind.FAIL, message);
        }
    }

    public record SetEvent(Kind kind,
                           Optional<String> payload,
                           Optional<String> mimeType,
                           List<SetEventVariable> variables) implements MUnitRecord {
        public SetEvent(Optional<String> payload, Optional<String> mimeType,
                        List<SetEventVariable> variables) {
            this(Kind.SET_EVENT, payload, mimeType, variables);
        }
    }

    public record SetEventVariable(String key, String value, Optional<String> mediaType) {
    }

    public record VerifyCall(Kind kind, String processor, Optional<String> docName,
                             Optional<String> times, Optional<String> atLeast,
                             Optional<String> atMost) implements MUnitRecord {
        public VerifyCall(String processor, Optional<String> docName,
                          Optional<String> times, Optional<String> atLeast,
                          Optional<String> atMost) {
            this(Kind.VERIFY_CALL, processor, docName, times, atLeast, atMost);
        }
    }

    public record MuleProcessorRef(Kind kind, MuleModel.MuleRecord muleRecord) implements MUnitRecord {
        public MuleProcessorRef(MuleModel.MuleRecord muleRecord) {
            this(Kind.MULE_PROCESSOR_REF, muleRecord);
        }
    }

    public record UnsupportedMUnitBlock(Kind kind, String xmlBlock) implements MUnitRecord {
        public UnsupportedMUnitBlock(String xmlBlock) {
            this(Kind.UNSUPPORTED_MUNIT_BLOCK, xmlBlock);
        }
    }

    public enum Kind {
        MOCK_WHEN,
        ASSERT_THAT,
        ASSERT_EQUALS,
        ASSERT_NOT_NULL,
        FAIL,
        SET_EVENT,
        VERIFY_CALL,
        MULE_PROCESSOR_REF,
        UNSUPPORTED_MUNIT_BLOCK
    }
}

