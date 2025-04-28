import ballerina/http;
import ballerina/xslt;

http:Client creditcheckservice_Process_client = checkpanic new ("localhost:8080/CreditScore/creditscore");
const string client_404_NotFound = "Not Found";
public listener http:Listener creditcheckservice_Process_listener = new (8080, {host: "localhost"});

service /CreditScore on creditcheckservice_Process_listener {
    resource function post creditscore(Request|xml req) returns Response|http:NotFound|http:InternalServerError|client_404_NotFound {
        Request|error input = tryBindToRequest(req);
        if input is error {
            return <http:InternalServerError>{};
        }
        xml inputValXml = checkpanic toXML(input);
        xml extractedBody = inputValXml/*;
        xml inputXml = xml `<item>
    ${extractedBody}
</item>`;
        xml inputXmlMap = xml `<root>${inputXml}</root>`;
        map<xml> paramXML = {post: inputXmlMap};
        return start_creditcheckservice_Process(input, paramXML);
    }
}

service / on creditcheckservice_Process_listener {
    resource function get creditscore() returns Response|http:NotFound|http:InternalServerError {
        xml inputXmlMap = xml `<root></root>`;
        map<xml> paramXML = {get: inputXmlMap};
        return start_creditcheckservice_Process((), paramXML);
    }
}

function activityExtension(map<xml> context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://www.tibco.com/pe/WriteToLogActivitySchema" version="2.0"><xsl:template name="LogSuccess-input" match="/"><tns:ActivityInput><message><xsl:value-of select="'Invoation Successful'"/></message></tns:ActivityInput></xsl:template></xsl:stylesheet>`, context);
    LogParametersType var2 = convertToLogParametersType(var1);
    logWrapper(var2);
    return var1;
}

function activityExtension_5(map<xml> context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://www.tibco.com/pe/WriteToLogActivitySchema" version="2.0"><xsl:template name="LogFailure-input" match="/"><tns:ActivityInput><message><xsl:value-of select="'Invocation Failed'"/></message></tns:ActivityInput></xsl:template></xsl:stylesheet>`, context);
    LogParametersType var2 = convertToLogParametersType(var1);
    logWrapper(var2);
    return var1;
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

function faultHandler(map<xml> context) returns xml|error {
    return xml `<root></root>`;
}

function pick(map<xml> context) returns xml|error {
    return scope1_6ScopeFn(context);
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

function scope1_6ActivityRunner(map<xml> cx) returns xml|error {
    xml result0 = check extActivity(cx);
    xml result1 = check activityExtension(cx);
    xml result2 = check reply(cx);
    return result2;
}

function scope1_6FaultHandler(error err, map<xml> cx) returns xml {
    xml input = xml `<root></root>`;
    xml result0 = checkpanic activityExtension_5(cx);
    xml result1 = checkpanic reply_6(cx);
    return result1;
}

function scope1_6ScopeFn(map<xml> cx) returns xml {
    xml|error result = scope1_6ActivityRunner(cx);
    if result is error {
        return scope_5FaultHandler(result, cx);
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
        return scope_5FaultHandler(result, cx);
    }
    return result;
}

function scope_5ActivityRunner(map<xml> cx) returns xml|error {
    xml result0 = check pick(cx);
    return result0;
}

function scope_5FaultHandler(error err, map<xml> cx) returns xml {
    panic err;
}

function scope_5ScopeFn(xml input, map<xml> params) returns xml {
    map<xml> context = {...params};
    addToContext(context, "$input", input);
    xml|error result = scope_5ActivityRunner(context);
    if result is error {
        return scope_5FaultHandler(result, context);
    }
    return result;
}

function start_creditcheckservice_Process(()|Request input, map<xml> params = {}) returns Response {
    xml inputXML = input is map<anydata> ? checkpanic toXML(input) : xml ``;
    xml xmlResult = scope_5ScopeFn(inputXML, params);
    Response result = convertToResponse(xmlResult);
    return result;
}
