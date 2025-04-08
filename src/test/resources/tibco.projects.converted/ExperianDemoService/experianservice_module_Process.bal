import ballerina/http;
import ballerina/sql;
import ballerina/xslt;

public listener http:Listener experianservice_module_Process_listener = new (8080, {host: "localhost"});

service /Creditscore on experianservice_module_Process_listener {
    resource function post creditscore(InputElement input) returns ExperianResponseSchemaElement|http:NotFound|http:InternalServerError {
        return experianservice_module_Process_start(input);
    }
}

function activityExtension(xml input, map<xml> context) returns xml|error {
    xml var0 = check xslt:transform(input, transformXSLT(xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns1="http://tns.tibco.com/bw/activity/sendhttpresponse/xsd/input+3847aa9b-8275-4b15-9ea8-812816768fa4+ResponseActivityInput" version="2.0"><xsl:param name="RenderJSON"/><xsl:template name="SendHTTPResponse-input" match="/"><tns1:ResponseActivityInput><asciiContent><xsl:value-of select="$RenderJSON/jsonString"/></asciiContent><Headers><Content-Type><xsl:value-of select="'application/json'"/></Content-Type></Headers></tns1:ResponseActivityInput></xsl:template></xsl:stylesheet>`), context);
    return var0;
}

function activityExtension_2(xml input, map<xml> context) returns xml|error {
    xml var0 = check xslt:transform(input, transformXSLT(xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns2="http://www.tibco.com/namespaces/tnt/plugins/jdbc+1d5225ab-4bc8-4898-8f74-01e4317c3e29+input" xmlns:tns="http://tns.tibco.com/bw/activity/jsonRender/xsd/input/55832ae5-2a37-4b37-8392-a64537f49367" version="2.0"><xsl:param name="ParseJSON"/><xsl:template name="JDBCQuery-input" match="/"><tns2:jdbcQueryActivityInput><ssn><xsl:value-of select="$ParseJSON/tns:ssn"/></ssn></tns2:jdbcQueryActivityInput></xsl:template></xsl:stylesheet>`), context);
    QueryData0 data = convertToQueryData0(var0);
    sql:ParameterizedQuery var1 = `SELECT *
  FROM public.creditscore where ssn like ${data.ssn}
`;
    sql:ExecutionResult var2 = checkpanic experianservice_module_JDBCConnectionResource->execute(var1);
    return var0;
}

function activityExtension_3(xml input, map<xml> context) returns xml|error {
    xml var0 = check xslt:transform(input, transformXSLT(xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="activity.jsonParser.input+f396d921-0bc7-459d-ae81-0eb1a0f94723+ActivityInputType" version="2.0"><xsl:param name="HTTPReceiver"/><xsl:template name="ParseJSON-input" match="/"><tns:ActivityInputClass><jsonString><xsl:value-of select="$HTTPReceiver/PostData"/></jsonString></tns:ActivityInputClass></xsl:template></xsl:stylesheet>`), context);
    addToContext(context, "ParseJSON", var0);
    return var0;
}

function activityExtension_4(xml input, map<xml> context) returns xml|error {
    xml var0 = check xslt:transform(input, transformXSLT(xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://tns.tibco.com/bw/json/1535671685533" version="2.0"><xsl:param name="JDBCQuery"/><xsl:template name="RenderJSON-input" match="/"><tns:ExperianResponseSchemaElement><xsl:if test="$JDBCQuery/Record[1]/ficoscore"><tns:fiCOScore><xsl:value-of select="$JDBCQuery/Record[1]/ficoscore"/></tns:fiCOScore></xsl:if><xsl:if test="$JDBCQuery/Record[1]/rating"><tns:rating><xsl:value-of select="$JDBCQuery/Record[1]/rating"/></tns:rating></xsl:if><xsl:if test="$JDBCQuery/Record[1]/numofpulls"><tns:noOfInquiries><xsl:value-of select="$JDBCQuery/Record[1]/numofpulls"/></tns:noOfInquiries></xsl:if></tns:ExperianResponseSchemaElement></xsl:template></xsl:stylesheet>`), context);
    addToContext(context, "RenderJSON", var0);
    return var0;
}

function activityRunner_experianservice_module_Process(xml input, map<xml> cx) returns xml|error {
    xml result0 = check receiveEvent(input, cx);
    xml result1 = check activityExtension_3(result0, cx);
    xml result2 = check activityExtension_2(result1, cx);
    xml result3 = check activityExtension_4(result2, cx);
    xml result4 = check activityExtension(result3, cx);
    return result4;
}

function errorHandler_experianservice_module_Process(error err, map<xml> cx) returns xml {
    checkpanic err;
}

function experianservice_module_Process_start(InputElement input) returns ExperianResponseSchemaElement {
    xml inputXML = checkpanic toXML(input);
    xml xmlResult = process_experianservice_module_Process(inputXML);
    ExperianResponseSchemaElement result = convertToExperianResponseSchemaElement(xmlResult);
    return result;
}

function process_experianservice_module_Process(xml input) returns xml {
    map<xml> context = {};
    addToContext(context, "post.item", input);
    xml|error result = activityRunner_experianservice_module_Process(input, context);
    if result is error {
        return errorHandler_experianservice_module_Process(result, context);
    }
    return result;
}

function receiveEvent(xml input, map<xml> context) returns xml|error {
    return input;
}
