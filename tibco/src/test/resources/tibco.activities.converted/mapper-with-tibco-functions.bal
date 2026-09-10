function activityExtension(Context cx) returns error? {
    xml var0 = getFromContext(cx, "InputVariable");
    
// WARNING: Non-standard XSLT functions detected: tib:trim, tib:parse-dateTime

    xml var1 = check xml:fromString(string`<?xml version="1.0" encoding="UTF-8"?><xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tib="http://www.tibco.com/bw/xslt/custom-functions" version="2.0"><xsl:param name="Input"/><xsl:template name="Template" match="/"><result><trimmedValue><xsl:value-of select="tib:trim($Input/root/value)"/></trimmedValue><parsedDate><xsl:value-of select="tib:parse-dateTime($Input/root/dateString, 'yyyy-MM-dd')"/></parsedDate></result></xsl:template></xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xml var3 = xml`<root>${var2}</root>`;
    addToContext(cx, "OutputVariable", var3);
}
