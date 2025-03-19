import ballerina/xslt;

function activityExtension(xml input, map<xml> context) returns xml {
    xml var0 = checkpanic xslt:transform(input, transformXSLT(xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns2="http://www.example.org/LogResult" version="2.0"><xsl:template name="End-input" match="/"><tns2:result><xsl:value-of select="'Logging Done'"/></tns2:result></xsl:template></xsl:stylesheet>`), context);
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
        result0 -> StartToLog;
        result0 -> StartToWriteFile;
        result0 -> StartToRenderXml;
        xml result1 = unhandled(input, context);
        xml result2 = unhandled_3(input, context);
        xml result3 = unhandled_4(input, context);
        xml result4 = unhandled_5(input, context);
    }
    worker LogToEnd {
    }
    worker WriteFileToEnd {
    }
    worker XMLFileToEnd {
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
    xml result1 = <- unhandled_worker;
    xml result2 = <- unhandled_3_worker;
    xml result3 = <- unhandled_4_worker;
    xml result4 = <- unhandled_5_worker;
    xml result = result0 + result1 + result2 + result3 + result4;
    return result;
}

function receiveEvent(xml input, map<xml> context) returns xml {
    return input;
}

function unhandled(xml input, map<xml> context) returns xml {
    //Unknown extension kind: bw.generalactivities.log
    return input;
}

function unhandled_3(xml input, map<xml> context) returns xml {
    //Unknown extension kind: bw.file.write
    return input;
}

function unhandled_4(xml input, map<xml> context) returns xml {
    //Unknown extension kind: bw.xml.renderxml
    return input;
}

function unhandled_5(xml input, map<xml> context) returns xml {
    //Unknown extension kind: bw.file.write
    return input;
}
