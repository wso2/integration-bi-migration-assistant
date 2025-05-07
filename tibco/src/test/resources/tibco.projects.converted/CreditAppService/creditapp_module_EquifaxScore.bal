import ballerina/http;
import ballerina/xslt;

const string client_404_RecordNotFound = "Record Not Found";
http:Client creditapp_module_EquifaxScore_client = checkpanic new ("localhost:8081/y54cuadtcxtfstqs3rux2gfdaxppoqgc/creditscore");
public listener http:Listener creditapp_module_EquifaxScore_listener = new (8081, {host: "localhost"});

service /y54cuadtcxtfstqs3rux2gfdaxppoqgc on creditapp_module_EquifaxScore_listener {
    resource function post creditscore(GiveNewSchemaNameHere|xml req) returns SuccessSchema|http:NotFound|http:InternalServerError|client_404_RecordNotFound {
        GiveNewSchemaNameHere|error input = tryBindToGiveNewSchemaNameHere(req);
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
        return start_creditapp_module_EquifaxScore(input, paramXML);
    }
}

service / on creditapp_module_EquifaxScore_listener {
    resource function post creditscore(GiveNewSchemaNameHere|xml req) returns SuccessSchema|http:NotFound|http:InternalServerError {
        GiveNewSchemaNameHere|error input = tryBindToGiveNewSchemaNameHere(req);
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
        return start_creditapp_module_EquifaxScore(input, paramXML);
    }
}

function activityExtension_6(map<xml> context) returns xml|error {
    xml var0 = context.get("End-input");
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns3="/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema" version="2.0">
    <xsl:param name="post"/>
    <xsl:template name="End-input" match="/">
        <tns3:SuccessSchema>
            <xsl:if test="$post/root/item/tns3:FICOScore">
                <tns3:FICOScore>
                    <xsl:value-of select="$post/root/item/tns3:FICOScore"/>
                </tns3:FICOScore>
            </xsl:if>
            <xsl:if test="$post/root/item/tns3:NoOfInquiries">
                <tns3:NoOfInquiries>
                    <xsl:value-of select="$post/root/item/tns3:NoOfInquiries"/>
                </tns3:NoOfInquiries>
            </xsl:if>
            <xsl:if test="$post/root/item/tns3:Rating">
                <tns3:Rating>
                    <xsl:value-of select="$post/root/item/tns3:Rating"/>
                </tns3:Rating>
            </xsl:if>
        </tns3:SuccessSchema>
    </xsl:template>
</xsl:stylesheet>`, context);
    return var1;
}

function invoke(map<xml> context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com/20180827154353PLT" xmlns:tns1="http://tns.tibco.com/bw/REST" xmlns:tns3="/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema" version="2.0"><xsl:param name="Start"/><xsl:template name="post-input" match="/"><tns:postRequest1><item><tns3:GiveNewSchemaNameHere><xsl:if test="$Start/root/tns3:DOB"><tns3:DOB><xsl:value-of select="$Start/root/tns3:DOB"/></tns3:DOB></xsl:if><xsl:if test="$Start/root/tns3:FirstName"><tns3:FirstName><xsl:value-of select="$Start/root/tns3:FirstName"/></tns3:FirstName></xsl:if><xsl:if test="$Start/root/tns3:LastName"><tns3:LastName><xsl:value-of select="$Start/root/tns3:LastName"/></tns3:LastName></xsl:if><xsl:if test="$Start/root/tns3:SSN"><tns3:SSN><xsl:value-of select="$Start/root/tns3:SSN"/></tns3:SSN></xsl:if></tns3:GiveNewSchemaNameHere></item><httpHeaders><tns1:httpHeaders/></httpHeaders></tns:postRequest1></xsl:template></xsl:stylesheet>`, context);
    xml var2 = check xslt:transform(var1, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com/20180827154353PLT" xmlns:tns1="http://tns.tibco.com/bw/REST" xmlns:tns3="/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema" version="2.0">
    <xsl:param name="Start"/>
    <xsl:template name="post-input" match="/">
        <tns3:GiveNewSchemaNameHere>
            <xsl:if test="$Start/root/tns3:DOB">
                <tns3:DOB>
                    <xsl:value-of select="$Start/root/tns3:DOB"/>
                </tns3:DOB>
            </xsl:if>
            <xsl:if test="$Start/root/tns3:FirstName">
                <tns3:FirstName>
                    <xsl:value-of select="$Start/root/tns3:FirstName"/>
                </tns3:FirstName>
            </xsl:if>
            <xsl:if test="$Start/root/tns3:LastName">
                <tns3:LastName>
                    <xsl:value-of select="$Start/root/tns3:LastName"/>
                </tns3:LastName>
            </xsl:if>
            <xsl:if test="$Start/root/tns3:SSN">
                <tns3:SSN>
                    <xsl:value-of select="$Start/root/tns3:SSN"/>
                </tns3:SSN>
            </xsl:if>
        </tns3:GiveNewSchemaNameHere>
    </xsl:template>
</xsl:stylesheet>`, context);
    xml var3 = check xslt:transform(var1, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com/20180827154353PLT" xmlns:tns1="http://tns.tibco.com/bw/REST" xmlns:tns3="/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema" version="2.0">
    <xsl:template name="post-input" match="/">
        <tns1:httpHeaders/>
    </xsl:template>
</xsl:stylesheet>`, context);
    xml var4 = xml `<root>${var2 + var3}</root>`;
    json var5 = check httpClient0->post("/creditscore", var4);
    xml var6 = check fromJson(var5);
    addToContext(context, "post", var6);
    return var6;
}

function receiveEvent_5(map<xml> context) returns xml|error {
    addToContext(context, "Start", context.get("$input"));
    return context.get("$input");
}

function scope_1ActivityRunner(map<xml> cx) returns xml|error {
    xml result0 = check receiveEvent_5(cx);
    xml result1 = check invoke(cx);
    xml result2 = check activityExtension_6(cx);
    return result2;
}

function scope_1FaultHandler(error err, map<xml> cx) returns xml {
    panic err;
}

function scope_1ScopeFn(xml input, map<xml> params) returns xml {
    map<xml> context = {...params};
    addToContext(context, "$input", input);
    xml|error result = scope_1ActivityRunner(context);
    if result is error {
        return scope_1FaultHandler(result, context);
    }
    return result;
}

function start_creditapp_module_EquifaxScore(GiveNewSchemaNameHere input, map<xml> params = {}) returns SuccessSchema {
    xml inputXML = input is map<anydata> ? checkpanic toXML(input) : xml ``;
    xml xmlResult = scope_1ScopeFn(inputXML, params);
    SuccessSchema result = convertToSuccessSchema(xmlResult);
    return result;
}

xmlns "http://www.tibco.com/bpel/2007/extensions" as tibex;
xmlns "http://www.tibco.com/pe/EngineTypes" as ns;
xmlns "/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema" as ns2;
xmlns "http://www.tibco.com/bw/process/info" as info;
xmlns "http://docs.oasis-open.org/ns/opencsa/sca/200912" as sca;
xmlns "http://tns.tibco.com/bw/palette/internal/activityerror+44ece17e-f278-4255-b693-65bb9cf58bca" as ns3;
xmlns "http://docs.oasis-open.org/wsbpel/2.0/process/executable" as bpws;
xmlns "http://tns.tibco.com/bw/palette/internal/activityerror+2632841e-3358-4851-a82f-2f2cd3aeec16" as ns1;
xmlns "http://www.w3.org/2001/XMLSchema" as xsd;
xmlns "http://ns.tibco.com/bw/property" as tibprop;
xmlns "http://xmlns.example.com/20180827154353PLT" as ns0;
