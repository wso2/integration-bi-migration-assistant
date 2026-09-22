import ballerina/log;
import ballerina/xslt;

function BranchA(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/EngineTypes" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="2.0"><xsl:param name="Init"/>     <xsl:template name="Transform1" match="/">
        <ns:ActivityInput xmlns:ns="http://www.tibco.com/pe/EngineTypes">
                    
    <message>
        Branch A saw
        <xsl:value-of select="$Init" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
    </message>
                
</ns:ActivityInput>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xml var3 = var2/**/<message>/*;
    log:printInfo(var3.toString());
    addToContext(cx, "BranchA", var3);
}

function BranchB(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/EngineTypes" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="2.0"><xsl:param name="Init"/>     <xsl:template name="Transform2" match="/">
        <ns:ActivityInput xmlns:ns="http://www.tibco.com/pe/EngineTypes">
                    
    <message>
        Branch B saw
        <xsl:value-of select="$Init" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
    </message>
                
</ns:ActivityInput>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xml var3 = var2/**/<message>/*;
    log:printInfo(var3.toString());
    addToContext(cx, "BranchB", var3);
}

function Init(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/EngineTypes" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="2.0">
     <xsl:template name="Transform0" match="/">
        <ns:ActivityInput xmlns:ns="http://www.tibco.com/pe/EngineTypes">
                    
    <message>FanOut subprocess entered</message>
                
</ns:ActivityInput>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xml var3 = var2/**/<message>/*;
    log:printInfo(var3.toString());
    addToContext(cx, "Init", var3);
}

function Merge(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/EngineTypes" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="2.0"><xsl:param name="BranchA"/><xsl:param name="BranchB"/>     <xsl:template name="Transform3" match="/">
        <ns:ActivityInput xmlns:ns="http://www.tibco.com/pe/EngineTypes">
                    
    <message>
        Merged
        <xsl:value-of select="$BranchA" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
        <xsl:value-of select="$BranchB" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
    </message>
                
</ns:ActivityInput>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xml var3 = var2/**/<message>/*;
    log:printInfo(var3.toString());
    addToContext(cx, "Merge", var3);
}

function scope0ActivityRunner(Context cx) returns error? {
    check Init(cx);
    check BranchB(cx);
    check BranchA(cx);
    check Merge(cx);
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

function start_Processes_FanOut_process(Context cx) returns () {
    return scope0ScopeFn(cx);
}

function Unreachable(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/EngineTypes" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="2.0">
     <xsl:template name="Transform0" match="/">
        <ns:ActivityInput xmlns:ns="http://www.tibco.com/pe/EngineTypes">
                    
    <message>Never reached, no transitions declared</message>
                
</ns:ActivityInput>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xml var3 = var2/**/<message>/*;
    log:printInfo(var3.toString());
    addToContext(cx, "Unreachable", var3);
}

function scope0_1ActivityRunner(Context cx) returns error? {
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

function start_Processes_NoTransitions_process(Context cx) returns () {
    return scope0_1ScopeFn(cx);
}

function FirstStep(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/EngineTypes" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="2.0">
     <xsl:template name="Transform0" match="/">
        <ns:ActivityInput xmlns:ns="http://www.tibco.com/pe/EngineTypes">
                    
    <message>BeginFlow first step</message>
                
</ns:ActivityInput>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xml var3 = var2/**/<message>/*;
    log:printInfo(var3.toString());
    addToContext(cx, "FirstStep", var3);
}

function SecondStep(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/EngineTypes" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="2.0"><xsl:param name="FirstStep"/>     <xsl:template name="Transform1" match="/">
        <ns:ActivityInput xmlns:ns="http://www.tibco.com/pe/EngineTypes">
                    
    <message>
        BeginFlow second step saw
        <xsl:value-of select="$FirstStep" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
    </message>
                
</ns:ActivityInput>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xml var3 = var2/**/<message>/*;
    log:printInfo(var3.toString());
    addToContext(cx, "SecondStep", var3);
}

function scope0_2ActivityRunner(Context cx) returns error? {
    check FirstStep(cx);
    check SecondStep(cx);
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

function start_Processes_BeginFlow_process(Context cx) returns () {
    return scope0_2ScopeFn(cx);
}

function addToContext(Context context, string varName, xml value) {
    xml children = value/*;
    xml transformed = xml `<root>${children}</root>`;
    context.variables[varName] = transformed;
    context.result = value;
}
