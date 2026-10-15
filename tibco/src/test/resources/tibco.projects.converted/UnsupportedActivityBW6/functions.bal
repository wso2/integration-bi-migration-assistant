import ballerina/log;
import ballerina/xslt;

function JavaInvoke(Context cx) returns error? {
    // FIXME: Unsupported activity JavaInvoke: Unknown extension kind: bw.java.javamethod

    // <bpws:extensionActivity xmlns:bpws="http://docs.oasis-open.org/wsbpel/2.0/process/executable">
    //     <tibex:activityExtension inputVariable="JavaInvoke-input" name="JavaInvoke" outputVariable="JavaInvoke" xmlns:tibex="http://www.tibco.com/bpel/2007/extensions">
    //         <bpws:targets>
    //             <bpws:target linkName="LogAToJava"/>
    //         </bpws:targets>
    //         <bpws:sources>
    //             <bpws:source linkName="JavaToLogB"/>
    //         </bpws:sources>
    //         <tibex:config>
    //             <bwext:BWActivity xmlns:activityconfig="http://tns.tibco.com/bw/model/activityconfig" xmlns:bwext="http://tns.tibco.com/bw/model/core/bwext" xmlns:java="http://ns.tibco.com/bw/palette/java" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" activityTypeID="bw.java.javamethod">
    //                 <activityConfig>
    //                     <properties name="config" xsi:type="activityconfig:EMFProperty">
    //                         <type href="http://ns.tibco.com/bw/palette/java#//JavaMethod"/>
    //                         <value className="com.example.Helper" isStaticMethod="true" methodName="doWork" methodReturn="void" xsi:type="java:JavaMethod"/>
    //                     </properties>
    //                 </activityConfig>
    //             </bwext:BWActivity>
    //         </tibex:config>
    //     </tibex:activityExtension>
    // </bpws:extensionActivity>
    return error("Unsupported activity JavaInvoke: Unknown extension kind: bw.java.javamethod");
}

function LogA(Context cx) returns error? {
    xml var0 = getFromContext(cx, "LogA-input");
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://www.tibco.com/pe/WriteToLogActivitySchema" xmlns:tib="http://www.tibco.com/bw/xslt/custom-functions" version="2.0"><xsl:template name="LogA-input" match="/"><tns:ActivityInput><message><xsl:value-of select="'A'"/></message></tns:ActivityInput></xsl:template></xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xml var3 = var2/**/<message>/*;
    log:printInfo(var3.toString());
}

function LogB(Context cx) returns error? {
    xml var0 = getFromContext(cx, "LogB-input");
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://www.tibco.com/pe/WriteToLogActivitySchema" xmlns:tib="http://www.tibco.com/bw/xslt/custom-functions" version="2.0"><xsl:template name="LogB-input" match="/"><tns:ActivityInput><message><xsl:value-of select="'B'"/></message></tns:ActivityInput></xsl:template></xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xml var3 = var2/**/<message>/*;
    log:printInfo(var3.toString());
}

function Start(Context cx) returns error? {
    addToContext(cx, "Start", getFromContext(cx, "$input"));
}

function scopeActivityRunner(Context cx) returns error? {
    check Start(cx);
    check LogA(cx);
    check JavaInvoke(cx);
    check LogB(cx);
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

function start_test_unsupported_MainProcess(Context params) returns () {
    scopeScopeFn(params);
}

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
