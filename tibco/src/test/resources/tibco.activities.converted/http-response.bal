function HTTP Response(Context cx) returns error? {
    xml var0 = xml`<root></root>`;
    xml var1 = check xslt:transform(var0, xml`<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0"><xsl:param name="Call-Foo"/>     <xsl:template name="Transform0" match="/">
        <ResponseActivityInput>
                
    <asciiContent>
                        
        <Response>
                                
            <xsl:value-of select="$Call-Foo//FromBar"/>
                            
        </Response>
                    
    </asciiContent>
            
</ResponseActivityInput>

    </xsl:template>
</xsl:stylesheet>`, cx.variables);
    xml var2 = var1/**/<asciiContent>/*;
    xml var3 = xml`<root>${var2}</root>`;
    addToContext(cx, "HTTP-Response", var3);
}
