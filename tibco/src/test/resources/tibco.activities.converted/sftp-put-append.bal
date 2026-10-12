function activityExtension(Context cx) returns error? {
    xml var0 = getFromContext(cx, "AppendDailyCsvFiles-input");
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns1="http://tns.tibco.com/bw/activity/putFile" version="2.0"><xsl:template name="AppendDailyCsvFiles-input" match="/"><tns1:SFTPPutInputDataFile><RemoteFileName><xsl:value-of select="'/outbound/'"/></RemoteFileName><LocalFileName><xsl:value-of select="'/work/*.csv'"/></LocalFileName></tns1:SFTPPutInputDataFile></xsl:template></xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    
// WARNING: SFTP Put ASCII mode is not supported; files are copied byte for byte.

    string var3 = (var2/**/<LocalFileName>/*).toString();
    string var4 = (var2/**/<RemoteFileName>/*).toString();
    xml var5 = check sftpPutFiles(OrderSftpConnection, var3, var4, false, true);
    xml var6 = xml`<root>${var5}</root>`;
    addToContext(cx, "AppendDailyCsvFiles", var6);
}
