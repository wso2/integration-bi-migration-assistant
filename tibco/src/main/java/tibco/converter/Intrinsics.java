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

package tibco.converter;

public enum Intrinsics {
    XPATH_PREDICATE(
            "test",
            """
                    function test(xml input, string xpath) returns boolean {
                        // TODO: support XPath
                        return false;
                    }
                    """
    ),
    XML_PARSER_RESULT(
            "XMLElementParseResult",
            """
                    type XMLElementParseResult record {|
                        string? namespace;
                        string name;
                    |};
                    """
    ),
    XML_PARSER(
            "",
            """
                    function parseElement(xml:Element element) returns XMLElementParseResult {
                        string name = element.getName();
                        if (name.startsWith("{")) {
                            int? index = name.indexOf("}");
                            if (index == ()) {
                                panic error("Invalid element name: " + name);
                            }
                            string namespace = name.substring(1, index);
                            name = name.substring(index + 1);
                            return {namespace: namespace, name: name};
                        }
                        return {namespace: (), name: name};
                    }
                    """
    ),
    TO_JSON("xmlToJson",
            """
                    function xmlToJson(xml value) returns json {
                        json result = toJsonInner(value);
                        if (result is map<json> && result.hasKey("InputElement")) {
                            return result.get("InputElement");
                        } else {
                            return result;
                        }
                    }

                    function toJsonInner(xml value) returns json {
                        json result;
                        if (value is xml:Element) {
                            result = toJsonElement(value);
                        } else {
                            result = value.toJson();
                        }
                        return result;
                    }

                    function toJsonElement(xml:Element element) returns json {
                        XMLElementParseResult parseResult = parseElement(element);
                        string name = parseResult.name;

                        xml children = element/*;
                        map<json> body = {};
                        map<json> result = {};
                        foreach xml child in children {
                            json r = toJsonInner(child);
                            if child !is xml:Element {
                                result[name] = r;
                                return result;
                            }
                            string childName = parseElement(child).name;
                            if r !is map<json> {
                                panic error("unexpected");
                            } else {
                                r = r.get(childName);
                            }
                            if body.hasKey(childName) {
                                json current = body.get(childName);
                                if current !is json[] {
                                    json[] n = [body.get(childName)];
                                    n.push(r);
                                    body[childName] = n;
                                } else {
                                    current.push(r);
                                }
                            } else {
                                body[childName] = r;
                            }
                        }
                        result[name] = body;
                        return result;
                    }
                    """),
    RENDER_JSON(
            "renderJson",
            """
                    function renderJson(xml value) returns xml {
                        json jsonValue = xmlToJson(value);
                        return xml `<root><jsonString>${jsonValue.toJsonString()}</jsonString></root>`;
                    }
                    """
    ),
    PATCH_XML_NAMESPACES(
            "transform",
            """
                    function transform(xml value) returns xml {
                        xml result = transformInner(value);
                        string str = result.toString();
                        return checkpanic xml:fromString(str);
                    }

                    function transformInner(xml value) returns xml {
                        xml result;
                        if (value is xml:Element) {
                            result = transformElement(value);
                        } else {
                            result = value;
                        }
                        return result;
                    }

                    function transformElement(xml:Element element) returns xml {
                        XMLElementParseResult parseResult = parseElement(element);
                        string? namespace = parseResult.namespace;

                        xml:Element transformedElement = element.clone();
                        transformedElement.setName(parseResult.name);
                        map<string> attributes = transformedElement.getAttributes();
                        if namespace != () {
                            attributes["xmlns"] = namespace;
                        }

                        // Get children and transform them recursively
                        xml children = element/*.clone();
                        xml transformedChildren = children.map(transform);

                        // Create new element with transformed children
                        transformedElement.setChildren(transformedChildren);
                        return transformedElement;
                    }
                    """

    ),
    RENDER_JSON_AS_XML(
            "renderJSONAsXML",
            """
                    function renderJSONAsXML(json value, string? namespace, string typeName) returns xml|error {
                        anydata body;
                        if (value is map<json>) {
                            xml acum = xml ``;
                            foreach string key in value.keys() {
                                acum += check renderJSONAsXML(value.get(key), namespace, key);
                            }
                            body = acum;
                        } else {
                            body = value;
                        }

                        string rep = string `<${typeName}>${body.toString()}</${typeName}>`;
                        xml result = check xml:fromString(rep);
                        if (namespace == ()) {
                            return result;
                        }
                        if (result !is xml:Element) {
                            panic error("Expected XML element");
                        }
                        map<string> attributes = result.getAttributes();
                        attributes["xmlns"] = namespace;
                        return result;
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
                                path = selectPath.substring(0, index) + "/" + selectPath.substring(index);
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
                                path = selectPath.substring(0, index) + "/" + selectPath.substring(index);
                            }
                            attributes["test"] = path;
                        }
                        return input;
                    }
                    """
    ),
            PARSE_HEADERS(
            "parseHeaders",
            """
                    function parseHeaders(xml headers) returns map<string> {
                        map<string> headerMap = {};
                        foreach xml header in headers {
                            if header is xml:Element {
                                string fullName = header.getName();
                                int? lastIndex = fullName.lastIndexOf("}");
                                string headerName = lastIndex is int ? fullName.substring(lastIndex + 1) : fullName;
                                string headerValue = header.data();
                                headerMap[headerName] = headerValue;
                            }
                        }
                        return headerMap;
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
