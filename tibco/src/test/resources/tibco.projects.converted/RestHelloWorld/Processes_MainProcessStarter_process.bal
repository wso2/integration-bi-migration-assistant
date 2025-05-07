import ballerina/http;
import ballerina/io;
import ballerina/log;
import ballerina/xslt;

listener http:Listener proj_annon_var0 = GeneralConnection_sharedhttp;
http:Client proj_annon_var1 = checkpanic new ("localhost:9090");

service on proj_annon_var0 {
    resource function 'default [string... path](xml input) returns xml {
        xml inputVal = xml `<root>
    <item>
        ${input}
    </item>
</root>`;
        map<xml> paramXML = {post: inputVal};
        xml result = start_Processes_MainProcessStarter_process(input, paramXML);
        return result;
    }
}

function HTTP_Receiver(map<xml> context) returns xml|error {
    xml var0 = xml `<root></root>`;
    addToContext(context, "HTTP Receiver", var0);
    return var0;
}

function InvokeProcess(map<xml> context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0"><xsl:param name="HTTP-Receiver"/>     <xsl:template name="Transform1" match="/">
        <InvokeProcessInput>
                    
    <options>
                            
        <xsl:value-of select="$HTTP-Receiver/root/ProcessStarterOutput/QueryString" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                        
    </options>
                
</InvokeProcessInput>

    </xsl:template>
</xsl:stylesheet>`, context);
    xml var2 = check proj_annon_var3->post("", var1);
    addToContext(context, "InvokeProcess", var2);
    return var2;
}

function Log(map<xml> context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0"><xsl:param name="Mapper"/>     <xsl:template name="Transform3" match="/">
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

function Mapper(map<xml> context) returns xml|error {
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

function Read_file(map<xml> context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0">
     <xsl:template name="Transform2" match="/">
        <ReadActivityInputClass>
                    
    <fileName>
                            
        <xsl:value-of select="&quot;input.txt&quot;" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                        
    </fileName>
                
</ReadActivityInputClass>

    </xsl:template>
</xsl:stylesheet>`, context);
    string fileName = (var1/**/<fileName>/*).toString();
    string content = check io:fileReadString(fileName);
    xml var2 = xml `<ns:ReadActivityOutputTextClass xmlns:ns="http://www.tibco.com/namespaces/tnt/plugins/file">
    <fileContent>
        <textContent>${content}</textContent>
    </fileContent>
</ns:ReadActivityOutputTextClass>`;
    addToContext(context, "Read file", var2);
    return var2;
}

function Write_File(map<xml> context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0"><xsl:param name="Mapper"/>     <xsl:template name="Transform1" match="/">
        <ns0:WriteActivityInputTextClass xmlns:ns0="http://www.tibco.com/namespaces/tnt/plugins/file">
                    
    <fileName>
                            
        <xsl:value-of select="&quot;output.txt&quot;" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                        
    </fileName>
                    
    <textContent>
                            
        <xsl:value-of select="$Mapper" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                        
    </textContent>
                    
    <addLineSeparator>
                            
        <xsl:value-of select="true()" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                        
    </addLineSeparator>
                
</ns0:WriteActivityInputTextClass>

    </xsl:template>
</xsl:stylesheet>`, context);
    string fileName = (var1/**/<fileName>/*).toString();
    string content = (var1/**/<textContent>/*).toString();
    check io:fileWriteString(fileName, content, "APPEND");
    addToContext(context, "Write File", var1);
    return var1;
}

function scope0ActivityRunner(map<xml> cx) returns xml|error {
    xml result0 = check HTTP_Receiver(cx);
    xml result1 = check Mapper(cx);
    xml result2 = check Log(cx);
    xml result3 = check InvokeProcess(cx);
    xml result4 = check Write_File(cx);
    xml result5 = check Read_file(cx);
    return result5;
}

function scope0FaultHandler(error err, map<xml> cx) returns xml {
    panic err;
}

function scope0ScopeFn(map<xml> cx) returns xml {
    xml|error result = scope0ActivityRunner(cx);
    if result is error {
        return scope0FaultHandler(result, cx);
    }
    return result;
}

function start_Processes_MainProcessStarter_process(xml inputXML, map<xml> params) returns xml {
    return scope0ScopeFn(params);
}

xmlns "http://xmlns.tibco.com/bw/process/2003" as pd;
xmlns "http://www.w3.org/1999/XSL/Transform" as xsl;
xmlns "http://www.tibco.com/pe/EngineTypes" as ns;
xmlns "http://www.w3.org/2001/XMLSchema" as xsd;
