import ballerina/data.jsondata;
import ballerina/data.xmldata;
import ballerina/http;
import ballerina/sql;
import ballerina/xslt;

function activityExtension(Context cx) returns error? {
    xml var0 = getFromContext(cx, "QueryRecords-input");
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://www.tibco.com/namespaces/tnt/plugins/jdbc+b75f079e-d363-4c28-9b66-44009f6eacf8+input" xmlns:tns1="http://www.example.com/namespaces/tns/1535845694732" version="2.0"><xsl:param name="Start"/><xsl:template name="JDBCQuery-input" match="/"><tns:jdbcQueryActivityInput><firstName><xsl:value-of select="$Start/root/FirstName"/></firstName><lastName><xsl:value-of select="$Start/root/LastName"/></lastName><age><xsl:value-of select="$Start/root/Age"/></age></tns:jdbcQueryActivityInput></xsl:template></xsl:stylesheet>`, cx.variables);
    string firstName = (var1/<firstName>/*).toString().trim();
    string lastName = (var1/<lastName>/*).toString().trim();
    string age = (var1/<age>/*).toString().trim();
    sql:ParameterizedQuery var2 = `select * from table where firstName like ${firstName} and lastName like ${lastName} and age < ${age}`;
    stream<map<anydata>, error?> var3 = dbConnection->query(var2);
    xml var4 = xml ``;
    check from var each in var3
        do {
            xml var5 = check toXML(each);
            var4 = var4 + xml `<Record>${var5}</Record>`;
        };
    xml var6 = xml `<root>${var4}</root>`;
    addToContext(cx, "QueryRecords", var6);
}

function receiveEvent(Context cx) returns error? {
    addToContext(cx, "Start", getFromContext(cx, "$input"));
}

function reply(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
                    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                    xmlns:ns1="http://xmlns.example.com/test/api"
                    version="2.0">
                    <xsl:param name="QueryRecords"/>
                    <xsl:template name="reply-template" match="/">
                    <ns1:Response>
                    <ns1:Score><xsl:value-of select="$QueryRecords/root/resultSet/Record[1]/score"/></ns1:Score>
                    </ns1:Response>
                    </xsl:template>
                    </xsl:stylesheet>`, cx.variables);
    setXMLResponse(cx, var1, {});
}

function scopeActivityRunner(Context cx) returns error? {
    check receiveEvent(cx);
    check activityExtension(cx);
    check reply(cx);
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

function setXMLResponse(Context cx, xml payload, map<string> headers) {
    XMLResponse res = {
        kind: "XMLResponse",
        payload: payload.cloneReadOnly(),
        headers: headers.cloneReadOnly()
    };
    cx.response = res;
}
