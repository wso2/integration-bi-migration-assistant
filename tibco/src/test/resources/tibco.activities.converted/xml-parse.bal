function Parse(Context cx) returns error? {
    xml var0 = xml`<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"><xsl:param name="Render"/>     <xsl:template name="Transform0" match="/">
        <xmlString>
                
    <xsl:value-of select="$Render/root/xmlString"/>
            
</xmlString>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xml var3 = var2/<xmlString>/*;
    string var4 = var3.toString();
    xml var5 = check xml:fromString(var4);
    xml var6 = xml`<root>${var5}</root>`;
    addToContext(cx, "Parse", var6);
}
