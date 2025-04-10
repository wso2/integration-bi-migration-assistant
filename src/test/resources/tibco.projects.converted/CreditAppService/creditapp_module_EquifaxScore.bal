import ballerina/http;
import ballerina/xslt;

const string client_404_RecordNotFound = "Record Not Found";
public listener http:Listener creditapp_module_EquifaxScore_listener = new (8081, {host: "localhost"});

service /y54cuadtcxtfstqs3rux2gfdaxppoqgc on creditapp_module_EquifaxScore_listener {
    resource function post creditscore(GiveNewSchemaNameHere input) returns SuccessSchema|http:NotFound|http:InternalServerError|client_404_RecordNotFound {
        return creditapp_module_EquifaxScore_start(input);
    }
}

service / on creditapp_module_EquifaxScore_listener {
    resource function post creditscore(GiveNewSchemaNameHere input) returns SuccessSchema|http:NotFound|http:InternalServerError {
        return creditapp_module_EquifaxScore_start(input);
    }
}

function activityExtension_6(xml input, map<xml> context) returns xml|error {
    xml var0 = check xslt:transform(input, transformXSLT(xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns3="/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema" version="2.0">
    <xsl:param name="post.item"/>
    <xsl:template name="End-input" match="/">
        <tns3:SuccessSchema>
            <xsl:if test="$post.item/tns3:FICOScore">
                <tns3:FICOScore>
                    <xsl:value-of select="$post.item/tns3:FICOScore"/>
                </tns3:FICOScore>
            </xsl:if>
            <xsl:if test="$post.item/tns3:NoOfInquiries">
                <tns3:NoOfInquiries>
                    <xsl:value-of select="$post.item/tns3:NoOfInquiries"/>
                </tns3:NoOfInquiries>
            </xsl:if>
            <xsl:if test="$post.item/tns3:Rating">
                <tns3:Rating>
                    <xsl:value-of select="$post.item/tns3:Rating"/>
                </tns3:Rating>
            </xsl:if>
        </tns3:SuccessSchema>
    </xsl:template>
</xsl:stylesheet>`), context);
    return var0;
}

function activityRunner_creditapp_module_EquifaxScore(xml input, map<xml> cx) returns xml|error {
    xml result0 = check receiveEvent_5(input, cx);
    xml result1 = check invoke(result0, cx);
    xml result2 = check activityExtension_6(result1, cx);
    return result2;
}

function creditapp_module_EquifaxScore_start(GiveNewSchemaNameHere input) returns SuccessSchema {
    xml inputXML = checkpanic toXML(input);
    xml xmlResult = process_creditapp_module_EquifaxScore(inputXML);
    SuccessSchema result = convertToSuccessSchema(xmlResult);
    return result;
}

function errorHandler_creditapp_module_EquifaxScore(error err, map<xml> cx) returns xml {
    checkpanic err;
}

function invoke(xml input, map<xml> context) returns xml|error {
    xml var0 = check xslt:transform(input, transformXSLT(xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com/20180827154353PLT" xmlns:tns1="http://tns.tibco.com/bw/REST" xmlns:tns3="/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema" version="2.0"><xsl:param name="Start"/><xsl:template name="post-input" match="/"><tns:postRequest1><item><tns3:GiveNewSchemaNameHere><xsl:if test="$Start/tns3:DOB"><tns3:DOB><xsl:value-of select="$Start/tns3:DOB"/></tns3:DOB></xsl:if><xsl:if test="$Start/tns3:FirstName"><tns3:FirstName><xsl:value-of select="$Start/tns3:FirstName"/></tns3:FirstName></xsl:if><xsl:if test="$Start/tns3:LastName"><tns3:LastName><xsl:value-of select="$Start/tns3:LastName"/></tns3:LastName></xsl:if><xsl:if test="$Start/tns3:SSN"><tns3:SSN><xsl:value-of select="$Start/tns3:SSN"/></tns3:SSN></xsl:if></tns3:GiveNewSchemaNameHere></item><httpHeaders><tns1:httpHeaders/></httpHeaders></tns:postRequest1></xsl:template></xsl:stylesheet>`), context);
    xml var1 = check xslt:transform(var0, transformXSLT(xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com/20180827154353PLT" xmlns:tns1="http://tns.tibco.com/bw/REST" xmlns:tns3="/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema" version="2.0">
    <xsl:param name="Start"/>
    <xsl:template name="post-input" match="/">
        <tns3:GiveNewSchemaNameHere>
            <xsl:if test="$Start/tns3:DOB">
                <tns3:DOB>
                    <xsl:value-of select="$Start/tns3:DOB"/>
                </tns3:DOB>
            </xsl:if>
            <xsl:if test="$Start/tns3:FirstName">
                <tns3:FirstName>
                    <xsl:value-of select="$Start/tns3:FirstName"/>
                </tns3:FirstName>
            </xsl:if>
            <xsl:if test="$Start/tns3:LastName">
                <tns3:LastName>
                    <xsl:value-of select="$Start/tns3:LastName"/>
                </tns3:LastName>
            </xsl:if>
            <xsl:if test="$Start/tns3:SSN">
                <tns3:SSN>
                    <xsl:value-of select="$Start/tns3:SSN"/>
                </tns3:SSN>
            </xsl:if>
        </tns3:GiveNewSchemaNameHere>
    </xsl:template>
</xsl:stylesheet>`), context);
    xml var2 = check xslt:transform(var0, transformXSLT(xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com/20180827154353PLT" xmlns:tns1="http://tns.tibco.com/bw/REST" xmlns:tns3="/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema" version="2.0">
    <xsl:template name="post-input" match="/">
        <tns1:httpHeaders/>
    </xsl:template>
</xsl:stylesheet>`), context);
    xml var3 = xml `<root>${var1} + ${var2}</root>`;
    json var4 = check httpInvoke("/", "/creditscore", "post", var3);
    xml var5 = check fromJson(var4);
    addToContext(context, "post", var5);
    return var5;
}

function process_creditapp_module_EquifaxScore(xml input) returns xml {
    map<xml> context = {};
    addToContext(context, "post.item", input);
    xml|error result = activityRunner_creditapp_module_EquifaxScore(input, context);
    if result is error {
        return errorHandler_creditapp_module_EquifaxScore(result, context);
    }
    return result;
}

function receiveEvent_5(xml input, map<xml> context) returns xml|error {
    addToContext(context, "Start", input);
    return input;
}
