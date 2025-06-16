function activityExtension(Context cx) returns error? {
    xml var0 = getFromContext(cx, "InputVariable");
    xml var1 = check xslt:transform(var0, xml`<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns1="http://tns.tibco.com/bw/activity/sendhttpresponse/xsd/input+3847aa9b-8275-4b15-9ea8-812816768fa4+ResponseActivityInput" version="2.0"><xsl:param name="RenderJSON"/><xsl:template name="SendHTTPResponse-input" match="/"><tns1:ResponseActivityInput><asciiContent><xsl:value-of select="$RenderJSON/root/jsonString"/></asciiContent><Headers><Content-Type><xsl:value-of select="'application/json'"/></Content-Type></Headers></tns1:ResponseActivityInput></xsl:template></xsl:stylesheet>`, cx.variables);
    
//FIXME ignoring headers others than content type

    xmlns "http://tns.tibco.com/bw/activity/sendhttpresponse/xsd/input+3847aa9b-8275-4b15-9ea8-812816768fa4+ResponseActivityInput" as ns;
    string var2 = (var1/**/<ns:Content\-Type>/*).toString();
    string var3 = (var1/**/<ns:asciiContent>/*).toString();
    match var2 {
    "application/json" => {
        map<json> jsonRepr = check jsondata:parseString(var3);
        cx.result = jsonRepr;
    }
    "application/xml" => {
        xml xmlRepr = xml `${var3}`;
        cx.result = xmlRepr;
    }
    _ => {
        panic error("Unsupported content type: " + var2);
    }
}

}
