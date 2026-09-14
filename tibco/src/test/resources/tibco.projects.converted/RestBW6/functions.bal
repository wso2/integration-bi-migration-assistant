import ballerina/data.jsondata;
import ballerina/data.xmldata;
import ballerina/http;
import ballerina/xslt;

function activityExtension(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com/test/api" version="2.0">
    <xsl:template name="RenderOutput-input" match="/">
        <tns:TestResponse>
            <tns:response>Hello world</tns:response>
        </tns:TestResponse>
    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    // WARNING: assuming single element
    record {|
        string response;
    |} var3 = check xmldata:parseAsType(var2);
    string var4 = var3.toJsonString();
    xml var5 = xml `<jsonString>${var4}</jsonString>`;
    xml var6 = xml `<root>${var5}</root>`;
    addToContext(cx, "RenderOutput", var6);
}

function activityExtension_2(Context cx) returns error? {
    xml var0 = getFromContext(cx, "RenderOutput");
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns1="http://tns.tibco.com/bw/activity/sendhttpresponse/xsd/input+3847aa9b-8275-4b15-9ea8-812816768fa4+ResponseActivityInput" version="2.0">
    <xsl:template name="SendHTTPResponse-input" match="/">
        <tns1:ResponseActivityInput>
            <asciiContent>
                <xsl:value-of select="/root/jsonString"/>
            </asciiContent>
            <Headers>
                <Content-Type>
                    <xsl:value-of select="'application/json'"/>
                </Content-Type>
            </Headers>
        </tns1:ResponseActivityInput>
    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    // FIXME ignoring headers others than content type
    string var3 = (var2/**/<Content\-Type>/*).toString();
    string var4 = (var2/**/<asciiContent>/*).toString();
    xml var5 = (var2/**/<Headers>/*);
    map<string> var6 = parseHeaders(var5);
    match var3 {
        "application/json" => {
            map<json> jsonRepr = check jsondata:parseString(var4);
            setJSONResponse(cx, jsonRepr, var6);
        }
        "application/xml" => {
            xml xmlRepr = xml `${var4}`;
            setXMLResponse(cx, xmlRepr, var6);
        }
        _ => {
            setTextResponse(cx, var4, var6);
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

function initContext(map<xml> initVariables = {},
        map<SharedVariableContext> jobSharedVariables = {})
            returns Context {
    map<SharedVariableContext> sharedVariables = {};

    foreach var key in jobSharedVariables.keys() {
        sharedVariables[key] = jobSharedVariables.get(key);
    }
    return {variables: initVariables, result: xml `<root/>`, sharedVariables};
}

function responseFromContext(Context cx) returns http:Response {
    http:Response httpRes = new;
    Response? res = cx.response;
    if res is JSONResponse {
        httpRes.setJsonPayload(res.payload);
    } else if res is XMLResponse {
        httpRes.setXmlPayload(res.payload);
    } else if res is TextResponse {
        httpRes.setTextPayload(res.payload);
    } else {
        httpRes.setXmlPayload(cx.result);
    }

    if res != () {
        foreach var header in res.headers.entries() {
            httpRes.setHeader(header[0], header[1]);
        }
    }
    return httpRes;
}

function setJSONResponse(Context cx, json payload, map<string> headers) {
    JSONResponse res = {
        kind: "JSONResponse",
        payload: payload.cloneReadOnly(),
        headers: headers.cloneReadOnly()
    };
    cx.response = res;
}

function setTextResponse(Context cx, string payload, map<string> headers) {
    TextResponse res = {
        kind: "TextResponse",
        payload: payload.cloneReadOnly(),
        headers: headers.cloneReadOnly()
    };
    cx.response = res;
}

function setXMLResponse(Context cx, xml payload, map<string> headers) {
    XMLResponse res = {
        kind: "XMLResponse",
        payload: payload.cloneReadOnly(),
        headers: headers.cloneReadOnly()
    };
    cx.response = res;
}
