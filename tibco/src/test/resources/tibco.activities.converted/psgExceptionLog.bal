function activityExtension(Context cx) returns error? {
    xml var0 = getFromContext(cx, "ExceptionLog-Input-Var");
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://www.tibco.com/PSGLogActivities" version="2.0"><xsl:param name="_error"/><xsl:template name="ExceptionLog-input" match="/"><tns:ExceptionLogInput><tns:Input><tns:errorCode><xsl:value-of select="$_error/root/MsgCode"/></tns:errorCode><tns:errorMessage><xsl:value-of select="$_error/root/Msg"/></tns:errorMessage><tns:processStack><xsl:value-of select="$_error/root/ProcessStack"/></tns:processStack><tns:stackTrace><xsl:value-of select="$_error/root/StackTrace"/></tns:stackTrace></tns:Input></tns:ExceptionLogInput></xsl:template></xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xmlns "http://www.tibco.com/PSGLogActivities" as psglog;
    string var3 = (var2/**/<psglog:errorCode>/*).toString().trim();
    string var4 = (var2/**/<psglog:errorMessage>/*).toString().trim();
    string var5 = (var2/**/<psglog:processStack>/*).toString().trim();
    string var6 = (var2/**/<psglog:stackTrace>/*).toString().trim();
    psgExceptionLog(var3, var4, var5, var6);
}
