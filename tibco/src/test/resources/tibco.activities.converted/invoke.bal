function invoke(Context cx) returns error? {
    xml var0 = xml`<root></root>`;
    xml var1 = check xml:fromString(string`<?xml version="1.0"
        encoding="UTF-8"?>
        <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:tns="http://xmlns.example.com/20180827154353PLT" xmlns:tns1="http://tns.tibco.com/bw/REST"
        xmlns:tns3="mySchema" version="2.0"><xsl:param
        name="Start"/><xsl:template name="post-input" match="/"><tns:postRequest1><item><tns3:Request><xsl:if
        test="$Start/root/field"><tns3:F><xsl:value-of select="$Start/root/field"/></tns3:F></tns3:Request></item><httpHeaders><tns1:httpHeaders/></httpHeaders></tns:postRequest1></xsl:template></xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    json var3 = check httpClient0->post("/path", var2);
    xml var4 = check fromJson(var3);
    xml var5 = xml`<root>${var4}</root>`;
    addToContext(cx, "OutputVariable", var5);
}
