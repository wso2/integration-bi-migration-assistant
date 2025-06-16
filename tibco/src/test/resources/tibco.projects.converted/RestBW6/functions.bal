import ballerina/data.jsondata;
import ballerina/data.xmldata;
import ballerina/http;
import ballerina/xslt;

function activityExtension(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com/test/api" version="2.0">
    <xsl:template name="RenderOutput-input" match="/">
        <tns:TestResponse>
            <tns:response>Hello world</tns:response>
        </tns:TestResponse>
    </xsl:template>
</xsl:stylesheet>`, cx.variables);
    //WARNING: assuming single element
    TestResponse var2 = check xmldata:parseAsType(var1);
    string var3 = var2.toJsonString();
    xml var4 = xml `<jsonString>${var3}</jsonString>`;
    addToContext(cx, "RenderOutput", var4);
}

function activityExtension_2(Context cx) returns error? {
    xml var0 = getFromContext(cx, "RenderOutput");
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns1="http://tns.tibco.com/bw/activity/sendhttpresponse/xsd/input+3847aa9b-8275-4b15-9ea8-812816768fa4+ResponseActivityInput" version="2.0">
    <xsl:param name="JSONPayloadOut"/>
    <xsl:template name="SendHTTPResponse-input" match="/">
        <tns1:ResponseActivityInput>
            <asciiContent>
                <xsl:value-of select="$JSONPayloadOut/root/jsonString"/>
            </asciiContent>
            <Headers>
                <Content-Type>
                    <xsl:value-of select="'application/json'"/>
                </Content-Type>
            </Headers>
        </tns1:ResponseActivityInput>
    </xsl:template>
</xsl:stylesheet>`, cx.variables);
    //FIXME ignoring headers others than content type
    xmlns "http://tns.tibco.com/bw/activity/sendhttpresponse/xsd/input+3847aa9b-8275-4b15-9ea8-812816768fa4+ResponseActivityInput" as ns;
    string var2 = (var1/**/<ns:Content\-Type>/*).toString();
    string var3 = (var1/**/<ns:asciiContent>/*).toString();
    match var2 {
        "application/json" => {
            map<json> jsonRepr = check jsondata:parseString(var3);
            cx.result = jsonRepr;
        }
        "application/xml" => {
            xml xmlRepr = xml `${var3}`;
            cx.result = xmlRepr;
        }
        _ => {
            panic error("Unsupported content type: " + var2);
        }
    }
}

function pick(Context cx) returns error? {
    scope1ScopeFn(cx);
}

function scope1ActivityRunner(Context cx) returns error? {
    check activityExtension(cx);
    check activityExtension_2(cx);
}

function scope1FaultHandler(error err, Context cx) returns () {
    panic err;
}

function scope1ScopeFn(Context cx) returns () {
    error? result = scope1ActivityRunner(cx);
    if result is error {
        scope1FaultHandler(result, cx);
    }
}

function scopeActivityRunner(Context cx) returns error? {
    check pick(cx);
}

function scopeFaultHandler(error err, Context cx) returns () {
    panic err;
}

function scopeScopeFn(Context cx) returns () {
    error? result = scopeActivityRunner(cx);
    if result is error {
        scopeFaultHandler(result, cx);
    }
}

function start_test_api_MainProcess(Context params) returns () {
    scopeScopeFn(params);
}

function toXML(map<anydata> data) returns error|xml {
    return xmldata:toXml(data);
}

function tryBindToTestRequest(xml|json input) returns TestRequest|error {
    return input is xml ? xmldata:parseAsType(input) : jsondata:parseAsType(input);
}

function addToContext(Context context, string varName, xml value) {
    xml children = value/*;
    xml transformed = xml `<root>${children}</root>`;
    context.variables[varName] = transformed;
    context.result = value;
}

function getFromContext(Context context, string varName) returns xml {
    xml? value = context.variables[varName];
    if value == () {
        return xml `<root/>`;
    }
    return value;
}

function initContext(map<xml> initVariables = {}) returns Context {
    return {variables: initVariables, result: xml `<root/>`};
}

function responseFromContext(Context context) returns http:Response {
    http:Response response = new;
    anydata result = context.result;
    if result is xml {
        response.setXmlPayload(result);
    } else if result is json {
        response.setJsonPayload(result);
    } else {
        response.setTextPayload(result.toString());
    }
    return response;
}
