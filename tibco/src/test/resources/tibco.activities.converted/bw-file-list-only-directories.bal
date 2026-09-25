function activityExtension(Context cx) returns error? {
    xml var0 = getFromContext(cx, "ListFilesLocalResponse-input");
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns5="http://www.tibco.com/namespaces/tnt/plugins/file" version="2.0"><xsl:param name="LocalWorkPath"/><xsl:template name="ListFilesLocalResponse-input" match="/"><tns5:ListFilesActivityConfig><fileName><xsl:value-of select="concat($LocalWorkPath, '*.csv')"/></fileName></tns5:ListFilesActivityConfig></xsl:template></xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    
// WARNING: Only fileName and fullName are supported in ListFiles output.

    string var3 = (var2/**/<fileName>/*).toString().trim();
    FileData[] var4 = check filesInPath(var3, false, true);
    xml var5 = xml``;
    foreach FileData file in var4 {
    var5 += xml `<fileInfo>
                    <fileName>${file.fileName}</fileName>
                    <fullName>${file.fullName}</fullName>
               </fileInfo>`;
}

    xml var6 = xml`<root>
    <ListFilesActivityOutput xmlns="http://www.tibco.com/namespaces/tnt/plugins/file">
        <files>${var5}</files>
    </ListFilesActivityOutput>
</root>`;
    addToContext(cx, "ListFilesLocalResponse", var6);
}
