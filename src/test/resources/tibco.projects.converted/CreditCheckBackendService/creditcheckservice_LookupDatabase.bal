import ballerina/xslt;

function activityExtension_24(xml input, map<xml> context) returns xml {
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
        xml result0 = receiveEvent_23(input, context);
        result0 -> StartToEnd;
        xml result1 = unhandled_25(input, context);
        xml result2 = unhandled_26(input, context);
    }
    worker JDBCUpdateToEnd {
    }
    worker QueryRecordsToThrow {
    }
    worker activityExtension_24_worker {
        xml input0 = <- JDBCUpdateToEnd;
        xml combinedInput = input0;
        xml output = activityExtension_24(combinedInput, context);
        output -> function;
    }
    worker unhandled_27_worker {
        xml input0 = <- QueryRecordsToThrow;
        xml combinedInput = input0;
        xml output = unhandled_27(combinedInput, context);
        output -> function;
    }
    xml result0 = <- activityExtension_24_worker;
    xml result1 = <- unhandled_25_worker;
    xml result2 = <- unhandled_26_worker;
    xml result3 = <- unhandled_27_worker;
    xml result = result0 + result1 + result2 + result3;
    return result;
}

function receiveEvent_23(xml input, map<xml> context) returns xml {
    return input;
}

function unhandled_25(xml input, map<xml> context) returns xml { // comment
    //Unknown extension kind: bw.jdbc.JDBCQuery
    return input;
}

function unhandled_26(xml input, map<xml> context) returns xml { // comment
    //Unknown extension kind: bw.jdbc.update
    return input;
}

function unhandled_27(xml input, map<xml> context) returns xml { // comment
    //[ParseError] : Unsupported activity tag: throw
    // comment
    //<bpws:throw faultName="ns8:DefaultFault" faultVariable="Throw-input" name="Throw" xmlns:tibex="http://www.tibco.com/bpel/2007/extensions" tibex:xpdlId="f4da282c-448a-48bb-b3ef-7d2e196a9ba8" xmlns:bpws="http://docs.oasis-open.org/wsbpel/2.0/process/executable">
    // comment
    //                    
    // comment
    //    <tibex:inputBindings>
    // comment
    //                            
    // comment
    //        <tibex:partBinding expression="&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;&lt;xsl:stylesheet xmlns:xsl=&quot;http://www.w3.org/1999/XSL/Transform&quot; xmlns:tns=&quot;http://schemas.tibco.com/bw/plugins/basic/6.0/Exceptions&quot; version=&quot;2.0&quot;&gt;&lt;xsl:template name=&quot;Throw-input&quot; match=&quot;/&quot;&gt;&lt;tns:DefaultFault/&gt;&lt;/xsl:template&gt; &lt;/xsl:stylesheet&gt;" expressionLanguage="urn:oasis:names:tc:wsbpel:2.0:sublang:xslt1.0"/>
    // comment
    //                        
    // comment
    //    </tibex:inputBindings>
    // comment
    //                    
    // comment
    //    <bpws:targets>
    // comment
    //                            
    // comment
    //        <bpws:target linkName="QueryRecordsToThrow"/>
    // comment
    //                        
    // comment
    //    </bpws:targets>
    // comment
    //                
    // comment
    //</bpws:throw>
    return input;
}
