function activityExtension(Context cx) returns error? {
    xml var0 = getFromContext(cx, "DeleteProcessedOrder-input");
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns1="http://tns.tibco.com/bw/activity/removeRemoteFile" version="2.0"><xsl:param name="OutboundDir"/><xsl:param name="FileName"/><xsl:template name="DeleteProcessedOrder-input" match="/"><tns1:Input><RemoteDirectory><xsl:value-of select="$OutboundDir"/></RemoteDirectory><RemoteFileName><xsl:value-of select="$FileName"/></RemoteFileName></tns1:Input></xsl:template></xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    string var3 = (var2/**/<RemoteDirectory>/*).toString() + (var2/**/<RemoteFileName>/*).toString();
    check sftpDeleteFiles(OrderSftpConnection, var3);
}
