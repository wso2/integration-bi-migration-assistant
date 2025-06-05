function activityExtension(map<xml> context) returns xml | error {
    xml var0 = context.get("InputVariable");
    xml var1 = check xslt:transform(var0, xml`<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns1="http://www.example.org/LogSchema" xmlns:tns="http://www.tibco.com/xml/render/example" version="2.0"><xsl:param name="Start"/><xsl:template name="RenderXml-input" match="/"><tns:InputElement><level><xsl:value-of select="$Start/root/tns1:level"/></level><message><xsl:value-of select="$Start/root/tns1:message"/></message><logger><xsl:value-of select="$Start/root/tns1:loggerName"/></logger><timestamp><xsl:value-of select="current-dateTime()"/></timestamp></tns:InputElement></xsl:template></xsl:stylesheet>`, context);
    xml var2 = xml`<root>${var1}</root>`;
    addToContext(context, "OutputVariable", var2);
    return var2;
}
