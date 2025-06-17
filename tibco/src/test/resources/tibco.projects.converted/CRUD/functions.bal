import ballerina/data.xmldata;
import ballerina/log;
import ballerina/sql;
import ballerina/xslt;

function Catch(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = xml `<root>${var0}</root>`;
    addToContext(cx, "Catch", var1);
}

function ErrorLog(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            xmlns:tns="http://xmlns.example.com" version="2.0">
     <xsl:template name="Transform2" match="/">
        <ActivityInput>
                    
    <message>
                            
        <xsl:value-of select="Error" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                        
    </message>
                
</ActivityInput>

    </xsl:template>
</xsl:stylesheet>`, cx.variables);
    xml var2 = var1/**/<message>/*;
    log:printInfo(var2.toString());
    addToContext(cx, "ErrorLog", var2);
}

function HTTP_Receiver(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = xml `<root>${var0}</root>`;
    addToContext(cx, "HTTP-Receiver", var1);
}

function Log(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            xmlns:tns="http://xmlns.example.com" version="2.0"><xsl:param name="post"/>     <xsl:template name="Transform0" match="/">
        <ns:ActivityInput xmlns:ns="http://www.tibco.com/pe/EngineTypes">
                    
    <message>
         Request received for lat:
        <xsl:value-of select="$post//UserId" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
    </message>
                
</ns:ActivityInput>

    </xsl:template>
</xsl:stylesheet>`, cx.variables);
    xml var2 = var1/**/<message>/*;
    log:printInfo(var2.toString());
    addToContext(cx, "Log", var2);
}

function SQL_Direct(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            xmlns:tns="http://xmlns.example.com" version="2.0"><xsl:param name="post"/>     <xsl:template name="Transform1" match="/">
        <jdbcGeneralActivityInput>
                        
    <statement>
                                SELECT * FROM DB WHERE USER_ID=
        <xsl:value-of select="$post//UserId" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                            
    </statement>
                    
</jdbcGeneralActivityInput>

    </xsl:template>
</xsl:stylesheet>`, cx.variables);
    string var2 = (var1/**/<statement>/*).toString();
    sql:ParameterizedQuery var3 = `${var2}`;
    xml var4;
    if var2.startsWith("SELECT") {
        stream<map<anydata>, error?> var5 = JDBCConnection->query(var3);
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
    //WARNING: validate jdbc query result mapping
    addToContext(cx, "SQL-Direct", var4);
}

function scope0ActivityRunner(Context cx) returns error? {
    check HTTP_Receiver(cx);
    check Log(cx);
    check SQL_Direct(cx);
}

function scope0FaultHandler(error err, Context cx) returns () {
    checkpanic Catch(cx);
    checkpanic ErrorLog(cx);
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

function toXML(map<anydata> data) returns error|xml {
    return xmldata:toXml(data);
}

function addToContext(Context context, string varName, xml value) {
    xml children = value/*;
    xml transformed = xml `<root>${children}</root>`;
    context.variables[varName] = transformed;
    context.result = value;
}

function initContext(map<xml> initVariables = {}) returns Context {
    return {variables: initVariables, result: xml `<root/>`};
}
