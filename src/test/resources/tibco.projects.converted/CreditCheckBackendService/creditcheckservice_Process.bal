import ballerina/http;
import ballerina/xslt;

const string client_404_NotFound = "Not Found";
public listener http:Listener creditcheckservice_Process_listener = new (8080, {host: "localhost"});

service /CreditScore on creditcheckservice_Process_listener {
    resource function post creditscore(Request input) returns Response|http:NotFound|http:InternalServerError|client_404_NotFound {
        return creditcheckservice_Process_start(input);
    }
}

service / on creditcheckservice_Process_listener {
    resource function get creditscore(httpHeaders input) returns Response|http:NotFound|http:InternalServerError {
        return creditcheckservice_Process_start(input);
    }
}

function activityExtension(xml input, map<xml> context) returns xml|error {
    xml var0 = check xslt:transform(input, transformXSLT(xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://www.tibco.com/pe/WriteToLogActivitySchema" version="2.0"><xsl:template name="LogSuccess-input" match="/"><tns:ActivityInput><message><xsl:value-of select="'Invoation Successful'"/></message></tns:ActivityInput></xsl:template></xsl:stylesheet>`), context);
    LogParametersType var1 = convertToLogParametersType(var0);
    logWrapper(var1);
    return var0;
}

function activityExtension_5(xml input, map<xml> context) returns xml|error {
    xml var0 = check xslt:transform(input, transformXSLT(xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://www.tibco.com/pe/WriteToLogActivitySchema" version="2.0"><xsl:template name="LogFailure-input" match="/"><tns:ActivityInput><message><xsl:value-of select="'Invocation Failed'"/></message></tns:ActivityInput></xsl:template></xsl:stylesheet>`), context);
    LogParametersType var1 = convertToLogParametersType(var0);
    logWrapper(var1);
    return var0;
}

function creditcheckservice_Process_start(httpHeaders input) returns Response {
    xml inputXML = checkpanic toXML(input);
    xml xmlResult = process_creditcheckservice_Process(inputXML);
    Response result = convertToResponse(xmlResult);
    return result;
}

function extActivity(xml input, map<xml> context) returns xml|error {
    xml var0 = check xslt:transform(input, transformXSLT(xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns2="/T1535753828744Converted/JsonSchema" xmlns:tns3="http://www.example.com/namespaces/tns/1535845694732" version="2.0">
    <xsl:param name="post.item"/>
    <xsl:template name="LookupDatabase-input" match="/">
        <tns3:Element>
            <tns3:ssn>
                <xsl:value-of select="$post.item/tns2:SSN"/>
            </tns3:ssn>
        </tns3:Element>
    </xsl:template>
</xsl:stylesheet>`), context);
    xml var1 = check toXML(check trap creditcheckservice_LookupDatabase_start(convertToanydata(var0)));
    addToContext(context, "LookupDatabase", var1);
    return var1;
}

function faultHandler(xml input, map<xml> context) returns xml|error {
    return input;
}

function pick(xml input, map<xml> context) returns xml|error {
    return input;
}

function process_creditcheckservice_Process(xml input) returns xml {
    map<xml> context = {};
    addToContext(context, "post.item", input);
    worker start_worker {
        xml|error result0 = extActivity(input, context);
        if result0 is error {
            result0 -> errorHandler;
            return;
        }
        result0 -> LookupDatabaseToLogSuccess_Name;
    }
    worker LogToReply {
        error:NoMessage|xml input = <- errorHandler;
        if input is error:NoMessage {
            return;
        }
        input -> reply_6_worker;
    }
    worker LogTopostOut {
        error:NoMessage|xml input = <- activityExtension_worker;
        if input is error:NoMessage {
            return;
        }
        input -> reply_worker;
    }
    worker LookupDatabaseToLogSuccess_Name {
        error:NoMessage|xml input = <- start_worker;
        if input is error:NoMessage {
            return;
        }
        input -> activityExtension_worker;
    }
    worker activityExtension_worker {
        error:NoMessage|xml inputVal = <- LookupDatabaseToLogSuccess_Name;
        if inputVal is error:NoMessage {
            return;
        }
        xml|error output = activityExtension(inputVal, context);
        if output is error {
            output -> errorHandler;
            return;
        }
        output -> LogTopostOut;
    }
    worker reply_6_worker {
        error:NoMessage|xml inputVal = <- LogToReply;
        if inputVal is error:NoMessage {
            return;
        }
        xml|error output = reply_6(inputVal, context);
        if output is error {
            output -> errorHandler;
            return;
        }
        output -> function;
    }
    worker reply_worker {
        error:NoMessage|xml inputVal = <- LogTopostOut;
        if inputVal is error:NoMessage {
            return;
        }
        xml|error output = reply(inputVal, context);
        if output is error {
            output -> errorHandler;
            return;
        }
        output -> function;
    }
    worker errorHandler {
        error result = <- activityExtension_worker | extActivity_worker | reply_6_worker | reply_worker;
        xml errorXML = xml `<error>${result.message()}</error>`;
        errorXML -> LogToReply;
    }
    error:NoMessage|xml result = <- reply_6_worker | reply_worker;
    xml result_clean = result is error ? xml `` : result;
    return result_clean;
}

function reply(xml input, map<xml> context) returns xml|error {
    xml var0 = check xslt:transform(input, transformXSLT(xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns1="http://xmlns.example.com/20180831151624PLT" xmlns:tns2="/T1535753828744Converted/JsonSchema" version="2.0">
    <xsl:param name="LookupDatabase"/>
    <xsl:template name="postOut-input" match="/">
        <tns2:Response>
            <xsl:if test="$LookupDatabase/tns2:FICOScore">
                <tns2:FICOScore>
                    <xsl:value-of select="$LookupDatabase/tns2:FICOScore"/>
                </tns2:FICOScore>
            </xsl:if>
            <xsl:if test="$LookupDatabase/tns2:Rating">
                <tns2:Rating>
                    <xsl:value-of select="$LookupDatabase/tns2:Rating"/>
                </tns2:Rating>
            </xsl:if>
            <xsl:if test="$LookupDatabase/tns2:NoOfInquiries">
                <tns2:NoOfInquiries>
                    <xsl:value-of select="$LookupDatabase/tns2:NoOfInquiries"/>
                </tns2:NoOfInquiries>
            </xsl:if>
        </tns2:Response>
    </xsl:template>
</xsl:stylesheet>`), context);
    xml var1 = check xslt:transform(var0, transformXSLT(xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns1="http://xmlns.example.com/20180831151624PLT" xmlns:tns2="/T1535753828744Converted/JsonSchema" version="2.0"><xsl:param name="LookupDatabase"/><xsl:template name="postOut-input" match="/"><tns1:postResponse><item><tns2:Response><xsl:if test="$LookupDatabase/tns2:FICOScore"><tns2:FICOScore><xsl:value-of select="$LookupDatabase/tns2:FICOScore"/></tns2:FICOScore></xsl:if><xsl:if test="$LookupDatabase/tns2:Rating"><tns2:Rating><xsl:value-of select="$LookupDatabase/tns2:Rating"/></tns2:Rating></xsl:if><xsl:if test="$LookupDatabase/tns2:NoOfInquiries"><tns2:NoOfInquiries><xsl:value-of select="$LookupDatabase/tns2:NoOfInquiries"/></tns2:NoOfInquiries></xsl:if></tns2:Response></item></tns1:postResponse></xsl:template></xsl:stylesheet>`), context);
    return var1;
}

function reply_6(xml input, map<xml> context) returns xml|error {
    xml var0 = check xslt:transform(input, transformXSLT(xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns1="http://xmlns.example.com/20180831151624PLT" xmlns:tns="http://tns.tibco.com/bw/REST" version="2.0"><xsl:template name="Reply-input" match="/"><tns1:post4XXFaultMessage><clientError><tns:client4XXError><statusCode><xsl:value-of select="404"/></statusCode></tns:client4XXError></clientError></tns1:post4XXFaultMessage></xsl:template></xsl:stylesheet>`), context);
    xml var1 = check xslt:transform(var0, transformXSLT(xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns1="http://xmlns.example.com/20180831151624PLT" xmlns:tns="http://tns.tibco.com/bw/REST" version="2.0">
    <xsl:template name="Reply-input" match="/">
        <tns:client4XXError>
            <statusCode>
                <xsl:value-of select="404"/>
            </statusCode>
        </tns:client4XXError>
    </xsl:template>
</xsl:stylesheet>`), context);
    return var1;
}
