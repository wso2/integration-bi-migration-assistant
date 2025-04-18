import ballerina/http;
import ballerina/xslt;

public listener http:Listener creditapp_module_MainProcess_listener = new (8082, {host: "localhost"});

service /CreditDetails on creditapp_module_MainProcess_listener {
    resource function post creditdetails(GiveNewSchemaNameHere input) returns CreditScoreSuccessSchema|http:NotFound|http:InternalServerError {
        return creditapp_module_MainProcess_start(input);
    }
}

function activityRunner_creditapp_module_MainProcess(map<xml> cx) returns xml|error {
    xml result0 = check extActivity_11(cx);
    xml result1 = check extActivity(cx);
    xml result2 = check reply(cx);
    return result2;
}

function creditapp_module_MainProcess_start(GiveNewSchemaNameHere input, map<xml> params = {}) returns CreditScoreSuccessSchema {
    xml inputXML = input is map<anydata> ? checkpanic toXML(input) : xml ``;
    xml xmlResult = process_creditapp_module_MainProcess(inputXML, params);
    CreditScoreSuccessSchema result = convertToCreditScoreSuccessSchema(xmlResult);
    return result;
}

function errorHandler_creditapp_module_MainProcess(error err, map<xml> cx) returns xml {
    panic err;
}

function extActivity(map<xml> context) returns xml|error {
    xml var0 = xml ``;
    xml var1 = check xslt:transform(var0, transformXSLT(xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema" version="2.0">
    <xsl:param name="post.item"/>
    <xsl:template name="FICOScore-input" match="/">
        <tns:GiveNewSchemaNameHere>
            <xsl:if test="$post.item/tns:DOB">
                <tns:DOB>
                    <xsl:value-of select="$post.item/tns:DOB"/>
                </tns:DOB>
            </xsl:if>
            <xsl:if test="$post.item/tns:FirstName">
                <tns:FirstName>
                    <xsl:value-of select="$post.item/tns:FirstName"/>
                </tns:FirstName>
            </xsl:if>
            <xsl:if test="$post.item/tns:LastName">
                <tns:LastName>
                    <xsl:value-of select="$post.item/tns:LastName"/>
                </tns:LastName>
            </xsl:if>
            <xsl:if test="$post.item/tns:SSN">
                <tns:SSN>
                    <xsl:value-of select="$post.item/tns:SSN"/>
                </tns:SSN>
            </xsl:if>
        </tns:GiveNewSchemaNameHere>
    </xsl:template>
</xsl:stylesheet>`), context);
    xml var2 = check toXML(check trap creditapp_module_EquifaxScore_start(convertToGiveNewSchemaNameHere(var1)));
    addToContext(context, "EquifaxScore", var2);
    return var2;
}

function extActivity_11(map<xml> context) returns xml|error {
    xml var0 = xml ``;
    xml var1 = check xslt:transform(var0, transformXSLT(xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns3="http://xmlns.example.com/Creditscore/parameters" xmlns:tns="/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema" version="2.0">
    <xsl:param name="post.item"/>
    <xsl:template name="ExperianScore-input" match="/">
        <tns:GiveNewSchemaNameHere>
            <xsl:if test="$post.item/tns:DOB">
                <tns:DOB>
                    <xsl:value-of select="$post.item/tns:DOB"/>
                </tns:DOB>
            </xsl:if>
            <xsl:if test="$post.item/tns:FirstName">
                <tns:FirstName>
                    <xsl:value-of select="$post.item/tns:FirstName"/>
                </tns:FirstName>
            </xsl:if>
            <xsl:if test="$post.item/tns:LastName">
                <tns:LastName>
                    <xsl:value-of select="$post.item/tns:LastName"/>
                </tns:LastName>
            </xsl:if>
            <xsl:if test="$post.item/tns:SSN">
                <tns:SSN>
                    <xsl:value-of select="$post.item/tns:SSN"/>
                </tns:SSN>
            </xsl:if>
        </tns:GiveNewSchemaNameHere>
    </xsl:template>
</xsl:stylesheet>`), context);
    xml var2 = check toXML(check trap creditapp_module_ExperianScore_start(convertToGiveNewSchemaNameHere(var1)));
    addToContext(context, "ExperianScore", var2);
    return var2;
}

function pick(map<xml> context) returns xml|error {
    return xml ``;
}

function process_creditapp_module_MainProcess(xml input, map<xml> params) returns xml {
    map<xml> context = {...params};
    addToContext(context, "$input", input);
    xml|error result = activityRunner_creditapp_module_MainProcess(context);
    if result is error {
        return errorHandler_creditapp_module_MainProcess(result, context);
    }
    return result;
}

function reply(map<xml> context) returns xml|error {
    xml var0 = xml ``;
    xml var1 = check xslt:transform(var0, transformXSLT(xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns1="http://xmlns.example.com/20180827160122PLT" xmlns:tns="/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema" xmlns:tns2="http://tns.tibco.com/bw/json/1535671685533" version="2.0">
    <xsl:param name="EquifaxScore"/>
    <xsl:param name="ExperianScore"/>
    <xsl:template name="postOut-input" match="/">
        <tns:CreditScoreSuccessSchema>
            <tns:EquifaxResponse>
                <xsl:if test="$EquifaxScore/tns:FICOScore">
                    <tns:FICOScore>
                        <xsl:value-of select="$EquifaxScore/tns:FICOScore"/>
                    </tns:FICOScore>
                </xsl:if>
                <xsl:if test="$EquifaxScore/tns:NoOfInquiries">
                    <tns:NoOfInquiries>
                        <xsl:value-of select="$EquifaxScore/tns:NoOfInquiries"/>
                    </tns:NoOfInquiries>
                </xsl:if>
                <xsl:if test="$EquifaxScore/tns:Rating">
                    <tns:Rating>
                        <xsl:value-of select="$EquifaxScore/tns:Rating"/>
                    </tns:Rating>
                </xsl:if>
            </tns:EquifaxResponse>
            <tns:ExperianResponse>
                <xsl:if test="$ExperianScore/tns2:fiCOScore">
                    <tns:FICOScore>
                        <xsl:value-of select="$ExperianScore/tns2:fiCOScore"/>
                    </tns:FICOScore>
                </xsl:if>
                <xsl:if test="$ExperianScore/tns2:noOfInquiries">
                    <tns:NoOfInquiries>
                        <xsl:value-of select="$ExperianScore/tns2:noOfInquiries"/>
                    </tns:NoOfInquiries>
                </xsl:if>
                <xsl:if test="$ExperianScore/tns2:rating">
                    <tns:Rating>
                        <xsl:value-of select="$ExperianScore/tns2:rating"/>
                    </tns:Rating>
                </xsl:if>
            </tns:ExperianResponse>
        </tns:CreditScoreSuccessSchema>
    </xsl:template>
</xsl:stylesheet>`), context);
    xml var2 = xml `<root>${var1}</root>`;
    xml var3 = check xslt:transform(var2, transformXSLT(xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns1="http://xmlns.example.com/20180827160122PLT" xmlns:tns="/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema" xmlns:tns2="http://tns.tibco.com/bw/json/1535671685533" version="2.0"><xsl:param name="EquifaxScore"/><xsl:param name="ExperianScore"/><xsl:template name="postOut-input" match="/"><tns1:postResponse><item><tns:CreditScoreSuccessSchema><tns:EquifaxResponse><xsl:if test="$EquifaxScore/tns:FICOScore"><tns:FICOScore><xsl:value-of select="$EquifaxScore/tns:FICOScore"/></tns:FICOScore></xsl:if><xsl:if test="$EquifaxScore/tns:NoOfInquiries"><tns:NoOfInquiries><xsl:value-of select="$EquifaxScore/tns:NoOfInquiries"/></tns:NoOfInquiries></xsl:if><xsl:if test="$EquifaxScore/tns:Rating"><tns:Rating><xsl:value-of select="$EquifaxScore/tns:Rating"/></tns:Rating></xsl:if></tns:EquifaxResponse><tns:ExperianResponse><xsl:if test="$ExperianScore/tns2:fiCOScore"><tns:FICOScore><xsl:value-of select="$ExperianScore/tns2:fiCOScore"/></tns:FICOScore></xsl:if><xsl:if test="$ExperianScore/tns2:noOfInquiries"><tns:NoOfInquiries><xsl:value-of select="$ExperianScore/tns2:noOfInquiries"/></tns:NoOfInquiries></xsl:if><xsl:if test="$ExperianScore/tns2:rating"><tns:Rating><xsl:value-of select="$ExperianScore/tns2:rating"/></tns:Rating></xsl:if></tns:ExperianResponse></tns:CreditScoreSuccessSchema></item></tns1:postResponse></xsl:template></xsl:stylesheet>`), context);
    return var3;
}
