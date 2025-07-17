function activityExtension(Context cx) returns error? {
    xml var0 = getFromContext(cx, "InputVariable");
    
// WARNING: Non-standard XSLT functions detected: tib:trim, tib:parse-dateTime

    xml var1 = check xslt:transform(var0, xml`<?xml version="1.0" encoding="UTF-8"?><xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tib="http://www.tibco.com/bw/xslt/custom-functions" version="2.0"><xsl:param name="Input"/><xsl:template name="Template" match="/"><result><trimmedValue><xsl:value-of select="tib:trim($Input/root/value)"/></trimmedValue><parsedDate><xsl:value-of select="tib:parse-dateTime($Input/root/dateString, 'yyyy-MM-dd')"/></parsedDate></result></xsl:template></xsl:stylesheet>`, cx.variables);
    xml var2 = xml`<root>${var1}</root>`;
    addToContext(cx, "OutputVariable", var2);
}
