function activityExtension(Context cx) returns error? {
    xml var0 = getFromContext(cx, "ProbeCapabilities-input");
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns1="http://www.tibco.com/namespaces/tnt/plugins/restinvoke+8e2f4a61-9c37-4b0d-a5e8-3d1c7b6f2a90+RestInvokeInput" version="2.0"><xsl:template name="ProbeCapabilities-input" match="/"><tns1:RestActivityInput/></xsl:template></xsl:stylesheet>`);
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
    http:Response var5 = check CatalogClient->execute("OPTIONS", var4 == "" ? "/catalog" : var4, (), var3, ());
    string|error var6 = var5.getTextPayload();
    string var7 = var6 is string ? var6 : "";
    if var5.statusCode >= 400 {
    string fault = var5.statusCode >= 500 ? "HttpServerException" : "HttpClientException";
    return error(string `${fault}: HTTP ${var5.statusCode} ${var5.reasonPhrase}: ${var7}`);
}

    xml var8 = xml`<root><statusCode>${var5.statusCode}</statusCode><reasonPhrase>${var5.reasonPhrase}</reasonPhrase><MessageBody><asciiContent>${var7}</asciiContent></MessageBody></root>`;
    addToContext(cx, "ProbeCapabilities", var8);
}
