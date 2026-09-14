function activityExtension(Context cx) returns error? {
    xml var0 = getFromContext(cx, "InputVariable");
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns3="http://www.tibco.com/namespaces/tnt/plugins/file" xmlns:tns1="http://www.example.org/LogSchema" xmlns:bw="http://www.tibco.com/bw/xpath/bw-custom-functions" version="2.0"><xsl:param name="Start"/><xsl:template name="WriteFile-input" match="/"><tns3:WriteActivityInputTextClass><fileName><xsl:value-of select="concat(concat(${fileDir}, $Start/root/fileName), '.txt')"/></fileName><textContent><xsl:value-of select="$Start/root/tns1:message"/></textContent></tns3:WriteActivityInputTextClass></xsl:template></xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    string fileName = (var2/**/<fileName>/*).toString();
    string content = (var2/**/<textContent>/*).toString();
    check io:fileWriteString(fileName, content, "OVERWRITE");
    addToContext(cx, "OutputVariable", var2);
}
