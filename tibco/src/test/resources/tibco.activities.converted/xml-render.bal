function Render(Context cx) returns error? {
    xml var0 = xml`<root></root>`;
    xml var1 = check xslt:transform(var0, xml`<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0"><xsl:param name="Mapper"/>     <xsl:template name="Transform0" match="/">
        <xsl:copy-of select="$Mapper"/>

    </xsl:template>
</xsl:stylesheet>`, cx.variables);
    string var2 = var1.toBalString();
    xml var3 = xml`<root><xmlString>${var2}</xmlString></root>`;
    addToContext(cx, "Render", var3);
}
