import ballerina/log;
import ballerina/soap.soap11;
import ballerina/xslt;

function Call_Foo(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            xmlns:tns="http://xmlns.example.com" version="2.0"><xsl:param name="post"/>     <xsl:template name="Transform1" match="/">
        <InvokeProcessInput>
                    
    <options>
                            
        <xsl:value-of select="$post/root/item/req" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                        
    </options>
                
</InvokeProcessInput>

    </xsl:template>
</xsl:stylesheet>`, cx.variables);
    xml var2 = var1/*;
    xml var3 = check proj_annon_var2->post("", var2);
    addToContext(cx, "Call-Foo", var3);
}

function HTTP_Receiver(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = xml `<root>${var0}</root>`;
    addToContext(cx, "HTTP-Receiver", var1);
}

function HTTP_Response(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            xmlns:tns="http://xmlns.example.com" version="2.0"><xsl:param name="Call-Foo"/>     <xsl:template name="Transform3" match="/">
        <ResponseActivityInput>
                    
    <asciiContent>
                            
        <Response>
                                    
            <xsl:value-of select="$Call-Foo//FromBar" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                                
        </Response>
                        
    </asciiContent>
                
</ResponseActivityInput>

    </xsl:template>
</xsl:stylesheet>`, cx.variables);
    xml var2 = var1/**/<asciiContent>/*;
    addToContext(cx, "HTTP-Response", var2);
}

function Log1(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            xmlns:tns="http://xmlns.example.com" version="2.0"><xsl:param name="post"/>     <xsl:template name="Transform0" match="/">
        <ns:ActivityInput xmlns:ns="http://www.tibco.com/pe/EngineTypes">
                    
    <message>
         Before calling Foo, initial payload 
        <xsl:value-of select="$post" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
    </message>
                
</ns:ActivityInput>

    </xsl:template>
</xsl:stylesheet>`, cx.variables);
    xml var2 = var1/**/<message>/*;
    log:printInfo(var2.toString());
    addToContext(cx, "Log1", var2);
}

function Log2(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            xmlns:tns="http://xmlns.example.com" version="2.0"><xsl:param name="Call-Foo"/>     <xsl:template name="Transform2" match="/">
        <ns:ActivityInput xmlns:ns="http://www.tibco.com/pe/EngineTypes">
                    
    <message>
        After calling Foo, received
        <xsl:value-of select="$Call-Foo" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
    </message>
                
</ns:ActivityInput>

    </xsl:template>
</xsl:stylesheet>`, cx.variables);
    xml var2 = var1/**/<message>/*;
    log:printInfo(var2.toString());
    addToContext(cx, "Log2", var2);
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
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            xmlns:tns="http://xmlns.example.com" version="2.0">
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
</xsl:stylesheet>`, cx.variables);
    xml var2 = var1/**/<asciiContent>/*;
    addToContext(cx, "HTTP-Response", var2);
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
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            xmlns:tns="http://xmlns.example.com" version="2.0"><xsl:param name="Call-Bar"/>     <xsl:template name="Transform2" match="/">
        <BarResponse>
                    
    <xsl:value-of select="$Call-Bar//response" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                
</BarResponse>

    </xsl:template>
</xsl:stylesheet>`, cx.variables);
    xml var2 = xml `<root>${var1}</root>`;
    addToContext(cx, "BarMapper", var2);
}

function Call_Bar(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            xmlns:tns="http://xmlns.example.com" version="2.0"><xsl:param name="post"/>     <xsl:template name="Transform1" match="/">
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
</xsl:stylesheet>`, cx.variables);
    soap11:Client var2 = check new ("http://localhost:9092");
    xml var3 = xml `<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
    soap:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/">
    <soap:Header/>
    <soap:Body>
        ${var1}
    </soap:Body>
</soap:Envelope>`;
    xml var4 = check var2->sendReceive(var3, "SOAPAction");
    addToContext(cx, "Call-Bar", var4);
}

function Foo_Receiver(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = xml `<root>${var0}</root>`;
    addToContext(cx, "Foo-Receiver", var1);
}

function HTTP_Response_11(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            xmlns:tns="http://xmlns.example.com" version="2.0"><xsl:param name="post"/><xsl:param name="BarMapper"/>     <xsl:template name="Transform3" match="/">
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
</xsl:stylesheet>`, cx.variables);
    xml var2 = var1/**/<asciiContent>/*;
    addToContext(cx, "HTTP-Response", var2);
}

function Log1_8(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            xmlns:tns="http://xmlns.example.com" version="2.0"><xsl:param name="post"/>     <xsl:template name="Transform0" match="/">
        <ns:ActivityInput xmlns:ns="http://www.tibco.com/pe/EngineTypes">
                    
    <message>
                            Called Foo with
        <xsl:value-of select="$post" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                        
    </message>
                
</ns:ActivityInput>

    </xsl:template>
</xsl:stylesheet>`, cx.variables);
    xml var2 = var1/**/<message>/*;
    log:printInfo(var2.toString());
    addToContext(cx, "Log1", var2);
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

function initContext(map<xml> initVariables = {}) returns Context {
    return {variables: initVariables, result: xml `<root/>`};
}
