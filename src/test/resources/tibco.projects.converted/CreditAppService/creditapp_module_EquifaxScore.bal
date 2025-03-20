import ballerina/http;
import ballerina/xslt;

const string client_404_RecordNotFound = "Record Not Found";
listener http:Listener creditapp_module_EquifaxScore_listener = new (8081, {host: "localhost"});

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

function activityExtension_6(xml input, map<xml> context) returns xml {
    xml var0 = checkpanic xslt:transform(input, transformXSLT(xml `<?xml version="1.0" encoding="UTF-8"?>
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

function creditapp_module_EquifaxScore_start(GiveNewSchemaNameHere input) returns SuccessSchema {
    xml inputXML = toXML(input);
    xml xmlResult = process_creditapp_module_EquifaxScore(inputXML);
    SuccessSchema result = convertToSuccessSchema(xmlResult);
    return result;
}

function invoke(xml input, map<xml> context) returns xml {
    http:Client var0 = checkpanic new ("/");
    json var1 = checkpanic var0->post("/creditscore", input);
    xml var2 = fromJson(var1);
    addToContext(context, "post", var2);
    return var2;
}

function process_creditapp_module_EquifaxScore(xml input) returns xml {
    map<xml> context = {};
    addToContext(context, "post.item", input);
    worker start_worker {
        xml result0 = receiveEvent_5(input, context);
        result0 -> StartTopost;
    }
    worker StartTopost {
        xml result0 = <- start_worker;
        result0 -> invoke_worker;
    }
    worker postToEnd {
        xml result0 = <- invoke_worker;
        result0 -> activityExtension_6_worker;
    }
    worker activityExtension_6_worker {
        xml input0 = <- postToEnd;
        xml combinedInput = input0;
        xml output = activityExtension_6(combinedInput, context);
        output -> function;
    }
    worker invoke_worker {
        xml input0 = <- StartTopost;
        xml combinedInput = input0;
        xml output = invoke(combinedInput, context);
        output -> postToEnd;
    }
    xml result0 = <- activityExtension_6_worker;
    xml result = result0;
    return result;
}

function receiveEvent_5(xml input, map<xml> context) returns xml {
    return input;
}
