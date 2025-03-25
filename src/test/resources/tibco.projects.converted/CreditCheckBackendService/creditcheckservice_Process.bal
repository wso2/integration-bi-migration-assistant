import ballerina/http;
import ballerina/xslt;

const string client_404_NotFound = "Not Found";
listener http:Listener creditcheckservice_Process_listener = new (8080, {host: "localhost"});

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

function activityExtension(xml input, map<xml> context) returns xml {
    xml var0 = checkpanic xslt:transform(input, transformXSLT(xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://www.tibco.com/pe/WriteToLogActivitySchema" version="2.0"><xsl:template name="LogSuccess-input" match="/"><tns:ActivityInput><message><xsl:value-of select="'Invoation Successful'"/></message></tns:ActivityInput></xsl:template></xsl:stylesheet>`), context);
    LogParametersType var1 = convertToLogParametersType(var0);
    logWrapper(var1);
    return var0;
}

function creditcheckservice_Process_start(httpHeaders input) returns Response {
    xml inputXML = toXML(input);
    xml xmlResult = process_creditcheckservice_Process(inputXML);
    Response result = convertToResponse(xmlResult);
    return result;
}

function extActivity(xml input, map<xml> context) returns xml {
    xml var0 = checkpanic xslt:transform(input, transformXSLT(xml `<?xml version="1.0" encoding="UTF-8"?>
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
    xml var1 = toXML(creditcheckservice_LookupDatabase_start(convertToanydata(var0)));
    addToContext(context, "LookupDatabase", var1);
    return var1;
}

function pick(xml input, map<xml> context) returns xml {
    return input;
}

function process_creditcheckservice_Process(xml input) returns xml {
    map<xml> context = {};
    addToContext(context, "post.item", input);
    worker start_worker {
        xml result0 = unhandled(input, context);
        xml result1 = extActivity(input, context);
        result1 -> LookupDatabaseToLogSuccess_Name;
    }
    worker LogTopostOut {
        xml result0 = <- activityExtension_worker;
        result0 -> reply_worker;
    }
    worker LookupDatabaseToLogSuccess_Name {
        xml result0 = <- start_worker;
        result0 -> activityExtension_worker;
    }
    worker activityExtension_worker {
        xml input0 = <- LookupDatabaseToLogSuccess_Name;
        xml combinedInput = input0;
        xml output = activityExtension(combinedInput, context);
        output -> LogTopostOut;
    }
    worker reply_worker {
        xml input0 = <- LogTopostOut;
        xml combinedInput = input0;
        xml output = reply(combinedInput, context);
        output -> function;
    }
    xml result0 = <- unhandled_worker;
    xml result1 = <- reply_worker;
    xml result = result0 + result1;
    return result;
}

function reply(xml input, map<xml> context) returns xml {
    xml var0 = checkpanic xslt:transform(input, transformXSLT(xml `<?xml version="1.0" encoding="UTF-8"?>
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
    xml var1 = checkpanic xslt:transform(var0, transformXSLT(xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns1="http://xmlns.example.com/20180831151624PLT" xmlns:tns2="/T1535753828744Converted/JsonSchema" version="2.0"><xsl:param name="LookupDatabase"/><xsl:template name="postOut-input" match="/"><tns1:postResponse><item><tns2:Response><xsl:if test="$LookupDatabase/tns2:FICOScore"><tns2:FICOScore><xsl:value-of select="$LookupDatabase/tns2:FICOScore"/></tns2:FICOScore></xsl:if><xsl:if test="$LookupDatabase/tns2:Rating"><tns2:Rating><xsl:value-of select="$LookupDatabase/tns2:Rating"/></tns2:Rating></xsl:if><xsl:if test="$LookupDatabase/tns2:NoOfInquiries"><tns2:NoOfInquiries><xsl:value-of select="$LookupDatabase/tns2:NoOfInquiries"/></tns2:NoOfInquiries></xsl:if></tns2:Response></item></tns1:postResponse></xsl:template></xsl:stylesheet>`), context);
    return var1;
}

function unhandled(xml input, map<xml> context) returns xml {
    //[ParseError] : Unsupported activity tag: catchAll
    //<bpws:catchAll xmlns:tibex="http://www.tibco.com/bpel/2007/extensions" tibex:faultDetailsVar="FaultDetails1" tibex:faultNameVar="FaultName1" tibex:xpdlId="4c604bae-a655-499f-a819-3730166f0e11" xmlns:bpws="http://docs.oasis-open.org/wsbpel/2.0/process/executable">
    //    <bpws:scope name="scope2">
    //        <bpws:flow name="flow2">
    //            <bpws:links>
    //                <bpws:link name="LogToReply" tibex:linkType="SUCCESS"/>
    //            </bpws:links>
    //            <bpws:extensionActivity>
    //                <tibex:activityExtension inputVariable="LogFailure-input" name="LogFailure" tibex:xpdlId="68aab8ee-1926-448a-9c14-d1b127aaecb7">
    //                    <bpws:sources>
    //                        <bpws:source linkName="LogToReply"/>
    //                    </bpws:sources>
    //                    <tibex:inputBindings>
    //                        <tibex:inputBinding expression="&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;&#10;&lt;xsl:stylesheet xmlns:xsl=&quot;http://www.w3.org/1999/XSL/Transform&quot; xmlns:tns=&quot;http://www.tibco.com/pe/WriteToLogActivitySchema&quot; version=&quot;2.0&quot;&gt;&lt;xsl:template name=&quot;LogFailure-input&quot; match=&quot;/&quot;&gt;&lt;tns:ActivityInput&gt;&lt;message&gt;&lt;xsl:value-of select=&quot;&amp;quot;Invocation Failed&amp;quot;&quot;/&gt;&lt;/message&gt;&lt;/tns:ActivityInput&gt;&lt;/xsl:template&gt;&lt;/xsl:stylesheet&gt;" expressionLanguage="urn:oasis:names:tc:wsbpel:2.0:sublang:xslt1.0"/>
    //                    </tibex:inputBindings>
    //                    <tibex:config>
    //                        <bwext:BWActivity xmlns:activityconfig="http://tns.tibco.com/bw/model/activityconfig" xmlns:bwext="http://tns.tibco.com/bw/model/core/bwext" xmlns:generalactivities="http://ns.tibco.com/bw/palette/generalactivities" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" activityTypeID="bw.generalactivities.log" version="6.0.0.001">
    //                            <activityConfig>
    //                                <properties name="config" xsi:type="activityconfig:EMFProperty">
    //                                    <type href="http://ns.tibco.com/bw/palette/generalactivities#//Log"/>
    //                                    <value controlBy="Application" role="Info" suppressJobInfo="true" xsi:type="generalactivities:Log"/>
    //                                </properties>
    //                            </activityConfig>
    //                        </bwext:BWActivity>
    //                    </tibex:config>
    //                </tibex:activityExtension>
    //            </bpws:extensionActivity>
    //            <bpws:reply faultName="clientFault" name="Reply" operation="post" partnerLink="creditscore" portType="ns0:creditscore" tibex:xpdlId="81a80515-beb2-4c76-b0e1-9b256e73aa8c" variable="Reply-input">
    //                <tibex:inputBinding expressionLanguage="urn:oasis:names:tc:wsbpel:2.0:sublang:xslt1.0">&lt;?xml version="1.0" encoding="UTF-8"?&gt;
    //&lt;xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns1="http://xmlns.example.com/20180831151624PLT" xmlns:tns="http://tns.tibco.com/bw/REST" version="2.0"&gt;&lt;xsl:template name="Reply-input" match="/"&gt;&lt;tns1:post4XXFaultMessage&gt;&lt;clientError&gt;&lt;tns:client4XXError&gt;&lt;statusCode&gt;&lt;xsl:value-of select="404"/&gt;&lt;/statusCode&gt;&lt;/tns:client4XXError&gt;&lt;/clientError&gt;&lt;/tns1:post4XXFaultMessage&gt;&lt;/xsl:template&gt;&lt;/xsl:stylesheet&gt;</tibex:inputBinding>
    //                <tibex:inputBindings>
    //                    <tibex:partBinding expression="&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;&#10;&lt;xsl:stylesheet xmlns:xsl=&quot;http://www.w3.org/1999/XSL/Transform&quot; xmlns:tns1=&quot;http://xmlns.example.com/20180831151624PLT&quot; xmlns:tns=&quot;http://tns.tibco.com/bw/REST&quot; version=&quot;2.0&quot;&gt;&#10;    &lt;xsl:template name=&quot;Reply-input&quot; match=&quot;/&quot;&gt;&#10;        &lt;tns:client4XXError&gt;&#10;            &lt;statusCode&gt;&#10;                &lt;xsl:value-of select=&quot;404&quot;/&gt;&#10;            &lt;/statusCode&gt;&#10;        &lt;/tns:client4XXError&gt;&#10;    &lt;/xsl:template&gt;&#10;&lt;/xsl:stylesheet&gt;" expressionLanguage="urn:oasis:names:tc:wsbpel:2.0:sublang:xslt1.0"/>
    //                </tibex:inputBindings>
    //                <bpws:targets>
    //                    <bpws:target linkName="LogToReply"/>
    //                </bpws:targets>
    //            </bpws:reply>
    //        </bpws:flow>
    //    </bpws:scope>
    //</bpws:catchAll>
    return input;
}
