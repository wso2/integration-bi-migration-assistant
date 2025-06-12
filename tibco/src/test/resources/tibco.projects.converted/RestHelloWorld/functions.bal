import ballerina/data.jsondata;
import ballerina/data.xmldata;
import ballerina/http;
import ballerina/io;
import ballerina/log;
import ballerina/soap.soap11;
import ballerina/sql;
import ballerina/xslt;

function Catch(Context context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = xml `<root>${var0}</root>`;
    addToContext(context, "Catch", var1);
    return var1;
}

function ErrorLog(Context context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0">
     <xsl:template name="Transform9" match="/">
        <ActivityInput>
                    
    <message>
                            
        <xsl:value-of select="Error" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                        
    </message>
                
</ActivityInput>

    </xsl:template>
</xsl:stylesheet>`, context);
    xml var2 = var1/**/<message>/*;
    log:printInfo(var2.toString());
    addToContext(context, "ErrorLog", var2);
    return var2;
}

function HTTP_Receiver(Context context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = xml `<root>${var0}</root>`;
    addToContext(context, "HTTP-Receiver", var1);
    return var1;
}

function InvokeProcess(Context context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0"><xsl:param name="HTTP-Receiver"/>     <xsl:template name="Transform1" match="/">
        <InvokeProcessInput>
                    
    <options>
                            
        <xsl:value-of select="$HTTP-Receiver/root/ProcessStarterOutput/QueryString" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                        
    </options>
                
</InvokeProcessInput>

    </xsl:template>
</xsl:stylesheet>`, context);
    xml var2 = var1/*;
    xml var3 = check proj_annon_var1->post("", var2);
    addToContext(context, "InvokeProcess", var3);
    return var3;
}

function Log(Context context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0"><xsl:param name="Mapper"/>     <xsl:template name="Transform4" match="/">
        <ns:ActivityInput xmlns:ns="http://www.tibco.com/pe/EngineTypes">
                    
    <message>
                            
        <xsl:value-of select="$Mapper" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                        
    </message>
                
</ns:ActivityInput>

    </xsl:template>
</xsl:stylesheet>`, context);
    xml var2 = var1/**/<message>/*;
    log:printInfo(var2.toString());
    addToContext(context, "Log", var2);
    return var2;
}

function Mapper(Context context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0"><xsl:param name="runAllTests"/>     <xsl:template name="Transform0" match="/">
        <failedTestsCount>
                    
    <xsl:value-of select="count($runAllTests/root/ns:test-suites-results-msg/test-suites-results/ns3:test-suites-results//ns3:test-failure)" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                
</failedTestsCount>

    </xsl:template>
</xsl:stylesheet>`, context);
    xml var2 = xml `<root>${var1}</root>`;
    addToContext(context, "Mapper", var2);
    return var2;
}

function Parse(Context context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0"><xsl:param name="Render"/>     <xsl:template name="Transform6" match="/">
        <xmlString>
                    
    <xsl:value-of select="$Render/root/xmlString" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                
</xmlString>

    </xsl:template>
</xsl:stylesheet>`, context);
    xml var2 = var1/<xmlString>/*;
    string var3 = var2.toString();
    xml var4 = check xml:fromString(var3);
    xml var5 = xml `<root>${var4}</root>`;
    addToContext(context, "Parse", var5);
    return var5;
}

function Read_file(Context context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0">
     <xsl:template name="Transform3" match="/">
        <ReadActivityInputClass>
                    
    <fileName>
                            
        <xsl:value-of select="&quot;input.txt&quot;" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                        
    </fileName>
                
</ReadActivityInputClass>

    </xsl:template>
</xsl:stylesheet>`, context);
    string fileName = (var1/**/<fileName>/*).toString();
    string content = check io:fileReadString(fileName);
    xml var2 = xml `<ns:ReadActivityOutputTextClass xmlns:ns="http://www.tibco.com/namespaces/tnt/plugins/file">
    <fileContent>
        <textContent>${content}</textContent>
    </fileContent>
</ns:ReadActivityOutputTextClass>`;
    addToContext(context, "Read-file", var2);
    return var2;
}

function Render(Context context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0"><xsl:param name="Mapper"/>     <xsl:template name="Transform5" match="/">
        <xsl:copy-of select="$Mapper" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>

    </xsl:template>
</xsl:stylesheet>`, context);
    string var2 = var1.toBalString();
    xml var3 = xml `<root><xmlString>${var2}</xmlString></root>`;
    addToContext(context, "Render", var3);
    return var3;
}

function SOAPRequestReply(Context context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0">
     <xsl:template name="Transform7" match="/">
        <inputMessage>
                    
    <message>
                    "foo bar"
                </message>
                
</inputMessage>

    </xsl:template>
</xsl:stylesheet>`, context);
    soap11:Client var2 = check new ("http://localhost:8800");
    xml var3 = xml `<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
    soap:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/">
    <soap:Header/>
    <soap:Body>
        ${var1}
    </soap:Body>
</soap:Envelope>`;
    xml var4 = check var2->sendReceive(var3, "SOAPAction");
    addToContext(context, "SOAPRequestReply", var4);
    return var4;
}

function SQL_Direct(Context context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0">
     <xsl:template name="Transform8" match="/">
        <jdbcGeneralActivityInput>
                        
    <statement>
                                
        <xsl:value-of select="'SELECT * FROM foo'" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                            
    </statement>
                    
</jdbcGeneralActivityInput>

    </xsl:template>
</xsl:stylesheet>`, context);
    string var2 = (var1/**/<statement>/*).toString();
    sql:ParameterizedQuery var3 = `${var2}`;
    xml var4;
    if var2.startsWith("SELECT") {
        stream<map<anydata>, error|()> var5 = JDBCConnection->query(var3);
        xml var6 = xml ``;
        check from var each in var5
            do {
                xml var7 = check toXML(each);
                var6 = var6 + xml `<Record>${var7}</Record>`;
            };

        xml var8 = xml `<root>${var6}</root>`;
        var4 = var8;
    } else {
        sql:ExecutionResult var9 = check JDBCConnection->execute(var3);
        xml var10 = xml `<root></root>`;
        var4 = var10;
    }
    //WARNING: validate jdbc query result mapping
    addToContext(context, "SQL-Direct", var4);
    return var4;
}

function Write_File(Context context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0"><xsl:param name="Mapper"/>     <xsl:template name="Transform2" match="/">
        <ns0:WriteActivityInputTextClass xmlns:ns0="http://www.tibco.com/namespaces/tnt/plugins/file">
                    
    <fileName>
                            
        <xsl:value-of select="&quot;output.txt&quot;" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                        
    </fileName>
                    
    <textContent>
                            
        <xsl:value-of select="$Mapper" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                        
    </textContent>
                    
    <addLineSeparator>
                            
        <xsl:value-of select="true()" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                        
    </addLineSeparator>
                
</ns0:WriteActivityInputTextClass>

    </xsl:template>
</xsl:stylesheet>`, context);
    string fileName = (var1/**/<fileName>/*).toString();
    string content = (var1/**/<textContent>/*).toString();
    check io:fileWriteString(fileName, content, "APPEND");
    addToContext(context, "Write-File", var1);
    return var1;
}

function scope0ActivityRunner(map<xml> cx) returns xml|error {
    xml result0 = check HTTP_Receiver(cx);
    xml result1 = check Mapper(cx);
    xml result2 = check Log(cx);
    xml result3 = check InvokeProcess(cx);
    xml result4 = check SOAPRequestReply(cx);
    return result4;
}

function scope0FaultHandler(error err, map<xml> cx) returns xml {
    xml result0 = checkpanic Catch(cx);
    xml result1 = checkpanic ErrorLog(cx);
    return result1;
}

function scope0ScopeFn(map<xml> cx) returns xml {
    xml|error result = scope0ActivityRunner(cx);
    if result is error {
        return scope0FaultHandler(result, cx);
    }
    return result;
}

function start_Processes_MainProcessStarter_process(Context cx) returns xml {
    return scope0ScopeFn(cx);
}

function HTTP_Receiver_12(Context context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = xml `<root>${var0}</root>`;
    addToContext(context, "HTTP-Receiver", var1);
    return var1;
}

function InnerLogElement(Context context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0"><xsl:param name="element"/>     <xsl:template name="Transform3" match="/">
        <ns:ActivityInput xmlns:ns="http://www.tibco.com/pe/EngineTypes">
                        
    <message>
                                
        <xsl:value-of select="$element" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                            
    </message>
                    
</ns:ActivityInput>

    </xsl:template>
</xsl:stylesheet>`, context);
    xml var2 = var1/**/<message>/*;
    log:printInfo(var2.toString());
    addToContext(context, "InnerLogElement", var2);
    return var2;
}

function InnerLogIndex(Context context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0"><xsl:param name="index"/>     <xsl:template name="Transform4" match="/">
        <ns:ActivityInput xmlns:ns="http://www.tibco.com/pe/EngineTypes">
                        
    <message>
                                
        <xsl:value-of select="$index" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                            
    </message>
                    
</ns:ActivityInput>

    </xsl:template>
</xsl:stylesheet>`, context);
    xml var2 = var1/**/<message>/*;
    log:printInfo(var2.toString());
    addToContext(context, "InnerLogIndex", var2);
    return var2;
}

function Log_14(Context context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0"><xsl:param name="Mapper"/>     <xsl:template name="Transform1" match="/">
        <ns:ActivityInput xmlns:ns="http://www.tibco.com/pe/EngineTypes">
                    
    <message>
                            
        <xsl:value-of select="$Mapper" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                        
    </message>
                
</ns:ActivityInput>

    </xsl:template>
</xsl:stylesheet>`, context);
    xml var2 = var1/**/<message>/*;
    log:printInfo(var2.toString());
    addToContext(context, "Log", var2);
    return var2;
}

function Loop(Context context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = xml `<root></root>`;
    xml var2 = getFromContext(context, "Mapper");
    var2 = check xmldata:transform(var2, `foo/bar`);
    int var3 = -1;
    addToContext(context, "element", xml `<root></root>`);
    foreach xml each in var2 {
        var3 = var3 + 1;
        addToContext(context, "index", xml `<root>${var3}</root>`);
        addToContext(context, "element", each);
        xml var4 = scope1ScopeFn(context);
        var1 = var4;
    }
    addToContext(context, "Loop", var1);
    return var1;
}

function Mapper_13(Context context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0"><xsl:param name="runAllTests"/>     <xsl:template name="Transform0" match="/">
        <failedTestsCount>
                    
    <xsl:value-of select="count($runAllTests/root/ns:test-suites-results-msg/test-suites-results/ns3:test-suites-results//ns3:test-failure)" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                
</failedTestsCount>

    </xsl:template>
</xsl:stylesheet>`, context);
    xml var2 = xml `<root>${var1}</root>`;
    addToContext(context, "Mapper", var2);
    return var2;
}

function Parse_JSON(Context context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0"><xsl:param name="Start"/>     <xsl:template name="Transform5" match="/">
        <ns1:ActivityInputClass xmlns:ns1="http://www.tibco.com/namespaces/tnt/plugins/json">
                    
    <jsonString>
                            
        <xsl:value-of select="$Start/root/foo" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                        
    </jsonString>
                
</ns1:ActivityInputClass>

    </xsl:template>
</xsl:stylesheet>`, context);
    xmlns "http://www.tibco.com/namespaces/tnt/plugins/json" as ns;
    xml var2 = check renderJsonAsFooXML(var1);
    xml var3 = xml `<ActivityOutputClass>var2</ActivityOutputClass>`;
    addToContext(context, "Parse-JSON", var3);
    return var3;
}

function Render_JSON(Context context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0"><xsl:param name="Parse-JSON"/>     <xsl:template name="Transform6" match="/">
        <ns1:ActivityInputClass xmlns:ns1="http://www.tibco.com/namespaces/tnt/plugins/json">
                        
    <jsonString>
                                
        <xsl:value-of select="$Parse-JSON/root/ns1:ActivityOutputClass/Foo" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                            
    </jsonString>
                    
</ns1:ActivityInputClass>

    </xsl:template>
</xsl:stylesheet>`, context);
    xml var2 = (var1/*);
    xmlns "http://www.tibco.com/namespaces/tnt/plugins/json" as ns;
    //WARNING: assuming single element
    record {|
        string foo;
        string bar?;
    |} var3 = check xmldata:parseAsType(var2);
    string var4 = var3.toJsonString();
    xml var5 = xml `<jsonString>${var4}</jsonString>`;
    xml var6 = xml `<ns:ActivityOutputClass>${var5}</ns:ActivityOutputClass>`;
    addToContext(context, "Render-JSON", var6);
    return var6;
}

function Rest_call(Context context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0"><xsl:param name="Schedule-Poller"/>     <xsl:template name="Transform7" match="/">
        <ns1:ActivityInput xmlns:ns1="http://www.tibco.com/namespaces/tnt/plugins/json">
                    
    <ns1:Parameters>
                            
        <Body>
                                    
            <Multipart>
                                            
                <name>
                                                    
                    <xsl:value-of select="&quot;schedule&quot;" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                                                
                </name>
                                            
                <filename>
                                                    
                    <xsl:value-of select="$Schedule-Poller/root/ns:EventSourceOuputTextClass/fileInfo/fileName" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                                                
                </filename>
                                            
                <content>
                                                    
                    <fileName>
                                                            
                        <xsl:value-of select="concat($Schedule-Poller/root/ns:EventSourceOuputTextClass/fileInfo/location, &quot;\&quot;, $Schedule-Poller/root/ns:EventSourceOuputTextClass/fileInfo/fileName)" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                                                        
                    </fileName>
                                                
                </content>
                                        
            </Multipart>
                                
        </Body>
                        
    </ns1:Parameters>
                
</ns1:ActivityInput>

    </xsl:template>
</xsl:stylesheet>`, context);
    xml var2 = var1/**/<Body>;
    map<json> var3 = <map<json>>xmlToJson(var2);
    http:Client var4 = check new ("https://eieio.azurewebsites.net/schedulepolling/uploadschedule");
    json var5 = check var4->post("/", var3["Body"]);
    xml var6 = check toXML(<map<json>>var5);
    xml var7 = xml `<ns:RESTOutput><msg>${var6}</msg></ns:RESTOutput>`;
    addToContext(context, "Rest-call", var7);
    return var7;
}

function SOAPSendReply(Context context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0">
     <xsl:template name="Transform2" match="/">
        <Response>
                    
    <Body>
                    "Foo bar"
                </Body>
                
</Response>

    </xsl:template>
</xsl:stylesheet>`, context);
    xml var2 = xml `<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
    soap:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/">
    <soap:Header/>
    <soap:Body>
        ${var1}
    </soap:Body>
</soap:Envelope>`;
    addToContext(context, "SOAPSendReply", var2);
    return var2;
}

function scope0_1ActivityRunner(map<xml> cx) returns xml|error {
    xml result0 = check HTTP_Receiver_12(cx);
    xml result1 = check Mapper_13(cx);
    xml result2 = check Log_14(cx);
    xml result3 = check SOAPSendReply(cx);
    return result3;
}

function scope0_1FaultHandler(error err, map<xml> cx) returns xml {
    panic err;
}

function scope0_1ScopeFn(map<xml> cx) returns xml {
    xml|error result = scope0_1ActivityRunner(cx);
    if result is error {
        return scope0_1FaultHandler(result, cx);
    }
    return result;
}

function scope1ActivityRunner(map<xml> cx) returns xml|error {
    xml result0 = check InnerLogIndex(cx);
    xml result1 = check InnerLogElement(cx);
    return result1;
}

function scope1FaultHandler(error err, map<xml> cx) returns xml {
    panic err;
}

function scope1ScopeFn(map<xml> cx) returns xml {
    xml|error result = scope1ActivityRunner(cx);
    if result is error {
        return scope1FaultHandler(result, cx);
    }
    return result;
}

function start_Processes_Other_process(Context cx) returns xml {
    return scope0_1ScopeFn(cx);
}

function toXML(map<anydata> data) returns error|xml {
    return xmldata:toXml(data);
}

function addToContext(Context context, string varName, xml value) {
    xml children = value/*;
    xml transformed = xml `<root>${children}</root>`;
    context[varName] = transformed;
}

function getFromContext(Context context, string varName) returns xml {
    xml? value = context[varName];
    if value == () {
        return xml `<root/>`;
    }
    return value;
}

function initContext(map<xml> initVariables = {}) returns Context {
    return initVariables;
}

function renderJSONAsXML(json value, string? namespace, string typeName) returns xml|error {
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

    string rep = string `<${typeName}>${body.toString()}</${typeName}>`;
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

function renderJsonAsFooXML(xml value) returns xml|error {
    string jsonString = (value/<jsonString>).data();
    map<json> jsonValue = check jsondata:parseString(jsonString);
    string? namespace = (Foo).@xmldata:Namespace["uri"];
    return renderJSONAsXML(jsonValue, namespace, "Foo");
}
