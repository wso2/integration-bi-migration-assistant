function activityExtension(Context cx) returns error? {
    xml var0 = getFromContext(cx, "ResponseMoveCsvClientiToErrorLocal-input");
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns5="http://www.tibco.com/namespaces/tnt/plugins/file" version="2.0"><xsl:param name="LocalWorkPath"/><xsl:param name="LocalErrorPath"/><xsl:param name="FileNameClientiCsv"/><xsl:template name="ResponseMoveCsvClientiToErrorLocal-input" match="/"><tns5:RenameActivityConfig><fromFileName><xsl:value-of select="concat($LocalWorkPath, $FileNameClientiCsv)"/></fromFileName><toFileName><xsl:value-of select="concat($LocalErrorPath,  $FileNameClientiCsv)"/></toFileName></tns5:RenameActivityConfig></xsl:template></xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    string fromFileName = (var2/**/<fromFileName>/*).toString();
    string toFileName = (var2/**/<toFileName>/*).toString();
    if check file:test(toFileName, file:EXISTS) {
    check file:remove(toFileName);
}

    check file:rename(fromFileName, toFileName);
    addToContext(cx, "ResponseMoveCsvClientiToErrorLocal", var2);
}
