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
            """
                    function getRequestPath(HTTPRequestConfig config) returns string {
                        string base = config.RequestURI;
                        if (config.parameters.length() == 0) {
                            return base;
                        }
                        return base + "?" + "&".'join(...from string key in config.parameters.keys()
                            select key + "=" + config.parameters.get(key));
                    }
                    """
    ),
    ADD_TO_CONTEXT(
            "addToContext",
            """
                    function addToContext(map<xml> context, string varName, xml value){
                        xml children = value/*;
                        xml transformed = xml `<root>${children}</root>`;
                        context[varName] = transformed;
                    }
                    """
    ),
    LOG_WRAPPER(
            "logWrapper",
            """
                    function logWrapper(LogParametersType input) {
                        match (input) {
                            {message: var m, logLevel: "info"} => {
                                log:printInfo(m);
                            }
                            {message: var m, logLevel: "debug"} => {
                                log:printDebug(m);
                            }
                            {message: var m, logLevel: "warn"} => {
                                log:printWarn(m);
                            }
                            {message: var m, logLevel: "error"} => {
                                log:printError(m);
                            }
                            {message: var m} => {
                                log:printInfo(m);
                            }
                        }
                    }
                    
                    """
    ),
    XPATH_PREDICATE(
            "test",
            """
                    function test(xml input, string xpath) returns boolean {
                        // TODO: support XPath
                        return false;
                    }
                    """
    ),
    TRANSFORM_XSLT(
            "transformXSLT",
            """
                    function transformXSLT(xml input) returns xml {
                        xmlns "http://www.w3.org/1999/XSL/Transform" as xsl;
                        xml<xml:Element> values = input/**/<xsl:value\\-of>;
                        foreach xml:Element item in values {
                            map<string> attributes = item.getAttributes();
                            string selectPath = attributes.get("select");
                            int? index = selectPath.indexOf("/");
                            string path;
                            if index == () {
                                path = selectPath;
                            } else {
                                path = selectPath.substring(0, index) + "/root" + selectPath.substring(index);
                            }
                            attributes["select"] = path;
                        }
                        xml<xml:Element> test = input/**/<xsl:'if>;
                        foreach xml:Element item in test {
                            map<string> attributes = item.getAttributes();
                            string selectPath = attributes.get("test");
                            int? index = selectPath.indexOf("/");
                            string path;
                            if index == () {
                                path = selectPath;
                            } else {
                                path = selectPath.substring(0, index) + "/root" + selectPath.substring(index);
                            }
                            attributes["test"] = path;
                        }
                        return input;
                    }
                    """
    );
    public final String body;
    public final String name;

    Intrinsics(String name, String body) {
        this.name = name;
        this.body = body;
    }
}
