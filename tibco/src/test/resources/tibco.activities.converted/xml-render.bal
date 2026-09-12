function Render(Context cx) returns error? {
    xml var0 = xml`<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"><xsl:param name="Mapper"/>     <xsl:template name="Transform0" match="/">
        <xsl:copy-of select="$Mapper"/>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    string var3 = var2.toBalString();
    xml var4 = xml`<root><xmlString>${var3}</xmlString></root>`;
    addToContext(cx, "Render", var4);
}
