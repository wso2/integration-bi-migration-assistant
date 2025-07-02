function Parse(Context cx) returns error? {
    xml var0 = xml`<root></root>`;
    xml var1 = check xslt:transform(var0, xml`<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0"><xsl:param name="Render"/>     <xsl:template name="Transform0" match="/">
        <xmlString>
                
    <xsl:value-of select="$Render/root/xmlString"/>
            
</xmlString>

    </xsl:template>
</xsl:stylesheet>`, cx.variables);
    xml var2 = var1/<xmlString>/*;
    string var3 = var2.toString();
    xml var4 = check xml:fromString(var3);
    xml var5 = xml`<root>${var4}</root>`;
    addToContext(cx, "Parse", var5);
}
