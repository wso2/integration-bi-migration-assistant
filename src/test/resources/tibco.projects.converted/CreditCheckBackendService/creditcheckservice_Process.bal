import ballerina/xslt;

function creditcheckservice_Process_start(anydata input) returns anydata {
    xml inputXML = toXML(input);
    xml xmlResult = process_creditcheckservice_Process(inputXML);
    anydata result = convertToanydata(xmlResult);
    return result;
}

function extActivity_22(xml input, map<xml> context) returns xml {
    xml var0 = checkpanic xslt:transform(input, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns2="/T1535753828744Converted/JsonSchema" xmlns:tns3="http://www.example.com/namespaces/tns/1535845694732" version="2.0">
    <xsl:param name="post.item"/>
    <xsl:template name="LookupDatabase-input" match="/">
        <tns3:Element>
            <tns3:ssn>
                <xsl:value-of select="$post.item/tns2:SSN"/>
            </tns3:ssn>
        </tns3:Element>
    </xsl:template>
</xsl:stylesheet>`, context);
    xml var1 = toXML(creditcheckservice_LookupDatabase_start(convertToanydata(var0)));
    context["LookupDatabase"] = var1;
    return var1;
}

function pick_18(xml input, map<xml> context) returns xml {
    return input;
}

function process_creditcheckservice_Process(xml input) returns xml {
    map<xml> context = {};
    context["post.item"] = input;
    worker start_worker {
        xml result0 = unhandled_19(input, context);
        xml result1 = unhandled_21(input, context);
        xml result2 = extActivity_22(input, context);
        result2 -> LookupDatabaseToLogSuccess_Name;
    }
    worker LogTopostOut {
    }
    worker reply_20_worker {
        xml input0 = <- LogTopostOut;
        xml combinedInput = input0;
        xml output = reply_20(combinedInput, context);
        output -> function;
    }
    xml result0 = <- unhandled_19_worker;
    xml result1 = <- reply_20_worker;
    xml result2 = <- unhandled_21_worker;
    xml result = result0 + result1 + result2;
    return result;
}

function reply_20(xml input, map<xml> context) returns xml {
    xml var0 = checkpanic xslt:transform(input, xml `<?xml version="1.0" encoding="UTF-8"?>
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
</xsl:stylesheet>`, context);
    xml var1 = checkpanic xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns1="http://xmlns.example.com/20180831151624PLT" xmlns:tns2="/T1535753828744Converted/JsonSchema" version="2.0"><xsl:param name="LookupDatabase"/><xsl:template name="postOut-input" match="/"><tns1:postResponse><item><tns2:Response><xsl:if test="$LookupDatabase/tns2:FICOScore"><tns2:FICOScore><xsl:value-of select="$LookupDatabase/tns2:FICOScore"/></tns2:FICOScore></xsl:if><xsl:if test="$LookupDatabase/tns2:Rating"><tns2:Rating><xsl:value-of select="$LookupDatabase/tns2:Rating"/></tns2:Rating></xsl:if><xsl:if test="$LookupDatabase/tns2:NoOfInquiries"><tns2:NoOfInquiries><xsl:value-of select="$LookupDatabase/tns2:NoOfInquiries"/></tns2:NoOfInquiries></xsl:if></tns2:Response></item></tns1:postResponse></xsl:template></xsl:stylesheet>`, context);
    return var1;
}

function unhandled_19(xml input, map<xml> context) returns xml { // comment
    //[ParseError] : Unsupported activity tag: catchAll
    // comment
    //<bpws:catchAll xmlns:tibex="http://www.tibco.com/bpel/2007/extensions" tibex:faultDetailsVar="FaultDetails1" tibex:faultNameVar="FaultName1" tibex:xpdlId="4c604bae-a655-499f-a819-3730166f0e11" xmlns:bpws="http://docs.oasis-open.org/wsbpel/2.0/process/executable">
    // comment
    //                                    
    // comment
    //    <bpws:scope name="scope2">
    // comment
    //                                            
    // comment
    //        <bpws:flow name="flow2">
    // comment
    //                                                
    // comment
    //            <bpws:links>
    // comment
    //                                                    
    // comment
    //                <bpws:link name="LogToReply" tibex:linkType="SUCCESS"/>
    // comment
    //                                                    
    // comment
    //            </bpws:links>
    // comment
    //                                                
    // comment
    //            <bpws:extensionActivity>
    // comment
    //                                                    
    // comment
    //                <tibex:activityExtension inputVariable="LogFailure-input" name="LogFailure" tibex:xpdlId="68aab8ee-1926-448a-9c14-d1b127aaecb7">
    // comment
    //                                                        
    // comment
    //                    <bpws:sources>
    // comment
    //                                                            
    // comment
    //                        <bpws:source linkName="LogToReply"/>
    // comment
    //                                                            
    // comment
    //                    </bpws:sources>
    // comment
    //                                                        
    // comment
    //                    <tibex:inputBindings>
    // comment
    //                                                            
    // comment
    //                        <tibex:inputBinding expression="&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;&#10;&lt;xsl:stylesheet xmlns:xsl=&quot;http://www.w3.org/1999/XSL/Transform&quot; xmlns:tns=&quot;http://www.tibco.com/pe/WriteToLogActivitySchema&quot; version=&quot;2.0&quot;&gt;&lt;xsl:template name=&quot;LogFailure-input&quot; match=&quot;/&quot;&gt;&lt;tns:ActivityInput&gt;&lt;message&gt;&lt;xsl:value-of select=&quot;&amp;quot;Invocation Failed&amp;quot;&quot;/&gt;&lt;/message&gt;&lt;/tns:ActivityInput&gt;&lt;/xsl:template&gt;&lt;/xsl:stylesheet&gt;" expressionLanguage="urn:oasis:names:tc:wsbpel:2.0:sublang:xslt1.0"/>
    // comment
    //                                                            
    // comment
    //                    </tibex:inputBindings>
    // comment
    //                                                        
    // comment
    //                    <tibex:config>
    // comment
    //                                                            
    // comment
    //                        <bwext:BWActivity xmlns:activityconfig="http://tns.tibco.com/bw/model/activityconfig" xmlns:bwext="http://tns.tibco.com/bw/model/core/bwext" xmlns:generalactivities="http://ns.tibco.com/bw/palette/generalactivities" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" activityTypeID="bw.generalactivities.log" version="6.0.0.001">
    // comment
    //                                                                
    // comment
    //                            <activityConfig>
    // comment
    //                                                                    
    // comment
    //                                <properties name="config" xsi:type="activityconfig:EMFProperty">
    // comment
    //                                                                        
    // comment
    //                                    <type href="http://ns.tibco.com/bw/palette/generalactivities#//Log"/>
    // comment
    //                                                                        
    // comment
    //                                    <value controlBy="Application" role="Info" suppressJobInfo="true" xsi:type="generalactivities:Log"/>
    // comment
    //                                                                        
    // comment
    //                                </properties>
    // comment
    //                                                                    
    // comment
    //                            </activityConfig>
    // comment
    //                                                                
    // comment
    //                        </bwext:BWActivity>
    // comment
    //                                                            
    // comment
    //                    </tibex:config>
    // comment
    //                                                        
    // comment
    //                </tibex:activityExtension>
    // comment
    //                                                    
    // comment
    //            </bpws:extensionActivity>
    // comment
    //                                                
    // comment
    //            <bpws:reply faultName="clientFault" name="Reply" operation="post" partnerLink="creditscore" portType="ns0:creditscore" tibex:xpdlId="81a80515-beb2-4c76-b0e1-9b256e73aa8c" variable="Reply-input">
    // comment
    //                                                    
    // comment
    //                <tibex:inputBinding expressionLanguage="urn:oasis:names:tc:wsbpel:2.0:sublang:xslt1.0">&lt;?xml version="1.0" encoding="UTF-8"?&gt;
    // comment
    //&lt;xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns1="http://xmlns.example.com/20180831151624PLT" xmlns:tns="http://tns.tibco.com/bw/REST" version="2.0"&gt;&lt;xsl:template name="Reply-input" match="/"&gt;&lt;tns1:post4XXFaultMessage&gt;&lt;clientError&gt;&lt;tns:client4XXError&gt;&lt;statusCode&gt;&lt;xsl:value-of select="404"/&gt;&lt;/statusCode&gt;&lt;/tns:client4XXError&gt;&lt;/clientError&gt;&lt;/tns1:post4XXFaultMessage&gt;&lt;/xsl:template&gt;&lt;/xsl:stylesheet&gt;</tibex:inputBinding>
    // comment
    //                                                    
    // comment
    //                <tibex:inputBindings>
    // comment
    //                                                        
    // comment
    //                    <tibex:partBinding expression="&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;&#10;&lt;xsl:stylesheet xmlns:xsl=&quot;http://www.w3.org/1999/XSL/Transform&quot; xmlns:tns1=&quot;http://xmlns.example.com/20180831151624PLT&quot; xmlns:tns=&quot;http://tns.tibco.com/bw/REST&quot; version=&quot;2.0&quot;&gt;&#10;    &lt;xsl:template name=&quot;Reply-input&quot; match=&quot;/&quot;&gt;&#10;        &lt;tns:client4XXError&gt;&#10;            &lt;statusCode&gt;&#10;                &lt;xsl:value-of select=&quot;404&quot;/&gt;&#10;            &lt;/statusCode&gt;&#10;        &lt;/tns:client4XXError&gt;&#10;    &lt;/xsl:template&gt;&#10;&lt;/xsl:stylesheet&gt;" expressionLanguage="urn:oasis:names:tc:wsbpel:2.0:sublang:xslt1.0"/>
    // comment
    //                                                        
    // comment
    //                </tibex:inputBindings>
    // comment
    //                                                    
    // comment
    //                <bpws:targets>
    // comment
    //                                                        
    // comment
    //                    <bpws:target linkName="LogToReply"/>
    // comment
    //                                                        
    // comment
    //                </bpws:targets>
    // comment
    //                                                    
    // comment
    //            </bpws:reply>
    // comment
    //                                                
    // comment
    //        </bpws:flow>
    // comment
    //                                        
    // comment
    //    </bpws:scope>
    // comment
    //                                
    // comment
    //</bpws:catchAll>
    return input;
}

function unhandled_21(xml input, map<xml> context) returns xml { // comment
    //Unknown extension kind: bw.generalactivities.log
    return input;
}
