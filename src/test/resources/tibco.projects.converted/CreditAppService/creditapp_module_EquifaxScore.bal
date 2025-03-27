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

function creditapp_module_EquifaxScore_start(GiveNewSchemaNameHere input) returns SuccessSchema {
    xml inputXML = checkpanic toXML(input);
    xml xmlResult = process_creditapp_module_EquifaxScore(inputXML);
    SuccessSchema result = convertToSuccessSchema(xmlResult);
    return result;
}

function invoke(xml input, map<xml> context) returns xml|error {
    http:Client var0 = check new ("/");
    json var1 = check var0->post("/creditscore", input);
    xml var2 = check fromJson(var1);
    addToContext(context, "post", var2);
    return var2;
}

function process_creditapp_module_EquifaxScore(xml input) returns xml {
    map<xml> context = {};
    addToContext(context, "post.item", input);
    worker start_worker {
        xml|error result0 = receiveEvent_5(input, context);
        if result0 is error {
            result0 -> errorHandler;
            return;
        }
        result0 -> StartTopost;
    }
    worker StartTopost {
        error:NoMessage|xml input = <- start_worker;
        if input is error:NoMessage {
            return;
        }
        input -> invoke_worker;
    }
    worker postToEnd {
        error:NoMessage|xml input = <- invoke_worker;
        if input is error:NoMessage {
            return;
        }
        input -> activityExtension_6_worker;
    }
    worker activityExtension_6_worker {
        error:NoMessage|xml inputVal = <- postToEnd;
        if inputVal is error:NoMessage {
            return;
        }
        xml|error output = activityExtension_6(inputVal, context);
        if output is error {
            output -> errorHandler;
            return;
        }
        output -> function;
    }
    worker invoke_worker {
        error:NoMessage|xml inputVal = <- StartTopost;
        if inputVal is error:NoMessage {
            return;
        }
        xml|error output = invoke(inputVal, context);
        if output is error {
            output -> errorHandler;
            return;
        }
        output -> postToEnd;
    }
    worker errorHandler {
        error result = <- start_worker | activityExtension_6_worker | invoke_worker;
        panic result;
    }
    error:NoMessage|xml result = <- activityExtension_6_worker;
    xml result_clean = result is error ? xml `` : result;
    return result_clean;
}

function receiveEvent_5(xml input, map<xml> context) returns xml|error {
    return input;
}
