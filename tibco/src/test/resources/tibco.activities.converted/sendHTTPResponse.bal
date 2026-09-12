function activityExtension(Context cx) returns error? {
    xml var0 = getFromContext(cx, "InputVariable");
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns1="http://tns.tibco.com/bw/activity/sendhttpresponse/xsd/input+3847aa9b-8275-4b15-9ea8-812816768fa4+ResponseActivityInput" version="2.0"><xsl:param name="RenderJSON"/><xsl:template name="SendHTTPResponse-input" match="/"><tns1:ResponseActivityInput><asciiContent><xsl:value-of select="$RenderJSON/root/jsonString"/></asciiContent><Headers><Content-Type><xsl:value-of select="'application/json'"/></Content-Type></Headers></tns1:ResponseActivityInput></xsl:template></xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    
// FIXME ignoring headers others than content type

    string var3 = (var2/**/<Content\-Type>/*).toString();
    string var4 = (var2/**/<asciiContent>/*).toString();
    xml var5 = (var2/**/<Headers>/*);
    map<string> var6 = parseHeaders(var5);
    match var3 {
    "application/json" => {
        map<json> jsonRepr = check jsondata:parseString(var4);
        setJSONResponse(cx, jsonRepr, var6);
    }
    "application/xml" => {
        xml xmlRepr = xml `${var4}`;
        setXMLResponse(cx, xmlRepr, var6);
    }
    _ => {
        setTextResponse(cx, var4, var6);
    }
}

}
