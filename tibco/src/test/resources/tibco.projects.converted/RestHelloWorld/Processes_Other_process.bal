import ballerina/data.xmldata;
import ballerina/http;
import ballerina/log;
import ballerina/xslt;

listener http:Listener proj_annon_var2 = GeneralConnection_sharedhttp;
http:Client proj_annon_var3 = checkpanic new ("localhost:9090");

service on proj_annon_var2 {
    resource function 'default [string... path](xml input) returns xml {
        xml inputVal = xml `<root>
    <item>
        ${input}
    </item>
</root>`;
        map<xml> paramXML = {post: inputVal};
        xml result = start_Processes_Other_process(input, paramXML);
        return result;
    }
}

function HTTP_Receiver_11(map<xml> context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = xml `<root>${var0}</root>`;
    addToContext(context, "HTTP Receiver", var1);
    return var1;
}

function InnerLogElement(map<xml> context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0"><xsl:param name="element"/>     <xsl:template name="Transform3" match="/">
        <ns:ActivityInput xmlns:ns="http://www.tibco.com/pe/EngineTypes">
                        
    <message>
                                
        <xsl:value-of select="$element" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                            
    </message>
                    
</ns:ActivityInput>

    </xsl:template>
</xsl:stylesheet>`, context);
    xml var2 = var1/**/<message>/*;
    log:printInfo(var2.toString());
    addToContext(context, "InnerLogElement", var2);
    return var2;
}

function InnerLogIndex(map<xml> context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0"><xsl:param name="index"/>     <xsl:template name="Transform4" match="/">
        <ns:ActivityInput xmlns:ns="http://www.tibco.com/pe/EngineTypes">
                        
    <message>
                                
        <xsl:value-of select="$index" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                            
    </message>
                    
</ns:ActivityInput>

    </xsl:template>
</xsl:stylesheet>`, context);
    xml var2 = var1/**/<message>/*;
    log:printInfo(var2.toString());
    addToContext(context, "InnerLogIndex", var2);
    return var2;
}

function Log_13(map<xml> context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0"><xsl:param name="Mapper"/>     <xsl:template name="Transform1" match="/">
        <ns:ActivityInput xmlns:ns="http://www.tibco.com/pe/EngineTypes">
                    
    <message>
                            
        <xsl:value-of select="$Mapper" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                        
    </message>
                
</ns:ActivityInput>

    </xsl:template>
</xsl:stylesheet>`, context);
    xml var2 = var1/**/<message>/*;
    log:printInfo(var2.toString());
    addToContext(context, "Log", var2);
    return var2;
}

function Loop(map<xml> context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = xml `<root></root>`;
    xml var2 = context.get("Mapper");
    var2 = check xmldata:transform(var2, `foo/bar`);
    int var3 = -1;
    addToContext(context, "element", xml `<root></root>`);
    foreach xml each in var2 {
        var3 = var3 + 1;
        addToContext(context, "index", xml `<root>${var3}</root>`);
        addToContext(context, "element", each);
        xml var4 = scope1ScopeFn(context);
        var1 = var4;
    }
    addToContext(context, "Loop", var1);
    return var1;
}

function Mapper_12(map<xml> context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0"><xsl:param name="runAllTests"/>     <xsl:template name="Transform0" match="/">
        <failedTestsCount>
                    
    <xsl:value-of select="count($runAllTests/root/ns:test-suites-results-msg/test-suites-results/ns3:test-suites-results//ns3:test-failure)" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                
</failedTestsCount>

    </xsl:template>
</xsl:stylesheet>`, context);
    xml var2 = xml `<root>${var1}</root>`;
    addToContext(context, "Mapper", var2);
    return var2;
}

function Rest_call(map<xml> context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0"><xsl:param name="Schedule-Poller"/>     <xsl:template name="Transform5" match="/">
        <ns1:ActivityInput xmlns:ns1="http://www.tibco.com/namespaces/tnt/plugins/json">
                    
    <ns1:Parameters>
                            
        <Body>
                                    
            <Multipart>
                                            
                <name>
                                                    
                    <xsl:value-of select="&quot;schedule&quot;" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                                                
                </name>
                                            
                <filename>
                                                    
                    <xsl:value-of select="$Schedule-Poller/root/ns:EventSourceOuputTextClass/fileInfo/fileName" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                                                
                </filename>
                                            
                <content>
                                                    
                    <fileName>
                                                            
                        <xsl:value-of select="concat($Schedule-Poller/root/ns:EventSourceOuputTextClass/fileInfo/location, &quot;\&quot;, $Schedule-Poller/root/ns:EventSourceOuputTextClass/fileInfo/fileName)" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                                                        
                    </fileName>
                                                
                </content>
                                        
            </Multipart>
                                
        </Body>
                        
    </ns1:Parameters>
                
</ns1:ActivityInput>

    </xsl:template>
</xsl:stylesheet>`, context);
    xmlns "http://www.tibco.com/namespaces/tnt/plugins/json" as ns;
    xml var2 = var1/**/<ns:Body>;
    json var3 = xmlToJson(var2);
    http:Client var4 = check new ("https://eieio.azurewebsites.net/schedulepolling/uploadschedule");
    json var5 = check var4->post("/", var3);
    xml var6 = check toXML(<map<json>>var5);
    xml var7 = xml `<ns:RESTOutput><msg>${var6}</msg></ns:RESTOutput>`;
    addToContext(context, "Rest call", var7);
    return var7;
}

function SOAPSendReply(map<xml> context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0">
     <xsl:template name="Transform2" match="/">
        <Response>
                    
    <Body>
                    "Foo bar"
                </Body>
                
</Response>

    </xsl:template>
</xsl:stylesheet>`, context);
    xml var2 = xml `<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
    soap:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/">
    <soap:Header/>
    <soap:Body>
        ${var1}
    </soap:Body>
</soap:Envelope>`;
    addToContext(context, "SOAPSendReply", var2);
    return var2;
}

function scope0_1ActivityRunner(map<xml> cx) returns xml|error {
    xml result0 = check HTTP_Receiver_11(cx);
    xml result1 = check Mapper_12(cx);
    xml result2 = check Log_13(cx);
    xml result3 = check SOAPSendReply(cx);
    return result3;
}

function scope0_1FaultHandler(error err, map<xml> cx) returns xml {
    panic err;
}

function scope0_1ScopeFn(map<xml> cx) returns xml {
    xml|error result = scope0_1ActivityRunner(cx);
    if result is error {
        return scope0_1FaultHandler(result, cx);
    }
    return result;
}

function scope1ActivityRunner(map<xml> cx) returns xml|error {
    xml result0 = check InnerLogIndex(cx);
    xml result1 = check InnerLogElement(cx);
    return result1;
}

function scope1FaultHandler(error err, map<xml> cx) returns xml {
    panic err;
}

function scope1ScopeFn(map<xml> cx) returns xml {
    xml|error result = scope1ActivityRunner(cx);
    if result is error {
        return scope1FaultHandler(result, cx);
    }
    return result;
}

function start_Processes_Other_process(xml inputXML, map<xml> params) returns xml {
    return scope0_1ScopeFn(params);
}

xmlns "http://xmlns.tibco.com/bw/process/2003" as pd;
xmlns "http://www.w3.org/1999/XSL/Transform" as xsl;
xmlns "http://www.tibco.com/pe/EngineTypes" as ns;
xmlns "http://www.tibco.com/namespaces/tnt/plugins/json" as ns1;
xmlns "http://www.w3.org/2001/XMLSchema" as xsd;
