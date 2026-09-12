import ballerina/data.jsondata;
import ballerina/data.xmldata;
import ballerina/http;
import ballerina/io;
import ballerina/log;
import ballerina/soap.soap11;
import ballerina/sql;
import ballerina/xslt;

function Catch(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = xml `<root>${var0}</root>`;
    addToContext(cx, "Catch", var1);
}

function ErrorLog(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/EngineTypes" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="2.0">
     <xsl:template name="Transform9" match="/">
        <ActivityInput>
                    
    <message>
                            
        <xsl:value-of select="Error" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                        
    </message>
                
</ActivityInput>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xml var3 = var2/**/<message>/*;
    log:printInfo(var3.toString());
    addToContext(cx, "ErrorLog", var3);
}

function HTTP_Receiver(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = xml `<root>${var0}</root>`;
    addToContext(cx, "HTTP-Receiver", var1);
}

function InvokeProcess(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/EngineTypes" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="2.0"><xsl:param name="HTTP-Receiver"/>     <xsl:template name="Transform1" match="/">
        <InvokeProcessInput>
                    
    <options>
                            
        <xsl:value-of select="$HTTP-Receiver/root/ProcessStarterOutput/QueryString" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                        
    </options>
                
</InvokeProcessInput>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    addToContext(cx, "$Start", var2);
    start_Processes_Other_process(cx);
    xml var3 = cx.result;
    addToContext(cx, "InvokeProcess", var3);
}

function Log(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/EngineTypes" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="2.0"><xsl:param name="Mapper"/>     <xsl:template name="Transform4" match="/">
        <ns:ActivityInput xmlns:ns="http://www.tibco.com/pe/EngineTypes">
                    
    <message>
                            
        <xsl:value-of select="$Mapper" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                        
    </message>
                
</ns:ActivityInput>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    // FIXME: Failed to convert rest of activity

    // <pd:activity name="Log" xmlns:pd="http://xmlns.tibco.com/bw/process/2003">
    //     <pd:type>Unknown2</pd:type>
    //     <pd:resourceType>ae.activities.log</pd:resourceType>
    //     <config>
    //         <role>User</role>
    //     </config>
    //     <pd:inputBindings>
    //         <ns:ActivityInput xmlns:ns="http://www.tibco.com/pe/EngineTypes">
    //             <message>
    //                 <xsl:value-of select="$Mapper" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
    //             </message>
    //         </ns:ActivityInput>
    //     </pd:inputBindings>
    // </pd:activity>
    addToContext(cx, "Log", var2);
}

function Mapper(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/EngineTypes" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="2.0"><xsl:param name="runAllTests"/>     <xsl:template name="Transform0" match="/">
        <failedTestsCount>
                    
    <xsl:value-of select="count($runAllTests/root/ns:test-suites-results-msg/test-suites-results/ns3:test-suites-results//ns3:test-failure)" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                
</failedTestsCount>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xml var3 = xml `<root>${var2}</root>`;
    addToContext(cx, "Mapper", var3);
}

function Parse(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/EngineTypes" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="2.0"><xsl:param name="Render"/>     <xsl:template name="Transform6" match="/">
        <xmlString>
                    
    <xsl:value-of select="$Render/root/xmlString" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                
</xmlString>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xml var3 = var2/<xmlString>/*;
    string var4 = var3.toString();
    xml var5 = check xml:fromString(var4);
    xml var6 = xml `<root>${var5}</root>`;
    addToContext(cx, "Parse", var6);
}

function Read_file(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/EngineTypes" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="2.0">
     <xsl:template name="Transform3" match="/">
        <ReadActivityInputClass>
                    
    <fileName>
                            
        <xsl:value-of select="&quot;input.txt&quot;" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                        
    </fileName>
                
</ReadActivityInputClass>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    string fileName = (var2/**/<fileName>/*).toString();
    string content = check io:fileReadString(fileName);
    xml var3 = xml `<ns:ReadActivityOutputTextClass xmlns:ns="http://www.tibco.com/namespaces/tnt/plugins/file">
    <fileContent>
        <textContent>${content}</textContent>
    </fileContent>
</ns:ReadActivityOutputTextClass>`;
    addToContext(cx, "Read-file", var3);
}

function Render(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/EngineTypes" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="2.0"><xsl:param name="Mapper"/>     <xsl:template name="Transform5" match="/">
        <xsl:copy-of select="$Mapper" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    string var3 = var2.toBalString();
    xml var4 = xml `<root><xmlString>${var3}</xmlString></root>`;
    addToContext(cx, "Render", var4);
}

function SOAPRequestReply(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/EngineTypes" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="2.0">
     <xsl:template name="Transform7" match="/">
        <inputMessage>
                    
    <message>
                    "foo bar"
                </message>
                
</inputMessage>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    soap11:Client var3 = check new ("http://localhost:8800");
    xml var4 = xml `<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
    soap:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/">
    <soap:Header/>
    <soap:Body>
        ${var2}
    </soap:Body>
</soap:Envelope>`;
    xml var5 = check var3->sendReceive(var4, "SOAPAction");
    xml var6 = xml `<root>${var5}</root>`;
    addToContext(cx, "SOAPRequestReply", var6);
}

function SQL_Direct(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/EngineTypes" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="2.0">
     <xsl:template name="Transform8" match="/">
        <jdbcGeneralActivityInput>
                        
    <statement>
                                
        <xsl:value-of select="'SELECT * FROM foo'" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                            
    </statement>
                    
</jdbcGeneralActivityInput>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    string var3 = (var2/**/<statement>/*).toString().trim();
    sql:ParameterizedQuery var4 = ``;
    var4.strings = [var3];
    // WARNING: Missing DB client resource '/RestHelloWorld/JDBCConnection.sharedjdbc'. Using placeholder client.
    xml var5;
    if var3.startsWith("SELECT") {
        stream<record {|anydata...;|}, error?> var6 = placeholder_db_connection->query(var4);
        xml var7 = xml ``;
        check from var each in var6
            do {
                xml var8 = check toXML(each);
                var7 = var7 + xml `<Record>${var8}</Record>`;
            };

        xml var9 = xml `<root>${var7}</root>`;
        var5 = var9;
    } else {
        sql:ExecutionResult var10 = check placeholder_db_connection->execute(var4);
        xml var11 = xml `<root></root>`;
        var5 = var11;
    }
    // WARNING: validate jdbc query result mapping
    addToContext(cx, "SQL-Direct", var5);
}

function Write_File(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/EngineTypes" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="2.0"><xsl:param name="Mapper"/>     <xsl:template name="Transform2" match="/">
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
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    // FIXME: Failed to convert rest of activity

    // <pd:activity name="Write File" xmlns:pd="http://xmlns.tibco.com/bw/process/2003">
    //     <pd:type>Unknown1</pd:type>
    //     <pd:resourceType>ae.activities.FileWriteActivity</pd:resourceType>
    //     <config>
    //         <encoding>text</encoding>
    //         <compressFile>None</compressFile>
    //         <append>true</append>
    //     </config>
    //     <pd:inputBindings>
    //         <ns0:WriteActivityInputTextClass xmlns:ns0="http://www.tibco.com/namespaces/tnt/plugins/file">
    //             <fileName>
    //                 <xsl:value-of select="&quot;output.txt&quot;" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
    //             </fileName>
    //             <textContent>
    //                 <xsl:value-of select="$Mapper" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
    //             </textContent>
    //             <addLineSeparator>
    //                 <xsl:value-of select="true()" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
    //             </addLineSeparator>
    //         </ns0:WriteActivityInputTextClass>
    //     </pd:inputBindings>
    // </pd:activity>
    addToContext(cx, "Write-File", var2);
}

function scope0ActivityRunner(Context cx) returns error? {
    check HTTP_Receiver(cx);
    check Mapper(cx);
    check Log(cx);
    check InvokeProcess(cx);
    check SOAPRequestReply(cx);
}

function scope0FaultHandler(error err, Context cx) returns () {
    checkpanic Catch(cx);
    checkpanic ErrorLog(cx);
}

function scope0ScopeFn(Context cx) returns () {
    error? result = scope0ActivityRunner(cx);
    if result is error {
        scope0FaultHandler(result, cx);
    }
}

function start_Processes_MainProcessStarter_process(Context cx) returns () {
    return scope0ScopeFn(cx);
}

function HTTP_Receiver_12(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = xml `<root>${var0}</root>`;
    addToContext(cx, "HTTP-Receiver", var1);
}

function InnerLogElement(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/EngineTypes" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:ns1="http://www.tibco.com/namespaces/tnt/plugins/json" version="2.0"><xsl:param name="element"/>     <xsl:template name="Transform3" match="/">
        <ns:ActivityInput xmlns:ns="http://www.tibco.com/pe/EngineTypes">
                        
    <message>
                                
        <xsl:value-of select="$element" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                            
    </message>
                    
</ns:ActivityInput>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xml var3 = var2/**/<message>/*;
    log:printInfo(var3.toString());
    addToContext(cx, "InnerLogElement", var3);
}

function InnerLogIndex(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/EngineTypes" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:ns1="http://www.tibco.com/namespaces/tnt/plugins/json" version="2.0"><xsl:param name="index"/>     <xsl:template name="Transform4" match="/">
        <ns:ActivityInput xmlns:ns="http://www.tibco.com/pe/EngineTypes">
                        
    <message>
                                
        <xsl:value-of select="$index" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                            
    </message>
                    
</ns:ActivityInput>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xml var3 = var2/**/<message>/*;
    log:printInfo(var3.toString());
    addToContext(cx, "InnerLogIndex", var3);
}

function Log_14(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/EngineTypes" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:ns1="http://www.tibco.com/namespaces/tnt/plugins/json" version="2.0"><xsl:param name="Mapper"/>     <xsl:template name="Transform1" match="/">
        <ns:ActivityInput xmlns:ns="http://www.tibco.com/pe/EngineTypes">
                    
    <message>
                            
        <xsl:value-of select="$Mapper" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                        
    </message>
                
</ns:ActivityInput>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    // FIXME: Failed to convert rest of activity

    // <pd:activity name="Log" xmlns:pd="http://xmlns.tibco.com/bw/process/2003">
    //     <pd:type>Unknown2</pd:type>
    //     <pd:resourceType>ae.activities.log</pd:resourceType>
    //     <config>
    //         <role>User</role>
    //     </config>
    //     <pd:inputBindings>
    //         <ns:ActivityInput xmlns:ns="http://www.tibco.com/pe/EngineTypes">
    //             <message>
    //                 <xsl:value-of select="$Mapper" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
    //             </message>
    //         </ns:ActivityInput>
    //     </pd:inputBindings>
    // </pd:activity>
    addToContext(cx, "Log", var2);
}

function Loop(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = xml `<root></root>`;
    xml var2 = getFromContext(cx, "Mapper");
    var2 = check xmldata:transform(var2, `foo/bar`);
    int var3 = -1;
    addToContext(cx, "element", xml `<root></root>`);
    foreach xml each in var2 {
        var3 = var3 + 1;
        addToContext(cx, "index", xml `<root>${var3}</root>`);
        addToContext(cx, "element", each);
        scope1ScopeFn(cx);
        xml result = cx.result;
        var1 = result;
    }
    addToContext(cx, "Loop", var1);
}

function Mapper_13(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/EngineTypes" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:ns1="http://www.tibco.com/namespaces/tnt/plugins/json" version="2.0"><xsl:param name="runAllTests"/>     <xsl:template name="Transform0" match="/">
        <failedTestsCount>
                    
    <xsl:value-of select="count($runAllTests/root/ns:test-suites-results-msg/test-suites-results/ns3:test-suites-results//ns3:test-failure)" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                
</failedTestsCount>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xml var3 = xml `<root>${var2}</root>`;
    addToContext(cx, "Mapper", var3);
}

function Parse_JSON(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/EngineTypes" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:ns1="http://www.tibco.com/namespaces/tnt/plugins/json" version="2.0"><xsl:param name="Start"/>     <xsl:template name="Transform5" match="/">
        <ns1:ActivityInputClass xmlns:ns1="http://www.tibco.com/namespaces/tnt/plugins/json">
                    
    <jsonString>
                            
        <xsl:value-of select="$Start/root/foo" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                        
    </jsonString>
                
</ns1:ActivityInputClass>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xmlns "http://www.tibco.com/namespaces/tnt/plugins/json" as ns;
    xml var3 = check renderJsonAsFooXML(var2);
    xml var4 = xml `<ActivityOutputClass>var3</ActivityOutputClass>`;
    xml var5 = xml `<root>${var4}</root>`;
    addToContext(cx, "Parse-JSON", var5);
}

function Render_JSON(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/EngineTypes" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:ns1="http://www.tibco.com/namespaces/tnt/plugins/json" version="2.0"><xsl:param name="Parse-JSON"/>     <xsl:template name="Transform6" match="/">
        <ns1:ActivityInputClass xmlns:ns1="http://www.tibco.com/namespaces/tnt/plugins/json">
                        
    <jsonString>
                                
        <xsl:value-of select="$Parse-JSON/root/ns1:ActivityOutputClass/Foo" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                            
    </jsonString>
                    
</ns1:ActivityInputClass>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xml var3 = (var2/*);
    xmlns "http://www.tibco.com/namespaces/tnt/plugins/json" as ns;
    // WARNING: assuming single element
    record {|
        string foo;
        string bar?;
    |} var4 = check xmldata:parseAsType(var3);
    string var5 = var4.toJsonString();
    xml var6 = xml `<jsonString>${var5}</jsonString>`;
    xml var7 = xml `<ns:ActivityOutputClass>${var6}</ns:ActivityOutputClass>`;
    addToContext(cx, "Render-JSON", var7);
}

function Rest_call(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/EngineTypes" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:ns1="http://www.tibco.com/namespaces/tnt/plugins/json" version="2.0"><xsl:param name="Schedule-Poller"/>     <xsl:template name="Transform7" match="/">
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
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xml var3 = var2/**/<Body>;
    map<json> var4 = <map<json>>xmlToJson(var3);
    http:Client var5 = check new ("https://eieio.azurewebsites.net/schedulepolling/uploadschedule");
    json var6 = check var5->post("/", var4["Body"]);
    xml var7 = check toXML(<map<json>>var6);
    xml var8 = xml `<ns:RESTOutput><msg>${var7}</msg></ns:RESTOutput>`;
    xml var9 = xml `<root>${var8}</root>`;
    addToContext(cx, "Rest-call", var9);
}

function SOAPSendReply(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/EngineTypes" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:ns1="http://www.tibco.com/namespaces/tnt/plugins/json" version="2.0">
     <xsl:template name="Transform2" match="/">
        <Response>
                    
    <Body>
                    "Foo bar"
                </Body>
                
</Response>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xml var3 = xml `<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
    soap:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/">
    <soap:Header/>
    <soap:Body>
        ${var2}
    </soap:Body>
</soap:Envelope>`;
    xml var4 = xml `<root>${var3}</root>`;
    addToContext(cx, "SOAPSendReply", var4);
}

function scope0_1ActivityRunner(Context cx) returns error? {
    check HTTP_Receiver_12(cx);
    check Mapper_13(cx);
    check Log_14(cx);
    check SOAPSendReply(cx);
}

function scope0_1FaultHandler(error err, Context cx) returns () {
    panic err;
}

function scope0_1ScopeFn(Context cx) returns () {
    error? result = scope0_1ActivityRunner(cx);
    if result is error {
        scope0_1FaultHandler(result, cx);
    }
}

function scope1ActivityRunner(Context cx) returns error? {
}

function scope1FaultHandler(error err, Context cx) returns () {
    panic err;
}

function scope1ScopeFn(Context cx) returns () {
    error? result = scope1ActivityRunner(cx);
    if result is error {
        scope1FaultHandler(result, cx);
    }
}

function start_Processes_Other_process(Context cx) returns () {
    return scope0_1ScopeFn(cx);
}

function toXML(map<anydata> data) returns error|xml {
    return xmldata:toXml(data);
}

function renderJSONAsXML(json value, string? namespace, string? typeName) returns xml|error {
    anydata body;
    if (value is map<json>) {
        xml acum = xml ``;
        foreach string key in value.keys() {
            acum += check renderJSONAsXML(value.get(key), namespace, key);
        }
        body = acum;
    } else {
        body = value;
    }

    string rep = typeName == () ? body.toString() :
        string `<${typeName}>${body.toString()}</${typeName}>`;
    xml result = check xml:fromString(rep);
    if (namespace == ()) {
        return result;
    }
    if (result !is xml:Element) {
        panic error("Expected XML element");
    }
    map<string> attributes = result.getAttributes();
    attributes["xmlns"] = namespace;
    return result;
}

function xmlToJson(xml value) returns json {
    json result = toJsonInner(value);
    if (result is map<json> && result.hasKey("InputElement")) {
        return result.get("InputElement");
    } else {
        return result;
    }
}

function toJsonInner(xml value) returns json {
json result;
if (value is xml:Element) {
result = toJsonElement(value);
} else {
result = value.toJson();
}
return result;
}

function toJsonElement(xml:Element element) returns json {
XMLElementParseResult parseResult = parseElement(element);
string name = parseResult.name;

xml children = element/*;
map<json> body = {};
map<json> result = {};
foreach xml child in children {
json r = toJsonInner(child);
if child !is xml:Element {
result[name] = r;
return result;
}
string childName = parseElement(child).name;
if r !is map<json> {
panic error("unexpected");
} else {
r = r.get(childName);
}
if body.hasKey(childName) {
json current = body.get(childName);
if current !is json[] {
json[] n = [body.get(childName)];
n.push(r);
body[childName] = n;
} else {
current.push(r);
}
} else {
body[childName] = r;
}
}
result[name] = body;
return result;
}

function parseElement(xml:Element element) returns XMLElementParseResult {
    string name = element.getName();
    if (name.startsWith("{")) {
        int? index = name.indexOf("}");
        if (index == ()) {
            panic error("Invalid element name: " + name);
        }
        string namespace = name.substring(1, index);
        name = name.substring(index + 1);
        return {namespace: namespace, name: name};
    }
    return {namespace: (), name: name};
}

function addToContext(Context context, string varName, xml value) {
    xml children = value/*;
    xml transformed = xml `<root>${children}</root>`;
    context.variables[varName] = transformed;
    context.result = value;
}

function getFromContext(Context context, string varName) returns xml {
    xml? value = context.variables[varName];
    if value == () {
        return xml `<root/>`;
    }
    return value;
}

function initContext(map<xml> initVariables = {},
        map<SharedVariableContext> jobSharedVariables = {})
            returns Context {
    map<SharedVariableContext> sharedVariables = {};

    foreach var key in jobSharedVariables.keys() {
        sharedVariables[key] = jobSharedVariables.get(key);
    }
    return {variables: initVariables, result: xml `<root/>`, sharedVariables};
}

function renderJsonAsFooXML(xml value) returns xml|error {
    string jsonString = (value/<jsonString>).data();
    map<json> jsonValue = check jsondata:parseString(jsonString);
    string? namespace = (Foo).@xmldata:Namespace["uri"];
    return renderJSONAsXML(jsonValue, namespace, "Foo");
}
