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
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/EngineTypes" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="2.0">
     <xsl:template name="Transform5" match="/">
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

function JDBC_Delete(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/EngineTypes" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="2.0">
     <xsl:template name="Transform3" match="/">
        <jdbcUpdateActivityInput/>

    </xsl:template>
</xsl:stylesheet>`, cx.variables);
    string var2 = "delete from DB where USER_ID=";
    sql:ParameterizedQuery var3 = ``;
    var3.strings = [var2];
    xml var4;
    sql:ExecutionResult var5 = check JDBCConnection->execute(var3);
    xml var6 = xml `<root></root>`;
    var4 = var6;
    // WARNING: validate jdbc update result mapping
    addToContext(cx, "JDBC-Delete", var4);
}

function JDBC_Query(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/EngineTypes" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="2.0">
     <xsl:template name="Transform2" match="/">
        <jdbcQueryActivityInput/>

    </xsl:template>
</xsl:stylesheet>`, cx.variables);
    string var2 = "select * FROM DB";
    sql:ParameterizedQuery var3 = ``;
    var3.strings = [var2];
    xml var4;
    stream<record {|anydata...;|}, error?> var5 = JDBCConnection->query(var3);
    xml var6 = xml ``;
    int var7 = 0;
    while var7 < 100 {
        var each = var5.next();
        if each is error? {
            break;
        }
        var7 += 1;
        xml var8 = check toXML(each);
        var6 = var6 + xml `<Record>${var8}</Record>`;
    }
    xml var9 = xml `<root>${var6}</root>`;
    var4 = var9;
    addToContext(cx, "JDBC-Query", var4);
}

function JDBC_Update(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/EngineTypes" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="2.0"><xsl:param name="post"/>     <xsl:template name="Transform4" match="/">
        <jdbcUpdateActivityInput>
                    
    <UserId>
                            
        <xsl:value-of select="$post//UserId" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                        
    </UserId>
                
</jdbcUpdateActivityInput>

    </xsl:template>
</xsl:stylesheet>`, cx.variables);
    // WARNING: Prepared data is not supported, validate generated query
    string var2 = "INSERT INTO DB (USER_ID) VALUES (?)";
    sql:ParameterizedQuery var3 = ``;
    var3.strings = [var2];
    xml var4;
    sql:ExecutionResult var5 = check JDBCConnection->execute(var3);
    xml var6 = xml `<root></root>`;
    var4 = var6;
    // WARNING: validate jdbc update result mapping
    addToContext(cx, "JDBC-Update", var4);
}

function Log(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/EngineTypes" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="2.0"><xsl:param name="post"/>     <xsl:template name="Transform0" match="/">
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
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/EngineTypes" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="2.0"><xsl:param name="post"/>     <xsl:template name="Transform1" match="/">
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

function scope0ActivityRunner(Context cx) returns error? {
    check HTTP_Receiver(cx);
    check Log(cx);
    check SQL_Direct(cx);
    check JDBC_Query(cx);
    check JDBC_Delete(cx);
    check JDBC_Update(cx);
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

function initContext(map<xml> initVariables = {},
        map<SharedVariableContext> jobSharedVariables = {})
            returns Context {
    map<SharedVariableContext> sharedVariables = {};

    foreach var key in jobSharedVariables.keys() {
        sharedVariables[key] = jobSharedVariables.get(key);
    }
    return {variables: initVariables, result: xml `<root/>`, sharedVariables};
}
