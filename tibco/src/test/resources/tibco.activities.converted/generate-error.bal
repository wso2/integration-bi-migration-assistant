function Generate_Error(Context cx) returns error? {
    xml var0 = xml`<root></root>`;
    xml var1 = check xslt:transform(var0, xml`<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"><xsl:param name="msg"/><xsl:param name="code"/>     <xsl:template name="Transform0" match="/">
        <xsl:variable name="code" select="'code'"/>
<xsl:variable name="msg" select="'msg'"/>
<ns:ActivityInput>
                
    <message>
                        
        <xsl:value-of select="$msg"/>
                    
    </message>
                
    <messageCode>
                        
        <xsl:value-of select="$code"/>
                    
    </messageCode>
            
</ns:ActivityInput>

    </xsl:template>
</xsl:stylesheet>`, cx.variables);
    string var2 = runtime:getStackTrace().toString();
    xml var3 = xml`<ns:ErrorReport xmlns:ns="http://www.tibco.com/pe/EngineTypes">
    <StackTrace>${stackTrace}</StackTrace>
    ${var1/*}
</ns:ErrorReport>`;
    addToContext(cx, "$_error", var3);
    addToContext(cx, "Generate-Error", var3);
}
