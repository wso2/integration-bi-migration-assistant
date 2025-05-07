import ballerina/http;
import ballerina/xslt;

listener http:Listener proj_annon_var0 = GeneralConnection_sharedhttp;

service on proj_annon_var0 {
    resource function 'default [string... path](xml input) returns xml {
        xml inputVal = xml `<root>
    <item>
        ${input}
    </item>
</root>`;
        map<xml> paramXML = {post: inputVal};
        xml result = start_Processes_MainProcessStarter_process(input, paramXML);
        return result;
    }
}

function HTTP_Receiver(map<xml> context) returns xml|error {
    xml var0 = xml `<root></root>`;
    addToContext(context, "HTTP Receiver", var0);
    return var0;
}

function Mapper(map<xml> context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0">
     <xsl:template name="Transform0" match="/">
        <failedTestsCount>
                    
    <xsl:value-of select="count($runAllTests/root/ns:test-suites-results-msg/test-suites-results/ns3:test-suites-results//ns3:test-failure)" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                
</failedTestsCount>

    </xsl:template>
</xsl:stylesheet>`, context);
    addToContext(context, "Mapper", var1);
    return var1;
}

function scope0ActivityRunner(map<xml> cx) returns xml|error {
    xml result0 = check HTTP_Receiver(cx);
    xml result1 = check Mapper(cx);
    return result1;
}

function scope0FaultHandler(error err, map<xml> cx) returns xml {
    panic err;
}

function scope0ScopeFn(map<xml> cx) returns xml {
    xml|error result = scope0ActivityRunner(cx);
    if result is error {
        return scope0FaultHandler(result, cx);
    }
    return result;
}

function start_Processes_MainProcessStarter_process(xml inputXML, map<xml> params) returns xml {
    return scope0ScopeFn(params);
}
