import ballerina/sql;
import ballerina/xslt;

function activityExtension_10(xml input, map<xml> context) returns xml|error {
    xml var0 = check xslt:transform(input, transformXSLT(xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns3="http://www.tibco.com/namespaces/tnt/plugins/jdbc+21902290-4882-46a2-8795-b85989c9d7c0+input" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:tns1="http://www.example.com/namespaces/tns/1535845694732" version="2.0">
    <xsl:param name="Start"/>
    <xsl:param name="QueryRecords"/>
    <xsl:template name="JDBCUpdate-input" match="/">
        <tns3:jdbcUpdateActivityInput>
            <noOfPulls>
                <xsl:value-of select="xsd:int($QueryRecords/Record[1]/numofpulls + 1)"/>
            </noOfPulls>
            <ssn>
                <xsl:value-of select="$Start/tns1:ssn"/>
            </ssn>
        </tns3:jdbcUpdateActivityInput>
    </xsl:template>
</xsl:stylesheet>`), context);
    QueryData1 data = convertToQueryData1(var0);
    sql:ParameterizedQuery var1 = `UPDATE creditscore
  SET numofpulls = ${data.noOfPulls}
  WHERE ssn like ${data.ssn}`;
    sql:ExecutionResult var2 = checkpanic jdbcProperty->execute(var1);
    return var0;
}

function activityExtension_8(xml input, map<xml> context) returns xml|error {
    xml var0 = check xslt:transform(input, transformXSLT(xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns2="/T1535753828744Converted/JsonSchema" version="2.0">
    <xsl:param name="QueryRecords"/>
    <xsl:template name="End-input" match="/">
        <tns2:Response>
            <xsl:if test="$QueryRecords/Record[1]/ficoscore">
                <tns2:FICOScore>
                    <xsl:value-of select="$QueryRecords/Record[1]/ficoscore"/>
                </tns2:FICOScore>
            </xsl:if>
            <xsl:if test="$QueryRecords/Record[1]/rating">
                <tns2:Rating>
                    <xsl:value-of select="$QueryRecords/Record[1]/rating"/>
                </tns2:Rating>
            </xsl:if>
            <xsl:if test="$QueryRecords/Record[1]/numofpulls">
                <tns2:NoOfInquiries>
                    <xsl:value-of select="$QueryRecords/Record[1]/numofpulls"/>
                </tns2:NoOfInquiries>
            </xsl:if>
        </tns2:Response>
    </xsl:template>
</xsl:stylesheet>`), context);
    return var0;
}

function activityExtension_9(xml input, map<xml> context) returns xml|error {
    xml var0 = check xslt:transform(input, transformXSLT(xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://www.tibco.com/namespaces/tnt/plugins/jdbc+b75f079e-d363-4c28-9b66-44009f6eacf8+input" xmlns:tns1="http://www.example.com/namespaces/tns/1535845694732" version="2.0"><xsl:param name="Start"/><xsl:template name="JDBCQuery-input" match="/"><tns:jdbcQueryActivityInput><ssn><xsl:value-of select="$Start/tns1:ssn"/></ssn></tns:jdbcQueryActivityInput></xsl:template></xsl:stylesheet>`), context);
    QueryData0 data = convertToQueryData0(var0);
    sql:ParameterizedQuery var1 = `select * from public.creditscore where ssn like ${data.ssn}`;
    sql:ExecutionResult var2 = checkpanic jdbcProperty->execute(var1);
    return var0;
}

function creditcheckservice_LookupDatabase_start(anydata input) returns anydata {
    xml inputXML = checkpanic toXML(input);
    xml xmlResult = process_creditcheckservice_LookupDatabase(inputXML);
    anydata result = convertToanydata(xmlResult);
    return result;
}

function process_creditcheckservice_LookupDatabase(xml input) returns xml {
    map<xml> context = {};
    addToContext(context, "post.item", input);
    worker start_worker {
        xml|error result0 = receiveEvent(input, context);
        if result0 is error {
            result0 -> errorHandler;
            return;
        }
        result0 -> StartToEnd;
    }
    worker JDBCQueryToEnd {
        error:NoMessage|xml result0 = <- activityExtension_9_worker;
        if result0 is error:NoMessage {
            return;
        }
        result0 -> activityExtension_10_worker;
    }
    worker JDBCUpdateToEnd {
        error:NoMessage|xml result0 = <- activityExtension_10_worker;
        if result0 is error:NoMessage {
            return;
        }
        result0 -> activityExtension_8_worker;
    }
    worker QueryRecordsToThrow {
        error:NoMessage|xml result0 = <- activityExtension_9_worker;
        if result0 is error:NoMessage {
            return;
        }
        result0 -> throw_worker;
    }
    worker StartToEnd {
        error:NoMessage|xml result0 = <- start_worker;
        if result0 is error:NoMessage {
            return;
        }
        result0 -> activityExtension_9_worker;
    }
    worker activityExtension_10_worker {
        error:NoMessage|xml input0 = <- JDBCQueryToEnd;
        if input0 is error:NoMessage {
            return;
        }
        xml combinedInput = input0;
        xml|error output = activityExtension_10(combinedInput, context);
        if output is error {
            output -> errorHandler;
            return;
        }
        output -> JDBCUpdateToEnd;
    }
    worker activityExtension_8_worker {
        error:NoMessage|xml input0 = <- JDBCUpdateToEnd;
        if input0 is error:NoMessage {
            return;
        }
        xml combinedInput = input0;
        xml|error output = activityExtension_8(combinedInput, context);
        if output is error {
            output -> errorHandler;
            return;
        }
        output -> function;
    }
    worker activityExtension_9_worker {
        error:NoMessage|xml input0 = <- StartToEnd;
        if input0 is error:NoMessage {
            return;
        }
        xml combinedInput = input0;
        xml|error output = activityExtension_9(combinedInput, context);
        if output is error {
            output -> errorHandler;
            return;
        }
        if test(output, "string-length($QueryRecords/Record[1]/rating)>0") {
            output -> JDBCQueryToEnd;
        } else {
            output -> QueryRecordsToThrow;
        }
    }
    worker throw_worker {
        error:NoMessage|xml input0 = <- QueryRecordsToThrow;
        if input0 is error:NoMessage {
            return;
        }
        xml combinedInput = input0;
        xml|error output = throw(combinedInput, context);
        if output is error {
            output -> errorHandler;
            return;
        }
        output -> function;
    }
    worker errorHandler {
        error result = <- activityExtension_10_worker | activityExtension_8_worker | activityExtension_9_worker | receiveEvent_worker | throw_worker;
        panic result;
    }
    error:NoMessage|xml result = <- activityExtension_8_worker | throw_worker;
    xml result_clean = result is error ? xml `` : result;
    return result_clean;
}

function receiveEvent(xml input, map<xml> context) returns xml|error {
    return input;
}

function throw(xml input, map<xml> context) returns xml|error {
    xml var0 = check xslt:transform(input, transformXSLT(xml `<?xml version="1.0" encoding="UTF-8"?><xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://schemas.tibco.com/bw/plugins/basic/6.0/Exceptions" version="2.0"><xsl:template name="Throw-input" match="/"><tns:DefaultFault/></xsl:template> </xsl:stylesheet>`), context);
    error var1 = error("TODO: create error value");
    panic var1;
}
