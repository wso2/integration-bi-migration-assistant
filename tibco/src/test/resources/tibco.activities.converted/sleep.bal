function Sleep(Context cx) returns error? {
    xml var0 = xml`<root></root>`;
    xml var1 = check xslt:transform(var0, xml`<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0">
     <xsl:template name="Transform0" match="/">
        <ns0:SleepInputSchema>
                
    <IntervalInMillisec>
                        
        <xsl:value-of select="100"/>
                    
    </IntervalInMillisec>
            
</ns0:SleepInputSchema>

    </xsl:template>
</xsl:stylesheet>`, cx.variables);
    decimal var2 = check decimal:fromString((var1/**/<IntervalInMillisec>/*).toString().trim());
    runtime:sleep(var2 / 1000);
    xml var3 = xml`<root></root>`;
    addToContext(cx, "Sleep", var3);
}
