import ballerina/xslt;

function HTTP_Receiver(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = xml `<root>${var0}</root>`;
    addToContext(cx, "HTTP-Receiver", var1);
}

function Sub(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="2.0"><xsl:param name="post"/>
     <xsl:template name="Transform1" match="/">
        <root>
                    
    <name>
                            
        <xsl:value-of select="$post/root/item/name" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                        
    </name>
                
</root>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    addToContext(cx, "Start", var2);
    check start_Processes_Sub_process(cx);
    xml var3 = cx.result;
    addToContext(cx, "Sub", var3);
}

function Use_Result(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="2.0"><xsl:param name="Sub"/>
     <xsl:template name="Transform2" match="/">
        <Reply>
                    
    <xsl:value-of select="$Sub/root/Out/value" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                
</Reply>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xml var3 = xml `<root>${var2}</root>`;
    addToContext(cx, "Use-Result", var3);
}

function scope0ActivityRunner(Context cx) returns error? {
    check HTTP_Receiver(cx);
    check Sub(cx);
    check Use_Result(cx);
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

function start_Processes_Caller_process(Context cx) returns error? {
    check scope0ScopeFn(cx);
    xml var0 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="2.0"><xsl:param name="Use-Result"/>
     <xsl:template name="Transform0" match="/">
        <Response>
                
    <greeting>
                        
        <xsl:value-of select="$Use-Result/root/Reply" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                    
    </greeting>
            
</Response>

    </xsl:template>
</xsl:stylesheet>`);
    xml var1 = check xslt:transform(cx.result, var0, cx.variables);
    cx.result = xml `<root>${var1}</root>`;
}

function M(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="2.0"><xsl:param name="Start"/>
     <xsl:template name="Transform1" match="/">
        <Greeting>
                    
    <xsl:value-of select="concat('Hello ', $Start/root/name)" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                
</Greeting>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xml var3 = xml `<root>${var2}</root>`;
    addToContext(cx, "M", var3);
}

function scope0_1ActivityRunner(Context cx) returns error? {
    check M(cx);
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

function start_Processes_Sub_process(Context cx) returns error? {
    check scope0_1ScopeFn(cx);
    xml var0 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="2.0"><xsl:param name="M"/>
     <xsl:template name="Transform0" match="/">
        <Out>
                
    <value>
                        
        <xsl:value-of select="$M/root/Greeting" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                    
    </value>
            
</Out>

    </xsl:template>
</xsl:stylesheet>`);
    xml var1 = check xslt:transform(cx.result, var0, cx.variables);
    cx.result = xml `<root>${var1}</root>`;
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
