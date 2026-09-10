function SOAPRequestReply(Context cx) returns error? {
    xml var0 = xml`<root></root>`;
    xml var1 = check xml:fromString(string`<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
     <xsl:template name="Transform0" match="/">
        <inputMessage>
                
    <message>
                "foo bar"
            </message>
            
</inputMessage>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    soap11:Client var3 = check new ("http://localhost:8800");
    xml var4 = xml`<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
  soap:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/">
    <soap:Header/>
    <soap:Body>
        ${var2}
    </soap:Body>
</soap:Envelope>`;
    xml var5 = check var3->sendReceive(var4, "SOAPAction");
    xml var6 = xml`<root>${var5}</root>`;
    addToContext(cx, "SOAPRequestReply", var6);
}
