function activityExtension(Context cx) returns error? {
    xml var0 = getFromContext(cx, "PsgLog-Input-Var");
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://www.tibco.com/PSGLogActivities" version="2.0"><xsl:template name="PsgLogTemplate" match="/"><tns:LogInput><tns:targetSystem><xsl:value-of select="'DAS'"/></tns:targetSystem><tns:message><xsl:value-of select="'Process started'"/></tns:message></tns:LogInput></xsl:template></xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xml var3 = var2/**/<message>/*;
    psgLog("Info", (var2/**/<targetSystem>/*).toString().trim(), var3, var2/**/<additionalLogParams>/**/<keyValuePair>);
}
