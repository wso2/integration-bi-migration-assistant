import ballerina/log;
import ballerina/xslt;

function JMS_Queue_Receiver(Context cx) returns error? {
    xml var0 = getFromContext(cx, "jms");
    xml var1 = xml `<root>
       <ActivityOutput xmlns="http://www.tibco.com/namespaces/tnt/plugins/jms">
            <Body>
                ${var0}
            </Body>
       </ActivityOutput>
   </root>`;
    addToContext(cx, "JMS-Queue-Receiver", var1);
}

function Log(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0"><xsl:param name="JMS-Queue-Receiver"/>     <xsl:template name="Transform0" match="/">
        <ns:ActivityInput xmlns:ns="http://www.tibco.com/pe/WriteToLogActivitySchema">
                    
    <message>
                            
        <xsl:value-of select="$JMS-Queue-Receiver/root/ns1:ActivityOutput/Body" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                        
    </message>
                
</ns:ActivityInput>

    </xsl:template>
</xsl:stylesheet>`, cx.variables);
    xml var2 = var1/**/<message>/*;
    log:printInfo(var2.toString());
    addToContext(cx, "Log", var2);
}

function scope0ActivityRunner(Context cx) returns error? {
    check JMS_Queue_Receiver(cx);
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

function start_Main_process(Context cx) returns () {
    return scope0ScopeFn(cx);
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

function initContext(map<xml> initVariables = {}) returns Context {
    return {variables: initVariables, result: xml `<root/>`};
}
