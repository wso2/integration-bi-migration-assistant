function Sleep(Context cx) returns error? {
    xml var0 = xml`<root></root>`;
    xml var1 = check xml:fromString(string`<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
     <xsl:template name="Transform0" match="/">
        <ns0:SleepInputSchema>
                
    <IntervalInMillisec>
                        
        <xsl:value-of select="100"/>
                    
    </IntervalInMillisec>
            
</ns0:SleepInputSchema>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    decimal var3 = check decimal:fromString((var2/**/<IntervalInMillisec>/*).toString().trim());
    runtime:sleep(var3 / 1000);
    xml var4 = xml`<root></root>`;
    addToContext(cx, "Sleep", var4);
}
