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

function HTTP_Receiver_6(map<xml> context) returns xml|error {
    xml var0 = xml `<root></root>`;
    addToContext(context, "HTTP Receiver", var0);
    return var0;
}

function Log_8(map<xml> context) returns xml|error {
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

function Mapper_7(map<xml> context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0"><xsl:param name="runAllTests"/>     <xsl:template name="Transform0" match="/">
        <failedTestsCount>
                    
    <xsl:value-of select="count($runAllTests/root/ns:test-suites-results-msg/test-suites-results/ns3:test-suites-results//ns3:test-failure)" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                
</failedTestsCount>

    </xsl:template>
</xsl:stylesheet>`, context);
    addToContext(context, "Mapper", var1);
    return var1;
}

function scope0_1ActivityRunner(map<xml> cx) returns xml|error {
    xml result0 = check HTTP_Receiver_6(cx);
    xml result1 = check Mapper_7(cx);
    xml result2 = check Log_8(cx);
    return result2;
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
