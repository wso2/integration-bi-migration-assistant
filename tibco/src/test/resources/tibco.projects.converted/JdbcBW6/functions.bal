import ballerina/data.jsondata;
import ballerina/data.xmldata;
import ballerina/sql;
import ballerina/xslt;

function activityExtension(map<xml> context) returns xml|error {
    xml var0 = context.get("QueryRecords-input");
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://www.tibco.com/namespaces/tnt/plugins/jdbc+b75f079e-d363-4c28-9b66-44009f6eacf8+input" xmlns:tns1="http://www.example.com/namespaces/tns/1535845694732" version="2.0"><xsl:param name="Start"/><xsl:template name="JDBCQuery-input" match="/"><tns:jdbcQueryActivityInput><firstName><xsl:value-of select="$Start/root/FirstName"/></firstName><lastName><xsl:value-of select="$Start/root/LastName"/></lastName><age><xsl:value-of select="$Start/root/Age"/></age></tns:jdbcQueryActivityInput></xsl:template></xsl:stylesheet>`, context);
    string firstName = (var1/<firstName>/*).toString().trim();
    string lastName = (var1/<lastName>/*).toString().trim();
    string age = (var1/<age>/*).toString().trim();
    sql:ParameterizedQuery var2 = `select * from table where firstName like ${firstName} and lastName like ${lastName} and age < ${age}`;
    stream<map<anydata>, error|()> var3 = dbConnection->query(var2);
    xml var4 = xml ``;
    check from var each in var3
        do {
            xml var5 = check toXML(each);
            var4 = var4 + xml `<Record>${var5}</Record>`;
        };
    xml var6 = xml `<root>${var4}</root>`;
    addToContext(context, "QueryRecords", var6);
    return var6;
}

function receiveEvent(map<xml> context) returns xml|error {
    addToContext(context, "Start", context.get("$input"));
    return context.get("$input");
}

function reply(map<xml> context) returns xml|error {
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
                    </xsl:stylesheet>`, context);
    return var1;
}

function scopeActivityRunner(map<xml> cx) returns xml|error {
    xml result0 = check receiveEvent(cx);
    xml result1 = check activityExtension(cx);
    xml result2 = check reply(cx);
    return result2;
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
