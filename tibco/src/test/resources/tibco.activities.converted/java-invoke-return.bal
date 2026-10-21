function activityExtension(Context cx) returns error? {
    xml var0 = getFromContext(cx, "ScaleTotal-input");
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="www.tibco.com/plugin/java/JavaInvokeActivityInput+6b0d4f2e-1a97-4c3d-8e5f-27c9a4b1d063+JavaInvokeActivityInput" version="2.0"><xsl:param name="Start"/><xsl:template name="ScaleTotal-input" match="/"><tns:JavaInvokeActivityInput><MethodParameters><value><xsl:value-of select="$Start/root/quantity"/></value><factor><xsl:value-of select="1.5"/></factor></MethodParameters></tns:JavaInvokeActivityInput></xsl:template></xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    int var3 = check int:fromString((var2/**/<value>/*).toString().trim());
    float var4 = check float:fromString((var2/**/<factor>/*).toString().trim());
    float var5 = check trap TextTools_scale(var3, var4);
    xml var6 = xml`<root><MethodReturnValue>${var5}</MethodReturnValue></root>`;
    addToContext(cx, "ScaleTotal", var6);
}
