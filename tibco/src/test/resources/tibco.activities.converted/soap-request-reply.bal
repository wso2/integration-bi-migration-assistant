function SOAPRequestReply(Context cx) returns error? {
    xml var0 = xml`<root></root>`;
    xml var1 = check xslt:transform(var0, xml`<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0">
     <xsl:template name="Transform0" match="/">
        <inputMessage>
                
    <message>
                "foo bar"
            </message>
            
</inputMessage>

    </xsl:template>
</xsl:stylesheet>`, cx.variables);
    soap11:Client var2 = check new ("http://localhost:8800");
    xml var3 = xml`<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
  soap:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/">
    <soap:Header/>
    <soap:Body>
        ${var1}
    </soap:Body>
</soap:Envelope>`;
    xml var4 = check var2->sendReceive(var3, "SOAPAction");
    xml var5 = xml`<root>${var4}</root>`;
    addToContext(cx, "SOAPRequestReply", var5);
}
