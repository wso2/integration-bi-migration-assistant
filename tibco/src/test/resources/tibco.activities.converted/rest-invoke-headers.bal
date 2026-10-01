function activityExtension(Context cx) returns error? {
    xml var0 = getFromContext(cx, "PublishEvent-input");
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns1="http://www.tibco.com/namespaces/tnt/plugins/restinvoke+9b4e1f27-6c83-4d0a-b5f2-3e7a6c1d8b49+RestInvokeInput" version="2.0"><xsl:param name="Event"/><xsl:param name="GatewayKey"/><xsl:template name="PublishEvent-input" match="/"><tns1:RestActivityInput><MessageBody><asciiContent><xsl:value-of select="$Event/root/jsonString"/></asciiContent></MessageBody><HttpHeaders><DynamicHeaders><Header><Name><xsl:value-of select="'x-api-key'"/></Name><Value><xsl:value-of select="$GatewayKey"/></Value></Header></DynamicHeaders></HttpHeaders></tns1:RestActivityInput></xsl:template></xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    
// WARNING: REST Invoke path and query parameters are not mapped.

    map<string> var3 = {"Accept": "application/json"};
    foreach xml header in var2/**/<HttpHeaders>/* {
    if header is xml:Element && header.getName() != "DynamicHeaders" && header.data() != "" {
        var3[header.getName()] = header.data();
    }
}

    foreach xml header in var2/**/<DynamicHeaders>/<Header> {
    var3[(header/<Name>).data()] = (header/<Value>).data();
}

    string var4 = (var2/**/<ResourcePath>).data();
    string var5 = (var2/**/<MessageBody>/<asciiContent>).data();
    http:Response var6 = check EventGatewayClient->execute("POST", var4 == "" ? "" : var4, var5, var3, "application/json");
    string|error var7 = var6.getTextPayload();
    string var8 = var7 is string ? var7 : "";
    if var6.statusCode >= 400 {
    string fault = var6.statusCode >= 500 ? "HttpServerException" : "HttpClientException";
    return error(string `${fault}: HTTP ${var6.statusCode} ${var6.reasonPhrase}: ${var8}`);
}

    xml var9 = xml`<root><statusCode>${var6.statusCode}</statusCode><reasonPhrase>${var6.reasonPhrase}</reasonPhrase><MessageBody><asciiContent>${var8}</asciiContent></MessageBody></root>`;
    addToContext(cx, "PublishEvent", var9);
}
