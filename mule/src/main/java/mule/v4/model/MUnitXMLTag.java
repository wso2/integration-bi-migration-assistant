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

public enum MUnitXMLTag {

    // MUnit core
    MUNIT_CONFIG("munit:config"),
    MUNIT_TEST("munit:test"),
    MUNIT_BEFORE_SUITE("munit:before-suite"),
    MUNIT_AFTER_SUITE("munit:after-suite"),
    MUNIT_BEFORE_TEST("munit:before-test"),
    MUNIT_AFTER_TEST("munit:after-test"),
    MUNIT_BEHAVIOR("munit:behavior"),
    MUNIT_EXECUTION("munit:execution"),
    MUNIT_VALIDATION("munit:validation"),
    MUNIT_ENABLE_FLOW_SOURCES("munit:enable-flow-sources"),
    MUNIT_SET_EVENT("munit:set-event"),

    // MUnit tools - mocking
    MOCK_WHEN("munit-tools:mock-when"),
    MOCK_THEN_RETURN("munit-tools:then-return"),
    MOCK_WITH_ATTRIBUTES("munit-tools:with-attributes"),
    MOCK_WITH_ATTRIBUTE("munit-tools:with-attribute"),
    MOCK_PAYLOAD("munit-tools:payload"),
    MOCK_VARIABLES("munit-tools:variables"),
    MOCK_VARIABLE("munit-tools:variable"),

    // MUnit tools - assertions
    ASSERT_THAT("munit-tools:assert-that"),
    ASSERT_EQUALS("munit-tools:assert-equals"),
    ASSERT_NOT_NULL("munit-tools:assert-not-null"),
    FAIL("munit-tools:fail"),

    // MUnit tools - verification
    VERIFY_CALL("munit-tools:verify-call"),

    UNSUPPORTED_MUNIT_TAG("unsupported-munit-tag");

    private final String tag;

    MUnitXMLTag(String tag) {
        this.tag = tag;
    }

    public String tag() {
        return tag;
    }

    public static MUnitXMLTag fromTag(String tag) {
        for (MUnitXMLTag munitTag : values()) {
            if (munitTag.tag.equals(tag)) {
                return munitTag;
            }
        }
        return UNSUPPORTED_MUNIT_TAG;
    }

    public static boolean isMUnitTag(String tag) {
        return tag.startsWith("munit:") || tag.startsWith("munit-tools:");
    }
}

