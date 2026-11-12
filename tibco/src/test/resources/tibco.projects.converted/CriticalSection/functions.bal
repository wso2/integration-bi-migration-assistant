import ballerina/log;
import ballerina/xslt;

function HTTP_Receiver(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = xml `<root>${var0}</root>`;
    addToContext(cx, "HTTP-Receiver", var1);
}

function Read_Counter(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/WriteToLogActivitySchema" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="2.0">
     <xsl:template name="Transform0" match="/">
        <ns:ActivityInput xmlns:ns="http://www.tibco.com/pe/WriteToLogActivitySchema">
                        
    <message>
                                
        <xsl:value-of select="'reading counter'" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                            
    </message>
                    
</ns:ActivityInput>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xml var3 = var2/**/<message>/*;
    log:printInfo(var3.toString());
    addToContext(cx, "Read-Counter", var3);
}

function Update_Counter(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    lock {
        check scope1ScopeFn(cx);
    }
    xml var1 = cx.result;
    addToContext(cx, "Update-Counter", var1);
}

function Write_Counter(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/WriteToLogActivitySchema" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="2.0">
     <xsl:template name="Transform1" match="/">
        <ns:ActivityInput xmlns:ns="http://www.tibco.com/pe/WriteToLogActivitySchema">
                        
    <message>
                                
        <xsl:value-of select="'writing counter'" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                            
    </message>
                    
</ns:ActivityInput>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xml var3 = var2/**/<message>/*;
    log:printInfo(var3.toString());
    addToContext(cx, "Write-Counter", var3);
}

function scope0ActivityRunner(Context cx) returns error? {
    check HTTP_Receiver(cx);
    check Update_Counter(cx);
}

function scope0FaultHandler(error err, Context cx) returns error? {
    return err;
}

function scope0ScopeFn(Context cx) returns error? {
    error? result = scope0ActivityRunner(cx);
    if result is error {
        check scope0FaultHandler(result, cx);
    }
}

function scope1ActivityRunner(Context cx) returns error? {
    check Read_Counter(cx);
    check Write_Counter(cx);
}

function scope1FaultHandler(error err, Context cx) returns error? {
    return err;
}

function scope1ScopeFn(Context cx) returns error? {
    error? result = scope1ActivityRunner(cx);
    if result is error {
        check scope1FaultHandler(result, cx);
    }
}

function start_Processes_Main_process(Context cx) returns error? {
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
