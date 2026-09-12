function Read_file(Context cx) returns error? {
    xml var0 = xml`<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
     <xsl:template name="Transform0" match="/">
        <ReadActivityInputClass>
                
    <fileName>
                        
        <xsl:value-of select="&quot;input.txt&quot;"/>
                    
    </fileName>
            
</ReadActivityInputClass>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    string fileName = (var2/**/<fileName>/*).toString();
    string content = check io:fileReadString(fileName);
    xml var3 = xml`<ns:ReadActivityOutputTextClass xmlns:ns="http://www.tibco.com/namespaces/tnt/plugins/file">
    <fileContent>
        <textContent>${content}</textContent>
    </fileContent>
</ns:ReadActivityOutputTextClass>`;
    addToContext(cx, "Read-file", var3);
}
