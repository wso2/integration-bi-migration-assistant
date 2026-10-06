function activityExtension(Context cx) returns error? {
    xml var0 = getFromContext(cx, "ArchiveOrderFile-input");
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns1="http://tns.tibco.com/bw/activity/renameFile" version="2.0"><xsl:param name="InboundDir"/><xsl:param name="ArchiveDir"/><xsl:param name="OrderFileName"/><xsl:template name="ArchiveOrderFile-input" match="/"><tns1:Input><OldRemoteFileName><xsl:value-of select="concat($InboundDir, $OrderFileName)"/></OldRemoteFileName><NewRemoteFileName><xsl:value-of select="concat($ArchiveDir, $OrderFileName)"/></NewRemoteFileName></tns1:Input></xsl:template></xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    string var3 = (var2/**/<OldRemoteFileName>/*).toString();
    string var4 = (var2/**/<NewRemoteFileName>/*).toString();
    check OrderSftpConnection->rename(var3, var4);
}
