function activityExtension(Context cx) returns error? {
    xml var0 = getFromContext(cx, "RenderOrders-input");
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns1="http://tns.tibco.com/bw/activity/render/xsd/+c3e9b7d2-0a16-4f85-b4d8-9e1a6c2f7b05+input" xmlns:tns2="http://example.com/xsd/orders" version="2.0"><xsl:param name="QueryOrders"/><xsl:template name="RenderOrders-input" match="/"><tns1:Rows><xsl:for-each select="$QueryOrders/root/Record"><tns2:order><tns2:orderId><xsl:value-of select="id"/></tns2:orderId><tns2:customer><xsl:value-of select="customer_name"/></tns2:customer><tns2:amount><xsl:value-of select="total"/></tns2:amount></tns2:order></xsl:for-each></tns1:Rows></xsl:template></xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    string var3 = renderDelimitedData(var2, "order", ";", "\n", ["orderId", "customer", "amount"]);
    xml var4 = xml`<root>${var3}</root>`;
    addToContext(cx, "RenderOrders", var4);
}
