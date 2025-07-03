function Read file(Context cx) returns error? {
    xml var0 = xml`<root></root>`;
    xml var1 = check xslt:transform(var0, xml`<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
     <xsl:template name="Transform0" match="/">
        <ReadActivityInputClass>
                
    <fileName>
                        
        <xsl:value-of select="&quot;input.txt&quot;"/>
                    
    </fileName>
            
</ReadActivityInputClass>

    </xsl:template>
</xsl:stylesheet>`, cx.variables);
    string fileName = (var1/**/<fileName>/*).toString();
    string content = check io:fileReadString(fileName);
    xml var2 = xml`<ns:ReadActivityOutputTextClass xmlns:ns="http://www.tibco.com/namespaces/tnt/plugins/file">
    <fileContent>
        <textContent>${content}</textContent>
    </fileContent>
</ns:ReadActivityOutputTextClass>`;
    addToContext(cx, "Read-file", var2);
}
