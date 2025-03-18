import ballerina/http;
import ballerina/xslt;

listener http:Listener experianservice_module_Process_listener = new (8080, {host: "localhost"});

service /Creditscore on experianservice_module_Process_listener {
    resource function post creditscore(InputElement input) returns ExperianResponseSchemaElement|http:NotFound|http:InternalServerError {
        return experianservice_module_Process_start(input);
    }
}

function activityExtension_31(xml input, map<xml> context) returns xml {
    xml var0 = checkpanic xslt:transform(input, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="activity.jsonParser.input+f396d921-0bc7-459d-ae81-0eb1a0f94723+ActivityInputType" version="2.0"><xsl:param name="HTTPReceiver"/><xsl:template name="ParseJSON-input" match="/"><tns:ActivityInputClass><jsonString><xsl:value-of select="$HTTPReceiver/PostData"/></jsonString></tns:ActivityInputClass></xsl:template></xsl:stylesheet>`, context);
    context["ParseJSON"] = var0;
    return var0;
}

function activityExtension_32(xml input, map<xml> context) returns xml {
    xml var0 = checkpanic xslt:transform(input, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://tns.tibco.com/bw/json/1535671685533" version="2.0"><xsl:param name="JDBCQuery"/><xsl:template name="RenderJSON-input" match="/"><tns:ExperianResponseSchemaElement><xsl:if test="$JDBCQuery/Record[1]/ficoscore"><tns:fiCOScore><xsl:value-of select="$JDBCQuery/Record[1]/ficoscore"/></tns:fiCOScore></xsl:if><xsl:if test="$JDBCQuery/Record[1]/rating"><tns:rating><xsl:value-of select="$JDBCQuery/Record[1]/rating"/></tns:rating></xsl:if><xsl:if test="$JDBCQuery/Record[1]/numofpulls"><tns:noOfInquiries><xsl:value-of select="$JDBCQuery/Record[1]/numofpulls"/></tns:noOfInquiries></xsl:if></tns:ExperianResponseSchemaElement></xsl:template></xsl:stylesheet>`, context);
    context["RenderJSON"] = var0;
    return var0;
}

function experianservice_module_Process_start(InputElement input) returns ExperianResponseSchemaElement {
    xml inputXML = toXML(input);
    xml xmlResult = process_experianservice_module_Process(inputXML);
    ExperianResponseSchemaElement result = convertToExperianResponseSchemaElement(xmlResult);
    return result;
}

function process_experianservice_module_Process(xml input) returns xml {
    map<xml> context = {};
    context["post.item"] = input;
    worker start_worker {
        xml result0 = receiveEvent_28(input, context);
        result0 -> HTTPReceiverToSendHTTPResponse;
        xml result1 = unhandled_29(input, context);
        xml result2 = unhandled_30(input, context);
    }
    worker HTTPReceiverToSendHTTPResponse {
        xml result0 = <- start_worker;
        result0 -> activityExtension_31_worker;
    }
    worker JDBCQueryToSendHTTPResponse {
    }
    worker activityExtension_31_worker {
        xml input0 = <- HTTPReceiverToSendHTTPResponse;
        xml combinedInput = input0;
        xml output = activityExtension_31(combinedInput, context);
        output -> ParseJSONToJDBCQuery;
    }
    worker activityExtension_32_worker {
        xml input0 = <- JDBCQueryToSendHTTPResponse;
        xml combinedInput = input0;
        xml output = activityExtension_32(combinedInput, context);
        output -> RenderJSONToSendHTTPResponse;
    }
    xml result0 = <- unhandled_29_worker;
    xml result1 = <- unhandled_30_worker;
    xml result = result0 + result1;
    return result;
}

function receiveEvent_28(xml input, map<xml> context) returns xml {
    return input;
}

function unhandled_29(xml input, map<xml> context) returns xml { // comment
    //Unknown extension kind: bw.http.sendHTTPResponse
    return input;
}

function unhandled_30(xml input, map<xml> context) returns xml { // comment
    //Unknown extension kind: bw.jdbc.JDBCQuery
    return input;
}
