import ballerina/data.jsondata;
import ballerina/data.xmldata;
import ballerina/log;
import ballerinax/java.jdbc;

jdbc:Client creditcheckservice_JDBCConnectionResource = checkpanic new (string `${dbURL} jdbc:postgresql://awagle:5432/bookstore`, "bwuser", "#!yk2zPUfipGX2vB+1XNJha9KX6eLVDmcZ");
configurable string dbURL = ?;

function convertToElement(xml input) returns Element {
    return checkpanic xmldata:parseAsType(input);
}

function convertToLogParametersType(xml input) returns LogParametersType {
    return checkpanic xmldata:parseAsType(input);
}

function convertToQueryData0(xml input) returns QueryData0 {
    return checkpanic xmldata:parseAsType(input);
}

function convertToQueryData1(xml input) returns QueryData1 {
    return checkpanic xmldata:parseAsType(input);
}

function convertToResponse(xml input) returns Response {
    return checkpanic xmldata:parseAsType(input);
}

function convertToResponse(xml input) returns Response {
    return checkpanic xmldata:parseAsType(input);
}

function toXML(map<anydata> data) returns error|xml {
    return xmldata:toXml(data);
}

function tryBindToRequest(xml|json input) returns Request|error {
    return input is xml ? xmldata:parseAsType(input) : jsondata:parseAsType(input);
}

function addToContext(map<xml> context, string varName, xml value) {
    xml children = value/*;
    xml transformed = xml `<root>${children}</root>`;
    context[varName] = transformed;
}

function logWrapper(LogParametersType input) {
    foreach var body in input {
        match (body) {
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
}

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

type XMLElementParseResult record {|
string? namespace;
string name;
|};

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

function test(xml input, string xpath) returns boolean {
    // TODO: support XPath
    return false;
}
