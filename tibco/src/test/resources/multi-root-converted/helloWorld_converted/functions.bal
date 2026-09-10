import ballerina/data.xmldata;
import ballerina/log;
import ballerina/sql;
import ballerina/xslt;

import testOrg/lib;

function Call_shared_process(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/EngineTypes" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="2.0"><xsl:param name="post"/>     <xsl:template name="Transform2" match="/">
        <InvokeProcessInput>
                    
    <options>
                            
        <xsl:value-of select="$post/root/item/req" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                        
    </options>
                
</InvokeProcessInput>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    addToContext(cx, "$Start", var2);
    lib:start_lib_Process_shared_process(cx);
    xml var3 = cx.result;
    addToContext(cx, "Call-shared-process", var3);
}

function HTTP_Receiver(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = xml `<root>${var0}</root>`;
    addToContext(cx, "HTTP-Receiver", var1);
}

function LogLoadedVars(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/EngineTypes" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="2.0"><xsl:param name="loadSharedVariable"/>     <xsl:template name="Transform0" match="/">
        <ns:ActivityInput xmlns:ns="http://www.tibco.com/pe/EngineTypes">
                    
    <message>
                            
        <sharedValue>
                                    
            <xsl:value-of select="$loadSharedVariable" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                                
        </sharedValue>
                        
    </message>
                
</ns:ActivityInput>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xml var3 = var2/**/<message>/*;
    log:printInfo(var3.toString());
    addToContext(cx, "LogLoadedVars", var3);
}

function SQL_Direct(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/EngineTypes" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="2.0"><xsl:param name="post"/>     <xsl:template name="Transform3" match="/">
        <jdbcGeneralActivityInput>
                    
    <statement>
                            SELECT * FROM DB WHERE USER_ID=
        <xsl:value-of select="$post//UserId" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                        
    </statement>
                
</jdbcGeneralActivityInput>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    string var3 = (var2/**/<statement>/*).toString().trim();
    sql:ParameterizedQuery var4 = ``;
    var4.strings = [var3];
    xml var5;
    if var3.startsWith("SELECT") {
        stream<record {|anydata...;|}, error?> var6 = JDBCConnection->query(var4);
        xml var7 = xml ``;
        check from var each in var6
            do {
                xml var8 = check toXML(each);
                var7 = var7 + xml `<Record>${var8}</Record>`;
            };

        xml var9 = xml `<root>${var7}</root>`;
        var5 = var9;
    } else {
        sql:ExecutionResult var10 = check JDBCConnection->execute(var4);
        xml var11 = xml `<root></root>`;
        var5 = var11;
    }
    // WARNING: validate jdbc query result mapping
    addToContext(cx, "SQL-Direct", var5);
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
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/EngineTypes" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="2.0"><xsl:param name="loadSharedVariable"/>     <xsl:template name="Transform1" match="/">
        <root>
                    
    <count>
                            
        <xsl:value-of select="$loadSharedVariable/root/count + 1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                        
    </count>
                
</root>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    setSharedVariable(cx, "shared", var2);
    addToContext(cx, "storeSharedVariable", var2);
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
