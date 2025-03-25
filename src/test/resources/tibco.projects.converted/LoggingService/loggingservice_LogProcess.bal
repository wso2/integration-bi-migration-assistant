import ballerina/io;
import ballerina/xslt;

function activityExtension(xml input, map<xml> context) returns xml {
    xml var0 = checkpanic xslt:transform(input, transformXSLT(xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns2="http://www.example.org/LogResult" version="2.0"><xsl:template name="End-input" match="/"><tns2:result><xsl:value-of select="'Logging Done'"/></tns2:result></xsl:template></xsl:stylesheet>`), context);
    return var0;
}

function activityExtension_2(xml input, map<xml> context) returns xml {
    xml var0 = checkpanic xslt:transform(input, transformXSLT(xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://www.tibco.com/pe/WriteToLogActivitySchema" xmlns:tns1="http://www.example.org/LogSchema" version="2.0"><xsl:param name="Start"/><xsl:template name="consolelog-input" match="/"><tns:ActivityInput><msgCode><xsl:value-of select="$Start/tns1:msgCode"/></msgCode><logLevel><xsl:value-of select="$Start/tns1:level"/></logLevel><message><xsl:value-of select="$Start/tns1:message"/></message></tns:ActivityInput></xsl:template></xsl:stylesheet>`), context);
    LogParametersType var1 = convertToLogParametersType(var0);
    logWrapper(var1);
    return var0;
}

function activityExtension_3(xml input, map<xml> context) returns xml {
    xml var0 = checkpanic xslt:transform(input, transformXSLT(xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns3="http://www.tibco.com/namespaces/tnt/plugins/file" xmlns:tns1="http://www.example.org/LogSchema" xmlns:bw="http://www.tibco.com/bw/xpath/bw-custom-functions" version="2.0"><xsl:param name="Start"/><xsl:template name="WriteFile-input" match="/"><tns3:WriteActivityInputTextClass><fileName><xsl:value-of select="concat(concat(bw:getModuleProperty('fileDir'), $Start/tns1:loggerName), '.txt')"/></fileName><textContent><xsl:value-of select="$Start/tns1:message"/></textContent></tns3:WriteActivityInputTextClass></xsl:template></xsl:stylesheet>`), context);
    WriteActivityInputTextClass var1 = convertToWriteActivityInputTextClass(var0);
    string var2 = <string>var1.fileName;
    string var3 = var1.textContent;
    checkpanic io:fileWriteString(var2, var3);
    return var0;
}

function activityExtension_4(xml input, map<xml> context) returns xml {
    xml var0 = checkpanic xslt:transform(input, transformXSLT(xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns1="http://www.example.org/LogSchema" xmlns:tns="http://www.tibco.com/xml/render/example" version="2.0"><xsl:param name="Start"/><xsl:template name="RenderXml-input" match="/"><tns:InputElement><level><xsl:value-of select="$Start/tns1:level"/></level><message><xsl:value-of select="$Start/tns1:message"/></message><logger><xsl:value-of select="$Start/tns1:loggerName"/></logger><timestamp><xsl:value-of select="current-dateTime()"/></timestamp></tns:InputElement></xsl:template></xsl:stylesheet>`), context);
    return var0;
}

function activityExtension_5(xml input, map<xml> context) returns xml {
    xml var0 = checkpanic xslt:transform(input, transformXSLT(xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns3="http://www.tibco.com/namespaces/tnt/plugins/file" xmlns:tns1="http://www.example.org/LogSchema" xmlns:bw="http://www.tibco.com/bw/xpath/bw-custom-functions" version="2.0"><xsl:param name="RenderXml"/><xsl:param name="Start"/><xsl:template name="XMLFile-input" match="/"><tns3:WriteActivityInputTextClass><fileName><xsl:value-of select="concat(concat(bw:getModuleProperty('fileDir'), $Start/tns1:loggerName), '.xml')"/></fileName><textContent><xsl:value-of select="$RenderXml"/></textContent></tns3:WriteActivityInputTextClass></xsl:template></xsl:stylesheet>`), context);
    WriteActivityInputTextClass var1 = convertToWriteActivityInputTextClass(var0);
    string var2 = <string>var1.fileName;
    string var3 = var1.textContent;
    checkpanic io:fileWriteString(var2, var3);
    return var0;
}

function loggingservice_LogProcess_start(anydata input) returns anydata {
    xml inputXML = toXML(input);
    xml xmlResult = process_loggingservice_LogProcess(inputXML);
    anydata result = convertToanydata(xmlResult);
    return result;
}

function process_loggingservice_LogProcess(xml input) returns xml {
    map<xml> context = {};
    addToContext(context, "post.item", input);
    worker start_worker {
        xml result0 = receiveEvent(input, context);
        if test(result0, "matches($Start/ns0:handler, " console ")") {
            result0 -> StartToLog;
        }
        if test(result0, "matches($Start/ns0:handler, " file ") and matches($Start/ns0:formatter, " text ")") {
            result0 -> StartToWriteFile;
        }
        if test(result0, "matches($Start/ns0:handler, " file ") and matches($Start/ns0:formatter, " xml  ")") {
            result0 -> StartToRenderXml;
        }
    }
    worker LogToEnd {
        xml result0 = <- activityExtension_2_worker;
        result0 -> activityExtension_worker;
    }
    worker RenderXmlToWriteFile1 {
        xml result0 = <- activityExtension_4_worker;
        result0 -> activityExtension_5_worker;
    }
    worker StartToLog {
        xml result0 = <- start_worker;
        result0 -> activityExtension_2_worker;
    }
    worker StartToRenderXml {
        xml result0 = <- start_worker;
        result0 -> activityExtension_4_worker;
    }
    worker StartToWriteFile {
        xml result0 = <- start_worker;
        result0 -> activityExtension_3_worker;
    }
    worker WriteFileToEnd {
        xml result0 = <- activityExtension_3_worker;
        result0 -> activityExtension_worker;
    }
    worker XMLFileToEnd {
        xml result0 = <- activityExtension_5_worker;
        result0 -> activityExtension_worker;
    }
    worker activityExtension_2_worker {
        xml input0 = <- StartToLog;
        xml combinedInput = input0;
        xml output = activityExtension_2(combinedInput, context);
        output -> LogToEnd;
    }
    worker activityExtension_3_worker {
        xml input0 = <- StartToWriteFile;
        xml combinedInput = input0;
        xml output = activityExtension_3(combinedInput, context);
        output -> WriteFileToEnd;
    }
    worker activityExtension_4_worker {
        xml input0 = <- StartToRenderXml;
        xml combinedInput = input0;
        xml output = activityExtension_4(combinedInput, context);
        output -> RenderXmlToWriteFile1;
    }
    worker activityExtension_5_worker {
        xml input0 = <- RenderXmlToWriteFile1;
        xml combinedInput = input0;
        xml output = activityExtension_5(combinedInput, context);
        output -> XMLFileToEnd;
    }
    worker activityExtension_worker {
        xml input0 = <- LogToEnd;
        xml input1 = <- WriteFileToEnd;
        xml input2 = <- XMLFileToEnd;
        xml combinedInput = input0 + input1 + input2;
        xml output = activityExtension(combinedInput, context);
        output -> function;
    }
    xml result0 = <- activityExtension_worker;
    xml result = result0;
    return result;
}

function receiveEvent(xml input, map<xml> context) returns xml {
    return input;
}
