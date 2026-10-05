import ballerina/xslt;

function activityExtension(Context cx) returns error? {
    xml var0 = getFromContext(cx, "ArchiveOrderFile-input");
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns1="http://tns.tibco.com/bw/activity/renameFile" version="2.0"><xsl:template name="ArchiveOrderFile-input" match="/"><tns1:Input><OldRemoteFileName><xsl:value-of select="concat('/inbound/', 'orders.csv')"/></OldRemoteFileName><NewRemoteFileName><xsl:value-of select="concat('/archive/', 'orders.csv')"/></NewRemoteFileName></tns1:Input></xsl:template></xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    string var3 = (var2/**/<OldRemoteFileName>/*).toString();
    string var4 = (var2/**/<NewRemoteFileName>/*).toString();
    check Orders_SftpConnection->rename(var3, var4);
}

function activityExtension_1(Context cx) returns error? {
    xml var0 = getFromContext(cx, "ArchiveReport-input");
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns1="http://tns.tibco.com/bw/activity/renameFile" version="2.0"><xsl:template name="ArchiveReport-input" match="/"><tns1:Input><OldRemoteFileName><xsl:value-of select="'/reports/daily.pdf'"/></OldRemoteFileName><NewRemoteFileName><xsl:value-of select="'/reports/archive/daily.pdf'"/></NewRemoteFileName></tns1:Input></xsl:template></xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    string var3 = (var2/**/<OldRemoteFileName>/*).toString();
    string var4 = (var2/**/<NewRemoteFileName>/*).toString();
    check Reports_SftpConnection->rename(var3, var4);
}

function receiveEvent(Context cx) returns error? {
}

function scopeActivityRunner(Context cx) returns error? {
    check receiveEvent(cx);
    check activityExtension(cx);
    check activityExtension_1(cx);
}

function scopeFaultHandler(error err, Context cx) returns () {
    panic err;
}

function scopeScopeFn(Context cx) returns () {
    error? result = scopeActivityRunner(cx);
    if result is error {
        scopeFaultHandler(result, cx);
    }
}

function start_test_sftp_ArchiveProcess(Context params) returns () {
    scopeScopeFn(params);
}

function getFromContext(Context context, string varName) returns xml {
    xml? value = context.variables[varName];
    if value == () {
        return xml `<root/>`;
    }
    return value;
}
