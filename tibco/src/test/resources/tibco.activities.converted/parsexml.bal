function activityExtension(Context cx) returns error? {
    xml var0 = getFromContext(cx, "ParseXml-input");
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns4="http://www.tibco.com/namespaces/tnt/plugins/parsexml" version="2.0"><xsl:param name="JMSReceiveMessage"/><xsl:template name="ParseXml-input" match="/"><tns4:xmlString><xsl:value-of select="$JMSReceiveMessage/root/Body"/></tns4:xmlString></xsl:template></xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xml var3 = xml`<root>${check xml:fromString((var2/*).toString())}</root>`;
    addToContext(cx, "ParseXml", var3);
}
