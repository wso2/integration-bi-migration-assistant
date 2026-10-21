function activityExtension(Context cx) returns error? {
    xml var0 = getFromContext(cx, "NormalizeCode-input");
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="www.tibco.com/plugin/java/JavaInvokeActivityInput+9c3e7a10-5b2f-4d86-b1e4-0f6a8d2c7e95+JavaInvokeActivityInput" version="2.0"><xsl:param name="Start"/><xsl:template name="NormalizeCode-input" match="/"><tns:JavaInvokeActivityInput><MethodParameters><type><xsl:value-of select="$Start/root/code"/></type></MethodParameters></tns:JavaInvokeActivityInput></xsl:template></xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    handle var3 = (var2/**/<'type>).length() == 0 ? java:createNull() : java:fromString((var2/**/<'type>/*).toString());
    handle var4 = check trap TextTools_normalize(var3);
    string? var5 = java:toString(var4);
    xml var6 = xml`<root>${var5 is () ? xml `` : xml `<MethodReturnValue>${var5}</MethodReturnValue>`}</root>`;
    addToContext(cx, "NormalizeCode", var6);
}
