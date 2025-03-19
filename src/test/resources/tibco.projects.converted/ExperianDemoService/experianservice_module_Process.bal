import ballerina/http;
import ballerina/sql;
import ballerina/xslt;

listener http:Listener experianservice_module_Process_listener = new (8080, {host: "localhost"});

service /Creditscore on experianservice_module_Process_listener {
    resource function post creditscore(InputElement input) returns ExperianResponseSchemaElement|http:NotFound|http:InternalServerError {
        return experianservice_module_Process_start(input);
    }
}

function activityExtension(xml input, map<xml> context) returns xml {
    xml var0 = checkpanic xslt:transform(input, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns2="http://www.tibco.com/namespaces/tnt/plugins/jdbc+1d5225ab-4bc8-4898-8f74-01e4317c3e29+input" xmlns:tns="http://tns.tibco.com/bw/activity/jsonRender/xsd/input/55832ae5-2a37-4b37-8392-a64537f49367" version="2.0"><xsl:param name="ParseJSON"/><xsl:template name="JDBCQuery-input" match="/"><tns2:jdbcQueryActivityInput><ssn><xsl:value-of select="$ParseJSON/tns:ssn"/></ssn></tns2:jdbcQueryActivityInput></xsl:template></xsl:stylesheet>`, context);
    QueryData0 data = convertToQueryData0(var0);
    sql:ParameterizedQuery var1 = `SELECT *
  FROM public.creditscore where ssn like ${data.ssn}
`;
    sql:ExecutionResult var2 = checkpanic jdbcProperty->execute(var1);
    return var0;
}

function activityExtension_3(xml input, map<xml> context) returns xml {
    xml var0 = checkpanic xslt:transform(input, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="activity.jsonParser.input+f396d921-0bc7-459d-ae81-0eb1a0f94723+ActivityInputType" version="2.0"><xsl:param name="HTTPReceiver"/><xsl:template name="ParseJSON-input" match="/"><tns:ActivityInputClass><jsonString><xsl:value-of select="$HTTPReceiver/PostData"/></jsonString></tns:ActivityInputClass></xsl:template></xsl:stylesheet>`, context);
    context["ParseJSON"] = var0;
    return var0;
}

function activityExtension_4(xml input, map<xml> context) returns xml {
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
        xml result0 = receiveEvent(input, context);
        result0 -> HTTPReceiverToSendHTTPResponse;
        xml result1 = unhandled(input, context);
    }
    worker HTTPReceiverToSendHTTPResponse {
        xml result0 = <- start_worker;
        result0 -> activityExtension_3_worker;
    }
    worker JDBCQueryToSendHTTPResponse {
        xml result0 = <- activityExtension_worker;
        result0 -> activityExtension_4_worker;
    }
    worker ParseJSONToJDBCQuery {
        xml result0 = <- activityExtension_3_worker;
        result0 -> activityExtension_worker;
    }
    worker activityExtension_3_worker {
        xml input0 = <- HTTPReceiverToSendHTTPResponse;
        xml combinedInput = input0;
        xml output = activityExtension_3(combinedInput, context);
        output -> ParseJSONToJDBCQuery;
    }
    worker activityExtension_4_worker {
        xml input0 = <- JDBCQueryToSendHTTPResponse;
        xml combinedInput = input0;
        xml output = activityExtension_4(combinedInput, context);
        output -> RenderJSONToSendHTTPResponse;
    }
    worker activityExtension_worker {
        xml input0 = <- ParseJSONToJDBCQuery;
        xml combinedInput = input0;
        xml output = activityExtension(combinedInput, context);
        output -> JDBCQueryToSendHTTPResponse;
    }
    xml result0 = <- unhandled_worker;
    xml result = result0;
    return result;
}

function receiveEvent(xml input, map<xml> context) returns xml {
    return input;
}

function unhandled(xml input, map<xml> context) returns xml {
    //Unknown extension kind: bw.http.sendHTTPResponse
    return input;
}
