function activityExtension(Context cx) returns error? {
    xml var0 = getFromContext(cx, "SetAndLog-Input-Var");
    
// WARNING: Non-standard XSLT functions detected: tib:uuid

    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://www.tibco.com/PSGLogActivities" xmlns:tib="http://www.tibco.com/bw/xslt/custom-functions" version="2.0"><xsl:template name="SetAndLog-input" match="/"><tns:SetAndLogInput><tns:Header><tns:sessionId><xsl:value-of select="tib:uuid()"/></tns:sessionId><tns:correlationId><xsl:value-of select="concat('batch_', tib:uuid())"/></tns:correlationId><tns:trackingId><xsl:value-of select="tib:uuid()"/></tns:trackingId><tns:sender><xsl:value-of select="'BATCH'"/></tns:sender><tns:serviceScope><xsl:value-of select="'BATCH_SCOPE'"/></tns:serviceScope></tns:Header><tns:Input><tns:targetSystem><xsl:value-of select="'DAS'"/></tns:targetSystem><tns:message><xsl:value-of select="'Starting batch processing'"/></tns:message></tns:Input></tns:SetAndLogInput></xsl:template></xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xmlns "http://www.tibco.com/PSGLogActivities" as psglog;
    xml var3 = var2/**/<psglog:message>/*;
    psgSetAndLog("Warning", (var2/**/<psglog:targetSystem>/*).toString().trim(), var3, (var2/**/<psglog:sessionId>/*).toString().trim(), (var2/**/<psglog:correlationId>/*).toString().trim(), (var2/**/<psglog:trackingId>/*).toString().trim(), (var2/**/<psglog:sender>/*).toString().trim(), (var2/**/<psglog:serviceScope>/*).toString().trim());
}
