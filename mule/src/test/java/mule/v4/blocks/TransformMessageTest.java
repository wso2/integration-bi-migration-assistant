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
package mule.v4.blocks;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TransformMessageTest extends AbstractBlockTest {

    @Test
    public void testTransformMessageWithSimpleValue() {
        testMule4ToBal("transform-message/transform_message_with_simple_value.xml",
                "transform-message/transform_message_with_simple_value.bal");
    }

    @Test
    public void testTransformMessageWithComponents() {
        testMule4ToBal("transform-message/transform_message_with_components.xml",
                "transform-message/transform_message_with_components.bal");
    }

    @Test(enabled = false)
    public void testTransformMessageWithUnsupportedComponents() {
        testMule4ToBal("transform-message/transform_message_with_unsupported_components.xml",
                "transform-message/transform_message_with_unsupported_components.bal");
    }

    @Test (dataProvider = "provideTestParams")
    public void testTransformMessageWithCustomDataWeave(String dwFileName, String balFileName) {
        String dwFilePath = "transform-message/dataweave-files/" + dwFileName;
        String balFilePath = "transform-message/dataweave-bal-files/" + balFileName;
        testDataWeaveMule4ToBal(dwFilePath, balFilePath);
    }

    @DataProvider (name = "provideTestParams")
    public Object[][] provideTestParams() {
        return new Object[][]{
                {"transform_message_with_string_return.dwl", "transform_message_with_string_return.bal"},
                {"transform_message_with_single_selector.dwl", "transform_message_with_single_selector.bal"},
                {"transform_message_with_sizeof.dwl", "transform_message_with_sizeof.bal"},
                {"transform_message_with_map_value_identifier.dwl", "transform_message_with_map_value_identifier.bal"},
                {"transform_message_with_map_index_identifier.dwl", "transform_message_with_map_index_identifier.bal"},
                {"transform_message_with_map_index_identifier_only.dwl",
                        "transform_message_with_map_index_identifier_only.bal"},
                {"transform_message_with_upper.dwl", "transform_message_with_upper.bal"},
                {"transform_message_with_lower.dwl", "transform_message_with_lower.bal"},
                {"transform_message_with_map_combination.dwl", "transform_message_with_map_combination.bal"},
                {"transform_message_with_map_with_parameters.dwl", "transform_message_with_map_with_parameters.bal"},
                {"transform_message_with_filter_value_identifier.dwl",
                        "transform_message_with_filter_value_identifier.bal"},
                {"transform_message_with_when_otherwise.dwl", "transform_message_with_when_otherwise.bal"},
                {"transform_message_with_when_otherwise_nested.dwl",
                        "transform_message_with_when_otherwise_nested.bal"},
                {"transform_message_with_replace_with.dwl", "transform_message_with_replace_with.bal"},
                {"transform_message_with_concat_string.dwl", "transform_message_with_concat_string.bal"},
                {"transform_message_with_concat_array.dwl", "transform_message_with_concat_array.bal"},
                {"transform_message_with_concat_object.dwl", "transform_message_with_concat_object.bal"},
                {"transform_message_with_date_type.dwl", "transform_message_with_date_type.bal"},
                {"transform_message_with_type_coercion_string.dwl", "transform_message_with_type_coercion_string.bal"},
                {"transform_message_with_type_coercion_number.dwl", "transform_message_with_type_coercion_number.bal"},
                {"transform_message_with_type_coercion_format.dwl", "transform_message_with_type_coercion_format.bal"},
                {"transform_message_with_type_coercion_date_to_number.dwl",
                        "transform_message_with_type_coercion_date_to_number.bal"},
                {"transform_message_with_type_coercion_to_date.dwl",
                        "transform_message_with_type_coercion_to_date.bal"},
                {"transform_message_with_default_value.dwl", "transform_message_with_default_value.bal"}
        };
    }
}
