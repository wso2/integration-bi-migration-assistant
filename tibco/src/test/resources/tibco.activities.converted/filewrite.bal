function activityExtension(map<xml> context) returns xml | error {
    xml var0 = context.get("InputVariable");
    xml var1 = check xslt:transform(var0, xml`<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns3="http://www.tibco.com/namespaces/tnt/plugins/file" xmlns:tns1="http://www.example.org/LogSchema" xmlns:bw="http://www.tibco.com/bw/xpath/bw-custom-functions" version="2.0"><xsl:param name="Start"/><xsl:template name="WriteFile-input" match="/"><tns3:WriteActivityInputTextClass><fileName><xsl:value-of select="concat(concat(${fileDir}, $Start/root/fileName), '.txt')"/></fileName><textContent><xsl:value-of select="$Start/root/tns1:message"/></textContent></tns3:WriteActivityInputTextClass></xsl:template></xsl:stylesheet>`, context);
    string fileName = (var1/**/<fileName>/*).toString();
    string content = (var1/**/<textContent>/*).toString();
    check io:fileWriteString(fileName, content, "OVERWRITE");
    addToContext(context, "OutputVariable", var1);
    return var1;
}
