function extActivity(map<xml> context) returns xml | error {
    xml var0 = xml`<root></root>`;
    xml var1 = check xslt:transform(var0, xml`<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"><xsl:param name="post"/><xsl:template name="Template" match="/"><Request><xsl:if test="$post/root/body"><tns:Body><xsl:value-of select="$post/root/body"/></tns:Body></Request></xsl:template></xsl:stylesheet>`, context);
    xml var2 = transform(var1);
    xml var3 = check processClient->post("", var2);
    addToContext(context, "OutputVariable", var3);
    return var3;
}
