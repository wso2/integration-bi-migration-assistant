function activityExtension(Context cx) returns error? {
    xml var0 = getFromContext(cx, "InputVariable");
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns4="http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput" xmlns:tns6="/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema" version="2.0"><xsl:param name="Payload"/><xsl:param name="Start"/><xsl:template name="SendHTTPRequest-input" match="/"><tns4:RequestActivityInput><Method><xsl:value-of select="'POST'"/></Method><RequestURI><xsl:value-of select="'/service'"/></RequestURI><PostData><xsl:value-of select="$Payload/root/jsonString"/></PostData><Headers><Accept><xsl:value-of select="'application/json'"/></Accept><Content-Type><xsl:value-of select="'application/json'"/></Content-Type></Headers><parameters></parameters></tns4:RequestActivityInput></xsl:template></xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    string var3 = (var2/**/<Method>[0]).data();
    string var4 = (var2/**/<RequestURI>[0]).data();
    json var5 = ();
    match var3 {
    "GET" => {
        var5 = check httpClientResource->get(var4);
    }
    "POST" => {
        json postData = (var2/**/<PostData>[0]).data();
        var5 = check httpClientResource->post(var4, postData);
    }
    _ => {
        panic error("Unsupported method: " + var3);
    }
}

    xml var6 = xml`<root><asciiContent>${var5.toJsonString()}</asciiContent></root>`;
    addToContext(cx, "OutputVariable", var6);
}
