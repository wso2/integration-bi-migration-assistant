function HTTP_Response(Context cx) returns error? {
    xml var0 = xml`<root></root>`;
    xml var1 = check xml:fromString(string`<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"><xsl:param name="Call-Foo"/>     <xsl:template name="Transform0" match="/">
        <ResponseActivityInput>
                
    <asciiContent>
                        
        <Response>
                                
            <xsl:value-of select="$Call-Foo//FromBar"/>
                            
        </Response>
                    
    </asciiContent>
            
</ResponseActivityInput>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xml var3 = var2/**/<asciiContent>/*;
    xml var4 = xml`<root>${var3}</root>`;
    addToContext(cx, "HTTP-Response", var4);
}
