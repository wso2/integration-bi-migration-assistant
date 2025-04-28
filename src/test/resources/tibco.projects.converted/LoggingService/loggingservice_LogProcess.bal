import ballerina/io;
import ballerina/xslt;

function activityExtension(map<xml> context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns2="http://www.example.org/LogResult" version="2.0"><xsl:template name="End-input" match="/"><tns2:result><xsl:value-of select="'Logging Done'"/></tns2:result></xsl:template></xsl:stylesheet>`, context);
    return var1;
}

function activityExtension_2(map<xml> context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://www.tibco.com/pe/WriteToLogActivitySchema" xmlns:tns1="http://www.example.org/LogSchema" version="2.0"><xsl:param name="Start"/><xsl:template name="consolelog-input" match="/"><tns:ActivityInput><msgCode><xsl:value-of select="$Start/root/tns1:msgCode"/></msgCode><logLevel><xsl:value-of select="$Start/root/tns1:level"/></logLevel><message><xsl:value-of select="$Start/root/tns1:message"/></message></tns:ActivityInput></xsl:template></xsl:stylesheet>`, context);
    LogParametersType var2 = convertToLogParametersType(var1);
    logWrapper(var2);
    return var1;
}

function activityExtension_3(map<xml> context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns3="http://www.tibco.com/namespaces/tnt/plugins/file" xmlns:tns1="http://www.example.org/LogSchema" xmlns:bw="http://www.tibco.com/bw/xpath/bw-custom-functions" version="2.0"><xsl:param name="Start"/><xsl:template name="WriteFile-input" match="/"><tns3:WriteActivityInputTextClass><fileName><xsl:value-of select="concat(concat(${fileDir}, $Start/root/tns1:loggerName), '.txt')"/></fileName><textContent><xsl:value-of select="$Start/root/tns1:message"/></textContent></tns3:WriteActivityInputTextClass></xsl:template></xsl:stylesheet>`, context);
    WriteActivityInputTextClass var2 = convertToWriteActivityInputTextClass(var1);
    string var3 = <string>var2.fileName;
    string var4 = var2.textContent;
    checkpanic io:fileWriteString(var3, var4);
    addToContext(context, "TextFile", var1);
    return var1;
}

function activityExtension_4(map<xml> context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns1="http://www.example.org/LogSchema" xmlns:tns="http://www.tibco.com/xml/render/example" version="2.0"><xsl:param name="Start"/><xsl:template name="RenderXml-input" match="/"><tns:InputElement><level><xsl:value-of select="$Start/root/tns1:level"/></level><message><xsl:value-of select="$Start/root/tns1:message"/></message><logger><xsl:value-of select="$Start/root/tns1:loggerName"/></logger><timestamp><xsl:value-of select="current-dateTime()"/></timestamp></tns:InputElement></xsl:template></xsl:stylesheet>`, context);
    addToContext(context, "RenderXml", var1);
    return var1;
}

function activityExtension_5(map<xml> context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns3="http://www.tibco.com/namespaces/tnt/plugins/file" xmlns:tns1="http://www.example.org/LogSchema" xmlns:bw="http://www.tibco.com/bw/xpath/bw-custom-functions" version="2.0"><xsl:param name="RenderXml"/><xsl:param name="Start"/><xsl:template name="XMLFile-input" match="/"><tns3:WriteActivityInputTextClass><fileName><xsl:value-of select="concat(concat(${fileDir}, $Start/root/tns1:loggerName), '.xml')"/></fileName><textContent><xsl:value-of select="$RenderXml"/></textContent></tns3:WriteActivityInputTextClass></xsl:template></xsl:stylesheet>`, context);
    WriteActivityInputTextClass var2 = convertToWriteActivityInputTextClass(var1);
    string var3 = <string>var2.fileName;
    string var4 = var2.textContent;
    checkpanic io:fileWriteString(var3, var4);
    addToContext(context, "XMLFile", var1);
    return var1;
}

function predicate_0(xml input) returns boolean {
    return test(input, "matches($Start/ns0:handler, \"console\")");
}

function predicate_1(xml input) returns boolean {
    return test(input, "matches($Start/ns0:handler, \"file\") and matches($Start/ns0:formatter, \"text\")");
}

function predicate_2(xml input) returns boolean {
    return test(input, "matches($Start/ns0:handler, \"file\") and matches($Start/ns0:formatter, \"xml\")");
}

function receiveEvent(map<xml> context) returns xml|error {
    addToContext(context, "Start", context.get("$input"));
    return context.get("$input");
}

function scope_4ActivityRunner(map<xml> cx) returns xml|error {
    xml result0 = check receiveEvent(cx);
    xml result1;
    if predicate_2(result0) {
        result1 = check activityExtension_4(cx);
    } else {
        result1 = result0;
    }
    xml result2 = check activityExtension_5(cx);
    xml result3;
    if predicate_1(result0) {
        result3 = check activityExtension_3(cx);
    } else {
        result3 = result2;
    }
    xml result4;
    if predicate_0(result0) {
        result4 = check activityExtension_2(cx);
    } else {
        result4 = result3;
    }
    xml result5 = check activityExtension(cx);
    return result5;
}

function scope_4FaultHandler(error err, map<xml> cx) returns xml {
    panic err;
}

function scope_4ScopeFn(xml input, map<xml> params) returns xml {
    map<xml> context = {...params};
    addToContext(context, "$input", input);
    xml|error result = scope_4ActivityRunner(context);
    if result is error {
        return scope_4FaultHandler(result, context);
    }
    return result;
}

function start_loggingservice_LogProcess(LogMessage input, map<xml> params = {}) returns result {
    xml inputXML = input is map<anydata> ? checkpanic toXML(input) : xml ``;
    xml xmlResult = scope_4ScopeFn(inputXML, params);
    result result = convertToresult(xmlResult);
    return result;
}
