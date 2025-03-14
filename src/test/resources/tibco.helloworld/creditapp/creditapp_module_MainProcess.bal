import ballerina/http;
import ballerina/xslt;

public listener http:Listener creditapp_module_MainProcess_listener = new (8080, {host: "localhost"});

service /CreditDetails on creditapp_module_MainProcess_listener {
    resource function post creditdetails(GiveNewSchemaNameHere input) returns CreditScoreSuccessSchema|http:NotFound|http:InternalServerError {
        return creditapp_module_MainProcess_start(input);
    }
}

function creditapp_module_MainProcess_start(GiveNewSchemaNameHere input) returns CreditScoreSuccessSchema {
    xml inputXML = toXML(input);
    xml xmlResult = process_creditapp_module_MainProcess(inputXML);
    CreditScoreSuccessSchema result = convertToCreditScoreSuccessSchema(xmlResult);
    return result;
}

function extActivity(xml input, map<xml> context) returns xml {
    xml var0 = checkpanic xslt:transform(input, xml `<?xml version="1.0" encoding="UTF-8"?>
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
</xsl:stylesheet>`, context);
    xml var1 = toXML(creditapp_module_EquifaxScore_start(convertToGiveNewSchemaNameHere(var0)));
    context["EquifaxScore"] = var1;
    return var1;
}

function extActivity_3(xml input, map<xml> context) returns xml {
    xml var0 = checkpanic xslt:transform(input, xml `<?xml version="1.0" encoding="UTF-8"?>
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
</xsl:stylesheet>`, context);
    xml var1 = toXML(creditapp_module_ExperianScore_start(convertToGiveNewSchemaNameHere(var0)));
    context["ExperianScore"] = var1;
    return var1;
}

function pick(xml input, map<xml> context) returns xml {
    return input;
}

function process_creditapp_module_MainProcess(xml input) returns xml {
    map<xml> context = {};
    context["post.item"] = input;
    worker start_worker {
        xml result0 = extActivity(input, context);
        result0 -> FICOScoreTopostOut;
        xml result1 = extActivity_3(input, context);
        result1 -> ExperianScoreTopostOut;
    }
    worker ExperianScoreTopostOut {
        xml result0 = <- start_worker;
        result0 -> reply_worker;
    }
    worker FICOScoreTopostOut {
        xml result0 = <- start_worker;
        result0 -> reply_worker;
    }
    worker reply_worker {
        xml input0 = <- FICOScoreTopostOut;
        xml input1 = <- ExperianScoreTopostOut;
        xml combinedInput = input0 + input1;
        xml output = reply(combinedInput, context);
        output -> function;
    }
    xml result0 = <- reply_worker;
    xml result = result0;
    return result;
}

function reply(xml input, map<xml> context) returns xml {
    xml var0 = checkpanic xslt:transform(input, xml `<?xml version="1.0" encoding="UTF-8"?>
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
</xsl:stylesheet>`, context);
    xml var1 = checkpanic xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns1="http://xmlns.example.com/20180827160122PLT" xmlns:tns="/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema" xmlns:tns2="http://tns.tibco.com/bw/json/1535671685533" version="2.0"><xsl:param name="EquifaxScore"/><xsl:param name="ExperianScore"/><xsl:template name="postOut-input" match="/"><tns1:postResponse><item><tns:CreditScoreSuccessSchema><tns:EquifaxResponse><xsl:if test="$EquifaxScore/tns:FICOScore"><tns:FICOScore><xsl:value-of select="$EquifaxScore/tns:FICOScore"/></tns:FICOScore></xsl:if><xsl:if test="$EquifaxScore/tns:NoOfInquiries"><tns:NoOfInquiries><xsl:value-of select="$EquifaxScore/tns:NoOfInquiries"/></tns:NoOfInquiries></xsl:if><xsl:if test="$EquifaxScore/tns:Rating"><tns:Rating><xsl:value-of select="$EquifaxScore/tns:Rating"/></tns:Rating></xsl:if></tns:EquifaxResponse><tns:ExperianResponse><xsl:if test="$ExperianScore/tns2:fiCOScore"><tns:FICOScore><xsl:value-of select="$ExperianScore/tns2:fiCOScore"/></tns:FICOScore></xsl:if><xsl:if test="$ExperianScore/tns2:noOfInquiries"><tns:NoOfInquiries><xsl:value-of select="$ExperianScore/tns2:noOfInquiries"/></tns:NoOfInquiries></xsl:if><xsl:if test="$ExperianScore/tns2:rating"><tns:Rating><xsl:value-of select="$ExperianScore/tns2:rating"/></tns:Rating></xsl:if></tns:ExperianResponse></tns:CreditScoreSuccessSchema></item></tns1:postResponse></xsl:template></xsl:stylesheet>`, context);
    return var1;
}
