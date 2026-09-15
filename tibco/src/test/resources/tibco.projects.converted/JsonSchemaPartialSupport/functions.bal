import ballerina/data.jsondata;
import ballerina/data.xmldata;
import ballerina/log;
import ballerina/time;
import ballerina/xslt;

function HTTP_Receiver(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = xml `<root>${var0}</root>`;
    addToContext(cx, "HTTP-Receiver", var1);
}

function Log(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/EngineTypes" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="2.0"><xsl:param name="Parse-JSON"/>     <xsl:template name="Transform3" match="/">
        <ns:ActivityInput xmlns:ns="http://www.tibco.com/pe/EngineTypes">
                    
    <message>
                            
        <xsl:value-of select="$Parse-JSON/root/ns1:ActivityOutputClass/EventName" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                        
    </message>
                
</ns:ActivityInput>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xml var3 = var2/**/<message>/*;
    log:printInfo(var3.toString());
    addToContext(cx, "Log", var3);
}

function Parse_JSON(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/EngineTypes" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="2.0"><xsl:param name="HTTP-Receiver"/>     <xsl:template name="Transform0" match="/">
        <ns1:ActivityInputClass xmlns:ns1="http://www.tibco.com/namespaces/tnt/plugins/json">
                    
    <jsonString>
                            
        <xsl:value-of select="$HTTP-Receiver/root/ProcessStarterOutput/Data" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                        
    </jsonString>
                
</ns1:ActivityInputClass>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xmlns "http://www.tibco.com/namespaces/tnt/plugins/json" as ns;
    xml var3 = check renderJsonAsEventXML(var2);
    xml var4 = xml `<ActivityOutputClass>var3</ActivityOutputClass>`;
    xml var5 = xml `<root>${var4}</root>`;
    addToContext(cx, "Parse-JSON", var5);
}

function Parse_Unsupported_JSON(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/EngineTypes" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="2.0"><xsl:param name="HTTP-Receiver"/>     <xsl:template name="Transform2" match="/">
        <ns1:ActivityInputClass xmlns:ns1="http://www.tibco.com/namespaces/tnt/plugins/json">
                    
    <jsonString>
                            
        <xsl:value-of select="$HTTP-Receiver/root/ProcessStarterOutput/Data" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                        
    </jsonString>
                
</ns1:ActivityInputClass>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xmlns "http://www.tibco.com/namespaces/tnt/plugins/json" as ns;
    xml var3 = check renderJsonAsmap_json_XML(var2);
    xml var4 = xml `<ActivityOutputClass>var3</ActivityOutputClass>`;
    xml var5 = xml `<root>${var4}</root>`;
    addToContext(cx, "Parse-Unsupported-JSON", var5);
}

function Render_JSON(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/EngineTypes" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="2.0"><xsl:param name="Parse-JSON"/>     <xsl:template name="Transform1" match="/">
        <ns1:ActivityInputClass xmlns:ns1="http://www.tibco.com/namespaces/tnt/plugins/json">
                    
    <jsonString>
                            
        <xsl:value-of select="$Parse-JSON/root/ns1:ActivityOutputClass/EventName" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                        
    </jsonString>
                
</ns1:ActivityInputClass>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xml var3 = (var2/*);
    xmlns "http://www.tibco.com/namespaces/tnt/plugins/json" as ns;
    // WARNING: assuming single element
    record {|
        time:Date EventDate;
        time:Civil EventTimestamp;
        string EventName?;
        anydata LegacyPayload?; // FIXME: unsupported XSD type, defaulted to anydata
    |} var4 = check xmldata:parseAsType(var3);
    string var5 = var4.toJsonString();
    xml var6 = xml `<jsonString>${var5}</jsonString>`;
    xml var7 = xml `<ns:ActivityOutputClass>${var6}</ns:ActivityOutputClass>`;
    addToContext(cx, "Render-JSON", var7);
}

function scope0ActivityRunner(Context cx) returns error? {
    check HTTP_Receiver(cx);
    check Parse_JSON(cx);
    check Render_JSON(cx);
    check Parse_Unsupported_JSON(cx);
    check Log(cx);
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

function renderJSONAsXML(json value, string? namespace, string? typeName) returns xml|error {
    anydata body;
    if (value is map<json>) {
        xml acum = xml ``;
        foreach string key in value.keys() {
            acum += check renderJSONAsXML(value.get(key), namespace, key);
        }
        body = acum;
    } else {
        body = value;
    }

    string rep = typeName == () ? body.toString() :
        string `<${typeName}>${body.toString()}</${typeName}>`;
    xml result = check xml:fromString(rep);
    if (namespace == ()) {
        return result;
    }
    if (result !is xml:Element) {
        panic error("Expected XML element");
    }
    map<string> attributes = result.getAttributes();
    attributes["xmlns"] = namespace;
    return result;
}

function addToContext(Context context, string varName, xml value) {
    xml children = value/*;
    xml transformed = xml `<root>${children}</root>`;
    context.variables[varName] = transformed;
    context.result = value;
}

function initContext(map<xml> initVariables = {},
        map<SharedVariableContext> jobSharedVariables = {})
            returns Context {
    map<SharedVariableContext> sharedVariables = {};

    foreach var key in jobSharedVariables.keys() {
        sharedVariables[key] = jobSharedVariables.get(key);
    }
    return {variables: initVariables, result: xml `<root/>`, sharedVariables};
}

function renderJsonAsEventXML(xml value) returns xml|error {
    string jsonString = (value/<jsonString>).data();
    map<json> jsonValue = check jsondata:parseString(jsonString);
    string? namespace = (Event).@xmldata:Namespace["uri"];
    return renderJSONAsXML(jsonValue, namespace, "Event");
}

function renderJsonAsmap_json_XML(xml value) returns xml|error {
    string jsonString = (value/<jsonString>).data();
    map<json> jsonValue = check jsondata:parseString(jsonString);
    string? namespace = ();
    return renderJSONAsXML(jsonValue, namespace, ());
}
