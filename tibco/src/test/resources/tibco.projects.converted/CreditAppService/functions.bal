import ballerina/data.jsondata;
import ballerina/data.xmldata;
import ballerina/xslt;

function activityExtension(map<xml> context) returns xml|error {
    xml var0 = context.get("End-input");
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns3="http://tns.tibco.com/bw/json/1535671685533" version="2.0"><xsl:param name="ParseJSON"/><xsl:template name="End-input" match="/"><tns3:ExperianResponseSchemaElement><xsl:if test="$ParseJSON/root/tns3:fiCOScore"><tns3:fiCOScore><xsl:value-of select="$ParseJSON/root/tns3:fiCOScore"/></tns3:fiCOScore></xsl:if><xsl:if test="$ParseJSON/root/tns3:rating"><tns3:rating><xsl:value-of select="$ParseJSON/root/tns3:rating"/></tns3:rating></xsl:if><xsl:if test="$ParseJSON/root/tns3:noOfInquiries"><tns3:noOfInquiries><xsl:value-of select="$ParseJSON/root/tns3:noOfInquiries"/></tns3:noOfInquiries></xsl:if></tns3:ExperianResponseSchemaElement></xsl:template></xsl:stylesheet>`, context);
    xml var2 = xml `<root>${var1}</root>`;
    return var2;
}

function activityExtension_2(map<xml> context) returns xml|error {
    xml var0 = context.get("SendHTTPRequest-input");
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns4="http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput" xmlns:tns6="/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema" version="2.0"><xsl:param name="RenderJSON"/><xsl:param name="Start"/><xsl:template name="SendHTTPRequest-input" match="/"><tns4:RequestActivityInput><Method><xsl:value-of select="'POST'"/></Method><RequestURI><xsl:value-of select="'/creditscore'"/></RequestURI><PostData><xsl:value-of select="$RenderJSON/root/jsonString"/></PostData><Headers><Accept><xsl:value-of select="'application/json'"/></Accept><Content-Type><xsl:value-of select="'application/json'"/></Content-Type></Headers><parameters><xsl:if test="$Start/root/tns6:SSN"><ssn><xsl:value-of select="$Start/root/tns6:SSN"/></ssn></xsl:if></parameters></tns4:RequestActivityInput></xsl:template></xsl:stylesheet>`, context);
    string var2 = (var1/**/<Method>[0]).data();
    string var3 = (var1/**/<RequestURI>[0]).data();
    json var4 = ();
    match var2 {
        "GET" => {
            var4 = check creditapp_module_HttpClientResource1->get(var3);
        }
        "POST" => {
            json postData = (var1/**/<PostData>[0]).data();
            var4 = check creditapp_module_HttpClientResource1->post(var3, postData);
        }
        _ => {
            panic error("Unsupported method: " + var2);
        }
    }
    xml var5 = xml `<root><asciiContent>${var4.toJsonString()}</asciiContent></root>`;
    addToContext(context, "SendHTTPRequest", var5);
    return var5;
}

function activityExtension_3(map<xml> context) returns xml|error {
    xml var0 = context.get("RenderJSON-input");
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns6="/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema" xmlns:tns="http://tns.tibco.com/bw/activity/jsonRender/xsd/input/55832ae5-2a37-4b37-8392-a64537f49367" version="2.0">
    <xsl:param name="Start"/>
    <xsl:template name="RenderJSON-input" match="/">
        <tns:InputElement>
            <tns:dob>
                <xsl:value-of select="$Start/root/tns6:DOB"/>
            </tns:dob>
            <tns:firstName>
                <xsl:value-of select="$Start/root/tns6:FirstName"/>
            </tns:firstName>
            <tns:lastName>
                <xsl:value-of select="$Start/root/tns6:LastName"/>
            </tns:lastName>
            <tns:ssn>
                <xsl:value-of select="$Start/root/tns6:SSN"/>
            </tns:ssn>
        </tns:InputElement>
    </xsl:template>
</xsl:stylesheet>`, context);
    xml var2 = renderJson(var1);
    addToContext(context, "RenderJSON", var2);
    return var2;
}

function activityExtension_4(map<xml> context) returns xml|error {
    xml var0 = context.get("ParseJSON-input");
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns5="activity.jsonParser.input+a3fa07a6-0270-48b7-ba84-7de6924acb3d+ActivityInputType" version="2.0"><xsl:param name="SendHTTPRequest"/><xsl:template name="ParseJSON-input" match="/"><tns5:ActivityInputClass><jsonString><xsl:value-of select="$SendHTTPRequest/root/asciiContent"/></jsonString></tns5:ActivityInputClass></xsl:template></xsl:stylesheet>`, context);
    xml var2 = check renderJsonAsExperianResponseSchemaElementXML(var1);
    addToContext(context, "ParseJSON", var2);
    return var2;
}

function receiveEvent(map<xml> context) returns xml|error {
    addToContext(context, "Start", context.get("$input"));
    return context.get("$input");
}

function scopeActivityRunner(map<xml> cx) returns xml|error {
    xml result0 = check receiveEvent(cx);
    xml result1 = check activityExtension_3(cx);
    xml result2 = check activityExtension_2(cx);
    xml result3 = check activityExtension_4(cx);
    xml result4 = check activityExtension(cx);
    return result4;
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

function start_creditapp_module_ExperianScore(GiveNewSchemaNameHere input, map<xml> params = {}) returns ExperianResponseSchemaElement {
    xml inputXML = input is map<anydata> ? checkpanic toXML(input) : xml ``;
    xml xmlResult = scopeScopeFn(inputXML, params);
    ExperianResponseSchemaElement result = convertToExperianResponseSchemaElement(xmlResult);
    return result;
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
    xml var2 = xml `<root>${var1}</root>`;
    return var2;
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

function extActivity(map<xml> context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema" version="2.0"><xsl:param name="post"/><xsl:template name="FICOScore-input" match="/"><tns:GiveNewSchemaNameHere><xsl:if test="$post/root/item/tns:GiveNewSchemaNameHere/tns:DOB"><tns:DOB><xsl:value-of select="$post/root/item/tns:GiveNewSchemaNameHere/tns:DOB"/></tns:DOB></xsl:if><xsl:if test="$post/root/item/tns:GiveNewSchemaNameHere/tns:FirstName"><tns:FirstName><xsl:value-of select="$post/root/item/tns:GiveNewSchemaNameHere/tns:FirstName"/></tns:FirstName></xsl:if><xsl:if test="$post/root/item/tns:GiveNewSchemaNameHere/tns:LastName"><tns:LastName><xsl:value-of select="$post/root/item/tns:GiveNewSchemaNameHere/tns:LastName"/></tns:LastName></xsl:if><xsl:if test="$post/root/item/tns:GiveNewSchemaNameHere/tns:SSN"><tns:SSN><xsl:value-of select="$post/root/item/tns:GiveNewSchemaNameHere/tns:SSN"/></tns:SSN></xsl:if></tns:GiveNewSchemaNameHere></xsl:template></xsl:stylesheet>`, context);
    xml var2 = check xslt:transform(var1, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema" version="2.0">
    <xsl:param name="post"/>
    <xsl:template name="FICOScore-input" match="/">
        <tns:GiveNewSchemaNameHere>
            <xsl:if test="$post/root/item/tns:DOB">
                <tns:DOB>
                    <xsl:value-of select="$post/root/item/tns:DOB"/>
                </tns:DOB>
            </xsl:if>
            <xsl:if test="$post/root/item/tns:FirstName">
                <tns:FirstName>
                    <xsl:value-of select="$post/root/item/tns:FirstName"/>
                </tns:FirstName>
            </xsl:if>
            <xsl:if test="$post/root/item/tns:LastName">
                <tns:LastName>
                    <xsl:value-of select="$post/root/item/tns:LastName"/>
                </tns:LastName>
            </xsl:if>
            <xsl:if test="$post/root/item/tns:SSN">
                <tns:SSN>
                    <xsl:value-of select="$post/root/item/tns:SSN"/>
                </tns:SSN>
            </xsl:if>
        </tns:GiveNewSchemaNameHere>
    </xsl:template>
</xsl:stylesheet>`, context);
    xml var3 = transform(var2);
    xml var4 = check creditapp_module_EquifaxScore_client->post("", var3);
    addToContext(context, "EquifaxScore", var4);
    return var4;
}

function extActivity_11(map<xml> context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns3="http://xmlns.example.com/Creditscore/parameters" xmlns:tns="/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema" version="2.0"><xsl:param name="post"/><xsl:template name="ExperianScore-input" match="/"><tns:GiveNewSchemaNameHere><xsl:if test="$post/root/item/tns:GiveNewSchemaNameHere/tns:DOB"><tns:DOB><xsl:value-of select="$post/root/item/tns:GiveNewSchemaNameHere/tns:DOB"/></tns:DOB></xsl:if><xsl:if test="$post/root/item/tns:GiveNewSchemaNameHere/tns:FirstName"><tns:FirstName><xsl:value-of select="$post/root/item/tns:GiveNewSchemaNameHere/tns:FirstName"/></tns:FirstName></xsl:if><xsl:if test="$post/root/item/tns:GiveNewSchemaNameHere/tns:LastName"><tns:LastName><xsl:value-of select="$post/root/item/tns:GiveNewSchemaNameHere/tns:LastName"/></tns:LastName></xsl:if><xsl:if test="$post/root/item/tns:GiveNewSchemaNameHere/tns:SSN"><tns:SSN><xsl:value-of select="$post/root/item/tns:GiveNewSchemaNameHere/tns:SSN"/></tns:SSN></xsl:if></tns:GiveNewSchemaNameHere></xsl:template></xsl:stylesheet>`, context);
    xml var2 = check xslt:transform(var1, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns3="http://xmlns.example.com/Creditscore/parameters" xmlns:tns="/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema" version="2.0">
    <xsl:param name="post"/>
    <xsl:template name="ExperianScore-input" match="/">
        <tns:GiveNewSchemaNameHere>
            <xsl:if test="$post/root/item/tns:DOB">
                <tns:DOB>
                    <xsl:value-of select="$post/root/item/tns:DOB"/>
                </tns:DOB>
            </xsl:if>
            <xsl:if test="$post/root/item/tns:FirstName">
                <tns:FirstName>
                    <xsl:value-of select="$post/root/item/tns:FirstName"/>
                </tns:FirstName>
            </xsl:if>
            <xsl:if test="$post/root/item/tns:LastName">
                <tns:LastName>
                    <xsl:value-of select="$post/root/item/tns:LastName"/>
                </tns:LastName>
            </xsl:if>
            <xsl:if test="$post/root/item/tns:SSN">
                <tns:SSN>
                    <xsl:value-of select="$post/root/item/tns:SSN"/>
                </tns:SSN>
            </xsl:if>
        </tns:GiveNewSchemaNameHere>
    </xsl:template>
</xsl:stylesheet>`, context);
    xml var3 = transform(var2);
    xml var4 = check creditapp_module_ExperianScore_client->post("", var3);
    addToContext(context, "ExperianScore", var4);
    return var4;
}

function pick(map<xml> context) returns xml|error {
    return scope1ScopeFn(context);
}

function reply(map<xml> context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns1="http://xmlns.example.com/20180827160122PLT" xmlns:tns="/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema" xmlns:tns2="http://tns.tibco.com/bw/json/1535671685533" version="2.0">
    <xsl:param name="EquifaxScore"/>
    <xsl:param name="ExperianScore"/>
    <xsl:template name="postOut-input" match="/">
        <tns:CreditScoreSuccessSchema>
            <tns:EquifaxResponse>
                <xsl:if test="$EquifaxScore/root/tns:FICOScore">
                    <tns:FICOScore>
                        <xsl:value-of select="$EquifaxScore/root/tns:FICOScore"/>
                    </tns:FICOScore>
                </xsl:if>
                <xsl:if test="$EquifaxScore/root/tns:NoOfInquiries">
                    <tns:NoOfInquiries>
                        <xsl:value-of select="$EquifaxScore/root/tns:NoOfInquiries"/>
                    </tns:NoOfInquiries>
                </xsl:if>
                <xsl:if test="$EquifaxScore/root/tns:Rating">
                    <tns:Rating>
                        <xsl:value-of select="$EquifaxScore/root/tns:Rating"/>
                    </tns:Rating>
                </xsl:if>
            </tns:EquifaxResponse>
            <tns:ExperianResponse>
                <xsl:if test="$ExperianScore/root/tns2:fiCOScore">
                    <tns:FICOScore>
                        <xsl:value-of select="$ExperianScore/root/tns2:fiCOScore"/>
                    </tns:FICOScore>
                </xsl:if>
                <xsl:if test="$ExperianScore/root/tns2:noOfInquiries">
                    <tns:NoOfInquiries>
                        <xsl:value-of select="$ExperianScore/root/tns2:noOfInquiries"/>
                    </tns:NoOfInquiries>
                </xsl:if>
                <xsl:if test="$ExperianScore/root/tns2:rating">
                    <tns:Rating>
                        <xsl:value-of select="$ExperianScore/root/tns2:rating"/>
                    </tns:Rating>
                </xsl:if>
            </tns:ExperianResponse>
        </tns:CreditScoreSuccessSchema>
    </xsl:template>
</xsl:stylesheet>`, context);
    xml var2 = xml `<root>${var1}</root>`;
    xml var3 = check xslt:transform(var2, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns1="http://xmlns.example.com/20180827160122PLT" xmlns:tns="/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema" xmlns:tns2="http://tns.tibco.com/bw/json/1535671685533" version="2.0"><xsl:param name="EquifaxScore"/><xsl:param name="ExperianScore"/><xsl:template name="postOut-input" match="/"><tns1:postResponse><item><tns:CreditScoreSuccessSchema><tns:EquifaxResponse><xsl:if test="$EquifaxScore/root/tns:FICOScore"><tns:FICOScore><xsl:value-of select="$EquifaxScore/root/tns:FICOScore"/></tns:FICOScore></xsl:if><xsl:if test="$EquifaxScore/root/tns:NoOfInquiries"><tns:NoOfInquiries><xsl:value-of select="$EquifaxScore/root/tns:NoOfInquiries"/></tns:NoOfInquiries></xsl:if><xsl:if test="$EquifaxScore/root/tns:Rating"><tns:Rating><xsl:value-of select="$EquifaxScore/root/tns:Rating"/></tns:Rating></xsl:if></tns:EquifaxResponse><tns:ExperianResponse><xsl:if test="$ExperianScore/root/tns2:fiCOScore"><tns:FICOScore><xsl:value-of select="$ExperianScore/root/tns2:fiCOScore"/></tns:FICOScore></xsl:if><xsl:if test="$ExperianScore/root/tns2:noOfInquiries"><tns:NoOfInquiries><xsl:value-of select="$ExperianScore/root/tns2:noOfInquiries"/></tns:NoOfInquiries></xsl:if><xsl:if test="$ExperianScore/root/tns2:rating"><tns:Rating><xsl:value-of select="$ExperianScore/root/tns2:rating"/></tns:Rating></xsl:if></tns:ExperianResponse></tns:CreditScoreSuccessSchema></item></tns1:postResponse></xsl:template></xsl:stylesheet>`, context);
    return var3;
}

function scope1ActivityRunner(map<xml> cx) returns xml|error {
    xml result0 = check extActivity_11(cx);
    xml result1 = check extActivity(cx);
    xml result2 = check reply(cx);
    return result2;
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

function scope_2ActivityRunner(map<xml> cx) returns xml|error {
    xml result0 = check pick(cx);
    return result0;
}

function scope_2FaultHandler(error err, map<xml> cx) returns xml {
    panic err;
}

function scope_2ScopeFn(xml input, map<xml> params) returns xml {
    map<xml> context = {...params};
    addToContext(context, "$input", input);
    xml|error result = scope_2ActivityRunner(context);
    if result is error {
        return scope_2FaultHandler(result, context);
    }
    return result;
}

function start_creditapp_module_MainProcess(GiveNewSchemaNameHere input, map<xml> params = {}) returns CreditScoreSuccessSchema {
    xml inputXML = input is map<anydata> ? checkpanic toXML(input) : xml ``;
    xml xmlResult = scope_2ScopeFn(inputXML, params);
    CreditScoreSuccessSchema result = convertToCreditScoreSuccessSchema(xmlResult);
    return result;
}

function convertToCreditScoreSuccessSchema(xml input) returns CreditScoreSuccessSchema {
    return checkpanic xmldata:parseAsType(input);
}

function convertToExperianResponseSchemaElement(xml input) returns ExperianResponseSchemaElement {
    return checkpanic xmldata:parseAsType(input);
}

function convertToSuccessSchema(xml input) returns SuccessSchema {
    return checkpanic xmldata:parseAsType(input);
}

function fromJson(json data) returns error|xml {
    return xmldata:fromJson(data);
}

function toXML(map<anydata> data) returns error|xml {
    return xmldata:toXml(data);
}

function tryBindToGiveNewSchemaNameHere(xml|json input) returns GiveNewSchemaNameHere|error {
    return input is xml ? xmldata:parseAsType(input) : jsondata:parseAsType(input);
}

function addToContext(map<xml> context, string varName, xml value) {
    xml children = value/*;
    xml transformed = xml `<root>${children}</root>`;
    context[varName] = transformed;
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

function renderJson(xml value) returns xml {
    json jsonValue = xmlToJson(value);
    return xml `<root><jsonString>${jsonValue.toJsonString()}</jsonString></root>`;
}

function renderJSONAsXML(json value, string? namespace, string typeName) returns xml|error {
    anydata body;
    if (value is map<json>) {
        xml acum = xml ``;
        foreach string key in value.keys() {
            acum += check renderJSONAsXML(value.get(key), namespace, key);
        }
        body = acum;
    } else {
        body = value;
    }

    string rep = string `<${typeName}>${body.toString()}</${typeName}>`;
    xml result = check xml:fromString(rep);
    if (namespace == ()) {
        return result;
    }
    if (result !is xml:Element) {
        panic error("Expected XML element");
    }
    map<string> attributes = result.getAttributes();
    attributes["xmlns"] = namespace;
    return result;
}

function xmlToJson(xml value) returns json {
    json result = toJsonInner(value);
    if (result is map<json> && result.hasKey("InputElement")) {
        return result.get("InputElement");
    } else {
        return result;
    }
}

function toJsonInner(xml value) returns json {
json result;
if (value is xml:Element) {
result = toJsonElement(value);
} else {
result = value.toJson();
}
return result;
}

function toJsonElement(xml:Element element) returns json {
XMLElementParseResult parseResult = parseElement(element);
string name = parseResult.name;

xml children = element/*;
map<json> body = {};
map<json> result = {};
foreach xml child in children {
json r = toJsonInner(child);
if child !is xml:Element {
result[name] = r;
return result;
}
string childName = parseElement(child).name;
if r !is map<json> {
panic error("unexpected");
} else {
r = r.get(childName);
}
if body.hasKey(childName) {
json current = body.get(childName);
if current !is json[] {
json[] n = [body.get(childName)];
n.push(r);
body[childName] = n;
} else {
current.push(r);
}
} else {
body[childName] = r;
}
}
result[name] = body;
return result;
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

function renderJsonAsExperianResponseSchemaElementXML(xml value) returns xml|error {
    string jsonString = (value/<jsonString>).data();
    map<json> jsonValue = check jsondata:parseString(jsonString);
    string? namespace = (ExperianResponseSchemaElement).@xmldata:Namespace["uri"];
    return renderJSONAsXML(jsonValue, namespace, "ExperianResponseSchemaElement");
}
