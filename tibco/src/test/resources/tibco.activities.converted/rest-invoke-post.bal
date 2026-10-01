function activityExtension(Context cx) returns error? {
    xml var0 = getFromContext(cx, "NotifySubscribers-input");
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns1="http://www.tibco.com/namespaces/tnt/plugins/restinvoke+3a7c2e91-0b4d-4f8a-9e65-1d2c8b7a4f30+RestInvokeInput" version="2.0"><xsl:param name="Payload"/><xsl:template name="NotifySubscribers-input" match="/"><tns1:RestActivityInput><MessageBody><asciiContent><xsl:value-of select="$Payload/root/jsonString"/></asciiContent></MessageBody></tns1:RestActivityInput></xsl:template></xsl:stylesheet>`);
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
    http:Response var6 = check NotificationClient->execute("POST", var4 == "" ? "/api/notifications" : var4, var5, var3, "application/json");
    string|error var7 = var6.getTextPayload();
    string var8 = var7 is string ? var7 : "";
    if var6.statusCode >= 400 {
    string fault = var6.statusCode >= 500 ? "HttpServerException" : "HttpClientException";
    return error(string `${fault}: HTTP ${var6.statusCode} ${var6.reasonPhrase}: ${var8}`);
}

    if var7 is error && var7 !is http:NoContentError {
    return var7;
}

    xml var9 = xml`<root><statusCode>${var6.statusCode}</statusCode><reasonPhrase>${var6.reasonPhrase}</reasonPhrase><MessageBody><asciiContent>${var8}</asciiContent></MessageBody></root>`;
    addToContext(cx, "NotifySubscribers", var9);
}
