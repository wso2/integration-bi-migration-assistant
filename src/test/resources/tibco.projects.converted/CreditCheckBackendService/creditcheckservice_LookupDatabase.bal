import ballerina/sql;
import ballerina/xslt;

function activityExtension(xml input, map<xml> context) returns xml {
    xml var0 = checkpanic xslt:transform(input, xml `<?xml version="1.0" encoding="UTF-8"?>
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
</xsl:stylesheet>`, context);
    return var0;
}

function activityExtension_7(xml input, map<xml> context) returns xml {
    xml var0 = checkpanic xslt:transform(input, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://www.tibco.com/namespaces/tnt/plugins/jdbc+b75f079e-d363-4c28-9b66-44009f6eacf8+input" xmlns:tns1="http://www.example.com/namespaces/tns/1535845694732" version="2.0"><xsl:param name="Start"/><xsl:template name="JDBCQuery-input" match="/"><tns:jdbcQueryActivityInput><ssn><xsl:value-of select="$Start/tns1:ssn"/></ssn></tns:jdbcQueryActivityInput></xsl:template></xsl:stylesheet>`, context);
    QueryData0 data = convertToQueryData0(var0);
    sql:ParameterizedQuery var1 = `select * from public.creditscore where ssn like ${data.ssn}`;
    sql:ExecutionResult var2 = checkpanic jdbcProperty->execute(var1);
    return var0;
}

function activityExtension_8(xml input, map<xml> context) returns xml {
    xml var0 = checkpanic xslt:transform(input, xml `<?xml version="1.0" encoding="UTF-8"?>
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
</xsl:stylesheet>`, context);
    QueryData1 data = convertToQueryData1(var0);
    sql:ParameterizedQuery var1 = `UPDATE creditscore
  SET numofpulls = ${data.noOfPulls}
  WHERE ssn like ${data.ssn}`;
    sql:ExecutionResult var2 = checkpanic jdbcProperty->execute(var1);
    return var0;
}

function creditcheckservice_LookupDatabase_start(anydata input) returns anydata {
    xml inputXML = toXML(input);
    xml xmlResult = process_creditcheckservice_LookupDatabase(inputXML);
    anydata result = convertToanydata(xmlResult);
    return result;
}

function process_creditcheckservice_LookupDatabase(xml input) returns xml {
    map<xml> context = {};
    context["post.item"] = input;
    worker start_worker {
        xml result0 = receiveEvent(input, context);
        result0 -> StartToEnd;
    }
    worker JDBCQueryToEnd {
        xml result0 = <- activityExtension_7_worker;
        result0 -> activityExtension_8_worker;
    }
    worker JDBCUpdateToEnd {
        xml result0 = <- activityExtension_8_worker;
        result0 -> activityExtension_worker;
    }
    worker QueryRecordsToThrow {
        xml result0 = <- activityExtension_7_worker;
        result0 -> unhandled_9_worker;
    }
    worker StartToEnd {
        xml result0 = <- start_worker;
        result0 -> activityExtension_7_worker;
    }
    worker activityExtension_7_worker {
        xml input0 = <- StartToEnd;
        xml combinedInput = input0;
        xml output = activityExtension_7(combinedInput, context);
        output -> JDBCQueryToEnd;
        output -> QueryRecordsToThrow;
    }
    worker activityExtension_8_worker {
        xml input0 = <- JDBCQueryToEnd;
        xml combinedInput = input0;
        xml output = activityExtension_8(combinedInput, context);
        output -> JDBCUpdateToEnd;
    }
    worker activityExtension_worker {
        xml input0 = <- JDBCUpdateToEnd;
        xml combinedInput = input0;
        xml output = activityExtension(combinedInput, context);
        output -> function;
    }
    worker unhandled_9_worker {
        xml input0 = <- QueryRecordsToThrow;
        xml combinedInput = input0;
        xml output = unhandled_9(combinedInput, context);
        output -> function;
    }
    xml result0 = <- activityExtension_worker;
    xml result1 = <- unhandled_9_worker;
    xml result = result0 + result1;
    return result;
}

function receiveEvent(xml input, map<xml> context) returns xml {
    return input;
}

function unhandled_9(xml input, map<xml> context) returns xml {
    //[ParseError] : Unsupported activity tag: throw
    //<bpws:throw faultName="ns8:DefaultFault" faultVariable="Throw-input" name="Throw" xmlns:tibex="http://www.tibco.com/bpel/2007/extensions" tibex:xpdlId="f4da282c-448a-48bb-b3ef-7d2e196a9ba8" xmlns:bpws="http://docs.oasis-open.org/wsbpel/2.0/process/executable">
    //    <tibex:inputBindings>
    //        <tibex:partBinding expression="&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;&lt;xsl:stylesheet xmlns:xsl=&quot;http://www.w3.org/1999/XSL/Transform&quot; xmlns:tns=&quot;http://schemas.tibco.com/bw/plugins/basic/6.0/Exceptions&quot; version=&quot;2.0&quot;&gt;&lt;xsl:template name=&quot;Throw-input&quot; match=&quot;/&quot;&gt;&lt;tns:DefaultFault/&gt;&lt;/xsl:template&gt; &lt;/xsl:stylesheet&gt;" expressionLanguage="urn:oasis:names:tc:wsbpel:2.0:sublang:xslt1.0"/>
    //    </tibex:inputBindings>
    //    <bpws:targets>
    //        <bpws:target linkName="QueryRecordsToThrow"/>
    //    </bpws:targets>
    //</bpws:throw>
    return input;
}
