import ballerina/data.xmldata;
import ballerina/sql;
import ballerina/xslt;

function activityExtension_10(map<xml> context) returns xml|error {
    xml var0 = context.get("UpdatePulls-input");
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns3="http://www.tibco.com/namespaces/tnt/plugins/jdbc+21902290-4882-46a2-8795-b85989c9d7c0+input" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:tns1="http://www.example.com/namespaces/tns/1535845694732" version="2.0">
    <xsl:param name="Start"/>
    <xsl:param name="QueryRecords"/>
    <xsl:template name="JDBCUpdate-input" match="/">
        <tns3:jdbcUpdateActivityInput>
            <noOfPulls>
                <xsl:value-of select="xsd:int($QueryRecords/root/Record[1]/numofpulls + 1)"/>
            </noOfPulls>
            <ssn>
                <xsl:value-of select="$Start/root/tns1:ssn"/>
            </ssn>
        </tns3:jdbcUpdateActivityInput>
    </xsl:template>
</xsl:stylesheet>`, context);
    string noOfPulls = (var1/<noOfPulls>/*).toString().trim();
    string ssn = (var1/<ssn>/*).toString().trim();
    sql:ParameterizedQuery var2 = `UPDATE creditscore
  SET numofpulls = ${noOfPulls}
  WHERE ssn like ${ssn}`;
    sql:ExecutionResult var3 = check creditcheckservice_JDBCConnectionResource->execute(var2);
    xml var4 = xml `<root></root>`;
    addToContext(context, "UpdatePulls", var4);
    return var4;
}

function activityExtension_8(map<xml> context) returns xml|error {
    xml var0 = context.get("End-input");
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns2="/T1535753828744Converted/JsonSchema" version="2.0">
    <xsl:param name="QueryRecords"/>
    <xsl:template name="End-input" match="/">
        <tns2:Response>
            <xsl:if test="$QueryRecords/root/Record[1]/ficoscore">
                <tns2:FICOScore>
                    <xsl:value-of select="$QueryRecords/root/Record[1]/ficoscore"/>
                </tns2:FICOScore>
            </xsl:if>
            <xsl:if test="$QueryRecords/root/Record[1]/rating">
                <tns2:Rating>
                    <xsl:value-of select="$QueryRecords/root/Record[1]/rating"/>
                </tns2:Rating>
            </xsl:if>
            <xsl:if test="$QueryRecords/root/Record[1]/numofpulls">
                <tns2:NoOfInquiries>
                    <xsl:value-of select="$QueryRecords/root/Record[1]/numofpulls"/>
                </tns2:NoOfInquiries>
            </xsl:if>
        </tns2:Response>
    </xsl:template>
</xsl:stylesheet>`, context);
    return var1;
}

function activityExtension_9(map<xml> context) returns xml|error {
    xml var0 = context.get("QueryRecords-input");
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://www.tibco.com/namespaces/tnt/plugins/jdbc+b75f079e-d363-4c28-9b66-44009f6eacf8+input" xmlns:tns1="http://www.example.com/namespaces/tns/1535845694732" version="2.0"><xsl:param name="Start"/><xsl:template name="JDBCQuery-input" match="/"><tns:jdbcQueryActivityInput><ssn><xsl:value-of select="$Start/root/tns1:ssn"/></ssn></tns:jdbcQueryActivityInput></xsl:template></xsl:stylesheet>`, context);
    string ssn = (var1/<ssn>/*).toString().trim();
    sql:ParameterizedQuery var2 = `select * from public.creditscore where ssn like ${ssn}`;
    stream<map<anydata>, error|()> var3 = creditcheckservice_JDBCConnectionResource->query(var2);
    xml var4 = xml ``;
    check from var each in var3
        do {
            xml var5 = check toXML(each);
            var4 = var4 + var5;
        };
    xml var6 = xml `<root>${var4}</root>`;
    addToContext(context, "QueryRecords", var6);
    return var6;
}

function predicate_0(xml input, map<xml> cx) returns boolean {
    return xmldata:transform(input, `string-length(${cx.get("QueryRecords")}/Record[1]/rating)>0`);
}

function predicate_1(xml input, map<xml> cx) returns boolean {
    return !xmldata:transform(input, `string-length(${cx.get("QueryRecords")}/Record[1]/rating)>0`);
}

function receiveEvent(map<xml> context) returns xml|error {
    addToContext(context, "Start", context.get("$input"));
    return context.get("$input");
}

function scope_3ActivityRunner(map<xml> cx) returns xml|error {
    xml result0 = check receiveEvent(cx);
    xml result1 = check activityExtension_9(cx);
    xml result2;
    if predicate_1(result1, cx) {
        result2 = check throw(cx);
    } else {
        result2 = result1;
    }
    xml result3;
    if predicate_0(result1, cx) {
        result3 = check activityExtension_10(cx);
    } else {
        result3 = result2;
    }
    xml result4 = check activityExtension_8(cx);
    return result4;
}

function scope_3FaultHandler(error err, map<xml> cx) returns xml {
    panic err;
}

function scope_3ScopeFn(xml input, map<xml> params) returns xml {
    map<xml> context = {...params};
    addToContext(context, "$input", input);
    xml|error result = scope_3ActivityRunner(context);
    if result is error {
        return scope_3FaultHandler(result, context);
    }
    return result;
}

function start_creditcheckservice_LookupDatabase(Element input, map<xml> params = {}) returns Response {
    xml inputXML = input is map<anydata> ? checkpanic toXML(input) : xml ``;
    xml xmlResult = scope_3ScopeFn(inputXML, params);
    Response result = convertToResponse(xmlResult);
    return result;
}

function throw(map<xml> context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?><xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://schemas.tibco.com/bw/plugins/basic/6.0/Exceptions" version="2.0"><xsl:template name="Throw-input" match="/"><tns:DefaultFault/></xsl:template> </xsl:stylesheet>`, context);
    xml var2 = xml `<root>${var1}</root>`;
    error var3 = error("TODO: create error value");
    panic var3;
}
