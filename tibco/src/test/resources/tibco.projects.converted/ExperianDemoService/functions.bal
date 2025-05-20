import ballerina/data.jsondata;
import ballerina/data.xmldata;
import ballerina/sql;
import ballerina/xslt;

function activityExtension(map<xml> context) returns xml|error {
    xml var0 = context.get("SendHTTPResponse-input");
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns1="http://tns.tibco.com/bw/activity/sendhttpresponse/xsd/input+3847aa9b-8275-4b15-9ea8-812816768fa4+ResponseActivityInput" version="2.0"><xsl:param name="RenderJSON"/><xsl:template name="SendHTTPResponse-input" match="/"><tns1:ResponseActivityInput><asciiContent><xsl:value-of select="$RenderJSON/root/jsonString"/></asciiContent><Headers><Content-Type><xsl:value-of select="'application/json'"/></Content-Type></Headers></tns1:ResponseActivityInput></xsl:template></xsl:stylesheet>`, context);
    xml var2 = xml `<root>${var1}</root>`;
    return var2;
}

function activityExtension_2(map<xml> context) returns xml|error {
    xml var0 = context.get("JDBCQuery-input");
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns2="http://www.tibco.com/namespaces/tnt/plugins/jdbc+1d5225ab-4bc8-4898-8f74-01e4317c3e29+input" xmlns:tns="http://tns.tibco.com/bw/activity/jsonRender/xsd/input/55832ae5-2a37-4b37-8392-a64537f49367" version="2.0"><xsl:param name="ParseJSON"/><xsl:template name="JDBCQuery-input" match="/"><tns2:jdbcQueryActivityInput><ssn><xsl:value-of select="$ParseJSON/root/tns:ssn"/></ssn></tns2:jdbcQueryActivityInput></xsl:template></xsl:stylesheet>`, context);
    string ssn = (var1/<ssn>/*).toString().trim();
    sql:ParameterizedQuery var2 = `SELECT *
  FROM public.creditscore where ssn like ${ssn}
`;
    stream<map<anydata>, error|()> var3 = experianservice_module_JDBCConnectionResource->query(var2);
    xml var4 = xml ``;
    check from var each in var3
        do {
            xml var5 = check toXML(each);
            var4 = var4 + var5;
        };
    xml var6 = xml `<root>${var4}</root>`;
    addToContext(context, "JDBCQuery", var6);
    return var6;
}

function activityExtension_3(map<xml> context) returns xml|error {
    xml var0 = context.get("ParseJSON-input");
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="activity.jsonParser.input+f396d921-0bc7-459d-ae81-0eb1a0f94723+ActivityInputType" version="2.0"><xsl:param name="HTTPReceiver"/><xsl:template name="ParseJSON-input" match="/"><tns:ActivityInputClass><jsonString><xsl:value-of select="$HTTPReceiver/root/PostData"/></jsonString></tns:ActivityInputClass></xsl:template></xsl:stylesheet>`, context);
    xml var2 = check renderJsonAsInputElementXML(var1);
    addToContext(context, "ParseJSON", var2);
    return var2;
}

function activityExtension_4(map<xml> context) returns xml|error {
    xml var0 = context.get("RenderJSON-input");
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://tns.tibco.com/bw/json/1535671685533" version="2.0"><xsl:param name="JDBCQuery"/><xsl:template name="RenderJSON-input" match="/"><tns:ExperianResponseSchemaElement><xsl:if test="$JDBCQuery/root/Record[1]/ficoscore"><tns:fiCOScore><xsl:value-of select="$JDBCQuery/root/Record[1]/ficoscore"/></tns:fiCOScore></xsl:if><xsl:if test="$JDBCQuery/root/Record[1]/rating"><tns:rating><xsl:value-of select="$JDBCQuery/root/Record[1]/rating"/></tns:rating></xsl:if><xsl:if test="$JDBCQuery/root/Record[1]/numofpulls"><tns:noOfInquiries><xsl:value-of select="$JDBCQuery/root/Record[1]/numofpulls"/></tns:noOfInquiries></xsl:if></tns:ExperianResponseSchemaElement></xsl:template></xsl:stylesheet>`, context);
    xml var2 = renderJson(var1);
    addToContext(context, "RenderJSON", var2);
    return var2;
}

function receiveEvent(map<xml> context) returns xml|error {
    addToContext(context, "HTTPReceiver", context.get("$input"));
    return context.get("$input");
}

function scopeActivityRunner(map<xml> cx) returns xml|error {
    xml result0 = check receiveEvent(cx);
    xml result1 = check activityExtension_3(cx);
    xml result2 = check activityExtension_2(cx);
    xml result3 = check activityExtension_4(cx);
    xml result4 = check activityExtension(cx);
    return result4;
}

function scopeFaultHandler(error err, map<xml> cx) returns xml {
    panic err;
}

function scopeScopeFn(xml input, map<xml> params) returns xml {
    map<xml> context = {...params};
    addToContext(context, "$input", input);
    xml|error result = scopeActivityRunner(context);
    if result is error {
        return scopeFaultHandler(result, context);
    }
    return result;
}

function start_experianservice_module_Process(InputElement input, map<xml> params = {}) returns ExperianResponseSchemaElement {
    xml inputXML = input is map<anydata> ? checkpanic toXML(input) : xml ``;
    xml xmlResult = scopeScopeFn(inputXML, params);
    ExperianResponseSchemaElement result = convertToExperianResponseSchemaElement(xmlResult);
    return result;
}

function convertToExperianResponseSchemaElement(xml input) returns ExperianResponseSchemaElement {
    return checkpanic xmldata:parseAsType(input);
}

function toXML(map<anydata> data) returns error|xml {
    return xmldata:toXml(data);
}

function tryBindToInputElement(xml|json input) returns InputElement|error {
    return input is xml ? xmldata:parseAsType(input) : jsondata:parseAsType(input);
}

function addToContext(map<xml> context, string varName, xml value) {
    xml children = value/*;
    xml transformed = xml `<root>${children}</root>`;
    context[varName] = transformed;
}

function renderJson(xml value) returns xml {
    json jsonValue = xmlToJson(value);
    return xml `<root><jsonString>${jsonValue.toJsonString()}</jsonString></root>`;
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

function renderJsonAsInputElementXML(xml value) returns xml|error {
    string jsonString = (value/<jsonString>).data();
    map<json> jsonValue = check jsondata:parseString(jsonString);
    string? namespace = (InputElement).@xmldata:Namespace["uri"];
    return renderJSONAsXML(jsonValue, namespace, "InputElement");
}
