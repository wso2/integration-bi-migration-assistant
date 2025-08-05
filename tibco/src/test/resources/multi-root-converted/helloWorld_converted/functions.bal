import ballerina/data.xmldata;
import ballerina/log;
import ballerina/sql;
import ballerina/xslt;

function Call_shared_process(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/EngineTypes" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="2.0"><xsl:param name="post"/>     <xsl:template name="Transform2" match="/">
        <InvokeProcessInput>
                    
    <options>
                            
        <xsl:value-of select="$post/root/item/req" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                        
    </options>
                
</InvokeProcessInput>

    </xsl:template>
</xsl:stylesheet>`, cx.variables);
    addToContext(cx, "$Start", var1);
    start_lib_shared_process(cx);
    xml var2 = cx.result;
    addToContext(cx, "Call-shared-process", var2);
}

function HTTP_Receiver(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = xml `<root>${var0}</root>`;
    addToContext(cx, "HTTP-Receiver", var1);
}

function LogLoadedVars(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/EngineTypes" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="2.0"><xsl:param name="loadSharedVariable"/>     <xsl:template name="Transform0" match="/">
        <ns:ActivityInput xmlns:ns="http://www.tibco.com/pe/EngineTypes">
                    
    <message>
                            
        <sharedValue>
                                    
            <xsl:value-of select="$loadSharedVariable" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                                
        </sharedValue>
                        
    </message>
                
</ns:ActivityInput>

    </xsl:template>
</xsl:stylesheet>`, cx.variables);
    xml var2 = var1/**/<message>/*;
    log:printInfo(var2.toString());
    addToContext(cx, "LogLoadedVars", var2);
}

function SQL_Direct(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/EngineTypes" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="2.0"><xsl:param name="post"/>     <xsl:template name="Transform3" match="/">
        <jdbcGeneralActivityInput>
                    
    <statement>
                            SELECT * FROM DB WHERE USER_ID=
        <xsl:value-of select="$post//UserId" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                        
    </statement>
                
</jdbcGeneralActivityInput>

    </xsl:template>
</xsl:stylesheet>`, cx.variables);
    string var2 = (var1/**/<statement>/*).toString().trim();
    sql:ParameterizedQuery var3 = ``;
    var3.strings = [var2];
    xml var4;
    if var2.startsWith("SELECT") {
        stream<record {|anydata...;|}, error?> var5 = JDBCConnection->query(var3);
        xml var6 = xml ``;
        check from var each in var5
            do {
                xml var7 = check toXML(each);
                var6 = var6 + xml `<Record>${var7}</Record>`;
            };

        xml var8 = xml `<root>${var6}</root>`;
        var4 = var8;
    } else {
        sql:ExecutionResult var9 = check JDBCConnection->execute(var3);
        xml var10 = xml `<root></root>`;
        var4 = var10;
    }
    // WARNING: validate jdbc query result mapping
    addToContext(cx, "SQL-Direct", var4);
}

function loadSharedVariable(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = getSharedVariable(cx, "shared");
    addToContext(cx, "loadSharedVariable", var1);
}

function scope0ActivityRunner(Context cx) returns error? {
    check HTTP_Receiver(cx);
    check loadSharedVariable(cx);
    check LogLoadedVars(cx);
    check Call_shared_process(cx);
    check SQL_Direct(cx);
    check storeSharedVariable(cx);
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

function storeSharedVariable(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/EngineTypes" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="2.0"><xsl:param name="loadSharedVariable"/>     <xsl:template name="Transform1" match="/">
        <root>
                    
    <count>
                            
        <xsl:value-of select="$loadSharedVariable/root/count + 1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                        
    </count>
                
</root>

    </xsl:template>
</xsl:stylesheet>`, cx.variables);
    setSharedVariable(cx, "shared", var1);
    addToContext(cx, "storeSharedVariable", var1);
}

function Log(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/EngineTypes" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="2.0">
     <xsl:template name="Transform0" match="/">
        <ns:ActivityInput xmlns:ns="http://www.tibco.com/pe/EngineTypes">
                    
    <message>Called shared process</message>
                
</ns:ActivityInput>

    </xsl:template>
</xsl:stylesheet>`, cx.variables);
    xml var2 = var1/**/<message>/*;
    log:printInfo(var2.toString());
    addToContext(cx, "Log", var2);
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

function start_lib_shared_process(Context cx) returns () {
    return scope0_1ScopeFn(cx);
}

function toXML(map<anydata> data) returns error|xml {
    return xmldata:toXml(data);
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

    foreach var key in jobSharedVariables.keys() {
        sharedVariables[key] = jobSharedVariables.get(key);
    }
    return {variables: initVariables, result: xml `<root/>`, sharedVariables};
}
