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


public interface Tool {

    /**
     * Get the name of the tool so we can refer it in the requests.
     *
     * @return name of the tool
     */
    String name();

    /**
     * Get the content part of a tool request response. Tool must validate the request.
     *
     * @param request json string representing the {@code input} part of the request
     * @param cx      Tool context
     * @return {@code content} part of the tool response
     */
    String execute(ToolContext cx, String request);

}
