import ballerina/data.jsondata;
import ballerina/data.xmldata;
import ballerina/http;

listener http:Listener GeneralConnection_sharedhttp = checkpanic new (9090, {host: "localhost"});

function toXML(map<anydata> data) returns error|xml {
    return xmldata:toXml(data);
}

function addToContext(map<xml> context, string varName, xml value) {
    xml children = value/*;
    xml transformed = xml `<root>${children}</root>`;
    context[varName] = transformed;
}

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

type XMLElementParseResult record {|
    string? namespace;
    string name;
|};

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

function renderJsonAsFooXML(xml value) returns xml|error {
    string jsonString = (value/<jsonString>).data();
    map<json> jsonValue = check jsondata:parseString(jsonString);
    string? namespace = (Foo).@xmldata:Namespace["uri"];
    return renderJSONAsXML(jsonValue, namespace, "Foo");
}
