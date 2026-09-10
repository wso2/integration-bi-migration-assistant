function activityExtension(Context cx) returns error? {
    xml var0 = getFromContext(cx, "InputVariable");
    xml var1 = check xml:fromString(string`<?xml version="1.0" encoding="UTF-8"?><xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://fundtech.com/SCL/CommonTypes" xmlns:tns1="http://www.vietcombank.com.vn/framework/utilities/pattern/pattern/1.0" version="2.0"><xsl:param name="Start"/><xsl:template name="Template" match="/"><xsl:copy-of select="$Start/root/msg"/></xsl:template></xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xml var3 = xml`<root>${var2}</root>`;
    addToContext(cx, "OutputVariable", var3);
}
