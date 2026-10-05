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
    GET_FILES_IN_PATH(
            "filesInPath",
            """
                    function getFileName(string absPath) returns string {
                        int? index = absPath.lastIndexOf("/");
                        if index == () {
                            return absPath;
                        }
                        return absPath.substring(index + 1, absPath.length());
                    }

                    function wildcardToRegex(string pattern) returns string {
                        string regexPattern = "";
                        foreach string:Char c in pattern {
                            if c == "*" {
                                regexPattern += ".*";
                            } else if "\\\\^$.|?+()[]{}".includes(c) {
                                regexPattern += "\\\\" + c;
                            } else {
                                regexPattern += c;
                            }
                        }
                        return regexPattern;
                    }

                    function filesInPath(string path, boolean includeFiles, boolean includeDirs)
                            returns FileData[]|error {
                        string basePath = path;
                        string? pattern = ();
                        if path.includes("*") {
                            int? index = path.lastIndexOf("/");
                            if index == () {
                                basePath = ".";
                                pattern = wildcardToRegex(path);
                            } else {
                                basePath = path.substring(0, index);
                                pattern = wildcardToRegex(path.substring(index + 1, path.length()));
                            }
                        } else {
                            file:MetaData metaData = check file:getMetaData(path);
                            if !metaData.dir {
                                return includeFiles
                                    ? [{fileName: getFileName(metaData.absPath), fullName: metaData.absPath}]
                                    : [];
                            }
                        }
                        file:MetaData[] entries = check file:readDir(basePath);
                        FileData[] result = [];
                        foreach file:MetaData entry in entries {
                            if entry.dir ? !includeDirs : !includeFiles {
                                continue;
                            }
                            string fileName = getFileName(entry.absPath);
                            if pattern == () {
                                result.push({fileName: fileName, fullName: entry.absPath});
                            } else if regex:matches(fileName, pattern) {
                                result.push({fileName: fileName, fullName: entry.absPath});
                            }
                        }
                        return result;
                    }
                    """

    ),
    PARSE_DELIMITED_DATA(
            "parseDelimitedData",
            """
                    function splitBySeparator(string text, string separator) returns string[] {
                        string[] parts = [];
                        int startIndex = 0;
                        int? separatorIndex = text.indexOf(separator, startIndex);
                        while separatorIndex is int {
                            parts.push(text.substring(startIndex, separatorIndex));
                            startIndex = separatorIndex + separator.length();
                            separatorIndex = text.indexOf(separator, startIndex);
                        }
                        parts.push(text.substring(startIndex));
                        return parts;
                    }

                    // TIBCO's "New Line" separator also accepts Windows line endings, so a trailing carriage return is
                    // dropped from each line.
                    function dataFormatLines(string content, string lineSeparator, int skipHeaderCharacters)
                            returns string[] {
                        string text = skipHeaderCharacters >= content.length() ? ""
                            : content.substring(skipHeaderCharacters);
                        if text.endsWith(lineSeparator) {
                            text = text.substring(0, text.length() - lineSeparator.length());
                        }
                        if text == "" {
                            return [];
                        }
                        return from string line in splitBySeparator(text, lineSeparator)
                            select lineSeparator == "\\n" && line.endsWith("\\r")
                                ? line.substring(0, line.length() - 1) : line;
                    }

                    function hasMoreDataFormatRecords(string[] lines, int fromIndex, boolean skipBlankLines)
                            returns boolean {
                        foreach int index in fromIndex ..< lines.length() {
                            if lines[index].trim() != "" {
                                return true;
                            }
                            if !skipBlankLines {
                                return false;
                            }
                        }
                        return false;
                    }

                    // Mirrors TIBCO Parse Data: startRecord is a 1-based line number, a negative noOfRecords
                    // reads every record, and a blank line ends the input unless blank lines are skipped.
                    // Field elements share the row's namespace only when the schema qualifies local elements.
                    function parseDelimitedData(string content, string columnSeparator, string lineSeparator,
                            string namespace, string rowName, string[] fieldNames, boolean qualifiedFields,
                            int startRecord, int noOfRecords, int skipHeaderCharacters, boolean skipBlankLines)
                            returns xml|error {
                        string[] lines = dataFormatLines(content, lineSeparator, skipHeaderCharacters);
                        string prefix = namespace == "" ? "" : "{" + namespace + "}";
                        string fieldPrefix = qualifiedFields ? prefix : "";
                        // A prefixed declaration keeps unqualified fields out of the row's namespace when serialized,
                        // since a default-namespace declaration would be inherited by them.
                        map<string> rowAttributes = namespace == "" ? {}
                            : {"{http://www.w3.org/2000/xmlns/}ns0": namespace};
                        xml rows = xml ``;
                        int recordCount = 0;
                        int lineIndex = startRecord < 1 ? 0 : startRecord - 1;
                        boolean reachedBlankLine = false;
                        while lineIndex < lines.length() && (noOfRecords < 0 || recordCount < noOfRecords) {
                            string line = lines[lineIndex];
                            lineIndex += 1;
                            if line.trim() == "" {
                                if skipBlankLines {
                                    continue;
                                }
                                reachedBlankLine = true;
                                break;
                            }
                            string[] values = splitBySeparator(line, columnSeparator);
                            if values.length() > fieldNames.length() {
                                return error(string `BadDataFormatException: line ${lineIndex} has ${values.length()} `
                                    + string `fields, expected at most ${fieldNames.length()}`);
                            }
                            xml fields = xml ``;
                            foreach int index in 0 ..< fieldNames.length() {
                                string value = index < values.length() ? values[index] : "";
                                fields += xml:createElement(fieldPrefix + fieldNames[index], {}, xml:createText(value));
                            }
                            rows += xml:createElement(prefix + rowName, rowAttributes, fields);
                            recordCount += 1;
                        }
                        boolean done = reachedBlankLine || !hasMoreDataFormatRecords(lines, lineIndex, skipBlankLines);
                        return xml `<root><Rows>${rows}</Rows><done>${done}</done></root>`;
                    }
                    """),
    RENDER_DELIMITED_DATA(
            "renderDelimitedData",
            """
                    // The rows come out of an XSLT mapping, so they are matched by local name regardless of
                    // the namespace the mapping put them in.
                    function renderDelimitedData(xml input, string rowName, string columnSeparator,
                            string lineSeparator, string[] fieldNames) returns string {
                        string[] lines = from xml:Element row in (input/**/<*>).elements()
                            where localXmlName(row) == rowName
                            select string:'join(columnSeparator, ...from string fieldName in fieldNames
                                    select dataFormatFieldValue(row, fieldName));
                        return string:'join(lineSeparator, ...lines);
                    }

                    function dataFormatFieldValue(xml:Element row, string fieldName) returns string {
                        foreach xml:Element child in row.children().elements() {
                            if localXmlName(child) == fieldName {
                                return child.data();
                            }
                        }
                        return "";
                    }

                    function localXmlName(xml:Element element) returns string {
                        string name = element.getName();
                        int? namespaceEnd = name.lastIndexOf("}");
                        return namespaceEnd is int ? name.substring(namespaceEnd + 1) : name;
                    }
                    """),
    SET_SHARED_VARIABLE(
            "setSharedVariable",
            """
                    function setSharedVariable(Context cx, string varName, xml value) {
                        SharedVariableContext varContext = cx.sharedVariables.get(varName);
                        function(xml) setter = varContext.setter;
                        setter(value);
                    }
                    """),
    GET_SHARED_VARIABLE(
            "getSharedVariable",
            """
                    function getSharedVariable(Context cx, string varName) returns xml{
                        SharedVariableContext varContext = cx.sharedVariables.get(varName);
                        function() returns xml getter = varContext.getter;
                        return getter();
                    }
                    """),
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
                    function renderJSONAsXML(json value, string? namespace, string? typeName) returns xml|error {
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

                        string rep = typeName == () ? body.toString() :
                            string `<${typeName}>${body.toString()}</${typeName}>`;
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
    ),
    PSG_LOG(
            "psgLog",
            """
                    // TIBCO's `bw.psglog.Log` is a custom (non-stock) logging activity, so this mapping
                    // (Level -> Ballerina log severity) is inferred from observed usage in the source project,
                    // not a spec. Review and adjust as needed.
                    function psgLog(string level, string targetSystem, xml message, xml<xml:Element> keyValuePairs) {
                        xmlns "http://www.tibco.com/PSGLogActivities" as psglog;
                        log:KeyValues keyValues = {};
                        foreach xml:Element pair in keyValuePairs {
                            string paramKey = (pair/**/<psglog:key>/*).toString().trim();
                            string paramValue = (pair/**/<psglog:value>/*).toString().trim();
                            keyValues[paramKey] = paramValue;
                        }
                        keyValues["targetSystem"] = targetSystem;
                        match level {
                            "Warning" => {
                                log:printWarn(message.toString(), keyValues = keyValues);
                            }
                            "Error" => {
                                log:printError(message.toString(), keyValues = keyValues);
                            }
                            "Debug" => {
                                log:printDebug(message.toString(), keyValues = keyValues);
                            }
                            _ => {
                                log:printInfo(message.toString(), keyValues = keyValues);
                            }
                        }
                    }
                    """
    ),
    PSG_SET_AND_LOG(
            "psgSetAndLog",
            """
                    // TIBCO's `bw.psglog.SetAndLog` is a custom (non-stock) logging activity, so this mapping
                    // (Level -> Ballerina log severity) is inferred from observed usage in the source project,
                    // not a spec. Review and adjust as needed.
                    function psgSetAndLog(string level, string targetSystem, xml message, string sessionId,
                            string correlationId, string trackingId, string sender, string serviceScope) {
                        log:KeyValues keyValues = {
                            targetSystem: targetSystem,
                            sessionId: sessionId,
                            correlationId: correlationId,
                            trackingId: trackingId,
                            sender: sender,
                            serviceScope: serviceScope
                        };
                        match level {
                            "Warning" => {
                                log:printWarn(message.toString(), keyValues = keyValues);
                            }
                            "Error" => {
                                log:printError(message.toString(), keyValues = keyValues);
                            }
                            "Debug" => {
                                log:printDebug(message.toString(), keyValues = keyValues);
                            }
                            _ => {
                                log:printInfo(message.toString(), keyValues = keyValues);
                            }
                        }
                    }
                    """
    ),
    PSG_EXCEPTION_LOG(
            "psgExceptionLog",
            """
                    // TIBCO's `bw.psglog.ExceptionLog` reads fault details (errorCode/errorMessage/processStack/
                    // stackTrace) off the active fault variable. There is no live Ballerina `error` in scope at
                    // this point, so one is synthesized here purely to carry these fields into `log:printError`.
                    function psgExceptionLog(string errorCode, string errorMessage, string processStack,
                            string stackTrace) {
                        error psgError = error(errorMessage, code = errorCode, processStack = processStack,
                                stackTrace = stackTrace);
                        // `stackTrace` is a reserved log:printError parameter name (error:StackFrame[]?), so the
                        // TIBCO stack-trace text is logged under `tibcoStackTrace` to avoid the collision.
                        log:printError(errorMessage, 'error = psgError, code = errorCode,
                                processStack = processStack, tibcoStackTrace = stackTrace);
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
