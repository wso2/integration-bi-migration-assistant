function activityExtension(Context cx) returns error? {
    xml var0 = getFromContext(cx, "PsgLog-Input-Var");
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://www.tibco.com/PSGLogActivities" version="2.0"><xsl:template name="PsgLogTemplate" match="/"><tns:LogInput><tns:targetSystem><xsl:value-of select="'DAS'"/></tns:targetSystem><tns:message><xsl:value-of select="'Something went wrong'"/></tns:message><tns:additionalLogParams><tns:keyValuePair><tns:key><xsl:value-of select="'PAYLOAD'"/></tns:key><tns:value><xsl:value-of select="'payload-data'"/></tns:value></tns:keyValuePair></tns:additionalLogParams></tns:LogInput></xsl:template></xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xml var3 = var2/**/<message>/*;
    psgLog("Warning", (var2/**/<targetSystem>/*).toString().trim(), var3, var2/**/<additionalLogParams>/**/<keyValuePair>);
}
