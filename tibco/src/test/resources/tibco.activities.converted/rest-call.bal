function Rest_call(Context cx) returns error? {
    xml var0 = xml`<root></root>`;
    xml var1 = check xml:fromString(string`<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"><xsl:param name="post"/>     <xsl:template name="Transform0" match="/">
        <ns1:ActivityInput xmlns:ns1="http://www.tibco.com/namespaces/tnt/plugins/json">
                
    <ns1:Parameters>
                        
        <Body>
                                
            <latitude>
                                        
                <xsl:value-of select="$post//Latitude"/>
                                    
            </latitude>
                                
            <longitude>
                                        
                <xsl:value-of select="$post//Longitude"/>
                                    
            </longitude>
                            
        </Body>
                    
    </ns1:Parameters>
            
</ns1:ActivityInput>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xml var3 = var2/**/<Body>;
    map<json> var4 = <map<json>>xmlToJson(var3);
    http:Client var5 = check new ("http://localhost:8080/weather");
    json var6 = check var5->post("/", var4["Body"]);
    xml var7 = check toXML(<map<json>>var6);
    xml var8 = xml`<ns:RESTOutput><msg>${var7}</msg></ns:RESTOutput>`;
    xml var9 = xml`<root>${var8}</root>`;
    addToContext(cx, "Rest-call", var9);
}
