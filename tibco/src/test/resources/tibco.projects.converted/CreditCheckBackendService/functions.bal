import ballerina/data.jsondata;
import ballerina/data.xmldata;
import ballerina/log;
import ballerina/sql;
import ballerina/xslt;

function activityExtension(map<xml> context) returns xml|error {
    xml var0 = context.get("LogSuccess-input");
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://www.tibco.com/pe/WriteToLogActivitySchema" version="2.0"><xsl:template name="LogSuccess-input" match="/"><tns:ActivityInput><message><xsl:value-of select="'Invoation Successful'"/></message></tns:ActivityInput></xsl:template></xsl:stylesheet>`, context);
    LogParametersType var2 = convertToLogParametersType(var1);
    logWrapper(var2);
    return var1;
}

function activityExtension_5(map<xml> context) returns xml|error {
    xml var0 = context.get("LogFailure-input");
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://www.tibco.com/pe/WriteToLogActivitySchema" version="2.0"><xsl:template name="LogFailure-input" match="/"><tns:ActivityInput><message><xsl:value-of select="'Invocation Failed'"/></message></tns:ActivityInput></xsl:template></xsl:stylesheet>`, context);
    LogParametersType var2 = convertToLogParametersType(var1);
    logWrapper(var2);
    return var1;
}

function catchAll(map<xml> context) returns xml|error {
    return scope2ScopeFn(context);
}

function extActivity(map<xml> context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns2="/T1535753828744Converted/JsonSchema" xmlns:tns3="http://www.example.com/namespaces/tns/1535845694732" version="2.0">
    <xsl:param name="post"/>
    <xsl:template name="LookupDatabase-input" match="/">
        <tns3:Element>
            <tns3:ssn>
                <xsl:value-of select="$post/root/item/tns2:Request/tns2:SSN"/>
            </tns3:ssn>
        </tns3:Element>
    </xsl:template>
</xsl:stylesheet>`, context);
    xml var2 = check xslt:transform(var1, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns2="/T1535753828744Converted/JsonSchema" xmlns:tns3="http://www.example.com/namespaces/tns/1535845694732" version="2.0">
    <xsl:param name="post"/>
    <xsl:template name="LookupDatabase-input" match="/">
        <tns3:Element>
            <tns3:ssn>
                <xsl:value-of select="$post/root/item/tns2:SSN"/>
            </tns3:ssn>
        </tns3:Element>
    </xsl:template>
</xsl:stylesheet>`, context);
    xml var3 = transform(var2);
    xml var4 = check toXML(check trap start_creditcheckservice_LookupDatabase(convertToElement(var3)));
    addToContext(context, "LookupDatabase", var4);
    return var4;
}

function pick(map<xml> context) returns xml|error {
    return scope1ScopeFn(context);
}

function reply(map<xml> context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns1="http://xmlns.example.com/20180831151624PLT" xmlns:tns2="/T1535753828744Converted/JsonSchema" version="2.0">
    <xsl:param name="LookupDatabase"/>
    <xsl:template name="postOut-input" match="/">
        <tns2:Response>
            <xsl:if test="$LookupDatabase/root/tns2:FICOScore">
                <tns2:FICOScore>
                    <xsl:value-of select="$LookupDatabase/root/tns2:FICOScore"/>
                </tns2:FICOScore>
            </xsl:if>
            <xsl:if test="$LookupDatabase/root/tns2:Rating">
                <tns2:Rating>
                    <xsl:value-of select="$LookupDatabase/root/tns2:Rating"/>
                </tns2:Rating>
            </xsl:if>
            <xsl:if test="$LookupDatabase/root/tns2:NoOfInquiries">
                <tns2:NoOfInquiries>
                    <xsl:value-of select="$LookupDatabase/root/tns2:NoOfInquiries"/>
                </tns2:NoOfInquiries>
            </xsl:if>
        </tns2:Response>
    </xsl:template>
</xsl:stylesheet>`, context);
    xml var2 = xml `<root>${var1}</root>`;
    xml var3 = check xslt:transform(var2, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns1="http://xmlns.example.com/20180831151624PLT" xmlns:tns2="/T1535753828744Converted/JsonSchema" version="2.0"><xsl:param name="LookupDatabase"/><xsl:template name="postOut-input" match="/"><tns1:postResponse><item><tns2:Response><xsl:if test="$LookupDatabase/root/tns2:FICOScore"><tns2:FICOScore><xsl:value-of select="$LookupDatabase/root/tns2:FICOScore"/></tns2:FICOScore></xsl:if><xsl:if test="$LookupDatabase/root/tns2:Rating"><tns2:Rating><xsl:value-of select="$LookupDatabase/root/tns2:Rating"/></tns2:Rating></xsl:if><xsl:if test="$LookupDatabase/root/tns2:NoOfInquiries"><tns2:NoOfInquiries><xsl:value-of select="$LookupDatabase/root/tns2:NoOfInquiries"/></tns2:NoOfInquiries></xsl:if></tns2:Response></item></tns1:postResponse></xsl:template></xsl:stylesheet>`, context);
    return var3;
}

function reply_6(map<xml> context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns1="http://xmlns.example.com/20180831151624PLT" xmlns:tns="http://tns.tibco.com/bw/REST" version="2.0"><xsl:template name="Reply-input" match="/"><tns1:post4XXFaultMessage><clientError><tns:client4XXError><statusCode><xsl:value-of select="404"/></statusCode></tns:client4XXError></clientError></tns1:post4XXFaultMessage></xsl:template></xsl:stylesheet>`, context);
    xml var2 = check xslt:transform(var1, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns1="http://xmlns.example.com/20180831151624PLT" xmlns:tns="http://tns.tibco.com/bw/REST" version="2.0">
    <xsl:template name="Reply-input" match="/">
        <tns:client4XXError>
            <statusCode>
                <xsl:value-of select="404"/>
            </statusCode>
        </tns:client4XXError>
    </xsl:template>
</xsl:stylesheet>`, context);
    xml var3 = xml `<root>${var2}</root>`;
    return var3;
}

function scope1ActivityRunner(map<xml> cx) returns xml|error {
    xml result0 = check catchAll(cx);
    xml result1 = check extActivity(cx);
    xml result2 = check activityExtension(cx);
    xml result3 = check reply(cx);
    return result3;
}

function scope1FaultHandler(error err, map<xml> cx) returns xml {
    xml result0 = checkpanic catchAll(cx);
    return result0;
}

function scope1ScopeFn(map<xml> cx) returns xml {
    xml|error result = scope1ActivityRunner(cx);
    if result is error {
        return scope1FaultHandler(result, cx);
    }
    return result;
}

function scope2ActivityRunner(map<xml> cx) returns xml|error {
    xml result0 = check activityExtension_5(cx);
    xml result1 = check reply_6(cx);
    return result1;
}

function scope2FaultHandler(error err, map<xml> cx) returns xml {
    panic err;
}

function scope2ScopeFn(map<xml> cx) returns xml {
    xml|error result = scope2ActivityRunner(cx);
    if result is error {
        return scope2FaultHandler(result, cx);
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

function start_creditcheckservice_Process(()|Request input, map<xml> params = {}) returns Response {
    xml inputXML = input is map<anydata> ? checkpanic toXML(input) : xml ``;
    xml xmlResult = scopeScopeFn(inputXML, params);
    Response result = convertToResponse(xmlResult);
    return result;
}

function activityExtension_10(map<xml> context) returns xml|error {
    xml var0 = context.get("UpdatePulls-input");
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns3="http://www.tibco.com/namespaces/tnt/plugins/jdbc+21902290-4882-46a2-8795-b85989c9d7c0+input" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:tns1="http://www.example.com/namespaces/tns/1535845694732" version="2.0">
    <xsl:param name="Start"/>
    <xsl:param name="QueryRecords"/>
    <xsl:template name="JDBCUpdate-input" match="/">
        <tns3:jdbcUpdateActivityInput>
            <noOfPulls>
                <xsl:value-of select="xsd:int($QueryRecords/root/Record[1]/numofpulls + 1)"/>
            </noOfPulls>
            <ssn>
                <xsl:value-of select="$Start/root/tns1:ssn"/>
            </ssn>
        </tns3:jdbcUpdateActivityInput>
    </xsl:template>
</xsl:stylesheet>`, context);
    string noOfPulls = (var1/<noOfPulls>/*).toString().trim();
    string ssn = (var1/<ssn>/*).toString().trim();
    sql:ParameterizedQuery var2 = `UPDATE creditscore
  SET numofpulls = ${noOfPulls}
  WHERE ssn like ${ssn}`;
    sql:ExecutionResult var3 = check creditcheckservice_JDBCConnectionResource->execute(var2);
    xml var4 = xml `<root></root>`;
    addToContext(context, "UpdatePulls", var4);
    return var4;
}

function activityExtension_8(map<xml> context) returns xml|error {
    xml var0 = context.get("End-input");
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns2="/T1535753828744Converted/JsonSchema" version="2.0">
    <xsl:param name="QueryRecords"/>
    <xsl:template name="End-input" match="/">
        <tns2:Response>
            <xsl:if test="$QueryRecords/root/Record[1]/ficoscore">
                <tns2:FICOScore>
                    <xsl:value-of select="$QueryRecords/root/Record[1]/ficoscore"/>
                </tns2:FICOScore>
            </xsl:if>
            <xsl:if test="$QueryRecords/root/Record[1]/rating">
                <tns2:Rating>
                    <xsl:value-of select="$QueryRecords/root/Record[1]/rating"/>
                </tns2:Rating>
            </xsl:if>
            <xsl:if test="$QueryRecords/root/Record[1]/numofpulls">
                <tns2:NoOfInquiries>
                    <xsl:value-of select="$QueryRecords/root/Record[1]/numofpulls"/>
                </tns2:NoOfInquiries>
            </xsl:if>
        </tns2:Response>
    </xsl:template>
</xsl:stylesheet>`, context);
    xml var2 = xml `<root>${var1}</root>`;
    return var2;
}

function activityExtension_9(map<xml> context) returns xml|error {
    xml var0 = context.get("QueryRecords-input");
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://www.tibco.com/namespaces/tnt/plugins/jdbc+b75f079e-d363-4c28-9b66-44009f6eacf8+input" xmlns:tns1="http://www.example.com/namespaces/tns/1535845694732" version="2.0"><xsl:param name="Start"/><xsl:template name="JDBCQuery-input" match="/"><tns:jdbcQueryActivityInput><ssn><xsl:value-of select="$Start/root/tns1:ssn"/></ssn></tns:jdbcQueryActivityInput></xsl:template></xsl:stylesheet>`, context);
    string ssn = (var1/<ssn>/*).toString().trim();
    sql:ParameterizedQuery var2 = `select * from public.creditscore where ssn like ${ssn}`;
    stream<map<anydata>, error|()> var3 = creditcheckservice_JDBCConnectionResource->query(var2);
    xml var4 = xml ``;
    check from var each in var3
        do {
            xml var5 = check toXML(each);
            var4 = var4 + var5;
        };
    xml var6 = xml `<root>${var4}</root>`;
    addToContext(context, "QueryRecords", var6);
    return var6;
}

function predicate_0(xml input, map<xml> cx) returns boolean {
    return xmldata:transform(input, `string-length(${cx.get("QueryRecords")}/Record[1]/rating)>0`);
}

function predicate_1(xml input, map<xml> cx) returns boolean {
    return !xmldata:transform(input, `string-length(${cx.get("QueryRecords")}/Record[1]/rating)>0`);
}

function receiveEvent(map<xml> context) returns xml|error {
    addToContext(context, "Start", context.get("$input"));
    return context.get("$input");
}

function scope_3ActivityRunner(map<xml> cx) returns xml|error {
    xml result0 = check receiveEvent(cx);
    xml result1 = check activityExtension_9(cx);
    xml result2;
    if predicate_1(result1, cx) {
        result2 = check throw(cx);
    } else {
        result2 = result1;
    }
    xml result3;
    if predicate_0(result1, cx) {
        result3 = check activityExtension_10(cx);
    } else {
        result3 = result2;
    }
    xml result4 = check activityExtension_8(cx);
    return result4;
}

function scope_3FaultHandler(error err, map<xml> cx) returns xml {
    panic err;
}

function scope_3ScopeFn(xml input, map<xml> params) returns xml {
    map<xml> context = {...params};
    addToContext(context, "$input", input);
    xml|error result = scope_3ActivityRunner(context);
    if result is error {
        return scope_3FaultHandler(result, context);
    }
    return result;
}

function start_creditcheckservice_LookupDatabase(Element input, map<xml> params = {}) returns Response {
    xml inputXML = input is map<anydata> ? checkpanic toXML(input) : xml ``;
    xml xmlResult = scope_3ScopeFn(inputXML, params);
    Response result = convertToResponse(xmlResult);
    return result;
}

function throw(map<xml> context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?><xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://schemas.tibco.com/bw/plugins/basic/6.0/Exceptions" version="2.0"><xsl:template name="Throw-input" match="/"><tns:DefaultFault/></xsl:template> </xsl:stylesheet>`, context);
    xml var2 = xml `<root>${var1}</root>`;
    error var3 = error("TODO: create error value");
    panic var3;
}

function convertToElement(xml input) returns Element {
    return checkpanic xmldata:parseAsType(input);
}

function convertToLogParametersType(xml input) returns LogParametersType {
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
