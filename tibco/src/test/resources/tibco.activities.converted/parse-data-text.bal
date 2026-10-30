function activityExtension(Context cx) returns error? {
    xml var0 = getFromContext(cx, "ParseOrderText-input");
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns1="http://tns.tibco.com/bw/activity/parse/xsd/+8a42c0e1-6f3d-4b57-a1e9-2c7d5f0b9e84+input" version="2.0"><xsl:param name="ReadOrders"/><xsl:template name="ParseOrderText-input" match="/"><tns1:Input><text><xsl:value-of select="$ReadOrders/root/fileContent/textContent"/></text><noOfRecords><xsl:value-of select="-1"/></noOfRecords><SkipHeaderCharacters><xsl:value-of select="12"/></SkipHeaderCharacters></tns1:Input></xsl:template></xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    string var3 = (var2/**/<text>/*).toString();
    
// WARNING: Start record is not manually specified; every run parses from the first record instead of continuing from where the previous iteration stopped.

    
// WARNING: Continue On Error is not supported; ErrorRows is not produced and parsing stops on the first bad record.

    int var4 = check int:fromString((var2/**/<noOfRecords>/*).toString().trim());
    string var5 = (var2/**/<SkipHeaderCharacters>/*).toString().trim();
    int var6 = var5 == "" ? 0 : check int:fromString(var5);
    xml var7 = check parseDelimitedData(var3, ";", "\n", "http://example.com/xsd/orders", "order", ["orderId", "customer", "amount"], true, 1, var4, var6, true);
    addToContext(cx, "ParseOrderText", var7);
}
