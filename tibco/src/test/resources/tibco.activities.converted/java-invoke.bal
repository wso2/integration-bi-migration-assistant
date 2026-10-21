function activityExtension(Context cx) returns error? {
    xml var0 = getFromContext(cx, "ArchiveReports-input");
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="www.tibco.com/plugin/java/JavaInvokeActivityInput+2f6c1e9a-7d43-4b8e-a5c2-91e0d3b7f468+JavaInvokeActivityInput" version="2.0"><xsl:param name="Start"/><xsl:template name="ArchiveReports-input" match="/"><tns:JavaInvokeActivityInput><MethodParameters><target><xsl:value-of select="$Start/root/archiveName"/></target><xsl:for-each select="$Start/root/report"><sources><xsl:value-of select="."/></sources></xsl:for-each></MethodParameters></tns:JavaInvokeActivityInput></xsl:template></xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    handle var3 = (var2/**/<target>).length() == 0 ? java:createNull() : java:fromString((var2/**/<target>/*).toString());
    string[] var4 = from xml item in var2/**/<sources> select (item/*).toString();
    handle var5 = arrays:newInstance(check java:getClass("java.lang.String"), var4.length());
    foreach int index in 0 ..< var4.length() {
    arrays:set(var5, index, java:fromString(var4[index]));
}

    check trap Archiver_archive(var3, var5);
    xml var6 = xml`<root></root>`;
    addToContext(cx, "ArchiveReports", var6);
}
