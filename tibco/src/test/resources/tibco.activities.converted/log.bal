function activityExtension(Context context) returns xml | error {
    xml var0 = context.get("Log-Input-Var");
    xml var1 = check xslt:transform(var0, xml`<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://www.tibco.com/pe/WriteToLogActivitySchema" version="2.0"><xsl:template name="LogTemplate" match="/"><tns:ActivityInput><message><xsl:value-of select="'Hello World'"/></message></tns:ActivityInput></xsl:template></xsl:stylesheet>`, context);
    xml var2 = var1/**/<message>/*;
    log:printInfo(var2.toString());
    return var2;
}
