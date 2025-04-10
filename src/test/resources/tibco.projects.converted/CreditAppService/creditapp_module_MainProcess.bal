import ballerina/http;
import ballerina/xslt;

public listener http:Listener creditapp_module_MainProcess_listener = new (8082, {host: "localhost"});

service /CreditDetails on creditapp_module_MainProcess_listener {
    resource function post creditdetails(GiveNewSchemaNameHere input) returns CreditScoreSuccessSchema|http:NotFound|http:InternalServerError {
        return creditapp_module_MainProcess_start(input);
    }
}

function activityRunner_creditapp_module_MainProcess(xml input, map<xml> cx) returns xml|error {
    xml result0 = check extActivity_11(input, cx);
    xml result1 = check extActivity(result0, cx);
    xml result2 = check reply(result1, cx);
    return result2;
}

function creditapp_module_MainProcess_start(GiveNewSchemaNameHere input) returns CreditScoreSuccessSchema {
    xml inputXML = checkpanic toXML(input);
    xml xmlResult = process_creditapp_module_MainProcess(inputXML);
    CreditScoreSuccessSchema result = convertToCreditScoreSuccessSchema(xmlResult);
    return result;
}

function errorHandler_creditapp_module_MainProcess(error err, map<xml> cx) returns xml {
    checkpanic err;
}

function extActivity(xml input, map<xml> context) returns xml|error {
    xml var0 = check xslt:transform(input, transformXSLT(xml `<?xml version="1.0" encoding="UTF-8"?>
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
    xml var1 = check toXML(check trap creditapp_module_EquifaxScore_start(convertToGiveNewSchemaNameHere(var0)));
    addToContext(context, "EquifaxScore", var1);
    return var1;
}

function extActivity_11(xml input, map<xml> context) returns xml|error {
    xml var0 = check xslt:transform(input, transformXSLT(xml `<?xml version="1.0" encoding="UTF-8"?>
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
    xml var1 = check toXML(check trap creditapp_module_ExperianScore_start(convertToGiveNewSchemaNameHere(var0)));
    addToContext(context, "ExperianScore", var1);
    return var1;
}

function pick(xml input, map<xml> context) returns xml|error {
    return input;
}

function process_creditapp_module_MainProcess(xml input) returns xml {
    map<xml> context = {};
    addToContext(context, "post.item", input);
    xml|error result = activityRunner_creditapp_module_MainProcess(input, context);
    if result is error {
        return errorHandler_creditapp_module_MainProcess(result, context);
    }
    return result;
}

function reply(xml input, map<xml> context) returns xml|error {
    xml var0 = check xslt:transform(input, transformXSLT(xml `<?xml version="1.0" encoding="UTF-8"?>
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
    xml var1 = xml `<root>${var0}</root>`;
    xml var2 = check xslt:transform(var1, transformXSLT(xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns1="http://xmlns.example.com/20180827160122PLT" xmlns:tns="/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema" xmlns:tns2="http://tns.tibco.com/bw/json/1535671685533" version="2.0"><xsl:param name="EquifaxScore"/><xsl:param name="ExperianScore"/><xsl:template name="postOut-input" match="/"><tns1:postResponse><item><tns:CreditScoreSuccessSchema><tns:EquifaxResponse><xsl:if test="$EquifaxScore/tns:FICOScore"><tns:FICOScore><xsl:value-of select="$EquifaxScore/tns:FICOScore"/></tns:FICOScore></xsl:if><xsl:if test="$EquifaxScore/tns:NoOfInquiries"><tns:NoOfInquiries><xsl:value-of select="$EquifaxScore/tns:NoOfInquiries"/></tns:NoOfInquiries></xsl:if><xsl:if test="$EquifaxScore/tns:Rating"><tns:Rating><xsl:value-of select="$EquifaxScore/tns:Rating"/></tns:Rating></xsl:if></tns:EquifaxResponse><tns:ExperianResponse><xsl:if test="$ExperianScore/tns2:fiCOScore"><tns:FICOScore><xsl:value-of select="$ExperianScore/tns2:fiCOScore"/></tns:FICOScore></xsl:if><xsl:if test="$ExperianScore/tns2:noOfInquiries"><tns:NoOfInquiries><xsl:value-of select="$ExperianScore/tns2:noOfInquiries"/></tns:NoOfInquiries></xsl:if><xsl:if test="$ExperianScore/tns2:rating"><tns:Rating><xsl:value-of select="$ExperianScore/tns2:rating"/></tns:Rating></xsl:if></tns:ExperianResponse></tns:CreditScoreSuccessSchema></item></tns1:postResponse></xsl:template></xsl:stylesheet>`), context);
    return var2;
}
