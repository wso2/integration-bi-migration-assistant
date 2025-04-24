import ballerina/http;
import ballerina/xslt;

const string client_404_RecordNotFound = "Record Not Found";
public listener http:Listener creditapp_module_EquifaxScore_listener = new (8081, {host: "localhost"});

service /y54cuadtcxtfstqs3rux2gfdaxppoqgc on creditapp_module_EquifaxScore_listener {
    resource function post creditscore(GiveNewSchemaNameHere input) returns SuccessSchema|http:NotFound|http:InternalServerError|client_404_RecordNotFound {
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
    resource function post creditscore(GiveNewSchemaNameHere input) returns SuccessSchema|http:NotFound|http:InternalServerError {
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
    xml var0 = xml `<root></root>`;
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

function activityRunner_creditapp_module_EquifaxScore(map<xml> cx) returns xml|error {
    xml result0 = check receiveEvent_5(cx);
    xml result1 = check invoke(cx);
    xml result2 = check activityExtension_6(cx);
    return result2;
}

function errorHandler_creditapp_module_EquifaxScore(error err, map<xml> cx) returns xml {
    panic err;
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

function process_creditapp_module_EquifaxScore(xml input, map<xml> params) returns xml {
    map<xml> context = {...params};
    addToContext(context, "$input", input);
    xml|error result = activityRunner_creditapp_module_EquifaxScore(context);
    if result is error {
        return errorHandler_creditapp_module_EquifaxScore(result, context);
    }
    return result;
}

function receiveEvent_5(map<xml> context) returns xml|error {
    addToContext(context, "Start", context.get("$input"));
    return context.get("$input");
}

function start_creditapp_module_EquifaxScore(GiveNewSchemaNameHere input, map<xml> params = {}) returns SuccessSchema {
    xml inputXML = input is map<anydata> ? checkpanic toXML(input) : xml ``;
    xml xmlResult = process_creditapp_module_EquifaxScore(inputXML, params);
    SuccessSchema result = convertToSuccessSchema(xmlResult);
    return result;
}
