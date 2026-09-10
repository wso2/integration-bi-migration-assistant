function Write_File(Context cx) returns error? {
    xml var0 = xml`<root></root>`;
    xml var1 = check xml:fromString(string`<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"><xsl:param name="Mapper"/>     <xsl:template name="Transform0" match="/">
        <ns0:WriteActivityInputTextClass xmlns:ns0="http://www.tibco.com/namespaces/tnt/plugins/file">
                
    <fileName>
                        
        <xsl:value-of select="&quot;output.txt&quot;"/>
                    
    </fileName>
                
    <textContent>
                        
        <xsl:value-of select="$Mapper"/>
                    
    </textContent>
                
    <addLineSeparator>
                        
        <xsl:value-of select="true()"/>
                    
    </addLineSeparator>
            
</ns0:WriteActivityInputTextClass>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    string fileName = (var2/**/<fileName>/*).toString();
    string content = (var2/**/<textContent>/*).toString();
    check io:fileWriteString(fileName, content, "APPEND");
    addToContext(cx, "Write-File", var2);
}
