import ballerina/http;
import ballerina/sql;
import ballerina/xslt;

http:Client experianservice_module_Process_client = checkpanic new ("localhost:8080/Creditscore/creditscore");
public listener http:Listener experianservice_module_Process_listener = new (8080, {host: "localhost"});

service /Creditscore on experianservice_module_Process_listener {
    resource function post creditscore(InputElement|xml req) returns ExperianResponseSchemaElement|http:NotFound|http:InternalServerError {
        InputElement|error input = tryBindToInputElement(req);
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
        return start_experianservice_module_Process(input, paramXML);
    }
}

function activityExtension(map<xml> context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns1="http://tns.tibco.com/bw/activity/sendhttpresponse/xsd/input+3847aa9b-8275-4b15-9ea8-812816768fa4+ResponseActivityInput" version="2.0"><xsl:param name="RenderJSON"/><xsl:template name="SendHTTPResponse-input" match="/"><tns1:ResponseActivityInput><asciiContent><xsl:value-of select="$RenderJSON/root/jsonString"/></asciiContent><Headers><Content-Type><xsl:value-of select="'application/json'"/></Content-Type></Headers></tns1:ResponseActivityInput></xsl:template></xsl:stylesheet>`, context);
    return var1;
}

function activityExtension_2(map<xml> context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns2="http://www.tibco.com/namespaces/tnt/plugins/jdbc+1d5225ab-4bc8-4898-8f74-01e4317c3e29+input" xmlns:tns="http://tns.tibco.com/bw/activity/jsonRender/xsd/input/55832ae5-2a37-4b37-8392-a64537f49367" version="2.0"><xsl:param name="ParseJSON"/><xsl:template name="JDBCQuery-input" match="/"><tns2:jdbcQueryActivityInput><ssn><xsl:value-of select="$ParseJSON/root/tns:ssn"/></ssn></tns2:jdbcQueryActivityInput></xsl:template></xsl:stylesheet>`, context);
    string ssn = (var1/<ssn>/*).toString().trim();
    sql:ParameterizedQuery var2 = `SELECT *
  FROM public.creditscore where ssn like ${ssn}
`;
    stream<map<anydata>, error|()> var3 = experianservice_module_JDBCConnectionResource->query(var2);
    xml var4 = xml ``;
    check from var each in var3
        do {
            xml var5 = check toXML(each);
            var4 = var4 + var5;
        };
    xml var6 = xml `<root>${var4}</root>`;
    addToContext(context, "JDBCQuery", var6);
    return var6;
}

function activityExtension_3(map<xml> context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="activity.jsonParser.input+f396d921-0bc7-459d-ae81-0eb1a0f94723+ActivityInputType" version="2.0"><xsl:param name="HTTPReceiver"/><xsl:template name="ParseJSON-input" match="/"><tns:ActivityInputClass><jsonString><xsl:value-of select="$HTTPReceiver/root/PostData"/></jsonString></tns:ActivityInputClass></xsl:template></xsl:stylesheet>`, context);
    xml var2 = check renderJsonAsInputElementXML(var1);
    addToContext(context, "ParseJSON", var2);
    return var2;
}

function activityExtension_4(map<xml> context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://tns.tibco.com/bw/json/1535671685533" version="2.0"><xsl:param name="JDBCQuery"/><xsl:template name="RenderJSON-input" match="/"><tns:ExperianResponseSchemaElement><xsl:if test="$JDBCQuery/root/Record[1]/ficoscore"><tns:fiCOScore><xsl:value-of select="$JDBCQuery/root/Record[1]/ficoscore"/></tns:fiCOScore></xsl:if><xsl:if test="$JDBCQuery/root/Record[1]/rating"><tns:rating><xsl:value-of select="$JDBCQuery/root/Record[1]/rating"/></tns:rating></xsl:if><xsl:if test="$JDBCQuery/root/Record[1]/numofpulls"><tns:noOfInquiries><xsl:value-of select="$JDBCQuery/root/Record[1]/numofpulls"/></tns:noOfInquiries></xsl:if></tns:ExperianResponseSchemaElement></xsl:template></xsl:stylesheet>`, context);
    xml var2 = renderJson(var1);
    addToContext(context, "RenderJSON", var2);
    return var2;
}

function receiveEvent(map<xml> context) returns xml|error {
    addToContext(context, "HTTPReceiver", context.get("$input"));
    return context.get("$input");
}

function scope_9ActivityRunner(map<xml> cx) returns xml|error {
    xml result0 = check receiveEvent(cx);
    xml result1 = check activityExtension_3(cx);
    xml result2 = check activityExtension_2(cx);
    xml result3 = check activityExtension_4(cx);
    xml result4 = check activityExtension(cx);
    return result4;
}

function scope_9FaultHandler(error err, map<xml> cx) returns xml {
    panic err;
}

function scope_9ScopeFn(xml input, map<xml> params) returns xml {
    map<xml> context = {...params};
    addToContext(context, "$input", input);
    xml|error result = scope_9ActivityRunner(context);
    if result is error {
        return scope_9FaultHandler(result, context);
    }
    return result;
}

function start_experianservice_module_Process(InputElement input, map<xml> params = {}) returns ExperianResponseSchemaElement {
    xml inputXML = input is map<anydata> ? checkpanic toXML(input) : xml ``;
    xml xmlResult = scope_9ScopeFn(inputXML, params);
    ExperianResponseSchemaElement result = convertToExperianResponseSchemaElement(xmlResult);
    return result;
}
