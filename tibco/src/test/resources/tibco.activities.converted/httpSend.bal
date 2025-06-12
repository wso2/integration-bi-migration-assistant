function activityExtension(Context context) returns xml | error {
    xml var0 = context.get("InputVariable");
    xml var1 = check xslt:transform(var0, xml`<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns4="http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput" xmlns:tns6="/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema" version="2.0"><xsl:param name="Payload"/><xsl:param name="Start"/><xsl:template name="SendHTTPRequest-input" match="/"><tns4:RequestActivityInput><Method><xsl:value-of select="'POST'"/></Method><RequestURI><xsl:value-of select="'/service'"/></RequestURI><PostData><xsl:value-of select="$Payload/root/jsonString"/></PostData><Headers><Accept><xsl:value-of select="'application/json'"/></Accept><Content-Type><xsl:value-of select="'application/json'"/></Content-Type></Headers><parameters></parameters></tns4:RequestActivityInput></xsl:template></xsl:stylesheet>`, context);
    string var2 = (var1/**/<Method>[0]).data();
    string var3 = (var1/**/<RequestURI>[0]).data();
    json var4 = ();
    match var2 {
    "GET" => {
        var4 = check httpClientResource->get(var3);
    }
    "POST" => {
        json postData = (var1/**/<PostData>[0]).data();
        var4 = check httpClientResource->post(var3, postData);
    }
    _ => {
        panic error("Unsupported method: " + var2);
    }
}

    xml var5 = xml`<root><asciiContent>${var4.toJsonString()}</asciiContent></root>`;
    addToContext(context, "OutputVariable", var5);
    return var5;
}
