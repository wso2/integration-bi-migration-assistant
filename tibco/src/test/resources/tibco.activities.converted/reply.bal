function reply(Context cx) returns error? {
    xml var0 = xml`<root></root>`;
    xml var1 = check xslt:transform(var0, xml`<?xml version="1.0"
        encoding="UTF-8"?>
        <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:tns1="http://xmlns.example.com/20180831151624PLT" xmlns:tns2="/T1535753828744Converted/JsonSchema"
        version="2.0"><xsl:param name="Value"/>xsl:param name="Parameter"/><xsl:template name="reply-template" match="/"><tns1:postResponse><item><tns2:Response><xsl:if
        test="$Value/root/field"><tns2:Field><xsl:value-of select="$Value/root/field"/></tns2:Field></tns2:Response></item></tns1:postResponse></xsl:template></xsl:stylesheet>`, cx.variables);
    setXMLResponse(cx, var1, {});
}
