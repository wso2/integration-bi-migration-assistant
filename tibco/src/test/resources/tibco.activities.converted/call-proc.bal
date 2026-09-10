function extActivity(Context cx) returns error? {
    xml var0 = xml`<root></root>`;
    xml var1 = check xml:fromString(string`<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"><xsl:param name="post"/><xsl:template name="Template" match="/"><Request><xsl:if test="$post/root/body"><tns:Body><xsl:value-of select="$post/root/body"/></tns:Body></Request></xsl:template></xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xml var3 = transform(var2);
    xml var4 = check processClient->post("", var3);
    xml var5 = xml`<root>${var4}</root>`;
    addToContext(cx, "OutputVariable", var5);
}
