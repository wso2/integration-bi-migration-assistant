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

function HTTP_Receiver_9(map<xml> context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = xml `<root>${var0}</root>`;
    addToContext(context, "HTTP Receiver", var1);
    return var1;
}

function Log_11(map<xml> context) returns xml|error {
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

function Mapper_10(map<xml> context) returns xml|error {
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
    xml result0 = check HTTP_Receiver_9(cx);
    xml result1 = check Mapper_10(cx);
    xml result2 = check Log_11(cx);
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

function start_Processes_Other_process(xml inputXML, map<xml> params) returns xml {
    return scope0_1ScopeFn(params);
}

xmlns "http://xmlns.tibco.com/bw/process/2003" as pd;
xmlns "http://www.w3.org/1999/XSL/Transform" as xsl;
xmlns "http://www.tibco.com/pe/EngineTypes" as ns;
xmlns "http://www.w3.org/2001/XMLSchema" as xsd;
