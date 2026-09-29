import ballerina/log;
import ballerina/xslt;

function BCommonLog(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/EngineTypes" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="2.0"><xsl:param name="Start"/>     <xsl:template name="Transform0" match="/">
        <ns:ActivityInput xmlns:ns="http://www.tibco.com/pe/EngineTypes">
                    
    <message>
        B Common invoked with
        <xsl:value-of select="$Start/root/options" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
    </message>
                
</ns:ActivityInput>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xml var3 = var2/**/<message>/*;
    log:printInfo(var3.toString());
    addToContext(cx, "BCommonLog", var3);
}

function WorkerResult(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/EngineTypes" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="2.0"><xsl:param name="BCommonLog"/>     <xsl:template name="Transform1" match="/">
        <WorkerOutput>
                    
    <xsl:value-of select="$BCommonLog" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                
</WorkerOutput>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xml var3 = xml `<root>${var2}</root>`;
    addToContext(cx, "WorkerResult", var3);
}

function scope0ActivityRunner(Context cx) returns error? {
    check BCommonLog(cx);
    check WorkerResult(cx);
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

function start_Processes_B_Common_process(Context cx) returns () {
    return scope0ScopeFn(cx);
}

function ACommonLog(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/EngineTypes" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="2.0"><xsl:param name="Start"/>     <xsl:template name="Transform0" match="/">
        <ns:ActivityInput xmlns:ns="http://www.tibco.com/pe/EngineTypes">
                    
    <message>
        A Common invoked with
        <xsl:value-of select="$Start/root/options" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
    </message>
                
</ns:ActivityInput>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xml var3 = var2/**/<message>/*;
    log:printInfo(var3.toString());
    addToContext(cx, "ACommonLog", var3);
}

function WorkerResult_1(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/EngineTypes" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="2.0"><xsl:param name="ACommonLog"/>     <xsl:template name="Transform1" match="/">
        <WorkerOutput>
                    
    <xsl:value-of select="$ACommonLog" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                
</WorkerOutput>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xml var3 = xml `<root>${var2}</root>`;
    addToContext(cx, "WorkerResult", var3);
}

function scope0_1ActivityRunner(Context cx) returns error? {
    check ACommonLog(cx);
    check WorkerResult_1(cx);
}

function scope0_1FaultHandler(error err, Context cx) returns () {
    panic err;
}

function scope0_1ScopeFn(Context cx) returns () {
    error? result = scope0_1ActivityRunner(cx);
    if result is error {
        scope0_1FaultHandler(result, cx);
    }
}

function start_Processes_A_Common_process(Context cx) returns () {
    return scope0_1ScopeFn(cx);
}

function Call_Common(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/EngineTypes" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="2.0"><xsl:param name="post"/>     <xsl:template name="Transform0" match="/">
        <InvokeProcessInput>
                    
    <options>
                            
        <xsl:value-of select="$post/root/item/req" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                        
    </options>
                
</InvokeProcessInput>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    addToContext(cx, "Start", var2);
    start_Processes_A_Common_process(cx);
    xml var3 = cx.result;
    addToContext(cx, "Call-Common", var3);
}

function CallerLog(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/EngineTypes" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="2.0"><xsl:param name="Call-Common"/>     <xsl:template name="Transform1" match="/">
        <ns:ActivityInput xmlns:ns="http://www.tibco.com/pe/EngineTypes">
                    
    <message>
        Common returned
        <xsl:value-of select="$Call-Common" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
    </message>
                
</ns:ActivityInput>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xml var3 = var2/**/<message>/*;
    log:printInfo(var3.toString());
    addToContext(cx, "CallerLog", var3);
}

function HTTP_Receiver(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = xml `<root>${var0}</root>`;
    addToContext(cx, "HTTP-Receiver", var1);
}

function scope0_2ActivityRunner(Context cx) returns error? {
    check HTTP_Receiver(cx);
    check Call_Common(cx);
    check CallerLog(cx);
}

function scope0_2FaultHandler(error err, Context cx) returns () {
    panic err;
}

function scope0_2ScopeFn(Context cx) returns () {
    error? result = scope0_2ActivityRunner(cx);
    if result is error {
        scope0_2FaultHandler(result, cx);
    }
}

function start_Processes_Caller_process(Context cx) returns () {
    return scope0_2ScopeFn(cx);
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
