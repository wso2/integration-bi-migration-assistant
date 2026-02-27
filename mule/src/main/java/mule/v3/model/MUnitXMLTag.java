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
package mule.v3.model;

public enum MUnitXMLTag {

    // MUnit core
    MUNIT_CONFIG("munit:config"),
    MUNIT_TEST("munit:test"),
    MUNIT_BEFORE_SUITE("munit:before-suite"),
    MUNIT_AFTER_SUITE("munit:after-suite"),
    MUNIT_BEFORE_TEST("munit:before-test"),
    MUNIT_AFTER_TEST("munit:after-test"),
    MUNIT_SET_PAYLOAD("munit:set-payload"),

    // MUnit assertions
    MUNIT_ASSERT_ON_EQUALS("munit:assert-on-equals"),
    MUNIT_ASSERT_NOT_NULL("munit:assert-not-null"),
    MUNIT_ASSERT_NULL("munit:assert-null"),
    MUNIT_ASSERT_TRUE("munit:assert-true"),
    MUNIT_ASSERT_THAT("munit:assert-that"),
    MUNIT_FAIL("munit:fail"),

    // MUnit mocking
    MUNIT_MOCK("munit:mock"),
    MUNIT_WHEN("munit:when"),
    MUNIT_RETURN("munit:return"),
    MUNIT_WITH_PAYLOAD("munit:with-payload"),
    MUNIT_WITH_ATTRIBUTES("munit:with-attributes"),
    MUNIT_WITH_INBOUND_PROPERTIES("munit:with-inbound-properties"),
    MUNIT_WITH_SESSION_VARIABLES("munit:with-session-variables"),
    MUNIT_WITH_INVOCATION_PROPERTIES("munit:with-invocation-properties"),

    // MUnit spy
    MUNIT_SPY("munit:spy"),
    MUNIT_ASSERT_PAYLOAD("munit:assert-payload"),
    MUNIT_ASSERT_VARIABLE("munit:assert-variable"),
    MUNIT_ASSERT_INBOUND_PROPERTY("munit:assert-inbound-property"),

    // MUnit verify
    MUNIT_VERIFY_CALL("munit:verify-call"),

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
        return tag.startsWith("munit:");
    }
}
