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
    ),
    ADD_TO_CONTEXT(
            "addToContext",
            "function addToContext(map<xml> context, string varName, xml value){\n" +
                    "    xml children = value/*;\n" +
                    "    xml transformed = xml `<root>${children}</root>`;\n" +
                    "    context[varName] = transformed;\n" +
                    "}\n"
    ),
    TRANSFORM_XSLT(
            "transformXSLT",
            "function transformXSLT(xml input) returns xml {\n" +
                    "    xmlns \"http://www.w3.org/1999/XSL/Transform\" as xsl;\n" +
                    "    xml<xml:Element> values = input/**/<xsl:value\\-of>;\n" +
                    "    foreach xml:Element item in values {\n" +
                    "        map<string> attributes = item.getAttributes();\n" +
                    "        string selectPath = attributes.get(\"select\");\n" +
                    "        int? index = selectPath.indexOf(\"/\");\n" +
                    "        string path;\n" +
                    "        if index == () {\n" +
                    "            path = selectPath;\n" +
                    "        } else {\n" +
                    "            path = selectPath.substring(0, index) + \"/root\" + selectPath.substring(index);\n" +
                    "        }\n" +
                    "        attributes[\"select\"] = path;\n" +
                    "    }\n" +
                    "    xml<xml:Element> test = input/**/<xsl:'if>;\n" +
                    "    foreach xml:Element item in test {\n" +
                    "        map<string> attributes = item.getAttributes();\n" +
                    "        string selectPath = attributes.get(\"test\");\n" +
                    "        int? index = selectPath.indexOf(\"/\");\n" +
                    "        string path;\n" +
                    "        if index == () {\n" +
                    "            path = selectPath;\n" +
                    "        } else {\n" +
                    "            path = selectPath.substring(0, index) + \"/root\" + selectPath.substring(index);\n"
                    +
                    "        }\n" +
                    "        attributes[\"test\"] = path;\n" +
                    "    }\n" +
                    "    return input;\n" +
                    "}\n"
    );
    public final String body;
    public final String name;

    Intrinsics(String name, String body) {
        this.name = name;
        this.body = body;
    }
}
