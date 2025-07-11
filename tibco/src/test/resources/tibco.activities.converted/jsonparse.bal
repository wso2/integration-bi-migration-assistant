function activityExtension(Context cx) returns error? {
    xml var0 = getFromContext(cx, "InputVariable");
    xml var1 = check xslt:transform(var0, xml`<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns5="activity.jsonParser.input+a3fa07a6-0270-48b7-ba84-7de6924acb3d+ActivityInputType" version="2.0"><xsl:param name="SendHTTPRequest"/><xsl:template name="ParseJSON-input" match="/"><tns5:ActivityInputClass><jsonString><xsl:value-of select="$SendHTTPRequest/root/asciiContent"/></jsonString></tns5:ActivityInputClass></xsl:template></xsl:stylesheet>`, cx.variables);
    xml var2 = check renderJsonAsElementXML(var1);
    xml var3 = xml`<root>${var2}</root>`;
    addToContext(cx, "OutputVariable", var3);
}
