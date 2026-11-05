import ballerina/data.xmldata;
import ballerina/log;
import ballerina/xslt;

function LogA(Context cx) returns error? {
    xml var0 = getFromContext(cx, "LogA-input");
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://www.tibco.com/pe/WriteToLogActivitySchema" version="2.0"><xsl:template name="LogA-input" match="/"><tns:ActivityInput><message><xsl:value-of select="'A'"/></message></tns:ActivityInput></xsl:template></xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xml var3 = var2/**/<message>/*;
    log:printInfo(var3.toString());
}

function LogB(Context cx) returns error? {
    xml var0 = getFromContext(cx, "LogB-input");
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://www.tibco.com/pe/WriteToLogActivitySchema" version="2.0"><xsl:template name="LogB-input" match="/"><tns:ActivityInput><message><xsl:value-of select="'B'"/></message></tns:ActivityInput></xsl:template></xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xml var3 = var2/**/<message>/*;
    log:printInfo(var3.toString());
}

function Start(Context cx) returns error? {
}

function scopeActivityRunner(Context cx) returns error? {
    check Start(cx);
    check LogA(cx);
    boolean LogAToLogBTaken = test_conditional_MainProcess_predicate_0(xml `<root></root>`, cx);
    if LogAToLogBTaken {
        check LogB(cx);
    }
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

function start_test_conditional_MainProcess(Context params) returns () {
    scopeScopeFn(params);
}

function test_conditional_MainProcess_predicate_0(xml input, Context cx) returns boolean {
    return checkpanic xmldata:transform(input, `1 = 1`, boolean);
}

function getFromContext(Context context, string varName) returns xml {
    xml? value = context.variables[varName];
    if value == () {
        return xml `<root/>`;
    }
    return value;
}
