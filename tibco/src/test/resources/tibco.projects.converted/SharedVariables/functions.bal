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
    //FIXME: Failed to convert rest of activity

    //<pd:activity name="loadJobSharedVariable" xmlns:pd="http://xmlns.tibco.com/bw/process/2003">
    //    <pd:type>com.tibco.pe.core.GetSharedVariableActivity</pd:type>
    //    <pd:resourceType>ae.activities.getSharedVariable</pd:resourceType>
    //    <config>
    //        <variableConfig>/Variables/shared.jobsharedvariable</variableConfig>
    //    </config>
    //    <pd:inputBindings/>
    //</pd:activity>
    addToContext(cx, "loadJobSharedVariable", var0);
}

function loadJobSharedVariable2(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    //FIXME: Failed to convert rest of activity

    //<pd:activity name="loadJobSharedVariable2" xmlns:pd="http://xmlns.tibco.com/bw/process/2003">
    //    <pd:type>com.tibco.pe.core.GetSharedVariableActivity</pd:type>
    //    <pd:resourceType>ae.activities.getSharedVariable</pd:resourceType>
    //    <config>
    //        <variableConfig>/Variables/shared.jobsharedvariable</variableConfig>
    //    </config>
    //    <pd:inputBindings/>
    //</pd:activity>
    addToContext(cx, "loadJobSharedVariable2", var0);
}

function loadSharedVariable(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    //FIXME: Failed to convert rest of activity

    //<pd:activity name="loadSharedVariable" xmlns:pd="http://xmlns.tibco.com/bw/process/2003">
    //    <pd:type>com.tibco.pe.core.GetSharedVariableActivity</pd:type>
    //    <pd:resourceType>ae.activities.getSharedVariable</pd:resourceType>
    //    <config>
    //        <variableConfig>/Variables/callVar.sharedvariable</variableConfig>
    //    </config>
    //    <pd:inputBindings/>
    //</pd:activity>
    addToContext(cx, "loadSharedVariable", var0);
}

function loadSharedVariable2(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    //FIXME: Failed to convert rest of activity

    //<pd:activity name="loadSharedVariable2" xmlns:pd="http://xmlns.tibco.com/bw/process/2003">
    //    <pd:type>com.tibco.pe.core.GetSharedVariableActivity</pd:type>
    //    <pd:resourceType>ae.activities.getSharedVariable</pd:resourceType>
    //    <config>
    //        <variableConfig>/Variables/callVar.sharedvariable</variableConfig>
    //    </config>
    //    <pd:inputBindings/>
    //</pd:activity>
    addToContext(cx, "loadSharedVariable2", var0);
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
        <xsl:value-of select="$loadJobSharedVariable/root/count + 1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>

    </xsl:template>
</xsl:stylesheet>`, cx.variables);
    //FIXME: Failed to convert rest of activity

    //<pd:activity name="storeJobSharedVariable" xmlns:pd="http://xmlns.tibco.com/bw/process/2003">
    //    <pd:type>com.tibco.pe.core.SetSharedVariableActivity</pd:type>
    //    <pd:resourceType>ae.activities.setSharedVariable</pd:resourceType>
    //    <config>
    //        <variableConfig>/Variables/shared.jobsharedvariable</variableConfig>
    //    </config>
    //    <pd:inputBindings>
    //        <xsl:value-of select="$loadJobSharedVariable/count + 1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
    //    </pd:inputBindings>
    //</pd:activity>
    addToContext(cx, "storeJobSharedVariable", var1);
}

function storeSharedVariable(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0"><xsl:param name="loadSharedVariable"/>     <xsl:template name="Transform1" match="/">
        <xsl:value-of select="$loadSharedVariable/root/count + 1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>

    </xsl:template>
</xsl:stylesheet>`, cx.variables);
    //FIXME: Failed to convert rest of activity

    //<pd:activity name="storeSharedVariable" xmlns:pd="http://xmlns.tibco.com/bw/process/2003">
    //    <pd:type>com.tibco.pe.core.SetSharedVariableActivity</pd:type>
    //    <pd:resourceType>ae.activities.setSharedVariable</pd:resourceType>
    //    <config>
    //        <variableConfig>/Variables/callVar.sharedvariable</variableConfig>
    //    </config>
    //    <pd:inputBindings>
    //        <xsl:value-of select="$loadSharedVariable/count + 1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
    //    </pd:inputBindings>
    //</pd:activity>
    addToContext(cx, "storeSharedVariable", var1);
}

function initContext(map<xml> initVariables = {}) returns Context {
    return {variables: initVariables, result: xml `<root/>`};
}

function addToContext(Context context, string varName, xml value) {
    xml children = value/*;
    xml transformed = xml `<root>${children}</root>`;
    context.variables[varName] = transformed;
    context.result = value;
}
