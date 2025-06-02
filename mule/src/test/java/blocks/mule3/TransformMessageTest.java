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
package blocks.mule3;

import blocks.AbstractBlockTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TransformMessageTest extends AbstractBlockTest {

    @Test
    public void testTransformMessageWithSimpleValue() {
        testMule3ToBal("transform-message/transform_message_with_simple_value.xml",
                "transform-message/transform_message_with_simple_value.bal");
    }

    @Test
    public void testTransformMessageWithComponents() {
        testMule3ToBal("transform-message/transform_message_with_components.xml",
                "transform-message/transform_message_with_components.bal");
    }

    @Test
    public void testTransformMessageWithUnsupportedComponents() {
        testMule3ToBal("transform-message/transform_message_with_unsupported_components.xml",
                "transform-message/transform_message_with_unsupported_components.bal");
    }

    @Test (dataProvider = "provideTestParams")
    public void testTransformMessageWithCustomDataWeave(String dwFile, String balFile) {
        testDataWeaveMule3ToBal(dwFile, balFile);
    }

    @DataProvider (name = "provideTestParams")
    public Object[][] provideTestParams() {
        return new Object[][]{
                {"transform-message/dataweave-files/transform_message_with_string_return.dwl",
                        "transform-message/dataweave-bal-files/transform_message_with_string_return.bal"},
                {"transform-message/dataweave-files/transform_message_with_single_selector.dwl",
                        "transform-message/dataweave-bal-files/transform_message_with_single_selector.bal"},
                {"transform-message/dataweave-files/transform_message_with_sizeof.dwl",
                        "transform-message/dataweave-bal-files/transform_message_with_sizeof.bal"},
                {"transform-message/dataweave-files/transform_message_with_map_value_identifier.dwl",
                        "transform-message/dataweave-bal-files/transform_message_with_map_value_identifier.bal"},
                {"transform-message/dataweave-files/transform_message_with_map_index_identifier.dwl",
                        "transform-message/dataweave-bal-files/transform_message_with_map_index_identifier.bal"},
                {"transform-message/dataweave-files/transform_message_with_map_index_identifier_only.dwl",
                        "transform-message/dataweave-bal-files/transform_message_with_map_index_identifier_only.bal"},
                {"transform-message/dataweave-files/transform_message_with_upper.dwl",
                        "transform-message/dataweave-bal-files/transform_message_with_upper.bal"},
                {"transform-message/dataweave-files/transform_message_with_lower.dwl",
                        "transform-message/dataweave-bal-files/transform_message_with_lower.bal"},
                {"transform-message/dataweave-files/transform_message_with_map_combination.dwl",
                        "transform-message/dataweave-bal-files/transform_message_with_map_combination.bal"},
                {"transform-message/dataweave-files/transform_message_with_map_with_parameters.dwl",
                        "transform-message/dataweave-bal-files/transform_message_with_map_with_parameters.bal"},
                {"transform-message/dataweave-files/transform_message_with_filter_value_identifier.dwl",
                        "transform-message/dataweave-bal-files/transform_message_with_filter_value_identifier.bal"},
                {"transform-message/dataweave-files/transform_message_with_when_otherwise.dwl",
                        "transform-message/dataweave-bal-files/transform_message_with_when_otherwise.bal"},
                {"transform-message/dataweave-files/transform_message_with_when_otherwise_nested.dwl",
                        "transform-message/dataweave-bal-files/transform_message_with_when_otherwise_nested.bal"},
                {"transform-message/dataweave-files/transform_message_with_replace_with.dwl",
                        "transform-message/dataweave-bal-files/transform_message_with_replace_with.bal"},
                {"transform-message/dataweave-files/transform_message_with_concat_string.dwl",
                        "transform-message/dataweave-bal-files/transform_message_with_concat_string.bal"},
                {"transform-message/dataweave-files/transform_message_with_concat_array.dwl",
                        "transform-message/dataweave-bal-files/transform_message_with_concat_array.bal"},
                {"transform-message/dataweave-files/transform_message_with_concat_object.dwl",
                        "transform-message/dataweave-bal-files/transform_message_with_concat_object.bal"},
                {"transform-message/dataweave-files/transform_message_with_date_type.dwl",
                        "transform-message/dataweave-bal-files/transform_message_with_date_type.bal"},
                {"transform-message/dataweave-files/transform_message_with_type_coercion_string.dwl",
                        "transform-message/dataweave-bal-files/transform_message_with_type_coercion_string.bal"},
                {"transform-message/dataweave-files/transform_message_with_type_coercion_number.dwl",
                        "transform-message/dataweave-bal-files/transform_message_with_type_coercion_number.bal"},
                {"transform-message/dataweave-files/transform_message_with_type_coercion_format.dwl",
                        "transform-message/dataweave-bal-files/transform_message_with_type_coercion_format.bal"},
                {"transform-message/dataweave-files/transform_message_with_type_coercion_date_to_number.dwl",
                        "transform-message/dataweave-bal-files/" +
                                "transform_message_with_type_coercion_date_to_number.bal"},
                {"transform-message/dataweave-files/transform_message_with_type_coercion_to_date.dwl",
                        "transform-message/dataweave-bal-files/transform_message_with_type_coercion_to_date.bal"},
        };
    }

}
