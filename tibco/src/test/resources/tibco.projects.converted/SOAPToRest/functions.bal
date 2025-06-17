import ballerina/data.xmldata;
import ballerina/http;
import ballerina/log;
import ballerina/xslt;

function HTTP_Receiver(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = xml `<root>${var0}</root>`;
    addToContext(cx, "HTTP-Receiver", var1);
}

function Log1(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            xmlns:tns="http://xmlns.example.com" version="2.0"><xsl:param name="post"/>     <xsl:template name="Transform0" match="/">
        <ns:ActivityInput xmlns:ns="http://www.tibco.com/pe/EngineTypes">
                    
    <message>
         Request received for lat:
        <xsl:value-of select="$post//Latitude" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
        , long:
        <xsl:value-of select="$post//Longitude" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
    </message>
                
</ns:ActivityInput>

    </xsl:template>
</xsl:stylesheet>`, cx.variables);
    xml var2 = var1/**/<message>/*;
    log:printInfo(var2.toString());
    addToContext(cx, "Log1", var2);
}

function Rest_call(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            xmlns:tns="http://xmlns.example.com" version="2.0"><xsl:param name="post"/>     <xsl:template name="Transform1" match="/">
        <ns1:ActivityInput xmlns:ns1="http://www.tibco.com/namespaces/tnt/plugins/json">
                    
    <ns1:Parameters>
                            
        <Body>
                                    
            <latitude>
                                            
                <xsl:value-of select="$post//Latitude" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                                        
            </latitude>
                                    
            <longitude>
                                            
                <xsl:value-of select="$post//Longitude" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                                        
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
    xml var7 = xml `<ns:RESTOutput><msg>${var6}</msg></ns:RESTOutput>`;
    addToContext(cx, "Rest-call", var7);
}

function SOAP_Response(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            xmlns:tns="http://xmlns.example.com" version="2.0"><xsl:param name="Rest-call"/>     <xsl:template name="Transform2" match="/">
        <ResponseActivityInput>
                    
    <asciiContent>
                            
        <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" soap:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/">
                                    
            <soap:Header/>
                                    
            <soap:Body>
                                            
                <response>
                                                    
                    <temperature>
                                                            
                        <xsl:value-of select="$Rest-call//temperature" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                                                        
                    </temperature>
                                                    
                    <windSpeed>
                                                            
                        <xsl:value-of select="$Rest-call//windSpeed" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                                                        
                    </windSpeed>
                                                    
                    <humidity>
                                                            
                        <xsl:value-of select="$Rest-call//humidity" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                                                        
                    </humidity>
                                                
                </response>
                                        
            </soap:Body>
                                
        </soap:Envelope>
                        
    </asciiContent>
                
</ResponseActivityInput>

    </xsl:template>
</xsl:stylesheet>`, cx.variables);
    xml var2 = var1/**/<asciiContent>/*;
    addToContext(cx, "SOAP-Response", var2);
}

function scope0ActivityRunner(Context cx) returns error? {
    check HTTP_Receiver(cx);
    check Log1(cx);
    check Rest_call(cx);
    check SOAP_Response(cx);
}

function scope0FaultHandler(error err, Context cx) returns () {
    panic err;
}

function scope0ScopeFn(Context cx) returns () {
    error? result = scope0ActivityRunner(cx);
    if result is error {
        scope0FaultHandler(result, cx);
    }
}

function start_Processes_Main_process(Context cx) returns () {
    return scope0ScopeFn(cx);
}

function toXML(map<anydata> data) returns error|xml {
    return xmldata:toXml(data);
}

function addToContext(Context context, string varName, xml value) {
    xml children = value/*;
    xml transformed = xml `<root>${children}</root>`;
    context.variables[varName] = transformed;
    context.result = value;
}

function initContext(map<xml> initVariables = {}) returns Context {
    return {variables: initVariables, result: xml `<root/>`};
}

function xmlToJson(xml value) returns json {
    json result = toJsonInner(value);
    if (result is map<json> && result.hasKey("InputElement")) {
        return result.get("InputElement");
    } else {
        return result;
    }
}

function toJsonInner(xml value) returns json {
json result;
if (value is xml:Element) {
result = toJsonElement(value);
} else {
result = value.toJson();
}
return result;
}

function toJsonElement(xml:Element element) returns json {
XMLElementParseResult parseResult = parseElement(element);
string name = parseResult.name;

xml children = element/*;
map<json> body = {};
map<json> result = {};
foreach xml child in children {
json r = toJsonInner(child);
if child !is xml:Element {
result[name] = r;
return result;
}
string childName = parseElement(child).name;
if r !is map<json> {
panic error("unexpected");
} else {
r = r.get(childName);
}
if body.hasKey(childName) {
json current = body.get(childName);
if current !is json[] {
json[] n = [body.get(childName)];
n.push(r);
body[childName] = n;
} else {
current.push(r);
}
} else {
body[childName] = r;
}
}
result[name] = body;
return result;
}

function parseElement(xml:Element element) returns XMLElementParseResult {
    string name = element.getName();
    if (name.startsWith("{")) {
        int? index = name.indexOf("}");
        if (index == ()) {
            panic error("Invalid element name: " + name);
        }
        string namespace = name.substring(1, index);
        name = name.substring(index + 1);
        return {namespace: namespace, name: name};
    }
    return {namespace: (), name: name};
}
