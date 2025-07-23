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

package synapse.converter.tools;

public interface SynapseConversionTool extends Tool {

    /**
     * Get the tool description such that we can send it to Claude.
     *
     * @return Short description of the tool
     */
    String description();

    /**
     * Get the input schema for the tool. This is used to validate the input before sending it to the tool.
     *
     * @return JSON schema as a string.
     */
    String inputSchema();
}
