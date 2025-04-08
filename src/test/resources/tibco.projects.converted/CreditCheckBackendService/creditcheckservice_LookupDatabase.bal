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
    sql:ExecutionResult var2 = checkpanic creditcheckservice_JDBCConnectionResource->execute(var1);
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
    sql:ExecutionResult var2 = checkpanic creditcheckservice_JDBCConnectionResource->execute(var1);
    return var0;
}

function activityRunner_creditcheckservice_LookupDatabase(xml input, map<xml> cx) returns xml|error {
    xml result0 = check receiveEvent(input, cx);
    xml result1 = check activityExtension_9(result0, cx);
    xml result2;
    if predicate_1(result1) {
        result2 = check throw(result1, cx);
    } else {
        result2 = result1;
    }
    xml result3;
    if predicate_0(result1) {
        result3 = check activityExtension_10(result2, cx);
    } else {
        result3 = result2;
    }
    xml result4 = check activityExtension_8(result3, cx);
    return result4;
}

function creditcheckservice_LookupDatabase_start(Element input) returns Response {
    xml inputXML = checkpanic toXML(input);
    xml xmlResult = process_creditcheckservice_LookupDatabase(inputXML);
    Response result = convertToResponse(xmlResult);
    return result;
}

function errorHandler_creditcheckservice_LookupDatabase(error err, map<xml> cx) returns xml {
    checkpanic err;
}

function predicate_0(xml input) returns boolean {
    return test(input, "string-length($QueryRecords/Record[1]/rating)>0");
}

function predicate_1(xml input) returns boolean {
    return !test(input, "string-length($QueryRecords/Record[1]/rating)>0");
}

function process_creditcheckservice_LookupDatabase(xml input) returns xml {
    map<xml> context = {};
    addToContext(context, "post.item", input);
    xml|error result = activityRunner_creditcheckservice_LookupDatabase(input, context);
    if result is error {
        return errorHandler_creditcheckservice_LookupDatabase(result, context);
    }
    return result;
}

function receiveEvent(xml input, map<xml> context) returns xml|error {
    return input;
}

function throw(xml input, map<xml> context) returns xml|error {
    xml var0 = check xslt:transform(input, transformXSLT(xml `<?xml version="1.0" encoding="UTF-8"?><xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://schemas.tibco.com/bw/plugins/basic/6.0/Exceptions" version="2.0"><xsl:template name="Throw-input" match="/"><tns:DefaultFault/></xsl:template> </xsl:stylesheet>`), context);
    error var1 = error("TODO: create error value");
    panic var1;
}
