function activityExtension(Context cx) returns error? {
    xml var0 = getFromContext(cx, "ListHomeDirectory-input");
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns1="http://tns.tibco.com/bw/activity/dir" version="2.0"><xsl:template name="ListHomeDirectory-input" match="/"><tns1:Input/></xsl:template></xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    
// WARNING: SFTP Dir detailed listing is not supported; only file names are listed.

    string var3 = (var2/**/<Directory>/*).toString();
    ftp:FileInfo[] var4 = check OrderSftpConnection->list(var3 == "" ? "." : var3);
    xml var5 = xml``;
    foreach ftp:FileInfo entry in var4 {
    var5 += xml `<DirectoryItems>${entry.name}</DirectoryItems>`;
}

    xml var6 = xml`<root><ItemCount>${var4.length()}</ItemCount>${var5}</root>`;
    addToContext(cx, "ListHomeDirectory", var6);
}
