function Rest call(Context cx) returns error? {
    xml var0 = xml`<root></root>`;
    xml var1 = check xslt:transform(var0, xml`<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0"><xsl:param name="post"/>     <xsl:template name="Transform0" match="/">
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
</xsl:stylesheet>`, cx.variables);
    xml var2 = var1/**/<Body>;
    map<json> var3 = <map<json>>xmlToJson(var2);
    http:Client var4 = check new ("http://localhost:8080/weather");
    json var5 = check var4->post("/", var3["Body"]);
    xml var6 = check toXML(<map<json>>var5);
    xml var7 = xml`<ns:RESTOutput><msg>${var6}</msg></ns:RESTOutput>`;
    xml var8 = xml`<root>${var7}</root>`;
    addToContext(cx, "Rest-call", var8);
}
