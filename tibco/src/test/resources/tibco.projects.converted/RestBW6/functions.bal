import ballerina/data.jsondata;
import ballerina/data.xmldata;
import ballerina/xslt;

function activityExtension(map<xml> context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com/test/api" version="2.0">
    <xsl:template name="RenderOutput-input" match="/">
        <tns:TestResponse>
            <tns:response>Hello world</tns:response>
        </tns:TestResponse>
    </xsl:template>
</xsl:stylesheet>`, context);
    //WARNING: assuming single element
    TestResponse var2 = check xmldata:parseAsType(var1);
    string var3 = var2.toJsonString();
    xml var4 = xml `<jsonString>${var3}</jsonString>`;
    addToContext(context, "RenderOutput", var4);
    return var4;
}

function activityExtension_2(map<xml> context) returns xml|error {
    xml var0 = context.get("RenderOutput");
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
</xsl:stylesheet>`, context);
    xml var2 = xml `<root>${var1}</root>`;
    return var2;
}

function pick(map<xml> context) returns xml|error {
    return scope1ScopeFn(context);
}

function scope1ActivityRunner(map<xml> cx) returns xml|error {
    xml result0 = check activityExtension(cx);
    xml result1 = check activityExtension_2(cx);
    return result1;
}

function scope1FaultHandler(error err, map<xml> cx) returns xml {
    panic err;
}

function scope1ScopeFn(map<xml> cx) returns xml {
    xml|error result = scope1ActivityRunner(cx);
    if result is error {
        return scope1FaultHandler(result, cx);
    }
    return result;
}

function scopeActivityRunner(map<xml> cx) returns xml|error {
    xml result0 = check pick(cx);
    return result0;
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

function start_test_api_MainProcess(TestRequest input, map<xml> params = {}) returns TestResponse {
    xml inputXML = input is map<anydata> ? checkpanic toXML(input) : xml ``;
    xml xmlResult = scopeScopeFn(inputXML, params);
    TestResponse result = convertToTestResponse(xmlResult);
    return result;
}

function convertToTestResponse(xml input) returns TestResponse {
    return checkpanic xmldata:parseAsType(input);
}

function toXML(map<anydata> data) returns error|xml {
    return xmldata:toXml(data);
}

function tryBindToTestRequest(xml|json input) returns TestRequest|error {
    return input is xml ? xmldata:parseAsType(input) : jsondata:parseAsType(input);
}

function addToContext(map<xml> context, string varName, xml value) {
    xml children = value/*;
    xml transformed = xml `<root>${children}</root>`;
    context[varName] = transformed;
}
