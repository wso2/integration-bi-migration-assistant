import ballerina/io;
import ballerina/jballerina.java;
import ballerina/jballerina.java.arrays;
import ballerina/xslt;

function JoinNames(Context cx) returns error? {
    xml var0 = getFromContext(cx, "JoinNames-input");
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="www.tibco.com/plugin/java/JavaInvokeActivityInput+3a7e9c21-4f0b-4d68-b2a5-8c1d6e0f9b47+JavaInvokeActivityInput" version="2.0"><xsl:template name="JoinNames-input" match="/"><tns:JavaInvokeActivityInput><MethodParameters><separator><xsl:value-of select="', '"/></separator><parts><xsl:value-of select="'alice'"/></parts><parts><xsl:value-of select="'bob'"/></parts></MethodParameters></tns:JavaInvokeActivityInput></xsl:template></xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    handle var3 = (var2/**/<separator>).length() == 0 ? java:createNull() : java:fromString((var2/**/<separator>/*).toString());
    string[] var4 = from xml item in var2/**/<parts>
        select (item/*).toString();
    handle var5 = arrays:newInstance(check java:getClass("java.lang.String"), var4.length());
    foreach int index in 0 ..< var4.length() {
        arrays:set(var5, index, java:fromString(var4[index]));
    }
    handle var6 = check trap TextTools_join(var3, var5);
    string? var7 = java:toString(var6);
    xml var8 = xml `<root>${var7 is () ? xml `` : xml `<MethodReturnValue>${var7}</MethodReturnValue>`}</root>`;
    addToContext(cx, "JoinNames", var8);
}

function ScaleTotal(Context cx) returns error? {
    xml var0 = getFromContext(cx, "ScaleTotal-input");
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="www.tibco.com/plugin/java/JavaInvokeActivityInput+8d2b4f60-9e1c-4a37-a6f8-0b5c3e7d2a19+JavaInvokeActivityInput" version="2.0"><xsl:template name="ScaleTotal-input" match="/"><tns:JavaInvokeActivityInput><MethodParameters><value><xsl:value-of select="4"/></value><factor><xsl:value-of select="2.5"/></factor></MethodParameters></tns:JavaInvokeActivityInput></xsl:template></xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    int var3 = check int:fromString((var2/**/<value>/*).toString().trim());
    float var4 = check float:fromString((var2/**/<factor>/*).toString().trim());
    float var5 = check trap TextTools_scale(var3, var4);
    xml var6 = xml `<root><MethodReturnValue>${var5}</MethodReturnValue></root>`;
    addToContext(cx, "ScaleTotal", var6);
}

function Start(Context cx) returns error? {
}

function WriteSummary(Context cx) returns error? {
    xml var0 = getFromContext(cx, "WriteSummary-input");
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns3="http://www.tibco.com/namespaces/tnt/plugins/file" version="2.0"><xsl:param name="JoinNames"/><xsl:param name="ScaleTotal"/><xsl:template name="WriteSummary-input" match="/"><tns3:WriteActivityInputTextClass><fileName><xsl:value-of select="'/data/outbound/summary.txt'"/></fileName><textContent><xsl:value-of select="concat($JoinNames/root/MethodReturnValue, $ScaleTotal/root/MethodReturnValue)"/></textContent></tns3:WriteActivityInputTextClass></xsl:template></xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    string fileName = (var2/**/<fileName>/*).toString();
    string content = (var2/**/<textContent>/*).toString();
    check io:fileWriteString(fileName, content, "OVERWRITE");
}

function scopeActivityRunner(Context cx) returns error? {
    check Start(cx);
    check JoinNames(cx);
    check ScaleTotal(cx);
    check WriteSummary(cx);
}

function scopeFaultHandler(error err, Context cx) returns () {
    panic err;
}

function scopeScopeFn(Context cx) returns () {
    error? result = scopeActivityRunner(cx);
    if result is error {
        scopeFaultHandler(result, cx);
    }
}

function start_test_javainvoke_SummaryProcess(Context params) returns () {
    scopeScopeFn(params);
}

function TextTools_join(handle separator, handle parts) returns handle|error = @java:Method {
    'class: "com.example.text.TextTools",
    name: "join",
    paramTypes: ["java.lang.String", {'class: "java.lang.String", dimensions: 1}]
} external;

function TextTools_scale(int value, float factor) returns float|error = @java:Method {
    'class: "com.example.text.TextTools",
    name: "scale",
    paramTypes: ["int", "double"]
} external;

function addToContext(Context context, string varName, xml value) {
    xml children = value/*;
    xml transformed = xml `<root>${children}</root>`;
    context.variables[varName] = transformed;
    context.result = value;
}

function getFromContext(Context context, string varName) returns xml {
    xml? value = context.variables[varName];
    if value == () {
        return xml `<root/>`;
    }
    return value;
}
