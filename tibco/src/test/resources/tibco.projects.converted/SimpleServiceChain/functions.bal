import ballerina/log;
import ballerina/soap.soap11;
import ballerina/xslt;

function Call_Foo(Context context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0"><xsl:param name="post"/>     <xsl:template name="Transform1" match="/">
        <InvokeProcessInput>
                    
    <options>
                            
        <xsl:value-of select="$post/root/item/req" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                        
    </options>
                
</InvokeProcessInput>

    </xsl:template>
</xsl:stylesheet>`, context);
    xml var2 = var1/*;
    xml var3 = check proj_annon_var2->post("", var2);
    addToContext(context, "Call-Foo", var3);
    return var3;
}

function HTTP_Receiver(Context context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = xml `<root>${var0}</root>`;
    addToContext(context, "HTTP-Receiver", var1);
    return var1;
}

function HTTP_Response(Context context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0"><xsl:param name="Call-Foo"/>     <xsl:template name="Transform3" match="/">
        <ResponseActivityInput>
                    
    <asciiContent>
                            
        <Response>
                                    
            <xsl:value-of select="$Call-Foo//FromBar" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                                
        </Response>
                        
    </asciiContent>
                
</ResponseActivityInput>

    </xsl:template>
</xsl:stylesheet>`, context);
    xml var2 = var1/**/<asciiContent>/*;
    addToContext(context, "HTTP-Response", var2);
    return var2;
}

function Log1(Context context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0"><xsl:param name="post"/>     <xsl:template name="Transform0" match="/">
        <ns:ActivityInput xmlns:ns="http://www.tibco.com/pe/EngineTypes">
                    
    <message>
         Before calling Foo, initial payload 
        <xsl:value-of select="$post" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
    </message>
                
</ns:ActivityInput>

    </xsl:template>
</xsl:stylesheet>`, context);
    xml var2 = var1/**/<message>/*;
    log:printInfo(var2.toString());
    addToContext(context, "Log1", var2);
    return var2;
}

function Log2(Context context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0"><xsl:param name="Call-Foo"/>     <xsl:template name="Transform2" match="/">
        <ns:ActivityInput xmlns:ns="http://www.tibco.com/pe/EngineTypes">
                    
    <message>
        After calling Foo, received
        <xsl:value-of select="$Call-Foo" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
    </message>
                
</ns:ActivityInput>

    </xsl:template>
</xsl:stylesheet>`, context);
    xml var2 = var1/**/<message>/*;
    log:printInfo(var2.toString());
    addToContext(context, "Log2", var2);
    return var2;
}

function scope0ActivityRunner(map<xml> cx) returns xml|error {
    xml result0 = check HTTP_Receiver(cx);
    xml result1 = check Log1(cx);
    xml result2 = check Call_Foo(cx);
    xml result3 = check Log2(cx);
    xml result4 = check HTTP_Response(cx);
    return result4;
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

function start_Processes_Main_process(Context cx) returns xml {
    return scope0ScopeFn(cx);
}

function Bar_Receiver(Context context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = xml `<root>${var0}</root>`;
    addToContext(context, "Bar-Receiver", var1);
    return var1;
}

function HTTP_Response_6(Context context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0">
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
</xsl:stylesheet>`, context);
    xml var2 = var1/**/<asciiContent>/*;
    addToContext(context, "HTTP-Response", var2);
    return var2;
}

function scope0_1ActivityRunner(map<xml> cx) returns xml|error {
    xml result0 = check Bar_Receiver(cx);
    xml result1 = check HTTP_Response_6(cx);
    return result1;
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

function start_Processes_Bar_process(Context cx) returns xml {
    return scope0_1ScopeFn(cx);
}

function BarMapper(Context context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0"><xsl:param name="Call-Bar"/>     <xsl:template name="Transform2" match="/">
        <BarResponse>
                    
    <xsl:value-of select="$Call-Bar//response" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                
</BarResponse>

    </xsl:template>
</xsl:stylesheet>`, context);
    xml var2 = xml `<root>${var1}</root>`;
    addToContext(context, "BarMapper", var2);
    return var2;
}

function Call_Bar(Context context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0"><xsl:param name="post"/>     <xsl:template name="Transform1" match="/">
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
</xsl:stylesheet>`, context);
    soap11:Client var2 = check new ("http://localhost:9092");
    xml var3 = xml `<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
    soap:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/">
    <soap:Header/>
    <soap:Body>
        ${var1}
    </soap:Body>
</soap:Envelope>`;
    xml var4 = check var2->sendReceive(var3, "SOAPAction");
    addToContext(context, "Call-Bar", var4);
    return var4;
}

function Foo_Receiver(Context context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = xml `<root>${var0}</root>`;
    addToContext(context, "Foo-Receiver", var1);
    return var1;
}

function HTTP_Response_11(Context context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0"><xsl:param name="post"/><xsl:param name="BarMapper"/>     <xsl:template name="Transform3" match="/">
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
</xsl:stylesheet>`, context);
    xml var2 = var1/**/<asciiContent>/*;
    addToContext(context, "HTTP-Response", var2);
    return var2;
}

function Log1_8(Context context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0"><xsl:param name="post"/>     <xsl:template name="Transform0" match="/">
        <ns:ActivityInput xmlns:ns="http://www.tibco.com/pe/EngineTypes">
                    
    <message>
                            Called Foo with
        <xsl:value-of select="$post" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                        
    </message>
                
</ns:ActivityInput>

    </xsl:template>
</xsl:stylesheet>`, context);
    xml var2 = var1/**/<message>/*;
    log:printInfo(var2.toString());
    addToContext(context, "Log1", var2);
    return var2;
}

function scope0_2ActivityRunner(map<xml> cx) returns xml|error {
    xml result0 = check Foo_Receiver(cx);
    xml result1 = check Log1_8(cx);
    xml result2 = check Call_Bar(cx);
    xml result3 = check BarMapper(cx);
    xml result4 = check HTTP_Response_11(cx);
    return result4;
}

function scope0_2FaultHandler(error err, map<xml> cx) returns xml {
    panic err;
}

function scope0_2ScopeFn(map<xml> cx) returns xml {
    xml|error result = scope0_2ActivityRunner(cx);
    if result is error {
        return scope0_2FaultHandler(result, cx);
    }
    return result;
}

function start_Processes_Foo_process(Context cx) returns xml {
    return scope0_2ScopeFn(cx);
}

function addToContext(Context context, string varName, xml value) {
    xml children = value/*;
    xml transformed = xml `<root>${children}</root>`;
    context[varName] = transformed;
}
