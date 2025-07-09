import ballerina/xslt;

function HTTP_Receiver(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = xml `<root>${var0}</root>`;
    addToContext(cx, "HTTP-Receiver", var1);
}

function Transform_XML(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/EngineTypes" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="2.0"><xsl:param name="HTTP-Receiver"/>     <xsl:template name="Transform0" match="/">
        <textInput>
                    
    <xmlString>
                            
        <xsl:value-of select="$HTTP-Receiver/root/payload" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                        
    </xmlString>
                
</textInput>

    </xsl:template>
</xsl:stylesheet>`, cx.variables);
    xml var2 = check xslt:transform(var1, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/EngineTypes"
            xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="2.0">
    <xsl:param name="HTTP-Receiver"/>
    <xsl:template name="Transform2" match="/">
        <ActivityInput>
            <message>
                <xsl:value-of select="$HTTP-Receiver/root/payload"
                    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
            </message>
        </ActivityInput>
    </xsl:template>
</xsl:stylesheet>`, cx);
    addToContext(cx, "Transform-XML", var2);
}

function scope0ActivityRunner(Context cx) returns error? {
    check HTTP_Receiver(cx);
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

function start_Processes_Main_process(Context cx) returns () {
    return scope0ScopeFn(cx);
}

function addToContext(Context context, string varName, xml value) {
    xml children = value/*;
    xml transformed = xml `<root>${children}</root>`;
    context.variables[varName] = transformed;
    context.result = value;
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
