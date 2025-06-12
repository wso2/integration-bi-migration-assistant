function throw(Context context) returns xml | error {
    xml var0 = xml`<root></root>`;
    xml var1 = check xslt:transform(var0, xml`<?xml version="1.0" encoding="UTF-8"?><xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://schemas.tibco.com/bw/plugins/basic/6.0/Exceptions" version="2.0"><xsl:template name="Throw-input" match="/"><tns:DefaultFault/></xsl:template> </xsl:stylesheet>`, context);
    xml var2 = xml`<root>${var1}</root>`;
    error var3 = error("TODO: create error value");
    panic var3;
}
