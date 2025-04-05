import ballerina/io;
import ballerina/xslt;

function activityExtension(xml input, map<xml> context) returns xml|error {
    xml var0 = check xslt:transform(input, transformXSLT(xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns2="http://www.example.org/LogResult" version="2.0"><xsl:template name="End-input" match="/"><tns2:result><xsl:value-of select="'Logging Done'"/></tns2:result></xsl:template></xsl:stylesheet>`), context);
    return var0;
}

function activityExtension_2(xml input, map<xml> context) returns xml|error {
    xml var0 = check xslt:transform(input, transformXSLT(xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://www.tibco.com/pe/WriteToLogActivitySchema" xmlns:tns1="http://www.example.org/LogSchema" version="2.0"><xsl:param name="Start"/><xsl:template name="consolelog-input" match="/"><tns:ActivityInput><msgCode><xsl:value-of select="$Start/tns1:msgCode"/></msgCode><logLevel><xsl:value-of select="$Start/tns1:level"/></logLevel><message><xsl:value-of select="$Start/tns1:message"/></message></tns:ActivityInput></xsl:template></xsl:stylesheet>`), context);
    LogParametersType var1 = convertToLogParametersType(var0);
    logWrapper(var1);
    return var0;
}

function activityExtension_3(xml input, map<xml> context) returns xml|error {
    xml var0 = check xslt:transform(input, transformXSLT(xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns3="http://www.tibco.com/namespaces/tnt/plugins/file" xmlns:tns1="http://www.example.org/LogSchema" xmlns:bw="http://www.tibco.com/bw/xpath/bw-custom-functions" version="2.0"><xsl:param name="Start"/><xsl:template name="WriteFile-input" match="/"><tns3:WriteActivityInputTextClass><fileName><xsl:value-of select="concat(concat(bw:getModuleProperty('fileDir'), $Start/tns1:loggerName), '.txt')"/></fileName><textContent><xsl:value-of select="$Start/tns1:message"/></textContent></tns3:WriteActivityInputTextClass></xsl:template></xsl:stylesheet>`), context);
    WriteActivityInputTextClass var1 = convertToWriteActivityInputTextClass(var0);
    string var2 = <string>var1.fileName;
    string var3 = var1.textContent;
    checkpanic io:fileWriteString(var2, var3);
    return var0;
}

function activityExtension_4(xml input, map<xml> context) returns xml|error {
    xml var0 = check xslt:transform(input, transformXSLT(xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns1="http://www.example.org/LogSchema" xmlns:tns="http://www.tibco.com/xml/render/example" version="2.0"><xsl:param name="Start"/><xsl:template name="RenderXml-input" match="/"><tns:InputElement><level><xsl:value-of select="$Start/tns1:level"/></level><message><xsl:value-of select="$Start/tns1:message"/></message><logger><xsl:value-of select="$Start/tns1:loggerName"/></logger><timestamp><xsl:value-of select="current-dateTime()"/></timestamp></tns:InputElement></xsl:template></xsl:stylesheet>`), context);
    return var0;
}

function activityExtension_5(xml input, map<xml> context) returns xml|error {
    xml var0 = check xslt:transform(input, transformXSLT(xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns3="http://www.tibco.com/namespaces/tnt/plugins/file" xmlns:tns1="http://www.example.org/LogSchema" xmlns:bw="http://www.tibco.com/bw/xpath/bw-custom-functions" version="2.0"><xsl:param name="RenderXml"/><xsl:param name="Start"/><xsl:template name="XMLFile-input" match="/"><tns3:WriteActivityInputTextClass><fileName><xsl:value-of select="concat(concat(bw:getModuleProperty('fileDir'), $Start/tns1:loggerName), '.xml')"/></fileName><textContent><xsl:value-of select="$RenderXml"/></textContent></tns3:WriteActivityInputTextClass></xsl:template></xsl:stylesheet>`), context);
    WriteActivityInputTextClass var1 = convertToWriteActivityInputTextClass(var0);
    string var2 = <string>var1.fileName;
    string var3 = var1.textContent;
    checkpanic io:fileWriteString(var2, var3);
    return var0;
}

function activityRunner_loggingservice_LogProcess(xml input, map<xml> cx) returns xml|error {
    xml result0 = check receiveEvent(input, cx);
    xml result1;
    if predicate_2(result0) {
        result1 = check activityExtension_4(result0, cx);
    } else {
        result1 = result0;
    }
    xml result2 = check activityExtension_5(result1, cx);
    xml result3;
    if predicate_1(result0) {
        result3 = check activityExtension_3(result2, cx);
    } else {
        result3 = result2;
    }
    xml result4;
    if predicate_0(result0) {
        result4 = check activityExtension_2(result3, cx);
    } else {
        result4 = result3;
    }
    xml result5 = check activityExtension(result4, cx);
    return result5;
}

function errorHandler_loggingservice_LogProcess(error err, map<xml> cx) returns xml {
    checkpanic err;
}

function loggingservice_LogProcess_start(LogMessage input) returns result {
    xml inputXML = checkpanic toXML(input);
    xml xmlResult = process_loggingservice_LogProcess(inputXML);
    result result = convertToresult(xmlResult);
    return result;
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

function process_loggingservice_LogProcess(xml input) returns xml {
    map<xml> context = {};
    addToContext(context, "post.item", input);
    xml|error result = activityRunner_loggingservice_LogProcess(input, context);
    if result is error {
        return errorHandler_loggingservice_LogProcess(result, context);
    }
    return result;
}

function receiveEvent(xml input, map<xml> context) returns xml|error {
    return input;
}
