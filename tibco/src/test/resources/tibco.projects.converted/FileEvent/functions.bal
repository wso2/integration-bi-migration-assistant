import ballerina/log;
import ballerina/xslt;

function File_Poller(Context cx) returns error? {
    xml var0 = getFromContext(cx, "file");
    xml var1 = xml `<root>
       <EventSourceOutputNoContentClass xmlns="http://www.tibco.com/namespaces/tnt/plugins/file">
            ${var0}
       </EventSourceOutputNoContentClass>
   </root>`;
    addToContext(cx, "File-Poller", var1);
}

function Log(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/WriteToLogActivitySchema" xmlns:ns1="http://www.tibco.com/namespaces/tnt/plugins/file" version="2.0"><xsl:param name="File-Poller"/>     <xsl:template name="Transform0" match="/">
        <ns:ActivityInput xmlns:ns="http://www.tibco.com/pe/WriteToLogActivitySchema">
                    
    <message>
                            
        <xsl:value-of select="$File-Poller/root/ns1:EventSourceOuputNoContentClass/fileInfo/fileName" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                        
    </message>
                
</ns:ActivityInput>

    </xsl:template>
</xsl:stylesheet>`, cx.variables);
    xml var2 = var1/**/<message>/*;
    log:printInfo(var2.toString());
    addToContext(cx, "Log", var2);
}

function scope0ActivityRunner(Context cx) returns error? {
    check File_Poller(cx);
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

function start_main_process(Context cx) returns () {
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

function initContext(map<xml> initVariables = {},
        map<SharedVariableContext> jobSharedVariables = {})
            returns Context {
    map<SharedVariableContext> sharedVariables = {};

    foreach var key in jobSharedVariables.keys() {
        sharedVariables[key] = jobSharedVariables.get(key);
    }
    return {variables: initVariables, result: xml `<root/>`, sharedVariables};
}
