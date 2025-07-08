function List Files Test(Context cx) returns error? {
    xml var0 = xml`<root></root>`;
    xml var1 = check xslt:transform(var0, xml`<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
     <xsl:template name="Transform0" match="/">
        <ns1:ListFilesActivityConfig>
                
    <fileName>test/path/*.txt</fileName>
            
</ns1:ListFilesActivityConfig>

    </xsl:template>
</xsl:stylesheet>`, cx.variables);
    
// WARNING: Only fileName and fullName are supported in ListFilesActivity output.

    string var2 = (var1/**/<fileName>/*).toString().trim();
    FileData[] var3 = check filesInPath(var2, false);
    xml var4 = xml``;
    foreach FileData file in var3 {
    var4 += xml `<fileInfo>
                    <fileName>${file.fileName}</fileName>
                    <fullName>${file.fullName}</fullName>
               </fileInfo>`;
}

    xml var5 = xml`<root>
    <ListFilesActivityOutput xmlns="http://www.tibco.com/namespaces/tnt/plugins/file">
        <files>${var4}</files>
    </ListFilesActivityOutput>
</root>`;
    addToContext(cx, "List-Files-Test", var5);
}
