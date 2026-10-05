function activityExtension(Context cx) returns error? {
    xml var0 = getFromContext(cx, "ParseOrders-input");
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns1="http://tns.tibco.com/bw/activity/parse/xsd/+5d1f3a7c-2b84-4e9a-9c61-7f0e8b2a4d13+input" version="2.0"><xsl:param name="InboundDir"/><xsl:param name="BatchSize"/><xsl:param name="batchIndex"/><xsl:template name="ParseOrders-input" match="/"><tns1:Input><fileName><xsl:value-of select="concat($InboundDir, 'orders.txt')"/></fileName><startRecord><xsl:value-of select="(($batchIndex - 1) * $BatchSize) + 1"/></startRecord><noOfRecords><xsl:value-of select="$BatchSize"/></noOfRecords></tns1:Input></xsl:template></xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    string var3 = check string:fromBytes(check io:fileReadBytes((var2/**/<fileName>/*).toString().trim()));
    int var4 = check int:fromString((var2/**/<startRecord>/*).toString().trim());
    int var5 = check int:fromString((var2/**/<noOfRecords>/*).toString().trim());
    string var6 = (var2/**/<SkipHeaderCharacters>/*).toString().trim();
    int var7 = var6 == "" ? 0 : check int:fromString(var6);
    xml var8 = check parseDelimitedData(var3, ";", "\n", "http://example.com/xsd/orders", "order", ["orderId", "customer", "amount"], true, var4, var5, var7, false);
    addToContext(cx, "ParseOrders", var8);
}
