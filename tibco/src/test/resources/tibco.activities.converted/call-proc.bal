function extActivity(Context cx) returns error? {
    xml var0 = xml`<root></root>`;
    xml var1 = check xslt:transform(var0, xml`<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"><xsl:param name="post"/><xsl:template name="Template" match="/"><Request><xsl:if test="$post/root/body"><tns:Body><xsl:value-of select="$post/root/body"/></tns:Body></Request></xsl:template></xsl:stylesheet>`, cx.variables);
    xml var2 = transform(var1);
    xml var3 = check processClient->post("", var2);
    xml var4 = xml`<root>${var3}</root>`;
    addToContext(cx, "OutputVariable", var4);
}
