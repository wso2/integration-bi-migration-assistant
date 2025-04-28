import ballerina/http;
import ballerina/xslt;

http:Client creditapp_module_ExperianScore_client = checkpanic new ("localhost:8080/creditscore");
public listener http:Listener creditapp_module_ExperianScore_listener = new (8080, {host: "localhost"});

service / on creditapp_module_ExperianScore_listener {
    resource function post creditscore(GiveNewSchemaNameHere|xml req) returns ExperianResponseSchemaElement|http:NotFound|http:InternalServerError {
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
        return start_creditapp_module_ExperianScore(input, paramXML);
    }
}

function activityExtension(map<xml> context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns3="http://tns.tibco.com/bw/json/1535671685533" version="2.0"><xsl:param name="ParseJSON"/><xsl:template name="End-input" match="/"><tns3:ExperianResponseSchemaElement><xsl:if test="$ParseJSON/root/tns3:fiCOScore"><tns3:fiCOScore><xsl:value-of select="$ParseJSON/root/tns3:fiCOScore"/></tns3:fiCOScore></xsl:if><xsl:if test="$ParseJSON/root/tns3:rating"><tns3:rating><xsl:value-of select="$ParseJSON/root/tns3:rating"/></tns3:rating></xsl:if><xsl:if test="$ParseJSON/root/tns3:noOfInquiries"><tns3:noOfInquiries><xsl:value-of select="$ParseJSON/root/tns3:noOfInquiries"/></tns3:noOfInquiries></xsl:if></tns3:ExperianResponseSchemaElement></xsl:template></xsl:stylesheet>`, context);
    return var1;
}

function activityExtension_2(map<xml> context) returns xml|error {
    xml var0 = xml `<root></root>`;
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
    xml var0 = xml `<root></root>`;
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
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns5="activity.jsonParser.input+a3fa07a6-0270-48b7-ba84-7de6924acb3d+ActivityInputType" version="2.0"><xsl:param name="SendHTTPRequest"/><xsl:template name="ParseJSON-input" match="/"><tns5:ActivityInputClass><jsonString><xsl:value-of select="$SendHTTPRequest/root/asciiContent"/></jsonString></tns5:ActivityInputClass></xsl:template></xsl:stylesheet>`, context);
    xml var2 = check renderJsonAsExperianResponseSchemaElementXML(var1);
    addToContext(context, "ParseJSON", var2);
    return var2;
}

function activityRunner_creditapp_module_ExperianScore(map<xml> cx) returns xml|error {
    xml result0 = check receiveEvent(cx);
    xml result1 = check activityExtension_3(cx);
    xml result2 = check activityExtension_2(cx);
    xml result3 = check activityExtension_4(cx);
    xml result4 = check activityExtension(cx);
    return result4;
}

function errorHandler_creditapp_module_ExperianScore(error err, map<xml> cx) returns xml {
    panic err;
}

function process_creditapp_module_ExperianScore(xml input, map<xml> params) returns xml {
    map<xml> context = {...params};
    addToContext(context, "$input", input);
    xml|error result = activityRunner_creditapp_module_ExperianScore(context);
    if result is error {
        return errorHandler_creditapp_module_ExperianScore(result, context);
    }
    return result;
}

function receiveEvent(map<xml> context) returns xml|error {
    addToContext(context, "Start", context.get("$input"));
    return context.get("$input");
}

function start_creditapp_module_ExperianScore(GiveNewSchemaNameHere input, map<xml> params = {}) returns ExperianResponseSchemaElement {
    xml inputXML = input is map<anydata> ? checkpanic toXML(input) : xml ``;
    xml xmlResult = process_creditapp_module_ExperianScore(inputXML, params);
    ExperianResponseSchemaElement result = convertToExperianResponseSchemaElement(xmlResult);
    return result;
}
