import ballerina/log;
import ballerina/soap.soap11;
import ballerina/xslt;

function Call_Foo(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/EngineTypes" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="2.0"><xsl:param name="post"/>     <xsl:template name="Transform1" match="/">
        <InvokeProcessInput>
                    
    <options>
                            
        <xsl:value-of select="$post/root/item/req" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                        
    </options>
                
</InvokeProcessInput>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    addToContext(cx, "$Start", var2);
    start_Processes_Foo_process(cx);
    xml var3 = cx.result;
    addToContext(cx, "Call-Foo", var3);
}

function HTTP_Receiver(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = xml `<root>${var0}</root>`;
    addToContext(cx, "HTTP-Receiver", var1);
}

function HTTP_Response(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/EngineTypes" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="2.0"><xsl:param name="Call-Foo"/>     <xsl:template name="Transform3" match="/">
        <ResponseActivityInput>
                    
    <asciiContent>
                            
        <Response>
                                    
            <xsl:value-of select="$Call-Foo//FromBar" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                                
        </Response>
                        
    </asciiContent>
                
</ResponseActivityInput>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xml var3 = var2/**/<asciiContent>/*;
    xml var4 = xml `<root>${var3}</root>`;
    addToContext(cx, "HTTP-Response", var4);
}

function Log1(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/EngineTypes" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="2.0"><xsl:param name="post"/>     <xsl:template name="Transform0" match="/">
        <ns:ActivityInput xmlns:ns="http://www.tibco.com/pe/EngineTypes">
                    
    <message>
         Before calling Foo, initial payload 
        <xsl:value-of select="$post" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
    </message>
                
</ns:ActivityInput>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xml var3 = var2/**/<message>/*;
    log:printInfo(var3.toString());
    addToContext(cx, "Log1", var3);
}

function Log2(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/EngineTypes" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="2.0"><xsl:param name="Call-Foo"/>     <xsl:template name="Transform2" match="/">
        <ns:ActivityInput xmlns:ns="http://www.tibco.com/pe/EngineTypes">
                    
    <message>
        After calling Foo, received
        <xsl:value-of select="$Call-Foo" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
    </message>
                
</ns:ActivityInput>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xml var3 = var2/**/<message>/*;
    log:printInfo(var3.toString());
    addToContext(cx, "Log2", var3);
}

function scope0ActivityRunner(Context cx) returns error? {
    check HTTP_Receiver(cx);
    check Log1(cx);
    check Call_Foo(cx);
    check Log2(cx);
    check HTTP_Response(cx);
}

function scope0FaultHandler(error err, Context cx) returns () {
    panic err;
}

function scope0ScopeFn(Context cx) returns () {
    error? result = scope0ActivityRunner(cx);
    if result is error {
        scope0FaultHandler(result, cx);
    }
}

function start_Processes_Main_process(Context cx) returns () {
    return scope0ScopeFn(cx);
}

function Bar_Receiver(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = xml `<root>${var0}</root>`;
    addToContext(cx, "Bar-Receiver", var1);
}

function HTTP_Response_6(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/EngineTypes" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="2.0">
     <xsl:template name="Transform0" match="/">
        <ResponseActivityInput>
                    
    <asciiContent>
                            
        <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" soap:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/">
                                    
            <soap:Header/>
                                    
            <soap:Body>
                                            
                <response> "bar" </response>
                                        
            </soap:Body>
                                
        </soap:Envelope>
                        
    </asciiContent>
                
</ResponseActivityInput>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xml var3 = var2/**/<asciiContent>/*;
    xml var4 = xml `<root>${var3}</root>`;
    addToContext(cx, "HTTP-Response", var4);
}

function scope0_1ActivityRunner(Context cx) returns error? {
    check Bar_Receiver(cx);
    check HTTP_Response_6(cx);
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

function start_Processes_Bar_process(Context cx) returns () {
    return scope0_1ScopeFn(cx);
}

function BarMapper(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/EngineTypes" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:ns1="http://www.tibco.com/namespaces/tnt/plugins/json" version="2.0"><xsl:param name="Call-Bar"/>     <xsl:template name="Transform2" match="/">
        <BarResponse>
                    
    <xsl:value-of select="$Call-Bar//response" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                
</BarResponse>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xml var3 = xml `<root>${var2}</root>`;
    addToContext(cx, "BarMapper", var3);
}

function Call_Bar(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/EngineTypes" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:ns1="http://www.tibco.com/namespaces/tnt/plugins/json" version="2.0"><xsl:param name="post"/>     <xsl:template name="Transform1" match="/">
        <ns1:ActivityInput xmlns:ns1="http://www.tibco.com/namespaces/tnt/plugins/json">
                    
    <ns1:Parameters>
                            
        <Body>
                                    
            <Main>
                                            
                <xsl:value-of select="$post/root/item/options" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                                        
            </Main>
                                    
            <Foo>
                                            
                <value>
                                99
                            </value>
                                        
            </Foo>
                                
        </Body>
                        
    </ns1:Parameters>
                
</ns1:ActivityInput>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    soap11:Client var3 = check new ("http://localhost:9092");
    xml var4 = xml `<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
    soap:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/">
    <soap:Header/>
    <soap:Body>
        ${var2}
    </soap:Body>
</soap:Envelope>`;
    xml var5 = check var3->sendReceive(var4, "SOAPAction");
    xml var6 = xml `<root>${var5}</root>`;
    addToContext(cx, "Call-Bar", var6);
}

function Foo_Receiver(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = xml `<root>${var0}</root>`;
    addToContext(cx, "Foo-Receiver", var1);
}

function HTTP_Response_11(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/EngineTypes" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:ns1="http://www.tibco.com/namespaces/tnt/plugins/json" version="2.0"><xsl:param name="post"/><xsl:param name="BarMapper"/>     <xsl:template name="Transform3" match="/">
        <ResponseActivityInput>
                    
    <asciiContent>
                            
        <FooResponse>
                                    
            <FromMain>
                                            
                <xsl:value-of select="$post" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                                        
            </FromMain>
                                    
            <FromBar>
                                            
                <xsl:value-of select="$BarMapper" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                                        
            </FromBar>
                                
        </FooResponse>
                        
    </asciiContent>
                
</ResponseActivityInput>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xml var3 = var2/**/<asciiContent>/*;
    xml var4 = xml `<root>${var3}</root>`;
    addToContext(cx, "HTTP-Response", var4);
}

function Log1_8(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/EngineTypes" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:ns1="http://www.tibco.com/namespaces/tnt/plugins/json" version="2.0"><xsl:param name="post"/>     <xsl:template name="Transform0" match="/">
        <ns:ActivityInput xmlns:ns="http://www.tibco.com/pe/EngineTypes">
                    
    <message>
                            Called Foo with
        <xsl:value-of select="$post" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                        
    </message>
                
</ns:ActivityInput>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xml var3 = var2/**/<message>/*;
    log:printInfo(var3.toString());
    addToContext(cx, "Log1", var3);
}

function scope0_2ActivityRunner(Context cx) returns error? {
    check Foo_Receiver(cx);
    check Log1_8(cx);
    check Call_Bar(cx);
    check BarMapper(cx);
    check HTTP_Response_11(cx);
}

function scope0_2FaultHandler(error err, Context cx) returns () {
    panic err;
}

function scope0_2ScopeFn(Context cx) returns () {
    error? result = scope0_2ActivityRunner(cx);
    if result is error {
        scope0_2FaultHandler(result, cx);
    }
}

function start_Processes_Foo_process(Context cx) returns () {
    return scope0_2ScopeFn(cx);
}

function addToContext(Context context, string varName, xml value) {
    xml children = value/*;
    xml transformed = xml `<root>${children}</root>`;
    context.variables[varName] = transformed;
    context.result = value;
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
