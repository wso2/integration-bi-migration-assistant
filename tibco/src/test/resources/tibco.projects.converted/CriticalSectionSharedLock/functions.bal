import ballerina/data.xmldata;
import ballerina/log;
import ballerina/xslt;

function Audit(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    // FIXME: multi-engine lock '/Resources/Locks/Audit.sharedLock' only applies within this runtime
    lock {
        _ = criticalSectionLock_Resources_Locks_Audit;
        check scope2ScopeFn(cx);
    }
    xml var1 = cx.result;
    addToContext(cx, "Audit", var1);
}

function HTTP_Receiver(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = xml `<root>${var0}</root>`;
    addToContext(cx, "HTTP-Receiver", var1);
}

function Log_Audit(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/WriteToLogActivitySchema" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="2.0">
     <xsl:template name="Transform1" match="/">
        <ns:ActivityInput xmlns:ns="http://www.tibco.com/pe/WriteToLogActivitySchema">
                        
    <message>
                                
        <xsl:value-of select="'auditing'" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                            
    </message>
                    
</ns:ActivityInput>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xml var3 = var2/**/<message>/*;
    log:printInfo(var3.toString());
    addToContext(cx, "Log-Audit", var3);
}

function Log_Write(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/WriteToLogActivitySchema" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="2.0">
     <xsl:template name="Transform0" match="/">
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
    addToContext(cx, "Log-Write", var3);
}

function Write_Counter(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    lock {
        _ = criticalSectionLock_Resources_Locks_Counter;
        check scope1ScopeFn(cx);
    }
    xml var1 = cx.result;
    addToContext(cx, "Write-Counter", var1);
}

function scope0ActivityRunner(Context cx) returns error? {
    check HTTP_Receiver(cx);
    check Write_Counter(cx);
    check Audit(cx);
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
    check Log_Write(cx);
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

function scope2ActivityRunner(Context cx) returns error? {
    check Log_Audit(cx);
}

function scope2FaultHandler(error err, Context cx) returns error? {
    return err;
}

function scope2ScopeFn(Context cx) returns error? {
    error? result = scope2ActivityRunner(cx);
    if result is error {
        check scope2FaultHandler(result, cx);
    }
}

function start_Processes_Main_process(Context cx) returns error? {
    return scope0ScopeFn(cx);
}

function For_Each(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = xml `<root></root>`;
    xml var2 = getFromContext(cx, "Start");
    var2 = check xmldata:transform(var2, `items/item`);
    int var3 = -1;
    addToContext(cx, "item", xml `<root></root>`);
    foreach xml each in var2 {
        var3 = var3 + 1;
        addToContext(cx, "idx", xml `<root>${var3}</root>`);
        addToContext(cx, "item", each);
        check scope1_1ScopeFn(cx);
        xml result = cx.result;
        var1 = result;
    }
    addToContext(cx, "For-Each", var1);
}

function Log_Read(Context cx) returns error? {
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
    addToContext(cx, "Log-Read", var3);
}

function Log_Reset(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/WriteToLogActivitySchema" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="2.0">
     <xsl:template name="Transform1" match="/">
        <ns:ActivityInput xmlns:ns="http://www.tibco.com/pe/WriteToLogActivitySchema">
                        
    <message>
                                
        <xsl:value-of select="'resetting counter'" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                            
    </message>
                    
</ns:ActivityInput>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xml var3 = var2/**/<message>/*;
    log:printInfo(var3.toString());
    addToContext(cx, "Log-Reset", var3);
}

function Log_Unscoped(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/WriteToLogActivitySchema" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="2.0">
     <xsl:template name="Transform2" match="/">
        <ns:ActivityInput xmlns:ns="http://www.tibco.com/pe/WriteToLogActivitySchema">
                        
    <message>
                                
        <xsl:value-of select="'no scope'" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                            
    </message>
                    
</ns:ActivityInput>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xml var3 = var2/**/<message>/*;
    log:printInfo(var3.toString());
    addToContext(cx, "Log-Unscoped", var3);
}

function Read_Counter(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    lock {
        _ = criticalSectionLock_Resources_Locks_Counter;
        check scope2_1ScopeFn(cx);
    }
    xml var1 = cx.result;
    addToContext(cx, "Read-Counter", var1);
}

function Reset_Counter(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    lock {
        _ = criticalSectionLock_Resources_Locks_Counter;
        check scope3ScopeFn(cx);
    }
    xml var1 = cx.result;
    addToContext(cx, "Reset-Counter", var1);
}

function Unscoped(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    lock {
        check scope4ScopeFn(cx);
    }
    xml var1 = cx.result;
    addToContext(cx, "Unscoped", var1);
}

function scope0_1ActivityRunner(Context cx) returns error? {
    check For_Each(cx);
    check Reset_Counter(cx);
    check Unscoped(cx);
}

function scope0_1FaultHandler(error err, Context cx) returns error? {
    return err;
}

function scope0_1ScopeFn(Context cx) returns error? {
    error? result = scope0_1ActivityRunner(cx);
    if result is error {
        check scope0_1FaultHandler(result, cx);
    }
}

function scope1_1ActivityRunner(Context cx) returns error? {
    check Read_Counter(cx);
}

function scope1_1FaultHandler(error err, Context cx) returns error? {
    return err;
}

function scope1_1ScopeFn(Context cx) returns error? {
    error? result = scope1_1ActivityRunner(cx);
    if result is error {
        check scope1_1FaultHandler(result, cx);
    }
}

function scope2_1ActivityRunner(Context cx) returns error? {
    check Log_Read(cx);
}

function scope2_1FaultHandler(error err, Context cx) returns error? {
    return err;
}

function scope2_1ScopeFn(Context cx) returns error? {
    error? result = scope2_1ActivityRunner(cx);
    if result is error {
        check scope2_1FaultHandler(result, cx);
    }
}

function scope3ActivityRunner(Context cx) returns error? {
    check Log_Reset(cx);
}

function scope3FaultHandler(error err, Context cx) returns error? {
    return err;
}

function scope3ScopeFn(Context cx) returns error? {
    error? result = scope3ActivityRunner(cx);
    if result is error {
        check scope3FaultHandler(result, cx);
    }
}

function scope4ActivityRunner(Context cx) returns error? {
    check Log_Unscoped(cx);
}

function scope4FaultHandler(error err, Context cx) returns error? {
    return err;
}

function scope4ScopeFn(Context cx) returns error? {
    error? result = scope4ActivityRunner(cx);
    if result is error {
        check scope4FaultHandler(result, cx);
    }
}

function start_Processes_Reader_process(Context cx) returns error? {
    return scope0_1ScopeFn(cx);
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
