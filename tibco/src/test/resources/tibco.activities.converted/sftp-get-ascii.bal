function activityExtension(Context cx) returns error? {
    xml var0 = getFromContext(cx, "FetchDailyCsvFiles-input");
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns1="http://tns.tibco.com/bw/activity/getFile" version="2.0"><xsl:template name="FetchDailyCsvFiles-input" match="/"><tns1:SFTPGetInputDataFile><RemoteFileName><xsl:value-of select="'/inbound/*.csv'"/></RemoteFileName><LocalFileName><xsl:value-of select="'/work/'"/></LocalFileName></tns1:SFTPGetInputDataFile></xsl:template></xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    
// WARNING: SFTP Get ASCII mode is not supported; files are copied byte for byte.

    string var3 = (var2/**/<RemoteFileName>/*).toString();
    string var4 = (var2/**/<LocalFileName>/*).toString();
    xml var5 = check sftpGetFiles(OrderSftpConnection, var3, var4, false);
    xml var6 = xml`<root>${var5}</root>`;
    addToContext(cx, "FetchDailyCsvFiles", var6);
}
