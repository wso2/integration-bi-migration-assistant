import ballerina/http;
import ballerina/xslt;

http:Client creditapp_module_MainProcess_client = checkpanic new ("localhost:8082/CreditDetails/creditdetails");
public listener http:Listener creditapp_module_MainProcess_listener = new (8082, {host: "localhost"});

service /CreditDetails on creditapp_module_MainProcess_listener {
    resource function post creditdetails(GiveNewSchemaNameHere|xml req) returns CreditScoreSuccessSchema|http:NotFound|http:InternalServerError {
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
        return start_creditapp_module_MainProcess(input, paramXML);
    }
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

xmlns "http://xmlns.example.com/20180827160122PLT" as ns0;
xmlns "/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema" as ns1;
xmlns "http://www.tibco.com/bpel/2007/extensions" as tibex;
xmlns "http://www.tibco.com/pe/EngineTypes" as ns;
xmlns "http://www.tibco.com/bw/process/info" as info;
xmlns "http://tns.tibco.com/bw/json/1535671685533" as ns3;
xmlns "http://docs.oasis-open.org/ns/opencsa/sca/200912" as sca;
xmlns "http://xmlns.example.com/Creditscore/parameters" as ns2;
xmlns "http://docs.oasis-open.org/wsbpel/2.0/process/executable" as bpws;
xmlns "http://www.w3.org/2001/XMLSchema" as xsd;
xmlns "http://ns.tibco.com/bw/property" as tibprop;
