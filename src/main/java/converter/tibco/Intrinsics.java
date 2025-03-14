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

package converter.tibco;

public enum Intrinsics {
    CREATE_HTTP_REQUEST_PATH_FROM_CONFIG(
            "getRequestPath",
            "function getRequestPath(HTTPRequestConfig config) returns string {\n" +
                    "    string base = config.RequestURI;\n" +
                    "    if (config.parameters.length() == 0) {\n" +
                    "        return base;\n" +
                    "    }\n" +
                    "    return base + \"?\" + \"&\".'join(...from string key in config.parameters.keys()\n" +
                    "        select key + \"=\" + config.parameters.get(key));\n" +
                    "}\n"
    );
    public final String body;
    public final String name;

    Intrinsics(String name, String body) {
        this.name = name;
        this.body = body;
    }
}
