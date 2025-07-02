import ballerina/log;
import ballerina/xslt;

function HTTP_Receiver(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = xml `<root>${var0}</root>`;
    addToContext(cx, "HTTP-Receiver", var1);
}

function LogLoadedVars(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0"><xsl:param name="loadSharedVariable"/><xsl:param name="loadJobSharedVariable"/>     <xsl:template name="Transform0" match="/">
        <ns:ActivityInput xmlns:ns="http://www.tibco.com/pe/EngineTypes">
                    
    <message>
                            
        <sharedValue>
                                    
            <xsl:value-of select="$loadSharedVariable" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                                
        </sharedValue>
                            
        <jobValue>
                                    
            <xsl:value-of select="$loadJobSharedVariable" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                                
        </jobValue>
                        
    </message>
                
</ns:ActivityInput>

    </xsl:template>
</xsl:stylesheet>`, cx.variables);
    xml var2 = var1/**/<message>/*;
    log:printInfo(var2.toString());
    addToContext(cx, "LogLoadedVars", var2);
}

function LogLoadedVars2(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0"><xsl:param name="loadSharedVariable2"/><xsl:param name="loadJobSharedVariable2"/>     <xsl:template name="Transform3" match="/">
        <ns:ActivityInput xmlns:ns="http://www.tibco.com/pe/EngineTypes">
                    
    <message>
                            
        <sharedValue>
                                    
            <xsl:value-of select="$loadSharedVariable2" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                                
        </sharedValue>
                            
        <jobValue>
                                    
            <xsl:value-of select="$loadJobSharedVariable2" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                                
        </jobValue>
                        
    </message>
                
</ns:ActivityInput>

    </xsl:template>
</xsl:stylesheet>`, cx.variables);
    xml var2 = var1/**/<message>/*;
    log:printInfo(var2.toString());
    addToContext(cx, "LogLoadedVars2", var2);
}

function MapperXMLPayload(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0"><xsl:param name="loadSharedVariable2"/><xsl:param name="loadJobSharedVariable2"/>     <xsl:template name="Transform4" match="/">
        <payload>
                    
    <results>
                            
        <shared>
                                    
            <xsl:value-of select="$loadSharedVariable2" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                                
        </shared>
                            
        <job>
                                    
            <xsl:value-of select="$loadJobSharedVariable2" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                                
        </job>
                        
    </results>
                
</payload>

    </xsl:template>
</xsl:stylesheet>`, cx.variables);
    xml var2 = xml `<root>${var1}</root>`;
    addToContext(cx, "MapperXMLPayload", var2);
}

function loadJobSharedVariable(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = getSharedVariable(cx, "sharedVariable");
    addToContext(cx, "loadJobSharedVariable", var1);
}

function loadJobSharedVariable2(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = getSharedVariable(cx, "sharedVariable");
    addToContext(cx, "loadJobSharedVariable2", var1);
}

function loadSharedVariable(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = getSharedVariable(cx, "callVar");
    addToContext(cx, "loadSharedVariable", var1);
}

function loadSharedVariable2(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = getSharedVariable(cx, "callVar");
    addToContext(cx, "loadSharedVariable2", var1);
}

function scope0ActivityRunner(Context cx) returns error? {
    check HTTP_Receiver(cx);
    check loadSharedVariable(cx);
    check loadJobSharedVariable(cx);
    check LogLoadedVars(cx);
    check storeSharedVariable(cx);
    check storeJobSharedVariable(cx);
    check loadSharedVariable2(cx);
    check loadJobSharedVariable2(cx);
    check LogLoadedVars2(cx);
    check MapperXMLPayload(cx);
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

function storeJobSharedVariable(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0"><xsl:param name="loadJobSharedVariable"/>     <xsl:template name="Transform2" match="/">
        <root>
                    
    <count>
                            
        <xsl:value-of select="$loadJobSharedVariable/root/count + 1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                        
    </count>
                
</root>

    </xsl:template>
</xsl:stylesheet>`, cx.variables);
    setSharedVariable(cx, "sharedVariable", var1);
    addToContext(cx, "storeJobSharedVariable", var1);
}

function storeSharedVariable(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0"><xsl:param name="loadSharedVariable"/>     <xsl:template name="Transform1" match="/">
        <root>
                    
    <count>
                            
        <xsl:value-of select="$loadSharedVariable/root/count + 1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                        
    </count>
                
</root>

    </xsl:template>
</xsl:stylesheet>`, cx.variables);
    setSharedVariable(cx, "callVar", var1);
    addToContext(cx, "storeSharedVariable", var1);
}

function getSharedVariable(Context cx, string varName) returns xml {
    SharedVariableContext varContext = cx.sharedVariables.get(varName);
    function () returns xml getter = varContext.getter;
    return getter();
}

function setSharedVariable(Context cx, string varName, xml value) {
    SharedVariableContext varContext = cx.sharedVariables.get(varName);
    function (xml) setter = varContext.setter;
    setter(value);
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
    SharedVariableContext sharedVarContext1 = {
        getter: function() returns xml {
            return sharedVariable;
        },
        setter: function(xml value) {
            sharedVariable = value;
        }
    };

    sharedVariables["sharedVariable"] = sharedVarContext1;

    foreach var key in jobSharedVariables.keys() {
        sharedVariables[key] = jobSharedVariables.get(key);
    }
    return {variables: initVariables, result: xml `<root/>`, sharedVariables};
}
